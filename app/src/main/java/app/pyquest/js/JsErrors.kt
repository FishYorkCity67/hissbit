package app.pyquest.js

import kotlinx.serialization.Serializable

/** Fehler, wie ihn die Laufzeit (assets/js/prelude.js) meldet. */
@Serializable
data class RawJsError(
    val kind: String = "error",
    val name: String = "Error",
    val message: String = "",
    val stack: String = "",
    val text: String = "",
    val phase: String = "",
    val line: Int? = null,
    val col: Int? = null,
)

/**
 * Bereitet JavaScript-Fehler für Lernende auf: Codezeile mit Markierung (wie Node.js),
 * Fehlermeldung, Aufrufkette im eigenen Code und ein erklärender Hinweis.
 */
object JsErrors {
    data class Formatted(val text: String, val type: String, val line: Int?, val hint: String?)

    private val frameRegex = Regex("""^\s*at (?:(.+?) \()?(?:async )?main\.js:(\d+):(\d+)\)?\s*$""")
    private val locationRegex = Regex("""main\.js:(\d+):(\d+)""")

    private class Frame(val function: String?, val line: Int, val col: Int)

    fun format(e: RawJsError, code: String, instr: JsInstrumenter.Result, lang: String): Formatted {
        val frames = e.stack.lineSequence().mapNotNull { l ->
            frameRegex.matchEntire(l)?.let { m ->
                val line = m.groupValues[2].toInt()
                val col = instr.originalColumn(line, m.groupValues[3].toInt())
                Frame(m.groupValues[1].ifBlank { null }?.removePrefix("async "), line, col)
            }
        }.toList()
        var line: Int? = frames.firstOrNull()?.line
        var col: Int? = frames.firstOrNull()?.col
        if (line == null) {
            // Syntaxfehler o. Ä.: Ort aus dem Stack-Kopf (Node) oder von window.onerror (App)
            val m = locationRegex.find(e.stack)
            if (m != null) {
                line = m.groupValues[1].toInt()
                col = instr.originalColumn(line, m.groupValues[2].toInt())
            } else if (e.line != null && e.line > 0) {
                line = e.line
                col = e.col?.takeIf { it > 0 }?.let { instr.originalColumn(e.line, it) }
            }
        }
        val lines = code.split('\n')
        if (line != null && line > lines.size) {
            line = null
            col = null
        }

        val sb = StringBuilder()
        if (line != null) {
            sb.append("main.js:").append(line).append('\n')
            val src = lines[line - 1].trimEnd()
            if (src.isNotBlank() && src.length <= 160) {
                sb.append(src).append('\n')
                if (col != null && col >= 1 && col <= src.length + 1) {
                    sb.append(" ".repeat(col - 1)).append("^\n")
                }
            }
            sb.append('\n')
        }
        val type: String
        val message: String
        if (e.kind == "value") {
            type = "Uncaught"
            sb.append("Uncaught ").append(e.text)
            message = e.text
        } else {
            type = e.name.ifBlank { "Error" }
            message = e.message
            sb.append(if (message.isEmpty()) type else "$type: $message")
            if (e.phase == "promise") sb.append(if (lang == "en") "  (in a Promise)" else "  (in einem Promise)")
        }
        if (frames.size > 1) {
            var shown = 0
            var i = 0
            while (i < frames.size && shown < 8) {
                val f = frames[i]
                var repeat = 1
                while (i + repeat < frames.size && frames[i + repeat].let { it.function == f.function && it.line == f.line && it.col == f.col }) repeat++
                sb.append('\n').append(frameText(f))
                if (repeat > 2) {
                    sb.append('\n').append(frameText(f))
                    sb.append('\n').append(
                        if (lang == "en") "    … ${repeat - 2} more identical calls"
                        else "    … ${repeat - 2} weitere gleiche Aufrufe"
                    )
                    shown += 2
                } else if (repeat == 2) {
                    sb.append('\n').append(frameText(f))
                    shown += 1
                }
                shown++
                i += repeat
            }
            if (i < frames.size) sb.append('\n').append("    …")
        }
        return Formatted(sb.toString(), type, line, hint(type, message, lang))
    }

    private fun frameText(f: Frame): String =
        if (f.function != null) "    at ${f.function} (main.js:${f.line}:${f.col})" else "    at main.js:${f.line}:${f.col}"

    private class Hint(val type: String?, val pattern: Regex?, val de: String, val en: String)

