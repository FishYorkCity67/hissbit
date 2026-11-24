package app.pyquest.content.js

import app.pyquest.data.GlossaryTerm
import app.pyquest.data.PythonTip
import app.pyquest.data.RefEntry

private fun g(term: String, meaning: String) = GlossaryTerm(term, meaning)

val JsGlossary: List<GlossaryTerm> = listOf(
    g("Algorithmus", "Eine genaue Schritt-für-Schritt-Anleitung, um ein Problem zu lösen – wie ein Rezept für den Computer."),
    g("Argument", "Der konkrete Wert, den du einer Funktion beim Aufruf übergibst, z. B. `5` in `quadrat(5)`."),
    g("Array", "Geordnete Liste von Werten: `[1, 2, 3]`. Der erste Index ist 0."),
    g("Arrow Function", "Pfeilfunktion – kurze Schreibweise für Funktionen: `(a, b) => a + b`."),
    g("Asynchron", "Code, der später fertig wird (Timer, Netzwerk), ohne dass das Programm in der Zwischenzeit stehen bleibt."),
    g("async / await", "Schlüsselwörter, mit denen asynchroner Code wie normaler Code von oben nach unten lesbar wird."),
    g("Ausdruck", "Etwas, das einen Wert ergibt, z. B. `3 + 4` oder `name.length`."),
    g("Bedingung", "Ein Ausdruck, der `true` oder `false` ergibt – z. B. in `if` oder `while`."),
    g("Block", "Code zwischen geschweiften Klammern `{ }`, z. B. der Inhalt einer Schleife oder Funktion."),
    g("Boolean", "Datentyp mit den Werten `true` und `false`."),
    g("Browser", "Programm zum Anzeigen von Webseiten (Chrome, Firefox, Safari …). Jeder Browser kann JavaScript ausführen."),
    g("Bug", "Ein Fehler im Programm. Ihn zu finden und zu beheben heißt **Debugging**."),
    g("Callback", "Eine Funktion, die einer anderen Funktion übergeben wird, damit diese sie später aufruft."),
    g("camelCase", "Schreibweise für Namen in JavaScript: erstes Wort klein, jedes weitere groß – `anzahlSpieler`."),
    g("Closure", "Eine Funktion, die sich die Variablen ihrer Umgebung merkt – auch wenn die äußere Funktion schon fertig ist."),
    g("Konsole", "Das Ausgabefenster, in dem `console.log()` Text anzeigt. Im Browser über die Entwicklerwerkzeuge (F12) erreichbar."),
    g("const", "Legt eine Variable an, die nicht neu zugewiesen werden kann."),
    g("Destrukturierung", "Werte aus Objekten oder Arrays direkt in Variablen auspacken: `const { name } = person;`."),
    g("DOM", "Document Object Model – die Webseite als Baum von Objekten, den JavaScript im Browser verändern kann."),
    g("Eigenschaft", "Ein benannter Wert in einem Objekt, z. B. `person.name`. Englisch: Property."),
    g("Event Loop", "Der Mechanismus, der Timer, Promises und Ereignisse nacheinander abarbeitet, sobald der aktuelle Code fertig ist."),
    g("falsy", "Werte, die in Bedingungen als falsch gelten: `false`, `0`, `\"\"`, `null`, `undefined`, `NaN`."),
    g("Funktion", "Ein benannter, wiederverwendbarer Codeblock – definiert mit `function` oder als Pfeilfunktion."),
    g("Getter", "Eine Methode mit `get`, die wie eine Eigenschaft gelesen wird – ohne Klammern."),
    g("Gültigkeitsbereich", "(Scope) Der Bereich, in dem eine Variable sichtbar ist – bei `let` und `const` der umgebende Block."),
    g("Hoisting", "JavaScript „zieht“ Funktionsdeklarationen nach oben: Man kann eine `function` schon vor ihrer Definition aufrufen."),
    g("Index", "Die Position eines Elements in einem Array oder String. Beginnt bei 0."),
    g("Instanz", "Ein konkretes Objekt einer Klasse: Bei `const rex = new Hund()` ist `rex` eine Instanz von `Hund`."),
    g("JSON", "JavaScript Object Notation – ein Textformat für Daten, das fast jede App und jeder Server versteht."),
    g("Klasse", "Ein Bauplan für Objekte, definiert mit `class`."),
    g("Kommentar", "Text nach `//` oder zwischen `/*` und `*/`, den JavaScript ignoriert."),
    g("Konkatenation", "Das Verbinden von Strings mit `+`: `\"Hallo \" + name`."),
    g("Konstruktor", "Die Methode `constructor` einer Klasse – sie läuft beim Erzeugen eines Objekts mit `new`."),
    g("let", "Legt eine Variable an, deren Wert sich ändern darf."),
    g("Literal", "Ein direkt hingeschriebener Wert, z. B. `42`, `\"Hi\"`, `[1, 2]` oder `{ a: 1 }`."),
    g("Map", "Sammlung von Schlüssel-Wert-Paaren mit Methoden wie `set`, `get` und `has`."),
    g("Methode", "Eine Funktion, die zu einem Objekt gehört: `text.toUpperCase()`."),
    g("Modul", "Eine eigene JavaScript-Datei, deren Inhalte mit `export` und `import` geteilt werden."),
    g("NaN", "„Not a Number“ – das Ergebnis ungültiger Rechnungen, z. B. `Number(\"abc\")`."),
    g("Node.js", "Eine Umgebung, die JavaScript außerhalb des Browsers ausführt – z. B. auf Servern."),
    g("npm", "Der Paketmanager von Node.js, mit dem man fertige Bibliotheken installiert."),
    g("null", "Ein Wert für „absichtlich leer“."),
    g("Objekt", "Eine Sammlung von Eigenschaften: `{ name: \"Ada\", alter: 36 }`."),
    g("Operator", "Zeichen für eine Operation, z. B. `+`, `===`, `&&`."),
    g("Parameter", "Der Platzhalter-Name in der Funktionsdefinition: In `function f(x)` ist `x` ein Parameter."),
    g("Promise", "Ein Objekt für ein späteres Ergebnis – es wird erfüllt (`resolve`) oder abgelehnt (`reject`)."),
    g("Prototyp", "Das Objekt, von dem ein anderes Objekt Eigenschaften und Methoden erbt. Klassen bauen darauf auf."),
    g("Rekursion", "Wenn sich eine Funktion selbst aufruft."),
    g("Rest-Parameter", "`...werte` in der Parameterliste – sammelt beliebig viele Argumente in einem Array."),
    g("Rückgabewert", "Der Wert, den eine Funktion mit `return` zurückgibt."),
    g("Schleife", "Wiederholt Code: `for`, `for…of` oder `while`."),
    g("Schlüsselwort", "Ein reserviertes Wort mit fester Bedeutung, z. B. `if`, `for`, `function`, `class`."),
    g("Set", "Eine Sammlung ohne doppelte Werte."),
    g("Spread", "`...` packt ein Array oder Objekt aus: `[...a, ...b]`."),
    g("String", "Zeichenkette / Text: `\"Hallo\"`."),
    g("Syntax", "Die Grammatik einer Programmiersprache – die Regeln, wie Code geschrieben werden muss."),
    g("Template-String", "String in Backticks, in dem `${D}{...}` durch den Wert eines Ausdrucks ersetzt wird."),
    g("this", "In einer Methode das Objekt, zu dem die Methode gehört."),
    g("truthy", "Alle Werte, die in Bedingungen als wahr gelten – also alles außer den falsy-Werten."),
    g("Typ", "Die Art eines Werts: `number`, `string`, `boolean` … herausfinden mit `typeof`."),
    g("Typumwandlung", "Einen Wert in einen anderen Typ umwandeln, z. B. `Number(\"5\")`."),
    g("undefined", "Der Wert von Variablen, die noch keinen Wert bekommen haben."),
    g("Variable", "Ein Name für einen gespeicherten Wert: `let alter = 14;`."),
    g("Vererbung", "Eine Klasse übernimmt mit `extends` die Eigenschaften und Methoden einer anderen."),
    g("Zuweisung", "Einer Variable mit `=` einen Wert geben."),
)

