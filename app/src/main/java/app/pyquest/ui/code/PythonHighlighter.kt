package app.pyquest.ui.code

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import app.pyquest.ui.theme.EditorTheme

enum class TokenKind { KEYWORD, BUILTIN, STRING, NUMBER, COMMENT, FUNCTION, CLASS, DECORATOR, CONSTANT, SELF, OPERATOR, PLAIN }

data class Token(val start: Int, val end: Int, val kind: TokenKind)

/** Kleiner, fehlertoleranter Python-Lexer – gut genug fürs Einfärben beim Tippen. */
object PythonLexer {
    val keywords = setOf(
        "and", "as", "assert", "async", "await", "break", "class", "continue", "def", "del", "elif", "else",
        "except", "finally", "for", "from", "global", "if", "import", "in", "is", "lambda", "nonlocal", "not",
        "or", "pass", "raise", "return", "try", "while", "with", "yield", "match", "case", "type",
    )
    private val constants = setOf("True", "False", "None", "Ellipsis", "NotImplemented", "__name__", "__main__")
    val builtins = setOf(
        "print", "input", "len", "range", "int", "str", "float", "bool", "list", "dict", "set", "tuple", "type",
        "isinstance", "issubclass", "sum", "min", "max", "abs", "round", "sorted", "reversed", "enumerate", "zip",
        "map", "filter", "open", "any", "all", "ord", "chr", "hex", "bin", "oct", "id", "hash", "help", "dir",
        "vars", "iter", "next", "super", "object", "property", "staticmethod", "classmethod", "format", "repr",
        "divmod", "pow", "callable", "getattr", "setattr", "hasattr", "delattr", "frozenset", "bytes", "bytearray",
        "complex", "slice", "globals", "locals", "exec", "eval", "breakpoint", "memoryview", "ascii",
        "Exception", "BaseException", "ValueError", "TypeError", "KeyError", "IndexError", "NameError",
        "ZeroDivisionError", "AttributeError", "RuntimeError", "StopIteration", "FileNotFoundError", "OSError",
        "ImportError", "ModuleNotFoundError", "AssertionError", "NotImplementedError", "RecursionError",
        "ArithmeticError", "LookupError", "OverflowError", "PermissionError", "KeyboardInterrupt", "SystemExit",
        "UnicodeError", "EOFError", "IndentationError", "SyntaxError", "UnboundLocalError", "TimeoutError",
    )

    fun tokenize(code: String): List<Token> {
        val tokens = ArrayList<Token>(code.length / 3)
        var i = 0
        val n = code.length
        var prevWord: String? = null
        var lineStart = true
        while (i < n) {
            val c = code[i]
            when {
                c == '\n' -> {
                    i++
                    lineStart = true
                    continue
                }
                c == ' ' || c == '\t' || c == '\r' -> {
                    i++
                    continue
                }
                c == '#' -> {
                    val end = code.indexOf('\n', i).let { if (it < 0) n else it }
                    tokens += Token(i, end, TokenKind.COMMENT)
                    i = end
                }
                c == '@' && lineStart -> {
                    var j = i + 1
                    while (j < n && (code[j].isLetterOrDigit() || code[j] == '_' || code[j] == '.')) j++
                    tokens += Token(i, j, TokenKind.DECORATOR)
                    i = j
                }
                isStringStart(code, i) -> {
                    val (end, fString) = scanString(code, i)
                    if (fString) addFString(code, i, end, tokens) else tokens += Token(i, end, TokenKind.STRING)
                    i = end
                    prevWord = null
                }
                c.isDigit() || (c == '.' && i + 1 < n && code[i + 1].isDigit()) -> {
                    var j = i + 1
                    while (j < n && (code[j].isLetterOrDigit() || code[j] == '_' || code[j] == '.' ||
                            ((code[j] == '+' || code[j] == '-') && (code[j - 1] == 'e' || code[j - 1] == 'E')))
                    ) j++
                    tokens += Token(i, j, TokenKind.NUMBER)
                    i = j
                }
                c.isLetter() || c == '_' -> {
                    var j = i + 1
                    while (j < n && (code[j].isLetterOrDigit() || code[j] == '_')) j++
                    val word = code.substring(i, j)
                    var k = j
                    while (k < n && code[k] == ' ') k++
                    val callFollows = k < n && code[k] == '('
                    val kind = when {
                        prevWord == "def" -> TokenKind.FUNCTION
                        prevWord == "class" -> TokenKind.CLASS
                        word in constants -> TokenKind.CONSTANT
                        word == "self" || word == "cls" -> TokenKind.SELF
                        word in keywords && !(word == "type" && callFollows) &&
                            !((word == "match" || word == "case" || word == "type") && !lineStartWord(code, i)) -> TokenKind.KEYWORD
                        word in builtins -> TokenKind.BUILTIN
                        callFollows -> TokenKind.FUNCTION
                        word.isNotEmpty() && word[0].isUpperCase() && word.any { it.isLowerCase() } -> TokenKind.CLASS
                        else -> TokenKind.PLAIN
                    }
                    if (kind != TokenKind.PLAIN) tokens += Token(i, j, kind)
                    prevWord = word
                    i = j
                }
                else -> {
                    if (c in "+-*/%=<>!&|^~:") tokens += Token(i, i + 1, TokenKind.OPERATOR)
                    i++
                    prevWord = null
                }
            }
            lineStart = false
        }
        return tokens
    }

