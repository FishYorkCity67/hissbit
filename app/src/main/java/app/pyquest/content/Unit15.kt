package app.pyquest.content

import app.pyquest.data.LessonIcon

fun CourseBuilder.unit15() = unit("Python im echten Leben", "PC-Setup, Pakete, sauberer Code und Testen") {
    lesson("Python auf dem PC", "Installieren, Editoren und Skripte starten", LessonIcon.CODE) {
        text("Hier in der App läuft Python direkt auf deinem Handy. Auf dem **Computer** installierst du Python von **python.org** (Windows und Mac) – unter Linux ist es meistens schon vorinstalliert. Danach schreibst du Python-Dateien mit der Endung `.py` und startest sie.")
        text("""
            So startest du ein Programm im **Terminal** (Eingabeaufforderung):
            - `python hallo.py` (Windows) bzw. `python3 hallo.py` (Mac/Linux)
            - Nur `python` eingeben öffnet die interaktive **Python-Shell** (REPL) – dort probierst du Befehle direkt aus
        """)
        text("""
            Zum Programmieren nimmt man einen guten Editor, eine **IDE**:
            - **VS Code** – kostenlos, sehr beliebt, mit Python-Erweiterung
            - **PyCharm** – speziell für Python gemacht
            - **Thonny** – ideal für Einsteiger
            - **IDLE** – wird direkt mit Python mitgeliefert
        """)
        code("""
            import sys
            import platform
            print("Python-Version:", sys.version.split()[0])
            print("Läuft auf:", platform.system())
        """)
        tip("Die Zeile `if __name__ == \"__main__\":` sorgt dafür, dass Code nur läuft, wenn die Datei direkt gestartet wird – und nicht, wenn eine andere Datei sie importiert.")
        code("""
            def main():
                print("Programm gestartet!")

            if __name__ == "__main__":
                main()
        """, output = "Programm gestartet!")

        choice("Welche Dateiendung haben Python-Programme?", ".py", ".python", ".pt", ".exe", mono = true)
        choice("Was öffnet sich, wenn man im Terminal nur `python` eingibt?", "Die interaktive Python-Shell (REPL)", "Eine Webseite", "Der Code-Editor", "Nichts")
        truth("VS Code, PyCharm und Thonny sind Programme, in denen man Python-Code schreiben kann.", true)
        output("""
            def main():
                print("läuft")

            if __name__ == "__main__":
                main()
        """, "läuft", "Nichts", "__main__", "Fehler", explain = "Beim direkten Start ist `__name__` gleich `\"__main__\"`.")
        match("python.org" to "Python herunterladen", "VS Code" to "beliebter Editor", "REPL" to "interaktive Shell", ".py" to "Python-Datei")
        choice("Wie startet man die Datei `spiel.py` im Terminal?", "python spiel.py", "run spiel.py", "start python spiel", "spiel.py --python", mono = true)
        truth("Der Block unter `if __name__ == \"__main__\":` läuft auch, wenn eine andere Datei diese Datei importiert.", false)
        fill("Ergänze den typischen Start-Block.", """
            def main():
                print("Hallo!")

            if __name__ == ??:
                main()
        """, answers = listOf("\"__main__\""), distractors = listOf("\"main\"", "__main__", "True"), output = "Hallo!")
    }

    lesson("pip & Pakete", "Zusatzpakete und virtuelle Umgebungen", LessonIcon.BOX) {
        text("Neben der Standardbibliothek gibt es über **500.000 Zusatzpakete** im **Python Package Index (PyPI)**. Auf dem Computer installierst du sie mit **pip**:")
        text("""
            - `pip install requests` – installiert ein Paket
            - `pip install --upgrade requests` – aktualisiert es
            - `pip list` – zeigt alle installierten Pakete
            - `pip uninstall requests` – entfernt es wieder
        """)
        text("""
            Beliebte Pakete:
            - `requests` – Webseiten und Online-Dienste abfragen
            - `numpy` – schnelle Mathematik mit großen Zahlenmengen
            - `pandas` – Tabellen und Datenanalyse
            - `matplotlib` – Diagramme zeichnen
            - `flask` / `django` – Webseiten bauen
            - `pygame` – Spiele entwickeln
        """)
        text("""
            Damit sich die Pakete verschiedener Projekte nicht in die Quere kommen, nutzt man **virtuelle Umgebungen** (venv):
            - `python -m venv .venv` – Umgebung anlegen
            - `.venv\Scripts\activate` (Windows) bzw. `source .venv/bin/activate` (Mac/Linux) – aktivieren
            - In einer `requirements.txt` listet man alle Pakete eines Projekts auf und installiert sie mit `pip install -r requirements.txt`
        """)
        tip("In dieser App ist die komplette **Standardbibliothek** dabei. Zusatzpakete wie `numpy` installierst du am PC mit pip.")
        warn("Installiere nur Pakete aus vertrauenswürdigen Quellen und achte auf die genaue Schreibweise – es gibt gefälschte Pakete mit ähnlichen Namen.")

        choice("Mit welchem Befehl installiert man das Paket `requests`?", "pip install requests", "python get requests", "install requests", "import requests --install", mono = true)
        match("requests" to "Internet-Anfragen", "pandas" to "Tabellen & Datenanalyse", "matplotlib" to "Diagramme", "pygame" to "Spiele")
        truth("Eine virtuelle Umgebung trennt die Pakete verschiedener Projekte voneinander.", true)
        choice("Wie heißt die übliche Datei, in der die Pakete eines Projekts aufgelistet werden?", "requirements.txt", "packages.py", "pip.json", "imports.txt", mono = true)
        choice("Was ist PyPI?", "Das Verzeichnis mit Zusatzpaketen für Python", "Ein Python-Editor", "Eine Programmiersprache", "Ein Fehlertyp")
        truth("Module der Standardbibliothek wie `math` muss man erst mit pip installieren.", false, explain = "Die Standardbibliothek ist bei jeder Python-Installation schon dabei.")
        fill("Lege eine virtuelle Umgebung an.", "python -m ?? .venv", answers = listOf("venv"), distractors = listOf("pip", "env", "install"))
        choice("Was macht `pip install -r requirements.txt`?", "Installiert alle Pakete, die in der Datei stehen", "Löscht alle Pakete", "Erstellt die Datei requirements.txt", "Startet Python")
    }

    lesson("Sauberer Code", "PEP 8, gute Namen und Docstrings", LessonIcon.STAR) {
        text("Code wird viel öfter **gelesen** als geschrieben. Deshalb gibt es mit **PEP 8** offizielle Stilregeln für Python:")
        text("""
            - **4 Leerzeichen** pro Einrückungsebene
            - `snake_case` für Variablen und Funktionen, `PascalCase` für Klassen, `GROSSBUCHSTABEN` für Konstanten
            - Leerzeichen um Operatoren: `x = a + b` statt `x=a+b`
            - Zeilen nicht zu lang (etwa 79–99 Zeichen)
            - Zwei Leerzeilen zwischen Funktionen und Klassen
        """)
        text("Noch wichtiger sind **sprechende Namen**. Vergleiche:")
        code("""
            # Schwer zu verstehen:
            def f(a, b):
                return a * b * 0.19

            # Sofort klar:
            MWST_SATZ = 0.19

            def berechne_mwst(preis, menge):
                return preis * menge * MWST_SATZ

            print(round(berechne_mwst(10, 3), 2))
        """, output = "5.7")
        text("Mit **Docstrings** dokumentierst du, was eine Funktion tut. `help()` und Editoren zeigen sie an:")
        code("""
            def flaeche_kreis(radius):
                ${TQ}Berechnet die Fläche eines Kreises mit dem gegebenen Radius.${TQ}
                return 3.14159 * radius ** 2

            print(flaeche_kreis.__doc__)
        """, output = "Berechnet die Fläche eines Kreises mit dem gegebenen Radius.")
        tip("Tippe im Spielplatz `import this` – das **Zen of Python** fasst die Philosophie zusammen, zum Beispiel „Readability counts.“ (Lesbarkeit zählt).")

        choice("Welcher Variablenname entspricht PEP 8?", "anzahl_spieler", "AnzahlSpieler", "anzahlspieler!", "ANZAHL-SPIELER", mono = true)
        choice("Wie schreibt man Klassennamen nach PEP 8?", "PascalCase, z.B. `SpielFigur`", "snake_case, z.B. `spiel_figur`", "GROSSBUCHSTABEN", "Egal")
        truth("PEP 8 empfiehlt 4 Leerzeichen pro Einrückungsebene.", true)
        choice("Welche Zeile ist am besten lesbar?", "gesamt_preis = preis * menge", "gp=p*m", "GesamtPreis=preis*menge", "x = preis*menge", mono = true)
        match("snake_case" to "Variablen & Funktionen", "PascalCase" to "Klassen", "GROSS_GESCHRIEBEN" to "Konstanten", "Docstring" to "Dokumentation")
        output("""
            def f():
                ${TQ}Sagt Hallo.${TQ}
                return "Hallo"

            print(f.__doc__)
        """, "Sagt Hallo.", "Hallo", "f", "None")
        truth("Code wird häufiger gelesen als geschrieben – deshalb ist Lesbarkeit so wichtig.", true)
        write("Dieser Code funktioniert, ist aber schwer zu lesen. Schreibe ihn sauber um: sprechende Namen (`berechne_durchschnitt`, `noten`), Leerzeichen um Operatoren und ein Docstring. Die Ausgabe soll gleich bleiben.",
            starter = """
                def f(l):
                    return sum(l)/len(l)
                print(f([1,2,3,4]))
            """,
            solution = """
                def berechne_durchschnitt(noten):
                    ${TQ}Gibt den Durchschnitt der Noten zurück.${TQ}
                    return sum(noten) / len(noten)


                print(berechne_durchschnitt([1, 2, 3, 4]))
            """,
            output = "2.5",
            mustContain = listOf("def berechne_durchschnitt", "noten"),
            hint = "Ein Docstring steht direkt unter der `def`-Zeile in dreifachen Anführungszeichen.")
    }

    lesson("Testen & Debuggen", "Fehler finden wie die Profis", LessonIcon.BUG) {
        text("Profis testen ihren Code automatisch. Am einfachsten mit `assert`. Für größere Projekte gibt es das Modul **unittest** aus der Standardbibliothek oder das beliebte Paket **pytest**:")
        code("""
            import unittest

            def ist_palindrom(wort):
                w = wort.lower()
                return w == w[::-1]

            class TestPalindrom(unittest.TestCase):
                def test_ja(self):
                    self.assertTrue(ist_palindrom("Anna"))

                def test_nein(self):
                    self.assertFalse(ist_palindrom("Python"))

            suite = unittest.TestLoader().loadTestsFromTestCase(TestPalindrom)
            ergebnis = unittest.TextTestRunner(verbosity=0).run(suite)
            print("Tests:", ergebnis.testsRun, "Fehlschläge:", len(ergebnis.failures))
        """, output = "Tests: 2 Fehlschläge: 0")
        text("""
            **Debugging** heißt: Fehler finden. Bewährte Strategien:
            - Die Fehlermeldung genau lesen (von unten nach oben)
            - Mit `print()` Zwischenwerte anzeigen – z.B. `print(f"{x=}")`
            - Das Problem verkleinern: Welche Zeile genau macht Ärger?
            - **Rubber Duck Debugging**: Erkläre deinen Code laut einer Gummiente (oder Monty 🐍) – oft fällt dir der Fehler dabei selbst auf!
        """)
        code("""
            def summe_bis(n):
                summe = 0
                for i in range(n):
                    summe += i
                    print(f"{i=} {summe=}")
                return summe

            print(summe_bis(3))
        """, output = """
            i=0 summe=0
            i=1 summe=1
            i=2 summe=3
            3
        """)
        tip("Siehst du den Bug? Wer die Zahlen **bis einschließlich** n addieren will, braucht `range(n + 1)` – die Debug-Ausgaben zeigen es sofort.")
        text("In professionellen IDEs gibt es außerdem einen **Debugger**: Du setzt Haltepunkte (Breakpoints) und gehst den Code Zeile für Zeile durch.")

        choice("Was ist eine gute erste Maßnahme bei einem Fehler?", "Die Fehlermeldung genau lesen", "Den ganzen Code löschen", "Den Computer neu starten", "Zufällig etwas ändern")
        truth("`unittest` ist in der Standardbibliothek von Python enthalten.", true)
        output("""
            x = 7
            print(f"{x=}")
        """, "x=7", "7", "x", "{x=}")
        choice("Was ist „Rubber Duck Debugging“?", "Den Code laut erklären, um den Fehler selbst zu finden", "Ein Python-Paket", "Ein Fehlertyp", "Eine Testmethode mit Enten-Emojis")
        output("""
            def summe_bis(n):
                return sum(range(n + 1))

            print(summe_bis(4))
        """, "10", "6", "4", "15")
        match("assert" to "einfacher Test", "unittest" to "Test-Modul", "Breakpoint" to "Haltepunkt im Debugger", "print(f\"{x=}\")" to "Wert anzeigen")
        fill("Vervollständige den Test.", """
            def doppelt(x):
                return x * 2

            ?? doppelt(4) == 8
            print("Test bestanden")
        """, answers = listOf("assert"), distractors = listOf("test", "check", "if"), output = "Test bestanden")
        write("Diese Funktion soll die **Vokale** in einem Text zählen, liefert aber falsche Ergebnisse. Finde und behebe den Fehler.",
            starter = """
                def vokale(text):
                    anzahl = 0
                    for b in text:
                        if b in "aeiou":
                            anzahl += 1
                    return anzahl

                print(vokale("PYTHON macht Spass"))
            """,
            solution = """
                def vokale(text):
                    anzahl = 0
                    for b in text.lower():
                        if b in "aeiou":
                            anzahl += 1
                    return anzahl

                print(vokale("PYTHON macht Spass"))
            """,
            output = "3",
            test = """
                assert vokale("AEIOU") == 5
                assert vokale("xyz") == 0
            """,
            hint = "Großbuchstaben werden nicht gezählt – wandle den Text vorher mit `.lower()` um.")
    }

    checkpoint()
}
