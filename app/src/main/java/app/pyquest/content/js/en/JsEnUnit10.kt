package app.pyquest.content.js.en

import app.pyquest.content.CourseBuilder
import app.pyquest.content.js.D
import app.pyquest.data.LessonIcon

fun CourseBuilder.jsEnUnit10() = unit("Classes", "Blueprints for your own objects") {
    lesson("Classes & Objects", "One blueprint, many objects", LessonIcon.CLASS) {
        text("A **class** is a blueprint for objects. With `new` you create as many **instances** from it as you like. The `constructor` runs when an object is created and sets up its properties.")
        code("""
            class Dog {
              constructor(name, age) {
                this.name = name;
                this.age = age;
              }
            }

            const rex = new Dog("Rex", 3);
            const max = new Dog("Max", 7);
            console.log(rex.name, max.age);
            console.log(rex);
        """, output = """
            Rex 7
            Dog { name: 'Rex', age: 3 }
        """)
        text("Inside the class you also define **methods** – they apply to all instances. Here, `this` is the object in question.")
        code("""
            class Dog {
              constructor(name) {
                this.name = name;
                this.tricks = [];
              }
              learn(trick) {
                this.tricks.push(trick);
              }
              show() {
                return `${D}{this.name} can: ${D}{this.tricks.join(", ")}`;
              }
            }

            const rex = new Dog("Rex");
            rex.learn("sit");
            rex.learn("paw");
            console.log(rex.show());
        """, output = "Rex can: sit, paw")
        tip("Class names start with a capital letter: `Dog`, `BankAccount`, `Player`. Variables and methods, on the other hand, start lowercase.")
        warn("It doesn't work without `new`: `Dog(\"Rex\")` throws a TypeError.")

        output("""
            class Point {
              constructor(x, y) {
                this.x = x;
                this.y = y;
              }
            }
            const p = new Point(3, 4);
            console.log(p.x + p.y);
        """, "7", "34", "undefined", "Point { x: 3, y: 4 }")
        output("""
            class Box {
              constructor(contents) {
                this.contents = contents;
              }
            }
            console.log(new Box("cookies"));
        """, "Box { contents: 'cookies' }", "{ contents: 'cookies' }", "cookies", "Box")
        choice("How do you create a new object from the class `Car`?", "new Car()", "Car.new()", "create Car()", "Car()", mono = true)
        typeOutput("""
            class Counter {
              constructor() {
                this.value = 0;
              }
              plus() {
                this.value++;
              }
            }
            const c = new Counter();
            c.plus();
            c.plus();
            c.plus();
            console.log(c.value);
        """, "3")
        fill("Complete the constructor.", """
            class City {
              ??(name) {
                this.name = name;
              }
            }
            console.log(new City("Vienna").name);
        """, answers = listOf("constructor"), distractors = listOf("init", "City", "new"), output = "Vienna")
        truth("All instances of a class share the same methods.", true)
        output("""
            class Light {
              constructor() {
                this.on = false;
              }
              toggle() {
                this.on = !this.on;
                return this.on;
              }
            }
            const l = new Light();
            l.toggle();
            console.log(l.toggle());
        """, "false", "true", "undefined", "Light { on: false }")
        write("Write a class `Rectangle` with `width` and `height` in the constructor and a method `area()` that returns the area.",
            solution = """
                class Rectangle {
                  constructor(width, height) {
                    this.width = width;
                    this.height = height;
                  }
                  area() {
                    return this.width * this.height;
                  }
                }
            """,
            test = """
                const r = new Rectangle(4, 5);
                if (r.width !== 4 || r.height !== 5) throw new Error("Store width and height in the constructor with this");
                if (r.area() !== 20) throw new Error("new Rectangle(4, 5).area() should be 20");
                if (new Rectangle(1.5, 2).area() !== 3) throw new Error("new Rectangle(1.5, 2).area() should be 3");
                console.log("✅ All tests passed!");
            """,
            mustContain = listOf("class Rectangle"),
            hint = "In the `constructor`, store the values with `this.width = width;` – the method returns `this.width * this.height`.")
    }

    lesson("Getters, Setters & toString", "Computed properties and checked values", LessonIcon.CLASS) {
        text("With `get` you define a **computed property**: it looks like a normal property, but it's calculated fresh every time you access it – without brackets.")
        code("""
            class Circle {
              constructor(radius) {
                this.radius = radius;
              }
              get diameter() {
                return this.radius * 2;
              }
            }
            const c = new Circle(5);
            console.log(c.diameter);
            c.radius = 8;
            console.log(c.diameter);
        """, output = """
            10
            16
        """)
        text("With `set` you can step in when a value is assigned – for example to catch invalid values:")
        code("""
            class Thermostat {
              constructor() {
                this._degrees = 20;
              }
              get degrees() {
                return this._degrees;
              }
              set degrees(value) {
                if (value < 5 || value > 30) {
                  console.log("Invalid:", value);
                  return;
                }
                this._degrees = value;
              }
            }
            const t = new Thermostat();
            t.degrees = 25;
            t.degrees = 99;
            console.log(t.degrees);
        """, output = """
            Invalid: 99
            25
        """)
        text("A method `toString()` decides how an object looks as text – for example inside a template string:")
        code("""
            class Fraction {
              constructor(n, d) {
                this.n = n;
                this.d = d;
              }
              toString() {
                return `${D}{this.n}/${D}{this.d}`;
              }
            }
            const f = new Fraction(3, 4);
            console.log(`A fraction: ${D}{f}`);
        """, output = "A fraction: 3/4")
        tip("An underscore like in `_degrees` signals: “Please don't use this directly.” You'll get to know truly protected fields with `#` in a moment.")

        output("""
            class Square {
              constructor(s) {
                this.s = s;
              }
              get area() {
                return this.s * this.s;
              }
            }
            console.log(new Square(3).area);
        """, "9", "undefined", "[Function: area]", "6")
        choice("How do you access a getter `age`?", "obj.age", "obj.age()", "obj.get(age)", "obj.getAge()", mono = true)
        typeOutput("""
            class Name {
              constructor(first, last) {
                this.first = first;
                this.last = last;
              }
              get full() {
                return this.first + " " + this.last;
              }
            }
            console.log(new Name("Ada", "Lovelace").full);
        """, "Ada Lovelace")
        output("""
            class Money {
              constructor(cents) {
                this.cents = cents;
              }
              toString() {
                return (this.cents / 100).toFixed(2) + " €";
              }
            }
            console.log(`Price: ${D}{new Money(250)}`);
        """, "Price: 2.50 €", "Price: 250", "Price: [object Object]", "Price: Money { cents: 250 }")
        fill("Turn `isEmpty` into a computed property.", """
            class List {
              constructor() {
                this.items = [];
              }
              ?? isEmpty() {
                return this.items.length === 0;
              }
            }
            console.log(new List().isEmpty);
        """, answers = listOf("get"), distractors = listOf("set", "static", "function"), output = "true")
        truth("A getter is called with round brackets: `obj.getter()`.", false)
        write("Write a class `Temperature` with a property `celsius` (from the constructor) and a getter `fahrenheit` that gives `celsius * 9 / 5 + 32`.",
            solution = """
                class Temperature {
                  constructor(celsius) {
                    this.celsius = celsius;
                  }
                  get fahrenheit() {
                    return this.celsius * 9 / 5 + 32;
                  }
                }
            """,
            test = """
                const t = new Temperature(100);
                if (t.fahrenheit !== 212) throw new Error("100 °C should be 212 °F (a getter has no brackets!)");
                t.celsius = 0;
                if (t.fahrenheit !== 32) throw new Error("After celsius = 0, fahrenheit should be 32");
                console.log("✅ All tests passed!");
            """,
            mustContain = listOf("get fahrenheit"),
            hint = "`get fahrenheit() { return this.celsius * 9 / 5 + 32; }`")
    }

    lesson("Inheritance", "Extending classes with extends", LessonIcon.CLASS) {
        text("With `extends`, a class inherits everything from another class and can add to it. `super(...)` calls the constructor of the parent class – that has to happen in the constructor **before** the first `this`.")
        code("""
            class Animal {
              constructor(name) {
                this.name = name;
              }
              introduce() {
                return `I am ${D}{this.name}.`;
              }
            }

            class Snake extends Animal {
              constructor(name, length) {
                super(name);
                this.length = length;
              }
              hiss() {
                return "Ssssss!";
              }
            }

            const monty = new Snake("Monty", 2);
            console.log(monty.introduce());
            console.log(monty.hiss());
        """, output = """
            I am Monty.
            Ssssss!
        """)
        text("A subclass can **override** an inherited method. With `super.method()` it can call the parent class's version.")
        code("""
            class Animal {
              constructor(name) {
                this.name = name;
              }
              sound() {
                return "...";
              }
              speak() {
                return `${D}{this.name}: ${D}{this.sound()}`;
              }
            }
            class Cat extends Animal {
              sound() {
                return "Meow";
              }
            }
            class Lion extends Cat {
              sound() {
                return super.sound().toUpperCase() + "!";
              }
            }
            console.log(new Animal("Thing").speak());
            console.log(new Cat("Mimi").speak());
            console.log(new Lion("Leo").speak());
        """, output = """
            Thing: ...
            Mimi: Meow
            Leo: MEOW!
        """)
        text("With `instanceof` you check whether an object belongs to a class – or to one of its parent classes:")
        code("""
            class Vehicle {}
            class Car extends Vehicle {}
            const c = new Car();
            console.log(c instanceof Car, c instanceof Vehicle, c instanceof Array);
        """, output = "true true false")

        output("""
            class A {
              hello() {
                return "A";
              }
            }
            class B extends A {}
            console.log(new B().hello());
        """, "A", "B", "undefined", "Error")
        output("""
            class A {
              who() {
                return "A";
              }
            }
            class B extends A {
              who() {
                return "B" + super.who();
              }
            }
            console.log(new B().who());
        """, "BA", "B", "AB", "A")
        choice("What does `super(name)` do in the constructor of a subclass?", "Calls the constructor of the parent class", "Creates a new object of the subclass", "Deletes the parent class", "Prints the name")
        typeOutput("""
            class Shape {}
            class Circle extends Shape {}
            console.log(new Circle() instanceof Shape);
        """, "true")
        fill("Let `Bird` inherit from `Animal`.", """
            class Animal {
              breathe() {
                return "breathes";
              }
            }
            class Bird ?? Animal {}
            console.log(new Bird().breathe());
        """, answers = listOf("extends"), distractors = listOf("inherits", "implements", "from"), output = "breathes")
        truth("In a subclass, `super(...)` has to come before the first `this` in the constructor.", true)
        write("""
            Write a class `Employee` with `name` and `salary` and a method `info()` that gives `<name> earns <salary> €`.
            Then write `Boss extends Employee`: the constructor also receives `bonus`, and `info()` adds ` + <bonus> € bonus`.
        """.trimIndent(),
            solution = """
                class Employee {
                  constructor(name, salary) {
                    this.name = name;
                    this.salary = salary;
                  }
                  info() {
                    return `${D}{this.name} earns ${D}{this.salary} €`;
                  }
                }

                class Boss extends Employee {
                  constructor(name, salary, bonus) {
                    super(name, salary);
                    this.bonus = bonus;
                  }
                  info() {
                    return `${D}{super.info()} + ${D}{this.bonus} € bonus`;
                  }
                }
            """,
            test = """
                const e = new Employee("Ben", 3000);
                if (e.info() !== "Ben earns 3000 €") throw new Error("Employee.info() isn't right: " + e.info());
                const b = new Boss("Ada", 5000, 800);
                if (!(b instanceof Employee)) throw new Error("Boss should inherit from Employee (extends)");
                if (b.info() !== "Ada earns 5000 € + 800 € bonus") throw new Error("Boss.info() isn't right: " + b.info());
                console.log("✅ All tests passed!");
            """,
            mustContain = listOf("extends", "super"),
            hint = "In the `Boss` constructor, call `super(name, salary)` first; in `info()` you can reuse `super.info()`.")
    }

    lesson("Private Fields & static", "Protecting data, and tools on the class", LessonIcon.CLASS) {
        text("Fields with a `#` in front of the name are **private**: only code inside the class can reach them. That way nobody from outside can simply overwrite a bank balance, for example.")
        code("""
            class Account {
              #balance = 0;

              deposit(amount) {
                if (amount <= 0) return false;
                this.#balance += amount;
                return true;
              }
              get balance() {
                return this.#balance;
              }
            }

            const a = new Account();
            a.deposit(50);
            a.deposit(-10);
            console.log(a.balance);
        """, output = "50")
        text("`static` makes a method or property part of the **class itself** instead of the individual objects. You call it via the class name.")
        code("""
            class MathTools {
              static PI = 3.14159;
              static circleArea(r) {
                return MathTools.PI * r * r;
              }
            }
            console.log(MathTools.circleArea(2).toFixed(2));

            class Ticket {
              static count = 0;
              constructor() {
                Ticket.count++;
                this.number = Ticket.count;
              }
            }
            new Ticket();
            const t = new Ticket();
            console.log(t.number, Ticket.count);
        """, output = """
            12.57
            2 2
        """)
        tip("You already know built-in static methods: `Math.floor()`, `Number.isNaN()` and `Object.keys()` all belong to their class, not to individual values.")
        warn("An access like `a.#balance` from outside is a **SyntaxError** – the program won't even start.")

        output("""
            class Secret {
              #code = 1234;
              check(guess) {
                return guess === this.#code;
              }
            }
            const s = new Secret();
            console.log(s.check(1234), s.code);
        """, "true undefined", "true 1234", "false undefined", "Error")
        output("""
            class Game {
              static players = 0;
              constructor() {
                Game.players++;
              }
            }
            new Game();
            new Game();
            new Game();
            console.log(Game.players);
        """, "3", "1", "0", "undefined")
        choice("How do you call the static method `create` of the class `Card`?", "Card.create()", "new Card().create()", "create(Card)", "Card#create()", mono = true)
        typeOutput("""
            class Converter {
              static kmToMiles(km) {
                return km * 0.5;
              }
            }
            console.log(Converter.kmToMiles(10));
        """, "5")
        fill("Make the field `pin` private – it should be invisible from outside.", """
            class Phone {
              ??pin = "0000";
              unlock(code) {
                return code === this.??pin;
              }
            }
            const p = new Phone();
            console.log(p.unlock("0000"));
            console.log(Object.keys(p).length);
        """, answers = listOf("#", "#"), distractors = listOf("_", "private "), output = "true\n0",
            explain = "With `_pin` it would work too – but the field would be visible and changeable from outside. Only `#` makes it truly private.")
        truth("You can access a private field `#x` from outside with `obj.#x`.", false)
        write("""
            Write a class `PiggyBank` with a private field `#amount` (starting at 0), a method `insert(euros)` that only accepts positive amounts,
            and a getter `contents` that gives the amount.
        """.trimIndent(),
            solution = """
                class PiggyBank {
                  #amount = 0;
                  insert(euros) {
                    if (euros > 0) this.#amount += euros;
                  }
                  get contents() {
                    return this.#amount;
                  }
                }
            """,
            test = """
                const p = new PiggyBank();
                p.insert(2);
                p.insert(5);
                p.insert(-3);
                if (p.contents !== 7) throw new Error("After 2 + 5 (and -3 ignored), contents should be 7, but it is " + p.contents);
                if (Object.keys(p).length !== 0) throw new Error("The amount should be in a private field #amount, not in this.amount");
                console.log("✅ All tests passed!");
            """,
            mustContain = listOf("#amount", "get contents"),
            hint = "`#amount = 0;` as a field, `if (euros > 0) this.#amount += euros;` and `get contents() { return this.#amount; }`")
    }

    checkpoint()
}
