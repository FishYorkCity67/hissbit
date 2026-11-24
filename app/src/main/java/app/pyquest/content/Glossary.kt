package app.pyquest.content

import app.pyquest.data.GlossaryTerm
import app.pyquest.data.PythonTip
import app.pyquest.data.RefEntry

private fun g(term: String, meaning: String) = GlossaryTerm(term, meaning)

val Glossary: List<GlossaryTerm> = listOf(
    g("Algorithmus", "Eine genaue Schritt-für-Schritt-Anleitung, um ein Problem zu lösen – z.B. ein Rezept für den Computer."),
    g("Argument", "Der konkrete Wert, den du einer Funktion beim Aufruf übergibst, z.B. `5` in `quadrat(5)`."),
    g("Attribut", "Eine Variable, die zu einem Objekt gehört, z.B. `hund.name`."),
    g("Ausdruck", "Etwas, das einen Wert ergibt, z.B. `3 + 4` oder `len(liste)`."),
    g("Bedingung", "Ein Ausdruck, der `True` oder `False` ergibt und z.B. in `if` oder `while` steht."),
    g("Bibliothek", "Fertiger Code, den du mit `import` benutzen kannst (z.B. `math`)."),
    g("Block", "Zusammengehörige Zeilen mit gleicher Einrückung, z.B. der Inhalt einer Schleife."),
    g("Bool", "Datentyp mit den Werten `True` und `False`."),
    g("Bug", "Ein Fehler im Programm. Ihn zu finden und zu beheben heißt **Debugging**."),
    g("Built-in", "Eine eingebaute Funktion, die ohne Import verfügbar ist, z.B. `print`, `len`, `range`."),
    g("Dekorator", "Eine Funktion, die eine andere Funktion erweitert. Wird mit `@name` über eine Funktion geschrieben."),
    g("Dictionary", "Sammlung von Schlüssel-Wert-Paaren: `{\"name\": \"Monty\"}`."),
    g("Docstring", "Ein String direkt am Anfang einer Funktion/Klasse, der sie beschreibt."),
    g("Einrückung", "Leerzeichen am Zeilenanfang. In Python legen sie fest, welche Zeilen zu einem Block gehören."),
    g("Exception", "Ein Fehler, der zur Laufzeit auftritt (z.B. `ValueError`). Kann mit `try/except` abgefangen werden."),
    g("f-String", "Ein String mit `f` davor, in dem `{ausdruck}` durch den Wert ersetzt wird."),
    g("Float", "Kommazahl, z.B. `3.14`."),
    g("Funktion", "Ein benannter, wiederverwendbarer Codeblock, definiert mit `def`."),
    g("Generator", "Eine Funktion mit `yield`, die Werte nach und nach liefert, statt alle auf einmal."),
    g("Gültigkeitsbereich", "(Scope) Der Bereich, in dem eine Variable sichtbar ist – lokal in einer Funktion oder global."),
    g("IDE", "Entwicklungsumgebung: ein Editor mit Extras wie Fehlersuche, z.B. VS Code oder PyCharm."),
    g("Immutable", "Unveränderlich: Der Wert kann nach dem Erstellen nicht geändert werden (z.B. `str`, `tuple`, `int`)."),
    g("Index", "Die Position eines Elements in einer Sequenz. Beginnt bei 0."),
    g("Instanz", "Ein konkretes Objekt einer Klasse: `bello = Hund()` – `bello` ist eine Instanz von `Hund`."),
    g("Integer", "Ganzzahl, z.B. `42`."),
    g("Interpreter", "Das Programm, das Python-Code Zeile für Zeile liest und ausführt."),
    g("Iterable", "Alles, was man mit `for` durchlaufen kann: Listen, Strings, Dicts, `range` …"),
    g("Iterator", "Ein Objekt, das mit `next()` nacheinander Elemente liefert."),
    g("Klasse", "Ein Bauplan für Objekte, definiert mit `class`."),
    g("Kommentar", "Text nach `#`, den Python ignoriert."),
    g("Konkatenation", "Das Verbinden von Strings mit `+`."),
    g("Lambda", "Eine kleine Funktion ohne Namen: `lambda x: x * 2`."),
    g("Liste", "Geordnete, veränderbare Sammlung: `[1, 2, 3]`."),
    g("Literal", "Ein direkt hingeschriebener Wert, z.B. `42`, `\"Hi\"`, `[1, 2]`."),
    g("Methode", "Eine Funktion, die zu einem Objekt gehört: `text.upper()`."),
    g("Modul", "Eine Python-Datei mit Code, die man importieren kann."),
    g("Mutable", "Veränderlich: Der Inhalt kann geändert werden (z.B. `list`, `dict`, `set`)."),
    g("None", "Spezialwert für „nichts“ bzw. „kein Wert“."),
    g("Objekt", "Alles in Python ist ein Objekt – mit Typ, Wert und eventuell Methoden."),
    g("Operator", "Zeichen für eine Operation, z.B. `+`, `==`, `and`."),
    g("Paket", "Eine Sammlung von Modulen. Installiert auf dem PC mit `pip`."),
    g("Parameter", "Der Platzhalter-Name in der Funktionsdefinition: `def f(x)` – `x` ist ein Parameter."),
    g("PEP 8", "Der offizielle Styleguide, wie Python-Code aussehen sollte."),
    g("pip", "Das Werkzeug, um Python-Pakete auf dem Computer zu installieren."),
    g("Rekursion", "Wenn sich eine Funktion selbst aufruft."),
    g("Rückgabewert", "Der Wert, den eine Funktion mit `return` zurückgibt."),
    g("Schleife", "Wiederholt Code: `for` (für jedes Element) oder `while` (solange eine Bedingung gilt)."),
    g("Schlüsselwort", "Ein reserviertes Wort mit fester Bedeutung, z.B. `if`, `for`, `def`, `class`."),
    g("Set", "Ungeordnete Menge ohne Duplikate: `{1, 2, 3}`."),
    g("Slicing", "Einen Teil einer Sequenz ausschneiden: `liste[1:3]`."),
    g("Standardbibliothek", "Alle Module, die mit Python mitgeliefert werden – „Batteries included“."),
    g("String", "Zeichenkette / Text: `\"Hallo\"`."),
    g("Syntax", "Die Grammatik einer Programmiersprache – die Regeln, wie Code geschrieben werden muss."),
    g("Traceback", "Die Fehlermeldung, die zeigt, wo genau im Code ein Fehler aufgetreten ist."),
    g("Tupel", "Unveränderliche, geordnete Sammlung: `(1, 2)`."),
    g("Typ", "Die Art eines Werts: `int`, `str`, `list` … Herausfinden mit `type()`."),
    g("Typumwandlung", "Einen Wert in einen anderen Typ umwandeln, z.B. `int(\"5\")`."),
    g("Unpacking", "Werte einer Sequenz auf mehrere Variablen verteilen: `a, b = (1, 2)`."),
    g("Variable", "Ein Name, der auf einen Wert zeigt: `alter = 14`."),
    g("Vererbung", "Eine Klasse übernimmt Eigenschaften und Methoden einer anderen Klasse."),
    g("venv", "Virtuelle Umgebung – trennt die Pakete verschiedener Projekte auf dem PC."),
    g("Zuweisung", "Einer Variable mit `=` einen Wert geben."),
)

