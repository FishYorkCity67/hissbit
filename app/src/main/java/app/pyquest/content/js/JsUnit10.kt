package app.pyquest.content.js

import app.pyquest.content.CourseBuilder
import app.pyquest.data.LessonIcon

fun CourseBuilder.jsUnit10() = unit("Klassen", "Baupläne für eigene Objekte") {
    lesson("Klassen & Objekte", "Ein Bauplan, viele Objekte", LessonIcon.CLASS) {
        text("Eine **Klasse** ist ein Bauplan für Objekte. Mit `new` erzeugst du daraus beliebig viele **Instanzen**. Der `constructor` läuft beim Erzeugen und richtet die Eigenschaften ein.")
        code("""
            class Hund {
              constructor(name, alter) {
                this.name = name;
                this.alter = alter;
              }
            }

            const bello = new Hund("Bello", 3);
            const rex = new Hund("Rex", 7);
            console.log(bello.name, rex.alter);
            console.log(bello);
        """, output = """
            Bello 7
            Hund { name: 'Bello', alter: 3 }
        """)
        text("In der Klasse definierst du auch **Methoden** – sie gelten für alle Instanzen. `this` ist dabei das jeweilige Objekt.")
        code("""
            class Hund {
              constructor(name) {
                this.name = name;
                this.tricks = [];
              }
              lerne(trick) {
                this.tricks.push(trick);
              }
              zeige() {
                return `${D}{this.name} kann: ${D}{this.tricks.join(", ")}`;
              }
            }

            const bello = new Hund("Bello");
            bello.lerne("Sitz");
            bello.lerne("Pfote");
            console.log(bello.zeige());
        """, output = "Bello kann: Sitz, Pfote")
        tip("Klassennamen schreibt man mit großem Anfangsbuchstaben: `Hund`, `Bankkonto`, `Spieler`. Variablen und Methoden dagegen klein.")
        warn("Ohne `new` geht es nicht: `Hund(\"Bello\")` wirft einen TypeError.")

        output("""
            class Punkt {
              constructor(x, y) {
                this.x = x;
                this.y = y;
              }
            }
            const p = new Punkt(3, 4);
            console.log(p.x + p.y);
        """, "7", "34", "undefined", "Punkt { x: 3, y: 4 }")
        output("""
            class Box {
              constructor(inhalt) {
                this.inhalt = inhalt;
              }
            }
            console.log(new Box("Kekse"));
        """, "Box { inhalt: 'Kekse' }", "{ inhalt: 'Kekse' }", "Kekse", "Box")
        choice("Womit erzeugst du ein neues Objekt aus der Klasse `Auto`?", "new Auto()", "Auto.new()", "create Auto()", "Auto()", mono = true)
        typeOutput("""
            class Zaehler {
              constructor() {
                this.wert = 0;
              }
              plus() {
                this.wert++;
              }
            }
            const z = new Zaehler();
            z.plus();
            z.plus();
            z.plus();
            console.log(z.wert);
        """, "3")
        fill("Vervollständige den Konstruktor.", """
            class Stadt {
              ??(name) {
                this.name = name;
              }
            }
            console.log(new Stadt("Wien").name);
        """, answers = listOf("constructor"), distractors = listOf("init", "Stadt", "new"), output = "Wien")
        truth("Alle Instanzen einer Klasse teilen sich dieselben Methoden.", true)
        output("""
            class Licht {
              constructor() {
                this.an = false;
              }
              schalte() {
                this.an = !this.an;
                return this.an;
              }
            }
            const l = new Licht();
            l.schalte();
            console.log(l.schalte());
        """, "false", "true", "undefined", "Licht { an: false }")
        write("Schreibe eine Klasse `Rechteck` mit `breite` und `hoehe` im Konstruktor und einer Methode `flaeche()`, die die Fläche zurückgibt.",
            solution = """
                class Rechteck {
                  constructor(breite, hoehe) {
                    this.breite = breite;
                    this.hoehe = hoehe;
                  }
                  flaeche() {
                    return this.breite * this.hoehe;
                  }
                }
            """,
            test = """
                const r = new Rechteck(4, 5);
                if (r.breite !== 4 || r.hoehe !== 5) throw new Error("Speichere breite und hoehe im Konstruktor mit this");
                if (r.flaeche() !== 20) throw new Error("new Rechteck(4, 5).flaeche() sollte 20 sein");
                if (new Rechteck(1.5, 2).flaeche() !== 3) throw new Error("new Rechteck(1.5, 2).flaeche() sollte 3 sein");
                console.log("✅ Alle Tests bestanden!");
            """,
            mustContain = listOf("class Rechteck"),
            hint = "Im `constructor` mit `this.breite = breite;` speichern, die Methode gibt `this.breite * this.hoehe` zurück.")
    }

    lesson("Getter, Setter & toString", "Berechnete Eigenschaften und geprüfte Werte", LessonIcon.CLASS) {
        text("Mit `get` definierst du eine **berechnete Eigenschaft**: Sie sieht aus wie eine normale Eigenschaft, wird aber bei jedem Zugriff neu berechnet – ohne Klammern beim Aufruf.")
        code("""
            class Kreis {
              constructor(radius) {
                this.radius = radius;
              }
              get durchmesser() {
                return this.radius * 2;
              }
            }
            const k = new Kreis(5);
            console.log(k.durchmesser);
            k.radius = 8;
            console.log(k.durchmesser);
        """, output = """
            10
            16
        """)
        text("Mit `set` kannst du beim Zuweisen eingreifen – zum Beispiel ungültige Werte abfangen:")
        code("""
            class Thermostat {
              constructor() {
                this._grad = 20;
              }
              get grad() {
                return this._grad;
              }
              set grad(wert) {
                if (wert < 5 || wert > 30) {
                  console.log("Ungültig:", wert);
                  return;
                }
                this._grad = wert;
              }
            }
            const t = new Thermostat();
            t.grad = 25;
            t.grad = 99;
            console.log(t.grad);
        """, output = """
            Ungültig: 99
            25
        """)
        text("Eine Methode `toString()` bestimmt, wie ein Objekt als Text aussieht – etwa in einem Template-String:")
        code("""
            class Bruch {
              constructor(z, n) {
                this.z = z;
                this.n = n;
              }
              toString() {
                return `${D}{this.z}/${D}{this.n}`;
              }
            }
            const b = new Bruch(3, 4);
            console.log(`Ein Bruch: ${D}{b}`);
        """, output = "Ein Bruch: 3/4")
        tip("Ein Unterstrich wie in `_grad` signalisiert: „Bitte nicht direkt benutzen.“ Echt geschützte Felder lernst du gleich mit `#` kennen.")

        output("""
            class Quadrat {
              constructor(s) {
                this.s = s;
              }
              get flaeche() {
                return this.s * this.s;
              }
            }
            console.log(new Quadrat(3).flaeche);
        """, "9", "undefined", "[Function: flaeche]", "6")
        choice("Wie greifst du auf einen Getter `alter` zu?", "obj.alter", "obj.alter()", "obj.get(alter)", "obj.getAlter()", mono = true)
        typeOutput("""
            class Name {
              constructor(vor, nach) {
                this.vor = vor;
                this.nach = nach;
              }
              get voll() {
                return this.vor + " " + this.nach;
              }
            }
            console.log(new Name("Ada", "Lovelace").voll);
        """, "Ada Lovelace")
        output("""
            class Geld {
              constructor(cent) {
                this.cent = cent;
              }
              toString() {
                return (this.cent / 100).toFixed(2) + " €";
              }
            }
            console.log(`Preis: ${D}{new Geld(250)}`);
        """, "Preis: 2.50 €", "Preis: 250", "Preis: [object Object]", "Preis: Geld { cent: 250 }")
        fill("Mache `istLeer` zu einer berechneten Eigenschaft.", """
            class Liste {
              constructor() {
                this.elemente = [];
              }
              ?? istLeer() {
                return this.elemente.length === 0;
              }
            }
            console.log(new Liste().istLeer);
        """, answers = listOf("get"), distractors = listOf("set", "static", "function"), output = "true")
        truth("Ein Getter wird mit runden Klammern aufgerufen: `obj.getter()`.", false)
        write("Schreibe eine Klasse `Temperatur` mit einer Eigenschaft `celsius` (aus dem Konstruktor) und einem Getter `fahrenheit`, der `celsius * 9 / 5 + 32` liefert.",
            solution = """
                class Temperatur {
                  constructor(celsius) {
                    this.celsius = celsius;
                  }
                  get fahrenheit() {
                    return this.celsius * 9 / 5 + 32;
                  }
                }
            """,
            test = """
                const t = new Temperatur(100);
                if (t.fahrenheit !== 212) throw new Error("100 °C sollten 212 °F sein (Getter ohne Klammern!)");
                t.celsius = 0;
                if (t.fahrenheit !== 32) throw new Error("Nach celsius = 0 sollte fahrenheit 32 sein");
                console.log("✅ Alle Tests bestanden!");
            """,
            mustContain = listOf("get fahrenheit"),
            hint = "`get fahrenheit() { return this.celsius * 9 / 5 + 32; }`")
    }

    lesson("Vererbung", "Klassen erweitern mit extends", LessonIcon.CLASS) {
        text("Mit `extends` erbt eine Klasse alles von einer anderen und kann es erweitern. `super(...)` ruft dabei den Konstruktor der Elternklasse auf – das muss im Konstruktor **vor** dem ersten `this` passieren.")
        code("""
            class Tier {
              constructor(name) {
                this.name = name;
              }
              vorstellen() {
                return `Ich bin ${D}{this.name}.`;
              }
            }

            class Schlange extends Tier {
              constructor(name, laenge) {
                super(name);
                this.laenge = laenge;
              }
              zischen() {
                return "Ssssss!";
              }
            }

            const monty = new Schlange("Monty", 2);
            console.log(monty.vorstellen());
            console.log(monty.zischen());
        """, output = """
            Ich bin Monty.
            Ssssss!
        """)
        text("Eine Unterklasse kann eine geerbte Methode **überschreiben**. Mit `super.methode()` rufst du dabei die Version der Elternklasse auf.")
        code("""
            class Tier {
              constructor(name) {
                this.name = name;
              }
              laut() {
                return "...";
              }
              sprich() {
                return `${D}{this.name}: ${D}{this.laut()}`;
              }
            }
            class Katze extends Tier {
              laut() {
                return "Miau";
              }
            }
            class Loewe extends Katze {
              laut() {
                return super.laut().toUpperCase() + "!";
              }
            }
            console.log(new Tier("Ding").sprich());
            console.log(new Katze("Mimi").sprich());
            console.log(new Loewe("Leo").sprich());
        """, output = """
            Ding: ...
            Mimi: Miau
            Leo: MIAU!
        """)
        text("Mit `instanceof` prüfst du, ob ein Objekt zu einer Klasse – oder einer ihrer Elternklassen – gehört:")
        code("""
            class Fahrzeug {}
            class Auto extends Fahrzeug {}
            const a = new Auto();
            console.log(a instanceof Auto, a instanceof Fahrzeug, a instanceof Array);
        """, output = "true true false")

        output("""
            class A {
              hallo() {
                return "A";
              }
            }
            class B extends A {}
            console.log(new B().hallo());
        """, "A", "B", "undefined", "Fehler")
        output("""
            class A {
              wer() {
                return "A";
              }
            }
            class B extends A {
              wer() {
                return "B" + super.wer();
              }
            }
            console.log(new B().wer());
        """, "BA", "B", "AB", "A")
        choice("Was macht `super(name)` im Konstruktor einer Unterklasse?", "Ruft den Konstruktor der Elternklasse auf", "Erzeugt ein neues Objekt der Unterklasse", "Löscht die Elternklasse", "Gibt den Namen aus")
        typeOutput("""
            class Form {}
            class Kreis extends Form {}
            console.log(new Kreis() instanceof Form);
        """, "true")
        fill("Lass `Vogel` von `Tier` erben.", """
            class Tier {
              atmen() {
                return "atmet";
              }
            }
            class Vogel ?? Tier {}
            console.log(new Vogel().atmen());
        """, answers = listOf("extends"), distractors = listOf("inherits", "implements", "from"), output = "atmet")
        truth("In einer Unterklasse muss `super(...)` vor dem ersten `this` im Konstruktor stehen.", true)
        write("""
            Schreibe eine Klasse `Mitarbeiter` mit `name` und `gehalt` und einer Methode `info()`, die `<name> verdient <gehalt> €` liefert.
            Schreibe dann `Chef extends Mitarbeiter`: Der Konstruktor bekommt zusätzlich `bonus`, und `info()` hängt ` + <bonus> € Bonus` an.
        """.trimIndent(),
            solution = """
                class Mitarbeiter {
                  constructor(name, gehalt) {
                    this.name = name;
                    this.gehalt = gehalt;
                  }
                  info() {
                    return `${D}{this.name} verdient ${D}{this.gehalt} €`;
                  }
                }

                class Chef extends Mitarbeiter {
                  constructor(name, gehalt, bonus) {
                    super(name, gehalt);
                    this.bonus = bonus;
                  }
                  info() {
                    return `${D}{super.info()} + ${D}{this.bonus} € Bonus`;
                  }
                }
            """,
            test = """
                const m = new Mitarbeiter("Ben", 3000);
                if (m.info() !== "Ben verdient 3000 €") throw new Error("Mitarbeiter.info() stimmt nicht: " + m.info());
                const c = new Chef("Ada", 5000, 800);
                if (!(c instanceof Mitarbeiter)) throw new Error("Chef soll von Mitarbeiter erben (extends)");
                if (c.info() !== "Ada verdient 5000 € + 800 € Bonus") throw new Error("Chef.info() stimmt nicht: " + c.info());
                console.log("✅ Alle Tests bestanden!");
            """,
            mustContain = listOf("extends", "super"),
            hint = "Im `Chef`-Konstruktor zuerst `super(name, gehalt)`, in `info()` kannst du `super.info()` wiederverwenden.")
    }

    lesson("Private Felder & static", "Daten schützen und Klassen-Werkzeuge", LessonIcon.CLASS) {
        text("Felder mit `#` vor dem Namen sind **privat**: Nur Code innerhalb der Klasse kommt an sie heran. So kann niemand von außen zum Beispiel einen Kontostand einfach überschreiben.")
        code("""
            class Konto {
              #stand = 0;

              einzahlen(betrag) {
                if (betrag <= 0) return false;
                this.#stand += betrag;
                return true;
              }
              get stand() {
                return this.#stand;
              }
            }

            const k = new Konto();
            k.einzahlen(50);
            k.einzahlen(-10);
            console.log(k.stand);
        """, output = "50")
        text("`static` macht eine Methode oder Eigenschaft zu einem Teil der **Klasse selbst** statt der einzelnen Objekte. Du rufst sie über den Klassennamen auf.")
        code("""
            class Mathe {
              static PI = 3.14159;
              static kreisflaeche(r) {
                return Mathe.PI * r * r;
              }
            }
            console.log(Mathe.kreisflaeche(2).toFixed(2));

            class Ticket {
              static anzahl = 0;
              constructor() {
                Ticket.anzahl++;
                this.nummer = Ticket.anzahl;
              }
            }
            new Ticket();
            const t = new Ticket();
            console.log(t.nummer, Ticket.anzahl);
        """, output = """
            12.57
            2 2
        """)
        tip("Du kennst schon eingebaute statische Methoden: `Math.floor()`, `Number.isNaN()` oder `Object.keys()` gehören alle zu ihrer Klasse, nicht zu einzelnen Werten.")
        warn("Ein Zugriff wie `k.#stand` von außen ist ein **SyntaxError** – das Programm startet dann gar nicht erst.")

        output("""
            class Geheim {
              #code = 1234;
              pruefe(versuch) {
                return versuch === this.#code;
              }
            }
            const g = new Geheim();
            console.log(g.pruefe(1234), g.code);
        """, "true undefined", "true 1234", "false undefined", "Fehler")
        output("""
            class Spiel {
              static spieler = 0;
              constructor() {
                Spiel.spieler++;
              }
            }
            new Spiel();
            new Spiel();
            new Spiel();
            console.log(Spiel.spieler);
        """, "3", "1", "0", "undefined")
        choice("Wie rufst du die statische Methode `erzeuge` der Klasse `Karte` auf?", "Karte.erzeuge()", "new Karte().erzeuge()", "erzeuge(Karte)", "Karte#erzeuge()", mono = true)
        typeOutput("""
            class Umrechner {
              static kmZuMeilen(km) {
                return km * 0.5;
              }
            }
            console.log(Umrechner.kmZuMeilen(10));
        """, "5")
        fill("Mache das Feld `pin` privat – von außen soll es unsichtbar sein.", """
            class Handy {
              ??pin = "0000";
              entsperren(code) {
                return code === this.??pin;
              }
            }
            const h = new Handy();
            console.log(h.entsperren("0000"));
            console.log(Object.keys(h).length);
        """, answers = listOf("#", "#"), distractors = listOf("_", "private "), output = "true\n0",
            explain = "Mit `_pin` würde es zwar funktionieren – aber das Feld wäre von außen sichtbar und änderbar. Nur `#` macht es wirklich privat.")
        truth("Auf ein privates Feld `#x` kann man von außen mit `obj.#x` zugreifen.", false)
        write("""
            Schreibe eine Klasse `Spardose` mit einem privaten Feld `#betrag` (Start: 0), einer Methode `einwerfen(euro)`, die nur positive Beträge annimmt,
            und einem Getter `inhalt`, der den Betrag liefert.
        """.trimIndent(),
            solution = """
                class Spardose {
                  #betrag = 0;
                  einwerfen(euro) {
                    if (euro > 0) this.#betrag += euro;
                  }
                  get inhalt() {
                    return this.#betrag;
                  }
                }
            """,
            test = """
                const s = new Spardose();
                s.einwerfen(2);
                s.einwerfen(5);
                s.einwerfen(-3);
                if (s.inhalt !== 7) throw new Error("Nach 2 + 5 (und -3 ignoriert) sollte inhalt 7 sein, ist aber " + s.inhalt);
                if (Object.keys(s).length !== 0) throw new Error("Der Betrag soll in einem privaten Feld #betrag liegen, nicht in this.betrag");
                console.log("✅ Alle Tests bestanden!");
            """,
            mustContain = listOf("#betrag", "get inhalt"),
            hint = "`#betrag = 0;` als Feld, `if (euro > 0) this.#betrag += euro;` und `get inhalt() { return this.#betrag; }`")
    }

    checkpoint()
}
