package app.pyquest.content.js.en

import app.pyquest.content.CourseBuilder
import app.pyquest.content.js.D
import app.pyquest.data.LessonIcon

private val TODO_CLASS = """
    class TodoList {
      constructor() {
        this.tasks = [];
        this.nextId = 1;
      }

      add(text) {
        const task = { id: this.nextId++, text, done: false };
        this.tasks.push(task);
        return task;
      }

      complete(id) {
        const task = this.tasks.find((t) => t.id === id);
        if (!task) throw new Error("No task with ID " + id);
        task.done = true;
      }

      pending() {
        return this.tasks.filter((t) => !t.done);
      }

      show() {
        for (const t of this.tasks) {
          console.log(`${D}{t.done ? "✅" : "⬜"} ${D}{t.id}. ${D}{t.text}`);
        }
      }
    }
""".trimIndent()

private val QUIZ_QUESTIONS = """
    const questions = [
      { question: "How many legs does a spider have?", answer: "8" },
      { question: "Which color do you get from mixing blue and yellow?", answer: "green" },
      { question: "What is the capital of France?", answer: "paris" },
    ];
""".trimIndent()

private val IS_CORRECT = """
    function isCorrect(input, correct) {
      return input.trim().toLowerCase() === correct.toLowerCase();
    }
""".trimIndent()

private val ACCOUNT = """
    class AccountError extends Error {
      constructor(message) {
        super(message);
        this.name = "AccountError";
      }
    }

    class Account {
      #balance = 0;

      constructor(owner) {
        this.owner = owner;
        this.transactions = [];
      }

      get balance() {
        return this.#balance;
      }

      deposit(amount) {
        if (amount <= 0) throw new AccountError("Amount must be positive");
        this.#balance += amount;
        this.transactions.push({ type: "Deposit", amount });
      }

      withdraw(amount) {
        if (amount > this.#balance) throw new AccountError("Insufficient funds");
        this.#balance -= amount;
        this.transactions.push({ type: "Withdrawal", amount: -amount });
      }

      toString() {
        return `Account of ${D}{this.owner}: ${D}{this.#balance.toFixed(2)} €`;
      }
    }
""".trimIndent()

