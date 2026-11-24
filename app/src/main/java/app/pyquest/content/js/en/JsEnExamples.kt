package app.pyquest.content.js.en

import app.pyquest.data.ExampleProgram

private fun ex(category: String, title: String, description: String, code: String, input: List<String> = emptyList()) =
    ExampleProgram(title, description, code.trimIndent().trimEnd() + "\n", category, input)

/** Example programs for the JavaScript playground (English). */
val JsEnExamplePrograms: List<ExampleProgram> = listOf(
    ex("Basics", "Hello World", "The classic first program.", """
        console.log("Hello, world! 👋");
        console.log("I'm coding with JavaScript", 2025);
    """),
    ex("Basics", "Greeting", "Asks for your name and age.", """
        const name = prompt("What's your name?");
        const age = Number(prompt("How old are you?"));
        console.log(`Hello ${'$'}{name}!`);
        console.log(`In 10 years you'll be ${'$'}{age + 10} years old.`);
    """, listOf("Ada", "14")),
    ex("Basics", "Calculator", "Calculates with two numbers and an operator.", """
        console.log("Mini calculator 🧮");
        const a = Number(prompt("First number:"));
        const op = prompt("Operator (+ - * /):");
        const b = Number(prompt("Second number:"));

        let result;
        switch (op) {
          case "+":
            result = a + b;
            break;
          case "-":
            result = a - b;
            break;
          case "*":
            result = a * b;
            break;
          case "/":
            result = b === 0 ? "Error: division by 0!" : a / b;
            break;
          default:
            result = "Unknown operator";
        }
        console.log("Result:", result);
    """, listOf("12", "*", "3.5")),
    ex("Basics", "Temperature Table", "Convert Celsius to Fahrenheit – neatly aligned.", """
        console.log("Celsius | Fahrenheit");
        console.log("--------+-----------");
        for (let celsius = -10; celsius <= 40; celsius += 10) {
          const fahrenheit = celsius * 9 / 5 + 32;
          console.log(`${'$'}{String(celsius).padStart(7)} | ${'$'}{fahrenheit.toFixed(1).padStart(9)}`);
        }
    """),
    ex("Games", "Number Guessing", "Guess Monty's secret number between 1 and 100.", """
        const secret = Math.floor(Math.random() * 100) + 1;
        let attempts = 0;
        console.log("I'm thinking of a number between 1 and 100.");

        while (true) {
          const input = prompt("Your guess:");
          if (input === null) break; // cancelled
          const guess = Number(input);
          if (!Number.isInteger(guess)) {
            console.log("Please enter a whole number!");
            continue;
          }
          attempts++;
          if (guess < secret) {
            console.log("Too low! ⬆️");
          } else if (guess > secret) {
            console.log("Too high! ⬇️");
          } else {
            console.log(`Correct! 🎉 It took you ${'$'}{attempts} attempts.`);
            break;
          }
        }
    """, listOf("abc") + (1..100).map { it.toString() }),
    ex("Games", "Rock, Paper, Scissors", "Three rounds against Monty.", """
        const options = ["rock", "paper", "scissors"];
        const beats = { rock: "scissors", paper: "rock", scissors: "paper" };
        let yourScore = 0;
        let montyScore = 0;

        for (let round = 1; round <= 3; round++) {
          const choice = (prompt(`Round ${'$'}{round} – rock, paper or scissors?`) ?? "").trim().toLowerCase();
          if (!options.includes(choice)) {
            console.log("That doesn't count – round lost!");
            montyScore++;
            continue;
          }
          const monty = options[Math.floor(Math.random() * 3)];
          console.log(`Monty picks ${'$'}{monty}.`);
          if (choice === monty) {
            console.log("It's a tie!");
          } else if (beats[choice] === monty) {
            console.log("Point for you! 🎉");
            yourScore++;
          } else {
            console.log("Point for Monty! 🐍");
            montyScore++;
          }
        }
        console.log(`Final score: You ${'$'}{yourScore} – ${'$'}{montyScore} Monty`);
    """, listOf("rock", "paper", "scissors")),
    ex("Games", "Dice Statistics", "Rolls a die 600 times and shows how often each number came up.", """
        const counts = [0, 0, 0, 0, 0, 0];
        for (let i = 0; i < 600; i++) {
          const roll = Math.floor(Math.random() * 6) + 1;
          counts[roll - 1]++;
        }
        counts.forEach((n, i) => {
          console.log(`${'$'}{i + 1}: ${'$'}{"■".repeat(Math.round(n / 10))} ${'$'}{n}`);
        });
    """),
    ex("Math", "FizzBuzz", "The classic from job interviews.", """
        for (let i = 1; i <= 20; i++) {
          if (i % 15 === 0) console.log("FizzBuzz");
          else if (i % 3 === 0) console.log("Fizz");
          else if (i % 5 === 0) console.log("Buzz");
          else console.log(i);
        }
    """),
    ex("Math", "Prime Numbers", "Finds all prime numbers up to 100.", """
        function isPrime(n) {
          if (n < 2) return false;
          for (let divisor = 2; divisor * divisor <= n; divisor++) {
            if (n % divisor === 0) return false;
          }
          return true;
        }

        const primes = [];
        for (let n = 2; n <= 100; n++) {
          if (isPrime(n)) primes.push(n);
        }
        console.log(primes.join(", "));
        console.log(`That's ${'$'}{primes.length} prime numbers.`);
    """),
    ex("Math", "Fibonacci", "Each number is the sum of the two before it.", """
        let a = 0;
        let b = 1;
        const sequence = [];
        while (sequence.length < 15) {
          sequence.push(a);
          [a, b] = [b, a + b];
        }
        console.log(sequence.join(" → "));
    """),
    ex("Data", "Shopping List", "Arrays with push, filter and map.", """
        const list = ["Apples", "Bread", "Milk"];
        list.push("Cheese");
        list.unshift("Coffee");

        console.log("Shopping list:");
        list.forEach((item, i) => console.log(`${'$'}{i + 1}. ${'$'}{item}`));

        const short = list.filter((item) => item.length <= 5);
        console.log("Short names:", short);
        console.log("In capital letters:", list.map((a) => a.toUpperCase()).join(", "));
    """),
    ex("Data", "Grade Report", "Objects, reduce and sort for a small class list.", """
        const students = [
          { name: "Ada", points: 92 },
          { name: "Ben", points: 67 },
          { name: "Cleo", points: 85 },
          { name: "Dev", points: 74 },
        ];

        const average = students.reduce((sum, s) => sum + s.points, 0) / students.length;
        console.log(`Average: ${'$'}{average.toFixed(1)} points`);

        const ranking = [...students].sort((a, b) => b.points - a.points);
        ranking.forEach((s, place) => {
          const grade = s.points >= 90 ? "excellent" : s.points >= 75 ? "good" : "satisfactory";
          console.log(`${'$'}{place + 1}. ${'$'}{s.name.padEnd(5)} ${'$'}{s.points} – ${'$'}{grade}`);
        });
    """),
    ex("Data", "Word Count", "Counts words in a text using a Map.", """
        const text = "The dog and the cat and the bird and the fish";
        const counter = new Map();
        for (const word of text.toLowerCase().split(" ")) {
          counter.set(word, (counter.get(word) ?? 0) + 1);
        }
        const sorted = [...counter].sort((a, b) => b[1] - a[1]);
        for (const [word, count] of sorted) {
          console.log(`${'$'}{word.padEnd(6)} ${'$'}{"*".repeat(count)} (${'$'}{count})`);
        }
    """),
    ex("Async", "Countdown", "Counts down with await and setTimeout.", """
        const wait = (ms) => new Promise((done) => setTimeout(done, ms));

        async function countdown(from) {
          for (let i = from; i > 0; i--) {
            console.log(i + "…");
            await wait(1000);
          }
          console.log("🚀 Liftoff!");
        }

        countdown(5);
    """),
    ex("Async", "Traffic Light", "A traffic light that switches with setInterval.", """
        const phases = ["🔴 Red", "🔴🟡 Red-Yellow", "🟢 Green", "🟡 Yellow"];
        let step = 0;

        const light = setInterval(() => {
          console.log(phases[step % phases.length]);
          step++;
          if (step === 8) {
            clearInterval(light);
            console.log("Light off.");
          }
        }, 700);
    """),
    ex("Fun", "Christmas Tree", "Draws a tree made of asterisks.", """
        const height = 6;
        for (let i = 0; i < height; i++) {
          console.log(" ".repeat(height - i - 1) + "*".repeat(2 * i + 1));
        }
        console.log(" ".repeat(height - 1) + "|");
    """),
    ex("Fun", "Password Generator", "Creates random, secure passwords.", """
        const characters = "abcdefghijkmnpqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ23456789!?#%";

        function password(length) {
          let pw = "";
          for (let i = 0; i < length; i++) {
            pw += characters[Math.floor(Math.random() * characters.length)];
          }
          return pw;
        }

        for (let i = 1; i <= 3; i++) {
          console.log(`Password ${'$'}{i}: ${'$'}{password(12)}`);
        }
    """),
    ex("Fun", "Pet Class", "Classes, methods and inheritance.", """
        class Pet {
          constructor(name, sound) {
            this.name = name;
            this.sound = sound;
          }
          speak() {
            return `${'$'}{this.name} says ${'$'}{this.sound}!`;
          }
        }

        class Snake extends Pet {
          constructor(name) {
            super(name, "Sssss");
          }
          speak() {
            return super.speak() + " 🐍";
          }
        }

        const pets = [new Pet("Rex", "Woof"), new Pet("Mimi", "Meow"), new Snake("Monty")];
        for (const pet of pets) console.log(pet.speak());
    """),
)
