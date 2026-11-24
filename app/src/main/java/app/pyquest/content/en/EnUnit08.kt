package app.pyquest.content.en

import app.pyquest.content.CourseBuilder
import app.pyquest.data.LessonIcon

fun CourseBuilder.enUnit08() = unit("Functions", "Writing and reusing your own building blocks") {
    lesson("Defining Functions", "def and parameters", LessonIcon.MAGIC) {
        text("A **function** is a named block of code you can call again and again – so you don't have to copy code. You define a function with `def`:")
        code("""
            def welcome():
                print("Hello!")
                print("Nice to have you here.")

            welcome()
            welcome()
        """, output = """
            Hello!
            Nice to have you here.
            Hello!
            Nice to have you here.
        """)
        text("Functions can have **parameters** – placeholders for values you pass in when calling. The values you pass are called **arguments**:")
        code("""
            def greet(name):
                print(f"Hello {name}!")

            greet("Ada")
            greet("Monty")
        """, output = """
            Hello Ada!
            Hello Monty!
        """)
        code("""
            def rectangle(width, height):
                print("Area:", width * height)

            rectangle(3, 4)
            rectangle(10, 2)
        """, output = """
            Area: 12
            Area: 20
        """)
        warn("A function has to be **defined** before you call it. And: the definition alone doesn't run anything – only the call with `()` does.")
        tip("Good function names describe what the function does: `calculate_price()`, `show_menu()`.")

        output("""
            def hello():
                print("Hi")

            hello()
            hello()
        """, "Hi\nHi", "Hi", "Nothing", "hello")
        output("""
            def f():
                print("A")

            print("B")
        """, "B", "A\nB", "B\nA", "A", explain = "The function is only defined, never called.")
        choice("Which keyword defines a function?", "def", "function", "fun", "define", mono = true)
        typeOutput("""
            def double(x):
                print(x * 2)

            double(21)
        """, "42")
        truth("In the call `greet(\"Ada\")`, `\"Ada\"` is the argument.", true)
        fill("Define the function `cheer` and call it.", """
            ?? cheer():
                print("Hooray!")

            ??()
        """, answers = listOf("def", "cheer"), distractors = listOf("fun", "print", "cheer:"), output = "Hooray!")
        arrange("Define a function that adds two numbers and prints the result, and call it with 2 and 3.", """
            def add(a, b):
                print(a + b)
            add(2, 3)
        """, output = "5")
        write("Write a function `square(number)` that prints the square of the number. Then call it with `7`.",
            solution = """
                def square(number):
                    print(number * number)

                square(7)
            """,
            output = "49",
            mustContain = listOf("def square"),
            hint = "`def square(number):` and, indented, `print(number * number)` – then `square(7)`.")
    }

    lesson("Return Values", "Results with return", LessonIcon.MAGIC) {
        text("So far our functions only printed something. It's much more useful when they **return a result** – with `return`. You can store the result or keep working with it:")
        code("""
            def square(x):
                return x * x

            result = square(5)
            print(result)
            print(square(3) + square(4))
        """, output = """
            25
            25
        """)
        text("`return` **ends** the function immediately. Code after it doesn't run anymore:")
        code("""
            def is_adult(age):
                if age >= 18:
                    return True
                return False

            print(is_adult(20))
            print(is_adult(15))
        """, output = """
            True
            False
        """)
        warn("**print or return?** `print()` shows something, `return` hands a value back to the caller. A function without `return` gives back `None`.")
        code("""
            def just_print(x):
                print(x * 2)

            value = just_print(4)
            print(value)
        """, output = """
            8
            None
        """)
        tip("A function can also return several values – as a tuple: `return minimum, maximum`.")

        output("""
            def f(x):
                return x + 1

            print(f(5))
        """, "6", "5", "None", "f(5)")
        output("""
            def f():
                print("A")

            x = f()
            print(x)
        """, "A\nNone", "None", "A", "A\nA")
        output("""
            def test():
                return 1
                print("Hello")

            print(test())
        """, "1", "Hello\n1", "1\nHello", "Hello", explain = "Nothing runs after `return`.")
        typeOutput("""
            def times(a, b):
                return a * b

            print(times(3, times(2, 2)))
        """, "12")
        choice("What does a function **without** `return` give back?", "None", "0", "False", "An error", mono = true)
        truth("`return` and `print` do the same thing.", false, explain = "`print` only displays. `return` hands back a value you can keep calculating with.")
        fill("The function should return the larger of the two values.", """
            def larger(a, b):
                if a > b:
                    ?? a
                return ??

            print(larger(4, 9))
        """, answers = listOf("return", "b"), distractors = listOf("print", "a", "yield"), output = "9")
        write("Write a function `gross(net)` that returns the price **including 19 % sales tax** – rounded to 2 decimal places. Then print `gross(100)` and `gross(9.99)`.",
            solution = """
                def gross(net):
                    return round(net * 1.19, 2)

                print(gross(100))
                print(gross(9.99))
            """,
            output = """
                119.0
                11.89
            """,
            mustContain = listOf("return"),
            hint = "`return round(net * 1.19, 2)`")
    }

    lesson("Parameter Pros", "Default values and keywords", LessonIcon.PUZZLE) {
        text("Parameters can have **default values**. If nothing is passed when calling, the default applies:")
        code("""
            def greet(name, text="Hello"):
                print(f"{text}, {name}!")

            greet("Ada")
            greet("Alan", "Hi")
        """, output = """
            Hello, Ada!
            Hi, Alan!
        """)
        text("With **keyword arguments** you say which parameter gets which value when calling. Then the order doesn't matter:")
        code("""
            def profile(name, age, city):
                print(f"{name} ({age}) from {city}")

            profile("Ada", 36, "London")
            profile(city="Berlin", name="Linus", age=21)
        """, output = """
            Ada (36) from London
            Linus (21) from Berlin
        """)
        warn("Parameters **with** a default value must come **after** those without: `def f(a, b=2)` is allowed, `def f(a=1, b)` isn't.")
        tip("Many built-in functions use keyword arguments, e.g. `print(..., sep=\"-\", end=\"\")` or `sorted(..., reverse=True)`.")

        output("""
            def f(x, y=10):
                return x + y

            print(f(5))
        """, "15", "5", "10", "Error")
        output("""
            def f(x, y=10):
                return x + y

            print(f(5, 1))
        """, "6", "15", "11", "Error")
        typeOutput("""
            def divide(a, b):
                return a // b

            print(divide(b=3, a=10))
        """, "3")
        choice("Which definition is **invalid**?", "def f(a=1, b):", "def f(a, b=2):", "def f(a, b):", "def f(a=1, b=2):", mono = true,
            explain = "Parameters without a default value can't come after parameters with a default value.")
        truth("With keyword arguments the order doesn't matter.", true)
        fill("Print `a-b-c` by using a keyword argument of `print`.", """
            print("a", "b", "c", ??="-")
        """, answers = listOf("sep"), distractors = listOf("end", "join", "split"), output = "a-b-c")
        output("""
            def power(base, exp=2):
                return base ** exp

            print(power(3), power(2, exp=5))
        """, "9 32", "6 10", "9 10", "Error")
        write("Write a function `coffee(size=\"medium\", milk=False)` that returns a text like `medium without milk` or `large with milk`. Print `coffee()` and `coffee(\"large\", milk=True)`.",
            solution = """
                def coffee(size="medium", milk=False):
                    if milk:
                        return f"{size} with milk"
                    return f"{size} without milk"

                print(coffee())
                print(coffee("large", milk=True))
            """,
            output = """
                medium without milk
                large with milk
            """,
            mustContain = listOf("def coffee"),
            hint = "Check with `if milk:` which text you return.")
    }

    lesson("*args & Scope", "Any number of arguments, local and global", LessonIcon.BOX) {
        text("With `*args` a function accepts **any number** of arguments. They end up in a tuple:")
        code("""
            def total(*numbers):
                result = 0
                for n in numbers:
                    result += n
                return result

            print(total(1, 2))
            print(total(1, 2, 3, 4, 5))
        """, output = """
            3
            15
        """)
        text("`**kwargs` collects any **keyword arguments** in a dictionary:")
        code("""
            def profile(**infos):
                for key, value in infos.items():
                    print(f"{key}: {value}")

            profile(name="Monty", species="Python", age=3)
        """, output = """
            name: Monty
            species: Python
            age: 3
        """)
        text("**Scope:** variables created inside a function are **local** – they only exist there. Variables outside are **global**.")
        code("""
            x = "global"

            def show():
                x = "local"
                print(x)

            show()
            print(x)
        """, output = """
            local
            global
        """)
        warn("If you want to **change** a global variable inside a function, you need `global` – otherwise you get an **UnboundLocalError**. Usually it's better to pass values in and return them.")
        code("""
            counter = 0

            def click():
                global counter
                counter += 1

            click()
            click()
            print(counter)
        """, output = "2")

        output("""
            def f(*args):
                return len(args)

            print(f(1, 2, 3))
        """, "3", "1", "6", "(1, 2, 3)")
        output("""
            def f(**kw):
                return kw["a"]

            print(f(a=5, b=7))
        """, "5", "7", "a", "Error")
        output("""
            x = 1

            def f():
                x = 2

            f()
            print(x)
        """, "1", "2", "None", "Error", explain = "The `x` inside the function is local and doesn't change the global `x`.")
        typeOutput("""
            def maximum(*values):
                return max(values)

            print(maximum(4, 17, 9))
        """, "17")
        truth("A variable created inside a function isn't available outside of it.", true)
        choice("What's inside `args` when you call `def f(*args)` with `f(\"a\", \"b\")`?", "('a', 'b')", "['a', 'b']", "\"ab\"", "{'a', 'b'}", mono = true)
        fill("The function should increase the global score.", """
            points = 0

            def bonus():
                ?? points
                points += 10

            bonus()
            print(points)
        """, answers = listOf("global"), distractors = listOf("local", "return", "self"), output = "10")
        write("Write a function `average(*numbers)` that returns the average of any number of values. Print `average(2, 4, 6)` and `average(1, 2)`.",
            solution = """
                def average(*numbers):
                    return sum(numbers) / len(numbers)

                print(average(2, 4, 6))
                print(average(1, 2))
            """,
            output = """
                4.0
                1.5
            """,
            mustContain = listOf("*numbers"),
            hint = "`sum(numbers) / len(numbers)` – `numbers` is a tuple.")
    }

    lesson("Lambda & Functions as Values", "Mini functions for sorted, map and filter", LessonIcon.STAR) {
        text("In Python, functions are **values** – you can store them in variables and pass them to other functions:")
        code("""
            def loud(text):
                return text.upper() + "!"

            f = loud
            print(f("hello"))
        """, output = "HELLO!")
        text("For small functions there's a shortcut: **lambda**. `lambda x: x * 2` is a function that doubles x – without a name and without `return`:")
        code("""
            double = lambda x: x * 2
            print(double(21))

            add = lambda a, b: a + b
            print(add(3, 4))
        """, output = """
            42
            7
        """)
        text("This gets really handy with `sorted()`, `max()`, `map()` and `filter()`:")
        code("""
            words = ["banana", "egg", "apple"]
            print(sorted(words, key=len))
            print(max(words, key=len))
            numbers = [1, 2, 3, 4, 5, 6]
            print(list(map(lambda x: x * 10, numbers)))
            print(list(filter(lambda x: x % 2 == 0, numbers)))
        """, output = """
            ['egg', 'apple', 'banana']
            banana
            [10, 20, 30, 40, 50, 60]
            [2, 4, 6]
        """)
        tip("`key=len` passes the function `len` itself – without brackets! With brackets you would call it right away.")

        output("""
            f = lambda x: x + 3
            print(f(4))
        """, "7", "4", "3", "lambda")
        output("print(sorted([\"ccc\", \"a\", \"bb\"], key=len))", "['a', 'bb', 'ccc']", "['ccc', 'bb', 'a']", "['a', 'ccc', 'bb']", "Error")
        typeOutput("print(list(map(lambda x: x * x, [1, 2, 3])))", "[1, 4, 9]")
        output("print(list(filter(lambda w: len(w) > 3, [\"egg\", \"kiwi\", \"melon\"])))", "['kiwi', 'melon']", "['egg']", "['melon']", "[]")
        choice("What's wrong with `sorted(my_list, key=len())`?",
            "`len` must not have brackets – you pass the function itself.", "`key` doesn't exist.", "`sorted` always needs `reverse`.", "Nothing.")
        truth("You can store a function in a variable.", true)
        fill("Sort the people by age and print the youngest one.", """
            people = [("Ada", 36), ("Alan", 41), ("Grace", 30)]
            youngest = sorted(people, key=?? p: p[1])
            print(youngest[0][0])
        """, answers = listOf("lambda"), distractors = listOf("def", "len", "return"), output = "Grace")
        write("Sort the list `words` **by its last letter** and print the sorted list.",
            starter = "words = [\"Python\", \"Code\", \"Bug\", \"Array\"]",
            solution = """
                words = ["Python", "Code", "Bug", "Array"]
                print(sorted(words, key=lambda w: w[-1]))
            """,
            output = "['Code', 'Bug', 'Python', 'Array']",
            mustContain = listOf("key="),
            hint = "`key=lambda w: w[-1]` sorts by the last character.")
    }

    lesson("Recursion", "Functions that call themselves", LessonIcon.LOOP) {
        text("A function may **call itself** – that's called **recursion**. Some problems can be solved very elegantly this way.")
        code("""
            def countdown(n):
                if n == 0:
                    print("Liftoff! 🚀")
                    return
                print(n)
                countdown(n - 1)

            countdown(3)
        """, output = """
            3
            2
            1
            Liftoff! 🚀
        """)
        text("Every recursion needs a **base case** that ends the chain – otherwise the function calls itself forever. A classic is the **factorial**: 5! = 5 · 4 · 3 · 2 · 1")
        code("""
            def factorial(n):
                if n <= 1:
                    return 1
                return n * factorial(n - 1)

            print(factorial(5))
            print(factorial(10))
        """, output = """
            120
            3628800
        """)
        warn("Without a base case you get a **RecursionError**: Python gives up after about 1000 nested calls.")
        tip("Think recursively: “How can I reduce the problem to a **smaller version** of the same problem?”")

        output("""
            def f(n):
                if n == 0:
                    return 0
                return n + f(n - 1)

            print(f(4))
        """, "10", "4", "0", "24")
        choice("What does every recursive function absolutely need?", "A base case that ends the recursion", "A loop", "A global counter", "At least two parameters")
        typeOutput("""
            def p(n):
                if n == 0:
                    return 1
                return 2 * p(n - 1)

            print(p(5))
        """, "32")
        truth("Without a base case, a recursion runs forever – until Python stops it with a RecursionError.", true)
        output("""
            def show(n):
                if n > 0:
                    show(n - 1)
                    print(n)

            show(3)
        """, "1\n2\n3", "3\n2\n1", "3", "0\n1\n2\n3", explain = "The recursive call comes first, then the `print` – that's why it prints from 1 upwards.")
        fill("Complete the base case.", """
            def total(n):
                if n == ??:
                    return 0
                return n + total(n - 1)

            print(total(5))
        """, answers = listOf("0"), distractors = listOf("1", "5", "n"), output = "15")
        arrange("Put together the recursive factorial function and print 4!.", """
            def fac(n):
                if n <= 1:
                    return 1
                return n * fac(n - 1)
            print(fac(4))
        """, output = "24")
        write("Write a **recursive** function `fib(n)` for the Fibonacci numbers: fib(0) = 0, fib(1) = 1, otherwise fib(n-1) + fib(n-2). Print `fib(10)`.",
            solution = """
                def fib(n):
                    if n < 2:
                        return n
                    return fib(n - 1) + fib(n - 2)

                print(fib(10))
            """,
            output = "55",
            mustContain = listOf("def fib"),
            hint = "Base case: `if n < 2: return n`. Otherwise `return fib(n - 1) + fib(n - 2)`.")
    }

    checkpoint()
}