    private val hints = listOf(
        Hint("SyntaxError", Regex("Unexpected end of input"),
            "Am Ende fehlt etwas – meist eine schließende Klammer } oder ). Zähle nach, ob jede geöffnete Klammer wieder geschlossen wird.",
            "Something is missing at the end – usually a closing } or ). Check that every opened bracket is closed again."),
        Hint("SyntaxError", Regex("Invalid or unexpected token"),
            "Ein Zeichen passt hier nicht hin. Oft sind es typografische Anführungszeichen („ “ ’) statt \" oder ' – oder ein Text, dessen Anführungszeichen nicht geschlossen wurde.",
            "A character doesn't belong here. Often these are curly quotes (“ ” ’) instead of \" or ' – or a string whose quote was never closed."),
        Hint("SyntaxError", Regex("missing \\) after argument list"),
            "Beim Funktionsaufruf fehlt eine schließende Klammer ) – oder ein Komma zwischen zwei Argumenten, z. B. console.log(\"a\", b).",
            "A closing ) is missing in the function call – or a comma between two arguments, e.g. console.log(\"a\", b)."),
        Hint("SyntaxError", Regex("has already been declared"),
            "Diese Variable wurde schon mit let oder const angelegt. Deklariere sie nur einmal – danach weist du ihr ohne let einen neuen Wert zu.",
            "This variable was already declared with let or const. Declare it only once – after that, assign new values without let."),
        Hint("SyntaxError", Regex("Missing initializer in const declaration"),
            "Eine const-Variable braucht sofort einen Wert, z. B. const preis = 5;",
            "A const variable needs a value right away, e.g. const price = 5;"),
        Hint("SyntaxError", Regex("Unterminated template"),
            "Ein Template-String mit Backticks ` wurde nicht geschlossen.",
            "A template string with backticks ` was never closed."),
        Hint("SyntaxError", Regex("await is only valid"),
            "await funktioniert nur in Funktionen, die mit async markiert sind: async function main() { … }",
            "await only works inside functions marked with async: async function main() { … }"),
        Hint("SyntaxError", Regex("Illegal return statement"),
            "return gibt es nur innerhalb von Funktionen.",
            "return only exists inside functions."),
        Hint("SyntaxError", Regex("Invalid left-hand side in assignment"),
            "Links vom = muss eine Variable stehen. Wolltest du vergleichen? Dafür nimmst du ===.",
            "The left side of = must be a variable. Did you want to compare? Use === for that."),
        Hint("SyntaxError", Regex("Unexpected token 'else'"),
            "Vor dem else steht etwas, das nicht zum if gehört – oft ein Semikolon direkt nach if (…) oder eine fehlende geschweifte Klammer.",
            "Something before the else doesn't belong to the if – often a semicolon right after if (…) or a missing curly brace."),
        Hint("SyntaxError", null,
            "JavaScript versteht die Schreibweise nicht. Prüfe Klammern ( ) { } [ ], Kommas, Anführungszeichen und ob ein Operator fehlt.",
            "JavaScript can't understand how this is written. Check brackets ( ) { } [ ], commas, quotes and whether an operator is missing."),
        Hint("ReferenceError", Regex("Cannot access '.+' before initialization"),
            "Du benutzt die Variable, bevor die Zeile mit let/const erreicht wurde. Verschiebe die Deklaration weiter nach oben.",
            "You're using the variable before the line with let/const has run. Move the declaration further up."),
        Hint("ReferenceError", Regex("is not defined"),
            "JavaScript kennt diesen Namen nicht. Tippfehler (auch Groß-/Kleinschreibung!)? Variable noch nicht angelegt? Oder fehlen Anführungszeichen um einen Text?",
            "JavaScript doesn't know this name. A typo (case matters!)? Variable not created yet? Or are quotes missing around a text?"),
        Hint("ReferenceError", null,
            "Ein Name oder eine Zuweisung ist hier nicht erlaubt.",
            "A name or assignment isn't allowed here."),
        Hint("TypeError", Regex("Assignment to constant variable"),
            "Eine const-Variable kann man nicht neu zuweisen. Nimm let, wenn sich der Wert ändern soll.",
            "You can't reassign a const variable. Use let if the value should change."),
        Hint("TypeError", Regex("Cannot (read|set) propert(y|ies) of (undefined|null)"),
            "Hier steht links vom Punkt undefined oder null. Hat die Variable wirklich einen Wert? Gibt die Funktion etwas mit return zurück? Tipp: obj?.wert bricht ruhig ab.",
            "The value left of the dot is undefined or null. Does the variable really have a value? Does the function return something? Tip: obj?.value stops quietly."),
        Hint("TypeError", Regex("is not a function"),
            "Das ist keine Funktion. Tippfehler im Namen? Oder ist der Wert gar kein Array/Text (z. B. map auf einem Objekt)? Hast du () an etwas angehängt, das keine Funktion ist?",
            "That's not a function. A typo in the name? Or is the value not an array/string (e.g. map on an object)? Did you add () to something that isn't a function?"),
        Hint("TypeError", Regex("is not iterable"),
            "Mit for…of und … (Spread) kann man nur über Arrays, Texte, Maps und Sets laufen – nicht über normale Objekte. Tipp: Object.entries(obj) oder Object.keys(obj).",
            "for…of and … (spread) only work with arrays, strings, maps and sets – not plain objects. Tip: Object.entries(obj) or Object.keys(obj)."),
        Hint("TypeError", Regex("is not a constructor"),
            "Das kann man nicht mit new aufrufen – nur Klassen und Konstruktor-Funktionen.",
            "This can't be called with new – only classes and constructor functions can."),
        Hint("TypeError", Regex("circular structure to JSON"),
            "Das Objekt verweist auf sich selbst – JSON.stringify kann das nicht umwandeln.",
            "The object refers to itself – JSON.stringify can't convert that."),
        Hint("TypeError", Regex("BigInt"),
            "BigInt und normale Zahlen lassen sich nicht mischen. Wandle mit Number(…) oder BigInt(…) um.",
            "BigInt and regular numbers can't be mixed. Convert with Number(…) or BigInt(…)."),
        Hint("TypeError", Regex("Class constructor .+ cannot be invoked without 'new'"),
            "Eine Klasse muss mit new aufgerufen werden: new Hund(\"Rex\").",
            "A class has to be called with new: new Dog(\"Rex\")."),
        Hint("TypeError", null,
            "Ein Wert hat den falschen Typ für diese Operation. Prüfe mit typeof, was in der Variable steckt.",
            "A value has the wrong type for this operation. Check with typeof what's inside the variable."),
        Hint("RangeError", Regex("Maximum call stack size exceeded"),
            "Eine Funktion ruft sich endlos selbst auf. Fehlt die Abbruchbedingung (Basisfall)?",
            "A function keeps calling itself forever. Is the stopping condition (base case) missing?"),
        Hint("RangeError", Regex("Invalid array length"),
            "Die Länge eines Arrays muss eine ganze Zahl ab 0 sein.",
            "An array's length must be a whole number of 0 or more."),
        Hint("RangeError", Regex("Invalid string length"),
            "Der Text wäre viel zu lang geworden – läuft eine Schleife zu oft?",
            "The text would have become far too long – does a loop run too often?"),
        Hint("RangeError", Regex("toFixed\\(\\) digits|precision"),
            "Die Anzahl der Nachkommastellen liegt außerhalb des erlaubten Bereichs (0 bis 100).",
            "The number of decimal places is outside the allowed range (0 to 100)."),
        Hint("RangeError", null,
            "Ein Wert liegt außerhalb des erlaubten Bereichs.",
            "A value is outside the allowed range."),
        Hint("URIError", null,
            "Der Text ist keine gültige URL-Kodierung.",
            "The text isn't valid URL encoding."),
        Hint("Uncaught", null,
            "Dein Programm hat mit throw einen Wert geworfen, den niemand mit try … catch aufgefangen hat.",
            "Your program threw a value with throw that no try … catch caught."),
        Hint(null, null,
            "Dein Programm hat einen Fehler ausgelöst (throw), der nicht mit try … catch abgefangen wurde.",
            "Your program raised an error (throw) that wasn't caught with try … catch."),
    )

    fun hint(type: String, message: String, lang: String): String? {
        val known = setOf("SyntaxError", "ReferenceError", "TypeError", "RangeError", "URIError", "Uncaught")
        val t = if (type in known) type else null
        val h = hints.firstOrNull { (it.type == t) && (it.pattern == null || it.pattern.containsMatchIn(message)) }
            ?: return null
        return if (lang == "en") h.en else h.de
    }
}
