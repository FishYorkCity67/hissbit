package app.pyquest.content.js

import app.pyquest.content.CourseBuilder
import app.pyquest.data.LessonIcon

fun CourseBuilder.jsUnit08() = unit("Objekte", "Zusammengehörige Daten mit Namen") {
    lesson("Objekte anlegen", "Eigenschaften mit Namen und Wert", LessonIcon.BOX) {
        text("Ein **Objekt** fasst zusammengehörige Werte unter Namen zusammen. Jede **Eigenschaft** besteht aus einem Schlüssel und einem Wert. Objekte stehen in geschweiften Klammern.")
        code("""
            const person = {
              name: "Ada",
              alter: 36,
              programmiert: true,
            };
            console.log(person);
            console.log(person.name);
        """, output = """
            { name: 'Ada', alter: 36, programmiert: true }
            Ada
        """)
        text("Auf Eigenschaften greifst du mit dem **Punkt** zu – oder mit **eckigen Klammern** und dem Namen als Text. Die Klammern brauchst du, wenn der Name in einer Variable steht.")
        code("""
            const auto = { marke: "VW", farbe: "rot" };
            console.log(auto["marke"]);
            const feld = "farbe";
            console.log(auto[feld]);
        """, output = """
            VW
            rot
        """)
        text("Eigenschaften lassen sich jederzeit ändern, hinzufügen und mit `delete` entfernen:")
        code("""
            const spieler = { name: "Monty", punkte: 10 };
            spieler.punkte += 5;
            spieler.level = 2;
            delete spieler.name;
            console.log(spieler);
            console.log(spieler.name);
        """, output = """
            { punkte: 15, level: 2 }
            undefined
        """)
        tip("Objekte dürfen andere Objekte und Arrays enthalten: `person.adresse.stadt` greift auf ein verschachteltes Objekt zu.")

        output("""
            const buch = { titel: "Momo", seiten: 304 };
            console.log(buch.seiten);
        """, "304", "Momo", "seiten", "undefined")
        output("""
            const tier = { art: "Katze" };
            console.log(tier.alter);
        """, "undefined", "null", "Fehler", "0")
        typeOutput("""
            const p = { x: 1, y: 2 };
            p.x = 10;
            console.log(p.x + p.y);
        """, "12")
        choice("Wie greifst du auf die Eigenschaft zu, deren Name in der Variable `key` steht?", "obj[key]", "obj.key", "obj(key)", "obj->key", mono = true,
            explain = "`obj.key` würde eine Eigenschaft suchen, die wirklich „key“ heißt.")
        output("""
            const user = { name: "Ben" };
            user.alter = 12;
            console.log(user);
        """, "{ name: 'Ben', alter: 12 }", "{ name: 'Ben' }", "{ alter: 12 }", "[ 'Ben', 12 ]")
        fill("Greife auf die Stadt in der Adresse zu.", """
            const kunde = { name: "Cleo", adresse: { stadt: "Hamburg" } };
            console.log(kunde.??.stadt);
        """, answers = listOf("adresse"), distractors = listOf("name", "stadt", "kunde"), output = "Hamburg")
        truth("Auch bei einem `const`-Objekt kann man Eigenschaften ändern.", true)
        write("Lege ein Objekt `handy` mit den Eigenschaften `marke` (`\"Samsung\"`) und `speicher` (`256`) an. Erhöhe danach `speicher` auf `512` und gib das Objekt aus.",
            solution = """
                const handy = { marke: "Samsung", speicher: 256 };
                handy.speicher = 512;
                console.log(handy);
            """,
            output = "{ marke: 'Samsung', speicher: 512 }",
            hint = "`const handy = { marke: \"Samsung\", speicher: 256 };` – danach `handy.speicher = 512;`.")
    }

    lesson("Objekte durchlaufen", "keys, values, entries und Listen von Objekten", LessonIcon.LOOP) {
        text("`Object.keys()` liefert alle Schlüssel als Array, `Object.values()` alle Werte und `Object.entries()` Paare aus Schlüssel und Wert.")
        code("""
            const preise = { apfel: 0.5, brot: 2.2, milch: 1.1 };
            console.log(Object.keys(preise));
            console.log(Object.values(preise));
            console.log(Object.entries(preise).length);
        """, output = """
            [ 'apfel', 'brot', 'milch' ]
            [ 0.5, 2.2, 1.1 ]
            3
        """)
        text("Mit `for…of` und `Object.entries()` gehst du alle Paare durch. `[schluessel, wert]` packt jedes Paar direkt in zwei Variablen aus:")
        code("""
            const preise = { apfel: 0.5, brot: 2.2 };
            for (const [ware, preis] of Object.entries(preise)) {
              console.log(`${D}{ware}: ${D}{preis.toFixed(2)} €`);
            }
        """, output = """
            apfel: 0.50 €
            brot: 2.20 €
        """)
        text("Mit `in` prüfst du, ob es eine Eigenschaft gibt. Und sehr häufig sind **Arrays von Objekten** – etwa eine Liste von Schülern:")
        code("""
            const klasse = [
              { name: "Ada", note: 1 },
              { name: "Ben", note: 3 },
            ];
            for (const s of klasse) {
              console.log(s.name, "hat eine", s.note);
            }
            console.log("note" in klasse[0]);
        """, output = """
            Ada hat eine 1
            Ben hat eine 3
            true
        """)
        tip("Es gibt auch `for (const key in obj)`. Damit bekommst du die Schlüssel – bei Arrays aber besser `for…of` benutzen.")

        output("""console.log(Object.keys({ a: 1, b: 2 }));""", "[ 'a', 'b' ]", "[ 1, 2 ]", "{ a: 1, b: 2 }", "2")
        output("""console.log(Object.values({ x: 3, y: 4 }));""", "[ 3, 4 ]", "[ 'x', 'y' ]", "7", "[ [ 'x', 3 ], [ 'y', 4 ] ]")
        typeOutput("""
            const lager = { aepfel: 3, birnen: 5 };
            let summe = 0;
            for (const anzahl of Object.values(lager)) {
              summe += anzahl;
            }
            console.log(summe);
        """, "8")
        output("""
            const auto = { marke: "Fiat" };
            console.log("farbe" in auto);
        """, "false", "true", "undefined", "Fehler")
        fill("Gehe alle Schlüssel-Wert-Paare durch.", """
            const team = { torwart: "Ada", stuermer: "Ben" };
            for (const [rolle, name] of Object.??(team)) {
              console.log(rolle, name);
            }
        """, answers = listOf("entries"), distractors = listOf("keys", "values", "pairs"), output = "torwart Ada\nstuermer Ben")
        output("""
            const liste = [{ n: 1 }, { n: 2 }, { n: 3 }];
            let s = 0;
            for (const e of liste) s += e.n;
            console.log(s);
        """, "6", "3", "123", "[object Object]")
        truth("`Object.keys(obj)` liefert die Werte eines Objekts.", false, explain = "Die Werte liefert `Object.values()`. `keys` sind die Namen der Eigenschaften.")
        write("Gib für jedes Produkt im Objekt `bestand` eine Zeile im Format `<Produkt>: <Anzahl> Stück` aus.",
            starter = """
                const bestand = { stifte: 12, hefte: 5, radierer: 3 };
            """,
            solution = """
                const bestand = { stifte: 12, hefte: 5, radierer: 3 };
                for (const [produkt, anzahl] of Object.entries(bestand)) {
                  console.log(`${D}{produkt}: ${D}{anzahl} Stück`);
                }
            """,
            output = """
                stifte: 12 Stück
                hefte: 5 Stück
                radierer: 3 Stück
            """,
            hint = "`for (const [produkt, anzahl] of Object.entries(bestand))`")
        write("Finde im Array `filme` den Film mit der **besten Bewertung** und gib seinen Titel aus.",
            starter = """
                const filme = [
                  { titel: "Shrek", bewertung: 7.9 },
                  { titel: "Coco", bewertung: 8.4 },
                  { titel: "Cars", bewertung: 7.2 },
                ];
            """,
            solution = """
                const filme = [
                  { titel: "Shrek", bewertung: 7.9 },
                  { titel: "Coco", bewertung: 8.4 },
                  { titel: "Cars", bewertung: 7.2 },
                ];
                let bester = filme[0];
                for (const film of filme) {
                  if (film.bewertung > bester.bewertung) bester = film;
                }
                console.log(bester.titel);
            """,
            output = "Coco",
            hint = "Merke dir den bisher besten Film und vergleiche in der Schleife die `bewertung`.")
    }

    lesson("Methoden & this", "Objekte, die etwas können", LessonIcon.CLASS) {
        text("Eine Eigenschaft kann auch eine **Funktion** sein – dann heißt sie **Methode**. In einer Methode zeigt `this` auf das Objekt selbst.")
        code("""
            const konto = {
              besitzer: "Ada",
              stand: 100,
              einzahlen(betrag) {
                this.stand += betrag;
                return this.stand;
              },
              info() {
                return `${D}{this.besitzer} hat ${D}{this.stand} €`;
              },
            };
            konto.einzahlen(50);
            console.log(konto.info());
        """, output = "Ada hat 150 €")
        text("`einzahlen(betrag) { ... }` ist die Kurzform von `einzahlen: function (betrag) { ... }`. Beides funktioniert gleich.")
        warn("Pfeilfunktionen haben **kein eigenes** `this`. Für Methoden, die `this` brauchen, nimm deshalb die Kurzform – keine Pfeilfunktion.")
        code("""
            const hund = {
              name: "Bello",
              bellen() {
                return this.name + " sagt Wuff!";
              },
            };
            console.log(hund.bellen());
        """, output = "Bello sagt Wuff!")
        tip("Auch Strings und Arrays sind Objekte mit Methoden: `\"abc\".toUpperCase()` oder `liste.push(4)` funktionieren genau so.")

        output("""
            const kreis = {
              radius: 2,
              durchmesser() {
                return this.radius * 2;
              },
            };
            console.log(kreis.durchmesser());
        """, "4", "2", "undefined", "radius * 2")
        output("""
            const zaehler = {
              wert: 0,
              plus() {
                this.wert++;
              },
            };
            zaehler.plus();
            zaehler.plus();
            console.log(zaehler.wert);
        """, "2", "0", "1", "undefined")
        choice("Worauf zeigt `this` in einer Methode wie `obj.methode()`?", "Auf das Objekt `obj`", "Auf die Methode selbst", "Auf das ganze Programm", "Auf die letzte Variable")
        typeOutput("""
            const katze = {
              name: "Mimi",
              vorstellen() {
                return "Ich bin " + this.name;
              },
            };
            console.log(katze.vorstellen());
        """, "Ich bin Mimi")
        fill("Greife in der Methode auf das eigene Objekt zu.", """
            const lampe = {
              an: false,
              schalten() {
                ??.an = !??.an;
              },
            };
            lampe.schalten();
            console.log(lampe.an);
        """, answers = listOf("this", "this"), distractors = listOf("self", "lampe.this"), output = "true")
        truth("Eine Methode ist eine Funktion, die in einem Objekt gespeichert ist.", true)
        write("Ergänze das Objekt `rechteck` um eine Methode `flaeche()`, die `breite * hoehe` zurückgibt.",
            starter = """
                const rechteck = {
                  breite: 4,
                  hoehe: 3,
                };
            """,
            solution = """
                const rechteck = {
                  breite: 4,
                  hoehe: 3,
                  flaeche() {
                    return this.breite * this.hoehe;
                  },
                };
            """,
            test = """
                if (typeof rechteck.flaeche !== "function") throw new Error("rechteck braucht eine Methode flaeche()");
                if (rechteck.flaeche() !== 12) throw new Error("flaeche() sollte 12 liefern");
                rechteck.breite = 10;
                if (rechteck.flaeche() !== 30) throw new Error("Nutze this.breite und this.hoehe – nach breite = 10 sollte flaeche() 30 sein");
                console.log("✅ Alle Tests bestanden!");
            """,
            mustContain = listOf("this"),
            hint = "`flaeche() { return this.breite * this.hoehe; },`")
    }

    lesson("Destrukturierung & ?.", "Werte bequem auspacken", LessonIcon.MAGIC) {
        text("Mit **Destrukturierung** packst du Eigenschaften direkt in Variablen gleichen Namens aus. Fehlt eine Eigenschaft, kannst du einen Standardwert angeben.")
        code("""
            const film = { titel: "Coco", jahr: 2017 };
            const { titel, jahr, sprache = "Deutsch" } = film;
            console.log(titel, jahr, sprache);
        """, output = "Coco 2017 Deutsch")
        text("Bei Arrays geht das über die Position – und damit lassen sich sogar zwei Variablen elegant tauschen:")
        code("""
            const [erster, zweiter] = ["Gold", "Silber", "Bronze"];
            console.log(erster, zweiter);
            let a = 1;
            let b = 2;
            [a, b] = [b, a];
            console.log(a, b);
        """, output = """
            Gold Silber
            2 1
        """)
        text("Mit `...` (Spread) kopierst du ein Objekt und kannst dabei Eigenschaften überschreiben. Mit `?.` (Optional Chaining) greifst du gefahrlos auf vielleicht fehlende Teile zu – statt eines Fehlers kommt `undefined`.")
        code("""
            const basis = { farbe: "blau", groesse: "M" };
            const variante = { ...basis, farbe: "rot" };
            console.log(variante);

            const kunde = { name: "Ben" };
            console.log(kunde.adresse?.stadt);
            console.log(kunde.adresse?.stadt ?? "unbekannt");
        """, output = """
            { farbe: 'rot', groesse: 'M' }
            undefined
            unbekannt
        """)
        tip("Destrukturierung klappt auch bei Parametern: `function zeige({ name, alter }) { ... }` packt das übergebene Objekt direkt aus.")

        output("""
            const { x, y } = { x: 3, y: 7 };
            console.log(x * y);
        """, "21", "37", "10", "undefined")
        output("""
            const [a, , c] = [1, 2, 3];
            console.log(a, c);
        """, "1 3", "1 2", "2 3", "1 undefined")
        typeOutput("""
            const { farbe = "grün" } = {};
            console.log(farbe);
        """, "grün")
        output("""
            const user = {};
            console.log(user.profil?.bild);
        """, "undefined", "Fehler", "null", "{}")
        output("""
            const a = { x: 1, y: 2 };
            const b = { ...a, y: 5 };
            console.log(a.y, b.y);
        """, "2 5", "5 5", "2 2", "5 2")
        fill("Packe `name` und `alter` aus dem Objekt aus.", """
            const person = { name: "Ada", alter: 36 };
            const ?? name, alter ?? = person;
            console.log(name, alter);
        """, answers = listOf("{", "}"), distractors = listOf("[", "]"), output = "Ada 36")
        truth("`kunde.adresse.stadt` wirft einen Fehler, wenn `kunde.adresse` fehlt – `kunde.adresse?.stadt` nicht.", true)
        write("Schreibe eine Funktion `beschreibe(tier)`, die ein Objekt mit `name` und `art` bekommt und `<name> ist ein(e) <art>` zurückgibt. Fehlt `art`, soll `Tier` eingesetzt werden. Nutze Destrukturierung.",
            solution = """
                function beschreibe({ name, art = "Tier" }) {
                  return `${D}{name} ist ein(e) ${D}{art}`;
                }
            """,
            test = """
                if (beschreibe({ name: "Monty", art: "Schlange" }) !== "Monty ist ein(e) Schlange") throw new Error("Falsch für Monty");
                if (beschreibe({ name: "Rex" }) !== "Rex ist ein(e) Tier") throw new Error("Ohne art sollte \"Tier\" eingesetzt werden");
                console.log("✅ Alle Tests bestanden!");
            """,
            hint = "`function beschreibe({ name, art = \"Tier\" }) { ... }`")
    }

    checkpoint()
}
