package app.pyquest.content.en

import app.pyquest.content.CourseBuilder
import app.pyquest.data.LessonIcon

fun CourseBuilder.enUnit09() = unit("Error Handling", "Understanding, catching and raising errors") {
    lesson("Kinds of Errors", "Syntax errors, exceptions and tracebacks", LessonIcon.BUG) {
        text("""
            Errors are completely normal when programming! Python distinguishes two kinds:
            - **Syntax errors** – the code is written incorrectly and doesn't even start
            - **Runtime errors (exceptions)** – the code is written correctly, but something goes wrong while running
        """)
        text("An error message (**traceback**) shows you *where* and *what* happened. The most important information is at the very bottom. Tap ▶:")
        code("""
            numbers = [1, 2, 3]
            print(numbers[5])
        """, fails = true)
        text("""
            The most common exceptions:
            - `NameError` – unknown name
            - `TypeError` – wrong type (e.g. text + number)
            - `ValueError` – wrong value (e.g. `int("abc")`)
            - `IndexError` – index outside the list
            - `KeyError` – key missing in the dictionary
            - `ZeroDivisionError` – division by 0
        """)
        tip("In the **Library** section you'll find an **error guide** with explanations and fixes for all important errors.")

        match(
            "int(\"abc\")" to "ValueError",
            "[1, 2][5]" to "IndexError",
            "10 / 0" to "ZeroDivisionError",
            "{}[\"x\"]" to "KeyError",
            "\"a\" + 1" to "TypeError",
        )
        choice("Which error do you get with `print(undefined_name)`?", "NameError", "TypeError", "ValueError", "SyntaxError", mono = true)
        truth("A SyntaxError only occurs when the faulty line is run.", false,
            explain = "Python finds syntax errors **before** starting – the program doesn't even begin to run.")
        choice("Where is the most important information in a traceback?", "In the last line", "In the first line", "In the middle", "Nowhere – you have to guess")
        choice("Which error occurs here?", "TypeError", "ValueError", "NameError", "KeyError", mono = true,
            code = """
                age = 14
                print("Age: " + age)
            """)
        choice("Which error occurs here?", "ValueError", "TypeError", "IndexError", "No error", mono = true,
            code = """number = int("three")""")
        truth("`[1, 2, 3][3]` causes an IndexError.", true, explain = "The valid indexes are 0, 1 and 2.")
        write("This program crashes. Fix it so that it prints `Age: 14`.",
            starter = """
                age = 14
                print("Age: " + age)
            """,
            solution = """
                age = 14
                print("Age: " + str(age))
            """,
            output = "Age: 14",
            hint = "Convert the number to text with `str()` – or use an f-string.")
    }

    lesson("try & except", "Catching errors instead of crashing", LessonIcon.BUG) {
        text("With `try` and `except` you catch errors instead of letting your program crash. Python tries the `try` block – if something goes wrong, it jumps into the `except` block:")
        code("""
            entry = input("Number: ")
            try:
                number = int(entry)
                print("Doubled:", number * 2)
            except ValueError:
                print("That wasn't a number!")
        """, output = """
            Number: abc
            That wasn't a number!
        """, stdin = listOf("abc"))
        text("You can have several `except` blocks for different errors. With `as` you get the error message:")
        code("""
            for divisor in [2, 0, "x"]:
                try:
                    print(10 / divisor)
                except ZeroDivisionError:
                    print("Can't divide by 0!")
                except TypeError as error:
                    print("Type error:", error)
        """, output = """
            5.0
            Can't divide by 0!
            Type error: unsupported operand type(s) for /: 'int' and 'str'
        """)
        warn("Catch errors as **specifically** as possible. A bare `except:` catches everything – including errors you really should see.")
        tip("A typical pattern: ask for input in a loop until it's valid – with `while True`, `try` and `break`.")

        output("""
            try:
                print(1 / 0)
            except ZeroDivisionError:
                print("Error!")
        """, "Error!", "0", "Infinity", "ZeroDivisionError")
        output("""
            try:
                x = int("5")
                print(x)
            except ValueError:
                print("broken")
        """, "5", "broken", "5\nbroken", "Nothing")
        typeOutput("""
            try:
                items = [1, 2]
                print(items[9])
            except IndexError:
                print("too far")
        """, "too far")
        choice("What happens if **no** error occurs in the `try` block?",
            "The `except` block is skipped.", "The `except` block runs anyway.", "The program stops.", "Python reports an error.")
        truth("With `except ValueError as e:` you can access the error message.", true)
        fill("Catch the error when a key is missing.", """
            stock = {"apples": 5}
            try:
                print(stock["pears"])
            ?? KeyError:
                print("Out of stock")
        """, answers = listOf("except"), distractors = listOf("catch", "else", "error"), output = "Out of stock")
        output("""
            def safe_divide(a, b):
                try:
                    return a / b
                except ZeroDivisionError:
                    return None

            print(safe_divide(8, 2), safe_divide(1, 0))
        """, "4.0 None", "4 0", "4.0 0", "Error")
        write("Write a function `to_number(text)` that converts the text into an integer and returns `0` if that fails. Print `to_number(\"42\")` and `to_number(\"Hello\")`.",
            solution = """
                def to_number(text):
                    try:
                        return int(text)
                    except ValueError:
                        return 0

                print(to_number("42"))
                print(to_number("Hello"))
            """,
            output = """
                42
                0
            """,
            mustContain = listOf("try", "except"),
            hint = "`try: return int(text)` and `except ValueError: return 0`.")
    }

    lesson("else, finally & raise", "Cleaning up and raising your own errors", LessonIcon.BUG) {
        text("""
            `try` can have two more blocks:
            - `else` – runs when **no** error occurred
            - `finally` – **always** runs, no matter what (e.g. for cleaning up)
        """)
        code("""
            def divide(a, b):
                try:
                    result = a / b
                except ZeroDivisionError:
                    print("Error: division by 0")
                else:
                    print("Result:", result)
                finally:
                    print("Calculation finished")

            divide(10, 2)
            divide(1, 0)
        """, output = """
            Result: 5.0
            Calculation finished
            Error: division by 0
            Calculation finished
        """)
        text("With `raise` you trigger an error **yourself** – for example when a function gets invalid values:")
        code("""
            def set_age(age):
                if age < 0:
                    raise ValueError("Age can't be negative")
                return age

            try:
                set_age(-5)
            except ValueError as e:
                print("Error:", e)
        """, output = "Error: Age can't be negative")
        tip("With `raise` without an argument, you can re-raise the caught error inside an `except` block.")

        output("""
            try:
                x = 1
            except ValueError:
                print("A")
            else:
                print("B")
        """, "B", "A", "A\nB", "Nothing")
        output("""
            try:
                1 / 0
            except ZeroDivisionError:
                print("X")
            finally:
                print("Y")
        """, "X\nY", "Y", "X", "Y\nX")
        truth("A `finally` block only runs when an error occurred.", false, explain = "`finally` **always** runs – with and without an error.")
        choice("How do you raise an error yourself in Python?", "raise", "throw", "error", "except", mono = true)
        typeOutput("""
            def check(n):
                if n > 10:
                    raise ValueError("too big")
                return n

            try:
                check(99)
            except ValueError as e:
                print(e)
        """, "too big")
        fill("Raise an error if the name is empty.", """
            def log_in(name):
                if not name:
                    ?? ValueError("Name missing")
                print("Hello", name)

            log_in("Ada")
            try:
                log_in("")
            except ValueError as error:
                print("Error:", error)
        """, answers = listOf("raise"), distractors = listOf("return", "throw", "print"), output = """
            Hello Ada
            Error: Name missing
        """, explain = "You raise an error with `raise`. `throw` doesn't exist in Python.")
        arrange("Try the division, catch the error and **always** print `done` at the end.", """
            try:
                print(10 / 0)
            except ZeroDivisionError:
                print("Division by 0!")
            finally:
                print("done")
        """, output = """
            Division by 0!
            done
        """)
        write("Write a function `root(x)` that raises a `ValueError` with the message `negative` for negative `x` and otherwise returns `x ** 0.5`. Call it for `16` and `-1` and catch the error so that `4.0` and `Error: negative` are printed.",
            solution = """
                def root(x):
                    if x < 0:
                        raise ValueError("negative")
                    return x ** 0.5

                for number in [16, -1]:
                    try:
                        print(root(number))
                    except ValueError as e:
                        print("Error:", e)
            """,
            output = """
                4.0
                Error: negative
            """,
            mustContain = listOf("raise", "except"),
            hint = "In the function `raise ValueError(\"negative\")`, when calling `try` and `except ValueError as e`.")
    }

    lesson("Custom Exceptions & assert", "Your own error types and tests", LessonIcon.BRAIN) {
        text("You can define **your own error types** for your programs. To do that, you create a class that inherits from `Exception` – you'll learn more about classes later:")
        code("""
            class NotEnoughMoney(Exception):
                pass

            def pay(balance, price):
                if price > balance:
                    raise NotEnoughMoney(f"{price - balance} € missing")
                return balance - price

            try:
                pay(20, 35)
            except NotEnoughMoney as e:
                print("Purchase declined:", e)
        """, output = "Purchase declined: 15 € missing")
        text("With `assert` you check assumptions in your code. If the condition is false, you get an `AssertionError`. That's perfect for **tests**:")
        code("""
            def double(x):
                return x * 2

            assert double(2) == 4
            assert double(-3) == -6
            assert double(0) == 0, "0 should stay 0"
            print("All tests passed! ✅")
        """, output = "All tests passed! ✅")
        tip("Many coding exercises in this app are checked in the background with `assert` tests – just like the pros do it.")
        warn("`assert` is meant for tests and development, not for checking user input.")

        output("""
            assert 1 + 1 == 2
            print("ok")
        """, "ok", "AssertionError", "True", "Nothing")
        choice("What happens with `assert 2 > 3`?", "An AssertionError is raised.", "It prints `False`.", "Nothing.", "A SyntaxError.")
        truth("Custom exceptions inherit from the class `Exception`.", true)
        fill("Define your own error type.", """
            class EmptyCart(??):
                pass

            try:
                raise EmptyCart("Nothing in it")
            except EmptyCart as e:
                print(e)
        """, answers = listOf("Exception"), distractors = listOf("Error", "object", "raise"), output = "Nothing in it")
        output("""
            class MyError(Exception):
                pass

            try:
                raise MyError("Oops")
            except Exception as e:
                print(type(e).__name__, e)
        """, "MyError Oops", "Exception Oops", "Oops", "MyError")
        typeOutput("""
            def is_even(n):
                return n % 2 == 0

            assert is_even(4)
            assert not is_even(7)
            print("fine")
        """, "fine")
        choice("What is `assert` mainly used for?", "To test whether the code does the right thing", "To check user input", "To end loops", "To create variables")
        write("Write a function `maximum(a, b)` that returns the larger value. Test it with **three** `assert` statements and print `Tests ok` at the end.",
            solution = """
                def maximum(a, b):
                    if a > b:
                        return a
                    return b

                assert maximum(1, 2) == 2
                assert maximum(5, 3) == 5
                assert maximum(4, 4) == 4
                print("Tests ok")
            """,
            output = "Tests ok",
            test = """
                assert maximum(10, -10) == 10
                assert maximum(-1, 0) == 0
            """,
            mustContain = listOf("assert"),
            hint = "For example `assert maximum(1, 2) == 2`.")
    }

    checkpoint()
}
