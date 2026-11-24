package app.pyquest.content

import app.pyquest.data.LessonIcon

fun CourseBuilder.unit09() = unit("Fehlerbehandlung", "Fehler verstehen, abfangen und selbst auslösen") {
    lesson("Fehlerarten verstehen", "Syntaxfehler, Exceptions und Tracebacks", LessonIcon.BUG) {
        text("""
            Fehler sind beim Programmieren völlig normal! Python unterscheidet zwei Arten:
            - **Syntaxfehler** – der Code ist falsch geschrieben und startet gar nicht erst
            - **Laufzeitfehler (Exceptions)** – der Code ist korrekt geschrieben, aber beim Ausführen geht etwas schief
        """)
        text("Eine Fehlermeldung (**Traceback**) zeigt dir, *wo* und *was* passiert ist. Die wichtigste Information steht ganz unten. Tippe auf ▶:")
        code("""
            zahlen = [1, 2, 3]
            print(zahlen[5])
        """, fails = true)
        text("""
            Die häufigsten Exceptions:
            - `NameError` – unbekannter Name
            - `TypeError` – falscher Typ (z.B. Text + Zahl)
            - `ValueError` – falscher Wert (z.B. `int("abc")`)
            - `IndexError` – Index außerhalb der Liste
            - `KeyError` – Schlüssel fehlt im Dictionary
            - `ZeroDivisionError` – Teilen durch 0
        """)
        tip("Im Bereich **Wissen** findest du ein **Fehler-Lexikon** mit Erklärungen und Lösungen zu allen wichtigen Fehlern.")

        match(
            "int(\"abc\")" to "ValueError",
            "[1, 2][5]" to "IndexError",
            "10 / 0" to "ZeroDivisionError",
            "{}[\"x\"]" to "KeyError",
            "\"a\" + 1" to "TypeError",
        )
        choice("Welcher Fehler entsteht bei `print(undefiniert)`?", "NameError", "TypeError", "ValueError", "SyntaxError", mono = true)
        truth("Ein SyntaxError tritt erst auf, wenn die fehlerhafte Zeile ausgeführt wird.", false,
            explain = "Syntaxfehler findet Python schon **vor** dem Start – das Programm läuft dann gar nicht erst los.")
        choice("Wo steht in einem Traceback die wichtigste Information?", "In der letzten Zeile", "In der ersten Zeile", "In der Mitte", "Nirgends – man muss raten")
        choice("Welcher Fehler entsteht hier?", "TypeError", "ValueError", "NameError", "KeyError", mono = true,
            code = """
                alter = 14
                print("Alter: " + alter)
            """)
        choice("Welcher Fehler entsteht hier?", "ValueError", "TypeError", "IndexError", "Kein Fehler", mono = true,
            code = """zahl = int("drei")""")
        truth("`[1, 2, 3][3]` führt zu einem IndexError.", true, explain = "Die gültigen Indizes sind 0, 1 und 2.")
        write("Dieses Programm stürzt ab. Repariere es, sodass `Alter: 14` ausgegeben wird.",
            starter = """
                alter = 14
                print("Alter: " + alter)
            """,
            solution = """
                alter = 14
                print("Alter: " + str(alter))
            """,
            output = "Alter: 14",
            hint = "Wandle die Zahl mit `str()` in Text um – oder nutze einen f-String.")
    }

    lesson("try & except", "Fehler abfangen statt abstürzen", LessonIcon.BUG) {
        text("Mit `try` und `except` fängst du Fehler ab, statt dass dein Programm abstürzt. Python versucht den `try`-Block – geht etwas schief, springt es in den `except`-Block:")
        code("""
            eingabe = input("Zahl: ")
            try:
                zahl = int(eingabe)
                print("Das Doppelte:", zahl * 2)
            except ValueError:
                print("Das war keine Zahl!")
        """, output = """
            Zahl: abc
            Das war keine Zahl!
        """, stdin = listOf("abc"))
        text("Du kannst mehrere `except`-Blöcke für verschiedene Fehler haben. Mit `as` bekommst du die Fehlermeldung:")
        code("""
            for teiler in [2, 0, "x"]:
                try:
                    print(10 / teiler)
                except ZeroDivisionError:
                    print("Durch 0 geht nicht!")
                except TypeError as fehler:
                    print("Typfehler:", fehler)
        """, output = """
            5.0
            Durch 0 geht nicht!
            Typfehler: unsupported operand type(s) for /: 'int' and 'str'
        """)
        warn("Fange möglichst **konkrete** Fehler ab. Ein nacktes `except:` fängt alles – auch Fehler, die du eigentlich sehen solltest.")
        tip("Ein typisches Muster: Eingaben in einer Schleife abfragen, bis sie gültig sind – mit `while True`, `try` und `break`.")

        output("""
            try:
                print(1 / 0)
            except ZeroDivisionError:
                print("Fehler!")
        """, "Fehler!", "0", "Infinity", "ZeroDivisionError")
        output("""
            try:
                x = int("5")
                print(x)
            except ValueError:
                print("kaputt")
        """, "5", "kaputt", "5\nkaputt", "Nichts")
        typeOutput("""
            try:
                liste = [1, 2]
                print(liste[9])
            except IndexError:
                print("zu weit")
        """, "zu weit")
        choice("Was passiert, wenn im `try`-Block **kein** Fehler auftritt?",
            "Der `except`-Block wird übersprungen.", "Der `except`-Block wird trotzdem ausgeführt.", "Das Programm stoppt.", "Python meldet einen Fehler.")
        truth("Mit `except ValueError as e:` kann man auf die Fehlermeldung zugreifen.", true)
        fill("Fange den Fehler ab, wenn ein Schlüssel fehlt.", """
            lager = {"Äpfel": 5}
            try:
                print(lager["Birnen"])
            ?? KeyError:
                print("Nicht vorrätig")
        """, answers = listOf("except"), distractors = listOf("catch", "else", "error"), output = "Nicht vorrätig")
        output("""
            def sicher_teilen(a, b):
                try:
                    return a / b
                except ZeroDivisionError:
                    return None

            print(sicher_teilen(8, 2), sicher_teilen(1, 0))
        """, "4.0 None", "4 0", "4.0 0", "Fehler")
        write("Schreibe eine Funktion `zu_zahl(text)`, die den Text in eine Ganzzahl umwandelt und bei einem Fehler `0` zurückgibt. Gib `zu_zahl(\"42\")` und `zu_zahl(\"Hallo\")` aus.",
            solution = """
                def zu_zahl(text):
                    try:
                        return int(text)
                    except ValueError:
                        return 0

                print(zu_zahl("42"))
                print(zu_zahl("Hallo"))
            """,
            output = """
                42
                0
            """,
            mustContain = listOf("try", "except"),
            hint = "`try: return int(text)` und `except ValueError: return 0`.")
    }

    lesson("else, finally & raise", "Aufräumen und eigene Fehler werfen", LessonIcon.BUG) {
        text("""
            `try` kann noch zwei weitere Blöcke haben:
            - `else` – läuft, wenn **kein** Fehler aufgetreten ist
            - `finally` – läuft **immer**, egal was passiert (z.B. zum Aufräumen)
        """)
        code("""
            def teile(a, b):
                try:
                    ergebnis = a / b
                except ZeroDivisionError:
                    print("Fehler: Division durch 0")
                else:
                    print("Ergebnis:", ergebnis)
                finally:
                    print("Berechnung beendet")

            teile(10, 2)
            teile(1, 0)
        """, output = """
            Ergebnis: 5.0
            Berechnung beendet
            Fehler: Division durch 0
            Berechnung beendet
        """)
        text("Mit `raise` löst du **selbst** einen Fehler aus – zum Beispiel, wenn eine Funktion ungültige Werte bekommt:")
        code("""
            def setze_alter(alter):
                if alter < 0:
                    raise ValueError("Das Alter kann nicht negativ sein")
                return alter

            try:
                setze_alter(-5)
            except ValueError as e:
                print("Fehler:", e)
        """, output = "Fehler: Das Alter kann nicht negativ sein")
        tip("Mit `raise` ohne Argument kannst du in einem `except`-Block den gefangenen Fehler weiterwerfen.")

        output("""
            try:
                x = 1
            except ValueError:
                print("A")
            else:
                print("B")
        """, "B", "A", "A\nB", "Nichts")
        output("""
            try:
                1 / 0
            except ZeroDivisionError:
                print("X")
            finally:
                print("Y")
        """, "X\nY", "Y", "X", "Y\nX")
        truth("Ein `finally`-Block wird nur ausgeführt, wenn ein Fehler aufgetreten ist.", false, explain = "`finally` läuft **immer** – mit und ohne Fehler.")
        choice("Womit löst man in Python selbst einen Fehler aus?", "raise", "throw", "error", "except", mono = true)
        typeOutput("""
            def check(n):
                if n > 10:
                    raise ValueError("zu groß")
                return n

            try:
                check(99)
            except ValueError as e:
                print(e)
        """, "zu groß")
        fill("Wirf einen Fehler, wenn der Name leer ist.", """
            def anmelden(name):
                if not name:
                    ?? ValueError("Name fehlt")
                print("Hallo", name)

            anmelden("Ada")
            try:
                anmelden("")
            except ValueError as fehler:
                print("Fehler:", fehler)
        """, answers = listOf("raise"), distractors = listOf("return", "throw", "print"), output = """
            Hallo Ada
            Fehler: Name fehlt
        """, explain = "Mit `raise` wirfst du einen Fehler. `throw` gibt es in Python nicht.")
        arrange("Versuche die Division, fange den Fehler ab und gib am Ende **immer** `fertig` aus.", """
            try:
                print(10 / 0)
            except ZeroDivisionError:
                print("Durch 0!")
            finally:
                print("fertig")
        """, output = """
            Durch 0!
            fertig
        """)
        write("Schreibe eine Funktion `wurzel(x)`, die bei negativem `x` einen `ValueError` mit der Meldung `negativ` wirft und sonst `x ** 0.5` zurückgibt. Rufe sie für `16` und `-1` auf und fange den Fehler ab, sodass `4.0` und `Fehler: negativ` ausgegeben werden.",
            solution = """
                def wurzel(x):
                    if x < 0:
                        raise ValueError("negativ")
                    return x ** 0.5

                for zahl in [16, -1]:
                    try:
                        print(wurzel(zahl))
                    except ValueError as e:
                        print("Fehler:", e)
            """,
            output = """
                4.0
                Fehler: negativ
            """,
            mustContain = listOf("raise", "except"),
            hint = "In der Funktion `raise ValueError(\"negativ\")`, beim Aufruf `try` und `except ValueError as e`.")
    }

    lesson("Eigene Exceptions & assert", "Eigene Fehlertypen und Tests", LessonIcon.BRAIN) {
        text("Für deine Programme kannst du **eigene Fehlertypen** definieren. Dafür erstellst du eine Klasse, die von `Exception` erbt – Klassen lernst du später noch genauer kennen:")
        code("""
            class ZuWenigGeld(Exception):
                pass

            def bezahlen(kontostand, preis):
                if preis > kontostand:
                    raise ZuWenigGeld(f"Es fehlen {preis - kontostand} €")
                return kontostand - preis

            try:
                bezahlen(20, 35)
            except ZuWenigGeld as e:
                print("Kauf abgelehnt:", e)
        """, output = "Kauf abgelehnt: Es fehlen 15 €")
        text("Mit `assert` prüfst du Annahmen in deinem Code. Ist die Bedingung falsch, gibt es einen `AssertionError`. Das ist perfekt für **Tests**:")
        code("""
            def verdopple(x):
                return x * 2

            assert verdopple(2) == 4
            assert verdopple(-3) == -6
            assert verdopple(0) == 0, "0 sollte 0 bleiben"
            print("Alle Tests bestanden! ✅")
        """, output = "Alle Tests bestanden! ✅")
        tip("Viele Programmieraufgaben in dieser App werden im Hintergrund mit `assert`-Tests geprüft – genau wie bei Profis.")
        warn("`assert` ist für Tests und Entwicklung gedacht, nicht zum Prüfen von Benutzereingaben.")

        output("""
            assert 1 + 1 == 2
            print("ok")
        """, "ok", "AssertionError", "True", "Nichts")
        choice("Was passiert bei `assert 2 > 3`?", "Ein AssertionError wird ausgelöst.", "Es wird `False` ausgegeben.", "Nichts.", "Ein SyntaxError.")
        truth("Eigene Exceptions erben von der Klasse `Exception`.", true)
        fill("Definiere einen eigenen Fehlertyp.", """
            class LeererWarenkorb(??):
                pass

            try:
                raise LeererWarenkorb("Nichts drin")
            except LeererWarenkorb as e:
                print(e)
        """, answers = listOf("Exception"), distractors = listOf("Error", "object", "raise"), output = "Nichts drin")
        output("""
            class MeinFehler(Exception):
                pass

            try:
                raise MeinFehler("Ups")
            except Exception as e:
                print(type(e).__name__, e)
        """, "MeinFehler Ups", "Exception Ups", "Ups", "MeinFehler")
        typeOutput("""
            def ist_gerade(n):
                return n % 2 == 0

            assert ist_gerade(4)
            assert not ist_gerade(7)
            print("passt")
        """, "passt")
        choice("Wozu nutzt man `assert` hauptsächlich?", "Um zu testen, ob der Code das Richtige tut", "Um Benutzereingaben zu prüfen", "Um Schleifen zu beenden", "Um Variablen zu erstellen")
        write("Schreibe eine Funktion `maximum(a, b)`, die den größeren Wert zurückgibt. Teste sie mit **drei** `assert`-Anweisungen und gib am Ende `Tests ok` aus.",
            solution = """
                def maximum(a, b):
                    if a > b:
                        return a
                    return b

                assert maximum(1, 2) == 2
                assert maximum(5, 3) == 5
                assert maximum(4, 4) == 4
                print("Tests ok")
            """,
            output = "Tests ok",
            test = """
                assert maximum(10, -10) == 10
                assert maximum(-1, 0) == 0
            """,
            mustContain = listOf("assert"),
            hint = "Zum Beispiel `assert maximum(1, 2) == 2`.")
    }

    checkpoint()
}
