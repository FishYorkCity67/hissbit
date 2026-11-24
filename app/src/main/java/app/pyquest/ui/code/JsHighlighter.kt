package app.pyquest.ui.code

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import app.pyquest.data.CourseId
import app.pyquest.ui.theme.EditorTheme

/** Kleiner, fehlertoleranter JavaScript-Lexer fürs Einfärben beim Tippen. */
object JsLexer {
    val keywords = setOf(
        "async", "await", "break", "case", "catch", "class", "const", "continue", "debugger", "default", "delete",
        "do", "else", "export", "extends", "finally", "for", "function", "if", "import", "in", "instanceof", "let",
        "new", "of", "return", "static", "super", "switch", "throw", "try", "typeof", "var", "void", "while",
        "with", "yield", "get", "set", "from",
    )
    private val contextual = setOf("of", "get", "set", "static", "async", "from")
    private val constants = setOf("true", "false", "null", "undefined", "NaN", "Infinity")
    val builtins = setOf(
        "console", "Math", "JSON", "Object", "Array", "String", "Number", "Boolean", "Promise", "Map", "Set",
        "WeakMap", "WeakSet", "Date", "RegExp", "Error", "TypeError", "RangeError", "SyntaxError", "ReferenceError",
        "Symbol", "BigInt", "Intl", "Reflect", "Proxy", "globalThis", "parseInt", "parseFloat", "isNaN", "isFinite",
        "prompt", "alert", "confirm", "setTimeout", "setInterval", "clearTimeout", "clearInterval", "queueMicrotask",
        "structuredClone", "encodeURIComponent", "decodeURIComponent", "process", "require",
    )
    private val regexAfter = setOf("return", "typeof", "instanceof", "in", "of", "new", "delete", "void", "throw", "case", "do", "else", "yield", "await")

    fun tokenize(code: String): List<Token> {
        val tokens = ArrayList<Token>(code.length / 3)
        tokenizeInto(code, 0, code.length, tokens)
        return tokens
    }

    private fun isIdentStart(c: Char) = c.isLetter() || c == '_' || c == '$'
    private fun isIdentPart(c: Char) = c.isLetterOrDigit() || c == '_' || c == '$'

    private fun tokenizeInto(code: String, from: Int, to: Int, tokens: MutableList<Token>) {
        var i = from
        var prevWord: String? = null
        // Letztes bedeutsames Zeichen/Token – entscheidet, ob "/" eine Regex beginnt.
        var regexOk = true
        var afterDot = false
        while (i < to) {
            val c = code[i]
            when {
                c == ' ' || c == '\t' || c == '\n' || c == '\r' -> i++
                c == '/' && i + 1 < to && code[i + 1] == '/' -> {
                    val end = code.indexOf('\n', i).let { if (it < 0 || it > to) to else it }
                    tokens += Token(i, end, TokenKind.COMMENT)
                    i = end
                }
                c == '/' && i + 1 < to && code[i + 1] == '*' -> {
                    val end = code.indexOf("*/", i + 2).let { if (it < 0 || it + 2 > to) to else it + 2 }
                    tokens += Token(i, end, TokenKind.COMMENT)
                    i = end
                }
                c == '"' || c == '\'' -> {
                    var j = i + 1
                    while (j < to && code[j] != c && code[j] != '\n') j += if (code[j] == '\\') 2 else 1
                    val end = if (j < to && code[j] == c) j + 1 else minOf(j, to)
                    tokens += Token(i, end, TokenKind.STRING)
                    i = end
                    regexOk = false
                    prevWord = null
                    afterDot = false
                }
                c == '`' -> {
                    i = template(code, i, to, tokens)
                    regexOk = false
                    prevWord = null
                    afterDot = false
                }
                c == '/' && regexOk -> {
                    var j = i + 1
                    var inClass = false
                    var ok = false
                    while (j < to && code[j] != '\n') {
                        val d = code[j]
                        if (d == '\\') { j += 2; continue }
                        if (d == '[') inClass = true else if (d == ']') inClass = false
                        else if (d == '/' && !inClass) { ok = true; break }
                        j++
                    }
                    if (ok) {
                        j++
                        while (j < to && code[j].isLetter()) j++
                        tokens += Token(i, j, TokenKind.STRING)
                        i = j
                        regexOk = false
                    } else {
                        tokens += Token(i, i + 1, TokenKind.OPERATOR)
                        i++
                        regexOk = true
                    }
                    afterDot = false
                }
                c.isDigit() || (c == '.' && i + 1 < to && code[i + 1].isDigit()) -> {
                    var j = i + 1
                    while (j < to && (code[j].isLetterOrDigit() || code[j] == '_' || code[j] == '.' ||
                            ((code[j] == '+' || code[j] == '-') && (code[j - 1] == 'e' || code[j - 1] == 'E') &&
                                !code.startsWith("0x", i, ignoreCase = true)))
                    ) j++
                    tokens += Token(i, j, TokenKind.NUMBER)
                    i = j
                    regexOk = false
                    afterDot = false
                }
                isIdentStart(c) || c == '#' -> {
                    var j = i + 1
                    while (j < to && isIdentPart(code[j])) j++
                    val word = code.substring(i, j)
                    var k = j
                    while (k < to && code[k] == ' ') k++
                    val callFollows = k < to && code[k] == '('
                    val kind = when {
                        afterDot -> if (callFollows) TokenKind.FUNCTION else TokenKind.PLAIN
                        prevWord == "function" || (prevWord == "async" && callFollows) -> TokenKind.FUNCTION
                        prevWord == "class" || prevWord == "new" || prevWord == "extends" -> TokenKind.CLASS
                        word == "this" -> TokenKind.SELF
                        word in constants -> TokenKind.CONSTANT
                        word in keywords && !(word in contextual && callFollows) -> TokenKind.KEYWORD
                        word in builtins -> TokenKind.BUILTIN
                        callFollows -> TokenKind.FUNCTION
                        word[0].isUpperCase() && word.any { it.isLowerCase() } -> TokenKind.CLASS
                        else -> TokenKind.PLAIN
                    }
                    if (kind != TokenKind.PLAIN) tokens += Token(i, j, kind)
                    regexOk = word in regexAfter
                    prevWord = word
                    afterDot = false
                    i = j
                }
                else -> {
                    if (c in "+-*/%=<>!&|^~?:") tokens += Token(i, i + 1, TokenKind.OPERATOR)
                    afterDot = c == '.' && !(i + 1 < to && code[i + 1] == '.')
                    regexOk = c !in ")]}" && !(c == '.' && afterDot)
                    if (c == '.' && i > from && code[i - 1] == '?') afterDot = true
                    prevWord = null
                    i++
                }
            }
        }
    }

