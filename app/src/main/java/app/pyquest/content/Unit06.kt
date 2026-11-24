package app.pyquest.content

import app.pyquest.data.LessonIcon

fun CourseBuilder.unit06() = unit("Listen", "Viele Werte speichern, sortieren und auswerten") {
    lesson("Listen erstellen", "Mehrere Werte in einer Variable", LessonIcon.BOX) {
        text("Eine **Liste** speichert mehrere Werte in einer festen Reihenfolge. Du schreibst sie in eckigen Klammern, getrennt durch Kommas:")
        code("""
            farben = ["rot", "grün", "blau"]
            zahlen = [4, 8, 15, 16, 23, 42]
            leer = []
            print(farben)
            print(len(zahlen))
        """, output = """
            ['rot', 'grün', 'blau']
            6
        """)
        text("Auf Elemente greifst du wie bei Strings über den **Index** zu – auch hier beginnt die Zählung bei 0:")
        code("""
            farben = ["rot", "grün", "blau"]
            print(farben[0])
            print(farben[-1])
        """, output = """
            rot
            blau
        """)
        text("Anders als Strings sind Listen **veränderbar**: Du kannst einzelne Elemente austauschen.")
        code("""
            farben = ["rot", "grün", "blau"]
            farben[1] = "gelb"
            print(farben)
        """, output = "['rot', 'gelb', 'blau']")
        tip("Listen dürfen gemischte Typen enthalten: `[1, \"zwei\", 3.0, True]` – sogar andere Listen.")

        output("""
            l = [10, 20, 30]
            print(l[1])
        """, "20", "10", "30", "[20]")
        output("""
            l = ["a", "b", "c"]
            print(l[-1])
        """, "c", "a", "b", "Fehler")
        typeOutput("print(len([1, 2, 3, 4]))", "4")
        output("""
            l = [1, 2, 3]
            l[0] = 9
            print(l)
        """, "[9, 2, 3]", "[1, 2, 3]", "[9, 1, 2, 3]", "Fehler")
        truth("Listen sind veränderbar – man kann einzelne Elemente austauschen.", true)
        choice("Wie erstellt man eine **leere** Liste?", "[]", "{}", "()", "list[]", mono = true,
            explain = "`{}` ist ein leeres Dictionary und `()` ein leeres Tupel.")
        fill("Gib das **zweite** Element der Liste aus.", """
            planeten = ["Merkur", "Venus", "Erde"]
            print(planeten[??])
        """, answers = listOf("1"), distractors = listOf("2", "0", "-1"), output = "Venus")
        write("Erstelle eine Liste `noten` mit den Zahlen 2, 1, 3 und 2. Gib die Liste und danach ihre Länge aus.",
            solution = """
                noten = [2, 1, 3, 2]
                print(noten)
                print(len(noten))
            """,
            output = """
                [2, 1, 3, 2]
                4
            """,
            mustContain = listOf("["),
            hint = "`noten = [2, 1, 3, 2]`, dann `print(noten)` und `print(len(noten))`.")
    }

    lesson("Listen verändern", "Hinzufügen und Entfernen", LessonIcon.BOX) {
        text("Listen haben praktische Methoden zum Hinzufügen und Entfernen:")
        code("""
            einkauf = ["Brot", "Milch"]
            einkauf.append("Käse")
            einkauf.insert(0, "Kaffee")
            print(einkauf)
        """, output = "['Kaffee', 'Brot', 'Milch', 'Käse']")
        text("""
            - `append(x)` hängt x **hinten** an
            - `insert(i, x)` fügt x an Position i ein
            - `extend(liste)` hängt alle Elemente einer anderen Liste an
        """)
        code("""
            einkauf = ["Kaffee", "Brot", "Milch", "Käse"]
            einkauf.remove("Brot")
            letztes = einkauf.pop()
            print(einkauf)
            print("Entfernt:", letztes)
            del einkauf[0]
            print(einkauf)
        """, output = """
            ['Kaffee', 'Milch']
            Entfernt: Käse
            ['Milch']
        """)
        text("""
            - `remove(x)` löscht das erste Element mit dem Wert x
            - `pop()` entfernt das letzte Element und gibt es zurück, `pop(i)` das an Position i
            - `del liste[i]` löscht das Element an Position i
            - `clear()` leert die Liste komplett
        """)
        warn("`remove(x)` wirft einen **ValueError**, wenn x nicht in der Liste ist. Prüfe vorher mit `if x in liste:`.")

        output("""
            l = [1, 2]
            l.append(3)
            print(l)
        """, "[1, 2, 3]", "[3, 1, 2]", "[1, 2]", "3")
        output("""
            l = ["a", "b", "c"]
            l.remove("b")
            print(l)
        """, "['a', 'c']", "['a', 'b']", "['b']", "Fehler")
        typeOutput("""
            l = [5, 6, 7]
            x = l.pop()
            print(x)
        """, "7")
        output("""
            l = [1, 2]
            l.insert(1, 9)
            print(l)
        """, "[1, 9, 2]", "[9, 1, 2]", "[1, 2, 9]", "[1, 9]")
        match(
            "append(x)" to "hinten anhängen",
            "insert(i, x)" to "an Position einfügen",
            "remove(x)" to "Wert entfernen",
            "pop()" to "letztes entnehmen",
            "clear()" to "alles löschen",
        )
        truth("`append` hängt mehrere Elemente einzeln an, wenn man eine Liste übergibt.", false,
            explain = "`l.append([1, 2])` hängt die Liste als **ein** Element an. Für mehrere Elemente nimmt man `extend`.")
        fill("Füge `\"Banane\"` am Ende der Liste hinzu.", """
            obst = ["Apfel"]
            obst.??("Banane")
            print(obst)
        """, answers = listOf("append"), distractors = listOf("add", "push", "insert"), output = "['Apfel', 'Banane']")
        write("Hänge an `stapel` die Zahl `4` an, entferne dann mit `pop()` **zweimal** das letzte Element und gib am Ende die Liste aus.",
            starter = "stapel = [1, 2, 3]",
            solution = """
                stapel = [1, 2, 3]
                stapel.append(4)
                stapel.pop()
                stapel.pop()
                print(stapel)
            """,
            output = "[1, 2]",
            mustContain = listOf("append", "pop"),
            hint = "`stapel.append(4)`, danach zweimal `stapel.pop()` und zum Schluss `print(stapel)`.")
    }

    lesson("Sortieren & Auswerten", "sorted, min, max, sum und in", LessonIcon.CALC) {
        text("Python kann Listen sortieren und auswerten – mit nur einem Befehl:")
        code("""
            zahlen = [42, 7, 19, 3]
            print(sorted(zahlen))
            print(sorted(zahlen, reverse=True))
            print(min(zahlen), max(zahlen), sum(zahlen))
        """, output = """
            [3, 7, 19, 42]
            [42, 19, 7, 3]
            3 42 71
        """)
        text("`sorted()` liefert eine **neue** sortierte Liste. `liste.sort()` sortiert dagegen die Liste selbst:")
        code("""
            namen = ["Zoe", "Ada", "Linus"]
            namen.sort()
            print(namen)
            namen.reverse()
            print(namen)
        """, output = """
            ['Ada', 'Linus', 'Zoe']
            ['Zoe', 'Linus', 'Ada']
        """)
        text("Mit `in` prüfst du, ob ein Element enthalten ist. `count()` zählt es, `index()` findet seine Position:")
        code("""
            wuerfe = [3, 6, 2, 6, 1]
            print(6 in wuerfe)
            print(wuerfe.count(6))
            print(wuerfe.index(2))
        """, output = """
            True
            2
            2
        """)
        tip("Den Durchschnitt berechnest du mit `sum(liste) / len(liste)`.")
        warn("`x = liste.sort()` ist ein Klassiker-Fehler: `sort()` gibt `None` zurück – in `x` steht dann nichts Brauchbares!")

        output("print(sorted([3, 1, 2]))", "[1, 2, 3]", "[3, 2, 1]", "[3, 1, 2]", "None")
        output("print(max([4, 9, 2]))", "9", "4", "2", "[9]")
        typeOutput("""
            noten = [1, 2, 3]
            print(sum(noten) / len(noten))
        """, "2.0")
        output("""
            l = [3, 1, 2]
            x = l.sort()
            print(x)
        """, "None", "[1, 2, 3]", "[3, 1, 2]", "Fehler", explain = "`sort()` sortiert die Liste selbst und gibt `None` zurück.")
        choice("Welcher Befehl liefert eine **neue**, absteigend sortierte Liste?", "sorted(l, reverse=True)", "l.sort()", "sorted(l)", "l.reverse()", mono = true)
        truth("`5 in [1, 5, 9]` ergibt `True`.", true)
        fill("Gib den kleinsten und den größten Wert aus.", """
            temps = [12, -3, 25, 8]
            print(??(temps), ??(temps))
        """, answers = listOf("min", "max"), distractors = listOf("sum", "len"), output = "-3 25")
        write("Berechne den **Durchschnitt** der Punkte und gib ihn auf eine Nachkommastelle gerundet aus.",
            starter = "punkte = [17, 23, 11, 30, 19]",
            solution = """
                punkte = [17, 23, 11, 30, 19]
                print(round(sum(punkte) / len(punkte), 1))
            """,
            output = "20.0",
            hint = "Durchschnitt = `sum(punkte) / len(punkte)`. Runden mit `round(wert, 1)`.")
    }

    lesson("Listen & Schleifen", "Durchlaufen und neue Listen bauen", LessonIcon.LOOP) {
        text("Mit `for` läufst du durch alle Elemente einer Liste. Brauchst du zusätzlich die **Position**, hilft `enumerate()`:")
        code("""
            aufgaben = ["Zimmer aufräumen", "Python lernen", "Sport"]
            for nummer, aufgabe in enumerate(aufgaben, start=1):
                print(f"{nummer}. {aufgabe}")
        """, output = """
            1. Zimmer aufräumen
            2. Python lernen
            3. Sport
        """)
        text("Ein wichtiges Muster: Eine **neue Liste aufbauen**. Man startet mit einer leeren Liste und ruft in der Schleife `append` auf:")
        code("""
            zahlen = [1, 2, 3, 4, 5, 6]
            gerade = []
            for z in zahlen:
                if z % 2 == 0:
                    gerade.append(z)
            print(gerade)
        """, output = "[2, 4, 6]")
        code("""
            preise = [10, 25, 4]
            mit_rabatt = []
            for p in preise:
                mit_rabatt.append(p * 0.9)
            print(mit_rabatt)
        """, output = "[9.0, 22.5, 3.6]")
        tip("In der Einheit über Comprehensions lernst du, wie man solche Listen in **einer einzigen Zeile** baut.")

        output("""
            for i, x in enumerate(["a", "b"]):
                print(i, x)
        """, "0 a\n1 b", "1 a\n2 b", "a 0\nb 1", "a\nb")
        typeOutput("""
            ergebnis = []
            for x in [1, 2, 3]:
                ergebnis.append(x * 10)
            print(ergebnis)
        """, "[10, 20, 30]")
        output("""
            woerter = ["Hund", "Elefant", "Maus"]
            lang = []
            for w in woerter:
                if len(w) > 4:
                    lang.append(w)
            print(lang)
        """, "['Elefant']", "['Hund', 'Maus']", "['Elefant', 'Maus']", "[]")
        truth("`enumerate(liste, start=1)` beginnt mit der Nummer 1.", true)
        arrange("Baue eine Liste mit den Quadratzahlen von 1 bis 4 und gib sie aus.", """
            quadrate = []
            for i in range(1, 5):
                quadrate.append(i * i)
            print(quadrate)
        """, output = "[1, 4, 9, 16]")
        fill("Nummeriere die Spieler ab 1.", """
            spieler = ["Ada", "Alan"]
            for nr, name in ??(spieler, start=1):
                print(nr, name)
        """, answers = listOf("enumerate"), distractors = listOf("range", "zip", "len"), output = """
            1 Ada
            2 Alan
        """)
        choice("Was ist der Fehler in diesem Code?",
            "Die Liste wird in jedem Durchlauf neu erstellt – am Ende enthält sie nur `3`.",
            "`append` gibt es nicht.",
            "In Schleifen darf man keine Listen benutzen.",
            "Es gibt keinen Fehler.",
            code = """
                for x in [1, 2, 3]:
                    ergebnis = []
                    ergebnis.append(x)
                print(ergebnis)
            """)
        write("Erstelle aus `namen` eine neue Liste `grosse`, die alle Namen in **Großbuchstaben** enthält, und gib sie aus.",
            starter = "namen = [\"ada\", \"grace\", \"linus\"]",
            solution = """
                namen = ["ada", "grace", "linus"]
                grosse = []
                for n in namen:
                    grosse.append(n.upper())
                print(grosse)
            """,
            output = "['ADA', 'GRACE', 'LINUS']",
            mustContain = listOf("append"),
            hint = "Starte mit `grosse = []` und hänge in der Schleife `n.upper()` an.")
    }

    lesson("Slicing & Kopien", "Teillisten, Kopien und Tabellen", LessonIcon.PUZZLE) {
        text("Slicing funktioniert bei Listen genau wie bei Strings:")
        code("""
            l = [0, 1, 2, 3, 4, 5, 6]
            print(l[2:5])
            print(l[:3])
            print(l[-2:])
            print(l[::2])
            print(l[::-1])
        """, output = """
            [2, 3, 4]
            [0, 1, 2]
            [5, 6]
            [0, 2, 4, 6]
            [6, 5, 4, 3, 2, 1, 0]
        """)
        warn("Achtung, Falle! `b = a` erstellt **keine Kopie**. Beide Namen zeigen auf dieselbe Liste:")
        code("""
            a = [1, 2, 3]
            b = a
            b.append(4)
            print(a)
        """, output = "[1, 2, 3, 4]")
        text("Eine echte Kopie bekommst du mit `a.copy()` oder `a[:]`:")
        code("""
            a = [1, 2, 3]
            b = a.copy()
            b.append(4)
            print(a, b)
        """, output = "[1, 2, 3] [1, 2, 3, 4]")
        text("Listen können andere Listen enthalten – zum Beispiel für Tabellen oder Spielfelder:")
        code("""
            feld = [
                ["X", "O", "X"],
                [" ", "X", "O"],
                ["O", " ", "X"],
            ]
            print(feld[1][2])
            for zeile in feld:
                print(" | ".join(zeile))
        """, output = """
            O
            X | O | X
              | X | O
            O |   | X
        """)

        output("""
            l = [1, 2, 3, 4, 5]
            print(l[1:3])
        """, "[2, 3]", "[1, 2, 3]", "[2, 3, 4]", "[1, 2]")
        output("""
            a = [1]
            b = a
            b.append(2)
            print(a)
        """, "[1, 2]", "[1]", "[2]", "Fehler", explain = "`b = a` ist keine Kopie – beide Namen zeigen auf dieselbe Liste.")
        typeOutput("""
            m = [[1, 2], [3, 4]]
            print(m[1][0])
        """, "3")
        truth("`kopie = original.copy()` erstellt eine unabhängige Kopie der Liste.", true)
        output("print([1, 2, 3][::-1])", "[3, 2, 1]", "[1, 2, 3]", "[3]", "Fehler")
        choice("Wie bekommst du die **letzten zwei** Elemente einer Liste `l`?", "l[-2:]", "l[:2]", "l[2:]", "l[-2]", mono = true)
        fill("Erstelle eine echte Kopie, damit das Original unverändert bleibt.", """
            original = [1, 2, 3]
            kopie = original.??()
            kopie.append(4)
            print(original)
        """, answers = listOf("copy"), distractors = listOf("clone", "list", "new"), output = "[1, 2, 3]")
        write("Gib aus der Tabelle die **Note von Linus** aus (zweite Zeile, zweite Spalte).",
            starter = """
                noten = [
                    ["Ada", 1],
                    ["Linus", 3],
                    ["Grace", 2],
                ]
            """,
            solution = """
                noten = [
                    ["Ada", 1],
                    ["Linus", 3],
                    ["Grace", 2],
                ]
                print(noten[1][1])
            """,
            output = "3",
            hint = "Zweite Zeile = Index 1, zweite Spalte = Index 1: `noten[1][1]`.")
    }

    checkpoint()
}
