package app.pyquest.content

import app.pyquest.data.LessonIcon

fun CourseBuilder.unit05() = unit("Schleifen", "Code wiederholen mit while und for") {
    lesson("while-Schleifen", "Wiederholen, solange etwas gilt", LessonIcon.LOOP) {
        text("Eine **Schleife** wiederholt Code. Die `while`-Schleife läuft, **solange** ihre Bedingung wahr ist:")
        code("""
            zaehler = 1
            while zaehler <= 3:
                print("Runde", zaehler)
                zaehler += 1
            print("Fertig!")
        """, output = """
            Runde 1
            Runde 2
            Runde 3
            Fertig!
        """)
        text("Der Ablauf: Python prüft die Bedingung → führt den Block aus → prüft wieder → … bis die Bedingung falsch ist.")
        warn("Wenn sich die Bedingung nie ändert, läuft die Schleife **endlos**! Vergiss nicht, die Variable im Block zu verändern (z.B. `zaehler += 1`). In dieser App bricht ein Zeitlimit Endlosschleifen ab.")
        code("""
            kontostand = 100
            jahre = 0
            while kontostand < 200:
                kontostand *= 1.1
                jahre += 1
            print(f"Nach {jahre} Jahren hat sich das Geld verdoppelt.")
        """, output = "Nach 8 Jahren hat sich das Geld verdoppelt.")

        output("""
            i = 0
            while i < 3:
                print(i)
                i += 1
        """, "0\n1\n2", "1\n2\n3", "0\n1\n2\n3", "3")
        typeOutput("""
            n = 10
            while n > 0:
                n -= 3
            print(n)
        """, "-2", explain = "10 → 7 → 4 → 1 → -2. Erst bei -2 ist die Bedingung `n > 0` falsch.")
        truth("Eine `while`-Schleife läuft so lange, bis ihre Bedingung falsch wird.", true)
        choice("Was passiert bei diesem Code?", "Eine Endlosschleife – `x` ändert sich nie.", "Es wird einmal `1` ausgegeben.", "Ein SyntaxError.", "Nichts.",
            code = """
                x = 1
                while x > 0:
                    print(x)
            """)
        fill("Zähle von 5 bis 1 herunter.", """
            n = 5
            while n ?? 0:
                print(n)
                n ?? 1
        """, answers = listOf(">", "-="), distractors = listOf("<", "+=", "=="), output = """
            5
            4
            3
            2
            1
        """)
        arrange("Gib die Zahlen 1, 2 und 3 mit einer while-Schleife aus.", """
            i = 1
            while i <= 3:
                print(i)
                i += 1
        """, output = """
            1
            2
            3
        """)
        output("""
            summe = 0
            zahl = 1
            while zahl <= 4:
                summe += zahl
                zahl += 1
            print(summe)
        """, "10", "4", "15", "6")
        write("Verdopple `wert` mit einer `while`-Schleife so lange, bis er **größer als 100** ist. Gib dann `wert` aus.",
            starter = "wert = 1",
            solution = """
                wert = 1
                while wert <= 100:
                    wert *= 2
                print(wert)
            """,
            output = "128",
            mustContain = listOf("while"),
            hint = "`while wert <= 100:` und im eingerückten Block `wert *= 2`.")
    }

    lesson("for & range", "Eine feste Anzahl Wiederholungen", LessonIcon.LOOP) {
        text("Wenn du weißt, **wie oft** etwas passieren soll, ist die `for`-Schleife mit `range()` ideal:")
        code("""
            for i in range(5):
                print(i)
        """, output = """
            0
            1
            2
            3
            4
        """)
        text("""
            `range()` gibt es in drei Varianten:
            - `range(stop)` – von 0 bis stop-1
            - `range(start, stop)` – von start bis stop-1
            - `range(start, stop, schritt)` – mit eigener Schrittweite
        """)
        code("""
            print(list(range(3, 7)))
            print(list(range(0, 10, 2)))
            print(list(range(5, 0, -1)))
        """, output = """
            [3, 4, 5, 6]
            [0, 2, 4, 6, 8]
            [5, 4, 3, 2, 1]
        """)
        warn("Der **Stop-Wert ist nicht enthalten**: `range(1, 5)` liefert 1, 2, 3, 4 – aber nicht 5.")
        tip("Brauchst du die Zählvariable nicht, nennt man sie oft `_`: `for _ in range(3): print(\"Hip hip hurra!\")`")

        output("""
            for i in range(3):
                print(i)
        """, "0\n1\n2", "1\n2\n3", "0\n1\n2\n3", "3")
        output("print(list(range(2, 6)))", "[2, 3, 4, 5]", "[2, 3, 4, 5, 6]", "[3, 4, 5, 6]", "[2, 6]")
        typeOutput("""
            for x in range(10, 0, -4):
                print(x)
        """, "10\n6\n2")
        choice("Welcher Aufruf liefert die Zahlen **1 bis 10**?", "range(1, 11)", "range(1, 10)", "range(10)", "range(0, 10)", mono = true)
        truth("`range(5)` enthält die Zahl 5.", false, explain = "`range(5)` liefert 0, 1, 2, 3 und 4.")
        fill("Gib alle geraden Zahlen von 0 bis 8 aus.", """
            for n in range(0, 9, ??):
                print(n)
        """, answers = listOf("2"), distractors = listOf("1", "-2", "0"), output = """
            0
            2
            4
            6
            8
        """)
        output("""
            for i in range(1, 4):
                print("*" * i)
        """, "*\n**\n***", "***\n**\n*", "*\n*\n*", "Fehler")
        write("Gib mit einer `for`-Schleife die **Fünferreihe** aus: 5, 10, 15, …, 50 – jede Zahl in einer eigenen Zeile.",
            solution = """
                for i in range(1, 11):
                    print(i * 5)
            """,
            output = """
                5
                10
                15
                20
                25
                30
                35
                40
                45
                50
            """,
            mustContain = listOf("for"),
            hint = "Entweder `range(1, 11)` und `print(i * 5)` – oder direkt `range(5, 51, 5)`.")
    }

    lesson("Durchlaufen & Aufsummieren", "Strings und Listen Element für Element", LessonIcon.CALC) {
        text("Mit `for` kannst du auch direkt durch einen **String** oder eine **Liste** laufen – Element für Element:")
        code("""
            for buchstabe in "Py":
                print(buchstabe)

            for tier in ["Katze", "Hund", "Python"]:
                print("Ich mag", tier)
        """, output = """
            P
            y
            Ich mag Katze
            Ich mag Hund
            Ich mag Python
        """)
        text("Ein wichtiges Muster ist das **Aufsummieren**: Du startest mit einer Variable bei 0 und addierst in jeder Runde etwas dazu.")
        code("""
            summe = 0
            for zahl in range(1, 101):
                summe += zahl
            print(summe)
        """, output = "5050")
        text("Genauso kannst du **zählen** – zum Beispiel, wie viele Vokale ein Wort hat:")
        code("""
            wort = "Programmieren"
            vokale = 0
            for b in wort.lower():
                if b in "aeiou":
                    vokale += 1
            print(vokale)
        """, output = "5")
        tip("Der kleine Carl Friedrich Gauß soll die Zahlen von 1 bis 100 als Schulkind blitzschnell zusammengezählt haben – Python schafft das in Millisekunden.")

        output("""
            for c in "abc":
                print(c.upper())
        """, "A\nB\nC", "ABC", "abc", "a\nb\nc")
        typeOutput("""
            summe = 0
            for x in [3, 5, 2]:
                summe += x
            print(summe)
        """, "10")
        output("""
            anzahl = 0
            for b in "Banane":
                if b == "a":
                    anzahl += 1
            print(anzahl)
        """, "2", "3", "1", "6")
        truth("Mit `for` kann man direkt über die Zeichen eines Strings laufen.", true)
        fill("Multipliziere alle Zahlen von 1 bis 5 miteinander.", """
            produkt = ??
            for i in range(1, 6):
                produkt ?? i
            print(produkt)
        """, answers = listOf("1", "*="), distractors = listOf("0", "+=", "="), output = "120",
            explain = "Beim Multiplizieren muss man mit 1 starten – mit 0 wäre das Ergebnis immer 0!")
        arrange("Berechne die Summe aller Zahlen in der Liste und gib sie aus.", """
            zahlen = [4, 8, 15]
            summe = 0
            for z in zahlen:
                summe += z
            print(summe)
        """, output = "27")
        choice("Warum startet man beim Aufsummieren mit `summe = 0`?",
            "Damit die erste Addition stimmt – 0 verändert die Summe nicht.",
            "Weil Python sonst abstürzt.",
            "Weil `summe` am Ende immer 0 sein muss.",
            "Das ist egal, man könnte genauso mit 1 starten.")
        write("Zähle, wie viele Wörter in der Liste **mehr als 4 Buchstaben** haben, und gib die Anzahl aus.",
            starter = "woerter = [\"Ei\", \"Python\", \"Code\", \"Schleife\", \"Bug\"]",
            solution = """
                woerter = ["Ei", "Python", "Code", "Schleife", "Bug"]
                anzahl = 0
                for w in woerter:
                    if len(w) > 4:
                        anzahl += 1
                print(anzahl)
            """,
            output = "2",
            mustContain = listOf("for"),
            hint = "Starte mit `anzahl = 0`, laufe durch die Liste und erhöhe `anzahl`, wenn `len(w) > 4` ist.")
    }

    lesson("break & continue", "Schleifen steuern", LessonIcon.BRANCH) {
        text("Mit `break` **beendest** du eine Schleife sofort. Mit `continue` **überspringst** du den Rest des aktuellen Durchlaufs:")
        code("""
            for zahl in range(1, 10):
                if zahl == 5:
                    break
                print(zahl)
        """, output = """
            1
            2
            3
            4
        """)
        code("""
            for zahl in range(1, 7):
                if zahl % 2 == 0:
                    continue
                print(zahl)
        """, output = """
            1
            3
            5
        """)
        text("Ein beliebtes Muster ist `while True` mit `break` – eine Schleife, die so lange läuft, bis eine bestimmte Situation eintritt:")
        code("""
            summe = 0
            while True:
                eingabe = input("Zahl (oder stopp): ")
                if eingabe == "stopp":
                    break
                if not eingabe.isdigit():
                    print("Das ist keine Zahl!")
                    continue
                summe += int(eingabe)
            print("Summe:", summe)
        """, output = """
            Zahl (oder stopp): 5
            Zahl (oder stopp): x
            Das ist keine Zahl!
            Zahl (oder stopp): 7
            Zahl (oder stopp): stopp
            Summe: 12
        """, stdin = listOf("5", "x", "7", "stopp"))
        tip("Bei verschachtelten Schleifen beendet `break` nur die **innerste** Schleife.")

        output("""
            for i in range(10):
                if i == 3:
                    break
                print(i)
        """, "0\n1\n2", "0\n1\n2\n3", "3", "0 bis 9")
        output("""
            for i in range(5):
                if i == 2:
                    continue
                print(i)
        """, "0\n1\n3\n4", "0\n1", "2", "0\n1\n2\n3\n4")
        match(
            "break" to "Schleife sofort beenden",
            "continue" to "zum nächsten Durchlauf springen",
            "while True" to "läuft, bis break kommt",
            "pass" to "tut nichts (Platzhalter)",
        )
        truth("`continue` beendet die ganze Schleife.", false, explain = "`continue` springt nur zum nächsten Durchlauf. Beenden tut `break`.")
        typeOutput("""
            for b in "Python":
                if b == "h":
                    break
                print(b, end="")
        """, "Pyt")
        fill("Gib nur Zahlen aus, die **nicht** durch 3 teilbar sind.", """
            for n in range(1, 8):
                if n % 3 == 0:
                    ??
                print(n)
        """, answers = listOf("continue"), distractors = listOf("break", "pass", "return"), output = """
            1
            2
            4
            5
            7
        """)
        output("""
            n = 0
            while True:
                n += 1
                if n * n > 20:
                    break
            print(n)
        """, "5", "4", "20", "Endlosschleife")
        write("Finde mit einer Schleife die **erste Zahl ab 1**, die sowohl durch 7 als auch durch 11 teilbar ist. Gib sie aus und beende die Schleife mit `break`.",
            solution = """
                zahl = 1
                while True:
                    if zahl % 7 == 0 and zahl % 11 == 0:
                        print(zahl)
                        break
                    zahl += 1
            """,
            output = "77",
            mustContain = listOf("break"),
            hint = "Prüfe `zahl % 7 == 0 and zahl % 11 == 0` – und erhöhe sonst `zahl` um 1.")
    }

    lesson("Verschachtelte Schleifen", "Schleifen in Schleifen und Muster", LessonIcon.PUZZLE) {
        text("Schleifen kann man ineinander packen. Die innere Schleife läuft dann für **jeden** Durchlauf der äußeren komplett durch:")
        code("""
            for zeile in range(1, 4):
                for spalte in range(1, 4):
                    print(zeile * spalte, end=" ")
                print()
        """, output = """
            1 2 3
            2 4 6
            3 6 9
        """)
        text("Damit lassen sich auch Muster zeichnen:")
        code("""
            for i in range(1, 5):
                print("#" * i)
        """, output = """
            #
            ##
            ###
            ####
        """)
        code("""
            for x in range(3):
                for y in range(2):
                    print(x, y)
        """, output = """
            0 0
            0 1
            1 0
            1 1
            2 0
            2 1
        """)
        text("Schleifen können auch ein `else` haben: Es wird ausgeführt, wenn die Schleife **ohne `break`** zu Ende gelaufen ist.")
        code("""
            for n in [3, 5, 7]:
                if n % 2 == 0:
                    print("Gerade Zahl gefunden!")
                    break
            else:
                print("Keine gerade Zahl dabei.")
        """, output = "Keine gerade Zahl dabei.")

        output("""
            for a in range(2):
                for b in range(2):
                    print(a, b)
        """, "0 0\n0 1\n1 0\n1 1", "0 0\n1 1", "0 1\n1 0", "0\n1")
        typeOutput("""
            anzahl = 0
            for i in range(3):
                for j in range(4):
                    anzahl += 1
            print(anzahl)
        """, "12", explain = "Die innere Schleife läuft 3-mal komplett durch: 3 × 4 = 12.")
        output("""
            for i in range(3, 0, -1):
                print("*" * i)
        """, "***\n**\n*", "*\n**\n***", "3\n2\n1", "***")
        truth("Bei verschachtelten Schleifen läuft die innere Schleife für jeden Durchlauf der äußeren komplett durch.", true)
        choice("Wann wird der `else`-Block einer `for`-Schleife ausgeführt?",
            "Wenn die Schleife ohne `break` zu Ende läuft", "Immer nach der Schleife", "Nur bei einem Fehler", "Nur wenn die Schleife leer ist")
        arrange("Zeichne eine Treppe aus Plus-Zeichen: erst `+`, dann `++`, dann `+++`.", """
            for stufe in range(1, 4):
                print("+" * stufe)
        """, distractors = listOf("for stufe in range(3):"), output = """
            +
            ++
            +++
        """)
        fill("Erzeuge ein 3 × 3 Feld aus `o`.", """
            for zeile in range(3):
                for spalte in range(??):
                    print("o", end="")
                ??()
        """, answers = listOf("3", "print"), distractors = listOf("2", "input", "4"), output = """
            ooo
            ooo
            ooo
        """)
        write("Gib das kleine Einmaleins für 1 bis 3 aus – im Format `1 x 1 = 1`. Erst alle Aufgaben mit 1, dann mit 2, dann mit 3 (jeweils mal 1 bis 3).",
            solution = """
                for a in range(1, 4):
                    for b in range(1, 4):
                        print(f"{a} x {b} = {a * b}")
            """,
            output = """
                1 x 1 = 1
                1 x 2 = 2
                1 x 3 = 3
                2 x 1 = 2
                2 x 2 = 4
                2 x 3 = 6
                3 x 1 = 3
                3 x 2 = 6
                3 x 3 = 9
            """,
            mustContain = listOf("for"),
            hint = "Zwei verschachtelte `for`-Schleifen mit `range(1, 4)` und ein f-String.")
    }

    checkpoint()
}
