package app.pyquest.content.js.en

import app.pyquest.content.js.D
import app.pyquest.data.GlossaryTerm
import app.pyquest.data.PythonTip
import app.pyquest.data.RefEntry

private fun g(term: String, meaning: String) = GlossaryTerm(term, meaning)

val JsEnGlossary: List<GlossaryTerm> = listOf(
    g("Algorithm", "An exact step-by-step set of instructions for solving a problem – like a recipe for the computer."),
    g("Argument", "The actual value you pass to a function when you call it, e.g. `5` in `square(5)`."),
    g("Array", "An ordered list of values: `[1, 2, 3]`. The first index is 0."),
    g("Arrow function", "A short way to write functions: `(a, b) => a + b`."),
    g("Asynchronous", "Code that finishes later (timers, network) without the program standing still in the meantime."),
    g("async / await", "Keywords that make asynchronous code readable like normal code from top to bottom."),
    g("Expression", "Something that results in a value, e.g. `3 + 4` or `name.length`."),
    g("Condition", "An expression that results in `true` or `false` – e.g. in `if` or `while`."),
    g("Block", "Code between curly braces `{ }`, e.g. the body of a loop or function."),
    g("Boolean", "A data type with the values `true` and `false`."),
    g("Browser", "A program for viewing web pages (Chrome, Firefox, Safari …). Every browser can run JavaScript."),
    g("Bug", "An error in a program. Finding and fixing it is called **debugging**."),
    g("Callback", "A function that is passed to another function so that it can call it later."),
    g("camelCase", "The naming style in JavaScript: first word lowercase, every following word capitalized – `playerCount`."),
    g("Closure", "A function that remembers the variables of its surroundings – even after the outer function has finished."),
    g("Console", "The output window where `console.log()` shows text. In the browser you reach it via the developer tools (F12)."),
    g("const", "Creates a variable that can't be reassigned."),
    g("Destructuring", "Unpacking values from objects or arrays straight into variables: `const { name } = person;`."),
    g("DOM", "Document Object Model – the web page as a tree of objects that JavaScript can change in the browser."),
    g("Property", "A named value in an object, e.g. `person.name`."),
    g("Event loop", "The mechanism that works through timers, Promises and events one after another once the current code is done."),
    g("falsy", "Values that count as false in conditions: `false`, `0`, `\"\"`, `null`, `undefined`, `NaN`."),
    g("Function", "A named, reusable block of code – defined with `function` or as an arrow function."),
    g("Getter", "A method with `get` that is read like a property – without brackets."),
    g("Scope", "The area in which a variable is visible – for `let` and `const`, the surrounding block."),
    g("Hoisting", "JavaScript “pulls” function declarations to the top: you can call a `function` even before its definition."),
    g("Index", "The position of an element in an array or string. It starts at 0."),
    g("Instance", "A concrete object of a class: in `const rex = new Dog()`, `rex` is an instance of `Dog`."),
    g("JSON", "JavaScript Object Notation – a text format for data that almost every app and server understands."),
    g("Class", "A blueprint for objects, defined with `class`."),
    g("Comment", "Text after `//` or between `/*` and `*/` that JavaScript ignores."),
    g("Concatenation", "Joining strings together with `+`: `\"Hello \" + name`."),
    g("Constructor", "The `constructor` method of a class – it runs when an object is created with `new`."),
    g("let", "Creates a variable whose value is allowed to change."),
    g("Literal", "A value written out directly, e.g. `42`, `\"Hi\"`, `[1, 2]` or `{ a: 1 }`."),
    g("Map", "A collection of key-value pairs with methods like `set`, `get` and `has`."),
    g("Method", "A function that belongs to an object: `text.toUpperCase()`."),
    g("Module", "A JavaScript file of its own whose contents are shared with `export` and `import`."),
    g("NaN", "“Not a Number” – the result of invalid calculations, e.g. `Number(\"abc\")`."),
    g("Node.js", "An environment that runs JavaScript outside the browser – e.g. on servers."),
    g("npm", "The package manager of Node.js, used to install ready-made libraries."),
    g("null", "A value meaning “intentionally empty”."),
    g("Object", "A collection of properties: `{ name: \"Ada\", age: 36 }`."),
    g("Operator", "A symbol for an operation, e.g. `+`, `===`, `&&`."),
    g("Parameter", "The placeholder name in a function definition: in `function f(x)`, `x` is a parameter."),
    g("Promise", "An object for a later result – it is fulfilled (`resolve`) or rejected (`reject`)."),
    g("Prototype", "The object from which another object inherits properties and methods. Classes are built on top of it."),
    g("Recursion", "When a function calls itself."),
    g("Rest parameter", "`...values` in the parameter list – gathers any number of arguments in an array."),
    g("Return value", "The value a function gives back with `return`."),
    g("Loop", "Repeats code: `for`, `for…of` or `while`."),
    g("Keyword", "A reserved word with a fixed meaning, e.g. `if`, `for`, `function`, `class`."),
    g("Set", "A collection without duplicate values."),
    g("Spread", "`...` unpacks an array or object: `[...a, ...b]`."),
    g("String", "A sequence of characters / text: `\"Hello\"`."),
    g("Syntax", "The grammar of a programming language – the rules for how code must be written."),
    g("Template string", "A string in backticks in which `${D}{...}` is replaced by the value of an expression."),
    g("this", "Inside a method, the object the method belongs to."),
    g("truthy", "All values that count as true in conditions – i.e. everything except the falsy values."),
    g("Type", "The kind of a value: `number`, `string`, `boolean` … find out with `typeof`."),
    g("Type conversion", "Turning a value into another type, e.g. `Number(\"5\")`."),
    g("undefined", "The value of variables that haven't been given a value yet."),
    g("Variable", "A name for a stored value: `let age = 14;`."),
    g("Inheritance", "A class takes over the properties and methods of another with `extends`."),
    g("Assignment", "Giving a variable a value with `=`."),
)