private fun err(id: String, title: String, summary: String, body: String, code: String) =
    RefEntry(id, title, summary, body.trimIndent().trim(), code.trimIndent().trimEnd(), listOf("fehler", "error", id))

val ErrorLexicon: List<RefEntry> = listOf(
    err("syntax", "SyntaxError", "Die Schreibweise ist ungültig.", """
        **Typische Ursachen:** fehlende Klammer, fehlendes Anführungszeichen, vergessener Doppelpunkt nach `if`/`for`/`def`, `=` statt `==` in einer Bedingung.

        **So behebst du ihn:** Schau dir die markierte Zeile **und die Zeile davor** an – oft fehlt dort etwas.
    """, """
        # Fehler: Doppelpunkt fehlt
        # if 5 > 3
        #     print("ja")
        if 5 > 3:
            print("So ist es richtig")
    """),
    err("indent", "IndentationError", "Die Einrückung stimmt nicht.", """
        Python erwartet nach einem Doppelpunkt einen eingerückten Block – oder eine Zeile ist unerwartet eingerückt.

        **Tipp:** Verwende immer 4 Leerzeichen und mische nie Tabs und Leerzeichen.
    """, """
        def hallo():
            print("eingerückt = richtig")

        hallo()
    """),
    err("name", "NameError", "Python kennt einen Namen nicht.", """
        **Ursachen:** Tippfehler, Variable wird benutzt, bevor sie existiert, Anführungszeichen bei Text vergessen, Groß-/Kleinschreibung (`Print` statt `print`).

        Python schlägt oft den richtigen Namen vor: *Did you mean: 'print'?*
    """, """
        nachricht = "Hallo"
        print(nachricht)   # nicht: print(Nachricht)
    """),
    err("type", "TypeError", "Ein Wert hat den falschen Typ.", """
        **Beispiele:** `"Alter: " + 14` (Text + Zahl), Funktion mit falscher Anzahl Argumente aufgerufen, `len(5)`.

        **Lösung:** Umwandeln mit `str()`, `int()`, `float()` – oder f-Strings verwenden.
    """, """
        alter = 14
        print("Alter: " + str(alter))
        print(f"Alter: {alter}")
    """),
    err("value", "ValueError", "Der Typ passt, der Wert aber nicht.", """
        Klassiker: `int("abc")` oder `int("3.5")`. Auch `liste.remove(x)`, wenn x nicht in der Liste ist.

        **Lösung:** Eingaben vorher prüfen (`isdigit()`) oder mit `try/except ValueError` abfangen.
    """, """
        eingabe = "abc"
        if eingabe.isdigit():
            print(int(eingabe))
        else:
            print("Keine gültige Zahl")
    """),
    err("zero", "ZeroDivisionError", "Division durch 0.", """
        Auch `%` und `//` durch 0 sind verboten. Prüfe den Teiler vorher.
    """, """
        teiler = 0
        if teiler != 0:
            print(10 / teiler)
        else:
            print("Teilen durch 0 geht nicht!")
    """),
    err("index", "IndexError", "Der Index liegt außerhalb der Sequenz.", """
        Eine Liste mit 3 Elementen hat die Indizes 0, 1, 2. `liste[3]` gibt es nicht!

        **Tipp:** Der letzte Index ist `len(liste) - 1` – oder einfach `liste[-1]`.
    """, """
        farben = ["rot", "grün", "blau"]
        print(farben[2])
        print(farben[-1])
    """),
    err("key", "KeyError", "Der Schlüssel fehlt im Dictionary.", """
        **Lösung:** Mit `in` prüfen oder `get()` mit Standardwert benutzen.
    """, """
        alter = {"Ada": 36}
        print(alter.get("Alan", "unbekannt"))
        if "Ada" in alter:
            print(alter["Ada"])
    """),
    err("attribute", "AttributeError", "Das Objekt hat diese Methode/dieses Attribut nicht.", """
        Oft ein Tippfehler (`liste.apend`) oder der falsche Typ (`5.upper()`). Mit `dir(objekt)` siehst du, was es gibt.
    """, """
        liste = [1, 2]
        liste.append(3)   # nicht: liste.apend(3)
        print(liste)
    """),
    err("import", "ModuleNotFoundError / ImportError", "Ein Modul wurde nicht gefunden.", """
        Prüfe die Schreibweise. Externe Pakete (z.B. `numpy`) müssen auf dem PC erst mit `pip` installiert werden – in dieser App steht die komplette Standardbibliothek zur Verfügung.
    """, """
        import math   # Standardbibliothek – klappt immer
        print(math.sqrt(2))
    """),
    err("file", "FileNotFoundError", "Die Datei existiert nicht.", """
        Prüfe Dateinamen und Pfad. Zum Erstellen einer neuen Datei öffnest du sie mit `"w"`. Mit `pathlib.Path(name).exists()` kannst du vorher prüfen.
    """, """
        from pathlib import Path
        p = Path("gibtsnicht.txt")
        print("existiert" if p.exists() else "Datei fehlt – erst anlegen!")
    """),
    err("recursion", "RecursionError", "Zu viele verschachtelte Funktionsaufrufe.", """
        Meist fehlt in einer rekursiven Funktion der **Basisfall**, oder er wird nie erreicht.
    """, """
        def zaehle_runter(n):
            if n <= 0:          # Basisfall!
                return "fertig"
            return zaehle_runter(n - 1)

        print(zaehle_runter(50))
    """),
    err("unbound", "UnboundLocalError", "Lokale Variable vor der Zuweisung benutzt.", """
        Passiert, wenn du in einer Funktion eine Variable änderst, die außerhalb definiert wurde. Lösung: Wert als Parameter übergeben und zurückgeben – oder `global` verwenden.
    """, """
        punkte = 0

        def plus_eins(p):
            return p + 1

        punkte = plus_eins(punkte)
        print(punkte)
    """),
    err("eof", "EOFError", "input() bekam keine Eingabe.", """
        Tritt auf, wenn ein Programm mit `input()` liest, aber keine Eingabe mehr kommt. Im Spielplatz kannst du Eingaben in der Konsole eintippen.
    """, """
        antworten = ["ja"]  # statt input() zum Testen
        print(antworten.pop(0))
    """),
    err("stop", "StopIteration", "Ein Iterator hat keine Werte mehr.", """
        `next()` wurde öfter aufgerufen, als Elemente vorhanden sind. Mit `next(it, standard)` vermeidest du den Fehler.
    """, """
        it = iter([1])
        print(next(it))
        print(next(it, "keine Werte mehr"))
    """),
    err("assert", "AssertionError", "Eine assert-Prüfung ist fehlgeschlagen.", """
        `assert` prüft eine Annahme. Ist sie falsch, stimmt im Code etwas nicht – genau dafür sind Tests da.
    """, """
        ergebnis = 2 + 2
        assert ergebnis == 4, "Mathe kaputt?"
        print("Test bestanden")
    """),
)

