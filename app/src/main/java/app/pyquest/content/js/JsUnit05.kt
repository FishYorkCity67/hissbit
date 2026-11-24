package app.pyquest.content.js

import app.pyquest.content.CourseBuilder
import app.pyquest.data.LessonIcon

fun CourseBuilder.jsUnit05() = unit("Schleifen", "Wiederholen mit while und for") {
    lesson("while-Schleifen", "Wiederholen, solange etwas gilt", LessonIcon.LOOP) {
        text("Eine `while`-Schleife wiederholt ihren Block, **solange** die Bedingung `true` ist. Vor jeder Runde wird die Bedingung neu geprüft.")
        code("""
            let countdown = 3;
            while (countdown > 0) {
              console.log(countdown);
              countdown--;
            }
            console.log("Start! 🚀");
        """, output = """
            3
            2
            1
            Start! 🚀
        """)
        warn("Ändert sich in der Schleife nichts an der Bedingung, läuft sie **endlos**. Hissbit bricht so ein Programm nach ein paar Sekunden ab – im Browser würde die Seite einfrieren.")
        text("Typisches Muster: Werte in einer Schleife **aufsummieren**.")
        code("""
            let summe = 0;
            let zahl = 1;
            while (zahl <= 5) {
              summe += zahl;
              zahl++;
            }
            console.log(summe);
        """, output = "15")
        tip("`do { ... } while (bedingung);` führt den Block mindestens **einmal** aus und prüft erst danach.")

        output("""
            let i = 1;
            while (i <= 3) {
              console.log(i);
              i++;
            }
        """, "1\n2\n3", "1\n2\n3\n4", "0\n1\n2", "1")
        output("""
            let n = 10;
            while (n > 10) {
              console.log(n);
              n--;
            }
            console.log("Ende");
        """, "Ende", "10\nEnde", "10", "Nichts", explain = "Die Bedingung ist von Anfang an falsch – der Schleifenblock läuft kein einziges Mal.")
        typeOutput("""
            let x = 1;
            while (x < 20) {
              x *= 2;
            }
            console.log(x);
        """, "32")
        choice("Was passiert bei `while (true) { }` ohne Ausstieg?", "Die Schleife läuft endlos", "Sie läuft genau einmal", "Sie läuft gar nicht", "Es gibt sofort einen SyntaxError")
        fill("Vervollständige den Countdown von 5 bis 1.", """
            let n = 5;
            ?? (n >= 1) {
              console.log(n);
              n--;
            }
        """, answers = listOf("while"), distractors = listOf("if", "repeat", "loop"), output = "5\n4\n3\n2\n1")
        truth("Bei `while` wird die Bedingung vor **jeder** Runde neu geprüft.", true)
        write("Gib mit einer `while`-Schleife die Zahlen **2, 4, 6, 8, 10** aus – jede in einer eigenen Zeile.",
            solution = """
                let n = 2;
                while (n <= 10) {
                  console.log(n);
                  n += 2;
                }
            """,
            output = """
                2
                4
                6
                8
                10
            """,
            mustContain = listOf("while"),
            hint = "Starte bei 2 und erhöhe in jeder Runde um 2, solange `n <= 10` ist.")
    }

    lesson("for-Schleifen", "Zählen leicht gemacht", LessonIcon.LOOP) {
        text("Die `for`-Schleife fasst alles fürs Zählen in einer Zeile zusammen: **Start**, **Bedingung** und **Schritt** – getrennt durch Semikolons.")
        code("""
            for (let i = 1; i <= 4; i++) {
              console.log("Runde", i);
            }
        """, output = """
            Runde 1
            Runde 2
            Runde 3
            Runde 4
        """)
        text("""
            So liest du `for (let i = 1; i <= 4; i++)`:
            - `let i = 1` – einmal am Anfang
            - `i <= 4` – vor jeder Runde prüfen
            - `i++` – nach jeder Runde ausführen
        """)
        code("""
            for (let i = 10; i > 0; i -= 3) {
              console.log(i);
            }
        """, output = """
            10
            7
            4
            1
        """)
        text("Mit einer `for`-Schleife kannst du auch die Zeichen eines Strings durchgehen:")
        code("""
            const wort = "Hey";
            for (let i = 0; i < wort.length; i++) {
              console.log(i, wort[i]);
            }
        """, output = """
            0 H
            1 e
            2 y
        """)
        tip("Der Zähler heißt traditionell `i` (für Index). Er existiert nur innerhalb der Schleife.")

        output("""
            for (let i = 0; i < 3; i++) {
              console.log(i);
            }
        """, "0\n1\n2", "1\n2\n3", "0\n1\n2\n3", "3")
        output("""
            let summe = 0;
            for (let i = 1; i <= 4; i++) {
              summe += i;
            }
            console.log(summe);
        """, "10", "4", "1234", "6")
        typeOutput("""
            for (let i = 5; i >= 1; i -= 2) {
              console.log(i);
            }
        """, "5\n3\n1")
        choice("Wie oft läuft `for (let i = 0; i < 5; i++)`?", "5-mal", "4-mal", "6-mal", "Endlos", mono = false)
        fill("Gib die Zahlen 1 bis 3 aus.", """
            for (let i = 1; i ?? 3; i++) {
              console.log(i);
            }
        """, answers = listOf("<="), distractors = listOf("<", ">=", "=="), output = "1\n2\n3")
        arrange("Gib das Dreier-Einmaleins bis 3 · 5 aus (3, 6, 9, 12, 15).", """
            for (let i = 1; i <= 5; i++) {
              console.log(3 * i);
            }
        """, output = "3\n6\n9\n12\n15")
        truth("In `for (let i = 0; i < 10; i++)` wird `i++` vor der ersten Runde ausgeführt.", false,
            explain = "Der Schritt `i++` kommt immer **nach** einer Runde. Die erste Runde läuft mit `i = 0`.")
        write("Gib mit einer `for`-Schleife `1 Schaf`, `2 Schafe`, … bis `5 Schafe` aus. Achtung: Bei 1 heißt es „Schaf“, sonst „Schafe“.",
            solution = """
                for (let i = 1; i <= 5; i++) {
                  console.log(i === 1 ? "1 Schaf" : `${D}{i} Schafe`);
                }
            """,
            output = """
                1 Schaf
                2 Schafe
                3 Schafe
                4 Schafe
                5 Schafe
            """,
            mustContain = listOf("for"),
            hint = "Mit `if` oder dem `?`-Operator wählst du zwischen „Schaf“ und „Schafe“.")
    }

    lesson("break & continue", "Schleifen steuern", LessonIcon.LOOP) {
        text("`break` beendet eine Schleife sofort. `continue` überspringt den Rest der aktuellen Runde und macht mit der nächsten weiter.")
        code("""
            for (let i = 1; i <= 10; i++) {
              if (i === 4) {
                break;
              }
              console.log(i);
            }
        """, output = """
            1
            2
            3
        """)
        code("""
            for (let i = 1; i <= 6; i++) {
              if (i % 2 === 0) {
                continue;
              }
              console.log(i);
            }
        """, output = """
            1
            3
            5
        """)
        text("Ein häufiges Muster ist `while (true)` mit einem `break` im Inneren – zum Beispiel, wenn du so lange fragst, bis eine gültige Eingabe kommt.")
        code("""
            while (true) {
              const antwort = prompt("Wie viel ist 3 + 4?");
              if (antwort === "7") {
                console.log("Richtig!");
                break;
              }
              console.log("Leider falsch, nochmal.");
            }
        """, output = """
            Wie viel ist 3 + 4? 6
            Leider falsch, nochmal.
            Wie viel ist 3 + 4? 7
            Richtig!
        """, stdin = listOf("6", "7"))

        output("""
            for (let i = 0; i < 5; i++) {
              if (i === 2) break;
              console.log(i);
            }
        """, "0\n1", "0\n1\n2", "2", "0\n1\n3\n4")
        output("""
            for (let i = 0; i < 5; i++) {
              if (i === 2) continue;
              console.log(i);
            }
        """, "0\n1\n3\n4", "0\n1", "0\n1\n2\n3\n4", "2")
        choice("Was macht `continue`?", "Springt sofort zur nächsten Runde", "Beendet die Schleife", "Beendet das Programm", "Wiederholt die aktuelle Runde")
        typeOutput("""
            let n = 0;
            while (true) {
              n += 3;
              if (n > 10) break;
            }
            console.log(n);
        """, "12")
        fill("Beende die Suche, sobald die erste durch 7 teilbare Zahl gefunden ist.", """
            for (let i = 20; i < 40; i++) {
              if (i % 7 === 0) {
                console.log(i);
                ??;
              }
            }
        """, answers = listOf("break"), distractors = listOf("continue", "stop", "return"), output = "21")
        truth("`break` beendet nur die Schleife, nicht das ganze Programm.", true)
        write("Gib alle Zahlen von 1 bis 15 aus, die **nicht** durch 3 teilbar sind. Benutze `continue`.",
            solution = """
                for (let i = 1; i <= 15; i++) {
                  if (i % 3 === 0) continue;
                  console.log(i);
                }
            """,
            output = """
                1
                2
                4
                5
                7
                8
                10
                11
                13
                14
            """,
            mustContain = listOf("continue"),
            hint = "Prüfe mit `i % 3 === 0` und überspringe diese Zahlen mit `continue`.")
    }

    lesson("Verschachtelte Schleifen", "Schleifen in Schleifen – Muster und Tabellen", LessonIcon.PUZZLE) {
        text("Schleifen lassen sich ineinander stecken. Die innere Schleife läuft dann für **jede** Runde der äußeren komplett durch.")
        code("""
            for (let zeile = 1; zeile <= 3; zeile++) {
              let text = "";
              for (let spalte = 1; spalte <= 4; spalte++) {
                text += zeile * spalte + " ";
              }
              console.log(text);
            }
        """, output = """
            1 2 3 4
            2 4 6 8
            3 6 9 12
        """)
        text("Muster aus Zeichen lassen sich oft auch mit `repeat()` bauen:")
        code("""
            for (let i = 1; i <= 4; i++) {
              console.log("*".repeat(i));
            }
        """, output = """
            *
            **
            ***
            ****
        """)
        tip("`console.log()` macht immer eine neue Zeile. Für eine Zeile aus mehreren Teilen baust du zuerst einen String zusammen und gibst ihn am Ende aus.")

        output("""
            for (let a = 1; a <= 2; a++) {
              for (let b = 1; b <= 2; b++) {
                console.log(a, b);
              }
            }
        """, "1 1\n1 2\n2 1\n2 2", "1 1\n2 2", "1 2\n1 2", "1 1\n2 1\n1 2\n2 2")
        typeOutput("""
            let zaehler = 0;
            for (let i = 0; i < 3; i++) {
              for (let j = 0; j < 4; j++) {
                zaehler++;
              }
            }
            console.log(zaehler);
        """, "12", explain = "3 Runden außen mal 4 Runden innen.")
        output("""
            for (let i = 3; i >= 1; i--) {
              console.log("#".repeat(i));
            }
        """, "###\n##\n#", "#\n##\n###", "###", "#\n#\n#")
        choice("Wie oft läuft der innere Block bei 5 Runden außen und 3 Runden innen?", "15-mal", "8-mal", "5-mal", "3-mal")
        fill("Baue eine Zeile aus 5 Sternen und gib sie aus.", """
            let zeile = "";
            for (let i = 0; i < 5; i++) {
              zeile ?? "*";
            }
            console.log(zeile);
        """, answers = listOf("+="), distractors = listOf("=", "-=", "=="), output = "*****")
        write("Gib ein Rechteck aus `#` mit **3 Zeilen** und **6 Spalten** aus.",
            solution = """
                for (let i = 0; i < 3; i++) {
                  console.log("#".repeat(6));
                }
            """,
            output = """
                ######
                ######
                ######
            """,
            mustContain = listOf("for"),
            hint = "Eine Schleife für die Zeilen; jede Zeile ist `\"#\".repeat(6)`.")
        write("Gib eine Treppe aus Zahlen aus: In Zeile `n` stehen die Zahlen von 1 bis `n`, getrennt durch Leerzeichen – für `n` von 1 bis 4.",
            solution = """
                for (let n = 1; n <= 4; n++) {
                  let zeile = "";
                  for (let i = 1; i <= n; i++) {
                    zeile += i + " ";
                  }
                  console.log(zeile.trim());
                }
            """,
            output = """
                1
                1 2
                1 2 3
                1 2 3 4
            """,
            mustContain = listOf("for"),
            hint = "Außen zählt `n` die Zeilen, innen baust du mit einer zweiten Schleife den Text für Zeile `n` zusammen.")
    }

    project("Projekt: Zahlenraten", "Ein kleines Spiel mit Schleife und Eingabe") {
        text("Zeit für ein erstes Spiel! Monty denkt sich eine Zahl aus, und du rätst so lange, bis du sie triffst. Nach jedem Tipp gibt das Programm einen Hinweis.")
        text("Echte Zufallszahlen erzeugst du mit `Math.random()` (eine Zahl zwischen 0 und 1). So entsteht eine ganze Zahl von 1 bis 10:")
        code("""
            const geheim = Math.floor(Math.random() * 10) + 1;
            console.log(geheim >= 1 && geheim <= 10);
        """, output = "true")
        tip("Für die Aufgabe nehmen wir eine **feste** Geheimzahl – sonst könnte man das Ergebnis nicht automatisch prüfen.")

        choice("Welche Werte kann `Math.floor(Math.random() * 10) + 1` haben?", "1 bis 10", "0 bis 10", "0 bis 9", "1 bis 11")
        arrange("Bringe die Spielschleife in die richtige Reihenfolge.", """
            const geheim = 4;
            let tipp = 0;
            while (tipp !== geheim) {
              tipp = Number(prompt("Dein Tipp?"));
            }
            console.log("Getroffen!");
        """)
        write("""
            Programmiere das Spiel: Die Geheimzahl ist `7`. Frage in einer Schleife mit `prompt("Dein Tipp?")` nach Zahlen.
            Ist der Tipp zu klein, gib `Zu klein!` aus, ist er zu groß, `Zu groß!`. Bei einem Treffer gib `Richtig! Versuche: <Anzahl>` aus und beende das Spiel.
        """.trimIndent(),
            starter = """
                const geheim = 7;
                let versuche = 0;
            """,
            solution = """
                const geheim = 7;
                let versuche = 0;
                while (true) {
                  const tipp = Number(prompt("Dein Tipp?"));
                  versuche++;
                  if (tipp < geheim) {
                    console.log("Zu klein!");
                  } else if (tipp > geheim) {
                    console.log("Zu groß!");
                  } else {
                    console.log(`Richtig! Versuche: ${D}{versuche}`);
                    break;
                  }
                }
            """,
            output = """
                Zu klein!
                Zu groß!
                Zu klein!
                Richtig! Versuche: 4
            """,
            stdin = listOf("3", "9", "6", "7"),
            mustContain = listOf("prompt", "while"),
            hint = "`while (true)` mit `break` beim Treffer. Zähle `versuche` in jeder Runde hoch.")
        output("""
            let tipp = 1;
            let versuche = 0;
            while (tipp !== 4) {
              tipp++;
              versuche++;
            }
            console.log(versuche);
        """, "3", "4", "1", "Die Schleife läuft endlos", explain = "`tipp` wird 2, 3 und 4 – nach drei Runden ist die Bedingung `tipp !== 4` falsch.")
    }

    checkpoint()
}