private fun err(id: String, title: String, summary: String, body: String, code: String) =
    RefEntry("js-$id", title, summary, body.trimIndent().trim(), code.trimIndent().trimEnd(), listOf("error", "bug", id))

val JsEnErrorLexicon: List<RefEntry> = listOf(
    err("syntax", "SyntaxError", "The notation is invalid – the program doesn't even start.", """
        **Typical causes:** a missing bracket `)` or `}`, an unclosed quotation mark, a missing comma between arguments, a word like `else` in the wrong place.

        **How to fix it:** Look at the marked spot (`^`) **and the line before it**. With “Unexpected end of input”, a closing bracket is usually missing at the end.
    """, """
        // Error: bracket missing
        // console.log("Hello";
        console.log("Hello");
    """),
    err("reference", "ReferenceError: … is not defined", "JavaScript doesn't know a name.", """
        **Causes:** a typo (upper/lower case counts too!), a variable that hasn't been created yet, forgotten quotation marks around text.

        Example: `console.log(Message)` instead of `console.log(message)`.
    """, """
        const message = "Hello";
        console.log(message); // not: console.log(Message)
    """),
    err("tdz", "ReferenceError: Cannot access … before initialization", "A variable is used before its `let`/`const` line has run.", """
        With `let` and `const`, a variable only exists from the line where it's created.

        **Solution:** Move the declaration up – before the first use.
    """, """
        // Error: console.log(points); let points = 5;
        let points = 5;
        console.log(points);
    """),
    err("undefined-prop", "TypeError: Cannot read properties of undefined", "To the left of the dot there's `undefined` or `null`.", """
        **Causes:** the variable has no value (yet), a function returns nothing, an array index doesn't exist, or a property is named differently.

        **Solutions:** Check where the value comes from. `obj?.value` stops quietly with `undefined`, `?? "fallback"` provides a replacement value.
    """, """
        const user = { name: "Ada" };
        // Error: user.address.city
        console.log(user.address?.city ?? "no city");
    """),
    err("not-function", "TypeError: … is not a function", "Something that isn't a function is being called with `()`.", """
        **Causes:** a typo in the method name (`toUppercase` instead of `toUpperCase`), an array method on an object (`obj.map`), or a variable hiding the function.
    """, """
        const text = "hello";
        // Error: text.toUppercase()
        console.log(text.toUpperCase());
    """),
    err("const", "TypeError: Assignment to constant variable", "A `const` variable is supposed to get a new value.", """
        `const` prevents new assignments. If the value needs to change, create the variable with `let`.

        Note: You may still change the **contents** of a `const` array or object.
    """, """
        let counter = 0; // not: const counter = 0;
        counter++;
        console.log(counter);
    """),
    err("stack", "RangeError: Maximum call stack size exceeded", "A function keeps calling itself forever.", """
        With recursion, every function needs a **base case** where it stops calling itself.
    """, """
        function countdown(n) {
          if (n === 0) return; // base case – without it, it runs forever
          console.log(n);
          countdown(n - 1);
        }
        countdown(3);
    """),
    err("nan", "NaN as a result", "No error message – but an unusable result.", """
        `NaN` appears when you calculate with something that isn't a number – e.g. `undefined` or text like `"abc"`.

        **Tip:** Convert input with `Number()` and check with `Number.isNaN()`.
    """, """
        const input = "12a";
        const number = Number(input);
        if (Number.isNaN(number)) {
          console.log("Please enter a valid number!");
        }
    """),
    err("concat", "\"5\" + 3 = \"53\"", "Plus joins text instead of calculating.", """
        If one side of `+` is a string, JavaScript sticks the values together. This often happens with input from `prompt()`.

        **Solution:** convert with `Number()` first.
    """, """
        const a = "5";
        console.log(a + 3);         // "53"
        console.log(Number(a) + 3); // 8
    """),
    err("object-object", "[object Object] in the text", "An object is put directly into a string.", """
        Objects have no useful text form. Insert their properties instead – or use `JSON.stringify(obj)`.
    """, """
        const p = { name: "Ada", age: 36 };
        console.log("Person: " + JSON.stringify(p));
        console.log(`${D}{p.name} is ${D}{p.age}`);
    """),
    err("pending", "Promise { <pending> }", "Instead of the result, you only see a Promise.", """
        Asynchronous functions return a Promise. You have to wait for the result with `await` (in an `async` function) or with `.then()`.
    """, """
        async function load() {
          return 42;
        }
        async function main() {
          const value = await load(); // not: const value = load();
          console.log(value);
        }
        main();
    """),
    err("equals", "= instead of ===", "An assignment inside a condition.", """
        `if (x = 5)` stores 5 in `x` and is always true. To compare, you need `===`.
    """, """
        let x = 3;
        if (x === 5) {
          console.log("five");
        } else {
          console.log("not five");
        }
    """),
)

