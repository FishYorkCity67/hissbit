package app.pyquest.content

import app.pyquest.data.LessonIcon

fun CourseBuilder.unit10() = unit("Pythonische Werkzeuge", "Comprehensions, zip, any/all und Generatoren") {
    lesson("List Comprehensions", "Listen in einer Zeile bauen", LessonIcon.MAGIC) {
        text("Eine **List Comprehension** erzeugt eine neue Liste in **einer einzigen Zeile**. Statt so …")
        code("""
            quadrate = []
            for x in range(1, 6):
                quadrate.append(x * x)
            print(quadrate)
        """, output = "[1, 4, 9, 16, 25]")
        text("… schreibst du einfach:")
        code("""
            quadrate = [x * x for x in range(1, 6)]
            print(quadrate)
        """, output = "[1, 4, 9, 16, 25]")
        text("Der Aufbau ist `[ausdruck for element in sammlung]`. Mit einem `if` am Ende kannst du **filtern**:")
        code("""
            zahlen = [3, 8, 1, 12, 7, 6]
            gerade = [z for z in zahlen if z % 2 == 0]
            print(gerade)
            namen = ["ada", "alan", "grace"]
            print([n.capitalize() for n in namen])
        """, output = """
            [8, 12, 6]
            ['Ada', 'Alan', 'Grace']
        """)
        text("Auch ein `if … else` im Ausdruck ist möglich – dann steht es **vorne**:")
        code("""print(["gerade" if z % 2 == 0 else "ungerade" for z in range(4)])""", output = "['gerade', 'ungerade', 'gerade', 'ungerade']")
        tip("Comprehensions sind schnell und gut lesbar – solange sie kurz bleiben. Wird es kompliziert, ist eine normale Schleife oft verständlicher.")

        output("print([x * 2 for x in [1, 2, 3]])", "[2, 4, 6]", "[1, 2, 3, 1, 2, 3]", "[1, 2, 3]", "6")
        output("print([x for x in range(10) if x > 6])", "[7, 8, 9]", "[6, 7, 8, 9]", "[0, 1, 2, 3, 4, 5, 6]", "[]")
        typeOutput("print([len(w) for w in [\"Py\", \"Code\"]])", "[2, 4]")
        choice("Welche Comprehension liefert die Liste `[0, 1, 4, 9]`?",
            "[x ** 2 for x in range(4)]", "[x * 2 for x in range(4)]", "[x ** 2 for x in range(1, 4)]", "[x for x in range(9)]", mono = true)
        truth("In einer Comprehension steht ein Filter-`if` hinter der `for`-Klausel.", true)
        fill("Erzeuge eine Liste aller Wörter in Großbuchstaben.", """
            woerter = ["rot", "grün"]
            gross = [w.??() ?? w in woerter]
            print(gross)
        """, answers = listOf("upper", "for"), distractors = listOf("lower", "in", "if"), output = "['ROT', 'GRÜN']")
        output("print([\"+\" if n > 0 else \"-\" for n in [3, -1, 2]])", "['+', '-', '+']", "['+', '+']", "['-']", "Fehler")
        write("Erstelle mit einer List Comprehension eine Liste aller Zahlen von 1 bis 30, die durch 3 **und** durch 5 teilbar sind, und gib sie aus.",
            solution = "print([z for z in range(1, 31) if z % 3 == 0 and z % 5 == 0])",
            output = "[15, 30]",
            mustContain = listOf("for", "if", "["),
            hint = "`[z for z in range(1, 31) if z % 3 == 0 and z % 5 == 0]`")
    }

    lesson("Dict- & Set-Comprehensions", "Dictionaries und Mengen in einer Zeile", LessonIcon.BOX) {
        text("Comprehensions gibt es auch für Dictionaries und Sets – mit geschweiften Klammern:")
        code("""
            woerter = ["Apfel", "Kiwi", "Banane"]
            laengen = {w: len(w) for w in woerter}
            print(laengen)
            buchstaben = {w[0] for w in woerter}
            print(sorted(buchstaben))
        """, output = """
            {'Apfel': 5, 'Kiwi': 4, 'Banane': 6}
            ['A', 'B', 'K']
        """)
        text("Damit kannst du Dictionaries elegant umbauen – zum Beispiel filtern oder Schlüssel und Werte tauschen:")
        code("""
            preise = {"Brot": 2.5, "Kaviar": 49.9, "Milch": 1.2}
            guenstig = {k: v for k, v in preise.items() if v < 5}
            print(guenstig)
            codes = {"rot": 1, "grün": 2}
            print({wert: name for name, wert in codes.items()})
        """, output = """
            {'Brot': 2.5, 'Milch': 1.2}
            {1: 'rot', 2: 'grün'}
        """)
        tip("Runde Klammern erzeugen übrigens keine „Tupel-Comprehension“, sondern einen **Generator** – mehr dazu in der letzten Lektion dieser Einheit.")

        output("print({x: x * x for x in range(3)})", "{0: 0, 1: 1, 2: 4}", "[0, 1, 4]", "{0, 1, 4}", "Fehler")
        output("print(len({b for b in \"banane\"}))", "4", "6", "3", "Fehler",
            explain = "Ein Set enthält jeden Buchstaben nur einmal: b, a, n, e – also 4.")
        typeOutput("""
            d = {"a": 1, "b": 2}
            print({v: k for k, v in d.items()})
        """, "{1: 'a', 2: 'b'}")
        truth("`{x for x in liste}` erzeugt ein Set.", true)
        choice("Was erzeugt `{n: len(n) for n in [\"Ada\", \"Bo\"]}`?", "{'Ada': 3, 'Bo': 2}", "[3, 2]", "{3, 2}", "{'Ada', 'Bo'}", mono = true)
        fill("Behalte nur die Fächer mit einer Note besser als 3.", """
            noten = {"Mathe": 2, "Kunst": 4, "Bio": 1}
            gut = {fach: n for fach, n in noten.??() if n < 3}
            print(gut)
        """, answers = listOf("items"), distractors = listOf("keys", "values", "get"), output = "{'Mathe': 2, 'Bio': 1}")
        output("print({w.lower() for w in [\"Py\", \"py\", \"PY\"]})", "{'py'}", "{'Py', 'py', 'PY'}", "['py', 'py', 'py']", "3")
        write("Erstelle mit einer Dict-Comprehension ein Dictionary, das jeder Zahl von 1 bis 5 ihr **Dreifaches** zuordnet, und gib es aus.",
            solution = "print({n: n * 3 for n in range(1, 6)})",
            output = "{1: 3, 2: 6, 3: 9, 4: 12, 5: 15}",
            mustContain = listOf("for", "{"),
            hint = "`{n: n * 3 for n in range(1, 6)}`")
    }

    lesson("zip, enumerate, any & all", "Praktische Helfer für Schleifen", LessonIcon.LOOP) {
        text("Python hat praktische Helfer für Schleifen. `zip()` läuft durch **mehrere** Listen gleichzeitig:")
        code("""
            namen = ["Ada", "Alan", "Grace"]
            jahre = [1815, 1912, 1906]
            for name, jahr in zip(namen, jahre):
                print(f"{name} wurde {jahr} geboren")
        """, output = """
            Ada wurde 1815 geboren
            Alan wurde 1912 geboren
            Grace wurde 1906 geboren
        """)
        text("Mit `dict(zip(...))` baust du aus zwei Listen direkt ein Dictionary. `enumerate()` liefert zusätzlich den Index:")
        code("""
            faecher = ["Mathe", "Kunst"]
            noten = [2, 1]
            print(dict(zip(faecher, noten)))
            for i, fach in enumerate(faecher):
                print(i, fach)
        """, output = """
            {'Mathe': 2, 'Kunst': 1}
            0 Mathe
            1 Kunst
        """)
        text("`any()` prüft, ob **mindestens ein** Element wahr ist, `all()`, ob **alle** wahr sind:")
        code("""
            noten = [2, 1, 4, 3]
            print(any(n == 1 for n in noten))
            print(all(n <= 4 for n in noten))
            print(all(n < 4 for n in noten))
        """, output = """
            True
            True
            False
        """)
        tip("`zip()` hört auf, sobald die **kürzeste** Liste zu Ende ist.")

        output("print(list(zip([1, 2], [\"a\", \"b\"])))", "[(1, 'a'), (2, 'b')]", "[1, 2, 'a', 'b']", "[(1, 2), ('a', 'b')]", "{1: 'a', 2: 'b'}")
        output("print(any([False, False, True]))", "True", "False", "[True]", "1")
        output("print(all([1, 2, 0]))", "False", "True", "0", "Fehler", explain = "`0` zählt als falsch – also sind nicht alle Werte wahr.")
        typeOutput("print(dict(zip([\"x\", \"y\"], [10, 20])))", "{'x': 10, 'y': 20}")
        truth("`zip([1, 2, 3], [\"a\"])` liefert nur ein einziges Paar.", true, explain = "`zip` stoppt bei der kürzesten Liste.")
        fill("Prüfe, ob **alle** Passwörter mindestens 8 Zeichen haben.", """
            passwoerter = ["geheim123", "python!", "abcdefgh"]
            print(??(len(p) >= 8 for p in passwoerter))
        """, answers = listOf("all"), distractors = listOf("any", "sum", "len"), output = "False",
            explain = "`\"python!\"` hat nur 7 Zeichen – also sind **nicht alle** lang genug. `any` würde `True` liefern.")
        output("""
            a = [1, 2, 3]
            b = [10, 20, 30]
            print([x + y for x, y in zip(a, b)])
        """, "[11, 22, 33]", "[1, 2, 3, 10, 20, 30]", "[10, 40, 90]", "Fehler")
        write("Gib mit `zip()` für jedes Produkt eine Zeile im Format `Produkt: Preis €` aus.",
            starter = """
                produkte = ["Brot", "Milch", "Käse"]
                preise = [2.5, 1.2, 3.8]
            """,
            solution = """
                produkte = ["Brot", "Milch", "Käse"]
                preise = [2.5, 1.2, 3.8]
                for produkt, preis in zip(produkte, preise):
                    print(f"{produkt}: {preis} €")
            """,
            output = """
                Brot: 2.5 €
                Milch: 1.2 €
                Käse: 3.8 €
            """,
            mustContain = listOf("zip"),
            hint = "`for produkt, preis in zip(produkte, preise):`")
    }

    lesson("Generatoren", "Werte erzeugen, wenn man sie braucht", LessonIcon.ROCKET) {
        text("Ein **Generator** erzeugt Werte **nach und nach** – erst dann, wenn sie gebraucht werden. Das spart Speicher. Eine Funktion wird zum Generator, wenn sie `yield` statt `return` benutzt:")
        code("""
            def zaehle_bis(n):
                i = 1
                while i <= n:
                    yield i
                    i += 1

            for zahl in zaehle_bis(3):
                print(zahl)
            print(list(zaehle_bis(5)))
        """, output = """
            1
            2
            3
            [1, 2, 3, 4, 5]
        """)
        text("Bei jedem `yield` pausiert die Funktion und gibt einen Wert heraus. Beim nächsten Mal macht sie genau dort weiter. Mit `next()` holst du dir die Werte einzeln:")
        code("""
            def farben():
                yield "rot"
                yield "grün"
                yield "blau"

            g = farben()
            print(next(g))
            print(next(g))
        """, output = """
            rot
            grün
        """)
        text("**Generator-Ausdrücke** sehen aus wie List Comprehensions, aber mit runden Klammern. Ideal für Summen über große Datenmengen:")
        code("print(sum(x * x for x in range(1, 1001)))", output = "333833500")
        tip("Ein Generator kann sogar **unendlich** viele Werte liefern – man holt sich einfach nur so viele, wie man braucht.")

        output("""
            def g():
                yield 1
                yield 2

            print(list(g()))
        """, "[1, 2]", "1\n2", "[1]", "2")
        output("""
            def g():
                yield "a"
                yield "b"

            x = g()
            print(next(x), next(x))
        """, "a b", "a a", "b a", "Fehler")
        typeOutput("print(sum(n for n in range(5)))", "10")
        choice("Welches Schlüsselwort macht eine Funktion zum Generator?", "yield", "return", "generate", "next", mono = true)
        truth("Ein Generator berechnet alle Werte sofort beim Aufruf.", false, explain = "Generatoren sind „faul“: Sie berechnen jeden Wert erst, wenn er angefordert wird.")
        fill("Ergänze den Generator für gerade Zahlen.", """
            def gerade(limit):
                for n in range(0, limit, 2):
                    ?? n

            print(list(gerade(7)))
        """, answers = listOf("yield"), distractors = listOf("return", "print", "next"), output = "[0, 2, 4, 6]")
        output("""
            def quadrate():
                n = 1
                while True:
                    yield n * n
                    n += 1

            g = quadrate()
            print([next(g) for _ in range(4)])
        """, "[1, 4, 9, 16]", "Endlosschleife", "[1, 2, 3, 4]", "[0, 1, 4, 9]")
        write("Schreibe einen Generator `countdown(n)`, der von `n` bis `1` herunterzählt. Gib `list(countdown(5))` aus.",
            solution = """
                def countdown(n):
                    while n > 0:
                        yield n
                        n -= 1

                print(list(countdown(5)))
            """,
            output = "[5, 4, 3, 2, 1]",
            mustContain = listOf("yield"),
            hint = "`while n > 0:` – darin `yield n` und `n -= 1`.")
    }

    checkpoint()
}