private fun err(id: String, title: String, summary: String, body: String, code: String) =
    RefEntry("js-$id", title, summary, body.trimIndent().trim(), code.trimIndent().trimEnd(), listOf("fehler", "error", id))

val JsErrorLexicon: List<RefEntry> = listOf(
    err("syntax", "SyntaxError", "Die Schreibweise ist ungültig – das Programm startet gar nicht.", """
        **Typische Ursachen:** fehlende Klammer `)` oder `}`, nicht geschlossenes Anführungszeichen, fehlendes Komma zwischen Argumenten, ein Wort wie `else` an falscher Stelle.

        **So behebst du ihn:** Schau dir die markierte Stelle (`^`) **und die Zeile davor** an. Bei „Unexpected end of input“ fehlt meist eine schließende Klammer am Ende.
    """, """
        // Fehler: Klammer fehlt
        // console.log("Hallo";
        console.log("Hallo");
    """),
    err("reference", "ReferenceError: … is not defined", "JavaScript kennt einen Namen nicht.", """
        **Ursachen:** Tippfehler (auch Groß-/Kleinschreibung!), Variable noch nicht angelegt, Anführungszeichen um einen Text vergessen.

        Beispiel: `console.log(Nachricht)` statt `console.log(nachricht)`.
    """, """
        const nachricht = "Hallo";
        console.log(nachricht); // nicht: console.log(Nachricht)
    """),
    err("tdz", "ReferenceError: Cannot access … before initialization", "Eine Variable wird benutzt, bevor ihre `let`/`const`-Zeile lief.", """
        Mit `let` und `const` gibt es eine Variable erst ab der Zeile, in der sie angelegt wird.

        **Lösung:** Verschiebe die Deklaration nach oben – vor die erste Benutzung.
    """, """
        // Fehler: console.log(punkte); let punkte = 5;
        let punkte = 5;
        console.log(punkte);
    """),
    err("undefined-prop", "TypeError: Cannot read properties of undefined", "Links vom Punkt steht `undefined` oder `null`.", """
        **Ursachen:** Die Variable hat (noch) keinen Wert, eine Funktion gibt nichts zurück, ein Array-Index existiert nicht oder eine Eigenschaft heißt anders.

        **Lösungen:** Prüfen, woher der Wert kommt. `obj?.wert` bricht ruhig mit `undefined` ab, `?? "Ersatz"` liefert einen Ersatzwert.
    """, """
        const nutzer = { name: "Ada" };
        // Fehler: nutzer.adresse.stadt
        console.log(nutzer.adresse?.stadt ?? "keine Stadt");
    """),
    err("not-function", "TypeError: … is not a function", "Etwas wird mit `()` aufgerufen, das keine Funktion ist.", """
        **Ursachen:** Tippfehler im Methodennamen (`toUppercase` statt `toUpperCase`), Array-Methode auf einem Objekt (`obj.map`), oder eine Variable überdeckt die Funktion.
    """, """
        const text = "hallo";
        // Fehler: text.toUppercase()
        console.log(text.toUpperCase());
    """),
    err("const", "TypeError: Assignment to constant variable", "Eine `const`-Variable soll einen neuen Wert bekommen.", """
        `const` verhindert neue Zuweisungen. Soll sich der Wert ändern, lege die Variable mit `let` an.

        Hinweis: Den **Inhalt** eines `const`-Arrays oder -Objekts darfst du trotzdem ändern.
    """, """
        let zaehler = 0; // nicht: const zaehler = 0;
        zaehler++;
        console.log(zaehler);
    """),
    err("stack", "RangeError: Maximum call stack size exceeded", "Eine Funktion ruft sich endlos selbst auf.", """
        Bei Rekursion braucht jede Funktion einen **Basisfall**, an dem sie aufhört, sich selbst aufzurufen.
    """, """
        function countdown(n) {
          if (n === 0) return; // Basisfall – ohne ihn läuft es endlos
          console.log(n);
          countdown(n - 1);
        }
        countdown(3);
    """),
    err("nan", "NaN als Ergebnis", "Keine Fehlermeldung – aber ein unbrauchbares Ergebnis.", """
        `NaN` entsteht, wenn mit etwas gerechnet wird, das keine Zahl ist – z. B. mit `undefined` oder einem Text wie `"abc"`.

        **Tipp:** Eingaben mit `Number()` umwandeln und mit `Number.isNaN()` prüfen.
    """, """
        const eingabe = "12a";
        const zahl = Number(eingabe);
        if (Number.isNaN(zahl)) {
          console.log("Bitte eine gültige Zahl eingeben!");
        }
    """),
    err("concat", "\"5\" + 3 = \"53\"", "Plus verbindet Texte, statt zu rechnen.", """
        Ist bei `+` eine Seite ein String, hängt JavaScript die Werte aneinander. Das passiert oft mit Eingaben aus `prompt()`.

        **Lösung:** vorher mit `Number()` umwandeln.
    """, """
        const a = "5";
        console.log(a + 3);         // "53"
        console.log(Number(a) + 3); // 8
    """),
    err("object-object", "[object Object] im Text", "Ein Objekt wird direkt in einen String eingebaut.", """
        Objekte haben keine sinnvolle Textform. Baue stattdessen ihre Eigenschaften ein – oder nutze `JSON.stringify(obj)`.
    """, """
        const p = { name: "Ada", alter: 36 };
        console.log("Person: " + JSON.stringify(p));
        console.log(`${D}{p.name} ist ${D}{p.alter}`);
    """),
    err("pending", "Promise { <pending> }", "Statt des Ergebnisses erscheint nur ein Promise.", """
        Asynchrone Funktionen liefern ein Promise. Auf das Ergebnis musst du mit `await` (in einer `async`-Funktion) oder mit `.then()` warten.
    """, """
        async function lade() {
          return 42;
        }
        async function main() {
          const wert = await lade(); // nicht: const wert = lade();
          console.log(wert);
        }
        main();
    """),
    err("equals", "= statt ===", "Eine Zuweisung in einer Bedingung.", """
        `if (x = 5)` speichert 5 in `x` und ist immer wahr. Zum Vergleichen brauchst du `===`.
    """, """
        let x = 3;
        if (x === 5) {
          console.log("fünf");
        } else {
          console.log("nicht fünf");
        }
    """),
)

