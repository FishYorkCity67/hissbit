package app.pyquest.content.en

import app.pyquest.content.CourseBuilder
import app.pyquest.data.LessonIcon

fun CourseBuilder.enUnit03() = unit("Strings", "Working with text: joining, slicing, formatting") {
    lesson("Joining Text", "+, * and len()", LessonIcon.CHAT) {
        text("Strings can be **joined** with `+` and **repeated** with `*`:")
        code("""
            first_name = "Ada"
            last_name = "Lovelace"
            print(first_name + " " + last_name)
            print("Ha" * 3)
        """, output = """
            Ada Lovelace
            HaHaHa
        """)
        text("`len()` tells you how many characters a string has – spaces count too:")
        code("""
            print(len("Python"))
            print(len("Hello World"))
            print(len(""))
        """, output = """
            6
            11
            0
        """)
        warn("When joining with `+`, Python does **not** add a space – you have to add it yourself.")
        tip("Strings are **immutable**: every operation creates a new string, the original stays the same.")

        output("print(\"Py\" + \"thon\")", "Python", "Py thon", "Py+thon", "Error")
        output("print(\"ab\" * 3)", "ababab", "ab3", "ab ab ab", "Error")
        typeOutput("print(len(\"Monty\"))", "5")
        output("print(len(\"a b\"))", "3", "2", "1", "4", explain = "The space is a character too.")
        fill("Join first and last name with a space in between.", """
            first_name = "Grace"
            last_name = "Hopper"
            print(first_name ?? " " ?? last_name)
        """, answers = listOf("+", "+"), distractors = listOf("*", "-"), output = "Grace Hopper")
        truth("`\"Hello\" + 5` gives `Hello5`.", false, explain = "You can't join text and a number with `+`. It would work with `\"Hello\" + str(5)`.")
        arrange("Print a line of 10 asterisks and the word `Title` below it.", """
            print("*" * 10)
            print("Title")
        """, output = """
            **********
            Title
        """)
        write("Create the variable `word = \"Monty\"` and print how long the word is – in the format `Monty has 5 characters`.",
            solution = """
                word = "Monty"
                print(word, "has", len(word), "characters")
            """,
            output = "Monty has 5 characters",
            mustContain = listOf("len("),
            hint = "`print(word, \"has\", len(word), \"characters\")` – the commas add spaces automatically.")
    }

    lesson("Indexes & Slicing", "Single characters and substrings", LessonIcon.PUZZLE) {
        text("Every character in a string has a position, its **index**. Careful: counting starts at **0**!")
        code("""
            word = "Python"
            print(word[0])
            print(word[1])
            print(word[5])
        """, output = """
            P
            y
            n
        """)
        text("With **negative** indexes you count from the end: `-1` is the last character.")
        code("""
            word = "Python"
            print(word[-1])
            print(word[-2])
        """, output = """
            n
            o
        """)
        text("With **slicing** you cut out parts: `word[start:stop]`. The character at position `stop` is **not** included. If you leave out `start` or `stop`, it goes to the beginning or the end.")
        code("""
            word = "Python"
            print(word[0:2])
            print(word[2:])
            print(word[:4])
            print(word[::-1])
        """, output = """
            Py
            thon
            Pyth
            nohtyP
        """)
        tip("`[::-1]` reverses a string – the step `-1` goes backwards.")
        warn("An index outside the string (e.g. `word[10]`) causes an **IndexError**. That doesn't happen with slicing – you simply get less back.")

        output("""
            s = "Monty"
            print(s[0])
        """, "M", "o", "Monty", "Error")
        output("""
            s = "Monty"
            print(s[-1])
        """, "y", "M", "t", "Error")
        typeOutput("""
            s = "Python"
            print(s[1:4])
        """, "yth", explain = "Index 1 to 3 – position 4 isn't included.")
        output("""
            s = "abcdef"
            print(s[:3])
        """, "abc", "abcd", "def", "cba")
        choice("Which index belongs to the **first** character of a string?", "0", "1", "-1", "first", mono = true)
        output("print(\"stressed\"[::-1])", "desserts", "stressed", "d", "Error", explain = "`[::-1]` reverses the string.")
        fill("Cut the word **World** out of the string.", """
            s = "Hello World"
            print(s[??:])
        """, answers = listOf("6"), distractors = listOf("5", "7", "-6"), output = "World")
        write("Print the **first** and the **last** letter of `word = \"snake\"` – both with one `print()`.",
            starter = "word = \"snake\"",
            solution = """
                word = "snake"
                print(word[0], word[-1])
            """,
            output = "s e",
            hint = "First letter: `word[0]`, last one: `word[-1]`.")
    }

    lesson("String Methods", "upper, lower, strip, replace, split", LessonIcon.MAGIC) {
        text("Strings come with lots of useful **methods**. You call a method with a dot: `text.method()`.")
        code("""
            text = "Python is fun"
            print(text.upper())
            print(text.lower())
            print(text.title())
        """, output = """
            PYTHON IS FUN
            python is fun
            Python Is Fun
        """)
        text("`strip()` removes spaces at the start and end – handy for input. `replace()` replaces parts:")
        code("""
            entry = "   hello   "
            print("[" + entry.strip() + "]")
            print("I like coffee".replace("coffee", "tea"))
        """, output = """
            [hello]
            I like tea
        """)
        text("`split()` breaks a string into a **list** of parts, `join()` puts parts back together:")
        code("""
            sentence = "Python is great"
            words = sentence.split()
            print(words)
            print("-".join(words))
        """, output = """
            ['Python', 'is', 'great']
            Python-is-great
        """)
        tip("Methods don't change the original string, they return a **new** one. Store the result if you need it: `text = text.upper()`.")

        output("print(\"hello\".upper())", "HELLO", "hello", "Hello", "Error")
        output("print(\"[\" + \"  Py  \".strip() + \"]\")", "[Py]", "[  Py  ]", "[Py  ]", "[  Py]",
            explain = "`strip()` removes spaces at **both** ends. The brackets show where the text starts and ends.")
        typeOutput("print(\"banana\".replace(\"a\", \"o\"))", "bonono")
        output("print(\"a,b,c\".split(\",\"))", "['a', 'b', 'c']", "a b c", "['a,b,c']", "abc")
        match(
            ".upper()" to "all uppercase",
            ".lower()" to "all lowercase",
            ".strip()" to "remove outer spaces",
            ".split()" to "break into parts",
            ".replace()" to "replace parts",
        )
        truth("`text.upper()` changes the string in `text` directly.", false, explain = "Strings are immutable. `upper()` returns a new string.")
        fill("Join the words into a sentence with spaces.", """
            words = ["I", "am", "learning", "Python"]
            print(" ".??(words))
        """, answers = listOf("join"), distractors = listOf("split", "add", "strip"), output = "I am learning Python")
        write("The variable `entry` has spaces around it. Remove them and print the text in **uppercase**.",
            starter = "entry = \"  monty python  \"",
            solution = """
                entry = "  monty python  "
                print(entry.strip().upper())
            """,
            output = "MONTY PYTHON",
            hint = "You can chain methods: `entry.strip().upper()`.")
    }

    lesson("f-Strings", "Putting values into text elegantly", LessonIcon.STAR) {
        text("With **f-strings** you put values directly into a text. Write an `f` before the quotes and put variables in curly braces `{}`:")
        code("""
            name = "Monty"
            age = 3
            print(f"{name} is {age} years old.")
        """, output = "Monty is 3 years old.")
        text("You can even calculate inside the braces:")
        code("""
            a = 7
            b = 6
            print(f"{a} times {b} is {a * b}")
        """, output = "7 times 6 is 42")
        text("After a colon you can **format**: `:.2f` shows two decimal places, `:5` reserves a width of 5 characters, `:>5` aligns to the right.")
        code("""
            price = 3.14159
            print(f"Price: {price:.2f} €")
            print(f"[{42:5}]")
            print(f"[{'ab':>5}]")
        """, output = """
            Price: 3.14 €
            [   42]
            [   ab]
        """)
        tip("A debugging trick: `f\"{x=}\"` shows the name **and** the value, e.g. `x=42`.")
        code("""
            x = 42
            print(f"{x=}")
        """, output = "x=42")

        output("""
            name = "Ada"
            print(f"Hello {name}!")
        """, "Hello Ada!", "Hello {name}!", "Hello name!", "f\"Hello Ada!\"")
        output("""
            x = 3
            print(f"{x} + {x} = {x + x}")
        """, "3 + 3 = 6", "{x} + {x} = {x + x}", "3 + 3 = 33", "x + x = 6")
        typeOutput("""
            pi = 3.14159
            print(f"{pi:.1f}")
        """, "3.1")
        choice("What's missing so that `{name}` is replaced by the value? `print(\"Hello {name}\")`",
            "An `f` before the string", "A `\$` before name", "Double braces", "Nothing, it already works")
        truth("Inside the `{}` of an f-string you may also calculate, e.g. `{a + b}`.", true)
        fill("Print the price with exactly two decimal places.", """
            price = 4.5
            print(f"{price:??} €")
        """, answers = listOf(".2f"), distractors = listOf("2", ".2", "%2"), output = "4.50 €")
        arrange("Use an f-string to build the output `Monty has 120 points`.", """
            name = "Monty"
            points = 120
            print(f"{name} has {points} points")
        """, output = "Monty has 120 points")
        write("Print with an f-string: `It's 21.5 degrees in Vienna.` – the temperature should be rounded to one decimal place.",
            starter = """
                city = "Vienna"
                degrees = 21.456
            """,
            solution = """
                city = "Vienna"
                degrees = 21.456
                print(f"It's {degrees:.1f} degrees in {city}.")
            """,
            output = "It's 21.5 degrees in Vienna.",
            mustContain = listOf(":.1f"),
            hint = "You get one decimal place with `{degrees:.1f}`.")
    }

    lesson("Searching & Checking", "in, find, count and more", LessonIcon.BRAIN) {
        text("With `in` you check whether a substring occurs. The result is `True` or `False`:")
        code("""
            sentence = "I love Python"
            print("Python" in sentence)
            print("Java" in sentence)
        """, output = """
            True
            False
        """)
        text("`find()` tells you **where** something is (or `-1` if it doesn't occur). `count()` counts the occurrences:")
        code("""
            s = "banana"
            print(s.find("na"))
            print(s.find("x"))
            print(s.count("a"))
        """, output = """
            2
            -1
            3
        """)
        text("`startswith()` and `endswith()` check the beginning and the end. Methods like `isdigit()` check the content:")
        code("""
            file = "photo.png"
            print(file.endswith(".png"))
            print("2026".isdigit())
            print("20a6".isdigit())
        """, output = """
            True
            True
            False
        """)
        tip("`isdigit()` is perfect for checking whether an input really is a number before calling `int()`.")

        output("print(\"th\" in \"Python\")", "True", "False", "2", "Error")
        output("print(\"Hello\".find(\"l\"))", "2", "3", "1", "-1", explain = "`find()` returns the position of the **first** match – counted from 0.")
        typeOutput("print(\"Mississippi\".count(\"s\"))", "4")
        choice("What does `\"abc\".find(\"z\")` return?", "-1", "0", "None", "An error", mono = true)
        truth("`\"123\".isdigit()` gives `True`.", true)
        fill("Check whether the file name ends with `.py`.", """
            file = "game.py"
            print(file.??(".py"))
        """, answers = listOf("endswith"), distractors = listOf("startswith", "find", "count"), output = "True")
        output("""
            email = "monty@python.org"
            print("@" in email and email.endswith(".org"))
        """, "True", "False", "@", "Error")
        write("Count how often the letter `e` (lowercase) occurs in the text and print the number.",
            starter = "text = \"Elephants never forget\"",
            solution = """
                text = "Elephants never forget"
                print(text.count("e"))
            """,
            output = "4",
            hint = "`text.count(\"e\")` counts all lowercase e – the capital E at the start doesn't count.")
    }

    checkpoint()
}
