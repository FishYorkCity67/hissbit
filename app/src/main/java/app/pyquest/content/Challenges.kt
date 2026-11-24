package app.pyquest.content

import app.pyquest.data.CodeExercise
import app.pyquest.data.LessonIcon
import app.pyquest.data.LessonKind

/** Tägliche Programmier-Challenges (eine pro Tag, rotierend). */
fun buildChallenges(): List<CodeExercise> {
    val b = LessonBuilder("challenge")

    fun ch(title: String, description: String, signature: String, solution: String, tests: String, hint: String) {
        b.write(
            prompt = "**$title:** $description",
            starter = signature.trimIndent() + "\n    # Dein Code hier\n    pass",
            solution = solution,
            test = tests.trimIndent() + "\nprint(\"✅ Alle Tests bestanden!\")",
            hint = hint,
        )
    }

    ch("Palindrom-Check", "Schreibe `ist_palindrom(text)`, die `True` zurückgibt, wenn ein Wort vorwärts und rückwärts gleich ist (Groß-/Kleinschreibung egal).",
        "def ist_palindrom(text):",
        """
            def ist_palindrom(text):
                t = text.lower()
                return t == t[::-1]
        """,
        """
            assert ist_palindrom("Anna") == True
            assert ist_palindrom("Otto") == True
            assert ist_palindrom("Python") == False
            assert ist_palindrom("") == True
        """,
        "Mit `text[::-1]` drehst du einen String um, mit `.lower()` machst du ihn klein.")
    ch("FizzBuzz", "Schreibe `fizzbuzz(n)`: Gib `\"FizzBuzz\"` zurück, wenn n durch 3 und 5 teilbar ist, `\"Fizz\"` bei 3, `\"Buzz\"` bei 5 – sonst die Zahl als String.",
        "def fizzbuzz(n):",
        """
            def fizzbuzz(n):
                if n % 15 == 0:
                    return "FizzBuzz"
                if n % 3 == 0:
                    return "Fizz"
                if n % 5 == 0:
                    return "Buzz"
                return str(n)
        """,
        """
            assert fizzbuzz(3) == "Fizz"
            assert fizzbuzz(10) == "Buzz"
            assert fizzbuzz(30) == "FizzBuzz"
            assert fizzbuzz(7) == "7"
        """,
        "Prüfe den Fall „durch 15 teilbar“ zuerst!")
    ch("Vokale zählen", "Schreibe `vokale(text)`, die zählt, wie viele Vokale (a, e, i, o, u, ä, ö, ü) ein Text enthält – Groß-/Kleinschreibung egal.",
        "def vokale(text):",
        """
            def vokale(text):
                return sum(1 for b in text.lower() if b in "aeiouäöü")
        """,
        """
            assert vokale("Hallo") == 2
            assert vokale("PYTHON") == 1
            assert vokale("Öl über Äpfel") == 5
            assert vokale("xyz") == 0
        """,
        "Laufe durch `text.lower()` und zähle jeden Buchstaben, der in `\"aeiouäöü\"` vorkommt.")
    ch("Größte Zahl", "Schreibe `groesste(zahlen)`, die die größte Zahl einer Liste zurückgibt – **ohne** `max()` zu benutzen.",
        "def groesste(zahlen):",
        """
            def groesste(zahlen):
                beste = zahlen[0]
                for z in zahlen:
                    if z > beste:
                        beste = z
                return beste
        """,
        """
            assert groesste([3, 9, 2]) == 9
            assert groesste([-5, -2, -9]) == -2
            assert groesste([7]) == 7
        """,
        "Merke dir die bisher größte Zahl und vergleiche jede weitere damit.")
    ch("Wörter umdrehen", "Schreibe `umgedreht(satz)`, die die Reihenfolge der Wörter umkehrt: `\"Ich mag Python\"` → `\"Python mag Ich\"`.",
        "def umgedreht(satz):",
        """
            def umgedreht(satz):
                return " ".join(satz.split()[::-1])
        """,
        """
            assert umgedreht("Ich mag Python") == "Python mag Ich"
            assert umgedreht("Hallo") == "Hallo"
            assert umgedreht("a b c d") == "d c b a"
        """,
        "`split()`, dann die Liste mit `[::-1]` umdrehen und mit `\" \".join()` zusammensetzen.")
    ch("Primzahl-Test", "Schreibe `ist_prim(n)`, die `True` zurückgibt, wenn n eine Primzahl ist.",
        "def ist_prim(n):",
        """
            def ist_prim(n):
                if n < 2:
                    return False
                for t in range(2, int(n ** 0.5) + 1):
                    if n % t == 0:
                        return False
                return True
        """,
        """
            assert ist_prim(2) and ist_prim(13) and ist_prim(97)
            assert not ist_prim(1)
            assert not ist_prim(0)
            assert not ist_prim(15)
            assert not ist_prim(100)
        """,
        "Zahlen unter 2 sind keine Primzahlen. Prüfe Teiler von 2 bis zur Wurzel aus n.")
    ch("Fakultät", "Schreibe `fakultaet(n)`, die n! = 1 · 2 · … · n berechnet (0! = 1).",
        "def fakultaet(n):",
        """
            def fakultaet(n):
                ergebnis = 1
                for i in range(2, n + 1):
                    ergebnis *= i
                return ergebnis
        """,
        """
            assert fakultaet(0) == 1
            assert fakultaet(1) == 1
            assert fakultaet(5) == 120
            assert fakultaet(10) == 3628800
        """,
        "Starte mit `ergebnis = 1` und multipliziere in einer Schleife.")
    ch("Anagramm", "Schreibe `ist_anagramm(a, b)`: Zwei Wörter sind Anagramme, wenn sie aus denselben Buchstaben bestehen (Groß-/Kleinschreibung und Leerzeichen egal).",
        "def ist_anagramm(a, b):",
        """
            def ist_anagramm(a, b):
                def norm(s):
                    return sorted(s.lower().replace(" ", ""))
                return norm(a) == norm(b)
        """,
        """
            assert ist_anagramm("Lager", "Regal")
            assert ist_anagramm("Tom Marvolo Riddle", "I am Lord Voldemort")
            assert not ist_anagramm("Python", "Java")
        """,
        "Vergleiche die sortierten Buchstaben: `sorted(text.lower().replace(\" \", \"\"))`.")
    ch("Quersumme", "Schreibe `quersumme(n)`, die die Summe aller Ziffern einer positiven Zahl berechnet.",
        "def quersumme(n):",
        """
            def quersumme(n):
                return sum(int(z) for z in str(n))
        """,
        """
            assert quersumme(123) == 6
            assert quersumme(9999) == 36
            assert quersumme(7) == 7
        """,
        "Wandle die Zahl in einen String um und addiere `int()` jeder Ziffer.")
    ch("Duplikate entfernen", "Schreibe `ohne_duplikate(liste)`, die eine neue Liste ohne doppelte Werte zurückgibt – die **Reihenfolge** soll erhalten bleiben.",
        "def ohne_duplikate(liste):",
        """
            def ohne_duplikate(liste):
                ergebnis = []
                for x in liste:
                    if x not in ergebnis:
                        ergebnis.append(x)
                return ergebnis
        """,
        """
            assert ohne_duplikate([3, 1, 3, 2, 1]) == [3, 1, 2]
            assert ohne_duplikate(["a", "a"]) == ["a"]
            assert ohne_duplikate([]) == []
        """,
        "Hänge nur Werte an, die noch nicht im Ergebnis sind – oder nutze `list(dict.fromkeys(liste))`.")
    ch("Temperatur-Umrechner", "Schreibe `fahrenheit(celsius)`, die Celsius in Fahrenheit umrechnet (F = C · 9/5 + 32), gerundet auf eine Nachkommastelle.",
        "def fahrenheit(celsius):",
        """
            def fahrenheit(celsius):
                return round(celsius * 9 / 5 + 32, 1)
        """,
        """
            assert fahrenheit(0) == 32.0
            assert fahrenheit(100) == 212.0
            assert fahrenheit(-40) == -40.0
            assert fahrenheit(36.6) == 97.9
        """,
        "`round(celsius * 9 / 5 + 32, 1)`")
    ch("Buchstaben zählen", "Schreibe `zaehle(text)`, die ein Dictionary zurückgibt, das jedem Buchstaben (klein geschrieben) seine Anzahl zuordnet. Leerzeichen und Satzzeichen ignorieren.",
        "def zaehle(text):",
        """
            def zaehle(text):
                ergebnis = {}
                for b in text.lower():
                    if b.isalpha():
                        ergebnis[b] = ergebnis.get(b, 0) + 1
                return ergebnis
        """,
        """
            assert zaehle("Aha!") == {"a": 2, "h": 1}
            assert zaehle("") == {}
            assert zaehle("Ab ba") == {"a": 2, "b": 2}
        """,
        "Nur Zeichen mit `b.isalpha()` zählen – mit `ergebnis.get(b, 0) + 1`.")
    ch("Zweitgrößte Zahl", "Schreibe `zweitgroesste(zahlen)`, die die zweitgrößte **verschiedene** Zahl zurückgibt.",
        "def zweitgroesste(zahlen):",
        """
            def zweitgroesste(zahlen):
                return sorted(set(zahlen))[-2]
        """,
        """
            assert zweitgroesste([4, 9, 1, 9]) == 4
            assert zweitgroesste([1, 2]) == 1
            assert zweitgroesste([5, 5, 3, 7, 7]) == 5
        """,
        "Entferne Duplikate mit `set()`, sortiere und nimm das vorletzte Element.")
    ch("Caesar-Verschlüsselung", "Schreibe `caesar(text, k)`, die jeden **Kleinbuchstaben** a–z um k Stellen im Alphabet verschiebt (nach z geht es bei a weiter). Andere Zeichen bleiben gleich.",
        "def caesar(text, k):",
        """
            def caesar(text, k):
                ergebnis = ""
                for b in text:
                    if "a" <= b <= "z":
                        ergebnis += chr((ord(b) - ord("a") + k) % 26 + ord("a"))
                    else:
                        ergebnis += b
                return ergebnis
        """,
        """
            assert caesar("abc", 1) == "bcd"
            assert caesar("xyz", 3) == "abc"
            assert caesar("hallo welt!", 13) == "unyyb jryg!"
            assert caesar(caesar("python", 5), 21) == "python"
        """,
        "Mit `ord()` bekommst du die Zahl eines Buchstabens, mit `chr()` wieder den Buchstaben. `% 26` sorgt fürs Herumlaufen.")
    ch("Schaltjahr", "Schreibe `ist_schaltjahr(jahr)`: Ein Jahr ist ein Schaltjahr, wenn es durch 4 teilbar ist – außer es ist durch 100 teilbar, aber nicht durch 400.",
        "def ist_schaltjahr(jahr):",
        """
            def ist_schaltjahr(jahr):
                return jahr % 4 == 0 and (jahr % 100 != 0 or jahr % 400 == 0)
        """,
        """
            assert ist_schaltjahr(2024)
            assert ist_schaltjahr(2000)
            assert not ist_schaltjahr(1900)
            assert not ist_schaltjahr(2026)
        """,
        "`jahr % 4 == 0 and (jahr % 100 != 0 or jahr % 400 == 0)`")
    ch("Fairer Durchschnitt", "Schreibe `fairer_schnitt(werte)`: Entferne den kleinsten und den größten Wert und gib den Durchschnitt der restlichen Werte zurück.",
        "def fairer_schnitt(werte):",
        """
            def fairer_schnitt(werte):
                rest = sorted(werte)[1:-1]
                return sum(rest) / len(rest)
        """,
        """
            assert fairer_schnitt([1, 5, 5, 9]) == 5.0
            assert fairer_schnitt([10, 2, 4, 6, 8]) == 6.0
        """,
        "Sortiere und schneide mit `[1:-1]` das erste und letzte Element ab.")
    ch("Römische Zahlen", "Schreibe `roemisch(n)`, die eine Zahl von 1 bis 3999 in römische Zahlen umwandelt (z.B. 1994 → `MCMXCIV`).",
        "def roemisch(n):",
        """
            def roemisch(n):
                werte = [(1000, "M"), (900, "CM"), (500, "D"), (400, "CD"), (100, "C"), (90, "XC"),
                         (50, "L"), (40, "XL"), (10, "X"), (9, "IX"), (5, "V"), (4, "IV"), (1, "I")]
                ergebnis = ""
                for wert, zeichen in werte:
                    while n >= wert:
                        ergebnis += zeichen
                        n -= wert
                return ergebnis
        """,
        """
            assert roemisch(3) == "III"
            assert roemisch(9) == "IX"
            assert roemisch(58) == "LVIII"
            assert roemisch(1994) == "MCMXCIV"
            assert roemisch(2026) == "MMXXVI"
        """,
        "Lege eine Liste von Wert-Zeichen-Paaren an (auch 900 → CM, 4 → IV …) und ziehe gierig ab.")
    ch("Fibonacci-Liste", "Schreibe `fib_liste(n)`, die die ersten n Fibonacci-Zahlen als Liste zurückgibt (beginnend mit 0, 1).",
        "def fib_liste(n):",
        """
            def fib_liste(n):
                ergebnis = []
                a, b = 0, 1
                for _ in range(n):
                    ergebnis.append(a)
                    a, b = b, a + b
                return ergebnis
        """,
        """
            assert fib_liste(0) == []
            assert fib_liste(1) == [0]
            assert fib_liste(8) == [0, 1, 1, 2, 3, 5, 8, 13]
        """,
        "Mit `a, b = b, a + b` berechnest du elegant die nächste Zahl.")
    ch("Passwort-Prüfer", "Schreibe `ist_sicher(passwort)`: Ein Passwort ist sicher, wenn es mindestens 8 Zeichen hat und mindestens eine Ziffer, einen Groß- und einen Kleinbuchstaben enthält.",
        "def ist_sicher(passwort):",
        """
            def ist_sicher(passwort):
                return (len(passwort) >= 8
                        and any(z.isdigit() for z in passwort)
                        and any(z.isupper() for z in passwort)
                        and any(z.islower() for z in passwort))
        """,
        """
            assert ist_sicher("Python123")
            assert not ist_sicher("python123")
            assert not ist_sicher("PYTHON123")
            assert not ist_sicher("Py1")
            assert not ist_sicher("Pythonista")
        """,
        "Kombiniere `len()` mit `any(z.isdigit() for z in passwort)` usw.")
    ch("Nach Länge gruppieren", "Schreibe `gruppiere(woerter)`, die ein Dictionary zurückgibt: Wortlänge → Liste der Wörter mit dieser Länge (in der ursprünglichen Reihenfolge).",
        "def gruppiere(woerter):",
        """
            def gruppiere(woerter):
                gruppen = {}
                for w in woerter:
                    gruppen.setdefault(len(w), []).append(w)
                return gruppen
        """,
        """
            assert gruppiere(["ab", "cde", "fg"]) == {2: ["ab", "fg"], 3: ["cde"]}
            assert gruppiere([]) == {}
        """,
        "`gruppen.setdefault(len(w), []).append(w)` legt fehlende Listen automatisch an.")
    ch("Matrix transponieren", "Schreibe `transponiere(matrix)`, die Zeilen und Spalten einer Matrix (Liste von Listen) vertauscht.",
        "def transponiere(matrix):",
        """
            def transponiere(matrix):
                return [list(zeile) for zeile in zip(*matrix)]
        """,
        """
            assert transponiere([[1, 2, 3], [4, 5, 6]]) == [[1, 4], [2, 5], [3, 6]]
            assert transponiere([[1]]) == [[1]]
        """,
        "Mit `zip(*matrix)` bekommst du die Spalten – wandle sie in Listen um.")
    ch("Binärzahl", "Schreibe `binaer(n)`, die eine positive Zahl in einen Binär-String umwandelt – **ohne** `bin()` (Tipp: immer durch 2 teilen und Reste sammeln).",
        "def binaer(n):",
        """
            def binaer(n):
                if n == 0:
                    return "0"
                ziffern = ""
                while n > 0:
                    ziffern = str(n % 2) + ziffern
                    n //= 2
                return ziffern
        """,
        """
            assert binaer(0) == "0"
            assert binaer(5) == "101"
            assert binaer(10) == "1010"
            assert binaer(255) == "11111111"
        """,
        "Der Rest `n % 2` ist die nächste Ziffer (von rechts), danach `n //= 2`.")
    ch("Häufigstes Element", "Schreibe `haeufigstes(liste)`, die das Element zurückgibt, das am häufigsten vorkommt (bei Gleichstand das, das zuerst auftaucht).",
        "def haeufigstes(liste):",
        """
            def haeufigstes(liste):
                from collections import Counter
                return Counter(liste).most_common(1)[0][0]
        """,
        """
            assert haeufigstes([1, 3, 3, 2]) == 3
            assert haeufigstes(["a", "b", "a"]) == "a"
            assert haeufigstes([5, 6]) == 5
        """,
        "`Counter(liste).most_common(1)` liefert das häufigste Element mit seiner Anzahl.")
    ch("Teiler finden", "Schreibe `teiler(n)`, die eine sortierte Liste aller Teiler von n zurückgibt.",
        "def teiler(n):",
        """
            def teiler(n):
                return [t for t in range(1, n + 1) if n % t == 0]
        """,
        """
            assert teiler(12) == [1, 2, 3, 4, 6, 12]
            assert teiler(7) == [1, 7]
            assert teiler(1) == [1]
        """,
        "Eine List Comprehension mit `if n % t == 0`.")
    ch("Ziffernwurzel", "Schreibe `ziffernwurzel(n)`: Bilde so lange die Quersumme, bis nur noch eine Ziffer übrig ist (z.B. 9875 → 29 → 11 → 2).",
        "def ziffernwurzel(n):",
        """
            def ziffernwurzel(n):
                while n >= 10:
                    n = sum(int(z) for z in str(n))
                return n
        """,
        """
            assert ziffernwurzel(9875) == 2
            assert ziffernwurzel(5) == 5
            assert ziffernwurzel(999999) == 9
        """,
        "Eine `while`-Schleife, die läuft, solange `n >= 10` ist.")
    ch("Klammer-Check", "Schreibe `klammern_ok(text)`, die prüft, ob alle runden Klammern `(` und `)` korrekt geöffnet und geschlossen sind.",
        "def klammern_ok(text):",
        """
            def klammern_ok(text):
                offen = 0
                for z in text:
                    if z == "(":
                        offen += 1
                    elif z == ")":
                        offen -= 1
                        if offen < 0:
                            return False
                return offen == 0
        """,
        """
            assert klammern_ok("(a(b)c)")
            assert klammern_ok("")
            assert not klammern_ok("(()")
            assert not klammern_ok(")(")
        """,
        "Zähle offene Klammern. Wird der Zähler negativ, ist es falsch – am Ende muss er 0 sein.")
    ch("Lauflängen-Kodierung", "Schreibe `rle(text)`, die gleiche aufeinanderfolgende Zeichen zusammenfasst: `\"aaabcc\"` → `\"a3b1c2\"`.",
        "def rle(text):",
        """
            def rle(text):
                if not text:
                    return ""
                ergebnis = ""
                aktuell = text[0]
                anzahl = 0
                for z in text:
                    if z == aktuell:
                        anzahl += 1
                    else:
                        ergebnis += aktuell + str(anzahl)
                        aktuell = z
                        anzahl = 1
                return ergebnis + aktuell + str(anzahl)
        """,
        """
            assert rle("aaabcc") == "a3b1c2"
            assert rle("x") == "x1"
            assert rle("") == ""
            assert rle("aabbaa") == "a2b2a2"
        """,
        "Merke dir das aktuelle Zeichen und seine Anzahl. Wechselt das Zeichen, schreibe beides ins Ergebnis.")
    ch("Größter gemeinsamer Teiler", "Schreibe `ggt(a, b)` mit dem **euklidischen Algorithmus** – ohne `math.gcd`.",
        "def ggt(a, b):",
        """
            def ggt(a, b):
                while b:
                    a, b = b, a % b
                return a
        """,
        """
            assert ggt(12, 18) == 6
            assert ggt(17, 5) == 1
            assert ggt(100, 75) == 25
        """,
        "Solange `b` nicht 0 ist: `a, b = b, a % b`. Dann ist `a` der ggT.")
    ch("Zahlen in Worten", "Schreibe `in_worten(n)`, die die Zahlen 0 bis 12 als deutsches Wort zurückgibt (z.B. 7 → `\"sieben\"`).",
        "def in_worten(n):",
        """
            def in_worten(n):
                woerter = ["null", "eins", "zwei", "drei", "vier", "fünf", "sechs",
                           "sieben", "acht", "neun", "zehn", "elf", "zwölf"]
                return woerter[n]
        """,
        """
            assert in_worten(0) == "null"
            assert in_worten(7) == "sieben"
            assert in_worten(12) == "zwölf"
        """,
        "Eine Liste mit allen Wörtern – der Index ist die Zahl.")
    ch("Längstes Wort", "Schreibe `laengstes(satz)`, die das längste Wort eines Satzes zurückgibt (bei Gleichstand das erste).",
        "def laengstes(satz):",
        """
            def laengstes(satz):
                return max(satz.split(), key=len)
        """,
        """
            assert laengstes("Ich lerne Programmieren") == "Programmieren"
            assert laengstes("ab cd ef") == "ab"
        """,
        "`max(satz.split(), key=len)`")
    ch("Notenschnitt pro Fach", "Schreibe `schnitt_pro_fach(noten)`, die für ein Dictionary Fach → Liste von Noten ein neues Dictionary Fach → Durchschnitt (gerundet auf 2 Stellen) zurückgibt.",
        "def schnitt_pro_fach(noten):",
        """
            def schnitt_pro_fach(noten):
                return {fach: round(sum(n) / len(n), 2) for fach, n in noten.items()}
        """,
        """
            ergebnis = schnitt_pro_fach({"Mathe": [1, 2, 2], "Kunst": [3, 1]})
            assert ergebnis == {"Mathe": 1.67, "Kunst": 2.0}
        """,
        "Eine Dict-Comprehension über `noten.items()` mit `round(sum(n) / len(n), 2)`.")
    ch("Wörter zählen", "Schreibe `wortanzahl(text)`, die zählt, wie viele Wörter ein Text hat (Wörter sind durch Leerzeichen getrennt, mehrfache Leerzeichen zählen nicht doppelt).",
        "def wortanzahl(text):",
        """
            def wortanzahl(text):
                return len(text.split())
        """,
        """
            assert wortanzahl("Python ist toll") == 3
            assert wortanzahl("  viele   Leerzeichen  ") == 2
            assert wortanzahl("") == 0
        """,
        "`split()` ohne Argument kümmert sich um mehrfache Leerzeichen.")

    return b.build("challenge", "challenge", "Challenges", "", LessonKind.LESSON, LessonIcon.ROCKET)
        .exercises.filterIsInstance<CodeExercise>()
}
