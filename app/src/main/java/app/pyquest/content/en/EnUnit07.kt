package app.pyquest.content.en

import app.pyquest.content.CourseBuilder
import app.pyquest.data.LessonIcon

fun CourseBuilder.enUnit07() = unit("Tuples, Dicts & Sets", "Organizing data cleverly") {
    lesson("Tuples", "Groups of values that can't change", LessonIcon.BOX) {
        text("A **tuple** is like a list – but **immutable**. You write it with round brackets:")
        code("""
            point = (3, 4)
            color = (255, 128, 0)
            print(point[0])
            print(len(color))
        """, output = """
            3
            3
        """)
        text("Tuples are used for values that belong together and shouldn't change – for example coordinates or a date.")
        warn("You can't change a tuple: `point[0] = 5` causes a **TypeError**.")
        text("With **unpacking** you spread the values of a tuple across several variables:")
        code("""
            birthday = (24, 12, 2010)
            day, month, year = birthday
            print(f"Day: {day}, month: {month}, year: {year}")
        """, output = "Day: 24, month: 12, year: 2010")
        tip("A tuple with just one element needs a comma: `(5,)`. Without the comma, `(5)` is simply the number 5.")

        output("""
            t = (10, 20, 30)
            print(t[2])
        """, "30", "20", "(30)", "Error")
        choice("What happens with `t = (1, 2)` followed by `t[0] = 9`?",
            "A TypeError – tuples are immutable.", "`t` becomes (9, 2).", "`t` becomes (9, 1, 2).", "Nothing happens.")
        typeOutput("""
            x, y = (7, 3)
            print(x - y)
        """, "4")
        truth("`(5)` is a tuple with one element.", false, explain = "Without a comma, `(5)` is just the number 5 in brackets. A tuple would be `(5,)`.")
        output("""
            a, b, c = "xyz"
            print(b)
        """, "y", "x", "z", "Error", explain = "Unpacking works with any sequence – even with strings.")
        match("[1, 2]" to "list (mutable)", "(1, 2)" to "tuple (immutable)", "a, b = b, a" to "swap values", "(5,)" to "tuple with one element")
        fill("Unpack the coordinates into `x` and `y`.", """
            position = (12, 7)
            ??, ?? = position
            print(x * 10 + y)
        """, answers = listOf("x", "y"), distractors = listOf("position", "z"), output = "127")
        write("`divmod(a, b)` returns a tuple of integer division and remainder. Unpack `divmod(100, 7)` into the variables `whole` and `remainder` and print both.",
            solution = """
                whole, remainder = divmod(100, 7)
                print(whole, remainder)
            """,
            output = "14 2",
            mustContain = listOf("divmod"),
            hint = "`whole, remainder = divmod(100, 7)` – then `print(whole, remainder)`.")
    }

    lesson("Dictionaries", "Key-value pairs", LessonIcon.BOX) {
        text("A **dictionary** stores **key-value pairs**. Instead of using a position you access values by a **key** – like in a phone book:")
        code("""
            ages = {"Ada": 36, "Alan": 41, "Grace": 85}
            print(ages["Alan"])
            print(len(ages))
        """, output = """
            41
            3
        """)
        text("Adding and changing works with the same notation. With `del` you delete an entry:")
        code("""
            stock = {"apples": 10}
            stock["pears"] = 5
            stock["apples"] = 7
            del stock["pears"]
            print(stock)
        """, output = "{'apples': 7}")
        warn("If you access a key that doesn't exist, you get a **KeyError**. `get()` is safer – it returns `None` or a default value:")
        code("""
            colors = {"red": "#FF0000"}
            print(colors.get("blue"))
            print(colors.get("blue", "unknown"))
            print("red" in colors)
        """, output = """
            None
            unknown
            True
        """)
        tip("Keys must be unique. If you write the same key twice, the last value wins.")

        output("""
            d = {"a": 1, "b": 2}
            print(d["b"])
        """, "2", "1", "b", "Error")
        output("""
            d = {"x": 5}
            d["x"] = 9
            print(d)
        """, "{'x': 9}", "{'x': 5}", "{'x': 5, 'x': 9}", "Error")
        typeOutput("""
            d = {"dog": 4, "chicken": 2}
            print(d.get("fish", 0))
        """, "0")
        choice("What happens with `d = {}` followed by `print(d[\"x\"])`?", "A KeyError", "It prints `None`", "It prints `0`", "It prints `x`")
        truth("With `\"key\" in d` you check whether a key exists in the dictionary.", true)
        fill("Add Linus, aged 21.", """
            ages = {"Ada": 36}
            ages[??] = 21
            print(ages)
        """, answers = listOf("\"Linus\""), distractors = listOf("Linus", "21", "ages"), output = "{'Ada': 36, 'Linus': 21}")
        output("""
            d = {"a": 1, "a": 2}
            print(d)
        """, "{'a': 2}", "{'a': 1}", "{'a': 1, 'a': 2}", "Error", explain = "Keys are unique – the last value wins.")
        write("Create a dictionary `capital` with the entries Germany → Berlin, France → Paris and Italy → Rome. Then print the capital of France.",
            solution = """
                capital = {"Germany": "Berlin", "France": "Paris", "Italy": "Rome"}
                print(capital["France"])
            """,
            output = "Paris",
            mustContain = listOf("{", "capital"),
            hint = "`{\"Germany\": \"Berlin\", ...}` – access with `capital[\"France\"]`.")
    }

    lesson("Looping Through Dicts", "keys, values, items and counting", LessonIcon.LOOP) {
        text("""
            You can loop through a dictionary with a `for` loop. There are three variants:
            - `d.keys()` – only the keys
            - `d.values()` – only the values
            - `d.items()` – key **and** value as a pair
        """)
        code("""
            prices = {"bread": 2.5, "milk": 1.2, "cheese": 3.8}
            for item, price in prices.items():
                print(f"{item}: {price} €")
            print("Total:", sum(prices.values()))
        """, output = """
            bread: 2.5 €
            milk: 1.2 €
            cheese: 3.8 €
            Total: 7.5
        """)
        text("A classic: **counting** with a dictionary. For every element you increase the matching counter:")
        code("""
            text = "Mississippi"
            counter = {}
            for c in text:
                counter[c] = counter.get(c, 0) + 1
            print(counter)
        """, output = "{'M': 1, 'i': 4, 's': 4, 'p': 2}")
        tip("Dictionaries remember the order in which the keys were inserted.")

        output("""
            d = {"a": 1, "b": 2}
            for k in d:
                print(k)
        """, "a\nb", "1\n2", "a 1\nb 2", "('a', 1)")
        output("""
            d = {"x": 3, "y": 4}
            print(sum(d.values()))
        """, "7", "xy", "2", "Error")
        typeOutput("""
            d = {"Ada": 1, "Bob": 2}
            for name, nr in d.items():
                print(nr, name)
        """, "1 Ada\n2 Bob")
        match(".keys()" to "all keys", ".values()" to "all values", ".items()" to "key-value pairs", ".get(k, 0)" to "value or 0")
        truth("If you loop over a dictionary directly with `for k in d`, you get the keys.", true)
        fill("Print every name with its age.", """
            ages = {"Ada": 36, "Alan": 41}
            for name, years in ages.??():
                print(name, years)
        """, answers = listOf("items"), distractors = listOf("keys", "values", "get"), output = """
            Ada 36
            Alan 41
        """)
        output("""
            words = ["red", "blue", "red"]
            z = {}
            for w in words:
                z[w] = z.get(w, 0) + 1
            print(z["red"])
        """, "2", "1", "3", "Error")
        write("Count how often each number occurs in `rolls` and print the dictionary.",
            starter = "rolls = [6, 2, 6, 3, 2, 6]",
            solution = """
                rolls = [6, 2, 6, 3, 2, 6]
                counter = {}
                for r in rolls:
                    counter[r] = counter.get(r, 0) + 1
                print(counter)
            """,
            output = "{6: 3, 2: 2, 3: 1}",
            mustContain = listOf("for"),
            hint = "`counter[r] = counter.get(r, 0) + 1` inside a loop.")
    }

    lesson("Sets", "Collections without duplicates", LessonIcon.PUZZLE) {
        text("A **set** is an unordered collection **without duplicates**. Perfect for getting rid of duplicate values:")
        code("""
            numbers = [1, 2, 2, 3, 3, 3]
            unique = set(numbers)
            print(unique)
            print(len(unique))
        """, output = """
            {1, 2, 3}
            3
        """)
        text("You can combine sets like in mathematics:")
        code("""
            python = {"Ada", "Linus", "Grace"}
            java = {"Linus", "James"}
            print(python & java)
            print(sorted(python - java))
            print(len(python | java))
        """, output = """
            {'Linus'}
            ['Ada', 'Grace']
            4
        """)
        text("""
            - `a | b` union (everything)
            - `a & b` intersection (in both)
            - `a - b` difference (in a, but not in b)
            - `a ^ b` in exactly one of the two sets
        """)
        code("""
            s = {1, 2}
            s.add(3)
            s.add(2)
            s.discard(1)
            print(s)
            print(2 in s)
        """, output = """
            {2, 3}
            True
        """)
        warn("You create an empty set with `set()` – `{}` is an empty **dictionary**!")
        tip("The order in a set isn't fixed. If you need an order, turn it into a sorted list with `sorted()`.")

        output("print(len({1, 1, 2, 2, 3}))", "3", "5", "2", "Error")
        output("""
            a = {1, 2, 3}
            b = {2, 3, 4}
            print(a & b)
        """, "{2, 3}", "{1, 2, 3, 4}", "{1, 4}", "{1}")
        typeOutput("""
            a = {1, 2, 3}
            b = {3}
            print(a - b)
        """, "{1, 2}")
        choice("What creates an **empty set**?", "set()", "{}", "[]", "()", mono = true)
        truth("A set can contain the same value several times.", false)
        match("a | b" to "union", "a & b" to "intersection", "a - b" to "difference", "x in a" to "contained?")
        fill("Remove the duplicates from the list.", """
            names = ["Ada", "Ada", "Alan"]
            single = ??(names)
            print(len(single))
        """, answers = listOf("set"), distractors = listOf("list", "dict", "len"), output = "2")
        write("How many **different** letters does the word `\"Mississippi\"` have? Print the number.",
            solution = """print(len(set("Mississippi")))""",
            output = "4",
            mustContain = listOf("set"),
            hint = "`set(\"Mississippi\")` contains every letter only once. Uppercase and lowercase count separately.")
    }

    lesson("Combining Data", "Lists of dicts and more", LessonIcon.BRAIN) {
        text("It gets really powerful when you combine data structures – for example a **list of dictionaries**. That's how you represent tables:")
        code("""
            students = [
                {"name": "Ada", "grade": 1},
                {"name": "Linus", "grade": 3},
                {"name": "Grace", "grade": 2},
            ]
            for s in students:
                print(s["name"], "got a", s["grade"])
        """, output = """
            Ada got a 1
            Linus got a 3
            Grace got a 2
        """)
        text("Dictionaries with lists as values are handy too:")
        code("""
            timetable = {
                "Monday": ["Math", "English"],
                "Tuesday": ["Computer science", "Sports", "Art"],
            }
            print(timetable["Tuesday"][0])
            timetable["Monday"].append("Music")
            print(timetable["Monday"])
        """, output = """
            Computer science
            ['Math', 'English', 'Music']
        """)
        text("To sort data like this, you give `sorted()` a function with `key=` that says **what** to sort by:")
        code("""
            students = [{"name": "Ada", "grade": 1}, {"name": "Linus", "grade": 3}, {"name": "Grace", "grade": 2}]
            for s in sorted(students, key=lambda s: s["grade"]):
                print(s["name"])
        """, output = """
            Ada
            Grace
            Linus
        """)
        tip("`lambda s: s[\"grade\"]` is a mini function. You'll learn more about it in the unit on functions.")

        output("""
            d = {"colors": ["red", "blue"]}
            print(d["colors"][1])
        """, "blue", "red", "colors", "Error")
        output("""
            l = [{"a": 1}, {"a": 5}]
            print(l[1]["a"])
        """, "5", "1", "a", "Error")
        typeOutput("""
            teams = {"red": ["Ada", "Bob"], "blue": ["Cem"]}
            print(len(teams["red"]) + len(teams["blue"]))
        """, "3")
        truth("A dictionary can contain lists as values.", true)
        choice("How do you access the age in `people = [{\"name\": \"Ada\", \"age\": 36}]`?",
            "people[0][\"age\"]", "people[\"age\"][0]", "people.age", "people[0].age", mono = true)
        fill("Add the subject Music to Monday.", """
            plan = {"Monday": ["Math"]}
            plan["Monday"].??("Music")
            print(plan)
        """, answers = listOf("append"), distractors = listOf("add", "insert", "get"), output = "{'Monday': ['Math', 'Music']}")
        output("""
            movies = [{"title": "A", "year": 2010}, {"title": "B", "year": 1999}]
            oldest = sorted(movies, key=lambda m: m["year"])[0]
            print(oldest["title"])
        """, "B", "A", "1999", "Error")
        write("Calculate the **total price** of the `shopping` list (price × quantity for each item) and print it.",
            starter = """
                shopping = [
                    {"item": "bread", "price": 2.5, "quantity": 2},
                    {"item": "milk", "price": 1.0, "quantity": 3},
                    {"item": "cheese", "price": 4.0, "quantity": 1},
                ]
            """,
            solution = """
                shopping = [
                    {"item": "bread", "price": 2.5, "quantity": 2},
                    {"item": "milk", "price": 1.0, "quantity": 3},
                    {"item": "cheese", "price": 4.0, "quantity": 1},
                ]
                total = 0
                for e in shopping:
                    total += e["price"] * e["quantity"]
                print(total)
            """,
            output = "12.0",
            hint = "Loop through the list and add `e[\"price\"] * e[\"quantity\"]` each time.")
    }

    checkpoint()
}
