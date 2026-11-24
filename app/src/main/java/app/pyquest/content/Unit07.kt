package app.pyquest.content

import app.pyquest.data.LessonIcon

fun CourseBuilder.unit07() = unit("Tupel, Dicts & Sets", "Daten clever organisieren") {
    lesson("Tupel", "Unveränderliche Werte-Gruppen", LessonIcon.BOX) {
        text("Ein **Tupel** ist wie eine Liste – aber **unveränderlich**. Du schreibst es mit runden Klammern:")
        code("""
            punkt = (3, 4)
            farbe = (255, 128, 0)
            print(punkt[0])
            print(len(farbe))
        """, output = """
            3
            3
        """)
        text("Tupel nimmt man für Werte, die zusammengehören und sich nicht ändern sollen – zum Beispiel Koordinaten oder ein Datum.")
        warn("Ein Tupel kann man nicht verändern: `punkt[0] = 5` führt zu einem **TypeError**.")
        text("Mit **Entpacken** verteilst du die Werte eines Tupels auf mehrere Variablen:")
        code("""
            geburtstag = (24, 12, 2010)
            tag, monat, jahr = geburtstag
            print(f"Tag: {tag}, Monat: {monat}, Jahr: {jahr}")
        """, output = "Tag: 24, Monat: 12, Jahr: 2010")
        tip("Ein Tupel mit nur einem Element braucht ein Komma: `(5,)`. Ohne Komma ist `(5)` einfach die Zahl 5.")

        output("""
            t = (10, 20, 30)
            print(t[2])
        """, "30", "20", "(30)", "Fehler")
        choice("Was passiert bei `t = (1, 2)` und danach `t[0] = 9`?",
            "Ein TypeError – Tupel sind unveränderlich.", "`t` wird zu (9, 2).", "`t` wird zu (9, 1, 2).", "Nichts passiert.")
        typeOutput("""
            x, y = (7, 3)
            print(x - y)
        """, "4")
        truth("`(5)` ist ein Tupel mit einem Element.", false, explain = "Ohne Komma ist `(5)` nur die Zahl 5 in Klammern. Ein Tupel wäre `(5,)`.")
        output("""
            a, b, c = "xyz"
            print(b)
        """, "y", "x", "z", "Fehler", explain = "Entpacken funktioniert mit jeder Sequenz – auch mit Strings.")
        match("[1, 2]" to "Liste (veränderbar)", "(1, 2)" to "Tupel (unveränderlich)", "a, b = b, a" to "Werte tauschen", "(5,)" to "Tupel mit einem Element")
        fill("Entpacke die Koordinaten in `x` und `y`.", """
            position = (12, 7)
            ??, ?? = position
            print(x * 10 + y)
        """, answers = listOf("x", "y"), distractors = listOf("position", "z"), output = "127")
        write("`divmod(a, b)` liefert ein Tupel aus Ganzzahl-Division und Rest. Entpacke `divmod(100, 7)` in die Variablen `ganz` und `rest` und gib beide aus.",
            solution = """
                ganz, rest = divmod(100, 7)
                print(ganz, rest)
            """,
            output = "14 2",
            mustContain = listOf("divmod"),
            hint = "`ganz, rest = divmod(100, 7)` – danach `print(ganz, rest)`.")
    }

    lesson("Dictionaries", "Schlüssel-Wert-Paare", LessonIcon.BOX) {
        text("Ein **Dictionary** (Wörterbuch) speichert **Schlüssel-Wert-Paare**. Statt über eine Position greifst du über einen **Schlüssel** zu – wie bei einem Telefonbuch:")
        code("""
            alter = {"Ada": 36, "Alan": 41, "Grace": 85}
            print(alter["Alan"])
            print(len(alter))
        """, output = """
            41
            3
        """)
        text("Hinzufügen und Ändern funktioniert mit derselben Schreibweise. Mit `del` löschst du einen Eintrag:")
        code("""
            lager = {"Äpfel": 10}
            lager["Birnen"] = 5
            lager["Äpfel"] = 7
            del lager["Birnen"]
            print(lager)
        """, output = "{'Äpfel': 7}")
        warn("Greifst du auf einen Schlüssel zu, den es nicht gibt, kommt ein **KeyError**. Sicherer ist `get()` – es liefert `None` oder einen Standardwert:")
        code("""
            farben = {"rot": "#FF0000"}
            print(farben.get("blau"))
            print(farben.get("blau", "unbekannt"))
            print("rot" in farben)
        """, output = """
            None
            unbekannt
            True
        """)
        tip("Schlüssel müssen eindeutig sein. Schreibst du denselben Schlüssel zweimal, gewinnt der letzte Wert.")

        output("""
            d = {"a": 1, "b": 2}
            print(d["b"])
        """, "2", "1", "b", "Fehler")
        output("""
            d = {"x": 5}
            d["x"] = 9
            print(d)
        """, "{'x': 9}", "{'x': 5}", "{'x': 5, 'x': 9}", "Fehler")
        typeOutput("""
            d = {"Hund": 4, "Huhn": 2}
            print(d.get("Fisch", 0))
        """, "0")
        choice("Was passiert bei `d = {}` und danach `print(d[\"x\"])`?", "Ein KeyError", "Es wird `None` ausgegeben", "Es wird `0` ausgegeben", "Es wird `x` ausgegeben")
        truth("Mit `\"schlüssel\" in d` prüft man, ob ein Schlüssel im Dictionary vorkommt.", true)
        fill("Füge Linus mit 21 Jahren hinzu.", """
            alter = {"Ada": 36}
            alter[??] = 21
            print(alter)
        """, answers = listOf("\"Linus\""), distractors = listOf("Linus", "21", "alter"), output = "{'Ada': 36, 'Linus': 21}")
        output("""
            d = {"a": 1, "a": 2}
            print(d)
        """, "{'a': 2}", "{'a': 1}", "{'a': 1, 'a': 2}", "Fehler", explain = "Schlüssel sind eindeutig – der letzte Wert gewinnt.")
        write("Erstelle ein Dictionary `hauptstadt` mit den Einträgen Deutschland → Berlin, Frankreich → Paris und Italien → Rom. Gib danach die Hauptstadt von Frankreich aus.",
            solution = """
                hauptstadt = {"Deutschland": "Berlin", "Frankreich": "Paris", "Italien": "Rom"}
                print(hauptstadt["Frankreich"])
            """,
            output = "Paris",
            mustContain = listOf("{", "hauptstadt"),
            hint = "`{\"Deutschland\": \"Berlin\", ...}` – Zugriff mit `hauptstadt[\"Frankreich\"]`.")
    }

    lesson("Durch Dicts laufen", "keys, values, items und Zählen", LessonIcon.LOOP) {
        text("""
            Mit einer `for`-Schleife kannst du durch ein Dictionary laufen. Es gibt drei Varianten:
            - `d.keys()` – nur die Schlüssel
            - `d.values()` – nur die Werte
            - `d.items()` – Schlüssel **und** Wert als Paar
        """)
        code("""
            preise = {"Brot": 2.5, "Milch": 1.2, "Käse": 3.8}
            for artikel, preis in preise.items():
                print(f"{artikel}: {preis} €")
            print("Summe:", sum(preise.values()))
        """, output = """
            Brot: 2.5 €
            Milch: 1.2 €
            Käse: 3.8 €
            Summe: 7.5
        """)
        text("Ein Klassiker: **Zählen** mit einem Dictionary. Für jedes Element erhöhst du den passenden Zähler:")
        code("""
            text = "Mississippi"
            zaehler = {}
            for b in text:
                zaehler[b] = zaehler.get(b, 0) + 1
            print(zaehler)
        """, output = "{'M': 1, 'i': 4, 's': 4, 'p': 2}")
        tip("Dictionaries merken sich die Reihenfolge, in der die Schlüssel eingefügt wurden.")

        output("""
            d = {"a": 1, "b": 2}
            for k in d:
                print(k)
        """, "a\nb", "1\n2", "a 1\nb 2", "('a', 1)")
        output("""
            d = {"x": 3, "y": 4}
            print(sum(d.values()))
        """, "7", "xy", "2", "Fehler")
        typeOutput("""
            d = {"Ada": 1, "Bob": 2}
            for name, nr in d.items():
                print(nr, name)
        """, "1 Ada\n2 Bob")
        match(".keys()" to "alle Schlüssel", ".values()" to "alle Werte", ".items()" to "Schlüssel-Wert-Paare", ".get(k, 0)" to "Wert oder 0")
        truth("Durchläuft man ein Dictionary direkt mit `for k in d`, bekommt man die Schlüssel.", true)
        fill("Gib jeden Namen mit seinem Alter aus.", """
            alter = {"Ada": 36, "Alan": 41}
            for name, jahre in alter.??():
                print(name, jahre)
        """, answers = listOf("items"), distractors = listOf("keys", "values", "get"), output = """
            Ada 36
            Alan 41
        """)
        output("""
            woerter = ["rot", "blau", "rot"]
            z = {}
            for w in woerter:
                z[w] = z.get(w, 0) + 1
            print(z["rot"])
        """, "2", "1", "3", "Fehler")
        write("Zähle, wie oft jede Zahl in `wuerfe` vorkommt, und gib das Dictionary aus.",
            starter = "wuerfe = [6, 2, 6, 3, 2, 6]",
            solution = """
                wuerfe = [6, 2, 6, 3, 2, 6]
                zaehler = {}
                for w in wuerfe:
                    zaehler[w] = zaehler.get(w, 0) + 1
                print(zaehler)
            """,
            output = "{6: 3, 2: 2, 3: 1}",
            mustContain = listOf("for"),
            hint = "`zaehler[w] = zaehler.get(w, 0) + 1` in einer Schleife.")
    }

    lesson("Sets", "Mengen ohne Duplikate", LessonIcon.PUZZLE) {
        text("Ein **Set** (Menge) ist eine ungeordnete Sammlung **ohne Duplikate**. Perfekt, um doppelte Werte loszuwerden:")
        code("""
            zahlen = [1, 2, 2, 3, 3, 3]
            einzigartig = set(zahlen)
            print(einzigartig)
            print(len(einzigartig))
        """, output = """
            {1, 2, 3}
            3
        """)
        text("Sets kann man wie in der Mathematik kombinieren:")
        code("""
            python = {"Ada", "Linus", "Grace"}
            java = {"Linus", "James"}
            print(python & java)
            print(sorted(python - java))
            print(len(python | java))
        """, output = """
            {'Linus'}
            ['Ada', 'Grace']
            4
        """)
        text("""
            - `a | b` Vereinigung (alles)
            - `a & b` Schnittmenge (in beiden)
            - `a - b` Differenz (in a, aber nicht in b)
            - `a ^ b` in genau einer der beiden Mengen
        """)
        code("""
            s = {1, 2}
            s.add(3)
            s.add(2)
            s.discard(1)
            print(s)
            print(2 in s)
        """, output = """
            {2, 3}
            True
        """)
        warn("Ein leeres Set erzeugst du mit `set()` – `{}` ist ein leeres **Dictionary**!")
        tip("Die Reihenfolge in einem Set ist nicht festgelegt. Brauchst du eine Reihenfolge, wandle es mit `sorted()` in eine sortierte Liste um.")

        output("print(len({1, 1, 2, 2, 3}))", "3", "5", "2", "Fehler")
        output("""
            a = {1, 2, 3}
            b = {2, 3, 4}
            print(a & b)
        """, "{2, 3}", "{1, 2, 3, 4}", "{1, 4}", "{1}")
        typeOutput("""
            a = {1, 2, 3}
            b = {3}
            print(a - b)
        """, "{1, 2}")
        choice("Was erzeugt ein **leeres Set**?", "set()", "{}", "[]", "()", mono = true)
        truth("Ein Set kann denselben Wert mehrmals enthalten.", false)
        match("a | b" to "Vereinigung", "a & b" to "Schnittmenge", "a - b" to "Differenz", "x in a" to "enthalten?")
        fill("Entferne die Duplikate aus der Liste.", """
            namen = ["Ada", "Ada", "Alan"]
            einzeln = ??(namen)
            print(len(einzeln))
        """, answers = listOf("set"), distractors = listOf("list", "dict", "len"), output = "2")
        write("Wie viele **verschiedene** Buchstaben hat das Wort `\"Mississippi\"`? Gib die Anzahl aus.",
            solution = """print(len(set("Mississippi")))""",
            output = "4",
            mustContain = listOf("set"),
            hint = "`set(\"Mississippi\")` enthält jeden Buchstaben nur einmal. Groß- und Kleinbuchstaben zählen getrennt.")
    }

    lesson("Daten kombinieren", "Listen von Dicts und mehr", LessonIcon.BRAIN) {
        text("Richtig mächtig wird es, wenn man Datenstrukturen kombiniert – zum Beispiel eine **Liste von Dictionaries**. So lassen sich Tabellen abbilden:")
        code("""
            schueler = [
                {"name": "Ada", "note": 1},
                {"name": "Linus", "note": 3},
                {"name": "Grace", "note": 2},
            ]
            for s in schueler:
                print(s["name"], "hat eine", s["note"])
        """, output = """
            Ada hat eine 1
            Linus hat eine 3
            Grace hat eine 2
        """)
        text("Auch Dictionaries mit Listen als Werten sind praktisch:")
        code("""
            stundenplan = {
                "Montag": ["Mathe", "Deutsch"],
                "Dienstag": ["Informatik", "Sport", "Kunst"],
            }
            print(stundenplan["Dienstag"][0])
            stundenplan["Montag"].append("Musik")
            print(stundenplan["Montag"])
        """, output = """
            Informatik
            ['Mathe', 'Deutsch', 'Musik']
        """)
        text("Zum Sortieren solcher Daten gibst du `sorted()` mit `key=` eine Funktion mit, die sagt, **wonach** sortiert wird:")
        code("""
            schueler = [{"name": "Ada", "note": 1}, {"name": "Linus", "note": 3}, {"name": "Grace", "note": 2}]
            for s in sorted(schueler, key=lambda s: s["note"]):
                print(s["name"])
        """, output = """
            Ada
            Grace
            Linus
        """)
        tip("`lambda s: s[\"note\"]` ist eine Mini-Funktion. Mehr dazu lernst du in der Einheit über Funktionen.")

        output("""
            d = {"farben": ["rot", "blau"]}
            print(d["farben"][1])
        """, "blau", "rot", "farben", "Fehler")
        output("""
            l = [{"a": 1}, {"a": 5}]
            print(l[1]["a"])
        """, "5", "1", "a", "Fehler")
        typeOutput("""
            teams = {"rot": ["Ada", "Bob"], "blau": ["Cem"]}
            print(len(teams["rot"]) + len(teams["blau"]))
        """, "3")
        truth("Ein Dictionary kann Listen als Werte enthalten.", true)
        choice("Wie greifst du auf das Alter in `personen = [{\"name\": \"Ada\", \"alter\": 36}]` zu?",
            "personen[0][\"alter\"]", "personen[\"alter\"][0]", "personen.alter", "personen[0].alter", mono = true)
        fill("Füge dem Montag das Fach Musik hinzu.", """
            plan = {"Montag": ["Mathe"]}
            plan["Montag"].??("Musik")
            print(plan)
        """, answers = listOf("append"), distractors = listOf("add", "insert", "get"), output = "{'Montag': ['Mathe', 'Musik']}")
        output("""
            filme = [{"titel": "A", "jahr": 2010}, {"titel": "B", "jahr": 1999}]
            alt = sorted(filme, key=lambda f: f["jahr"])[0]
            print(alt["titel"])
        """, "B", "A", "1999", "Fehler")
        write("Berechne aus der Liste `einkauf` den **Gesamtpreis** (Preis × Menge für jeden Artikel) und gib ihn aus.",
            starter = """
                einkauf = [
                    {"artikel": "Brot", "preis": 2.5, "menge": 2},
                    {"artikel": "Milch", "preis": 1.0, "menge": 3},
                    {"artikel": "Käse", "preis": 4.0, "menge": 1},
                ]
            """,
            solution = """
                einkauf = [
                    {"artikel": "Brot", "preis": 2.5, "menge": 2},
                    {"artikel": "Milch", "preis": 1.0, "menge": 3},
                    {"artikel": "Käse", "preis": 4.0, "menge": 1},
                ]
                summe = 0
                for e in einkauf:
                    summe += e["preis"] * e["menge"]
                print(summe)
            """,
            output = "12.0",
            hint = "Laufe durch die Liste und addiere jeweils `e[\"preis\"] * e[\"menge\"]`.")
    }

    checkpoint()
}