fun CourseBuilder.jsEnUnit14() = unit("Final Projects", "Putting it all together: three real programs") {
    project("Project: To-do List", "A small task manager with classes and arrays") {
        heading("The Goal")
        text("We're building a **to-do list**: add tasks, tick them off and display them. Along the way you combine classes, arrays of objects, array methods and error handling.")
        code(TODO_CLASS + "\n\n" + """
            const list = new TodoList();
            list.add("Learn JavaScript");
            list.add("Feed Monty");
            list.add("Tidy up the room");
            list.complete(2);
            list.show();
            console.log("Still open:", list.pending().length);
        """.trimIndent(), output = """
            ⬜ 1. Learn JavaScript
            ✅ 2. Feed Monty
            ⬜ 3. Tidy up the room
            Still open: 2
        """)
        tip("Tap ↗ at the top to try out the finished list in the Playground and extend it. In the tasks below, you'll rebuild it piece by piece.")

        write("**Step 1:** Write the class `TodoList` with a constructor that creates an empty array `tasks` and `nextId = 1`, plus the method `add(text)`. It creates `{ id, text, done: false }`, increases `nextId`, stores the task and returns it.",
            starter = """
                class TodoList {
                  // Your code here
                }
            """,
            solution = """
                class TodoList {
                  constructor() {
                    this.tasks = [];
                    this.nextId = 1;
                  }
                  add(text) {
                    const task = { id: this.nextId++, text, done: false };
                    this.tasks.push(task);
                    return task;
                  }
                }
            """,
            test = """
                const l = new TodoList();
                const a = l.add("Shopping");
                const b = l.add("Cooking");
                if (a.id !== 1 || b.id !== 2) throw new Error("The IDs should start at 1 and count up");
                if (a.text !== "Shopping" || a.done !== false) throw new Error("A new task needs text and done: false");
                if (l.tasks.length !== 2) throw new Error("Both tasks should be in the tasks array");
                console.log("✅ Step 1 done!");
            """,
            hint = "`this.nextId++` returns the current ID and increases it afterwards. `{ id: ..., text, done: false }` – `text` is the shorthand for `text: text`.")
        write("**Step 2:** Add `complete(id)`: find the task with `find` and set `done` to `true`. If there is no task with that ID, throw an `Error` with the message `No task with ID <id>`.",
            starter = """
                class TodoList {
                  constructor() {
                    this.tasks = [];
                    this.nextId = 1;
                  }
                  add(text) {
                    const task = { id: this.nextId++, text, done: false };
                    this.tasks.push(task);
                    return task;
                  }
                  // complete(id) ...
                }
            """,
            solution = """
                class TodoList {
                  constructor() {
                    this.tasks = [];
                    this.nextId = 1;
                  }
                  add(text) {
                    const task = { id: this.nextId++, text, done: false };
                    this.tasks.push(task);
                    return task;
                  }
                  complete(id) {
                    const task = this.tasks.find((t) => t.id === id);
                    if (!task) throw new Error("No task with ID " + id);
                    task.done = true;
                  }
                }
            """,
            test = """
                const l = new TodoList();
                l.add("A");
                l.add("B");
                l.complete(2);
                if (l.tasks[1].done !== true || l.tasks[0].done !== false) throw new Error("Only task 2 should be done");
                let message = "";
                try {
                  l.complete(99);
                } catch (e) {
                  message = e.message;
                }
                if (message !== "No task with ID 99") throw new Error("ID 99 should throw an error \"No task with ID 99\"");
                console.log("✅ Step 2 done!");
            """,
            mustContain = listOf("find", "throw"),
            hint = "`const task = this.tasks.find((t) => t.id === id);` – if the result is `undefined`, throw the error.")
        write("**Step 3:** Write the methods `pending()` (returns all tasks that are **not** done, as an array) and `show()` (prints each task as `✅ 1. Text` or `⬜ 1. Text`). The test code creates three tasks, ticks one off and shows the list.",
            starter = """
                class TodoList {
                  constructor() {
                    this.tasks = [];
                    this.nextId = 1;
                  }
                  add(text) {
                    const task = { id: this.nextId++, text, done: false };
                    this.tasks.push(task);
                    return task;
                  }
                  complete(id) {
                    const task = this.tasks.find((t) => t.id === id);
                    if (!task) throw new Error("No task with ID " + id);
                    task.done = true;
                  }
                  // pending() and show() ...
                }
            """,
            solution = TODO_CLASS,
            test = """
                const list = new TodoList();
                list.add("Reading");
                list.add("Sports");
                list.add("Cooking");
                list.complete(1);
                list.show();
                console.log("Open:", list.pending().map((t) => t.text).join(", "));
            """,
            output = """
                ✅ 1. Reading
                ⬜ 2. Sports
                ⬜ 3. Cooking
                Open: Sports, Cooking
            """,
            mustContain = listOf("filter"),
            hint = "`pending()` returns `this.tasks.filter((t) => !t.done)`. In `show()`, the `?` operator helps: `t.done ? \"✅\" : \"⬜\"`.")
        choice("Why does `complete()` throw an error for an unknown ID instead of quietly doing nothing?",
            "So a typo in the ID is noticed right away instead of going unnoticed.",
            "Because `find` would otherwise cause an infinite loop.",
            "Because methods always have to throw an error.",
            "To make the program run faster.")
    }

    project("Project: Quiz Game", "Ask questions, check answers, count points") {
        heading("The Goal")
        text("We're programming a **quiz**: the program asks questions with `prompt()`, checks the answers (ignoring upper/lower case and spaces) and counts the points.")
        code(QUIZ_QUESTIONS + "\n\n" + IS_CORRECT + "\n\n" + """
            let points = 0;
            for (const { question, answer } of questions) {
              const input = prompt(question) ?? "";
              if (isCorrect(input, answer)) {
                console.log("Correct! 🎉");
                points++;
              } else {
                console.log(`Sorry, wrong – the answer is: ${D}{answer}`);
              }
            }
            console.log(`You scored ${D}{points} out of ${D}{questions.length} points.`);
        """.trimIndent(), runnable = false)
        tip("Open the quiz in the Playground with ↗ and play it – or add questions of your own!")

        write("**Step 1:** Write the function `isCorrect(input, correct)`. It returns `true` if the input – ignoring spaces at the edges and upper/lower case – matches the correct answer.",
            starter = """
                function isCorrect(input, correct) {
                  // Your code here
                }
            """,
            solution = IS_CORRECT,
            test = """
                if (isCorrect("Paris", "paris") !== true) throw new Error("Upper/lower case should not matter");
                if (isCorrect("  green ", "green") !== true) throw new Error("Spaces at the edges should not matter");
                if (isCorrect("Rome", "paris") !== false) throw new Error("Wrong answers should return false");
                if (isCorrect("8", "8") !== true) throw new Error("isCorrect(\"8\", \"8\") should be true");
                console.log("✅ Step 1 done!");
            """,
            mustContain = listOf("trim", "toLowerCase"),
            hint = "`input.trim().toLowerCase() === correct.toLowerCase()`")
        write("**Step 2:** Ask every question from `questions` with `prompt()`. For a correct answer print `Correct! 🎉`, otherwise `Sorry, wrong – the answer is: <answer>`. At the end: `You scored <points> out of 3 points.` The test uses the answers **8**, **Blue** and **Paris**.",
            starter = QUIZ_QUESTIONS + "\n\n" + IS_CORRECT + "\n\nlet points = 0;\n// Your quiz loop here",
            solution = QUIZ_QUESTIONS + "\n\n" + IS_CORRECT + "\n\n" + """
                let points = 0;
                for (const { question, answer } of questions) {
                  const input = prompt(question) ?? "";
                  if (isCorrect(input, answer)) {
                    console.log("Correct! 🎉");
                    points++;
                  } else {
                    console.log(`Sorry, wrong – the answer is: ${D}{answer}`);
                  }
                }
                console.log(`You scored ${D}{points} out of ${D}{questions.length} points.`);
            """.trimIndent(),
            stdin = listOf("8", "Blue", "Paris"),
            output = """
                Correct! 🎉
                Sorry, wrong – the answer is: green
                Correct! 🎉
                You scored 2 out of 3 points.
            """,
            mustContain = listOf("prompt", "isCorrect"),
            hint = "`for (const { question, answer } of questions)` unpacks each question directly. Call `prompt(question)` and check it with `isCorrect`.")
        write("**Step 3:** Write a function `rating(points, max)` that returns a comment: `Perfect!` for a full score, `Well done!` from half upwards, otherwise `Keep practicing!`.",
            solution = """
                function rating(points, max) {
                  if (points === max) return "Perfect!";
                  if (points >= max / 2) return "Well done!";
                  return "Keep practicing!";
                }
            """,
            test = """
                if (rating(3, 3) !== "Perfect!") throw new Error("rating(3, 3) should be \"Perfect!\"");
                if (rating(2, 4) !== "Well done!") throw new Error("rating(2, 4) should be \"Well done!\"");
                if (rating(1, 3) !== "Keep practicing!") throw new Error("rating(1, 3) should be \"Keep practicing!\"");
                if (rating(0, 5) !== "Keep practicing!") throw new Error("rating(0, 5) should be \"Keep practicing!\"");
                console.log("✅ Step 3 done!");
            """,
            hint = "First check for the full score, then `points >= max / 2`.")
        choice("What is `?? \"\"` for in `prompt(question) ?? \"\"`?",
            "If someone cancels the input, an empty text is used instead of `null`.",
            "It removes spaces from the answer.",
            "It repeats the question.",
            "It makes the answer lowercase.")
    }

    project("Project: Bank Account", "Private data, error classes and transactions") {
        heading("The Goal")
        text("Finally, a **bank account**: the balance lives in a private field, deposits and withdrawals are checked, every transaction is recorded – and errors get a class of their own.")
        code(ACCOUNT + "\n\n" + """
            const account = new Account("Ada");
            account.deposit(100);
            account.withdraw(30);
            try {
              account.withdraw(500);
            } catch (e) {
              console.log(`${D}{e.name}: ${D}{e.message}`);
            }
            console.log(`${D}{account}`);
            console.log(account.transactions.length, "transactions");
        """.trimIndent(), output = """
            AccountError: Insufficient funds
            Account of Ada: 70.00 €
            2 transactions
        """)
        tip("The balance `#balance` is private: nobody can change it from outside – only through `deposit()` and `withdraw()`.")

        write("**Step 1:** Write the error class `AccountError extends Error`, which passes the message on to `super` in its constructor and sets `this.name = \"AccountError\"`.",
            solution = """
                class AccountError extends Error {
                  constructor(message) {
                    super(message);
                    this.name = "AccountError";
                  }
                }
            """,
            test = """
                const e = new AccountError("Test");
                if (!(e instanceof Error)) throw new Error("AccountError should extend Error");
                if (e.name !== "AccountError" || e.message !== "Test") throw new Error("name or message are wrong");
                console.log("✅ Step 1 done!");
            """,
            mustContain = listOf("extends Error"),
            hint = "Just like in the lesson on custom error classes: `super(message);` and then `this.name = \"AccountError\";`")
        write("**Step 2:** Write the class `Account` with a private field `#balance = 0`, a constructor `(owner)` with an empty array `transactions`, a getter `balance` and the method `deposit(amount)`. If `amount <= 0`, it throws `new AccountError(\"Amount must be positive\")`; otherwise it increases the balance and stores `{ type: \"Deposit\", amount }`.",
            starter = """
                class AccountError extends Error {
                  constructor(message) {
                    super(message);
                    this.name = "AccountError";
                  }
                }

                // class Account ...
            """,
            solution = """
                class AccountError extends Error {
                  constructor(message) {
                    super(message);
                    this.name = "AccountError";
                  }
                }

                class Account {
                  #balance = 0;
                  constructor(owner) {
                    this.owner = owner;
                    this.transactions = [];
                  }
                  get balance() {
                    return this.#balance;
                  }
                  deposit(amount) {
                    if (amount <= 0) throw new AccountError("Amount must be positive");
                    this.#balance += amount;
                    this.transactions.push({ type: "Deposit", amount });
                  }
                }
            """,
            test = """
                const a = new Account("Ben");
                a.deposit(40);
                a.deposit(2.5);
                if (a.balance !== 42.5) throw new Error("After 40 + 2.5 the balance should be 42.5");
                if (a.owner !== "Ben") throw new Error("Store the owner");
                if (a.transactions.length !== 2 || a.transactions[0].type !== "Deposit") throw new Error("Every deposit should end up in transactions");
                let error = null;
                try {
                  a.deposit(-5);
                } catch (e) {
                  error = e;
                }
                if (!(error instanceof AccountError)) throw new Error("-5 should throw an AccountError");
                if (Object.keys(a).includes("balance")) throw new Error("The balance should live in the private field #balance");
                console.log("✅ Step 2 done!");
            """,
            mustContain = listOf("#balance", "get balance"),
            hint = "`#balance = 0;` sits directly in the class as a field. The getter: `get balance() { return this.#balance; }`")
        write("**Step 3:** Add `withdraw(amount)` (throws `AccountError(\"Insufficient funds\")` if `amount` is greater than the balance, and stores `{ type: \"Withdrawal\", amount: -amount }`) as well as `toString()`, which returns `Account of <owner>: <balance with 2 decimal places> €`.",
            starter = """
                class AccountError extends Error {
                  constructor(message) {
                    super(message);
                    this.name = "AccountError";
                  }
                }

                class Account {
                  #balance = 0;
                  constructor(owner) {
                    this.owner = owner;
                    this.transactions = [];
                  }
                  get balance() {
                    return this.#balance;
                  }
                  deposit(amount) {
                    if (amount <= 0) throw new AccountError("Amount must be positive");
                    this.#balance += amount;
                    this.transactions.push({ type: "Deposit", amount });
                  }
                  // withdraw(amount) and toString() ...
                }
            """,
            solution = ACCOUNT,
            test = """
                const a = new Account("Cleo");
                a.deposit(100);
                a.withdraw(25.5);
                if (a.balance !== 74.5) throw new Error("After 100 - 25.5 the balance should be 74.5");
                if (String(a) !== "Account of Cleo: 74.50 €") throw new Error("toString() returns: " + String(a));
                let message = "";
                try {
                  a.withdraw(1000);
                } catch (e) {
                  message = e.name + ": " + e.message;
                }
                if (message !== "AccountError: Insufficient funds") throw new Error("Withdrawing too much should report \"Insufficient funds\"");
                if (a.balance !== 74.5) throw new Error("A rejected withdrawal must not change the balance");
                if (a.transactions.at(-1).amount !== -25.5) throw new Error("Withdrawals should be stored with a negative amount");
                console.log("✅ Step 3 done!");
            """,
            mustContain = listOf("withdraw", "toString"),
            hint = "In `withdraw`, first check `if (amount > this.#balance)` – only then subtract. `toString()` uses `this.#balance.toFixed(2)`.")
        write("**Step 4:** Use the finished class: create an account for `Monty`, deposit `50` and `20`, withdraw `35`, then print one line `<type>: <amount>` for each transaction and finally the account itself (using a template string).",
            starter = ACCOUNT + "\n\n// Your code here",
            solution = ACCOUNT + "\n\n" + """
                const account = new Account("Monty");
                account.deposit(50);
                account.deposit(20);
                account.withdraw(35);
                for (const t of account.transactions) {
                  console.log(`${D}{t.type}: ${D}{t.amount}`);
                }
                console.log(`${D}{account}`);
            """.trimIndent(),
            output = """
                Deposit: 50
                Deposit: 20
                Withdrawal: -35
                Account of Monty: 35.00 €
            """,
            mustContain = listOf("new Account"),
            hint = "Three method calls, then `for (const t of account.transactions)` and finally ``console.log(`${D}{account}`)``.")
        choice("Why is `#balance` private instead of simply `this.balance`?",
            "So the balance can only be changed through deposit() and withdraw() – with all their checks.",
            "Because private fields are faster.",
            "Because JavaScript can't store numbers otherwise.",
            "So that `console.log` doesn't print the balance.")
    }

    checkpoint()
}
