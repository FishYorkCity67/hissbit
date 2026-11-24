package app.pyquest.content.en

import app.pyquest.content.CourseBuilder
import app.pyquest.data.LessonIcon

fun CourseBuilder.enUnit10() = unit("Pythonic Tools", "Comprehensions, zip, any/all and generators") {
    lesson("List Comprehensions", "Building lists in one line", LessonIcon.MAGIC) {
        text("A **list comprehension** creates a new list in **a single line**. Instead of this …")
        code("""
            squares = []
            for x in range(1, 6):
                squares.append(x * x)
            print(squares)
        """, output = "[1, 4, 9, 16, 25]")
        text("… you simply write:")
        code("""
            squares = [x * x for x in range(1, 6)]
            print(squares)
        """, output = "[1, 4, 9, 16, 25]")
        text("The structure is `[expression for element in collection]`. With an `if` at the end you can **filter**:")
        code("""
            numbers = [3, 8, 1, 12, 7, 6]
            even = [n for n in numbers if n % 2 == 0]
            print(even)
            names = ["ada", "alan", "grace"]
            print([n.capitalize() for n in names])
        """, output = """
            [8, 12, 6]
            ['Ada', 'Alan', 'Grace']
        """)
        text("An `if … else` inside the expression is possible too – then it goes at the **front**:")
        code("""print(["even" if n % 2 == 0 else "odd" for n in range(4)])""", output = "['even', 'odd', 'even', 'odd']")
        tip("Comprehensions are fast and easy to read – as long as they stay short. When it gets complicated, a normal loop is often easier to understand.")

        output("print([x * 2 for x in [1, 2, 3]])", "[2, 4, 6]", "[1, 2, 3, 1, 2, 3]", "[1, 2, 3]", "6")
        output("print([x for x in range(10) if x > 6])", "[7, 8, 9]", "[6, 7, 8, 9]", "[0, 1, 2, 3, 4, 5, 6]", "[]")
        typeOutput("print([len(w) for w in [\"Py\", \"Code\"]])", "[2, 4]")
        choice("Which comprehension gives the list `[0, 1, 4, 9]`?",
            "[x ** 2 for x in range(4)]", "[x * 2 for x in range(4)]", "[x ** 2 for x in range(1, 4)]", "[x for x in range(9)]", mono = true)
        truth("In a comprehension, a filtering `if` goes after the `for` clause.", true)
        fill("Create a list of all words in uppercase.", """
            words = ["red", "green"]
            upper_words = [w.??() ?? w in words]
            print(upper_words)
        """, answers = listOf("upper", "for"), distractors = listOf("lower", "in", "if"), output = "['RED', 'GREEN']")
        output("print([\"+\" if n > 0 else \"-\" for n in [3, -1, 2]])", "['+', '-', '+']", "['+', '+']", "['-']", "Error")
        write("Use a list comprehension to create a list of all numbers from 1 to 30 that are divisible by 3 **and** by 5, and print it.",
            solution = "print([n for n in range(1, 31) if n % 3 == 0 and n % 5 == 0])",
            output = "[15, 30]",
            mustContain = listOf("for", "if", "["),
            hint = "`[n for n in range(1, 31) if n % 3 == 0 and n % 5 == 0]`")
    }

    lesson("Dict & Set Comprehensions", "Dictionaries and sets in one line", LessonIcon.BOX) {
        text("Comprehensions also exist for dictionaries and sets – with curly braces:")
        code("""
            words = ["apple", "kiwi", "banana"]
            lengths = {w: len(w) for w in words}
            print(lengths)
            letters = {w[0] for w in words}
            print(sorted(letters))
        """, output = """
            {'apple': 5, 'kiwi': 4, 'banana': 6}
            ['a', 'b', 'k']
        """)
        text("That lets you rebuild dictionaries elegantly – for example filter them or swap keys and values:")
        code("""
            prices = {"bread": 2.5, "caviar": 49.9, "milk": 1.2}
            cheap = {k: v for k, v in prices.items() if v < 5}
            print(cheap)
            codes = {"red": 1, "green": 2}
            print({value: name for name, value in codes.items()})
        """, output = """
            {'bread': 2.5, 'milk': 1.2}
            {1: 'red', 2: 'green'}
        """)
        tip("By the way, round brackets don't create a “tuple comprehension” but a **generator** – more about that in the last lesson of this unit.")

        output("print({x: x * x for x in range(3)})", "{0: 0, 1: 1, 2: 4}", "[0, 1, 4]", "{0, 1, 4}", "Error")
        output("print(len({c for c in \"banana\"}))", "3", "6", "4", "Error",
            explain = "A set contains every letter only once: b, a, n – so 3.")
        typeOutput("""
            d = {"a": 1, "b": 2}
            print({v: k for k, v in d.items()})
        """, "{1: 'a', 2: 'b'}")
        truth("`{x for x in my_list}` creates a set.", true)
        choice("What does `{n: len(n) for n in [\"Ada\", \"Bo\"]}` create?", "{'Ada': 3, 'Bo': 2}", "[3, 2]", "{3, 2}", "{'Ada', 'Bo'}", mono = true)
        fill("Keep only the subjects with a grade better than 3.", """
            grades = {"Math": 2, "Art": 4, "Biology": 1}
            good = {subject: g for subject, g in grades.??() if g < 3}
            print(good)
        """, answers = listOf("items"), distractors = listOf("keys", "values", "get"), output = "{'Math': 2, 'Biology': 1}")
        output("print({w.lower() for w in [\"Py\", \"py\", \"PY\"]})", "{'py'}", "{'Py', 'py', 'PY'}", "['py', 'py', 'py']", "3")
        write("Use a dict comprehension to create a dictionary that maps every number from 1 to 5 to **three times** its value, and print it.",
            solution = "print({n: n * 3 for n in range(1, 6)})",
            output = "{1: 3, 2: 6, 3: 9, 4: 12, 5: 15}",
            mustContain = listOf("for", "{"),
            hint = "`{n: n * 3 for n in range(1, 6)}`")
    }

    lesson("zip, enumerate, any & all", "Handy helpers for loops", LessonIcon.LOOP) {
        text("Python has handy helpers for loops. `zip()` loops through **several** lists at the same time:")
        code("""
            names = ["Ada", "Alan", "Grace"]
            years = [1815, 1912, 1906]
            for name, year in zip(names, years):
                print(f"{name} was born in {year}")
        """, output = """
            Ada was born in 1815
            Alan was born in 1912
            Grace was born in 1906
        """)
        text("With `dict(zip(...))` you build a dictionary directly from two lists. `enumerate()` additionally gives you the index:")
        code("""
            subjects = ["Math", "Art"]
            grades = [2, 1]
            print(dict(zip(subjects, grades)))
            for i, subject in enumerate(subjects):
                print(i, subject)
        """, output = """
            {'Math': 2, 'Art': 1}
            0 Math
            1 Art
        """)
        text("`any()` checks whether **at least one** element is true, `all()` whether **all** are true:")
        code("""
            grades = [2, 1, 4, 3]
            print(any(g == 1 for g in grades))
            print(all(g <= 4 for g in grades))
            print(all(g < 4 for g in grades))
        """, output = """
            True
            True
            False
        """)
        tip("`zip()` stops as soon as the **shortest** list ends.")

        output("print(list(zip([1, 2], [\"a\", \"b\"])))", "[(1, 'a'), (2, 'b')]", "[1, 2, 'a', 'b']", "[(1, 2), ('a', 'b')]", "{1: 'a', 2: 'b'}")
        output("print(any([False, False, True]))", "True", "False", "[True]", "1")
        output("print(all([1, 2, 0]))", "False", "True", "0", "Error", explain = "`0` counts as false – so not all values are true.")
        typeOutput("print(dict(zip([\"x\", \"y\"], [10, 20])))", "{'x': 10, 'y': 20}")
        truth("`zip([1, 2, 3], [\"a\"])` gives only a single pair.", true, explain = "`zip` stops at the shortest list.")
        fill("Check whether **all** passwords have at least 8 characters.", """
            passwords = ["secret123", "python!", "abcdefgh"]
            print(??(len(p) >= 8 for p in passwords))
        """, answers = listOf("all"), distractors = listOf("any", "sum", "len"), output = "False",
            explain = "`\"python!\"` has only 7 characters – so **not all** are long enough. `any` would give `True`.")
        output("""
            a = [1, 2, 3]
            b = [10, 20, 30]
            print([x + y for x, y in zip(a, b)])
        """, "[11, 22, 33]", "[1, 2, 3, 10, 20, 30]", "[10, 40, 90]", "Error")
        write("Use `zip()` to print one line per product in the format `product: price €`.",
            starter = """
                products = ["bread", "milk", "cheese"]
                prices = [2.5, 1.2, 3.8]
            """,
            solution = """
                products = ["bread", "milk", "cheese"]
                prices = [2.5, 1.2, 3.8]
                for product, price in zip(products, prices):
                    print(f"{product}: {price} €")
            """,
            output = """
                bread: 2.5 €
                milk: 1.2 €
                cheese: 3.8 €
            """,
            mustContain = listOf("zip"),
            hint = "`for product, price in zip(products, prices):`")
    }

    lesson("Generators", "Producing values when you need them", LessonIcon.ROCKET) {
        text("A **generator** produces values **one at a time** – only when they're needed. That saves memory. A function becomes a generator when it uses `yield` instead of `return`:")
        code("""
            def count_to(n):
                i = 1
                while i <= n:
                    yield i
                    i += 1

            for number in count_to(3):
                print(number)
            print(list(count_to(5)))
        """, output = """
            1
            2
            3
            [1, 2, 3, 4, 5]
        """)
        text("At every `yield` the function pauses and hands out a value. Next time it continues exactly there. With `next()` you get the values one by one:")
        code("""
            def colors():
                yield "red"
                yield "green"
                yield "blue"

            g = colors()
            print(next(g))
            print(next(g))
        """, output = """
            red
            green
        """)
        text("**Generator expressions** look like list comprehensions, but with round brackets. Ideal for sums over large amounts of data:")
        code("print(sum(x * x for x in range(1, 1001)))", output = "333833500")
        tip("A generator can even produce **infinitely** many values – you simply take as many as you need.")

        output("""
            def g():
                yield 1
                yield 2

            print(list(g()))
        """, "[1, 2]", "1\n2", "[1]", "2")
        output("""
            def g():
                yield "a"
                yield "b"

            x = g()
            print(next(x), next(x))
        """, "a b", "a a", "b a", "Error")
        typeOutput("print(sum(n for n in range(5)))", "10")
        choice("Which keyword turns a function into a generator?", "yield", "return", "generate", "next", mono = true)
        truth("A generator calculates all values immediately when it's called.", false, explain = "Generators are “lazy”: they calculate each value only when it's requested.")
        fill("Complete the generator for even numbers.", """
            def even(limit):
                for n in range(0, limit, 2):
                    ?? n

            print(list(even(7)))
        """, answers = listOf("yield"), distractors = listOf("return", "print", "next"), output = "[0, 2, 4, 6]")
        output("""
            def squares():
                n = 1
                while True:
                    yield n * n
                    n += 1

            g = squares()
            print([next(g) for _ in range(4)])
        """, "[1, 4, 9, 16]", "Endless loop", "[1, 2, 3, 4]", "[0, 1, 4, 9]")
        write("Write a generator `countdown(n)` that counts down from `n` to `1`. Print `list(countdown(5))`.",
            solution = """
                def countdown(n):
                    while n > 0:
                        yield n
                        n -= 1

                print(list(countdown(5)))
            """,
            output = "[5, 4, 3, 2, 1]",
            mustContain = listOf("yield"),
            hint = "`while n > 0:` – inside it `yield n` and `n -= 1`.")
    }

    checkpoint()
}
