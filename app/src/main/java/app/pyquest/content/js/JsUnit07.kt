package app.pyquest.content.js

import app.pyquest.content.CourseBuilder
import app.pyquest.data.LessonIcon

fun CourseBuilder.jsUnit07() = unit("Funktionen", "Code verpacken und wiederverwenden") {
    lesson("Eigene Funktionen", "Code einen Namen geben", LessonIcon.CODE) {
        text("Eine **Funktion** ist ein Stück Code mit einem Namen. Du definierst sie einmal mit `function` und kannst sie danach beliebig oft **aufrufen**.")
        code("""
            function begruessen() {
              console.log("Hallo!");
              console.log("Schön, dass du da bist.");
            }

            begruessen();
            begruessen();
        """, output = """
            Hallo!
            Schön, dass du da bist.
            Hallo!
            Schön, dass du da bist.
        """)
        text("Funktionen können **Parameter** haben – Platzhalter für Werte, die du beim Aufruf mitgibst. Die mitgegebenen Werte heißen **Argumente**.")
        code("""
            function begruessen(name, emoji) {
              console.log(`Hallo ${D}{name} ${D}{emoji}`);
            }

            begruessen("Ada", "👋");
            begruessen("Monty", "🐍");
        """, output = """
            Hallo Ada 👋
            Hallo Monty 🐍
        """)
        tip("Ohne die runden Klammern wird die Funktion nicht ausgeführt: `begruessen` ist die Funktion selbst, erst `begruessen()` ruft sie auf.")
        warn("Gibst du ein Argument nicht mit, hat der Parameter den Wert `undefined`.")

        output("""
            function sag(text) {
              console.log(text + "!");
            }
            sag("Hey");
        """, "Hey!", "text!", "Hey", "Nichts")
        output("""
            function zeige() {
              console.log("A");
            }
            console.log("B");
            zeige();
        """, "B\nA", "A\nB", "A", "B", explain = "Die Definition allein gibt nichts aus – erst der Aufruf `zeige()` führt den Code aus.")
        choice("Wie rufst du die Funktion `starte` auf?", "starte();", "starte;", "call starte;", "function starte();", mono = true)
        typeOutput("""
            function wiederhole(wort, mal) {
              console.log(wort.repeat(mal));
            }
            wiederhole("ha", 3);
        """, "hahaha")
        fill("Definiere die Funktion.", """
            ?? jubel() {
              console.log("Juhu!");
            }
            jubel();
        """, answers = listOf("function"), distractors = listOf("def", "func", "fun"), output = "Juhu!")
        truth("Eine Funktion wird schon ausgeführt, wenn man sie definiert.", false)
        output("""
            function info(name, alter) {
              console.log(name, alter);
            }
            info("Ada");
        """, "Ada undefined", "Ada", "Ada 0", "Fehler")
        write("Schreibe eine Funktion `quadratAusgeben(zahl)`, die das Quadrat der Zahl ausgibt. Rufe sie mit `4` und mit `9` auf.",
            solution = """
                function quadratAusgeben(zahl) {
                  console.log(zahl * zahl);
                }
                quadratAusgeben(4);
                quadratAusgeben(9);
            """,
            output = """
                16
                81
            """,
            mustContain = listOf("function quadratAusgeben"),
            hint = "`function quadratAusgeben(zahl) { console.log(zahl * zahl); }` – danach zwei Aufrufe.")
    }

    lesson("Rückgabewerte", "Ergebnisse mit return zurückgeben", LessonIcon.CODE) {
        text("Mit `return` gibt eine Funktion ein **Ergebnis** zurück. Das Ergebnis kannst du speichern, weiterverwenden oder ausgeben.")
        code("""
            function verdoppeln(x) {
              return x * 2;
            }

            const ergebnis = verdoppeln(21);
            console.log(ergebnis);
            console.log(verdoppeln(5) + 1);
        """, output = """
            42
            11
        """)
        text("`return` beendet die Funktion sofort. Code danach wird nicht mehr ausgeführt. Eine Funktion ohne `return` liefert `undefined`.")
        code("""
            function pruefe(zahl) {
              if (zahl < 0) {
                return "negativ";
              }
              return "nicht negativ";
            }
            console.log(pruefe(-3));
            console.log(pruefe(8));
        """, output = """
            negativ
            nicht negativ
        """)
        text("Parameter können **Standardwerte** haben. Sie gelten, wenn beim Aufruf nichts mitgegeben wird.")
        code("""
            function gruss(name = "Gast") {
              return `Willkommen, ${D}{name}!`;
            }
            console.log(gruss("Ada"));
            console.log(gruss());
        """, output = """
            Willkommen, Ada!
            Willkommen, Gast!
        """)
        tip("Faustregel: Funktionen sollten lieber Werte **zurückgeben** als sie selbst auszugeben. Dann kann der Rest des Programms entscheiden, was mit dem Ergebnis passiert.")

        output("""
            function plus(a, b) {
              return a + b;
            }
            console.log(plus(2, 3) * 10);
        """, "50", "23", "5", "32")
        output("""
            function test() {
              console.log("A");
            }
            const x = test();
            console.log(x);
        """, "A\nundefined", "A", "undefined", "A\nA")
        typeOutput("""
            function stopp() {
              return "fertig";
              console.log("nie");
            }
            console.log(stopp());
        """, "fertig")
        choice("Was liefert eine Funktion ohne `return`?", "undefined", "null", "0", "Einen Fehler", mono = true)
        fill("Gib das Ergebnis aus der Funktion zurück.", """
            function hoch3(n) {
              ?? n ** 3;
            }
            console.log(hoch3(2));
        """, answers = listOf("return"), distractors = listOf("console.log", "give", "yield"), output = "8")
        output("""
            function teile(a, b = 2) {
              return a / b;
            }
            console.log(teile(10), teile(10, 5));
        """, "5 2", "5 5", "2 5", "10 2")
        write("Schreibe eine Funktion `istGerade(n)`, die `true` zurückgibt, wenn `n` gerade ist, sonst `false`.",
            solution = """
                function istGerade(n) {
                  return n % 2 === 0;
                }
            """,
            test = """
                if (istGerade(4) !== true) throw new Error("istGerade(4) sollte true sein");
                if (istGerade(7) !== false) throw new Error("istGerade(7) sollte false sein");
                if (istGerade(0) !== true) throw new Error("istGerade(0) sollte true sein");
                console.log("✅ Alle Tests bestanden!");
            """,
            hint = "Der Vergleich `n % 2 === 0` liefert schon `true` oder `false` – gib ihn direkt mit `return` zurück.")
        write("Schreibe eine Funktion `rabatt(preis, prozent = 10)`, die den Preis nach Abzug des Rabatts zurückgibt. Ohne Prozentangabe gelten 10 %.",
            solution = """
                function rabatt(preis, prozent = 10) {
                  return preis - preis * prozent / 100;
                }
            """,
            test = """
                if (rabatt(100) !== 90) throw new Error("rabatt(100) sollte 90 sein");
                if (rabatt(80, 25) !== 60) throw new Error("rabatt(80, 25) sollte 60 sein");
                if (rabatt(50, 0) !== 50) throw new Error("rabatt(50, 0) sollte 50 sein");
                console.log("✅ Alle Tests bestanden!");
            """,
            hint = "Der Rabatt ist `preis * prozent / 100` – den ziehst du vom Preis ab.")
    }

    lesson("Pfeilfunktionen", "Kurze Funktionen mit =>", LessonIcon.MAGIC) {
        text("Funktionen sind in JavaScript **Werte** – du kannst sie in Variablen speichern. Besonders kurz geht das mit **Pfeilfunktionen** (Arrow Functions):")
        code("""
            const verdoppeln = (x) => {
              return x * 2;
            };
            console.log(verdoppeln(4));
        """, output = "8")
        text("Besteht der Körper nur aus einem Ausdruck, kannst du die geschweiften Klammern **und** `return` weglassen. Bei genau einem Parameter dürfen sogar die runden Klammern weg.")
        code("""
            const plus = (a, b) => a + b;
            const hoch2 = n => n * n;
            const hallo = () => "Hallo!";
            console.log(plus(2, 3), hoch2(5), hallo());
        """, output = "5 25 Hallo!")
        text("Weil Funktionen Werte sind, kannst du sie anderen Funktionen **übergeben**. Eine übergebene Funktion nennt man **Callback**.")
        code("""
            function zweimal(aktion) {
              aktion();
              aktion();
            }
            zweimal(() => console.log("Klick"));
        """, output = """
            Klick
            Klick
        """)
        tip("Pfeilfunktionen werden dir ständig begegnen – vor allem bei Array-Methoden wie `map` und `filter` in der nächsten Einheit.")

        output("""
            const mal3 = x => x * 3;
            console.log(mal3(7));
        """, "21", "7", "x * 3", "37")
        output("""
            const f = (a, b) => a - b;
            console.log(f(10, 4));
        """, "6", "-6", "14", "4")
        choice("Welche Pfeilfunktion gibt die Summe von `a` und `b` zurück?", "(a, b) => a + b", "(a, b) -> a + b", "a, b => a + b", "(a, b) => { a + b }", mono = true,
            explain = "Mit geschweiften Klammern bräuchtest du `return`. Ohne Klammern um `a, b` versteht JavaScript den Ausdruck anders.")
        typeOutput("""
            const begruessung = name => `Hi ${D}{name}`;
            console.log(begruessung("Monty"));
        """, "Hi Monty")
        fill("Schreibe die Pfeilfunktion fertig.", """
            const halbiere = (x) ?? x / 2;
            console.log(halbiere(9));
        """, answers = listOf("=>"), distractors = listOf("->", "==", ">="), output = "4.5")
        output("""
            function machWas(fn, wert) {
              return fn(wert);
            }
            console.log(machWas(x => x + 1, 9));
        """, "10", "9", "x + 1", "undefined")
        truth("`const f = () => 5;` gibt beim Aufruf `f()` die Zahl 5 zurück.", true)
        write("Schreibe eine Pfeilfunktion `celsiusZuFahrenheit`, die Celsius in Fahrenheit umrechnet (`c * 9 / 5 + 32`).",
            solution = """
                const celsiusZuFahrenheit = (c) => c * 9 / 5 + 32;
            """,
            test = """
                if (celsiusZuFahrenheit(0) !== 32) throw new Error("0 °C sind 32 °F");
                if (celsiusZuFahrenheit(100) !== 212) throw new Error("100 °C sind 212 °F");
                if (celsiusZuFahrenheit(-40) !== -40) throw new Error("-40 °C sind -40 °F");
                console.log("✅ Alle Tests bestanden!");
            """,
            mustContain = listOf("=>"),
            hint = "`const celsiusZuFahrenheit = (c) => ...;`")
    }

    lesson("Gültigkeitsbereich", "Wo eine Variable sichtbar ist", LessonIcon.BRAIN) {
        text("Variablen mit `let` und `const` gelten nur in dem **Block** `{ }`, in dem sie angelegt wurden – das ist ihr **Gültigkeitsbereich** (Scope). Außerhalb gibt es sie nicht.")
        code("""
            const aussen = "überall";
            function zeige() {
              const lokal = "nur hier";
              console.log(aussen, lokal);
            }
            zeige();
            console.log(typeof lokal);
        """, output = """
            überall nur hier
            undefined
        """)
        text("Eine innere Variable kann eine äußere mit gleichem Namen **verdecken**. Die äußere bleibt dabei unverändert.")
        code("""
            const farbe = "blau";
            function maleRot() {
              const farbe = "rot";
              console.log(farbe);
            }
            maleRot();
            console.log(farbe);
        """, output = """
            rot
            blau
        """)
        text("Spannend: Eine Funktion „merkt“ sich die Variablen aus ihrer Umgebung – auch nachdem die äußere Funktion schon fertig ist. Das nennt man **Closure**.")
        code("""
            function macheZaehler() {
              let stand = 0;
              return () => {
                stand++;
                return stand;
              };
            }
            const zaehle = macheZaehler();
            console.log(zaehle(), zaehle(), zaehle());
        """, output = "1 2 3")
        tip("Lege Variablen so lokal wie möglich an. Je weniger globale Variablen, desto weniger können sich Programmteile gegenseitig stören.")

        output("""
            if (true) {
              const geheim = 42;
            }
            console.log(typeof geheim);
        """, "undefined", "number", "42", "Fehler")
        output("""
            let x = 1;
            function aendern() {
              x = 5;
            }
            aendern();
            console.log(x);
        """, "5", "1", "undefined", "Fehler", explain = "Ohne `let` in der Funktion wird die äußere Variable `x` verändert.")
        output("""
            let x = 1;
            function aendern() {
              let x = 5;
            }
            aendern();
            console.log(x);
        """, "1", "5", "undefined", "Fehler", explain = "`let x` in der Funktion legt eine neue, lokale Variable an – die äußere bleibt 1.")
        choice("Wo ist eine mit `let` in einem `if`-Block angelegte Variable sichtbar?", "Nur in diesem Block", "Im ganzen Programm", "In allen Funktionen", "Nur in der nächsten Zeile")
        typeOutput("""
            function zaehler() {
              let n = 0;
              return () => ++n;
            }
            const a = zaehler();
            a();
            a();
            console.log(a());
        """, "3")
        truth("Eine Funktion kann auf Variablen zugreifen, die außerhalb von ihr definiert wurden.", true)
        write("Schreibe eine Funktion `macheBegruesser(gruss)`, die eine **Funktion** zurückgibt. Diese nimmt einen Namen und liefert `<gruss>, <name>!` – zum Beispiel `Moin, Ada!`.",
            solution = """
                function macheBegruesser(gruss) {
                  return (name) => `${D}{gruss}, ${D}{name}!`;
                }
            """,
            test = """
                const moin = macheBegruesser("Moin");
                const hallo = macheBegruesser("Hallo");
                if (moin("Ada") !== "Moin, Ada!") throw new Error("moin(\"Ada\") sollte \"Moin, Ada!\" liefern");
                if (hallo("Ben") !== "Hallo, Ben!") throw new Error("hallo(\"Ben\") sollte \"Hallo, Ben!\" liefern");
                console.log("✅ Alle Tests bestanden!");
            """,
            hint = "Gib eine Pfeilfunktion zurück: `return (name) => ...;` – sie kennt `gruss` aus ihrer Umgebung.")
    }

    checkpoint()
}
