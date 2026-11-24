package app.pyquest.ui.code

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import app.pyquest.data.CourseId

/** Textoperationen des Code-Editors (rein funktional, gut testbar). */
object EditorOps {
    const val INDENT = "    "

    /** Einrückung je Sprache: Python 4 Leerzeichen, JavaScript (üblicher Stil) 2. */
    fun indentFor(course: CourseId): String = if (course == CourseId.JAVASCRIPT) "  " else INDENT

    fun insert(value: TextFieldValue, text: String): TextFieldValue {
        val start = minOf(value.selection.start, value.selection.end)
        val end = maxOf(value.selection.start, value.selection.end)
        val newText = value.text.substring(0, start) + text + value.text.substring(end)
        return TextFieldValue(newText, TextRange(start + text.length))
    }

    /** Fügt ein Klammerpaar ein und setzt den Cursor dazwischen. */
    fun insertPair(value: TextFieldValue, open: String, close: String): TextFieldValue {
        val start = minOf(value.selection.start, value.selection.end)
        val end = maxOf(value.selection.start, value.selection.end)
        val selected = value.text.substring(start, end)
        val newText = value.text.substring(0, start) + open + selected + close + value.text.substring(end)
        return TextFieldValue(newText, TextRange(start + open.length + selected.length))
    }

    private fun lineStartOf(text: String, pos: Int): Int = text.lastIndexOf('\n', (pos - 1).coerceAtLeast(-1)).let { if (pos == 0) 0 else it + 1 }

    fun dedent(value: TextFieldValue, unit: String = INDENT): TextFieldValue {
        val pos = value.selection.start
        val lineStart = lineStartOf(value.text, pos)
        var remove = 0
        while (remove < unit.length && lineStart + remove < value.text.length && value.text[lineStart + remove] == ' ') remove++
        if (remove == 0) return value
        val newText = value.text.removeRange(lineStart, lineStart + remove)
        val newPos = (pos - remove).coerceAtLeast(lineStart)
        return TextFieldValue(newText, TextRange(newPos))
    }

    /**
     * Wird nach jeder Eingabe aufgerufen: Nach einem Zeilenumbruch wird die
     * Einrückung der vorigen Zeile übernommen (+4 nach einem Doppelpunkt).
     */
    fun smartEdit(old: TextFieldValue, new: TextFieldValue, autoIndent: Boolean, course: CourseId = CourseId.PYTHON): TextFieldValue {
        var v = new
        val unit = indentFor(course)
        // Tabs gibt es auf Handytastaturen selten – falls doch, in Leerzeichen umwandeln.
        if (v.text.contains('\t')) {
            val before = v.text.substring(0, v.selection.start).count { it == '\t' }
            v = TextFieldValue(v.text.replace("\t", unit), TextRange(v.selection.start + before * (unit.length - 1)))
        }
        if (!autoIndent) return v
        if (course == CourseId.JAVASCRIPT) return smartEditJs(old, v, unit)
        if (v.text.length != old.text.length + 1 || !v.selection.collapsed) return v
        val pos = v.selection.start
        if (pos <= 0 || v.text[pos - 1] != '\n') return v
        // Nur echte Einfügung am Cursor (nicht z.B. Ersetzen durch Autokorrektur)
        if (old.text.substring(0, pos - 1) != v.text.substring(0, pos - 1)) return v
        val prevLineStart = v.text.lastIndexOf('\n', pos - 2).let { if (pos - 2 < 0) 0 else it + 1 }
        val prevLine = v.text.substring(prevLineStart, pos - 1)
        var indent = prevLine.takeWhile { it == ' ' }
        val trimmed = prevLine.trimEnd()
        if (trimmed.endsWith(":") && !trimmed.trimStart().startsWith("#")) indent += INDENT
        val dedentWords = listOf("return", "break", "continue", "pass", "raise")
        if (dedentWords.any { trimmed.trimStart() == it || trimmed.trimStart().startsWith("$it ") } && indent.length >= 4) {
            indent = indent.dropLast(4)
        }
        if (indent.isEmpty()) return v
        val text = v.text.substring(0, pos) + indent + v.text.substring(pos)
        return TextFieldValue(text, TextRange(pos + indent.length))
    }

