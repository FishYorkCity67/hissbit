package app.pyquest.content

import app.pyquest.data.LessonIcon

fun CourseBuilder.unit08() = unit("Funktionen", "Eigene Bausteine schreiben und wiederverwenden") {
    lesson("Funktionen definieren", "def und Parameter", LessonIcon.MAGIC) {
        text("Eine **Funktion** ist ein benannter Codeblock, den du immer wieder aufrufen kannst – so musst du Code nicht kopieren. Mit `def` definierst du eine Funktion:")
        code("""
            def begruessung():
                print("Hallo!")
                print("Schön, dass du da bist.")

            begruessung()
            begruessung()
        """, output = """
            Hallo!
            Schön, dass du da bist.
            Hallo!
            Schön, dass du da bist.
        """)
        text("Funktionen können **Parameter** haben – Platzhalter für Werte, die du beim Aufruf übergibst. Die übergebenen Werte heißen **Argumente**:")
        code("""
            def gruss(name):
                print(f"Hallo {name}!")

            gruss("Ada")
            gruss("Monty")
        """, output = """
            Hallo Ada!
            Hallo Monty!
        """)
        code("""
            def rechteck(breite, hoehe):
                print("Fläche:", breite * hoehe)

            rechteck(3, 4)
            rechteck(10, 2)
        """, output = """
            Fläche: 12
            Fläche: 20
        """)
        warn("Eine Funktion muss **definiert** sein, bevor du sie aufrufst. Und: Die Definition allein führt noch nichts aus – erst der Aufruf mit `()`.")
        tip("Gute Funktionsnamen beschreiben, was die Funktion tut: `berechne_preis()`, `zeige_menue()`.")

        output("""
            def hallo():
                print("Hi")

            hallo()
            hallo()
        """, "Hi\nHi", "Hi", "Nichts", "hallo")
        output("""
            def f():
                print("A")

            print("B")
        """, "B", "A\nB", "B\nA", "A", explain = "Die Funktion wird nur definiert, aber nie aufgerufen.")
        choice("Mit welchem Schlüsselwort definiert man eine Funktion?", "def", "function", "fun", "define", mono = true)
        typeOutput("""
            def doppelt(x):
                print(x * 2)

            doppelt(21)
        """, "42")
        truth("Beim Aufruf `gruss(\"Ada\")` ist `\"Ada\"` das Argument.", true)
        fill("Definiere die Funktion `jubel` und rufe sie auf.", """
            ?? jubel():
                print("Juhu!")

            ??()
        """, answers = listOf("def", "jubel"), distractors = listOf("fun", "print", "jubel:"), output = "Juhu!")
        arrange("Definiere eine Funktion, die zwei Zahlen addiert und ausgibt, und rufe sie mit 2 und 3 auf.", """
            def summe(a, b):
                print(a + b)
            summe(2, 3)
        """, output = "5")
        write("Schreibe eine Funktion `quadrat(zahl)`, die das Quadrat der Zahl ausgibt. Rufe sie danach mit `7` auf.",
            solution = """
                def quadrat(zahl):
                    print(zahl * zahl)

                quadrat(7)
            """,
            output = "49",
            mustContain = listOf("def quadrat"),
            hint = "`def quadrat(zahl):` und eingerückt `print(zahl * zahl)` – danach `quadrat(7)`.")
    }

    lesson("Rückgabewerte", "Ergebnisse mit return", LessonIcon.MAGIC) {
        text("Bisher haben unsere Funktionen nur etwas ausgegeben. Viel nützlicher ist es, wenn sie ein **Ergebnis zurückgeben** – mit `return`. Das Ergebnis kannst du speichern oder weiterverwenden:")
        code("""
            def quadrat(x):
                return x * x

            ergebnis = quadrat(5)
            print(ergebnis)
            print(quadrat(3) + quadrat(4))
        """, output = """
            25
            25
        """)
        text("`return` **beendet** die Funktion sofort. Code danach wird nicht mehr ausgeführt:")
        code("""
            def ist_volljaehrig(alter):
                if alter >= 18:
                    return True
                return False

            print(ist_volljaehrig(20))
            print(ist_volljaehrig(15))
        """, output = """
            True
            False
        """)
        warn("**print oder return?** `print()` zeigt etwas an, `return` gibt einen Wert an den Aufrufer zurück. Eine Funktion ohne `return` liefert `None`.")
        code("""
            def nur_print(x):
                print(x * 2)

            wert = nur_print(4)
            print(wert)
        """, output = """
            8
            None
        """)
        tip("Eine Funktion kann auch mehrere Werte zurückgeben – als Tupel: `return minimum, maximum`.")

        output("""
            def f(x):
                return x + 1

            print(f(5))
        """, "6", "5", "None", "f(5)")
        output("""
            def f():
                print("A")

            x = f()
            print(x)
        """, "A\nNone", "None", "A", "A\nA")
        output("""
            def test():
                return 1
                print("Hallo")

            print(test())
        """, "1", "Hallo\n1", "1\nHallo", "Hallo", explain = "Nach `return` wird nichts mehr ausgeführt.")
        typeOutput("""
            def mal(a, b):
                return a * b

            print(mal(3, mal(2, 2)))
        """, "12")
        choice("Was liefert eine Funktion **ohne** `return`?", "None", "0", "False", "Einen Fehler", mono = true)
        truth("`return` und `print` machen dasselbe.", false, explain = "`print` zeigt nur an. `return` liefert einen Wert zurück, mit dem weitergerechnet werden kann.")
        fill("Die Funktion soll den größeren der beiden Werte zurückgeben.", """
            def groesser(a, b):
                if a > b:
                    ?? a
                return ??

            print(groesser(4, 9))
        """, answers = listOf("return", "b"), distractors = listOf("print", "a", "yield"), output = "9")
        write("Schreibe eine Funktion `brutto(netto)`, die den Preis **inklusive 19 % Mehrwertsteuer** zurückgibt – gerundet auf 2 Nachkommastellen. Gib dann `brutto(100)` und `brutto(9.99)` aus.",
            solution = """
                def brutto(netto):
                    return round(netto * 1.19, 2)

                print(brutto(100))
                print(brutto(9.99))
            """,
            output = """
                119.0
                11.89
            """,
            mustContain = listOf("return"),
            hint = "`return round(netto * 1.19, 2)`")
    }

    lesson("Parameter-Profis", "Standardwerte und Schlüsselwörter", LessonIcon.PUZZLE) {
        text("Parameter können **Standardwerte** haben. Wird beim Aufruf nichts übergeben, gilt der Standard:")
        code("""
            def gruss(name, text="Hallo"):
                print(f"{text}, {name}!")

            gruss("Ada")
            gruss("Alan", "Moin")
        """, output = """
            Hallo, Ada!
            Moin, Alan!
        """)
        text("Mit **Schlüsselwort-Argumenten** gibst du beim Aufruf an, welcher Parameter welchen Wert bekommt. Dann ist die Reihenfolge egal:")
        code("""
            def profil(name, alter, stadt):
                print(f"{name} ({alter}) aus {stadt}")

            profil("Ada", 36, "London")
            profil(stadt="Berlin", name="Linus", alter=21)
        """, output = """
            Ada (36) aus London
            Linus (21) aus Berlin
        """)
        warn("Parameter **mit** Standardwert müssen **hinter** denen ohne stehen: `def f(a, b=2)` ist erlaubt, `def f(a=1, b)` nicht.")
        tip("Viele eingebaute Funktionen nutzen Schlüsselwort-Argumente, z.B. `print(..., sep=\"-\", end=\"\")` oder `sorted(..., reverse=True)`.")

        output("""
            def f(x, y=10):
                return x + y

            print(f(5))
        """, "15", "5", "10", "Fehler")
        output("""
            def f(x, y=10):
                return x + y

            print(f(5, 1))
        """, "6", "15", "11", "Fehler")
        typeOutput("""
            def teile(a, b):
                return a // b

            print(teile(b=3, a=10))
        """, "3")
        choice("Welche Definition ist **ungültig**?", "def f(a=1, b):", "def f(a, b=2):", "def f(a, b):", "def f(a=1, b=2):", mono = true,
            explain = "Parameter ohne Standardwert dürfen nicht hinter Parametern mit Standardwert stehen.")
        truth("Bei Schlüsselwort-Argumenten spielt die Reihenfolge keine Rolle.", true)
        fill("Gib `a-b-c` aus, indem du ein Schlüsselwort-Argument von `print` nutzt.", """
            print("a", "b", "c", ??="-")
        """, answers = listOf("sep"), distractors = listOf("end", "join", "split"), output = "a-b-c")
        output("""
            def potenz(basis, exp=2):
                return basis ** exp

            print(potenz(3), potenz(2, exp=5))
        """, "9 32", "6 10", "9 10", "Fehler")
        write("Schreibe eine Funktion `kaffee(groesse=\"mittel\", milch=False)`, die einen Text wie `mittel ohne Milch` oder `groß mit Milch` zurückgibt. Gib `kaffee()` und `kaffee(\"groß\", milch=True)` aus.",
            solution = """
                def kaffee(groesse="mittel", milch=False):
                    if milch:
                        return f"{groesse} mit Milch"
                    return f"{groesse} ohne Milch"

                print(kaffee())
                print(kaffee("groß", milch=True))
            """,
            output = """
                mittel ohne Milch
                groß mit Milch
            """,
            mustContain = listOf("def kaffee"),
            hint = "Prüfe mit `if milch:`, welchen Text du zurückgibst.")
    }

    lesson("*args & Gültigkeitsbereich", "Beliebig viele Argumente, lokal und global", LessonIcon.BOX) {
        text("Mit `*args` nimmt eine Funktion **beliebig viele** Argumente an. Sie landen in einem Tupel:")
        code("""
            def summe(*zahlen):
                gesamt = 0
                for z in zahlen:
                    gesamt += z
                return gesamt

            print(summe(1, 2))
            print(summe(1, 2, 3, 4, 5))
        """, output = """
            3
            15
        """)
        text("`**kwargs` sammelt beliebige **Schlüsselwort-Argumente** in einem Dictionary:")
        code("""
            def steckbrief(**infos):
                for key, wert in infos.items():
                    print(f"{key}: {wert}")

            steckbrief(name="Monty", art="Python", alter=3)
        """, output = """
            name: Monty
            art: Python
            alter: 3
        """)
        text("**Gültigkeitsbereich (Scope):** Variablen, die in einer Funktion entstehen, sind **lokal** – sie existieren nur dort. Variablen außerhalb sind **global**.")
        code("""
            x = "global"

            def zeige():
                x = "lokal"
                print(x)

            zeige()
            print(x)
        """, output = """
            lokal
            global
        """)
        warn("Willst du eine globale Variable in einer Funktion **ändern**, brauchst du `global` – sonst gibt es einen **UnboundLocalError**. Meist ist es aber besser, Werte zu übergeben und zurückzugeben.")
        code("""
            zaehler = 0

            def klick():
                global zaehler
                zaehler += 1

            klick()
            klick()
            print(zaehler)
        """, output = "2")

        output("""
            def f(*args):
                return len(args)

            print(f(1, 2, 3))
        """, "3", "1", "6", "(1, 2, 3)")
        output("""
            def f(**kw):
                return kw["a"]

            print(f(a=5, b=7))
        """, "5", "7", "a", "Fehler")
        output("""
            x = 1

            def f():
                x = 2

            f()
            print(x)
        """, "1", "2", "None", "Fehler", explain = "Das `x` in der Funktion ist lokal und ändert das globale `x` nicht.")
        typeOutput("""
            def maximum(*werte):
                return max(werte)

            print(maximum(4, 17, 9))
        """, "17")
        truth("Eine Variable, die innerhalb einer Funktion erstellt wird, ist außerhalb nicht verfügbar.", true)
        choice("Was steht in `args`, wenn man `def f(*args)` mit `f(\"a\", \"b\")` aufruft?", "('a', 'b')", "['a', 'b']", "\"ab\"", "{'a', 'b'}", mono = true)
        fill("Die Funktion soll den globalen Punktestand erhöhen.", """
            punkte = 0

            def bonus():
                ?? punkte
                punkte += 10

            bonus()
            print(punkte)
        """, answers = listOf("global"), distractors = listOf("local", "return", "self"), output = "10")
        write("Schreibe eine Funktion `durchschnitt(*zahlen)`, die den Durchschnitt beliebig vieler Zahlen zurückgibt. Gib `durchschnitt(2, 4, 6)` und `durchschnitt(1, 2)` aus.",
            solution = """
                def durchschnitt(*zahlen):
                    return sum(zahlen) / len(zahlen)

                print(durchschnitt(2, 4, 6))
                print(durchschnitt(1, 2))
            """,
            output = """
                4.0
                1.5
            """,
            mustContain = listOf("*zahlen"),
            hint = "`sum(zahlen) / len(zahlen)` – `zahlen` ist ein Tupel.")
    }

    lesson("Lambda & Funktionen als Werte", "Mini-Funktionen für sorted, map und filter", LessonIcon.STAR) {
        text("In Python sind Funktionen **Werte** – man kann sie in Variablen speichern und anderen Funktionen übergeben:")
        code("""
            def laut(text):
                return text.upper() + "!"

            f = laut
            print(f("hallo"))
        """, output = "HALLO!")
        text("Für kleine Funktionen gibt es eine Kurzform: **lambda**. `lambda x: x * 2` ist eine Funktion, die x verdoppelt – ohne Namen und ohne `return`:")
        code("""
            doppelt = lambda x: x * 2
            print(doppelt(21))

            addiere = lambda a, b: a + b
            print(addiere(3, 4))
        """, output = """
            42
            7
        """)
        text("Richtig praktisch wird das mit `sorted()`, `max()`, `map()` und `filter()`:")
        code("""
            woerter = ["Banane", "Ei", "Apfel"]
            print(sorted(woerter, key=len))
            print(max(woerter, key=len))
            zahlen = [1, 2, 3, 4, 5, 6]
            print(list(map(lambda x: x * 10, zahlen)))
            print(list(filter(lambda x: x % 2 == 0, zahlen)))
        """, output = """
            ['Ei', 'Apfel', 'Banane']
            Banane
            [10, 20, 30, 40, 50, 60]
            [2, 4, 6]
        """)
        tip("`key=len` übergibt die Funktion `len` selbst – ohne Klammern! Mit Klammern würdest du sie sofort aufrufen.")

        output("""
            f = lambda x: x + 3
            print(f(4))
        """, "7", "4", "3", "lambda")
        output("print(sorted([\"ccc\", \"a\", \"bb\"], key=len))", "['a', 'bb', 'ccc']", "['ccc', 'bb', 'a']", "['a', 'ccc', 'bb']", "Fehler")
        typeOutput("print(list(map(lambda x: x * x, [1, 2, 3])))", "[1, 4, 9]")
        output("print(list(filter(lambda w: len(w) > 3, [\"Ei\", \"Kiwi\", \"Melone\"])))", "['Kiwi', 'Melone']", "['Ei']", "['Melone']", "[]")
        choice("Was ist an `sorted(liste, key=len())` falsch?",
            "`len` darf keine Klammern haben – übergeben wird die Funktion selbst.", "`key` gibt es nicht.", "`sorted` braucht immer `reverse`.", "Nichts.")
        truth("Eine Funktion kann man in einer Variable speichern.", true)
        fill("Sortiere die Personen nach Alter und gib die jüngste aus.", """
            personen = [("Ada", 36), ("Alan", 41), ("Grace", 30)]
            juengste = sorted(personen, key=?? p: p[1])
            print(juengste[0][0])
        """, answers = listOf("lambda"), distractors = listOf("def", "len", "return"), output = "Grace")
        write("Sortiere die Liste `woerter` **nach dem letzten Buchstaben** und gib die sortierte Liste aus.",
            starter = "woerter = [\"Python\", \"Code\", \"Bug\", \"Array\"]",
            solution = """
                woerter = ["Python", "Code", "Bug", "Array"]
                print(sorted(woerter, key=lambda w: w[-1]))
            """,
            output = "['Code', 'Bug', 'Python', 'Array']",
            mustContain = listOf("key="),
            hint = "`key=lambda w: w[-1]` sortiert nach dem letzten Zeichen.")
    }

    lesson("Rekursion", "Funktionen, die sich selbst aufrufen", LessonIcon.LOOP) {
        text("Eine Funktion darf sich **selbst aufrufen** – das nennt man **Rekursion**. Damit lassen sich manche Probleme sehr elegant lösen.")
        code("""
            def countdown(n):
                if n == 0:
                    print("Start! 🚀")
                    return
                print(n)
                countdown(n - 1)

            countdown(3)
        """, output = """
            3
            2
            1
            Start! 🚀
        """)
        text("Jede Rekursion braucht einen **Basisfall**, der die Kette beendet – sonst ruft sich die Funktion endlos selbst auf. Ein Klassiker ist die **Fakultät**: 5! = 5 · 4 · 3 · 2 · 1")
        code("""
            def fakultaet(n):
                if n <= 1:
                    return 1
                return n * fakultaet(n - 1)

            print(fakultaet(5))
            print(fakultaet(10))
        """, output = """
            120
            3628800
        """)
        warn("Ohne Basisfall gibt es einen **RecursionError**: Python bricht nach etwa 1000 verschachtelten Aufrufen ab.")
        tip("Denk rekursiv: „Wie kann ich das Problem auf eine **kleinere Version** desselben Problems zurückführen?“")

        output("""
            def f(n):
                if n == 0:
                    return 0
                return n + f(n - 1)

            print(f(4))
        """, "10", "4", "0", "24")
        choice("Was braucht jede rekursive Funktion unbedingt?", "Einen Basisfall, der die Rekursion beendet", "Eine Schleife", "Einen globalen Zähler", "Mindestens zwei Parameter")
        typeOutput("""
            def p(n):
                if n == 0:
                    return 1
                return 2 * p(n - 1)

            print(p(5))
        """, "32")
        truth("Ohne Basisfall läuft eine Rekursion endlos – bis Python mit einem RecursionError abbricht.", true)
        output("""
            def zeige(n):
                if n > 0:
                    zeige(n - 1)
                    print(n)

            zeige(3)
        """, "1\n2\n3", "3\n2\n1", "3", "0\n1\n2\n3", explain = "Erst kommt der rekursive Aufruf, dann das `print` – deshalb wird von 1 aufwärts ausgegeben.")
        fill("Ergänze den Basisfall.", """
            def summe(n):
                if n == ??:
                    return 0
                return n + summe(n - 1)

            print(summe(5))
        """, answers = listOf("0"), distractors = listOf("1", "5", "n"), output = "15")
        arrange("Baue die rekursive Fakultäts-Funktion zusammen und gib 4! aus.", """
            def fak(n):
                if n <= 1:
                    return 1
                return n * fak(n - 1)
            print(fak(4))
        """, output = "24")
        write("Schreibe eine **rekursive** Funktion `fib(n)` für die Fibonacci-Zahlen: fib(0) = 0, fib(1) = 1, sonst fib(n-1) + fib(n-2). Gib `fib(10)` aus.",
            solution = """
                def fib(n):
                    if n < 2:
                        return n
                    return fib(n - 1) + fib(n - 2)

                print(fib(10))
            """,
            output = "55",
            mustContain = listOf("def fib"),
            hint = "Basisfall: `if n < 2: return n`. Sonst `return fib(n - 1) + fib(n - 2)`.")
    }

    checkpoint()
}