private fun tip(title: String, code: String) = PythonTip(title, code.trimIndent().trim())

val PythonTips: List<PythonTip> = listOf(
    tip("Werte tauschen ohne Hilfsvariable", "a, b = b, a"),
    tip("String umdrehen", "\"Python\"[::-1]  # 'nohtyP'"),
    tip("Duplikate entfernen", "list(dict.fromkeys([3, 1, 3, 2]))"),
    tip("Debuggen mit f-Strings", "x = 42\nprint(f\"{x=}\")  # x=42"),
    tip("Große Zahlen lesbar schreiben", "einwohner = 83_000_000"),
    tip("Index und Wert zugleich", "for i, x in enumerate(liste, 1):\n    print(i, x)"),
    tip("Zwei Listen parallel", "for name, note in zip(namen, noten):\n    print(name, note)"),
    tip("Sortieren nach Länge", "sorted(woerter, key=len)"),
    tip("Häufigste Elemente", "from collections import Counter\nCounter(text).most_common(3)"),
    tip("Gibt es mindestens eins?", "any(x > 10 for x in zahlen)"),
    tip("Erfüllen alle die Bedingung?", "all(x > 0 for x in zahlen)"),
    tip("Sicherer Dict-Zugriff", "alter.get(\"Ada\", 0)"),
    tip("Vergleiche verketten", "if 0 < x < 10:\n    print(\"einstellig\")"),
    tip("Wenn-dann in einer Zeile", "status = \"ok\" if punkte > 50 else \"üben\""),
    tip("Liste zu Text", "\", \".join([\"a\", \"b\", \"c\"])"),
    tip("Text in Wörter zerlegen", "\"Python ist toll\".split()"),
    tip("Liste in einer Zeile bauen", "[x * x for x in range(10)]"),
    tip("Dictionary in einer Zeile", "{w: len(w) for w in woerter}"),
    tip("Erstes und Rest", "erster, *rest = [1, 2, 3, 4]"),
    tip("Dicts zusammenführen", "alles = standard | eigene"),
    tip("Walross-Operator", "if (n := len(daten)) > 10:\n    print(n)"),
    tip("Ganzzahl-Division und Rest", "minuten, sekunden = divmod(135, 60)"),
    tip("Runden auf 2 Stellen", "round(3.14159, 2)  # 3.14"),
    tip("Zahl aus anderem System", "int(\"ff\", 16)  # 255"),
    tip("Zahl als Binärzahl", "bin(10)  # '0b1010'"),
    tip("Ist das eine Zahl?", "\"123\".isdigit()  # True"),
    tip("Zufällig auswählen", "import random\nrandom.choice([\"rot\", \"blau\"])"),
    tip("Liste mischen", "random.shuffle(karten)"),
    tip("Datei sicher öffnen", "with open(\"a.txt\") as f:\n    text = f.read()"),
    tip("Schön formatierte JSON", "json.dumps(daten, indent=2)"),
    tip("Heutiges Datum", "from datetime import date\ndate.today()"),
    tip("Zeit messen", "import time\nstart = time.perf_counter()"),
    tip("Ergebnisse cachen", "from functools import lru_cache\n\n@lru_cache\ndef fib(n): ..."),
    tip("Zahlen auf Breite formatieren", "f\"{preis:8.2f} €\""),
    tip("Text zentrieren", "\"Titel\".center(20, \"-\")"),
    tip("Endlosschleife mit Ausgang", "while True:\n    if fertig:\n        break"),
    tip("Schleife mit else", "for x in daten:\n    if x < 0: break\nelse:\n    print(\"alle positiv\")"),
    tip("Mehrere Rückgabewerte", "def mm(l):\n    return min(l), max(l)"),
    tip("Standardwert-Falle vermeiden", "def f(liste=None):\n    liste = liste or []"),
    tip("Das Zen von Python", "import this"),
)