    /**
     * JavaScript: Nach "{", "[" oder "(" am Zeilenende eine Stufe tiefer einrücken; steht die
     * schließende Klammer direkt hinter dem Cursor, wandert sie in eine eigene Zeile.
     * Eine "}" am Anfang einer leeren Zeile rückt wieder aus.
     */
    private fun smartEditJs(old: TextFieldValue, v: TextFieldValue, unit: String): TextFieldValue {
        if (v.text.length != old.text.length + 1 || !v.selection.collapsed) return v
        val pos = v.selection.start
        if (pos <= 0) return v
        if (old.text.substring(0, pos - 1) != v.text.substring(0, pos - 1)) return v
        val typed = v.text[pos - 1]
        if (typed == '}' || typed == ']' || typed == ')') {
            val lineStart = v.text.lastIndexOf('\n', pos - 2).let { if (pos - 2 < 0) 0 else it + 1 }
            val before = v.text.substring(lineStart, pos - 1)
            if (before.isNotEmpty() && before.isBlank() && before.length >= unit.length) {
                val text = v.text.removeRange(pos - 1 - unit.length, pos - 1)
                return TextFieldValue(text, TextRange(pos - unit.length))
            }
            return v
        }
        if (typed != '\n') return v
        val prevLineStart = v.text.lastIndexOf('\n', pos - 2).let { if (pos - 2 < 0) 0 else it + 1 }
        val prevLine = v.text.substring(prevLineStart, pos - 1)
        val base = prevLine.takeWhile { it == ' ' }
        val code = prevLine.substringBefore("//").trimEnd()
        val opener = code.lastOrNull()
        val opens = opener == '{' || opener == '[' || opener == '('
        val indent = if (opens) base + unit else base
        val after = v.text.getOrNull(pos)
        val closer = when (opener) { '{' -> '}'; '[' -> ']'; '(' -> ')'; else -> null }
        if (opens && after == closer) {
            val text = v.text.substring(0, pos) + indent + "\n" + base + v.text.substring(pos)
            return TextFieldValue(text, TextRange(pos + indent.length))
        }
        if (indent.isEmpty()) return v
        val text = v.text.substring(0, pos) + indent + v.text.substring(pos)
        return TextFieldValue(text, TextRange(pos + indent.length))
    }
}

/** Einfache Rückgängig/Wiederholen-Historie mit Zusammenfassen schneller Eingaben. */
class EditHistory(private val limit: Int = 200) {
    private val undoStack = ArrayDeque<TextFieldValue>()
    private val redoStack = ArrayDeque<TextFieldValue>()
    private var lastRecord = 0L

    val canUndo get() = undoStack.isNotEmpty()
    val canRedo get() = redoStack.isNotEmpty()

    fun record(previous: TextFieldValue, next: TextFieldValue, now: Long = System.currentTimeMillis()) {
        if (previous.text == next.text) return
        val coalesce = now - lastRecord < 700 && undoStack.isNotEmpty() &&
            !next.text.endsWith("\n") && kotlin.math.abs(next.text.length - previous.text.length) == 1
        if (!coalesce) {
            undoStack.addLast(previous)
            if (undoStack.size > limit) undoStack.removeFirst()
        }
        redoStack.clear()
        lastRecord = now
    }

    fun undo(current: TextFieldValue): TextFieldValue? {
        val prev = undoStack.removeLastOrNull() ?: return null
        redoStack.addLast(current)
        lastRecord = 0
        return prev
    }

    fun redo(current: TextFieldValue): TextFieldValue? {
        val next = redoStack.removeLastOrNull() ?: return null
        undoStack.addLast(current)
        lastRecord = 0
        return next
    }

    fun clear() {
        undoStack.clear()
        redoStack.clear()
    }
}
