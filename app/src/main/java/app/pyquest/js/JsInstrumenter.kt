package app.pyquest.js

/**
 * Baut in JavaScript-Code Wächter gegen Endlosschleifen ein, damit Zeitlimit und
 * Stopp-Knopf auch bei `while (true) {}` greifen (siehe __hb_g in assets/js/prelude.js):
 *
 * - `while (c)`        → `while (__hb_g() && (c))` (gilt auch für do … while)
 * - `for (a; c; b)`    → `for (a; __hb_g() && (c); b)`, leere Bedingung → `__hb_g()`
 * - `for (x of xs)`    → `for (x of __hb_gi(xs, "xs"))`
 *
 * Es wird nur innerhalb von Zeilen eingefügt – Zeilennummern in Fehlermeldungen bleiben
 * gleich, Spalten lassen sich mit [Result.originalColumn] zurückrechnen. Code, der sich
 * nicht sauber zerlegen lässt (offener String, Kommentar …), bleibt unverändert.
 */
object JsInstrumenter {
    const val GUARD = "__hb_g"
    const val ITER = "__hb_gi"

    /** Eine Einfügung: [column] (0-basiert) bezieht sich auf die Zeile im *neuen* Code. */
    class Insertion(val column: Int, val length: Int)

    class Result(val code: String, private val insertions: Map<Int, List<Insertion>>) {
        val changed: Boolean get() = insertions.isNotEmpty()

        /** Rechnet eine 1-basierte Spalte aus dem neuen Code in den Originalcode um. */
        fun originalColumn(line: Int, column: Int): Int {
            val list = insertions[line] ?: return column
            val c = column - 1
            var shift = 0
            for (ins in list) {
                if (ins.column + ins.length <= c) shift += ins.length
                else if (ins.column <= c) return ins.column - shift + 1
            }
            return c - shift + 1
        }
    }

    private enum class T { IDENT, NUM, STR, TEMPLATE, REGEX, PUNCT }

    private class Tok(val type: T, val start: Int, val end: Int, val text: String)

    private class Malformed : RuntimeException()

    private val regexAfterWords = setOf(
        "return", "typeof", "instanceof", "in", "of", "new", "delete", "void", "throw", "case", "do", "else",
        "yield", "await",
    )

    private val punctuators = listOf(
        ">>>=", "...", "===", "!==", "**=", "<<=", ">>=", ">>>", "&&=", "||=", "??=",
        "=>", "==", "!=", "<=", ">=", "&&", "||", "??", "?.", "++", "--", "+=", "-=", "*=", "%=", "&=", "|=",
        "^=", "**", "<<", ">>",
    )

    fun instrument(code: String): Result {
        val toks = try {
            tokenize(code)
        } catch (_: Malformed) {
            return Result(code, emptyMap())
        }
        val edits = ArrayList<Pair<Int, String>>()
        for (i in toks.indices) {
            val t = toks[i]
            if (t.type != T.IDENT || (t.text != "while" && t.text != "for")) continue
            val prev = toks.getOrNull(i - 1)
            if (prev != null && prev.type == T.PUNCT && (prev.text == "." || prev.text == "?.")) continue
            val openIdx = i + 1
            val open = toks.getOrNull(openIdx) ?: continue
            if (open.type != T.PUNCT || open.text != "(") continue
            val closeIdx = matching(toks, openIdx) ?: continue
            if (t.text == "while") {
                if (closeIdx == openIdx + 1) continue
                edits += open.end to "$GUARD() && ("
                edits += toks[closeIdx].start to ")"
                continue
            }
            // for: Kopf an Semikolons der obersten Ebene zerlegen
            val semis = ArrayList<Int>()
            var ofIdx = -1
            var depth = 0
            for (k in openIdx + 1 until closeIdx) {
                val x = toks[k]
                if (x.type == T.PUNCT) {
                    when (x.text) {
                        "(", "[", "{", "\${" -> depth++
                        ")", "]", "}" -> depth--
                        ";" -> if (depth == 0) semis += k
                    }
                } else if (x.type == T.IDENT && x.text == "of" && depth == 0 && ofIdx < 0 && semis.isEmpty() &&
                    k > openIdx + 1 && toks[k - 1].let { p -> !(p.type == T.IDENT && p.text in setOf("const", "let", "var")) }
                ) {
                    ofIdx = k
                }
            }
            if (semis.size == 2) {
                val s1 = semis[0]
                val s2 = semis[1]
                if (s2 == s1 + 1) {
                    edits += toks[s1].end to " $GUARD()"
                } else {
                    edits += toks[s1].end to " $GUARD() && ("
                    edits += toks[s2].start to ")"
                }
            } else if (semis.isEmpty() && ofIdx > 0 && ofIdx + 1 < closeIdx) {
                val exprStart = toks[ofIdx + 1].start
                val exprEnd = toks[closeIdx - 1].end
                val text = describe(code.substring(exprStart, exprEnd))
                edits += exprStart to "$ITER("
                edits += exprEnd to ", ${jsString(text)})"
            }
        }
        if (edits.isEmpty()) return Result(code, emptyMap())
        return apply(code, edits)
    }

