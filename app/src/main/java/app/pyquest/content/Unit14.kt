package app.pyquest.content

import app.pyquest.data.LessonIcon

fun CourseBuilder.unit14() = unit("Fortgeschrittenes Python", "Dekoratoren, Type Hints, match-case und mehr") {
    lesson("Dekoratoren", "Funktionen erweitern mit @", LessonIcon.MAGIC) {
        text("Ein **Dekorator** erweitert eine Funktion, ohne ihren Code zu ändern. Er ist selbst eine Funktion, die eine Funktion bekommt und eine neue zurückgibt:")
        code("""
            def mit_rahmen(funktion):
                def wrapper():
                    print("*" * 10)
                    funktion()
                    print("*" * 10)
                return wrapper

            @mit_rahmen
            def hallo():
                print("  Hallo!")

            hallo()
        """, output = """
            **********
              Hallo!
            **********
        """)
        text("`@mit_rahmen` über der Funktion ist eine Abkürzung für `hallo = mit_rahmen(hallo)`. Damit ein Dekorator für Funktionen mit beliebigen Parametern funktioniert, nutzt man `*args` und `**kwargs`:")
        code("""
            import functools

            def protokoll(funktion):
                @functools.wraps(funktion)
                def wrapper(*args, **kwargs):
                    print(f"Aufruf: {funktion.__name__}{args}")
                    ergebnis = funktion(*args, **kwargs)
                    print(f"Ergebnis: {ergebnis}")
                    return ergebnis
                return wrapper

            @protokoll
            def addiere(a, b):
                return a + b

            addiere(3, 4)
        """, output = """
            Aufruf: addiere(3, 4)
            Ergebnis: 7
        """)
        tip("`@functools.wraps` sorgt dafür, dass die dekorierte Funktion ihren Namen und Docstring behält.")
        text("Die Standardbibliothek bringt nützliche Dekoratoren mit, zum Beispiel `@lru_cache`, der Ergebnisse zwischenspeichert:")
        code("""
            from functools import lru_cache

            @lru_cache
            def fib(n):
                return n if n < 2 else fib(n - 1) + fib(n - 2)

            print(fib(100))
        """, output = "354224848179261915075")

        output("""
            def doppelt(f):
                def w():
                    f()
                    f()
                return w

            @doppelt
            def hi():
                print("Hi")

            hi()
        """, "Hi\nHi", "Hi", "Nichts", "Fehler")
        choice("Wofür steht `@dekorator` über einer Funktion `f`?", "f = dekorator(f)", "dekorator = f(dekorator)", "f wird gelöscht", "f wird zweimal ausgeführt", mono = true)
        typeOutput("""
            def laut(f):
                def w(text):
                    return f(text).upper()
                return w

            @laut
            def gruss(name):
                return "hallo " + name

            print(gruss("ada"))
        """, "HALLO ADA")
        truth("Ein Dekorator ist eine Funktion, die eine Funktion bekommt und eine neue Funktion zurückgibt.", true)
        choice("Warum benutzt man `*args, **kwargs` im Wrapper?",
            "Damit der Dekorator für Funktionen mit beliebigen Parametern funktioniert", "Weil Python es vorschreibt", "Damit die Funktion schneller wird", "Um Fehler zu verstecken")
        fill("Ergänze den Dekorator.", """
            def sternchen(funktion):
                def wrapper():
                    print("*")
                    ??()
                return ??

            @sternchen
            def zeige():
                print("Python")

            zeige()
        """, answers = listOf("funktion", "wrapper"), distractors = listOf("zeige", "sternchen", "print"), output = """
            *
            Python
        """)
        output("""
            from functools import lru_cache

            aufrufe = 0

            @lru_cache
            def quadrat(n):
                global aufrufe
                aufrufe += 1
                return n * n

            quadrat(4)
            quadrat(4)
            quadrat(4)
            print(aufrufe)
        """, "1", "3", "0", "16", explain = "Dank `lru_cache` wird das Ergebnis gespeichert – die Funktion läuft nur beim ersten Mal wirklich.")
        write("Schreibe einen Dekorator `zweimal`, der die dekorierte Funktion zweimal ausführt. Dekoriere damit eine Funktion `jubel()`, die `Hurra!` ausgibt, und rufe sie auf.",
            solution = """
                def zweimal(funktion):
                    def wrapper(*args, **kwargs):
                        funktion(*args, **kwargs)
                        funktion(*args, **kwargs)
                    return wrapper

                @zweimal
                def jubel():
                    print("Hurra!")

                jubel()
            """,
            output = """
                Hurra!
                Hurra!
            """,
            mustContain = listOf("@zweimal", "def zweimal"),
            hint = "Im `wrapper` die Funktion zweimal aufrufen – und am Ende `return wrapper`.")
    }

    lesson("Type Hints", "Typen dazuschreiben", LessonIcon.BRAIN) {
        text("Mit **Type Hints** schreibst du dazu, welche Typen Parameter und Rückgabewerte haben sollen. Python prüft das beim Ausführen **nicht** – aber Editoren und Werkzeuge nutzen die Angaben, um Fehler früh zu finden. Und Menschen verstehen den Code schneller:")
        code("""
            def begruessen(name: str, mal: int = 1) -> str:
                return ("Hallo " + name + "! ") * mal

            print(begruessen("Ada", 2))
        """, output = "Hallo Ada! Hallo Ada!")
        text("Auch Sammlungen kann man genau beschreiben:")
        code("""
            def durchschnitt(werte: list[float]) -> float:
                return sum(werte) / len(werte)

            def inventar() -> dict[str, int]:
                return {"Äpfel": 3, "Birnen": 5}

            print(durchschnitt([1.0, 2.0, 4.5]))
            print(inventar())
        """, output = """
            2.5
            {'Äpfel': 3, 'Birnen': 5}
        """)
        text("Für Werte, die auch fehlen dürfen, schreibt man `str | None`:")
        code("""
            def finde(namen: list[str], anfang: str) -> str | None:
                for n in namen:
                    if n.startswith(anfang):
                        return n
                return None

            print(finde(["Ada", "Alan"], "Al"))
            print(finde(["Ada"], "X"))
        """, output = """
            Alan
            None
        """)
        tip("Auch Variablen kann man annotieren: `punkte: int = 0`.")

        output("""
            def f(x: int) -> int:
                return x * 2

            print(f("ab"))
        """, "abab", "Fehler", "4", "None", explain = "Type Hints werden beim Ausführen nicht geprüft – `\"ab\" * 2` funktioniert einfach.")
        choice("Was bedeutet `-> str` in `def f() -> str:`?", "Die Funktion gibt einen String zurück", "Die Funktion nimmt einen String", "Die Funktion wird zu einem String", "Einen Fehler")
        truth("Python wirft automatisch einen Fehler, wenn ein Type Hint nicht eingehalten wird.", false)
        match("list[int]" to "Liste von Ganzzahlen", "dict[str, float]" to "Text → Kommazahl", "str | None" to "Text oder nichts", "-> bool" to "liefert True/False")
        typeOutput("""
            def summe(zahlen: list[int]) -> int:
                return sum(zahlen)

            print(summe([1, 2, 3]))
        """, "6")
        fill("Ergänze die Type Hints: Der Parameter ist ein String, die Rückgabe eine Ganzzahl.", """
            def laenge(text: ??) -> ??:
                return len(text)

            print(laenge("Monty"))
        """, answers = listOf("str", "int"), distractors = listOf("list", "float", "bool"),
            explain = "Type Hints ändern nichts am Ablauf – sie dokumentieren nur, was erwartet wird. Prüfen kann das ein Tool wie mypy.")
        choice("Welche Annotation passt für eine Funktion, die nichts zurückgibt?", "-> None", "-> void", "-> nothing", "-> 0", mono = true)
        write("Schreibe eine Funktion `ist_teenager(alter: int) -> bool`, die `True` zurückgibt, wenn das Alter zwischen 13 und 19 liegt (inklusive). Gib `ist_teenager(15)` und `ist_teenager(20)` aus.",
            solution = """
                def ist_teenager(alter: int) -> bool:
                    return 13 <= alter <= 19

                print(ist_teenager(15))
                print(ist_teenager(20))
            """,
            output = """
                True
                False
            """,
            test = """
                assert ist_teenager(13) and ist_teenager(19)
                assert not ist_teenager(12)
            """,
            mustContain = listOf("-> bool", ": int"),
            hint = "`return 13 <= alter <= 19`")
    }

    lesson("match-case", "Muster erkennen statt if-Ketten", LessonIcon.BRANCH) {
        text("Mit `match` und `case` prüfst du einen Wert gegen mehrere **Muster**. Das ist oft übersichtlicher als lange `if-elif`-Ketten:")
        code("""
            def http_status(code):
                match code:
                    case 200:
                        return "OK"
                    case 404:
                        return "Nicht gefunden"
                    case 500 | 503:
                        return "Serverfehler"
                    case _:
                        return "Unbekannt"

            print(http_status(404))
            print(http_status(503))
            print(http_status(999))
        """, output = """
            Nicht gefunden
            Serverfehler
            Unbekannt
        """)
        text("""
            - `case _:` fängt alles andere ab (wie `else`)
            - `|` bedeutet „oder“
            - Muster können sogar **Strukturen** zerlegen:
        """)
        code("""
            def befehl(eingabe):
                match eingabe.split():
                    case ["gehe", richtung]:
                        return f"Du gehst nach {richtung}."
                    case ["nimm", *dinge]:
                        return f"Du nimmst: {', '.join(dinge)}"
                    case ["ende"]:
                        return "Tschüss!"
                    case _:
                        return "Unbekannter Befehl"

            print(befehl("gehe norden"))
            print(befehl("nimm schwert schild"))
            print(befehl("tanze"))
        """, output = """
            Du gehst nach norden.
            Du nimmst: schwert, schild
            Unbekannter Befehl
        """)
        text("Mit `if` hinter einem Muster (einem **Guard**) prüfst du zusätzliche Bedingungen:")
        code("""
            def einordnen(zahl):
                match zahl:
                    case 0:
                        return "null"
                    case n if n < 0:
                        return "negativ"
                    case _:
                        return "positiv"

            print(einordnen(-5), einordnen(0), einordnen(7))
        """, output = "negativ null positiv")

        output("""
            x = 2
            match x:
                case 1:
                    print("eins")
                case 2:
                    print("zwei")
                case _:
                    print("?")
        """, "zwei", "eins", "?", "zwei\n?")
        output("""
            farbe = "lila"
            match farbe:
                case "rot" | "orange":
                    print("warm")
                case _:
                    print("andere")
        """, "andere", "warm", "lila", "Nichts")
        choice("Was bedeutet `case _:`?", "Alle übrigen Fälle", "Ein leerer String", "Ein Fehler", "Nur der Wert `_`")
        truth("Bei `match` wird nur der **erste** passende `case` ausgeführt.", true)
        typeOutput("""
            punkt = (0, 5)
            match punkt:
                case (0, y):
                    print("auf der y-Achse bei", y)
                case (x, 0):
                    print("auf der x-Achse bei", x)
                case _:
                    print("irgendwo")
        """, "auf der y-Achse bei 5")
        fill("Ergänze das Muster für „oder“.", """
            tag = "So"
            match tag:
                case "Sa" ?? "So":
                    print("Wochenende")
                case _:
                    print("Werktag")
        """, answers = listOf("|"), distractors = listOf("or", "||", ","), output = "Wochenende")
        output("""
            def f(n):
                match n:
                    case n if n > 100:
                        return "groß"
                    case _:
                        return "klein"

            print(f(500), f(5))
        """, "groß klein", "klein groß", "groß groß", "Fehler")
        write("Schreibe eine Funktion `rechne(a, op, b)`, die mit `match` die Operatoren `\"+\"`, `\"-\"`, `\"*\"` und `\"/\"` unterstützt und bei allem anderen `\"?\"` zurückgibt. Gib `rechne(6, \"*\", 7)` und `rechne(1, \"%\", 2)` aus.",
            solution = """
                def rechne(a, op, b):
                    match op:
                        case "+":
                            return a + b
                        case "-":
                            return a - b
                        case "*":
                            return a * b
                        case "/":
                            return a / b
                        case _:
                            return "?"

                print(rechne(6, "*", 7))
                print(rechne(1, "%", 2))
            """,
            output = """
                42
                ?
            """,
            test = """
                assert rechne(1, "+", 2) == 3
                assert rechne(9, "/", 3) == 3
            """,
            mustContain = listOf("match", "case"),
            hint = "Für jeden Operator ein `case \"+\":` usw. – und am Ende `case _: return \"?\"`.")
    }

    lesson("Iteratoren & Kontextmanager", "Wie for und with wirklich funktionieren", LessonIcon.LOOP) {
        text("Alles, was man mit `for` durchlaufen kann, folgt dem **Iterator-Protokoll**: `iter()` liefert einen Iterator, `next()` holt das nächste Element. Ist nichts mehr da, kommt `StopIteration`:")
        code("""
            it = iter(["a", "b"])
            print(next(it))
            print(next(it))
            print(next(it, "leer"))
        """, output = """
            a
            b
            leer
        """)
        text("Eigene Klassen werden iterierbar, wenn sie `__iter__` und `__next__` haben:")
        code("""
            class Countdown:
                def __init__(self, start):
                    self.aktuell = start

                def __iter__(self):
                    return self

                def __next__(self):
                    if self.aktuell <= 0:
                        raise StopIteration
                    self.aktuell -= 1
                    return self.aktuell + 1

            for zahl in Countdown(3):
                print(zahl)
        """, output = """
            3
            2
            1
        """)
        text("Ein **Kontextmanager** sorgt dafür, dass am Ende garantiert etwas passiert – wie das Schließen einer Datei bei `with open(...)`. Eigene Kontextmanager baust du am einfachsten mit `contextlib.contextmanager`:")
        code("""
            from contextlib import contextmanager

            @contextmanager
            def kapitel(titel):
                print(f"== {titel} ==")
                yield
                print("== Ende ==")

            with kapitel("Einleitung"):
                print("Python ist toll.")
        """, output = """
            == Einleitung ==
            Python ist toll.
            == Ende ==
        """)
        tip("Soll der Code nach `yield` auch bei einem Fehler im `with`-Block laufen, packst du `yield` in `try` / `finally`.")

        output("""
            it = iter([1, 2, 3])
            next(it)
            print(next(it))
        """, "2", "1", "3", "[2, 3]")
        output("""
            it = iter([])
            print(next(it, "fertig"))
        """, "fertig", "Fehler", "None", "[]")
        choice("Welche zwei Methoden braucht ein Iterator?", "__iter__ und __next__", "__init__ und __str__", "__len__ und __getitem__", "iter und next", mono = true)
        truth("Bei `with open(...)` wird die Datei auch dann geschlossen, wenn im Block ein Fehler auftritt.", true)
        typeOutput("""
            from contextlib import contextmanager

            @contextmanager
            def klammern():
                print("(", end="")
                yield
                print(")")

            with klammern():
                print("Py", end="")
        """, "(Py)")
        fill("Ergänze den Kontextmanager und seine Verwendung.", """
            from contextlib import contextmanager

            @contextmanager
            def rahmen():
                print("Start")
                ??
                print("Stopp")

            ?? rahmen():
                print("Mitte")
        """, answers = listOf("yield", "with"), distractors = listOf("return", "for", "pass"), output = """
            Start
            Mitte
            Stopp
        """)
        output("""
            class Zwei:
                def __init__(self):
                    self.n = 0

                def __iter__(self):
                    return self

                def __next__(self):
                    self.n += 1
                    if self.n > 2:
                        raise StopIteration
                    return self.n

            print(list(Zwei()))
        """, "[1, 2]", "[0, 1, 2]", "[1, 2, 3]", "Endlosschleife")
        write("Schreibe eine Klasse `Gerade(maximum)`, die beim Durchlaufen alle geraden Zahlen von 0 bis `maximum` liefert – mit `__iter__` und `__next__`. Gib `list(Gerade(8))` aus.",
            solution = """
                class Gerade:
                    def __init__(self, maximum):
                        self.maximum = maximum
                        self.aktuell = 0

                    def __iter__(self):
                        return self

                    def __next__(self):
                        if self.aktuell > self.maximum:
                            raise StopIteration
                        wert = self.aktuell
                        self.aktuell += 2
                        return wert

                print(list(Gerade(8)))
            """,
            output = "[0, 2, 4, 6, 8]",
            test = "assert list(Gerade(3)) == [0, 2]",
            mustContain = listOf("__next__", "StopIteration"),
            hint = "In `__next__`: Ist `maximum` überschritten, `raise StopIteration` – sonst den aktuellen Wert zurückgeben und um 2 erhöhen.")
    }

    lesson("Walross & Entpacken", ":=, * und ** wie die Profis", LessonIcon.STAR) {
        text("Der **Walross-Operator** `:=` weist einen Wert zu und benutzt ihn gleichzeitig – praktisch in Bedingungen:")
        code("""
            daten = [4, 8, 15, 16, 23, 42]
            if (anzahl := len(daten)) > 5:
                print(f"Viele Daten: {anzahl}")
        """, output = "Viele Daten: 6")
        text("Mit `*` kannst du beim **Entpacken** beliebig viele Werte einsammeln:")
        code("""
            erster, *mitte, letzter = [1, 2, 3, 4, 5]
            print(erster, mitte, letzter)
            kopf, *rest = "Python"
            print(kopf, rest)
        """, output = """
            1 [2, 3, 4] 5
            P ['y', 't', 'h', 'o', 'n']
        """)
        text("`*` und `**` funktionieren auch beim **Aufrufen** von Funktionen und beim Zusammenbauen von Listen und Dictionaries:")
        code("""
            def punkt(x, y, z):
                return f"({x}|{y}|{z})"

            koordinaten = [1, 2, 3]
            print(punkt(*koordinaten))
            einstellungen = {"x": 0, "y": 5, "z": 9}
            print(punkt(**einstellungen))
            print([*range(3), *"ab"])
            standard = {"farbe": "blau", "groesse": 12}
            print({**standard, "farbe": "rot"})
            print(standard | {"neu": True})
        """, output = """
            (1|2|3)
            (0|5|9)
            [0, 1, 2, 'a', 'b']
            {'farbe': 'rot', 'groesse': 12}
            {'farbe': 'blau', 'groesse': 12, 'neu': True}
        """)
        tip("Verkettete Vergleiche, Mehrfachzuweisung, `*`-Entpacken und Comprehensions – all das macht Code „pythonisch“: kurz, klar und elegant.")

        output("""
            a, *b = [1, 2, 3]
            print(b)
        """, "[2, 3]", "2", "[1, 2]", "Fehler")
        output("""
            *a, b = "xyz"
            print(a, b)
        """, "['x', 'y'] z", "x yz", "xy z", "Fehler")
        typeOutput("""
            def summe(a, b, c):
                return a + b + c

            werte = [1, 2, 3]
            print(summe(*werte))
        """, "6")
        choice("Was macht `if (n := len(text)) > 10:`?", "Speichert die Länge in `n` und vergleicht sie mit 10", "Vergleicht `n` mit 10", "Einen Syntaxfehler", "Setzt `n` auf 10")
        truth("`{**a, **b}` erzeugt ein neues Dictionary mit den Einträgen von `a` und `b`.", true)
        fill("Füge zwei Dictionaries zusammen.", """
            basis = {"a": 1}
            extra = {"b": 2}
            print(basis ?? extra)
        """, answers = listOf("|"), distractors = listOf("+", "&", "or"), output = "{'a': 1, 'b': 2}")
        output("print([*\"ab\", *[1, 2]])", "['a', 'b', 1, 2]", "['ab', 1, 2]", "[['a', 'b'], [1, 2]]", "Fehler")
        write("Entpacke die Liste `ergebnisse` so, dass `sieger` den ersten Wert, `letzter` den letzten Wert und `rest` alle Werte dazwischen enthält. Gib alle drei aus.",
            starter = "ergebnisse = [\"Ada\", \"Alan\", \"Grace\", \"Linus\", \"Guido\"]",
            solution = """
                ergebnisse = ["Ada", "Alan", "Grace", "Linus", "Guido"]
                sieger, *rest, letzter = ergebnisse
                print(sieger)
                print(rest)
                print(letzter)
            """,
            output = """
                Ada
                ['Alan', 'Grace', 'Linus']
                Guido
            """,
            mustContain = listOf("*rest"),
            hint = "`sieger, *rest, letzter = ergebnisse`")
    }

    checkpoint()
}
