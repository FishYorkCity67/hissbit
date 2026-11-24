package app.pyquest.content.js.en

import app.pyquest.content.CourseBuilder
import app.pyquest.content.js.D
import app.pyquest.data.LessonIcon

fun CourseBuilder.jsEnUnit02() = unit("Variables & Data Types", "Storing values with let and const") {
    lesson("Variables with let", "Storing values under a name", LessonIcon.BOX) {
        text("A **variable** is like a labeled box: you put a value inside and get it back out later using its name. With `let` you create a new variable.")
        code("""
            let name = "Monty";
            let age = 5;
            console.log(name);
            console.log(age + 1);
        """, output = """
            Monty
            6
        """)
        text("The `=` means **“store”** – not “is equal to”. A `let` variable can get a new value later. When you do that, you **don't** write `let` again.")
        code("""
            let points = 10;
            points = 15;
            points = points + 5;
            console.log(points);
        """, output = "20")
        tip("Shorthands: `points += 5` is the same as `points = points + 5`. And `points++` increases it by exactly 1.")
        code("""
            let lives = 3;
            lives -= 1;
            lives++;
            console.log(lives);
        """, output = "3")

        choice("What do you use to create a new variable whose value may change?", "let", "var", "make", "new", mono = true,
            explain = "`var` exists too – but that's the old way, and it has some pitfalls. Today you use `let` and `const`.")
        output("""
            let x = 4;
            x = x * 2;
            console.log(x);
        """, "8", "4", "x * 2", "16")
        truth("When you give a variable a new value, you write `let` in front of it again.", false,
            explain = "`let` only goes in front when you first create the variable. After that, `name = newValue;` is enough.")
        typeOutput("""
            let a = 3;
            let b = a + 2;
            a = 10;
            console.log(b);
        """, "5", explain = "`b` gets the value 5 when that line runs. Changing `a` afterwards doesn't change `b` anymore.")
        fill("Increase the counter by 1.", """
            let counter = 7;
            counter??;
            console.log(counter);
        """, answers = listOf("++"), distractors = listOf("--", "+1", "**"), output = "8")
        output("""
            let balance = 100;
            balance -= 30;
            balance += 5;
            console.log(balance);
        """, "75", "135", "65", "100")
        arrange("Create the variable **price** with the value 4, double it and print it.", """
            let price = 4;
            price = price * 2;
            console.log(price);
        """, output = "8")
        write("Create a variable `city` with the value `\"London\"` and print it.",
            solution = """
                let city = "London";
                console.log(city);
            """,
            output = "London",
            mustContain = listOf("city"),
            hint = "`let city = \"London\";` and then `console.log(city);`")
    }

    lesson("const and Good Names", "Fixed values and rules for names", LessonIcon.BOX) {
        text("With `const` you create a **constant**: its value is set once and can't be replaced afterwards. Pros use `const` whenever they can – and `let` only when the value really needs to change.")
        code("""
            const pi = 3.14159;
            const greeting = "Hello";
            console.log(greeting, pi);
        """, output = "Hello 3.14159")
        text("If you try to give a `const` variable a new value, the program stops with an error:")
        code("""
            const days = 7;
            days = 8;
        """, fails = true)
        text("""
            Rules for variable names:
            - Letters, digits, `_` and `${D}` are allowed – but no spaces
            - A name must **not start with a digit**
            - Upper and lower case matter: `name` and `Name` are different variables
            - Words like `let`, `if` or `class` are reserved
        """)
        tip("In JavaScript, names made of several words are written in **camelCase**: `playerCount`, `maxPoints`, `isDone` – every new word starts with a capital letter.")
        warn("Letters with accents, like in `café`, technically work, but they're unusual. Stick to plain letters: `cafe`.")

        choice("Which variable name is **valid**?", "playerName", "2players", "player name", "let", mono = true)
        truth("You can replace the value of a `const` variable later.", false)
        choice("What happens with `const x = 1;` followed by `x = 2;`?",
            "You get an error (TypeError).",
            "x has the value 2 afterwards.",
            "x has the value 3 afterwards.",
            "The second line is ignored.")
        choice("Which name follows the usual **camelCase** style?", "highScore", "high_score", "HighScore", "highscore", mono = true,
            explain = "camelCase: the first word is lowercase, and every further word starts with a capital letter.")
        output("""
            const word = "coffee";
            let count = 2;
            count = count + 1;
            console.log(count, word);
        """, "3 coffee", "2 coffee", "3coffee", "count word")
        fill("Create a constant that should never change.", """
            ?? hours = 24;
            console.log(hours);
        """, answers = listOf("const"), distractors = listOf("constant", "fix", "Const"), output = "24")
        truth("In JavaScript, `color` and `Color` are two different variables.", true)
        write("Store your favorite name for a snake in the constant `snake` (e.g. `\"Monty\"`) and print `My snake is called` followed by the name.",
            solution = """
                const snake = "Monty";
                console.log("My snake is called", snake);
            """,
            test = """
                if (typeof snake !== "string") throw new Error("Create the constant snake with a text value.");
            """,
            mustContain = listOf("const snake", "console.log"),
            hint = "`const snake = \"Monty\";` and then `console.log(\"My snake is called\", snake);`")
    }

    lesson("Data Types", "Numbers, text, truth values and nothing", LessonIcon.BRAIN) {
        text("""
            Every value has a **type**. The most important ones:
            - `number` – numbers like `42` or `3.5`
            - `string` – text like `"Hello"`
            - `boolean` – truth values: `true` or `false`
            - `undefined` – “no value yet”
            - `null` – “empty on purpose”
        """)
        text("With `typeof` you ask for the type of a value:")
        code("""
            console.log(typeof 42);
            console.log(typeof "42");
            console.log(typeof true);
            let empty;
            console.log(empty, typeof empty);
        """, output = """
            number
            string
            boolean
            undefined undefined
        """)
        tip("A variable that you haven't given a value yet contains `undefined`. With `null`, on the other hand, you say on purpose: “There's nothing here (yet).”")
        warn("`typeof null` gives `\"object\"` – a famous old bug in JavaScript that was never fixed, so that old websites keep working.")
        code("""
            let winner = null;
            console.log(winner);
            console.log(typeof null);
        """, output = """
            null
            object
        """)

        output("console.log(typeof 3.14);", "number", "float", "string", "decimal")
        output("console.log(typeof \"true\");", "string", "boolean", "true", "undefined",
            explain = "Because of the quotes, `\"true\"` is text, not a truth value.")
        match(
            "42" to "number",
            "\"Hello\"" to "string",
            "false" to "boolean",
            "undefined" to "no value yet",
            "null" to "empty on purpose",
        )
        typeOutput("""
            let x;
            console.log(x);
        """, "undefined")
        choice("Which values does the type `boolean` have?", "true and false", "0 and 1", "yes and no", "right and wrong", mono = false)
        truth("`typeof null` gives `\"null\"`.", false, explain = "Surprise: it gives `\"object\"` – a historical bug in JavaScript.")
        fill("Find out which type the value has.", """console.log(?? "Monty");""", answers = listOf("typeof"), distractors = listOf("type", "typeOf", "instanceof"), output = "string")
        write("Print the types of the values `7`, `\"7\"` and `false` one after another – each with `typeof` on its own line.",
            solution = """
                console.log(typeof 7);
                console.log(typeof "7");
                console.log(typeof false);
            """,
            output = """
                number
                string
                boolean
            """,
            mustContain = listOf("typeof"),
            hint = "Three times `console.log(typeof ...);`")
    }

    lesson("Conversion & NaN", "When text and numbers meet", LessonIcon.MAGIC) {
        text("Careful, here's a typical JavaScript trap: when `+` meets **text**, it doesn't calculate – it **joins** the values. Other arithmetic operators, on the other hand, turn text into numbers.")
        code("""
            console.log("5" + 3);
            console.log("5" - 3);
            console.log("5" * "2");
        """, output = """
            53
            2
            10
        """)
        text("It's better to convert values yourself: `Number(text)` turns text into a number, `String(number)` turns a number into text.")
        code("""
            const entry = "42";
            const number = Number(entry);
            console.log(number + 8);
            console.log(String(7) + 7);
        """, output = """
            50
            77
        """)
        text("If text can't be turned into a number, you get **`NaN`** – “Not a Number”. You check for it with `Number.isNaN()`.")
        code("""
            const x = Number("Hello");
            console.log(x);
            console.log(Number.isNaN(x));
            console.log(parseInt("12 apples"));
        """, output = """
            NaN
            true
            12
        """)
        tip("`parseInt()` reads a whole number from the start of a text, `parseFloat()` reads decimals too: `parseFloat(\"3.5 kg\")` gives `3.5`.")
        tip("Rounding decimals: `(2 / 3).toFixed(2)` gives the text `\"0.67\"`.")

        output("console.log(\"2\" + 2);", "22", "4", "\"4\"", "NaN")
        output("console.log(\"10\" - 4);", "6", "104", "NaN", "10 - 4",
            explain = "`-` can only calculate – so JavaScript turns `\"10\"` into the number 10.")
        output("console.log(Number(\"abc\"));", "NaN", "0", "abc", "undefined")
        typeOutput("console.log(Number(\"3\") + 4);", "7")
        choice("What does **NaN** mean?", "Not a Number – not a valid number", "Null and Nothing", "New array Number", "A very large number")
        truth("In JavaScript, `\"3\" * \"4\"` gives `12`.", true, explain = "`*` turns both texts into numbers.")
        fill("Turn the text into a number so that JavaScript calculates.", """
            const age = "16";
            console.log(??(age) + 1);
        """, answers = listOf("Number"), distractors = listOf("String", "number", "Int"), output = "17")
        output("console.log((10 / 3).toFixed(1));", "3.3", "3.33", "3", "3.4")
        write("`priceText` holds the price `\"19.99\"` as text. Turn it into a number and print the price for **3 items**, rounded to 2 decimal places (`59.97`).",
            starter = """
                const priceText = "19.99";
            """,
            solution = """
                const priceText = "19.99";
                const price = Number(priceText);
                console.log((price * 3).toFixed(2));
            """,
            output = "59.97",
            mustContain = listOf("toFixed"),
            hint = "`Number(priceText) * 3` does the math, and `.toFixed(2)` rounds to two places.")
    }

    lesson("Template Strings", "Text with values built right in", LessonIcon.CHAT) {
        text("Building text with `+` quickly gets messy. **Template strings** are more elegant: they go between **backticks** `` ` `` and can insert values directly with `${D}{...}`.")
        code("""
            const name = "Ada";
            const age = 36;
            console.log(`${D}{name} is ${D}{age} years old.`);
        """, output = "Ada is 36 years old.")
        text("Any expression can go inside `${D}{...}` – even calculations:")
        code("""
            const price = 4;
            const quantity = 3;
            console.log(`Total: ${D}{price * quantity} euros`);
        """, output = "Total: 12 euros")
        text("Template strings can also span **several lines**:")
        code("""
            const poem = `Roses are red,
            violets are blue.`;
            console.log(poem);
        """, output = """
            Roses are red,
            violets are blue.
        """)
        tip("On many phone keyboards you'll find the backtick `` ` `` among the special characters – or at the bottom in Hissbit's symbol bar.")

        output("""
            const animal = "cat";
            console.log(`The ${D}{animal} is sleeping.`);
        """, "The cat is sleeping.", "The ${D}{animal} is sleeping.", "The animal is sleeping.", "The \"cat\" is sleeping.")
        output("console.log(`2 + 3 = ${D}{2 + 3}`);", "2 + 3 = 5", "2 + 3 = 2 + 3", "5 = 5", "2 + 3 = 23")
        choice("Which characters does a template string go between?", "Backticks `` ` ``", "Double quotes \"", "Single quotes '", "Brackets ( )")
        truth("In `\"Hello ${D}{name}\"` (with normal quotes), the name gets inserted.", false,
            explain = "Only template strings with backticks insert `${D}{...}`. In normal strings, the text stays exactly as it is.")
        fill("Put the name into the text.", """
            const hero = "Monty";
            console.log(`Hello, ??!`);
        """, answers = listOf("${D}{hero}"), distractors = listOf("{hero}", "hero", "${D}hero"), output = "Hello, Monty!")
        typeOutput("""
            const a = 6;
            console.log(`${D}{a} * 7 = ${D}{a * 7}`);
        """, "6 * 7 = 42")
        arrange("Use a template string to print `Level 3 of 10`.", """
            const level = 3;
            const max = 10;
            console.log(`Level ${D}{level} of ${D}{max}`);
        """, output = "Level 3 of 10")
        write("Create the constants `product` (`\"pizza\"`) and `price` (`8`). Use **one template string** to print: `A pizza costs 8 euros.`",
            solution = """
                const product = "pizza";
                const price = 8;
                console.log(`A ${D}{product} costs ${D}{price} euros.`);
            """,
            output = "A pizza costs 8 euros.",
            mustContain = listOf("`", "${D}{"),
            hint = "Use backticks instead of quotes and insert the values with `${D}{product}` and `${D}{price}`.")
    }

    checkpoint()
}
