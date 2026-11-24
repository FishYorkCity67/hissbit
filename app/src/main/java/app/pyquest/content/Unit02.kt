package app.pyquest.content

import app.pyquest.data.LessonIcon

fun CourseBuilder.unit02() = unit("Variablen & Datentypen", "Werte speichern, Typen verstehen und Eingaben verarbeiten") {
    lesson("Variablen", "Werte unter einem Namen speichern", LessonIcon.BOX) {
        text("Eine **Variable** ist wie eine beschriftete Box: Du legst einen Wert hinein und kannst ihn später über den Namen wieder herausholen. Mit `=` weist du einen Wert zu.")
        code("""
            name = "Monty"
            alter = 3
            print(name)
            print(alter)
        """, output = """
            Monty
            3
        """)
        text("Variablen kann man jederzeit **neu belegen** – der alte Wert wird überschrieben. Rechts vom `=` darf sogar die Variable selbst stehen:")
        code("""
            punkte = 10
            punkte = punkte + 5
            print(punkte)
        """, output = "15")
        tip("Das `=` ist in Python **keine** Gleichung wie in Mathe, sondern ein Befehl: „Berechne den Wert rechts und speichere ihn unter dem Namen links.“")
        text("""
            Regeln für Variablennamen:
            - nur Buchstaben, Ziffern und `_`
            - nicht mit einer Ziffer beginnen
            - keine Leerzeichen
            - Groß-/Kleinschreibung zählt: `alter` und `Alter` sind verschieden

            In Python schreibt man Namen klein und trennt Wörter mit Unterstrichen: `meine_lieblingszahl` – das nennt man **snake_case**.
        """)
        warn("Schlüsselwörter wie `if`, `for` oder `class` darfst du nicht als Namen verwenden.")

        output("""
            x = 5
            print(x)
        """, "5", "x", "\"5\"", "Fehler")
        output("""
            a = 3
            a = 7
            print(a)
        """, "7", "3", "10", "3\n7", explain = "Die zweite Zuweisung überschreibt den alten Wert.")
        choice("Welcher Variablenname ist **gültig**?", "mein_name", "2name", "mein name", "class", mono = true,
            explain = "Namen dürfen nicht mit einer Ziffer beginnen, keine Leerzeichen enthalten und keine Schlüsselwörter sein.")
        truth("`Alter` und `alter` sind in Python dieselbe Variable.", false, explain = "Python unterscheidet Groß- und Kleinschreibung.")
        fill("Speichere die Zahl 42 in der Variable `antwort` und gib sie aus.", """
            antwort ?? 42
            print(??)
        """, answers = listOf("=", "antwort"), distractors = listOf("==", "\"antwort\"", ":"), output = "42")
        typeOutput("""
            x = 4
            y = x + 3
            x = 10
            print(y)
        """, "7", explain = "`y` wurde berechnet, als `x` noch 4 war. Spätere Änderungen an `x` ändern `y` nicht mehr.")
        arrange("Erstelle die Variable `stadt` mit dem Wert `\"Berlin\"` und gib sie danach aus.", """
            stadt = "Berlin"
            print(stadt)
        """, distractors = listOf("print(\"stadt\")"), output = "Berlin")
        write("Speichere `3` in der Variable `punkte`. Erhöhe `punkte` danach um `5` und gib den neuen Wert aus.",
            solution = """
                punkte = 3
                punkte = punkte + 5
                print(punkte)
            """,
            output = "8",
            mustContain = listOf("punkte"),
            hint = "Erst `punkte = 3`, dann `punkte = punkte + 5` und zum Schluss `print(punkte)`.")
    }

    lesson("Datentypen", "Zahlen, Texte und Wahrheitswerte", LessonIcon.BRAIN) {
        text("""
            Jeder Wert in Python hat einen **Datentyp**. Die vier wichtigsten Grundtypen:
            - `int` – ganze Zahlen wie `42` oder `-7`
            - `float` – Kommazahlen wie `3.14`
            - `str` – Text (String) wie `"Hallo"`
            - `bool` – Wahrheitswerte: `True` oder `False`
        """)
        text("Mit `type()` findest du heraus, welchen Typ ein Wert hat:")
        code("""
            print(type(42))
            print(type(3.14))
            print(type("Hi"))
            print(type(True))
        """, output = """
            <class 'int'>
            <class 'float'>
            <class 'str'>
            <class 'bool'>
        """)
        text("Der Typ bestimmt, was man mit einem Wert machen kann. Zahlen werden addiert, Strings werden aneinandergehängt:")
        code("""
            print(2 + 3)
            print("2" + "3")
        """, output = """
            5
            23
        """)
        warn("`\"5\"` (mit Anführungszeichen) ist ein **String**, `5` ist eine **Zahl**. Das ist ein großer Unterschied!")
        tip("`True` und `False` schreibt man immer mit großem Anfangsbuchstaben.")

        match("42" to "int", "3.14" to "float", "\"Hallo\"" to "str", "True" to "bool")
        output("print(type(7.0))", "<class 'float'>", "<class 'int'>", "<class 'str'>", "7.0")
        output("print(\"4\" + \"4\")", "44", "8", "4 + 4", "Fehler", explain = "Beide Werte sind Strings – `+` hängt sie aneinander.")
        choice("Welcher Wert ist ein **String**?", "\"123\"", "123", "12.3", "True", mono = true)
        truth("`False` ist ein gültiger Wahrheitswert in Python.", true)
        choice("Was passiert bei `print(\"Alter: \" + 14)`?",
            "Ein **TypeError** – Text und Zahl kann man nicht mit `+` verbinden.",
            "Es wird `Alter: 14` ausgegeben.",
            "Es wird `Alter: + 14` ausgegeben.",
            "Python rechnet 14 + 0.")
        typeOutput("print(type(\"True\"))", "<class 'str'>", explain = "Wegen der Anführungszeichen ist das ein String, kein Wahrheitswert.")
        write("Erstelle drei Variablen: `name` mit einem Text, `alter` mit einer ganzen Zahl und `groesse` mit einer Kommazahl. Gib danach mit `print(type(groesse))` den Typ von `groesse` aus.",
            solution = """
                name = "Ada"
                alter = 14
                groesse = 1.65
                print(type(groesse))
            """,
            output = "<class 'float'>",
            mustContain = listOf("name", "alter", "groesse"),
            hint = "Kommazahlen schreibt man mit Punkt: `1.65`.")
    }

    lesson("Typumwandlung", "Aus Text wird Zahl – und umgekehrt", LessonIcon.MAGIC) {
        text("Manchmal musst du einen Wert in einen anderen Typ **umwandeln**. Dafür gibt es Funktionen, die genauso heißen wie die Typen:")
        code("""
            zahl = int("42")
            print(zahl + 8)

            text = str(99)
            print(text + " Luftballons")

            komma = float("2.5")
            print(komma * 2)
        """, output = """
            50
            99 Luftballons
            5.0
        """)
        text("`int()` schneidet bei Kommazahlen die Nachkommastellen einfach ab – es rundet nicht! Zum Runden gibt es `round()`:")
        code("""
            print(int(3.99))
            print(round(3.99))
            print(round(3.14159, 2))
        """, output = """
            3
            4
            3.14
        """)
        warn("Nicht alles lässt sich umwandeln: `int(\"Hallo\")` führt zu einem **ValueError**.")
        tip("Mit `bool()` wird fast alles zu `True` – außer „leeren“ Werten wie `0`, `\"\"` oder `None`.")
        code("print(bool(0), bool(5), bool(\"\"), bool(\"Hi\"))", output = "False True False True")

        output("print(int(\"7\") + 3)", "10", "73", "\"73\"", "Fehler")
        output("print(str(7) + \"3\")", "73", "10", "7 3", "Fehler")
        output("print(int(9.8))", "9", "10", "9.8", "Fehler", explain = "`int()` schneidet ab, es rundet nicht.")
        choice("Welche Umwandlung führt zu einem **Fehler**?", "int(\"zwölf\")", "int(\"12\")", "float(\"1.5\")", "str(12)", mono = true,
            explain = "\"zwölf\" besteht nicht aus Ziffern – Python wirft einen ValueError.")
        typeOutput("print(round(2.678, 1))", "2.7")
        fill("Wandle den Text in eine Zahl um, damit gerechnet werden kann.", """
            eingabe = "20"
            print(??(eingabe) * 2)
        """, answers = listOf("int"), distractors = listOf("str", "text", "number"), output = "40")
        truth("`float(\"3\")` ergibt `3.0`.", true, explain = "`float()` macht aus der Ganzzahl eine Kommazahl.")
        write("Die Variable `preis` enthält den Text `\"19\"`. Wandle ihn in eine Zahl um, addiere `1` und gib das Ergebnis aus.",
            starter = """preis = "19"""",
            solution = """
                preis = "19"
                print(int(preis) + 1)
            """,
            output = "20",
            hint = "`int(preis)` macht aus dem Text eine Zahl, mit der du rechnen kannst.")
    }

    lesson("Eingaben mit input()", "Dein Programm stellt Fragen", LessonIcon.CHAT) {
        text("Mit `input()` kann dein Programm etwas fragen. Der Text in den Klammern wird als Frage angezeigt – und die Antwort bekommst du als **String** zurück.")
        code("""
            name = input("Wie heißt du? ")
            print("Hallo", name)
        """, output = """
            Wie heißt du? Monty
            Hallo Monty
        """, stdin = listOf("Monty"))
        tip("Im **Code-Spielplatz** kannst du `input()` richtig ausprobieren – dort tippst du die Antwort selbst in die Konsole.")
        warn("`input()` liefert **immer einen String** – auch wenn jemand eine Zahl eintippt! Zum Rechnen musst du umwandeln.")
        code("""
            alter = int(input("Wie alt bist du? "))
            print("Nächstes Jahr bist du", alter + 1)
        """, output = """
            Wie alt bist du? 15
            Nächstes Jahr bist du 16
        """, stdin = listOf("15"))
        text("In den Programmieraufgaben werden die Eingaben automatisch eingetippt – sie stehen jeweils in der Aufgabe.")

        choice("Welchen Typ liefert `input()` immer zurück?", "str", "int", "float", "bool", mono = true)
        choice("Jemand tippt bei `x = input()` die Zahl **5** ein. Was ergibt dann `x + x`?", "\"55\"", "10", "Einen Fehler", "\"5 + 5\"", mono = true,
            explain = "`x` ist der String \"5\" – und Strings werden mit `+` aneinandergehängt.")
        truth("Um mit einer Eingabe zu rechnen, muss man sie z.B. mit `int()` umwandeln.", true)
        fill("Lies eine Zahl ein und gib das Dreifache aus.", """
            zahl = ??(input())
            print(zahl * 3)
        """, answers = listOf("int"), distractors = listOf("str", "input", "print"))
        arrange("Frage nach einer Lieblingsfarbe und gib sie danach aus.", """
            farbe = input("Lieblingsfarbe? ")
            print("Du magst", farbe)
        """)
        write("Lies mit `input()` einen Namen ein und gib `Hallo, <Name>!` aus. Getestet wird mit der Eingabe **Ada**.",
            stdin = listOf("Ada"),
            solution = """
                name = input("Name: ")
                print("Hallo, " + name + "!")
            """,
            output = "Hallo, Ada!",
            mustContain = listOf("input"),
            hint = "Mit `+` verbindest du Strings: `\"Hallo, \" + name + \"!\"`.")
        write("Lies zwei Zahlen ein und gib ihre **Summe** aus. Getestet wird mit den Eingaben **4** und **9**.",
            stdin = listOf("4", "9"),
            solution = """
                a = int(input())
                b = int(input())
                print(a + b)
            """,
            output = "13",
            mustContain = listOf("input", "int"),
            hint = "Wandle beide Eingaben mit `int()` um, bevor du addierst.")
    }

    lesson("Kurzschreibweisen", "+=, Mehrfachzuweisung und Tauschen", LessonIcon.STAR) {
        text("Oft möchte man eine Variable um etwas erhöhen. Statt `punkte = punkte + 10` geht es kürzer mit `+=`:")
        code("""
            punkte = 0
            punkte += 10
            punkte += 5
            punkte -= 3
            print(punkte)
        """, output = "12")
        text("Das klappt mit allen Rechenzeichen: `+=`, `-=`, `*=`, `/=`, `//=`, `%=` und `**=`.")
        text("Du kannst auch **mehrere Variablen auf einmal** belegen:")
        code("""
            x, y, z = 1, 2, 3
            print(x, y, z)
            a = b = 0
            print(a, b)
        """, output = """
            1 2 3
            0 0
        """)
        tip("Ein beliebter Python-Trick: zwei Variablen **tauschen** – ganz ohne Hilfsvariable.")
        code("""
            links = "Apfel"
            rechts = "Birne"
            links, rechts = rechts, links
            print(links, rechts)
        """, output = "Birne Apfel")

        output("""
            x = 5
            x += 2
            print(x)
        """, "7", "5", "52", "2")
        output("""
            n = 10
            n *= 3
            n -= 5
            print(n)
        """, "25", "30", "15", "5")
        choice("Was ist eine Kurzform von `geld = geld - 2`?", "geld -= 2", "geld =- 2", "geld - = 2", "geld--", mono = true)
        typeOutput("""
            a, b = 3, 8
            a, b = b, a
            print(a, b)
        """, "8 3")
        truth("`x, y = 1, 2` setzt `x` auf 1 und `y` auf 2.", true)
        fill("Verdopple den Wert von `kontostand` mit einer Kurzschreibweise.", """
            kontostand = 50
            kontostand ?? 2
            print(kontostand)
        """, answers = listOf("*="), distractors = listOf("+=", "**=", "=*"), output = "100")
        match("+=" to "erhöhen", "-=" to "verringern", "*=" to "vervielfachen", "/=" to "teilen")
        write("Starte mit `level = 1`. Erhöhe `level` dreimal mit `+=` um jeweils `1` und gib dann `level` aus.",
            solution = """
                level = 1
                level += 1
                level += 1
                level += 1
                print(level)
            """,
            output = "4",
            mustContain = listOf("+="),
            hint = "Dreimal hintereinander `level += 1` – und am Ende `print(level)`.")
    }

    checkpoint()
}
