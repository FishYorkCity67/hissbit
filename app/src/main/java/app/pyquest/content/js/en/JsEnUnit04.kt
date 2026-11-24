package app.pyquest.content.js.en

import app.pyquest.content.CourseBuilder
import app.pyquest.data.LessonIcon

fun CourseBuilder.jsEnUnit04() = unit("Decisions", "Comparisons, if and else, logic") {
    lesson("Comparisons & Booleans", "True or false?", LessonIcon.BRANCH) {
        text("Comparisons give you a **truth value** (a boolean): `true` or `false`.")
        code("""
            console.log(5 > 3);
            console.log(2 >= 7);
            console.log(4 === 4);
            console.log("Yes" !== "No");
        """, output = """
            true
            false
            true
            true
        """)
        text("""
            The comparison operators:
            - `===` equal and `!==` not equal
            - `<` less than, `>` greater than
            - `<=` less than or equal, `>=` greater than or equal
        """)
        warn("There are also `==` and `!=` with only two characters. But they secretly convert types: `5 == \"5\"` is `true`! So always use `===` and `!==`.")
        code("""
            console.log(5 == "5");
            console.log(5 === "5");
        """, output = """
            true
            false
        """)
        tip("A single `=` stores a value. To compare, you need `===`.")

        output("console.log(10 > 9);", "true", "false", "10 > 9", "1")
        output("console.log(3 === \"3\");", "false", "true", "3", "Error",
            explain = "`===` also checks the type: a number and a text are never equal.")
        choice("Which operator checks for **equality** without converting types?", "===", "=", "==", "=>", mono = true)
        typeOutput("console.log(7 <= 7);", "true")
        truth("In JavaScript, `5 == \"5\"` gives `true`.", true, explain = "`==` turns the text into a number. That's why you should use `===` instead!")
        match(
            "===" to "equal",
            "!==" to "not equal",
            ">=" to "greater than or equal",
            "<" to "less than",
            "=" to "store a value",
        )
        fill("Check whether the two values are **not equal**.", "console.log(\"dog\" ?? \"cat\");",
            answers = listOf("!=="), distractors = listOf("===", "=!", "<>"), output = "true")
        write("The variable `age` contains `17`. Print whether `age` is **at least 18** (so `true` or `false`).",
            starter = """
                const age = 17;
            """,
            solution = """
                const age = 17;
                console.log(age >= 18);
            """,
            output = "false",
            mustContain = listOf(">="),
            hint = "`age >= 18` gives you `true` or `false` directly.")
    }

    lesson("if, else if, else", "Running code only under a condition", LessonIcon.BRANCH) {
        text("With `if`, code only runs when a condition is `true`. The condition goes in round brackets, the code in **curly braces** `{ }`.")
        code("""
            const temperature = 28;
            if (temperature > 25) {
              console.log("Off to the pool! 🏊");
            }
            console.log("Done");
        """, output = """
            Off to the pool! 🏊
            Done
        """)
        text("With `else` you say what should happen otherwise. With `else if` you check more conditions – the first one that's true wins.")
        code("""
            const points = 72;
            if (points >= 90) {
              console.log("Excellent");
            } else if (points >= 70) {
              console.log("Good");
            } else {
              console.log("Keep practicing");
            }
        """, output = "Good")
        tip("Indent the code inside the curly braces (2 spaces are common in JavaScript). JavaScript itself doesn't care – but it's much easier for people to read.")
        warn("No semicolon right after `if (...)`! `if (x > 5); { ... }` would always run the block.")

        output("""
            const x = 3;
            if (x > 5) {
              console.log("big");
            } else {
              console.log("small");
            }
        """, "small", "big", "big\nsmall", "Nothing")
        output("""
            const n = 15;
            if (n > 10) {
              console.log("A");
            } else if (n > 5) {
              console.log("B");
            }
        """, "A", "B", "A\nB", "Nothing", explain = "Only the first matching branch runs – after that, it's done.")
        choice("What goes in the round brackets after `if`?", "The condition", "The code that runs", "A comment", "The variable name")
        typeOutput("""
            const number = 0;
            if (number > 0) {
              console.log("positive");
            } else if (number < 0) {
              console.log("negative");
            } else {
              console.log("zero");
            }
        """, "zero")
        arrange("Print **even** if `n` is even, otherwise **odd**.", """
            const n = 8;
            if (n % 2 === 0) {
              console.log("even");
            } else {
              console.log("odd");
            }
        """, output = "even")
        fill("Complete the branch.", """
            const money = 3;
            if (money >= 5) {
              console.log("Buy ice cream");
            } ?? {
              console.log("Save up");
            }
        """, answers = listOf("else"), distractors = listOf("elif", "otherwise", "then"), output = "Save up")
        write("The variable `hour` contains `14`. Print `Good morning` if `hour` is less than 12, `Good afternoon` if it's less than 18, and `Good evening` otherwise.",
            starter = """
                const hour = 14;
            """,
            solution = """
                const hour = 14;
                if (hour < 12) {
                  console.log("Good morning");
                } else if (hour < 18) {
                  console.log("Good afternoon");
                } else {
                  console.log("Good evening");
                }
            """,
            output = "Good afternoon",
            mustContain = listOf("if", "else"),
            hint = "Three cases: `if`, `else if` and `else`.")
    }

    lesson("Logical Operators", "&&, || and ! – and what “truthy” means", LessonIcon.BRAIN) {
        text("""
            You combine several conditions with logical operators:
            - `&&` (and): only `true` if **both** sides are true
            - `||` (or): `true` if **at least one** side is true
            - `!` (not): flips the value
        """)
        code("""
            const age = 15;
            const withParents = true;
            console.log(age >= 12 && age < 18);
            console.log(age >= 18 || withParents);
            console.log(!withParents);
        """, output = """
            true
            true
            false
        """)
        text("In conditions, it's not just `true` that counts: every value is either **truthy** or **falsy**. Only `false`, `0`, `\"\"` (empty text), `null`, `undefined` and `NaN` are falsy – everything else is truthy.")
        code("""
            const name = "";
            if (name) {
              console.log("Hello " + name);
            } else {
              console.log("No name entered");
            }
        """, output = "No name entered")
        text("Handy: `??` gives you a fallback value if the left side is `null` or `undefined`.")
        code("""
            let nickname = null;
            console.log(nickname ?? "Guest");
        """, output = "Guest")

        output("console.log(true && false);", "false", "true", "undefined", "Error")
        output("console.log(false || true);", "true", "false", "undefined", "Error")
        output("console.log(!(3 > 2));", "false", "true", "3 > 2", "Error")
        choice("Which value is **falsy**?", "0", "\"0\"", "\"false\"", "[]", mono = true,
            explain = "Only the number 0 is falsy. The texts `\"0\"` and `\"false\"` aren't empty – so they're truthy.")
        truth("`\"Hello\" && 0` is falsy in a condition.", true, explain = "With `&&`, both sides have to be truthy – and `0` is falsy.")
        typeOutput("console.log(undefined ?? \"fallback\");", "fallback")
        fill("Entry is free for children **under 6** or for people **65 and over**.", """
            const age = 70;
            if (age < 6 ?? age >= 65) {
              console.log("Free entry");
            }
        """, answers = listOf("||"), distractors = listOf("&&", "or", "!"), output = "Free entry")
        output("""
            const a = 5;
            const b = 10;
            if (a > 3 && b > 3) {
              console.log("both");
            } else {
              console.log("not both");
            }
        """, "both", "not both", "both\nnot both", "Nothing")
        write("A password is valid if it's **at least 8 characters** long **and** contains the digit `\"1\"`. Print whether `password = \"secret12\"` is valid.",
            starter = """
                const password = "secret12";
            """,
            solution = """
                const password = "secret12";
                console.log(password.length >= 8 && password.includes("1"));
            """,
            output = "true",
            mustContain = listOf("&&", "length"),
            hint = "Combine `password.length >= 8` and `password.includes(\"1\")` with `&&`.")
    }

    lesson("switch & the ? Operator", "Short decisions and many cases", LessonIcon.PUZZLE) {
        text("For short decisions there's the **conditional operator** `condition ? ifYes : ifNo`. It gives you one of two values.")
        code("""
            const age = 20;
            const status = age >= 18 ? "adult" : "minor";
            console.log(status);
        """, output = "adult")
        text("When a value is compared with lots of fixed possibilities, `switch` keeps things tidy. Every case ends with `break`, and `default` kicks in when no case matches.")
        code("""
            const day = "Sat";
            switch (day) {
              case "Sat":
              case "Sun":
                console.log("Weekend! 🎉");
                break;
              case "Fri":
                console.log("Almost the weekend");
                break;
              default:
                console.log("School day");
            }
        """, output = "Weekend! 🎉")
        warn("If you forget `break`, the program simply runs on into the next case (“fall-through”). Sometimes that's intended – but usually it's a bug.")

        output("console.log(5 > 3 ? \"yes\" : \"no\");", "yes", "no", "true", "5")
        typeOutput("""
            const points = 40;
            console.log(points >= 50 ? "passed" : "failed");
        """, "failed")
        output("""
            const color = "green";
            switch (color) {
              case "red":
                console.log("Stop");
                break;
              case "green":
                console.log("Go");
                break;
              default:
                console.log("Careful");
            }
        """, "Go", "Stop", "Careful", "Go\nCareful")
        output("""
            const n = 1;
            switch (n) {
              case 1:
                console.log("one");
              case 2:
                console.log("two");
                break;
              default:
                console.log("many");
            }
        """, "one\ntwo", "one", "two", "one\ntwo\nmany", explain = "There's no `break` after `one` – so it runs on into the next case.")
        choice("What does `default` stand for in a `switch`?", "All the cases that aren't listed", "The first case", "An error", "The end of the program")
        fill("Complete the conditional operator.", """
            const number = 7;
            console.log(number % 2 === 0 ?? "even" ?? "odd");
        """, answers = listOf("?", ":"), distractors = listOf("||", "else"), output = "odd")
        truth("Without `break`, a `switch` runs on into the next case.", true)
        write("Use the `?` operator to store `\"win\"` in `result` if `goals` is greater than `goalsAgainst`, otherwise `\"no win\"`, and print `result`.",
            starter = """
                const goals = 3;
                const goalsAgainst = 1;
            """,
            solution = """
                const goals = 3;
                const goalsAgainst = 1;
                const result = goals > goalsAgainst ? "win" : "no win";
                console.log(result);
            """,
            output = "win",
            mustContain = listOf("?", ":"),
            hint = "`const result = condition ? \"win\" : \"no win\";`")
    }

    checkpoint()
}
