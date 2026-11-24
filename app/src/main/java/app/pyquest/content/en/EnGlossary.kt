package app.pyquest.content.en

import app.pyquest.data.GlossaryTerm
import app.pyquest.data.PythonTip
import app.pyquest.data.RefEntry

private fun g(term: String, meaning: String) = GlossaryTerm(term, meaning)

val EnGlossary: List<GlossaryTerm> = listOf(
    g("Algorithm", "An exact step-by-step set of instructions for solving a problem – like a recipe for the computer."),
    g("Argument", "The actual value you pass to a function when you call it, e.g. `5` in `square(5)`."),
    g("Assignment", "Giving a variable a value with `=`."),
    g("Attribute", "A variable that belongs to an object, e.g. `dog.name`."),
    g("Block", "Lines that belong together and have the same indentation, e.g. the body of a loop."),
    g("Bool", "The data type with the values `True` and `False`."),
    g("Bug", "An error in a program. Finding and fixing it is called **debugging**."),
    g("Built-in", "A function that's available without any import, e.g. `print`, `len`, `range`."),
    g("Class", "A blueprint for objects, defined with `class`."),
    g("Comment", "Text after `#` that Python ignores."),
    g("Concatenation", "Joining strings together with `+`."),
    g("Condition", "An expression that results in `True` or `False`, used e.g. in `if` or `while`."),
    g("Decorator", "A function that extends another function. It's written as `@name` above a function."),
    g("Dictionary", "A collection of key-value pairs: `{\"name\": \"Monty\"}`."),
    g("Docstring", "A string right at the start of a function or class that describes it."),
    g("Exception", "An error that occurs while the program is running (e.g. `ValueError`). It can be caught with `try/except`."),
    g("Expression", "Something that results in a value, e.g. `3 + 4` or `len(my_list)`."),
    g("f-string", "A string with an `f` in front, in which `{expression}` is replaced by its value."),
    g("Float", "A decimal number, e.g. `3.14`."),
    g("Function", "A named, reusable block of code, defined with `def`."),
    g("Generator", "A function with `yield` that delivers values one at a time instead of all at once."),
    g("IDE", "Integrated development environment: an editor with extras like a debugger, e.g. VS Code or PyCharm."),
    g("Immutable", "Unchangeable: the value can't be changed after it's created (e.g. `str`, `tuple`, `int`)."),
    g("Indentation", "Spaces at the start of a line. In Python, they decide which lines belong to a block."),
    g("Index", "The position of an element in a sequence. It starts at 0."),
    g("Inheritance", "A class takes over the properties and methods of another class."),
    g("Instance", "A concrete object of a class: in `rex = Dog()`, `rex` is an instance of `Dog`."),
    g("Integer", "A whole number, e.g. `42`."),
    g("Interpreter", "The program that reads Python code line by line and runs it."),
    g("Iterable", "Anything you can loop through with `for`: lists, strings, dicts, `range` …"),
    g("Iterator", "An object that delivers elements one after another with `next()`."),
    g("Keyword", "A reserved word with a fixed meaning, e.g. `if`, `for`, `def`, `class`."),
    g("Lambda", "A small function without a name: `lambda x: x * 2`."),
    g("Library", "Ready-made code that you can use with `import` (e.g. `math`)."),
    g("List", "An ordered, changeable collection: `[1, 2, 3]`."),
    g("Literal", "A value written directly in the code, e.g. `42`, `\"Hi\"`, `[1, 2]`."),
    g("Loop", "Repeats code: `for` (for every element) or `while` (as long as a condition holds)."),
    g("Method", "A function that belongs to an object: `text.upper()`."),
    g("Module", "A Python file with code that you can import."),
    g("Mutable", "Changeable: the content can be changed (e.g. `list`, `dict`, `set`)."),
    g("None", "A special value for “nothing” or “no value”."),
    g("Object", "Everything in Python is an object – with a type, a value and possibly methods."),
    g("Operator", "A symbol for an operation, e.g. `+`, `==`, `and`."),
    g("Package", "A collection of modules. On a computer, you install packages with `pip`."),
    g("Parameter", "The placeholder name in a function definition: in `def f(x)`, `x` is a parameter."),
    g("PEP 8", "The official style guide for how Python code should look."),
    g("pip", "The tool for installing Python packages on a computer."),
    g("Recursion", "When a function calls itself."),
    g("Return Value", "The value a function gives back with `return`."),
    g("Scope", "The area in which a variable is visible – local inside a function, or global."),
    g("Set", "An unordered collection without duplicates: `{1, 2, 3}`."),
    g("Slicing", "Cutting out part of a sequence: `my_list[1:3]`."),
    g("Standard Library", "All the modules that come with Python – “batteries included”."),
    g("String", "A sequence of characters, i.e. text: `\"Hello\"`."),
    g("Syntax", "The grammar of a programming language – the rules for how code has to be written."),
    g("Traceback", "The error message that shows exactly where in the code an error happened."),
    g("Tuple", "An immutable, ordered collection: `(1, 2)`."),
    g("Type", "The kind of a value: `int`, `str`, `list` … You can find it out with `type()`."),
    g("Type Conversion", "Turning a value into another type, e.g. `int(\"5\")`."),
    g("Unpacking", "Spreading the values of a sequence over several variables: `a, b = (1, 2)`."),
    g("Variable", "A name that points to a value: `age = 14`."),
    g("venv", "A virtual environment – it keeps the packages of different projects on a computer separate."),
)

