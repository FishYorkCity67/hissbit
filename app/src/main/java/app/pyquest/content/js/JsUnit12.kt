package app.pyquest.content.js

import app.pyquest.content.CourseBuilder
import app.pyquest.data.LessonIcon

fun CourseBuilder.jsUnit12() = unit("Asynchrones JavaScript", "Timer, Promises, async und await") {
    lesson("Timer", "setTimeout und setInterval", LessonIcon.STAR) {
        text("Mit `setTimeout(funktion, ms)` führst du eine Funktion **später** aus – nach der angegebenen Zahl von Millisekunden (1000 ms = 1 Sekunde). Das Programm wartet dabei nicht, sondern macht sofort weiter!")
        code("""
            console.log("1. Bestellung aufgegeben");
            setTimeout(() => {
              console.log("3. Pizza ist da! 🍕");
            }, 2000);
            console.log("2. Tisch decken");
        """, output = """
            1. Bestellung aufgegeben
            2. Tisch decken
            3. Pizza ist da! 🍕
        """)
        text("Das ist **asynchron**: JavaScript merkt sich die Funktion und erledigt erst alles andere. Sogar bei einer Wartezeit von 0 kommt der Timer erst dran, wenn der aktuelle Code fertig ist.")
        code("""
            setTimeout(() => console.log("C"), 0);
            setTimeout(() => console.log("B"), 0);
            console.log("A");
        """, output = """
            A
            C
            B
        """)
        text("`setInterval` wiederholt eine Funktion in festen Abständen, bis du sie mit `clearInterval` stoppst:")
        code("""
            let n = 0;
            const uhr = setInterval(() => {
              n++;
              console.log("Tick", n);
              if (n === 3) {
                clearInterval(uhr);
                console.log("Stopp!");
              }
            }, 500);
        """, output = """
            Tick 1
            Tick 2
            Tick 3
            Stopp!
        """)
        tip("Timer mit kürzerer Wartezeit kommen zuerst dran. Bei gleicher Wartezeit gilt die Reihenfolge, in der sie gestartet wurden.")

        output("""
            setTimeout(() => console.log("später"), 100);
            console.log("jetzt");
        """, "jetzt\nspäter", "später\njetzt", "jetzt", "später")
        output("""
            setTimeout(() => console.log("A"), 300);
            setTimeout(() => console.log("B"), 100);
            setTimeout(() => console.log("C"), 200);
        """, "B\nC\nA", "A\nB\nC", "C\nB\nA", "A\nC\nB")
        typeOutput("""
            console.log(1);
            setTimeout(() => console.log(2), 0);
            console.log(3);
        """, "1\n3\n2", explain = "Auch ein Timer mit 0 ms wartet, bis der übrige Code durchgelaufen ist.")
        choice("Wie viele Millisekunden sind 3 Sekunden?", "3000", "300", "30", "3", mono = true)
        fill("Stoppe den Timer nach dem zweiten Durchlauf.", """
            let runden = 0;
            const t = setInterval(() => {
              runden++;
              console.log("Runde", runden);
              if (runden === 2) ??(t);
            }, 100);
        """, answers = listOf("clearInterval"), distractors = listOf("setTimeout", "stopInterval", "break"), output = "Runde 1\nRunde 2")
        truth("`setTimeout` hält das Programm an, bis die Zeit abgelaufen ist.", false,
            explain = "Genau das tut es nicht: Das Programm läuft sofort weiter, die Funktion wird später aufgerufen.")
        write("Gib sofort `Los` aus, nach 1 Sekunde `Eins` und nach 2 Sekunden `Zwei`. Benutze `setTimeout`.",
            solution = """
                console.log("Los");
                setTimeout(() => console.log("Eins"), 1000);
                setTimeout(() => console.log("Zwei"), 2000);
            """,
            output = """
                Los
                Eins
                Zwei
            """,
            mustContain = listOf("setTimeout"),
            hint = "Zwei `setTimeout`-Aufrufe mit 1000 und 2000 Millisekunden.")
    }

    lesson("Promises", "Ein Versprechen auf ein späteres Ergebnis", LessonIcon.MAGIC) {
        text("Ein **Promise** steht für ein Ergebnis, das erst **später** da ist – etwa Daten aus dem Internet. Es wird entweder **erfüllt** (`resolve`) oder **abgelehnt** (`reject`).")
        code("""
            const bestellung = new Promise((resolve) => {
              setTimeout(() => resolve("🍕 Pizza Margherita"), 1000);
            });

            bestellung.then((essen) => {
              console.log("Geliefert:", essen);
            });
            console.log("Bestellt – jetzt warten …");
        """, output = """
            Bestellt – jetzt warten …
            Geliefert: 🍕 Pizza Margherita
        """)
        text("Mit `.then()` reagierst du auf das Ergebnis, mit `.catch()` auf einen Fehler. Jedes `.then()` liefert wieder ein Promise – deshalb kannst du sie **verketten**.")
        code("""
            function wuerfeln(zahl) {
              return new Promise((resolve, reject) => {
                setTimeout(() => {
                  if (zahl >= 1 && zahl <= 6) resolve(zahl);
                  else reject(new Error("Kein gültiger Wurf: " + zahl));
                }, 500);
              });
            }

            wuerfeln(4)
              .then((z) => z * 10)
              .then((punkte) => console.log("Punkte:", punkte));

            wuerfeln(9)
              .then((z) => console.log("Gewürfelt:", z))
              .catch((e) => console.log("Fehler:", e.message));
        """, output = """
            Punkte: 40
            Fehler: Kein gültiger Wurf: 9
        """)
        tip("`Promise.resolve(wert)` erzeugt ein schon erfülltes Promise – praktisch zum Ausprobieren.")
        warn("Ein abgelehntes Promise ohne `.catch()` führt zu einem Fehler, der das Programm beendet.")

        output("""
            Promise.resolve(5).then((x) => console.log(x * 2));
            console.log("zuerst");
        """, "zuerst\n10", "10\nzuerst", "10", "zuerst")
        output("""
            Promise.resolve(1)
              .then((x) => x + 1)
              .then((x) => x * 3)
              .then((x) => console.log(x));
        """, "6", "4", "3", "1")
        typeOutput("""
            Promise.reject(new Error("nein"))
              .then(() => console.log("ja"))
              .catch((e) => console.log(e.message));
        """, "nein")
        choice("Mit welcher Methode reagierst du auf ein **abgelehntes** Promise?", ".catch()", ".then()", ".error()", ".finally()", mono = true)
        fill("Erfülle das Promise mit dem Wert „fertig“.", """
            const p = new Promise((resolve, reject) => {
              ??("fertig");
            });
            p.then((x) => console.log(x));
        """, answers = listOf("resolve"), distractors = listOf("reject", "return", "then"), output = "fertig")
        truth("Ein `.then()` liefert wieder ein Promise, deshalb kann man `.then()`-Aufrufe aneinanderhängen.", true)
        write("""
            Schreibe eine Funktion `warte(ms)`, die ein Promise zurückgibt, das nach `ms` Millisekunden erfüllt wird.
            Nutze sie dann so: `warte(500).then(() => console.log("Fertig gewartet"));`
        """.trimIndent(),
            solution = """
                function warte(ms) {
                  return new Promise((resolve) => setTimeout(resolve, ms));
                }
                warte(500).then(() => console.log("Fertig gewartet"));
            """,
            output = "Fertig gewartet",
            mustContain = listOf("new Promise", "setTimeout"),
            hint = "`return new Promise((resolve) => setTimeout(resolve, ms));`")
    }

    lesson("async & await", "Asynchronen Code lesbar schreiben", LessonIcon.MAGIC) {
        text("Mit `async` und `await` liest sich asynchroner Code wie normaler Code von oben nach unten. `await` wartet auf ein Promise – das geht nur in Funktionen, die mit `async` markiert sind.")
        code("""
            const warte = (ms) => new Promise((r) => setTimeout(r, ms));

            async function morgenroutine() {
              console.log("⏰ Wecker klingelt");
              await warte(1000);
              console.log("🚿 Duschen");
              await warte(1000);
              console.log("☕ Kaffee");
            }

            morgenroutine();
        """, output = """
            ⏰ Wecker klingelt
            🚿 Duschen
            ☕ Kaffee
        """)
        text("`await` liefert direkt den Wert des Promise. Fehler fängst du ganz normal mit `try`/`catch` ab. Eine `async`-Funktion gibt selbst immer ein Promise zurück.")
        code("""
            function ladeNutzer(id) {
              return new Promise((resolve, reject) => {
                setTimeout(() => {
                  if (id === 1) resolve({ id: 1, name: "Ada" });
                  else reject(new Error("Nutzer " + id + " gibt es nicht"));
                }, 300);
              });
            }

            async function zeige(id) {
              try {
                const nutzer = await ladeNutzer(id);
                console.log("Hallo,", nutzer.name);
              } catch (e) {
                console.log("Fehler:", e.message);
              }
            }

            async function main() {
              await zeige(1);
              await zeige(2);
            }
            main();
        """, output = """
            Hallo, Ada
            Fehler: Nutzer 2 gibt es nicht
        """)
        warn("`await` außerhalb einer `async`-Funktion ist hier ein **SyntaxError**. Packe dein Programm deshalb in eine `async function main() { ... }` und rufe `main()` auf.")
        tip("Vergisst du `await`, bekommst du statt des Ergebnisses nur das Promise: `console.log(ladeNutzer(1))` zeigt `Promise { <pending> }`.")

        output("""
            async function f() {
              return 42;
            }
            f().then((x) => console.log(x));
        """, "42", "Promise { 42 }", "undefined", "f")
        output("""
            const warte = (ms) => new Promise((r) => setTimeout(r, ms));
            async function a() {
              console.log("A1");
              await warte(100);
              console.log("A2");
            }
            a();
            console.log("B");
        """, "A1\nB\nA2", "A1\nA2\nB", "B\nA1\nA2", "A1\nB")
        typeOutput("""
            async function doppelt(x) {
              return x * 2;
            }
            async function main() {
              const ergebnis = await doppelt(21);
              console.log(ergebnis);
            }
            main();
        """, "42")
        choice("Wo darfst du `await` benutzen?", "In Funktionen, die mit `async` markiert sind", "Überall im Programm", "Nur in Schleifen", "Nur in `then()`")
        fill("Warte auf das Ergebnis der Funktion.", """
            const holeZahl = () => Promise.resolve(7);
            async function main() {
              const z = ?? holeZahl();
              console.log(z + 1);
            }
            main();
        """, answers = listOf("await"), distractors = listOf("async", "wait", "then"), output = "8")
        output("""
            async function test() {
              try {
                await Promise.reject(new Error("weg"));
                console.log("ok");
              } catch (e) {
                console.log("Fehler:", e.message);
              }
            }
            test();
        """, "Fehler: weg", "ok", "ok\nFehler: weg", "weg")
        truth("Eine `async`-Funktion gibt immer ein Promise zurück.", true)
        write("""
            Schreibe eine `async`-Funktion `countdown(von)`, die von `von` bis 1 herunterzählt und jede Zahl ausgibt – mit je 1 Sekunde Pause dazwischen.
            Danach soll sie `Start!` ausgeben. Rufe `countdown(3)` auf. Die Funktion `warte(ms)` ist schon vorhanden.
        """.trimIndent(),
            starter = """
                const warte = (ms) => new Promise((r) => setTimeout(r, ms));
            """,
            solution = """
                const warte = (ms) => new Promise((r) => setTimeout(r, ms));

                async function countdown(von) {
                  for (let i = von; i >= 1; i--) {
                    console.log(i);
                    await warte(1000);
                  }
                  console.log("Start!");
                }
                countdown(3);
            """,
            output = """
                3
                2
                1
                Start!
            """,
            mustContain = listOf("async", "await"),
            hint = "Eine `for`-Schleife, in der nach jedem `console.log(i)` ein `await warte(1000);` steht.")
    }

    lesson("Mehrere Promises", "Promise.all und Co.", LessonIcon.ROCKET) {
        text("Mit `Promise.all([...])` wartest du auf **mehrere** Promises gleichzeitig. Das Ergebnis ist ein Array mit allen Werten – in der Reihenfolge der Promises, nicht in der Reihenfolge, in der sie fertig wurden.")
        code("""
            const lade = (was, ms) => new Promise((r) => setTimeout(() => r(was), ms));

            async function main() {
              const ergebnisse = await Promise.all([
                lade("Profil", 300),
                lade("Fotos", 100),
                lade("Freunde", 200),
              ]);
              console.log(ergebnisse);
            }
            main();
        """, output = "[ 'Profil', 'Fotos', 'Freunde' ]")
        text("Das spart Zeit: Nacheinander mit `await` dauert es so lange wie alle Wartezeiten **zusammen**, mit `Promise.all` nur so lange wie die **längste**.")
        code("""
            const warte = (ms) => new Promise((r) => setTimeout(r, ms));

            async function nacheinander() {
              const start = Date.now();
              await warte(1000);
              await warte(1000);
              console.log("nacheinander:", Math.round((Date.now() - start) / 1000), "s");
            }
            async function gleichzeitig() {
              const start = Date.now();
              await Promise.all([warte(1000), warte(1000)]);
              console.log("gleichzeitig:", Math.round((Date.now() - start) / 1000), "s");
            }
            async function main() {
              await nacheinander();
              await gleichzeitig();
            }
            main();
        """, output = """
            nacheinander: 2 s
            gleichzeitig: 1 s
        """)
        text("Wird eines der Promises abgelehnt, scheitert `Promise.all` sofort. `Promise.race` liefert das Ergebnis des **schnellsten** Promise.")
        code("""
            const lade = (was, ms) => new Promise((r) => setTimeout(() => r(was), ms));
            Promise.race([lade("Schnecke 🐌", 500), lade("Hase 🐇", 100)])
              .then((sieger) => console.log("Gewinner:", sieger));
        """, output = "Gewinner: Hase 🐇")
        tip("`Promise.allSettled` wartet auf alle – auch auf die abgelehnten – und sagt dir zu jedem, ob es geklappt hat.")

        output("""
            Promise.all([Promise.resolve(1), Promise.resolve(2)])
              .then((werte) => console.log(werte));
        """, "[ 1, 2 ]", "3", "1\n2", "[ [ 1 ], [ 2 ] ]")
        output("""
            const lade = (x, ms) => new Promise((r) => setTimeout(() => r(x), ms));
            Promise.all([lade("A", 300), lade("B", 100)]).then((e) => console.log(e.join("")));
        """, "AB", "BA", "A", "B", explain = "`Promise.all` behält die Reihenfolge der Promises bei – egal, welches zuerst fertig ist.")
        typeOutput("""
            const lade = (x, ms) => new Promise((r) => setTimeout(() => r(x), ms));
            Promise.race([lade("rot", 200), lade("blau", 50)]).then((x) => console.log(x));
        """, "blau")
        choice("Was passiert, wenn bei `Promise.all` **ein** Promise abgelehnt wird?", "Promise.all wird ebenfalls abgelehnt", "Das Promise wird einfach ausgelassen", "Es wartet für immer", "Es wird automatisch wiederholt")
        fill("Warte auf beide Downloads gleichzeitig.", """
            const lade = (x) => Promise.resolve(x);
            async function main() {
              const [a, b] = await Promise.??([lade(1), lade(2)]);
              console.log(a + b);
            }
            main();
        """, answers = listOf("all"), distractors = listOf("race", "then", "wait"), output = "3")
        truth("Mit `Promise.all` dauert es so lange wie die Summe aller Wartezeiten.", false)
        write("""
            Die Funktion `preis(produkt)` liefert den Preis eines Produkts als Promise. Lade mit `Promise.all` gleichzeitig die Preise
            von `"Brot"`, `"Milch"` und `"Käse"` und gib die Summe mit 2 Nachkommastellen aus.
        """.trimIndent(),
            starter = """
                const liste = { Brot: 2.49, Milch: 1.19, Käse: 3.99 };
                const preis = (p) => new Promise((r) => setTimeout(() => r(liste[p]), 200));
            """,
            solution = """
                const liste = { Brot: 2.49, Milch: 1.19, Käse: 3.99 };
                const preis = (p) => new Promise((r) => setTimeout(() => r(liste[p]), 200));

                async function main() {
                  const preise = await Promise.all([preis("Brot"), preis("Milch"), preis("Käse")]);
                  const summe = preise.reduce((s, p) => s + p, 0);
                  console.log(summe.toFixed(2));
                }
                main();
            """,
            output = "7.67",
            mustContain = listOf("Promise.all"),
            hint = "`await Promise.all([preis(\"Brot\"), preis(\"Milch\"), preis(\"Käse\")])` liefert ein Array mit drei Preisen.")
    }

    checkpoint()
}
