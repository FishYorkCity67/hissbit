package app.pyquest.content.en

import app.pyquest.content.CourseBuilder
import app.pyquest.data.LessonIcon

fun CourseBuilder.enUnit01() = unit("First Steps", "Your first program, calculating and error messages") {
    lesson("Hello, World!", "Your very first program", LessonIcon.CHAT) {
        text("""
            Welcome to Python! 🐍 Python is one of the most popular programming languages in the world – it's inside apps, websites, artificial intelligence and even space travel. And the best part: Python reads almost like English.
        """)
        text("""
            The function `print()` displays something on the screen. Whatever should be displayed goes inside the round brackets.
        """)
        code("""print("Hello, World!")""", output = "Hello, World!")
        text("""
            Text must be in **quotes**. You can use double `"..."` or single `'...'` quotes – both work the same. A piece of text like this is called a **string**.
        """)
        code("""
            print("I'm learning Python!")
            print('Monty is a snake.')
        """, output = """
            I'm learning Python!
            Monty is a snake.
        """)
        tip("Each `print()` produces its own line. Python runs your program from top to bottom – line by line.")
        warn("Watch the spelling: `print` is lowercase. Python doesn't know `Print` or `PRINT`!")

        choice("Which function displays something on the screen?", "print()", "output()", "show()", "write()", mono = true)
        output("""print("Hello, World!")""", "Hello, World!", "\"Hello, World!\"", "print(Hello, World!)", "Nothing",
            explain = "The quotes only mark the start and end of the string – they aren't printed.")
        truth("In Python, text has to be in quotes.", true,
            explain = "Without quotes, Python thinks the text is a name (for example a variable).")
        fill("Print the text **Hello**.", """??("Hello")""", answers = listOf("print"), distractors = listOf("Print", "say", "echo"), output = "Hello")
        arrange("Print **Good morning** first and then **Monty**.", """
            print("Good morning")
            print("Monty")
        """, output = """
            Good morning
            Monty
        """)
        choice("Which line is **correct**?", "print('Hi')", "print(Hi')", "print(\"Hi')", "Print('Hi')", mono = true,
            explain = "The start and end of a string need the same kind of quote – and `print` is lowercase.")
        typeOutput("""print("3 + 4")""", "3 + 4", explain = "Everything in quotes is text – Python doesn't calculate here.")
        write("Write a program that prints **I'm learning Python!**",
            solution = """print("I'm learning Python!")""",
            output = "I'm learning Python!",
            hint = "Use `print()` and put the text in quotes.")
    }

    lesson("Calculating with Python", "Python as a calculator", LessonIcon.CALC) {
        text("Python can do math – and it's pretty good at it. You write numbers **without** quotes.")
        code("""
            print(3 + 4)
            print(10 - 2.5)
            print(6 * 7)
            print(9 / 2)
        """, output = """
            7
            7.5
            42
            4.5
        """)
        text("""
            The most important operators:
            - `+` plus and `-` minus
            - `*` times
            - `/` divided by – the result is always a decimal number
            - `//` integer division – cuts off everything after the decimal point
            - `%` modulo – the remainder of a division
            - `**` to the power of
        """)
        code("""
            print(7 // 2)
            print(7 % 2)
            print(2 ** 10)
        """, output = """
            3
            1
            1024
        """)
        tip("Decimal numbers are written with a **dot**: `2.5`. (Some countries write 2,5 – Python doesn't understand that.)")
        text("Multiplication comes **before** addition – just like in math. Parentheses change the order.")
        code("""
            print(2 + 3 * 4)
            print((2 + 3) * 4)
        """, output = """
            14
            20
        """)

        output("print(5 + 3)", "8", "53", "5 + 3", "\"8\"")
        output("print(10 / 4)", "2.5", "2", "2,5", "3",
            explain = "`/` always gives a decimal number. Python writes it with a dot.")
        choice("Which operator calculates the **remainder** of a division?", "%", "/", "//", "**", mono = true)
        typeOutput("print(2 ** 3)", "8", explain = "`**` means to the power of: 2 · 2 · 2 = 8.")
        output("print(2 + 3 * 4)", "14", "20", "24", explain = "Multiplication first: 3 * 4 = 12, then + 2.")
        match(
            "+" to "Addition",
            "*" to "Multiplication",
            "//" to "Integer division",
            "%" to "Remainder (modulo)",
            "**" to "Power",
        )
        truth("`print(\"2 + 2\")` prints `4`.", false, explain = "Because of the quotes it's text – it prints `2 + 2`.")
        fill("Calculate how many seconds an hour has (60 · 60).", "print(60 ?? 60)", answers = listOf("*"), distractors = listOf("+", "x", "**"), output = "3600")
        write("20 bottles are packed into crates of 6 bottles each. Print in two lines how many **full crates** there are and how many bottles are **left over**.",
            solution = """
                print(20 // 6)
                print(20 % 6)
            """,
            output = """
                3
                2
            """,
            mustContain = listOf("//", "%"),
            hint = "`//` gives the number of full crates, `%` the remainder.")
    }

    lesson("Comments & Errors", "Notes in your code and your first error messages", LessonIcon.BUG) {
        text("Everything after a `#` is a **comment**. Python ignores it – comments are only for the humans reading the code.")
        code("""
            # This is a comment
            print("Hello")  # comment at the end of a line
        """, output = "Hello")
        text("Errors are part of programming – even pros make them all the time! Python tells you what went wrong with an **error message**. Tap ▶ to see the error:")
        code("""print("Hello"""", fails = true)
        text("A **SyntaxError** means the way something is written is wrong – for example, a bracket or a quote is missing. A **NameError** means Python doesn't know a word – often it's a typo.")
        code("""prnt("Hello")""", fails = true)
        tip("Read error messages from the **bottom up**: the last line shows the kind of error and a description. Python often even suggests the fix (“Did you mean: 'print'?”).")
        warn("Python is case-sensitive: `print`, `Print` and `PRINT` are three different words.")

        choice("What does a comment start with in Python?", "#", "//", "--", "/*", mono = true)
        output("""
            # print("A")
            print("B")
        """, "B", "A\nB", "A", "Nothing", explain = "The first line is commented out, so it doesn't run.")
        truth("Python ignores comments when it runs your program.", true)
        choice("Which error do you get with `print(\"Hello\"` – a missing bracket?", "SyntaxError", "NameError", "TypeError", "ZeroDivisionError", mono = true)
        choice("What does **NameError: name 'prnt' is not defined** mean?",
            "Python doesn't know the word `prnt` – probably a typo.",
            "A bracket is missing at the end.",
            "You can't divide by 0.",
            "The text is too long.")
        choice("Which line causes an error?", "Print(\"Hi\")", "print(\"Hi\")", "print('Hi')", "print(\"Hi\")  # greeting", mono = true,
            explain = "Python doesn't know `Print` with a capital P → NameError.")
        fill("Turn the first line into a comment.", """
            ?? Greeting
            print("Hello")
        """, answers = listOf("#"), distractors = listOf("//", "--"), output = "Hello")
        write("This program has two errors. Fix it so it prints **I am Monty**.",
            starter = """Print("I am Monty"""",
            solution = """print("I am Monty")""",
            output = "I am Monty",
            hint = "Watch the capitalization of `print` and the closing bracket.")
    }

    lesson("print() in detail", "Several values, separators and special characters", LessonIcon.STAR) {
        text("`print()` can print several values at once. Separate them with **commas** – Python automatically puts a space between them.")
        code("""print("I am", 12, "years old")""", output = "I am 12 years old")
        text("With `sep` you choose what goes **between** the values. With `end` you choose what comes **at the end** – normally a line break.")
        code("""
            print("A", "B", "C", sep="-")
            print("Hello", end=" ")
            print("World")
        """, output = """
            A-B-C
            Hello World
        """)
        text("""
            Special characters are written in strings with a **backslash** `\`:
            - `\n` new line
            - `\t` tab (indentation)
            - `\"` a quote inside the string
            - `\\` a backslash
        """)
        code("""
            print("Line 1\nLine 2")
            print("Name:\tMonty")
            print("He said: \"Hello!\"")
        """, output = """
            Line 1
            Line 2
            Name:	Monty
            He said: "Hello!"
        """)
        tip("An empty `print()` simply prints an empty line – handy for making output easier to read.")

        output("""print("A", "B")""", "A B", "AB", "A, B", "\"A\" \"B\"")
        output("""print(1, 2, 3, sep="+")""", "1+2+3", "6", "1 2 3", "1+2+3+")
        choice("What does `\\n` stand for in a string?", "A new line", "A space", "The letter n", "The end of the program")
        typeOutput("""
            print("Py", end="")
            print("thon")
        """, "Python", explain = "With `end=\"\"` the first `print()` doesn't add a line break.")
        fill("Print **10:30** by setting `sep` correctly.", "print(10, 30, sep=??)",
            answers = listOf("\":\""), distractors = listOf("\" \"", "\",\"", "\"-\""), output = "10:30")
        truth("`print()` with nothing inside prints an empty line.", true)
        arrange("Put the lines in the right order so that there's an empty line between **Hello** and **World**.", """
            print("Hello")
            print()
            print("World")
        """, output = """
            Hello

            World
        """)
        write("Print the sentence `Python is great!` with **one** `print()` – pass the three words separately, separated by commas.",
            solution = """print("Python", "is", "great!")""",
            output = "Python is great!",
            mustContain = listOf(","),
            hint = "Separate several values in `print()` with commas: `print(\"a\", \"b\")`.")
    }

    checkpoint()
}
