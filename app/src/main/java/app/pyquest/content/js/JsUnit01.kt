package app.pyquest.content.js

import app.pyquest.content.CourseBuilder
import app.pyquest.data.LessonIcon

fun CourseBuilder.jsUnit01() = unit("Erste Schritte", "Dein erstes Programm, Rechnen und Fehlermeldungen") {
    lesson("Hallo, Welt!", "Dein allererstes JavaScript-Programm", LessonIcon.CHAT) {
        text("""
            Willkommen bei JavaScript! JavaScript ist die Sprache des Webs: Sie läuft in jedem Browser, steckt in fast jeder Webseite, in Apps und – mit Node.js – sogar auf Servern. Wer JavaScript kann, kann Webseiten zum Leben erwecken.
        """)
        text("""
            Mit `console.log()` gibt JavaScript etwas in der **Konsole** aus. Was ausgegeben werden soll, steht in den runden Klammern. Am Ende der Zeile steht ein **Semikolon** `;`.
        """)
        code("""console.log("Hallo, Welt!");""", output = "Hallo, Welt!")
        text("""
            Text muss in **Anführungszeichen** stehen. Du kannst doppelte `"..."` oder einfache `'...'` benutzen – beides funktioniert gleich. Einen solchen Text nennt man **String**.
        """)
        code("""
            console.log("Ich lerne JavaScript!");
            console.log('Monty ist eine Schlange.');
        """, output = """
            Ich lerne JavaScript!
            Monty ist eine Schlange.
        """)
        tip("Jedes `console.log()` gibt eine eigene Zeile aus. JavaScript arbeitet dein Programm von oben nach unten ab – Anweisung für Anweisung.")
        warn("Achte auf die Schreibweise: `console.log` – alles klein und mit Punkt dazwischen. `Console.Log` oder `console.Log` kennt JavaScript nicht!")

        choice("Welche Anweisung gibt etwas in der Konsole aus?", "console.log()", "print()", "echo()", "System.out()", mono = true)
        output("""console.log("Hallo, Welt!");""", "Hallo, Welt!", "\"Hallo, Welt!\"", "console.log(Hallo, Welt!)", "Nichts",
            explain = "Die Anführungszeichen markieren nur Anfang und Ende des Strings – sie werden nicht mit ausgegeben.")
        truth("Text muss in JavaScript in Anführungszeichen stehen.", true,
            explain = "Ohne Anführungszeichen hält JavaScript den Text für einen Namen, zum Beispiel eine Variable.")
        fill("Gib den Text **Hallo** aus.", """console.??("Hallo");""", answers = listOf("log"), distractors = listOf("print", "write", "show"), output = "Hallo")
        arrange("Gib zuerst **Guten Morgen** und danach **Monty** aus.", """
            console.log("Guten Morgen");
            console.log("Monty");
        """, output = """
            Guten Morgen
            Monty
        """)
        choice("Welche Zeile ist **korrekt**?", "console.log('Hi');", "console.log(Hi');", "console.log(\"Hi');", "Console.log('Hi');", mono = true,
            explain = "Anfang und Ende eines Strings brauchen dieselbe Art von Anführungszeichen – und `console` wird klein geschrieben.")
        typeOutput("""console.log("3 + 4");""", "3 + 4", explain = "Alles in Anführungszeichen ist Text – JavaScript rechnet hier nicht.")
        write("Schreibe ein Programm, das **Ich lerne JavaScript!** ausgibt.",
            solution = """console.log("Ich lerne JavaScript!");""",
            output = "Ich lerne JavaScript!",
            hint = "Nutze `console.log()` und setze den Text in Anführungszeichen.")
    }

    lesson("Rechnen mit JavaScript", "JavaScript als Taschenrechner", LessonIcon.CALC) {
        text("JavaScript kann rechnen. Zahlen schreibst du **ohne** Anführungszeichen.")
        code("""
            console.log(3 + 4);
            console.log(10 - 2.5);
            console.log(6 * 7);
            console.log(9 / 2);
        """, output = """
            7
            7.5
            42
            4.5
        """)
        text("""
            Die wichtigsten Rechenzeichen (Operatoren):
            - `+` plus und `-` minus
            - `*` mal und `/` geteilt
            - `%` Modulo – der Rest einer Division
            - `**` hoch (Potenz)
        """)
        code("""
            console.log(7 % 2);
            console.log(2 ** 10);
            console.log(Math.floor(7 / 2));
        """, output = """
            1
            1024
            3
        """)
        tip("`Math.floor()` rundet **ab**. So bekommst du das ganzzahlige Ergebnis einer Division: `Math.floor(7 / 2)` ist `3`.")
        text("Es gilt **Punkt vor Strich** – wie in Mathe. Mit Klammern änderst du die Reihenfolge.")
        code("""
            console.log(2 + 3 * 4);
            console.log((2 + 3) * 4);
        """, output = """
            14
            20
        """)
        tip("Kommazahlen schreibt man mit **Punkt**: `2.5` statt `2,5`. JavaScript kennt nur einen Zahlentyp – `10 / 2` ergibt einfach `5`.")

        output("console.log(5 + 3);", "8", "53", "5 + 3", "\"8\"")
        output("console.log(10 / 4);", "2.5", "2", "2,5", "3",
            explain = "`/` teilt ganz normal – mit Nachkommastellen, falls nötig.")
        choice("Welcher Operator berechnet den **Rest** einer Division?", "%", "/", "//", "**", mono = true,
            explain = "Vorsicht: `//` ist in JavaScript kein Rechenzeichen, sondern der Beginn eines Kommentars!")
        typeOutput("console.log(2 ** 3);", "8", explain = "`**` bedeutet hoch: 2 · 2 · 2 = 8.")
        output("console.log(2 + 3 * 4);", "14", "20", "24", explain = "Punkt vor Strich: zuerst 3 * 4 = 12, dann + 2.")
        match(
            "+" to "Addition",
            "*" to "Multiplikation",
            "%" to "Rest (Modulo)",
            "**" to "Potenz",
            "Math.floor()" to "Abrunden",
        )
        truth("`console.log(\"2 + 2\");` gibt `4` aus.", false, explain = "Wegen der Anführungszeichen ist es Text – ausgegeben wird `2 + 2`.")
        fill("Berechne, wie viele Sekunden eine Stunde hat (60 · 60).", "console.log(60 ?? 60);", answers = listOf("*"), distractors = listOf("+", "x", "**"), output = "3600")
        write("20 Flaschen sollen in Kisten zu je 6 Flaschen. Gib in zwei Zeilen aus, wie viele **volle Kisten** es gibt und wie viele Flaschen **übrig** bleiben.",
            solution = """
                console.log(Math.floor(20 / 6));
                console.log(20 % 6);
            """,
            output = """
                3
                2
            """,
            mustContain = listOf("Math.floor", "%"),
            hint = "`Math.floor(20 / 6)` liefert die vollen Kisten, `20 % 6` den Rest.")
    }

    lesson("Kommentare & Fehler", "Notizen im Code und erste Fehlermeldungen", LessonIcon.BUG) {
        text("Alles hinter `//` ist ein **Kommentar**. JavaScript ignoriert ihn – Kommentare sind nur für Menschen. Längere Kommentare stehen zwischen `/*` und `*/`.")
        code("""
            // Das ist ein Kommentar
            console.log("Hallo"); // Kommentar am Zeilenende
            /* Ein Kommentar
               über mehrere Zeilen */
        """, output = "Hallo")
        text("Fehler gehören zum Programmieren dazu – auch Profis machen ständig welche! JavaScript sagt dir mit einer **Fehlermeldung**, was schiefgelaufen ist. Tippe auf ▶, um den Fehler zu sehen:")
        code("""console.log("Hallo";""", fails = true)
        text("Ein **SyntaxError** bedeutet: Die Schreibweise stimmt nicht – zum Beispiel fehlt eine Klammer oder ein Anführungszeichen. Ein **ReferenceError** heißt: JavaScript kennt ein Wort nicht – oft ist es ein Tippfehler.")
        code("""consle.log("Hallo");""", fails = true)
        tip("Die Fehlermeldung zeigt oben die Zeilennummer (`main.js:1`), darunter deine Codezeile mit einem `^` an der Fehlerstelle und zuletzt die Art des Fehlers mit einer Beschreibung.")
        warn("JavaScript unterscheidet Groß- und Kleinschreibung: `console`, `Console` und `CONSOLE` sind drei verschiedene Wörter.")

        choice("Womit beginnt ein einzeiliger Kommentar in JavaScript?", "//", "#", "--", "**", mono = true)
        output("""
            // console.log("A");
            console.log("B");
        """, "B", "A\nB", "A", "Nichts", explain = "Die erste Zeile ist auskommentiert und wird nicht ausgeführt.")
        truth("JavaScript ignoriert Kommentare beim Ausführen.", true)
        choice("Welcher Fehler entsteht bei `console.log(\"Hallo\";` – also mit fehlender Klammer?", "SyntaxError", "ReferenceError", "TypeError", "RangeError", mono = true)
        choice("Was bedeutet **ReferenceError: consle is not defined**?",
            "JavaScript kennt das Wort `consle` nicht – vermutlich ein Tippfehler.",
            "Am Ende fehlt eine Klammer.",
            "Man darf nicht durch 0 teilen.",
            "Der Text ist zu lang.")
        choice("Welche Zeile führt zu einem Fehler?", "Console.log(\"Hi\");", "console.log(\"Hi\");", "console.log('Hi');", "console.log(\"Hi\"); // Gruß", mono = true,
            explain = "`Console` mit großem C kennt JavaScript nicht → ReferenceError.")
        fill("Mache die erste Zeile zu einem Kommentar.", """
            ?? Begrüßung
            console.log("Hallo");
        """, answers = listOf("//"), distractors = listOf("#", "--"), output = "Hallo")
        write("Dieses Programm hat zwei Fehler. Repariere es, sodass **Ich bin Monty** ausgegeben wird.",
            starter = """Console.log("Ich bin Monty";""",
            solution = """console.log("Ich bin Monty");""",
            output = "Ich bin Monty",
            hint = "Achte auf die Kleinschreibung von `console` und auf die schließende Klammer vor dem Semikolon.")
    }

    lesson("console.log im Detail", "Mehrere Werte, Texte verbinden, Sonderzeichen", LessonIcon.STAR) {
        text("`console.log()` kann mehrere Werte auf einmal ausgeben. Trenne sie mit **Kommas** – JavaScript setzt automatisch ein Leerzeichen dazwischen.")
        code("""console.log("Ich bin", 12, "Jahre alt");""", output = "Ich bin 12 Jahre alt")
        text("Mit `+` kannst du Texte auch **aneinanderhängen**. Dabei entsteht kein automatisches Leerzeichen – das musst du selbst einbauen.")
        code("""
            console.log("Hallo" + "Welt");
            console.log("Hallo " + "Welt");
        """, output = """
            HalloWelt
            Hallo Welt
        """)
        text("""
            Sonderzeichen schreibt man in Strings mit einem **Backslash** `\`:
            - `\n` neue Zeile
            - `\t` Tabulator (Einrückung)
            - `\"` Anführungszeichen im String
            - `\\` ein Backslash
        """)
        code("""
            console.log("Zeile 1\nZeile 2");
            console.log("Name:\tMonty");
            console.log("Er sagte: \"Hallo!\"");
            console.log('It\'s me');
        """, output = """
            Zeile 1
            Zeile 2
            Name:	Monty
            Er sagte: "Hallo!"
            It's me
        """)
        tip("Ein leeres `console.log()` gibt einfach eine leere Zeile aus – praktisch, um Ausgaben übersichtlicher zu machen.")

        output("""console.log("A", "B");""", "A B", "AB", "A, B", "\"A\" \"B\"")
        output("""console.log("A" + "B");""", "AB", "A B", "A + B", "A, B",
            explain = "`+` hängt Texte direkt aneinander – ohne Leerzeichen.")
        output("console.log(1, 2, 3);", "1 2 3", "6", "123", "1, 2, 3")
        choice("Wofür steht `\\n` in einem String?", "Eine neue Zeile", "Ein Leerzeichen", "Das Zeichen n", "Das Ende des Programms")
        typeOutput("""console.log("Java" + "Script");""", "JavaScript")
        fill("Gib **A** und **B** in zwei Zeilen aus – mit nur einem `console.log()`.", """console.log("A??B");""",
            answers = listOf("\\n"), distractors = listOf("\\t", "/n", "n"), output = "A\nB")
        truth("`console.log()` ohne Inhalt gibt eine leere Zeile aus.", true)
        arrange("Bringe die Zeilen in die richtige Reihenfolge, sodass zwischen **Hallo** und **Welt** eine Leerzeile steht.", """
            console.log("Hallo");
            console.log();
            console.log("Welt");
        """, output = """
            Hallo

            Welt
        """)
        write("Gib mit **einem** `console.log()` den Satz `JavaScript ist toll!` aus – übergib dabei die drei Wörter einzeln, getrennt durch Kommas.",
            solution = """console.log("JavaScript", "ist", "toll!");""",
            output = "JavaScript ist toll!",
            mustContain = listOf(","),
            hint = "Mehrere Werte trennst du in `console.log()` mit Kommas: `console.log(\"a\", \"b\");`.")
    }

    checkpoint()
}
