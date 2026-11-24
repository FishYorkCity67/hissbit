package app.pyquest.content

import app.pyquest.data.LessonIcon

fun CourseBuilder.unit12() = unit("Module & Bibliothek", "Die riesige Standardbibliothek von Python nutzen") {
    lesson("import", "Fertige Module einbinden", LessonIcon.FILE) {
        text("Python bringt eine riesige **Standardbibliothek** mit – fertige Module für fast alles. Mit `import` holst du sie in dein Programm:")
        code("""
            import math
            print(math.sqrt(81))
            print(math.pi)
        """, output = """
            9.0
            3.141592653589793
        """)
        text("""
            Es gibt mehrere Varianten:
            - `import math` – dann `math.sqrt()`
            - `from math import sqrt, pi` – dann direkt `sqrt()`
            - `import statistics as st` – mit einem Kurznamen
        """)
        code("""
            from math import sqrt, floor
            import statistics as st

            print(sqrt(2) > 1.4)
            print(floor(7.9))
            print(st.mean([2, 4, 9]))
        """, output = """
            True
            7
            5
        """)
        tip("Mit `dir(math)` siehst du alles, was ein Modul enthält – mit `help(math.sqrt)` die Beschreibung einer Funktion.")
        warn("Nenne deine eigenen Dateien nie so wie Standardmodule (z.B. `random.py`) – sonst importiert Python deine Datei statt des echten Moduls!")

        output("""
            import math
            print(math.sqrt(16))
        """, "4.0", "4", "16", "Fehler")
        choice("Wie importierst du nur die Funktion `randint` aus `random`?", "from random import randint", "import randint from random", "import random.randint()", "using random.randint", mono = true)
        typeOutput("""
            from math import floor, ceil
            print(floor(2.7), ceil(2.1))
        """, "2 3")
        truth("Nach `import math as m` ruft man die Wurzel mit `m.sqrt()` auf.", true)
        choice("Was passiert bei `import gibtsnicht`?", "Ein ModuleNotFoundError", "Python lädt das Modul aus dem Internet", "Nichts", "Ein SyntaxError")
        fill("Importiere `pi` direkt, sodass es ohne `math.` funktioniert.", """
            ?? math ?? pi
            print(round(pi, 2))
        """, answers = listOf("from", "import"), distractors = listOf("using", "include", "as"), output = "3.14")
        match("import math" to "ganzes Modul laden", "from math import pi" to "einzelnen Namen laden", "import math as m" to "mit Kurzname laden", "dir(math)" to "Inhalt anzeigen")
        write("Berechne mit dem Modul `math` die Hypotenuse eines rechtwinkligen Dreiecks mit den Seiten 6 und 8 und gib sie aus.",
            solution = """
                import math
                print(math.hypot(6, 8))
            """,
            output = "10.0",
            mustContain = listOf("import math"),
            hint = "`math.hypot(6, 8)` oder Pythagoras: `math.sqrt(6**2 + 8**2)`.")
    }

    lesson("random & math", "Zufall und Mathematik", LessonIcon.CALC) {
        text("Das Modul `random` erzeugt Zufallswerte – perfekt für Spiele:")
        code("""
            import random
            print(random.randint(1, 6))
            print(random.choice(["Schere", "Stein", "Papier"]))
            karten = [1, 2, 3, 4, 5]
            random.shuffle(karten)
            print(karten)
            print(random.random())
        """)
        text("""
            - `randint(a, b)` – ganze Zahl von a bis b (**inklusive** b!)
            - `choice(liste)` – ein zufälliges Element
            - `shuffle(liste)` – mischt die Liste (verändert sie direkt)
            - `random()` – Kommazahl zwischen 0 und 1
            - `sample(liste, k)` – k verschiedene Elemente
        """)
        tip("Mit `random.seed(42)` bekommst du jedes Mal dieselben „Zufallszahlen“ – nützlich zum Testen. Tippe zweimal auf ▶ und vergleiche!")
        code("""
            import random
            random.seed(42)
            print([random.randint(1, 10) for _ in range(5)])
        """)
        text("Das Modul `math` bietet mathematische Funktionen und Konstanten:")
        code("""
            import math
            print(math.floor(3.7), math.ceil(3.2))
            print(math.gcd(24, 36))
            print(math.factorial(6))
            print(round(math.sin(math.pi / 2)))
            print(math.isclose(0.1 + 0.2, 0.3))
        """, output = """
            3 4
            12
            720
            1
            True
        """)

        choice("Welche Werte kann `random.randint(1, 3)` liefern?", "1, 2 oder 3", "1 oder 2", "0, 1, 2 oder 3", "Nur Kommazahlen")
        truth("`random.shuffle(liste)` gibt eine neue gemischte Liste zurück und lässt das Original unverändert.", false,
            explain = "`shuffle` mischt die Liste **direkt** und gibt `None` zurück.")
        output("""
            import math
            print(math.gcd(12, 18))
        """, "6", "36", "3", "216")
        typeOutput("""
            import math
            print(math.factorial(4))
        """, "24")
        output("""
            import random
            wert = random.choice([7, 7, 7])
            print(wert)
        """, "7", "[7]", "Eine Zufallszahl", "Fehler")
        fill("Würfle 1000-mal mit einem normalen Würfel und gib die kleinste und die größte Augenzahl aus.", """
            import random
            wuerfe = [random.??(1, ??) for _ in range(1000)]
            print(min(wuerfe), max(wuerfe))
        """, answers = listOf("randint", "6"), distractors = listOf("choice", "7", "random"), output = "1 6",
            explain = "`randint(1, 6)` liefert 1 bis **einschließlich** 6. Bei 1000 Würfen kommen praktisch sicher beide Extreme vor.")
        output("""
            import math
            print(math.ceil(4.01))
        """, "5", "4", "4.0", "4.01")
        write("Simuliere 1000 Münzwürfe mit `random.choice([\"Kopf\", \"Zahl\"])` und zähle, wie oft `Kopf` kommt. Gib `True` aus, wenn die Anzahl zwischen 400 und 600 liegt.",
            solution = """
                import random
                kopf = 0
                for _ in range(1000):
                    if random.choice(["Kopf", "Zahl"]) == "Kopf":
                        kopf += 1
                print(400 <= kopf <= 600)
            """,
            output = "True",
            mustContain = listOf("random"),
            hint = "Zähle in einer Schleife: `if random.choice([...]) == \"Kopf\": kopf += 1`.")
    }

    lesson("datetime & time", "Mit Datum und Uhrzeit rechnen", LessonIcon.STAR) {
        text("Mit dem Modul `datetime` rechnest du mit Datum und Uhrzeit:")
        code("""
            from datetime import date, timedelta

            weihnachten = date(2026, 12, 24)
            print(weihnachten)
            print(weihnachten.strftime("%d.%m.%Y"))
            print(weihnachten + timedelta(days=7))
            print(weihnachten.weekday())
        """, output = """
            2026-12-24
            24.12.2026
            2026-12-31
            3
        """)
        text("Zwei Daten kann man voneinander abziehen – das Ergebnis ist ein `timedelta`:")
        code("""
            from datetime import date

            start = date(2026, 1, 1)
            ende = date(2026, 12, 31)
            diff = ende - start
            print(diff.days)
        """, output = "364")
        text("Die wichtigsten Format-Codes für `strftime`: `%d` Tag, `%m` Monat, `%Y` Jahr, `%H` Stunde, `%M` Minute, `%A` Wochentag.")
        code("""
            from datetime import datetime

            jetzt = datetime.now()
            print(jetzt.strftime("Heute ist der %d.%m.%Y, es ist %H:%M Uhr."))
        """)
        text("Mit dem Modul `time` misst du Zeiten oder lässt dein Programm warten:")
        code("""
            import time

            start = time.perf_counter()
            summe = sum(range(1_000_000))
            dauer = time.perf_counter() - start
            print(f"Gerechnet in {dauer:.3f} Sekunden")
        """)
        tip("`time.sleep(1)` lässt dein Programm eine Sekunde warten – nützlich für Countdowns.")

        output("""
            from datetime import date
            print(date(2026, 5, 17))
        """, "2026-05-17", "17.05.2026", "17-5-2026", "Fehler")
        typeOutput("""
            from datetime import date
            print((date(2026, 3, 10) - date(2026, 3, 1)).days)
        """, "9")
        output("""
            from datetime import date, timedelta
            print(date(2026, 1, 30) + timedelta(days=5))
        """, "2026-02-04", "2026-01-35", "2026-02-05", "Fehler")
        choice("Welcher Format-Code steht für das vierstellige Jahr?", "%Y", "%y", "%J", "%a", mono = true)
        truth("`date(2026, 12, 24).strftime(\"%d.%m.\")` ergibt `24.12.`", true)
        fill("Gib das Datum im deutschen Format aus.", """
            from datetime import date
            d = date(2027, 7, 4)
            print(d.??("%d.%m.%Y"))
        """, answers = listOf("strftime"), distractors = listOf("format", "strptime", "print"), output = "04.07.2027")
        output("""
            from datetime import date
            print(date(2026, 10, 18).weekday())
        """, "6", "0", "7", "18", explain = "`weekday()` zählt ab Montag = 0. Der 18.10.2026 ist ein Sonntag.")
        write("Berechne, wie viele Tage zwischen dem 1. Januar 2027 und dem 1. Juli 2027 liegen, und gib die Zahl aus.",
            solution = """
                from datetime import date
                print((date(2027, 7, 1) - date(2027, 1, 1)).days)
            """,
            output = "181",
            mustContain = listOf("date"),
            hint = "Zieh zwei `date`-Objekte voneinander ab und nimm `.days`.")
    }

    lesson("collections & itertools", "Zählen, Gruppieren, Kombinieren", LessonIcon.BOX) {
        text("Das Modul `collections` enthält spezielle Datenstrukturen. Am beliebtesten ist `Counter` – er zählt Dinge für dich:")
        code("""
            from collections import Counter

            woerter = "der die das der der die".split()
            zaehler = Counter(woerter)
            print(zaehler)
            print(zaehler.most_common(1))
            print(zaehler["das"])
        """, output = """
            Counter({'der': 3, 'die': 2, 'das': 1})
            [('der', 3)]
            1
        """)
        text("`defaultdict` erstellt fehlende Einträge automatisch, `deque` ist eine Warteschlange, an der man an beiden Enden schnell arbeiten kann:")
        code("""
            from collections import defaultdict, deque

            gruppen = defaultdict(list)
            for name in ["Anna", "Ben", "Alex", "Bea"]:
                gruppen[name[0]].append(name)
            print(dict(gruppen))

            schlange = deque(["Ada", "Alan"])
            schlange.append("Grace")
            print(schlange.popleft())
            print(list(schlange))
        """, output = """
            {'A': ['Anna', 'Alex'], 'B': ['Ben', 'Bea']}
            Ada
            ['Alan', 'Grace']
        """)
        text("`itertools` liefert Werkzeuge für Kombinationen und mehr:")
        code("""
            import itertools

            print(list(itertools.permutations("ABC", 2)))
            print(list(itertools.combinations([1, 2, 3], 2)))
            print(list(itertools.product("ab", repeat=2)))
        """, output = """
            [('A', 'B'), ('A', 'C'), ('B', 'A'), ('B', 'C'), ('C', 'A'), ('C', 'B')]
            [(1, 2), (1, 3), (2, 3)]
            [('a', 'a'), ('a', 'b'), ('b', 'a'), ('b', 'b')]
        """)
        tip("`Counter` kann man sogar addieren und subtrahieren – perfekt, um Lagerbestände zu verrechnen.")

        output("""
            from collections import Counter
            print(Counter("aab")["a"])
        """, "2", "1", "3", "Fehler")
        output("""
            from collections import Counter
            print(Counter([3, 1, 3, 3, 1]).most_common(1))
        """, "[(3, 3)]", "[(1, 2)]", "3", "[3]")
        typeOutput("""
            from collections import defaultdict
            d = defaultdict(int)
            d["x"] += 5
            print(d["x"])
        """, "5")
        output("""
            import itertools
            print(len(list(itertools.permutations([1, 2, 3]))))
        """, "6", "3", "9", "27")
        truth("Bei einem `defaultdict(list)` entsteht beim ersten Zugriff auf einen neuen Schlüssel automatisch eine leere Liste.", true)
        fill("Zähle die Buchstaben und gib die zwei häufigsten aus.", """
            from collections import ??
            print(Counter("abracadabra").??(2))
        """, answers = listOf("Counter", "most_common"), distractors = listOf("count", "deque", "top"), output = "[('a', 5), ('b', 2)]")
        output("""
            import itertools
            print(list(itertools.combinations("XYZ", 2)))
        """, "[('X', 'Y'), ('X', 'Z'), ('Y', 'Z')]", "[('X', 'Y'), ('Y', 'X')]", "['XY', 'XZ', 'YZ']", "Fehler")
        write("Finde mit `Counter` das **häufigste Wort** im Text und gib nur dieses Wort aus.",
            starter = "text = \"python ist toll python ist schnell python macht spass\"",
            solution = """
                from collections import Counter
                text = "python ist toll python ist schnell python macht spass"
                print(Counter(text.split()).most_common(1)[0][0])
            """,
            output = "python",
            mustContain = listOf("Counter"),
            hint = "`Counter(text.split()).most_common(1)` liefert `[('python', 3)]` – davon brauchst du nur das Wort.")
    }

    lesson("json & re", "Datenformate und Textmuster", LessonIcon.FILE) {
        text("**JSON** ist das Standardformat, um Daten auszutauschen – zum Beispiel zwischen Apps und Webseiten. Mit dem Modul `json` wandelt Python Dictionaries und Listen in Text und zurück:")
        code("""
            import json

            profil = {"name": "Monty", "level": 7, "items": ["Hut", "Brille"]}
            text = json.dumps(profil)
            print(text)
            zurueck = json.loads(text)
            print(zurueck["items"][0])
        """, output = """
            {"name": "Monty", "level": 7, "items": ["Hut", "Brille"]}
            Hut
        """)
        tip("Mit `json.dumps(daten, indent=2)` wird der Text schön eingerückt und gut lesbar.")
        text("**Reguläre Ausdrücke** (Modul `re`) beschreiben Textmuster. Damit findest du zum Beispiel alle Zahlen oder E-Mail-Adressen in einem Text:")
        code("""
            import re

            text = "Bestellung 4711 vom 12.03. für 3 Artikel"
            print(re.findall(r"\d+", text))
            print(re.sub(r"\d", "#", "PIN: 1234"))
            print(bool(re.fullmatch(r"[A-Z][a-z]+", "Monty")))
        """, output = """
            ['4711', '12', '03', '3']
            PIN: ####
            True
        """)
        text("""
            Wichtige Bausteine:
            - `\d` eine Ziffer, `\w` ein Wortzeichen, `\s` ein Leerzeichen
            - `+` einmal oder öfter, `*` beliebig oft, `?` optional
            - `[a-z]` ein Zeichen aus dem Bereich, `.` ein beliebiges Zeichen
        """)
        warn("Schreibe Muster immer als **Raw-String** (`r\"...\"`), damit Python die Backslashes nicht selbst interpretiert.")

        output("""
            import json
            print(json.loads('{"a": 1}')["a"])
        """, "1", "a", "{'a': 1}", "Fehler")
        output("""
            import json
            print(json.dumps([1, "zwei"]))
        """, "[1, \"zwei\"]", "[1, 'zwei']", "1 zwei", "Fehler")
        typeOutput("""
            import re
            print(re.findall(r"\d+", "a1b22c333"))
        """, "['1', '22', '333']")
        choice("Wofür steht `\\d` in einem regulären Ausdruck?", "Eine Ziffer", "Ein Buchstabe", "Ein Punkt", "Das Ende des Textes")
        truth("`json.dumps()` wandelt Python-Daten in einen JSON-Text um.", true)
        fill("Ersetze alle Leerzeichen durch Unterstriche.", """
            import re
            print(re.??(r" ", "_", "mein neues spiel"))
        """, answers = listOf("sub"), distractors = listOf("findall", "match", "replace"), output = "mein_neues_spiel")
        match("json.dumps" to "Daten → Text", "json.loads" to "Text → Daten", "re.findall" to "alle Treffer finden", "re.sub" to "Treffer ersetzen")
        write("Finde mit `re.findall` alle Wörter, die mit einem **Großbuchstaben** beginnen, und gib die Liste aus.",
            starter = "text = \"Gestern hat Ada mit Alan in Berlin Python gelernt\"",
            solution = """
                import re
                text = "Gestern hat Ada mit Alan in Berlin Python gelernt"
                print(re.findall(r"[A-Z]\w*", text))
            """,
            output = "['Gestern', 'Ada', 'Alan', 'Berlin', 'Python']",
            mustContain = listOf("re.findall"),
            hint = "Das Muster `r\"[A-Z]\\w*\"` bedeutet: ein Großbuchstabe, danach beliebig viele Wortzeichen.")
    }

    checkpoint()
}
