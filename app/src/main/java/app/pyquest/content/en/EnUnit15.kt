package app.pyquest.content.en

import app.pyquest.content.CourseBuilder
import app.pyquest.content.TQ
import app.pyquest.data.LessonIcon

fun CourseBuilder.enUnit15() = unit("Python in Real Life", "Computer setup, packages, clean code and testing") {
    lesson("Python on a Computer", "Installing, editors and running scripts", LessonIcon.CODE) {
        text("Here in the app, Python runs right on your phone. On a **computer**, you install Python from **python.org** (Windows and Mac) – on Linux it's usually already installed. Then you write Python files with the extension `.py` and run them.")
        text("""
            This is how you start a program in the **terminal** (command prompt):
            - `python hello.py` (Windows) or `python3 hello.py` (Mac/Linux)
            - Typing just `python` opens the interactive **Python shell** (REPL) – there you can try out commands directly
        """)
        text("""
            For programming you use a good editor, an **IDE**:
            - **VS Code** – free, very popular, with a Python extension
            - **PyCharm** – made especially for Python
            - **Thonny** – ideal for beginners
            - **IDLE** – comes included with Python
        """)
        code("""
            import sys
            import platform
            print("Python version:", sys.version.split()[0])
            print("Running on:", platform.system())
        """)
        tip("The line `if __name__ == \"__main__\":` makes sure code only runs when the file is started directly – and not when another file imports it.")
        code("""
            def main():
                print("Program started!")

            if __name__ == "__main__":
                main()
        """, output = "Program started!")

        choice("Which file extension do Python programs have?", ".py", ".python", ".pt", ".exe", mono = true)
        choice("What opens when you type just `python` in the terminal?", "The interactive Python shell (REPL)", "A website", "The code editor", "Nothing")
        truth("VS Code, PyCharm and Thonny are programs in which you can write Python code.", true)
        output("""
            def main():
                print("running")

            if __name__ == "__main__":
                main()
        """, "running", "Nothing", "__main__", "Error", explain = "When the file is started directly, `__name__` is equal to `\"__main__\"`.")
        match("python.org" to "download Python", "VS Code" to "popular editor", "REPL" to "interactive shell", ".py" to "Python file")
        choice("How do you run the file `game.py` in the terminal?", "python game.py", "run game.py", "start python game", "game.py --python", mono = true)
        truth("The block under `if __name__ == \"__main__\":` also runs when another file imports this file.", false)
        fill("Complete the typical start block.", """
            def main():
                print("Hello!")

            if __name__ == ??:
                main()
        """, answers = listOf("\"__main__\""), distractors = listOf("\"main\"", "__main__", "True"), output = "Hello!")
    }

    lesson("pip & Packages", "Extra packages and virtual environments", LessonIcon.BOX) {
        text("Besides the standard library, there are more than **500,000 extra packages** in the **Python Package Index (PyPI)**. On a computer, you install them with **pip**:")
        text("""
            - `pip install requests` – installs a package
            - `pip install --upgrade requests` – updates it
            - `pip list` – shows all installed packages
            - `pip uninstall requests` – removes it again
        """)
        text("""
            Popular packages:
            - `requests` – talk to websites and online services
            - `numpy` – fast math with huge amounts of numbers
            - `pandas` – tables and data analysis
            - `matplotlib` – drawing charts
            - `flask` / `django` – building websites
            - `pygame` – making games
        """)
        text("""
            So that the packages of different projects don't get in each other's way, you use **virtual environments** (venv):
            - `python -m venv .venv` – create an environment
            - `.venv\Scripts\activate` (Windows) or `source .venv/bin/activate` (Mac/Linux) – activate it
            - In a `requirements.txt` you list all the packages of a project and install them with `pip install -r requirements.txt`
        """)
        tip("This app includes the complete **standard library**. You install extra packages like `numpy` on a computer with pip.")
        warn("Only install packages from trustworthy sources and check the exact spelling – there are fake packages with similar names.")

        choice("Which command installs the package `requests`?", "pip install requests", "python get requests", "install requests", "import requests --install", mono = true)
        match("requests" to "internet requests", "pandas" to "tables & data analysis", "matplotlib" to "charts", "pygame" to "games")
        truth("A virtual environment keeps the packages of different projects separate.", true)
        choice("What's the usual name of the file that lists a project's packages?", "requirements.txt", "packages.py", "pip.json", "imports.txt", mono = true)
        choice("What is PyPI?", "The directory of extra packages for Python", "A Python editor", "A programming language", "A type of error")
        truth("Standard library modules like `math` have to be installed with pip first.", false, explain = "The standard library is already included in every Python installation.")
        fill("Create a virtual environment.", "python -m ?? .venv", answers = listOf("venv"), distractors = listOf("pip", "env", "install"))
        choice("What does `pip install -r requirements.txt` do?", "Installs all the packages listed in the file", "Deletes all packages", "Creates the file requirements.txt", "Starts Python")
    }

    lesson("Clean Code", "PEP 8, good names and docstrings", LessonIcon.STAR) {
        text("Code is **read** much more often than it's written. That's why **PEP 8** gives Python official style rules:")
        text("""
            - **4 spaces** per indentation level
            - `snake_case` for variables and functions, `PascalCase` for classes, `UPPER_CASE` for constants
            - Spaces around operators: `x = a + b` instead of `x=a+b`
            - Lines that aren't too long (about 79–99 characters)
            - Two blank lines between functions and classes
        """)
        text("**Meaningful names** are even more important. Compare:")
        code("""
            # Hard to understand:
            def f(a, b):
                return a * b * 0.08

            # Clear right away:
            SALES_TAX_RATE = 0.08

            def calculate_sales_tax(price, quantity):
                return price * quantity * SALES_TAX_RATE

            print(round(calculate_sales_tax(10, 3), 2))
        """, output = "2.4")
        text("With **docstrings** you document what a function does. `help()` and editors show them:")
        code("""
            def circle_area(radius):
                ${TQ}Calculates the area of a circle with the given radius.${TQ}
                return 3.14159 * radius ** 2

            print(circle_area.__doc__)
        """, output = "Calculates the area of a circle with the given radius.")
        tip("Type `import this` in the playground – the **Zen of Python** sums up the philosophy, for example “Readability counts.”")

        choice("Which variable name follows PEP 8?", "player_count", "PlayerCount", "playercount!", "PLAYER-COUNT", mono = true)
        choice("How do you write class names according to PEP 8?", "PascalCase, e.g. `GamePiece`", "snake_case, e.g. `game_piece`", "UPPER_CASE", "It doesn't matter")
        truth("PEP 8 recommends 4 spaces per indentation level.", true)
        choice("Which line is the most readable?", "total_price = price * quantity", "tp=p*q", "TotalPrice=price*quantity", "x = price*quantity", mono = true)
        match("snake_case" to "variables & functions", "PascalCase" to "classes", "UPPER_CASE" to "constants", "Docstring" to "documentation")
        output("""
            def f():
                ${TQ}Says hello.${TQ}
                return "Hello"

            print(f.__doc__)
        """, "Says hello.", "Hello", "f", "None")
        truth("Code is read more often than it's written – that's why readability is so important.", true)
        write("This code works, but it's hard to read. Rewrite it cleanly: meaningful names (`calculate_average`, `grades`), spaces around operators and a docstring. The output should stay the same.",
            starter = """
                def f(l):
                    return sum(l)/len(l)
                print(f([1,2,3,4]))
            """,
            solution = """
                def calculate_average(grades):
                    ${TQ}Returns the average of the grades.${TQ}
                    return sum(grades) / len(grades)


                print(calculate_average([1, 2, 3, 4]))
            """,
            output = "2.5",
            mustContain = listOf("def calculate_average", "grades"),
            hint = "A docstring goes directly below the `def` line, in triple quotes.")
    }

    lesson("Testing & Debugging", "Finding bugs like the pros", LessonIcon.BUG) {
        text("Pros test their code automatically. The simplest way is `assert`. For bigger projects there's the **unittest** module from the standard library or the popular **pytest** package:")
        code("""
            import unittest

            def is_palindrome(word):
                w = word.lower()
                return w == w[::-1]

            class TestPalindrome(unittest.TestCase):
                def test_yes(self):
                    self.assertTrue(is_palindrome("Racecar"))

                def test_no(self):
                    self.assertFalse(is_palindrome("Python"))

            suite = unittest.TestLoader().loadTestsFromTestCase(TestPalindrome)
            result = unittest.TextTestRunner(verbosity=0).run(suite)
            print("Tests:", result.testsRun, "Failures:", len(result.failures))
        """, output = "Tests: 2 Failures: 0")
        text("""
            **Debugging** means finding bugs. Proven strategies:
            - Read the error message carefully (from the bottom up)
            - Show intermediate values with `print()` – e.g. `print(f"{x=}")`
            - Shrink the problem: which line exactly is causing trouble?
            - **Rubber duck debugging**: explain your code out loud to a rubber duck (or to Monty 🐍) – you'll often spot the bug yourself!
        """)
        code("""
            def sum_to(n):
                total = 0
                for i in range(n):
                    total += i
                    print(f"{i=} {total=}")
                return total

            print(sum_to(3))
        """, output = """
            i=0 total=0
            i=1 total=1
            i=2 total=3
            3
        """)
        tip("Do you see the bug? To add up the numbers **up to and including** n, you need `range(n + 1)` – the debug output shows it right away.")
        text("Professional IDEs also have a **debugger**: you set breakpoints and step through the code line by line.")

        choice("What's a good first step when you get an error?", "Read the error message carefully", "Delete all the code", "Restart the computer", "Change something at random")
        truth("`unittest` is included in Python's standard library.", true)
        output("""
            x = 7
            print(f"{x=}")
        """, "x=7", "7", "x", "{x=}")
        choice("What is “rubber duck debugging”?", "Explaining your code out loud to find the bug yourself", "A Python package", "A type of error", "A testing method with duck emojis")
        output("""
            def sum_to(n):
                return sum(range(n + 1))

            print(sum_to(4))
        """, "10", "6", "4", "15")
        match("assert" to "simple test", "unittest" to "test module", "Breakpoint" to "stopping point in the debugger", "print(f\"{x=}\")" to "show a value")
        fill("Complete the test.", """
            def double(x):
                return x * 2

            ?? double(4) == 8
            print("Test passed")
        """, answers = listOf("assert"), distractors = listOf("test", "check", "if"), output = "Test passed")
        write("This function is supposed to count the **vowels** in a text, but it gives wrong results. Find and fix the bug.",
            starter = """
                def vowels(text):
                    count = 0
                    for c in text:
                        if c in "aeiou":
                            count += 1
                    return count

                print(vowels("PYTHON is fun"))
            """,
            solution = """
                def vowels(text):
                    count = 0
                    for c in text.lower():
                        if c in "aeiou":
                            count += 1
                    return count

                print(vowels("PYTHON is fun"))
            """,
            output = "3",
            test = """
                assert vowels("AEIOU") == 5
                assert vowels("xyz") == 0
            """,
            hint = "Capital letters aren't counted – convert the text with `.lower()` first.")
    }

    checkpoint()
}
