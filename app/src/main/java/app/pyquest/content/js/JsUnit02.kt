package app.pyquest.content.js

import app.pyquest.content.CourseBuilder
import app.pyquest.data.LessonIcon

fun CourseBuilder.jsUnit02() = unit("Variablen & Datentypen", "Werte speichern mit let und const") {
    lesson("Variablen mit let", "Werte unter einem Namen speichern", LessonIcon.BOX) {
        text("Eine **Variable** ist wie eine beschriftete Box: Du legst einen Wert hinein und holst ihn später über den Namen wieder heraus. Mit `let` legst du eine neue Variable an.")
        code("""
            let name = "Monty";
            let alter = 5;
            console.log(name);
            console.log(alter + 1);
        """, output = """
            Monty
            6
        """)
        text("Das `=` bedeutet **„speichere“** – nicht „ist gleich“. Eine `let`-Variable kann später einen neuen Wert bekommen. Dabei schreibst du `let` **nicht** noch einmal.")
        code("""
            let punkte = 10;
            punkte = 15;
            punkte = punkte + 5;
            console.log(punkte);
        """, output = "20")
        tip("Kurzschreibweisen: `punkte += 5` ist dasselbe wie `punkte = punkte + 5`. Und `punkte++` erhöht um genau 1.")
        code("""
            let leben = 3;
            leben -= 1;
            leben++;
            console.log(leben);
        """, output = "3")

        choice("Womit legst du eine neue Variable an, deren Wert sich ändern darf?", "let", "var", "make", "new", mono = true,
            explain = "`var` gibt es auch – aber das ist der alte Weg mit Tücken. Heute nimmt man `let` und `const`.")
        output("""
            let x = 4;
            x = x * 2;
            console.log(x);
        """, "8", "4", "x * 2", "16")
        truth("Wenn du einer Variable einen neuen Wert gibst, schreibst du wieder `let` davor.", false,
            explain = "`let` steht nur beim ersten Anlegen. Danach reicht `name = neuerWert;`.")
        typeOutput("""
            let a = 3;
            let b = a + 2;
            a = 10;
            console.log(b);
        """, "5", explain = "`b` bekommt den Wert 5, als die Zeile ausgeführt wird. Dass sich `a` danach ändert, ändert `b` nicht mehr.")
        fill("Erhöhe den Zähler um 1.", """
            let zaehler = 7;
            zaehler??;
            console.log(zaehler);
        """, answers = listOf("++"), distractors = listOf("--", "+1", "**"), output = "8")
        output("""
            let kontostand = 100;
            kontostand -= 30;
            kontostand += 5;
            console.log(kontostand);
        """, "75", "135", "65", "100")
        arrange("Lege die Variable **preis** mit 4 an, verdopple sie und gib sie aus.", """
            let preis = 4;
            preis = preis * 2;
            console.log(preis);
        """, output = "8")
        write("Lege eine Variable `stadt` mit dem Wert `\"Berlin\"` an und gib sie aus.",
            solution = """
                let stadt = "Berlin";
                console.log(stadt);
            """,
            output = "Berlin",
            mustContain = listOf("stadt"),
            hint = "`let stadt = \"Berlin\";` und danach `console.log(stadt);`")
    }

    lesson("const und gute Namen", "Feste Werte und Regeln für Namen", LessonIcon.BOX) {
        text("Mit `const` legst du eine **Konstante** an: Der Wert wird einmal festgelegt und kann danach nicht mehr ersetzt werden. Profis nehmen `const`, wann immer es geht – und `let` nur, wenn sich der Wert wirklich ändern soll.")
        code("""
            const pi = 3.14159;
            const begruessung = "Hallo";
            console.log(begruessung, pi);
        """, output = "Hallo 3.14159")
        text("Versuchst du, einer `const`-Variable einen neuen Wert zu geben, bricht das Programm mit einem Fehler ab:")
        code("""
            const tage = 7;
            tage = 8;
        """, fails = true)
        text("""
            Regeln für Variablennamen:
            - Buchstaben, Ziffern, `_` und `${D}` sind erlaubt – aber keine Leerzeichen
            - Ein Name darf **nicht mit einer Ziffer** beginnen
            - Groß-/Kleinschreibung zählt: `name` und `Name` sind verschiedene Variablen
            - Wörter wie `let`, `if` oder `class` sind reserviert
        """)
        tip("In JavaScript schreibt man zusammengesetzte Namen im **camelCase**: `anzahlSpieler`, `maxPunkte`, `istFertig` – jedes neue Wort beginnt groß.")
        warn("Umlaute wie in `größe` funktionieren zwar, sind aber unüblich. Schreibe lieber `groesse`.")

        choice("Welcher Variablenname ist **gültig**?", "spielerName", "2spieler", "spieler name", "let", mono = true)
        truth("Den Wert einer `const`-Variable kann man später ersetzen.", false)
        choice("Was passiert bei `const x = 1;` und danach `x = 2;`?",
            "Es gibt einen Fehler (TypeError).",
            "x hat danach den Wert 2.",
            "x hat danach den Wert 3.",
            "Die zweite Zeile wird ignoriert.")
        choice("Welcher Name folgt dem üblichen **camelCase**?", "highScore", "high_score", "HighScore", "highscore", mono = true,
            explain = "camelCase: erstes Wort klein, jedes weitere Wort beginnt mit einem Großbuchstaben.")
        output("""
            const wort = "Kaffee";
            let anzahl = 2;
            anzahl = anzahl + 1;
            console.log(anzahl, wort);
        """, "3 Kaffee", "2 Kaffee", "3Kaffee", "anzahl wort")
        fill("Lege eine Konstante an, die sich nie ändern soll.", """
            ?? stunden = 24;
            console.log(stunden);
        """, answers = listOf("const"), distractors = listOf("constant", "fix", "Const"), output = "24")
        truth("`farbe` und `Farbe` sind in JavaScript zwei verschiedene Variablen.", true)
        write("Speichere deinen Lieblingsnamen für eine Schlange in der Konstante `schlange` (z. B. `\"Monty\"`) und gib aus: `Meine Schlange heißt` und dahinter den Namen.",
            solution = """
                const schlange = "Monty";
                console.log("Meine Schlange heißt", schlange);
            """,
            test = """
                if (typeof schlange !== "string") throw new Error("Lege die Konstante schlange mit einem Text an.");
            """,
            mustContain = listOf("const schlange", "console.log"),
            hint = "`const schlange = \"Monty\";` und dann `console.log(\"Meine Schlange heißt\", schlange);`")
    }

    lesson("Datentypen", "Zahlen, Texte, Wahrheitswerte und Nichts", LessonIcon.BRAIN) {
        text("""
            Jeder Wert hat einen **Typ**. Die wichtigsten:
            - `number` – Zahlen wie `42` oder `3.5`
            - `string` – Texte wie `"Hallo"`
            - `boolean` – Wahrheitswerte: `true` oder `false`
            - `undefined` – „noch kein Wert“
            - `null` – „absichtlich leer“
        """)
        text("Mit `typeof` fragst du nach dem Typ eines Wertes:")
        code("""
            console.log(typeof 42);
            console.log(typeof "42");
            console.log(typeof true);
            let leer;
            console.log(leer, typeof leer);
        """, output = """
            number
            string
            boolean
            undefined undefined
        """)
        tip("Eine Variable, der du noch keinen Wert gegeben hast, enthält `undefined`. Mit `null` sagst du dagegen bewusst: „Hier ist (noch) nichts.“")
        warn("`typeof null` liefert `\"object\"` – ein berühmter alter Fehler in JavaScript, der aus Rücksicht auf alte Webseiten nie behoben wurde.")
        code("""
            let gewinner = null;
            console.log(gewinner);
            console.log(typeof null);
        """, output = """
            null
            object
        """)

        output("console.log(typeof 3.14);", "number", "float", "string", "decimal")
        output("console.log(typeof \"true\");", "string", "boolean", "true", "undefined",
            explain = "Durch die Anführungszeichen ist `\"true\"` ein Text, kein Wahrheitswert.")
        match(
            "42" to "number",
            "\"Hallo\"" to "string",
            "false" to "boolean",
            "undefined" to "noch kein Wert",
            "null" to "absichtlich leer",
        )
        typeOutput("""
            let x;
            console.log(x);
        """, "undefined")
        choice("Welche Werte hat der Typ `boolean`?", "true und false", "0 und 1", "ja und nein", "wahr und falsch", mono = false)
        truth("`typeof null` ergibt `\"null\"`.", false, explain = "Überraschung: Es ergibt `\"object\"` – ein historischer Fehler in JavaScript.")
        fill("Finde heraus, welchen Typ der Wert hat.", """console.log(?? "Monty");""", answers = listOf("typeof"), distractors = listOf("type", "typ", "instanceof"), output = "string")
        write("Gib nacheinander den Typ der Werte `7`, `\"7\"` und `false` aus – jeweils mit `typeof` in einer eigenen Zeile.",
            solution = """
                console.log(typeof 7);
                console.log(typeof "7");
                console.log(typeof false);
            """,
            output = """
                number
                string
                boolean
            """,
            mustContain = listOf("typeof"),
            hint = "Dreimal `console.log(typeof ...);`")
    }

    lesson("Umwandeln & NaN", "Wenn Texte und Zahlen sich treffen", LessonIcon.MAGIC) {
        text("Vorsicht, typische JavaScript-Falle: Trifft `+` auf einen **Text**, wird nicht gerechnet, sondern **aneinandergehängt**. Andere Rechenzeichen wandeln Texte dagegen in Zahlen um.")
        code("""
            console.log("5" + 3);
            console.log("5" - 3);
            console.log("5" * "2");
        """, output = """
            53
            2
            10
        """)
        text("Besser ist es, selbst umzuwandeln: `Number(text)` macht aus einem Text eine Zahl, `String(zahl)` aus einer Zahl einen Text.")
        code("""
            const eingabe = "42";
            const zahl = Number(eingabe);
            console.log(zahl + 8);
            console.log(String(7) + 7);
        """, output = """
            50
            77
        """)
        text("Kann ein Text nicht in eine Zahl umgewandelt werden, entsteht **`NaN`** – „Not a Number“. Mit `Number.isNaN()` prüfst du darauf.")
        code("""
            const x = Number("Hallo");
            console.log(x);
            console.log(Number.isNaN(x));
            console.log(parseInt("12 Äpfel"));
        """, output = """
            NaN
            true
            12
        """)
        tip("`parseInt()` liest eine ganze Zahl vom Anfang eines Textes, `parseFloat()` auch Kommazahlen: `parseFloat(\"3.5 kg\")` ergibt `3.5`.")
        tip("Kommazahlen runden: `(2 / 3).toFixed(2)` ergibt den Text `\"0.67\"`.")

        output("console.log(\"2\" + 2);", "22", "4", "\"4\"", "NaN")
        output("console.log(\"10\" - 4);", "6", "104", "NaN", "10 - 4",
            explain = "`-` kann nur rechnen – JavaScript wandelt `\"10\"` deshalb in die Zahl 10 um.")
        output("console.log(Number(\"abc\"));", "NaN", "0", "abc", "undefined")
        typeOutput("console.log(Number(\"3\") + 4);", "7")
        choice("Was bedeutet **NaN**?", "Not a Number – keine gültige Zahl", "Null and Nothing", "New array Number", "Eine sehr große Zahl")
        truth("`\"3\" * \"4\"` ergibt in JavaScript `12`.", true, explain = "`*` wandelt beide Texte in Zahlen um.")
        fill("Wandle den Text in eine Zahl um, damit gerechnet wird.", """
            const alter = "16";
            console.log(??(alter) + 1);
        """, answers = listOf("Number"), distractors = listOf("String", "number", "Zahl"), output = "17")
        output("console.log((10 / 3).toFixed(1));", "3.3", "3.33", "3", "3.4")
        write("In `preisText` steht der Preis `\"19.99\"` als Text. Wandle ihn in eine Zahl um und gib den Preis für **3 Stück** aus, gerundet auf 2 Nachkommastellen (`59.97`).",
            starter = """
                const preisText = "19.99";
            """,
            solution = """
                const preisText = "19.99";
                const preis = Number(preisText);
                console.log((preis * 3).toFixed(2));
            """,
            output = "59.97",
            mustContain = listOf("toFixed"),
            hint = "`Number(preisText) * 3` rechnet, `.toFixed(2)` rundet auf zwei Stellen.")
    }

    lesson("Template-Strings", "Texte mit eingebauten Werten", LessonIcon.CHAT) {
        text("Texte mit `+` zusammenzubauen wird schnell unübersichtlich. Eleganter sind **Template-Strings**: Sie stehen zwischen **Backticks** `` ` `` und können mit `${D}{...}` Werte direkt einbauen.")
        code("""
            const name = "Ada";
            const alter = 36;
            console.log(`${D}{name} ist ${D}{alter} Jahre alt.`);
        """, output = "Ada ist 36 Jahre alt.")
        text("In `${D}{...}` darf jeder Ausdruck stehen – auch Rechnungen:")
        code("""
            const preis = 4;
            const menge = 3;
            console.log(`Gesamt: ${D}{preis * menge} Euro`);
        """, output = "Gesamt: 12 Euro")
        text("Template-Strings dürfen außerdem über **mehrere Zeilen** gehen:")
        code("""
            const gedicht = `Rosen sind rot,
            Veilchen sind blau.`;
            console.log(gedicht);
        """, output = """
            Rosen sind rot,
            Veilchen sind blau.
        """)
        tip("Den Backtick `` ` `` findest du auf vielen Handytastaturen bei den Sonderzeichen – oder unten in Hissbits Symbolleiste.")

        output("""
            const tier = "Katze";
            console.log(`Die ${D}{tier} schläft.`);
        """, "Die Katze schläft.", "Die ${D}{tier} schläft.", "Die tier schläft.", "Die \"Katze\" schläft.")
        output("console.log(`2 + 3 = ${D}{2 + 3}`);", "2 + 3 = 5", "2 + 3 = 2 + 3", "5 = 5", "2 + 3 = 23")
        choice("Zwischen welchen Zeichen steht ein Template-String?", "Backticks `` ` ``", "Anführungszeichen \"", "Hochkommas '", "Klammern ( )")
        truth("In `\"Hallo ${D}{name}\"` (mit normalen Anführungszeichen) wird der Name eingesetzt.", false,
            explain = "Nur Template-Strings mit Backticks setzen `${D}{...}` ein. In normalen Strings bleibt der Text genau so stehen.")
        fill("Baue den Namen in den Text ein.", """
            const held = "Monty";
            console.log(`Hallo, ??!`);
        """, answers = listOf("${D}{held}"), distractors = listOf("{held}", "held", "${D}held"), output = "Hallo, Monty!")
        typeOutput("""
            const a = 6;
            console.log(`${D}{a} * 7 = ${D}{a * 7}`);
        """, "6 * 7 = 42")
        arrange("Gib mit einem Template-String `Level 3 von 10` aus.", """
            const level = 3;
            const max = 10;
            console.log(`Level ${D}{level} von ${D}{max}`);
        """, output = "Level 3 von 10")
        write("Lege die Konstanten `produkt` (`\"Pizza\"`) und `preis` (`8`) an. Gib mit **einem Template-String** aus: `Eine Pizza kostet 8 Euro.`",
            solution = """
                const produkt = "Pizza";
                const preis = 8;
                console.log(`Eine ${D}{produkt} kostet ${D}{preis} Euro.`);
            """,
            output = "Eine Pizza kostet 8 Euro.",
            mustContain = listOf("`", "${D}{"),
            hint = "Backticks statt Anführungszeichen und die Werte mit `${D}{produkt}` und `${D}{preis}` einsetzen.")
    }

    checkpoint()
}
