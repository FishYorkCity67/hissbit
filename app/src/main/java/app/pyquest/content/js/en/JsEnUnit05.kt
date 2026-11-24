package app.pyquest.content.js.en

import app.pyquest.content.CourseBuilder
import app.pyquest.content.js.D
import app.pyquest.data.LessonIcon

fun CourseBuilder.jsEnUnit05() = unit("Loops", "Repeating with while and for") {
    lesson("while Loops", "Repeating as long as something is true", LessonIcon.LOOP) {
        text("A `while` loop repeats its block **as long as** the condition is `true`. The condition is checked again before every round.")
        code("""
            let countdown = 3;
            while (countdown > 0) {
              console.log(countdown);
              countdown--;
            }
            console.log("Liftoff! 🚀");
        """, output = """
            3
            2
            1
            Liftoff! 🚀
        """)
        warn("If nothing in the loop changes the condition, it runs **forever**. Hissbit stops a program like that after a few seconds – in the browser, the page would freeze.")
        text("A typical pattern: **adding up** values in a loop.")
        code("""
            let total = 0;
            let number = 1;
            while (number <= 5) {
              total += number;
              number++;
            }
            console.log(total);
        """, output = "15")
        tip("`do { ... } while (condition);` runs the block at least **once** and only checks afterwards.")

        output("""
            let i = 1;
            while (i <= 3) {
              console.log(i);
              i++;
            }
        """, "1\n2\n3", "1\n2\n3\n4", "0\n1\n2", "1")
        output("""
            let n = 10;
            while (n > 10) {
              console.log(n);
              n--;
            }
            console.log("End");
        """, "End", "10\nEnd", "10", "Nothing", explain = "The condition is false right from the start – the loop body doesn't run even once.")
        typeOutput("""
            let x = 1;
            while (x < 20) {
              x *= 2;
            }
            console.log(x);
        """, "32")
        choice("What happens with `while (true) { }` without a way out?", "The loop runs forever", "It runs exactly once", "It doesn't run at all", "You get a SyntaxError right away")
        fill("Complete the countdown from 5 to 1.", """
            let n = 5;
            ?? (n >= 1) {
              console.log(n);
              n--;
            }
        """, answers = listOf("while"), distractors = listOf("if", "repeat", "loop"), output = "5\n4\n3\n2\n1")
        truth("With `while`, the condition is checked again before **every** round.", true)
        write("Use a `while` loop to print the numbers **2, 4, 6, 8, 10** – each on its own line.",
            solution = """
                let n = 2;
                while (n <= 10) {
                  console.log(n);
                  n += 2;
                }
            """,
            output = """
                2
                4
                6
                8
                10
            """,
            mustContain = listOf("while"),
            hint = "Start at 2 and add 2 in every round, as long as `n <= 10`.")
    }

    lesson("for Loops", "Counting made easy", LessonIcon.LOOP) {
        text("The `for` loop puts everything you need for counting into one line: the **start**, the **condition** and the **step** – separated by semicolons.")
        code("""
            for (let i = 1; i <= 4; i++) {
              console.log("Round", i);
            }
        """, output = """
            Round 1
            Round 2
            Round 3
            Round 4
        """)
        text("""
            This is how you read `for (let i = 1; i <= 4; i++)`:
            - `let i = 1` – once at the start
            - `i <= 4` – checked before every round
            - `i++` – run after every round
        """)
        code("""
            for (let i = 10; i > 0; i -= 3) {
              console.log(i);
            }
        """, output = """
            10
            7
            4
            1
        """)
        text("With a `for` loop you can also go through the characters of a string:")
        code("""
            const word = "Hey";
            for (let i = 0; i < word.length; i++) {
              console.log(i, word[i]);
            }
        """, output = """
            0 H
            1 e
            2 y
        """)
        tip("Traditionally, the counter is called `i` (for index). It only exists inside the loop.")

        output("""
            for (let i = 0; i < 3; i++) {
              console.log(i);
            }
        """, "0\n1\n2", "1\n2\n3", "0\n1\n2\n3", "3")
        output("""
            let total = 0;
            for (let i = 1; i <= 4; i++) {
              total += i;
            }
            console.log(total);
        """, "10", "4", "1234", "6")
        typeOutput("""
            for (let i = 5; i >= 1; i -= 2) {
              console.log(i);
            }
        """, "5\n3\n1")
        choice("How many times does `for (let i = 0; i < 5; i++)` run?", "5 times", "4 times", "6 times", "Forever", mono = false)
        fill("Print the numbers 1 to 3.", """
            for (let i = 1; i ?? 3; i++) {
              console.log(i);
            }
        """, answers = listOf("<="), distractors = listOf("<", ">=", "=="), output = "1\n2\n3")
        arrange("Print the 3 times table up to 3 · 5 (3, 6, 9, 12, 15).", """
            for (let i = 1; i <= 5; i++) {
              console.log(3 * i);
            }
        """, output = "3\n6\n9\n12\n15")
        truth("In `for (let i = 0; i < 10; i++)`, `i++` runs before the first round.", false,
            explain = "The step `i++` always comes **after** a round. The first round runs with `i = 0`.")
        write("Use a `for` loop to print `1 apple`, `2 apples`, … up to `5 apples`. Careful: for 1 it's “apple”, otherwise “apples”.",
            solution = """
                for (let i = 1; i <= 5; i++) {
                  console.log(i === 1 ? "1 apple" : `${D}{i} apples`);
                }
            """,
            output = """
                1 apple
                2 apples
                3 apples
                4 apples
                5 apples
            """,
            mustContain = listOf("for"),
            hint = "Use `if` or the `?` operator to choose between “apple” and “apples”.")
    }

    lesson("break & continue", "Steering loops", LessonIcon.LOOP) {
        text("`break` ends a loop immediately. `continue` skips the rest of the current round and carries on with the next one.")
        code("""
            for (let i = 1; i <= 10; i++) {
              if (i === 4) {
                break;
              }
              console.log(i);
            }
        """, output = """
            1
            2
            3
        """)
        code("""
            for (let i = 1; i <= 6; i++) {
              if (i % 2 === 0) {
                continue;
              }
              console.log(i);
            }
        """, output = """
            1
            3
            5
        """)
        text("A common pattern is `while (true)` with a `break` inside – for example, when you keep asking until a valid answer comes in.")
        code("""
            while (true) {
              const answer = prompt("What is 3 + 4?");
              if (answer === "7") {
                console.log("Correct!");
                break;
              }
              console.log("Sorry, wrong. Try again.");
            }
        """, output = """
            What is 3 + 4? 6
            Sorry, wrong. Try again.
            What is 3 + 4? 7
            Correct!
        """, stdin = listOf("6", "7"))

        output("""
            for (let i = 0; i < 5; i++) {
              if (i === 2) break;
              console.log(i);
            }
        """, "0\n1", "0\n1\n2", "2", "0\n1\n3\n4")
        output("""
            for (let i = 0; i < 5; i++) {
              if (i === 2) continue;
              console.log(i);
            }
        """, "0\n1\n3\n4", "0\n1", "0\n1\n2\n3\n4", "2")
        choice("What does `continue` do?", "Jumps straight to the next round", "Ends the loop", "Ends the program", "Repeats the current round")
        typeOutput("""
            let n = 0;
            while (true) {
              n += 3;
              if (n > 10) break;
            }
            console.log(n);
        """, "12")
        fill("End the search as soon as the first number divisible by 7 has been found.", """
            for (let i = 20; i < 40; i++) {
              if (i % 7 === 0) {
                console.log(i);
                ??;
              }
            }
        """, answers = listOf("break"), distractors = listOf("continue", "stop", "return"), output = "21")
        truth("`break` only ends the loop, not the whole program.", true)
        write("Print all the numbers from 1 to 15 that are **not** divisible by 3. Use `continue`.",
            solution = """
                for (let i = 1; i <= 15; i++) {
                  if (i % 3 === 0) continue;
                  console.log(i);
                }
            """,
            output = """
                1
                2
                4
                5
                7
                8
                10
                11
                13
                14
            """,
            mustContain = listOf("continue"),
            hint = "Check with `i % 3 === 0` and skip those numbers with `continue`.")
    }

    lesson("Nested Loops", "Loops inside loops – patterns and tables", LessonIcon.PUZZLE) {
        text("You can put loops inside each other. The inner loop then runs all the way through for **every** round of the outer loop.")
        code("""
            for (let row = 1; row <= 3; row++) {
              let text = "";
              for (let column = 1; column <= 4; column++) {
                text += row * column + " ";
              }
              console.log(text);
            }
        """, output = """
            1 2 3 4
            2 4 6 8
            3 6 9 12
        """)
        text("Patterns made of characters can often be built with `repeat()` too:")
        code("""
            for (let i = 1; i <= 4; i++) {
              console.log("*".repeat(i));
            }
        """, output = """
            *
            **
            ***
            ****
        """)
        tip("`console.log()` always starts a new line. For a line made of several parts, you build a string first and print it at the end.")

        output("""
            for (let a = 1; a <= 2; a++) {
              for (let b = 1; b <= 2; b++) {
                console.log(a, b);
              }
            }
        """, "1 1\n1 2\n2 1\n2 2", "1 1\n2 2", "1 2\n1 2", "1 1\n2 1\n1 2\n2 2")
        typeOutput("""
            let counter = 0;
            for (let i = 0; i < 3; i++) {
              for (let j = 0; j < 4; j++) {
                counter++;
              }
            }
            console.log(counter);
        """, "12", explain = "3 rounds outside times 4 rounds inside.")
        output("""
            for (let i = 3; i >= 1; i--) {
              console.log("#".repeat(i));
            }
        """, "###\n##\n#", "#\n##\n###", "###", "#\n#\n#")
        choice("How many times does the inner block run with 5 rounds outside and 3 rounds inside?", "15 times", "8 times", "5 times", "3 times")
        fill("Build a line of 5 stars and print it.", """
            let line = "";
            for (let i = 0; i < 5; i++) {
              line ?? "*";
            }
            console.log(line);
        """, answers = listOf("+="), distractors = listOf("=", "-=", "=="), output = "*****")
        write("Print a rectangle made of `#` with **3 rows** and **6 columns**.",
            solution = """
                for (let i = 0; i < 3; i++) {
                  console.log("#".repeat(6));
                }
            """,
            output = """
                ######
                ######
                ######
            """,
            mustContain = listOf("for"),
            hint = "One loop for the rows; every row is `\"#\".repeat(6)`.")
        write("Print a staircase of numbers: row `n` contains the numbers from 1 to `n`, separated by spaces – for `n` from 1 to 4.",
            solution = """
                for (let n = 1; n <= 4; n++) {
                  let line = "";
                  for (let i = 1; i <= n; i++) {
                    line += i + " ";
                  }
                  console.log(line.trim());
                }
            """,
            output = """
                1
                1 2
                1 2 3
                1 2 3 4
            """,
            mustContain = listOf("for"),
            hint = "The outer loop counts the rows with `n`; inside, a second loop builds the text for row `n`.")
    }

    project("Project: Number Guessing", "A little game with a loop and input") {
        text("Time for your first game! Monty thinks of a number, and you keep guessing until you hit it. After every guess, the program gives you a hint.")
        text("You create real random numbers with `Math.random()` (a number between 0 and 1). This is how you get a whole number from 1 to 10:")
        code("""
            const secret = Math.floor(Math.random() * 10) + 1;
            console.log(secret >= 1 && secret <= 10);
        """, output = "true")
        tip("For the exercise we use a **fixed** secret number – otherwise the result couldn't be checked automatically.")

        choice("Which values can `Math.floor(Math.random() * 10) + 1` have?", "1 to 10", "0 to 10", "0 to 9", "1 to 11")
        arrange("Put the game loop in the right order.", """
            const secret = 4;
            let guess = 0;
            while (guess !== secret) {
              guess = Number(prompt("Your guess?"));
            }
            console.log("Got it!");
        """)
        write("""
            Program the game: the secret number is `7`. In a loop, ask for numbers with `prompt("Your guess?")`.
            If the guess is too low, print `Too low!`; if it's too high, print `Too high!`. When it's a hit, print `Correct! Attempts: <count>` and end the game.
        """.trimIndent(),
            starter = """
                const secret = 7;
                let attempts = 0;
            """,
            solution = """
                const secret = 7;
                let attempts = 0;
                while (true) {
                  const guess = Number(prompt("Your guess?"));
                  attempts++;
                  if (guess < secret) {
                    console.log("Too low!");
                  } else if (guess > secret) {
                    console.log("Too high!");
                  } else {
                    console.log(`Correct! Attempts: ${D}{attempts}`);
                    break;
                  }
                }
            """,
            output = """
                Too low!
                Too high!
                Too low!
                Correct! Attempts: 4
            """,
            stdin = listOf("3", "9", "6", "7"),
            mustContain = listOf("prompt", "while"),
            hint = "`while (true)` with `break` when you hit the number. Count up `attempts` in every round.")
        output("""
            let guess = 1;
            let attempts = 0;
            while (guess !== 4) {
              guess++;
              attempts++;
            }
            console.log(attempts);
        """, "3", "4", "1", "The loop runs forever", explain = "`guess` becomes 2, 3 and 4 – after three rounds, the condition `guess !== 4` is false.")
    }

    checkpoint()
}
