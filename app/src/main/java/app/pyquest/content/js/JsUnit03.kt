package app.pyquest.content.js

import app.pyquest.content.CourseBuilder
import app.pyquest.data.LessonIcon

fun CourseBuilder.jsUnit03() = unit("Eingaben & Texte", "prompt() und die Werkzeuge für Strings") {
    lesson("Eingaben mit prompt()", "Dein Programm stellt Fragen", LessonIcon.CHAT) {
        text("Mit `prompt()` fragt dein Programm etwas und wartet, bis eine Antwort eingegeben wurde. Die Antwort kannst du in einer Variable speichern. Im Browser erscheint dafür ein kleines Fenster – in Hissbit ein Eingabefeld unter der Konsole.")
        code("""
            const name = prompt("Wie heißt du?");
            console.log(`Hallo ${D}{name}!`);
        """, output = """
            Wie heißt du? Ada
            Hallo Ada!
        """, stdin = listOf("Ada"))
        text("Ganz wichtig: `prompt()` liefert **immer einen Text** – auch wenn jemand eine Zahl eintippt. Zum Rechnen wandelst du die Eingabe mit `Number()` um.")
        code("""
            const alter = Number(prompt("Wie alt bist du?"));
            console.log(`Nächstes Jahr bist du ${D}{alter + 1}.`);
        """, output = """
            Wie alt bist du? 13
            Nächstes Jahr bist du 14.
        """, stdin = listOf("13"))
        warn("Ohne `Number()` würde `\"13\" + 1` den Text `\"131\"` ergeben – die Zahl wird einfach drangehängt!")
        tip("Bricht jemand die Eingabe ab, liefert `prompt()` den Wert `null`. Für den Anfang kannst du das ignorieren.")

        choice("Welchen Typ hat das Ergebnis von `prompt()`?", "string", "number", "boolean", "Das hängt von der Eingabe ab", mono = false,
            explain = "`prompt()` liefert immer einen Text – auch bei der Eingabe 42.")
        output("""
            const zahl = "4";
            console.log(zahl + 1);
        """, "41", "5", "4 + 1", "NaN", explain = "Ohne Umwandlung wird der Text `\"4\"` mit 1 verbunden.")
        fill("Wandle die Eingabe in eine Zahl um.", """
            const n = ??(prompt("Zahl?"));
            console.log(n * 2);
        """, answers = listOf("Number"), distractors = listOf("String", "Text", "toNumber"))
        truth("`prompt()` wartet, bis eine Eingabe gemacht wurde.", true)
        arrange("Frage nach einer Lieblingsfarbe und gib sie in einem Satz aus.", """
            const farbe = prompt("Lieblingsfarbe?");
            console.log(`Deine Lieblingsfarbe ist ${D}{farbe}.`);
        """)
        write("Frage mit `prompt(\"Wie heißt du?\")` nach einem Namen und gib dann `Willkommen, <Name>!` aus.",
            solution = """
                const name = prompt("Wie heißt du?");
                console.log(`Willkommen, ${D}{name}!`);
            """,
            output = "Willkommen, Monty!",
            stdin = listOf("Monty"),
            mustContain = listOf("prompt"),
            hint = "Speichere die Antwort von `prompt()` in einer Variable und baue sie in einen Template-String ein.")
        write("Frage nach zwei Zahlen (je ein `prompt()`) und gib ihre **Summe** aus.",
            solution = """
                const a = Number(prompt("Erste Zahl?"));
                const b = Number(prompt("Zweite Zahl?"));
                console.log(a + b);
            """,
            output = "12",
            stdin = listOf("5", "7"),
            mustContain = listOf("prompt", "Number"),
            hint = "Ohne `Number()` kämen `\"5\"` und `\"7\"` als Text `57` heraus.")
    }

    lesson("Zeichen & Länge", "Auf einzelne Buchstaben zugreifen", LessonIcon.BOOK) {
        text("Ein String ist eine Reihe von Zeichen. Mit `.length` erfährst du, wie viele Zeichen er hat – Leerzeichen zählen mit.")
        code("""
            const wort = "JavaScript";
            console.log(wort.length);
            console.log("Hallo Welt".length);
        """, output = """
            10
            10
        """)
        text("Jedes Zeichen hat eine Position, den **Index**. Gezählt wird ab **0**! Mit eckigen Klammern holst du ein Zeichen heraus. `.at(-1)` liefert das letzte Zeichen.")
        code("""
            const tier = "Monty";
            console.log(tier[0]);
            console.log(tier[4]);
            console.log(tier.at(-1));
            console.log(tier[tier.length - 1]);
        """, output = """
            M
            y
            y
            y
        """)
        tip("Der letzte Index ist immer `length - 1`. Ein Index, den es nicht gibt, liefert `undefined` – ohne Fehlermeldung.")
        warn("Strings lassen sich nicht verändern: `tier[0] = \"B\"` bewirkt nichts. Du kannst nur neue Strings bauen.")

        output("""console.log("Hallo".length);""", "5", "4", "6", "Hallo")
        output("""
            const s = "Python";
            console.log(s[1]);
        """, "y", "P", "t", "Py", explain = "Der Index beginnt bei 0: `s[0]` ist P, `s[1]` ist y.")
        typeOutput("""console.log("Monty".at(-1));""", "y")
        output("""console.log("abc"[5]);""", "undefined", "c", "Fehler", "\"\"")
        choice("Welchen Index hat das **erste** Zeichen eines Strings?", "0", "1", "-1", "Das hängt vom String ab", mono = true)
        fill("Gib den ersten Buchstaben aus.", """
            const name = "Ada";
            console.log(name[??]);
        """, answers = listOf("0"), distractors = listOf("1", "-1", "first"), output = "A")
        truth("`\"Hallo Welt\".length` ergibt 9.", false, explain = "Das Leerzeichen zählt mit – es sind 10 Zeichen.")
        write("Die Variable `wort` enthält `\"Schlange\"`. Gib das **erste** und das **letzte** Zeichen in einer Zeile aus, getrennt durch ein Leerzeichen: `S e`.",
            starter = """
                const wort = "Schlange";
            """,
            solution = """
                const wort = "Schlange";
                console.log(wort[0], wort.at(-1));
            """,
            output = "S e",
            hint = "`wort[0]` ist das erste, `wort.at(-1)` oder `wort[wort.length - 1]` das letzte Zeichen.")
    }

    lesson("String-Methoden", "Texte umwandeln und durchsuchen", LessonIcon.MAGIC) {
        text("Strings bringen viele **Methoden** mit – Funktionen, die du mit einem Punkt dahinter aufrufst. Sie verändern den Original-String nicht, sondern liefern einen neuen.")
        code("""
            const text = "  Hallo Welt  ";
            console.log(text.toUpperCase());
            console.log(text.toLowerCase());
            console.log(text.trim());
            console.log(`[${D}{text.trim()}]`);
        """, output = """
              HALLO WELT
              hallo welt
            Hallo Welt
            [Hallo Welt]
        """)
        text("Zum Suchen: `includes()` prüft, ob etwas vorkommt, `startsWith()`/`endsWith()` prüfen Anfang und Ende, `indexOf()` liefert die Position (oder `-1`).")
        code("""
            const satz = "JavaScript macht Spaß";
            console.log(satz.includes("macht"));
            console.log(satz.startsWith("Java"));
            console.log(satz.indexOf("Spaß"));
            console.log(satz.indexOf("Python"));
        """, output = """
            true
            true
            17
            -1
        """)
        text("Zum Ersetzen und Wiederholen: `replace()` ersetzt das erste Vorkommen, `replaceAll()` alle. `repeat()` wiederholt einen String.")
        code("""
            console.log("Ich mag Tee".replace("Tee", "Kakao"));
            console.log("a-b-c".replaceAll("-", "+"));
            console.log("ab".repeat(3));
            console.log("7".padStart(3, "0"));
        """, output = """
            Ich mag Kakao
            a+b+c
            ababab
            007
        """)
        tip("Methoden kann man **verketten**: `eingabe.trim().toLowerCase()` entfernt erst die Leerzeichen und macht dann alles klein.")

        output("""console.log("monty".toUpperCase());""", "MONTY", "Monty", "monty", "mONTY")
        output("""console.log("Banane".includes("nan"));""", "true", "false", "2", "nan")
        typeOutput("""console.log("Hallo".replace("H", "B"));""", "Ballo")
        output("""console.log("Kuchen".indexOf("x"));""", "-1", "0", "undefined", "false")
        match(
            "toUpperCase()" to "alles groß",
            "trim()" to "Leerzeichen außen weg",
            "includes()" to "kommt es vor?",
            "replace()" to "ersetzen",
            "repeat()" to "wiederholen",
        )
        fill("Entferne zuerst die Leerzeichen am Rand – danach wird alles klein geschrieben.", """
            const eingabe = "  JA ";
            console.log(eingabe.??().toLowerCase());
        """, answers = listOf("trim"), distractors = listOf("toUpperCase", "strip", "length"), output = "ja")
        output("""console.log("Na".repeat(4) + " Batman!");""", "NaNaNaNa Batman!", "Na4 Batman!", "Na Na Na Na Batman!", "NaN Batman!")
        write("In `eingabe` steht `\"  HISSBIT  \"`. Gib den Text ohne die Leerzeichen am Rand und in Kleinbuchstaben aus.",
            starter = """
                const eingabe = "  HISSBIT  ";
            """,
            solution = """
                const eingabe = "  HISSBIT  ";
                console.log(eingabe.trim().toLowerCase());
            """,
            output = "hissbit",
            mustContain = listOf("trim", "toLowerCase"),
            hint = "Verkette die beiden Methoden: `eingabe.trim().toLowerCase()`.")
    }

    lesson("Texte zerschneiden", "slice(), split() und join()", LessonIcon.PUZZLE) {
        text("Mit `slice(start, ende)` schneidest du ein Stück aus einem String heraus. Das Zeichen an der Position `ende` ist **nicht** mehr dabei. Lässt du `ende` weg, geht es bis zum Schluss. Negative Zahlen zählen von hinten.")
        code("""
            const wort = "Schokolade";
            console.log(wort.slice(0, 5));
            console.log(wort.slice(5));
            console.log(wort.slice(-4));
        """, output = """
            Schok
            olade
            lade
        """)
        text("`split()` zerlegt einen String an einem Trennzeichen in ein **Array** (eine Liste). `join()` setzt ein Array wieder zu einem String zusammen. Mehr über Arrays lernst du bald.")
        code("""
            const datum = "24.12.2025";
            const teile = datum.split(".");
            console.log(teile);
            console.log(teile[2]);
            console.log(teile.join("/"));
        """, output = """
            [ '24', '12', '2025' ]
            2025
            24/12/2025
        """)
        tip("`split(\"\")` zerlegt einen String in einzelne Zeichen – und `.split(\"\").reverse().join(\"\")` dreht ihn um.")
        code("""
            const umgedreht = "Monty".split("").reverse().join("");
            console.log(umgedreht);
        """, output = "ytnoM")

        output("""console.log("Hallo".slice(1, 3));""", "al", "all", "Ha", "ll", explain = "Von Index 1 (a) bis vor Index 3 – also a und l.")
        output("""console.log("Banane".slice(-3));""", "ane", "Ban", "nane", "ne")
        typeOutput("""console.log("a,b,c".split(",").length);""", "3")
        output("""console.log(["x", "y", "z"].join("-"));""", "x-y-z", "xyz", "x, y, z", "[x-y-z]")
        choice("Was liefert `\"rot grün blau\".split(\" \")`?", "Ein Array mit drei Texten", "Einen String ohne Leerzeichen", "Die Zahl 3", "Einen Fehler")
        fill("Hole die ersten drei Buchstaben heraus.", """console.log("Pizza".slice(??, ??));""",
            answers = listOf("0", "3"), distractors = listOf("1", "2"), output = "Piz")
        truth("Bei `slice(2, 5)` ist das Zeichen mit Index 5 noch dabei.", false)
        write("Die Variable `name` enthält `\"Ada Lovelace\"`. Zerlege den Namen mit `split()` am Leerzeichen und gib nur den **Nachnamen** aus.",
            starter = """
                const name = "Ada Lovelace";
            """,
            solution = """
                const name = "Ada Lovelace";
                const teile = name.split(" ");
                console.log(teile[1]);
            """,
            output = "Lovelace",
            mustContain = listOf("split"),
            hint = "`name.split(\" \")` liefert `[\"Ada\", \"Lovelace\"]` – der Nachname hat den Index 1.")
    }

    checkpoint()
}
