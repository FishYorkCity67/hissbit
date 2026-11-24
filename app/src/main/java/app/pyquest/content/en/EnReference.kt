package app.pyquest.content.en

import app.pyquest.data.LessonIcon
import app.pyquest.data.RefCategory
import app.pyquest.data.RefEntry

private class EnCategoryBuilder(private val id: String) {
    val entries = mutableListOf<RefEntry>()
    fun e(title: String, summary: String, body: String = "", code: String? = null, vararg keywords: String) {
        entries += RefEntry(
            id = "$id-${entries.size + 1}",
            title = title,
            summary = summary,
            body = body.trimIndent().trim(),
            code = code?.trimIndent()?.trimEnd(),
            keywords = keywords.toList(),
        )
    }
}

private fun category(id: String, title: String, icon: LessonIcon, block: EnCategoryBuilder.() -> Unit) =
    RefCategory(id, title, icon, EnCategoryBuilder(id).apply(block).entries.toList())

/** Die Python-Referenz auf Englisch (gleiche Kategorien wie die deutsche). */
val EnReferenceCategories: List<RefCategory> = listOf(
    category("basics", "Basics", LessonIcon.BOOK) {
        e("print() – Output", "Shows values on the screen.",
            "Separate several values with commas. `sep` changes the separator, `end` changes the line ending.",
            """
            print("Hello")
            print("a", "b", "c", sep="-")
            print("no line break", end=" ")
            print("– and on it goes")
            """, "output", "sep", "end")
        e("Variables", "Store values under a name.",
            """
            A variable is created by an assignment with `=`. Names consist of letters, digits and `_`, can't start with a digit and are case-sensitive. The usual style is `snake_case`.
            """,
            """
            name = "Monty"
            age = 3
            age = age + 1
            print(name, "is", age)
            """, "assignment", "snake_case")
        e("Comments", "Notes in the code that Python ignores.",
            "Single-line comments start with `#`. For longer explanations of functions, you use docstrings.",
            """
            # This is a comment
            x = 42  # a comment at the end of a line
            print(x)
            """, "#", "note")
        e("input() – Input", "Reads a line of text from the keyboard.",
            "`input()` **always returns a string**. For numbers you have to convert it, e.g. with `int()` or `float()`.",
            """
            # In the playground you can type in the input:
            # number = int(input("Number: "))
            text = "42"
            number = int(text)
            print(number * 2)
            """, "input", "keyboard")
        e("Indentation", "In Python, blocks are marked by indentation.",
            "A colon (`if`, `for`, `def` …) is followed by an indented block. The standard is **4 spaces**. Everything with the same indentation belongs to the same block.",
            """
            for i in range(2):
                print("in the block", i)
                print("also in the block")
            print("after the block")
            """, "block", "spaces", "indent")
        e("Arithmetic Operators", "`+ - * / // % **`",
            "`/` always gives a decimal number, `//` a whole number, `%` the remainder, `**` the power. Multiplication and division come before addition and subtraction.",
            """
            print(7 + 2, 7 - 2, 7 * 2)
            print(7 / 2, 7 // 2, 7 % 2)
            print(2 ** 8)
            print((2 + 3) * 4)
            """, "plus", "minus", "modulo", "power")
        e("Comparison Operators", "`== != < > <= >=` give True or False.",
            "Careful: `=` assigns, `==` compares! Comparisons can be chained: `1 < x < 10`.",
            """
            x = 5
            print(x == 5, x != 5)
            print(x > 3, x <= 4)
            print(1 < x < 10)
            """, "equal", "compare")
        e("Logical Operators", "`and`, `or` and `not` combine conditions.",
            "`and` is only true if both sides are true, `or` if at least one is true. `not` flips the value.",
            """
            sunny = True
            holidays = False
            print(sunny and holidays)
            print(sunny or holidays)
            print(not holidays)
            """, "and", "or", "not", "bool")
        e("Shorthand Assignments", "`+= -= *= /=` change a variable directly.",
            "",
            """
            points = 10
            points += 5   # like points = points + 5
            points *= 2
            print(points)
            """, "+=", "increase")
        e("None", "The value for “nothing”.",
            "`None` stands for a missing value. Functions without `return` return `None`. You check for it with `is None`.",
            """
            result = None
            print(result is None)
            def nothing():
                pass
            print(nothing())
            """, "null", "empty")
        e("pass", "A placeholder that does nothing.",
            "Needed when Python expects a block but you don't have any code for it (yet).",
            """
            def later():
                pass  # coming soon

            print("still runs")
            """, "placeholder")
    },
    category("types", "Data Types", LessonIcon.BOX) {
        e("int – Integers", "Whole numbers without a decimal point, as big as you like.",
            "Underscores make big numbers readable: `1_000_000`.",
            """
            a = 42
            b = 1_000_000
            print(a + b)
            print(2 ** 100)
            """, "integer", "whole number")
        e("float – Decimal Numbers", "Numbers with decimal places.",
            "Decimal numbers are written with a dot. They aren't always exact – that's why you round them: `round(x, 2)`.",
            """
            price = 2.99
            print(price * 3)
            print(0.1 + 0.2)
            print(round(0.1 + 0.2, 2))
            """, "decimal", "float")
        e("str – Text", "Strings of characters in quotes.",
            "Strings are immutable. Methods like `upper()` return a **new** string.",
            """
            greeting = "Hello"
            print(greeting + " world")
            print(greeting * 3)
            print(len(greeting))
            """, "string", "text")
        e("bool – Truth Values", "`True` or `False`.",
            "The result of every comparison. Write them with a capital letter at the start!",
            """
            is_big = 10 > 5
            print(is_big, type(is_big))
            print(True + True)  # True counts as 1
            """, "true", "false", "boolean")
        e("list – Lists", "An ordered, changeable collection: `[1, 2, 3]`.",
            "", """
            colors = ["red", "green"]
            colors.append("blue")
            print(colors, colors[0], len(colors))
            """, "list", "array")
        e("tuple – Tuples", "Like a list, but immutable: `(1, 2)`.",
            "Ideal for fixed values like coordinates. Tuples can be unpacked.",
            """
            point = (3, 4)
            x, y = point
            print(x, y)
            """, "tuple", "immutable")
        e("dict – Dictionaries", "Key-value pairs: `{\"a\": 1}`.",
            "", """
            ages = {"Ada": 36, "Alan": 41}
            ages["Grace"] = 85
            print(ages["Ada"], ages)
            """, "dictionary", "key", "map")
        e("set – Sets", "Unordered, without duplicates: `{1, 2, 3}`.",
            "You create an empty set with `set()` – `{}` is an empty dictionary!",
            """
            numbers = {1, 2, 2, 3, 3, 3}
            print(numbers)
            print(2 in numbers)
            """, "set", "duplicates")
        e("Type Conversion", "`int()`, `float()`, `str()`, `bool()`, `list()` …",
            "If a conversion doesn't work, you get a `ValueError`.",
            """
            print(int("42") + 1)
            print(float("3.5"))
            print(str(99) + "!")
            print(list("abc"))
            print(bool(0), bool("Hello"))
            """, "casting", "convert")
        e("type() and isinstance()", "Finding out the type of a value.",
            "`isinstance(x, int)` is usually better than `type(x) == int` because it also recognizes subclasses.",
            """
            print(type(3.14))
            print(isinstance(5, int))
            print(isinstance("5", (int, float)))
            """, "type")
        e("True or False? (Truthiness)", "Every value is either true or false in a condition.",
            "These are false: `False`, `None`, `0`, `0.0`, `\"\"`, `[]`, `{}`, `set()`. Everything else is true.",
            """
            for value in [0, 1, "", "x", [], [0], None]:
                print(repr(value), "->", bool(value))
            """, "truthy", "falsy")
    },
    category("strings", "Strings", LessonIcon.CHAT) {
        e("Indexes & Slicing", "`s[0]`, `s[-1]`, `s[1:4]`, `s[::-1]`",
            "Counting starts at 0. Negative indexes count from the end. With slicing `[start:stop:step]`, `stop` itself is not included.",
            """
            s = "Python"
            print(s[0], s[-1])
            print(s[1:4])
            print(s[:2], s[2:])
            print(s[::-1])
            """, "index", "substring", "reverse")
        e("f-Strings", "Putting values right into text: `f\"{x}\"`.",
            "Any expression can go inside the curly braces. Formatting comes after the colon: `{price:.2f}`, `{number:5}`, `{x:>8}`. With `{x=}` you see the name and the value.",
            """
            name = "Monty"
            price = 3.14159
            print(f"Hello {name}!")
            print(f"Price: {price:.2f} €")
            print(f"{name:>10}|")
            x = 42
            print(f"{x=}")
            """, "format", "formatting", "placeholder")
        e("Upper and Lower Case", "`upper()`, `lower()`, `title()`, `capitalize()`",
            "", """
            s = "hello world"
            print(s.upper())
            print(s.title())
            print(s.capitalize())
            print("ABC".lower())
            """, "upper", "lower")
        e("strip()", "Removes spaces (or other characters) at the edges.",
            "`lstrip()` only on the left, `rstrip()` only on the right.",
            """
            entry = "   Monty  "
            print("[" + entry.strip() + "]")
            print("---Title---".strip("-"))
            """, "trim", "spaces")
        e("split() and join()", "Taking text apart and putting it back together.",
            "`split()` without an argument splits at spaces. `join()` is called on the separator.",
            """
            sentence = "Python is great"
            words = sentence.split()
            print(words)
            print("-".join(words))
            print("a,b,c".split(","))
            """, "split", "separate", "join")
        e("replace()", "Replacing parts of a string.",
            "", """
            s = "I like Java"
            print(s.replace("Java", "Python"))
            print("aaa".replace("a", "b", 2))
            """, "replace")
        e("Searching: find(), count(), in", "Finding and counting substrings.",
            "`find()` returns the position or -1. `in` only checks whether it appears at all.",
            """
            s = "banana"
            print(s.find("na"))
            print(s.count("a"))
            print("nan" in s)
            print(s.startswith("ba"), s.endswith("na"))
            """, "find", "search", "startswith")
        e("Checking Content", "`isdigit()`, `isalpha()`, `isalnum()`, `isspace()` …",
            "Handy for checking input before you convert it.",
            """
            print("123".isdigit())
            print("abc".isalpha())
            print("abc123".isalnum())
            print("Monty".istitle())
            """, "check", "isdigit")
        e("Escape Sequences", "`\\n`, `\\t`, `\\\\`, `\\\"`",
            "With an `r` in front of the string (a raw string), backslashes aren't interpreted – handy for paths and regular expressions.",
            """
            print("Line 1\nLine 2")
            print("A\tB")
            print("She says: \"Hi\"")
            print(r"C:\new\folder")
            """, "backslash", "line break", "raw")
        e("Multi-line Strings", "With three quotes.",
            "", """
            poem = '''Roses are red,
            Python is blue,
            coding is fun,
            and I love it too.'''
            print(poem)
            """, "multiline", "docstring")
        e("Characters and Numbers", "`ord()` and `chr()` convert characters to codes and back.",
            "", """
            print(ord("A"), chr(66))
            print([chr(c) for c in range(97, 102)])
            """, "ascii", "unicode")
    },
    category("lists", "Lists", LessonIcon.BOX) {
        e("Creating & Accessing", "`[a, b, c]`, `my_list[i]`, `len(my_list)`",
            "Lists can contain anything – even mixed types and other lists.",
            """
            numbers = [10, 20, 30]
            print(numbers[0], numbers[-1], len(numbers))
            numbers[1] = 99
            print(numbers)
            """, "list", "index")
        e("Adding", "`append()`, `insert()`, `extend()`",
            "`append` adds **one** element, `extend` adds all the elements of another list.",
            """
            l = [1, 2]
            l.append(3)
            l.insert(0, 0)
            l.extend([4, 5])
            print(l)
            """, "append", "add")
        e("Removing", "`remove()`, `pop()`, `del`, `clear()`",
            "`remove(x)` deletes the first value x, `pop(i)` removes and returns the element at position i (without i: the last one).",
            """
            l = ["a", "b", "c", "d"]
            l.remove("b")
            last = l.pop()
            del l[0]
            print(l, last)
            """, "delete", "pop")
        e("Sorting", "`sort()` changes the list, `sorted()` returns a new one.",
            "With `reverse=True` in descending order, with `key=` by your own rule.",
            """
            numbers = [3, 1, 2]
            print(sorted(numbers))
            numbers.sort(reverse=True)
            print(numbers)
            words = ["kiwi", "apple", "fig"]
            print(sorted(words, key=len))
            """, "sort", "order")
        e("Searching & Counting", "`in`, `index()`, `count()`",
            "", """
            l = [5, 3, 5, 1]
            print(3 in l, l.index(5), l.count(5))
            """, "search")
        e("Min, Max, Sum", "`min()`, `max()`, `sum()`",
            "", """
            scores = [82, 95, 67, 88]
            print(min(scores), max(scores), sum(scores))
            print(sum(scores) / len(scores))
            """, "average", "sum")
        e("Slicing", "Sublists with `[start:stop:step]`.",
            "", """
            l = list(range(10))
            print(l[2:5], l[:3], l[-3:], l[::2])
            """, "sublist")
        e("Copying Instead of Linking", "`b = a` doesn't make a copy!",
            "With `b = a`, both names point to the **same** list. You get a real copy with `a.copy()` or `a[:]`.",
            """
            a = [1, 2]
            b = a
            c = a.copy()
            a.append(3)
            print(b, c)
            """, "copy", "reference")
        e("Nested Lists", "Lists inside lists – e.g. for tables.",
            "", """
            grid = [[1, 2, 3], [4, 5, 6]]
            print(grid[1][2])
            for row in grid:
                print(row)
            """, "matrix", "2d")
        e("List Comprehension", "`[expression for x in data if condition]`",
            "Creates a new list in one line.",
            """
            squares = [x * x for x in range(6)]
            even = [x for x in range(10) if x % 2 == 0]
            print(squares, even)
            """, "comprehension")
    },
    category("dicts", "Dictionaries & Sets", LessonIcon.BOX) {
        e("Creating a Dictionary", "`{key: value}`",
            "Keys must be unique and immutable (e.g. strings, numbers, tuples).",
            """
            phone = {"Ada": "555-0151", "Alan": "555-0170"}
            empty = {}
            print(phone, len(phone), empty)
            """, "dict")
        e("Reading with [] and get()", "`d[k]` or `d.get(k, default)`",
            "`d[k]` raises a `KeyError` if k is missing. `get` returns `None` or your default value instead.",
            """
            d = {"a": 1}
            print(d["a"])
            print(d.get("b"))
            print(d.get("b", 0))
            """, "get", "keyerror")
        e("Changing & Deleting", "`d[k] = v`, `del d[k]`, `pop()`, `update()`",
            "", """
            d = {"a": 1}
            d["b"] = 2
            d.update({"c": 3})
            del d["a"]
            print(d, d.pop("b"), d)
            """, "update", "del")
        e("Looping", "`keys()`, `values()`, `items()`",
            "", """
            prices = {"bread": 2.5, "milk": 1.2}
            for item, price in prices.items():
                print(item, price)
            print(list(prices.keys()), sum(prices.values()))
            """, "items", "loop")
        e("Counting with Dictionaries", "A classic: finding out how often things appear.",
            "`d.get(x, 0) + 1` counts up conveniently. It's even easier with `collections.Counter`.",
            """
            text = "banana"
            counter = {}
            for letter in text:
                counter[letter] = counter.get(letter, 0) + 1
            print(counter)
            """, "count", "frequency")
        e("Dict Comprehension", "`{k: v for ...}`",
            "", """
            squares = {x: x * x for x in range(5)}
            print(squares)
            """, "comprehension")
        e("Set Operations", "Union `|`, intersection `&`, difference `-`",
            "", """
            a = {1, 2, 3}
            b = {3, 4}
            print(a | b, a & b, a - b, a ^ b)
            a.add(9)
            print(a)
            """, "set", "union", "intersection")
        e("Removing Duplicates", "With `set()` – or keeping the order with `dict.fromkeys()`.",
            "", """
            l = [3, 1, 3, 2, 1]
            print(set(l))
            print(list(dict.fromkeys(l)))
            """, "unique", "duplicates")
    },
    category("flow", "Control Flow", LessonIcon.BRANCH) {
        e("if / elif / else", "Running code only under a condition.",
            "Python checks from top to bottom and runs **only the first** matching block.",
            """
            temperature = 18
            if temperature > 25:
                print("Hot!")
            elif temperature > 15:
                print("Pleasant")
            else:
                print("Cold")
            """, "if", "condition")
        e("Conditional Expression", "`a if condition else b` in one line.",
            "", """
            age = 16
            status = "adult" if age >= 18 else "minor"
            print(status)
            """, "ternary", "one-liner")
        e("while Loop", "Repeats as long as the condition is true.",
            "Make sure the condition becomes false at some point – otherwise you get an endless loop.",
            """
            countdown = 3
            while countdown > 0:
                print(countdown)
                countdown -= 1
            print("Liftoff!")
            """, "while", "repeat")
        e("for Loop", "Goes through every element of a collection.",
            "", """
            for fruit in ["apple", "pear"]:
                print(fruit)
            for letter in "Py":
                print(letter)
            """, "for", "loop")
        e("range()", "Number sequences: `range(stop)`, `range(start, stop, step)`.",
            "The stop value itself is not included.",
            """
            print(list(range(5)))
            print(list(range(2, 8)))
            print(list(range(10, 0, -3)))
            """, "sequence")
        e("break & continue", "Stopping a loop or skipping a round.",
            "", """
            for i in range(10):
                if i == 2:
                    continue
                if i == 5:
                    break
                print(i)
            """, "stop", "skip")
        e("else with Loops", "Runs when the loop ends **without** break.",
            "", """
            for number in [3, 5, 7]:
                if number % 2 == 0:
                    print("found an even number")
                    break
            else:
                print("no even number in there")
            """, "for else")
        e("enumerate()", "Index and element at the same time.",
            "", """
            for nr, name in enumerate(["Ada", "Alan"], start=1):
                print(nr, name)
            """, "index", "numbering")
        e("zip()", "Looping through several lists in parallel.",
            "", """
            names = ["Ada", "Alan"]
            years = [1815, 1912]
            for name, year in zip(names, years):
                print(name, year)
            """, "parallel", "pairs")
        e("match-case", "Pattern matching (Python 3.10 and newer).",
            "`case _:` catches everything else. Patterns can also combine lists, tuples or values with `|`.",
            """
            def day_type(day):
                match day:
                    case "Sat" | "Sun":
                        return "weekend"
                    case "Mon" | "Tue" | "Wed" | "Thu" | "Fri":
                        return "weekday"
                    case _:
                        return "unknown"

            print(day_type("Sat"), day_type("Wed"))
            """, "switch", "match")
    },
    category("functions", "Functions", LessonIcon.MAGIC) {
        e("def & return", "Defining your own functions.",
            "`return` ends the function and gives back a value. Without `return`, it returns `None`.",
            """
            def square(x):
                return x * x

            print(square(4))
            """, "function", "return")
        e("Parameters & Default Values", "`def f(a, b=2)`",
            "Parameters with default values have to come after those without. Careful: never use a list as a default value – use `None` instead.",
            """
            def greet(name, greeting="Hello"):
                return f"{greeting}, {name}!"

            print(greet("Ada"))
            print(greet("Alan", "Hi"))
            """, "default", "argument")
        e("Keyword Arguments", "`f(b=1, a=2)` – the order doesn't matter.",
            "", """
            def rectangle(width, height):
                return width * height

            print(rectangle(height=3, width=5))
            """, "keyword", "kwargs")
        e("*args and **kwargs", "Accepting any number of arguments.",
            "`*args` collects positional arguments in a tuple, `**kwargs` collects keyword arguments in a dictionary.",
            """
            def total(*numbers):
                return sum(numbers)

            def profile(**info):
                for k, v in info.items():
                    print(k, "=", v)

            print(total(1, 2, 3))
            profile(name="Monty", species="python")
            """, "args", "variable")
        e("Multiple Return Values", "Returning a tuple and unpacking it.",
            "", """
            def min_max(values):
                return min(values), max(values)

            small, big = min_max([4, 9, 1])
            print(small, big)
            """, "tuple", "unpack")
        e("lambda", "Small anonymous functions: `lambda x: x * 2`.",
            "Handy as a `key` when sorting or for `map`/`filter`.",
            """
            double = lambda x: x * 2
            print(double(5))
            pairs = [("Ada", 36), ("Alan", 41), ("Grace", 30)]
            print(sorted(pairs, key=lambda p: p[1]))
            """, "anonymous")
        e("Scope", "Local and global variables.",
            "Variables inside a function are local. To **change** a global variable you need `global` – but it's better to return values.",
            """
            counter = 0

            def increase():
                global counter
                counter += 1

            increase()
            increase()
            print(counter)
            """, "scope", "global", "local")
        e("Recursion", "A function calls itself.",
            "Every recursion needs a **base case** that ends the calls.",
            """
            def countdown(n):
                if n == 0:
                    print("Liftoff!")
                    return
                print(n)
                countdown(n - 1)

            countdown(3)
            """, "recursive")
        e("Docstrings", "Documentation right inside the function.",
            "The first string in a function is its docstring. `help(function)` shows it.",
            """
            def area(r):
                '''Calculates the area of a circle with radius r.'''
                return 3.14159 * r * r

            print(area.__doc__)
            """, "documentation", "help")
        e("Type Hints", "Type annotations for parameters and return values.",
            "Type hints aren't enforced, but they help editors and people understand the code.",
            """
            def repeat(text: str, n: int = 2) -> str:
                return text * n

            def total(values: list[float]) -> float:
                return sum(values)

            print(repeat("ab"), total([1.5, 2.5]))
            """, "types", "annotation")
        e("Decorators", "Extending functions with `@`.",
            "A decorator is a function that takes a function and returns a new one.",
            """
            def loud(function):
                def wrapper(*args):
                    return function(*args).upper() + "!"
                return wrapper

            @loud
            def greet(name):
                return f"hello {name}"

            print(greet("monty"))
            """, "decorator", "@")
    },
    category("errors", "Error Handling", LessonIcon.BUG) {
        e("try & except", "Catching errors instead of crashing.",
            "Catch error types as **specifically** as possible – not everything at once.",
            """
            try:
                number = int("abc")
            except ValueError:
                print("That wasn't a number!")
            """, "exception", "catch")
        e("Several Error Types & as", "`except (A, B) as e:`",
            "", """
            for value in ["10", "0", "x"]:
                try:
                    print(100 / int(value))
                except ZeroDivisionError:
                    print("Not by 0!")
                except ValueError as e:
                    print("Error:", e)
            """, "as", "error message")
        e("else & finally", "`else` if there was no error, `finally` always.",
            "", """
            try:
                result = 10 / 2
            except ZeroDivisionError:
                print("Error")
            else:
                print("Result:", result)
            finally:
                print("Done – no matter what happened.")
            """, "finally", "clean up")
        e("raise", "Raising an error yourself.",
            "", """
            def root(x):
                if x < 0:
                    raise ValueError("x must not be negative")
                return x ** 0.5

            try:
                root(-4)
            except ValueError as e:
                print("Error:", e)
            """, "throw", "raise")
        e("Custom Exceptions", "Inheriting from `Exception`.",
            "", """
            class NotEnoughMoney(Exception):
                pass

            try:
                raise NotEnoughMoney("The balance is too low")
            except NotEnoughMoney as e:
                print("Custom error:", e)
            """, "custom", "class")
        e("assert", "Checking assumptions in the code.",
            "`assert condition, \"message\"` raises an `AssertionError` if the condition is false. Great for tests.",
            """
            def double(x):
                return x * 2

            assert double(2) == 4
            assert double(0) == 0, "0 should stay 0"
            print("All checks passed")
            """, "test", "check")
    },
    category("oop", "Classes & Objects", LessonIcon.CLASS) {
        e("class & __init__", "A blueprint for your own objects.",
            "`__init__` is called when an object is created. `self` is the object itself.",
            """
            class Dog:
                def __init__(self, name):
                    self.name = name

            rex = Dog("Rex")
            print(rex.name)
            """, "class", "object", "constructor")
        e("Methods", "Functions that belong to a class.",
            "", """
            class Counter:
                def __init__(self):
                    self.value = 0

                def click(self):
                    self.value += 1
                    return self.value

            c = Counter()
            c.click()
            print(c.click())
            """, "method", "self")
        e("Inheritance & super()", "One class extends another.",
            "The child class takes over everything from the parent class and can override methods. `super()` calls the parent class's version.",
            """
            class Animal:
                def __init__(self, name):
                    self.name = name
                def sound(self):
                    return "..."

            class Cat(Animal):
                def sound(self):
                    return "Meow"

            class Lion(Cat):
                def sound(self):
                    return super().sound().upper() + "!"

            for a in [Animal("?"), Cat("Mimi"), Lion("Leo")]:
                print(a.name, a.sound())
            """, "inheritance", "inherit", "super")
        e("Magic Methods", "`__str__`, `__repr__`, `__len__`, `__eq__`, `__add__` …",
            "They make your own objects behave like built-in types.",
            """
            class Vector:
                def __init__(self, x, y):
                    self.x, self.y = x, y
                def __add__(self, o):
                    return Vector(self.x + o.x, self.y + o.y)
                def __eq__(self, o):
                    return (self.x, self.y) == (o.x, o.y)
                def __repr__(self):
                    return f"Vector({self.x}, {self.y})"

            print(Vector(1, 2) + Vector(3, 4))
            print(Vector(1, 1) == Vector(1, 1))
            """, "dunder", "operator")
        e("@property", "Using methods like attributes.",
            "", """
            class Circle:
                def __init__(self, r):
                    self.r = r

                @property
                def area(self):
                    return round(3.14159 * self.r ** 2, 2)

            c = Circle(2)
            print(c.area)
            """, "getter", "property")
        e("Class and Static Methods", "`@classmethod` and `@staticmethod`",
            "Class methods receive the class (`cls`), static methods don't receive anything automatically.",
            """
            class Temperature:
                def __init__(self, celsius):
                    self.celsius = celsius

                @classmethod
                def from_fahrenheit(cls, f):
                    return cls((f - 32) * 5 / 9)

                @staticmethod
                def is_cold(c):
                    return c < 10

            t = Temperature.from_fahrenheit(212)
            print(t.celsius, Temperature.is_cold(5))
            """, "classmethod", "staticmethod")
        e("Class Attributes", "Values that all objects share.",
            "", """
            class Player:
                count = 0
                def __init__(self, name):
                    self.name = name
                    Player.count += 1

            Player("Ada")
            Player("Alan")
            print(Player.count)
            """, "static", "shared")
        e("Dataclasses", "Classes for data with `@dataclass`.",
            "`__init__`, `__repr__` and `__eq__` are created automatically.",
            """
            from dataclasses import dataclass, field

            @dataclass
            class Book:
                title: str
                pages: int = 100
                tags: list[str] = field(default_factory=list)

            b = Book("Learning Python", 250)
            print(b)
            print(b == Book("Learning Python", 250))
            """, "dataclass", "data")
        e("Encapsulation", "`_internal` and `__private` as a convention.",
            "One underscore means “please don't use this from outside”. Two underscores change the name (name mangling).",
            """
            class Account:
                def __init__(self):
                    self._balance = 0

                def deposit(self, amount):
                    self._balance += amount

                def balance(self):
                    return self._balance

            a = Account()
            a.deposit(50)
            print(a.balance())
            """, "private", "underscore")
    },
    category("iter", "Iterators & Generators", LessonIcon.LOOP) {
        e("Iterators", "`iter()` and `next()`",
            "Everything you can loop through with `for` is iterable. An iterator gives you the next element with `next()`.",
            """
            it = iter([1, 2, 3])
            print(next(it), next(it), next(it))
            """, "iterator", "next")
        e("Generators with yield", "Functions that deliver values one at a time.",
            "Generators save memory because they only create values when they're needed.",
            """
            def even_numbers(limit):
                n = 0
                while n <= limit:
                    yield n
                    n += 2

            print(list(even_numbers(10)))
            """, "yield", "generator")
        e("Generator Expressions", "`(x for x in ...)` – like comprehensions, but lazy.",
            "", """
            total = sum(x * x for x in range(1000))
            print(total)
            """, "lazy")
        e("itertools", "Tools for iterating.",
            "", """
            import itertools
            print(list(itertools.permutations("abc", 2)))
            print(list(itertools.combinations([1, 2, 3], 2)))
            print(list(itertools.chain([1, 2], [3])))
            print(list(itertools.islice(itertools.count(10, 5), 4)))
            """, "permutation", "combination")
        e("any() and all()", "Is it true for any / all of the elements?",
            "", """
            numbers = [2, 4, 7]
            print(any(n % 2 == 1 for n in numbers))
            print(all(n > 0 for n in numbers))
            """, "any", "all")
        e("map() and filter()", "Applying a function to all elements / picking elements.",
            "Comprehensions are often easier to read – but these are good to know.",
            """
            numbers = [1, 2, 3, 4]
            print(list(map(str, numbers)))
            print(list(filter(lambda n: n % 2 == 0, numbers)))
            """, "map", "filter")
    },
    category("modules", "Modules & Packages", LessonIcon.FILE) {
        e("import", "Bringing in modules.",
            "`import module`, `from module import name`, `import module as short`.",
            """
            import math
            from random import randint
            import statistics as st

            print(math.pi)
            print(1 <= randint(1, 6) <= 6)
            print(st.mean([1, 2, 3]))
            """, "import", "library")
        e("if __name__ == \"__main__\"", "Running code only when the file is started directly.",
            "In files that are also imported as modules, this block protects the start code.",
            """
            def main():
                print("The program is running!")

            if __name__ == "__main__":
                main()
            """, "main", "start")
        e("pip & Packages", "Installing extra libraries (on a computer).",
            """
            On your computer, you install packages from the Python Package Index (PyPI) with:

            `pip install requests`

            Popular packages: `requests` (internet), `numpy` (math), `pandas` (data analysis), `matplotlib` (charts), `flask` (websites), `pygame` (games). This app includes the complete **standard library**.
            """, null, "pip", "install", "pypi")
        e("Virtual Environments", "Keeping packages separate for each project.",
            """
            On a computer, you create a virtual environment with `python -m venv .venv` and activate it (Windows: `.venv\Scripts\activate`, Mac/Linux: `source .venv/bin/activate`). After that, `pip` installs packages only for this project.
            """, null, "venv", "environment")
        e("dir() and help()", "Finding out what a module can do.",
            "", """
            import math
            print([n for n in dir(math) if not n.startswith("_")][:10])
            """, "help", "documentation")
    },
    category("files", "Files", LessonIcon.FILE) {
        e("Writing a File", "`open(name, \"w\")` with `with`.",
            "`with` closes the file automatically. Mode `\"w\"` overwrites, `\"a\"` appends.",
            """
            with open("note.txt", "w", encoding="utf-8") as f:
                f.write("First line\n")
                f.write("Second line\n")
            print("saved")
            """, "write", "save")
        e("Reading a File", "`read()`, `readlines()` or line by line.",
            "", """
            with open("demo.txt", "w", encoding="utf-8") as f:
                f.write("apple\npear\n")

            with open("demo.txt", encoding="utf-8") as f:
                for line in f:
                    print(line.strip())
            """, "read", "lines")
        e("File Modes", "`r`, `w`, `a`, `x`, `b` …",
            """
            - `"r"` read (default)
            - `"w"` write (overwrites!)
            - `"a"` append
            - `"x"` create new (error if it exists)
            - `"b"` binary, e.g. `"rb"` for images
            """, null, "mode")
        e("pathlib", "Modern paths and files.",
            "", """
            from pathlib import Path

            p = Path("hello.txt")
            p.write_text("Hello file!", encoding="utf-8")
            print(p.exists(), p.read_text(encoding="utf-8"))
            print(p.suffix, p.stem)
            """, "path", "pathlib")
        e("CSV Files", "Tables with the csv module.",
            "", """
            import csv

            with open("scores.csv", "w", newline="", encoding="utf-8") as f:
                w = csv.writer(f)
                w.writerow(["Name", "Score"])
                w.writerow(["Ada", 95])

            with open("scores.csv", encoding="utf-8") as f:
                for row in csv.reader(f):
                    print(row)
            """, "csv", "table")
        e("JSON Files", "Saving and loading data.",
            "", """
            import json

            data = {"name": "Monty", "level": 3}
            with open("save.json", "w", encoding="utf-8") as f:
                json.dump(data, f)

            with open("save.json", encoding="utf-8") as f:
                print(json.load(f))
            """, "json", "save")
    },
    category("stdlib", "Standard Library", LessonIcon.STAR) {
        e("math", "Mathematical functions.",
            "", """
            import math
            print(math.sqrt(16), math.pi)
            print(math.floor(2.7), math.ceil(2.1))
            print(math.gcd(12, 18), math.factorial(5))
            """, "square root", "math")
        e("random", "Random numbers and random choices.",
            "", """
            import random
            random.seed(1)
            print(random.randint(1, 6))
            print(random.choice(["red", "green", "blue"]))
            cards = [1, 2, 3, 4]
            random.shuffle(cards)
            print(cards, random.random() < 1)
            """, "random", "dice")
        e("datetime", "Date and time.",
            "", """
            from datetime import date, timedelta
            d = date(2026, 12, 24)
            print(d.strftime("%m/%d/%Y"), d.weekday())
            print(d + timedelta(days=7))
            """, "date", "time", "clock")
        e("time", "Measuring time and waiting.",
            "", """
            import time
            start = time.perf_counter()
            time.sleep(0.2)
            print(round(time.perf_counter() - start, 1), "seconds")
            """, "sleep", "stopwatch")
        e("collections.Counter", "Counting things.",
            "", """
            from collections import Counter
            c = Counter("mississippi")
            print(c.most_common(2))
            """, "count", "frequency")
        e("collections.defaultdict & deque", "Dictionaries with a default value, fast queues.",
            "", """
            from collections import defaultdict, deque
            groups = defaultdict(list)
            for word in ["apple", "avocado", "banana"]:
                groups[word[0]].append(word)
            print(dict(groups))
            q = deque([1, 2])
            q.appendleft(0)
            q.append(3)
            print(q.popleft(), q)
            """, "defaultdict", "deque", "queue")
        e("functools", "`lru_cache`, `reduce`, `partial` …",
            "`@lru_cache` remembers results – it makes recursive functions lightning fast.",
            """
            from functools import lru_cache, reduce

            @lru_cache
            def fib(n):
                return n if n < 2 else fib(n - 1) + fib(n - 2)

            print(fib(80))
            print(reduce(lambda a, b: a * b, [1, 2, 3, 4]))
            """, "cache", "reduce")
        e("re – Regular Expressions", "Searching for patterns in texts.",
            "", """
            import re
            text = "Phone: 555-1234, Fax: 555-9876"
            print(re.findall(r"\d+-\d+", text))
            print(re.sub(r"\d", "#", "PIN 1234"))
            """, "regex", "pattern")
        e("json", "Exchanging data as text.",
            "", """
            import json
            s = json.dumps({"a": [1, 2], "b": True})
            print(s)
            print(json.loads(s)["a"])
            """, "json")
        e("statistics", "Mean, median, mode …",
            "", """
            import statistics as st
            values = [2, 4, 4, 5, 9]
            print(st.mean(values), st.median(values), st.mode(values))
            """, "statistics", "average")
        e("string & secrets", "Character sets and secure random values.",
            "", """
            import string, secrets
            print(string.ascii_uppercase[:5], string.digits)
            print(len(secrets.token_hex(8)))
            """, "password", "token")
        e("os & sys", "Information about the system and the program.",
            "", """
            import os, sys
            print(sys.version.split()[0])
            print(os.getcwd() != "")
            print(os.path.join("folder", "file.txt"))
            """, "system", "path")
    },
    category("builtins", "Built-in Functions", LessonIcon.CODE) {
        e("len()", "The length of strings, lists, dicts …", "", """
            print(len("Python"), len([1, 2]), len({"a": 1}))
            """, "length", "count")
        e("abs(), round(), pow(), divmod()", "Calculating with built-in functions.", "", """
            print(abs(-7), round(3.14159, 2), pow(2, 5))
            print(divmod(17, 5))
            """, "round", "absolute")
        e("sorted() & reversed()", "Going through things sorted / in reverse.", "", """
            print(sorted("python"))
            print(list(reversed([1, 2, 3])))
            """, "sort")
        e("min() & max() with key", "The smallest/largest by your own rule.", "", """
            words = ["fig", "banana", "kiwi"]
            print(max(words, key=len), min(words, key=len))
            """, "maximum", "minimum")
        e("int() with a Base", "Numbers in other number systems.", "", """
            print(int("ff", 16), int("1010", 2))
            print(bin(10), hex(255), oct(8))
            """, "binary", "hex")
        e("help() & dir()", "Help for everything in Python.", "", """
            print([m for m in dir(str) if not m.startswith("_")][:8])
            """, "help")
        e("id() & is", "Is it the same object?", "`==` compares values, `is` compares identity.", """
            a = [1]
            b = [1]
            print(a == b, a is b)
            """, "identity")
    },
    category("pro", "Pro Tips", LessonIcon.ROCKET) {
        e("Unpacking with *", "`first, *rest = my_list`", "", """
            first, *middle, last = [1, 2, 3, 4, 5]
            print(first, middle, last)
            print(*[1, 2, 3])
            """, "unpacking", "star")
        e("Swapping Values", "`a, b = b, a`", "", """
            a, b = 1, 2
            a, b = b, a
            print(a, b)
            """, "swap")
        e("Walrus Operator :=", "Assigning and using a value at the same time.", "", """
            numbers = [3, 8, 1, 9]
            if (n := len(numbers)) > 3:
                print(f"Long list with {n} elements")
            """, "walrus", "assignment")
        e("Merging Dicts", "`a | b` (Python 3.9 and newer)", "", """
            defaults = {"color": "blue", "size": 12}
            mine = {"color": "green"}
            print(defaults | mine)
            """, "merge", "combine")
        e("Context Managers", "`with` cleans up automatically.", "You build your own context managers with `contextlib.contextmanager`.", """
            from contextlib import contextmanager

            @contextmanager
            def frame():
                print("┌──────")
                yield
                print("└──────")

            with frame():
                print("│ Content")
            """, "with", "contextmanager")
        e("PEP 8 – Good Style", "The official style rules for Python.", """
            - 4 spaces of indentation
            - `snake_case` for variables and functions, `PascalCase` for classes, `UPPER_CASE` for constants
            - Spaces around operators: `x = a + b`
            - Lines no longer than about 79–99 characters
            - Meaningful names instead of `x1`, `tmp2`
            """, null, "style", "pep8")
        e("The Zen of Python", "The philosophy behind Python.", "Type `import this` in the playground – Python shows you 19 guiding principles like “Beautiful is better than ugly.”", """
            import this
            """, "zen", "philosophy")
    },
)
