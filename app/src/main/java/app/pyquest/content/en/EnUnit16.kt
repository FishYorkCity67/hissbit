package app.pyquest.content.en

import app.pyquest.content.CourseBuilder
import app.pyquest.content.TQ

private val ACCOUNT_BASE = """
    class AccountError(Exception):
        pass

    class Account:
        def __init__(self, owner, balance=0.0):
            self.owner = owner
            self.balance = balance
            self.transactions = []

        def deposit(self, amount):
            if amount <= 0:
                raise AccountError("Amount must be positive")
            self.balance += amount
            self.transactions.append(("Deposit", amount))

        def withdraw(self, amount):
            if amount > self.balance:
                raise AccountError("Insufficient funds")
            self.balance -= amount
            self.transactions.append(("Withdrawal", -amount))

        def __str__(self):
            return f"Account of {self.owner}: {self.balance:.2f} €"
""".trimIndent()

private val CHECK_GUESS = """
    def check_guess(guess, secret):
        if guess < secret:
            return "too low"
        if guess > secret:
            return "too high"
        return "correct"
""".trimIndent()

fun CourseBuilder.enUnit16() = unit("Final Projects", "Putting it all together: four real programs") {
    project("Project: Number Guessing", "A complete game, step by step") {
        heading("The Goal")
        text("Step by step, we'll build a **number guessing game**: the computer picks a number, you guess, and it tells you “too high” or “too low”. Along the way you combine everything you've learned: variables, loops, conditions, functions and input.")
        code("import random\n\n" + CHECK_GUESS + "\n\n" + """
            secret = random.randint(1, 100)
            attempts = 0
            while True:
                entry = input("Your guess: ")
                if not entry.isdigit():
                    print("Please enter a number!")
                    continue
                attempts += 1
                result = check_guess(int(entry), secret)
                print(result)
                if result == "correct":
                    print(f"Done in {attempts} attempts!")
                    break
        """.trimIndent(), runnable = false)
        tip("Tap ↗ above to play the finished game yourself in the playground! In the exercises, you'll now rebuild it piece by piece.")

        write("**Step 1:** Write the function `check_guess(guess, secret)`. It returns `\"too low\"`, `\"too high\"` or `\"correct\"`.",
            starter = """
                def check_guess(guess, secret):
                    # Your code here
                    pass
            """,
            solution = CHECK_GUESS,
            test = """
                assert check_guess(3, 7) == "too low"
                assert check_guess(9, 7) == "too high"
                assert check_guess(7, 7) == "correct"
                print("✅ Step 1 done!")
            """,
            hint = "Three cases: `if guess < secret: return \"too low\"`, then `too high`, otherwise `correct`.")
        write("**Step 2:** Write `is_valid(entry)`. It returns `True` if the entry is a number from 1 to 100 – otherwise `False`. Careful: the entry is a string!",
            starter = """
                def is_valid(entry):
                    pass
            """,
            solution = """
                def is_valid(entry):
                    if not entry.isdigit():
                        return False
                    return 1 <= int(entry) <= 100
            """,
            test = """
                assert is_valid("50")
                assert is_valid("1") and is_valid("100")
                assert not is_valid("0")
                assert not is_valid("101")
                assert not is_valid("abc")
                print("✅ Step 2 done!")
            """,
            hint = "Check with `isdigit()` first, then with `1 <= int(entry) <= 100`.")
        write("**Step 3:** The game loop. Here the secret number is always `42`. Keep reading guesses with `input()` until the number has been guessed. After every guess, print the result of `check_guess()`, and at the end `Done in X attempts!`. It's tested with the inputs **50**, **25** and **42**.",
            starter = CHECK_GUESS + "\n\nsecret = 42\n# Your game loop here",
            solution = CHECK_GUESS + "\n\n" + """
                secret = 42
                attempts = 0
                while True:
                    guess = int(input("Your guess: "))
                    attempts += 1
                    result = check_guess(guess, secret)
                    print(result)
                    if result == "correct":
                        print(f"Done in {attempts} attempts!")
                        break
            """.trimIndent(),
            stdin = listOf("50", "25", "42"),
            output = """
                too high
                too low
                correct
                Done in 3 attempts!
            """,
            mustContain = listOf("while", "input"),
            hint = "`while True:` – read a guess, `attempts += 1`, check it, print the result and leave with `break` when it's “correct”.")
        choice("Why is a separate function `check_guess()` better than writing everything directly into the loop?",
            "You can test it on its own, and the code is easier to follow.",
            "Because loops can't contain if statements.",
            "So that the game runs faster.",
            "There's no advantage.")
        truth("`random.randint(1, 100)` can also return the number 100.", true)
    }

    project("Project: To-do List", "Managing tasks with lists and dicts") {
        heading("The Goal")
        text("Now let's build a small **task manager**. We store the tasks as a list of dictionaries. Functions take care of adding, checking off and showing them.")
        code("""
            tasks = []

            def add(title):
                tasks.append({"title": title, "done": False})

            def check_off(number):
                tasks[number - 1]["done"] = True

            def show():
                for nr, t in enumerate(tasks, start=1):
                    mark = "✅" if t["done"] else "⬜"
                    print(f"{nr}. {mark} {t['title']}")

            add("Learn Python")
            add("Tidy up my room")
            add("Feed Monty")
            check_off(1)
            show()
        """, output = """
            1. ✅ Learn Python
            2. ⬜ Tidy up my room
            3. ⬜ Feed Monty
        """)
        tip("Later you could save the tasks to a file with `json` – then they're kept even after the program ends.")

        write("**Step 1:** Write `pending(tasks)`. It returns a list with the **titles** of all tasks that aren't done yet.",
            starter = """
                def pending(tasks):
                    pass
            """,
            solution = """
                def pending(tasks):
                    return [t["title"] for t in tasks if not t["done"]]
            """,
            test = """
                my_tasks = [
                    {"title": "A", "done": True},
                    {"title": "B", "done": False},
                    {"title": "C", "done": False},
                ]
                assert pending(my_tasks) == ["B", "C"]
                assert pending([]) == []
                print("✅ Step 1 done!")
            """,
            hint = "A list comprehension with `if not t[\"done\"]`.")
        write("**Step 2:** Write `progress(tasks)`. It returns the share of completed tasks in percent as a whole number – `0` for an empty list.",
            starter = """
                def progress(tasks):
                    pass
            """,
            solution = """
                def progress(tasks):
                    if not tasks:
                        return 0
                    done = sum(1 for t in tasks if t["done"])
                    return round(done / len(tasks) * 100)
            """,
            test = """
                my_tasks = [{"title": "A", "done": True}, {"title": "B", "done": False}]
                assert progress(my_tasks) == 50
                assert progress([]) == 0
                assert progress([{"title": "X", "done": True}]) == 100
                print("✅ Step 2 done!")
            """,
            hint = "Count the completed tasks and calculate `done / len(tasks) * 100`. Don't forget the empty list!")
        write("**Step 3:** Write `sorted_tasks(tasks)`. It returns the tasks sorted so that open tasks come before completed ones – and within each group alphabetically by title.",
            starter = """
                def sorted_tasks(tasks):
                    pass
            """,
            solution = """
                def sorted_tasks(tasks):
                    return sorted(tasks, key=lambda t: (t["done"], t["title"]))
            """,
            test = """
                my_tasks = [
                    {"title": "Walk the dog", "done": False},
                    {"title": "Dishes", "done": True},
                    {"title": "Buy milk", "done": False},
                ]
                assert [t["title"] for t in sorted_tasks(my_tasks)] == ["Buy milk", "Walk the dog", "Dishes"]
                print("✅ Step 3 done!")
            """,
            hint = "Sort with a tuple as the key: `key=lambda t: (t[\"done\"], t[\"title\"])` – `False` comes before `True`.")
        truth("A list of dictionaries is a good way to store many tasks together with their properties.", true)
        choice("How could you save the tasks permanently?", "With `json` in a file", "With `print()`", "With `random`", "Not at all")
    }

    project("Project: Text Analysis", "Counting words, estimating reading time") {
        heading("The Goal")
        text("We'll write a tool that analyzes texts: it counts words, finds the most common words and estimates the reading time. Features like these are built into search engines and writing apps.")
        code("""
            from collections import Counter

            text = ${TQ}Python is a programming language. Python is easy to learn
            and Python is fun. Many people learn Python as their first language.${TQ}

            words = [w.strip(".,!?").lower() for w in text.split()]
            print("Words:", len(words))
            print("Different:", len(set(words)))
            print("Top 3:", Counter(words).most_common(3))
        """, output = """
            Words: 22
            Different: 15
            Top 3: [('python', 4), ('is', 3), ('language', 2)]
        """)

        write("**Step 1:** Write `words(text)`. It returns a list of all the words in lowercase – without the punctuation marks `.,!?` at their edges.",
            starter = """
                def words(text):
                    pass
            """,
            solution = """
                def words(text):
                    return [w.strip(".,!?").lower() for w in text.split()]
            """,
            test = """
                assert words("Hello world!") == ["hello", "world"]
                assert words("Yes, no. Maybe?") == ["yes", "no", "maybe"]
                print("✅ Step 1 done!")
            """,
            hint = "`text.split()`, then clean up every word with `.strip(\".,!?\").lower()`.")
        write("**Step 2:** Write `reading_time(text)`. It returns the reading time in **minutes** (rounded up) – at 200 words per minute. Use `math.ceil`.",
            starter = """
                import math

                def reading_time(text):
                    pass
            """,
            solution = """
                import math

                def reading_time(text):
                    return math.ceil(len(text.split()) / 200)
            """,
            test = """
                assert reading_time("word " * 200) == 1
                assert reading_time("word " * 201) == 2
                assert reading_time("word " * 450) == 3
                print("✅ Step 2 done!")
            """,
            hint = "`math.ceil(len(text.split()) / 200)`")
        write("**Step 3:** Write `longest_word(text)`. It returns the longest word without punctuation – if there's a tie, the first one.",
            starter = """
                def longest_word(text):
                    pass
            """,
            solution = """
                def longest_word(text):
                    words = [w.strip(".,!?") for w in text.split()]
                    return max(words, key=len)
            """,
            test = """
                assert longest_word("I love programming!") == "programming"
                assert longest_word("ab cd") == "ab"
                print("✅ Step 3 done!")
            """,
            hint = "`max(words, key=len)` – if there's a tie, `max` automatically returns the first element.")
        output("""
            from collections import Counter
            print(Counter("a b a c a b".split()).most_common(2))
        """, "[('a', 3), ('b', 2)]", "[('a', 3)]", "['a', 'b']", "{'a': 3, 'b': 2}")
        truth("`set(words)` contains every word only once.", true)
    }

    project("Project: Bank Account", "Object-oriented, with its own error class") {
        heading("The Goal")
        text("To finish, an object-oriented project: a **bank account** with deposits, withdrawals, a statement and its own error class.")
        code(ACCOUNT_BASE + "\n\n" + """
            a = Account("Ada")
            a.deposit(100)
            a.withdraw(30)
            try:
                a.withdraw(500)
            except AccountError as e:
                print("Error:", e)
            print(a)
        """.trimIndent(), output = """
            Error: Insufficient funds
            Account of Ada: 70.00 €
        """)

        write("**Step 1:** Add a method `statement()` to the class `Account`. It returns all transactions as a list of strings in the format `Deposit: 100`.",
            starter = ACCOUNT_BASE,
            solution = ACCOUNT_BASE.replace(
                "    def __str__(self):",
                "    def statement(self):\n        return [f\"{kind}: {amount}\" for kind, amount in self.transactions]\n\n    def __str__(self):",
            ),
            test = """
                a = Account("Test")
                a.deposit(100)
                a.withdraw(30)
                assert a.statement() == ["Deposit: 100", "Withdrawal: -30"]
                print("✅ Step 1 done!")
            """,
            mustContain = listOf("def statement"),
            hint = "`return [f\"{kind}: {amount}\" for kind, amount in self.transactions]`")
        write("**Step 2:** Write a child class `SavingsAccount(Account)` with the extra attribute `interest_rate` (default `0.02`) and a method `add_interest()` that deposits `balance * interest_rate`.",
            starter = ACCOUNT_BASE,
            solution = ACCOUNT_BASE + """


class SavingsAccount(Account):
    def __init__(self, owner, balance=0.0, interest_rate=0.02):
        super().__init__(owner, balance)
        self.interest_rate = interest_rate

    def add_interest(self):
        self.deposit(self.balance * self.interest_rate)""",
            test = """
                s = SavingsAccount("Ada", 1000, 0.05)
                s.add_interest()
                assert round(s.balance, 2) == 1050.0
                assert isinstance(s, Account)
                print("✅ Step 2 done!")
            """,
            mustContain = listOf("class SavingsAccount(Account)", "super()"),
            hint = "In `__init__`, first call `super().__init__(owner, balance)`, then set `self.interest_rate`.")
        write("**Step 3:** Write a function `transfer(source, target, amount)` that moves money from one account to another. If the balance isn't enough, an `AccountError` should occur – and **neither** account may change.",
            starter = ACCOUNT_BASE,
            solution = ACCOUNT_BASE + """


def transfer(source, target, amount):
    source.withdraw(amount)
    target.deposit(amount)""",
            test = """
                a = Account("A", 100)
                b = Account("B", 0)
                transfer(a, b, 40)
                assert a.balance == 60 and b.balance == 40
                try:
                    transfer(a, b, 1000)
                    assert False, "An AccountError should have occurred"
                except AccountError:
                    pass
                assert a.balance == 60 and b.balance == 40
                print("✅ Step 3 done!")
            """,
            mustContain = listOf("def transfer"),
            hint = "Withdraw first – that raises the error if the balance is too low – then deposit.")
        choice("Why does the project have its own class `AccountError`?",
            "So you can catch exactly these errors on purpose.", "Because Python would crash otherwise.", "So that the account is faster.", "Classes always need their own errors.")
        truth("Thanks to inheritance, `SavingsAccount` automatically has the methods `deposit` and `withdraw` too.", true)
    }

    checkpoint("Final Exam", "Show that you know Python!")
}
