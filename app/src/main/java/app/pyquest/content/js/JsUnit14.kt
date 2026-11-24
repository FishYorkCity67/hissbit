package app.pyquest.content.js

import app.pyquest.content.CourseBuilder
import app.pyquest.data.LessonIcon

private val TODO_KLASSE = """
    class TodoListe {
      constructor() {
        this.aufgaben = [];
        this.naechsteId = 1;
      }

      hinzufuegen(text) {
        const aufgabe = { id: this.naechsteId++, text, erledigt: false };
        this.aufgaben.push(aufgabe);
        return aufgabe;
      }

      abhaken(id) {
        const aufgabe = this.aufgaben.find((a) => a.id === id);
        if (!aufgabe) throw new Error("Keine Aufgabe mit ID " + id);
        aufgabe.erledigt = true;
      }

      offen() {
        return this.aufgaben.filter((a) => !a.erledigt);
      }

      anzeigen() {
        for (const a of this.aufgaben) {
          console.log(`${D}{a.erledigt ? "✅" : "⬜"} ${D}{a.id}. ${D}{a.text}`);
        }
      }
    }
""".trimIndent()

private val QUIZ_FRAGEN = """
    const fragen = [
      { frage: "Wie viele Beine hat eine Spinne?", antwort: "8" },
      { frage: "Welche Farbe entsteht aus Blau und Gelb?", antwort: "grün" },
      { frage: "Wie heißt die Hauptstadt von Frankreich?", antwort: "paris" },
    ];
""".trimIndent()

private val PRUEFE = """
    function pruefe(eingabe, richtig) {
      return eingabe.trim().toLowerCase() === richtig.toLowerCase();
    }
""".trimIndent()

private val KONTO = """
    class KontoFehler extends Error {
      constructor(meldung) {
        super(meldung);
        this.name = "KontoFehler";
      }
    }

    class Konto {
      #stand = 0;

      constructor(inhaber) {
        this.inhaber = inhaber;
        this.buchungen = [];
      }

      get stand() {
        return this.#stand;
      }

      einzahlen(betrag) {
        if (betrag <= 0) throw new KontoFehler("Betrag muss positiv sein");
        this.#stand += betrag;
        this.buchungen.push({ art: "Einzahlung", betrag });
      }

      abheben(betrag) {
        if (betrag > this.#stand) throw new KontoFehler("Nicht genug Guthaben");
        this.#stand -= betrag;
        this.buchungen.push({ art: "Abhebung", betrag: -betrag });
      }

      toString() {
        return `Konto von ${D}{this.inhaber}: ${D}{this.#stand.toFixed(2)} €`;
      }
    }
""".trimIndent()

