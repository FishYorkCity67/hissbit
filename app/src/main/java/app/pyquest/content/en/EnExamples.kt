package app.pyquest.content.en

import app.pyquest.content.TQ
import app.pyquest.data.ExampleProgram

private fun ex(category: String, title: String, description: String, code: String, input: List<String> = emptyList()) =
    ExampleProgram(title, description, code.trimIndent().trimEnd() + "\n", category, input)

/** Beispielprogramme für den Code-Spielplatz (Englisch). */
val EnExamplePrograms: List<ExampleProgram> = listOf(
    ex("Basics", "Hello World", "The classic first program.", """
        print("Hello, world! 👋")
        print("I'm coding with Python", 3, "🐍")
    """),
    ex("Basics", "Greeting", "Asks for your name and age.", """
        name = input("What's your name? ")
        age = int(input("How old are you? "))
        print(f"Hello {name}!")
        print(f"In 10 years you'll be {age + 10} years old.")
    """, listOf("Ada", "14")),
    ex("Basics", "Calculator", "Calculates with two numbers and an operator.", """
        print("Mini calculator 🧮")
        a = float(input("First number: "))
        op = input("Operator (+ - * /): ")
        b = float(input("Second number: "))

        if op == "+":
            result = a + b
        elif op == "-":
            result = a - b
        elif op == "*":
            result = a * b
        elif op == "/":
            if b == 0:
                result = "Error: division by 0!"
            else:
                result = a / b
        else:
            result = "Unknown operator"

        print("Result:", result)
    """, listOf("12", "*", "3.5")),
    ex("Basics", "Temperature Table", "Converting Celsius to Fahrenheit – nicely formatted.", """
        print("Celsius | Fahrenheit")
        print("--------+-----------")
        for celsius in range(-10, 41, 10):
            fahrenheit = celsius * 9 / 5 + 32
            print(f"{celsius:7} | {fahrenheit:9.1f}")
    """),
    ex("Games", "Number Guessing", "Guess Monty's secret number between 1 and 100.", """
        import random

        secret = random.randint(1, 100)
        attempts = 0
        print("I'm thinking of a number between 1 and 100.")

        while True:
            entry = input("Your guess: ")
            if not entry.isdigit():
                print("Please enter a whole number!")
                continue
            guess = int(entry)
            attempts += 1
            if guess < secret:
                print("Too low! ⬆️")
            elif guess > secret:
                print("Too high! ⬇️")
            else:
                print(f"Correct! 🎉 You needed {attempts} attempts.")
                break
    """, listOf("abc") + (1..100).map { it.toString() }),
    ex("Games", "Rock, Paper, Scissors", "Three rounds against Monty.", """
        import random

        options = ["rock", "paper", "scissors"]
        beats = {"scissors": "paper", "rock": "scissors", "paper": "rock"}
        your_points, monty_points = 0, 0

        for round_number in range(1, 4):
            pick = input(f"Round {round_number} – rock, paper or scissors? ").strip().lower()
            if pick not in options:
                print("Invalid – this round doesn't count.")
                continue
            monty = random.choice(options)
            print("Monty picks:", monty)
            if pick == monty:
                print("It's a tie!")
            elif beats[pick] == monty:
                print("You win the round! 🎉")
                your_points += 1
            else:
                print("Monty wins the round! 🐍")
                monty_points += 1

        print(f"Final score: You {your_points} : {monty_points} Monty")
    """, listOf("rock", "paper", "scissors")),
    ex("Games", "Dice Statistics", "Simulate 600 rolls and show them as a bar chart.", """
        import random
        from collections import Counter

        rolls = [random.randint(1, 6) for _ in range(600)]
        stats = Counter(rolls)

        print("600 rolls – how often each number came up:")
        for face in range(1, 7):
            count = stats[face]
            bar = "█" * (count // 5)
            print(f"{face}: {count:3} {bar}")
    """),
    ex("Games", "Python Quiz", "A little quiz with a score.", """
        questions = {
            "What's the name of the function for output?": "print",
            "Which data type stores True/False?": "bool",
            "Which character starts a comment?": "#",
        }
        points = 0
        for question, answer in questions.items():
            entry = input(question + " ")
            if entry.strip().lower() == answer:
                print("✅ Correct!")
                points += 1
            else:
                print(f"❌ Sorry, that's wrong – the right answer is: {answer}")
        print(f"You got {points} out of {len(questions)} points.")
    """, listOf("print", "int", "#")),
    ex("Games", "Text Adventure", "A mini story where you make the decisions.", """
        print("You're standing in front of a dark cave. 🕳️")
        way = input("Do you go 'left' or 'right'? ").strip().lower()
        if way == "left":
            print("You find a treasure chest! 💰")
            open_it = input("Open it? (yes/no) ").strip().lower()
            if open_it == "yes":
                print("It's full of Python books. The real treasure! 📚")
            else:
                print("You walk on and miss the treasure …")
        elif way == "right":
            print("A friendly Python dragon greets you. 🐉")
        else:
            print("You stand still and think about your life.")
    """, listOf("left", "yes")),
    ex("Math", "FizzBuzz", "The classic from job interviews.", """
        for number in range(1, 31):
            if number % 15 == 0:
                print("FizzBuzz")
            elif number % 3 == 0:
                print("Fizz")
            elif number % 5 == 0:
                print("Buzz")
            else:
                print(number)
    """),
    ex("Math", "Prime Numbers", "The Sieve of Eratosthenes finds all primes up to 100.", """
        limit = 100
        is_prime = [True] * (limit + 1)
        is_prime[0] = is_prime[1] = False

        for number in range(2, int(limit ** 0.5) + 1):
            if is_prime[number]:
                for multiple in range(number * number, limit + 1, number):
                    is_prime[multiple] = False

        primes = [n for n in range(limit + 1) if is_prime[n]]
        print(f"{len(primes)} primes up to {limit}:")
        print(primes)
    """),
    ex("Math", "Fibonacci", "The famous number sequence, with a generator.", """
        def fibonacci(n):
            a, b = 0, 1
            for _ in range(n):
                yield a
                a, b = b, a + b

        print(list(fibonacci(15)))
    """),
    ex("Math", "Collatz Sequence", "An unsolved puzzle of mathematics.", """
        number = 27
        steps = 0
        highest = number
        while number != 1:
            number = number // 2 if number % 2 == 0 else 3 * number + 1
            highest = max(highest, number)
            steps += 1
        print(f"The Collatz sequence of 27 reaches 1 after {steps} steps.")
        print(f"The highest value along the way was {highest}.")
    """),
    ex("Math", "Factorial (recursive)", "A function that calls itself.", """
        def factorial(n):
            if n <= 1:  # base case
                return 1
            return n * factorial(n - 1)  # recursive call

        for i in range(1, 11):
            print(f"{i}! = {factorial(i)}")
    """),
    ex("Math", "Towers of Hanoi", "A recursive brain teaser.", """
        def hanoi(n, start, target, helper):
            if n == 0:
                return 0
            moves = hanoi(n - 1, start, helper, target)
            print(f"Disk {n}: {start} → {target}")
            moves += 1
            moves += hanoi(n - 1, helper, target, start)
            return moves

        count = hanoi(3, "A", "C", "B")
        print(f"Done in {count} moves!")
    """),
    ex("Data", "Shopping List", "Creating, changing and sorting lists.", """
        shopping = ["apples", "bread", "milk"]
        shopping.append("cheese")
        shopping.insert(0, "coffee")
        shopping.remove("bread")

        print("My shopping list:")
        for number, item in enumerate(shopping, start=1):
            print(f"{number}. {item}")

        print("Count:", len(shopping))
        print("Alphabetical:", sorted(shopping))
    """),
    ex("Data", "Counting Words", "Which words come up most often?", """
        from collections import Counter

        text = ${TQ}Python is great. Python is simple.
        With Python you can program almost anything.${TQ}

        words = text.lower().replace(".", "").split()
        counter = Counter(words)

        print("The most common words:")
        for word, count in counter.most_common(3):
            print(f"{word}: {count}x")
    """),
    ex("Data", "Grade Calculator", "Calculating averages with lists and dictionaries.", """
        students = [
            {"name": "Ada", "scores": [95, 88, 92]},
            {"name": "Linus", "scores": [78, 85, 80]},
            {"name": "Grace", "scores": [90, 94, 97]},
        ]

        for s in students:
            average = sum(s["scores"]) / len(s["scores"])
            print(f"{s['name']:6} Average: {average:.2f}")

        best = max(students, key=lambda s: sum(s["scores"]) / len(s["scores"]))
        print("Best average:", best["name"])
    """),
    ex("Data", "JSON", "Turning data into text and back.", """
        import json

        data = {"name": "Monty", "age": 3, "favorite_food": ["mice", "bugs"]}
        text = json.dumps(data, indent=2, ensure_ascii=False)
        print(text)

        back = json.loads(text)
        print(back["favorite_food"][1])
    """),
    ex("Data", "Files", "Writing a file, adding to it and reading it again.", """
        with open("diary.txt", "w", encoding="utf-8") as file:
            file.write("Day 1: Started Python 🐍\n")

        with open("diary.txt", "a", encoding="utf-8") as file:
            file.write("Day 2: Learned loops 🔁\n")

        with open("diary.txt", encoding="utf-8") as file:
            for number, line in enumerate(file, start=1):
                print(number, line.strip())
    """),
    ex("Objects", "Pet Class", "Your very first class.", """
        class Pet:
            def __init__(self, name, species):
                self.name = name
                self.species = species
                self.hunger = 5

            def feed(self):
                self.hunger = max(0, self.hunger - 3)
                print(f"{self.name} has eaten. Hunger: {self.hunger}")

            def __str__(self):
                return f"{self.name} ({self.species})"

        monty = Pet("Monty", "python")
        print(monty)
        monty.feed()
        monty.feed()
    """),
    ex("Objects", "Bank Account", "A class with error checking.", """
        class Account:
            def __init__(self, owner, balance=0):
                self.owner = owner
                self.balance = balance

            def deposit(self, amount):
                if amount <= 0:
                    raise ValueError("Amount must be positive")
                self.balance += amount

            def withdraw(self, amount):
                if amount > self.balance:
                    raise ValueError("Not enough money in the account!")
                self.balance -= amount

        account = Account("Ada", 100)
        account.deposit(50)
        try:
            account.withdraw(500)
        except ValueError as error:
            print("Error:", error)
        print(f"Balance of {account.owner}: {account.balance} €")
    """),
    ex("Objects", "Dataclass: Points", "Classes with less typing, thanks to @dataclass.", """
        from dataclasses import dataclass
        import math

        @dataclass
        class Point:
            x: float
            y: float

            def distance(self, other):
                return math.dist((self.x, self.y), (other.x, other.y))

        a = Point(0, 0)
        b = Point(3, 4)
        print(a, b)
        print("Distance:", a.distance(b))
        print("Equal?", a == Point(0, 0))
    """),
    ex("Pro", "Comprehensions", "Building lists and dictionaries in one line.", """
        numbers = range(1, 11)
        squares = [n ** 2 for n in numbers]
        even = [n for n in numbers if n % 2 == 0]
        words = ["python", "is", "awesome"]
        lengths = {w: len(w) for w in words}

        print(squares)
        print(even)
        print(lengths)
    """),
    ex("Pro", "Generators", "Creating values only when you need them.", """
        def countdown(n):
            while n > 0:
                yield n
                n -= 1
            yield "Liftoff! 🚀"

        for value in countdown(5):
            print(value)

        total = sum(x * x for x in range(100_000))
        print("Sum of squares:", total)
    """),
    ex("Pro", "Decorator: Stopwatch", "Extending functions without changing them.", """
        import time
        from functools import wraps

        def stopwatch(function):
            @wraps(function)
            def wrapper(*args, **kwargs):
                start = time.perf_counter()
                result = function(*args, **kwargs)
                duration = (time.perf_counter() - start) * 1000
                print(f"{function.__name__} took {duration:.1f} ms")
                return result
            return wrapper

        @stopwatch
        def sum_to(n):
            return sum(range(n))

        print(sum_to(100_000))
    """),
    ex("Pro", "Regular Expressions", "Finding patterns in texts.", """
        import re

        text = "Write to monty@python.org or to ada@lovelace.dev!"
        emails = re.findall(r"[\w.]+@[\w.]+\w", text)
        print("Emails found:", emails)

        date = "Today is 10/18/2026"
        found = re.search(r"(\d{2})/(\d{2})/(\d{4})", date)
        if found:
            month, day, year = found.groups()
            print(f"Month {month}, day {day}, year {year}")
    """),
    ex("Pro", "match-case", "Structural pattern matching (Python 3.10 and newer).", """
        def describe(command):
            match command.split():
                case ["go", direction]:
                    return f"You go {direction}."
                case ["take", thing]:
                    return f"You take: {thing}"
                case ["help"]:
                    return "Commands: go <direction>, take <thing>"
                case _:
                    return "I don't understand that."

        for c in ["go north", "take sword", "help", "dance"]:
            print(describe(c))
    """),
    ex("Creative", "Mandelbrot in ASCII", "A fractal – made only of text characters.", """
        width, height = 40, 20
        chars = " .:-=+*#%@"

        for row in range(height):
            y = row / height * 2.4 - 1.2
            line = ""
            for column in range(width):
                x = column / width * 3.2 - 2.3
                z = 0
                c = complex(x, y)
                n = 0
                while abs(z) <= 2 and n < 30:
                    z = z * z + c
                    n += 1
                line += chars[n * (len(chars) - 1) // 30]
            print(line)
    """),
    ex("Creative", "Christmas Tree", "Drawing patterns with loops.", """
        height = 7
        for i in range(height):
            print(" " * (height - i - 1) + "*" * (2 * i + 1))
        print(" " * (height - 1) + "|")
    """),
    ex("Creative", "Times Tables", "A neatly formatted table.", """
        for a in range(1, 11):
            line = ""
            for b in range(1, 11):
                line += f"{a * b:4}"
            print(line)
    """),
    ex("Tools", "Date & Time", "Calculating with days and times.", """
        from datetime import date, datetime, timedelta

        today = date.today()
        print("Today:", today.strftime("%m/%d/%Y"))

        new_years_eve = date(today.year, 12, 31)
        print("Days until New Year's Eve:", (new_years_eve - today).days)

        now = datetime.now()
        print("Time:", now.strftime("%H:%M:%S"))
        print("In 100 days:", (today + timedelta(days=100)).strftime("%m/%d/%Y"))
    """),
    ex("Tools", "Password Generator", "Secure random passwords with secrets.", """
        import secrets
        import string

        chars = string.ascii_letters + string.digits + "!?#%&*"
        length = 16
        password = "".join(secrets.choice(chars) for _ in range(length))
        print("Your secure password:", password)
    """),
    ex("Tools", "Calendar", "The calendar module prints a month.", """
        import calendar

        calendar.setfirstweekday(calendar.MONDAY)
        print(calendar.month(2026, 12))
    """),
    ex("Tools", "Statistics", "Mean, median and more from the statistics module.", """
        import statistics

        points = [12, 15, 9, 20, 15, 18, 11]
        print("Mean:", round(statistics.mean(points), 2))
        print("Median:", statistics.median(points))
        print("Most common value:", statistics.mode(points))
        print("Standard deviation:", round(statistics.stdev(points), 2))
    """),
    ex("Tools", "Zen of Python", "Python's 19 guiding principles – built right in.", """
        import this
    """),
)
