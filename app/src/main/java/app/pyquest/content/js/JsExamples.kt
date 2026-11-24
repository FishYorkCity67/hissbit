package app.pyquest.content.js

import app.pyquest.data.ExampleProgram

private fun ex(category: String, title: String, description: String, code: String, input: List<String> = emptyList()) =
    ExampleProgram(title, description, code.trimIndent().trimEnd() + "\n", category, input)

/** Beispielprogramme für den JavaScript-Spielplatz. */
val JsExamplePrograms: List<ExampleProgram> = listOf(
    ex("Grundlagen", "Hallo Welt", "Das klassische erste Programm.", """
        console.log("Hallo, Welt! 👋");
        console.log("Ich programmiere mit JavaScript", 2025);
    """),
    ex("Grundlagen", "Begrüßung", "Fragt nach deinem Namen und Alter.", """
        const name = prompt("Wie heißt du?");
        const alter = Number(prompt("Wie alt bist du?"));
        console.log(`Hallo ${'$'}{name}!`);
        console.log(`In 10 Jahren bist du ${'$'}{alter + 10} Jahre alt.`);
    """, listOf("Ada", "14")),
    ex("Grundlagen", "Taschenrechner", "Rechnet mit zwei Zahlen und einem Rechenzeichen.", """
        console.log("Mini-Taschenrechner 🧮");
        const a = Number(prompt("Erste Zahl:"));
        const op = prompt("Rechenzeichen (+ - * /):");
        const b = Number(prompt("Zweite Zahl:"));

        let ergebnis;
        switch (op) {
          case "+":
            ergebnis = a + b;
            break;
          case "-":
            ergebnis = a - b;
            break;
          case "*":
            ergebnis = a * b;
            break;
          case "/":
            ergebnis = b === 0 ? "Fehler: Teilen durch 0!" : a / b;
            break;
          default:
            ergebnis = "Unbekanntes Rechenzeichen";
        }
        console.log("Ergebnis:", ergebnis);
    """, listOf("12", "*", "3.5")),
    ex("Grundlagen", "Temperatur-Tabelle", "Celsius in Fahrenheit umrechnen – schön ausgerichtet.", """
        console.log("Celsius | Fahrenheit");
        console.log("--------+-----------");
        for (let celsius = -10; celsius <= 40; celsius += 10) {
          const fahrenheit = celsius * 9 / 5 + 32;
          console.log(`${'$'}{String(celsius).padStart(7)} | ${'$'}{fahrenheit.toFixed(1).padStart(9)}`);
        }
    """),
    ex("Spiele", "Zahlenraten", "Errate Montys Geheimzahl zwischen 1 und 100.", """
        const geheim = Math.floor(Math.random() * 100) + 1;
        let versuche = 0;
        console.log("Ich denke an eine Zahl zwischen 1 und 100.");

        while (true) {
          const eingabe = prompt("Dein Tipp:");
          if (eingabe === null) break; // abgebrochen
          const tipp = Number(eingabe);
          if (!Number.isInteger(tipp)) {
            console.log("Bitte eine ganze Zahl eingeben!");
            continue;
          }
          versuche++;
          if (tipp < geheim) {
            console.log("Zu klein! ⬆️");
          } else if (tipp > geheim) {
            console.log("Zu groß! ⬇️");
          } else {
            console.log(`Richtig! 🎉 Du hast ${'$'}{versuche} Versuche gebraucht.`);
            break;
          }
        }
    """, listOf("abc") + (1..100).map { it.toString() }),
    ex("Spiele", "Schere, Stein, Papier", "Drei Runden gegen Monty.", """
        const optionen = ["schere", "stein", "papier"];
        const gewinntGegen = { schere: "papier", stein: "schere", papier: "stein" };
        let punkteDu = 0;
        let punkteMonty = 0;

        for (let runde = 1; runde <= 3; runde++) {
          const wahl = (prompt(`Runde ${'$'}{runde} – schere, stein oder papier?`) ?? "").trim().toLowerCase();
          if (!optionen.includes(wahl)) {
            console.log("Das gilt nicht – Runde verloren!");
            punkteMonty++;
            continue;
          }
          const monty = optionen[Math.floor(Math.random() * 3)];
          console.log(`Monty wählt ${'$'}{monty}.`);
          if (wahl === monty) {
            console.log("Unentschieden!");
          } else if (gewinntGegen[wahl] === monty) {
            console.log("Punkt für dich! 🎉");
            punkteDu++;
          } else {
            console.log("Punkt für Monty! 🐍");
            punkteMonty++;
          }
        }
        console.log(`Endstand: Du ${'$'}{punkteDu} – ${'$'}{punkteMonty} Monty`);
    """, listOf("stein", "papier", "schere")),
    ex("Spiele", "Würfel-Statistik", "Würfelt 600-mal und zeigt, wie oft jede Zahl kam.", """
        const anzahl = [0, 0, 0, 0, 0, 0];
        for (let i = 0; i < 600; i++) {
          const wurf = Math.floor(Math.random() * 6) + 1;
          anzahl[wurf - 1]++;
        }
        anzahl.forEach((n, i) => {
          console.log(`${'$'}{i + 1}: ${'$'}{"■".repeat(Math.round(n / 10))} ${'$'}{n}`);
        });
    """),
    ex("Mathe", "FizzBuzz", "Der Klassiker aus Bewerbungsgesprächen.", """
        for (let i = 1; i <= 20; i++) {
          if (i % 15 === 0) console.log("FizzBuzz");
          else if (i % 3 === 0) console.log("Fizz");
          else if (i % 5 === 0) console.log("Buzz");
          else console.log(i);
        }
    """),
    ex("Mathe", "Primzahlen", "Findet alle Primzahlen bis 100.", """
        function istPrim(n) {
          if (n < 2) return false;
          for (let teiler = 2; teiler * teiler <= n; teiler++) {
            if (n % teiler === 0) return false;
          }
          return true;
        }

        const primzahlen = [];
        for (let n = 2; n <= 100; n++) {
          if (istPrim(n)) primzahlen.push(n);
        }
        console.log(primzahlen.join(", "));
        console.log(`Das sind ${'$'}{primzahlen.length} Primzahlen.`);
    """),
    ex("Mathe", "Fibonacci", "Jede Zahl ist die Summe der beiden davor.", """
        let a = 0;
        let b = 1;
        const folge = [];
        while (folge.length < 15) {
          folge.push(a);
          [a, b] = [b, a + b];
        }
        console.log(folge.join(" → "));
    """),
    ex("Daten", "Einkaufsliste", "Arrays mit push, filter und map.", """
        const liste = ["Äpfel", "Brot", "Milch"];
        liste.push("Käse");
        liste.unshift("Kaffee");

        console.log("Einkaufsliste:");
        liste.forEach((artikel, i) => console.log(`${'$'}{i + 1}. ${'$'}{artikel}`));

        const kurz = liste.filter((artikel) => artikel.length <= 5);
        console.log("Kurze Namen:", kurz);
        console.log("In Großbuchstaben:", liste.map((a) => a.toUpperCase()).join(", "));
    """),
    ex("Daten", "Notenspiegel", "Objekte, reduce und sort für eine kleine Klassenliste.", """
        const klasse = [
          { name: "Ada", punkte: 92 },
          { name: "Ben", punkte: 67 },
          { name: "Cleo", punkte: 85 },
          { name: "Dev", punkte: 74 },
        ];

        const schnitt = klasse.reduce((summe, s) => summe + s.punkte, 0) / klasse.length;
        console.log(`Durchschnitt: ${'$'}{schnitt.toFixed(1)} Punkte`);

        const rangliste = [...klasse].sort((a, b) => b.punkte - a.punkte);
        rangliste.forEach((s, platz) => {
          const note = s.punkte >= 90 ? "sehr gut" : s.punkte >= 75 ? "gut" : "befriedigend";
          console.log(`${'$'}{platz + 1}. ${'$'}{s.name.padEnd(5)} ${'$'}{s.punkte} – ${'$'}{note}`);
        });
    """),
    ex("Daten", "Wörter zählen", "Zählt Wörter in einem Text mit einer Map.", """
        const text = "Der Hund und die Katze und der Vogel und der Fisch";
        const zaehler = new Map();
        for (const wort of text.toLowerCase().split(" ")) {
          zaehler.set(wort, (zaehler.get(wort) ?? 0) + 1);
        }
        const sortiert = [...zaehler].sort((a, b) => b[1] - a[1]);
        for (const [wort, anzahl] of sortiert) {
          console.log(`${'$'}{wort.padEnd(6)} ${'$'}{"*".repeat(anzahl)} (${'$'}{anzahl})`);
        }
    """),
    ex("Asynchron", "Countdown", "Zählt mit await und setTimeout herunter.", """
        const warte = (ms) => new Promise((fertig) => setTimeout(fertig, ms));

        async function countdown(von) {
          for (let i = von; i > 0; i--) {
            console.log(i + "…");
            await warte(1000);
          }
          console.log("🚀 Start!");
        }

        countdown(5);
    """),
    ex("Asynchron", "Ampel", "Eine Ampel, die mit setInterval umschaltet.", """
        const phasen = ["🔴 Rot", "🔴🟡 Rot-Gelb", "🟢 Grün", "🟡 Gelb"];
        let schritt = 0;

        const ampel = setInterval(() => {
          console.log(phasen[schritt % phasen.length]);
          schritt++;
          if (schritt === 8) {
            clearInterval(ampel);
            console.log("Ampel aus.");
          }
        }, 700);
    """),
    ex("Spaß", "Tannenbaum", "Zeichnet einen Baum aus Sternchen.", """
        const hoehe = 6;
        for (let i = 0; i < hoehe; i++) {
          console.log(" ".repeat(hoehe - i - 1) + "*".repeat(2 * i + 1));
        }
        console.log(" ".repeat(hoehe - 1) + "|");
    """),
    ex("Spaß", "Passwort-Generator", "Erzeugt zufällige, sichere Passwörter.", """
        const zeichen = "abcdefghijkmnpqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ23456789!?#%";

        function passwort(laenge) {
          let pw = "";
          for (let i = 0; i < laenge; i++) {
            pw += zeichen[Math.floor(Math.random() * zeichen.length)];
          }
          return pw;
        }

        for (let i = 1; i <= 3; i++) {
          console.log(`Passwort ${'$'}{i}: ${'$'}{passwort(12)}`);
        }
    """),
    ex("Spaß", "Klasse Haustier", "Klassen, Methoden und Vererbung.", """
        class Haustier {
          constructor(name, laut) {
            this.name = name;
            this.laut = laut;
          }
          sprich() {
            return `${'$'}{this.name} macht ${'$'}{this.laut}!`;
          }
        }

        class Schlange extends Haustier {
          constructor(name) {
            super(name, "Sssss");
          }
          sprich() {
            return super.sprich() + " 🐍";
          }
        }

        const tiere = [new Haustier("Bello", "Wuff"), new Haustier("Mimi", "Miau"), new Schlange("Monty")];
        for (const tier of tiere) console.log(tier.sprich());
    """),
)
