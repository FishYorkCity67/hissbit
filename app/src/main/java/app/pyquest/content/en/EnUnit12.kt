package app.pyquest.content.en

import app.pyquest.content.CourseBuilder
import app.pyquest.data.LessonIcon

fun CourseBuilder.enUnit12() = unit("Modules & the Library", "Using Python's huge standard library") {
    lesson("import", "Bringing in ready-made modules", LessonIcon.FILE) {
        text("Python comes with a huge **standard library** – ready-made modules for almost everything. With `import` you bring them into your program:")
        code("""
            import math
            print(math.sqrt(81))
            print(math.pi)
        """, output = """
            9.0
            3.141592653589793
        """)
        text("""
            There are several variants:
            - `import math` – then `math.sqrt()`
            - `from math import sqrt, pi` – then directly `sqrt()`
            - `import statistics as st` – with a short name
        """)
        code("""
            from math import sqrt, floor
            import statistics as st

            print(sqrt(2) > 1.4)
            print(floor(7.9))
            print(st.mean([2, 4, 9]))
        """, output = """
            True
            7
            5
        """)
        tip("`dir(math)` shows everything a module contains – `help(math.sqrt)` shows the description of a function.")
        warn("Never name your own files like standard modules (e.g. `random.py`) – otherwise Python imports your file instead of the real module!")

        output("""
            import math
            print(math.sqrt(16))
        """, "4.0", "4", "16", "Error")
        choice("How do you import only the function `randint` from `random`?", "from random import randint", "import randint from random", "import random.randint()", "using random.randint", mono = true)
        typeOutput("""
            from math import floor, ceil
            print(floor(2.7), ceil(2.1))
        """, "2 3")
        truth("After `import math as m` you call the square root with `m.sqrt()`.", true)
        choice("What happens with `import doesnotexist`?", "A ModuleNotFoundError", "Python downloads the module from the internet", "Nothing", "A SyntaxError")
        fill("Import `pi` directly so that it works without `math.`.", """
            ?? math ?? pi
            print(round(pi, 2))
        """, answers = listOf("from", "import"), distractors = listOf("using", "include", "as"), output = "3.14")
        match("import math" to "load the whole module", "from math import pi" to "load a single name", "import math as m" to "load with a short name", "dir(math)" to "show the contents")
        write("Use the `math` module to calculate the hypotenuse of a right triangle with sides 6 and 8, and print it.",
            solution = """
                import math
                print(math.hypot(6, 8))
            """,
            output = "10.0",
            mustContain = listOf("import math"),
            hint = "`math.hypot(6, 8)` or Pythagoras: `math.sqrt(6**2 + 8**2)`.")
    }

    lesson("random & math", "Randomness and mathematics", LessonIcon.CALC) {
        text("The `random` module creates random values – perfect for games:")
        code("""
            import random
            print(random.randint(1, 6))
            print(random.choice(["rock", "paper", "scissors"]))
            cards = [1, 2, 3, 4, 5]
            random.shuffle(cards)
            print(cards)
            print(random.random())
        """)
        text("""
            - `randint(a, b)` – a whole number from a to b (b **included**!)
            - `choice(my_list)` – a random element
            - `shuffle(my_list)` – shuffles the list (changes it directly)
            - `random()` – a decimal number between 0 and 1
            - `sample(my_list, k)` – k different elements
        """)
        tip("With `random.seed(42)` you get the same “random numbers” every time – useful for testing. Tap ▶ twice and compare!")
        code("""
            import random
            random.seed(42)
            print([random.randint(1, 10) for _ in range(5)])
        """)
        text("The `math` module offers mathematical functions and constants:")
        code("""
            import math
            print(math.floor(3.7), math.ceil(3.2))
            print(math.gcd(24, 36))
            print(math.factorial(6))
            print(round(math.sin(math.pi / 2)))
            print(math.isclose(0.1 + 0.2, 0.3))
        """, output = """
            3 4
            12
            720
            1
            True
        """)

        choice("Which values can `random.randint(1, 3)` return?", "1, 2 or 3", "1 or 2", "0, 1, 2 or 3", "Only decimal numbers")
        truth("`random.shuffle(my_list)` returns a new shuffled list and leaves the original unchanged.", false,
            explain = "`shuffle` shuffles the list **directly** and returns `None`.")
        output("""
            import math
            print(math.gcd(12, 18))
        """, "6", "36", "3", "216")
        typeOutput("""
            import math
            print(math.factorial(4))
        """, "24")
        output("""
            import random
            value = random.choice([7, 7, 7])
            print(value)
        """, "7", "[7]", "A random number", "Error")
        fill("Roll a normal die 1000 times and print the smallest and the largest number rolled.", """
            import random
            rolls = [random.??(1, ??) for _ in range(1000)]
            print(min(rolls), max(rolls))
        """, answers = listOf("randint", "6"), distractors = listOf("choice", "7", "random"), output = "1 6",
            explain = "`randint(1, 6)` gives 1 up to and **including** 6. With 1000 rolls, both extremes are practically certain to come up.")
        output("""
            import math
            print(math.ceil(4.01))
        """, "5", "4", "4.0", "4.01")
        write("Simulate 1000 coin flips with `random.choice([\"heads\", \"tails\"])` and count how often `heads` comes up. Print `True` if the count is between 400 and 600.",
            solution = """
                import random
                heads = 0
                for _ in range(1000):
                    if random.choice(["heads", "tails"]) == "heads":
                        heads += 1
                print(400 <= heads <= 600)
            """,
            output = "True",
            mustContain = listOf("random"),
            hint = "Count in a loop: `if random.choice([...]) == \"heads\": heads += 1`.")
    }

    lesson("datetime & time", "Calculating with dates and times", LessonIcon.STAR) {
        text("With the `datetime` module you calculate with dates and times:")
        code("""
            from datetime import date, timedelta

            christmas = date(2026, 12, 24)
            print(christmas)
            print(christmas.strftime("%m/%d/%Y"))
            print(christmas + timedelta(days=7))
            print(christmas.weekday())
        """, output = """
            2026-12-24
            12/24/2026
            2026-12-31
            3
        """)
        text("You can subtract two dates – the result is a `timedelta`:")
        code("""
            from datetime import date

            start = date(2026, 1, 1)
            end = date(2026, 12, 31)
            diff = end - start
            print(diff.days)
        """, output = "364")
        text("The most important format codes for `strftime`: `%d` day, `%m` month, `%Y` year, `%H` hour, `%M` minute, `%A` weekday.")
        code("""
            from datetime import datetime

            now = datetime.now()
            print(now.strftime("Today is %m/%d/%Y and it's %H:%M."))
        """)
        text("With the `time` module you measure time or let your program wait:")
        code("""
            import time

            start = time.perf_counter()
            total = sum(range(1_000_000))
            duration = time.perf_counter() - start
            print(f"Calculated in {duration:.3f} seconds")
        """)
        tip("`time.sleep(1)` makes your program wait one second – handy for countdowns.")

        output("""
            from datetime import date
            print(date(2026, 5, 17))
        """, "2026-05-17", "05/17/2026", "17-5-2026", "Error")
        typeOutput("""
            from datetime import date
            print((date(2026, 3, 10) - date(2026, 3, 1)).days)
        """, "9")
        output("""
            from datetime import date, timedelta
            print(date(2026, 1, 30) + timedelta(days=5))
        """, "2026-02-04", "2026-01-35", "2026-02-05", "Error")
        choice("Which format code stands for the four-digit year?", "%Y", "%y", "%J", "%a", mono = true)
        truth("`date(2026, 12, 24).strftime(\"%m/%d\")` gives `12/24`.", true)
        fill("Print the date in the US format.", """
            from datetime import date
            d = date(2027, 7, 4)
            print(d.??("%m/%d/%Y"))
        """, answers = listOf("strftime"), distractors = listOf("format", "strptime", "print"), output = "07/04/2027")
        output("""
            from datetime import date
            print(date(2026, 10, 18).weekday())
        """, "6", "0", "7", "18", explain = "`weekday()` counts from Monday = 0. October 18, 2026 is a Sunday.")
        write("Calculate how many days there are between January 1, 2027 and July 1, 2027, and print the number.",
            solution = """
                from datetime import date
                print((date(2027, 7, 1) - date(2027, 1, 1)).days)
            """,
            output = "181",
            mustContain = listOf("date"),
            hint = "Subtract two `date` objects and take `.days`.")
    }

    lesson("collections & itertools", "Counting, grouping, combining", LessonIcon.BOX) {
        text("The `collections` module contains special data structures. The most popular is `Counter` – it counts things for you:")
        code("""
            from collections import Counter

            words = "the cat the dog the cat".split()
            counter = Counter(words)
            print(counter)
            print(counter.most_common(1))
            print(counter["dog"])
        """, output = """
            Counter({'the': 3, 'cat': 2, 'dog': 1})
            [('the', 3)]
            1
        """)
        text("`defaultdict` creates missing entries automatically, `deque` is a queue you can work on quickly at both ends:")
        code("""
            from collections import defaultdict, deque

            groups = defaultdict(list)
            for name in ["Anna", "Ben", "Alex", "Bea"]:
                groups[name[0]].append(name)
            print(dict(groups))

            queue = deque(["Ada", "Alan"])
            queue.append("Grace")
            print(queue.popleft())
            print(list(queue))
        """, output = """
            {'A': ['Anna', 'Alex'], 'B': ['Ben', 'Bea']}
            Ada
            ['Alan', 'Grace']
        """)
        text("`itertools` provides tools for combinations and more:")
        code("""
            import itertools

            print(list(itertools.permutations("ABC", 2)))
            print(list(itertools.combinations([1, 2, 3], 2)))
            print(list(itertools.product("ab", repeat=2)))
        """, output = """
            [('A', 'B'), ('A', 'C'), ('B', 'A'), ('B', 'C'), ('C', 'A'), ('C', 'B')]
            [(1, 2), (1, 3), (2, 3)]
            [('a', 'a'), ('a', 'b'), ('b', 'a'), ('b', 'b')]
        """)
        tip("You can even add and subtract `Counter`s – perfect for keeping track of stock.")

        output("""
            from collections import Counter
            print(Counter("aab")["a"])
        """, "2", "1", "3", "Error")
        output("""
            from collections import Counter
            print(Counter([3, 1, 3, 3, 1]).most_common(1))
        """, "[(3, 3)]", "[(1, 2)]", "3", "[3]")
        typeOutput("""
            from collections import defaultdict
            d = defaultdict(int)
            d["x"] += 5
            print(d["x"])
        """, "5")
        output("""
            import itertools
            print(len(list(itertools.permutations([1, 2, 3]))))
        """, "6", "3", "9", "27")
        truth("With a `defaultdict(list)`, the first access to a new key automatically creates an empty list.", true)
        fill("Count the letters and print the two most common ones.", """
            from collections import ??
            print(Counter("abracadabra").??(2))
        """, answers = listOf("Counter", "most_common"), distractors = listOf("count", "deque", "top"), output = "[('a', 5), ('b', 2)]")
        output("""
            import itertools
            print(list(itertools.combinations("XYZ", 2)))
        """, "[('X', 'Y'), ('X', 'Z'), ('Y', 'Z')]", "[('X', 'Y'), ('Y', 'X')]", "['XY', 'XZ', 'YZ']", "Error")
        write("Use `Counter` to find the **most common word** in the text and print only that word.",
            starter = "text = \"python is great python is fast python rocks\"",
            solution = """
                from collections import Counter
                text = "python is great python is fast python rocks"
                print(Counter(text.split()).most_common(1)[0][0])
            """,
            output = "python",
            mustContain = listOf("Counter"),
            hint = "`Counter(text.split()).most_common(1)` gives `[('python', 3)]` – you only need the word from it.")
    }

    lesson("json & re", "Data formats and text patterns", LessonIcon.FILE) {
        text("**JSON** is the standard format for exchanging data – for example between apps and websites. With the `json` module, Python turns dictionaries and lists into text and back:")
        code("""
            import json

            profile = {"name": "Monty", "level": 7, "items": ["hat", "glasses"]}
            text = json.dumps(profile)
            print(text)
            back = json.loads(text)
            print(back["items"][0])
        """, output = """
            {"name": "Monty", "level": 7, "items": ["hat", "glasses"]}
            hat
        """)
        tip("With `json.dumps(data, indent=2)` the text is nicely indented and easy to read.")
        text("**Regular expressions** (module `re`) describe text patterns. With them you can find, for example, all numbers or email addresses in a text:")
        code("""
            import re

            text = "Order 4711 from 12.03. for 3 items"
            print(re.findall(r"\d+", text))
            print(re.sub(r"\d", "#", "PIN: 1234"))
            print(bool(re.fullmatch(r"[A-Z][a-z]+", "Monty")))
        """, output = """
            ['4711', '12', '03', '3']
            PIN: ####
            True
        """)
        text("""
            Important building blocks:
            - `\d` a digit, `\w` a word character, `\s` a whitespace character
            - `+` once or more, `*` any number of times, `?` optional
            - `[a-z]` a character from the range, `.` any character
        """)
        warn("Always write patterns as **raw strings** (`r\"...\"`) so that Python doesn't interpret the backslashes itself.")

        output("""
            import json
            print(json.loads('{"a": 1}')["a"])
        """, "1", "a", "{'a': 1}", "Error")
        output("""
            import json
            print(json.dumps([1, "two"]))
        """, "[1, \"two\"]", "[1, 'two']", "1 two", "Error")
        typeOutput("""
            import re
            print(re.findall(r"\d+", "a1b22c333"))
        """, "['1', '22', '333']")
        choice("What does `\\d` stand for in a regular expression?", "A digit", "A letter", "A dot", "The end of the text")
        truth("`json.dumps()` turns Python data into JSON text.", true)
        fill("Replace all spaces with underscores.", """
            import re
            print(re.??(r" ", "_", "my new game"))
        """, answers = listOf("sub"), distractors = listOf("findall", "match", "replace"), output = "my_new_game")
        match("json.dumps" to "data → text", "json.loads" to "text → data", "re.findall" to "find all matches", "re.sub" to "replace matches")
        write("Use `re.findall` to find all words that start with a **capital letter** and print the list.",
            starter = "text = \"Yesterday Ada learned Python with Alan in Berlin\"",
            solution = """
                import re
                text = "Yesterday Ada learned Python with Alan in Berlin"
                print(re.findall(r"[A-Z]\w*", text))
            """,
            output = "['Yesterday', 'Ada', 'Python', 'Alan', 'Berlin']",
            mustContain = listOf("re.findall"),
            hint = "The pattern `r\"[A-Z]\\w*\"` means: one capital letter, then any number of word characters.")
    }

    checkpoint()
}
