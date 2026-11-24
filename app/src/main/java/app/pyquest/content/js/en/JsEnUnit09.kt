package app.pyquest.content.js.en

import app.pyquest.content.CourseBuilder
import app.pyquest.content.js.D
import app.pyquest.data.LessonIcon

fun CourseBuilder.jsEnUnit09() = unit("Array Methods", "forEach, map, filter, reduce & co.") {
    lesson("forEach & map", "Working on every element", LessonIcon.LOOP) {
        text("Arrays have methods that you pass a **function** to. `forEach` calls it for every element – as a second value, you also get the index.")
        code("""
            const animals = ["dog", "cat", "mouse"];
            animals.forEach((animal, i) => {
              console.log(`${D}{i}: ${D}{animal}`);
            });
        """, output = """
            0: dog
            1: cat
            2: mouse
        """)
        text("`map` creates a **new array**: every element is replaced by the result of your function. The original stays unchanged.")
        code("""
            const prices = [10, 20, 30];
            const halfPrices = prices.map((p) => p / 2);
            console.log(halfPrices);
            console.log(prices);
            console.log(["a", "b"].map((s) => s.toUpperCase()));
        """, output = """
            [ 5, 10, 15 ]
            [ 10, 20, 30 ]
            [ 'A', 'B' ]
        """)
        tip("Remember: `forEach` is for **doing** something (e.g. printing) and returns nothing. `map` is for **transforming** and returns a new array.")

        output("""console.log([1, 2, 3].map((x) => x * 10));""", "[ 10, 20, 30 ]", "[ 1, 2, 3 ]", "60", "[ 10 ]")
        output("""
            const r = [1, 2].forEach((x) => x * 2);
            console.log(r);
        """, "undefined", "[ 2, 4 ]", "[ 1, 2 ]", "null", explain = "`forEach` never returns a result – that's what `map` is for.")
        typeOutput("""
            const names = ["ada", "ben"];
            console.log(names.map((n) => n.length));
        """, "[ 3, 3 ]")
        choice("What does `map` return?", "A new array with the transformed values", "The changed original array", "The number of elements", "undefined")
        fill("Create an array with all the numbers doubled.", """
            const numbers = [4, 5, 6];
            const doubled = numbers.??((n) => n * 2);
            console.log(doubled);
        """, answers = listOf("map"), distractors = listOf("forEach", "filter", "push"), output = "[ 8, 10, 12 ]")
        output("""
            ["x", "y"].forEach((b, i) => console.log(i + b));
        """, "0x\n1y", "x0\ny1", "1x\n2y", "[ '0x', '1y' ]")
        truth("`map` changes the original array.", false)
        write("Use `map` to convert the array `celsius` to Fahrenheit (`c * 9 / 5 + 32`) and print the new array.",
            starter = """
                const celsius = [0, 10, 25, 100];
            """,
            solution = """
                const celsius = [0, 10, 25, 100];
                const fahrenheit = celsius.map((c) => c * 9 / 5 + 32);
                console.log(fahrenheit);
            """,
            output = "[ 32, 50, 77, 212 ]",
            mustContain = listOf("map"),
            hint = "`celsius.map((c) => c * 9 / 5 + 32)`")
    }

    lesson("filter & find", "Picking the matching elements", LessonIcon.PUZZLE) {
        text("`filter` keeps only the elements for which your function returns `true` – the result is a new array. `find`, on the other hand, only gives you the **first** matching element (or `undefined`).")
        code("""
            const numbers = [3, 8, 12, 5, 20];
            console.log(numbers.filter((n) => n > 6));
            console.log(numbers.find((n) => n > 6));
            console.log(numbers.find((n) => n > 100));
            console.log(numbers.findIndex((n) => n === 5));
        """, output = """
            [ 8, 12, 20 ]
            8
            undefined
            3
        """)
        text("`some` checks whether **at least one** element matches, `every` whether **all** of them do. Both give you `true` or `false`.")
        code("""
            const ages = [12, 15, 19];
            console.log(ages.some((a) => a >= 18));
            console.log(ages.every((a) => a >= 18));
        """, output = """
            true
            false
        """)
        text("These methods are especially useful with arrays of objects:")
        code("""
            const products = [
              { name: "apple", price: 0.5 },
              { name: "coffee", price: 6.99 },
              { name: "bread", price: 2.5 },
            ];
            const cheap = products.filter((p) => p.price < 3);
            console.log(cheap.map((p) => p.name));
            console.log(products.find((p) => p.name === "coffee").price);
        """, output = """
            [ 'apple', 'bread' ]
            6.99
        """)

        output("""console.log([1, 2, 3, 4].filter((x) => x % 2 === 0));""", "[ 2, 4 ]", "[ 1, 3 ]", "2", "[ false, true, false, true ]")
        output("""console.log(["Ada", "Ben", "Anna"].find((n) => n.startsWith("A")));""", "Ada", "[ 'Ada', 'Anna' ]", "Anna", "true")
        typeOutput("""console.log([5, 7, 9].every((x) => x > 4));""", "true")
        output("""console.log([1, 2, 3].some((x) => x > 5));""", "false", "true", "[]", "undefined")
        match(
            "filter" to "all matching elements",
            "find" to "the first matching element",
            "some" to "does at least one match?",
            "every" to "do all of them match?",
            "findIndex" to "position of the first match",
        )
        fill("Keep only the words with more than 3 letters.", """
            const words = ["Hi", "Hello", "Howdy", "Yo"];
            console.log(words.??((w) => w.length > 3));
        """, answers = listOf("filter"), distractors = listOf("find", "map", "some"), output = "[ 'Hello', 'Howdy' ]")
        truth("`find` always returns an array.", false, explain = "`find` returns the first matching element itself – or `undefined`.")
        write("From the array `students`, print the names of everyone who **passed** (`points >= 50`) as an array.",
            starter = """
                const students = [
                  { name: "Ada", points: 78 },
                  { name: "Ben", points: 45 },
                  { name: "Cleo", points: 50 },
                  { name: "Dev", points: 31 },
                ];
            """,
            solution = """
                const students = [
                  { name: "Ada", points: 78 },
                  { name: "Ben", points: 45 },
                  { name: "Cleo", points: 50 },
                  { name: "Dev", points: 31 },
                ];
                const passed = students.filter((s) => s.points >= 50).map((s) => s.name);
                console.log(passed);
            """,
            output = "[ 'Ada', 'Cleo' ]",
            mustContain = listOf("filter"),
            hint = "First `filter` by points, then use `map` to keep only the names.")
    }

    lesson("reduce", "Combining everything into one value", LessonIcon.CALC) {
        text("`reduce` combines an array into **one single value** – a sum, for example. Your function receives the running total so far and the current element. The second argument of `reduce` is the starting value.")
        code("""
            const numbers = [5, 10, 15];
            const total = numbers.reduce((soFar, n) => soFar + n, 0);
            console.log(total);
        """, output = "30")
        text("""
            This is how `reduce` calculates step by step:
            - Start: `soFar = 0`
            - `0 + 5 = 5`
            - `5 + 10 = 15`
            - `15 + 15 = 30`
        """)
        code("""
            const prices = [2.5, 4, 1.5];
            const sum = prices.reduce((s, p) => s + p, 0);
            console.log(sum.toFixed(2));

            const words = ["a", "bb", "ccc"];
            const chars = words.reduce((n, w) => n + w.length, 0);
            console.log(chars);
        """, output = """
            8.00
            6
        """)
        text("With an object as the starting value, you can even count how often something appears:")
        code("""
            const votes = ["pizza", "pasta", "pizza", "salad", "pizza"];
            const tally = votes.reduce((t, food) => {
              t[food] = (t[food] ?? 0) + 1;
              return t;
            }, {});
            console.log(tally);
        """, output = "{ pizza: 3, pasta: 1, salad: 1 }")
        tip("For simple sums, `reduce` is perfect. If the function inside gets long and complicated, a normal loop is often easier to read.")

        output("""console.log([1, 2, 3, 4].reduce((a, b) => a + b, 0));""", "10", "24", "[ 1, 2, 3, 4 ]", "4")
        output("""console.log([2, 3, 4].reduce((a, b) => a * b, 1));""", "24", "9", "0", "10")
        typeOutput("""console.log([3, 9, 2].reduce((max, x) => (x > max ? x : max), 0));""", "9")
        choice("What does the `0` stand for in `numbers.reduce((s, n) => s + n, 0)`?", "The starting value", "The first index", "The result", "The number of rounds")
        fill("Calculate the total number of goals.", """
            const games = [{ goals: 2 }, { goals: 0 }, { goals: 3 }];
            const goals = games.reduce((total, g) => total ?? g.goals, 0);
            console.log(goals);
        """, answers = listOf("+"), distractors = listOf("*", "-", "||"), output = "5")
        truth("`reduce` always returns an array.", false)
        write("Use `reduce` to calculate the total price of all items in the shopping cart (`price * quantity`) and print it with 2 decimal places.",
            starter = """
                const cart = [
                  { name: "pen", price: 1.2, quantity: 3 },
                  { name: "notebook", price: 2.5, quantity: 2 },
                  { name: "ruler", price: 0.9, quantity: 1 },
                ];
            """,
            solution = """
                const cart = [
                  { name: "pen", price: 1.2, quantity: 3 },
                  { name: "notebook", price: 2.5, quantity: 2 },
                  { name: "ruler", price: 0.9, quantity: 1 },
                ];
                const total = cart.reduce((sum, item) => sum + item.price * item.quantity, 0);
                console.log(total.toFixed(2));
            """,
            output = "9.50",
            mustContain = listOf("reduce"),
            hint = "`cart.reduce((sum, item) => sum + item.price * item.quantity, 0)` – then `.toFixed(2)`.")
    }

    lesson("Sorting & Chaining", "sort with a compare function, and method chains", LessonIcon.MAGIC) {
        text("`sort()` sorts an array – but careful: without help, it compares everything **as text**. With numbers, that produces nonsense!")
        code("""
            console.log([10, 9, 100, 1].sort());
            console.log(["kiwi", "apple", "mango"].sort());
        """, output = """
            [ 1, 10, 100, 9 ]
            [ 'apple', 'kiwi', 'mango' ]
        """)
        text("For numbers, you pass a **compare function**: `(a, b) => a - b` sorts in ascending order, `(a, b) => b - a` in descending order. If the result is negative, `a` moves to the front.")
        code("""
            const numbers = [10, 9, 100, 1];
            console.log([...numbers].sort((a, b) => a - b));
            console.log([...numbers].sort((a, b) => b - a));

            const players = [
              { name: "Ada", points: 30 },
              { name: "Ben", points: 50 },
              { name: "Cleo", points: 40 },
            ];
            players.sort((a, b) => b.points - a.points);
            console.log(players.map((p) => p.name));
        """, output = """
            [ 1, 9, 10, 100 ]
            [ 100, 10, 9, 1 ]
            [ 'Ben', 'Cleo', 'Ada' ]
        """)
        warn("`sort()` changes the array itself. If you want to keep the original, sort a copy: `[...list].sort(...)`.")
        text("Because `filter` and `map` return new arrays, you can **chain** them – like a conveyor belt:")
        code("""
            const result = [5, 12, 8, 21, 3]
              .filter((n) => n > 4)
              .map((n) => n * 2)
              .sort((a, b) => a - b);
            console.log(result);
        """, output = "[ 10, 16, 24, 42 ]")

        output("""console.log([3, 1, 2].sort((a, b) => a - b));""", "[ 1, 2, 3 ]", "[ 3, 2, 1 ]", "[ 3, 1, 2 ]", "[ 1, 3, 2 ]")
        output("""console.log([25, 3, 100].sort());""", "[ 100, 25, 3 ]", "[ 3, 25, 100 ]", "[ 100, 3, 25 ]", "[ 25, 3, 100 ]",
            explain = "Without a compare function, it sorts as text: \"100\" < \"25\" < \"3\", because the first character is compared first.")
        typeOutput("""console.log([4, 8, 1].sort((a, b) => b - a));""", "[ 8, 4, 1 ]")
        choice("Which compare function sorts numbers in **descending** order?", "(a, b) => b - a", "(a, b) => a - b", "(a, b) => a > b", "(a, b) => 0", mono = true)
        output("""console.log([1, 2, 3, 4, 5].filter((x) => x > 2).map((x) => x * x));""", "[ 9, 16, 25 ]", "[ 1, 4 ]", "[ 3, 4, 5 ]", "[ 9, 16 ]")
        fill("Sort the words by their **length** (shortest first).", """
            const w = ["banana", "kiwi", "apple"];
            w.sort((a, b) => a.length ?? b.length);
            console.log(w);
        """, answers = listOf("-"), distractors = listOf("+", ">", "*"), output = "[ 'kiwi', 'apple', 'banana' ]")
        truth("`[...list].sort()` leaves `list` unchanged.", true)
        write("Print the names of the **three oldest** people in `people` – oldest first, as an array. Chain `sort`, `slice` and `map`.",
            starter = """
                const people = [
                  { name: "Ada", age: 36 },
                  { name: "Ben", age: 12 },
                  { name: "Cleo", age: 71 },
                  { name: "Dev", age: 45 },
                  { name: "Eli", age: 28 },
                ];
            """,
            solution = """
                const people = [
                  { name: "Ada", age: 36 },
                  { name: "Ben", age: 12 },
                  { name: "Cleo", age: 71 },
                  { name: "Dev", age: 45 },
                  { name: "Eli", age: 28 },
                ];
                const oldest = [...people]
                  .sort((a, b) => b.age - a.age)
                  .slice(0, 3)
                  .map((p) => p.name);
                console.log(oldest);
            """,
            output = "[ 'Cleo', 'Dev', 'Ada' ]",
            mustContain = listOf("sort", "map"),
            hint = "`sort((a, b) => b.age - a.age)`, then `slice(0, 3)` and `map((p) => p.name)`.")
    }

    project("Project: Grade Calculator", "Everything from this unit in one program") {
        text("Now you'll combine `map`, `filter`, `reduce` and `sort` into a small grade calculator. A class has written a test – you evaluate the scores.")
        code("""
            const results = [
              { name: "Ada", points: 92 },
              { name: "Ben", points: 58 },
              { name: "Cleo", points: 74 },
            ];
            const average = results.reduce((s, r) => s + r.points, 0) / results.length;
            console.log(`Average: ${D}{average.toFixed(1)}`);
        """, output = "Average: 74.7")

        write("""
            Write a function `grade(points)` that returns a letter grade according to this key:
            - 90 points or more: `"A"`
            - 75 or more: `"B"`
            - 60 or more: `"C"`
            - 50 or more: `"D"`
            - below that: `"F"`
        """.trimIndent(),
            solution = """
                function grade(points) {
                  if (points >= 90) return "A";
                  if (points >= 75) return "B";
                  if (points >= 60) return "C";
                  if (points >= 50) return "D";
                  return "F";
                }
            """,
            test = """
                const cases = [[95, "A"], [90, "A"], [89, "B"], [75, "B"], [60, "C"], [59, "D"], [50, "D"], [12, "F"]];
                for (const [p, g] of cases) {
                  if (grade(p) !== g) throw new Error(`grade(${D}{p}) should be ${D}{g}, but it is ${D}{grade(p)}`);
                }
                console.log("✅ The grading key is correct!");
            """,
            hint = "Check from top to bottom: the first `return` that applies ends the function.")
        write("""
            For every person in `students`, print a line `<name>: <points> points → grade <grade>` – sorted by points, **best first**.
            The function `grade` is already done.
        """.trimIndent(),
            starter = """
                function grade(p) {
                  return p >= 90 ? "A" : p >= 75 ? "B" : p >= 60 ? "C" : p >= 50 ? "D" : "F";
                }
                const students = [
                  { name: "Ada", points: 92 },
                  { name: "Ben", points: 58 },
                  { name: "Cleo", points: 74 },
                  { name: "Dev", points: 81 },
                ];
            """,
            solution = """
                function grade(p) {
                  return p >= 90 ? "A" : p >= 75 ? "B" : p >= 60 ? "C" : p >= 50 ? "D" : "F";
                }
                const students = [
                  { name: "Ada", points: 92 },
                  { name: "Ben", points: 58 },
                  { name: "Cleo", points: 74 },
                  { name: "Dev", points: 81 },
                ];
                [...students]
                  .sort((a, b) => b.points - a.points)
                  .forEach((s) => console.log(`${D}{s.name}: ${D}{s.points} points → grade ${D}{grade(s.points)}`));
            """,
            output = """
                Ada: 92 points → grade A
                Dev: 81 points → grade B
                Cleo: 74 points → grade C
                Ben: 58 points → grade D
            """,
            mustContain = listOf("sort"),
            hint = "Sort a copy in descending order by `points`, then print every line with `forEach`.")
        write("Print how many people in `students` **passed** (any grade except `\"F\"`), in the format `Passed: 3 of 4`.",
            starter = """
                const students = [
                  { name: "Ada", grade: "A" },
                  { name: "Ben", grade: "F" },
                  { name: "Cleo", grade: "C" },
                  { name: "Dev", grade: "D" },
                ];
            """,
            solution = """
                const students = [
                  { name: "Ada", grade: "A" },
                  { name: "Ben", grade: "F" },
                  { name: "Cleo", grade: "C" },
                  { name: "Dev", grade: "D" },
                ];
                const passed = students.filter((s) => s.grade !== "F").length;
                console.log(`Passed: ${D}{passed} of ${D}{students.length}`);
            """,
            output = "Passed: 3 of 4",
            mustContain = listOf("filter"),
            hint = "`students.filter((s) => s.grade !== \"F\").length` counts everyone who passed.")
        output("""
            const p = [70, 85, 40];
            console.log(p.map((x) => (x >= 50 ? "✓" : "✗")).join(" "));
        """, "✓ ✓ ✗", "✓ ✓ ✓", "✗ ✓ ✗", "[ '✓', '✓', '✗' ]")
    }

    checkpoint()
}
