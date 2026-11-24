package app.pyquest.content.en

import app.pyquest.content.CourseBuilder
import app.pyquest.data.LessonIcon

fun CourseBuilder.enUnit06() = unit("Lists", "Storing, sorting and analyzing many values") {
    lesson("Creating Lists", "Several values in one variable", LessonIcon.BOX) {
        text("A **list** stores several values in a fixed order. You write it in square brackets, separated by commas:")
        code("""
            colors = ["red", "green", "blue"]
            numbers = [4, 8, 15, 16, 23, 42]
            empty = []
            print(colors)
            print(len(numbers))
        """, output = """
            ['red', 'green', 'blue']
            6
        """)
        text("You access elements by their **index**, just like with strings – counting starts at 0 here too:")
        code("""
            colors = ["red", "green", "blue"]
            print(colors[0])
            print(colors[-1])
        """, output = """
            red
            blue
        """)
        text("Unlike strings, lists are **mutable**: you can replace single elements.")
        code("""
            colors = ["red", "green", "blue"]
            colors[1] = "yellow"
            print(colors)
        """, output = "['red', 'yellow', 'blue']")
        tip("Lists may contain mixed types: `[1, \"two\", 3.0, True]` – even other lists.")

        output("""
            l = [10, 20, 30]
            print(l[1])
        """, "20", "10", "30", "[20]")
        output("""
            l = ["a", "b", "c"]
            print(l[-1])
        """, "c", "a", "b", "Error")
        typeOutput("print(len([1, 2, 3, 4]))", "4")
        output("""
            l = [1, 2, 3]
            l[0] = 9
            print(l)
        """, "[9, 2, 3]", "[1, 2, 3]", "[9, 1, 2, 3]", "Error")
        truth("Lists are mutable – you can replace single elements.", true)
        choice("How do you create an **empty** list?", "[]", "{}", "()", "list[]", mono = true,
            explain = "`{}` is an empty dictionary and `()` an empty tuple.")
        fill("Print the **second** element of the list.", """
            planets = ["Mercury", "Venus", "Earth"]
            print(planets[??])
        """, answers = listOf("1"), distractors = listOf("2", "0", "-1"), output = "Venus")
        write("Create a list `grades` with the numbers 2, 1, 3 and 2. Print the list and then its length.",
            solution = """
                grades = [2, 1, 3, 2]
                print(grades)
                print(len(grades))
            """,
            output = """
                [2, 1, 3, 2]
                4
            """,
            mustContain = listOf("["),
            hint = "`grades = [2, 1, 3, 2]`, then `print(grades)` and `print(len(grades))`.")
    }

    lesson("Changing Lists", "Adding and removing", LessonIcon.BOX) {
        text("Lists have handy methods for adding and removing:")
        code("""
            shopping = ["bread", "milk"]
            shopping.append("cheese")
            shopping.insert(0, "coffee")
            print(shopping)
        """, output = "['coffee', 'bread', 'milk', 'cheese']")
        text("""
            - `append(x)` adds x at the **end**
            - `insert(i, x)` inserts x at position i
            - `extend(list)` adds all elements of another list
        """)
        code("""
            shopping = ["coffee", "bread", "milk", "cheese"]
            shopping.remove("bread")
            last = shopping.pop()
            print(shopping)
            print("Removed:", last)
            del shopping[0]
            print(shopping)
        """, output = """
            ['coffee', 'milk']
            Removed: cheese
            ['milk']
        """)
        text("""
            - `remove(x)` deletes the first element with the value x
            - `pop()` removes the last element and returns it, `pop(i)` the one at position i
            - `del my_list[i]` deletes the element at position i
            - `clear()` empties the list completely
        """)
        warn("`remove(x)` raises a **ValueError** if x isn't in the list. Check first with `if x in my_list:`.")

        output("""
            l = [1, 2]
            l.append(3)
            print(l)
        """, "[1, 2, 3]", "[3, 1, 2]", "[1, 2]", "3")
        output("""
            l = ["a", "b", "c"]
            l.remove("b")
            print(l)
        """, "['a', 'c']", "['a', 'b']", "['b']", "Error")
        typeOutput("""
            l = [5, 6, 7]
            x = l.pop()
            print(x)
        """, "7")
        output("""
            l = [1, 2]
            l.insert(1, 9)
            print(l)
        """, "[1, 9, 2]", "[9, 1, 2]", "[1, 2, 9]", "[1, 9]")
        match(
            "append(x)" to "add at the end",
            "insert(i, x)" to "insert at a position",
            "remove(x)" to "remove a value",
            "pop()" to "take out the last one",
            "clear()" to "delete everything",
        )
        truth("If you pass a list to `append`, it adds the elements one by one.", false,
            explain = "`l.append([1, 2])` adds the list as **one** element. For several elements you use `extend`.")
        fill("Add `\"banana\"` at the end of the list.", """
            fruit = ["apple"]
            fruit.??("banana")
            print(fruit)
        """, answers = listOf("append"), distractors = listOf("add", "push", "insert"), output = "['apple', 'banana']")
        write("Add the number `4` to `stack`, then remove the last element **twice** with `pop()` and finally print the list.",
            starter = "stack = [1, 2, 3]",
            solution = """
                stack = [1, 2, 3]
                stack.append(4)
                stack.pop()
                stack.pop()
                print(stack)
            """,
            output = "[1, 2]",
            mustContain = listOf("append", "pop"),
            hint = "`stack.append(4)`, then `stack.pop()` twice and finally `print(stack)`.")
    }

    lesson("Sorting & Analyzing", "sorted, min, max, sum and in", LessonIcon.CALC) {
        text("Python can sort and analyze lists – with a single command:")
        code("""
            numbers = [42, 7, 19, 3]
            print(sorted(numbers))
            print(sorted(numbers, reverse=True))
            print(min(numbers), max(numbers), sum(numbers))
        """, output = """
            [3, 7, 19, 42]
            [42, 19, 7, 3]
            3 42 71
        """)
        text("`sorted()` returns a **new** sorted list. `my_list.sort()`, on the other hand, sorts the list itself:")
        code("""
            names = ["Zoe", "Ada", "Linus"]
            names.sort()
            print(names)
            names.reverse()
            print(names)
        """, output = """
            ['Ada', 'Linus', 'Zoe']
            ['Zoe', 'Linus', 'Ada']
        """)
        text("With `in` you check whether an element is included. `count()` counts it, `index()` finds its position:")
        code("""
            rolls = [3, 6, 2, 6, 1]
            print(6 in rolls)
            print(rolls.count(6))
            print(rolls.index(2))
        """, output = """
            True
            2
            2
        """)
        tip("You calculate the average with `sum(my_list) / len(my_list)`.")
        warn("`x = my_list.sort()` is a classic mistake: `sort()` returns `None` – so `x` contains nothing useful!")

        output("print(sorted([3, 1, 2]))", "[1, 2, 3]", "[3, 2, 1]", "[3, 1, 2]", "None")
        output("print(max([4, 9, 2]))", "9", "4", "2", "[9]")
        typeOutput("""
            grades = [1, 2, 3]
            print(sum(grades) / len(grades))
        """, "2.0")
        output("""
            l = [3, 1, 2]
            x = l.sort()
            print(x)
        """, "None", "[1, 2, 3]", "[3, 1, 2]", "Error", explain = "`sort()` sorts the list itself and returns `None`.")
        choice("Which command returns a **new** list sorted in descending order?", "sorted(l, reverse=True)", "l.sort()", "sorted(l)", "l.reverse()", mono = true)
        truth("`5 in [1, 5, 9]` gives `True`.", true)
        fill("Print the smallest and the largest value.", """
            temps = [12, -3, 25, 8]
            print(??(temps), ??(temps))
        """, answers = listOf("min", "max"), distractors = listOf("sum", "len"), output = "-3 25")
        write("Calculate the **average** of the points and print it rounded to one decimal place.",
            starter = "points = [17, 23, 11, 30, 19]",
            solution = """
                points = [17, 23, 11, 30, 19]
                print(round(sum(points) / len(points), 1))
            """,
            output = "20.0",
            hint = "Average = `sum(points) / len(points)`. Round with `round(value, 1)`.")
    }

    lesson("Lists & Loops", "Looping through and building new lists", LessonIcon.LOOP) {
        text("With `for` you loop through all elements of a list. If you also need the **position**, `enumerate()` helps:")
        code("""
            tasks = ["Tidy my room", "Learn Python", "Sports"]
            for number, task in enumerate(tasks, start=1):
                print(f"{number}. {task}")
        """, output = """
            1. Tidy my room
            2. Learn Python
            3. Sports
        """)
        text("An important pattern: **building a new list**. You start with an empty list and call `append` inside the loop:")
        code("""
            numbers = [1, 2, 3, 4, 5, 6]
            even = []
            for n in numbers:
                if n % 2 == 0:
                    even.append(n)
            print(even)
        """, output = "[2, 4, 6]")
        code("""
            prices = [10, 25, 4]
            discounted = []
            for p in prices:
                discounted.append(p * 0.9)
            print(discounted)
        """, output = "[9.0, 22.5, 3.6]")
        tip("In the unit about comprehensions you'll learn how to build lists like this in **a single line**.")

        output("""
            for i, x in enumerate(["a", "b"]):
                print(i, x)
        """, "0 a\n1 b", "1 a\n2 b", "a 0\nb 1", "a\nb")
        typeOutput("""
            result = []
            for x in [1, 2, 3]:
                result.append(x * 10)
            print(result)
        """, "[10, 20, 30]")
        output("""
            words = ["dog", "elephant", "cat"]
            long_words = []
            for w in words:
                if len(w) > 4:
                    long_words.append(w)
            print(long_words)
        """, "['elephant']", "['dog', 'cat']", "['elephant', 'cat']", "[]")
        truth("`enumerate(my_list, start=1)` starts with the number 1.", true)
        arrange("Build a list of the square numbers from 1 to 4 and print it.", """
            squares = []
            for i in range(1, 5):
                squares.append(i * i)
            print(squares)
        """, output = "[1, 4, 9, 16]")
        fill("Number the players starting at 1.", """
            players = ["Ada", "Alan"]
            for nr, name in ??(players, start=1):
                print(nr, name)
        """, answers = listOf("enumerate"), distractors = listOf("range", "zip", "len"), output = """
            1 Ada
            2 Alan
        """)
        choice("What's the mistake in this code?",
            "The list is created anew in every round – at the end it only contains `3`.",
            "`append` doesn't exist.",
            "You can't use lists inside loops.",
            "There's no mistake.",
            code = """
                for x in [1, 2, 3]:
                    result = []
                    result.append(x)
                print(result)
            """)
        write("Create a new list `upper_names` from `names` that contains all names in **uppercase**, and print it.",
            starter = "names = [\"ada\", \"grace\", \"linus\"]",
            solution = """
                names = ["ada", "grace", "linus"]
                upper_names = []
                for n in names:
                    upper_names.append(n.upper())
                print(upper_names)
            """,
            output = "['ADA', 'GRACE', 'LINUS']",
            mustContain = listOf("append"),
            hint = "Start with `upper_names = []` and append `n.upper()` inside the loop.")
    }

    lesson("Slicing & Copies", "Sublists, copies and tables", LessonIcon.PUZZLE) {
        text("Slicing works with lists exactly like with strings:")
        code("""
            l = [0, 1, 2, 3, 4, 5, 6]
            print(l[2:5])
            print(l[:3])
            print(l[-2:])
            print(l[::2])
            print(l[::-1])
        """, output = """
            [2, 3, 4]
            [0, 1, 2]
            [5, 6]
            [0, 2, 4, 6]
            [6, 5, 4, 3, 2, 1, 0]
        """)
        warn("Watch out, trap! `b = a` does **not** create a copy. Both names point to the same list:")
        code("""
            a = [1, 2, 3]
            b = a
            b.append(4)
            print(a)
        """, output = "[1, 2, 3, 4]")
        text("You get a real copy with `a.copy()` or `a[:]`:")
        code("""
            a = [1, 2, 3]
            b = a.copy()
            b.append(4)
            print(a, b)
        """, output = "[1, 2, 3] [1, 2, 3, 4]")
        text("Lists can contain other lists – for example for tables or game boards:")
        code("""
            board = [
                ["X", "O", "X"],
                [" ", "X", "O"],
                ["O", " ", "X"],
            ]
            print(board[1][2])
            for row in board:
                print(" | ".join(row))
        """, output = """
            O
            X | O | X
              | X | O
            O |   | X
        """)

        output("""
            l = [1, 2, 3, 4, 5]
            print(l[1:3])
        """, "[2, 3]", "[1, 2, 3]", "[2, 3, 4]", "[1, 2]")
        output("""
            a = [1]
            b = a
            b.append(2)
            print(a)
        """, "[1, 2]", "[1]", "[2]", "Error", explain = "`b = a` is not a copy – both names point to the same list.")
        typeOutput("""
            m = [[1, 2], [3, 4]]
            print(m[1][0])
        """, "3")
        truth("`copy = original.copy()` creates an independent copy of the list.", true)
        output("print([1, 2, 3][::-1])", "[3, 2, 1]", "[1, 2, 3]", "[3]", "Error")
        choice("How do you get the **last two** elements of a list `l`?", "l[-2:]", "l[:2]", "l[2:]", "l[-2]", mono = true)
        fill("Create a real copy so that the original stays unchanged.", """
            original = [1, 2, 3]
            duplicate = original.??()
            duplicate.append(4)
            print(original)
        """, answers = listOf("copy"), distractors = listOf("clone", "list", "new"), output = "[1, 2, 3]")
        write("Print **Linus's grade** from the table (second row, second column).",
            starter = """
                grades = [
                    ["Ada", 1],
                    ["Linus", 3],
                    ["Grace", 2],
                ]
            """,
            solution = """
                grades = [
                    ["Ada", 1],
                    ["Linus", 3],
                    ["Grace", 2],
                ]
                print(grades[1][1])
            """,
            output = "3",
            hint = "Second row = index 1, second column = index 1: `grades[1][1]`.")
    }

    checkpoint()
}
