package app.pyquest.content.js.en

import app.pyquest.content.CourseBuilder
import app.pyquest.content.js.D
import app.pyquest.data.LessonIcon

fun CourseBuilder.jsEnUnit11() = unit("Error Handling", "When something goes wrong: try, catch and throw") {
    lesson("try & catch", "Catching errors instead of crashing", LessonIcon.BUG) {
        text("Normally, an error stops the whole program. With `try { ... } catch (error) { ... }` you catch it: if an error happens in the `try` block, JavaScript jumps straight into the `catch` block – and the program keeps running afterwards.")
        code("""
            const user = undefined;
            try {
              console.log(user.name);
              console.log("This is skipped");
            } catch (error) {
              console.log("Oops:", error.message);
            }
            console.log("The program keeps running.");
        """, output = """
            Oops: Cannot read properties of undefined (reading 'name')
            The program keeps running.
        """)
        text("The error object has two important properties: `name` (the kind of error) and `message` (the description).")
        code("""
            try {
              notDefined();
            } catch (e) {
              console.log(e.name);
              console.log(e.message);
            }
        """, output = """
            ReferenceError
            notDefined is not defined
        """)
        tip("If there's no error, the `catch` block is simply skipped.")
        warn("Don't catch errors “blindly” just to make the message go away. A `catch` should react sensibly – for example by showing an understandable message or using a fallback value.")

        output("""
            try {
              console.log("A");
            } catch (e) {
              console.log("B");
            }
            console.log("C");
        """, "A\nC", "A\nB\nC", "B\nC", "A")
        output("""
            try {
              const x = null;
              x.value = 5;
              console.log("done");
            } catch (e) {
              console.log("error");
            }
        """, "error", "done", "done\nerror", "Nothing")
        typeOutput("""
            try {
              undefinedFunction();
            } catch (e) {
              console.log(e.name);
            }
        """, "ReferenceError")
        choice("When does the `catch` block run?", "When an error happens in the `try` block", "Always", "Never", "Only at the end of the program")
        fill("Catch the error.", """
            try {
              JSON.parse("not JSON");
            } ?? (e) {
              console.log("Invalid data");
            }
        """, answers = listOf("catch"), distractors = listOf("except", "error", "finally"), output = "Invalid data")
        truth("After a caught error, the program carries on after the `catch` block.", true)
        write("In a `try` block, call the (non-existent) function `loadData()`. In the `catch` block, print `Loading failed: ` followed by the error message.",
            solution = """
                try {
                  loadData();
                } catch (error) {
                  console.log("Loading failed: " + error.message);
                }
            """,
            output = "Loading failed: loadData is not defined",
            mustContain = listOf("try", "catch"),
            hint = "In `catch (error)`, the description is in `error.message`.")
    }

    lesson("Throwing Errors", "Raising the alarm yourself with throw", LessonIcon.BUG) {
        text("With `throw` you raise an error yourself – for example when a function receives invalid values. The usual way is `throw new Error(\"description\")`.")
        code("""
            function root(x) {
              if (x < 0) {
                throw new Error("No square root of negative numbers!");
              }
              return Math.sqrt(x);
            }

            console.log(root(16));
            try {
              console.log(root(-4));
            } catch (e) {
              console.log("Error:", e.message);
            }
        """, output = """
            4
            Error: No square root of negative numbers!
        """)
        text("A thrown error “travels” upwards until a `catch` catches it – even across several function calls. If nobody catches it, the program stops:")
        code("""
            function checkAge(age) {
              if (typeof age !== "number") throw new TypeError("Age must be a number");
              return age >= 18;
            }
            console.log(checkAge("twenty"));
        """, fails = true)
        tip("There are built-in kinds of errors for typical cases: `TypeError` for wrong types, `RangeError` for values outside the allowed range.")

        output("""
            function f() {
              throw new Error("broken");
            }
            try {
              f();
              console.log("next");
            } catch (e) {
              console.log(e.message);
            }
        """, "broken", "next", "next\nbroken", "Error")
        output("""
            function divide(a, b) {
              if (b === 0) throw new RangeError("Division by 0");
              return a / b;
            }
            try {
              console.log(divide(8, 2));
              console.log(divide(1, 0));
            } catch (e) {
              console.log(e.name);
            }
        """, "4\nRangeError", "4", "RangeError", "4\nInfinity")
        choice("How do you raise an error yourself?", "throw new Error(\"...\")", "raise Error(\"...\")", "error(\"...\")", "catch new Error(\"...\")", mono = true)
        typeOutput("""
            try {
              throw new Error("Stop!");
            } catch (e) {
              console.log(e.message.toUpperCase());
            }
        """, "STOP!")
        fill("Throw an error if the name is empty.", """
            function setName(name) {
              if (name === "") ?? new Error("Name missing");
              return name;
            }
            try {
              setName("");
            } catch (e) {
              console.log(e.message);
            }
        """, answers = listOf("throw"), distractors = listOf("return", "catch", "raise"), output = "Name missing")
        truth("An error that isn't caught ends the program.", true)
        write("""
            Write a function `checkPin(pin)` that throws an `Error` with the message `PIN must have 4 digits` if `pin` isn't a string of exactly 4 digits.
            Otherwise it returns `true`. Tip: `/^\d{4}$/.test(pin)` checks for exactly 4 digits.
        """.trimIndent(),
            solution = """
                function checkPin(pin) {
                  if (typeof pin !== "string" || !/^\d{4}$/.test(pin)) {
                    throw new Error("PIN must have 4 digits");
                  }
                  return true;
                }
            """,
            test = """
                if (checkPin("1234") !== true) throw new Error("checkPin(\"1234\") should return true");
                for (const wrong of ["123", "12345", "abcd", 1234]) {
                  let thrown = false;
                  try {
                    checkPin(wrong);
                  } catch (e) {
                    thrown = e.message === "PIN must have 4 digits";
                  }
                  if (!thrown) throw new Error("For " + JSON.stringify(wrong) + " an error with the right message should be thrown");
                }
                console.log("✅ All tests passed!");
            """,
            mustContain = listOf("throw"),
            hint = "`if (...) throw new Error(\"PIN must have 4 digits\");` – then `return true;`")
    }

    lesson("Custom Error Classes", "Telling errors apart with instanceof", LessonIcon.CLASS) {
        text("For your own kinds of errors, you extend the class `Error`. Set `this.name` so that the error message shows what kind of error it is.")
        code("""
            class InputError extends Error {
              constructor(message) {
                super(message);
                this.name = "InputError";
              }
            }

            try {
              throw new InputError("Please enter a number");
            } catch (e) {
              console.log(e.name + ": " + e.message);
              console.log(e instanceof InputError, e instanceof Error);
            }
        """, output = """
            InputError: Please enter a number
            true true
        """)
        text("Inside `catch`, you can use `instanceof` to tell which kind of error happened. Unexpected errors you simply pass on with `throw`.")
        code("""
            class NotFound extends Error {}

            function search(list, value) {
              const i = list.indexOf(value);
              if (i === -1) throw new NotFound(`${D}{value} is missing`);
              return i;
            }

            for (const value of ["kiwi", "melon"]) {
              try {
                console.log("Position:", search(["apple", "kiwi"], value));
              } catch (e) {
                if (e instanceof NotFound) {
                  console.log("Not found:", e.message);
                } else {
                  throw e;
                }
              }
            }
        """, output = """
            Position: 1
            Not found: melon is missing
        """)
        tip("Custom errors can have extra properties – for example `this.field = \"email\"`, so you know which form field was wrong.")

        output("""
            class MyError extends Error {}
            try {
              throw new MyError("x");
            } catch (e) {
              console.log(e instanceof MyError);
            }
        """, "true", "false", "MyError", "x")
        output("""
            class AppError extends Error {
              constructor(m) {
                super(m);
                this.name = "AppError";
              }
            }
            const e = new AppError("Server down");
            console.log(`${D}{e.name}: ${D}{e.message}`);
        """, "AppError: Server down", "Error: Server down", "Server down", "AppError")
        choice("Which class does a custom error class inherit from?", "Error", "Exception", "Object", "Failure", mono = true)
        typeOutput("""
            class EmptyError extends Error {}
            try {
              throw new EmptyError("empty");
            } catch (e) {
              console.log(e instanceof Error ? "Error" : "not an Error");
            }
        """, "Error")
        fill("Tell the kind of error apart.", """
            class TooSmall extends Error {}
            try {
              throw new TooSmall("3 < 5");
            } catch (e) {
              if (e ?? TooSmall) {
                console.log("too small:", e.message);
              }
            }
        """, answers = listOf("instanceof"), distractors = listOf("typeof", "===", "in"), output = "too small: 3 < 5")
        truth("With `throw e;` inside the `catch` block, you can pass an error on.", true)
        write("""
            Write a class `NotEnoughMoney extends Error` whose constructor receives the missing amount, stores it in `this.missing`,
            sets the message `<missing> € missing` and sets `this.name = "NotEnoughMoney"`.
        """.trimIndent(),
            solution = """
                class NotEnoughMoney extends Error {
                  constructor(missing) {
                    super(`${D}{missing} € missing`);
                    this.name = "NotEnoughMoney";
                    this.missing = missing;
                  }
                }
            """,
            test = """
                const e = new NotEnoughMoney(12);
                if (!(e instanceof Error)) throw new Error("NotEnoughMoney should inherit from Error");
                if (e.message !== "12 € missing") throw new Error("The message should be \"12 € missing\", but it is: " + e.message);
                if (e.name !== "NotEnoughMoney") throw new Error("Set this.name = \"NotEnoughMoney\"");
                if (e.missing !== 12) throw new Error("Store the amount in this.missing");
                console.log("✅ All tests passed!");
            """,
            mustContain = listOf("extends Error", "super"),
            hint = "In the constructor, call `super(...)` with the message first, then set `this.name` and `this.missing`.")
    }

    lesson("finally & Error Types", "Cleaning up, and the typical errors", LessonIcon.BUG) {
        text("A `finally` block **always** runs – whether an error happened or not. It's perfect for cleaning up, for example hiding a loading spinner again.")
        code("""
            function load(ok) {
              console.log("Loading …");
              try {
                if (!ok) throw new Error("Network error");
                console.log("Data's here!");
              } catch (e) {
                console.log("Problem:", e.message);
              } finally {
                console.log("Spinner off");
              }
            }
            load(true);
            load(false);
        """, output = """
            Loading …
            Data's here!
            Spinner off
            Loading …
            Problem: Network error
            Spinner off
        """)
        text("""
            The most common kinds of errors in JavaScript:
            - `SyntaxError` – the code is written wrong, the program doesn't even start
            - `ReferenceError` – a name is unknown (a typo?)
            - `TypeError` – a value has the wrong type, e.g. accessing `undefined.x`
            - `RangeError` – a value is outside the allowed range
        """)
        code("""
            const tests = [
              () => undefined.x,
              () => doesNotExist,
              () => new Array(-1),
              () => (5).toFixed(500),
            ];
            for (const t of tests) {
              try {
                t();
              } catch (e) {
                console.log(e.name);
              }
            }
        """, output = """
            TypeError
            ReferenceError
            RangeError
            RangeError
        """)
        tip("If you don't need the error object in `catch`, you can leave out the brackets: `catch { ... }`.")

        output("""
            try {
              console.log("A");
            } finally {
              console.log("B");
            }
        """, "A\nB", "A", "B", "B\nA")
        output("""
            try {
              throw new Error("x");
            } catch {
              console.log("caught");
            } finally {
              console.log("done");
            }
        """, "caught\ndone", "done", "caught", "x\ndone")
        match(
            "SyntaxError" to "written wrong",
            "ReferenceError" to "unknown name",
            "TypeError" to "wrong type",
            "RangeError" to "value out of range",
        )
        choice("Which error do you get with `null.length`?", "TypeError", "ReferenceError", "SyntaxError", "RangeError", mono = true)
        typeOutput("""
            function test() {
              try {
                return "try";
              } finally {
                console.log("finally");
              }
            }
            console.log(test());
        """, "finally\ntry", explain = "Even a `return` inside `try` waits until `finally` has run.")
        fill("Make sure that “Bye” is always printed.", """
            try {
              console.log("Hello");
            } ?? {
              console.log("Bye");
            }
        """, answers = listOf("finally"), distractors = listOf("catch", "else", "always"), output = "Hello\nBye")
        truth("`finally` only runs if no error happens.", false)
        write("""
            Write a function `safeDivide(a, b)` that returns `a / b`. If `b` is `0`, it should throw a `RangeError` with the message `Division by 0`.
            Then call `safeDivide(10, 0)` in a `try` block, print `<name>: <message>` in the `catch` block and `done` in the `finally` block.
        """.trimIndent(),
            solution = """
                function safeDivide(a, b) {
                  if (b === 0) throw new RangeError("Division by 0");
                  return a / b;
                }
                try {
                  console.log(safeDivide(10, 0));
                } catch (e) {
                  console.log(`${D}{e.name}: ${D}{e.message}`);
                } finally {
                  console.log("done");
                }
            """,
            output = """
                RangeError: Division by 0
                done
            """,
            mustContain = listOf("RangeError", "finally"),
            hint = "`throw new RangeError(\"Division by 0\")` in the function, then wrap the call in `try`/`catch`/`finally`.")
    }

    checkpoint()
}
