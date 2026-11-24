package app.pyquest.content.en

import app.pyquest.content.CourseBuilder
import app.pyquest.data.LessonIcon

fun CourseBuilder.enUnit02() = unit("Variables & Data Types", "Storing values, understanding types and handling input") {
    lesson("Variables", "Storing values under a name", LessonIcon.BOX) {
        text("A **variable** is like a labelled box: you put a value in and can take it out again later using its name. You assign a value with `=`.")
        code("""
            name = "Monty"
            age = 3
            print(name)
            print(age)
        """, output = """
            Monty
            3
        """)
        text("You can give a variable a **new value** at any time – the old value is overwritten. The variable itself may even appear on the right of the `=`:")
        code("""
            points = 10
            points = points + 5
            print(points)
        """, output = "15")
        tip("In Python, `=` is **not** an equation like in math but a command: “Calculate the value on the right and store it under the name on the left.”")
        text("""
            Rules for variable names:
            - only letters, digits and `_`
            - must not start with a digit
            - no spaces
            - case matters: `age` and `Age` are different

            In Python, names are written in lowercase with underscores between words: `my_favorite_number` – this is called **snake_case**.
        """)
        warn("You can't use keywords like `if`, `for` or `class` as names.")

        output("""
            x = 5
            print(x)
        """, "5", "x", "\"5\"", "Error")
        output("""
            a = 3
            a = 7
            print(a)
        """, "7", "3", "10", "3\n7", explain = "The second assignment overwrites the old value.")
        choice("Which variable name is **valid**?", "my_name", "2name", "my name", "class", mono = true,
            explain = "Names can't start with a digit, can't contain spaces and can't be keywords.")
        truth("`Age` and `age` are the same variable in Python.", false, explain = "Python is case-sensitive.")
        fill("Store the number 42 in the variable `answer` and print it.", """
            answer ?? 42
            print(??)
        """, answers = listOf("=", "answer"), distractors = listOf("==", "\"answer\"", ":"), output = "42")
        typeOutput("""
            x = 4
            y = x + 3
            x = 10
            print(y)
        """, "7", explain = "`y` was calculated while `x` was still 4. Later changes to `x` don't change `y` anymore.")
        arrange("Create the variable `city` with the value `\"Berlin\"` and print it afterwards.", """
            city = "Berlin"
            print(city)
        """, distractors = listOf("print(\"city\")"), output = "Berlin")
        write("Store `3` in the variable `points`. Then increase `points` by `5` and print the new value.",
            solution = """
                points = 3
                points = points + 5
                print(points)
            """,
            output = "8",
            mustContain = listOf("points"),
            hint = "First `points = 3`, then `points = points + 5` and finally `print(points)`.")
    }

    lesson("Data Types", "Numbers, text and truth values", LessonIcon.BRAIN) {
        text("""
            Every value in Python has a **data type**. The four most important basic types:
            - `int` – whole numbers like `42` or `-7`
            - `float` – decimal numbers like `3.14`
            - `str` – text (string) like `"Hello"`
            - `bool` – truth values: `True` or `False`
        """)
        text("`type()` tells you which type a value has:")
        code("""
            print(type(42))
            print(type(3.14))
            print(type("Hi"))
            print(type(True))
        """, output = """
            <class 'int'>
            <class 'float'>
            <class 'str'>
            <class 'bool'>
        """)
        text("The type decides what you can do with a value. Numbers are added, strings are joined together:")
        code("""
            print(2 + 3)
            print("2" + "3")
        """, output = """
            5
            23
        """)
        warn("`\"5\"` (with quotes) is a **string**, `5` is a **number**. That's a big difference!")
        tip("`True` and `False` always start with a capital letter.")

        match("42" to "int", "3.14" to "float", "\"Hello\"" to "str", "True" to "bool")
        output("print(type(7.0))", "<class 'float'>", "<class 'int'>", "<class 'str'>", "7.0")
        output("print(\"4\" + \"4\")", "44", "8", "4 + 4", "Error", explain = "Both values are strings – `+` joins them together.")
        choice("Which value is a **string**?", "\"123\"", "123", "12.3", "True", mono = true)
        truth("`False` is a valid truth value in Python.", true)
        choice("What happens with `print(\"Age: \" + 14)`?",
            "A **TypeError** – you can't join text and a number with `+`.",
            "It prints `Age: 14`.",
            "It prints `Age: + 14`.",
            "Python calculates 14 + 0.")
        typeOutput("print(type(\"True\"))", "<class 'str'>", explain = "Because of the quotes this is a string, not a truth value.")
        write("Create three variables: `name` with some text, `age` with a whole number and `height` with a decimal number. Then print the type of `height` with `print(type(height))`.",
            solution = """
                name = "Ada"
                age = 14
                height = 1.65
                print(type(height))
            """,
            output = "<class 'float'>",
            mustContain = listOf("name", "age", "height"),
            hint = "Decimal numbers are written with a dot: `1.65`.")
    }

    lesson("Type Conversion", "Turning text into numbers – and back", LessonIcon.MAGIC) {
        text("Sometimes you need to **convert** a value into another type. There are functions with the same names as the types:")
        code("""
            number = int("42")
            print(number + 8)

            text = str(99)
            print(text + " balloons")

            decimal = float("2.5")
            print(decimal * 2)
        """, output = """
            50
            99 balloons
            5.0
        """)
        text("`int()` simply cuts off everything after the decimal point – it doesn't round! For rounding there's `round()`:")
        code("""
            print(int(3.99))
            print(round(3.99))
            print(round(3.14159, 2))
        """, output = """
            3
            4
            3.14
        """)
        warn("Not everything can be converted: `int(\"Hello\")` causes a **ValueError**.")
        tip("With `bool()` almost everything becomes `True` – except “empty” values like `0`, `\"\"` or `None`.")
        code("print(bool(0), bool(5), bool(\"\"), bool(\"Hi\"))", output = "False True False True")

        output("print(int(\"7\") + 3)", "10", "73", "\"73\"", "Error")
        output("print(str(7) + \"3\")", "73", "10", "7 3", "Error")
        output("print(int(9.8))", "9", "10", "9.8", "Error", explain = "`int()` cuts off, it doesn't round.")
        choice("Which conversion causes an **error**?", "int(\"twelve\")", "int(\"12\")", "float(\"1.5\")", "str(12)", mono = true,
            explain = "\"twelve\" isn't made of digits – Python raises a ValueError.")
        typeOutput("print(round(2.678, 1))", "2.7")
        fill("Convert the text into a number so you can calculate with it.", """
            entry = "20"
            print(??(entry) * 2)
        """, answers = listOf("int"), distractors = listOf("str", "text", "number"), output = "40")
        truth("`float(\"3\")` gives `3.0`.", true, explain = "`float()` turns the whole number into a decimal number.")
        write("The variable `price` contains the text `\"19\"`. Convert it into a number, add `1` and print the result.",
            starter = """price = "19"""",
            solution = """
                price = "19"
                print(int(price) + 1)
            """,
            output = "20",
            hint = "`int(price)` turns the text into a number you can calculate with.")
    }

    lesson("Input with input()", "Your program asks questions", LessonIcon.CHAT) {
        text("With `input()` your program can ask something. The text in the brackets is shown as the question – and you get the answer back as a **string**.")
        code("""
            name = input("What's your name? ")
            print("Hello", name)
        """, output = """
            What's your name? Monty
            Hello Monty
        """, stdin = listOf("Monty"))
        tip("In the **code playground** you can really try out `input()` – there you type the answer into the console yourself.")
        warn("`input()` **always returns a string** – even if someone types a number! To calculate, you have to convert it.")
        code("""
            age = int(input("How old are you? "))
            print("Next year you'll be", age + 1)
        """, output = """
            How old are you? 15
            Next year you'll be 16
        """, stdin = listOf("15"))
        text("In the coding exercises, the input is typed in automatically – it's always stated in the task.")

        choice("Which type does `input()` always return?", "str", "int", "float", "bool", mono = true)
        choice("Someone types the number **5** for `x = input()`. What does `x + x` give?", "\"55\"", "10", "An error", "\"5 + 5\"", mono = true,
            explain = "`x` is the string \"5\" – and strings are joined with `+`.")
        truth("To calculate with an input, you have to convert it, e.g. with `int()`.", true)
        fill("Read a number and print three times its value.", """
            number = ??(input())
            print(number * 3)
        """, answers = listOf("int"), distractors = listOf("str", "input", "print"))
        arrange("Ask for a favorite color and print it afterwards.", """
            color = input("Favorite color? ")
            print("You like", color)
        """)
        write("Read a name with `input()` and print `Hello, <name>!`. The test uses the input **Ada**.",
            stdin = listOf("Ada"),
            solution = """
                name = input("Name: ")
                print("Hello, " + name + "!")
            """,
            output = "Hello, Ada!",
            mustContain = listOf("input"),
            hint = "Join strings with `+`: `\"Hello, \" + name + \"!\"`.")
        write("Read two numbers and print their **sum**. The test uses the inputs **4** and **9**.",
            stdin = listOf("4", "9"),
            solution = """
                a = int(input())
                b = int(input())
                print(a + b)
            """,
            output = "13",
            mustContain = listOf("input", "int"),
            hint = "Convert both inputs with `int()` before adding them.")
    }

    lesson("Shortcuts", "+=, multiple assignment and swapping", LessonIcon.STAR) {
        text("You often want to increase a variable by something. Instead of `points = points + 10` there's the shorter `+=`:")
        code("""
            points = 0
            points += 10
            points += 5
            points -= 3
            print(points)
        """, output = "12")
        text("This works with all operators: `+=`, `-=`, `*=`, `/=`, `//=`, `%=` and `**=`.")
        text("You can also assign **several variables at once**:")
        code("""
            x, y, z = 1, 2, 3
            print(x, y, z)
            a = b = 0
            print(a, b)
        """, output = """
            1 2 3
            0 0
        """)
        tip("A popular Python trick: **swap** two variables – without a helper variable.")
        code("""
            left = "apple"
            right = "pear"
            left, right = right, left
            print(left, right)
        """, output = "pear apple")

        output("""
            x = 5
            x += 2
            print(x)
        """, "7", "5", "52", "2")
        output("""
            n = 10
            n *= 3
            n -= 5
            print(n)
        """, "25", "30", "15", "5")
        choice("What's a shortcut for `money = money - 2`?", "money -= 2", "money =- 2", "money - = 2", "money--", mono = true)
        typeOutput("""
            a, b = 3, 8
            a, b = b, a
            print(a, b)
        """, "8 3")
        truth("`x, y = 1, 2` sets `x` to 1 and `y` to 2.", true)
        fill("Double the value of `balance` using a shortcut.", """
            balance = 50
            balance ?? 2
            print(balance)
        """, answers = listOf("*="), distractors = listOf("+=", "**=", "=*"), output = "100")
        match("+=" to "increase", "-=" to "decrease", "*=" to "multiply", "/=" to "divide")
        write("Start with `level = 1`. Increase `level` three times by `1` using `+=` and then print `level`.",
            solution = """
                level = 1
                level += 1
                level += 1
                level += 1
                print(level)
            """,
            output = "4",
            mustContain = listOf("+="),
            hint = "Three times `level += 1` – and finally `print(level)`.")
    }

    checkpoint()
}