    /** Template-String ab dem Backtick bei [start]; Ausdrücke in ${…} werden normal eingefärbt. */
    private fun template(code: String, start: Int, to: Int, tokens: MutableList<Token>): Int {
        var segStart = start
        var i = start + 1
        while (i < to) {
            val c = code[i]
            when {
                c == '\\' -> i += 2
                c == '`' -> {
                    tokens += Token(segStart, i + 1, TokenKind.STRING)
                    return i + 1
                }
                c == '$' && i + 1 < to && code[i + 1] == '{' -> {
                    tokens += Token(segStart, i + 2, TokenKind.STRING)
                    var depth = 1
                    var j = i + 2
                    while (j < to && depth > 0) {
                        when (code[j]) {
                            '{' -> depth++
                            '}' -> depth--
                            '`' -> { j = template(code, j, to, mutableListOf()) - 1 }
                        }
                        if (depth > 0) j++
                    }
                    tokenizeInto(code, i + 2, minOf(j, to), tokens)
                    segStart = minOf(j, to)
                    i = segStart + 1
                }
                else -> i++
            }
        }
        tokens += Token(segStart, to, TokenKind.STRING)
        return to
    }
}

fun highlightJs(code: String, theme: EditorTheme): AnnotatedString = buildAnnotatedString {
    append(code)
    addStyle(SpanStyle(color = theme.text), 0, code.length)
    for (t in JsLexer.tokenize(code)) {
        if (t.start < t.end && t.end <= code.length) addStyle(theme.styleFor(t.kind), t.start, t.end)
    }
}

/** Färbt Code passend zur Kurssprache ein. */
fun highlightCode(code: String, theme: EditorTheme, course: CourseId): AnnotatedString = when (course) {
    CourseId.PYTHON -> highlightPython(code, theme)
    CourseId.JAVASCRIPT -> highlightJs(code, theme)
}

/** Live-Einfärbung im Editor für die jeweilige Sprache. */
class CodeVisualTransformation(private val theme: EditorTheme, private val course: CourseId) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText =
        TransformedText(highlightCode(text.text, theme, course), OffsetMapping.Identity)

    override fun equals(other: Any?) = other is CodeVisualTransformation && other.theme == theme && other.course == course
    override fun hashCode() = theme.hashCode() * 31 + course.hashCode()
}
