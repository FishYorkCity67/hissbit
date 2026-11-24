package app.pyquest.content

import app.pyquest.data.LessonIcon

fun CourseBuilder.unit11() = unit("Objektorientierung", "Eigene Klassen, Objekte und Vererbung") {
    lesson("Klassen & Objekte", "Baupläne für eigene Datentypen", LessonIcon.CLASS) {
        text("Mit **Klassen** baust du eigene Datentypen. Eine Klasse ist wie ein **Bauplan** – die **Objekte** (Instanzen) sind die Dinge, die nach diesem Bauplan gebaut werden.")
        code("""
            class Hund:
                def __init__(self, name, rasse):
                    self.name = name
                    self.rasse = rasse

            bello = Hund("Bello", "Dackel")
            luna = Hund("Luna", "Pudel")
            print(bello.name, "ist ein", bello.rasse)
            print(luna.name)
        """, output = """
            Bello ist ein Dackel
            Luna
        """)
        text("""
            - `class Hund:` definiert die Klasse (Klassennamen schreibt man in **PascalCase**)
            - `__init__` ist der **Konstruktor** – er wird beim Erstellen eines Objekts automatisch aufgerufen
            - `self` ist das Objekt selbst; mit `self.name = name` speicherst du ein **Attribut**
        """)
        tip("`Hund(\"Bello\", \"Dackel\")` ruft im Hintergrund `__init__(self, \"Bello\", \"Dackel\")` auf – `self` übergibt Python automatisch.")
        text("Attribute kann man jederzeit lesen und ändern:")
        code("""
            class Spieler:
                def __init__(self, name):
                    self.name = name
                    self.punkte = 0

            s = Spieler("Ada")
            s.punkte += 10
            print(s.name, s.punkte)
        """, output = "Ada 10")

        output("""
            class Katze:
                def __init__(self, name):
                    self.name = name

            k = Katze("Mimi")
            print(k.name)
        """, "Mimi", "name", "Katze", "Fehler")
        choice("Wie heißt die Methode, die beim Erstellen eines Objekts automatisch aufgerufen wird?", "__init__", "__start__", "create", "new", mono = true)
        truth("`self` steht für das Objekt, mit dem gerade gearbeitet wird.", true)
        typeOutput("""
            class Box:
                def __init__(self, inhalt):
                    self.inhalt = inhalt

            a = Box(3)
            b = Box(5)
            print(a.inhalt + b.inhalt)
        """, "8")
        fill("Speichere die Marke als Attribut.", """
            class Auto:
                def __init__(self, marke):
                    ??.marke = marke

            auto1 = Auto("VW")
            auto2 = Auto("BMW")
            print(auto1.marke, auto2.marke)
        """, answers = listOf("self"), distractors = listOf("this", "Auto", "marke"), output = "VW BMW",
            explain = "`self` ist das Objekt, das gerade gebaut wird – so bekommt **jedes** Auto seine eigene Marke.")
        match("class" to "Bauplan definieren", "__init__" to "Konstruktor", "self" to "das Objekt selbst", "objekt.attribut" to "auf Daten zugreifen")
        output("""
            class Zaehler:
                def __init__(self):
                    self.wert = 0

            z = Zaehler()
            z.wert += 5
            z.wert += 2
            print(z.wert)
        """, "7", "5", "0", "Fehler")
        write("Erstelle eine Klasse `Buch` mit den Attributen `titel` und `seiten`. Erzeuge das Buch `\"Python lernen\"` mit `320` Seiten und gib aus: `Python lernen hat 320 Seiten`.",
            solution = """
                class Buch:
                    def __init__(self, titel, seiten):
                        self.titel = titel
                        self.seiten = seiten

                b = Buch("Python lernen", 320)
                print(b.titel, "hat", b.seiten, "Seiten")
            """,
            output = "Python lernen hat 320 Seiten",
            mustContain = listOf("class Buch", "__init__"),
            hint = "Im `__init__` mit `self.titel = titel` und `self.seiten = seiten` speichern.")
    }

    lesson("Methoden", "Funktionen, die zu Objekten gehören", LessonIcon.CLASS) {
        text("**Methoden** sind Funktionen, die zu einer Klasse gehören. Ihr erster Parameter ist immer `self` – so können sie auf die Attribute des Objekts zugreifen:")
        code("""
            class Konto:
                def __init__(self, inhaber):
                    self.inhaber = inhaber
                    self.stand = 0

                def einzahlen(self, betrag):
                    self.stand += betrag

                def anzeigen(self):
                    print(f"{self.inhaber}: {self.stand} €")

            k = Konto("Ada")
            k.einzahlen(50)
            k.einzahlen(25)
            k.anzeigen()
        """, output = "Ada: 75 €")
        text("Methoden können auch Werte zurückgeben und andere Methoden aufrufen:")
        code("""
            class Rechteck:
                def __init__(self, breite, hoehe):
                    self.breite = breite
                    self.hoehe = hoehe

                def flaeche(self):
                    return self.breite * self.hoehe

                def ist_quadrat(self):
                    return self.breite == self.hoehe

            r = Rechteck(4, 4)
            print(r.flaeche(), r.ist_quadrat())
        """, output = "16 True")
        tip("Der große Vorteil: **Daten** (Attribute) und **Verhalten** (Methoden) gehören zusammen. Das macht große Programme übersichtlich.")

        output("""
            class A:
                def hallo(self):
                    return "Hi"

            print(A().hallo())
        """, "Hi", "hallo", "A", "Fehler")
        typeOutput("""
            class Kreis:
                def __init__(self, r):
                    self.r = r

                def durchmesser(self):
                    return 2 * self.r

            print(Kreis(7).durchmesser())
        """, "14")
        choice("Was ist der erste Parameter jeder normalen Methode?", "self", "this", "cls", "objekt", mono = true)
        truth("Eine Methode kann die Attribute ihres Objekts verändern.", true)
        output("""
            class Lampe:
                def __init__(self):
                    self.an = False

                def schalten(self):
                    self.an = not self.an

            l = Lampe()
            l.schalten()
            l.schalten()
            l.schalten()
            print(l.an)
        """, "True", "False", "3", "Fehler")
        fill("Ergänze die Methode, die das Alter um 1 erhöht.", """
            class Person:
                def __init__(self, alter):
                    self.alter = alter

                def geburtstag(??):
                    self.alter += 1

            p = Person(13)
            p.geburtstag()
            print(p.alter)
        """, answers = listOf("self"), distractors = listOf("alter", "Person", "p"), output = "14")
        arrange("Baue eine Klasse `Hund` mit einer Methode `bellen`, die `Wuff!` ausgibt, und rufe sie auf.", """
            class Hund:
                def bellen(self):
                    print("Wuff!")
            Hund().bellen()
        """, output = "Wuff!")
        write("Ergänze die Klasse `Warenkorb` um eine Methode `summe()`, die die Summe aller Preise in `self.preise` zurückgibt. Lege dann zwei Artikel (`4.5` und `3.0`) hinein und gib die Summe aus.",
            starter = """
                class Warenkorb:
                    def __init__(self):
                        self.preise = []

                    def hinzufuegen(self, preis):
                        self.preise.append(preis)
            """,
            solution = """
                class Warenkorb:
                    def __init__(self):
                        self.preise = []

                    def hinzufuegen(self, preis):
                        self.preise.append(preis)

                    def summe(self):
                        return sum(self.preise)

                k = Warenkorb()
                k.hinzufuegen(4.5)
                k.hinzufuegen(3.0)
                print(k.summe())
            """,
            output = "7.5",
            test = """
                w = Warenkorb()
                assert w.summe() == 0
                w.hinzufuegen(2)
                assert w.summe() == 2
            """,
            mustContain = listOf("def summe"),
            hint = "`def summe(self): return sum(self.preise)`")
    }

    lesson("Vererbung", "Klassen erweitern mit super()", LessonIcon.CLASS) {
        text("Mit **Vererbung** baust du eine Klasse auf einer anderen auf. Die **Kindklasse** übernimmt alle Attribute und Methoden der **Elternklasse** und kann eigene hinzufügen oder ändern:")
        code("""
            class Tier:
                def __init__(self, name):
                    self.name = name

                def vorstellen(self):
                    print(f"Ich bin {self.name}.")

                def laut(self):
                    return "..."

            class Hund(Tier):
                def laut(self):
                    return "Wuff!"

            class Katze(Tier):
                def laut(self):
                    return "Miau!"

            for tier in [Hund("Bello"), Katze("Mimi")]:
                tier.vorstellen()
                print(tier.laut())
        """, output = """
            Ich bin Bello.
            Wuff!
            Ich bin Mimi.
            Miau!
        """)
        text("Braucht die Kindklasse **zusätzliche** Attribute, ruft sie mit `super()` den Konstruktor der Elternklasse auf:")
        code("""
            class Fahrzeug:
                def __init__(self, raeder):
                    self.raeder = raeder

            class Fahrrad(Fahrzeug):
                def __init__(self, gaenge):
                    super().__init__(2)
                    self.gaenge = gaenge

            f = Fahrrad(21)
            print(f.raeder, f.gaenge)
        """, output = "2 21")
        tip("`isinstance(bello, Tier)` ist `True` – ein Hund ist ja auch ein Tier.")
        warn("Vererbung lohnt sich bei einer echten „ist ein“-Beziehung: Ein Hund **ist ein** Tier. Ein Auto **hat** Räder, ist aber selbst kein Rad.")

        output("""
            class A:
                def f(self):
                    return "A"

            class B(A):
                pass

            print(B().f())
        """, "A", "B", "Fehler", "None", explain = "B erbt die Methode `f` von A.")
        output("""
            class A:
                def f(self):
                    return "A"

            class B(A):
                def f(self):
                    return "B"

            print(B().f())
        """, "B", "A", "AB", "Fehler", explain = "B überschreibt die Methode `f`.")
        choice("Wie erbt `Katze` von `Tier`?", "class Katze(Tier):", "class Katze extends Tier:", "class Katze < Tier:", "class Katze: Tier", mono = true)
        truth("Mit `super()` kann man Methoden der Elternklasse aufrufen.", true)
        typeOutput("""
            class Tier:
                pass

            class Hund(Tier):
                pass

            print(isinstance(Hund(), Tier))
        """, "True")
        fill("Rufe den Konstruktor der Elternklasse auf.", """
            class Person:
                def __init__(self, name):
                    self.name = name

            class Schueler(Person):
                def __init__(self, name, klasse):
                    ??().__init__(name)
                    self.klasse = klasse

            s = Schueler("Ada", "8b")
            print(s.name, s.klasse)
        """, answers = listOf("super"), distractors = listOf("self", "Person", "parent"), output = "Ada 8b")
        output("""
            class Basis:
                def hallo(self):
                    return "Hallo"

            class Kind(Basis):
                def hallo(self):
                    return super().hallo() + " Welt"

            print(Kind().hallo())
        """, "Hallo Welt", "Hallo", " Welt", "Fehler")
        write("Erstelle eine Klasse `Form` mit der Methode `flaeche()`, die `0` zurückgibt. Erstelle die Kindklassen `Quadrat(seite)` und `Kreis(radius)`, die `flaeche()` überschreiben (Kreis: `3.14 * r * r`). Gib die Flächen von `Quadrat(3)` und `Kreis(2)` aus.",
            solution = """
                class Form:
                    def flaeche(self):
                        return 0

                class Quadrat(Form):
                    def __init__(self, seite):
                        self.seite = seite

                    def flaeche(self):
                        return self.seite * self.seite

                class Kreis(Form):
                    def __init__(self, radius):
                        self.radius = radius

                    def flaeche(self):
                        return 3.14 * self.radius * self.radius

                print(Quadrat(3).flaeche())
                print(Kreis(2).flaeche())
            """,
            output = """
                9
                12.56
            """,
            test = """
                assert isinstance(Quadrat(1), Form)
                assert isinstance(Kreis(1), Form)
                assert Form().flaeche() == 0
            """,
            mustContain = listOf("class Form", "(Form)"),
            hint = "`class Quadrat(Form):` mit eigenem `__init__` und eigener `flaeche`-Methode.")
    }

    lesson("Magische Methoden", "__str__, __eq__, __add__ und Co.", LessonIcon.MAGIC) {
        text("**Magische Methoden** (auch *Dunder-Methoden*, wegen der doppelten Unterstriche) bestimmen, wie sich deine Objekte mit eingebauten Funktionen und Operatoren verhalten.")
        text("`__str__` legt fest, was `print()` anzeigt. Ohne sie siehst du nur eine kryptische Speicheradresse:")
        code("""
            class Punkt:
                def __init__(self, x, y):
                    self.x = x
                    self.y = y

                def __str__(self):
                    return f"({self.x}, {self.y})"

            print(Punkt(3, 4))
        """, output = "(3, 4)")
        text("""
            Weitere wichtige Dunder-Methoden:
            - `__repr__` – die „technische“ Darstellung, z.B. in Listen
            - `__eq__` – was `==` bedeutet
            - `__len__` – was `len()` liefert
            - `__add__` – was `+` macht
            - `__lt__` – was `<` bedeutet (damit klappt auch `sorted()`)
        """)
        code("""
            class Geld:
                def __init__(self, euro):
                    self.euro = euro

                def __add__(self, other):
                    return Geld(self.euro + other.euro)

                def __eq__(self, other):
                    return self.euro == other.euro

                def __repr__(self):
                    return f"Geld({self.euro})"

            summe = Geld(5) + Geld(7)
            print(summe)
            print(summe == Geld(12))
            print([Geld(1), Geld(2)])
        """, output = """
            Geld(12)
            True
            [Geld(1), Geld(2)]
        """)
        tip("Gibt es kein `__str__`, nutzt `print()` automatisch `__repr__`.")

        output("""
            class A:
                def __str__(self):
                    return "Ich bin A"

            print(A())
        """, "Ich bin A", "A", "<__main__.A object>", "Fehler")
        choice("Welche Methode bestimmt, was `len(objekt)` liefert?", "__len__", "__size__", "__count__", "len", mono = true)
        typeOutput("""
            class Team:
                def __init__(self, *spieler):
                    self.spieler = spieler

                def __len__(self):
                    return len(self.spieler)

            print(len(Team("Ada", "Alan", "Grace")))
        """, "3")
        match("__str__" to "Text für print()", "__eq__" to "Vergleich mit ==", "__add__" to "Operator +", "__len__" to "Funktion len()", "__lt__" to "Operator <")
        truth("Ohne `__eq__` sind zwei verschiedene Objekte mit gleichen Werten bei `==` gleich.", false,
            explain = "Ohne `__eq__` prüft Python nur, ob es **dasselbe** Objekt ist.")
        fill("Lass `print()` den Namen der Stadt anzeigen.", """
            class Stadt:
                def __init__(self, name):
                    self.name = name

                def ??(self):
                    return self.name

            print(Stadt("Hamburg"))
        """, answers = listOf("__str__"), distractors = listOf("__init__", "__len__", "str"), output = "Hamburg")
        output("""
            class V:
                def __init__(self, x):
                    self.x = x

                def __add__(self, o):
                    return V(self.x + o.x)

            print((V(2) + V(3)).x)
        """, "5", "23", "V(5)", "Fehler")
        write("Erstelle eine Klasse `Bruch(zaehler, nenner)` mit `__str__` (Ausgabe wie `1/2`) und `__mul__`, die zwei Brüche multipliziert. Gib `Bruch(1, 2) * Bruch(3, 4)` aus – das Ergebnis ist `3/8`.",
            solution = """
                class Bruch:
                    def __init__(self, zaehler, nenner):
                        self.zaehler = zaehler
                        self.nenner = nenner

                    def __str__(self):
                        return f"{self.zaehler}/{self.nenner}"

                    def __mul__(self, other):
                        return Bruch(self.zaehler * other.zaehler, self.nenner * other.nenner)

                print(Bruch(1, 2) * Bruch(3, 4))
            """,
            output = "3/8",
            test = """
                b = Bruch(2, 3) * Bruch(5, 7)
                assert str(b) == "10/21"
            """,
            mustContain = listOf("__mul__", "__str__"),
            hint = "`__mul__` multipliziert Zähler mit Zähler und Nenner mit Nenner.")
    }

    lesson("Klassen-Profis", "Klassenattribute, @property und Co.", LessonIcon.STAR) {
        text("**Klassenattribute** gehören der Klasse selbst und werden von allen Objekten geteilt:")
        code("""
            class Spieler:
                anzahl = 0

                def __init__(self, name):
                    self.name = name
                    Spieler.anzahl += 1

            Spieler("Ada")
            Spieler("Alan")
            print(Spieler.anzahl)
        """, output = "2")
        text("Mit `@property` benutzt man eine Methode wie ein Attribut – ohne Klammern. Praktisch für berechnete Werte:")
        code("""
            class Temperatur:
                def __init__(self, celsius):
                    self.celsius = celsius

                @property
                def fahrenheit(self):
                    return self.celsius * 9 / 5 + 32

            t = Temperatur(25)
            print(t.fahrenheit)
        """, output = "77.0")
        text("`@classmethod` bekommt statt des Objekts die **Klasse** (`cls`) – ideal für alternative Konstruktoren. `@staticmethod` bekommt gar nichts automatisch – eine normale Funktion, die thematisch zur Klasse gehört:")
        code("""
            class Datum:
                def __init__(self, tag, monat, jahr):
                    self.tag, self.monat, self.jahr = tag, monat, jahr

                @classmethod
                def aus_text(cls, text):
                    t, m, j = text.split(".")
                    return cls(int(t), int(m), int(j))

                @staticmethod
                def ist_schaltjahr(jahr):
                    return jahr % 4 == 0 and (jahr % 100 != 0 or jahr % 400 == 0)

            d = Datum.aus_text("24.12.2026")
            print(d.tag, d.monat)
            print(Datum.ist_schaltjahr(2028))
        """, output = """
            24 12
            True
        """)
        tip("Ein Unterstrich am Anfang (`self._stand`) signalisiert: „Interne Sache, bitte nicht von außen ändern.“ Python verbietet es nicht – aber es ist eine wichtige Konvention.")

        output("""
            class A:
                x = 5

            a = A()
            b = A()
            print(a.x + b.x)
        """, "10", "5", "Fehler", "0")
        output("""
            class K:
                def __init__(self, r):
                    self.r = r

                @property
                def d(self):
                    return self.r * 2

            print(K(4).d)
        """, "8", "4", "Fehler", "<bound method>")
        choice("Was bekommt eine `@classmethod` als ersten Parameter?", "Die Klasse (`cls`)", "Das Objekt (`self`)", "Nichts", "Den Klassennamen als Text")
        truth("Eine `@staticmethod` hat keinen Zugriff auf `self`.", true)
        typeOutput("""
            class Zaehler:
                gesamt = 0

                def __init__(self):
                    Zaehler.gesamt += 1

            for _ in range(4):
                Zaehler()
            print(Zaehler.gesamt)
        """, "4")
        fill("Mach `flaeche` zu einer Property.", """
            class Quadrat:
                def __init__(self, s):
                    self.s = s

                ??
                def flaeche(self):
                    return self.s ** 2

            print(Quadrat(5).flaeche)
        """, answers = listOf("@property"), distractors = listOf("@staticmethod", "@classmethod", "property"), output = "25")
        choice("Was bedeutet `self._geheim` per Konvention?", "Ein internes Attribut – nicht von außen benutzen", "Python verbietet jeden Zugriff", "Eine Konstante", "Eine Methode")
        write("Erstelle eine Klasse `Rechteck(breite, hoehe)` mit einer `@property` namens `flaeche` und einer `@classmethod` namens `quadrat(seite)`, die ein Rechteck mit gleichen Seiten erzeugt. Gib `Rechteck(2, 5).flaeche` und `Rechteck.quadrat(4).flaeche` aus.",
            solution = """
                class Rechteck:
                    def __init__(self, breite, hoehe):
                        self.breite = breite
                        self.hoehe = hoehe

                    @property
                    def flaeche(self):
                        return self.breite * self.hoehe

                    @classmethod
                    def quadrat(cls, seite):
                        return cls(seite, seite)

                print(Rechteck(2, 5).flaeche)
                print(Rechteck.quadrat(4).flaeche)
            """,
            output = """
                10
                16
            """,
            mustContain = listOf("@property", "@classmethod"),
            hint = "`@classmethod def quadrat(cls, seite): return cls(seite, seite)`")
    }

    lesson("Dataclasses", "Datenklassen ohne Tipparbeit", LessonIcon.BOX) {
        text("Viele Klassen sind einfach **Datenbehälter**. Mit `@dataclass` schreibt Python `__init__`, `__repr__` und `__eq__` automatisch für dich:")
        code("""
            from dataclasses import dataclass

            @dataclass
            class Punkt:
                x: int
                y: int

            p = Punkt(3, 4)
            print(p)
            print(p == Punkt(3, 4))
            print(p.x + p.y)
        """, output = """
            Punkt(x=3, y=4)
            True
            7
        """)
        text("Die Felder werden mit **Typ-Angaben** notiert. Standardwerte und eigene Methoden sind auch möglich:")
        code("""
            from dataclasses import dataclass, field

            @dataclass
            class Aufgabe:
                titel: str
                erledigt: bool = False
                tags: list[str] = field(default_factory=list)

                def abhaken(self):
                    self.erledigt = True

            a = Aufgabe("Python üben", tags=["lernen"])
            a.abhaken()
            print(a)
        """, output = "Aufgabe(titel='Python üben', erledigt=True, tags=['lernen'])")
        warn("Listen als Standardwert gehen nur mit `field(default_factory=list)` – sonst würden sich alle Objekte dieselbe Liste teilen.")
        tip("Mit `@dataclass(frozen=True)` werden die Objekte unveränderlich – wie Tupel mit Namen.")

        output("""
            from dataclasses import dataclass

            @dataclass
            class Tier:
                name: str
                beine: int

            print(Tier("Spinne", 8))
        """, "Tier(name='Spinne', beine=8)", "Spinne 8", "<Tier object>", "Fehler")
        output("""
            from dataclasses import dataclass

            @dataclass
            class P:
                x: int

            print(P(1) == P(1))
        """, "True", "False", "Fehler", "None", explain = "Dataclasses erzeugen automatisch ein `__eq__`, das die Felder vergleicht.")
        choice("Was erzeugt `@dataclass` automatisch?", "`__init__`, `__repr__` und `__eq__`", "Nur `__init__`", "Eine Datenbank", "Nur `__str__`")
        truth("Bei einer Dataclass werden die Felder mit Typ-Angaben wie `name: str` notiert.", true)
        typeOutput("""
            from dataclasses import dataclass

            @dataclass
            class Konto:
                inhaber: str
                stand: float = 0.0

            k = Konto("Ada")
            print(k.stand)
        """, "0.0")
        fill("Mach die Klasse zu einer Dataclass.", """
            from dataclasses import dataclass

            ??
            class Farbe:
                r: int
                g: int
                b: int

            print(Farbe(255, 0, 0))
        """, answers = listOf("@dataclass"), distractors = listOf("@property", "@classmethod", "dataclass"), output = "Farbe(r=255, g=0, b=0)")
        output("""
            from dataclasses import dataclass

            @dataclass
            class Film:
                titel: str
                jahr: int

            filme = [Film("B", 2020), Film("A", 1999)]
            print(min(filme, key=lambda f: f.jahr).titel)
        """, "A", "B", "1999", "Fehler")
        write("Erstelle eine Dataclass `Produkt` mit `name: str`, `preis: float` und `menge: int = 1` sowie einer Methode `gesamt()` (Preis × Menge). Gib `Produkt(\"Stift\", 1.5, 4).gesamt()` aus.",
            solution = """
                from dataclasses import dataclass

                @dataclass
                class Produkt:
                    name: str
                    preis: float
                    menge: int = 1

                    def gesamt(self):
                        return self.preis * self.menge

                print(Produkt("Stift", 1.5, 4).gesamt())
            """,
            output = "6.0",
            test = """assert Produkt("X", 2.0).gesamt() == 2.0""",
            mustContain = listOf("@dataclass"),
            hint = "Felder mit Typen notieren, dann `def gesamt(self): return self.preis * self.menge`.")
    }

    checkpoint()
}
