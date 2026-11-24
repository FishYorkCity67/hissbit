package app.pyquest.content.js.en

import app.pyquest.content.CourseBuilder
import app.pyquest.content.js.D
import app.pyquest.data.LessonIcon

fun CourseBuilder.jsEnUnit07() = unit("Functions", "Packaging code and reusing it") {
    lesson("Your Own Functions", "Giving code a name", LessonIcon.CODE) {
        text("A **function** is a piece of code with a name. You define it once with `function` and can then **call** it as often as you like.")
        code("""
            function greet() {
              console.log("Hello!");
              console.log("Nice to have you here.");
            }

            greet();
            greet();
        """, output = """
            Hello!
            Nice to have you here.
            Hello!
            Nice to have you here.
        """)
        text("Functions can have **parameters** – placeholders for values that you pass in when you call them. The values you pass in are called **arguments**.")
        code("""
            function greet(name, emoji) {
              console.log(`Hello ${D}{name} ${D}{emoji}`);
            }

            greet("Ada", "👋");
            greet("Monty", "🐍");
        """, output = """
            Hello Ada 👋
            Hello Monty 🐍
        """)
        tip("Without the round brackets, the function doesn't run: `greet` is the function itself – only `greet()` calls it.")
        warn("If you don't pass an argument, the parameter has the value `undefined`.")

        output("""
            function say(text) {
              console.log(text + "!");
            }
            say("Hey");
        """, "Hey!", "text!", "Hey", "Nothing")
        output("""
            function show() {
              console.log("A");
            }
            console.log("B");
            show();
        """, "B\nA", "A\nB", "A", "B", explain = "The definition alone doesn't print anything – only the call `show()` runs the code.")
        choice("How do you call the function `start`?", "start();", "start;", "call start;", "function start();", mono = true)
        typeOutput("""
            function repeat(word, times) {
              console.log(word.repeat(times));
            }
            repeat("ha", 3);
        """, "hahaha")
        fill("Define the function.", """
            ?? cheer() {
              console.log("Yay!");
            }
            cheer();
        """, answers = listOf("function"), distractors = listOf("def", "func", "fun"), output = "Yay!")
        truth("A function already runs when you define it.", false)
        output("""
            function info(name, age) {
              console.log(name, age);
            }
            info("Ada");
        """, "Ada undefined", "Ada", "Ada 0", "Error")
        write("Write a function `printSquare(number)` that prints the square of the number. Call it with `4` and with `9`.",
            solution = """
                function printSquare(number) {
                  console.log(number * number);
                }
                printSquare(4);
                printSquare(9);
            """,
            output = """
                16
                81
            """,
            mustContain = listOf("function printSquare"),
            hint = "`function printSquare(number) { console.log(number * number); }` – then two calls.")
    }

    lesson("Return Values", "Giving back results with return", LessonIcon.CODE) {
        text("With `return`, a function gives back a **result**. You can store the result, keep working with it or print it.")
        code("""
            function double(x) {
              return x * 2;
            }

            const result = double(21);
            console.log(result);
            console.log(double(5) + 1);
        """, output = """
            42
            11
        """)
        text("`return` ends the function immediately. Code after it doesn't run anymore. A function without `return` gives back `undefined`.")
        code("""
            function check(number) {
              if (number < 0) {
                return "negative";
              }
              return "not negative";
            }
            console.log(check(-3));
            console.log(check(8));
        """, output = """
            negative
            not negative
        """)
        text("Parameters can have **default values**. They apply when nothing is passed in.")
        code("""
            function welcome(name = "Guest") {
              return `Welcome, ${D}{name}!`;
            }
            console.log(welcome("Ada"));
            console.log(welcome());
        """, output = """
            Welcome, Ada!
            Welcome, Guest!
        """)
        tip("Rule of thumb: functions should rather **return** values than print them themselves. Then the rest of the program can decide what happens with the result.")

        output("""
            function plus(a, b) {
              return a + b;
            }
            console.log(plus(2, 3) * 10);
        """, "50", "23", "5", "32")
        output("""
            function test() {
              console.log("A");
            }
            const x = test();
            console.log(x);
        """, "A\nundefined", "A", "undefined", "A\nA")
        typeOutput("""
            function stop() {
              return "done";
              console.log("never");
            }
            console.log(stop());
        """, "done")
        choice("What does a function without `return` give back?", "undefined", "null", "0", "An error", mono = true)
        fill("Return the result from the function.", """
            function cube(n) {
              ?? n ** 3;
            }
            console.log(cube(2));
        """, answers = listOf("return"), distractors = listOf("console.log", "give", "yield"), output = "8")
        output("""
            function divide(a, b = 2) {
              return a / b;
            }
            console.log(divide(10), divide(10, 5));
        """, "5 2", "5 5", "2 5", "10 2")
        write("Write a function `isEven(n)` that returns `true` if `n` is even, otherwise `false`.",
            solution = """
                function isEven(n) {
                  return n % 2 === 0;
                }
            """,
            test = """
                if (isEven(4) !== true) throw new Error("isEven(4) should be true");
                if (isEven(7) !== false) throw new Error("isEven(7) should be false");
                if (isEven(0) !== true) throw new Error("isEven(0) should be true");
                console.log("✅ All tests passed!");
            """,
            hint = "The comparison `n % 2 === 0` already gives `true` or `false` – return it directly with `return`.")
        write("Write a function `discount(price, percent = 10)` that returns the price after the discount has been taken off. Without a percentage, 10 % applies.",
            solution = """
                function discount(price, percent = 10) {
                  return price - price * percent / 100;
                }
            """,
            test = """
                if (discount(100) !== 90) throw new Error("discount(100) should be 90");
                if (discount(80, 25) !== 60) throw new Error("discount(80, 25) should be 60");
                if (discount(50, 0) !== 50) throw new Error("discount(50, 0) should be 50");
                console.log("✅ All tests passed!");
            """,
            hint = "The discount is `price * percent / 100` – take it off the price.")
    }

    lesson("Arrow Functions", "Short functions with =>", LessonIcon.MAGIC) {
        text("In JavaScript, functions are **values** – you can store them in variables. **Arrow functions** make this especially short:")
        code("""
            const double = (x) => {
              return x * 2;
            };
            console.log(double(4));
        """, output = "8")
        text("If the body is just one expression, you can leave out the curly braces **and** `return`. With exactly one parameter, you can even leave out the round brackets.")
        code("""
            const plus = (a, b) => a + b;
            const square = n => n * n;
            const hello = () => "Hello!";
            console.log(plus(2, 3), square(5), hello());
        """, output = "5 25 Hello!")
        text("Because functions are values, you can **pass** them to other functions. A function that's passed in like this is called a **callback**.")
        code("""
            function twice(action) {
              action();
              action();
            }
            twice(() => console.log("Click"));
        """, output = """
            Click
            Click
        """)
        tip("You'll run into arrow functions all the time – especially with array methods like `map` and `filter` in the next unit.")

        output("""
            const times3 = x => x * 3;
            console.log(times3(7));
        """, "21", "7", "x * 3", "37")
        output("""
            const f = (a, b) => a - b;
            console.log(f(10, 4));
        """, "6", "-6", "14", "4")
        choice("Which arrow function returns the sum of `a` and `b`?", "(a, b) => a + b", "(a, b) -> a + b", "a, b => a + b", "(a, b) => { a + b }", mono = true,
            explain = "With curly braces you'd need `return`. Without brackets around `a, b`, JavaScript reads the expression differently.")
        typeOutput("""
            const greeting = name => `Hi ${D}{name}`;
            console.log(greeting("Monty"));
        """, "Hi Monty")
        fill("Finish the arrow function.", """
            const halve = (x) ?? x / 2;
            console.log(halve(9));
        """, answers = listOf("=>"), distractors = listOf("->", "==", ">="), output = "4.5")
        output("""
            function doSomething(fn, value) {
              return fn(value);
            }
            console.log(doSomething(x => x + 1, 9));
        """, "10", "9", "x + 1", "undefined")
        truth("When you call `f()` with `const f = () => 5;`, it returns the number 5.", true)
        write("Write an arrow function `celsiusToFahrenheit` that converts Celsius to Fahrenheit (`c * 9 / 5 + 32`).",
            solution = """
                const celsiusToFahrenheit = (c) => c * 9 / 5 + 32;
            """,
            test = """
                if (celsiusToFahrenheit(0) !== 32) throw new Error("0 °C is 32 °F");
                if (celsiusToFahrenheit(100) !== 212) throw new Error("100 °C is 212 °F");
                if (celsiusToFahrenheit(-40) !== -40) throw new Error("-40 °C is -40 °F");
                console.log("✅ All tests passed!");
            """,
            mustContain = listOf("=>"),
            hint = "`const celsiusToFahrenheit = (c) => ...;`")
    }

    lesson("Scope", "Where a variable is visible", LessonIcon.BRAIN) {
        text("Variables made with `let` and `const` only exist in the **block** `{ }` where they were created – that's their **scope**. Outside of it, they don't exist.")
        code("""
            const outside = "everywhere";
            function show() {
              const local = "only here";
              console.log(outside, local);
            }
            show();
            console.log(typeof local);
        """, output = """
            everywhere only here
            undefined
        """)
        text("An inner variable can **hide** an outer one with the same name. The outer one stays unchanged.")
        code("""
            const color = "blue";
            function paintRed() {
              const color = "red";
              console.log(color);
            }
            paintRed();
            console.log(color);
        """, output = """
            red
            blue
        """)
        text("Here's something exciting: a function “remembers” the variables around it – even after the outer function has already finished. That's called a **closure**.")
        code("""
            function makeCounter() {
              let count = 0;
              return () => {
                count++;
                return count;
              };
            }
            const next = makeCounter();
            console.log(next(), next(), next());
        """, output = "1 2 3")
        tip("Create variables as locally as possible. The fewer global variables there are, the less parts of the program can get in each other's way.")

        output("""
            if (true) {
              const secret = 42;
            }
            console.log(typeof secret);
        """, "undefined", "number", "42", "Error")
        output("""
            let x = 1;
            function change() {
              x = 5;
            }
            change();
            console.log(x);
        """, "5", "1", "undefined", "Error", explain = "Without `let` inside the function, the outer variable `x` is changed.")
        output("""
            let x = 1;
            function change() {
              let x = 5;
            }
            change();
            console.log(x);
        """, "1", "5", "undefined", "Error", explain = "`let x` inside the function creates a new, local variable – the outer one stays 1.")
        choice("Where is a variable that was created with `let` inside an `if` block visible?", "Only in that block", "In the whole program", "In all functions", "Only in the next line")
        typeOutput("""
            function counter() {
              let n = 0;
              return () => ++n;
            }
            const a = counter();
            a();
            a();
            console.log(a());
        """, "3")
        truth("A function can access variables that were defined outside of it.", true)
        write("Write a function `makeGreeter(greeting)` that returns a **function**. That function takes a name and gives back `<greeting>, <name>!` – for example `Hi, Ada!`.",
            solution = """
                function makeGreeter(greeting) {
                  return (name) => `${D}{greeting}, ${D}{name}!`;
                }
            """,
            test = """
                const hi = makeGreeter("Hi");
                const hello = makeGreeter("Hello");
                if (hi("Ada") !== "Hi, Ada!") throw new Error("hi(\"Ada\") should give \"Hi, Ada!\"");
                if (hello("Ben") !== "Hello, Ben!") throw new Error("hello(\"Ben\") should give \"Hello, Ben!\"");
                console.log("✅ All tests passed!");
            """,
            hint = "Return an arrow function: `return (name) => ...;` – it knows `greeting` from its surroundings.")
    }

    checkpoint()
}
