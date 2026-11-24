package app.pyquest.content

import app.pyquest.data.LessonIcon

fun CourseBuilder.unit13() = unit("Dateien", "Daten speichern, lesen und Tabellen verarbeiten") {
    lesson("Schreiben & Lesen", "open, with und die Dateimodi", LessonIcon.FILE) {
        text("Programme können Daten in **Dateien** speichern, damit sie auch nach dem Beenden erhalten bleiben. Mit `open()` öffnest du eine Datei – am besten zusammen mit `with`, dann wird sie automatisch wieder geschlossen:")
        code("""
            with open("gruss.txt", "w", encoding="utf-8") as datei:
                datei.write("Hallo Datei!\n")
                datei.write("Zweite Zeile\n")

            with open("gruss.txt", encoding="utf-8") as datei:
                inhalt = datei.read()
            print(inhalt)
        """, output = """
            Hallo Datei!
            Zweite Zeile
        """)
        text("""
            Der zweite Parameter ist der **Modus**:
            - `"r"` lesen (Standard)
            - `"w"` schreiben – **überschreibt** die Datei!
            - `"a"` anhängen (append)
            - `"x"` neu erstellen – Fehler, wenn sie schon existiert
        """)
        warn("`write()` fügt **keinen** Zeilenumbruch hinzu – das `\\n` musst du selbst schreiben.")
        tip("Gib immer `encoding=\"utf-8\"` an – dann klappen auch Umlaute und Emojis zuverlässig.")
        text("Die Dateien landen im Speicher der App – im Code-Spielplatz kannst du sie mit eigenen Programmen weiterverwenden.")

        choice("Welcher Modus **überschreibt** eine Datei?", "\"w\"", "\"r\"", "\"a\"", "\"x\"", mono = true)
        truth("Mit `with open(...)` wird die Datei am Ende des Blocks automatisch geschlossen.", true)
        output("""
            with open("t.txt", "w", encoding="utf-8") as f:
                f.write("abc")
            with open("t.txt", encoding="utf-8") as f:
                print(f.read())
        """, "abc", "t.txt", "3", "Fehler")
        output("""
            with open("n.txt", "w") as f:
                f.write("1")
            with open("n.txt", "w") as f:
                f.write("2")
            with open("n.txt") as f:
                print(f.read())
        """, "2", "12", "1", "Fehler", explain = "Der Modus `\"w\"` überschreibt den alten Inhalt.")
        typeOutput("""
            with open("a.txt", "w") as f:
                f.write("Py")
            with open("a.txt", "a") as f:
                f.write("thon")
            with open("a.txt") as f:
                print(f.read())
        """, "Python")
        fill("Öffne die Datei zum **Anhängen**.", """
            with open("log.txt", ??, encoding="utf-8") as f:
                f.write("Neuer Eintrag\n")
            print("gespeichert")
        """, answers = listOf("\"a\""), distractors = listOf("\"r\"", "\"append\"", "\"+\""), output = "gespeichert")
        match("\"r\"" to "lesen", "\"w\"" to "schreiben (überschreiben)", "\"a\"" to "anhängen", "\"x\"" to "neu erstellen")
        write("Schreibe die Wörter `Apfel`, `Birne` und `Kirsche` jeweils in eine eigene Zeile der Datei `obst.txt`. Lies die Datei danach wieder ein und gib ihren Inhalt aus.",
            solution = """
                with open("obst.txt", "w", encoding="utf-8") as f:
                    for frucht in ["Apfel", "Birne", "Kirsche"]:
                        f.write(frucht + "\n")

                with open("obst.txt", encoding="utf-8") as f:
                    print(f.read())
            """,
            output = """
                Apfel
                Birne
                Kirsche
            """,
            mustContain = listOf("open"),
            hint = "Schreiben mit `\"w\"` und `f.write(frucht + \"\\n\")`, dann mit `f.read()` wieder lesen.")
    }

    lesson("Zeilenweise arbeiten", "Große Dateien Zeile für Zeile", LessonIcon.LOOP) {
        text("Große Dateien liest man am besten **Zeile für Zeile** – einfach mit einer `for`-Schleife über die Datei:")
        code("""
            with open("noten.txt", "w", encoding="utf-8") as f:
                f.write("Ada;1\nLinus;3\nGrace;2\n")

            with open("noten.txt", encoding="utf-8") as f:
                for zeile in f:
                    name, note = zeile.strip().split(";")
                    print(f"{name} hat eine {note}")
        """, output = """
            Ada hat eine 1
            Linus hat eine 3
            Grace hat eine 2
        """)
        text("Jede Zeile endet mit einem Zeilenumbruch `\\n` – mit `strip()` entfernst du ihn. `readlines()` liefert alle Zeilen als Liste:")
        code("""
            with open("noten.txt", "w", encoding="utf-8") as f:
                f.write("Ada;1\nLinus;3\nGrace;2\n")

            with open("noten.txt", encoding="utf-8") as f:
                zeilen = f.readlines()
            print(len(zeilen))
            print(repr(zeilen[0]))
        """, output = """
            3
            'Ada;1\n'
        """)
        tip("Zum Schreiben vieler Zeilen ist `print(..., file=f)` praktisch – `print` hängt den Zeilenumbruch automatisch an.")
        code("""
            with open("liste.txt", "w", encoding="utf-8") as f:
                for i in range(1, 4):
                    print("Eintrag", i, file=f)

            with open("liste.txt", encoding="utf-8") as f:
                print(f.read())
        """, output = """
            Eintrag 1
            Eintrag 2
            Eintrag 3
        """)
        warn("Existiert eine Datei nicht, führt das Öffnen zum Lesen zu einem **FileNotFoundError**. Prüfe vorher mit `os.path.exists()` oder fange den Fehler ab.")

        output("""
            with open("z.txt", "w") as f:
                f.write("a\nb\nc\n")
            with open("z.txt") as f:
                print(len(f.readlines()))
        """, "3", "1", "6", "Fehler")
        output("""
            with open("y.txt", "w") as f:
                f.write("x\ny\n")
            with open("y.txt") as f:
                for z in f:
                    print(z.strip().upper())
        """, "X\nY", "x\ny", "XY", "Fehler")
        truth("Jede gelesene Zeile enthält am Ende normalerweise noch den Zeilenumbruch `\\n`.", true)
        choice("Was passiert bei `open(\"gibtsnicht.txt\")`?", "Ein FileNotFoundError", "Eine leere Datei wird erstellt", "Es wird `None` zurückgegeben", "Nichts")
        typeOutput("""
            with open("p.txt", "w") as f:
                print("Hallo", "Welt", file=f)
            with open("p.txt") as f:
                print(f.read().strip())
        """, "Hallo Welt")
        fill("Lies die Datei Zeile für Zeile und zähle die Zeilen.", """
            with open("zahlen.txt", "w") as f:
                f.write("4\n8\n15\n")
            anzahl = 0
            with open("zahlen.txt") as f:
                ?? zeile ?? f:
                    anzahl += 1
            print(anzahl)
        """, answers = listOf("for", "in"), distractors = listOf("while", "of", "if"), output = "3")
        output("""
            try:
                with open("fehlt_sicher_123.txt") as f:
                    print(f.read())
            except FileNotFoundError:
                print("Datei fehlt")
        """, "Datei fehlt", "Fehler", "None", "Nichts")
        write("Die Datei `punkte.txt` enthält pro Zeile eine Zahl – sie wird oben im Code für dich erstellt. Lies alle Zahlen ein und gib ihre **Summe** aus.",
            starter = """
                with open("punkte.txt", "w") as f:
                    f.write("12\n7\n30\n1\n")

                # Dein Code ab hier:
            """,
            solution = """
                with open("punkte.txt", "w") as f:
                    f.write("12\n7\n30\n1\n")

                summe = 0
                with open("punkte.txt") as f:
                    for zeile in f:
                        summe += int(zeile)
                print(summe)
            """,
            output = "50",
            mustContain = listOf("open"),
            hint = "Lies Zeile für Zeile und addiere `int(zeile)` – `int()` ignoriert das `\\n` am Ende.")
    }

    lesson("pathlib & CSV", "Moderne Pfade und Tabellen", LessonIcon.FILE) {
        text("Das moderne Modul `pathlib` macht die Arbeit mit Pfaden und Dateien besonders bequem:")
        code("""
            from pathlib import Path

            datei = Path("notiz.txt")
            datei.write_text("Einkaufen: Milch, Brot", encoding="utf-8")
            print(datei.exists())
            print(datei.read_text(encoding="utf-8"))
            print(datei.suffix, datei.stem)
        """, output = """
            True
            Einkaufen: Milch, Brot
            .txt notiz
        """)
        text("**CSV-Dateien** speichern Tabellen als Text – jede Zeile ist ein Datensatz, die Werte sind durch Kommas getrennt. Tabellenprogramme können sie öffnen. Das Modul `csv` hilft beim Lesen und Schreiben:")
        code("""
            import csv

            with open("klasse.csv", "w", newline="", encoding="utf-8") as f:
                schreiber = csv.writer(f)
                schreiber.writerow(["Name", "Alter"])
                schreiber.writerow(["Ada", 14])
                schreiber.writerow(["Linus", 15])

            with open("klasse.csv", encoding="utf-8") as f:
                for zeile in csv.DictReader(f):
                    print(zeile["Name"], "ist", zeile["Alter"])
        """, output = """
            Ada ist 14
            Linus ist 15
        """)
        tip("`csv.DictReader` liefert jede Zeile als Dictionary – mit den Spaltennamen aus der ersten Zeile als Schlüssel.")
        warn("Beim Schreiben von CSV-Dateien unbedingt `newline=\"\"` angeben, sonst entstehen unter Windows leere Zwischenzeilen.")

        output("""
            from pathlib import Path
            print(Path("bild.png").suffix)
        """, ".png", "png", "bild", "bild.png")
        output("""
            from pathlib import Path
            p = Path("x.txt")
            p.write_text("42")
            print(int(p.read_text()) + 1)
        """, "43", "421", "42", "Fehler")
        truth("`csv.DictReader` liefert jede Zeile als Dictionary.", true)
        choice("Was steht in einer CSV-Datei typischerweise zwischen den Werten?", "Ein Komma", "Ein Doppelpunkt", "Ein Tabulator ist Pflicht", "Nichts")
        typeOutput("""
            from pathlib import Path
            print(Path("daten/bericht.pdf").stem)
        """, "bericht")
        fill("Prüfe, ob die Datei existiert.", """
            from pathlib import Path
            p = Path("gibts_nicht_123.txt")
            print(p.??())
        """, answers = listOf("exists"), distractors = listOf("is", "open", "read_text"), output = "False")
        output("""
            import csv
            with open("t.csv", "w", newline="") as f:
                csv.writer(f).writerow(["a", "b", "c"])
            with open("t.csv") as f:
                print(next(csv.reader(f)))
        """, "['a', 'b', 'c']", "a,b,c", "['a,b,c']", "Fehler")
        write("Schreibe mit dem `csv`-Modul eine Datei `einkauf.csv` mit den Spalten `artikel` und `preis` und zwei Zeilen: Brot 2.5 und Milch 1.5. Lies sie danach mit `csv.DictReader` ein und gib die **Summe der Preise** aus.",
            solution = """
                import csv

                with open("einkauf.csv", "w", newline="", encoding="utf-8") as f:
                    w = csv.writer(f)
                    w.writerow(["artikel", "preis"])
                    w.writerow(["Brot", 2.5])
                    w.writerow(["Milch", 1.5])

                summe = 0
                with open("einkauf.csv", encoding="utf-8") as f:
                    for zeile in csv.DictReader(f):
                        summe += float(zeile["preis"])
                print(summe)
            """,
            output = "4.0",
            mustContain = listOf("csv"),
            hint = "Beim Lesen sind alle Werte Strings – wandle den Preis mit `float()` um.")
    }

    checkpoint()
}
