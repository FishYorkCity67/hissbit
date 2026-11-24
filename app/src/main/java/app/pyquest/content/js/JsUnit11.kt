package app.pyquest.content.js

import app.pyquest.content.CourseBuilder
import app.pyquest.data.LessonIcon

fun CourseBuilder.jsUnit11() = unit("Fehlerbehandlung", "Wenn etwas schiefgeht: try, catch und throw") {
    lesson("try & catch", "Fehler abfangen statt abstürzen", LessonIcon.BUG) {
        text("Normalerweise bricht ein Fehler das ganze Programm ab. Mit `try { ... } catch (fehler) { ... }` fängst du ihn ab: Tritt im `try`-Block ein Fehler auf, springt JavaScript sofort in den `catch`-Block – und das Programm läuft danach weiter.")
        code("""
            const nutzer = undefined;
            try {
              console.log(nutzer.name);
              console.log("Das wird übersprungen");
            } catch (fehler) {
              console.log("Hoppla:", fehler.message);
            }
            console.log("Das Programm läuft weiter.");
        """, output = """
            Hoppla: Cannot read properties of undefined (reading 'name')
            Das Programm läuft weiter.
        """)
        text("Das Fehlerobjekt hat zwei wichtige Eigenschaften: `name` (die Art des Fehlers) und `message` (die Beschreibung).")
        code("""
            try {
              nichtDefiniert();
            } catch (e) {
              console.log(e.name);
              console.log(e.message);
            }
        """, output = """
            ReferenceError
            nichtDefiniert is not defined
        """)
        tip("Gibt es keinen Fehler, wird der `catch`-Block einfach übersprungen.")
        warn("Fange Fehler nicht „blind“ ab, nur damit die Meldung verschwindet. Ein `catch` sollte sinnvoll reagieren – etwa eine verständliche Meldung ausgeben oder einen Ersatzwert nehmen.")

        output("""
            try {
              console.log("A");
            } catch (e) {
              console.log("B");
            }
            console.log("C");
        """, "A\nC", "A\nB\nC", "B\nC", "A")
        output("""
            try {
              const x = null;
              x.wert = 5;
              console.log("geschafft");
            } catch (e) {
              console.log("Fehler");
            }
        """, "Fehler", "geschafft", "geschafft\nFehler", "Nichts")
        typeOutput("""
            try {
              undefinierteFunktion();
            } catch (e) {
              console.log(e.name);
            }
        """, "ReferenceError")
        choice("Wann wird der `catch`-Block ausgeführt?", "Wenn im `try`-Block ein Fehler auftritt", "Immer", "Nie", "Nur am Ende des Programms")
        fill("Fange den Fehler ab.", """
            try {
              JSON.parse("kein JSON");
            } ?? (e) {
              console.log("Ungültige Daten");
            }
        """, answers = listOf("catch"), distractors = listOf("except", "error", "finally"), output = "Ungültige Daten")
        truth("Nach einem abgefangenen Fehler läuft das Programm hinter dem `catch`-Block weiter.", true)
        write("Rufe in einem `try`-Block die (nicht existierende) Funktion `ladeDaten()` auf. Gib im `catch`-Block `Laden fehlgeschlagen: ` gefolgt von der Fehlermeldung aus.",
            solution = """
                try {
                  ladeDaten();
                } catch (fehler) {
                  console.log("Laden fehlgeschlagen: " + fehler.message);
                }
            """,
            output = "Laden fehlgeschlagen: ladeDaten is not defined",
            mustContain = listOf("try", "catch"),
            hint = "Im `catch (fehler)` steht die Beschreibung in `fehler.message`.")
    }

    lesson("Fehler werfen", "Mit throw selbst Alarm schlagen", LessonIcon.BUG) {
        text("Mit `throw` löst du selbst einen Fehler aus – zum Beispiel, wenn eine Funktion ungültige Werte bekommt. Üblich ist `throw new Error(\"Beschreibung\")`.")
        code("""
            function wurzel(x) {
              if (x < 0) {
                throw new Error("Keine Wurzel aus negativen Zahlen!");
              }
              return Math.sqrt(x);
            }

            console.log(wurzel(16));
            try {
              console.log(wurzel(-4));
            } catch (e) {
              console.log("Fehler:", e.message);
            }
        """, output = """
            4
            Fehler: Keine Wurzel aus negativen Zahlen!
        """)
        text("Ein geworfener Fehler „wandert“ nach oben, bis ihn ein `catch` auffängt – auch über mehrere Funktionsaufrufe hinweg. Fängt ihn niemand, bricht das Programm ab:")
        code("""
            function pruefeAlter(alter) {
              if (typeof alter !== "number") throw new TypeError("Alter muss eine Zahl sein");
              return alter >= 18;
            }
            console.log(pruefeAlter("zwanzig"));
        """, fails = true)
        tip("Es gibt eingebaute Fehlerarten für typische Fälle: `TypeError` für falsche Typen, `RangeError` für Werte außerhalb des erlaubten Bereichs.")

        output("""
            function f() {
              throw new Error("kaputt");
            }
            try {
              f();
              console.log("weiter");
            } catch (e) {
              console.log(e.message);
            }
        """, "kaputt", "weiter", "weiter\nkaputt", "Error")
        output("""
            function teile(a, b) {
              if (b === 0) throw new RangeError("Teilen durch 0");
              return a / b;
            }
            try {
              console.log(teile(8, 2));
              console.log(teile(1, 0));
            } catch (e) {
              console.log(e.name);
            }
        """, "4\nRangeError", "4", "RangeError", "4\nInfinity")
        choice("Womit löst du selbst einen Fehler aus?", "throw new Error(\"...\")", "raise Error(\"...\")", "error(\"...\")", "catch new Error(\"...\")", mono = true)
        typeOutput("""
            try {
              throw new Error("Stopp!");
            } catch (e) {
              console.log(e.message.toUpperCase());
            }
        """, "STOPP!")
        fill("Wirf einen Fehler, wenn der Name leer ist.", """
            function setzeName(name) {
              if (name === "") ?? new Error("Name fehlt");
              return name;
            }
            try {
              setzeName("");
            } catch (e) {
              console.log(e.message);
            }
        """, answers = listOf("throw"), distractors = listOf("return", "catch", "raise"), output = "Name fehlt")
        truth("Ein nicht abgefangener Fehler beendet das Programm.", true)
        write("""
            Schreibe eine Funktion `pruefePin(pin)`, die einen `Error` mit der Meldung `PIN muss 4 Ziffern haben` wirft, wenn `pin` kein String aus genau 4 Ziffern ist.
            Sonst gibt sie `true` zurück. Tipp: `/^\d{4}$/.test(pin)` prüft auf genau 4 Ziffern.
        """.trimIndent(),
            solution = """
                function pruefePin(pin) {
                  if (typeof pin !== "string" || !/^\d{4}$/.test(pin)) {
                    throw new Error("PIN muss 4 Ziffern haben");
                  }
                  return true;
                }
            """,
            test = """
                if (pruefePin("1234") !== true) throw new Error("pruefePin(\"1234\") sollte true liefern");
                for (const falsch of ["123", "12345", "abcd", 1234]) {
                  let geworfen = false;
                  try {
                    pruefePin(falsch);
                  } catch (e) {
                    geworfen = e.message === "PIN muss 4 Ziffern haben";
                  }
                  if (!geworfen) throw new Error("Für " + JSON.stringify(falsch) + " sollte ein Fehler mit der richtigen Meldung kommen");
                }
                console.log("✅ Alle Tests bestanden!");
            """,
            mustContain = listOf("throw"),
            hint = "`if (...) throw new Error(\"PIN muss 4 Ziffern haben\");` – danach `return true;`")
    }

    lesson("Eigene Fehlerklassen", "Fehler unterscheiden mit instanceof", LessonIcon.CLASS) {
        text("Für eigene Fehlerarten erweiterst du die Klasse `Error`. Setze `this.name`, damit die Fehlermeldung zeigt, um welche Art es geht.")
        code("""
            class EingabeFehler extends Error {
              constructor(meldung) {
                super(meldung);
                this.name = "EingabeFehler";
              }
            }

            try {
              throw new EingabeFehler("Bitte eine Zahl eingeben");
            } catch (e) {
              console.log(e.name + ": " + e.message);
              console.log(e instanceof EingabeFehler, e instanceof Error);
            }
        """, output = """
            EingabeFehler: Bitte eine Zahl eingeben
            true true
        """)
        text("Im `catch` kannst du mit `instanceof` unterscheiden, welche Art von Fehler aufgetreten ist. Unerwartete Fehler wirfst du mit `throw` einfach weiter.")
        code("""
            class NichtGefunden extends Error {}

            function suche(liste, wert) {
              const i = liste.indexOf(wert);
              if (i === -1) throw new NichtGefunden(`${D}{wert} fehlt`);
              return i;
            }

            for (const wert of ["Kiwi", "Melone"]) {
              try {
                console.log("Position:", suche(["Apfel", "Kiwi"], wert));
              } catch (e) {
                if (e instanceof NichtGefunden) {
                  console.log("Nicht gefunden:", e.message);
                } else {
                  throw e;
                }
              }
            }
        """, output = """
            Position: 1
            Nicht gefunden: Melone fehlt
        """)
        tip("Eigene Fehler dürfen zusätzliche Eigenschaften haben – zum Beispiel `this.feld = \"email\"`, damit du weißt, welches Formularfeld falsch war.")

        output("""
            class MeinFehler extends Error {}
            try {
              throw new MeinFehler("x");
            } catch (e) {
              console.log(e instanceof MeinFehler);
            }
        """, "true", "false", "MeinFehler", "x")
        output("""
            class AppFehler extends Error {
              constructor(m) {
                super(m);
                this.name = "AppFehler";
              }
            }
            const f = new AppFehler("Server weg");
            console.log(`${D}{f.name}: ${D}{f.message}`);
        """, "AppFehler: Server weg", "Error: Server weg", "Server weg", "AppFehler")
        choice("Von welcher Klasse erbt eine eigene Fehlerklasse?", "Error", "Exception", "Object", "Fehler", mono = true)
        typeOutput("""
            class LeerFehler extends Error {}
            try {
              throw new LeerFehler("leer");
            } catch (e) {
              console.log(e instanceof Error ? "Error" : "kein Error");
            }
        """, "Error")
        fill("Unterscheide die Fehlerart.", """
            class ZuKlein extends Error {}
            try {
              throw new ZuKlein("3 < 5");
            } catch (e) {
              if (e ?? ZuKlein) {
                console.log("zu klein:", e.message);
              }
            }
        """, answers = listOf("instanceof"), distractors = listOf("typeof", "===", "in"), output = "zu klein: 3 < 5")
        truth("Mit `throw e;` im `catch`-Block kann man einen Fehler weiterwerfen.", true)
        write("""
            Schreibe eine Klasse `ZuWenigGeld extends Error`, deren Konstruktor den fehlenden Betrag bekommt, ihn in `this.fehlt` speichert,
            die Meldung `Es fehlen <fehlt> €` setzt und `this.name = "ZuWenigGeld"` festlegt.
        """.trimIndent(),
            solution = """
                class ZuWenigGeld extends Error {
                  constructor(fehlt) {
                    super(`Es fehlen ${D}{fehlt} €`);
                    this.name = "ZuWenigGeld";
                    this.fehlt = fehlt;
                  }
                }
            """,
            test = """
                const e = new ZuWenigGeld(12);
                if (!(e instanceof Error)) throw new Error("ZuWenigGeld soll von Error erben");
                if (e.message !== "Es fehlen 12 €") throw new Error("Die Meldung sollte \"Es fehlen 12 €\" sein, ist aber: " + e.message);
                if (e.name !== "ZuWenigGeld") throw new Error("Setze this.name = \"ZuWenigGeld\"");
                if (e.fehlt !== 12) throw new Error("Speichere den Betrag in this.fehlt");
                console.log("✅ Alle Tests bestanden!");
            """,
            mustContain = listOf("extends Error", "super"),
            hint = "Im Konstruktor zuerst `super(\"Es fehlen ...\")` aufrufen, danach `this.name` und `this.fehlt` setzen.")
    }

    lesson("finally & Fehlerarten", "Aufräumen und die typischen Fehler", LessonIcon.BUG) {
        text("Ein `finally`-Block läuft **immer** – egal ob ein Fehler auftrat oder nicht. Er ist perfekt zum Aufräumen, etwa um eine Ladeanzeige wieder auszublenden.")
        code("""
            function lade(ok) {
              console.log("Lade …");
              try {
                if (!ok) throw new Error("Netzwerkfehler");
                console.log("Daten da!");
              } catch (e) {
                console.log("Problem:", e.message);
              } finally {
                console.log("Ladeanzeige aus");
              }
            }
            lade(true);
            lade(false);
        """, output = """
            Lade …
            Daten da!
            Ladeanzeige aus
            Lade …
            Problem: Netzwerkfehler
            Ladeanzeige aus
        """)
        text("""
            Die häufigsten Fehlerarten in JavaScript:
            - `SyntaxError` – falsche Schreibweise, das Programm startet gar nicht
            - `ReferenceError` – ein Name ist unbekannt (Tippfehler?)
            - `TypeError` – ein Wert hat den falschen Typ, z. B. Zugriff auf `undefined.x`
            - `RangeError` – ein Wert liegt außerhalb des erlaubten Bereichs
        """)
        code("""
            const tests = [
              () => undefined.x,
              () => gibtsNicht,
              () => new Array(-1),
              () => (5).toFixed(500),
            ];
            for (const t of tests) {
              try {
                t();
              } catch (e) {
                console.log(e.name);
              }
            }
        """, output = """
            TypeError
            ReferenceError
            RangeError
            RangeError
        """)
        tip("Brauchst du das Fehlerobjekt im `catch` nicht, darfst du die Klammern weglassen: `catch { ... }`.")

        output("""
            try {
              console.log("A");
            } finally {
              console.log("B");
            }
        """, "A\nB", "A", "B", "B\nA")
        output("""
            try {
              throw new Error("x");
            } catch {
              console.log("gefangen");
            } finally {
              console.log("fertig");
            }
        """, "gefangen\nfertig", "fertig", "gefangen", "x\nfertig")
        match(
            "SyntaxError" to "falsche Schreibweise",
            "ReferenceError" to "unbekannter Name",
            "TypeError" to "falscher Typ",
            "RangeError" to "Wert außerhalb des Bereichs",
        )
        choice("Welcher Fehler entsteht bei `null.laenge`?", "TypeError", "ReferenceError", "SyntaxError", "RangeError", mono = true)
        typeOutput("""
            function test() {
              try {
                return "try";
              } finally {
                console.log("finally");
              }
            }
            console.log(test());
        """, "finally\ntry", explain = "Sogar ein `return` im `try` wartet, bis `finally` gelaufen ist.")
        fill("Sorge dafür, dass „Tschüss“ immer ausgegeben wird.", """
            try {
              console.log("Hallo");
            } ?? {
              console.log("Tschüss");
            }
        """, answers = listOf("finally"), distractors = listOf("catch", "else", "always"), output = "Hallo\nTschüss")
        truth("`finally` wird nur ausgeführt, wenn kein Fehler auftritt.", false)
        write("""
            Schreibe eine Funktion `sicherTeilen(a, b)`, die `a / b` zurückgibt. Ist `b` gleich `0`, soll sie einen `RangeError` mit der Meldung `Teilen durch 0` werfen.
            Rufe danach `sicherTeilen(10, 0)` in einem `try`-Block auf, gib im `catch` `<name>: <message>` aus und im `finally` `fertig`.
        """.trimIndent(),
            solution = """
                function sicherTeilen(a, b) {
                  if (b === 0) throw new RangeError("Teilen durch 0");
                  return a / b;
                }
                try {
                  console.log(sicherTeilen(10, 0));
                } catch (e) {
                  console.log(`${D}{e.name}: ${D}{e.message}`);
                } finally {
                  console.log("fertig");
                }
            """,
            output = """
                RangeError: Teilen durch 0
                fertig
            """,
            mustContain = listOf("RangeError", "finally"),
            hint = "`throw new RangeError(\"Teilen durch 0\")` in der Funktion, dann `try`/`catch`/`finally` drumherum.")
    }

    checkpoint()
}
