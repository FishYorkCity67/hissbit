package app.pyquest.content.js.en

import app.pyquest.content.CourseBuilder
import app.pyquest.data.LessonIcon

fun CourseBuilder.jsEnUnit01() = unit("First Steps", "Your first program, calculating and error messages") {
    lesson("Hello, World!", "Your very first JavaScript program", LessonIcon.CHAT) {
        text("""
            Welcome to JavaScript! JavaScript is the language of the web: it runs in every browser, powers almost every website and lots of apps – and with Node.js it even runs on servers. If you know JavaScript, you can bring websites to life.
        """)
        text("""
            With `console.log()`, JavaScript prints something to the **console**. What should be printed goes inside the round brackets. At the end of the line there's a **semicolon** `;`.
        """)
        code("""console.log("Hello, World!");""", output = "Hello, World!")
        text("""
            Text has to be in **quotes**. You can use double `"..."` or single `'...'` quotes – both work the same way. A piece of text like this is called a **string**.
        """)
        code("""
            console.log("I am learning JavaScript!");
            console.log('Monty is a snake.');
        """, output = """
            I am learning JavaScript!
            Monty is a snake.
        """)
        tip("Every `console.log()` prints its own line. JavaScript works through your program from top to bottom – one statement after another.")
        warn("Watch the spelling: `console.log` – all lowercase with a dot in between. JavaScript doesn't know `Console.Log` or `console.Log`!")

        choice("Which statement prints something to the console?", "console.log()", "print()", "echo()", "System.out()", mono = true)
        output("""console.log("Hello, World!");""", "Hello, World!", "\"Hello, World!\"", "console.log(Hello, World!)", "Nothing",
            explain = "The quotes only mark the start and the end of the string – they aren't printed.")
        truth("In JavaScript, text has to be in quotes.", true,
            explain = "Without quotes, JavaScript thinks the text is a name – for example, the name of a variable.")
        fill("Print the text **Hello**.", """console.??("Hello");""", answers = listOf("log"), distractors = listOf("print", "write", "show"), output = "Hello")
        arrange("Print **Good morning** first and then **Monty**.", """
            console.log("Good morning");
            console.log("Monty");
        """, output = """
            Good morning
            Monty
        """)
        choice("Which line is **correct**?", "console.log('Hi');", "console.log(Hi');", "console.log(\"Hi');", "Console.log('Hi');", mono = true,
            explain = "The start and the end of a string need the same kind of quotes – and `console` is written in lowercase.")
        typeOutput("""console.log("3 + 4");""", "3 + 4", explain = "Everything in quotes is text – JavaScript doesn't calculate anything here.")
        write("Write a program that prints **I love JavaScript!**",
            solution = """console.log("I love JavaScript!");""",
            output = "I love JavaScript!",
            hint = "Use `console.log()` and put the text in quotes.")
    }

    lesson("Calculating with JavaScript", "JavaScript as a calculator", LessonIcon.CALC) {
        text("JavaScript can do math. You write numbers **without** quotes.")
        code("""
            console.log(3 + 4);
            console.log(10 - 2.5);
            console.log(6 * 7);
            console.log(9 / 2);
        """, output = """
            7
            7.5
            42
            4.5
        """)
        text("""
            The most important arithmetic symbols (operators):
            - `+` plus and `-` minus
            - `*` times and `/` divided by
            - `%` modulo – the remainder of a division
            - `**` to the power of
        """)
        code("""
            console.log(7 % 2);
            console.log(2 ** 10);
            console.log(Math.floor(7 / 2));
        """, output = """
            1
            1024
            3
        """)
        tip("`Math.floor()` rounds **down**. That's how you get the whole-number result of a division: `Math.floor(7 / 2)` is `3`.")
        text("Multiplication and division come **before** addition and subtraction – just like in math. Brackets change the order.")
        code("""
            console.log(2 + 3 * 4);
            console.log((2 + 3) * 4);
        """, output = """
            14
            20
        """)
        tip("Decimal numbers are written with a **dot**: `2.5`. JavaScript has only one number type – `10 / 2` simply gives `5`.")

        output("console.log(5 + 3);", "8", "53", "5 + 3", "\"8\"")
        output("console.log(10 / 4);", "2.5", "2", "2,5", "3",
            explain = "`/` divides normally – with decimal places if needed.")
        choice("Which operator calculates the **remainder** of a division?", "%", "/", "//", "**", mono = true,
            explain = "Careful: in JavaScript, `//` isn't an arithmetic operator but the start of a comment!")
        typeOutput("console.log(2 ** 3);", "8", explain = "`**` means “to the power of”: 2 · 2 · 2 = 8.")
        output("console.log(2 + 3 * 4);", "14", "20", "24", explain = "Multiplication comes first: 3 * 4 = 12, then + 2.")
        match(
            "+" to "addition",
            "*" to "multiplication",
            "%" to "remainder (modulo)",
            "**" to "power",
            "Math.floor()" to "rounding down",
        )
        truth("`console.log(\"2 + 2\");` prints `4`.", false, explain = "Because of the quotes, it's text – it prints `2 + 2`.")
        fill("Calculate how many seconds an hour has (60 · 60).", "console.log(60 ?? 60);", answers = listOf("*"), distractors = listOf("+", "x", "**"), output = "3600")
        write("20 bottles have to go into crates that hold 6 bottles each. Print on two lines how many **full crates** there are and how many bottles are **left over**.",
            solution = """
                console.log(Math.floor(20 / 6));
                console.log(20 % 6);
            """,
            output = """
                3
                2
            """,
            mustContain = listOf("Math.floor", "%"),
            hint = "`Math.floor(20 / 6)` gives you the full crates, `20 % 6` the rest.")
    }

    lesson("Comments & Errors", "Notes in the code and your first error messages", LessonIcon.BUG) {
        text("Everything after `//` is a **comment**. JavaScript ignores it – comments are only for people. Longer comments go between `/*` and `*/`.")
        code("""
            // This is a comment
            console.log("Hello"); // a comment at the end of a line
            /* A comment
               over several lines */
        """, output = "Hello")
        text("Errors are part of programming – even pros make them all the time! With an **error message**, JavaScript tells you what went wrong. Tap ▶ to see the error:")
        code("""console.log("Hello";""", fails = true)
        text("A **SyntaxError** means the code isn't written correctly – for example, a bracket or a quote is missing. A **ReferenceError** means JavaScript doesn't know a word – often it's a typo.")
        code("""consle.log("Hello");""", fails = true)
        tip("The error message shows the line number at the top (`main.js:1`), below it your line of code with a `^` where the error is, and finally the kind of error with a description.")
        warn("JavaScript is case-sensitive: `console`, `Console` and `CONSOLE` are three different words.")

        choice("What does a single-line comment in JavaScript start with?", "//", "#", "--", "**", mono = true)
        output("""
            // console.log("A");
            console.log("B");
        """, "B", "A\nB", "A", "Nothing", explain = "The first line is commented out, so it isn't run.")
        truth("JavaScript ignores comments when it runs the code.", true)
        choice("Which error do you get with `console.log(\"Hello\";` – that is, with a missing bracket?", "SyntaxError", "ReferenceError", "TypeError", "RangeError", mono = true)
        choice("What does **ReferenceError: consle is not defined** mean?",
            "JavaScript doesn't know the word `consle` – probably a typo.",
            "A bracket is missing at the end.",
            "You can't divide by 0.",
            "The text is too long.")
        choice("Which line causes an error?", "Console.log(\"Hi\");", "console.log(\"Hi\");", "console.log('Hi');", "console.log(\"Hi\"); // greeting", mono = true,
            explain = "JavaScript doesn't know `Console` with a capital C → ReferenceError.")
        fill("Turn the first line into a comment.", """
            ?? Greeting
            console.log("Hello");
        """, answers = listOf("//"), distractors = listOf("#", "--"), output = "Hello")
        write("This program has two errors. Fix it so that it prints **I am Monty**.",
            starter = """Console.log("I am Monty";""",
            solution = """console.log("I am Monty");""",
            output = "I am Monty",
            hint = "Check the lowercase spelling of `console` and the closing bracket before the semicolon.")
    }

    lesson("console.log in Detail", "Several values, joining text, special characters", LessonIcon.STAR) {
        text("`console.log()` can print several values at once. Separate them with **commas** – JavaScript automatically puts a space between them.")
        code("""console.log("I am", 12, "years old");""", output = "I am 12 years old")
        text("With `+` you can also **join** pieces of text. This doesn't add a space automatically – you have to put it in yourself.")
        code("""
            console.log("Hello" + "World");
            console.log("Hello " + "World");
        """, output = """
            HelloWorld
            Hello World
        """)
        text("""
            You write special characters in strings with a **backslash** `\`:
            - `\n` a new line
            - `\t` a tab (indentation)
            - `\"` a quote inside the string
            - `\\` a backslash
        """)
        code("""
            console.log("Line 1\nLine 2");
            console.log("Name:\tMonty");
            console.log("She said: \"Hello!\"");
            console.log('It\'s me');
        """, output = """
            Line 1
            Line 2
            Name:	Monty
            She said: "Hello!"
            It's me
        """)
        tip("An empty `console.log()` simply prints an empty line – handy for making your output easier to read.")

        output("""console.log("A", "B");""", "A B", "AB", "A, B", "\"A\" \"B\"")
        output("""console.log("A" + "B");""", "AB", "A B", "A + B", "A, B",
            explain = "`+` joins text directly – without a space.")
        output("console.log(1, 2, 3);", "1 2 3", "6", "123", "1, 2, 3")
        choice("What does `\\n` stand for in a string?", "A new line", "A space", "The letter n", "The end of the program")
        typeOutput("""console.log("Java" + "Script");""", "JavaScript")
        fill("Print **A** and **B** on two lines – with only one `console.log()`.", """console.log("A??B");""",
            answers = listOf("\\n"), distractors = listOf("\\t", "/n", "n"), output = "A\nB")
        truth("`console.log()` with nothing inside prints an empty line.", true)
        arrange("Put the lines in the right order so that there's an empty line between **Hello** and **World**.", """
            console.log("Hello");
            console.log();
            console.log("World");
        """, output = """
            Hello

            World
        """)
        write("Use **one** `console.log()` to print the sentence `JavaScript is great!` – pass the three words separately, separated by commas.",
            solution = """console.log("JavaScript", "is", "great!");""",
            output = "JavaScript is great!",
            mustContain = listOf(","),
            hint = "In `console.log()`, you separate several values with commas: `console.log(\"a\", \"b\");`.")
    }

    checkpoint()
}
