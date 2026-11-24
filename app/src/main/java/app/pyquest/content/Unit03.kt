package app.pyquest.content

import app.pyquest.data.LessonIcon

fun CourseBuilder.unit03() = unit("Strings", "Mit Texten arbeiten: verbinden, schneiden, formatieren") {
    lesson("Texte verbinden", "+, * und len()", LessonIcon.CHAT) {
        text("Strings kann man mit `+` **verbinden** und mit `*` **wiederholen**:")
        code("""
            vorname = "Ada"
            nachname = "Lovelace"
            print(vorname + " " + nachname)
            print("Ha" * 3)
        """, output = """
            Ada Lovelace
            HaHaHa
        """)
        text("`len()` verrät dir, wie viele Zeichen ein String hat – Leerzeichen zählen mit:")
        code("""
            print(len("Python"))
            print(len("Hallo Welt"))
            print(len(""))
        """, output = """
            6
            10
            0
        """)
        warn("Beim Verbinden mit `+` fügt Python **kein** Leerzeichen ein – das musst du selbst ergänzen.")
        tip("Strings sind **unveränderlich**: Jede Operation erzeugt einen neuen String, das Original bleibt gleich.")

        output("print(\"Py\" + \"thon\")", "Python", "Py thon", "Py+thon", "Fehler")
        output("print(\"ab\" * 3)", "ababab", "ab3", "ab ab ab", "Fehler")
        typeOutput("print(len(\"Monty\"))", "5")
        output("print(len(\"a b\"))", "3", "2", "1", "4", explain = "Das Leerzeichen ist auch ein Zeichen.")
        fill("Verbinde Vor- und Nachnamen mit einem Leerzeichen dazwischen.", """
            vorname = "Grace"
            nachname = "Hopper"
            print(vorname ?? " " ?? nachname)
        """, answers = listOf("+", "+"), distractors = listOf("*", "-"), output = "Grace Hopper")
        truth("`\"Hallo\" + 5` ergibt `Hallo5`.", false, explain = "Text und Zahl lassen sich nicht mit `+` verbinden. Richtig wäre `\"Hallo\" + str(5)`.")
        arrange("Gib eine Linie aus 10 Sternchen aus und darunter das Wort `Titel`.", """
            print("*" * 10)
            print("Titel")
        """, output = """
            **********
            Titel
        """)
        write("Erstelle die Variable `wort = \"Monty\"` und gib aus, wie lang das Wort ist – im Format `Monty hat 5 Zeichen`.",
            solution = """
                wort = "Monty"
                print(wort, "hat", len(wort), "Zeichen")
            """,
            output = "Monty hat 5 Zeichen",
            mustContain = listOf("len("),
            hint = "`print(wort, \"hat\", len(wort), \"Zeichen\")` – die Kommas setzen automatisch Leerzeichen.")
    }

    lesson("Indizes & Slicing", "Einzelne Zeichen und Teilstrings", LessonIcon.PUZZLE) {
        text("Jedes Zeichen in einem String hat eine Position, den **Index**. Achtung: Die Zählung beginnt bei **0**!")
        code("""
            wort = "Python"
            print(wort[0])
            print(wort[1])
            print(wort[5])
        """, output = """
            P
            y
            n
        """)
        text("Mit **negativen** Indizes zählst du von hinten: `-1` ist das letzte Zeichen.")
        code("""
            wort = "Python"
            print(wort[-1])
            print(wort[-2])
        """, output = """
            n
            o
        """)
        text("Mit **Slicing** schneidest du Teile heraus: `wort[start:stop]`. Das Zeichen an der Position `stop` ist **nicht** mehr dabei. Lässt du `start` oder `stop` weg, geht es bis zum Anfang bzw. Ende.")
        code("""
            wort = "Python"
            print(wort[0:2])
            print(wort[2:])
            print(wort[:4])
            print(wort[::-1])
        """, output = """
            Py
            thon
            Pyth
            nohtyP
        """)
        tip("`[::-1]` dreht einen String um – der Schritt `-1` läuft rückwärts.")
        warn("Ein Index außerhalb des Strings (z.B. `wort[10]`) führt zu einem **IndexError**. Beim Slicing passiert das nicht – da wird einfach weniger zurückgegeben.")

        output("""
            s = "Monty"
            print(s[0])
        """, "M", "o", "Monty", "Fehler")
        output("""
            s = "Monty"
            print(s[-1])
        """, "y", "M", "t", "Fehler")
        typeOutput("""
            s = "Python"
            print(s[1:4])
        """, "yth", explain = "Index 1 bis 3 – die 4 ist nicht mehr dabei.")
        output("""
            s = "abcdef"
            print(s[:3])
        """, "abc", "abcd", "def", "cba")
        choice("Welcher Index gehört zum **ersten** Zeichen eines Strings?", "0", "1", "-1", "first", mono = true)
        output("print(\"lager\"[::-1])", "regal", "lager", "r", "Fehler", explain = "Mit `[::-1]` wird der String umgedreht.")
        fill("Schneide das Wort **Welt** aus dem String heraus.", """
            s = "Hallo Welt"
            print(s[??:])
        """, answers = listOf("6"), distractors = listOf("5", "7", "-5"), output = "Welt")
        write("Gib den **ersten** und den **letzten** Buchstaben von `wort = \"Schlange\"` aus – beide mit einem `print()`.",
            starter = "wort = \"Schlange\"",
            solution = """
                wort = "Schlange"
                print(wort[0], wort[-1])
            """,
            output = "S e",
            hint = "Erster Buchstabe: `wort[0]`, letzter: `wort[-1]`.")
    }

    lesson("String-Methoden", "upper, lower, strip, replace, split", LessonIcon.MAGIC) {
        text("Strings bringen viele nützliche **Methoden** mit. Eine Methode rufst du mit einem Punkt auf: `text.methode()`.")
        code("""
            text = "Python macht Spaß"
            print(text.upper())
            print(text.lower())
            print(text.title())
        """, output = """
            PYTHON MACHT SPASS
            python macht spaß
            Python Macht Spaß
        """)
        text("`strip()` entfernt Leerzeichen am Anfang und Ende – praktisch bei Eingaben. `replace()` ersetzt Teile:")
        code("""
            eingabe = "   hallo   "
            print("[" + eingabe.strip() + "]")
            print("Ich mag Kaffee".replace("Kaffee", "Tee"))
        """, output = """
            [hallo]
            Ich mag Tee
        """)
        text("`split()` zerlegt einen String in eine **Liste** von Teilen, `join()` setzt Teile wieder zusammen:")
        code("""
            satz = "Python ist super"
            woerter = satz.split()
            print(woerter)
            print("-".join(woerter))
        """, output = """
            ['Python', 'ist', 'super']
            Python-ist-super
        """)
        tip("Methoden verändern den Original-String nicht, sondern liefern einen **neuen** zurück. Speichere das Ergebnis, wenn du es brauchst: `text = text.upper()`.")

        output("print(\"hallo\".upper())", "HALLO", "hallo", "Hallo", "Fehler")
        output("print(\"[\" + \"  Py  \".strip() + \"]\")", "[Py]", "[  Py  ]", "[Py  ]", "[  Py]",
            explain = "`strip()` entfernt Leerzeichen an **beiden** Enden. Die Klammern machen sichtbar, wo der Text anfängt und aufhört.")
        typeOutput("print(\"Banane\".replace(\"a\", \"o\"))", "Bonone")
        output("print(\"a,b,c\".split(\",\"))", "['a', 'b', 'c']", "a b c", "['a,b,c']", "abc")
        match(
            ".upper()" to "alles groß",
            ".lower()" to "alles klein",
            ".strip()" to "Rand-Leerzeichen weg",
            ".split()" to "in Teile zerlegen",
            ".replace()" to "Teile ersetzen",
        )
        truth("`text.upper()` verändert den String in `text` direkt.", false, explain = "Strings sind unveränderlich. `upper()` liefert einen neuen String zurück.")
        fill("Verbinde die Wörter mit Leerzeichen zu einem Satz.", """
            woerter = ["Ich", "lerne", "Python"]
            print(" ".??(woerter))
        """, answers = listOf("join"), distractors = listOf("split", "add", "strip"), output = "Ich lerne Python")
        write("Die Variable `eingabe` enthält Leerzeichen am Rand. Entferne sie und gib den Text in **Großbuchstaben** aus.",
            starter = "eingabe = \"  monty python  \"",
            solution = """
                eingabe = "  monty python  "
                print(eingabe.strip().upper())
            """,
            output = "MONTY PYTHON",
            hint = "Methoden kann man verketten: `eingabe.strip().upper()`.")
    }

    lesson("f-Strings", "Werte elegant in Text einbauen", LessonIcon.STAR) {
        text("Mit **f-Strings** baust du Werte direkt in einen Text ein. Schreib ein `f` vor die Anführungszeichen und setze Variablen in geschweifte Klammern `{}`:")
        code("""
            name = "Monty"
            alter = 3
            print(f"{name} ist {alter} Jahre alt.")
        """, output = "Monty ist 3 Jahre alt.")
        text("In den Klammern darf sogar gerechnet werden:")
        code("""
            a = 7
            b = 6
            print(f"{a} mal {b} ist {a * b}")
        """, output = "7 mal 6 ist 42")
        text("Nach einem Doppelpunkt kannst du **formatieren**: `:.2f` zeigt zwei Nachkommastellen, `:5` reserviert 5 Zeichen Breite, `:>5` richtet rechts aus.")
        code("""
            preis = 3.14159
            print(f"Preis: {preis:.2f} €")
            print(f"[{42:5}]")
            print(f"[{'ab':>5}]")
        """, output = """
            Preis: 3.14 €
            [   42]
            [   ab]
        """)
        tip("Trick zum Debuggen: `f\"{x=}\"` zeigt Name **und** Wert, z.B. `x=42`.")
        code("""
            x = 42
            print(f"{x=}")
        """, output = "x=42")

        output("""
            name = "Ada"
            print(f"Hallo {name}!")
        """, "Hallo Ada!", "Hallo {name}!", "Hallo name!", "f\"Hallo Ada!\"")
        output("""
            x = 3
            print(f"{x} + {x} = {x + x}")
        """, "3 + 3 = 6", "{x} + {x} = {x + x}", "3 + 3 = 33", "x + x = 6")
        typeOutput("""
            pi = 3.14159
            print(f"{pi:.1f}")
        """, "3.1")
        choice("Was fehlt, damit `{name}` durch den Wert ersetzt wird? `print(\"Hallo {name}\")`",
            "Ein `f` vor dem String", "Ein `\$` vor name", "Doppelte Klammern", "Nichts, es funktioniert schon")
        truth("In f-Strings darf man in `{}` auch rechnen, z.B. `{a + b}`.", true)
        fill("Gib den Preis mit genau zwei Nachkommastellen aus.", """
            preis = 4.5
            print(f"{preis:??} €")
        """, answers = listOf(".2f"), distractors = listOf("2", ".2", "%2"), output = "4.50 €")
        arrange("Baue mit einem f-String die Ausgabe `Monty hat 120 Punkte`.", """
            name = "Monty"
            punkte = 120
            print(f"{name} hat {punkte} Punkte")
        """, output = "Monty hat 120 Punkte")
        write("Gib mit einem f-String aus: `In Wien sind es 21.5 Grad.` – die Temperatur soll auf eine Nachkommastelle gerundet sein.",
            starter = """
                stadt = "Wien"
                grad = 21.456
            """,
            solution = """
                stadt = "Wien"
                grad = 21.456
                print(f"In {stadt} sind es {grad:.1f} Grad.")
            """,
            output = "In Wien sind es 21.5 Grad.",
            mustContain = listOf(":.1f"),
            hint = "Eine Nachkommastelle bekommst du mit `{grad:.1f}`.")
    }

    lesson("Suchen & Prüfen", "in, find, count und Co.", LessonIcon.BRAIN) {
        text("Mit `in` prüfst du, ob ein Teilstring vorkommt. Das Ergebnis ist `True` oder `False`:")
        code("""
            satz = "Ich liebe Python"
            print("Python" in satz)
            print("Java" in satz)
        """, output = """
            True
            False
        """)
        text("`find()` sagt dir, **wo** etwas steht (oder `-1`, wenn es nicht vorkommt). `count()` zählt die Vorkommen:")
        code("""
            s = "Banane"
            print(s.find("na"))
            print(s.find("x"))
            print(s.count("a"))
        """, output = """
            2
            -1
            2
        """)
        text("Mit `startswith()` und `endswith()` prüfst du Anfang und Ende. Methoden wie `isdigit()` prüfen den Inhalt:")
        code("""
            datei = "foto.png"
            print(datei.endswith(".png"))
            print("2026".isdigit())
            print("20a6".isdigit())
        """, output = """
            True
            True
            False
        """)
        tip("`isdigit()` ist perfekt, um vor `int()` zu prüfen, ob eine Eingabe wirklich eine Zahl ist.")

        output("print(\"th\" in \"Python\")", "True", "False", "2", "Fehler")
        output("print(\"Hallo\".find(\"l\"))", "2", "3", "1", "-1", explain = "`find()` liefert die Position des **ersten** Treffers – gezählt ab 0.")
        typeOutput("print(\"Mississippi\".count(\"s\"))", "4")
        choice("Was liefert `\"abc\".find(\"z\")`?", "-1", "0", "None", "Einen Fehler", mono = true)
        truth("`\"123\".isdigit()` ergibt `True`.", true)
        fill("Prüfe, ob der Dateiname mit `.py` endet.", """
            datei = "spiel.py"
            print(datei.??(".py"))
        """, answers = listOf("endswith"), distractors = listOf("startswith", "find", "count"), output = "True")
        output("""
            email = "monty@python.org"
            print("@" in email and email.endswith(".org"))
        """, "True", "False", "@", "Fehler")
        write("Zähle, wie oft der Buchstabe `e` (klein) im Text vorkommt, und gib die Zahl aus.",
            starter = "text = \"Erdbeermarmelade\"",
            solution = """
                text = "Erdbeermarmelade"
                print(text.count("e"))
            """,
            output = "4",
            hint = "`text.count(\"e\")` zählt alle kleinen e – das große E am Anfang zählt nicht mit.")
    }

    checkpoint()
}
