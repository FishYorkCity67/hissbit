package app.pyquest.content.js.en

import app.pyquest.content.CourseBuilder
import app.pyquest.content.js.D
import app.pyquest.data.LessonIcon

fun CourseBuilder.jsEnUnit13() = unit("Useful Tools", "Map, Set, spread, JSON, Math and dates") {
    lesson("Map & Set", "Key-value lists and sets", LessonIcon.BOX) {
        text("A **Set** is a collection without duplicates. Perfect for removing duplicates or quickly checking whether something is in there.")
        code("""
            const tags = new Set(["js", "web", "js", "code", "web"]);
            console.log(tags);
            console.log(tags.size, tags.has("code"));
            tags.add("new");
            tags.delete("web");
            console.log([...tags]);
        """, output = """
            Set(3) { 'js', 'web', 'code' }
            3 true
            [ 'js', 'code', 'new' ]
        """)
        text("A **Map** stores pairs of key and value – similar to an object, but with its own methods: `set`, `get`, `has`, `delete` and `size`. Keys can be any values.")
        code("""
            const phonebook = new Map();
            phonebook.set("Ada", "0171 123");
            phonebook.set("Ben", "0160 456");
            console.log(phonebook.get("Ada"));
            console.log(phonebook.has("Cleo"), phonebook.size);
            for (const [name, number] of phonebook) {
              console.log(`${D}{name}: ${D}{number}`);
            }
        """, output = """
            0171 123
            false 2
            Ada: 0171 123
            Ben: 0160 456
        """)
        tip("Removing duplicates from an array in one line: `[...new Set(array)]`.")

        output("""console.log(new Set([1, 1, 2, 2, 3]).size);""", "3", "5", "2", "6")
        output("""console.log([...new Set(["a", "b", "a"])]);""", "[ 'a', 'b' ]", "[ 'a', 'b', 'a' ]", "Set(2) { 'a', 'b' }", "2")
        typeOutput("""
            const m = new Map([["x", 10], ["y", 20]]);
            console.log(m.get("y"));
        """, "20")
        output("""
            const s = new Set();
            s.add(5);
            s.add(5);
            console.log(s.has(5), s.size);
        """, "true 1", "true 2", "false 0", "5 1")
        match(
            "set()" to "Map: store a pair",
            "get()" to "Map: fetch a value",
            "add()" to "Set: add a value",
            "has()" to "contains it?",
            "size" to "number of entries",
        )
        fill("Count how many **different** colors there are.", """
            const colors = ["red", "blue", "red", "green", "blue"];
            console.log(new ??(colors).size);
        """, answers = listOf("Set"), distractors = listOf("Map", "Array", "Object"), output = "3")
        truth("A Set can contain the same value several times.", false)
        write("Use a `Map` to count how often each word occurs in the array `words`, then print each line as `<word>: <count>`.",
            starter = """
                const words = ["yes", "no", "yes", "maybe", "yes", "no"];
            """,
            solution = """
                const words = ["yes", "no", "yes", "maybe", "yes", "no"];
                const counter = new Map();
                for (const w of words) {
                  counter.set(w, (counter.get(w) ?? 0) + 1);
                }
                for (const [word, count] of counter) {
                  console.log(`${D}{word}: ${D}{count}`);
                }
            """,
            output = """
                yes: 3
                no: 2
                maybe: 1
            """,
            mustContain = listOf("Map"),
            hint = "`counter.set(w, (counter.get(w) ?? 0) + 1)` – the first time, `get` returns `undefined`, hence the fallback value 0.")
    }

    lesson("Spread & Rest", "The three dots ...", LessonIcon.MAGIC) {
        text("The three dots `...` have two jobs. As **spread** they **unpack** an array or object – for example, to join arrays or to pass values one by one to a function.")
        code("""
            const a = [1, 2];
            const b = [3, 4];
            console.log([...a, ...b, 5]);
            console.log(Math.max(...b));
            const base = { color: "red" };
            console.log({ ...base, size: "L" });
        """, output = """
            [ 1, 2, 3, 4, 5 ]
            4
            { color: 'red', size: 'L' }
        """)
        text("As a **rest parameter** they gather any number of arguments into an array. That's how you write functions that cope with any number of values:")
        code("""
            function sum(...numbers) {
              return numbers.reduce((s, n) => s + n, 0);
            }
            console.log(sum(1, 2), sum(5, 5, 5, 5));

            function team(captain, ...rest) {
              console.log("Captain:", captain, "| Rest:", rest);
            }
            team("Ada", "Ben", "Cleo");
        """, output = """
            3 20
            Captain: Ada | Rest: [ 'Ben', 'Cleo' ]
        """)
        tip("Rule of thumb: if `...` appears in a **call** or inside an array/object, it's spread (unpacking). If it appears in the **parameter list**, it's rest (gathering).")

        output("""console.log([..."Hey"]);""", "[ 'H', 'e', 'y' ]", "[ 'Hey' ]", "Hey", "[ ...'Hey' ]")
        output("""
            function count(...values) {
              return values.length;
            }
            console.log(count("a", "b", "c"));
        """, "3", "1", "0", "[ 'a', 'b', 'c' ]")
        typeOutput("""
            const x = [10, 20];
            const y = [...x, 30];
            console.log(y.length);
        """, "3")
        output("""
            const o = { a: 1, b: 2 };
            const p = { ...o, b: 3, c: 4 };
            console.log(p);
        """, "{ a: 1, b: 3, c: 4 }", "{ a: 1, b: 2, c: 4 }", "{ b: 3, c: 4 }", "{ a: 1, b: 2, b: 3, c: 4 }")
        choice("What does `...` do in `function f(...args)`?", "Gathers all arguments in an array", "Unpacks an array", "Copies the function", "Is a syntax error")
        fill("Find the minimum of the array.", """
            const temps = [7, -2, 12, 3];
            console.log(Math.min(??temps));
        """, answers = listOf("..."), distractors = listOf("*", "&", ".."), output = "-2")
        truth("`[...list]` creates a copy of `list`.", true)
        write("Write a function `average(...numbers)` that returns the average of any number of numbers.",
            solution = """
                function average(...numbers) {
                  return numbers.reduce((s, n) => s + n, 0) / numbers.length;
                }
            """,
            test = """
                if (average(2, 4) !== 3) throw new Error("average(2, 4) should be 3");
                if (average(1, 2, 3, 4, 5) !== 3) throw new Error("average(1, 2, 3, 4, 5) should be 3");
                if (average(10) !== 10) throw new Error("average(10) should be 10");
                console.log("✅ All tests passed!");
            """,
            mustContain = listOf("..."),
            hint = "With `...numbers` you get an array – sum it with `reduce`, then divide by `numbers.length`.")
    }

    lesson("JSON", "Saving and reading data as text", LessonIcon.FILE) {
        text("**JSON** (JavaScript Object Notation) is the standard format for saving or sending data as text – almost every app on the internet uses it. `JSON.stringify()` turns data into text.")
        code("""
            const player = { name: "Ada", level: 7, items: ["Sword", "Potion"] };
            const text = JSON.stringify(player);
            console.log(text);
            console.log(typeof text);
        """, output = """
            {"name":"Ada","level":7,"items":["Sword","Potion"]}
            string
        """)
        text("With a third argument, the text gets neatly indented:")
        code("""
            console.log(JSON.stringify({ a: 1, b: [true, null] }, null, 2));
        """, output = """
            {
              "a": 1,
              "b": [
                true,
                null
              ]
            }
        """)
        text("`JSON.parse()` turns JSON text back into real data. If the text isn't valid JSON, you get a `SyntaxError` – so `JSON.parse` often belongs in a `try`.")
        code("""
            const data = JSON.parse('{"city":"Berlin","population":3700000}');
            console.log(data.city, data.population);
            try {
              JSON.parse("{city: Berlin}");
            } catch (e) {
              console.log(e.name);
            }
        """, output = """
            Berlin 3700000
            SyntaxError
        """)
        warn("In JSON, keys and strings must be in **double** quotes. Functions and `undefined` are simply left out during conversion.")
        tip("With `JSON.parse(JSON.stringify(obj))` you get a deep copy of a simple object.")

        output("""console.log(JSON.stringify([1, "a", true]));""", "[1,\"a\",true]", "[ 1, 'a', true ]", "1,a,true", "\"[1,a,true]\"")
        output("""console.log(JSON.parse("[1, 2, 3]").length);""", "3", "9", "[1, 2, 3]", "undefined")
        typeOutput("""
            const o = JSON.parse('{"x": 5}');
            console.log(o.x * 2);
        """, "10")
        output("""console.log(JSON.stringify({ name: "Bo", f() {} }));""", "{\"name\":\"Bo\"}", "{\"name\":\"Bo\",\"f\":\"f() {}\"}", "{ name: 'Bo' }", "Error",
            explain = "Functions aren't part of JSON and are left out during conversion.")
        choice("What does `JSON.stringify(obj)` return?", "A string", "An object", "An array", "A number")
        fill("Turn the JSON text back into an object.", """
            const text = '{"animal":"Snake"}';
            const obj = JSON.??(text);
            console.log(obj.animal);
        """, answers = listOf("parse"), distractors = listOf("stringify", "read", "toObject"), output = "Snake")
        truth("In JSON, keys may be in single quotes.", false)
        write("""
            The variable `saved` holds a game state as JSON text. Read it in with `JSON.parse`, increase `points` by 50,
            add `"Shield"` to the `items` array, and print the new game state using `JSON.stringify` (without indentation).
        """.trimIndent(),
            starter = """
                const saved = '{"name":"Monty","points":100,"items":["Hat"]}';
            """,
            solution = """
                const saved = '{"name":"Monty","points":100,"items":["Hat"]}';
                const state = JSON.parse(saved);
                state.points += 50;
                state.items.push("Shield");
                console.log(JSON.stringify(state));
            """,
            output = """{"name":"Monty","points":150,"items":["Hat","Shield"]}""",
            mustContain = listOf("JSON.parse", "JSON.stringify"),
            hint = "`JSON.parse` → change → `JSON.stringify`.")
    }

    lesson("Math, Random & Dates", "Rounding, rolling dice, calculating with dates", LessonIcon.CALC) {
        text("""
            The `Math` object has lots of helpers:
            - `Math.round()` rounds to the nearest whole number, `Math.floor()` rounds down, `Math.ceil()` rounds up
            - `Math.max()`/`Math.min()` find the largest/smallest
            - `Math.abs()` is the absolute value, `Math.sqrt()` the square root, `Math.PI` the number pi
        """)
        code("""
            console.log(Math.round(2.5), Math.floor(2.9), Math.ceil(2.1));
            console.log(Math.max(4, 9, 1), Math.abs(-7), Math.sqrt(81));
            console.log(Math.PI.toFixed(4));
        """, output = """
            3 2 3
            9 7 9
            3.1416
        """)
        text("`Math.random()` returns a random decimal number from 0 (inclusive) up to 1 (exclusive). For a die from 1 to 6, you multiply, round down and add 1.")
        code("""
            const roll = Math.floor(Math.random() * 6) + 1;
            console.log(roll >= 1 && roll <= 6, Number.isInteger(roll));
            const names = ["Ada", "Ben", "Cleo"];
            const pick = names[Math.floor(Math.random() * names.length)];
            console.log(names.includes(pick));
        """, output = """
            true true
            true
        """)
        text("Decimal numbers aren't always exact inside a computer – a well-known effect in almost all programming languages. To display them, you round with `toFixed()`.")
        code("""
            console.log(0.1 + 0.2);
            console.log((0.1 + 0.2).toFixed(2));
        """, output = """
            0.30000000000000004
            0.30
        """)
        text("With `Date` you work with dates and times. Careful: months count **from 0** – January is 0, December is 11!")
        code("""
            const birthday = new Date(2010, 4, 17);
            console.log(birthday.getFullYear(), birthday.getMonth() + 1, birthday.getDate());
            const weekdays = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"];
            console.log(weekdays[birthday.getDay()]);
            const newYearsEve = new Date(2025, 11, 31);
            const days = Math.round((newYearsEve - new Date(2025, 11, 24)) / 86400000);
            console.log(`From Christmas Eve to New Year's Eve: ${D}{days} days`);
        """, output = """
            2010 5 17
            Mon
            From Christmas Eve to New Year's Eve: 7 days
        """)
        tip("`new Date()` without arguments returns the current moment. Subtracting two dates gives you the difference in milliseconds.")

        output("console.log(Math.round(4.4), Math.round(4.6));", "4 5", "4 4", "5 5", "4.4 4.6")
        output("console.log(Math.ceil(7.01));", "8", "7", "7.01", "7.1")
        typeOutput("console.log(Math.max(3, 11, 7) - Math.min(3, 11, 7));", "8")
        choice("Which values can `Math.floor(Math.random() * 6) + 1` have?", "1 to 6", "0 to 5", "0 to 6", "1 to 7")
        output("""
            const d = new Date(2024, 0, 15);
            console.log(d.getMonth());
        """, "0", "1", "15", "2024", explain = "Months count from 0 – January is month 0.")
        fill("Round the price **up** to the next whole number.", """console.log(Math.??(19.01));""",
            answers = listOf("ceil"), distractors = listOf("floor", "round", "trunc"), output = "20")
        truth("`0.1 + 0.2 === 0.3` gives `true` in JavaScript.", false, explain = "Because decimal numbers are imprecise, the result is 0.30000000000000004.")
        write("Write a function `randomBetween(min, max)` that returns a random **whole** number from `min` to `max` (both inclusive).",
            solution = """
                function randomBetween(min, max) {
                  return Math.floor(Math.random() * (max - min + 1)) + min;
                }
            """,
            test = """
                const seen = new Set();
                for (let i = 0; i < 2000; i++) {
                  const n = randomBetween(3, 6);
                  if (!Number.isInteger(n) || n < 3 || n > 6) throw new Error("randomBetween(3, 6) returned " + n);
                  seen.add(n);
                }
                if (seen.size !== 4) throw new Error("All numbers from 3 to 6 should appear, seen: " + [...seen].sort().join(", "));
                console.log("✅ All tests passed!");
            """,
            mustContain = listOf("Math.random", "Math.floor"),
            hint = "`Math.floor(Math.random() * (max - min + 1)) + min`")
    }

    checkpoint()
}
