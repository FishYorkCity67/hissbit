package app.pyquest.content.js

import app.pyquest.content.CourseBuilder
import app.pyquest.data.LessonIcon

fun CourseBuilder.jsUnit06() = unit("Arrays", "Listen von Werten speichern und bearbeiten") {
    lesson("Arrays erstellen", "Viele Werte in einer Variable", LessonIcon.BOX) {
        text("Ein **Array** speichert mehrere Werte in einer festen Reihenfolge. Du schreibst sie in eckige Klammern, getrennt durch Kommas.")
        code("""
            const fruechte = ["Apfel", "Birne", "Kiwi"];
            console.log(fruechte);
            console.log(fruechte.length);
        """, output = """
            [ 'Apfel', 'Birne', 'Kiwi' ]
            3
        """)
        text("Wie bei Strings hat jedes Element einen **Index**, beginnend bei 0. Über den Index kannst du ein Element lesen **und ändern**.")
        code("""
            const fruechte = ["Apfel", "Birne", "Kiwi"];
            console.log(fruechte[0]);
            console.log(fruechte.at(-1));
            fruechte[1] = "Mango";
            console.log(fruechte);
        """, output = """
            Apfel
            Kiwi
            [ 'Apfel', 'Mango', 'Kiwi' ]
        """)
        tip("Auch mit `const` kannst du den **Inhalt** eines Arrays ändern. `const` verhindert nur, dass die Variable ein ganz anderes Array bekommt.")
        text("Ein Array kann alles enthalten – Zahlen, Texte, Wahrheitswerte und sogar andere Arrays. Ein leeres Array schreibst du als `[]`.")
        code("""
            const gemischt = [42, "Hallo", true];
            const leer = [];
            console.log(gemischt, leer.length);
        """, output = "[ 42, 'Hallo', true ] 0")

        output("""
            const zahlen = [10, 20, 30];
            console.log(zahlen[1]);
        """, "20", "10", "30", "[ 10, 20, 30 ]")
        output("""console.log(["a", "b", "c", "d"].length);""", "4", "3", "5", "d")
        typeOutput("""
            const tiere = ["Hund", "Katze", "Maus"];
            console.log(tiere.at(-1));
        """, "Maus")
        output("""
            const farben = ["rot", "grün"];
            farben[0] = "blau";
            console.log(farben);
        """, "[ 'blau', 'grün' ]", "[ 'rot', 'grün' ]", "[ 'blau', 'rot', 'grün' ]", "Fehler: farben ist const")
        output("""
            const a = [1, 2, 3];
            console.log(a[3]);
        """, "undefined", "3", "Fehler", "null")
        truth("Bei einem `const`-Array kann man einzelne Elemente ändern.", true)
        fill("Gib das **letzte** Element aus, egal wie lang das Array ist.", """
            const noten = [2, 1, 3, 2];
            console.log(noten[noten.length ?? 1]);
        """, answers = listOf("-"), distractors = listOf("+", "*", "/"), output = "2")
        write("Lege ein Array `wochentage` mit den Texten `\"Mo\"`, `\"Di\"` und `\"Mi\"` an. Gib das Array und danach seine Länge aus.",
            solution = """
                const wochentage = ["Mo", "Di", "Mi"];
                console.log(wochentage);
                console.log(wochentage.length);
            """,
            output = """
                [ 'Mo', 'Di', 'Mi' ]
                3
            """,
            mustContain = listOf("["),
            hint = "`const wochentage = [\"Mo\", \"Di\", \"Mi\"];` – dann zweimal `console.log`.")
    }

    lesson("Arrays verändern", "Hinzufügen, entfernen und suchen", LessonIcon.BOX) {
        text("""
            Arrays wachsen und schrumpfen mit diesen Methoden:
            - `push(x)` hängt hinten an, `pop()` nimmt hinten weg
            - `unshift(x)` fügt vorne ein, `shift()` nimmt vorne weg
        """)
        code("""
            const liste = ["Brot"];
            liste.push("Milch");
            liste.push("Eier");
            liste.unshift("Kaffee");
            console.log(liste);
            const letztes = liste.pop();
            console.log(letztes, liste);
        """, output = """
            [ 'Kaffee', 'Brot', 'Milch', 'Eier' ]
            Eier [ 'Kaffee', 'Brot', 'Milch' ]
        """)
        text("Zum Suchen: `includes(x)` prüft, ob `x` vorkommt. `indexOf(x)` liefert die Position oder `-1`.")
        code("""
            const team = ["Ada", "Ben", "Cleo"];
            console.log(team.includes("Ben"));
            console.log(team.indexOf("Cleo"));
            console.log(team.indexOf("Zoe"));
        """, output = """
            true
            2
            -1
        """)
        text("Mit `splice(index, anzahl)` entfernst du Elemente an einer beliebigen Stelle:")
        code("""
            const zahlen = [10, 20, 30, 40];
            zahlen.splice(1, 2);
            console.log(zahlen);
        """, output = "[ 10, 40 ]")
        tip("`push`, `pop`, `shift`, `unshift` und `splice` verändern das Array selbst. Viele andere Methoden liefern dagegen ein neues Array.")

        output("""
            const a = [1, 2];
            a.push(3);
            console.log(a);
        """, "[ 1, 2, 3 ]", "[ 3, 1, 2 ]", "[ 1, 2 ]", "3")
        output("""
            const a = ["x", "y", "z"];
            const weg = a.shift();
            console.log(weg, a.length);
        """, "x 2", "z 2", "x 3", "y 2")
        typeOutput("""
            const stapel = [1, 2, 3];
            stapel.pop();
            stapel.pop();
            console.log(stapel);
        """, "[ 1 ]")
        match(
            "push()" to "hinten anhängen",
            "pop()" to "hinten entfernen",
            "unshift()" to "vorne einfügen",
            "shift()" to "vorne entfernen",
            "includes()" to "enthalten?",
        )
        output("""console.log([5, 6, 7].indexOf(8));""", "-1", "0", "undefined", "3")
        fill("Füge `\"Eis\"` **hinten** an die Liste an.", """
            const einkauf = ["Brot", "Käse"];
            einkauf.??("Eis");
            console.log(einkauf);
        """, answers = listOf("push"), distractors = listOf("unshift", "add", "append"), output = "[ 'Brot', 'Käse', 'Eis' ]")
        truth("`pop()` liefert das entfernte Element zurück.", true)
        write("Das Array `warteschlange` enthält `\"Ada\"`, `\"Ben\"` und `\"Cleo\"`. Die erste Person ist dran: Entferne sie von **vorne** und gib `Ada ist dran` aus. Füge danach `\"Dev\"` **hinten** an und gib das Array aus.",
            starter = """
                const warteschlange = ["Ada", "Ben", "Cleo"];
            """,
            solution = """
                const warteschlange = ["Ada", "Ben", "Cleo"];
                const naechste = warteschlange.shift();
                console.log(`${D}{naechste} ist dran`);
                warteschlange.push("Dev");
                console.log(warteschlange);
            """,
            output = """
                Ada ist dran
                [ 'Ben', 'Cleo', 'Dev' ]
            """,
            mustContain = listOf("shift", "push"),
            hint = "`shift()` holt die erste Person und liefert sie zurück, `push()` hängt hinten an.")
    }

    lesson("Arrays durchlaufen", "for…of und Schleifen mit Index", LessonIcon.LOOP) {
        text("Mit `for…of` gehst du alle Elemente eines Arrays nacheinander durch – ohne dich um den Index zu kümmern.")
        code("""
            const tiere = ["Hund", "Katze", "Schlange"];
            for (const tier of tiere) {
              console.log("Ich mag", tier);
            }
        """, output = """
            Ich mag Hund
            Ich mag Katze
            Ich mag Schlange
        """)
        text("Brauchst du auch die Position, nimm eine klassische `for`-Schleife mit Index:")
        code("""
            const plaetze = ["Ada", "Ben", "Cleo"];
            for (let i = 0; i < plaetze.length; i++) {
              console.log(`${D}{i + 1}. ${D}{plaetze[i]}`);
            }
        """, output = """
            1. Ada
            2. Ben
            3. Cleo
        """)
        text("Typische Aufgaben mit Schleifen: Summe bilden, Größtes finden, Treffer zählen.")
        code("""
            const punkte = [7, 12, 3, 9];
            let summe = 0;
            let maximum = punkte[0];
            for (const p of punkte) {
              summe += p;
              if (p > maximum) maximum = p;
            }
            console.log(summe, maximum);
            console.log(Math.max(...punkte));
        """, output = """
            31 12
            12
        """)
        tip("`Math.max(...array)` findet das Maximum direkt. Die drei Punkte `...` packen das Array aus – mehr dazu später.")

        output("""
            for (const z of [3, 1, 2]) {
              console.log(z * 2);
            }
        """, "6\n2\n4", "3\n1\n2", "12", "2\n4\n6")
        typeOutput("""
            let summe = 0;
            for (const n of [5, 10, 15]) {
              summe += n;
            }
            console.log(summe);
        """, "30")
        output("""
            const woerter = ["Ja", "Nein", "Vielleicht"];
            let lang = 0;
            for (const w of woerter) {
              if (w.length > 3) lang++;
            }
            console.log(lang);
        """, "2", "1", "3", "0")
        choice("Was liefert `for (const x of liste)` in jeder Runde?", "Das nächste Element", "Den Index", "Die Länge", "Das ganze Array")
        fill("Gehe alle Namen durch.", """
            const namen = ["Ada", "Ben"];
            for (const name ?? namen) {
              console.log(name);
            }
        """, answers = listOf("of"), distractors = listOf("in", "from", "at"), output = "Ada\nBen",
            explain = "`for…in` liefert bei Arrays die Indizes (als Text) – für die Werte nimmst du `for…of`.")
        truth("Mit `for…of` bekommst du automatisch den Index jedes Elements.", false)
        write("Zähle, wie viele Zahlen im Array `werte` **größer als 10** sind, und gib die Anzahl aus.",
            starter = """
                const werte = [4, 15, 8, 23, 11, 2];
            """,
            solution = """
                const werte = [4, 15, 8, 23, 11, 2];
                let anzahl = 0;
                for (const w of werte) {
                  if (w > 10) anzahl++;
                }
                console.log(anzahl);
            """,
            output = "3",
            mustContain = listOf("for"),
            hint = "Starte einen Zähler bei 0 und erhöhe ihn in der Schleife, wenn `w > 10` ist.")
        write("Berechne den **Durchschnitt** der Noten im Array `noten` und gib ihn aus.",
            starter = """
                const noten = [2, 3, 1, 2, 2];
            """,
            solution = """
                const noten = [2, 3, 1, 2, 2];
                let summe = 0;
                for (const n of noten) {
                  summe += n;
                }
                console.log(summe / noten.length);
            """,
            output = "2",
            mustContain = listOf("length"),
            hint = "Erst alles aufsummieren, dann durch `noten.length` teilen.")
    }

    lesson("Kopieren, Verbinden, Umdrehen", "slice, join, reverse und verschachtelte Arrays", LessonIcon.MAGIC) {
        text("Arrays werden **nicht** kopiert, wenn du sie einer anderen Variable zuweist – beide Namen zeigen auf **dasselbe** Array!")
        code("""
            const original = [1, 2, 3];
            const auchOriginal = original;
            auchOriginal.push(4);
            console.log(original);
        """, output = "[ 1, 2, 3, 4 ]")
        text("Eine echte Kopie bekommst du mit `[...array]` oder `array.slice()`. `slice(start, ende)` schneidet wie bei Strings ein Stück heraus.")
        code("""
            const original = [1, 2, 3];
            const kopie = [...original];
            kopie.push(4);
            console.log(original, kopie);
            console.log([10, 20, 30, 40].slice(1, 3));
        """, output = """
            [ 1, 2, 3 ] [ 1, 2, 3, 4 ]
            [ 20, 30 ]
        """)
        text("`join()` macht aus einem Array einen String, `concat()` oder `[...a, ...b]` verbinden Arrays, `reverse()` dreht die Reihenfolge um.")
        code("""
            const a = ["x", "y"];
            const b = ["z"];
            console.log([...a, ...b]);
            console.log(a.concat(b).join(" + "));
            console.log([1, 2, 3].reverse());
        """, output = """
            [ 'x', 'y', 'z' ]
            x + y + z
            [ 3, 2, 1 ]
        """)
        text("Arrays können wieder Arrays enthalten – praktisch für Tabellen oder Spielfelder:")
        code("""
            const feld = [
              ["X", "O", "X"],
              ["O", "X", "O"],
            ];
            console.log(feld[1][2]);
            console.log(feld[0].join(" | "));
        """, output = """
            O
            X | O | X
        """)
        warn("`reverse()` verändert das Array selbst. Willst du das Original behalten, drehe eine Kopie um: `[...a].reverse()`.")

        output("""
            const a = [1, 2];
            const b = a;
            b.push(3);
            console.log(a.length);
        """, "3", "2", "1", "Fehler", explain = "`b` ist kein neues Array, sondern nur ein zweiter Name für dasselbe Array.")
        output("""console.log(["a", "b", "c"].join(""));""", "abc", "a,b,c", "a b c", "[ 'abc' ]")
        typeOutput("""console.log([1, 2, 3, 4, 5].slice(-2));""", "[ 4, 5 ]")
        output("""
            const raster = [[1, 2], [3, 4]];
            console.log(raster[1][0]);
        """, "3", "2", "1", "[ 3, 4 ]")
        choice("Wie erstellst du eine **unabhängige Kopie** von `liste`?", "[...liste]", "liste", "new liste", "liste.copy", mono = true)
        fill("Verbinde die Wörter mit Bindestrichen.", """console.log(["Hiss", "Bit", "App"].??("-"));""",
            answers = listOf("join"), distractors = listOf("concat", "split", "push"), output = "Hiss-Bit-App")
        truth("`const b = a;` erzeugt eine Kopie des Arrays `a`.", false)
        write("Gib das Array `buchstaben` **rückwärts** als String aus (`ytnoM`), ohne das Original zu verändern. Gib danach das unveränderte Original aus.",
            starter = """
                const buchstaben = ["M", "o", "n", "t", "y"];
            """,
            solution = """
                const buchstaben = ["M", "o", "n", "t", "y"];
                console.log([...buchstaben].reverse().join(""));
                console.log(buchstaben);
            """,
            output = """
                ytnoM
                [ 'M', 'o', 'n', 't', 'y' ]
            """,
            mustContain = listOf("reverse", "join"),
            hint = "Kopiere zuerst mit `[...buchstaben]`, drehe die Kopie mit `reverse()` um und verbinde sie mit `join(\"\")`.")
    }

    checkpoint()
}
