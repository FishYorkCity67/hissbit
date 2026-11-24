package app.pyquest.content.js

import app.pyquest.data.LessonIcon
import app.pyquest.data.RefCategory
import app.pyquest.data.RefEntry

private class JsCategoryBuilder(private val id: String) {
    val entries = mutableListOf<RefEntry>()
    fun e(title: String, summary: String, body: String = "", code: String? = null, vararg keywords: String) {
        entries += RefEntry(
            id = "$id-${entries.size + 1}",
            title = title,
            summary = summary,
            body = body.trimIndent().trim(),
            code = code?.trimIndent()?.trimEnd(),
            keywords = keywords.toList(),
        )
    }
}

private fun category(id: String, title: String, icon: LessonIcon, block: JsCategoryBuilder.() -> Unit) =
    RefCategory(id, title, icon, JsCategoryBuilder(id).apply(block).entries.toList())

/** Nachschlagewerk für JavaScript. Kategorie-IDs mit Präfix "js-". */
val JsReferenceCategories: List<RefCategory> = listOf(
    category("js-basics", "Grundlagen", LessonIcon.BOOK) {
        e("console.log() – Ausgabe", "Gibt Werte in der Konsole aus.",
            "Mehrere Werte trennst du mit Kommas – dazwischen kommt ein Leerzeichen. `console.error()` gibt Fehlermeldungen aus, `console.table()` zeichnet Tabellen.",
            """
            console.log("Hallo");
            console.log("Alter:", 12, true);
            console.table([{ name: "Ada", punkte: 9 }]);
            """, "ausgabe", "print", "konsole")
        e("let und const", "Variablen anlegen.",
            "`const` für Werte, die nicht neu zugewiesen werden, `let` für Variablen, die sich ändern. Namen schreibt man im camelCase. `var` ist veraltet.",
            """
            const name = "Monty";
            let punkte = 10;
            punkte += 5;
            console.log(name, punkte);
            """, "variable", "var", "camelCase")
        e("Kommentare", "Notizen, die JavaScript ignoriert.",
            "`//` bis zum Zeilenende, `/* ... */` über mehrere Zeilen.",
            """
            // Einzeiliger Kommentar
            /* Kommentar über
               mehrere Zeilen */
            console.log("Kommentare werden ignoriert"); // auch hier
            """, "//", "notiz")
        e("prompt() – Eingabe", "Fragt den Nutzer nach einem Text.",
            "`prompt()` liefert **immer einen String** – oder `null`, wenn abgebrochen wurde. Für Zahlen: `Number(prompt(...))`.",
            """
            // Im Spielplatz erscheint ein Eingabefeld:
            // const alter = Number(prompt("Wie alt bist du?"));
            const eingabe = "12";
            const alter = Number(eingabe);
            console.log(alter + 1);
            """, "eingabe", "input", "tastatur")
        e("Rechenoperatoren", "`+ - * / % **`",
            "`%` liefert den Rest, `**` die Potenz. Für ganzzahlige Division: `Math.floor(a / b)`. Kurzformen: `x += 2`, `x++`, `x--`.",
            """
            console.log(7 / 2, 7 % 2, 2 ** 8);
            console.log(Math.floor(7 / 2));
            let x = 5;
            x *= 3;
            x++;
            console.log(x);
            """, "plus", "modulo", "potenz")
        e("Datentypen", "number, string, boolean, undefined, null, object …",
            "Mit `typeof` fragst du den Typ ab. Arrays und `null` liefern `\"object\"` – Arrays erkennst du mit `Array.isArray()`.",
            """
            console.log(typeof 42, typeof "Hi", typeof true);
            console.log(typeof undefined, typeof null, typeof {});
            console.log(Array.isArray([1, 2]));
            """, "typeof", "typ")
        e("Typen umwandeln", "Number(), String(), parseInt(), parseFloat()",
            "Ungültige Zahlen ergeben `NaN` (Not a Number) – prüfen mit `Number.isNaN()`. Vorsicht: `\"5\" + 3` ist `\"53\"`.",
            """
            console.log(Number("42") + 1);
            console.log(String(7) + 7);
            console.log(parseInt("12px"), parseFloat("3.5kg"));
            console.log(Number("abc"), Number.isNaN(Number("abc")));
            """, "NaN", "parseInt", "konvertieren")
        e("Template-Strings", "Texte mit eingebauten Werten: `` `${D}{x}` ``",
            "Template-Strings stehen in Backticks, dürfen mehrzeilig sein und setzen mit `${D}{...}` beliebige Ausdrücke ein.",
            """
            const name = "Ada";
            const punkte = 7;
            console.log(`${D}{name} hat ${D}{punkte * 10} Punkte.`);
            """, "backtick", "interpolation", "f-string")
    },
    category("js-strings", "Strings", LessonIcon.CHAT) {
        e("Länge & Zeichen", "`text.length`, `text[0]`, `text.at(-1)`",
            "Der Index beginnt bei 0. Ein Index außerhalb liefert `undefined`. Strings lassen sich nicht verändern – Methoden liefern neue Strings.",
            """
            const t = "Monty";
            console.log(t.length, t[0], t.at(-1));
            """, "length", "index")
        e("Groß & klein", "`toUpperCase()`, `toLowerCase()`",
            "Praktisch, um Eingaben unabhängig von der Schreibweise zu vergleichen.",
            """
            console.log("Hallo".toUpperCase(), "HALLO".toLowerCase());
            console.log("JA".toLowerCase() === "ja");
            """, "upper", "lower")
        e("trim()", "Leerzeichen am Rand entfernen.",
            "`trimStart()` und `trimEnd()` entfernen nur links bzw. rechts.",
            """
            console.log(`[${D}{"  Hallo  ".trim()}]`);
            """, "strip", "leerzeichen")
        e("Suchen", "`includes()`, `startsWith()`, `endsWith()`, `indexOf()`",
            "`indexOf` liefert die Position oder `-1`, wenn nichts gefunden wurde.",
            """
            const satz = "JavaScript ist toll";
            console.log(satz.includes("ist"), satz.startsWith("Java"), satz.endsWith("!"));
            console.log(satz.indexOf("toll"), satz.indexOf("Python"));
            """, "suchen", "enthalten")
        e("slice()", "Ein Stück ausschneiden.",
            "`slice(start, ende)` – das Zeichen bei `ende` ist nicht mehr dabei. Negative Werte zählen von hinten.",
            """
            const w = "Schokolade";
            console.log(w.slice(0, 5), w.slice(5), w.slice(-4));
            """, "substring", "teilstring")
        e("split() & join()", "String ↔ Array",
            "`split(trenner)` zerlegt einen String in ein Array, `join(trenner)` setzt ein Array zu einem String zusammen.",
            """
            const teile = "a,b,c".split(",");
            console.log(teile);
            console.log(teile.join(" | "));
            console.log([..."Hey"].reverse().join(""));
            """, "zerlegen", "zusammenfügen")
        e("Ersetzen & Wiederholen", "`replace()`, `replaceAll()`, `repeat()`, `padStart()`",
            "`replace` ersetzt das erste Vorkommen, `replaceAll` alle. `padStart(länge, zeichen)` füllt vorne auf.",
            """
            console.log("Tee und Tee".replace("Tee", "Kakao"));
            console.log("a-b-c".replaceAll("-", "+"));
            console.log("ab".repeat(3), "5".padStart(3, "0"));
            """, "ersetzen", "auffüllen")
    },
    category("js-control", "Entscheidungen", LessonIcon.BRANCH) {
        e("if / else if / else", "Code abhängig von Bedingungen ausführen.",
            "Die Bedingung steht in runden Klammern, der Code in geschweiften Klammern. Der erste zutreffende Zweig gewinnt.",
            """
            const temp = 18;
            if (temp > 25) {
              console.log("heiß");
            } else if (temp > 15) {
              console.log("angenehm");
            } else {
              console.log("kalt");
            }
            """, "wenn", "bedingung")
        e("Vergleiche", "`===`, `!==`, `<`, `>`, `<=`, `>=`",
            "Nimm immer `===` und `!==` – `==` wandelt Typen heimlich um (`5 == \"5\"` ist `true`).",
            """
            console.log(5 === 5, 5 === "5", 5 == "5");
            console.log("a" < "b", 3 >= 3);
            """, "gleich", "ungleich", "==")
        e("Logische Operatoren", "`&&` (und), `||` (oder), `!` (nicht)",
            "`&&` braucht beide Seiten, `||` mindestens eine. Falsy sind: `false`, `0`, `\"\"`, `null`, `undefined`, `NaN`.",
            """
            const alter = 15;
            console.log(alter > 12 && alter < 18);
            console.log(alter < 6 || alter > 65);
            console.log(!true, Boolean(""), Boolean("0"));
            """, "und", "oder", "truthy", "falsy")
        e("Bedingungsoperator ?:", "Kurze Entscheidung zwischen zwei Werten.",
            "`bedingung ? wennJa : wennNein` liefert einen Wert – praktisch in Zuweisungen und Template-Strings.",
            """
            const punkte = 72;
            console.log(punkte >= 50 ? "bestanden" : "nicht bestanden");
            """, "ternary", "ternär")
        e("switch", "Einen Wert mit vielen Fällen vergleichen.",
            "Jeder Fall endet mit `break`, sonst läuft das Programm in den nächsten Fall weiter. `default` greift, wenn nichts passt.",
            """
            const tag = "So";
            switch (tag) {
              case "Sa":
              case "So":
                console.log("Wochenende");
                break;
              default:
                console.log("Werktag");
            }
            """, "case", "break", "default")
        e("?? und ?.", "Ersatzwerte und sicherer Zugriff.",
            "`a ?? b` liefert `b`, wenn `a` `null` oder `undefined` ist. `obj?.x` liefert `undefined`, statt einen Fehler zu werfen, wenn `obj` fehlt.",
            """
            const nutzer = { name: "Ben" };
            console.log(nutzer.spitzname ?? "kein Spitzname");
            console.log(nutzer.adresse?.stadt);
            """, "nullish", "optional chaining")
    },
    category("js-loops", "Schleifen", LessonIcon.LOOP) {
        e("while", "Wiederholen, solange eine Bedingung gilt.",
            "Achte darauf, dass die Bedingung irgendwann `false` wird – sonst läuft die Schleife endlos.",
            """
            let n = 3;
            while (n > 0) {
              console.log(n);
              n--;
            }
            """, "solange", "endlos")
        e("for", "Zählschleife: Start; Bedingung; Schritt",
            "`for (let i = 0; i < 5; i++)` läuft für i = 0, 1, 2, 3, 4.",
            """
            for (let i = 1; i <= 3; i++) {
              console.log("Runde", i);
            }
            """, "zählen", "index")
        e("for…of", "Alle Elemente durchgehen.",
            "Funktioniert mit Arrays, Strings, Maps und Sets. Für Objekte: `Object.entries(obj)`.",
            """
            for (const tier of ["Hund", "Katze"]) {
              console.log(tier);
            }
            for (const zeichen of "Hi") {
              console.log(zeichen);
            }
            """, "foreach", "iterieren")
        e("break & continue", "Schleifen beenden oder Runden überspringen.",
            "`break` beendet die Schleife sofort, `continue` springt zur nächsten Runde.",
            """
            for (let i = 1; i <= 10; i++) {
              if (i % 2 === 0) continue;
              if (i > 7) break;
              console.log(i);
            }
            """, "abbrechen", "überspringen")
        e("for…in", "Die Schlüssel eines Objekts durchgehen.",
            "Für Arrays lieber `for…of` verwenden – `for…in` liefert dort die Indizes als Texte.",
            """
            const preise = { apfel: 1, birne: 2 };
            for (const ware in preise) {
              console.log(ware, preise[ware]);
            }
            """, "schlüssel", "keys")
    },
    category("js-arrays", "Arrays", LessonIcon.BOX) {
        e("Arrays anlegen", "`[1, 2, 3]`, Index, `length`",
            "Der erste Index ist 0, der letzte `length - 1`. Auch `const`-Arrays lassen sich verändern.",
            """
            const farben = ["rot", "grün", "blau"];
            farben[1] = "gelb";
            console.log(farben, farben.length, farben.at(-1));
            """, "liste", "index")
        e("Hinzufügen & Entfernen", "`push`, `pop`, `unshift`, `shift`, `splice`",
            "`push`/`pop` arbeiten hinten, `unshift`/`shift` vorne. `splice(index, anzahl)` entfernt an beliebiger Stelle.",
            """
            const a = [2, 3];
            a.push(4);
            a.unshift(1);
            console.log(a);
            a.splice(1, 2);
            console.log(a, a.pop(), a);
            """, "append", "remove")
        e("Suchen", "`includes`, `indexOf`, `find`, `findIndex`",
            "`includes` liefert `true`/`false`, `indexOf` die Position oder `-1`.",
            """
            const team = ["Ada", "Ben"];
            console.log(team.includes("Ben"), team.indexOf("Ben"), team.indexOf("Zoe"));
            """, "enthalten", "position")
        e("Kopieren & Verbinden", "`[...a]`, `slice()`, `concat()`, `join()`",
            "`const b = a` kopiert nicht – beide Namen zeigen auf dasselbe Array. Eine echte Kopie: `[...a]`.",
            """
            const a = [1, 2];
            const kopie = [...a, 3];
            console.log(a, kopie, a.concat([9]).join("-"));
            """, "kopie", "spread")
        e("Sortieren", "`sort()` mit Vergleichsfunktion",
            "Ohne Vergleichsfunktion wird als Text sortiert! Für Zahlen: `(a, b) => a - b`. `sort()` verändert das Array.",
            """
            const z = [10, 9, 100, 1];
            console.log([...z].sort());
            console.log([...z].sort((a, b) => a - b));
            console.log(["Kiwi", "Apfel"].sort());
            """, "ordnen", "reihenfolge")
        e("reverse() & Verschachteln", "Umdrehen und Arrays in Arrays",
            "`reverse()` dreht das Array selbst um. Verschachtelte Arrays erreichst du mit zwei Indizes: `feld[zeile][spalte]`.",
            """
            console.log([1, 2, 3].reverse());
            const feld = [[1, 2], [3, 4]];
            console.log(feld[1][0]);
            """, "umdrehen", "2d", "matrix")
    },
    category("js-array-methods", "Array-Methoden", LessonIcon.MAGIC) {
        e("forEach", "Für jedes Element etwas tun.",
            "Bekommt Element und Index. Liefert nichts zurück.",
            """
            ["a", "b"].forEach((wert, i) => console.log(i, wert));
            """, "schleife", "callback")
        e("map", "Jedes Element umwandeln → neues Array.",
            "Das Original bleibt unverändert.",
            """
            console.log([1, 2, 3].map((x) => x * 10));
            console.log(["ada", "ben"].map((n) => n.toUpperCase()));
            """, "umwandeln", "transformieren")
        e("filter", "Passende Elemente behalten → neues Array.",
            "Die Funktion gibt `true` für Elemente zurück, die bleiben sollen.",
            """
            console.log([3, 8, 12, 5].filter((x) => x > 6));
            """, "auswählen", "filtern")
        e("find, some, every", "Erstes Element finden, prüfen ob eins/alle passen.",
            "`find` liefert das Element (oder `undefined`), `some`/`every` liefern `true`/`false`.",
            """
            const z = [4, 7, 10];
            console.log(z.find((x) => x > 5), z.some((x) => x > 9), z.every((x) => x > 5));
            """, "suchen", "alle", "mindestens")
        e("reduce", "Alles zu einem Wert zusammenfassen.",
            "`array.reduce((bisher, element) => ..., startwert)` – z. B. für Summen.",
            """
            const summe = [5, 10, 15].reduce((s, z) => s + z, 0);
            console.log(summe);
            """, "summe", "zusammenfassen")
        e("Methoden verketten", "`filter(...).map(...).sort(...)`",
            "Weil `filter` und `map` neue Arrays liefern, lassen sie sich direkt hintereinander aufrufen.",
            """
            const ergebnis = [5, 12, 8, 21]
              .filter((z) => z > 6)
              .map((z) => z * 2)
              .sort((a, b) => a - b);
            console.log(ergebnis);
            """, "kette", "chaining")
    },
    category("js-functions", "Funktionen", LessonIcon.CODE) {
        e("function", "Funktion definieren und aufrufen.",
            "Parameter stehen in den runden Klammern. Aufgerufen wird mit `name(argumente)`.",
            """
            function begruesse(name) {
              console.log("Hallo " + name);
            }
            begruesse("Ada");
            """, "def", "aufrufen")
        e("return", "Ein Ergebnis zurückgeben.",
            "`return` beendet die Funktion. Ohne `return` liefert sie `undefined`.",
            """
            function quadrat(x) {
              return x * x;
            }
            console.log(quadrat(4) + 1);
            """, "rückgabe", "ergebnis")
        e("Standardwerte", "`function f(x = 10)`",
            "Gilt, wenn beim Aufruf kein (oder `undefined` als) Wert übergeben wird.",
            """
            function gruss(name = "Gast") {
              return "Hallo " + name;
            }
            console.log(gruss(), gruss("Ben"));
            """, "default", "parameter")
        e("Pfeilfunktionen", "`(a, b) => a + b`",
            "Kurze Schreibweise. Mit nur einem Ausdruck ohne `{}` und ohne `return`. Haben kein eigenes `this`.",
            """
            const plus = (a, b) => a + b;
            const doppelt = (x) => x * 2;
            console.log(plus(2, 3), doppelt(7));
            """, "arrow", "lambda", "=>")
        e("Rest-Parameter", "`function f(...werte)`",
            "Sammelt beliebig viele Argumente in einem Array.",
            """
            function summe(...zahlen) {
              return zahlen.reduce((s, z) => s + z, 0);
            }
            console.log(summe(1, 2, 3, 4));
            """, "variadisch", "argumente")
        e("Callbacks", "Funktionen als Argument übergeben.",
            "Funktionen sind Werte – man kann sie speichern und anderen Funktionen übergeben.",
            """
            function zweimal(aktion) {
              aktion();
              aktion();
            }
            zweimal(() => console.log("Hey"));
            """, "rückruf", "höhere funktion")
        e("Closures", "Funktionen merken sich ihre Umgebung.",
            "Eine innere Funktion behält Zugriff auf Variablen der äußeren Funktion – auch nachdem diese fertig ist.",
            """
            function zaehler() {
              let n = 0;
              return () => ++n;
            }
            const zaehle = zaehler();
            zaehle();
            console.log(zaehle());
            """, "scope", "gültigkeitsbereich")
    },
    category("js-objects", "Objekte & JSON", LessonIcon.CLASS) {
        e("Objekte anlegen", "`{ schluessel: wert }`",
            "Zugriff mit `obj.name` oder `obj[\"name\"]`. Eigenschaften lassen sich hinzufügen, ändern und mit `delete` entfernen.",
            """
            const auto = { marke: "VW", baujahr: 2019 };
            auto.farbe = "rot";
            console.log(auto.marke, auto["baujahr"], auto);
            """, "dictionary", "eigenschaft")
        e("keys, values, entries", "Objekte durchlaufen.",
            "`Object.keys/values/entries(obj)` liefern Arrays – kombinierbar mit `for…of`.",
            """
            const preise = { apfel: 1, brot: 3 };
            console.log(Object.keys(preise), Object.values(preise));
            for (const [ware, preis] of Object.entries(preise)) {
              console.log(ware, preis);
            }
            """, "schlüssel", "werte")
        e("Methoden & this", "Funktionen in Objekten.",
            "In einer Methode zeigt `this` auf das Objekt. Kurzform: `name() { ... }`.",
            """
            const konto = {
              stand: 10,
              einzahlen(b) {
                this.stand += b;
              },
            };
            konto.einzahlen(5);
            console.log(konto.stand);
            """, "methode", "this")
        e("Destrukturierung", "Werte auspacken.",
            "`const { a, b } = obj;` und `const [x, y] = arr;` – auch mit Standardwerten.",
            """
            const { name, alter = 0 } = { name: "Ada" };
            const [erster, zweiter] = [10, 20, 30];
            console.log(name, alter, erster, zweiter);
            """, "auspacken", "destructuring")
        e("Spread bei Objekten", "`{ ...obj, x: 1 }`",
            "Kopiert alle Eigenschaften in ein neues Objekt – spätere Angaben überschreiben frühere.",
            """
            const basis = { farbe: "blau", groesse: "M" };
            console.log({ ...basis, farbe: "rot" });
            """, "kopieren", "zusammenführen")
        e("JSON", "`JSON.stringify()` und `JSON.parse()`",
            "Daten als Text speichern und wieder einlesen. Ungültiges JSON → `SyntaxError`.",
            """
            const text = JSON.stringify({ a: 1, b: [true] });
            console.log(text);
            console.log(JSON.parse(text).b[0]);
            """, "speichern", "serialisieren")
    },
    category("js-classes", "Klassen", LessonIcon.CLASS) {
        e("class & constructor", "Bauplan für Objekte.",
            "Mit `new` entsteht eine Instanz, der `constructor` richtet sie ein.",
            """
            class Hund {
              constructor(name) {
                this.name = name;
              }
              bellen() {
                return this.name + ": Wuff!";
              }
            }
            console.log(new Hund("Rex").bellen());
            """, "objekt", "instanz", "new")
        e("get & set", "Berechnete und geprüfte Eigenschaften.",
            "Getter werden ohne Klammern aufgerufen. Setter greifen beim Zuweisen ein.",
            """
            class Kreis {
              constructor(r) {
                this.r = r;
              }
              get flaeche() {
                return (Math.PI * this.r ** 2).toFixed(1);
              }
            }
            console.log(new Kreis(2).flaeche);
            """, "getter", "setter")
        e("extends & super", "Vererbung.",
            "Die Unterklasse übernimmt alles von der Elternklasse. `super(...)` ruft deren Konstruktor auf, `super.methode()` deren Methode.",
            """
            class Tier {
              constructor(name) {
                this.name = name;
              }
              laut() {
                return "...";
              }
            }
            class Katze extends Tier {
              laut() {
                return "Miau";
              }
            }
            const k = new Katze("Mimi");
            console.log(k.name, k.laut(), k instanceof Tier);
            """, "vererbung", "instanceof")
        e("static & #privat", "Klassen-Eigenschaften und geschützte Felder.",
            "`static` gehört zur Klasse selbst, `#feld` ist nur innerhalb der Klasse zugänglich.",
            """
            class Zaehler {
              static erstellt = 0;
              #wert = 0;
              constructor() {
                Zaehler.erstellt++;
              }
              plus() {
                return ++this.#wert;
              }
            }
            const z = new Zaehler();
            z.plus();
            console.log(z.plus(), Zaehler.erstellt);
            """, "statisch", "privat", "kapselung")
    },
    category("js-errors", "Fehler", LessonIcon.BUG) {
        e("try / catch / finally", "Fehler abfangen.",
            "Tritt im `try`-Block ein Fehler auf, geht es im `catch` weiter. `finally` läuft immer.",
            """
            try {
              null.wert;
            } catch (e) {
              console.log(e.name, "-", e.message);
            } finally {
              console.log("fertig");
            }
            """, "abfangen", "exception")
        e("throw", "Selbst einen Fehler auslösen.",
            "Üblich: `throw new Error(\"Beschreibung\")`, für falsche Typen `TypeError`, für unerlaubte Werte `RangeError`.",
            """
            function wurzel(x) {
              if (x < 0) throw new RangeError("x muss positiv sein");
              return Math.sqrt(x);
            }
            try {
              wurzel(-1);
            } catch (e) {
              console.log(e.name + ": " + e.message);
            }
            """, "werfen", "auslösen")
        e("Eigene Fehlerklassen", "`class MeinFehler extends Error`",
            "Setze `this.name`, damit die Fehlerart in Meldungen erscheint. Mit `instanceof` unterscheidest du Fehler im `catch`.",
            """
            class EingabeFehler extends Error {
              constructor(m) {
                super(m);
                this.name = "EingabeFehler";
              }
            }
            try {
              throw new EingabeFehler("Zahl fehlt");
            } catch (e) {
              console.log(e instanceof EingabeFehler, `${D}{e.name}: ${D}{e.message}`);
            }
            """, "custom", "fehlerklasse")
    },
    category("js-async", "Asynchron", LessonIcon.ROCKET) {
        e("setTimeout & setInterval", "Später oder wiederholt ausführen.",
            "Zeiten in Millisekunden. Das Programm wartet nicht, sondern läuft sofort weiter. `clearInterval(id)` stoppt einen Intervall-Timer.",
            """
            setTimeout(() => console.log("nach 1 s"), 1000);
            let n = 0;
            const id = setInterval(() => {
              n++;
              console.log("Tick", n);
              if (n === 2) clearInterval(id);
            }, 300);
            console.log("sofort");
            """, "timer", "warten", "verzögern")
        e("Promise", "Ein späteres Ergebnis.",
            "`new Promise((resolve, reject) => ...)`, Reaktion mit `.then()` und `.catch()`.",
            """
            const p = new Promise((resolve) => setTimeout(() => resolve(42), 200));
            p.then((wert) => console.log("Ergebnis:", wert));
            """, "versprechen", "then", "catch")
        e("async & await", "Asynchron wie synchron schreiben.",
            "`await` wartet auf ein Promise – nur innerhalb einer `async`-Funktion. Fehler mit `try`/`catch` abfangen.",
            """
            const warte = (ms) => new Promise((r) => setTimeout(r, ms));
            async function main() {
              console.log("Start");
              await warte(500);
              console.log("Nach einer halben Sekunde");
            }
            main();
            """, "warten", "asynchron")
        e("Promise.all", "Auf mehrere Promises gleichzeitig warten.",
            "Liefert ein Array mit allen Ergebnissen in der ursprünglichen Reihenfolge. `Promise.race` liefert das schnellste.",
            """
            const lade = (x, ms) => new Promise((r) => setTimeout(() => r(x), ms));
            Promise.all([lade("A", 300), lade("B", 100)]).then((e) => console.log(e));
            """, "parallel", "gleichzeitig")
    },
    category("js-tools", "Werkzeuge", LessonIcon.STAR) {
        e("Set", "Sammlung ohne Doppelte.",
            "`add`, `has`, `delete`, `size`. Duplikate entfernen: `[...new Set(array)]`.",
            """
            const s = new Set([1, 2, 2, 3]);
            s.add(4);
            console.log(s.size, s.has(2), [...s]);
            """, "menge", "eindeutig")
        e("Map", "Schlüssel-Wert-Paare mit Methoden.",
            "`set`, `get`, `has`, `delete`, `size` – Schlüssel dürfen beliebige Werte sein.",
            """
            const m = new Map();
            m.set("Ada", 36);
            m.set("Ben", 12);
            console.log(m.get("Ada"), m.size);
            for (const [name, alter] of m) console.log(name, alter);
            """, "wörterbuch", "dictionary")
        e("Math", "Runden, Minimum, Maximum, Wurzel …",
            "`round`, `floor`, `ceil`, `max`, `min`, `abs`, `sqrt`, `PI`.",
            """
            console.log(Math.round(2.5), Math.floor(2.9), Math.ceil(2.1));
            console.log(Math.max(1, 5, 3), Math.sqrt(16), Math.PI.toFixed(2));
            """, "runden", "mathe")
        e("Zufallszahlen", "`Math.random()`",
            "Liefert eine Zahl von 0 bis unter 1. Ganze Zahl von 1 bis 6: `Math.floor(Math.random() * 6) + 1`.",
            """
            const wurf = Math.floor(Math.random() * 6) + 1;
            console.log(wurf >= 1 && wurf <= 6);
            """, "zufall", "random", "würfel")
        e("Date", "Datum und Uhrzeit.",
            "Monate zählen ab 0! `getFullYear()`, `getMonth()`, `getDate()`, `getDay()` (0 = Sonntag).",
            """
            const d = new Date(2025, 11, 24);
            console.log(d.getFullYear(), d.getMonth() + 1, d.getDate());
            """, "datum", "uhrzeit", "zeit")
        e("Kommazahlen runden", "`toFixed()` und das 0.1-Problem",
            "Kommazahlen sind im Computer nicht immer exakt. `toFixed(n)` liefert einen gerundeten Text mit n Nachkommastellen.",
            """
            console.log(0.1 + 0.2);
            console.log((0.1 + 0.2).toFixed(2));
            console.log(Math.round(2.345 * 100) / 100);
            """, "gleitkomma", "nachkommastellen")
    }
)