    private fun apply(code: String, edits: List<Pair<Int, String>>): Result {
        val sorted = edits.withIndex().sortedWith(compareBy({ it.value.first }, { it.index })).map { it.value }
        val sb = StringBuilder(code.length + edits.size * 12)
        val insertions = HashMap<Int, MutableList<Insertion>>()
        var line = 1
        var lineStart = 0 // Position des Zeilenanfangs im neuen Code
        var pos = 0
        fun copyTo(target: Int) {
            while (pos < target) {
                val c = code[pos]
                sb.append(c)
                pos++
                if (c == '\n') {
                    line++
                    lineStart = sb.length
                }
            }
        }
        for ((at, text) in sorted) {
            copyTo(at)
            insertions.getOrPut(line) { ArrayList() } += Insertion(sb.length - lineStart, text.length)
            sb.append(text)
        }
        copyTo(code.length)
        return Result(sb.toString(), insertions)
    }

    /** Index der passenden schließenden Klammer zu toks[openIdx] == "(". */
    private fun matching(toks: List<Tok>, openIdx: Int): Int? {
        var depth = 0
        for (k in openIdx until toks.size) {
            val x = toks[k]
            if (x.type != T.PUNCT) continue
            when (x.text) {
                "(", "[", "{", "\${" -> depth++
                ")", "]", "}" -> {
                    depth--
                    if (depth == 0) return if (x.text == ")") k else null
                    if (depth < 0) return null
                }
            }
        }
        return null
    }

    /** Kurzform eines Ausdrucks für die Meldung "… is not iterable" (wie V8: f(...)). */
    private fun describe(expr: String): String {
        val compact = expr.replace(Regex("\\s+"), " ").trim()
        val sb = StringBuilder()
        var depth = 0
        for (c in compact) {
            when {
                c == '(' -> {
                    if (depth == 0) sb.append("(...")
                    depth++
                }
                c == ')' -> {
                    depth--
                    if (depth == 0) sb.append(')')
                }
                depth == 0 -> sb.append(c)
            }
        }
        val s = sb.toString()
        return if (s.length > 60) s.take(57) + "..." else s
    }

    private fun jsString(s: String): String {
        val sb = StringBuilder("\"")
        for (c in s) {
            when (c) {
                '"' -> sb.append("\\\"")
                '\\' -> sb.append("\\\\")
                '\n' -> sb.append("\\n")
                '\r' -> sb.append("\\r")
                '\u2028' -> sb.append("\\u2028")
                '\u2029' -> sb.append("\\u2029")
                else -> sb.append(c)
            }
        }
        return sb.append('"').toString()
    }

    // ------------------------------------------------------------------ Tokenizer

    private fun isIdentStart(c: Char) = c.isLetter() || c == '_' || c == '$' || c.code > 127
    private fun isIdentPart(c: Char) = isIdentStart(c) || c.isDigit() || c == '‌' || c == '‍'