private fun tip(title: String, code: String) = PythonTip(title, code.trimIndent().trim())

val JsTips: List<PythonTip> = listOf(
    tip("Werte tauschen ohne Hilfsvariable", "[a, b] = [b, a];"),
    tip("Duplikate entfernen", "const eindeutig = [...new Set(liste)];"),
    tip("Ersatzwert mit ??", "const name = eingabe ?? \"Gast\";"),
    tip("Sicherer Zugriff mit ?.", "const stadt = nutzer?.adresse?.stadt;"),
    tip("Zahlen von 1 bis n", "const zahlen = Array.from({ length: 5 }, (_, i) => i + 1);"),
    tip("Letztes Element", "const letztes = liste.at(-1);"),
    tip("Zufälliges Element", "const zufall = liste[Math.floor(Math.random() * liste.length)];"),
    tip("String umdrehen", "const rueckwaerts = [...text].reverse().join(\"\");"),
    tip("Objekt kopieren und ändern", "const neu = { ...alt, farbe: \"rot\" };"),
    tip("Summe eines Arrays", "const summe = zahlen.reduce((s, z) => s + z, 0);"),
    tip("Nach Eigenschaft sortieren", "spieler.sort((a, b) => b.punkte - a.punkte);"),
    tip("Objekt durchlaufen", "for (const [schluessel, wert] of Object.entries(obj)) {\n  console.log(schluessel, wert);\n}"),
    tip("Führende Nullen", "const nummer = String(7).padStart(3, \"0\"); // \"007\""),
    tip("Auf 2 Stellen runden", "const gerundet = Math.round(wert * 100) / 100;"),
    tip("Tabelle in der Konsole", "console.table([{ name: \"Ada\", punkte: 9 }, { name: \"Ben\", punkte: 7 }]);"),
    tip("Maximum eines Arrays", "const max = Math.max(...zahlen);"),
    tip("Ganze Zahl prüfen", "Number.isInteger(4.0); // true"),
    tip("Array leeren", "liste.length = 0;"),
    tip("Bedingte Ausführung mit &&", "istAngemeldet && console.log(\"Willkommen zurück!\");"),
    tip("Zuweisung nur bei null/undefined", "einstellungen.farbe ??= \"blau\";"),
    tip("Warten mit await", "const warte = (ms) => new Promise((r) => setTimeout(r, ms));\nasync function main() {\n  await warte(1000);\n}"),
    tip("Parallel laden", "async function lade() {\n  const [profil, fotos] = await Promise.all([ladeProfil(), ladeFotos()]);\n}"),
    tip("Parameter auspacken", "function zeige({ name, alter = 0 }) {\n  console.log(name, alter);\n}"),
    tip("Text ohne Groß/klein vergleichen", "const gleich = a.toLowerCase() === b.toLowerCase();"),
    tip("Schöne JSON-Ausgabe", "console.log(JSON.stringify(daten, null, 2));"),
    tip("Tiefe Kopie", "const kopie = structuredClone(original);"),
    tip("Objekt einfrieren", "const config = Object.freeze({ farbe: \"blau\" });"),
    tip("Zahl mit Tausenderpunkt", "(1234567.891).toLocaleString(\"de-DE\"); // \"1.234.567,891\""),
    tip("Anzahl zählen mit Objekt", "zaehler[wort] = (zaehler[wort] ?? 0) + 1;"),
    tip("Mehrzeiliger Text", "const brief = `Hallo,\nwie geht's?`;"),
)
