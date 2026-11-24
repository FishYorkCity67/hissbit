package app.pyquest.content.js

import app.pyquest.content.LessonBuilder
import app.pyquest.data.CodeExercise
import app.pyquest.data.LessonIcon
import app.pyquest.data.LessonKind

/**
 * Tägliche JavaScript-Challenges (eine pro Tag, rotierend). Die Tests laufen in einem eigenen
 * Block, damit ihr Hilfsname `pruefe` nicht mit Namen aus dem Lösungscode kollidiert.
 */
fun buildJsChallenges(): List<CodeExercise> {
    val b = LessonBuilder("js-challenge")

    fun ch(title: String, description: String, signature: String, solution: String, tests: String, hint: String) {
        b.write(
            prompt = "**$title:** $description",
            starter = signature.trimIndent() + "\n  // Dein Code hier\n}",
            solution = solution,
            test = "{\n" + PRUEFE_HELFER + "\n" + tests.trimIndent() + "\n}\nconsole.log(\"✅ Alle Tests bestanden!\");",
            hint = hint,
        )
    }

    ch("Palindrom-Check", "Schreibe `istPalindrom(text)`, die `true` zurückgibt, wenn ein Wort vorwärts und rückwärts gleich ist (Groß-/Kleinschreibung egal).",
        "function istPalindrom(text) {",
        """
            function istPalindrom(text) {
              const t = text.toLowerCase();
              return t === [...t].reverse().join("");
            }
        """,
        """
            pruefe(() => istPalindrom("Anna"), true);
            pruefe(() => istPalindrom("Rentner"), true);
            pruefe(() => istPalindrom("Monty"), false);
            pruefe(() => istPalindrom(""), true);
        """,
        "Mit `[...text].reverse().join(\"\")` drehst du einen String um, mit `toLowerCase()` machst du ihn klein.")
    ch("FizzBuzz", "Schreibe `fizzbuzz(n)`: Gib `\"FizzBuzz\"` zurück, wenn n durch 3 und 5 teilbar ist, `\"Fizz\"` bei 3, `\"Buzz\"` bei 5 – sonst die Zahl als String.",
        "function fizzbuzz(n) {",
        """
            function fizzbuzz(n) {
              if (n % 15 === 0) return "FizzBuzz";
              if (n % 3 === 0) return "Fizz";
              if (n % 5 === 0) return "Buzz";
              return String(n);
            }
        """,
        """
            pruefe(() => fizzbuzz(3), "Fizz");
            pruefe(() => fizzbuzz(10), "Buzz");
            pruefe(() => fizzbuzz(30), "FizzBuzz");
            pruefe(() => fizzbuzz(7), "7");
        """,
        "Prüfe den Fall „durch 15 teilbar“ zuerst!")
    ch("Vokale zählen", "Schreibe `vokale(text)`, die zählt, wie viele Vokale (a, e, i, o, u, ä, ö, ü) ein Text enthält – Groß-/Kleinschreibung egal.",
        "function vokale(text) {",
        """
            function vokale(text) {
              let anzahl = 0;
              for (const b of text.toLowerCase()) {
                if ("aeiouäöü".includes(b)) anzahl++;
              }
              return anzahl;
            }
        """,
        """
            pruefe(() => vokale("Hallo Welt"), 3);
            pruefe(() => vokale("JAVASCRIPT"), 3);
            pruefe(() => vokale("Bäume"), 3);
            pruefe(() => vokale("xyz"), 0);
        """,
        "Gehe mit `for…of` durch die Buchstaben und prüfe mit `\"aeiouäöü\".includes(b)`.")
    ch("Größte Zahl", "Schreibe `groesste(zahlen)`, die die größte Zahl eines Arrays zurückgibt. Für ein leeres Array gib `undefined` zurück.",
        "function groesste(zahlen) {",
        """
            function groesste(zahlen) {
              if (zahlen.length === 0) return undefined;
              let max = zahlen[0];
              for (const z of zahlen) {
                if (z > max) max = z;
              }
              return max;
            }
        """,
        """
            pruefe(() => groesste([3, 9, 2]), 9);
            pruefe(() => groesste([-5, -1, -9]), -1);
            pruefe(() => groesste([42]), 42);
            pruefe(() => groesste([]), undefined);
        """,
        "Merke dir die bisher größte Zahl und vergleiche jede weitere damit – oder nutze `Math.max(...zahlen)`.")
    ch("Quersumme", "Schreibe `quersumme(n)`, die die Summe aller Ziffern einer positiven ganzen Zahl zurückgibt (z. B. 1234 → 10).",
        "function quersumme(n) {",
        """
            function quersumme(n) {
              return String(n).split("").reduce((s, z) => s + Number(z), 0);
            }
        """,
        """
            pruefe(() => quersumme(1234), 10);
            pruefe(() => quersumme(9), 9);
            pruefe(() => quersumme(1000), 1);
            pruefe(() => quersumme(99999), 45);
        """,
        "Wandle die Zahl mit `String(n)` in Text um, zerlege ihn in Ziffern und addiere sie als Zahlen.")
    ch("Wörter umdrehen", "Schreibe `umdrehen(satz)`, die die Reihenfolge der Wörter umkehrt: `\"Hallo schöne Welt\"` → `\"Welt schöne Hallo\"`.",
        "function umdrehen(satz) {",
        """
            function umdrehen(satz) {
              return satz.split(" ").reverse().join(" ");
            }
        """,
        """
            pruefe(() => umdrehen("Hallo schöne Welt"), "Welt schöne Hallo");
            pruefe(() => umdrehen("eins zwei"), "zwei eins");
            pruefe(() => umdrehen("Solo"), "Solo");
        """,
        "`split(\" \")`, `reverse()` und `join(\" \")`.")
    ch("Fakultät", "Schreibe `fakultaet(n)`, die n! = 1 · 2 · … · n berechnet. Es gilt 0! = 1.",
        "function fakultaet(n) {",
        """
            function fakultaet(n) {
              let ergebnis = 1;
              for (let i = 2; i <= n; i++) ergebnis *= i;
              return ergebnis;
            }
        """,
        """
            pruefe(() => fakultaet(0), 1);
            pruefe(() => fakultaet(1), 1);
            pruefe(() => fakultaet(5), 120);
            pruefe(() => fakultaet(10), 3628800);
        """,
        "Starte mit `ergebnis = 1` und multipliziere in einer Schleife mit 2, 3, … bis n.")
    ch("Primzahl", "Schreibe `istPrim(n)`, die `true` zurückgibt, wenn n eine Primzahl ist (nur durch 1 und sich selbst teilbar, größer als 1).",
        "function istPrim(n) {",
        """
            function istPrim(n) {
              if (n < 2) return false;
              for (let t = 2; t * t <= n; t++) {
                if (n % t === 0) return false;
              }
              return true;
            }
        """,
        """
            pruefe(() => istPrim(2), true);
            pruefe(() => istPrim(17), true);
            pruefe(() => istPrim(1), false);
            pruefe(() => istPrim(21), false);
            pruefe(() => istPrim(97), true);
        """,
        "Teste alle Teiler von 2 bis zur Wurzel aus n. Findest du einen, ist n keine Primzahl.")
    ch("Ohne Duplikate", "Schreibe `ohneDuplikate(liste)`, die ein neues Array ohne doppelte Werte zurückgibt – die Reihenfolge des ersten Auftretens bleibt erhalten.",
        "function ohneDuplikate(liste) {",
        """
            function ohneDuplikate(liste) {
              return [...new Set(liste)];
            }
        """,
        """
            pruefe(() => ohneDuplikate([1, 2, 2, 3, 1]), [1, 2, 3]);
            pruefe(() => ohneDuplikate(["b", "a", "b"]), ["b", "a"]);
            pruefe(() => ohneDuplikate([]), []);
        """,
        "Ein `Set` speichert jeden Wert nur einmal: `[...new Set(liste)]`.")
    ch("Anagramm", "Schreibe `istAnagramm(a, b)`: `true`, wenn beide Wörter aus genau denselben Buchstaben bestehen (Groß-/Kleinschreibung egal), z. B. „Lampe“ und „Palme“.",
        "function istAnagramm(a, b) {",
        """
            function istAnagramm(a, b) {
              const sortiert = (w) => [...w.toLowerCase()].sort().join("");
              return sortiert(a) === sortiert(b);
            }
        """,
        """
            pruefe(() => istAnagramm("Lampe", "Palme"), true);
            pruefe(() => istAnagramm("Regal", "Lager"), true);
            pruefe(() => istAnagramm("Haus", "Maus"), false);
            pruefe(() => istAnagramm("ab", "abb"), false);
        """,
        "Sortiere die Buchstaben beider Wörter und vergleiche die Ergebnisse.")
    ch("Titel-Schreibweise", "Schreibe `titel(text)`, die jedes Wort mit einem Großbuchstaben beginnen lässt: `\"hallo schöne welt\"` → `\"Hallo Schöne Welt\"`.",
        "function titel(text) {",
        """
            function titel(text) {
              return text
                .split(" ")
                .map((w) => (w ? w[0].toUpperCase() + w.slice(1) : w))
                .join(" ");
            }
        """,
        """
            pruefe(() => titel("hallo schöne welt"), "Hallo Schöne Welt");
            pruefe(() => titel("javaScript"), "JavaScript");
            pruefe(() => titel("a b c"), "A B C");
        """,
        "Zerlege in Wörter, mache bei jedem `w[0]` groß und hänge `w.slice(1)` an.")
    ch("Zeichen zählen", "Schreibe `zaehleZeichen(text)`, die ein Objekt zurückgibt, das für jedes Zeichen angibt, wie oft es vorkommt.",
        "function zaehleZeichen(text) {",
        """
            function zaehleZeichen(text) {
              const ergebnis = {};
              for (const z of text) {
                ergebnis[z] = (ergebnis[z] ?? 0) + 1;
              }
              return ergebnis;
            }
        """,
        """
            pruefe(() => zaehleZeichen("banane"), { b: 1, a: 2, n: 2, e: 1 });
            pruefe(() => zaehleZeichen("aaa"), { a: 3 });
            pruefe(() => zaehleZeichen(""), {});
        """,
        "Ein leeres Objekt `{}` als Zähler, dann `ergebnis[z] = (ergebnis[z] ?? 0) + 1`.")
    ch("Nur gerade Zahlen", "Schreibe `nurGerade(zahlen)`, die ein neues Array mit allen geraden Zahlen zurückgibt.",
        "function nurGerade(zahlen) {",
        """
            function nurGerade(zahlen) {
              return zahlen.filter((z) => z % 2 === 0);
            }
        """,
        """
            pruefe(() => nurGerade([1, 2, 3, 4, 5, 6]), [2, 4, 6]);
            pruefe(() => nurGerade([7, 9]), []);
            pruefe(() => nurGerade([0, -2]), [0, -2]);
        """,
        "`filter` mit der Bedingung `z % 2 === 0`.")
    ch("Durchschnitt", "Schreibe `durchschnitt(zahlen)`, die den Mittelwert eines Arrays zurückgibt – bei einem leeren Array `0`.",
        "function durchschnitt(zahlen) {",
        """
            function durchschnitt(zahlen) {
              if (zahlen.length === 0) return 0;
              return zahlen.reduce((s, z) => s + z, 0) / zahlen.length;
            }
        """,
        """
            pruefe(() => durchschnitt([2, 4, 6]), 4);
            pruefe(() => durchschnitt([5]), 5);
            pruefe(() => durchschnitt([1, 2]), 1.5);
            pruefe(() => durchschnitt([]), 0);
        """,
        "Summe mit `reduce` durch die Länge teilen – den leeren Fall vorher abfangen.")
    ch("Fibonacci", "Schreibe `fibonacci(n)`, die ein Array mit den ersten n Fibonacci-Zahlen liefert: 0, 1, 1, 2, 3, 5, …",
        "function fibonacci(n) {",
        """
            function fibonacci(n) {
              const folge = [];
              let a = 0;
              let b = 1;
              for (let i = 0; i < n; i++) {
                folge.push(a);
                [a, b] = [b, a + b];
              }
              return folge;
            }
        """,
        """
            pruefe(() => fibonacci(1), [0]);
            pruefe(() => fibonacci(7), [0, 1, 1, 2, 3, 5, 8]);
            pruefe(() => fibonacci(0), []);
        """,
        "Zwei Variablen `a` und `b`; in jeder Runde `a` speichern und dann `[a, b] = [b, a + b]`.")
    ch("Längstes Wort", "Schreibe `laengstesWort(satz)`, die das längste Wort zurückgibt. Bei Gleichstand gewinnt das erste.",
        "function laengstesWort(satz) {",
        """
            function laengstesWort(satz) {
              let bestes = "";
              for (const w of satz.split(" ")) {
                if (w.length > bestes.length) bestes = w;
              }
              return bestes;
            }
        """,
        """
            pruefe(() => laengstesWort("Ich lerne heute JavaScript"), "JavaScript");
            pruefe(() => laengstesWort("ab cd ef"), "ab");
            pruefe(() => laengstesWort("Hallo"), "Hallo");
        """,
        "Merke dir das bisher längste Wort – nur bei echt größerer Länge ersetzen.")
    ch("Caesar-Code", "Schreibe `caesar(text, n)`, die jeden Kleinbuchstaben a–z um n Stellen im Alphabet verschiebt (nach z geht es bei a weiter). Andere Zeichen bleiben gleich.",
        "function caesar(text, n) {",
        """
            function caesar(text, n) {
              let ergebnis = "";
              for (const z of text) {
                if (z >= "a" && z <= "z") {
                  const pos = (z.charCodeAt(0) - 97 + n) % 26;
                  ergebnis += String.fromCharCode(97 + pos);
                } else {
                  ergebnis += z;
                }
              }
              return ergebnis;
            }
        """,
        """
            pruefe(() => caesar("abc", 1), "bcd");
            pruefe(() => caesar("xyz", 3), "abc");
            pruefe(() => caesar("hallo welt!", 13), "unyyb jryg!");
        """,
        "`z.charCodeAt(0) - 97` ist die Position im Alphabet, `% 26` sorgt fürs Herumlaufen, `String.fromCharCode` macht wieder einen Buchstaben daraus.")
    ch("Countdown-Array", "Schreibe `countdown(n)`, die das Array `[n, n-1, …, 1]` zurückgibt.",
        "function countdown(n) {",
        """
            function countdown(n) {
              const ergebnis = [];
              for (let i = n; i >= 1; i--) ergebnis.push(i);
              return ergebnis;
            }
        """,
        """
            pruefe(() => countdown(3), [3, 2, 1]);
            pruefe(() => countdown(1), [1]);
            pruefe(() => countdown(0), []);
        """,
        "Eine Schleife, die von n rückwärts bis 1 zählt.")
    ch("Klassenbester", "Schreibe `bester(schueler)`: Das Array enthält Objekte mit `name` und `punkte`. Gib den Namen der Person mit den meisten Punkten zurück.",
        "function bester(schueler) {",
        """
            function bester(schueler) {
              return schueler.reduce((top, s) => (s.punkte > top.punkte ? s : top)).name;
            }
        """,
        """
            pruefe(() => bester([{ name: "Ada", punkte: 80 }, { name: "Ben", punkte: 95 }]), "Ben");
            pruefe(() => bester([{ name: "Cleo", punkte: 10 }]), "Cleo");
            pruefe(() => bester([{ name: "X", punkte: 5 }, { name: "Y", punkte: 3 }, { name: "Z", punkte: 4 }]), "X");
        """,
        "Vergleiche die Punkte in einer Schleife oder mit `reduce` und gib am Ende `.name` zurück.")
    ch("Summe bis n", "Schreibe `summeBis(n)`, die 1 + 2 + … + n berechnet.",
        "function summeBis(n) {",
        """
            function summeBis(n) {
              let s = 0;
              for (let i = 1; i <= n; i++) s += i;
              return s;
            }
        """,
        """
            pruefe(() => summeBis(1), 1);
            pruefe(() => summeBis(10), 55);
            pruefe(() => summeBis(100), 5050);
        """,
        "Eine Schleife – oder die Formel des kleinen Gauß: `n * (n + 1) / 2`.")
    ch("Schaltjahr", "Schreibe `istSchaltjahr(jahr)`: Ein Jahr ist ein Schaltjahr, wenn es durch 4 teilbar ist – außer es ist durch 100 teilbar, dann nur, wenn es auch durch 400 teilbar ist.",
        "function istSchaltjahr(jahr) {",
        """
            function istSchaltjahr(jahr) {
              return (jahr % 4 === 0 && jahr % 100 !== 0) || jahr % 400 === 0;
            }
        """,
        """
            pruefe(() => istSchaltjahr(2024), true);
            pruefe(() => istSchaltjahr(1900), false);
            pruefe(() => istSchaltjahr(2000), true);
            pruefe(() => istSchaltjahr(2023), false);
        """,
        "Verknüpfe die drei Regeln mit `&&` und `||`.")
    ch("Gruppieren", "Schreibe `gruppiere(liste, schluessel)`, die Objekte nach dem Wert einer Eigenschaft in einem Objekt aus Arrays sammelt.",
        "function gruppiere(liste, schluessel) {",
        """
            function gruppiere(liste, schluessel) {
              const gruppen = {};
              for (const element of liste) {
                const k = element[schluessel];
                if (!gruppen[k]) gruppen[k] = [];
                gruppen[k].push(element);
              }
              return gruppen;
            }
        """,
        """
            const tiere = [
              { name: "Rex", art: "Hund" },
              { name: "Mimi", art: "Katze" },
              { name: "Bello", art: "Hund" },
            ];
            pruefe(() => gruppiere(tiere, "art"), {
              Hund: [{ name: "Rex", art: "Hund" }, { name: "Bello", art: "Hund" }],
              Katze: [{ name: "Mimi", art: "Katze" }],
            });
            pruefe(() => gruppiere([], "x"), {});
        """,
        "Lege für jeden neuen Schlüssel ein leeres Array an und `push` das Element hinein.")
    ch("Flach machen", "Schreibe `flach(arrays)`, die ein Array von Arrays zu einem einzigen Array zusammenfügt: `[[1, 2], [3], []]` → `[1, 2, 3]`.",
        "function flach(arrays) {",
        """
            function flach(arrays) {
              const ergebnis = [];
              for (const a of arrays) ergebnis.push(...a);
              return ergebnis;
            }
        """,
        """
            pruefe(() => flach([[1, 2], [3], []]), [1, 2, 3]);
            pruefe(() => flach([["a"], ["b", "c"]]), ["a", "b", "c"]);
            pruefe(() => flach([]), []);
        """,
        "`ergebnis.push(...a)` hängt alle Elemente eines Teil-Arrays an. Es gibt auch die eingebaute Methode `arrays.flat()`.")
    ch("Schnittmenge", "Schreibe `gemeinsam(a, b)`, die alle Werte aus `a` zurückgibt, die auch in `b` vorkommen (Reihenfolge wie in `a`, ohne Doppelte).",
        "function gemeinsam(a, b) {",
        """
            function gemeinsam(a, b) {
              const inB = new Set(b);
              return [...new Set(a.filter((x) => inB.has(x)))];
            }
        """,
        """
            pruefe(() => gemeinsam([1, 2, 3, 4], [4, 2, 9]), [2, 4]);
            pruefe(() => gemeinsam(["a", "a", "b"], ["a"]), ["a"]);
            pruefe(() => gemeinsam([1, 2], [3]), []);
        """,
        "Mit `filter` und `includes` (oder einem `Set`) findest du gemeinsame Werte; ein weiteres `Set` entfernt Doppelte.")
    ch("Zweitgrößte Zahl", "Schreibe `zweitgroesste(zahlen)`, die die zweitgrößte **verschiedene** Zahl zurückgibt – gibt es keine, dann `undefined`.",
        "function zweitgroesste(zahlen) {",
        """
            function zweitgroesste(zahlen) {
              const sortiert = [...new Set(zahlen)].sort((a, b) => b - a);
              return sortiert[1];
            }
        """,
        """
            pruefe(() => zweitgroesste([4, 9, 2, 9]), 4);
            pruefe(() => zweitgroesste([1, 2]), 1);
            pruefe(() => zweitgroesste([5, 5]), undefined);
            pruefe(() => zweitgroesste([10, 100, 25]), 25);
        """,
        "Doppelte entfernen, absteigend sortieren (Vergleichsfunktion nicht vergessen!) und Index 1 nehmen.")
    ch("Text komprimieren", "Schreibe `komprimiere(text)`, die gleiche Zeichen hintereinander zusammenfasst: `\"aaabcc\"` → `\"a3b1c2\"`.",
        "function komprimiere(text) {",
        """
            function komprimiere(text) {
              let ergebnis = "";
              let i = 0;
              while (i < text.length) {
                let j = i;
                while (j < text.length && text[j] === text[i]) j++;
                ergebnis += text[i] + (j - i);
                i = j;
              }
              return ergebnis;
            }
        """,
        """
            pruefe(() => komprimiere("aaabcc"), "a3b1c2");
            pruefe(() => komprimiere("x"), "x1");
            pruefe(() => komprimiere(""), "");
            pruefe(() => komprimiere("aabbaa"), "a2b2a2");
        """,
        "Zähle, wie lange dasselbe Zeichen wiederholt wird, hänge Zeichen + Anzahl an und springe weiter.")
    ch("Klammern prüfen", "Schreibe `klammernOk(text)`, die `true` liefert, wenn alle runden Klammern richtig geöffnet und geschlossen werden.",
        "function klammernOk(text) {",
        """
            function klammernOk(text) {
              let offen = 0;
              for (const z of text) {
                if (z === "(") offen++;
                if (z === ")") {
                  offen--;
                  if (offen < 0) return false;
                }
              }
              return offen === 0;
            }
        """,
        """
            pruefe(() => klammernOk("(()())"), true);
            pruefe(() => klammernOk("(a + b) * (c)"), true);
            pruefe(() => klammernOk("(()"), false);
            pruefe(() => klammernOk(")("), false);
        """,
        "Zähle offene Klammern mit. Fällt der Zähler unter 0, ist es falsch – am Ende muss er genau 0 sein.")
    ch("Binärzahl", "Schreibe `zuBinaer(n)`, die eine positive ganze Zahl als Binärzahl-String zurückgibt (z. B. 10 → `\"1010\"`), ohne `toString(2)` zu benutzen.",
        "function zuBinaer(n) {",
        """
            function zuBinaer(n) {
              if (n === 0) return "0";
              let ergebnis = "";
              while (n > 0) {
                ergebnis = (n % 2) + ergebnis;
                n = Math.floor(n / 2);
              }
              return ergebnis;
            }
        """,
        """
            pruefe(() => zuBinaer(10), "1010");
            pruefe(() => zuBinaer(1), "1");
            pruefe(() => zuBinaer(0), "0");
            pruefe(() => zuBinaer(255), "11111111");
        """,
        "Teile immer wieder durch 2 – die Reste von hinten gelesen ergeben die Binärzahl.")
    ch("Zeit formatieren", "Schreibe `zeit(sekunden)`, die Sekunden als `M:SS` formatiert, z. B. 125 → `\"2:05\"`.",
        "function zeit(sekunden) {",
        """
            function zeit(sekunden) {
              const m = Math.floor(sekunden / 60);
              const s = sekunden % 60;
              return `${D}{m}:${D}{String(s).padStart(2, "0")}`;
            }
        """,
        """
            pruefe(() => zeit(125), "2:05");
            pruefe(() => zeit(59), "0:59");
            pruefe(() => zeit(600), "10:00");
        """,
        "Minuten mit `Math.floor(sekunden / 60)`, Sekunden mit `%`, führende Null mit `padStart(2, \"0\")`.")
    ch("Wörter zählen", "Schreibe `woerter(text)`, die zählt, wie viele Wörter ein Text hat. Mehrere Leerzeichen hintereinander zählen als eine Lücke.",
        "function woerter(text) {",
        """
            function woerter(text) {
              return text.split(" ").filter((w) => w !== "").length;
            }
        """,
        """
            pruefe(() => woerter("Hallo Welt"), 2);
            pruefe(() => woerter("  viele   Leerzeichen hier "), 3);
            pruefe(() => woerter(""), 0);
        """,
        "Nach `split(\" \")` entstehen bei mehreren Leerzeichen leere Strings – filtere sie heraus.")
    ch("Array rotieren", "Schreibe `rotiere(liste, k)`, die die Elemente um k Stellen nach rechts verschiebt: `rotiere([1, 2, 3, 4], 1)` → `[4, 1, 2, 3]`.",
        "function rotiere(liste, k) {",
        """
            function rotiere(liste, k) {
              if (liste.length === 0) return [];
              const n = k % liste.length;
              if (n === 0) return [...liste];
              return [...liste.slice(-n), ...liste.slice(0, -n)];
            }
        """,
        """
            pruefe(() => rotiere([1, 2, 3, 4], 1), [4, 1, 2, 3]);
            pruefe(() => rotiere([1, 2, 3, 4], 2), [3, 4, 1, 2]);
            pruefe(() => rotiere([1, 2, 3], 3), [1, 2, 3]);
            pruefe(() => rotiere([1, 2, 3], 4), [3, 1, 2]);
            pruefe(() => rotiere([], 5), []);
        """,
        "Mit `k % liste.length` bleibt die Verschiebung im Rahmen. Die letzten k Elemente kommen nach vorne: `slice(-k)` und `slice(0, -k)`.")
    return b.build("js-challenge", "js-challenge", "Challenges", "", LessonKind.LESSON, LessonIcon.ROCKET)
        .exercises.filterIsInstance<CodeExercise>()
}

/** Testhelfer für Challenges: vergleicht über JSON und meldet verständlich, was nicht stimmt. */
private val PRUEFE_HELFER = """
    const pruefe = (aufruf, soll) => {
      const ist = aufruf();
      const text = (w) => (w === undefined ? "undefined" : JSON.stringify(w));
      if (text(ist) !== text(soll)) {
        const was = String(aufruf).replace(/^\(\)\s*=>\s*/, "");
        throw new Error(was + " sollte " + text(soll) + " liefern, liefert aber " + text(ist));
      }
    };
""".trimIndent()
