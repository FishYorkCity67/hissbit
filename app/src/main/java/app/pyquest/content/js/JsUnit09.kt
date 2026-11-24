package app.pyquest.content.js

import app.pyquest.content.CourseBuilder
import app.pyquest.data.LessonIcon

fun CourseBuilder.jsUnit09() = unit("Array-Methoden", "forEach, map, filter, reduce & Co.") {
    lesson("forEach & map", "Jedes Element bearbeiten", LessonIcon.LOOP) {
        text("Arrays haben Methoden, denen du eine **Funktion** übergibst. `forEach` ruft sie für jedes Element auf – als zweiten Wert bekommst du den Index dazu.")
        code("""
            const tiere = ["Hund", "Katze", "Maus"];
            tiere.forEach((tier, i) => {
              console.log(`${D}{i}: ${D}{tier}`);
            });
        """, output = """
            0: Hund
            1: Katze
            2: Maus
        """)
        text("`map` erzeugt ein **neues Array**: Jedes Element wird durch das Ergebnis deiner Funktion ersetzt. Das Original bleibt unverändert.")
        code("""
            const preise = [10, 20, 30];
            const halbePreise = preise.map((p) => p / 2);
            console.log(halbePreise);
            console.log(preise);
            console.log(["a", "b"].map((s) => s.toUpperCase()));
        """, output = """
            [ 5, 10, 15 ]
            [ 10, 20, 30 ]
            [ 'A', 'B' ]
        """)
        tip("Merke: `forEach` ist zum **Tun** da (z. B. ausgeben) und liefert nichts zurück. `map` ist zum **Umwandeln** da und liefert ein neues Array.")

        output("""console.log([1, 2, 3].map((x) => x * 10));""", "[ 10, 20, 30 ]", "[ 1, 2, 3 ]", "60", "[ 10 ]")
        output("""
            const r = [1, 2].forEach((x) => x * 2);
            console.log(r);
        """, "undefined", "[ 2, 4 ]", "[ 1, 2 ]", "null", explain = "`forEach` liefert nie ein Ergebnis – dafür gibt es `map`.")
        typeOutput("""
            const namen = ["ada", "ben"];
            console.log(namen.map((n) => n.length));
        """, "[ 3, 3 ]")
        choice("Was liefert `map`?", "Ein neues Array mit den umgewandelten Werten", "Das veränderte Original-Array", "Die Anzahl der Elemente", "undefined")
        fill("Erzeuge ein Array mit allen Zahlen verdoppelt.", """
            const zahlen = [4, 5, 6];
            const doppelt = zahlen.??((z) => z * 2);
            console.log(doppelt);
        """, answers = listOf("map"), distractors = listOf("forEach", "filter", "push"), output = "[ 8, 10, 12 ]")
        output("""
            ["x", "y"].forEach((b, i) => console.log(i + b));
        """, "0x\n1y", "x0\ny1", "1x\n2y", "[ '0x', '1y' ]")
        truth("`map` verändert das ursprüngliche Array.", false)
        write("Wandle das Array `celsius` mit `map` in Fahrenheit um (`c * 9 / 5 + 32`) und gib das neue Array aus.",
            starter = """
                const celsius = [0, 10, 25, 100];
            """,
            solution = """
                const celsius = [0, 10, 25, 100];
                const fahrenheit = celsius.map((c) => c * 9 / 5 + 32);
                console.log(fahrenheit);
            """,
            output = "[ 32, 50, 77, 212 ]",
            mustContain = listOf("map"),
            hint = "`celsius.map((c) => c * 9 / 5 + 32)`")
    }

    lesson("filter & find", "Passende Elemente auswählen", LessonIcon.PUZZLE) {
        text("`filter` behält nur die Elemente, für die deine Funktion `true` liefert – das Ergebnis ist ein neues Array. `find` liefert dagegen nur das **erste** passende Element (oder `undefined`).")
        code("""
            const zahlen = [3, 8, 12, 5, 20];
            console.log(zahlen.filter((z) => z > 6));
            console.log(zahlen.find((z) => z > 6));
            console.log(zahlen.find((z) => z > 100));
            console.log(zahlen.findIndex((z) => z === 5));
        """, output = """
            [ 8, 12, 20 ]
            8
            undefined
            3
        """)
        text("`some` prüft, ob **mindestens ein** Element passt, `every`, ob **alle** passen. Beide liefern `true` oder `false`.")
        code("""
            const alter = [12, 15, 19];
            console.log(alter.some((a) => a >= 18));
            console.log(alter.every((a) => a >= 18));
        """, output = """
            true
            false
        """)
        text("Besonders nützlich sind diese Methoden bei Arrays von Objekten:")
        code("""
            const produkte = [
              { name: "Apfel", preis: 0.5 },
              { name: "Kaffee", preis: 6.99 },
              { name: "Brot", preis: 2.5 },
            ];
            const guenstig = produkte.filter((p) => p.preis < 3);
            console.log(guenstig.map((p) => p.name));
            console.log(produkte.find((p) => p.name === "Kaffee").preis);
        """, output = """
            [ 'Apfel', 'Brot' ]
            6.99
        """)

        output("""console.log([1, 2, 3, 4].filter((x) => x % 2 === 0));""", "[ 2, 4 ]", "[ 1, 3 ]", "2", "[ false, true, false, true ]")
        output("""console.log(["Ada", "Ben", "Anna"].find((n) => n.startsWith("A")));""", "Ada", "[ 'Ada', 'Anna' ]", "Anna", "true")
        typeOutput("""console.log([5, 7, 9].every((x) => x > 4));""", "true")
        output("""console.log([1, 2, 3].some((x) => x > 5));""", "false", "true", "[]", "undefined")
        match(
            "filter" to "alle passenden Elemente",
            "find" to "das erste passende Element",
            "some" to "passt mindestens eins?",
            "every" to "passen alle?",
            "findIndex" to "Position des ersten Treffers",
        )
        fill("Behalte nur die Wörter mit mehr als 3 Buchstaben.", """
            const woerter = ["Hi", "Hallo", "Moin", "Yo"];
            console.log(woerter.??((w) => w.length > 3));
        """, answers = listOf("filter"), distractors = listOf("find", "map", "some"), output = "[ 'Hallo', 'Moin' ]")
        truth("`find` liefert immer ein Array.", false, explain = "`find` liefert das erste passende Element selbst – oder `undefined`.")
        write("Gib aus dem Array `schueler` die Namen aller aus, die **bestanden** haben (`punkte >= 50`), als Array.",
            starter = """
                const schueler = [
                  { name: "Ada", punkte: 78 },
                  { name: "Ben", punkte: 45 },
                  { name: "Cleo", punkte: 50 },
                  { name: "Dev", punkte: 31 },
                ];
            """,
            solution = """
                const schueler = [
                  { name: "Ada", punkte: 78 },
                  { name: "Ben", punkte: 45 },
                  { name: "Cleo", punkte: 50 },
                  { name: "Dev", punkte: 31 },
                ];
                const bestanden = schueler.filter((s) => s.punkte >= 50).map((s) => s.name);
                console.log(bestanden);
            """,
            output = "[ 'Ada', 'Cleo' ]",
            mustContain = listOf("filter"),
            hint = "Erst `filter` nach Punkten, dann mit `map` nur die Namen übrig lassen.")
    }

    lesson("reduce", "Alles zu einem Wert zusammenfassen", LessonIcon.CALC) {
        text("`reduce` fasst ein Array zu **einem einzigen Wert** zusammen – etwa einer Summe. Deine Funktion bekommt den bisherigen Zwischenstand und das aktuelle Element. Der zweite Wert von `reduce` ist der Startwert.")
        code("""
            const zahlen = [5, 10, 15];
            const summe = zahlen.reduce((bisher, z) => bisher + z, 0);
            console.log(summe);
        """, output = "30")
        text("""
            So rechnet `reduce` Schritt für Schritt:
            - Start: `bisher = 0`
            - `0 + 5 = 5`
            - `5 + 10 = 15`
            - `15 + 15 = 30`
        """)
        code("""
            const preise = [2.5, 4, 1.5];
            const gesamt = preise.reduce((s, p) => s + p, 0);
            console.log(gesamt.toFixed(2));

            const woerter = ["a", "bb", "ccc"];
            const zeichen = woerter.reduce((n, w) => n + w.length, 0);
            console.log(zeichen);
        """, output = """
            8.00
            6
        """)
        text("Mit einem Objekt als Startwert kannst du sogar zählen, wie oft etwas vorkommt:")
        code("""
            const stimmen = ["Pizza", "Pasta", "Pizza", "Salat", "Pizza"];
            const zaehlung = stimmen.reduce((z, essen) => {
              z[essen] = (z[essen] ?? 0) + 1;
              return z;
            }, {});
            console.log(zaehlung);
        """, output = "{ Pizza: 3, Pasta: 1, Salat: 1 }")
        tip("Für einfache Summen ist `reduce` perfekt. Wird die Funktion darin lang und kompliziert, ist eine normale Schleife oft lesbarer.")

        output("""console.log([1, 2, 3, 4].reduce((a, b) => a + b, 0));""", "10", "24", "[ 1, 2, 3, 4 ]", "4")
        output("""console.log([2, 3, 4].reduce((a, b) => a * b, 1));""", "24", "9", "0", "10")
        typeOutput("""console.log([3, 9, 2].reduce((max, x) => (x > max ? x : max), 0));""", "9")
        choice("Wofür steht die `0` in `zahlen.reduce((s, z) => s + z, 0)`?", "Der Startwert", "Der erste Index", "Das Ergebnis", "Die Anzahl der Runden")
        fill("Berechne die Gesamtzahl der Tore.", """
            const spiele = [{ tore: 2 }, { tore: 0 }, { tore: 3 }];
            const tore = spiele.reduce((summe, s) => summe ?? s.tore, 0);
            console.log(tore);
        """, answers = listOf("+"), distractors = listOf("*", "-", "||"), output = "5")
        truth("`reduce` liefert immer ein Array zurück.", false)
        write("Berechne mit `reduce` den Gesamtpreis aller Artikel im Warenkorb (`preis * menge`) und gib ihn mit 2 Nachkommastellen aus.",
            starter = """
                const warenkorb = [
                  { name: "Stift", preis: 1.2, menge: 3 },
                  { name: "Heft", preis: 2.5, menge: 2 },
                  { name: "Lineal", preis: 0.9, menge: 1 },
                ];
            """,
            solution = """
                const warenkorb = [
                  { name: "Stift", preis: 1.2, menge: 3 },
                  { name: "Heft", preis: 2.5, menge: 2 },
                  { name: "Lineal", preis: 0.9, menge: 1 },
                ];
                const gesamt = warenkorb.reduce((summe, a) => summe + a.preis * a.menge, 0);
                console.log(gesamt.toFixed(2));
            """,
            output = "9.50",
            mustContain = listOf("reduce"),
            hint = "`warenkorb.reduce((summe, a) => summe + a.preis * a.menge, 0)` – danach `.toFixed(2)`.")
    }

    lesson("Sortieren & Verketten", "sort mit Vergleichsfunktion und Methodenketten", LessonIcon.MAGIC) {
        text("`sort()` sortiert ein Array – aber Achtung: Ohne Hilfe vergleicht es alles **als Text**. Bei Zahlen kommt dann Unsinn heraus!")
        code("""
            console.log([10, 9, 100, 1].sort());
            console.log(["Kiwi", "Apfel", "Mango"].sort());
        """, output = """
            [ 1, 10, 100, 9 ]
            [ 'Apfel', 'Kiwi', 'Mango' ]
        """)
        text("Für Zahlen übergibst du eine **Vergleichsfunktion**: `(a, b) => a - b` sortiert aufsteigend, `(a, b) => b - a` absteigend. Ist das Ergebnis negativ, kommt `a` nach vorne.")
        code("""
            const zahlen = [10, 9, 100, 1];
            console.log([...zahlen].sort((a, b) => a - b));
            console.log([...zahlen].sort((a, b) => b - a));

            const spieler = [
              { name: "Ada", punkte: 30 },
              { name: "Ben", punkte: 50 },
              { name: "Cleo", punkte: 40 },
            ];
            spieler.sort((a, b) => b.punkte - a.punkte);
            console.log(spieler.map((s) => s.name));
        """, output = """
            [ 1, 9, 10, 100 ]
            [ 100, 10, 9, 1 ]
            [ 'Ben', 'Cleo', 'Ada' ]
        """)
        warn("`sort()` verändert das Array selbst. Willst du das Original behalten, sortiere eine Kopie: `[...liste].sort(...)`.")
        text("Weil `filter` und `map` neue Arrays liefern, kannst du sie **verketten** – wie ein Fließband:")
        code("""
            const ergebnis = [5, 12, 8, 21, 3]
              .filter((z) => z > 4)
              .map((z) => z * 2)
              .sort((a, b) => a - b);
            console.log(ergebnis);
        """, output = "[ 10, 16, 24, 42 ]")

        output("""console.log([3, 1, 2].sort((a, b) => a - b));""", "[ 1, 2, 3 ]", "[ 3, 2, 1 ]", "[ 3, 1, 2 ]", "[ 1, 3, 2 ]")
        output("""console.log([25, 3, 100].sort());""", "[ 100, 25, 3 ]", "[ 3, 25, 100 ]", "[ 100, 3, 25 ]", "[ 25, 3, 100 ]",
            explain = "Ohne Vergleichsfunktion wird als Text sortiert: \"100\" < \"25\" < \"3\", weil zuerst das erste Zeichen verglichen wird.")
        typeOutput("""console.log([4, 8, 1].sort((a, b) => b - a));""", "[ 8, 4, 1 ]")
        choice("Welche Vergleichsfunktion sortiert Zahlen **absteigend**?", "(a, b) => b - a", "(a, b) => a - b", "(a, b) => a > b", "(a, b) => 0", mono = true)
        output("""console.log([1, 2, 3, 4, 5].filter((x) => x > 2).map((x) => x * x));""", "[ 9, 16, 25 ]", "[ 1, 4 ]", "[ 3, 4, 5 ]", "[ 9, 16 ]")
        fill("Sortiere die Wörter nach ihrer **Länge** (kürzeste zuerst).", """
            const w = ["Banane", "Kiwi", "Apfel"];
            w.sort((a, b) => a.length ?? b.length);
            console.log(w);
        """, answers = listOf("-"), distractors = listOf("+", ">", "*"), output = "[ 'Kiwi', 'Apfel', 'Banane' ]")
        truth("`[...liste].sort()` lässt `liste` unverändert.", true)
        write("Gib die Namen der **drei ältesten** Personen aus `leute` aus – älteste zuerst, als Array. Verkette `sort`, `slice` und `map`.",
            starter = """
                const leute = [
                  { name: "Ada", alter: 36 },
                  { name: "Ben", alter: 12 },
                  { name: "Cleo", alter: 71 },
                  { name: "Dev", alter: 45 },
                  { name: "Eli", alter: 28 },
                ];
            """,
            solution = """
                const leute = [
                  { name: "Ada", alter: 36 },
                  { name: "Ben", alter: 12 },
                  { name: "Cleo", alter: 71 },
                  { name: "Dev", alter: 45 },
                  { name: "Eli", alter: 28 },
                ];
                const aelteste = [...leute]
                  .sort((a, b) => b.alter - a.alter)
                  .slice(0, 3)
                  .map((p) => p.name);
                console.log(aelteste);
            """,
            output = "[ 'Cleo', 'Dev', 'Ada' ]",
            mustContain = listOf("sort", "map"),
            hint = "`sort((a, b) => b.alter - a.alter)`, dann `slice(0, 3)` und `map((p) => p.name)`.")
    }

    project("Projekt: Notenrechner", "Alles aus dieser Einheit in einem Programm") {
        text("Jetzt kombinierst du `map`, `filter`, `reduce` und `sort` zu einem kleinen Notenrechner. Eine Klasse hat Punktzahlen geschrieben – du wertest sie aus.")
        code("""
            const ergebnisse = [
              { name: "Ada", punkte: 92 },
              { name: "Ben", punkte: 58 },
              { name: "Cleo", punkte: 74 },
            ];
            const schnitt = ergebnisse.reduce((s, e) => s + e.punkte, 0) / ergebnisse.length;
            console.log(`Durchschnitt: ${D}{schnitt.toFixed(1)}`);
        """, output = "Durchschnitt: 74.7")

        write("""
            Schreibe eine Funktion `note(punkte)`, die nach diesem Schlüssel eine Note zurückgibt:
            - ab 90 Punkten: `1`
            - ab 75: `2`
            - ab 60: `3`
            - ab 50: `4`
            - darunter: `5`
        """.trimIndent(),
            solution = """
                function note(punkte) {
                  if (punkte >= 90) return 1;
                  if (punkte >= 75) return 2;
                  if (punkte >= 60) return 3;
                  if (punkte >= 50) return 4;
                  return 5;
                }
            """,
            test = """
                const faelle = [[95, 1], [90, 1], [89, 2], [75, 2], [60, 3], [59, 4], [50, 4], [12, 5]];
                for (const [p, n] of faelle) {
                  if (note(p) !== n) throw new Error(`note(${D}{p}) sollte ${D}{n} sein, ist aber ${D}{note(p)}`);
                }
                console.log("✅ Notenschlüssel stimmt!");
            """,
            hint = "Prüfe von oben nach unten: Das erste `return`, das zutrifft, beendet die Funktion.")
        write("""
            Gib für jede Person in `klasse` eine Zeile `<Name>: <Punkte> Punkte → Note <Note>` aus – sortiert nach Punkten, **beste zuerst**.
            Die Funktion `note` ist schon fertig.
        """.trimIndent(),
            starter = """
                function note(p) {
                  return p >= 90 ? 1 : p >= 75 ? 2 : p >= 60 ? 3 : p >= 50 ? 4 : 5;
                }
                const klasse = [
                  { name: "Ada", punkte: 92 },
                  { name: "Ben", punkte: 58 },
                  { name: "Cleo", punkte: 74 },
                  { name: "Dev", punkte: 81 },
                ];
            """,
            solution = """
                function note(p) {
                  return p >= 90 ? 1 : p >= 75 ? 2 : p >= 60 ? 3 : p >= 50 ? 4 : 5;
                }
                const klasse = [
                  { name: "Ada", punkte: 92 },
                  { name: "Ben", punkte: 58 },
                  { name: "Cleo", punkte: 74 },
                  { name: "Dev", punkte: 81 },
                ];
                [...klasse]
                  .sort((a, b) => b.punkte - a.punkte)
                  .forEach((s) => console.log(`${D}{s.name}: ${D}{s.punkte} Punkte → Note ${D}{note(s.punkte)}`));
            """,
            output = """
                Ada: 92 Punkte → Note 1
                Dev: 81 Punkte → Note 2
                Cleo: 74 Punkte → Note 3
                Ben: 58 Punkte → Note 4
            """,
            mustContain = listOf("sort"),
            hint = "Sortiere eine Kopie absteigend nach `punkte` und gib dann jede Zeile mit `forEach` aus.")
        write("Gib aus, wie viele Personen in `klasse` **bestanden** haben (Note 4 oder besser), im Format `Bestanden: 3 von 4`.",
            starter = """
                const klasse = [
                  { name: "Ada", note: 1 },
                  { name: "Ben", note: 5 },
                  { name: "Cleo", note: 3 },
                  { name: "Dev", note: 4 },
                ];
            """,
            solution = """
                const klasse = [
                  { name: "Ada", note: 1 },
                  { name: "Ben", note: 5 },
                  { name: "Cleo", note: 3 },
                  { name: "Dev", note: 4 },
                ];
                const bestanden = klasse.filter((s) => s.note <= 4).length;
                console.log(`Bestanden: ${D}{bestanden} von ${D}{klasse.length}`);
            """,
            output = "Bestanden: 3 von 4",
            mustContain = listOf("filter"),
            hint = "`klasse.filter((s) => s.note <= 4).length` zählt die Bestandenen.")
        output("""
            const p = [70, 85, 40];
            console.log(p.map((x) => (x >= 50 ? "✓" : "✗")).join(" "));
        """, "✓ ✓ ✗", "✓ ✓ ✓", "✗ ✓ ✗", "[ '✓', '✓', '✗' ]")
    }

    checkpoint()
}
