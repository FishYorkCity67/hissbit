package app.pyquest.content

import app.pyquest.data.ExampleProgram

private fun ex(category: String, title: String, description: String, code: String, input: List<String> = emptyList()) =
    ExampleProgram(title, description, code.trimIndent().trimEnd() + "\n", category, input)

/** Beispielprogramme für den Code-Spielplatz. */
val ExamplePrograms: List<ExampleProgram> = listOf(
    ex("Grundlagen", "Hallo Welt", "Das klassische erste Programm.", """
        print("Hallo, Welt! 👋")
        print("Ich programmiere mit Python", 3, "🐍")
    """),
    ex("Grundlagen", "Begrüßung", "Fragt nach deinem Namen und Alter.", """
        name = input("Wie heißt du? ")
        alter = int(input("Wie alt bist du? "))
        print(f"Hallo {name}!")
        print(f"In 10 Jahren bist du {alter + 10} Jahre alt.")
    """, listOf("Ada", "14")),
    ex("Grundlagen", "Taschenrechner", "Rechnet mit zwei Zahlen und einem Rechenzeichen.", """
        print("Mini-Taschenrechner 🧮")
        a = float(input("Erste Zahl: "))
        op = input("Rechenzeichen (+ - * /): ")
        b = float(input("Zweite Zahl: "))

        if op == "+":
            ergebnis = a + b
        elif op == "-":
            ergebnis = a - b
        elif op == "*":
            ergebnis = a * b
        elif op == "/":
            if b == 0:
                ergebnis = "Fehler: Teilen durch 0!"
            else:
                ergebnis = a / b
        else:
            ergebnis = "Unbekanntes Rechenzeichen"

        print("Ergebnis:", ergebnis)
    """, listOf("12", "*", "3.5")),
    ex("Grundlagen", "Temperatur-Tabelle", "Celsius in Fahrenheit umrechnen – mit schöner Formatierung.", """
        print("Celsius | Fahrenheit")
        print("--------+-----------")
        for celsius in range(-10, 41, 10):
            fahrenheit = celsius * 9 / 5 + 32
            print(f"{celsius:7} | {fahrenheit:9.1f}")
    """),
    ex("Spiele", "Zahlenraten", "Errate Montys Geheimzahl zwischen 1 und 100.", """
        import random

        geheim = random.randint(1, 100)
        versuche = 0
        print("Ich denke an eine Zahl zwischen 1 und 100.")

        while True:
            eingabe = input("Dein Tipp: ")
            if not eingabe.isdigit():
                print("Bitte eine ganze Zahl eingeben!")
                continue
            tipp = int(eingabe)
            versuche += 1
            if tipp < geheim:
                print("Zu klein! ⬆️")
            elif tipp > geheim:
                print("Zu groß! ⬇️")
            else:
                print(f"Richtig! 🎉 Du hast {versuche} Versuche gebraucht.")
                break
    """, listOf("abc") + (1..100).map { it.toString() }),
    ex("Spiele", "Schere, Stein, Papier", "Drei Runden gegen Monty.", """
        import random

        optionen = ["schere", "stein", "papier"]
        gewinnt_gegen = {"schere": "papier", "stein": "schere", "papier": "stein"}
        punkte_du, punkte_monty = 0, 0

        for runde in range(1, 4):
            wahl = input(f"Runde {runde} – schere, stein oder papier? ").strip().lower()
            if wahl not in optionen:
                print("Ungültig – diese Runde zählt nicht.")
                continue
            monty = random.choice(optionen)
            print("Monty wählt:", monty)
            if wahl == monty:
                print("Unentschieden!")
            elif gewinnt_gegen[wahl] == monty:
                print("Du gewinnst die Runde! 🎉")
                punkte_du += 1
            else:
                print("Monty gewinnt die Runde! 🐍")
                punkte_monty += 1

        print(f"Endstand: Du {punkte_du} : {punkte_monty} Monty")
    """, listOf("stein", "papier", "schere")),
    ex("Spiele", "Würfel-Statistik", "600 Würfe simulieren und als Balkendiagramm zeigen.", """
        import random
        from collections import Counter

        wuerfe = [random.randint(1, 6) for _ in range(600)]
        statistik = Counter(wuerfe)

        print("600 Würfe – so oft kam jede Zahl:")
        for augen in range(1, 7):
            anzahl = statistik[augen]
            balken = "█" * (anzahl // 5)
            print(f"{augen}: {anzahl:3} {balken}")
    """),
    ex("Spiele", "Python-Quiz", "Ein kleines Quiz mit Punktestand.", """
        fragen = {
            "Wie heißt die Funktion für Ausgaben?": "print",
            "Welcher Datentyp speichert True/False?": "bool",
            "Mit welchem Zeichen beginnt ein Kommentar?": "#",
        }
        punkte = 0
        for frage, antwort in fragen.items():
            eingabe = input(frage + " ")
            if eingabe.strip().lower() == antwort:
                print("✅ Richtig!")
                punkte += 1
            else:
                print(f"❌ Leider falsch – richtig ist: {antwort}")
        print(f"Du hast {punkte} von {len(fragen)} Punkten.")
    """, listOf("print", "int", "#")),
    ex("Spiele", "Text-Adventure", "Eine Mini-Geschichte, in der du entscheidest.", """
        print("Du stehst vor einer dunklen Höhle. 🕳️")
        weg = input("Gehst du nach 'links' oder 'rechts'? ").strip().lower()
        if weg == "links":
            print("Du findest eine Schatztruhe! 💰")
            oeffnen = input("Öffnen? (ja/nein) ").strip().lower()
            if oeffnen == "ja":
                print("Sie ist voller Python-Bücher. Der wahre Schatz! 📚")
            else:
                print("Du gehst weiter und verpasst den Schatz …")
        elif weg == "rechts":
            print("Ein freundlicher Python-Drache begrüßt dich. 🐉")
        else:
            print("Du bleibst stehen und denkst über dein Leben nach.")
    """, listOf("links", "ja")),
    ex("Mathe", "FizzBuzz", "Der Klassiker aus Bewerbungsgesprächen.", """
        for zahl in range(1, 31):
            if zahl % 15 == 0:
                print("FizzBuzz")
            elif zahl % 3 == 0:
                print("Fizz")
            elif zahl % 5 == 0:
                print("Buzz")
            else:
                print(zahl)
    """),
    ex("Mathe", "Primzahlen", "Das Sieb des Eratosthenes findet alle Primzahlen bis 100.", """
        grenze = 100
        ist_prim = [True] * (grenze + 1)
        ist_prim[0] = ist_prim[1] = False

        for zahl in range(2, int(grenze ** 0.5) + 1):
            if ist_prim[zahl]:
                for vielfaches in range(zahl * zahl, grenze + 1, zahl):
                    ist_prim[vielfaches] = False

        primzahlen = [z for z in range(grenze + 1) if ist_prim[z]]
        print(f"{len(primzahlen)} Primzahlen bis {grenze}:")
        print(primzahlen)
    """),
    ex("Mathe", "Fibonacci", "Die berühmte Zahlenfolge mit einem Generator.", """
        def fibonacci(n):
            a, b = 0, 1
            for _ in range(n):
                yield a
                a, b = b, a + b

        print(list(fibonacci(15)))
    """),
    ex("Mathe", "Collatz-Folge", "Ein ungelöstes Rätsel der Mathematik.", """
        zahl = 27
        schritte = 0
        hoechster = zahl
        while zahl != 1:
            zahl = zahl // 2 if zahl % 2 == 0 else 3 * zahl + 1
            hoechster = max(hoechster, zahl)
            schritte += 1
        print(f"Die Collatz-Folge von 27 erreicht nach {schritte} Schritten die 1.")
        print(f"Der höchste Wert unterwegs war {hoechster}.")
    """),
    ex("Mathe", "Fakultät (rekursiv)", "Eine Funktion, die sich selbst aufruft.", """
        def fakultaet(n):
            if n <= 1:  # Basisfall
                return 1
            return n * fakultaet(n - 1)  # rekursiver Aufruf

        for i in range(1, 11):
            print(f"{i}! = {fakultaet(i)}")
    """),
    ex("Mathe", "Türme von Hanoi", "Ein rekursives Knobelspiel.", """
        def hanoi(n, start, ziel, hilfe):
            if n == 0:
                return 0
            zuege = hanoi(n - 1, start, hilfe, ziel)
            print(f"Scheibe {n}: {start} → {ziel}")
            zuege += 1
            zuege += hanoi(n - 1, hilfe, ziel, start)
            return zuege

        anzahl = hanoi(3, "A", "C", "B")
        print(f"Fertig in {anzahl} Zügen!")
    """),
    ex("Daten", "Einkaufsliste", "Listen erstellen, ändern und sortieren.", """
        liste = ["Äpfel", "Brot", "Milch"]
        liste.append("Käse")
        liste.insert(0, "Kaffee")
        liste.remove("Brot")

        print("Meine Einkaufsliste:")
        for nummer, artikel in enumerate(liste, start=1):
            print(f"{nummer}. {artikel}")

        print("Anzahl:", len(liste))
        print("Alphabetisch:", sorted(liste))
    """),
    ex("Daten", "Wörter zählen", "Welche Wörter kommen am häufigsten vor?", """
        from collections import Counter

        text = ${TQ}Python ist toll. Python ist einfach.
        Mit Python kann man fast alles programmieren.${TQ}

        woerter = text.lower().replace(".", "").split()
        zaehler = Counter(woerter)

        print("Die häufigsten Wörter:")
        for wort, anzahl in zaehler.most_common(3):
            print(f"{wort}: {anzahl}x")
    """),
    ex("Daten", "Notenrechner", "Durchschnitte mit Listen und Dictionaries berechnen.", """
        schueler = [
            {"name": "Ada", "noten": [1, 2, 1]},
            {"name": "Linus", "noten": [3, 2, 2]},
            {"name": "Grace", "noten": [2, 1, 1]},
        ]

        for s in schueler:
            schnitt = sum(s["noten"]) / len(s["noten"])
            print(f"{s['name']:6} Schnitt: {schnitt:.2f}")

        beste = min(schueler, key=lambda s: sum(s["noten"]) / len(s["noten"]))
        print("Bester Schnitt:", beste["name"])
    """),
    ex("Daten", "JSON", "Daten in Text umwandeln und zurück.", """
        import json

        daten = {"name": "Monty", "alter": 3, "lieblingsessen": ["Mäuse", "Bugs"]}
        text = json.dumps(daten, indent=2, ensure_ascii=False)
        print(text)

        zurueck = json.loads(text)
        print(zurueck["lieblingsessen"][1])
    """),
    ex("Daten", "Dateien", "Eine Datei schreiben, ergänzen und wieder lesen.", """
        with open("tagebuch.txt", "w", encoding="utf-8") as datei:
            datei.write("Tag 1: Python angefangen 🐍\n")

        with open("tagebuch.txt", "a", encoding="utf-8") as datei:
            datei.write("Tag 2: Schleifen gelernt 🔁\n")

        with open("tagebuch.txt", encoding="utf-8") as datei:
            for nummer, zeile in enumerate(datei, start=1):
                print(nummer, zeile.strip())
    """),
    ex("Objekte", "Haustier-Klasse", "Deine erste eigene Klasse.", """
        class Haustier:
            def __init__(self, name, art):
                self.name = name
                self.art = art
                self.hunger = 5

            def fuettern(self):
                self.hunger = max(0, self.hunger - 3)
                print(f"{self.name} hat gegessen. Hunger: {self.hunger}")

            def __str__(self):
                return f"{self.name} ({self.art})"

        monty = Haustier("Monty", "Python")
        print(monty)
        monty.fuettern()
        monty.fuettern()
    """),
    ex("Objekte", "Bankkonto", "Eine Klasse mit Fehlerprüfung.", """
        class Konto:
            def __init__(self, inhaber, stand=0):
                self.inhaber = inhaber
                self.stand = stand

            def einzahlen(self, betrag):
                if betrag <= 0:
                    raise ValueError("Betrag muss positiv sein")
                self.stand += betrag

            def abheben(self, betrag):
                if betrag > self.stand:
                    raise ValueError("Nicht genug Geld auf dem Konto!")
                self.stand -= betrag

        konto = Konto("Ada", 100)
        konto.einzahlen(50)
        try:
            konto.abheben(500)
        except ValueError as fehler:
            print("Fehler:", fehler)
        print(f"Kontostand von {konto.inhaber}: {konto.stand} €")
    """),
    ex("Objekte", "Dataclass: Punkte", "Klassen ohne Tipparbeit mit @dataclass.", """
        from dataclasses import dataclass
        import math

        @dataclass
        class Punkt:
            x: float
            y: float

            def abstand(self, anderer):
                return math.dist((self.x, self.y), (anderer.x, anderer.y))

        a = Punkt(0, 0)
        b = Punkt(3, 4)
        print(a, b)
        print("Abstand:", a.abstand(b))
        print("Gleich?", a == Punkt(0, 0))
    """),
    ex("Profi", "Comprehensions", "Listen und Dictionaries in einer Zeile bauen.", """
        zahlen = range(1, 11)
        quadrate = [z ** 2 for z in zahlen]
        gerade = [z for z in zahlen if z % 2 == 0]
        woerter = ["python", "ist", "super"]
        laengen = {w: len(w) for w in woerter}

        print(quadrate)
        print(gerade)
        print(laengen)
    """),
    ex("Profi", "Generatoren", "Werte erst erzeugen, wenn man sie braucht.", """
        def countdown(n):
            while n > 0:
                yield n
                n -= 1
            yield "Start! 🚀"

        for wert in countdown(5):
            print(wert)

        summe = sum(x * x for x in range(100_000))
        print("Summe der Quadrate:", summe)
    """),
    ex("Profi", "Dekorator: Stoppuhr", "Funktionen erweitern, ohne sie zu ändern.", """
        import time
        from functools import wraps

        def stoppuhr(funktion):
            @wraps(funktion)
            def wrapper(*args, **kwargs):
                start = time.perf_counter()
                ergebnis = funktion(*args, **kwargs)
                dauer = (time.perf_counter() - start) * 1000
                print(f"{funktion.__name__} dauerte {dauer:.1f} ms")
                return ergebnis
            return wrapper

        @stoppuhr
        def summe_bis(n):
            return sum(range(n))

        print(summe_bis(100_000))
    """),
    ex("Profi", "Reguläre Ausdrücke", "Muster in Texten finden.", """
        import re

        text = "Schreib an monty@python.org oder an ada@lovelace.dev!"
        mails = re.findall(r"[\w.]+@[\w.]+\w", text)
        print("Gefundene E-Mails:", mails)

        datum = "Heute ist der 18.10.2026"
        treffer = re.search(r"(\d{2})\.(\d{2})\.(\d{4})", datum)
        if treffer:
            tag, monat, jahr = treffer.groups()
            print(f"Tag {tag}, Monat {monat}, Jahr {jahr}")
    """),
    ex("Profi", "match-case", "Strukturelles Pattern Matching (ab Python 3.10).", """
        def beschreibe(befehl):
            match befehl.split():
                case ["gehe", richtung]:
                    return f"Du gehst nach {richtung}."
                case ["nimm", ding]:
                    return f"Du nimmst: {ding}"
                case ["hilfe"]:
                    return "Befehle: gehe <richtung>, nimm <ding>"
                case _:
                    return "Das verstehe ich nicht."

        for b in ["gehe norden", "nimm schwert", "hilfe", "tanze"]:
            print(beschreibe(b))
    """),
    ex("Kreativ", "Mandelbrot in ASCII", "Ein Fraktal – nur aus Textzeichen.", """
        breite, hoehe = 40, 20
        zeichen = " .:-=+*#%@"

        for zeile in range(hoehe):
            y = zeile / hoehe * 2.4 - 1.2
            text = ""
            for spalte in range(breite):
                x = spalte / breite * 3.2 - 2.3
                z = 0
                c = complex(x, y)
                n = 0
                while abs(z) <= 2 and n < 30:
                    z = z * z + c
                    n += 1
                text += zeichen[n * (len(zeichen) - 1) // 30]
            print(text)
    """),
    ex("Kreativ", "Tannenbaum", "Muster mit Schleifen zeichnen.", """
        hoehe = 7
        for i in range(hoehe):
            print(" " * (hoehe - i - 1) + "*" * (2 * i + 1))
        print(" " * (hoehe - 1) + "|")
    """),
    ex("Kreativ", "Einmaleins", "Eine ordentlich formatierte Tabelle.", """
        for a in range(1, 11):
            zeile = ""
            for b in range(1, 11):
                zeile += f"{a * b:4}"
            print(zeile)
    """),
    ex("Werkzeuge", "Datum & Uhrzeit", "Rechnen mit Tagen und Zeiten.", """
        from datetime import date, datetime, timedelta

        heute = date.today()
        print("Heute:", heute.strftime("%d.%m.%Y"))

        silvester = date(heute.year, 12, 31)
        print("Tage bis Silvester:", (silvester - heute).days)

        jetzt = datetime.now()
        print("Uhrzeit:", jetzt.strftime("%H:%M:%S"))
        print("In 100 Tagen:", (heute + timedelta(days=100)).strftime("%d.%m.%Y"))
    """),
    ex("Werkzeuge", "Passwort-Generator", "Sichere Zufallspasswörter mit secrets.", """
        import secrets
        import string

        zeichen = string.ascii_letters + string.digits + "!?#%&*"
        laenge = 16
        passwort = "".join(secrets.choice(zeichen) for _ in range(laenge))
        print("Dein sicheres Passwort:", passwort)
    """),
    ex("Werkzeuge", "Kalender", "Das calendar-Modul druckt einen Monat.", """
        import calendar

        calendar.setfirstweekday(calendar.MONDAY)
        print(calendar.month(2026, 12))
    """),
    ex("Werkzeuge", "Statistik", "Mittelwert, Median und Co. aus dem statistics-Modul.", """
        import statistics

        punkte = [12, 15, 9, 20, 15, 18, 11]
        print("Mittelwert:", round(statistics.mean(punkte), 2))
        print("Median:", statistics.median(punkte))
        print("Häufigster Wert:", statistics.mode(punkte))
        print("Standardabweichung:", round(statistics.stdev(punkte), 2))
    """),
    ex("Werkzeuge", "Zen of Python", "Die 19 Leitsätze von Python – direkt eingebaut.", """
        import this
    """),
)
