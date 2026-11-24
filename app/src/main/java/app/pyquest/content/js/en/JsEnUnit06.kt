package app.pyquest.content.js.en

import app.pyquest.content.CourseBuilder
import app.pyquest.content.js.D
import app.pyquest.data.LessonIcon

fun CourseBuilder.jsEnUnit06() = unit("Arrays", "Storing and working with lists of values") {
    lesson("Creating Arrays", "Lots of values in one variable", LessonIcon.BOX) {
        text("An **array** stores several values in a fixed order. You write them in square brackets, separated by commas.")
        code("""
            const fruits = ["apple", "pear", "kiwi"];
            console.log(fruits);
            console.log(fruits.length);
        """, output = """
            [ 'apple', 'pear', 'kiwi' ]
            3
        """)
        text("Just like with strings, every element has an **index**, starting at 0. You can use the index to read an element **and to change it**.")
        code("""
            const fruits = ["apple", "pear", "kiwi"];
            console.log(fruits[0]);
            console.log(fruits.at(-1));
            fruits[1] = "mango";
            console.log(fruits);
        """, output = """
            apple
            kiwi
            [ 'apple', 'mango', 'kiwi' ]
        """)
        tip("Even with `const`, you can change the **contents** of an array. `const` only prevents the variable from getting a completely different array.")
        text("An array can contain anything – numbers, text, truth values and even other arrays. You write an empty array as `[]`.")
        code("""
            const mixed = [42, "Hello", true];
            const empty = [];
            console.log(mixed, empty.length);
        """, output = "[ 42, 'Hello', true ] 0")

        output("""
            const numbers = [10, 20, 30];
            console.log(numbers[1]);
        """, "20", "10", "30", "[ 10, 20, 30 ]")
        output("""console.log(["a", "b", "c", "d"].length);""", "4", "3", "5", "d")
        typeOutput("""
            const animals = ["dog", "cat", "mouse"];
            console.log(animals.at(-1));
        """, "mouse")
        output("""
            const colors = ["red", "green"];
            colors[0] = "blue";
            console.log(colors);
        """, "[ 'blue', 'green' ]", "[ 'red', 'green' ]", "[ 'blue', 'red', 'green' ]", "Error: colors is const")
        output("""
            const a = [1, 2, 3];
            console.log(a[3]);
        """, "undefined", "3", "Error", "null")
        truth("With a `const` array, you can change single elements.", true)
        fill("Print the **last** element, no matter how long the array is.", """
            const scores = [80, 95, 70, 85];
            console.log(scores[scores.length ?? 1]);
        """, answers = listOf("-"), distractors = listOf("+", "*", "/"), output = "85")
        write("Create an array `weekdays` with the strings `\"Mon\"`, `\"Tue\"` and `\"Wed\"`. Print the array and then its length.",
            solution = """
                const weekdays = ["Mon", "Tue", "Wed"];
                console.log(weekdays);
                console.log(weekdays.length);
            """,
            output = """
                [ 'Mon', 'Tue', 'Wed' ]
                3
            """,
            mustContain = listOf("["),
            hint = "`const weekdays = [\"Mon\", \"Tue\", \"Wed\"];` – then use `console.log` twice.")
    }

    lesson("Changing Arrays", "Adding, removing and searching", LessonIcon.BOX) {
        text("""
            Arrays grow and shrink with these methods:
            - `push(x)` adds to the end, `pop()` takes from the end
            - `unshift(x)` adds to the front, `shift()` takes from the front
        """)
        code("""
            const list = ["bread"];
            list.push("milk");
            list.push("eggs");
            list.unshift("coffee");
            console.log(list);
            const last = list.pop();
            console.log(last, list);
        """, output = """
            [ 'coffee', 'bread', 'milk', 'eggs' ]
            eggs [ 'coffee', 'bread', 'milk' ]
        """)
        text("For searching: `includes(x)` checks whether `x` appears. `indexOf(x)` gives you the position or `-1`.")
        code("""
            const team = ["Ada", "Ben", "Cleo"];
            console.log(team.includes("Ben"));
            console.log(team.indexOf("Cleo"));
            console.log(team.indexOf("Zoe"));
        """, output = """
            true
            2
            -1
        """)
        text("With `splice(index, count)` you remove elements at any position:")
        code("""
            const numbers = [10, 20, 30, 40];
            numbers.splice(1, 2);
            console.log(numbers);
        """, output = "[ 10, 40 ]")
        tip("`push`, `pop`, `shift`, `unshift` and `splice` change the array itself. Many other methods return a new array instead.")

        output("""
            const a = [1, 2];
            a.push(3);
            console.log(a);
        """, "[ 1, 2, 3 ]", "[ 3, 1, 2 ]", "[ 1, 2 ]", "3")
        output("""
            const a = ["x", "y", "z"];
            const removed = a.shift();
            console.log(removed, a.length);
        """, "x 2", "z 2", "x 3", "y 2")
        typeOutput("""
            const stack = [1, 2, 3];
            stack.pop();
            stack.pop();
            console.log(stack);
        """, "[ 1 ]")
        match(
            "push()" to "add to the end",
            "pop()" to "remove from the end",
            "unshift()" to "add to the front",
            "shift()" to "remove from the front",
            "includes()" to "is it in there?",
        )
        output("""console.log([5, 6, 7].indexOf(8));""", "-1", "0", "undefined", "3")
        fill("Add `\"ice cream\"` to the **end** of the list.", """
            const shopping = ["bread", "cheese"];
            shopping.??("ice cream");
            console.log(shopping);
        """, answers = listOf("push"), distractors = listOf("unshift", "add", "append"), output = "[ 'bread', 'cheese', 'ice cream' ]")
        truth("`pop()` returns the element it removed.", true)
        write("The array `queue` contains `\"Ada\"`, `\"Ben\"` and `\"Cleo\"`. It's the first person's turn: remove them from the **front** and print `Ada is next`. Then add `\"Dev\"` to the **end** and print the array.",
            starter = """
                const queue = ["Ada", "Ben", "Cleo"];
            """,
            solution = """
                const queue = ["Ada", "Ben", "Cleo"];
                const next = queue.shift();
                console.log(`${D}{next} is next`);
                queue.push("Dev");
                console.log(queue);
            """,
            output = """
                Ada is next
                [ 'Ben', 'Cleo', 'Dev' ]
            """,
            mustContain = listOf("shift", "push"),
            hint = "`shift()` takes the first person out and returns them, `push()` adds to the end.")
    }

    lesson("Looping Through Arrays", "for…of and loops with an index", LessonIcon.LOOP) {
        text("With `for…of` you go through all the elements of an array one after another – without worrying about the index.")
        code("""
            const animals = ["dog", "cat", "snake"];
            for (const animal of animals) {
              console.log("I like the", animal);
            }
        """, output = """
            I like the dog
            I like the cat
            I like the snake
        """)
        text("If you need the position too, use a classic `for` loop with an index:")
        code("""
            const places = ["Ada", "Ben", "Cleo"];
            for (let i = 0; i < places.length; i++) {
              console.log(`${D}{i + 1}. ${D}{places[i]}`);
            }
        """, output = """
            1. Ada
            2. Ben
            3. Cleo
        """)
        text("Typical jobs for loops: adding things up, finding the biggest value, counting matches.")
        code("""
            const points = [7, 12, 3, 9];
            let total = 0;
            let maximum = points[0];
            for (const p of points) {
              total += p;
              if (p > maximum) maximum = p;
            }
            console.log(total, maximum);
            console.log(Math.max(...points));
        """, output = """
            31 12
            12
        """)
        tip("`Math.max(...array)` finds the maximum directly. The three dots `...` unpack the array – more about that later.")

        output("""
            for (const z of [3, 1, 2]) {
              console.log(z * 2);
            }
        """, "6\n2\n4", "3\n1\n2", "12", "2\n4\n6")
        typeOutput("""
            let total = 0;
            for (const n of [5, 10, 15]) {
              total += n;
            }
            console.log(total);
        """, "30")
        output("""
            const words = ["Yes", "Nope", "Maybe"];
            let long = 0;
            for (const w of words) {
              if (w.length > 3) long++;
            }
            console.log(long);
        """, "2", "1", "3", "0")
        choice("What does `for (const x of list)` give you in every round?", "The next element", "The index", "The length", "The whole array")
        fill("Go through all the names.", """
            const names = ["Ada", "Ben"];
            for (const name ?? names) {
              console.log(name);
            }
        """, answers = listOf("of"), distractors = listOf("in", "from", "at"), output = "Ada\nBen",
            explain = "With arrays, `for…in` gives you the indexes (as text) – for the values, you use `for…of`.")
        truth("With `for…of`, you automatically get the index of every element.", false)
        write("Count how many numbers in the array `values` are **greater than 10**, and print the count.",
            starter = """
                const values = [4, 15, 8, 23, 11, 2];
            """,
            solution = """
                const values = [4, 15, 8, 23, 11, 2];
                let count = 0;
                for (const v of values) {
                  if (v > 10) count++;
                }
                console.log(count);
            """,
            output = "3",
            mustContain = listOf("for"),
            hint = "Start a counter at 0 and increase it in the loop whenever `v > 10`.")
        write("Calculate the **average** of the scores in the array `scores` and print it.",
            starter = """
                const scores = [80, 90, 70, 85, 75];
            """,
            solution = """
                const scores = [80, 90, 70, 85, 75];
                let total = 0;
                for (const s of scores) {
                  total += s;
                }
                console.log(total / scores.length);
            """,
            output = "80",
            mustContain = listOf("length"),
            hint = "First add everything up, then divide by `scores.length`.")
    }

    lesson("Copying, Joining, Reversing", "slice, join, reverse and nested arrays", LessonIcon.MAGIC) {
        text("Arrays are **not** copied when you assign them to another variable – both names point to the **same** array!")
        code("""
            const original = [1, 2, 3];
            const alsoOriginal = original;
            alsoOriginal.push(4);
            console.log(original);
        """, output = "[ 1, 2, 3, 4 ]")
        text("You get a real copy with `[...array]` or `array.slice()`. Just like with strings, `slice(start, end)` cuts out a piece.")
        code("""
            const original = [1, 2, 3];
            const copy = [...original];
            copy.push(4);
            console.log(original, copy);
            console.log([10, 20, 30, 40].slice(1, 3));
        """, output = """
            [ 1, 2, 3 ] [ 1, 2, 3, 4 ]
            [ 20, 30 ]
        """)
        text("`join()` turns an array into a string, `concat()` or `[...a, ...b]` join arrays together, and `reverse()` flips the order.")
        code("""
            const a = ["x", "y"];
            const b = ["z"];
            console.log([...a, ...b]);
            console.log(a.concat(b).join(" + "));
            console.log([1, 2, 3].reverse());
        """, output = """
            [ 'x', 'y', 'z' ]
            x + y + z
            [ 3, 2, 1 ]
        """)
        text("Arrays can contain other arrays – handy for tables or game boards:")
        code("""
            const board = [
              ["X", "O", "X"],
              ["O", "X", "O"],
            ];
            console.log(board[1][2]);
            console.log(board[0].join(" | "));
        """, output = """
            O
            X | O | X
        """)
        warn("`reverse()` changes the array itself. If you want to keep the original, reverse a copy: `[...a].reverse()`.")

        output("""
            const a = [1, 2];
            const b = a;
            b.push(3);
            console.log(a.length);
        """, "3", "2", "1", "Error", explain = "`b` isn't a new array – it's just a second name for the same array.")
        output("""console.log(["a", "b", "c"].join(""));""", "abc", "a,b,c", "a b c", "[ 'abc' ]")
        typeOutput("""console.log([1, 2, 3, 4, 5].slice(-2));""", "[ 4, 5 ]")
        output("""
            const grid = [[1, 2], [3, 4]];
            console.log(grid[1][0]);
        """, "3", "2", "1", "[ 3, 4 ]")
        choice("How do you create an **independent copy** of `list`?", "[...list]", "list", "new list", "list.copy", mono = true)
        fill("Join the words with hyphens.", """console.log(["Hiss", "Bit", "App"].??("-"));""",
            answers = listOf("join"), distractors = listOf("concat", "split", "push"), output = "Hiss-Bit-App")
        truth("`const b = a;` creates a copy of the array `a`.", false)
        write("Print the array `letters` **backwards** as a string (`ytnoM`) without changing the original. Then print the unchanged original.",
            starter = """
                const letters = ["M", "o", "n", "t", "y"];
            """,
            solution = """
                const letters = ["M", "o", "n", "t", "y"];
                console.log([...letters].reverse().join(""));
                console.log(letters);
            """,
            output = """
                ytnoM
                [ 'M', 'o', 'n', 't', 'y' ]
            """,
            mustContain = listOf("reverse", "join"),
            hint = "First make a copy with `[...letters]`, reverse the copy with `reverse()` and join it with `join(\"\")`.")
    }

    checkpoint()
}
