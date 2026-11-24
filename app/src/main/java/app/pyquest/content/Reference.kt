package app.pyquest.content

import app.pyquest.data.LessonIcon
import app.pyquest.data.RefCategory
import app.pyquest.data.RefEntry

private class CategoryBuilder(private val id: String) {
    val entries = mutableListOf<RefEntry>()
    fun e(title: String, summary: String, body: String = "", code: String? = null, vararg keywords: String) {
        entries += RefEntry(
            id = "$id-${entries.size + 1}",
            title = title,
            summary = summary,
            body = body.trimIndent().trim(),
            code = code?.trimIndent()?.trimEnd(),
            keywords = keywords.toList(),
        )
    }
}

private fun category(id: String, title: String, icon: LessonIcon, block: CategoryBuilder.() -> Unit) =
    RefCategory(id, title, icon, CategoryBuilder(id).apply(block).entries.toList())

val ReferenceCategories: List<RefCategory> = listOf(
    category("basics", "Grundlagen", LessonIcon.BOOK) {
        e("print() – Ausgabe", "Gibt Werte auf dem Bildschirm aus.",
            "Mehrere Werte trennst du mit Kommas. Mit `sep` änderst du das Trennzeichen, mit `end` das Zeilenende.",
            """
            print("Hallo")
            print("a", "b", "c", sep="-")
            print("ohne Zeilenumbruch", end=" ")
            print("– weiter geht's")
            """, "ausgabe", "sep", "end")
        e("Variablen", "Speichern Werte unter einem Namen.",
            """
            Eine Variable entsteht durch eine Zuweisung mit `=`. Namen bestehen aus Buchstaben, Ziffern und `_`, dürfen nicht mit einer Ziffer beginnen und unterscheiden Groß-/Kleinschreibung. Üblich ist `snake_case`.
            """,
            """
            name = "Monty"
            alter = 3
            alter = alter + 1
            print(name, "ist", alter)
            """, "zuweisung", "snake_case")
        e("Kommentare", "Notizen im Code, die Python ignoriert.",
            "Einzeilige Kommentare beginnen mit `#`. Für längere Erklärungen von Funktionen nutzt man Docstrings.",
            """
            # Das ist ein Kommentar
            x = 42  # Kommentar am Zeilenende
            print(x)
            """, "#", "notiz")
        e("input() – Eingabe", "Liest eine Zeile Text von der Tastatur.",
            "`input()` liefert **immer einen String**. Für Zahlen musst du umwandeln, z.B. mit `int()` oder `float()`.",
            """
            # Im Spielplatz kannst du die Eingabe eintippen:
            # zahl = int(input("Zahl: "))
            text = "42"
            zahl = int(text)
            print(zahl * 2)
            """, "eingabe", "tastatur")
        e("Einrückung", "Blöcke werden in Python durch Einrückung markiert.",
            "Nach einem Doppelpunkt (`if`, `for`, `def` …) folgt ein eingerückter Block. Standard sind **4 Leerzeichen**. Alles mit gleicher Einrückung gehört zum selben Block.",
            """
            for i in range(2):
                print("im Block", i)
                print("auch im Block")
            print("nach dem Block")
            """, "block", "leerzeichen", "indent")
        e("Rechenoperatoren", "`+ - * / // % **`",
            "`/` ergibt immer eine Kommazahl, `//` ganzzahlig, `%` den Rest, `**` die Potenz. Es gilt Punkt vor Strich.",
            """
            print(7 + 2, 7 - 2, 7 * 2)
            print(7 / 2, 7 // 2, 7 % 2)
            print(2 ** 8)
            print((2 + 3) * 4)
            """, "plus", "minus", "modulo", "potenz")
        e("Vergleichsoperatoren", "`== != < > <= >=` liefern True oder False.",
            "Achtung: `=` weist zu, `==` vergleicht! Vergleiche lassen sich verketten: `1 < x < 10`.",
            """
            x = 5
            print(x == 5, x != 5)
            print(x > 3, x <= 4)
            print(1 < x < 10)
            """, "gleich", "vergleich")
        e("Logische Operatoren", "`and`, `or` und `not` verknüpfen Bedingungen.",
            "`and` ist nur wahr, wenn beide Seiten wahr sind. `or`, wenn mindestens eine wahr ist. `not` kehrt um.",
            """
            sonne = True
            ferien = False
            print(sonne and ferien)
            print(sonne or ferien)
            print(not ferien)
            """, "und", "oder", "nicht", "bool")
        e("Kurz-Zuweisungen", "`+= -= *= /=` ändern eine Variable direkt.",
            "",
            """
            punkte = 10
            punkte += 5   # wie punkte = punkte + 5
            punkte *= 2
            print(punkte)
            """, "+=", "erhöhen")
        e("None", "Der Wert für „nichts“.",
            "`None` steht für einen fehlenden Wert. Funktionen ohne `return` geben `None` zurück. Geprüft wird mit `is None`.",
            """
            ergebnis = None
            print(ergebnis is None)
            def nichts():
                pass
            print(nichts())
            """, "null", "leer")
        e("pass", "Ein Platzhalter, der nichts tut.",
            "Wird gebraucht, wenn Python einen Block erwartet, du aber (noch) keinen Code hast.",
            """
            def spaeter():
                pass  # kommt noch

            print("läuft trotzdem")
            """, "platzhalter")
    },
    category("types", "Datentypen", LessonIcon.BOX) {
        e("int – Ganzzahlen", "Ganze Zahlen ohne Komma, beliebig groß.",
            "Unterstriche machen große Zahlen lesbar: `1_000_000`.",
            """
            a = 42
            b = 1_000_000
            print(a + b)
            print(2 ** 100)
            """, "ganzzahl", "integer")
        e("float – Kommazahlen", "Zahlen mit Nachkommastellen.",
            "Kommazahlen werden mit Punkt geschrieben. Sie sind nicht immer exakt – deshalb runden: `round(x, 2)`.",
            """
            preis = 2.99
            print(preis * 3)
            print(0.1 + 0.2)
            print(round(0.1 + 0.2, 2))
            """, "kommazahl", "dezimal")
        e("str – Texte", "Zeichenketten in Anführungszeichen.",
            "Strings sind unveränderlich. Methoden wie `upper()` liefern einen **neuen** String.",
            """
            gruss = "Hallo"
            print(gruss + " Welt")
            print(gruss * 3)
            print(len(gruss))
            """, "string", "text")
        e("bool – Wahrheitswerte", "`True` oder `False`.",
            "Ergebnis jedes Vergleichs. Mit Großbuchstaben am Anfang schreiben!",
            """
            ist_gross = 10 > 5
            print(ist_gross, type(ist_gross))
            print(True + True)  # True zählt als 1
            """, "true", "false", "boolean")
        e("list – Listen", "Geordnete, veränderbare Sammlung: `[1, 2, 3]`.",
            "", """
            farben = ["rot", "grün"]
            farben.append("blau")
            print(farben, farben[0], len(farben))
            """, "liste", "array")
        e("tuple – Tupel", "Wie eine Liste, aber unveränderlich: `(1, 2)`.",
            "Ideal für feste Werte wie Koordinaten. Tupel lassen sich entpacken.",
            """
            punkt = (3, 4)
            x, y = punkt
            print(x, y)
            """, "tupel", "unveränderlich")
        e("dict – Wörterbücher", "Schlüssel-Wert-Paare: `{\"a\": 1}`.",
            "", """
            alter = {"Ada": 36, "Alan": 41}
            alter["Grace"] = 85
            print(alter["Ada"], alter)
            """, "dictionary", "schlüssel", "map")
        e("set – Mengen", "Ungeordnet, ohne Duplikate: `{1, 2, 3}`.",
            "Ein leeres Set erzeugst du mit `set()` – `{}` ist ein leeres Dictionary!",
            """
            zahlen = {1, 2, 2, 3, 3, 3}
            print(zahlen)
            print(2 in zahlen)
            """, "menge", "duplikate")
        e("Typumwandlung", "`int()`, `float()`, `str()`, `bool()`, `list()` …",
            "Klappt eine Umwandlung nicht, gibt es einen `ValueError`.",
            """
            print(int("42") + 1)
            print(float("3.5"))
            print(str(99) + "!")
            print(list("abc"))
            print(bool(0), bool("Hallo"))
            """, "casting", "umwandeln", "konvertieren")
        e("type() und isinstance()", "Den Typ eines Werts herausfinden.",
            "`isinstance(x, int)` ist meist besser als `type(x) == int`, weil es auch Unterklassen erkennt.",
            """
            print(type(3.14))
            print(isinstance(5, int))
            print(isinstance("5", (int, float)))
            """, "typ")
        e("Wahr oder falsch? (Truthiness)", "Jeder Wert ist in einer Bedingung wahr oder falsch.",
            "Falsch sind: `False`, `None`, `0`, `0.0`, `\"\"`, `[]`, `{}`, `set()`. Alles andere ist wahr.",
            """
            for wert in [0, 1, "", "x", [], [0], None]:
                print(repr(wert), "->", bool(wert))
            """, "truthy", "falsy")
    },
    category("strings", "Strings", LessonIcon.CHAT) {
        e("Indizes & Slicing", "`s[0]`, `s[-1]`, `s[1:4]`, `s[::-1]`",
            "Die Zählung beginnt bei 0. Negative Indizes zählen von hinten. Beim Slicing `[start:stop:schritt]` ist `stop` nicht mehr dabei.",
            """
            s = "Python"
            print(s[0], s[-1])
            print(s[1:4])
            print(s[:2], s[2:])
            print(s[::-1])
            """, "index", "teilstring", "umdrehen")
        e("f-Strings", "Werte direkt in Text einsetzen: `f\"{x}\"`.",
            "In geschweiften Klammern steht ein beliebiger Ausdruck. Formatierung nach dem Doppelpunkt: `{preis:.2f}`, `{zahl:5}`, `{x:>8}`. Mit `{x=}` siehst du Name und Wert.",
            """
            name = "Monty"
            preis = 3.14159
            print(f"Hallo {name}!")
            print(f"Preis: {preis:.2f} €")
            print(f"{name:>10}|")
            x = 42
            print(f"{x=}")
            """, "format", "formatieren", "platzhalter")
        e("Groß- und Kleinschreibung", "`upper()`, `lower()`, `title()`, `capitalize()`",
            "", """
            s = "hallo welt"
            print(s.upper())
            print(s.title())
            print(s.capitalize())
            print("ABC".lower())
            """, "upper", "lower")
        e("strip()", "Entfernt Leerzeichen (oder Zeichen) am Rand.",
            "`lstrip()` nur links, `rstrip()` nur rechts.",
            """
            eingabe = "   Monty  "
            print("[" + eingabe.strip() + "]")
            print("---Titel---".strip("-"))
            """, "trim", "leerzeichen")
        e("split() und join()", "Text zerlegen und zusammensetzen.",
            "`split()` ohne Argument trennt an Leerzeichen. `join()` wird auf dem Trennzeichen aufgerufen.",
            """
            satz = "Python ist super"
            woerter = satz.split()
            print(woerter)
            print("-".join(woerter))
            print("a,b,c".split(","))
            """, "zerlegen", "trennen", "verbinden")
        e("replace()", "Teile eines Strings ersetzen.",
            "", """
            s = "Ich mag Java"
            print(s.replace("Java", "Python"))
            print("aaa".replace("a", "b", 2))
            """, "ersetzen")
        e("Suchen: find(), count(), in", "Teilstrings finden und zählen.",
            "`find()` liefert die Position oder -1. `in` prüft nur, ob es vorkommt.",
            """
            s = "banane"
            print(s.find("na"))
            print(s.count("a"))
            print("nan" in s)
            print(s.startswith("ba"), s.endswith("ne"))
            """, "find", "suchen", "startswith")
        e("Inhalt prüfen", "`isdigit()`, `isalpha()`, `isalnum()`, `isspace()` …",
            "Praktisch, um Eingaben zu prüfen, bevor man sie umwandelt.",
            """
            print("123".isdigit())
            print("abc".isalpha())
            print("abc123".isalnum())
            print("Monty".istitle())
            """, "prüfen", "isdigit")
        e("Escape-Sequenzen", "`\\n`, `\\t`, `\\\\`, `\\\"`",
            "Mit einem `r` vor dem String (Raw-String) werden Backslashes nicht interpretiert – praktisch für Pfade und reguläre Ausdrücke.",
            """
            print("Zeile 1\nZeile 2")
            print("A\tB")
            print("Er sagt: \"Hi\"")
            print(r"C:\neu\ordner")
            """, "backslash", "zeilenumbruch", "raw")
        e("Mehrzeilige Strings", "Mit drei Anführungszeichen.",
            "", """
            gedicht = '''Rosen sind rot,
            Python ist blau,
            Code macht Spaß,
            das weiß ich genau.'''
            print(gedicht)
            """, "mehrzeilig", "docstring")
        e("Zeichen und Zahlen", "`ord()` und `chr()` wandeln Zeichen in Codes um.",
            "", """
            print(ord("A"), chr(66))
            print([chr(c) for c in range(97, 102)])
            """, "ascii", "unicode")
    },
    category("lists", "Listen", LessonIcon.BOX) {
        e("Erstellen & Zugreifen", "`[a, b, c]`, `liste[i]`, `len(liste)`",
            "Listen können alles enthalten – auch gemischte Typen und andere Listen.",
            """
            zahlen = [10, 20, 30]
            print(zahlen[0], zahlen[-1], len(zahlen))
            zahlen[1] = 99
            print(zahlen)
            """, "liste", "index")
        e("Hinzufügen", "`append()`, `insert()`, `extend()`",
            "`append` hängt **ein** Element an, `extend` alle Elemente einer anderen Liste.",
            """
            l = [1, 2]
            l.append(3)
            l.insert(0, 0)
            l.extend([4, 5])
            print(l)
            """, "append", "anhängen")
        e("Entfernen", "`remove()`, `pop()`, `del`, `clear()`",
            "`remove(x)` löscht den ersten Wert x, `pop(i)` entfernt und liefert das Element an Position i (ohne i: das letzte).",
            """
            l = ["a", "b", "c", "d"]
            l.remove("b")
            letztes = l.pop()
            del l[0]
            print(l, letztes)
            """, "löschen", "pop")
        e("Sortieren", "`sort()` ändert die Liste, `sorted()` liefert eine neue.",
            "Mit `reverse=True` absteigend, mit `key=` nach eigener Regel.",
            """
            zahlen = [3, 1, 2]
            print(sorted(zahlen))
            zahlen.sort(reverse=True)
            print(zahlen)
            woerter = ["Kiwi", "Apfel", "Ei"]
            print(sorted(woerter, key=len))
            """, "sort", "reihenfolge")
        e("Suchen & Zählen", "`in`, `index()`, `count()`",
            "", """
            l = [5, 3, 5, 1]
            print(3 in l, l.index(5), l.count(5))
            """, "suchen")
        e("Min, Max, Summe", "`min()`, `max()`, `sum()`",
            "", """
            noten = [2, 1, 3, 2]
            print(min(noten), max(noten), sum(noten))
            print(sum(noten) / len(noten))
            """, "durchschnitt", "summe")
        e("Slicing", "Teillisten mit `[start:stop:schritt]`.",
            "", """
            l = list(range(10))
            print(l[2:5], l[:3], l[-3:], l[::2])
            """, "teilliste")
        e("Kopieren statt Verknüpfen", "`b = a` kopiert nicht!",
            "Mit `b = a` zeigen beide Namen auf **dieselbe** Liste. Eine echte Kopie bekommst du mit `a.copy()` oder `a[:]`.",
            """
            a = [1, 2]
            b = a
            c = a.copy()
            a.append(3)
            print(b, c)
            """, "copy", "referenz")
        e("Verschachtelte Listen", "Listen in Listen – z.B. für Tabellen.",
            "", """
            feld = [[1, 2, 3], [4, 5, 6]]
            print(feld[1][2])
            for zeile in feld:
                print(zeile)
            """, "matrix", "2d")
        e("List Comprehension", "`[ausdruck for x in daten if bedingung]`",
            "Erzeugt eine neue Liste in einer Zeile.",
            """
            quadrate = [x * x for x in range(6)]
            gerade = [x for x in range(10) if x % 2 == 0]
            print(quadrate, gerade)
            """, "comprehension")
    },
    category("dicts", "Dictionaries & Sets", LessonIcon.BOX) {
        e("Dictionary erstellen", "`{schlüssel: wert}`",
            "Schlüssel müssen eindeutig und unveränderlich sein (z.B. Strings, Zahlen, Tupel).",
            """
            telefon = {"Ada": "0151", "Alan": "0170"}
            leer = {}
            print(telefon, len(telefon), leer)
            """, "dict")
        e("Lesen mit [] und get()", "`d[k]` oder `d.get(k, standard)`",
            "`d[k]` wirft einen `KeyError`, wenn k fehlt. `get` liefert stattdessen `None` oder deinen Standardwert.",
            """
            d = {"a": 1}
            print(d["a"])
            print(d.get("b"))
            print(d.get("b", 0))
            """, "get", "keyerror")
        e("Ändern & Löschen", "`d[k] = v`, `del d[k]`, `pop()`, `update()`",
            "", """
            d = {"a": 1}
            d["b"] = 2
            d.update({"c": 3})
            del d["a"]
            print(d, d.pop("b"), d)
            """, "update", "del")
        e("Durchlaufen", "`keys()`, `values()`, `items()`",
            "", """
            preise = {"Brot": 2.5, "Milch": 1.2}
            for artikel, preis in preise.items():
                print(artikel, preis)
            print(list(preise.keys()), sum(preise.values()))
            """, "items", "schleife")
        e("Zählen mit Dictionaries", "Ein Klassiker: Häufigkeiten ermitteln.",
            "`d.get(x, 0) + 1` zählt bequem hoch. Noch einfacher geht es mit `collections.Counter`.",
            """
            text = "banane"
            zaehler = {}
            for buchstabe in text:
                zaehler[buchstabe] = zaehler.get(buchstabe, 0) + 1
            print(zaehler)
            """, "zählen", "häufigkeit")
        e("Dict Comprehension", "`{k: v for ...}`",
            "", """
            quadrate = {x: x * x for x in range(5)}
            print(quadrate)
            """, "comprehension")
        e("Set-Operationen", "Vereinigung `|`, Schnitt `&`, Differenz `-`",
            "", """
            a = {1, 2, 3}
            b = {3, 4}
            print(a | b, a & b, a - b, a ^ b)
            a.add(9)
            print(a)
            """, "menge", "union", "schnittmenge")
        e("Duplikate entfernen", "Mit `set()` – oder reihenfolgetreu mit `dict.fromkeys()`.",
            "", """
            l = [3, 1, 3, 2, 1]
            print(set(l))
            print(list(dict.fromkeys(l)))
            """, "unique", "doppelte")
    },
    category("flow", "Kontrollfluss", LessonIcon.BRANCH) {
        e("if / elif / else", "Code nur unter einer Bedingung ausführen.",
            "Python prüft von oben nach unten und führt **nur den ersten** passenden Block aus.",
            """
            temperatur = 18
            if temperatur > 25:
                print("Heiß!")
            elif temperatur > 15:
                print("Angenehm")
            else:
                print("Kalt")
            """, "wenn", "bedingung")
        e("Bedingter Ausdruck", "`a if bedingung else b` in einer Zeile.",
            "", """
            alter = 16
            status = "volljährig" if alter >= 18 else "minderjährig"
            print(status)
            """, "ternär", "einzeiler")
        e("while-Schleife", "Wiederholt, solange die Bedingung wahr ist.",
            "Achte darauf, dass die Bedingung irgendwann falsch wird – sonst entsteht eine Endlosschleife.",
            """
            countdown = 3
            while countdown > 0:
                print(countdown)
                countdown -= 1
            print("Start!")
            """, "while", "solange")
        e("for-Schleife", "Durchläuft jedes Element einer Sammlung.",
            "", """
            for frucht in ["Apfel", "Birne"]:
                print(frucht)
            for buchstabe in "Py":
                print(buchstabe)
            """, "for", "schleife")
        e("range()", "Zahlenfolgen: `range(stop)`, `range(start, stop, schritt)`.",
            "Der Stop-Wert ist nicht mehr enthalten.",
            """
            print(list(range(5)))
            print(list(range(2, 8)))
            print(list(range(10, 0, -3)))
            """, "zahlenfolge")
        e("break & continue", "Schleife abbrechen oder Durchlauf überspringen.",
            "", """
            for i in range(10):
                if i == 2:
                    continue
                if i == 5:
                    break
                print(i)
            """, "abbrechen", "überspringen")
        e("else bei Schleifen", "Läuft, wenn die Schleife **ohne** break endet.",
            "", """
            for zahl in [3, 5, 7]:
                if zahl % 2 == 0:
                    print("gerade Zahl gefunden")
                    break
            else:
                print("keine gerade Zahl dabei")
            """, "for else")
        e("enumerate()", "Index und Element gleichzeitig.",
            "", """
            for nr, name in enumerate(["Ada", "Alan"], start=1):
                print(nr, name)
            """, "index", "nummerieren")
        e("zip()", "Mehrere Listen parallel durchlaufen.",
            "", """
            namen = ["Ada", "Alan"]
            jahre = [1815, 1912]
            for name, jahr in zip(namen, jahre):
                print(name, jahr)
            """, "parallel", "paare")
        e("match-case", "Pattern Matching (ab Python 3.10).",
            "`case _:` fängt alles andere ab. Muster können auch Listen, Tupel oder Werte mit `|` kombinieren.",
            """
            def tag_typ(tag):
                match tag:
                    case "Sa" | "So":
                        return "Wochenende"
                    case "Mo" | "Di" | "Mi" | "Do" | "Fr":
                        return "Werktag"
                    case _:
                        return "unbekannt"

            print(tag_typ("Sa"), tag_typ("Mi"))
            """, "switch", "match")
    },
    category("functions", "Funktionen", LessonIcon.MAGIC) {
        e("def & return", "Eigene Funktionen definieren.",
            "`return` beendet die Funktion und gibt einen Wert zurück. Ohne `return` liefert sie `None`.",
            """
            def quadrat(x):
                return x * x

            print(quadrat(4))
            """, "funktion", "rückgabe")
        e("Parameter & Standardwerte", "`def f(a, b=2)`",
            "Parameter mit Standardwert müssen hinter denen ohne stehen. Achtung: Nie eine Liste als Standardwert verwenden – nimm `None`.",
            """
            def gruss(name, gruss="Hallo"):
                return f"{gruss}, {name}!"

            print(gruss("Ada"))
            print(gruss("Alan", "Moin"))
            """, "default", "argument")
        e("Schlüsselwort-Argumente", "`f(b=1, a=2)` – Reihenfolge egal.",
            "", """
            def rechteck(breite, hoehe):
                return breite * hoehe

            print(rechteck(hoehe=3, breite=5))
            """, "keyword", "kwargs")
        e("*args und **kwargs", "Beliebig viele Argumente annehmen.",
            "`*args` sammelt Positionsargumente in einem Tupel, `**kwargs` Schlüsselwort-Argumente in einem Dictionary.",
            """
            def summe(*zahlen):
                return sum(zahlen)

            def profil(**infos):
                for k, v in infos.items():
                    print(k, "=", v)

            print(summe(1, 2, 3))
            profil(name="Monty", art="Python")
            """, "args", "variabel")
        e("Mehrere Rückgabewerte", "Ein Tupel zurückgeben und entpacken.",
            "", """
            def min_max(werte):
                return min(werte), max(werte)

            klein, gross = min_max([4, 9, 1])
            print(klein, gross)
            """, "tupel", "entpacken")
        e("lambda", "Kleine anonyme Funktionen: `lambda x: x * 2`.",
            "Praktisch als `key` beim Sortieren oder für `map`/`filter`.",
            """
            doppelt = lambda x: x * 2
            print(doppelt(5))
            paare = [("Ada", 36), ("Alan", 41), ("Grace", 30)]
            print(sorted(paare, key=lambda p: p[1]))
            """, "anonym")
        e("Gültigkeitsbereich", "Lokale und globale Variablen.",
            "Variablen in einer Funktion sind lokal. Um eine globale Variable zu **ändern**, brauchst du `global` – besser ist es aber, Werte zurückzugeben.",
            """
            zaehler = 0

            def erhoehen():
                global zaehler
                zaehler += 1

            erhoehen()
            erhoehen()
            print(zaehler)
            """, "scope", "global", "lokal")
        e("Rekursion", "Eine Funktion ruft sich selbst auf.",
            "Jede Rekursion braucht einen **Basisfall**, der den Aufruf beendet.",
            """
            def countdown(n):
                if n == 0:
                    print("Start!")
                    return
                print(n)
                countdown(n - 1)

            countdown(3)
            """, "rekursiv")
        e("Docstrings", "Dokumentation direkt in der Funktion.",
            "Der erste String in einer Funktion ist ihr Docstring. `help(funktion)` zeigt ihn an.",
            """
            def flaeche(r):
                '''Berechnet die Kreisfläche für den Radius r.'''
                return 3.14159 * r * r

            print(flaeche.__doc__)
            """, "dokumentation", "help")
        e("Type Hints", "Typangaben für Parameter und Rückgabe.",
            "Type Hints werden nicht erzwungen, helfen aber Editoren und Menschen beim Verstehen.",
            """
            def wiederhole(text: str, n: int = 2) -> str:
                return text * n

            def summe(werte: list[float]) -> float:
                return sum(werte)

            print(wiederhole("ab"), summe([1.5, 2.5]))
            """, "typen", "annotation")
        e("Dekoratoren", "Funktionen mit `@` erweitern.",
            "Ein Dekorator ist eine Funktion, die eine Funktion nimmt und eine neue zurückgibt.",
            """
            def laut(funktion):
                def wrapper(*args):
                    return funktion(*args).upper() + "!"
                return wrapper

            @laut
            def gruss(name):
                return f"hallo {name}"

            print(gruss("monty"))
            """, "decorator", "@")
    },
    category("errors", "Fehlerbehandlung", LessonIcon.BUG) {
        e("try & except", "Fehler abfangen, statt abzustürzen.",
            "Fange möglichst **konkrete** Fehlertypen ab, nicht pauschal alles.",
            """
            try:
                zahl = int("abc")
            except ValueError:
                print("Das war keine Zahl!")
            """, "exception", "abfangen")
        e("Mehrere Fehlertypen & as", "`except (A, B) as e:`",
            "", """
            for wert in ["10", "0", "x"]:
                try:
                    print(100 / int(wert))
                except ZeroDivisionError:
                    print("Nicht durch 0!")
                except ValueError as e:
                    print("Fehler:", e)
            """, "as", "fehlermeldung")
        e("else & finally", "`else` wenn kein Fehler, `finally` immer.",
            "", """
            try:
                ergebnis = 10 / 2
            except ZeroDivisionError:
                print("Fehler")
            else:
                print("Ergebnis:", ergebnis)
            finally:
                print("Fertig – egal was passiert ist.")
            """, "finally", "aufräumen")
        e("raise", "Selbst einen Fehler auslösen.",
            "", """
            def wurzel(x):
                if x < 0:
                    raise ValueError("x darf nicht negativ sein")
                return x ** 0.5

            try:
                wurzel(-4)
            except ValueError as e:
                print("Fehler:", e)
            """, "werfen", "auslösen")
        e("Eigene Exceptions", "Von `Exception` erben.",
            "", """
            class ZuWenigGeld(Exception):
                pass

            try:
                raise ZuWenigGeld("Kontostand reicht nicht")
            except ZuWenigGeld as e:
                print("Eigener Fehler:", e)
            """, "custom", "klasse")
        e("assert", "Annahmen im Code prüfen.",
            "`assert bedingung, \"Meldung\"` löst einen `AssertionError` aus, wenn die Bedingung falsch ist. Gut für Tests.",
            """
            def verdopple(x):
                return x * 2

            assert verdopple(2) == 4
            assert verdopple(0) == 0, "0 sollte 0 bleiben"
            print("Alle Prüfungen bestanden")
            """, "test", "prüfen")
    },
    category("oop", "Klassen & Objekte", LessonIcon.CLASS) {
        e("class & __init__", "Bauplan für eigene Objekte.",
            "`__init__` wird beim Erstellen eines Objekts aufgerufen. `self` ist das Objekt selbst.",
            """
            class Hund:
                def __init__(self, name):
                    self.name = name

            bello = Hund("Bello")
            print(bello.name)
            """, "klasse", "objekt", "konstruktor")
        e("Methoden", "Funktionen, die zu einer Klasse gehören.",
            "", """
            class Zaehler:
                def __init__(self):
                    self.wert = 0

                def klick(self):
                    self.wert += 1
                    return self.wert

            z = Zaehler()
            z.klick()
            print(z.klick())
            """, "methode", "self")
        e("Vererbung & super()", "Eine Klasse erweitert eine andere.",
            "Die Kindklasse übernimmt alles von der Elternklasse und kann Methoden überschreiben. `super()` ruft die Version der Elternklasse auf.",
            """
            class Tier:
                def __init__(self, name):
                    self.name = name
                def laut(self):
                    return "..."

            class Katze(Tier):
                def laut(self):
                    return "Miau"

            class Loewe(Katze):
                def laut(self):
                    return super().laut().upper() + "!"

            for t in [Tier("?"), Katze("Mimi"), Loewe("Leo")]:
                print(t.name, t.laut())
            """, "vererbung", "erben", "super")
        e("Magische Methoden", "`__str__`, `__repr__`, `__len__`, `__eq__`, `__add__` …",
            "Damit verhalten sich eigene Objekte wie eingebaute Typen.",
            """
            class Vektor:
                def __init__(self, x, y):
                    self.x, self.y = x, y
                def __add__(self, o):
                    return Vektor(self.x + o.x, self.y + o.y)
                def __eq__(self, o):
                    return (self.x, self.y) == (o.x, o.y)
                def __repr__(self):
                    return f"Vektor({self.x}, {self.y})"

            print(Vektor(1, 2) + Vektor(3, 4))
            print(Vektor(1, 1) == Vektor(1, 1))
            """, "dunder", "operator")
        e("@property", "Methoden wie Attribute benutzen.",
            "", """
            class Kreis:
                def __init__(self, r):
                    self.r = r

                @property
                def flaeche(self):
                    return round(3.14159 * self.r ** 2, 2)

            k = Kreis(2)
            print(k.flaeche)
            """, "getter", "eigenschaft")
        e("Klassen- und statische Methoden", "`@classmethod` und `@staticmethod`",
            "Klassenmethoden bekommen die Klasse (`cls`), statische Methoden gar nichts automatisch.",
            """
            class Temperatur:
                def __init__(self, celsius):
                    self.celsius = celsius

                @classmethod
                def aus_fahrenheit(cls, f):
                    return cls((f - 32) * 5 / 9)

                @staticmethod
                def ist_kalt(c):
                    return c < 10

            t = Temperatur.aus_fahrenheit(212)
            print(t.celsius, Temperatur.ist_kalt(5))
            """, "classmethod", "staticmethod")
        e("Klassenattribute", "Werte, die alle Objekte teilen.",
            "", """
            class Spieler:
                anzahl = 0
                def __init__(self, name):
                    self.name = name
                    Spieler.anzahl += 1

            Spieler("Ada")
            Spieler("Alan")
            print(Spieler.anzahl)
            """, "statisch", "gemeinsam")
        e("Dataclasses", "Klassen für Daten mit `@dataclass`.",
            "`__init__`, `__repr__` und `__eq__` werden automatisch erzeugt.",
            """
            from dataclasses import dataclass, field

            @dataclass
            class Buch:
                titel: str
                seiten: int = 100
                tags: list[str] = field(default_factory=list)

            b = Buch("Python lernen", 250)
            print(b)
            print(b == Buch("Python lernen", 250))
            """, "dataclass", "daten")
        e("Kapselung", "`_intern` und `__privat` als Konvention.",
            "Ein Unterstrich bedeutet „bitte nicht von außen benutzen“. Zwei Unterstriche verändern den Namen (Name Mangling).",
            """
            class Konto:
                def __init__(self):
                    self._stand = 0

                def einzahlen(self, betrag):
                    self._stand += betrag

                def stand(self):
                    return self._stand

            k = Konto()
            k.einzahlen(50)
            print(k.stand())
            """, "privat", "unterstrich")
    },
    category("iter", "Iteratoren & Generatoren", LessonIcon.LOOP) {
        e("Iteratoren", "`iter()` und `next()`",
            "Alles, was man mit `for` durchlaufen kann, ist iterierbar. Ein Iterator liefert mit `next()` das nächste Element.",
            """
            it = iter([1, 2, 3])
            print(next(it), next(it), next(it))
            """, "iterator", "next")
        e("Generatoren mit yield", "Funktionen, die Werte nach und nach liefern.",
            "Generatoren sparen Speicher, weil sie Werte erst bei Bedarf erzeugen.",
            """
            def gerade_zahlen(limit):
                n = 0
                while n <= limit:
                    yield n
                    n += 2

            print(list(gerade_zahlen(10)))
            """, "yield", "generator")
        e("Generator-Ausdrücke", "`(x for x in ...)` – wie Comprehensions, aber faul.",
            "", """
            summe = sum(x * x for x in range(1000))
            print(summe)
            """, "lazy")
        e("itertools", "Werkzeuge für Iterationen.",
            "", """
            import itertools
            print(list(itertools.permutations("abc", 2)))
            print(list(itertools.combinations([1, 2, 3], 2)))
            print(list(itertools.chain([1, 2], [3])))
            print(list(itertools.islice(itertools.count(10, 5), 4)))
            """, "permutation", "kombination")
        e("any() und all()", "Gilt es für irgendein / alle Elemente?",
            "", """
            zahlen = [2, 4, 7]
            print(any(z % 2 == 1 for z in zahlen))
            print(all(z > 0 for z in zahlen))
            """, "any", "all")
        e("map() und filter()", "Funktion auf alle Elemente anwenden / Elemente auswählen.",
            "Oft sind Comprehensions lesbarer – aber gut zu kennen.",
            """
            zahlen = [1, 2, 3, 4]
            print(list(map(str, zahlen)))
            print(list(filter(lambda z: z % 2 == 0, zahlen)))
            """, "map", "filter")
    },
    category("modules", "Module & Pakete", LessonIcon.FILE) {
        e("import", "Module einbinden.",
            "`import modul`, `from modul import name`, `import modul as kurz`.",
            """
            import math
            from random import randint
            import statistics as st

            print(math.pi)
            print(1 <= randint(1, 6) <= 6)
            print(st.mean([1, 2, 3]))
            """, "import", "bibliothek")
        e("if __name__ == \"__main__\"", "Code nur beim direkten Start ausführen.",
            "In Dateien, die auch als Modul importiert werden, schützt dieser Block den Startcode.",
            """
            def main():
                print("Programm läuft!")

            if __name__ == "__main__":
                main()
            """, "main", "start")
        e("pip & Pakete", "Zusätzliche Bibliotheken installieren (auf dem PC).",
            """
            Auf deinem Computer installierst du Pakete aus dem Python Package Index (PyPI) mit:

            `pip install requests`

            Beliebte Pakete: `requests` (Internet), `numpy` (Mathe), `pandas` (Datenanalyse), `matplotlib` (Diagramme), `flask` (Webseiten), `pygame` (Spiele). In dieser App ist die komplette **Standardbibliothek** dabei.
            """, null, "pip", "installieren", "pypi")
        e("Virtuelle Umgebungen", "Pakete pro Projekt getrennt halten.",
            """
            Auf dem PC legst du mit `python -m venv .venv` eine virtuelle Umgebung an und aktivierst sie (Windows: `.venv\Scripts\activate`, Mac/Linux: `source .venv/bin/activate`). Danach installiert `pip` Pakete nur für dieses Projekt.
            """, null, "venv", "umgebung")
        e("dir() und help()", "Herausfinden, was ein Modul kann.",
            "", """
            import math
            print([n for n in dir(math) if not n.startswith("_")][:10])
            """, "hilfe", "dokumentation")
    },
    category("files", "Dateien", LessonIcon.FILE) {
        e("Datei schreiben", "`open(name, \"w\")` mit `with`.",
            "`with` schließt die Datei automatisch. Modus `\"w\"` überschreibt, `\"a\"` hängt an.",
            """
            with open("notiz.txt", "w", encoding="utf-8") as f:
                f.write("Erste Zeile\n")
                f.write("Zweite Zeile\n")
            print("gespeichert")
            """, "schreiben", "write")
        e("Datei lesen", "`read()`, `readlines()` oder Zeile für Zeile.",
            "", """
            with open("demo.txt", "w", encoding="utf-8") as f:
                f.write("Apfel\nBirne\n")

            with open("demo.txt", encoding="utf-8") as f:
                for zeile in f:
                    print(zeile.strip())
            """, "lesen", "read")
        e("Dateimodi", "`r`, `w`, `a`, `x`, `b` …",
            """
            - `"r"` lesen (Standard)
            - `"w"` schreiben (überschreibt!)
            - `"a"` anhängen
            - `"x"` neu erstellen (Fehler, wenn sie existiert)
            - `"b"` binär, z.B. `"rb"` für Bilder
            """, null, "modus")
        e("pathlib", "Moderne Pfade und Dateien.",
            "", """
            from pathlib import Path

            p = Path("hallo.txt")
            p.write_text("Hallo Datei!", encoding="utf-8")
            print(p.exists(), p.read_text(encoding="utf-8"))
            print(p.suffix, p.stem)
            """, "pfad", "path")
        e("CSV-Dateien", "Tabellen mit dem csv-Modul.",
            "", """
            import csv

            with open("noten.csv", "w", newline="", encoding="utf-8") as f:
                w = csv.writer(f)
                w.writerow(["Name", "Note"])
                w.writerow(["Ada", 1])

            with open("noten.csv", encoding="utf-8") as f:
                for zeile in csv.reader(f):
                    print(zeile)
            """, "csv", "tabelle")
        e("JSON-Dateien", "Daten speichern und laden.",
            "", """
            import json

            daten = {"name": "Monty", "level": 3}
            with open("save.json", "w", encoding="utf-8") as f:
                json.dump(daten, f)

            with open("save.json", encoding="utf-8") as f:
                print(json.load(f))
            """, "json", "speichern")
    },
    category("stdlib", "Standardbibliothek", LessonIcon.STAR) {
        e("math", "Mathematische Funktionen.",
            "", """
            import math
            print(math.sqrt(16), math.pi)
            print(math.floor(2.7), math.ceil(2.1))
            print(math.gcd(12, 18), math.factorial(5))
            """, "wurzel", "mathe")
        e("random", "Zufallszahlen und Zufallsauswahl.",
            "", """
            import random
            random.seed(1)
            print(random.randint(1, 6))
            print(random.choice(["rot", "grün", "blau"]))
            karten = [1, 2, 3, 4]
            random.shuffle(karten)
            print(karten, random.random() < 1)
            """, "zufall", "würfel")
        e("datetime", "Datum und Uhrzeit.",
            "", """
            from datetime import date, timedelta
            d = date(2026, 12, 24)
            print(d.strftime("%d.%m.%Y"), d.weekday())
            print(d + timedelta(days=7))
            """, "datum", "zeit", "uhrzeit")
        e("time", "Zeit messen und warten.",
            "", """
            import time
            start = time.perf_counter()
            time.sleep(0.2)
            print(round(time.perf_counter() - start, 1), "Sekunden")
            """, "sleep", "stoppuhr")
        e("collections.Counter", "Dinge zählen.",
            "", """
            from collections import Counter
            c = Counter("mississippi")
            print(c.most_common(2))
            """, "zählen", "häufigkeit")
        e("collections.defaultdict & deque", "Dictionaries mit Standardwert, schnelle Warteschlangen.",
            "", """
            from collections import defaultdict, deque
            gruppen = defaultdict(list)
            for wort in ["apfel", "ananas", "birne"]:
                gruppen[wort[0]].append(wort)
            print(dict(gruppen))
            q = deque([1, 2])
            q.appendleft(0)
            q.append(3)
            print(q.popleft(), q)
            """, "defaultdict", "deque", "queue")
        e("functools", "`lru_cache`, `reduce`, `partial` …",
            "`@lru_cache` merkt sich Ergebnisse – macht rekursive Funktionen blitzschnell.",
            """
            from functools import lru_cache, reduce

            @lru_cache
            def fib(n):
                return n if n < 2 else fib(n - 1) + fib(n - 2)

            print(fib(80))
            print(reduce(lambda a, b: a * b, [1, 2, 3, 4]))
            """, "cache", "reduce")
        e("re – Reguläre Ausdrücke", "Muster in Texten suchen.",
            "", """
            import re
            text = "Tel: 0151-1234, Fax: 030-9876"
            print(re.findall(r"\d+-\d+", text))
            print(re.sub(r"\d", "#", "PIN 1234"))
            """, "regex", "muster")
        e("json", "Daten als Text austauschen.",
            "", """
            import json
            s = json.dumps({"a": [1, 2], "b": True})
            print(s)
            print(json.loads(s)["a"])
            """, "json")
        e("statistics", "Mittelwert, Median, Modus …",
            "", """
            import statistics as st
            werte = [2, 4, 4, 5, 9]
            print(st.mean(werte), st.median(werte), st.mode(werte))
            """, "statistik", "durchschnitt")
        e("string & secrets", "Zeichensätze und sichere Zufallswerte.",
            "", """
            import string, secrets
            print(string.ascii_uppercase[:5], string.digits)
            print(len(secrets.token_hex(8)))
            """, "passwort", "token")
        e("os & sys", "Infos über System und Programm.",
            "", """
            import os, sys
            print(sys.version.split()[0])
            print(os.getcwd() != "")
            print(os.path.join("ordner", "datei.txt"))
            """, "system", "pfad")
    },
    category("builtins", "Eingebaute Funktionen", LessonIcon.CODE) {
        e("len()", "Länge von Strings, Listen, Dicts …", "", """
            print(len("Python"), len([1, 2]), len({"a": 1}))
            """, "länge", "anzahl")
        e("abs(), round(), pow(), divmod()", "Rechnen mit eingebauten Funktionen.", "", """
            print(abs(-7), round(3.14159, 2), pow(2, 5))
            print(divmod(17, 5))
            """, "runden", "betrag")
        e("sorted() & reversed()", "Sortiert / umgekehrt durchlaufen.", "", """
            print(sorted("python"))
            print(list(reversed([1, 2, 3])))
            """, "sortieren")
        e("min() & max() mit key", "Das kleinste/größte nach eigener Regel.", "", """
            woerter = ["Ei", "Banane", "Kiwi"]
            print(max(woerter, key=len), min(woerter, key=len))
            """, "maximum", "minimum")
        e("int() mit Basis", "Zahlen in anderen Zahlensystemen.", "", """
            print(int("ff", 16), int("1010", 2))
            print(bin(10), hex(255), oct(8))
            """, "binär", "hex")
        e("help() & dir()", "Hilfe zu allem in Python.", "", """
            print([m for m in dir(str) if not m.startswith("_")][:8])
            """, "hilfe")
        e("id() & is", "Ist es dasselbe Objekt?", "`==` vergleicht Werte, `is` die Identität.", """
            a = [1]
            b = [1]
            print(a == b, a is b)
            """, "identität")
    },
    category("pro", "Profi-Tipps", LessonIcon.ROCKET) {
        e("Entpacken mit *", "`erster, *rest = liste`", "", """
            erster, *mitte, letzter = [1, 2, 3, 4, 5]
            print(erster, mitte, letzter)
            print(*[1, 2, 3])
            """, "unpacking", "stern")
        e("Werte tauschen", "`a, b = b, a`", "", """
            a, b = 1, 2
            a, b = b, a
            print(a, b)
            """, "tausch", "swap")
        e("Walross-Operator :=", "Zuweisen und gleichzeitig benutzen.", "", """
            zahlen = [3, 8, 1, 9]
            if (n := len(zahlen)) > 3:
                print(f"Lange Liste mit {n} Elementen")
            """, "walrus", "zuweisung")
        e("Dicts zusammenführen", "`a | b` (ab Python 3.9)", "", """
            standard = {"farbe": "blau", "groesse": 12}
            eigene = {"farbe": "grün"}
            print(standard | eigene)
            """, "merge", "kombinieren")
        e("Kontextmanager", "`with` räumt automatisch auf.", "Eigene Kontextmanager baust du mit `contextlib.contextmanager`.", """
            from contextlib import contextmanager

            @contextmanager
            def rahmen():
                print("┌──────")
                yield
                print("└──────")

            with rahmen():
                print("│ Inhalt")
            """, "with", "contextmanager")
        e("PEP 8 – guter Stil", "Die offiziellen Stil-Regeln für Python.", """
            - 4 Leerzeichen Einrückung
            - `snake_case` für Variablen und Funktionen, `PascalCase` für Klassen, `GROSS_BUCHSTABEN` für Konstanten
            - Leerzeichen um Operatoren: `x = a + b`
            - Zeilen nicht länger als ca. 79–99 Zeichen
            - Sprechende Namen statt `x1`, `tmp2`
            """, null, "stil", "pep8")
        e("The Zen of Python", "Die Philosophie hinter Python.", "Tippe im Spielplatz `import this` – Python zeigt dir 19 Leitsätze wie „Beautiful is better than ugly“.", """
            import this
            """, "zen", "philosophie")
    },
)
