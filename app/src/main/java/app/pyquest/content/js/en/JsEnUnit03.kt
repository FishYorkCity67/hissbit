package app.pyquest.content.js.en

import app.pyquest.content.CourseBuilder
import app.pyquest.content.js.D
import app.pyquest.data.LessonIcon

fun CourseBuilder.jsEnUnit03() = unit("Input & Text", "prompt() and the tools for strings") {
    lesson("Input with prompt()", "Your program asks questions", LessonIcon.CHAT) {
        text("With `prompt()`, your program asks something and waits until an answer has been typed in. You can store the answer in a variable. In the browser a small window pops up for this – in Hissbit it's an input field below the console.")
        code("""
            const name = prompt("What's your name?");
            console.log(`Hello ${D}{name}!`);
        """, output = """
            What's your name? Ada
            Hello Ada!
        """, stdin = listOf("Ada"))
        text("Very important: `prompt()` **always returns text** – even if someone types in a number. To calculate with it, you convert the input with `Number()`.")
        code("""
            const age = Number(prompt("How old are you?"));
            console.log(`Next year you'll be ${D}{age + 1}.`);
        """, output = """
            How old are you? 13
            Next year you'll be 14.
        """, stdin = listOf("13"))
        warn("Without `Number()`, `\"13\" + 1` would give the text `\"131\"` – the number simply gets tacked on!")
        tip("If someone cancels the input, `prompt()` returns the value `null`. You can ignore that for now.")

        choice("What type does the result of `prompt()` have?", "string", "number", "boolean", "It depends on the input", mono = false,
            explain = "`prompt()` always returns text – even when the input is 42.")
        output("""
            const number = "4";
            console.log(number + 1);
        """, "41", "5", "4 + 1", "NaN", explain = "Without a conversion, the text `\"4\"` is joined with 1.")
        fill("Convert the input into a number.", """
            const n = ??(prompt("Number?"));
            console.log(n * 2);
        """, answers = listOf("Number"), distractors = listOf("String", "Text", "toNumber"))
        truth("`prompt()` waits until something has been typed in.", true)
        arrange("Ask for a favorite color and print it in a sentence.", """
            const color = prompt("Favorite color?");
            console.log(`Your favorite color is ${D}{color}.`);
        """)
        write("Use `prompt(\"What's your name?\")` to ask for a name and then print `Welcome, <name>!`.",
            solution = """
                const name = prompt("What's your name?");
                console.log(`Welcome, ${D}{name}!`);
            """,
            output = "Welcome, Monty!",
            stdin = listOf("Monty"),
            mustContain = listOf("prompt"),
            hint = "Store the answer from `prompt()` in a variable and insert it into a template string.")
        write("Ask for two numbers (one `prompt()` each) and print their **sum**.",
            solution = """
                const a = Number(prompt("First number?"));
                const b = Number(prompt("Second number?"));
                console.log(a + b);
            """,
            output = "12",
            stdin = listOf("5", "7"),
            mustContain = listOf("prompt", "Number"),
            hint = "Without `Number()`, `\"5\"` and `\"7\"` would come out as the text `57`.")
    }

    lesson("Characters & Length", "Getting at single letters", LessonIcon.BOOK) {
        text("A string is a sequence of characters. With `.length` you find out how many characters it has – spaces count too.")
        code("""
            const word = "JavaScript";
            console.log(word.length);
            console.log("Hello world".length);
        """, output = """
            10
            11
        """)
        text("Every character has a position, its **index**. Counting starts at **0**! You get a character out with square brackets. `.at(-1)` gives you the last character.")
        code("""
            const animal = "Monty";
            console.log(animal[0]);
            console.log(animal[4]);
            console.log(animal.at(-1));
            console.log(animal[animal.length - 1]);
        """, output = """
            M
            y
            y
            y
        """)
        tip("The last index is always `length - 1`. An index that doesn't exist gives you `undefined` – without an error message.")
        warn("Strings can't be changed: `animal[0] = \"B\"` does nothing. You can only build new strings.")

        output("""console.log("Hello".length);""", "5", "4", "6", "Hello")
        output("""
            const s = "Python";
            console.log(s[1]);
        """, "y", "P", "t", "Py", explain = "The index starts at 0: `s[0]` is P, `s[1]` is y.")
        typeOutput("""console.log("Monty".at(-1));""", "y")
        output("""console.log("abc"[5]);""", "undefined", "c", "Error", "\"\"")
        choice("What index does the **first** character of a string have?", "0", "1", "-1", "It depends on the string", mono = true)
        fill("Print the first letter.", """
            const name = "Ada";
            console.log(name[??]);
        """, answers = listOf("0"), distractors = listOf("1", "-1", "first"), output = "A")
        truth("`\"Hi there\".length` gives 7.", false, explain = "The space counts too – it's 8 characters.")
        write("The variable `word` contains `\"Snake\"`. Print the **first** and the **last** character on one line, separated by a space: `S e`.",
            starter = """
                const word = "Snake";
            """,
            solution = """
                const word = "Snake";
                console.log(word[0], word.at(-1));
            """,
            output = "S e",
            hint = "`word[0]` is the first character, `word.at(-1)` or `word[word.length - 1]` is the last one.")
    }

    lesson("String Methods", "Transforming and searching text", LessonIcon.MAGIC) {
        text("Strings come with lots of **methods** – functions that you call with a dot after the string. They don't change the original string; they return a new one.")
        code("""
            const text = "  Hello World  ";
            console.log(text.toUpperCase());
            console.log(text.toLowerCase());
            console.log(text.trim());
            console.log(`[${D}{text.trim()}]`);
        """, output = """
              HELLO WORLD
              hello world
            Hello World
            [Hello World]
        """)
        text("For searching: `includes()` checks whether something appears, `startsWith()`/`endsWith()` check the start and the end, and `indexOf()` gives you the position (or `-1`).")
        code("""
            const sentence = "JavaScript is fun";
            console.log(sentence.includes("is"));
            console.log(sentence.startsWith("Java"));
            console.log(sentence.indexOf("fun"));
            console.log(sentence.indexOf("Python"));
        """, output = """
            true
            true
            14
            -1
        """)
        text("For replacing and repeating: `replace()` replaces the first occurrence, `replaceAll()` replaces all of them. `repeat()` repeats a string.")
        code("""
            console.log("I like tea".replace("tea", "cocoa"));
            console.log("a-b-c".replaceAll("-", "+"));
            console.log("ab".repeat(3));
            console.log("7".padStart(3, "0"));
        """, output = """
            I like cocoa
            a+b+c
            ababab
            007
        """)
        tip("You can **chain** methods: `entry.trim().toLowerCase()` first removes the spaces and then makes everything lowercase.")

        output("""console.log("monty".toUpperCase());""", "MONTY", "Monty", "monty", "mONTY")
        output("""console.log("banana".includes("nan"));""", "true", "false", "2", "nan")
        typeOutput("""console.log("Hello".replace("H", "J"));""", "Jello")
        output("""console.log("cake".indexOf("x"));""", "-1", "0", "undefined", "false")
        match(
            "toUpperCase()" to "all capital letters",
            "trim()" to "remove outer spaces",
            "includes()" to "does it appear?",
            "replace()" to "swap out a part",
            "repeat()" to "repeat the text",
        )
        fill("First remove the spaces at the edges – then everything is made lowercase.", """
            const entry = "  YES ";
            console.log(entry.??().toLowerCase());
        """, answers = listOf("trim"), distractors = listOf("toUpperCase", "strip", "length"), output = "yes")
        output("""console.log("Na".repeat(4) + " Batman!");""", "NaNaNaNa Batman!", "Na4 Batman!", "Na Na Na Na Batman!", "NaN Batman!")
        write("`entry` holds `\"  HISSBIT  \"`. Print the text without the spaces at the edges and in lowercase.",
            starter = """
                const entry = "  HISSBIT  ";
            """,
            solution = """
                const entry = "  HISSBIT  ";
                console.log(entry.trim().toLowerCase());
            """,
            output = "hissbit",
            mustContain = listOf("trim", "toLowerCase"),
            hint = "Chain the two methods: `entry.trim().toLowerCase()`.")
    }

    lesson("Cutting Up Text", "slice(), split() and join()", LessonIcon.PUZZLE) {
        text("With `slice(start, end)` you cut a piece out of a string. The character at position `end` is **not** included anymore. If you leave out `end`, it goes all the way to the end. Negative numbers count from the back.")
        code("""
            const word = "chocolate";
            console.log(word.slice(0, 5));
            console.log(word.slice(5));
            console.log(word.slice(-3));
        """, output = """
            choco
            late
            ate
        """)
        text("`split()` splits a string at a separator into an **array** (a list). `join()` puts an array back together into a string. You'll learn more about arrays soon.")
        code("""
            const date = "12/24/2025";
            const parts = date.split("/");
            console.log(parts);
            console.log(parts[2]);
            console.log(parts.join("-"));
        """, output = """
            [ '12', '24', '2025' ]
            2025
            12-24-2025
        """)
        tip("`split(\"\")` splits a string into single characters – and `.split(\"\").reverse().join(\"\")` reverses it.")
        code("""
            const reversed = "Monty".split("").reverse().join("");
            console.log(reversed);
        """, output = "ytnoM")

        output("""console.log("Hello".slice(1, 3));""", "el", "ell", "He", "ll", explain = "From index 1 (e) up to just before index 3 – so e and l.")
        output("""console.log("banana".slice(-3));""", "ana", "ban", "nana", "na")
        typeOutput("""console.log("a,b,c".split(",").length);""", "3")
        output("""console.log(["x", "y", "z"].join("-"));""", "x-y-z", "xyz", "x, y, z", "[x-y-z]")
        choice("What does `\"red green blue\".split(\" \")` return?", "An array with three strings", "A string without spaces", "The number 3", "An error")
        fill("Get the first three letters.", """console.log("Pizza".slice(??, ??));""",
            answers = listOf("0", "3"), distractors = listOf("1", "2"), output = "Piz")
        truth("With `slice(2, 5)`, the character at index 5 is still included.", false)
        write("The variable `name` contains `\"Ada Lovelace\"`. Split the name at the space with `split()` and print only the **last name**.",
            starter = """
                const name = "Ada Lovelace";
            """,
            solution = """
                const name = "Ada Lovelace";
                const parts = name.split(" ");
                console.log(parts[1]);
            """,
            output = "Lovelace",
            mustContain = listOf("split"),
            hint = "`name.split(\" \")` gives you `[\"Ada\", \"Lovelace\"]` – the last name has index 1.")
    }

    checkpoint()
}