    private fun tokenize(src: String): List<Tok> {
        val out = ArrayList<Tok>(src.length / 3)
        val n = src.length
        var i = 0
        var braceDepth = 0
        // Für jede offene Template-Ersetzung ${…}: die Klammertiefe, bei der sie begann.
        val templateStack = ArrayDeque<Int>()

        fun regexAllowed(): Boolean {
            val p = out.lastOrNull() ?: return true
            return when (p.type) {
                T.NUM, T.STR, T.TEMPLATE, T.REGEX -> false
                T.IDENT -> p.text in regexAfterWords
                T.PUNCT -> p.text !in setOf(")", "]", "}", "++", "--")
            }
        }

        /** Liest Template-Text ab [from] bis ` oder ${. Liefert die Position danach. */
        fun scanTemplate(from: Int): Int {
            var j = from
            while (j < n) {
                val c = src[j]
                when {
                    c == '\\' -> j += 2
                    c == '`' -> {
                        out += Tok(T.TEMPLATE, from, j + 1, "")
                        return j + 1
                    }
                    c == '$' && j + 1 < n && src[j + 1] == '{' -> {
                        out += Tok(T.TEMPLATE, from, j, "")
                        out += Tok(T.PUNCT, j, j + 2, "\${")
                        templateStack.addLast(braceDepth)
                        braceDepth++
                        return j + 2
                    }
                    else -> j++
                }
            }
            throw Malformed()
        }

        while (i < n) {
            val c = src[i]
            when {
                c == ' ' || c == '\t' || c == '\n' || c == '\r' || c == '\u00A0' || c == '\uFEFF' || c == '\u2028' || c == '\u2029' -> i++
                c == '/' && i + 1 < n && src[i + 1] == '/' -> {
                    while (i < n && src[i] != '\n') i++
                }
                c == '/' && i + 1 < n && src[i + 1] == '*' -> {
                    val end = src.indexOf("*/", i + 2)
                    if (end < 0) throw Malformed()
                    i = end + 2
                }
                c == '"' || c == '\'' -> {
                    var j = i + 1
                    while (true) {
                        if (j >= n) throw Malformed()
                        val d = src[j]
                        if (d == '\\') {
                            j += 2
                            continue
                        }
                        if (d == '\n') throw Malformed()
                        if (d == c) break
                        j++
                    }
                    out += Tok(T.STR, i, j + 1, "")
                    i = j + 1
                }
                c == '`' -> i = scanTemplate(i + 1)
                c == '}' && templateStack.isNotEmpty() && templateStack.last() == braceDepth - 1 -> {
                    templateStack.removeLast()
                    braceDepth--
                    out += Tok(T.PUNCT, i, i + 1, "}")
                    i = scanTemplate(i + 1)
                }
                c == '/' && regexAllowed() -> {
                    var j = i + 1
                    var inClass = false
                    while (true) {
                        if (j >= n || src[j] == '\n') throw Malformed()
                        val d = src[j]
                        if (d == '\\') {
                            j += 2
                            continue
                        }
                        if (d == '[') inClass = true
                        else if (d == ']') inClass = false
                        else if (d == '/' && !inClass) break
                        j++
                    }
                    j++
                    while (j < n && isIdentPart(src[j])) j++
                    out += Tok(T.REGEX, i, j, "")
                    i = j
                }
                isIdentStart(c) || c == '#' || (c == '\\' && i + 1 < n && src[i + 1] == 'u') -> {
                    var j = i + 1
                    while (j < n && (isIdentPart(src[j]) || src[j] == '\\')) j++
                    out += Tok(T.IDENT, i, j, src.substring(i, j))
                    i = j
                }
                c.isDigit() || (c == '.' && i + 1 < n && src[i + 1].isDigit()) -> {
                    var j = i + 1
                    while (j < n) {
                        val d = src[j]
                        if (d.isLetterOrDigit() || d == '_' || d == '.') {
                            j++
                        } else if ((d == '+' || d == '-') && (src[j - 1] == 'e' || src[j - 1] == 'E') &&
                            !src.substring(i, j).startsWith("0x", ignoreCase = true)
                        ) {
                            j++
                        } else {
                            break
                        }
                    }
                    out += Tok(T.NUM, i, j, "")
                    i = j
                }
                else -> {
                    val p = punctuators.firstOrNull { src.startsWith(it, i) }
                        ?.takeIf { it != "?." || i + 2 >= n || !src[i + 2].isDigit() }
                    val text = p ?: c.toString()
                    when (text) {
                        "{" -> braceDepth++
                        "}" -> braceDepth--
                    }
                    out += Tok(T.PUNCT, i, i + text.length, text)
                    i += text.length
                }
            }
        }
        if (templateStack.isNotEmpty()) throw Malformed()
        return out
    }
}