    private fun lineStartWord(code: String, pos: Int): Boolean {
        var j = pos - 1
        while (j >= 0 && (code[j] == ' ' || code[j] == '\t')) j--
        return j < 0 || code[j] == '\n'
    }

    private fun isStringStart(code: String, i: Int): Boolean {
        val c = code[i]
        if (c == '"' || c == '\'') return true
        if (c !in "rRbBuUfFtT") return false
        // Präfixe wie f", rb', Rf"
        if (i > 0 && (code[i - 1].isLetterOrDigit() || code[i - 1] == '_')) return false
        var j = i
        var len = 0
        while (j < code.length && code[j] in "rRbBuUfFtT" && len < 2) {
            j++
            len++
        }
        return j < code.length && (code[j] == '"' || code[j] == '\'')
    }

    /** Liefert Ende (exklusiv) und ob es ein f-String ist. */
    private fun scanString(code: String, start: Int): Pair<Int, Boolean> {
        var i = start
        var raw = false
        var f = false
        while (code[i] != '"' && code[i] != '\'') {
            if (code[i] == 'r' || code[i] == 'R') raw = true
            if (code[i] == 'f' || code[i] == 'F' || code[i] == 't' || code[i] == 'T') f = true
            i++
        }
        val q = code[i]
        val triple = i + 2 < code.length && code[i + 1] == q && code[i + 2] == q
        i += if (triple) 3 else 1
        while (i < code.length) {
            val c = code[i]
            if (c == '\\' && !raw) {
                i += 2
                continue
            }
            if (!triple && c == '\n') return i to f
            if (c == q) {
                if (!triple) return (i + 1) to f
                if (i + 2 < code.length && code[i + 1] == q && code[i + 2] == q) return (i + 3) to f
            }
            i++
        }
        return code.length to f
    }

    private fun addFString(code: String, start: Int, end: Int, out: MutableList<Token>) {
        var segStart = start
        var i = start
        var depth = 0
        while (i < end) {
            val c = code[i]
            if (depth == 0 && c == '{') {
                if (i + 1 < end && code[i + 1] == '{') {
                    i += 2
                    continue
                }
                out += Token(segStart, i + 1, TokenKind.STRING)
                depth = 1
                segStart = i + 1
            } else if (depth > 0) {
                if (c == '{') depth++
                if (c == '}') {
                    depth--
                    if (depth == 0) {
                        // Ausdruck innerhalb von {...} normal einfärben
                        val inner = code.substring(segStart, i)
                        tokenize(inner).forEach { t -> out += Token(segStart + t.start, segStart + t.end, t.kind) }
                        segStart = i
                    }
                }
            }
            i++
        }
        if (segStart < end) out += Token(segStart, end, TokenKind.STRING)
    }
}

fun EditorTheme.styleFor(kind: TokenKind): SpanStyle = when (kind) {
    TokenKind.KEYWORD -> SpanStyle(color = keyword, fontWeight = FontWeight.Bold)
    TokenKind.BUILTIN -> SpanStyle(color = builtin)
    TokenKind.STRING -> SpanStyle(color = string)
    TokenKind.NUMBER -> SpanStyle(color = number)
    TokenKind.COMMENT -> SpanStyle(color = comment, fontStyle = FontStyle.Italic)
    TokenKind.FUNCTION -> SpanStyle(color = function)
    TokenKind.CLASS -> SpanStyle(color = className)
    TokenKind.DECORATOR -> SpanStyle(color = decorator)
    TokenKind.CONSTANT -> SpanStyle(color = constant, fontWeight = FontWeight.Bold)
    TokenKind.SELF -> SpanStyle(color = self, fontStyle = FontStyle.Italic)
    TokenKind.OPERATOR -> SpanStyle(color = operator)
    TokenKind.PLAIN -> SpanStyle(color = text)
}

fun highlightPython(code: String, theme: EditorTheme): AnnotatedString = buildAnnotatedString {
    append(code)
    addStyle(SpanStyle(color = theme.text), 0, code.length)
    for (t in PythonLexer.tokenize(code)) {
        if (t.start < t.end && t.end <= code.length) addStyle(theme.styleFor(t.kind), t.start, t.end)
    }
}

/** Färbt den Text eines Eingabefelds live ein. */
class PythonVisualTransformation(private val theme: EditorTheme) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText =
        TransformedText(highlightPython(text.text, theme), OffsetMapping.Identity)

    override fun equals(other: Any?) = other is PythonVisualTransformation && other.theme == theme
    override fun hashCode() = theme.hashCode()
}

/** Einfärbung für Konsolen-/Ausgabetext ohne Syntax. */
fun plainCode(code: String, color: Color): AnnotatedString = buildAnnotatedString {
    append(code)
    addStyle(SpanStyle(color = color), 0, code.length)
}
