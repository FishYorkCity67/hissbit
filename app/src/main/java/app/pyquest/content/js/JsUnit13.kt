package app.pyquest.content.js

import app.pyquest.content.CourseBuilder
import app.pyquest.data.LessonIcon

fun CourseBuilder.jsUnit13() = unit("Nützliche Werkzeuge", "Map, Set, Spread, JSON, Math und Datum") {
    lesson("Map & Set", "Schlüssel-Wert-Listen und Mengen", LessonIcon.BOX) {
        text("Ein **Set** ist eine Sammlung ohne Doppelte. Perfekt, um Duplikate zu entfernen oder schnell zu prüfen, ob etwas dabei ist.")
        code("""
            const tags = new Set(["js", "web", "js", "code", "web"]);
            console.log(tags);
            console.log(tags.size, tags.has("code"));
            tags.add("neu");
            tags.delete("web");
            console.log([...tags]);
        """, output = """
            Set(3) { 'js', 'web', 'code' }
            3 true
            [ 'js', 'code', 'neu' ]
        """)
        text("Eine **Map** speichert Paare aus Schlüssel und Wert – ähnlich wie ein Objekt, aber mit eigenen Methoden: `set`, `get`, `has`, `delete` und `size`. Schlüssel dürfen beliebige Werte sein.")
        code("""
            const telefonbuch = new Map();
            telefonbuch.set("Ada", "0171 123");
            telefonbuch.set("Ben", "0160 456");
            console.log(telefonbuch.get("Ada"));
            console.log(telefonbuch.has("Cleo"), telefonbuch.size);
            for (const [name, nummer] of telefonbuch) {
              console.log(`${D}{name}: ${D}{nummer}`);
            }
        """, output = """
            0171 123
            false 2
            Ada: 0171 123
            Ben: 0160 456
        """)
        tip("Duplikate aus einem Array entfernen in einer Zeile: `[...new Set(array)]`.")

        output("""console.log(new Set([1, 1, 2, 2, 3]).size);""", "3", "5", "2", "6")
        output("""console.log([...new Set(["a", "b", "a"])]);""", "[ 'a', 'b' ]", "[ 'a', 'b', 'a' ]", "Set(2) { 'a', 'b' }", "2")
        typeOutput("""
            const m = new Map([["x", 10], ["y", 20]]);
            console.log(m.get("y"));
        """, "20")
        output("""
            const s = new Set();
            s.add(5);
            s.add(5);
            console.log(s.has(5), s.size);
        """, "true 1", "true 2", "false 0", "5 1")
        match(
            "set()" to "Map: Paar speichern",
            "get()" to "Map: Wert holen",
            "add()" to "Set: Wert hinzufügen",
            "has()" to "enthalten?",
            "size" to "Anzahl der Einträge",
        )
        fill("Zähle, wie viele **verschiedene** Farben es gibt.", """
            const farben = ["rot", "blau", "rot", "grün", "blau"];
            console.log(new ??(farben).size);
        """, answers = listOf("Set"), distractors = listOf("Map", "Array", "Object"), output = "3")
        truth("Ein Set kann denselben Wert mehrmals enthalten.", false)
        write("Zähle mit einer `Map`, wie oft jedes Wort im Array `woerter` vorkommt, und gib danach jede Zeile als `<Wort>: <Anzahl>` aus.",
            starter = """
                const woerter = ["ja", "nein", "ja", "vielleicht", "ja", "nein"];
            """,
            solution = """
                const woerter = ["ja", "nein", "ja", "vielleicht", "ja", "nein"];
                const zaehler = new Map();
                for (const w of woerter) {
                  zaehler.set(w, (zaehler.get(w) ?? 0) + 1);
                }
                for (const [wort, anzahl] of zaehler) {
                  console.log(`${D}{wort}: ${D}{anzahl}`);
                }
            """,
            output = """
                ja: 3
                nein: 2
                vielleicht: 1
            """,
            mustContain = listOf("Map"),
            hint = "`zaehler.set(w, (zaehler.get(w) ?? 0) + 1)` – beim ersten Mal liefert `get` `undefined`, daher der Ersatzwert 0.")
    }

    lesson("Spread & Rest", "Die drei Punkte ...", LessonIcon.MAGIC) {
        text("Die drei Punkte `...` haben zwei Aufgaben. Als **Spread** packen sie ein Array oder Objekt **aus** – zum Beispiel, um Arrays zu verbinden oder Werte einzeln an eine Funktion zu geben.")
        code("""
            const a = [1, 2];
            const b = [3, 4];
            console.log([...a, ...b, 5]);
            console.log(Math.max(...b));
            const basis = { farbe: "rot" };
            console.log({ ...basis, groesse: "L" });
        """, output = """
            [ 1, 2, 3, 4, 5 ]
            4
            { farbe: 'rot', groesse: 'L' }
        """)
        text("Als **Rest-Parameter** sammeln sie beliebig viele Argumente in einem Array. So schreibst du Funktionen, die mit beliebig vielen Werten klarkommen:")
        code("""
            function summe(...zahlen) {
              return zahlen.reduce((s, z) => s + z, 0);
            }
            console.log(summe(1, 2), summe(5, 5, 5, 5));

            function team(kapitaen, ...rest) {
              console.log("Kapitän:", kapitaen, "| Rest:", rest);
            }
            team("Ada", "Ben", "Cleo");
        """, output = """
            3 20
            Kapitän: Ada | Rest: [ 'Ben', 'Cleo' ]
        """)
        tip("Faustregel: Steht `...` beim **Aufruf** oder in einem Array/Objekt, ist es Spread (auspacken). Steht es in der **Parameterliste**, ist es Rest (einsammeln).")

        output("""console.log([..."Hey"]);""", "[ 'H', 'e', 'y' ]", "[ 'Hey' ]", "Hey", "[ ...'Hey' ]")
        output("""
            function zaehle(...werte) {
              return werte.length;
            }
            console.log(zaehle("a", "b", "c"));
        """, "3", "1", "0", "[ 'a', 'b', 'c' ]")
        typeOutput("""
            const x = [10, 20];
            const y = [...x, 30];
            console.log(y.length);
        """, "3")
        output("""
            const o = { a: 1, b: 2 };
            const p = { ...o, b: 3, c: 4 };
            console.log(p);
        """, "{ a: 1, b: 3, c: 4 }", "{ a: 1, b: 2, c: 4 }", "{ b: 3, c: 4 }", "{ a: 1, b: 2, b: 3, c: 4 }")
        choice("Was macht `...` in `function f(...args)`?", "Sammelt alle Argumente in einem Array", "Packt ein Array aus", "Kopiert die Funktion", "Ist ein Syntaxfehler")
        fill("Finde das Minimum des Arrays.", """
            const temps = [7, -2, 12, 3];
            console.log(Math.min(??temps));
        """, answers = listOf("..."), distractors = listOf("*", "&", ".."), output = "-2")
        truth("`[...liste]` erzeugt eine Kopie von `liste`.", true)
        write("Schreibe eine Funktion `durchschnitt(...zahlen)`, die den Durchschnitt beliebig vieler Zahlen zurückgibt.",
            solution = """
                function durchschnitt(...zahlen) {
                  return zahlen.reduce((s, z) => s + z, 0) / zahlen.length;
                }
            """,
            test = """
                if (durchschnitt(2, 4) !== 3) throw new Error("durchschnitt(2, 4) sollte 3 sein");
                if (durchschnitt(1, 2, 3, 4, 5) !== 3) throw new Error("durchschnitt(1, 2, 3, 4, 5) sollte 3 sein");
                if (durchschnitt(10) !== 10) throw new Error("durchschnitt(10) sollte 10 sein");
                console.log("✅ Alle Tests bestanden!");
            """,
            mustContain = listOf("..."),
            hint = "Mit `...zahlen` bekommst du ein Array – Summe mit `reduce`, dann durch `zahlen.length` teilen.")
    }

    lesson("JSON", "Daten als Text speichern und lesen", LessonIcon.FILE) {
        text("**JSON** (JavaScript Object Notation) ist das Standardformat, um Daten als Text zu speichern oder zu verschicken – fast jede App im Internet benutzt es. `JSON.stringify()` macht aus Daten einen Text.")
        code("""
            const spieler = { name: "Ada", level: 7, items: ["Schwert", "Trank"] };
            const text = JSON.stringify(spieler);
            console.log(text);
            console.log(typeof text);
        """, output = """
            {"name":"Ada","level":7,"items":["Schwert","Trank"]}
            string
        """)
        text("Mit einem dritten Argument wird der Text schön eingerückt:")
        code("""
            console.log(JSON.stringify({ a: 1, b: [true, null] }, null, 2));
        """, output = """
            {
              "a": 1,
              "b": [
                true,
                null
              ]
            }
        """)
        text("`JSON.parse()` macht aus einem JSON-Text wieder echte Daten. Ist der Text kein gültiges JSON, gibt es einen `SyntaxError` – deshalb gehört `JSON.parse` oft in ein `try`.")
        code("""
            const daten = JSON.parse('{"stadt":"Berlin","einwohner":3700000}');
            console.log(daten.stadt, daten.einwohner);
            try {
              JSON.parse("{stadt: Berlin}");
            } catch (e) {
              console.log(e.name);
            }
        """, output = """
            Berlin 3700000
            SyntaxError
        """)
        warn("In JSON müssen Schlüssel und Texte in **doppelten** Anführungszeichen stehen. Funktionen und `undefined` werden beim Umwandeln einfach weggelassen.")
        tip("Mit `JSON.parse(JSON.stringify(obj))` bekommst du eine tiefe Kopie eines einfachen Objekts.")

        output("""console.log(JSON.stringify([1, "a", true]));""", "[1,\"a\",true]", "[ 1, 'a', true ]", "1,a,true", "\"[1,a,true]\"")
        output("""console.log(JSON.parse("[1, 2, 3]").length);""", "3", "9", "[1, 2, 3]", "undefined")
        typeOutput("""
            const o = JSON.parse('{"x": 5}');
            console.log(o.x * 2);
        """, "10")
        output("""console.log(JSON.stringify({ name: "Bo", f() {} }));""", "{\"name\":\"Bo\"}", "{\"name\":\"Bo\",\"f\":\"f() {}\"}", "{ name: 'Bo' }", "Fehler",
            explain = "Funktionen gehören nicht zu JSON und werden beim Umwandeln weggelassen.")
        choice("Was liefert `JSON.stringify(obj)`?", "Einen String", "Ein Objekt", "Ein Array", "Eine Zahl")
        fill("Mache aus dem JSON-Text wieder ein Objekt.", """
            const text = '{"tier":"Schlange"}';
            const obj = JSON.??(text);
            console.log(obj.tier);
        """, answers = listOf("parse"), distractors = listOf("stringify", "read", "toObject"), output = "Schlange")
        truth("In JSON dürfen Schlüssel in einfachen Anführungszeichen stehen.", false)
        write("""
            In `gespeichert` liegt ein Spielstand als JSON-Text. Lies ihn mit `JSON.parse` ein, erhöhe `punkte` um 50,
            hänge `"Schild"` an das Array `items` an und gib den neuen Spielstand mit `JSON.stringify` (ohne Einrückung) aus.
        """.trimIndent(),
            starter = """
                const gespeichert = '{"name":"Monty","punkte":100,"items":["Hut"]}';
            """,
            solution = """
                const gespeichert = '{"name":"Monty","punkte":100,"items":["Hut"]}';
                const stand = JSON.parse(gespeichert);
                stand.punkte += 50;
                stand.items.push("Schild");
                console.log(JSON.stringify(stand));
            """,
            output = """{"name":"Monty","punkte":150,"items":["Hut","Schild"]}""",
            mustContain = listOf("JSON.parse", "JSON.stringify"),
            hint = "`JSON.parse` → ändern → `JSON.stringify`.")
    }

    lesson("Math, Zufall & Datum", "Runden, würfeln, rechnen mit Daten", LessonIcon.CALC) {
        text("""
            Das Objekt `Math` hat viele Helfer:
            - `Math.round()` rundet kaufmännisch, `Math.floor()` ab, `Math.ceil()` auf
            - `Math.max()`/`Math.min()` finden das Größte/Kleinste
            - `Math.abs()` ist der Betrag, `Math.sqrt()` die Wurzel, `Math.PI` die Kreiszahl
        """)
        code("""
            console.log(Math.round(2.5), Math.floor(2.9), Math.ceil(2.1));
            console.log(Math.max(4, 9, 1), Math.abs(-7), Math.sqrt(81));
            console.log(Math.PI.toFixed(4));
        """, output = """
            3 2 3
            9 7 9
            3.1416
        """)
        text("`Math.random()` liefert eine zufällige Kommazahl von 0 (inklusive) bis 1 (exklusive). Für einen Würfel von 1 bis 6 multiplizierst du, rundest ab und addierst 1.")
        code("""
            const wurf = Math.floor(Math.random() * 6) + 1;
            console.log(wurf >= 1 && wurf <= 6, Number.isInteger(wurf));
            const namen = ["Ada", "Ben", "Cleo"];
            const zufall = namen[Math.floor(Math.random() * namen.length)];
            console.log(namen.includes(zufall));
        """, output = """
            true true
            true
        """)
        text("Kommazahlen sind im Computer nicht immer exakt – ein bekannter Effekt in fast allen Programmiersprachen. Zum Anzeigen rundest du mit `toFixed()`.")
        code("""
            console.log(0.1 + 0.2);
            console.log((0.1 + 0.2).toFixed(2));
        """, output = """
            0.30000000000000004
            0.30
        """)
        text("Mit `Date` arbeitest du mit Datum und Uhrzeit. Achtung: Die Monate zählen **ab 0** – Januar ist 0, Dezember ist 11!")
        code("""
            const geburtstag = new Date(2010, 4, 17);
            console.log(geburtstag.getFullYear(), geburtstag.getMonth() + 1, geburtstag.getDate());
            const wochentage = ["So", "Mo", "Di", "Mi", "Do", "Fr", "Sa"];
            console.log(wochentage[geburtstag.getDay()]);
            const silvester = new Date(2025, 11, 31);
            const tage = Math.round((silvester - new Date(2025, 11, 24)) / 86400000);
            console.log(`Von Heiligabend bis Silvester: ${D}{tage} Tage`);
        """, output = """
            2010 5 17
            Mo
            Von Heiligabend bis Silvester: 7 Tage
        """)
        tip("`new Date()` ohne Argumente liefert den aktuellen Zeitpunkt. Zieht man zwei Daten voneinander ab, erhält man die Differenz in Millisekunden.")

        output("console.log(Math.round(4.4), Math.round(4.6));", "4 5", "4 4", "5 5", "4.4 4.6")
        output("console.log(Math.ceil(7.01));", "8", "7", "7.01", "7.1")
        typeOutput("console.log(Math.max(3, 11, 7) - Math.min(3, 11, 7));", "8")
        choice("Welche Werte kann `Math.floor(Math.random() * 6) + 1` haben?", "1 bis 6", "0 bis 5", "0 bis 6", "1 bis 7")
        output("""
            const d = new Date(2024, 0, 15);
            console.log(d.getMonth());
        """, "0", "1", "15", "2024", explain = "Monate zählen ab 0 – der Januar ist Monat 0.")
        fill("Runde den Preis auf die nächste ganze Zahl **auf**.", """console.log(Math.??(19.01));""",
            answers = listOf("ceil"), distractors = listOf("floor", "round", "trunc"), output = "20")
        truth("`0.1 + 0.2 === 0.3` ergibt in JavaScript `true`.", false, explain = "Durch die Ungenauigkeit von Kommazahlen kommt 0.30000000000000004 heraus.")
        write("Schreibe eine Funktion `zufallZwischen(min, max)`, die eine zufällige **ganze** Zahl von `min` bis `max` (beide inklusive) zurückgibt.",
            solution = """
                function zufallZwischen(min, max) {
                  return Math.floor(Math.random() * (max - min + 1)) + min;
                }
            """,
            test = """
                const gesehen = new Set();
                for (let i = 0; i < 2000; i++) {
                  const z = zufallZwischen(3, 6);
                  if (!Number.isInteger(z) || z < 3 || z > 6) throw new Error("zufallZwischen(3, 6) lieferte " + z);
                  gesehen.add(z);
                }
                if (gesehen.size !== 4) throw new Error("Es sollten alle Zahlen von 3 bis 6 vorkommen, gesehen: " + [...gesehen].sort().join(", "));
                console.log("✅ Alle Tests bestanden!");
            """,
            mustContain = listOf("Math.random", "Math.floor"),
            hint = "`Math.floor(Math.random() * (max - min + 1)) + min`")
    }

    checkpoint()
}
