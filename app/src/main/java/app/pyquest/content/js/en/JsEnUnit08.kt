package app.pyquest.content.js.en

import app.pyquest.content.CourseBuilder
import app.pyquest.content.js.D
import app.pyquest.data.LessonIcon

fun CourseBuilder.jsEnUnit08() = unit("Objects", "Data that belongs together, with names") {
    lesson("Creating Objects", "Properties with a name and a value", LessonIcon.BOX) {
        text("An **object** groups values that belong together under names. Every **property** consists of a key and a value. Objects go between curly braces.")
        code("""
            const person = {
              name: "Ada",
              age: 36,
              coder: true,
            };
            console.log(person);
            console.log(person.name);
        """, output = """
            { name: 'Ada', age: 36, coder: true }
            Ada
        """)
        text("You access properties with a **dot** – or with **square brackets** and the name as text. You need the brackets when the name is stored in a variable.")
        code("""
            const car = { brand: "VW", color: "red" };
            console.log(car["brand"]);
            const field = "color";
            console.log(car[field]);
        """, output = """
            VW
            red
        """)
        text("You can change and add properties at any time, and remove them with `delete`:")
        code("""
            const player = { name: "Monty", points: 10 };
            player.points += 5;
            player.level = 2;
            delete player.name;
            console.log(player);
            console.log(player.name);
        """, output = """
            { points: 15, level: 2 }
            undefined
        """)
        tip("Objects can contain other objects and arrays: `person.address.city` reaches into a nested object.")

        output("""
            const book = { title: "Momo", pages: 304 };
            console.log(book.pages);
        """, "304", "Momo", "pages", "undefined")
        output("""
            const animal = { species: "cat" };
            console.log(animal.age);
        """, "undefined", "null", "Error", "0")
        typeOutput("""
            const p = { x: 1, y: 2 };
            p.x = 10;
            console.log(p.x + p.y);
        """, "12")
        choice("How do you access the property whose name is stored in the variable `key`?", "obj[key]", "obj.key", "obj(key)", "obj->key", mono = true,
            explain = "`obj.key` would look for a property that's really called “key”.")
        output("""
            const user = { name: "Ben" };
            user.age = 12;
            console.log(user);
        """, "{ name: 'Ben', age: 12 }", "{ name: 'Ben' }", "{ age: 12 }", "[ 'Ben', 12 ]")
        fill("Get the city from the address.", """
            const customer = { name: "Cleo", address: { city: "London" } };
            console.log(customer.??.city);
        """, answers = listOf("address"), distractors = listOf("name", "city", "customer"), output = "London")
        truth("You can change properties even on a `const` object.", true)
        write("Create an object `phone` with the properties `brand` (`\"Samsung\"`) and `storage` (`256`). Then increase `storage` to `512` and print the object.",
            solution = """
                const phone = { brand: "Samsung", storage: 256 };
                phone.storage = 512;
                console.log(phone);
            """,
            output = "{ brand: 'Samsung', storage: 512 }",
            hint = "`const phone = { brand: \"Samsung\", storage: 256 };` – then `phone.storage = 512;`.")
    }

    lesson("Looping Through Objects", "keys, values, entries and lists of objects", LessonIcon.LOOP) {
        text("`Object.keys()` gives you all the keys as an array, `Object.values()` all the values and `Object.entries()` pairs of key and value.")
        code("""
            const prices = { apple: 0.5, bread: 2.2, milk: 1.1 };
            console.log(Object.keys(prices));
            console.log(Object.values(prices));
            console.log(Object.entries(prices).length);
        """, output = """
            [ 'apple', 'bread', 'milk' ]
            [ 0.5, 2.2, 1.1 ]
            3
        """)
        text("With `for…of` and `Object.entries()` you go through all the pairs. `[key, value]` unpacks each pair straight into two variables:")
        code("""
            const prices = { apple: 0.5, bread: 2.2 };
            for (const [item, price] of Object.entries(prices)) {
              console.log(`${D}{item}: ${D}{price.toFixed(2)} €`);
            }
        """, output = """
            apple: 0.50 €
            bread: 2.20 €
        """)
        text("With `in` you check whether a property exists. And **arrays of objects** are very common – for example a list of players:")
        code("""
            const team = [
              { name: "Ada", score: 95 },
              { name: "Ben", score: 78 },
            ];
            for (const s of team) {
              console.log(s.name, "scored", s.score);
            }
            console.log("score" in team[0]);
        """, output = """
            Ada scored 95
            Ben scored 78
            true
        """)
        tip("There's also `for (const key in obj)`. That gives you the keys – but for arrays, `for…of` is the better choice.")

        output("""console.log(Object.keys({ a: 1, b: 2 }));""", "[ 'a', 'b' ]", "[ 1, 2 ]", "{ a: 1, b: 2 }", "2")
        output("""console.log(Object.values({ x: 3, y: 4 }));""", "[ 3, 4 ]", "[ 'x', 'y' ]", "7", "[ [ 'x', 3 ], [ 'y', 4 ] ]")
        typeOutput("""
            const stock = { apples: 3, pears: 5 };
            let total = 0;
            for (const count of Object.values(stock)) {
              total += count;
            }
            console.log(total);
        """, "8")
        output("""
            const car = { brand: "Fiat" };
            console.log("color" in car);
        """, "false", "true", "undefined", "Error")
        fill("Go through all the key-value pairs.", """
            const team = { goalkeeper: "Ada", striker: "Ben" };
            for (const [role, name] of Object.??(team)) {
              console.log(role, name);
            }
        """, answers = listOf("entries"), distractors = listOf("keys", "values", "pairs"), output = "goalkeeper Ada\nstriker Ben")
        output("""
            const list = [{ n: 1 }, { n: 2 }, { n: 3 }];
            let s = 0;
            for (const e of list) s += e.n;
            console.log(s);
        """, "6", "3", "123", "[object Object]")
        truth("`Object.keys(obj)` gives you the values of an object.", false, explain = "The values come from `Object.values()`. `keys` are the names of the properties.")
        write("For every product in the object `stock`, print one line in the format `<product>: <count> in stock`.",
            starter = """
                const stock = { pens: 12, notebooks: 5, erasers: 3 };
            """,
            solution = """
                const stock = { pens: 12, notebooks: 5, erasers: 3 };
                for (const [product, count] of Object.entries(stock)) {
                  console.log(`${D}{product}: ${D}{count} in stock`);
                }
            """,
            output = """
                pens: 12 in stock
                notebooks: 5 in stock
                erasers: 3 in stock
            """,
            hint = "`for (const [product, count] of Object.entries(stock))`")
        write("Find the movie with the **best rating** in the array `movies` and print its title.",
            starter = """
                const movies = [
                  { title: "Shrek", rating: 7.9 },
                  { title: "Coco", rating: 8.4 },
                  { title: "Cars", rating: 7.2 },
                ];
            """,
            solution = """
                const movies = [
                  { title: "Shrek", rating: 7.9 },
                  { title: "Coco", rating: 8.4 },
                  { title: "Cars", rating: 7.2 },
                ];
                let best = movies[0];
                for (const movie of movies) {
                  if (movie.rating > best.rating) best = movie;
                }
                console.log(best.title);
            """,
            output = "Coco",
            hint = "Remember the best movie so far and compare the `rating` in the loop.")
    }

    lesson("Methods & this", "Objects that can do things", LessonIcon.CLASS) {
        text("A property can also be a **function** – then it's called a **method**. Inside a method, `this` points to the object itself.")
        code("""
            const account = {
              owner: "Ada",
              balance: 100,
              deposit(amount) {
                this.balance += amount;
                return this.balance;
              },
              info() {
                return `${D}{this.owner} has ${D}{this.balance} €`;
              },
            };
            account.deposit(50);
            console.log(account.info());
        """, output = "Ada has 150 €")
        text("`deposit(amount) { ... }` is the short form of `deposit: function (amount) { ... }`. Both work the same way.")
        warn("Arrow functions **don't** have their own `this`. So for methods that need `this`, use the short form – not an arrow function.")
        code("""
            const dog = {
              name: "Rex",
              bark() {
                return this.name + " says woof!";
              },
            };
            console.log(dog.bark());
        """, output = "Rex says woof!")
        tip("Strings and arrays are objects with methods too: `\"abc\".toUpperCase()` or `list.push(4)` work exactly the same way.")

        output("""
            const circle = {
              radius: 2,
              diameter() {
                return this.radius * 2;
              },
            };
            console.log(circle.diameter());
        """, "4", "2", "undefined", "radius * 2")
        output("""
            const counter = {
              value: 0,
              plus() {
                this.value++;
              },
            };
            counter.plus();
            counter.plus();
            console.log(counter.value);
        """, "2", "0", "1", "undefined")
        choice("What does `this` point to in a method like `obj.method()`?", "The object `obj`", "The method itself", "The whole program", "The last variable")
        typeOutput("""
            const cat = {
              name: "Mimi",
              introduce() {
                return "I am " + this.name;
              },
            };
            console.log(cat.introduce());
        """, "I am Mimi")
        fill("Access the object itself inside the method.", """
            const lamp = {
              on: false,
              toggle() {
                ??.on = !??.on;
              },
            };
            lamp.toggle();
            console.log(lamp.on);
        """, answers = listOf("this", "this"), distractors = listOf("self", "lamp.this"), output = "true")
        truth("A method is a function that's stored in an object.", true)
        write("Add a method `area()` to the object `rectangle` that returns `width * height`.",
            starter = """
                const rectangle = {
                  width: 4,
                  height: 3,
                };
            """,
            solution = """
                const rectangle = {
                  width: 4,
                  height: 3,
                  area() {
                    return this.width * this.height;
                  },
                };
            """,
            test = """
                if (typeof rectangle.area !== "function") throw new Error("rectangle needs a method area()");
                if (rectangle.area() !== 12) throw new Error("area() should give 12");
                rectangle.width = 10;
                if (rectangle.area() !== 30) throw new Error("Use this.width and this.height – after width = 10, area() should be 30");
                console.log("✅ All tests passed!");
            """,
            mustContain = listOf("this"),
            hint = "`area() { return this.width * this.height; },`")
    }

    lesson("Destructuring & ?.", "Unpacking values the easy way", LessonIcon.MAGIC) {
        text("With **destructuring** you unpack properties straight into variables with the same name. If a property is missing, you can give a default value.")
        code("""
            const movie = { title: "Coco", year: 2017 };
            const { title, year, language = "English" } = movie;
            console.log(title, year, language);
        """, output = "Coco 2017 English")
        text("With arrays it works by position – and that even lets you swap two variables elegantly:")
        code("""
            const [first, second] = ["gold", "silver", "bronze"];
            console.log(first, second);
            let a = 1;
            let b = 2;
            [a, b] = [b, a];
            console.log(a, b);
        """, output = """
            gold silver
            2 1
        """)
        text("With `...` (spread) you copy an object and can overwrite properties along the way. With `?.` (optional chaining) you safely access parts that might be missing – instead of an error, you get `undefined`.")
        code("""
            const base = { color: "blue", size: "M" };
            const variant = { ...base, color: "red" };
            console.log(variant);

            const customer = { name: "Ben" };
            console.log(customer.address?.city);
            console.log(customer.address?.city ?? "unknown");
        """, output = """
            { color: 'red', size: 'M' }
            undefined
            unknown
        """)
        tip("Destructuring works with parameters too: `function show({ name, age }) { ... }` unpacks the object you pass in right away.")

        output("""
            const { x, y } = { x: 3, y: 7 };
            console.log(x * y);
        """, "21", "37", "10", "undefined")
        output("""
            const [a, , c] = [1, 2, 3];
            console.log(a, c);
        """, "1 3", "1 2", "2 3", "1 undefined")
        typeOutput("""
            const { color = "green" } = {};
            console.log(color);
        """, "green")
        output("""
            const user = {};
            console.log(user.profile?.picture);
        """, "undefined", "Error", "null", "{}")
        output("""
            const a = { x: 1, y: 2 };
            const b = { ...a, y: 5 };
            console.log(a.y, b.y);
        """, "2 5", "5 5", "2 2", "5 2")
        fill("Unpack `name` and `age` from the object.", """
            const person = { name: "Ada", age: 36 };
            const ?? name, age ?? = person;
            console.log(name, age);
        """, answers = listOf("{", "}"), distractors = listOf("[", "]"), output = "Ada 36")
        truth("`customer.address.city` throws an error if `customer.address` is missing – `customer.address?.city` doesn't.", true)
        write("Write a function `describe(animal)` that receives an object with `name` and `species` and returns `<name> the <species>`. If `species` is missing, `animal` should be used. Use destructuring.",
            solution = """
                function describe({ name, species = "animal" }) {
                  return `${D}{name} the ${D}{species}`;
                }
            """,
            test = """
                if (describe({ name: "Monty", species: "snake" }) !== "Monty the snake") throw new Error("Wrong for Monty");
                if (describe({ name: "Rex" }) !== "Rex the animal") throw new Error("Without species, \"animal\" should be used");
                console.log("✅ All tests passed!");
            """,
            hint = "`function describe({ name, species = \"animal\" }) { ... }`")
    }

    checkpoint()
}
