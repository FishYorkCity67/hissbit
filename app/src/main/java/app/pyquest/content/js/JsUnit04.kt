package app.pyquest.content.js

import app.pyquest.content.CourseBuilder
import app.pyquest.data.LessonIcon

fun CourseBuilder.jsUnit04() = unit("Entscheidungen", "Vergleiche, if und else, Logik") {
    lesson("Vergleiche & Booleans", "Wahr oder falsch?", LessonIcon.BRANCH) {
        text("Vergleiche liefern einen **Wahrheitswert** (Boolean): `true` oder `false`.")
        code("""
            console.log(5 > 3);
            console.log(2 >= 7);
            console.log(4 === 4);
            console.log("Ja" !== "Nein");
        """, output = """
            true
            false
            true
            true
        """)
        text("""
            Die Vergleichsoperatoren:
            - `===` gleich und `!==` ungleich
            - `<` kleiner, `>` größer
            - `<=` kleiner oder gleich, `>=` größer oder gleich
        """)
        warn("Es gibt auch `==` und `!=` mit nur zwei Zeichen. Die wandeln aber Typen heimlich um: `5 == \"5\"` ist `true`! Nimm deshalb immer `===` und `!==`.")
        code("""
            console.log(5 == "5");
            console.log(5 === "5");
        """, output = """
            true
            false
        """)
        tip("Ein einzelnes `=` speichert einen Wert. Zum Vergleichen brauchst du `===`.")

        output("console.log(10 > 9);", "true", "false", "10 > 9", "1")
        output("console.log(3 === \"3\");", "false", "true", "3", "Fehler",
            explain = "`===` prüft auch den Typ: Zahl und Text sind nie gleich.")
        choice("Welcher Operator prüft auf **Gleichheit**, ohne Typen umzuwandeln?", "===", "=", "==", "=>", mono = true)
        typeOutput("console.log(7 <= 7);", "true")
        truth("`5 == \"5\"` ergibt in JavaScript `true`.", true, explain = "`==` wandelt den Text in eine Zahl um. Deshalb: lieber `===` benutzen!")
        match(
            "===" to "gleich",
            "!==" to "ungleich",
            ">=" to "größer oder gleich",
            "<" to "kleiner",
            "=" to "Wert speichern",
        )
        fill("Prüfe, ob die beiden Werte **ungleich** sind.", "console.log(\"Hund\" ?? \"Katze\");",
            answers = listOf("!=="), distractors = listOf("===", "=!", "<>"), output = "true")
        write("Die Variable `alter` enthält `17`. Gib aus, ob `alter` **mindestens 18** ist (also `true` oder `false`).",
            starter = """
                const alter = 17;
            """,
            solution = """
                const alter = 17;
                console.log(alter >= 18);
            """,
            output = "false",
            mustContain = listOf(">="),
            hint = "`alter >= 18` liefert direkt `true` oder `false`.")
    }

    lesson("if, else if, else", "Code nur unter einer Bedingung ausführen", LessonIcon.BRANCH) {
        text("Mit `if` führst du Code nur aus, wenn eine Bedingung `true` ist. Die Bedingung steht in runden Klammern, der Code in **geschweiften Klammern** `{ }`.")
        code("""
            const temperatur = 28;
            if (temperatur > 25) {
              console.log("Ab ins Freibad! 🏊");
            }
            console.log("Fertig");
        """, output = """
            Ab ins Freibad! 🏊
            Fertig
        """)
        text("Mit `else` sagst du, was sonst passieren soll. Mit `else if` prüfst du weitere Bedingungen – die erste, die zutrifft, gewinnt.")
        code("""
            const punkte = 72;
            if (punkte >= 90) {
              console.log("Sehr gut");
            } else if (punkte >= 70) {
              console.log("Gut");
            } else {
              console.log("Weiter üben");
            }
        """, output = "Gut")
        tip("Rücke den Code in den geschweiften Klammern ein (in JavaScript üblich: 2 Leerzeichen). Für JavaScript selbst ist das egal – aber für Menschen viel lesbarer.")
        warn("Kein Semikolon direkt nach `if (...)`! `if (x > 5); { ... }` würde den Block immer ausführen.")

        output("""
            const x = 3;
            if (x > 5) {
              console.log("groß");
            } else {
              console.log("klein");
            }
        """, "klein", "groß", "groß\nklein", "Nichts")
        output("""
            const n = 15;
            if (n > 10) {
              console.log("A");
            } else if (n > 5) {
              console.log("B");
            }
        """, "A", "B", "A\nB", "Nichts", explain = "Nur der erste zutreffende Zweig wird ausgeführt – danach ist Schluss.")
        choice("Was steht bei `if` in den runden Klammern?", "Die Bedingung", "Der Code, der ausgeführt wird", "Ein Kommentar", "Der Variablenname")
        typeOutput("""
            const zahl = 0;
            if (zahl > 0) {
              console.log("positiv");
            } else if (zahl < 0) {
              console.log("negativ");
            } else {
              console.log("null");
            }
        """, "null")
        arrange("Gib **gerade** aus, wenn `n` gerade ist, sonst **ungerade**.", """
            const n = 8;
            if (n % 2 === 0) {
              console.log("gerade");
            } else {
              console.log("ungerade");
            }
        """, output = "gerade")
        fill("Vervollständige die Abzweigung.", """
            const geld = 3;
            if (geld >= 5) {
              console.log("Eis kaufen");
            } ?? {
              console.log("Sparen");
            }
        """, answers = listOf("else"), distractors = listOf("elif", "otherwise", "then"), output = "Sparen")
        write("Die Variable `stunde` enthält `14`. Gib `Guten Morgen` aus, wenn `stunde` kleiner als 12 ist, `Guten Tag` bei kleiner als 18 und sonst `Guten Abend`.",
            starter = """
                const stunde = 14;
            """,
            solution = """
                const stunde = 14;
                if (stunde < 12) {
                  console.log("Guten Morgen");
                } else if (stunde < 18) {
                  console.log("Guten Tag");
                } else {
                  console.log("Guten Abend");
                }
            """,
            output = "Guten Tag",
            mustContain = listOf("if", "else"),
            hint = "Drei Fälle: `if`, `else if` und `else`.")
    }

    lesson("Logische Operatoren", "&&, || und ! – und was „truthy“ heißt", LessonIcon.BRAIN) {
        text("""
            Mehrere Bedingungen verknüpfst du mit logischen Operatoren:
            - `&&` (und): nur `true`, wenn **beide** Seiten stimmen
            - `||` (oder): `true`, wenn **mindestens eine** Seite stimmt
            - `!` (nicht): dreht den Wert um
        """)
        code("""
            const alter = 15;
            const mitEltern = true;
            console.log(alter >= 12 && alter < 18);
            console.log(alter >= 18 || mitEltern);
            console.log(!mitEltern);
        """, output = """
            true
            true
            false
        """)
        text("In Bedingungen zählt nicht nur `true`: Jeder Wert ist entweder **truthy** oder **falsy**. Falsy sind nur `false`, `0`, `\"\"` (leerer Text), `null`, `undefined` und `NaN` – alles andere ist truthy.")
        code("""
            const name = "";
            if (name) {
              console.log("Hallo " + name);
            } else {
              console.log("Kein Name eingegeben");
            }
        """, output = "Kein Name eingegeben")
        text("Praktisch: `??` liefert einen Ersatzwert, wenn links `null` oder `undefined` steht.")
        code("""
            let spitzname = null;
            console.log(spitzname ?? "Gast");
        """, output = "Gast")

        output("console.log(true && false);", "false", "true", "undefined", "Fehler")
        output("console.log(false || true);", "true", "false", "undefined", "Fehler")
        output("console.log(!(3 > 2));", "false", "true", "3 > 2", "Fehler")
        choice("Welcher Wert ist **falsy**?", "0", "\"0\"", "\"false\"", "[]", mono = true,
            explain = "Nur die Zahl 0 ist falsy. Die Texte `\"0\"` und `\"false\"` sind nicht leer – also truthy.")
        truth("`\"Hallo\" && 0` ist in einer Bedingung falsy.", true, explain = "Bei `&&` müssen beide Seiten truthy sein – `0` ist falsy.")
        typeOutput("console.log(undefined ?? \"Ersatz\");", "Ersatz")
        fill("Der Eintritt ist frei für Kinder **unter 6** oder Leute **ab 65**.", """
            const alter = 70;
            if (alter < 6 ?? alter >= 65) {
              console.log("Eintritt frei");
            }
        """, answers = listOf("||"), distractors = listOf("&&", "or", "!"), output = "Eintritt frei")
        output("""
            const a = 5;
            const b = 10;
            if (a > 3 && b > 3) {
              console.log("beide");
            } else {
              console.log("nicht beide");
            }
        """, "beide", "nicht beide", "beide\nnicht beide", "Nichts")
        write("Ein Passwort ist gültig, wenn es **mindestens 8 Zeichen** lang ist **und** eine Ziffer `\"1\"` enthält. Gib für `passwort = \"geheim12\"` aus, ob es gültig ist.",
            starter = """
                const passwort = "geheim12";
            """,
            solution = """
                const passwort = "geheim12";
                console.log(passwort.length >= 8 && passwort.includes("1"));
            """,
            output = "true",
            mustContain = listOf("&&", "length"),
            hint = "Verknüpfe `passwort.length >= 8` und `passwort.includes(\"1\")` mit `&&`.")
    }

    lesson("switch & der ?-Operator", "Kurze Entscheidungen und viele Fälle", LessonIcon.PUZZLE) {
        text("Für kurze Entscheidungen gibt es den **Bedingungsoperator** `bedingung ? wennJa : wennNein`. Er liefert einen von zwei Werten.")
        code("""
            const alter = 20;
            const status = alter >= 18 ? "volljährig" : "minderjährig";
            console.log(status);
        """, output = "volljährig")
        text("Wenn ein Wert mit vielen festen Möglichkeiten verglichen wird, ist `switch` übersichtlich. Jeder Fall endet mit `break`, `default` greift, wenn kein Fall passt.")
        code("""
            const tag = "Sa";
            switch (tag) {
              case "Sa":
              case "So":
                console.log("Wochenende! 🎉");
                break;
              case "Fr":
                console.log("Fast Wochenende");
                break;
              default:
                console.log("Schultag");
            }
        """, output = "Wochenende! 🎉")
        warn("Vergisst du `break`, läuft das Programm einfach in den nächsten Fall weiter („fall-through“). Manchmal gewollt – meistens ein Fehler.")

        output("console.log(5 > 3 ? \"ja\" : \"nein\");", "ja", "nein", "true", "5")
        typeOutput("""
            const punkte = 40;
            console.log(punkte >= 50 ? "bestanden" : "durchgefallen");
        """, "durchgefallen")
        output("""
            const farbe = "grün";
            switch (farbe) {
              case "rot":
                console.log("Stopp");
                break;
              case "grün":
                console.log("Los");
                break;
              default:
                console.log("Achtung");
            }
        """, "Los", "Stopp", "Achtung", "Los\nAchtung")
        output("""
            const n = 1;
            switch (n) {
              case 1:
                console.log("eins");
              case 2:
                console.log("zwei");
                break;
              default:
                console.log("viele");
            }
        """, "eins\nzwei", "eins", "zwei", "eins\nzwei\nviele", explain = "Nach `eins` fehlt das `break` – deshalb läuft es in den nächsten Fall weiter.")
        choice("Wofür steht `default` in einem `switch`?", "Für alle Fälle, die nicht aufgelistet sind", "Für den ersten Fall", "Für einen Fehler", "Für das Ende des Programms")
        fill("Vervollständige den Bedingungsoperator.", """
            const zahl = 7;
            console.log(zahl % 2 === 0 ?? "gerade" ?? "ungerade");
        """, answers = listOf("?", ":"), distractors = listOf("||", "else"), output = "ungerade")
        truth("Ohne `break` läuft ein `switch` in den nächsten Fall weiter.", true)
        write("Speichere in `ergebnis` mit dem `?`-Operator `\"Sieg\"`, wenn `tore` größer als `gegentore` ist, sonst `\"kein Sieg\"`, und gib `ergebnis` aus.",
            starter = """
                const tore = 3;
                const gegentore = 1;
            """,
            solution = """
                const tore = 3;
                const gegentore = 1;
                const ergebnis = tore > gegentore ? "Sieg" : "kein Sieg";
                console.log(ergebnis);
            """,
            output = "Sieg",
            mustContain = listOf("?", ":"),
            hint = "`const ergebnis = bedingung ? \"Sieg\" : \"kein Sieg\";`")
    }

    checkpoint()
}
