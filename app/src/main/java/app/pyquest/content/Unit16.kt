package app.pyquest.content

import app.pyquest.data.LessonIcon

private val KONTO_BASIS = """
    class KontoFehler(Exception):
        pass

    class Konto:
        def __init__(self, inhaber, stand=0.0):
            self.inhaber = inhaber
            self.stand = stand
            self.buchungen = []

        def einzahlen(self, betrag):
            if betrag <= 0:
                raise KontoFehler("Betrag muss positiv sein")
            self.stand += betrag
            self.buchungen.append(("Einzahlung", betrag))

        def abheben(self, betrag):
            if betrag > self.stand:
                raise KontoFehler("Nicht genug Guthaben")
            self.stand -= betrag
            self.buchungen.append(("Abhebung", -betrag))

        def __str__(self):
            return f"Konto von {self.inhaber}: {self.stand:.2f} €"
""".trimIndent()

private val BEWERTE = """
    def bewerte(tipp, geheim):
        if tipp < geheim:
            return "zu klein"
        if tipp > geheim:
            return "zu groß"
        return "richtig"
""".trimIndent()

fun CourseBuilder.unit16() = unit("Abschlussprojekte", "Alles kombinieren: vier echte Programme") {
    project("Projekt: Zahlenraten", "Ein komplettes Spiel Schritt für Schritt") {
        heading("Das Ziel")
        text("Wir bauen Schritt für Schritt ein **Zahlenratespiel**: Der Computer denkt sich eine Zahl aus, du rätst, und er sagt dir „zu groß“ oder „zu klein“. Dabei kombinierst du alles, was du gelernt hast: Variablen, Schleifen, Bedingungen, Funktionen und Eingaben.")
        code("import random\n\n" + BEWERTE + "\n\n" + """
            geheim = random.randint(1, 100)
            versuche = 0
            while True:
                eingabe = input("Dein Tipp: ")
                if not eingabe.isdigit():
                    print("Bitte eine Zahl eingeben!")
                    continue
                versuche += 1
                ergebnis = bewerte(int(eingabe), geheim)
                print(ergebnis)
                if ergebnis == "richtig":
                    print(f"Geschafft in {versuche} Versuchen!")
                    break
        """.trimIndent(), runnable = false)
        tip("Tippe oben auf ↗, um das fertige Spiel im Spielplatz selbst zu spielen! In den Aufgaben baust du es jetzt Stück für Stück nach.")

        write("**Schritt 1:** Schreibe die Funktion `bewerte(tipp, geheim)`. Sie gibt `\"zu klein\"`, `\"zu groß\"` oder `\"richtig\"` zurück.",
            starter = """
                def bewerte(tipp, geheim):
                    # Dein Code hier
                    pass
            """,
            solution = BEWERTE,
            test = """
                assert bewerte(3, 7) == "zu klein"
                assert bewerte(9, 7) == "zu groß"
                assert bewerte(7, 7) == "richtig"
                print("✅ Schritt 1 geschafft!")
            """,
            hint = "Drei Fälle: `if tipp < geheim: return \"zu klein\"`, dann `zu groß`, sonst `richtig`.")
        write("**Schritt 2:** Schreibe `ist_gueltig(eingabe)`. Sie gibt `True` zurück, wenn die Eingabe eine Zahl zwischen 1 und 100 ist – sonst `False`. Achtung: Die Eingabe ist ein String!",
            starter = """
                def ist_gueltig(eingabe):
                    pass
            """,
            solution = """
                def ist_gueltig(eingabe):
                    if not eingabe.isdigit():
                        return False
                    return 1 <= int(eingabe) <= 100
            """,
            test = """
                assert ist_gueltig("50")
                assert ist_gueltig("1") and ist_gueltig("100")
                assert not ist_gueltig("0")
                assert not ist_gueltig("101")
                assert not ist_gueltig("abc")
                print("✅ Schritt 2 geschafft!")
            """,
            hint = "Erst mit `isdigit()` prüfen, dann mit `1 <= int(eingabe) <= 100`.")
        write("**Schritt 3:** Die Spielschleife. Die geheime Zahl ist hier fest `42`. Lies so lange Tipps mit `input()` ein, bis die Zahl erraten ist. Gib nach jedem Tipp das Ergebnis von `bewerte()` aus und am Ende `Geschafft in X Versuchen!`. Getestet wird mit den Eingaben **50**, **25** und **42**.",
            starter = BEWERTE + "\n\ngeheim = 42\n# Deine Spielschleife hier",
            solution = BEWERTE + "\n\n" + """
                geheim = 42
                versuche = 0
                while True:
                    tipp = int(input("Dein Tipp: "))
                    versuche += 1
                    ergebnis = bewerte(tipp, geheim)
                    print(ergebnis)
                    if ergebnis == "richtig":
                        print(f"Geschafft in {versuche} Versuchen!")
                        break
            """.trimIndent(),
            stdin = listOf("50", "25", "42"),
            output = """
                zu groß
                zu klein
                richtig
                Geschafft in 3 Versuchen!
            """,
            mustContain = listOf("while", "input"),
            hint = "`while True:` – Tipp einlesen, `versuche += 1`, bewerten, ausgeben und bei „richtig“ mit `break` raus.")
        choice("Warum ist die eigene Funktion `bewerte()` sinnvoll, statt alles direkt in die Schleife zu schreiben?",
            "Man kann sie einzeln testen und der Code wird übersichtlicher.",
            "Weil Schleifen keine if-Abfragen enthalten dürfen.",
            "Damit das Spiel schneller läuft.",
            "Es gibt keinen Vorteil.")
        truth("Mit `random.randint(1, 100)` kann auch die Zahl 100 gezogen werden.", true)
    }

    project("Projekt: To-do-Liste", "Aufgaben verwalten mit Listen und Dicts") {
        heading("Das Ziel")
        text("Jetzt bauen wir eine kleine **Aufgabenverwaltung**. Die Aufgaben speichern wir als Liste von Dictionaries. Funktionen kümmern sich ums Hinzufügen, Abhaken und Anzeigen.")
        code("""
            aufgaben = []

            def hinzufuegen(titel):
                aufgaben.append({"titel": titel, "erledigt": False})

            def abhaken(nummer):
                aufgaben[nummer - 1]["erledigt"] = True

            def anzeigen():
                for nr, a in enumerate(aufgaben, start=1):
                    haken = "✅" if a["erledigt"] else "⬜"
                    print(f"{nr}. {haken} {a['titel']}")

            hinzufuegen("Python lernen")
            hinzufuegen("Zimmer aufräumen")
            hinzufuegen("Monty füttern")
            abhaken(1)
            anzeigen()
        """, output = """
            1. ✅ Python lernen
            2. ⬜ Zimmer aufräumen
            3. ⬜ Monty füttern
        """)
        tip("Später könntest du die Aufgaben mit `json` in einer Datei speichern – dann bleiben sie auch nach dem Beenden erhalten.")

        write("**Schritt 1:** Schreibe `offene(aufgaben)`. Sie gibt eine Liste mit den **Titeln** aller noch nicht erledigten Aufgaben zurück.",
            starter = """
                def offene(aufgaben):
                    pass
            """,
            solution = """
                def offene(aufgaben):
                    return [a["titel"] for a in aufgaben if not a["erledigt"]]
            """,
            test = """
                liste = [
                    {"titel": "A", "erledigt": True},
                    {"titel": "B", "erledigt": False},
                    {"titel": "C", "erledigt": False},
                ]
                assert offene(liste) == ["B", "C"]
                assert offene([]) == []
                print("✅ Schritt 1 geschafft!")
            """,
            hint = "Eine List Comprehension mit `if not a[\"erledigt\"]`.")
        write("**Schritt 2:** Schreibe `fortschritt(aufgaben)`. Sie gibt den Anteil erledigter Aufgaben in Prozent als ganze Zahl zurück – bei einer leeren Liste `0`.",
            starter = """
                def fortschritt(aufgaben):
                    pass
            """,
            solution = """
                def fortschritt(aufgaben):
                    if not aufgaben:
                        return 0
                    erledigt = sum(1 for a in aufgaben if a["erledigt"])
                    return round(erledigt / len(aufgaben) * 100)
            """,
            test = """
                liste = [{"titel": "A", "erledigt": True}, {"titel": "B", "erledigt": False}]
                assert fortschritt(liste) == 50
                assert fortschritt([]) == 0
                assert fortschritt([{"titel": "X", "erledigt": True}]) == 100
                print("✅ Schritt 2 geschafft!")
            """,
            hint = "Zähle die erledigten Aufgaben und rechne `erledigt / len(aufgaben) * 100`. Denk an die leere Liste!")
        write("**Schritt 3:** Schreibe `sortiert(aufgaben)`. Sie gibt die Aufgaben so sortiert zurück, dass offene vor erledigten stehen – und innerhalb davon alphabetisch nach Titel.",
            starter = """
                def sortiert(aufgaben):
                    pass
            """,
            solution = """
                def sortiert(aufgaben):
                    return sorted(aufgaben, key=lambda a: (a["erledigt"], a["titel"]))
            """,
            test = """
                liste = [
                    {"titel": "Zahnarzt", "erledigt": False},
                    {"titel": "Abwasch", "erledigt": True},
                    {"titel": "Bücher", "erledigt": False},
                ]
                assert [a["titel"] for a in sortiert(liste)] == ["Bücher", "Zahnarzt", "Abwasch"]
                print("✅ Schritt 3 geschafft!")
            """,
            hint = "Sortiere mit einem Tupel als Schlüssel: `key=lambda a: (a[\"erledigt\"], a[\"titel\"])` – `False` kommt vor `True`.")
        truth("Eine Liste von Dictionaries eignet sich gut, um viele Aufgaben mit ihren Eigenschaften zu speichern.", true)
        choice("Womit könnte man die Aufgaben dauerhaft speichern?", "Mit `json` in einer Datei", "Mit `print()`", "Mit `random`", "Gar nicht")
    }

    project("Projekt: Textanalyse", "Wörter zählen, Lesezeit schätzen") {
        heading("Das Ziel")
        text("Wir schreiben ein Werkzeug, das Texte analysiert: Wörter zählen, die häufigsten Wörter finden und die Lesezeit schätzen. Solche Funktionen stecken in Suchmaschinen und Schreib-Apps.")
        code("""
            from collections import Counter

            text = ${TQ}Python ist eine Programmiersprache. Python ist leicht zu lernen
            und Python macht Spaß. Viele lernen Python als erste Sprache.${TQ}

            woerter = [w.strip(".,!?").lower() for w in text.split()]
            print("Wörter:", len(woerter))
            print("Verschiedene:", len(set(woerter)))
            print("Top 3:", Counter(woerter).most_common(3))
        """, output = """
            Wörter: 19
            Verschiedene: 14
            Top 3: [('python', 4), ('ist', 2), ('lernen', 2)]
        """)

        write("**Schritt 1:** Schreibe `woerter(text)`. Sie gibt eine Liste aller Wörter in Kleinbuchstaben zurück – ohne die Satzzeichen `.,!?` am Rand.",
            starter = """
                def woerter(text):
                    pass
            """,
            solution = """
                def woerter(text):
                    return [w.strip(".,!?").lower() for w in text.split()]
            """,
            test = """
                assert woerter("Hallo Welt!") == ["hallo", "welt"]
                assert woerter("Ja, nein. Vielleicht?") == ["ja", "nein", "vielleicht"]
                print("✅ Schritt 1 geschafft!")
            """,
            hint = "`text.split()`, dann jedes Wort mit `.strip(\".,!?\").lower()` bearbeiten.")
        write("**Schritt 2:** Schreibe `lesezeit(text)`. Sie gibt die Lesezeit in **Minuten** zurück (aufgerundet) – bei 200 Wörtern pro Minute. Nutze `math.ceil`.",
            starter = """
                import math

                def lesezeit(text):
                    pass
            """,
            solution = """
                import math

                def lesezeit(text):
                    return math.ceil(len(text.split()) / 200)
            """,
            test = """
                assert lesezeit("wort " * 200) == 1
                assert lesezeit("wort " * 201) == 2
                assert lesezeit("wort " * 450) == 3
                print("✅ Schritt 2 geschafft!")
            """,
            hint = "`math.ceil(len(text.split()) / 200)`")
        write("**Schritt 3:** Schreibe `laengstes_wort(text)`. Sie gibt das längste Wort ohne Satzzeichen zurück – bei Gleichstand das erste.",
            starter = """
                def laengstes_wort(text):
                    pass
            """,
            solution = """
                def laengstes_wort(text):
                    woerter = [w.strip(".,!?") for w in text.split()]
                    return max(woerter, key=len)
            """,
            test = """
                assert laengstes_wort("Ich liebe Programmieren!") == "Programmieren"
                assert laengstes_wort("ab cd") == "ab"
                print("✅ Schritt 3 geschafft!")
            """,
            hint = "`max(woerter, key=len)` – bei Gleichstand liefert `max` automatisch das erste Element.")
        output("""
            from collections import Counter
            print(Counter("a b a c a b".split()).most_common(2))
        """, "[('a', 3), ('b', 2)]", "[('a', 3)]", "['a', 'b']", "{'a': 3, 'b': 2}")
        truth("`set(woerter)` enthält jedes Wort nur einmal.", true)
    }

    project("Projekt: Bankkonto", "Objektorientiert mit eigener Fehlerklasse") {
        heading("Das Ziel")
        text("Zum Abschluss ein objektorientiertes Projekt: ein **Bankkonto** mit Einzahlungen, Abhebungen, Kontoauszug und einer eigenen Fehlerklasse.")
        code(KONTO_BASIS + "\n\n" + """
            k = Konto("Ada")
            k.einzahlen(100)
            k.abheben(30)
            try:
                k.abheben(500)
            except KontoFehler as e:
                print("Fehler:", e)
            print(k)
        """.trimIndent(), output = """
            Fehler: Nicht genug Guthaben
            Konto von Ada: 70.00 €
        """)

        write("**Schritt 1:** Ergänze die Klasse `Konto` um eine Methode `auszug()`. Sie gibt alle Buchungen als Liste von Strings im Format `Einzahlung: 100` zurück.",
            starter = KONTO_BASIS,
            solution = KONTO_BASIS.replace(
                "    def __str__(self):",
                "    def auszug(self):\n        return [f\"{art}: {betrag}\" for art, betrag in self.buchungen]\n\n    def __str__(self):",
            ),
            test = """
                k = Konto("Test")
                k.einzahlen(100)
                k.abheben(30)
                assert k.auszug() == ["Einzahlung: 100", "Abhebung: -30"]
                print("✅ Schritt 1 geschafft!")
            """,
            mustContain = listOf("def auszug"),
            hint = "`return [f\"{art}: {betrag}\" for art, betrag in self.buchungen]`")
        write("**Schritt 2:** Schreibe eine Kindklasse `Sparkonto(Konto)` mit dem zusätzlichen Attribut `zinssatz` (Standard `0.02`) und einer Methode `zinsen_gutschreiben()`, die `stand * zinssatz` einzahlt.",
            starter = KONTO_BASIS,
            solution = KONTO_BASIS + """


class Sparkonto(Konto):
    def __init__(self, inhaber, stand=0.0, zinssatz=0.02):
        super().__init__(inhaber, stand)
        self.zinssatz = zinssatz

    def zinsen_gutschreiben(self):
        self.einzahlen(self.stand * self.zinssatz)""",
            test = """
                s = Sparkonto("Ada", 1000, 0.05)
                s.zinsen_gutschreiben()
                assert round(s.stand, 2) == 1050.0
                assert isinstance(s, Konto)
                print("✅ Schritt 2 geschafft!")
            """,
            mustContain = listOf("class Sparkonto(Konto)", "super()"),
            hint = "Im `__init__` zuerst `super().__init__(inhaber, stand)` aufrufen, dann `self.zinssatz` setzen.")
        write("**Schritt 3:** Schreibe eine Funktion `ueberweisen(von, nach, betrag)`, die Geld von einem Konto auf ein anderes überweist. Reicht das Guthaben nicht, soll ein `KontoFehler` auftreten – und **keines** der Konten darf sich verändern.",
            starter = KONTO_BASIS,
            solution = KONTO_BASIS + """


def ueberweisen(von, nach, betrag):
    von.abheben(betrag)
    nach.einzahlen(betrag)""",
            test = """
                a = Konto("A", 100)
                b = Konto("B", 0)
                ueberweisen(a, b, 40)
                assert a.stand == 60 and b.stand == 40
                try:
                    ueberweisen(a, b, 1000)
                    assert False, "Es hätte ein KontoFehler kommen müssen"
                except KontoFehler:
                    pass
                assert a.stand == 60 and b.stand == 40
                print("✅ Schritt 3 geschafft!")
            """,
            mustContain = listOf("def ueberweisen"),
            hint = "Erst abheben – das wirft bei zu wenig Guthaben den Fehler –, dann einzahlen.")
        choice("Warum hat das Projekt eine eigene Klasse `KontoFehler`?",
            "So kann man genau diese Fehler gezielt abfangen.", "Weil Python sonst abstürzt.", "Damit das Konto schneller ist.", "Klassen brauchen immer eigene Fehler.")
        truth("Durch Vererbung hat `Sparkonto` automatisch auch die Methoden `einzahlen` und `abheben`.", true)
    }

    checkpoint("Abschlussprüfung", "Zeig, dass du Python kannst!")
}