private fun tip(title: String, code: String) = PythonTip(title, code.trimIndent().trim())

val JsEnTips: List<PythonTip> = listOf(
    tip("Swap values without a helper variable", "[a, b] = [b, a];"),
    tip("Remove duplicates", "const unique = [...new Set(list)];"),
    tip("Fallback value with ??", "const name = input ?? \"Guest\";"),
    tip("Safe access with ?.", "const city = user?.address?.city;"),
    tip("Numbers from 1 to n", "const numbers = Array.from({ length: 5 }, (_, i) => i + 1);"),
    tip("Last element", "const last = list.at(-1);"),
    tip("Random element", "const random = list[Math.floor(Math.random() * list.length)];"),
    tip("Reverse a string", "const backwards = [...text].reverse().join(\"\");"),
    tip("Copy and change an object", "const updated = { ...old, color: \"red\" };"),
    tip("Sum of an array", "const sum = numbers.reduce((s, n) => s + n, 0);"),
    tip("Sort by property", "players.sort((a, b) => b.points - a.points);"),
    tip("Loop over an object", "for (const [key, value] of Object.entries(obj)) {\n  console.log(key, value);\n}"),
    tip("Leading zeros", "const number = String(7).padStart(3, \"0\"); // \"007\""),
    tip("Round to 2 places", "const rounded = Math.round(value * 100) / 100;"),
    tip("Table in the console", "console.table([{ name: \"Ada\", points: 9 }, { name: \"Ben\", points: 7 }]);"),
    tip("Maximum of an array", "const max = Math.max(...numbers);"),
    tip("Check for a whole number", "Number.isInteger(4.0); // true"),
    tip("Empty an array", "list.length = 0;"),
    tip("Conditional execution with &&", "isLoggedIn && console.log(\"Welcome back!\");"),
    tip("Assign only if null/undefined", "settings.color ??= \"blue\";"),
    tip("Waiting with await", "const wait = (ms) => new Promise((r) => setTimeout(r, ms));\nasync function main() {\n  await wait(1000);\n}"),
    tip("Loading in parallel", "async function load() {\n  const [profile, photos] = await Promise.all([loadProfile(), loadPhotos()]);\n}"),
    tip("Unpack parameters", "function show({ name, age = 0 }) {\n  console.log(name, age);\n}"),
    tip("Compare text ignoring case", "const same = a.toLowerCase() === b.toLowerCase();"),
    tip("Pretty JSON output", "console.log(JSON.stringify(data, null, 2));"),
    tip("Deep copy", "const copy = structuredClone(original);"),
    tip("Freeze an object", "const config = Object.freeze({ color: \"blue\" });"),
    tip("Number with thousands separator", "(1234567.891).toLocaleString(\"en-US\"); // \"1,234,567.891\""),
    tip("Counting with an object", "counter[word] = (counter[word] ?? 0) + 1;"),
    tip("Multi-line text", "const letter = `Hello,\nhow's it going?`;"),
)