fun CourseBuilder.jsUnit14() = unit("Abschlussprojekte", "Alles kombinieren: drei echte Programme") {
    project("Projekt: To-do-Liste", "Eine kleine Aufgabenverwaltung mit Klassen und Arrays") {
        heading("Das Ziel")
        text("Wir bauen eine **To-do-Liste**: Aufgaben hinzufügen, abhaken und anzeigen. Dabei kombinierst du Klassen, Arrays von Objekten, Array-Methoden und Fehlerbehandlung.")
        code(TODO_KLASSE + "\n\n" + """
            const liste = new TodoListe();
            liste.hinzufuegen("JavaScript lernen");
            liste.hinzufuegen("Monty füttern");
            liste.hinzufuegen("Zimmer aufräumen");
            liste.abhaken(2);
            liste.anzeigen();
            console.log("Noch offen:", liste.offen().length);
        """.trimIndent(), output = """
            ⬜ 1. JavaScript lernen
            ✅ 2. Monty füttern
            ⬜ 3. Zimmer aufräumen
            Noch offen: 2
        """)
        tip("Tippe oben auf ↗, um die fertige Liste im Spielplatz auszuprobieren und zu erweitern. In den Aufgaben baust du sie jetzt Stück für Stück nach.")

        write("**Schritt 1:** Schreibe die Klasse `TodoListe` mit einem Konstruktor, der ein leeres Array `aufgaben` und `naechsteId = 1` anlegt, und der Methode `hinzufuegen(text)`. Sie legt `{ id, text, erledigt: false }` an, erhöht `naechsteId`, speichert die Aufgabe und gibt sie zurück.",
            starter = """
                class TodoListe {
                  // Dein Code hier
                }
            """,
            solution = """
                class TodoListe {
                  constructor() {
                    this.aufgaben = [];
                    this.naechsteId = 1;
                  }
                  hinzufuegen(text) {
                    const aufgabe = { id: this.naechsteId++, text, erledigt: false };
                    this.aufgaben.push(aufgabe);
                    return aufgabe;
                  }
                }
            """,
            test = """
                const l = new TodoListe();
                const a = l.hinzufuegen("Einkaufen");
                const b = l.hinzufuegen("Kochen");
                if (a.id !== 1 || b.id !== 2) throw new Error("Die IDs sollen bei 1 beginnen und hochzählen");
                if (a.text !== "Einkaufen" || a.erledigt !== false) throw new Error("Eine neue Aufgabe braucht text und erledigt: false");
                if (l.aufgaben.length !== 2) throw new Error("Beide Aufgaben sollen im Array aufgaben liegen");
                console.log("✅ Schritt 1 geschafft!");
            """,
            hint = "`this.naechsteId++` liefert die aktuelle ID und erhöht danach. `{ id: ..., text, erledigt: false }` – `text` ist die Kurzform für `text: text`.")
        write("**Schritt 2:** Ergänze `abhaken(id)`: Finde die Aufgabe mit `find` und setze `erledigt` auf `true`. Gibt es keine Aufgabe mit dieser ID, wirf einen `Error` mit der Meldung `Keine Aufgabe mit ID <id>`.",
            starter = """
                class TodoListe {
                  constructor() {
                    this.aufgaben = [];
                    this.naechsteId = 1;
                  }
                  hinzufuegen(text) {
                    const aufgabe = { id: this.naechsteId++, text, erledigt: false };
                    this.aufgaben.push(aufgabe);
                    return aufgabe;
                  }
                  // abhaken(id) ...
                }
            """,
            solution = """
                class TodoListe {
                  constructor() {
                    this.aufgaben = [];
                    this.naechsteId = 1;
                  }
                  hinzufuegen(text) {
                    const aufgabe = { id: this.naechsteId++, text, erledigt: false };
                    this.aufgaben.push(aufgabe);
                    return aufgabe;
                  }
                  abhaken(id) {
                    const aufgabe = this.aufgaben.find((a) => a.id === id);
                    if (!aufgabe) throw new Error("Keine Aufgabe mit ID " + id);
                    aufgabe.erledigt = true;
                  }
                }
            """,
            test = """
                const l = new TodoListe();
                l.hinzufuegen("A");
                l.hinzufuegen("B");
                l.abhaken(2);
                if (l.aufgaben[1].erledigt !== true || l.aufgaben[0].erledigt !== false) throw new Error("Nur Aufgabe 2 soll erledigt sein");
                let meldung = "";
                try {
                  l.abhaken(99);
                } catch (e) {
                  meldung = e.message;
                }
                if (meldung !== "Keine Aufgabe mit ID 99") throw new Error("Für ID 99 sollte ein Fehler \"Keine Aufgabe mit ID 99\" kommen");
                console.log("✅ Schritt 2 geschafft!");
            """,
            mustContain = listOf("find", "throw"),
            hint = "`const aufgabe = this.aufgaben.find((a) => a.id === id);` – ist das Ergebnis `undefined`, wirf den Fehler.")
        write("**Schritt 3:** Schreibe die Methoden `offen()` (liefert alle **nicht** erledigten Aufgaben als Array) und `anzeigen()` (gibt jede Aufgabe als `✅ 1. Text` bzw. `⬜ 1. Text` aus). Der Testcode legt drei Aufgaben an, hakt eine ab und zeigt die Liste an.",
            starter = """
                class TodoListe {
                  constructor() {
                    this.aufgaben = [];
                    this.naechsteId = 1;
                  }
                  hinzufuegen(text) {
                    const aufgabe = { id: this.naechsteId++, text, erledigt: false };
                    this.aufgaben.push(aufgabe);
                    return aufgabe;
                  }
                  abhaken(id) {
                    const aufgabe = this.aufgaben.find((a) => a.id === id);
                    if (!aufgabe) throw new Error("Keine Aufgabe mit ID " + id);
                    aufgabe.erledigt = true;
                  }
                  // offen() und anzeigen() ...
                }
            """,
            solution = TODO_KLASSE,
            test = """
                const liste = new TodoListe();
                liste.hinzufuegen("Lesen");
                liste.hinzufuegen("Sport");
                liste.hinzufuegen("Kochen");
                liste.abhaken(1);
                liste.anzeigen();
                console.log("Offen:", liste.offen().map((a) => a.text).join(", "));
            """,
            output = """
                ✅ 1. Lesen
                ⬜ 2. Sport
                ⬜ 3. Kochen
                Offen: Sport, Kochen
            """,
            mustContain = listOf("filter"),
            hint = "`offen()` gibt `this.aufgaben.filter((a) => !a.erledigt)` zurück. In `anzeigen()` hilft der `?`-Operator: `a.erledigt ? \"✅\" : \"⬜\"`.")
        choice("Warum wirft `abhaken()` bei einer unbekannten ID einen Fehler, statt still nichts zu tun?",
            "So fällt ein Tippfehler bei der ID sofort auf, statt unbemerkt zu bleiben.",
            "Weil `find` sonst eine Endlosschleife auslöst.",
            "Weil Methoden immer einen Fehler werfen müssen.",
            "Damit das Programm schneller läuft.")
    }

    project("Projekt: Quiz-Spiel", "Fragen stellen, Antworten prüfen, Punkte zählen") {
        heading("Das Ziel")
        text("Wir programmieren ein **Quiz**: Das Programm stellt Fragen mit `prompt()`, prüft die Antworten (ohne auf Groß-/Kleinschreibung und Leerzeichen zu achten) und zählt die Punkte.")
        code(QUIZ_FRAGEN + "\n\n" + PRUEFE + "\n\n" + """
            let punkte = 0;
            for (const { frage, antwort } of fragen) {
              const eingabe = prompt(frage) ?? "";
              if (pruefe(eingabe, antwort)) {
                console.log("Richtig! 🎉");
                punkte++;
              } else {
                console.log(`Leider falsch – richtig ist: ${D}{antwort}`);
              }
            }
            console.log(`Du hast ${D}{punkte} von ${D}{fragen.length} Punkten.`);
        """.trimIndent(), runnable = false)
        tip("Öffne das Quiz mit ↗ im Spielplatz und spiele es – oder ergänze eigene Fragen!")

        write("**Schritt 1:** Schreibe die Funktion `pruefe(eingabe, richtig)`. Sie gibt `true` zurück, wenn die Eingabe – ohne Leerzeichen am Rand und unabhängig von Groß-/Kleinschreibung – der richtigen Antwort entspricht.",
            starter = """
                function pruefe(eingabe, richtig) {
                  // Dein Code hier
                }
            """,
            solution = PRUEFE,
            test = """
                if (pruefe("Paris", "paris") !== true) throw new Error("Groß-/Kleinschreibung soll egal sein");
                if (pruefe("  grün ", "grün") !== true) throw new Error("Leerzeichen am Rand sollen egal sein");
                if (pruefe("Rom", "paris") !== false) throw new Error("Falsche Antworten sollen false liefern");
                if (pruefe("8", "8") !== true) throw new Error("pruefe(\"8\", \"8\") sollte true sein");
                console.log("✅ Schritt 1 geschafft!");
            """,
            mustContain = listOf("trim", "toLowerCase"),
            hint = "`eingabe.trim().toLowerCase() === richtig.toLowerCase()`")
        write("**Schritt 2:** Stelle jede Frage aus `fragen` mit `prompt()`, gib bei richtiger Antwort `Richtig! 🎉` aus, sonst `Leider falsch – richtig ist: <antwort>`. Am Ende: `Du hast <punkte> von 3 Punkten.` Getestet wird mit den Antworten **8**, **Blau** und **Paris**.",
            starter = QUIZ_FRAGEN + "\n\n" + PRUEFE + "\n\nlet punkte = 0;\n// Deine Quiz-Schleife hier",
            solution = QUIZ_FRAGEN + "\n\n" + PRUEFE + "\n\n" + """
                let punkte = 0;
                for (const { frage, antwort } of fragen) {
                  const eingabe = prompt(frage) ?? "";
                  if (pruefe(eingabe, antwort)) {
                    console.log("Richtig! 🎉");
                    punkte++;
                  } else {
                    console.log(`Leider falsch – richtig ist: ${D}{antwort}`);
                  }
                }
                console.log(`Du hast ${D}{punkte} von ${D}{fragen.length} Punkten.`);
            """.trimIndent(),
            stdin = listOf("8", "Blau", "Paris"),
            output = """
                Richtig! 🎉
                Leider falsch – richtig ist: grün
                Richtig! 🎉
                Du hast 2 von 3 Punkten.
            """,
            mustContain = listOf("prompt", "pruefe"),
            hint = "`for (const { frage, antwort } of fragen)` packt jede Frage direkt aus. Rufe `prompt(frage)` auf und prüfe mit `pruefe`.")
        write("**Schritt 3:** Schreibe eine Funktion `bewertung(punkte, max)`, die einen Kommentar liefert: bei voller Punktzahl `Perfekt!`, ab der Hälfte `Gut gemacht!`, sonst `Weiter üben!`.",
            solution = """
                function bewertung(punkte, max) {
                  if (punkte === max) return "Perfekt!";
                  if (punkte >= max / 2) return "Gut gemacht!";
                  return "Weiter üben!";
                }
            """,
            test = """
                if (bewertung(3, 3) !== "Perfekt!") throw new Error("bewertung(3, 3) sollte \"Perfekt!\" sein");
                if (bewertung(2, 4) !== "Gut gemacht!") throw new Error("bewertung(2, 4) sollte \"Gut gemacht!\" sein");
                if (bewertung(1, 3) !== "Weiter üben!") throw new Error("bewertung(1, 3) sollte \"Weiter üben!\" sein");
                if (bewertung(0, 5) !== "Weiter üben!") throw new Error("bewertung(0, 5) sollte \"Weiter üben!\" sein");
                console.log("✅ Schritt 3 geschafft!");
            """,
            hint = "Prüfe zuerst die volle Punktzahl, dann `punkte >= max / 2`.")
        choice("Wozu dient `?? \"\"` in `prompt(frage) ?? \"\"`?",
            "Bricht jemand die Eingabe ab, wird statt `null` ein leerer Text benutzt.",
            "Es entfernt Leerzeichen aus der Antwort.",
            "Es wiederholt die Frage.",
            "Es macht die Antwort klein.")
    }

    project("Projekt: Bankkonto", "Private Daten, Fehlerklassen und Buchungen") {
        heading("Das Ziel")
        text("Zum Schluss ein **Bankkonto**: Der Kontostand liegt in einem privaten Feld, Einzahlungen und Abhebungen werden geprüft, jede Buchung wird gespeichert – und Fehler haben eine eigene Klasse.")
        code(KONTO + "\n\n" + """
            const konto = new Konto("Ada");
            konto.einzahlen(100);
            konto.abheben(30);
            try {
              konto.abheben(500);
            } catch (e) {
              console.log(`${D}{e.name}: ${D}{e.message}`);
            }
            console.log(`${D}{konto}`);
            console.log(konto.buchungen.length, "Buchungen");
        """.trimIndent(), output = """
            KontoFehler: Nicht genug Guthaben
            Konto von Ada: 70.00 €
            2 Buchungen
        """)
        tip("Der Kontostand `#stand` ist privat: Von außen kann ihn niemand verändern – nur über `einzahlen()` und `abheben()`.")

        write("**Schritt 1:** Schreibe die Fehlerklasse `KontoFehler extends Error`, die im Konstruktor die Meldung an `super` weitergibt und `this.name = \"KontoFehler\"` setzt.",
            solution = """
                class KontoFehler extends Error {
                  constructor(meldung) {
                    super(meldung);
                    this.name = "KontoFehler";
                  }
                }
            """,
            test = """
                const e = new KontoFehler("Test");
                if (!(e instanceof Error)) throw new Error("KontoFehler soll von Error erben");
                if (e.name !== "KontoFehler" || e.message !== "Test") throw new Error("name oder message stimmen nicht");
                console.log("✅ Schritt 1 geschafft!");
            """,
            mustContain = listOf("extends Error"),
            hint = "Wie in der Lektion über eigene Fehlerklassen: `super(meldung);` und dann `this.name = \"KontoFehler\";`")
        write("**Schritt 2:** Schreibe die Klasse `Konto` mit privatem Feld `#stand = 0`, Konstruktor `(inhaber)` mit leerem Array `buchungen`, einem Getter `stand` und der Methode `einzahlen(betrag)`. Bei `betrag <= 0` wirft sie `new KontoFehler(\"Betrag muss positiv sein\")`, sonst erhöht sie den Stand und speichert `{ art: \"Einzahlung\", betrag }`.",
            starter = """
                class KontoFehler extends Error {
                  constructor(meldung) {
                    super(meldung);
                    this.name = "KontoFehler";
                  }
                }

                // class Konto ...
            """,
            solution = """
                class KontoFehler extends Error {
                  constructor(meldung) {
                    super(meldung);
                    this.name = "KontoFehler";
                  }
                }

                class Konto {
                  #stand = 0;
                  constructor(inhaber) {
                    this.inhaber = inhaber;
                    this.buchungen = [];
                  }
                  get stand() {
                    return this.#stand;
                  }
                  einzahlen(betrag) {
                    if (betrag <= 0) throw new KontoFehler("Betrag muss positiv sein");
                    this.#stand += betrag;
                    this.buchungen.push({ art: "Einzahlung", betrag });
                  }
                }
            """,
            test = """
                const k = new Konto("Ben");
                k.einzahlen(40);
                k.einzahlen(2.5);
                if (k.stand !== 42.5) throw new Error("Nach 40 + 2.5 sollte stand 42.5 sein");
                if (k.inhaber !== "Ben") throw new Error("Speichere den Inhaber");
                if (k.buchungen.length !== 2 || k.buchungen[0].art !== "Einzahlung") throw new Error("Jede Einzahlung soll in buchungen landen");
                let fehler = null;
                try {
                  k.einzahlen(-5);
                } catch (e) {
                  fehler = e;
                }
                if (!(fehler instanceof KontoFehler)) throw new Error("Bei -5 soll ein KontoFehler kommen");
                if (Object.keys(k).includes("stand")) throw new Error("Der Stand soll im privaten Feld #stand liegen");
                console.log("✅ Schritt 2 geschafft!");
            """,
            mustContain = listOf("#stand", "get stand"),
            hint = "`#stand = 0;` steht als Feld direkt in der Klasse. Der Getter: `get stand() { return this.#stand; }`")
        write("**Schritt 3:** Ergänze `abheben(betrag)` (wirft `KontoFehler(\"Nicht genug Guthaben\")`, wenn `betrag` größer als der Stand ist, und speichert `{ art: \"Abhebung\", betrag: -betrag }`) sowie `toString()`, die `Konto von <inhaber>: <stand mit 2 Nachkommastellen> €` liefert.",
            starter = """
                class KontoFehler extends Error {
                  constructor(meldung) {
                    super(meldung);
                    this.name = "KontoFehler";
                  }
                }

                class Konto {
                  #stand = 0;
                  constructor(inhaber) {
                    this.inhaber = inhaber;
                    this.buchungen = [];
                  }
                  get stand() {
                    return this.#stand;
                  }
                  einzahlen(betrag) {
                    if (betrag <= 0) throw new KontoFehler("Betrag muss positiv sein");
                    this.#stand += betrag;
                    this.buchungen.push({ art: "Einzahlung", betrag });
                  }
                  // abheben(betrag) und toString() ...
                }
            """,
            solution = KONTO,
            test = """
                const k = new Konto("Cleo");
                k.einzahlen(100);
                k.abheben(25.5);
                if (k.stand !== 74.5) throw new Error("Nach 100 - 25.5 sollte stand 74.5 sein");
                if (String(k) !== "Konto von Cleo: 74.50 €") throw new Error("toString() liefert: " + String(k));
                let meldung = "";
                try {
                  k.abheben(1000);
                } catch (e) {
                  meldung = e.name + ": " + e.message;
                }
                if (meldung !== "KontoFehler: Nicht genug Guthaben") throw new Error("Zu hohe Abhebung sollte \"Nicht genug Guthaben\" melden");
                if (k.stand !== 74.5) throw new Error("Eine abgelehnte Abhebung darf den Stand nicht ändern");
                if (k.buchungen.at(-1).betrag !== -25.5) throw new Error("Abhebungen sollen mit negativem Betrag gespeichert werden");
                console.log("✅ Schritt 3 geschafft!");
            """,
            mustContain = listOf("abheben", "toString"),
            hint = "Prüfe in `abheben` zuerst `if (betrag > this.#stand)` – erst danach abziehen. `toString()` nutzt `this.#stand.toFixed(2)`.")
        write("**Schritt 4:** Nutze die fertige Klasse: Lege ein Konto für `Monty` an, zahle `50` und `20` ein, hebe `35` ab und gib dann für jede Buchung eine Zeile `<art>: <betrag>` aus und zum Schluss das Konto selbst (mit Template-String).",
            starter = KONTO + "\n\n// Dein Code hier",
            solution = KONTO + "\n\n" + """
                const konto = new Konto("Monty");
                konto.einzahlen(50);
                konto.einzahlen(20);
                konto.abheben(35);
                for (const b of konto.buchungen) {
                  console.log(`${D}{b.art}: ${D}{b.betrag}`);
                }
                console.log(`${D}{konto}`);
            """.trimIndent(),
            output = """
                Einzahlung: 50
                Einzahlung: 20
                Abhebung: -35
                Konto von Monty: 35.00 €
            """,
            mustContain = listOf("new Konto"),
            hint = "Drei Methodenaufrufe, dann `for (const b of konto.buchungen)` und zum Schluss ``console.log(`${D}{konto}`)``.")
        choice("Warum ist `#stand` privat und nicht einfach `this.stand`?",
            "Damit der Kontostand nur über einzahlen() und abheben() – mit allen Prüfungen – verändert werden kann.",
            "Weil private Felder schneller sind.",
            "Weil JavaScript Zahlen sonst nicht speichern kann.",
            "Damit `console.log` den Stand nicht ausgibt.")
    }

    checkpoint()
}