private fun err(id: String, title: String, summary: String, body: String, code: String) =
    RefEntry(id, title, summary, body.trimIndent().trim(), code.trimIndent().trimEnd(), listOf("error", "bug", id))

val EnErrorLexicon: List<RefEntry> = listOf(
    err("syntax", "SyntaxError", "The way the code is written is invalid.", """
        **Typical causes:** a missing bracket, a missing quote, a forgotten colon after `if`/`for`/`def`, `=` instead of `==` in a condition.

        **How to fix it:** Look at the marked line **and the line before it** – that's often where something is missing.
    """, """
        # Error: the colon is missing
        # if 5 > 3
        #     print("yes")
        if 5 > 3:
            print("This is the right way")
    """),
    err("indent", "IndentationError", "The indentation is wrong.", """
        After a colon, Python expects an indented block – or a line is indented when it shouldn't be.

        **Tip:** Always use 4 spaces and never mix tabs and spaces.
    """, """
        def hello():
            print("indented = correct")

        hello()
    """),
    err("name", "NameError", "Python doesn't know a name.", """
        **Causes:** a typo, a variable that's used before it exists, forgotten quotes around text, upper/lower case (`Print` instead of `print`).

        Python often suggests the right name: *Did you mean: 'print'?*
    """, """
        message = "Hello"
        print(message)   # not: print(Message)
    """),
    err("type", "TypeError", "A value has the wrong type.", """
        **Examples:** `"Age: " + 14` (text + number), a function called with the wrong number of arguments, `len(5)`.

        **Solution:** Convert with `str()`, `int()`, `float()` – or use f-strings.
    """, """
        age = 14
        print("Age: " + str(age))
        print(f"Age: {age}")
    """),
    err("value", "ValueError", "The type is right, but the value isn't.", """
        The classic: `int("abc")` or `int("3.5")`. Also `my_list.remove(x)` when x isn't in the list.

        **Solution:** Check input first (`isdigit()`) or catch the error with `try/except ValueError`.
    """, """
        entry = "abc"
        if entry.isdigit():
            print(int(entry))
        else:
            print("Not a valid number")
    """),
    err("zero", "ZeroDivisionError", "Division by 0.", """
        `%` and `//` by 0 aren't allowed either. Check the divisor first.
    """, """
        divisor = 0
        if divisor != 0:
            print(10 / divisor)
        else:
            print("You can't divide by 0!")
    """),
    err("index", "IndexError", "The index is outside the sequence.", """
        A list with 3 elements has the indexes 0, 1 and 2. `my_list[3]` doesn't exist!

        **Tip:** The last index is `len(my_list) - 1` – or simply use `my_list[-1]`.
    """, """
        colors = ["red", "green", "blue"]
        print(colors[2])
        print(colors[-1])
    """),
    err("key", "KeyError", "The key is missing from the dictionary.", """
        **Solution:** Check with `in` first or use `get()` with a default value.
    """, """
        ages = {"Ada": 36}
        print(ages.get("Alan", "unknown"))
        if "Ada" in ages:
            print(ages["Ada"])
    """),
    err("attribute", "AttributeError", "The object doesn't have this method or attribute.", """
        Often a typo (`my_list.apend`) or the wrong type (`5.upper()`). `dir(obj)` shows you what's available.
    """, """
        my_list = [1, 2]
        my_list.append(3)   # not: my_list.apend(3)
        print(my_list)
    """),
    err("import", "ModuleNotFoundError / ImportError", "A module wasn't found.", """
        Check the spelling. External packages (e.g. `numpy`) have to be installed on a computer with `pip` first – in this app, the complete standard library is available.
    """, """
        import math   # standard library – always works
        print(math.sqrt(2))
    """),
    err("file", "FileNotFoundError", "The file doesn't exist.", """
        Check the file name and the path. To create a new file, open it with `"w"`. You can check beforehand with `pathlib.Path(name).exists()`.
    """, """
        from pathlib import Path
        p = Path("doesnotexist.txt")
        print("exists" if p.exists() else "File missing – create it first!")
    """),
    err("recursion", "RecursionError", "Too many nested function calls.", """
        Usually a recursive function is missing its **base case**, or the base case is never reached.
    """, """
        def count_down(n):
            if n <= 0:          # base case!
                return "done"
            return count_down(n - 1)

        print(count_down(50))
    """),
    err("unbound", "UnboundLocalError", "A local variable was used before it got a value.", """
        This happens when you change a variable inside a function that was defined outside of it. Solution: pass the value in as a parameter and return the result – or use `global`.
    """, """
        points = 0

        def plus_one(p):
            return p + 1

        points = plus_one(points)
        print(points)
    """),
    err("eof", "EOFError", "input() didn't get any input.", """
        Occurs when a program reads with `input()` but no more input arrives. In the playground you can type input into the console.
    """, """
        answers = ["yes"]  # instead of input(), for testing
        print(answers.pop(0))
    """),
    err("stop", "StopIteration", "An iterator has no more values.", """
        `next()` was called more often than there are elements. `next(it, default)` avoids the error.
    """, """
        it = iter([1])
        print(next(it))
        print(next(it, "no more values"))
    """),
    err("assert", "AssertionError", "An assert check failed.", """
        `assert` checks an assumption. If it's false, something in the code is wrong – that's exactly what tests are for.
    """, """
        result = 2 + 2
        assert result == 4, "Is math broken?"
        print("Test passed")
    """),
)

