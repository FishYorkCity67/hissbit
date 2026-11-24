package app.pyquest.content.en

import app.pyquest.content.CourseBuilder
import app.pyquest.data.LessonIcon

fun CourseBuilder.enUnit04() = unit("Decisions", "Comparisons, if, elif, else and logical operators") {
    lesson("Comparisons", "True or false?", LessonIcon.BRAIN) {
        text("A **comparison** always gives a truth value: `True` or `False`.")
        code("""
            print(5 > 3)
            print(5 < 3)
            print(5 == 5)
            print(5 != 5)
        """, output = """
            True
            False
            True
            False
        """)
        text("""
            The comparison operators:
            - `==` equal
            - `!=` not equal
            - `<` less than, `>` greater than
            - `<=` less than or equal, `>=` greater than or equal
        """)
        warn("`=` **assigns**, `==` **compares**. Mixing them up is one of the most common beginner mistakes!")
        text("You can compare strings too – capitalization matters. `<` compares alphabetically:")
        code("""
            print("Python" == "Python")
            print("python" == "Python")
            print("apple" < "pear")
        """, output = """
            True
            False
            True
        """)
        tip("Python allows chained comparisons like in math: `1 < x < 10`.")

        output("print(7 > 2)", "True", "False", "7 > 2", "5")
        output("print(4 == 4.0)", "True", "False", "Error", "4", explain = "Python compares the numeric value – 4 and 4.0 are equally big.")
        choice("Which operator checks for **inequality**?", "!=", "<>", "=!", "==", mono = true)
        typeOutput("""
            x = 10
            print(x >= 10)
        """, "True")
        truth("`=` and `==` mean the same in Python.", false, explain = "`=` assigns a value, `==` compares two values.")
        output("print(\"a\" == \"A\")", "False", "True", "Error", "a", explain = "Uppercase and lowercase letters are different characters.")
        match("==" to "equal", "!=" to "not equal", "<=" to "less than or equal", ">" to "greater than")
        output("""
            age = 15
            print(13 <= age < 18)
        """, "True", "False", "Error", "15")
    }

    lesson("if & else", "Running code only under a condition", LessonIcon.BRANCH) {
        text("With `if` you run code **only** when a condition is true. Pay attention to the **colon** and the **indentation**:")
        code("""
            temperature = 30
            if temperature > 25:
                print("Off to the pool!")
            print("Done")
        """, output = """
            Off to the pool!
            Done
        """)
        text("Everything that's indented (4 spaces) belongs to the `if` block. The last line isn't indented – it always runs.")
        text("With `else` you decide what happens when the condition is **not** true:")
        code("""
            age = 12
            if age >= 18:
                print("You can vote.")
            else:
                print("Wait", 18 - age, "more years.")
        """, output = "Wait 6 more years.")
        warn("If you forget the colon or the indentation, Python reports a **SyntaxError** or an **IndentationError**.")
        tip("The editor indents automatically after a colon. You can change the indentation with the arrow keys in the symbol bar.")

        output("""
            x = 5
            if x > 3:
                print("big")
            print("End")
        """, "big\nEnd", "End", "big", "Error")
        output("""
            x = 1
            if x > 3:
                print("big")
            print("End")
        """, "End", "big\nEnd", "big", "Nothing")
        choice("What's missing at the end of `if points > 100`?", "A colon `:`", "A semicolon `;`", "A bracket `)`", "Nothing")
        fill("Print `Even` or `Odd`.", """
            number = 7
            if number % 2 == 0??
                print("Even")
            ??:
                print("Odd")
        """, answers = listOf(":", "else"), distractors = listOf(";", "elif", "then"), output = "Odd")
        arrange("If it's raining, print `Take an umbrella`, otherwise `Sunglasses`.", """
            rain = True
            if rain:
                print("Take an umbrella")
            else:
                print("Sunglasses")
        """, output = "Take an umbrella")
        truth("Code indented under an `if` only runs when the condition is true.", true)
        typeOutput("""
            points = 80
            if points >= 50:
                print("passed")
            else:
                print("failed")
        """, "passed")
        write("Check the variable `temperature`: if it's **below 0**, print `It's freezing!`, otherwise `No frost.`",
            starter = "temperature = -3",
            solution = """
                temperature = -3
                if temperature < 0:
                    print("It's freezing!")
                else:
                    print("No frost.")
            """,
            output = "It's freezing!",
            mustContain = listOf("if", "else"),
            hint = "`if temperature < 0:` – and indent the block after it.")
    }

    lesson("elif", "More than two options", LessonIcon.BRANCH) {
        text("For more than two cases there's `elif` (short for “else if”). Python checks the conditions **from top to bottom** and only runs the **first** matching block.")
        code("""
            grade = 2
            if grade == 1:
                print("Excellent")
            elif grade == 2:
                print("Good")
            elif grade == 3:
                print("Satisfactory")
            else:
                print("Keep practicing!")
        """, output = "Good")
        tip("The order matters! Check the strictest case first.")
        code("""
            points = 95
            if points >= 90:
                print("Gold")
            elif points >= 70:
                print("Silver")
            elif points >= 50:
                print("Bronze")
            else:
                print("No medal")
        """, output = "Gold")
        warn("With 95 points all three conditions are true – but only `Gold` is printed, because the first match wins.")

        output("""
            x = 0
            if x > 0:
                print("positive")
            elif x < 0:
                print("negative")
            else:
                print("zero")
        """, "zero", "positive", "negative", "Nothing")
        output("""
            n = 15
            if n > 10:
                print("A")
            elif n > 5:
                print("B")
        """, "A", "B", "A\nB", "Nothing", explain = "Only the **first** matching block runs.")
        truth("An `if` can have any number of `elif` branches.", true)
        choice("How many `else` branches can an `if` have at most?", "One", "None", "As many as you like", "Two")
        typeOutput("""
            age = 65
            if age < 13:
                print("Child")
            elif age < 18:
                print("Teenager")
            elif age < 65:
                print("Adult")
            else:
                print("Senior")
        """, "Senior")
        fill("Add the missing branch.", """
            light = "yellow"
            if light == "red":
                print("Stop")
            ?? light == "yellow":
                print("Caution")
            else:
                print("Go")
        """, answers = listOf("elif"), distractors = listOf("else", "else if", "elseif"), output = "Caution")
        arrange("Print `cold` (below 10 degrees), `pleasant` (below 25 degrees) or otherwise `hot`.", """
            t = 18
            if t < 10:
                print("cold")
            elif t < 25:
                print("pleasant")
            else:
                print("hot")
        """, output = "pleasant")
        write("Print whether `number` is `positive`, `negative` or `zero`.",
            starter = "number = -7",
            solution = """
                number = -7
                if number > 0:
                    print("positive")
                elif number < 0:
                    print("negative")
                else:
                    print("zero")
            """,
            output = "negative",
            mustContain = listOf("if", "elif", "else"),
            hint = "Three cases: `if number > 0:`, `elif number < 0:` and `else:`.")
    }

    lesson("and, or & not", "Combining conditions", LessonIcon.PUZZLE) {
        text("""
            You combine several conditions with `and`, `or` and `not`:
            - `and` – **both** must be true
            - `or` – **at least one** must be true
            - `not` – flips the value
        """)
        code("""
            age = 16
            has_ticket = True
            print(age >= 16 and has_ticket)
            print(age >= 18 or has_ticket)
            print(not has_ticket)
        """, output = """
            True
            True
            False
        """)
        code("""
            day = "Saturday"
            if day == "Saturday" or day == "Sunday":
                print("Weekend! 🎉")
        """, output = "Weekend! 🎉")
        warn("`if day == \"Saturday\" or \"Sunday\":` is a typical mistake – it's **always** true! You have to repeat the comparison on both sides.")
        tip("It's often shorter with `in`: `if day in (\"Saturday\", \"Sunday\"):`")

        output("print(True and False)", "False", "True", "None", "Error")
        output("print(True or False)", "True", "False", "None", "Error")
        output("print(not 5 > 3)", "False", "True", "Error", "5")
        typeOutput("""
            x = 7
            print(x > 5 and x < 10)
        """, "True")
        choice("When is `a or b` **false**?", "Only when a and b are both false", "When a is false", "When b is false", "Never")
        truth("`not (3 == 3)` gives `False`.", true)
        fill("Entry is free for children under 6 **or** people aged 65 and over.", """
            age = 70
            if age < 6 ?? age >= 65:
                print("Free entry")
        """, answers = listOf("or"), distractors = listOf("and", "not", "||"), output = "Free entry")
        write("A password is valid if it's **at least 8 characters** long **and** contains the digit `1`. Print `valid` or `invalid`.",
            starter = "password = \"python123\"",
            solution = """
                password = "python123"
                if len(password) >= 8 and "1" in password:
                    print("valid")
                else:
                    print("invalid")
            """,
            output = "valid",
            mustContain = listOf("and"),
            hint = "Combine `len(password) >= 8` and `\"1\" in password` with `and`.")
    }

    lesson("Nesting & Shortcuts", "if inside if and one-liners", LessonIcon.BRANCH) {
        text("You can **nest** conditions – an `if` inside an `if`. Each level is indented further:")
        code("""
            has_money = True
            price = 4
            if has_money:
                if price < 5:
                    print("Buy ice cream 🍦")
                else:
                    print("Too expensive")
            else:
                print("No money with me")
        """, output = "Buy ice cream 🍦")
        text("For simple cases there's a one-line **shortcut**: `value_if_true if condition else value_if_false`")
        code("""
            number = 7
            text = "even" if number % 2 == 0 else "odd"
            print(text)
        """, output = "odd")
        text("In conditions, empty values like `0`, `\"\"`, `[]` and `None` count as **false**, everything else as **true**. That way you can check, for example, whether something was entered:")
        code("""
            name = ""
            if name:
                print("Hello", name)
            else:
                print("No name entered")
        """, output = "No name entered")
        tip("Too much nesting makes code hard to read. Combining conditions with `and` often helps.")

        output("""
            x = 10
            print("big" if x > 5 else "small")
        """, "big", "small", "True", "Error")
        output("""
            a = 3
            b = 8
            if a > 1:
                if b > 10:
                    print("X")
                else:
                    print("Y")
            else:
                print("Z")
        """, "Y", "X", "Z", "Nothing")
        truth("An empty string `\"\"` counts as false in a condition.", true)
        typeOutput("""
            items = []
            print("empty" if not items else "full")
        """, "empty")
        choice("Which line is a valid shortcut?",
            "x = \"yes\" if ok else \"no\"", "x = if ok \"yes\" else \"no\"", "x = ok ? \"yes\" : \"no\"", "x = \"yes\" if ok", mono = true,
            explain = "The shortcut always needs an `else` in Python – and `? :` doesn't exist in Python.")
        fill("Assign the text `adult` or `minor` to `status`.", """
            age = 17
            status = "adult" ?? age >= 18 ?? "minor"
            print(status)
        """, answers = listOf("if", "else"), distractors = listOf("?", ":", "then", "elif"), output = "minor",
            explain = "Python's shortcut is `value_a if condition else value_b` – the `? :` from other languages doesn't exist.")
        output("""
            points = 0
            if points:
                print("Points!")
            else:
                print("No points")
        """, "No points", "Points!", "0", "Error", explain = "`0` counts as false in a condition.")
        write("Cinema check: print `Enjoy the movie!` if `age` is at least 12 **and** `has_ticket` is true. Without a ticket, print `Buy a ticket first`. If you're too young, print `Too young`.",
            starter = """
                age = 13
                has_ticket = False
            """,
            solution = """
                age = 13
                has_ticket = False
                if age >= 12:
                    if has_ticket:
                        print("Enjoy the movie!")
                    else:
                        print("Buy a ticket first")
                else:
                    print("Too young")
            """,
            output = "Buy a ticket first",
            mustContain = listOf("if"),
            hint = "Check the age first and then, in the indented block, the ticket.")
    }

    checkpoint()
}
