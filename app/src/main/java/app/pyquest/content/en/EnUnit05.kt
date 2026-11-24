package app.pyquest.content.en

import app.pyquest.content.CourseBuilder
import app.pyquest.data.LessonIcon

fun CourseBuilder.enUnit05() = unit("Loops", "Repeating code with while and for") {
    lesson("while Loops", "Repeat as long as something is true", LessonIcon.LOOP) {
        text("A **loop** repeats code. The `while` loop runs **as long as** its condition is true:")
        code("""
            counter = 1
            while counter <= 3:
                print("Round", counter)
                counter += 1
            print("Done!")
        """, output = """
            Round 1
            Round 2
            Round 3
            Done!
        """)
        text("What happens: Python checks the condition → runs the block → checks again → … until the condition is false.")
        warn("If the condition never changes, the loop runs **forever**! Don't forget to change the variable inside the block (e.g. `counter += 1`). In this app, a time limit stops endless loops.")
        code("""
            balance = 100
            years = 0
            while balance < 200:
                balance *= 1.1
                years += 1
            print(f"The money doubled after {years} years.")
        """, output = "The money doubled after 8 years.")

        output("""
            i = 0
            while i < 3:
                print(i)
                i += 1
        """, "0\n1\n2", "1\n2\n3", "0\n1\n2\n3", "3")
        typeOutput("""
            n = 10
            while n > 0:
                n -= 3
            print(n)
        """, "-2", explain = "10 → 7 → 4 → 1 → -2. Only at -2 is the condition `n > 0` false.")
        truth("A `while` loop keeps running until its condition becomes false.", true)
        choice("What happens with this code?", "An endless loop – `x` never changes.", "It prints `1` once.", "A SyntaxError.", "Nothing.",
            code = """
                x = 1
                while x > 0:
                    print(x)
            """)
        fill("Count down from 5 to 1.", """
            n = 5
            while n ?? 0:
                print(n)
                n ?? 1
        """, answers = listOf(">", "-="), distractors = listOf("<", "+=", "=="), output = """
            5
            4
            3
            2
            1
        """)
        arrange("Print the numbers 1, 2 and 3 with a while loop.", """
            i = 1
            while i <= 3:
                print(i)
                i += 1
        """, output = """
            1
            2
            3
        """)
        output("""
            total = 0
            number = 1
            while number <= 4:
                total += number
                number += 1
            print(total)
        """, "10", "4", "15", "6")
        write("Double `value` with a `while` loop until it's **greater than 100**. Then print `value`.",
            starter = "value = 1",
            solution = """
                value = 1
                while value <= 100:
                    value *= 2
                print(value)
            """,
            output = "128",
            mustContain = listOf("while"),
            hint = "`while value <= 100:` and in the indented block `value *= 2`.")
    }

    lesson("for & range", "A fixed number of repetitions", LessonIcon.LOOP) {
        text("When you know **how often** something should happen, the `for` loop with `range()` is ideal:")
        code("""
            for i in range(5):
                print(i)
        """, output = """
            0
            1
            2
            3
            4
        """)
        text("""
            `range()` comes in three variants:
            - `range(stop)` – from 0 to stop-1
            - `range(start, stop)` – from start to stop-1
            - `range(start, stop, step)` – with your own step size
        """)
        code("""
            print(list(range(3, 7)))
            print(list(range(0, 10, 2)))
            print(list(range(5, 0, -1)))
        """, output = """
            [3, 4, 5, 6]
            [0, 2, 4, 6, 8]
            [5, 4, 3, 2, 1]
        """)
        warn("The **stop value is not included**: `range(1, 5)` gives 1, 2, 3, 4 – but not 5.")
        tip("If you don't need the counting variable, it's often called `_`: `for _ in range(3): print(\"Hip hip hooray!\")`")

        output("""
            for i in range(3):
                print(i)
        """, "0\n1\n2", "1\n2\n3", "0\n1\n2\n3", "3")
        output("print(list(range(2, 6)))", "[2, 3, 4, 5]", "[2, 3, 4, 5, 6]", "[3, 4, 5, 6]", "[2, 6]")
        typeOutput("""
            for x in range(10, 0, -4):
                print(x)
        """, "10\n6\n2")
        choice("Which call gives the numbers **1 to 10**?", "range(1, 11)", "range(1, 10)", "range(10)", "range(0, 10)", mono = true)
        truth("`range(5)` contains the number 5.", false, explain = "`range(5)` gives 0, 1, 2, 3 and 4.")
        fill("Print all even numbers from 0 to 8.", """
            for n in range(0, 9, ??):
                print(n)
        """, answers = listOf("2"), distractors = listOf("1", "-2", "0"), output = """
            0
            2
            4
            6
            8
        """)
        output("""
            for i in range(1, 4):
                print("*" * i)
        """, "*\n**\n***", "***\n**\n*", "*\n*\n*", "Error")
        write("Use a `for` loop to print the **5 times table**: 5, 10, 15, …, 50 – each number on its own line.",
            solution = """
                for i in range(1, 11):
                    print(i * 5)
            """,
            output = """
                5
                10
                15
                20
                25
                30
                35
                40
                45
                50
            """,
            mustContain = listOf("for"),
            hint = "Either `range(1, 11)` and `print(i * 5)` – or directly `range(5, 51, 5)`.")
    }

    lesson("Looping & Summing Up", "Strings and lists element by element", LessonIcon.CALC) {
        text("With `for` you can also loop directly through a **string** or a **list** – element by element:")
        code("""
            for letter in "Py":
                print(letter)

            for animal in ["cats", "dogs", "pythons"]:
                print("I like", animal)
        """, output = """
            P
            y
            I like cats
            I like dogs
            I like pythons
        """)
        text("An important pattern is **summing up**: you start with a variable at 0 and add something in each round.")
        code("""
            total = 0
            for number in range(1, 101):
                total += number
            print(total)
        """, output = "5050")
        text("You can **count** the same way – for example, how many vowels a word has:")
        code("""
            word = "Programming"
            vowels = 0
            for c in word.lower():
                if c in "aeiou":
                    vowels += 1
            print(vowels)
        """, output = "3")
        tip("Young Carl Friedrich Gauss is said to have added up the numbers from 1 to 100 lightning-fast as a schoolboy – Python does it in milliseconds.")

        output("""
            for c in "abc":
                print(c.upper())
        """, "A\nB\nC", "ABC", "abc", "a\nb\nc")
        typeOutput("""
            total = 0
            for x in [3, 5, 2]:
                total += x
            print(total)
        """, "10")
        output("""
            count = 0
            for c in "banana":
                if c == "a":
                    count += 1
            print(count)
        """, "3", "2", "1", "6")
        truth("With `for` you can loop directly over the characters of a string.", true)
        fill("Multiply all numbers from 1 to 5 together.", """
            product = ??
            for i in range(1, 6):
                product ?? i
            print(product)
        """, answers = listOf("1", "*="), distractors = listOf("0", "+=", "="), output = "120",
            explain = "When multiplying you have to start at 1 – with 0 the result would always be 0!")
        arrange("Calculate the sum of all numbers in the list and print it.", """
            numbers = [4, 8, 15]
            total = 0
            for n in numbers:
                total += n
            print(total)
        """, output = "27")
        choice("Why do you start with `total = 0` when summing up?",
            "So that the first addition is right – 0 doesn't change the sum.",
            "Because Python would crash otherwise.",
            "Because `total` always has to be 0 at the end.",
            "It doesn't matter, you could just as well start with 1.")
        write("Count how many words in the list have **more than 4 letters** and print the number.",
            starter = "words = [\"egg\", \"Python\", \"code\", \"variable\", \"bug\"]",
            solution = """
                words = ["egg", "Python", "code", "variable", "bug"]
                count = 0
                for w in words:
                    if len(w) > 4:
                        count += 1
                print(count)
            """,
            output = "2",
            mustContain = listOf("for"),
            hint = "Start with `count = 0`, loop through the list and increase `count` when `len(w) > 4`.")
    }

    lesson("break & continue", "Controlling loops", LessonIcon.BRANCH) {
        text("With `break` you **end** a loop immediately. With `continue` you **skip** the rest of the current round:")
        code("""
            for number in range(1, 10):
                if number == 5:
                    break
                print(number)
        """, output = """
            1
            2
            3
            4
        """)
        code("""
            for number in range(1, 7):
                if number % 2 == 0:
                    continue
                print(number)
        """, output = """
            1
            3
            5
        """)
        text("A popular pattern is `while True` with `break` – a loop that runs until a certain situation occurs:")
        code("""
            total = 0
            while True:
                entry = input("Number (or stop): ")
                if entry == "stop":
                    break
                if not entry.isdigit():
                    print("That's not a number!")
                    continue
                total += int(entry)
            print("Sum:", total)
        """, output = """
            Number (or stop): 5
            Number (or stop): x
            That's not a number!
            Number (or stop): 7
            Number (or stop): stop
            Sum: 12
        """, stdin = listOf("5", "x", "7", "stop"))
        tip("With nested loops, `break` only ends the **innermost** loop.")

        output("""
            for i in range(10):
                if i == 3:
                    break
                print(i)
        """, "0\n1\n2", "0\n1\n2\n3", "3", "0 to 9")
        output("""
            for i in range(5):
                if i == 2:
                    continue
                print(i)
        """, "0\n1\n3\n4", "0\n1", "2", "0\n1\n2\n3\n4")
        match(
            "break" to "end the loop immediately",
            "continue" to "jump to the next round",
            "while True" to "runs until break comes",
            "pass" to "does nothing (placeholder)",
        )
        truth("`continue` ends the whole loop.", false, explain = "`continue` only jumps to the next round. `break` ends the loop.")
        typeOutput("""
            for c in "Python":
                if c == "h":
                    break
                print(c, end="")
        """, "Pyt")
        fill("Only print numbers that are **not** divisible by 3.", """
            for n in range(1, 8):
                if n % 3 == 0:
                    ??
                print(n)
        """, answers = listOf("continue"), distractors = listOf("break", "pass", "return"), output = """
            1
            2
            4
            5
            7
        """)
        output("""
            n = 0
            while True:
                n += 1
                if n * n > 20:
                    break
            print(n)
        """, "5", "4", "20", "Endless loop")
        write("Use a loop to find the **first number from 1 upwards** that is divisible by both 7 and 11. Print it and end the loop with `break`.",
            solution = """
                number = 1
                while True:
                    if number % 7 == 0 and number % 11 == 0:
                        print(number)
                        break
                    number += 1
            """,
            output = "77",
            mustContain = listOf("break"),
            hint = "Check `number % 7 == 0 and number % 11 == 0` – otherwise increase `number` by 1.")
    }

    lesson("Nested Loops", "Loops inside loops and patterns", LessonIcon.PUZZLE) {
        text("You can put loops inside each other. The inner loop then runs completely for **every** round of the outer one:")
        code("""
            for row in range(1, 4):
                for col in range(1, 4):
                    print(row * col, end=" ")
                print()
        """, output = """
            1 2 3
            2 4 6
            3 6 9
        """)
        text("This also lets you draw patterns:")
        code("""
            for i in range(1, 5):
                print("#" * i)
        """, output = """
            #
            ##
            ###
            ####
        """)
        code("""
            for x in range(3):
                for y in range(2):
                    print(x, y)
        """, output = """
            0 0
            0 1
            1 0
            1 1
            2 0
            2 1
        """)
        text("Loops can even have an `else`: it runs when the loop finished **without a `break`**.")
        code("""
            for n in [3, 5, 7]:
                if n % 2 == 0:
                    print("Found an even number!")
                    break
            else:
                print("No even number in there.")
        """, output = "No even number in there.")

        output("""
            for a in range(2):
                for b in range(2):
                    print(a, b)
        """, "0 0\n0 1\n1 0\n1 1", "0 0\n1 1", "0 1\n1 0", "0\n1")
        typeOutput("""
            count = 0
            for i in range(3):
                for j in range(4):
                    count += 1
            print(count)
        """, "12", explain = "The inner loop runs completely 3 times: 3 × 4 = 12.")
        output("""
            for i in range(3, 0, -1):
                print("*" * i)
        """, "***\n**\n*", "*\n**\n***", "3\n2\n1", "***")
        truth("With nested loops, the inner loop runs completely for every round of the outer loop.", true)
        choice("When does the `else` block of a `for` loop run?",
            "When the loop finishes without `break`", "Always after the loop", "Only when there's an error", "Only when the loop is empty")
        arrange("Draw a staircase of plus signs: first `+`, then `++`, then `+++`.", """
            for step in range(1, 4):
                print("+" * step)
        """, distractors = listOf("for step in range(3):"), output = """
            +
            ++
            +++
        """)
        fill("Create a 3 × 3 grid of `o`.", """
            for row in range(3):
                for col in range(??):
                    print("o", end="")
                ??()
        """, answers = listOf("3", "print"), distractors = listOf("2", "input", "4"), output = """
            ooo
            ooo
            ooo
        """)
        write("Print the times tables for 1 to 3 – in the format `1 x 1 = 1`. First all tasks with 1, then with 2, then with 3 (each times 1 to 3).",
            solution = """
                for a in range(1, 4):
                    for b in range(1, 4):
                        print(f"{a} x {b} = {a * b}")
            """,
            output = """
                1 x 1 = 1
                1 x 2 = 2
                1 x 3 = 3
                2 x 1 = 2
                2 x 2 = 4
                2 x 3 = 6
                3 x 1 = 3
                3 x 2 = 6
                3 x 3 = 9
            """,
            mustContain = listOf("for"),
            hint = "Two nested `for` loops with `range(1, 4)` and an f-string.")
    }

    checkpoint()
}