private fun tip(title: String, code: String) = PythonTip(title, code.trimIndent().trim())

val EnPythonTips: List<PythonTip> = listOf(
    tip("Swap values without a helper variable", "a, b = b, a"),
    tip("Reverse a string", "\"Python\"[::-1]  # 'nohtyP'"),
    tip("Remove duplicates", "list(dict.fromkeys([3, 1, 3, 2]))"),
    tip("Debugging with f-strings", "x = 42\nprint(f\"{x=}\")  # x=42"),
    tip("Write big numbers readably", "population = 8_000_000_000"),
    tip("Index and value at once", "for i, x in enumerate(my_list, 1):\n    print(i, x)"),
    tip("Two lists in parallel", "for name, score in zip(names, scores):\n    print(name, score)"),
    tip("Sort by length", "sorted(words, key=len)"),
    tip("Most common elements", "from collections import Counter\nCounter(text).most_common(3)"),
    tip("Is there at least one?", "any(x > 10 for x in numbers)"),
    tip("Do all of them match?", "all(x > 0 for x in numbers)"),
    tip("Safe dict access", "ages.get(\"Ada\", 0)"),
    tip("Chain comparisons", "if 0 < x < 10:\n    print(\"single digit\")"),
    tip("If-then in one line", "status = \"ok\" if points > 50 else \"practice\""),
    tip("List to text", "\", \".join([\"a\", \"b\", \"c\"])"),
    tip("Split text into words", "\"Python is great\".split()"),
    tip("Build a list in one line", "[x * x for x in range(10)]"),
    tip("A dictionary in one line", "{w: len(w) for w in words}"),
    tip("The first one and the rest", "first, *rest = [1, 2, 3, 4]"),
    tip("Merge dicts", "everything = defaults | mine"),
    tip("Walrus operator", "if (n := len(data)) > 10:\n    print(n)"),
    tip("Integer division and remainder", "minutes, seconds = divmod(135, 60)"),
    tip("Round to 2 decimal places", "round(3.14159, 2)  # 3.14"),
    tip("A number from another number system", "int(\"ff\", 16)  # 255"),
    tip("A number as binary", "bin(10)  # '0b1010'"),
    tip("Is it a number?", "\"123\".isdigit()  # True"),
    tip("Pick something at random", "import random\nrandom.choice([\"red\", \"blue\"])"),
    tip("Shuffle a list", "random.shuffle(cards)"),
    tip("Open a file safely", "with open(\"a.txt\") as f:\n    text = f.read()"),
    tip("Nicely formatted JSON", "json.dumps(data, indent=2)"),
    tip("Today's date", "from datetime import date\ndate.today()"),
    tip("Measure time", "import time\nstart = time.perf_counter()"),
    tip("Cache results", "from functools import lru_cache\n\n@lru_cache\ndef fib(n): ..."),
    tip("Format numbers to a fixed width", "f\"{price:8.2f} €\""),
    tip("Center text", "\"Title\".center(20, \"-\")"),
    tip("An endless loop with an exit", "while True:\n    if done:\n        break"),
    tip("A loop with else", "for x in data:\n    if x < 0: break\nelse:\n    print(\"all positive\")"),
    tip("Multiple return values", "def mm(l):\n    return min(l), max(l)"),
    tip("Avoid the default value trap", "def f(items=None):\n    items = items or []"),
    tip("The Zen of Python", "import this"),
)
