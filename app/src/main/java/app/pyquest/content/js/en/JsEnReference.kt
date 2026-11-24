package app.pyquest.content.js.en

import app.pyquest.content.js.D
import app.pyquest.data.LessonIcon
import app.pyquest.data.RefCategory
import app.pyquest.data.RefEntry

private class JsEnCategoryBuilder(private val id: String) {
    val entries = mutableListOf<RefEntry>()
    fun e(title: String, summary: String, body: String = "", code: String? = null, vararg keywords: String) {
        entries += RefEntry(
            id = "$id-${entries.size + 1}",
            title = title,
            summary = summary,
            body = body.trimIndent().trim(),
            code = code?.trimIndent()?.trimEnd(),
            keywords = keywords.toList(),
        )
    }
}

private fun category(id: String, title: String, icon: LessonIcon, block: JsEnCategoryBuilder.() -> Unit) =
    RefCategory(id, title, icon, JsEnCategoryBuilder(id).apply(block).entries.toList())

/** JavaScript reference in English. Same category and entry IDs as the German one (prefix "js-"). */
val JsEnReferenceCategories: List<RefCategory> = listOf(
    category("js-basics", "Basics", LessonIcon.BOOK) {
        e("console.log() – Output", "Prints values to the console.",
            "Separate several values with commas – a space is placed between them. `console.error()` prints error messages, `console.table()` draws tables.",
            """
            console.log("Hello");
            console.log("Age:", 12, true);
            console.table([{ name: "Ada", points: 9 }]);
            """, "output", "print", "console")
        e("let and const", "Creating variables.",
            "`const` for values that are never reassigned, `let` for variables that change. Names are written in camelCase. `var` is outdated.",
            """
            const name = "Monty";
            let points = 10;
            points += 5;
            console.log(name, points);
            """, "variable", "var", "camelCase")
        e("Comments", "Notes that JavaScript ignores.",
            "`//` to the end of the line, `/* ... */` across several lines.",
            """
            // Single-line comment
            /* Comment across
               several lines */
            console.log("Comments are ignored"); // here too
            """, "//", "note")
        e("prompt() – Input", "Asks the user for text.",
            "`prompt()` **always returns a string** – or `null` if the user cancelled. For numbers: `Number(prompt(...))`.",
            """
            // In the playground an input field appears:
            // const age = Number(prompt("How old are you?"));
            const input = "12";
            const age = Number(input);
            console.log(age + 1);
            """, "input", "keyboard")
        e("Arithmetic operators", "`+ - * / % **`",
            "`%` gives the remainder, `**` the power. For whole-number division: `Math.floor(a / b)`. Shorthands: `x += 2`, `x++`, `x--`.",
            """
            console.log(7 / 2, 7 % 2, 2 ** 8);
            console.log(Math.floor(7 / 2));
            let x = 5;
            x *= 3;
            x++;
            console.log(x);
            """, "plus", "modulo", "power")
        e("Data types", "number, string, boolean, undefined, null, object …",
            "Use `typeof` to ask for the type. Arrays and `null` give `\"object\"` – you can recognize arrays with `Array.isArray()`.",
            """
            console.log(typeof 42, typeof "Hi", typeof true);
            console.log(typeof undefined, typeof null, typeof {});
            console.log(Array.isArray([1, 2]));
            """, "typeof", "type")
        e("Converting types", "Number(), String(), parseInt(), parseFloat()",
            "Invalid numbers result in `NaN` (Not a Number) – check with `Number.isNaN()`. Careful: `\"5\" + 3` is `\"53\"`.",
            """
            console.log(Number("42") + 1);
            console.log(String(7) + 7);
            console.log(parseInt("12px"), parseFloat("3.5kg"));
            console.log(Number("abc"), Number.isNaN(Number("abc")));
            """, "NaN", "parseInt", "convert")
        e("Template strings", "Text with built-in values: `` `${D}{x}` ``",
            "Template strings are written in backticks, may span several lines, and insert any expression with `${D}{...}`.",
            """
            const name = "Ada";
            const points = 7;
            console.log(`${D}{name} has ${D}{points * 10} points.`);
            """, "backtick", "interpolation", "f-string")
    },
    category("js-strings", "Strings", LessonIcon.CHAT) {
        e("Length & characters", "`text.length`, `text[0]`, `text.at(-1)`",
            "The index starts at 0. An index out of range gives `undefined`. Strings can't be changed – methods return new strings.",
            """
            const t = "Monty";
            console.log(t.length, t[0], t.at(-1));
            """, "length", "index")
        e("Upper & lower case", "`toUpperCase()`, `toLowerCase()`",
            "Handy for comparing input regardless of how it was typed.",
            """
            console.log("Hello".toUpperCase(), "HELLO".toLowerCase());
            console.log("YES".toLowerCase() === "yes");
            """, "upper", "lower")
        e("trim()", "Remove spaces at the edges.",
            "`trimStart()` and `trimEnd()` remove only on the left or right.",
            """
            console.log(`[${D}{"  Hello  ".trim()}]`);
            """, "strip", "spaces")
        e("Searching", "`includes()`, `startsWith()`, `endsWith()`, `indexOf()`",
            "`indexOf` returns the position or `-1` if nothing was found.",
            """
            const sentence = "JavaScript is great";
            console.log(sentence.includes("is"), sentence.startsWith("Java"), sentence.endsWith("!"));
            console.log(sentence.indexOf("great"), sentence.indexOf("Python"));
            """, "search", "contains")
        e("slice()", "Cut out a piece.",
            "`slice(start, end)` – the character at `end` is no longer included. Negative values count from the back.",
            """
            const w = "Chocolate";
            console.log(w.slice(0, 5), w.slice(5), w.slice(-4));
            """, "substring", "part")
        e("split() & join()", "String ↔ array",
            "`split(separator)` splits a string into an array, `join(separator)` joins an array into a string.",
            """
            const parts = "a,b,c".split(",");
            console.log(parts);
            console.log(parts.join(" | "));
            console.log([..."Hey"].reverse().join(""));
            """, "split", "join", "combine")
        e("Replacing & repeating", "`replace()`, `replaceAll()`, `repeat()`, `padStart()`",
            "`replace` replaces the first occurrence, `replaceAll` all of them. `padStart(length, char)` pads at the front.",
            """
            console.log("Tea and Tea".replace("Tea", "Cocoa"));
            console.log("a-b-c".replaceAll("-", "+"));
            console.log("ab".repeat(3), "5".padStart(3, "0"));
            """, "replace", "pad")
    },
    category("js-control", "Decisions", LessonIcon.BRANCH) {
        e("if / else if / else", "Run code depending on conditions.",
            "The condition goes in round brackets, the code in curly braces. The first matching branch wins.",
            """
            const temp = 18;
            if (temp > 25) {
              console.log("hot");
            } else if (temp > 15) {
              console.log("pleasant");
            } else {
              console.log("cold");
            }
            """, "when", "condition")
        e("Comparisons", "`===`, `!==`, `<`, `>`, `<=`, `>=`",
            "Always use `===` and `!==` – `==` secretly converts types (`5 == \"5\"` is `true`).",
            """
            console.log(5 === 5, 5 === "5", 5 == "5");
            console.log("a" < "b", 3 >= 3);
            """, "equal", "not equal", "==")
        e("Logical operators", "`&&` (and), `||` (or), `!` (not)",
            "`&&` needs both sides, `||` at least one. Falsy values are: `false`, `0`, `\"\"`, `null`, `undefined`, `NaN`.",
            """
            const age = 15;
            console.log(age > 12 && age < 18);
            console.log(age < 6 || age > 65);
            console.log(!true, Boolean(""), Boolean("0"));
            """, "and", "or", "truthy", "falsy")
        e("Conditional operator ?:", "A short choice between two values.",
            "`condition ? ifYes : ifNo` returns a value – handy in assignments and template strings.",
            """
            const points = 72;
            console.log(points >= 50 ? "passed" : "failed");
            """, "ternary")
        e("switch", "Compare one value with many cases.",
            "Each case ends with `break`, otherwise the program falls through into the next case. `default` applies when nothing matches.",
            """
            const day = "Sun";
            switch (day) {
              case "Sat":
              case "Sun":
                console.log("Weekend");
                break;
              default:
                console.log("Weekday");
            }
            """, "case", "break", "default")
        e("?? and ?.", "Fallback values and safe access.",
            "`a ?? b` returns `b` if `a` is `null` or `undefined`. `obj?.x` returns `undefined` instead of throwing an error if `obj` is missing.",
            """
            const user = { name: "Ben" };
            console.log(user.nickname ?? "no nickname");
            console.log(user.address?.city);
            """, "nullish", "optional chaining")
    },
    category("js-loops", "Loops", LessonIcon.LOOP) {
        e("while", "Repeat as long as a condition holds.",
            "Make sure the condition eventually becomes `false` – otherwise the loop runs forever.",
            """
            let n = 3;
            while (n > 0) {
              console.log(n);
              n--;
            }
            """, "while", "endless")
        e("for", "Counting loop: start; condition; step",
            "`for (let i = 0; i < 5; i++)` runs for i = 0, 1, 2, 3, 4.",
            """
            for (let i = 1; i <= 3; i++) {
              console.log("Round", i);
            }
            """, "count", "index")
        e("for…of", "Go through all elements.",
            "Works with arrays, strings, Maps and Sets. For objects: `Object.entries(obj)`.",
            """
            for (const animal of ["Dog", "Cat"]) {
              console.log(animal);
            }
            for (const char of "Hi") {
              console.log(char);
            }
            """, "foreach", "iterate")
        e("break & continue", "End loops or skip rounds.",
            "`break` ends the loop immediately, `continue` jumps to the next round.",
            """
            for (let i = 1; i <= 10; i++) {
              if (i % 2 === 0) continue;
              if (i > 7) break;
              console.log(i);
            }
            """, "stop", "skip")
        e("for…in", "Go through an object's keys.",
            "For arrays, prefer `for…of` – `for…in` gives you the indices as strings there.",
            """
            const prices = { apple: 1, pear: 2 };
            for (const item in prices) {
              console.log(item, prices[item]);
            }
            """, "keys")
    },
    category("js-arrays", "Arrays", LessonIcon.BOX) {
        e("Creating arrays", "`[1, 2, 3]`, index, `length`",
            "The first index is 0, the last one is `length - 1`. Even `const` arrays can be changed.",
            """
            const colors = ["red", "green", "blue"];
            colors[1] = "yellow";
            console.log(colors, colors.length, colors.at(-1));
            """, "list", "index")
        e("Adding & removing", "`push`, `pop`, `unshift`, `shift`, `splice`",
            "`push`/`pop` work at the end, `unshift`/`shift` at the front. `splice(index, count)` removes anywhere.",
            """
            const a = [2, 3];
            a.push(4);
            a.unshift(1);
            console.log(a);
            a.splice(1, 2);
            console.log(a, a.pop(), a);
            """, "append", "remove")
        e("Searching", "`includes`, `indexOf`, `find`, `findIndex`",
            "`includes` returns `true`/`false`, `indexOf` the position or `-1`.",
            """
            const team = ["Ada", "Ben"];
            console.log(team.includes("Ben"), team.indexOf("Ben"), team.indexOf("Zoe"));
            """, "contains", "position")
        e("Copying & joining", "`[...a]`, `slice()`, `concat()`, `join()`",
            "`const b = a` doesn't copy – both names point to the same array. A real copy: `[...a]`.",
            """
            const a = [1, 2];
            const copy = [...a, 3];
            console.log(a, copy, a.concat([9]).join("-"));
            """, "copy", "spread")
        e("Sorting", "`sort()` with a compare function",
            "Without a compare function it sorts as text! For numbers: `(a, b) => a - b`. `sort()` changes the array itself.",
            """
            const z = [10, 9, 100, 1];
            console.log([...z].sort());
            console.log([...z].sort((a, b) => a - b));
            console.log(["Kiwi", "Apple"].sort());
            """, "order", "sequence")
        e("reverse() & nesting", "Reversing and arrays inside arrays",
            "`reverse()` reverses the array itself. You reach nested arrays with two indices: `grid[row][column]`.",
            """
            console.log([1, 2, 3].reverse());
            const grid = [[1, 2], [3, 4]];
            console.log(grid[1][0]);
            """, "flip", "2d", "matrix")
    },
    category("js-array-methods", "Array Methods", LessonIcon.MAGIC) {
        e("forEach", "Do something for each element.",
            "Receives the element and the index. Returns nothing.",
            """
            ["a", "b"].forEach((value, i) => console.log(i, value));
            """, "loop", "callback")
        e("map", "Transform every element → new array.",
            "The original stays unchanged.",
            """
            console.log([1, 2, 3].map((x) => x * 10));
            console.log(["ada", "ben"].map((n) => n.toUpperCase()));
            """, "convert", "transform")
        e("filter", "Keep matching elements → new array.",
            "The function returns `true` for elements that should stay.",
            """
            console.log([3, 8, 12, 5].filter((x) => x > 6));
            """, "select", "filter")
        e("find, some, every", "Find the first element, check whether one/all match.",
            "`find` returns the element (or `undefined`), `some`/`every` return `true`/`false`.",
            """
            const z = [4, 7, 10];
            console.log(z.find((x) => x > 5), z.some((x) => x > 9), z.every((x) => x > 5));
            """, "search", "all", "at least")
        e("reduce", "Combine everything into one value.",
            "`array.reduce((soFar, element) => ..., startValue)` – e.g. for sums.",
            """
            const sum = [5, 10, 15].reduce((s, n) => s + n, 0);
            console.log(sum);
            """, "sum", "combine")
        e("Chaining methods", "`filter(...).map(...).sort(...)`",
            "Because `filter` and `map` return new arrays, you can call them one right after another.",
            """
            const result = [5, 12, 8, 21]
              .filter((n) => n > 6)
              .map((n) => n * 2)
              .sort((a, b) => a - b);
            console.log(result);
            """, "chain", "chaining")
    },
    category("js-functions", "Functions", LessonIcon.CODE) {
        e("function", "Define and call a function.",
            "Parameters go in the round brackets. You call it with `name(arguments)`.",
            """
            function greet(name) {
              console.log("Hello " + name);
            }
            greet("Ada");
            """, "def", "call")
        e("return", "Give back a result.",
            "`return` ends the function. Without `return` it returns `undefined`.",
            """
            function square(x) {
              return x * x;
            }
            console.log(square(4) + 1);
            """, "result", "output")
        e("Default values", "`function f(x = 10)`",
            "Applies if no value (or `undefined`) is passed in the call.",
            """
            function welcome(name = "Guest") {
              return "Hello " + name;
            }
            console.log(welcome(), welcome("Ben"));
            """, "default", "parameter")
        e("Arrow functions", "`(a, b) => a + b`",
            "Short notation. With just one expression, no `{}` and no `return`. They don't have their own `this`.",
            """
            const plus = (a, b) => a + b;
            const twice = (x) => x * 2;
            console.log(plus(2, 3), twice(7));
            """, "arrow", "lambda", "=>")
        e("Rest parameters", "`function f(...values)`",
            "Gathers any number of arguments in an array.",
            """
            function sum(...numbers) {
              return numbers.reduce((s, n) => s + n, 0);
            }
            console.log(sum(1, 2, 3, 4));
            """, "variadic", "arguments")
        e("Callbacks", "Pass functions as arguments.",
            "Functions are values – you can store them and pass them to other functions.",
            """
            function twice(action) {
              action();
              action();
            }
            twice(() => console.log("Hey"));
            """, "callback", "higher-order function")
        e("Closures", "Functions remember their surroundings.",
            "An inner function keeps access to variables of the outer function – even after that has finished.",
            """
            function counter() {
              let n = 0;
              return () => ++n;
            }
            const count = counter();
            count();
            console.log(count());
            """, "scope")
    },
    category("js-objects", "Objects & JSON", LessonIcon.CLASS) {
        e("Creating objects", "`{ key: value }`",
            "Access with `obj.name` or `obj[\"name\"]`. Properties can be added, changed and removed with `delete`.",
            """
            const car = { brand: "VW", year: 2019 };
            car.color = "red";
            console.log(car.brand, car["year"], car);
            """, "dictionary", "property")
        e("keys, values, entries", "Looping over objects.",
            "`Object.keys/values/entries(obj)` return arrays – combine them with `for…of`.",
            """
            const prices = { apple: 1, bread: 3 };
            console.log(Object.keys(prices), Object.values(prices));
            for (const [item, price] of Object.entries(prices)) {
              console.log(item, price);
            }
            """, "keys", "values")
        e("Methods & this", "Functions inside objects.",
            "In a method, `this` points to the object. Shorthand: `name() { ... }`.",
            """
            const account = {
              balance: 10,
              deposit(amount) {
                this.balance += amount;
              },
            };
            account.deposit(5);
            console.log(account.balance);
            """, "method", "this")
        e("Destructuring", "Unpacking values.",
            "`const { a, b } = obj;` and `const [x, y] = arr;` – also with default values.",
            """
            const { name, age = 0 } = { name: "Ada" };
            const [first, second] = [10, 20, 30];
            console.log(name, age, first, second);
            """, "unpack", "destructuring")
        e("Spread with objects", "`{ ...obj, x: 1 }`",
            "Copies all properties into a new object – later entries overwrite earlier ones.",
            """
            const base = { color: "blue", size: "M" };
            console.log({ ...base, color: "red" });
            """, "copy", "merge")
        e("JSON", "`JSON.stringify()` and `JSON.parse()`",
            "Save data as text and read it back in. Invalid JSON → `SyntaxError`.",
            """
            const text = JSON.stringify({ a: 1, b: [true] });
            console.log(text);
            console.log(JSON.parse(text).b[0]);
            """, "save", "serialize")
    },
    category("js-classes", "Classes", LessonIcon.CLASS) {
        e("class & constructor", "A blueprint for objects.",
            "`new` creates an instance, the `constructor` sets it up.",
            """
            class Dog {
              constructor(name) {
                this.name = name;
              }
              bark() {
                return this.name + ": Woof!";
              }
            }
            console.log(new Dog("Rex").bark());
            """, "object", "instance", "new")
        e("get & set", "Computed and validated properties.",
            "Getters are used without brackets. Setters kick in on assignment.",
            """
            class Circle {
              constructor(r) {
                this.r = r;
              }
              get area() {
                return (Math.PI * this.r ** 2).toFixed(1);
              }
            }
            console.log(new Circle(2).area);
            """, "getter", "setter")
        e("extends & super", "Inheritance.",
            "The subclass takes over everything from the parent class. `super(...)` calls its constructor, `super.method()` its method.",
            """
            class Animal {
              constructor(name) {
                this.name = name;
              }
              sound() {
                return "...";
              }
            }
            class Cat extends Animal {
              sound() {
                return "Meow";
              }
            }
            const k = new Cat("Mimi");
            console.log(k.name, k.sound(), k instanceof Animal);
            """, "inheritance", "instanceof")
        e("static & #private", "Class properties and protected fields.",
            "`static` belongs to the class itself, `#field` is only accessible inside the class.",
            """
            class Counter {
              static created = 0;
              #value = 0;
              constructor() {
                Counter.created++;
              }
              plus() {
                return ++this.#value;
              }
            }
            const c = new Counter();
            c.plus();
            console.log(c.plus(), Counter.created);
            """, "static", "private", "encapsulation")
    },
    category("js-errors", "Errors", LessonIcon.BUG) {
        e("try / catch / finally", "Catching errors.",
            "If an error occurs in the `try` block, execution continues in `catch`. `finally` always runs.",
            """
            try {
              null.value;
            } catch (e) {
              console.log(e.name, "-", e.message);
            } finally {
              console.log("done");
            }
            """, "catch", "exception")
        e("throw", "Raising an error yourself.",
            "Usual: `throw new Error(\"description\")`, `TypeError` for wrong types, `RangeError` for disallowed values.",
            """
            function root(x) {
              if (x < 0) throw new RangeError("x must be positive");
              return Math.sqrt(x);
            }
            try {
              root(-1);
            } catch (e) {
              console.log(e.name + ": " + e.message);
            }
            """, "raise", "trigger")
        e("Custom error classes", "`class MyError extends Error`",
            "Set `this.name` so the kind of error appears in messages. With `instanceof` you tell errors apart in `catch`.",
            """
            class InputError extends Error {
              constructor(m) {
                super(m);
                this.name = "InputError";
              }
            }
            try {
              throw new InputError("Number missing");
            } catch (e) {
              console.log(e instanceof InputError, `${D}{e.name}: ${D}{e.message}`);
            }
            """, "custom", "error class")
    },
    category("js-async", "Async", LessonIcon.ROCKET) {
        e("setTimeout & setInterval", "Run later or repeatedly.",
            "Times are in milliseconds. The program doesn't wait but carries on immediately. `clearInterval(id)` stops an interval timer.",
            """
            setTimeout(() => console.log("after 1 s"), 1000);
            let n = 0;
            const id = setInterval(() => {
              n++;
              console.log("Tick", n);
              if (n === 2) clearInterval(id);
            }, 300);
            console.log("immediately");
            """, "timer", "wait", "delay")
        e("Promise", "A result that arrives later.",
            "`new Promise((resolve, reject) => ...)`, react with `.then()` and `.catch()`.",
            """
            const p = new Promise((resolve) => setTimeout(() => resolve(42), 200));
            p.then((value) => console.log("Result:", value));
            """, "promise", "then", "catch")
        e("async & await", "Write asynchronous code like synchronous code.",
            "`await` waits for a Promise – only inside an `async` function. Catch errors with `try`/`catch`.",
            """
            const wait = (ms) => new Promise((r) => setTimeout(r, ms));
            async function main() {
              console.log("Start");
              await wait(500);
              console.log("After half a second");
            }
            main();
            """, "wait", "asynchronous")
        e("Promise.all", "Wait for several Promises at once.",
            "Returns an array with all results in the original order. `Promise.race` returns the fastest.",
            """
            const load = (x, ms) => new Promise((r) => setTimeout(() => r(x), ms));
            Promise.all([load("A", 300), load("B", 100)]).then((e) => console.log(e));
            """, "parallel", "simultaneous")
    },
    category("js-tools", "Tools", LessonIcon.STAR) {
        e("Set", "A collection without duplicates.",
            "`add`, `has`, `delete`, `size`. Remove duplicates: `[...new Set(array)]`.",
            """
            const s = new Set([1, 2, 2, 3]);
            s.add(4);
            console.log(s.size, s.has(2), [...s]);
            """, "set", "unique")
        e("Map", "Key-value pairs with methods.",
            "`set`, `get`, `has`, `delete`, `size` – keys can be any values.",
            """
            const m = new Map();
            m.set("Ada", 36);
            m.set("Ben", 12);
            console.log(m.get("Ada"), m.size);
            for (const [name, age] of m) console.log(name, age);
            """, "dictionary", "lookup")
        e("Math", "Rounding, minimum, maximum, square root …",
            "`round`, `floor`, `ceil`, `max`, `min`, `abs`, `sqrt`, `PI`.",
            """
            console.log(Math.round(2.5), Math.floor(2.9), Math.ceil(2.1));
            console.log(Math.max(1, 5, 3), Math.sqrt(16), Math.PI.toFixed(2));
            """, "round", "math")
        e("Random numbers", "`Math.random()`",
            "Returns a number from 0 up to (but not including) 1. Whole number from 1 to 6: `Math.floor(Math.random() * 6) + 1`.",
            """
            const roll = Math.floor(Math.random() * 6) + 1;
            console.log(roll >= 1 && roll <= 6);
            """, "random", "dice")
        e("Date", "Date and time.",
            "Months count from 0! `getFullYear()`, `getMonth()`, `getDate()`, `getDay()` (0 = Sunday).",
            """
            const d = new Date(2025, 11, 24);
            console.log(d.getFullYear(), d.getMonth() + 1, d.getDate());
            """, "date", "time", "clock")
        e("Rounding decimals", "`toFixed()` and the 0.1 problem",
            "Decimal numbers aren't always exact in a computer. `toFixed(n)` returns rounded text with n decimal places.",
            """
            console.log(0.1 + 0.2);
            console.log((0.1 + 0.2).toFixed(2));
            console.log(Math.round(2.345 * 100) / 100);
            """, "floating point", "decimal places")
    }
)
