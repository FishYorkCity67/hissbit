package app.pyquest.content.en

import app.pyquest.content.CourseBuilder
import app.pyquest.data.LessonIcon

fun CourseBuilder.enUnit14() = unit("Advanced Python", "Decorators, type hints, match-case and more") {
    lesson("Decorators", "Extending functions with @", LessonIcon.MAGIC) {
        text("A **decorator** extends a function without changing its code. It's a function itself: it receives a function and returns a new one:")
        code("""
            def with_frame(function):
                def wrapper():
                    print("*" * 10)
                    function()
                    print("*" * 10)
                return wrapper

            @with_frame
            def hello():
                print("  Hello!")

            hello()
        """, output = """
            **********
              Hello!
            **********
        """)
        text("`@with_frame` above the function is a shortcut for `hello = with_frame(hello)`. To make a decorator work for functions with any parameters, you use `*args` and `**kwargs`:")
        code("""
            import functools

            def log(function):
                @functools.wraps(function)
                def wrapper(*args, **kwargs):
                    print(f"Call: {function.__name__}{args}")
                    result = function(*args, **kwargs)
                    print(f"Result: {result}")
                    return result
                return wrapper

            @log
            def add(a, b):
                return a + b

            add(3, 4)
        """, output = """
            Call: add(3, 4)
            Result: 7
        """)
        tip("`@functools.wraps` makes sure the decorated function keeps its name and docstring.")
        text("The standard library comes with useful decorators, for example `@lru_cache`, which remembers results:")
        code("""
            from functools import lru_cache

            @lru_cache
            def fib(n):
                return n if n < 2 else fib(n - 1) + fib(n - 2)

            print(fib(100))
        """, output = "354224848179261915075")

        output("""
            def double(f):
                def w():
                    f()
                    f()
                return w

            @double
            def hi():
                print("Hi")

            hi()
        """, "Hi\nHi", "Hi", "Nothing", "Error")
        choice("What does `@decorator` above a function `f` stand for?", "f = decorator(f)", "decorator = f(decorator)", "f is deleted", "f is run twice", mono = true)
        typeOutput("""
            def loud(f):
                def w(text):
                    return f(text).upper()
                return w

            @loud
            def greet(name):
                return "hello " + name

            print(greet("ada"))
        """, "HELLO ADA")
        truth("A decorator is a function that receives a function and returns a new function.", true)
        choice("Why do you use `*args, **kwargs` in the wrapper?",
            "So that the decorator works for functions with any parameters", "Because Python requires it", "So that the function gets faster", "To hide errors")
        fill("Complete the decorator.", """
            def stars(function):
                def wrapper():
                    print("*")
                    ??()
                return ??

            @stars
            def show():
                print("Python")

            show()
        """, answers = listOf("function", "wrapper"), distractors = listOf("show", "stars", "print"), output = """
            *
            Python
        """)
        output("""
            from functools import lru_cache

            calls = 0

            @lru_cache
            def square(n):
                global calls
                calls += 1
                return n * n

            square(4)
            square(4)
            square(4)
            print(calls)
        """, "1", "3", "0", "16", explain = "Thanks to `lru_cache`, the result is stored – the function only really runs the first time.")
        write("Write a decorator `twice` that runs the decorated function twice. Use it to decorate a function `cheer()` that prints `Hooray!`, and call it.",
            solution = """
                def twice(function):
                    def wrapper(*args, **kwargs):
                        function(*args, **kwargs)
                        function(*args, **kwargs)
                    return wrapper

                @twice
                def cheer():
                    print("Hooray!")

                cheer()
            """,
            output = """
                Hooray!
                Hooray!
            """,
            mustContain = listOf("@twice", "def twice"),
            hint = "Call the function twice inside the `wrapper` – and `return wrapper` at the end.")
    }

    lesson("Type Hints", "Writing down the types", LessonIcon.BRAIN) {
        text("With **type hints** you write down which types parameters and return values should have. Python does **not** check them when running – but editors and tools use them to find errors early. And people understand the code faster:")
        code("""
            def greet(name: str, times: int = 1) -> str:
                return ("Hello " + name + "! ") * times

            print(greet("Ada", 2))
        """, output = "Hello Ada! Hello Ada!")
        text("You can describe collections precisely too:")
        code("""
            def average(values: list[float]) -> float:
                return sum(values) / len(values)

            def inventory() -> dict[str, int]:
                return {"apples": 3, "pears": 5}

            print(average([1.0, 2.0, 4.5]))
            print(inventory())
        """, output = """
            2.5
            {'apples': 3, 'pears': 5}
        """)
        text("For values that may also be missing, you write `str | None`:")
        code("""
            def find(names: list[str], start: str) -> str | None:
                for n in names:
                    if n.startswith(start):
                        return n
                return None

            print(find(["Ada", "Alan"], "Al"))
            print(find(["Ada"], "X"))
        """, output = """
            Alan
            None
        """)
        tip("You can annotate variables too: `points: int = 0`.")

        output("""
            def f(x: int) -> int:
                return x * 2

            print(f("ab"))
        """, "abab", "Error", "4", "None", explain = "Type hints aren't checked when running – `\"ab\" * 2` simply works.")
        choice("What does `-> str` mean in `def f() -> str:`?", "The function returns a string", "The function takes a string", "The function turns into a string", "An error")
        truth("Python automatically raises an error when a type hint isn't followed.", false)
        match("list[int]" to "list of integers", "dict[str, float]" to "text → decimal number", "str | None" to "text or nothing", "-> bool" to "returns True/False")
        typeOutput("""
            def total(numbers: list[int]) -> int:
                return sum(numbers)

            print(total([1, 2, 3]))
        """, "6")
        fill("Complete the type hints: the parameter is a string, the return value an integer.", """
            def length(text: ??) -> ??:
                return len(text)

            print(length("Monty"))
        """, answers = listOf("str", "int"), distractors = listOf("list", "float", "bool"),
            explain = "Type hints don't change how the code runs – they only document what's expected. A tool like mypy can check them.")
        choice("Which annotation fits a function that returns nothing?", "-> None", "-> void", "-> nothing", "-> 0", mono = true)
        write("Write a function `is_teenager(age: int) -> bool` that returns `True` if the age is between 13 and 19 (inclusive). Print `is_teenager(15)` and `is_teenager(20)`.",
            solution = """
                def is_teenager(age: int) -> bool:
                    return 13 <= age <= 19

                print(is_teenager(15))
                print(is_teenager(20))
            """,
            output = """
                True
                False
            """,
            test = """
                assert is_teenager(13) and is_teenager(19)
                assert not is_teenager(12)
            """,
            mustContain = listOf("-> bool", ": int"),
            hint = "`return 13 <= age <= 19`")
    }

    lesson("match-case", "Matching patterns instead of if chains", LessonIcon.BRANCH) {
        text("With `match` and `case` you check a value against several **patterns**. That's often clearer than long `if-elif` chains:")
        code("""
            def http_status(code):
                match code:
                    case 200:
                        return "OK"
                    case 404:
                        return "Not found"
                    case 500 | 503:
                        return "Server error"
                    case _:
                        return "Unknown"

            print(http_status(404))
            print(http_status(503))
            print(http_status(999))
        """, output = """
            Not found
            Server error
            Unknown
        """)
        text("""
            - `case _:` catches everything else (like `else`)
            - `|` means “or”
            - Patterns can even take **structures** apart:
        """)
        code("""
            def command(entry):
                match entry.split():
                    case ["go", direction]:
                        return f"You go {direction}."
                    case ["take", *things]:
                        return f"You take: {', '.join(things)}"
                    case ["quit"]:
                        return "Bye!"
                    case _:
                        return "Unknown command"

            print(command("go north"))
            print(command("take sword shield"))
            print(command("dance"))
        """, output = """
            You go north.
            You take: sword, shield
            Unknown command
        """)
        text("With an `if` after a pattern (a **guard**) you check additional conditions:")
        code("""
            def classify(number):
                match number:
                    case 0:
                        return "zero"
                    case n if n < 0:
                        return "negative"
                    case _:
                        return "positive"

            print(classify(-5), classify(0), classify(7))
        """, output = "negative zero positive")

        output("""
            x = 2
            match x:
                case 1:
                    print("one")
                case 2:
                    print("two")
                case _:
                    print("?")
        """, "two", "one", "?", "two\n?")
        output("""
            color = "purple"
            match color:
                case "red" | "orange":
                    print("warm")
                case _:
                    print("other")
        """, "other", "warm", "purple", "Nothing")
        choice("What does `case _:` mean?", "All remaining cases", "An empty string", "An error", "Only the value `_`")
        truth("With `match`, only the **first** matching `case` runs.", true)
        typeOutput("""
            point = (0, 5)
            match point:
                case (0, y):
                    print("on the y-axis at", y)
                case (x, 0):
                    print("on the x-axis at", x)
                case _:
                    print("somewhere")
        """, "on the y-axis at 5")
        fill("Complete the pattern for “or”.", """
            day = "Sun"
            match day:
                case "Sat" ?? "Sun":
                    print("Weekend")
                case _:
                    print("Weekday")
        """, answers = listOf("|"), distractors = listOf("or", "||", ","), output = "Weekend")
        output("""
            def f(n):
                match n:
                    case n if n > 100:
                        return "big"
                    case _:
                        return "small"

            print(f(500), f(5))
        """, "big small", "small big", "big big", "Error")
        write("Write a function `calc(a, op, b)` that uses `match` to support the operators `\"+\"`, `\"-\"`, `\"*\"` and `\"/\"`, and returns `\"?\"` for anything else. Print `calc(6, \"*\", 7)` and `calc(1, \"%\", 2)`.",
            solution = """
                def calc(a, op, b):
                    match op:
                        case "+":
                            return a + b
                        case "-":
                            return a - b
                        case "*":
                            return a * b
                        case "/":
                            return a / b
                        case _:
                            return "?"

                print(calc(6, "*", 7))
                print(calc(1, "%", 2))
            """,
            output = """
                42
                ?
            """,
            test = """
                assert calc(1, "+", 2) == 3
                assert calc(9, "/", 3) == 3
            """,
            mustContain = listOf("match", "case"),
            hint = "One `case \"+\":` per operator and so on – and at the end `case _: return \"?\"`.")
    }

    lesson("Iterators & Context Managers", "How for and with really work", LessonIcon.LOOP) {
        text("Everything you can loop through with `for` follows the **iterator protocol**: `iter()` gives you an iterator, and `next()` fetches the next element. When nothing is left, you get `StopIteration`:")
        code("""
            it = iter(["a", "b"])
            print(next(it))
            print(next(it))
            print(next(it, "empty"))
        """, output = """
            a
            b
            empty
        """)
        text("Your own classes become iterable when they have `__iter__` and `__next__`:")
        code("""
            class Countdown:
                def __init__(self, start):
                    self.current = start

                def __iter__(self):
                    return self

                def __next__(self):
                    if self.current <= 0:
                        raise StopIteration
                    self.current -= 1
                    return self.current + 1

            for number in Countdown(3):
                print(number)
        """, output = """
            3
            2
            1
        """)
        text("A **context manager** guarantees that something happens at the end – like closing the file with `with open(...)`. The easiest way to build your own context managers is `contextlib.contextmanager`:")
        code("""
            from contextlib import contextmanager

            @contextmanager
            def chapter(title):
                print(f"== {title} ==")
                yield
                print("== End ==")

            with chapter("Introduction"):
                print("Python is great.")
        """, output = """
            == Introduction ==
            Python is great.
            == End ==
        """)
        tip("If the code after `yield` should also run when an error occurs in the `with` block, put `yield` inside `try` / `finally`.")

        output("""
            it = iter([1, 2, 3])
            next(it)
            print(next(it))
        """, "2", "1", "3", "[2, 3]")
        output("""
            it = iter([])
            print(next(it, "done"))
        """, "done", "Error", "None", "[]")
        choice("Which two methods does an iterator need?", "__iter__ and __next__", "__init__ and __str__", "__len__ and __getitem__", "iter and next", mono = true)
        truth("With `with open(...)`, the file is closed even if an error occurs in the block.", true)
        typeOutput("""
            from contextlib import contextmanager

            @contextmanager
            def brackets():
                print("(", end="")
                yield
                print(")")

            with brackets():
                print("Py", end="")
        """, "(Py)")
        fill("Complete the context manager and how it's used.", """
            from contextlib import contextmanager

            @contextmanager
            def frame():
                print("Start")
                ??
                print("Stop")

            ?? frame():
                print("Middle")
        """, answers = listOf("yield", "with"), distractors = listOf("return", "for", "pass"), output = """
            Start
            Middle
            Stop
        """)
        output("""
            class Two:
                def __init__(self):
                    self.n = 0

                def __iter__(self):
                    return self

                def __next__(self):
                    self.n += 1
                    if self.n > 2:
                        raise StopIteration
                    return self.n

            print(list(Two()))
        """, "[1, 2]", "[0, 1, 2]", "[1, 2, 3]", "Endless loop")
        write("Write a class `Even(maximum)` that, when you loop over it, gives you all even numbers from 0 to `maximum` – with `__iter__` and `__next__`. Print `list(Even(8))`.",
            solution = """
                class Even:
                    def __init__(self, maximum):
                        self.maximum = maximum
                        self.current = 0

                    def __iter__(self):
                        return self

                    def __next__(self):
                        if self.current > self.maximum:
                            raise StopIteration
                        value = self.current
                        self.current += 2
                        return value

                print(list(Even(8)))
            """,
            output = "[0, 2, 4, 6, 8]",
            test = "assert list(Even(3)) == [0, 2]",
            mustContain = listOf("__next__", "StopIteration"),
            hint = "In `__next__`: if `maximum` is exceeded, `raise StopIteration` – otherwise return the current value and increase it by 2.")
    }

    lesson("Walrus & Unpacking", ":=, * and ** like the pros", LessonIcon.STAR) {
        text("The **walrus operator** `:=` assigns a value and uses it at the same time – handy in conditions:")
        code("""
            data = [4, 8, 15, 16, 23, 42]
            if (count := len(data)) > 5:
                print(f"Lots of data: {count}")
        """, output = "Lots of data: 6")
        text("With `*` you can collect any number of values when **unpacking**:")
        code("""
            first, *middle, last = [1, 2, 3, 4, 5]
            print(first, middle, last)
            head, *rest = "Python"
            print(head, rest)
        """, output = """
            1 [2, 3, 4] 5
            P ['y', 't', 'h', 'o', 'n']
        """)
        text("`*` and `**` also work when **calling** functions and when building lists and dictionaries:")
        code("""
            def point(x, y, z):
                return f"({x}|{y}|{z})"

            coordinates = [1, 2, 3]
            print(point(*coordinates))
            settings = {"x": 0, "y": 5, "z": 9}
            print(point(**settings))
            print([*range(3), *"ab"])
            defaults = {"color": "blue", "size": 12}
            print({**defaults, "color": "red"})
            print(defaults | {"new": True})
        """, output = """
            (1|2|3)
            (0|5|9)
            [0, 1, 2, 'a', 'b']
            {'color': 'red', 'size': 12}
            {'color': 'blue', 'size': 12, 'new': True}
        """)
        tip("Chained comparisons, multiple assignment, `*` unpacking and comprehensions – all of these make code “pythonic”: short, clear and elegant.")

        output("""
            a, *b = [1, 2, 3]
            print(b)
        """, "[2, 3]", "2", "[1, 2]", "Error")
        output("""
            *a, b = "xyz"
            print(a, b)
        """, "['x', 'y'] z", "x yz", "xy z", "Error")
        typeOutput("""
            def total(a, b, c):
                return a + b + c

            values = [1, 2, 3]
            print(total(*values))
        """, "6")
        choice("What does `if (n := len(text)) > 10:` do?", "Stores the length in `n` and compares it with 10", "Compares `n` with 10", "A syntax error", "Sets `n` to 10")
        truth("`{**a, **b}` creates a new dictionary with the entries of `a` and `b`.", true)
        fill("Merge two dictionaries.", """
            base = {"a": 1}
            extra = {"b": 2}
            print(base ?? extra)
        """, answers = listOf("|"), distractors = listOf("+", "&", "or"), output = "{'a': 1, 'b': 2}")
        output("print([*\"ab\", *[1, 2]])", "['a', 'b', 1, 2]", "['ab', 1, 2]", "[['a', 'b'], [1, 2]]", "Error")
        write("Unpack the list `results` so that `winner` holds the first value, `last` the last value and `rest` all the values in between. Print all three.",
            starter = "results = [\"Ada\", \"Alan\", \"Grace\", \"Linus\", \"Guido\"]",
            solution = """
                results = ["Ada", "Alan", "Grace", "Linus", "Guido"]
                winner, *rest, last = results
                print(winner)
                print(rest)
                print(last)
            """,
            output = """
                Ada
                ['Alan', 'Grace', 'Linus']
                Guido
            """,
            mustContain = listOf("*rest"),
            hint = "`winner, *rest, last = results`")
    }

    checkpoint()
}
