package app.pyquest.content

import app.pyquest.data.LessonIcon

fun CourseBuilder.unit01() = unit("Erste Schritte", "Dein erstes Programm, Rechnen und Fehlermeldungen") {
    lesson("Hallo, Welt!", "Dein allererstes Programm", LessonIcon.CHAT) {
        text("""
            Willkommen bei Python! 🐍 Python ist eine der beliebtesten Programmiersprachen der Welt – sie steckt in Apps, Webseiten, künstlicher Intelligenz und sogar in der Raumfahrt. Und das Beste: Python liest sich fast wie Englisch.
        """)
        text("""
            Mit der Funktion `print()` gibt Python etwas auf dem Bildschirm aus. Was ausgegeben werden soll, steht in den runden Klammern.
        """)
        code("""print("Hallo, Welt!")""", output = "Hallo, Welt!")
        text("""
            Text muss in **Anführungszeichen** stehen. Du kannst doppelte `"..."` oder einfache `'...'` benutzen – beides funktioniert gleich. Einen solchen Text nennt man **String**.
        """)
        code("""
            print("Ich lerne Python!")
            print('Monty ist eine Schlange.')
        """, output = """
            Ich lerne Python!
            Monty ist eine Schlange.
        """)
        tip("Jedes `print()` gibt eine eigene Zeile aus. Python arbeitet dein Programm von oben nach unten ab – Zeile für Zeile.")
        warn("Achte auf die Schreibweise: `print` wird klein geschrieben. `Print` oder `PRINT` kennt Python nicht!")

        choice("Welche Funktion gibt etwas auf dem Bildschirm aus?", "print()", "output()", "show()", "write()", mono = true)
        output("""print("Hallo, Welt!")""", "Hallo, Welt!", "\"Hallo, Welt!\"", "print(Hallo, Welt!)", "Nichts",
            explain = "Die Anführungszeichen markieren nur den Anfang und das Ende des Strings – sie werden nicht mit ausgegeben.")
        truth("Text muss in Python in Anführungszeichen stehen.", true,
            explain = "Ohne Anführungszeichen hält Python den Text für einen Namen (z.B. eine Variable).")
        fill("Gib den Text **Hallo** aus.", """??("Hallo")""", answers = listOf("print"), distractors = listOf("Print", "say", "echo"), output = "Hallo")
        arrange("Gib zuerst **Guten Morgen** und danach **Monty** aus.", """
            print("Guten Morgen")
            print("Monty")
        """, output = """
            Guten Morgen
            Monty
        """)
        choice("Welche Zeile ist **korrekt**?", "print('Hi')", "print(Hi')", "print(\"Hi')", "Print('Hi')", mono = true,
            explain = "Anfang und Ende eines Strings brauchen dieselbe Art von Anführungszeichen – und `print` wird klein geschrieben.")
        typeOutput("""print("3 + 4")""", "3 + 4", explain = "Alles in Anführungszeichen ist Text – Python rechnet hier nicht.")
        write("Schreibe ein Programm, das **Ich lerne Python!** ausgibt.",
            solution = """print("Ich lerne Python!")""",
            output = "Ich lerne Python!",
            hint = "Nutze `print()` und setze den Text in Anführungszeichen.")
    }

    lesson("Rechnen mit Python", "Python als Taschenrechner", LessonIcon.CALC) {
        text("Python kann rechnen – und zwar ziemlich gut. Zahlen schreibst du **ohne** Anführungszeichen.")
        code("""
            print(3 + 4)
            print(10 - 2.5)
            print(6 * 7)
            print(9 / 2)
        """, output = """
            7
            7.5
            42
            4.5
        """)
        text("""
            Die wichtigsten Rechenzeichen (Operatoren):
            - `+` plus und `-` minus
            - `*` mal
            - `/` geteilt – das Ergebnis ist immer eine Kommazahl
            - `//` ganzzahlig geteilt – schneidet die Nachkommastellen ab
            - `%` Modulo – der Rest einer Division
            - `**` hoch (Potenz)
        """)
        code("""
            print(7 // 2)
            print(7 % 2)
            print(2 ** 10)
        """, output = """
            3
            1
            1024
        """)
        tip("Kommazahlen schreibt man in Python mit **Punkt**, nicht mit Komma: `2.5` statt `2,5`.")
        text("Es gilt **Punkt vor Strich** – wie in Mathe. Mit Klammern kannst du die Reihenfolge ändern.")
        code("""
            print(2 + 3 * 4)
            print((2 + 3) * 4)
        """, output = """
            14
            20
        """)

        output("print(5 + 3)", "8", "53", "5 + 3", "\"8\"")
        output("print(10 / 4)", "2.5", "2", "2,5", "3",
            explain = "`/` liefert immer eine Kommazahl. Python schreibt sie mit Punkt.")
        choice("Welcher Operator berechnet den **Rest** einer Division?", "%", "/", "//", "**", mono = true)
        typeOutput("print(2 ** 3)", "8", explain = "`**` bedeutet hoch: 2 · 2 · 2 = 8.")
        output("print(2 + 3 * 4)", "14", "20", "24", explain = "Punkt vor Strich: zuerst 3 * 4 = 12, dann + 2.")
        match(
            "+" to "Addition",
            "*" to "Multiplikation",
            "//" to "Ganzzahl-Division",
            "%" to "Rest (Modulo)",
            "**" to "Potenz",
        )
        truth("`print(\"2 + 2\")` gibt `4` aus.", false, explain = "Wegen der Anführungszeichen ist es Text – ausgegeben wird `2 + 2`.")
        fill("Berechne, wie viele Sekunden eine Stunde hat (60 · 60).", "print(60 ?? 60)", answers = listOf("*"), distractors = listOf("+", "x", "**"), output = "3600")
        write("Mit 20 Flaschen sollen Kisten zu je 6 Flaschen gefüllt werden. Gib in zwei Zeilen aus, wie viele **volle Kisten** es gibt und wie viele Flaschen **übrig** bleiben.",
            solution = """
                print(20 // 6)
                print(20 % 6)
            """,
            output = """
                3
                2
            """,
            mustContain = listOf("//", "%"),
            hint = "`//` liefert die Anzahl der vollen Kisten, `%` den Rest.")
    }

    lesson("Kommentare & Fehler", "Notizen im Code und erste Fehlermeldungen", LessonIcon.BUG) {
        text("Alles hinter einem `#` ist ein **Kommentar**. Python ignoriert ihn – Kommentare sind nur für Menschen, die den Code lesen.")
        code("""
            # Das ist ein Kommentar
            print("Hallo")  # Kommentar am Zeilenende
        """, output = "Hallo")
        text("Fehler gehören zum Programmieren dazu – auch Profis machen ständig welche! Python sagt dir mit einer **Fehlermeldung**, was schiefgelaufen ist. Tippe auf ▶, um den Fehler zu sehen:")
        code("""print("Hallo"""", fails = true)
        text("Ein **SyntaxError** bedeutet: Die Schreibweise stimmt nicht – z.B. fehlt eine Klammer oder ein Anführungszeichen. Ein **NameError** heißt: Python kennt ein Wort nicht – oft ist es ein Tippfehler.")
        code("""prnt("Hallo")""", fails = true)
        tip("Lies Fehlermeldungen von **unten nach oben**: In der letzten Zeile stehen die Art des Fehlers und eine Beschreibung. Oft schlägt Python sogar die Lösung vor („Did you mean: 'print'?“).")
        warn("Python unterscheidet Groß- und Kleinschreibung: `print`, `Print` und `PRINT` sind drei verschiedene Wörter.")

        choice("Womit beginnt ein Kommentar in Python?", "#", "//", "--", "/*", mono = true)
        output("""
            # print("A")
            print("B")
        """, "B", "A\nB", "A", "Nichts", explain = "Die erste Zeile ist auskommentiert und wird nicht ausgeführt.")
        truth("Python ignoriert Kommentare beim Ausführen.", true)
        choice("Welcher Fehler entsteht bei `print(\"Hallo\"` – also mit fehlender Klammer?", "SyntaxError", "NameError", "TypeError", "ZeroDivisionError", mono = true)
        choice("Was bedeutet **NameError: name 'prnt' is not defined**?",
            "Python kennt das Wort `prnt` nicht – vermutlich ein Tippfehler.",
            "Am Ende fehlt eine Klammer.",
            "Man darf nicht durch 0 teilen.",
            "Der Text ist zu lang.")
        choice("Welche Zeile führt zu einem Fehler?", "Print(\"Hi\")", "print(\"Hi\")", "print('Hi')", "print(\"Hi\")  # Gruß", mono = true,
            explain = "`Print` mit großem P kennt Python nicht → NameError.")
        fill("Mache die erste Zeile zu einem Kommentar.", """
            ?? Begrüßung
            print("Hallo")
        """, answers = listOf("#"), distractors = listOf("//", "--"), output = "Hallo")
        write("Dieses Programm hat zwei Fehler. Repariere es, sodass **Ich bin Monty** ausgegeben wird.",
            starter = """Print("Ich bin Monty"""",
            solution = """print("Ich bin Monty")""",
            output = "Ich bin Monty",
            hint = "Achte auf die Groß-/Kleinschreibung von `print` und auf die schließende Klammer.")
    }

    lesson("print() im Detail", "Mehrere Werte, Trennzeichen und Sonderzeichen", LessonIcon.STAR) {
        text("`print()` kann mehrere Werte auf einmal ausgeben. Trenne sie mit **Kommas** – Python setzt automatisch ein Leerzeichen dazwischen.")
        code("""print("Ich bin", 12, "Jahre alt")""", output = "Ich bin 12 Jahre alt")
        text("Mit `sep` legst du fest, was **zwischen** den Werten stehen soll. Mit `end` bestimmst du, was **am Ende** kommt – normalerweise ein Zeilenumbruch.")
        code("""
            print("A", "B", "C", sep="-")
            print("Hallo", end=" ")
            print("Welt")
        """, output = """
            A-B-C
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
            print("Zeile 1\nZeile 2")
            print("Name:\tMonty")
            print("Er sagte: \"Hallo!\"")
        """, output = """
            Zeile 1
            Zeile 2
            Name:	Monty
            Er sagte: "Hallo!"
        """)
        tip("Ein leeres `print()` gibt einfach eine leere Zeile aus – praktisch, um Ausgaben übersichtlicher zu machen.")

        output("""print("A", "B")""", "A B", "AB", "A, B", "\"A\" \"B\"")
        output("""print(1, 2, 3, sep="+")""", "1+2+3", "6", "1 2 3", "1+2+3+")
        choice("Wofür steht `\\n` in einem String?", "Eine neue Zeile", "Ein Leerzeichen", "Das Zeichen n", "Das Ende des Programms")
        typeOutput("""
            print("Py", end="")
            print("thon")
        """, "Python", explain = "Mit `end=\"\"` fügt das erste `print()` keinen Zeilenumbruch an.")
        fill("Gib **10:30** aus, indem du `sep` passend setzt.", "print(10, 30, sep=??)",
            answers = listOf("\":\""), distractors = listOf("\" \"", "\",\"", "\"-\""), output = "10:30")
        truth("`print()` ohne Inhalt gibt eine leere Zeile aus.", true)
        arrange("Bringe die Zeilen in die richtige Reihenfolge, sodass zwischen **Hallo** und **Welt** eine Leerzeile steht.", """
            print("Hallo")
            print()
            print("Welt")
        """, output = """
            Hallo

            Welt
        """)
        write("Gib mit **einem** `print()` den Satz `Python ist toll!` aus – übergib dabei die drei Wörter einzeln, getrennt durch Kommas.",
            solution = """print("Python", "ist", "toll!")""",
            output = "Python ist toll!",
            mustContain = listOf(","),
            hint = "Mehrere Werte trennst du in `print()` mit Kommas: `print(\"a\", \"b\")`.")
    }

    checkpoint()
}
