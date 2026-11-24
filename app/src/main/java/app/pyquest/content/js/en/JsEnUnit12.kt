package app.pyquest.content.js.en

import app.pyquest.content.CourseBuilder
import app.pyquest.data.LessonIcon

fun CourseBuilder.jsEnUnit12() = unit("Asynchronous JavaScript", "Timers, Promises, async and await") {
    lesson("Timers", "setTimeout and setInterval", LessonIcon.STAR) {
        text("With `setTimeout(function, ms)` you run a function **later** – after the given number of milliseconds (1000 ms = 1 second). The program doesn't wait – it carries on right away!")
        code("""
            console.log("1. Order placed");
            setTimeout(() => {
              console.log("3. Pizza is here! 🍕");
            }, 2000);
            console.log("2. Set the table");
        """, output = """
            1. Order placed
            2. Set the table
            3. Pizza is here! 🍕
        """)
        text("That's **asynchronous**: JavaScript remembers the function and takes care of everything else first. Even with a wait time of 0, the timer only runs once the current code has finished.")
        code("""
            setTimeout(() => console.log("C"), 0);
            setTimeout(() => console.log("B"), 0);
            console.log("A");
        """, output = """
            A
            C
            B
        """)
        text("`setInterval` repeats a function at fixed intervals until you stop it with `clearInterval`:")
        code("""
            let n = 0;
            const clock = setInterval(() => {
              n++;
              console.log("Tick", n);
              if (n === 3) {
                clearInterval(clock);
                console.log("Stop!");
              }
            }, 500);
        """, output = """
            Tick 1
            Tick 2
            Tick 3
            Stop!
        """)
        tip("Timers with a shorter wait time run first. With equal wait times, the order in which they were started applies.")

        output("""
            setTimeout(() => console.log("later"), 100);
            console.log("now");
        """, "now\nlater", "later\nnow", "now", "later")
        output("""
            setTimeout(() => console.log("A"), 300);
            setTimeout(() => console.log("B"), 100);
            setTimeout(() => console.log("C"), 200);
        """, "B\nC\nA", "A\nB\nC", "C\nB\nA", "A\nC\nB")
        typeOutput("""
            console.log(1);
            setTimeout(() => console.log(2), 0);
            console.log(3);
        """, "1\n3\n2", explain = "Even a timer with 0 ms waits until the rest of the code has run.")
        choice("How many milliseconds are 3 seconds?", "3000", "300", "30", "3", mono = true)
        fill("Stop the timer after the second round.", """
            let rounds = 0;
            const t = setInterval(() => {
              rounds++;
              console.log("Round", rounds);
              if (rounds === 2) ??(t);
            }, 100);
        """, answers = listOf("clearInterval"), distractors = listOf("setTimeout", "stopInterval", "break"), output = "Round 1\nRound 2")
        truth("`setTimeout` pauses the program until the time is up.", false,
            explain = "That's exactly what it doesn't do: the program carries on immediately, and the function is called later.")
        write("Print `Go` immediately, `One` after 1 second and `Two` after 2 seconds. Use `setTimeout`.",
            solution = """
                console.log("Go");
                setTimeout(() => console.log("One"), 1000);
                setTimeout(() => console.log("Two"), 2000);
            """,
            output = """
                Go
                One
                Two
            """,
            mustContain = listOf("setTimeout"),
            hint = "Two `setTimeout` calls with 1000 and 2000 milliseconds.")
    }

    lesson("Promises", "A promise of a result to come", LessonIcon.MAGIC) {
        text("A **Promise** stands for a result that only arrives **later** – for example data from the internet. It is either **fulfilled** (`resolve`) or **rejected** (`reject`).")
        code("""
            const order = new Promise((resolve) => {
              setTimeout(() => resolve("🍕 Pizza Margherita"), 1000);
            });

            order.then((food) => {
              console.log("Delivered:", food);
            });
            console.log("Ordered – now waiting …");
        """, output = """
            Ordered – now waiting …
            Delivered: 🍕 Pizza Margherita
        """)
        text("With `.then()` you react to the result, with `.catch()` to an error. Every `.then()` returns a new Promise again – that's why you can **chain** them.")
        code("""
            function rollDie(number) {
              return new Promise((resolve, reject) => {
                setTimeout(() => {
                  if (number >= 1 && number <= 6) resolve(number);
                  else reject(new Error("Not a valid roll: " + number));
                }, 500);
              });
            }

            rollDie(4)
              .then((n) => n * 10)
              .then((points) => console.log("Points:", points));

            rollDie(9)
              .then((n) => console.log("Rolled:", n))
              .catch((e) => console.log("Error:", e.message));
        """, output = """
            Points: 40
            Error: Not a valid roll: 9
        """)
        tip("`Promise.resolve(value)` creates an already fulfilled Promise – handy for experimenting.")
        warn("A rejected Promise without a `.catch()` causes an error that ends the program.")

        output("""
            Promise.resolve(5).then((x) => console.log(x * 2));
            console.log("first");
        """, "first\n10", "10\nfirst", "10", "first")
        output("""
            Promise.resolve(1)
              .then((x) => x + 1)
              .then((x) => x * 3)
              .then((x) => console.log(x));
        """, "6", "4", "3", "1")
        typeOutput("""
            Promise.reject(new Error("no"))
              .then(() => console.log("yes"))
              .catch((e) => console.log(e.message));
        """, "no")
        choice("Which method do you use to react to a **rejected** Promise?", ".catch()", ".then()", ".error()", ".finally()", mono = true)
        fill("Fulfil the Promise with the value “done”.", """
            const p = new Promise((resolve, reject) => {
              ??("done");
            });
            p.then((x) => console.log(x));
        """, answers = listOf("resolve"), distractors = listOf("reject", "return", "then"), output = "done")
        truth("A `.then()` returns a new Promise again, so you can chain `.then()` calls.", true)
        write("""
            Write a function `wait(ms)` that returns a Promise which is fulfilled after `ms` milliseconds.
            Then use it like this: `wait(500).then(() => console.log("Done waiting"));`
        """.trimIndent(),
            solution = """
                function wait(ms) {
                  return new Promise((resolve) => setTimeout(resolve, ms));
                }
                wait(500).then(() => console.log("Done waiting"));
            """,
            output = "Done waiting",
            mustContain = listOf("new Promise", "setTimeout"),
            hint = "`return new Promise((resolve) => setTimeout(resolve, ms));`")
    }

    lesson("async & await", "Writing asynchronous code readably", LessonIcon.MAGIC) {
        text("With `async` and `await`, asynchronous code reads like normal code from top to bottom. `await` waits for a Promise – and that only works in functions marked `async`.")
        code("""
            const wait = (ms) => new Promise((r) => setTimeout(r, ms));

            async function morningRoutine() {
              console.log("⏰ Alarm rings");
              await wait(1000);
              console.log("🚿 Shower");
              await wait(1000);
              console.log("☕ Coffee");
            }

            morningRoutine();
        """, output = """
            ⏰ Alarm rings
            🚿 Shower
            ☕ Coffee
        """)
        text("`await` gives you the Promise's value directly. You catch errors the normal way with `try`/`catch`. An `async` function itself always returns a Promise.")
        code("""
            function loadUser(id) {
              return new Promise((resolve, reject) => {
                setTimeout(() => {
                  if (id === 1) resolve({ id: 1, name: "Ada" });
                  else reject(new Error("User " + id + " does not exist"));
                }, 300);
              });
            }

            async function show(id) {
              try {
                const user = await loadUser(id);
                console.log("Hello,", user.name);
              } catch (e) {
                console.log("Error:", e.message);
              }
            }

            async function main() {
              await show(1);
              await show(2);
            }
            main();
        """, output = """
            Hello, Ada
            Error: User 2 does not exist
        """)
        warn("`await` outside an `async` function is a **SyntaxError** here. So wrap your program in an `async function main() { ... }` and call `main()`.")
        tip("If you forget `await`, you get just the Promise instead of the result: `console.log(loadUser(1))` shows `Promise { <pending> }`.")

        output("""
            async function f() {
              return 42;
            }
            f().then((x) => console.log(x));
        """, "42", "Promise { 42 }", "undefined", "f")
        output("""
            const wait = (ms) => new Promise((r) => setTimeout(r, ms));
            async function a() {
              console.log("A1");
              await wait(100);
              console.log("A2");
            }
            a();
            console.log("B");
        """, "A1\nB\nA2", "A1\nA2\nB", "B\nA1\nA2", "A1\nB")
        typeOutput("""
            async function twice(x) {
              return x * 2;
            }
            async function main() {
              const result = await twice(21);
              console.log(result);
            }
            main();
        """, "42")
        choice("Where can you use `await`?", "In functions marked with `async`", "Anywhere in the program", "Only in loops", "Only in `then()`")
        fill("Wait for the result of the function.", """
            const getNumber = () => Promise.resolve(7);
            async function main() {
              const n = ?? getNumber();
              console.log(n + 1);
            }
            main();
        """, answers = listOf("await"), distractors = listOf("async", "wait", "then"), output = "8")
        output("""
            async function test() {
              try {
                await Promise.reject(new Error("gone"));
                console.log("ok");
              } catch (e) {
                console.log("Error:", e.message);
              }
            }
            test();
        """, "Error: gone", "ok", "ok\nError: gone", "gone")
        truth("An `async` function always returns a Promise.", true)
        write("""
            Write an `async` function `countdown(from)` that counts down from `from` to 1 and prints every number – with a 1-second pause between them.
            After that it should print `Go!`. Call `countdown(3)`. The function `wait(ms)` is already there.
        """.trimIndent(),
            starter = """
                const wait = (ms) => new Promise((r) => setTimeout(r, ms));
            """,
            solution = """
                const wait = (ms) => new Promise((r) => setTimeout(r, ms));

                async function countdown(from) {
                  for (let i = from; i >= 1; i--) {
                    console.log(i);
                    await wait(1000);
                  }
                  console.log("Go!");
                }
                countdown(3);
            """,
            output = """
                3
                2
                1
                Go!
            """,
            mustContain = listOf("async", "await"),
            hint = "A `for` loop in which every `console.log(i)` is followed by `await wait(1000);`.")
    }

    lesson("Multiple Promises", "Promise.all and friends", LessonIcon.ROCKET) {
        text("With `Promise.all([...])` you wait for **several** Promises at once. The result is an array with all the values – in the order of the Promises, not in the order in which they finished.")
        code("""
            const load = (what, ms) => new Promise((r) => setTimeout(() => r(what), ms));

            async function main() {
              const results = await Promise.all([
                load("Profile", 300),
                load("Photos", 100),
                load("Friends", 200),
              ]);
              console.log(results);
            }
            main();
        """, output = "[ 'Profile', 'Photos', 'Friends' ]")
        text("That saves time: one after another with `await` takes as long as all the waiting times **added up**, with `Promise.all` only as long as the **longest** one.")
        code("""
            const wait = (ms) => new Promise((r) => setTimeout(r, ms));

            async function oneByOne() {
              const start = Date.now();
              await wait(1000);
              await wait(1000);
              console.log("one by one:", Math.round((Date.now() - start) / 1000), "s");
            }
            async function together() {
              const start = Date.now();
              await Promise.all([wait(1000), wait(1000)]);
              console.log("together:", Math.round((Date.now() - start) / 1000), "s");
            }
            async function main() {
              await oneByOne();
              await together();
            }
            main();
        """, output = """
            one by one: 2 s
            together: 1 s
        """)
        text("If one of the Promises is rejected, `Promise.all` fails immediately. `Promise.race` returns the result of the **fastest** Promise.")
        code("""
            const load = (what, ms) => new Promise((r) => setTimeout(() => r(what), ms));
            Promise.race([load("Snail 🐌", 500), load("Hare 🐇", 100)])
              .then((winner) => console.log("Winner:", winner));
        """, output = "Winner: Hare 🐇")
        tip("`Promise.allSettled` waits for all of them – even the rejected ones – and tells you for each one whether it worked.")

        output("""
            Promise.all([Promise.resolve(1), Promise.resolve(2)])
              .then((values) => console.log(values));
        """, "[ 1, 2 ]", "3", "1\n2", "[ [ 1 ], [ 2 ] ]")
        output("""
            const load = (x, ms) => new Promise((r) => setTimeout(() => r(x), ms));
            Promise.all([load("A", 300), load("B", 100)]).then((e) => console.log(e.join("")));
        """, "AB", "BA", "A", "B", explain = "`Promise.all` keeps the order of the Promises – no matter which one finishes first.")
        typeOutput("""
            const load = (x, ms) => new Promise((r) => setTimeout(() => r(x), ms));
            Promise.race([load("red", 200), load("blue", 50)]).then((x) => console.log(x));
        """, "blue")
        choice("What happens when **one** Promise in `Promise.all` is rejected?", "Promise.all is rejected as well", "The Promise is simply skipped", "It waits forever", "It is retried automatically")
        fill("Wait for both downloads at the same time.", """
            const load = (x) => Promise.resolve(x);
            async function main() {
              const [a, b] = await Promise.??([load(1), load(2)]);
              console.log(a + b);
            }
            main();
        """, answers = listOf("all"), distractors = listOf("race", "then", "wait"), output = "3")
        truth("With `Promise.all` it takes as long as the sum of all waiting times.", false)
        write("""
            The function `price(product)` returns the price of a product as a Promise. Use `Promise.all` to load the prices
            of `"Bread"`, `"Milk"` and `"Cheese"` at the same time and print the sum with 2 decimal places.
        """.trimIndent(),
            starter = """
                const priceList = { Bread: 2.49, Milk: 1.19, Cheese: 3.99 };
                const price = (p) => new Promise((r) => setTimeout(() => r(priceList[p]), 200));
            """,
            solution = """
                const priceList = { Bread: 2.49, Milk: 1.19, Cheese: 3.99 };
                const price = (p) => new Promise((r) => setTimeout(() => r(priceList[p]), 200));

                async function main() {
                  const prices = await Promise.all([price("Bread"), price("Milk"), price("Cheese")]);
                  const total = prices.reduce((s, p) => s + p, 0);
                  console.log(total.toFixed(2));
                }
                main();
            """,
            output = "7.67",
            mustContain = listOf("Promise.all"),
            hint = "`await Promise.all([price(\"Bread\"), price(\"Milk\"), price(\"Cheese\")])` gives you an array with three prices.")
    }

    checkpoint()
}
