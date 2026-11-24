package app.pyquest.content

import app.pyquest.data.LessonIcon

fun CourseBuilder.unit04() = unit("Entscheidungen", "Vergleiche, if, elif, else und logische Operatoren") {
    lesson("Vergleiche", "Wahr oder falsch?", LessonIcon.BRAIN) {
        text("Ein **Vergleich** liefert immer einen Wahrheitswert: `True` oder `False`.")
        code("""
            print(5 > 3)
            print(5 < 3)
            print(5 == 5)
            print(5 != 5)
        """, output = """
            True
            False
            True
            False
        """)
        text("""
            Die Vergleichsoperatoren:
            - `==` gleich
            - `!=` ungleich
            - `<` kleiner, `>` größer
            - `<=` kleiner oder gleich, `>=` größer oder gleich
        """)
        warn("`=` **weist zu**, `==` **vergleicht**. Das zu verwechseln ist einer der häufigsten Anfängerfehler!")
        text("Auch Strings kann man vergleichen – dabei zählt die Groß-/Kleinschreibung. `<` vergleicht alphabetisch:")
        code("""
            print("Python" == "Python")
            print("python" == "Python")
            print("Apfel" < "Birne")
        """, output = """
            True
            False
            True
        """)
        tip("Python erlaubt verkettete Vergleiche wie in Mathe: `1 < x < 10`.")

        output("print(7 > 2)", "True", "False", "7 > 2", "5")
        output("print(4 == 4.0)", "True", "False", "Fehler", "4", explain = "Python vergleicht den Zahlenwert – 4 und 4.0 sind gleich groß.")
        choice("Welcher Operator prüft auf **Ungleichheit**?", "!=", "<>", "=!", "==", mono = true)
        typeOutput("""
            x = 10
            print(x >= 10)
        """, "True")
        truth("`=` und `==` bedeuten in Python dasselbe.", false, explain = "`=` weist einen Wert zu, `==` vergleicht zwei Werte.")
        output("print(\"a\" == \"A\")", "False", "True", "Fehler", "a", explain = "Groß- und Kleinbuchstaben sind verschiedene Zeichen.")
        match("==" to "gleich", "!=" to "ungleich", "<=" to "kleiner oder gleich", ">" to "größer")
        output("""
            alter = 15
            print(13 <= alter < 18)
        """, "True", "False", "Fehler", "15")
    }

    lesson("if & else", "Code nur unter einer Bedingung ausführen", LessonIcon.BRANCH) {
        text("Mit `if` führst du Code **nur dann** aus, wenn eine Bedingung wahr ist. Achte auf den **Doppelpunkt** und die **Einrückung**:")
        code("""
            temperatur = 30
            if temperatur > 25:
                print("Ab ins Freibad!")
            print("Fertig")
        """, output = """
            Ab ins Freibad!
            Fertig
        """)
        text("Alles, was eingerückt ist (4 Leerzeichen), gehört zum `if`-Block. Die letzte Zeile ist nicht eingerückt – sie wird immer ausgeführt.")
        text("Mit `else` legst du fest, was passiert, wenn die Bedingung **nicht** zutrifft:")
        code("""
            alter = 12
            if alter >= 18:
                print("Du darfst wählen.")
            else:
                print("Noch", 18 - alter, "Jahre warten.")
        """, output = "Noch 6 Jahre warten.")
        warn("Vergisst du den Doppelpunkt oder die Einrückung, meldet Python einen **SyntaxError** bzw. **IndentationError**.")
        tip("Der Editor rückt nach einem Doppelpunkt automatisch ein. Mit den Pfeil-Tasten in der Symbolleiste kannst du Einrückungen ändern.")

        output("""
            x = 5
            if x > 3:
                print("groß")
            print("Ende")
        """, "groß\nEnde", "Ende", "groß", "Fehler")
        output("""
            x = 1
            if x > 3:
                print("groß")
            print("Ende")
        """, "Ende", "groß\nEnde", "groß", "Nichts")
        choice("Was fehlt in `if punkte > 100` am Zeilenende?", "Ein Doppelpunkt `:`", "Ein Semikolon `;`", "Eine Klammer `)`", "Nichts")
        fill("Gib `Gerade` oder `Ungerade` aus.", """
            zahl = 7
            if zahl % 2 == 0??
                print("Gerade")
            ??:
                print("Ungerade")
        """, answers = listOf(":", "else"), distractors = listOf(";", "elif", "then"), output = "Ungerade")
        arrange("Wenn es regnet, gib `Schirm mitnehmen` aus, sonst `Sonnenbrille`.", """
            regen = True
            if regen:
                print("Schirm mitnehmen")
            else:
                print("Sonnenbrille")
        """, output = "Schirm mitnehmen")
        truth("Code, der unter einem `if` eingerückt ist, wird nur ausgeführt, wenn die Bedingung wahr ist.", true)
        typeOutput("""
            punkte = 80
            if punkte >= 50:
                print("bestanden")
            else:
                print("nicht bestanden")
        """, "bestanden")
        write("Prüfe die Variable `temperatur`: Ist sie **unter 0**, gib `Es friert!` aus, sonst `Kein Frost.`",
            starter = "temperatur = -3",
            solution = """
                temperatur = -3
                if temperatur < 0:
                    print("Es friert!")
                else:
                    print("Kein Frost.")
            """,
            output = "Es friert!",
            mustContain = listOf("if", "else"),
            hint = "`if temperatur < 0:` – und den Block danach einrücken.")
    }

    lesson("elif", "Mehr als zwei Möglichkeiten", LessonIcon.BRANCH) {
        text("Für mehr als zwei Fälle gibt es `elif` (kurz für „else if“). Python prüft die Bedingungen **von oben nach unten** und führt nur den **ersten** passenden Block aus.")
        code("""
            note = 2
            if note == 1:
                print("Sehr gut")
            elif note == 2:
                print("Gut")
            elif note == 3:
                print("Befriedigend")
            else:
                print("Weiter üben!")
        """, output = "Gut")
        tip("Die Reihenfolge ist wichtig! Prüfe den strengsten Fall zuerst.")
        code("""
            punkte = 95
            if punkte >= 90:
                print("Gold")
            elif punkte >= 70:
                print("Silber")
            elif punkte >= 50:
                print("Bronze")
            else:
                print("Keine Medaille")
        """, output = "Gold")
        warn("Bei 95 Punkten sind alle drei Bedingungen wahr – ausgegeben wird aber nur `Gold`, weil der erste Treffer zählt.")

        output("""
            x = 0
            if x > 0:
                print("positiv")
            elif x < 0:
                print("negativ")
            else:
                print("null")
        """, "null", "positiv", "negativ", "Nichts")
        output("""
            n = 15
            if n > 10:
                print("A")
            elif n > 5:
                print("B")
        """, "A", "B", "A\nB", "Nichts", explain = "Nur der **erste** passende Block wird ausgeführt.")
        truth("Ein `if` kann beliebig viele `elif`-Zweige haben.", true)
        choice("Wie viele `else`-Zweige darf ein `if` höchstens haben?", "Einen", "Keinen", "Beliebig viele", "Zwei")
        typeOutput("""
            alter = 65
            if alter < 13:
                print("Kind")
            elif alter < 18:
                print("Jugendlich")
            elif alter < 65:
                print("Erwachsen")
            else:
                print("Senior")
        """, "Senior")
        fill("Ergänze den fehlenden Zweig.", """
            ampel = "gelb"
            if ampel == "rot":
                print("Stopp")
            ?? ampel == "gelb":
                print("Achtung")
            else:
                print("Fahren")
        """, answers = listOf("elif"), distractors = listOf("else", "else if", "elseif"), output = "Achtung")
        arrange("Gib `kalt` (unter 10 Grad), `angenehm` (unter 25 Grad) oder sonst `heiß` aus.", """
            t = 18
            if t < 10:
                print("kalt")
            elif t < 25:
                print("angenehm")
            else:
                print("heiß")
        """, output = "angenehm")
        write("Gib für `zahl` aus, ob sie `positiv`, `negativ` oder `null` ist.",
            starter = "zahl = -7",
            solution = """
                zahl = -7
                if zahl > 0:
                    print("positiv")
                elif zahl < 0:
                    print("negativ")
                else:
                    print("null")
            """,
            output = "negativ",
            mustContain = listOf("if", "elif", "else"),
            hint = "Drei Fälle: `if zahl > 0:`, `elif zahl < 0:` und `else:`.")
    }

    lesson("and, or & not", "Bedingungen kombinieren", LessonIcon.PUZZLE) {
        text("""
            Mehrere Bedingungen verknüpfst du mit `and`, `or` und `not`:
            - `and` – **beide** müssen wahr sein
            - `or` – **mindestens eine** muss wahr sein
            - `not` – kehrt den Wert um
        """)
        code("""
            alter = 16
            hat_ticket = True
            print(alter >= 16 and hat_ticket)
            print(alter >= 18 or hat_ticket)
            print(not hat_ticket)
        """, output = """
            True
            True
            False
        """)
        code("""
            tag = "Samstag"
            if tag == "Samstag" or tag == "Sonntag":
                print("Wochenende! 🎉")
        """, output = "Wochenende! 🎉")
        warn("`if tag == \"Samstag\" or \"Sonntag\":` ist ein typischer Fehler – das ist **immer** wahr! Du musst den Vergleich auf beiden Seiten wiederholen.")
        tip("Kürzer geht es oft mit `in`: `if tag in (\"Samstag\", \"Sonntag\"):`")

        output("print(True and False)", "False", "True", "None", "Fehler")
        output("print(True or False)", "True", "False", "None", "Fehler")
        output("print(not 5 > 3)", "False", "True", "Fehler", "5")
        typeOutput("""
            x = 7
            print(x > 5 and x < 10)
        """, "True")
        choice("Wann ist `a or b` **falsch**?", "Nur wenn a und b beide falsch sind", "Wenn a falsch ist", "Wenn b falsch ist", "Nie")
        truth("`not (3 == 3)` ergibt `False`.", true)
        fill("Der Eintritt ist frei für Kinder unter 6 **oder** Menschen ab 65.", """
            alter = 70
            if alter < 6 ?? alter >= 65:
                print("Eintritt frei")
        """, answers = listOf("or"), distractors = listOf("and", "not", "||"), output = "Eintritt frei")
        write("Ein Passwort ist gültig, wenn es **mindestens 8 Zeichen** lang ist **und** die Ziffer `1` enthält. Gib `gültig` oder `ungültig` aus.",
            starter = "passwort = \"python123\"",
            solution = """
                passwort = "python123"
                if len(passwort) >= 8 and "1" in passwort:
                    print("gültig")
                else:
                    print("ungültig")
            """,
            output = "gültig",
            mustContain = listOf("and"),
            hint = "Kombiniere `len(passwort) >= 8` und `\"1\" in passwort` mit `and`.")
    }

    lesson("Verschachteln & Kurzform", "if im if und Einzeiler", LessonIcon.BRANCH) {
        text("Bedingungen kann man **verschachteln** – ein `if` im `if`. Jede Ebene wird weiter eingerückt:")
        code("""
            hat_geld = True
            preis = 4
            if hat_geld:
                if preis < 5:
                    print("Eis kaufen 🍦")
                else:
                    print("Zu teuer")
            else:
                print("Kein Geld dabei")
        """, output = "Eis kaufen 🍦")
        text("Für einfache Fälle gibt es eine **Kurzform** in einer Zeile: `wert_wenn_wahr if bedingung else wert_wenn_falsch`")
        code("""
            zahl = 7
            text = "gerade" if zahl % 2 == 0 else "ungerade"
            print(text)
        """, output = "ungerade")
        text("In Bedingungen gilt: Leere Werte wie `0`, `\"\"`, `[]` und `None` zählen als **falsch**, alles andere als **wahr**. So kannst du z.B. prüfen, ob etwas eingegeben wurde:")
        code("""
            name = ""
            if name:
                print("Hallo", name)
            else:
                print("Kein Name eingegeben")
        """, output = "Kein Name eingegeben")
        tip("Zu tiefe Verschachtelung macht Code unübersichtlich. Oft hilft es, Bedingungen mit `and` zu kombinieren.")

        output("""
            x = 10
            print("groß" if x > 5 else "klein")
        """, "groß", "klein", "True", "Fehler")
        output("""
            a = 3
            b = 8
            if a > 1:
                if b > 10:
                    print("X")
                else:
                    print("Y")
            else:
                print("Z")
        """, "Y", "X", "Z", "Nichts")
        truth("Ein leerer String `\"\"` zählt in einer Bedingung als falsch.", true)
        typeOutput("""
            liste = []
            print("leer" if not liste else "voll")
        """, "leer")
        choice("Welche Zeile ist eine gültige Kurzform?",
            "x = \"ja\" if ok else \"nein\"", "x = if ok \"ja\" else \"nein\"", "x = ok ? \"ja\" : \"nein\"", "x = \"ja\" if ok", mono = true,
            explain = "Die Kurzform braucht in Python immer ein `else` – und `? :` gibt es in Python nicht.")
        fill("Weise `status` den Text `volljährig` oder `minderjährig` zu.", """
            alter = 17
            status = "volljährig" ?? alter >= 18 ?? "minderjährig"
            print(status)
        """, answers = listOf("if", "else"), distractors = listOf("?", ":", "then", "elif"), output = "minderjährig",
            explain = "Pythons Kurzform lautet `wert_a if bedingung else wert_b` – das `? :` aus anderen Sprachen gibt es nicht.")
        output("""
            punkte = 0
            if punkte:
                print("Punkte!")
            else:
                print("Keine Punkte")
        """, "Keine Punkte", "Punkte!", "0", "Fehler", explain = "`0` zählt in einer Bedingung als falsch.")
        write("Kino-Check: Gib `Film ab!` aus, wenn `alter` mindestens 12 ist **und** `hat_ticket` wahr ist. Fehlt das Ticket, gib `Erst ein Ticket kaufen` aus. Ist man zu jung, gib `Zu jung` aus.",
            starter = """
                alter = 13
                hat_ticket = False
            """,
            solution = """
                alter = 13
                hat_ticket = False
                if alter >= 12:
                    if hat_ticket:
                        print("Film ab!")
                    else:
                        print("Erst ein Ticket kaufen")
                else:
                    print("Zu jung")
            """,
            output = "Erst ein Ticket kaufen",
            mustContain = listOf("if"),
            hint = "Prüfe zuerst das Alter und dann im eingerückten Block das Ticket.")
    }

    checkpoint()
}
