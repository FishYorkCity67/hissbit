package app.pyquest.content.en

import app.pyquest.content.CourseBuilder
import app.pyquest.data.LessonIcon

fun CourseBuilder.enUnit11() = unit("Object-Oriented Programming", "Your own classes, objects and inheritance") {
    lesson("Classes & Objects", "Blueprints for your own data types", LessonIcon.CLASS) {
        text("With **classes** you build your own data types. A class is like a **blueprint** – the **objects** (instances) are the things built from that blueprint.")
        code("""
            class Dog:
                def __init__(self, name, breed):
                    self.name = name
                    self.breed = breed

            bello = Dog("Bello", "Dachshund")
            luna = Dog("Luna", "Poodle")
            print(bello.name, "is a", bello.breed)
            print(luna.name)
        """, output = """
            Bello is a Dachshund
            Luna
        """)
        text("""
            - `class Dog:` defines the class (class names are written in **PascalCase**)
            - `__init__` is the **constructor** – it's called automatically when an object is created
            - `self` is the object itself; with `self.name = name` you store an **attribute**
        """)
        tip("`Dog(\"Bello\", \"Dachshund\")` calls `__init__(self, \"Bello\", \"Dachshund\")` behind the scenes – Python passes `self` automatically.")
        text("You can read and change attributes at any time:")
        code("""
            class Player:
                def __init__(self, name):
                    self.name = name
                    self.points = 0

            p = Player("Ada")
            p.points += 10
            print(p.name, p.points)
        """, output = "Ada 10")

        output("""
            class Cat:
                def __init__(self, name):
                    self.name = name

            c = Cat("Mimi")
            print(c.name)
        """, "Mimi", "name", "Cat", "Error")
        choice("What's the name of the method that's called automatically when an object is created?", "__init__", "__start__", "create", "new", mono = true)
        truth("`self` stands for the object you're currently working with.", true)
        typeOutput("""
            class Box:
                def __init__(self, content):
                    self.content = content

            a = Box(3)
            b = Box(5)
            print(a.content + b.content)
        """, "8")
        fill("Store the brand as an attribute.", """
            class Car:
                def __init__(self, brand):
                    ??.brand = brand

            car1 = Car("VW")
            car2 = Car("BMW")
            print(car1.brand, car2.brand)
        """, answers = listOf("self"), distractors = listOf("this", "Car", "brand"), output = "VW BMW",
            explain = "`self` is the object that's being built – that way **every** car gets its own brand.")
        match("class" to "define a blueprint", "__init__" to "constructor", "self" to "the object itself", "obj.attribute" to "access data")
        output("""
            class Counter:
                def __init__(self):
                    self.value = 0

            c = Counter()
            c.value += 5
            c.value += 2
            print(c.value)
        """, "7", "5", "0", "Error")
        write("Create a class `Book` with the attributes `title` and `pages`. Create the book `\"Learning Python\"` with `320` pages and print: `Learning Python has 320 pages`.",
            solution = """
                class Book:
                    def __init__(self, title, pages):
                        self.title = title
                        self.pages = pages

                b = Book("Learning Python", 320)
                print(b.title, "has", b.pages, "pages")
            """,
            output = "Learning Python has 320 pages",
            mustContain = listOf("class Book", "__init__"),
            hint = "In `__init__`, store them with `self.title = title` and `self.pages = pages`.")
    }

    lesson("Methods", "Functions that belong to objects", LessonIcon.CLASS) {
        text("**Methods** are functions that belong to a class. Their first parameter is always `self` – that's how they access the object's attributes:")
        code("""
            class Account:
                def __init__(self, owner):
                    self.owner = owner
                    self.balance = 0

                def deposit(self, amount):
                    self.balance += amount

                def show(self):
                    print(f"{self.owner}: {self.balance} €")

            a = Account("Ada")
            a.deposit(50)
            a.deposit(25)
            a.show()
        """, output = "Ada: 75 €")
        text("Methods can also return values and call other methods:")
        code("""
            class Rectangle:
                def __init__(self, width, height):
                    self.width = width
                    self.height = height

                def area(self):
                    return self.width * self.height

                def is_square(self):
                    return self.width == self.height

            r = Rectangle(4, 4)
            print(r.area(), r.is_square())
        """, output = "16 True")
        tip("The big advantage: **data** (attributes) and **behavior** (methods) belong together. That keeps big programs organized.")

        output("""
            class A:
                def hello(self):
                    return "Hi"

            print(A().hello())
        """, "Hi", "hello", "A", "Error")
        typeOutput("""
            class Circle:
                def __init__(self, r):
                    self.r = r

                def diameter(self):
                    return 2 * self.r

            print(Circle(7).diameter())
        """, "14")
        choice("What's the first parameter of every normal method?", "self", "this", "cls", "obj", mono = true)
        truth("A method can change the attributes of its object.", true)
        output("""
            class Lamp:
                def __init__(self):
                    self.on = False

                def switch(self):
                    self.on = not self.on

            l = Lamp()
            l.switch()
            l.switch()
            l.switch()
            print(l.on)
        """, "True", "False", "3", "Error")
        fill("Complete the method that increases the age by 1.", """
            class Person:
                def __init__(self, age):
                    self.age = age

                def birthday(??):
                    self.age += 1

            p = Person(13)
            p.birthday()
            print(p.age)
        """, answers = listOf("self"), distractors = listOf("age", "Person", "p"), output = "14")
        arrange("Build a class `Dog` with a method `bark` that prints `Woof!`, and call it.", """
            class Dog:
                def bark(self):
                    print("Woof!")
            Dog().bark()
        """, output = "Woof!")
        write("Add a method `total()` to the class `Cart` that returns the sum of all prices in `self.prices`. Then put two items (`4.5` and `3.0`) into it and print the total.",
            starter = """
                class Cart:
                    def __init__(self):
                        self.prices = []

                    def add(self, price):
                        self.prices.append(price)
            """,
            solution = """
                class Cart:
                    def __init__(self):
                        self.prices = []

                    def add(self, price):
                        self.prices.append(price)

                    def total(self):
                        return sum(self.prices)

                c = Cart()
                c.add(4.5)
                c.add(3.0)
                print(c.total())
            """,
            output = "7.5",
            test = """
                w = Cart()
                assert w.total() == 0
                w.add(2)
                assert w.total() == 2
            """,
            mustContain = listOf("def total"),
            hint = "`def total(self): return sum(self.prices)`")
    }

    lesson("Inheritance", "Extending classes with super()", LessonIcon.CLASS) {
        text("With **inheritance** you build one class on top of another. The **child class** takes over all attributes and methods of the **parent class** and can add or change its own:")
        code("""
            class Animal:
                def __init__(self, name):
                    self.name = name

                def introduce(self):
                    print(f"I am {self.name}.")

                def sound(self):
                    return "..."

            class Dog(Animal):
                def sound(self):
                    return "Woof!"

            class Cat(Animal):
                def sound(self):
                    return "Meow!"

            for animal in [Dog("Bello"), Cat("Mimi")]:
                animal.introduce()
                print(animal.sound())
        """, output = """
            I am Bello.
            Woof!
            I am Mimi.
            Meow!
        """)
        text("If the child class needs **additional** attributes, it calls the parent's constructor with `super()`:")
        code("""
            class Vehicle:
                def __init__(self, wheels):
                    self.wheels = wheels

            class Bicycle(Vehicle):
                def __init__(self, gears):
                    super().__init__(2)
                    self.gears = gears

            b = Bicycle(21)
            print(b.wheels, b.gears)
        """, output = "2 21")
        tip("`isinstance(bello, Animal)` is `True` – a dog is an animal, after all.")
        warn("Inheritance makes sense for a real “is a” relationship: a dog **is an** animal. A car **has** wheels, but isn't a wheel itself.")

        output("""
            class A:
                def f(self):
                    return "A"

            class B(A):
                pass

            print(B().f())
        """, "A", "B", "Error", "None", explain = "B inherits the method `f` from A.")
        output("""
            class A:
                def f(self):
                    return "A"

            class B(A):
                def f(self):
                    return "B"

            print(B().f())
        """, "B", "A", "AB", "Error", explain = "B overrides the method `f`.")
        choice("How does `Cat` inherit from `Animal`?", "class Cat(Animal):", "class Cat extends Animal:", "class Cat < Animal:", "class Cat: Animal", mono = true)
        truth("With `super()` you can call methods of the parent class.", true)
        typeOutput("""
            class Animal:
                pass

            class Dog(Animal):
                pass

            print(isinstance(Dog(), Animal))
        """, "True")
        fill("Call the constructor of the parent class.", """
            class Person:
                def __init__(self, name):
                    self.name = name

            class Student(Person):
                def __init__(self, name, school_class):
                    ??().__init__(name)
                    self.school_class = school_class

            s = Student("Ada", "8b")
            print(s.name, s.school_class)
        """, answers = listOf("super"), distractors = listOf("self", "Person", "parent"), output = "Ada 8b")
        output("""
            class Base:
                def hello(self):
                    return "Hello"

            class Child(Base):
                def hello(self):
                    return super().hello() + " World"

            print(Child().hello())
        """, "Hello World", "Hello", " World", "Error")
        write("Create a class `Shape` with the method `area()` that returns `0`. Create the child classes `Square(side)` and `Circle(radius)` that override `area()` (circle: `3.14 * r * r`). Print the areas of `Square(3)` and `Circle(2)`.",
            solution = """
                class Shape:
                    def area(self):
                        return 0

                class Square(Shape):
                    def __init__(self, side):
                        self.side = side

                    def area(self):
                        return self.side * self.side

                class Circle(Shape):
                    def __init__(self, radius):
                        self.radius = radius

                    def area(self):
                        return 3.14 * self.radius * self.radius

                print(Square(3).area())
                print(Circle(2).area())
            """,
            output = """
                9
                12.56
            """,
            test = """
                assert isinstance(Square(1), Shape)
                assert isinstance(Circle(1), Shape)
                assert Shape().area() == 0
            """,
            mustContain = listOf("class Shape", "(Shape)"),
            hint = "`class Square(Shape):` with its own `__init__` and its own `area` method.")
    }

    lesson("Magic Methods", "__str__, __eq__, __add__ and more", LessonIcon.MAGIC) {
        text("**Magic methods** (also called *dunder methods*, because of the double underscores) decide how your objects behave with built-in functions and operators.")
        text("`__str__` defines what `print()` shows. Without it, you only see a cryptic memory address:")
        code("""
            class Point:
                def __init__(self, x, y):
                    self.x = x
                    self.y = y

                def __str__(self):
                    return f"({self.x}, {self.y})"

            print(Point(3, 4))
        """, output = "(3, 4)")
        text("""
            More important dunder methods:
            - `__repr__` – the “technical” representation, e.g. inside lists
            - `__eq__` – what `==` means
            - `__len__` – what `len()` returns
            - `__add__` – what `+` does
            - `__lt__` – what `<` means (then `sorted()` works too)
        """)
        code("""
            class Money:
                def __init__(self, euro):
                    self.euro = euro

                def __add__(self, other):
                    return Money(self.euro + other.euro)

                def __eq__(self, other):
                    return self.euro == other.euro

                def __repr__(self):
                    return f"Money({self.euro})"

            total = Money(5) + Money(7)
            print(total)
            print(total == Money(12))
            print([Money(1), Money(2)])
        """, output = """
            Money(12)
            True
            [Money(1), Money(2)]
        """)
        tip("If there's no `__str__`, `print()` automatically uses `__repr__`.")

        output("""
            class A:
                def __str__(self):
                    return "I am A"

            print(A())
        """, "I am A", "A", "<__main__.A object>", "Error")
        choice("Which method decides what `len(obj)` returns?", "__len__", "__size__", "__count__", "len", mono = true)
        typeOutput("""
            class Team:
                def __init__(self, *players):
                    self.players = players

                def __len__(self):
                    return len(self.players)

            print(len(Team("Ada", "Alan", "Grace")))
        """, "3")
        match("__str__" to "text for print()", "__eq__" to "comparison with ==", "__add__" to "operator +", "__len__" to "function len()", "__lt__" to "operator <")
        truth("Without `__eq__`, two different objects with the same values are equal with `==`.", false,
            explain = "Without `__eq__`, Python only checks whether it's the **same** object.")
        fill("Make `print()` show the name of the city.", """
            class City:
                def __init__(self, name):
                    self.name = name

                def ??(self):
                    return self.name

            print(City("Hamburg"))
        """, answers = listOf("__str__"), distractors = listOf("__init__", "__len__", "str"), output = "Hamburg")
        output("""
            class V:
                def __init__(self, x):
                    self.x = x

                def __add__(self, o):
                    return V(self.x + o.x)

            print((V(2) + V(3)).x)
        """, "5", "23", "V(5)", "Error")
        write("Create a class `Fraction(numerator, denominator)` with `__str__` (output like `1/2`) and `__mul__` that multiplies two fractions. Print `Fraction(1, 2) * Fraction(3, 4)` – the result is `3/8`.",
            solution = """
                class Fraction:
                    def __init__(self, numerator, denominator):
                        self.numerator = numerator
                        self.denominator = denominator

                    def __str__(self):
                        return f"{self.numerator}/{self.denominator}"

                    def __mul__(self, other):
                        return Fraction(self.numerator * other.numerator, self.denominator * other.denominator)

                print(Fraction(1, 2) * Fraction(3, 4))
            """,
            output = "3/8",
            test = """
                f = Fraction(2, 3) * Fraction(5, 7)
                assert str(f) == "10/21"
            """,
            mustContain = listOf("__mul__", "__str__"),
            hint = "`__mul__` multiplies numerator by numerator and denominator by denominator.")
    }

    lesson("Class Pros", "Class attributes, @property and more", LessonIcon.STAR) {
        text("**Class attributes** belong to the class itself and are shared by all objects:")
        code("""
            class Player:
                count = 0

                def __init__(self, name):
                    self.name = name
                    Player.count += 1

            Player("Ada")
            Player("Alan")
            print(Player.count)
        """, output = "2")
        text("With `@property` you use a method like an attribute – without brackets. Handy for calculated values:")
        code("""
            class Temperature:
                def __init__(self, celsius):
                    self.celsius = celsius

                @property
                def fahrenheit(self):
                    return self.celsius * 9 / 5 + 32

            t = Temperature(25)
            print(t.fahrenheit)
        """, output = "77.0")
        text("`@classmethod` gets the **class** (`cls`) instead of the object – ideal for alternative constructors. `@staticmethod` gets nothing automatically – a normal function that belongs to the class thematically:")
        code("""
            class Date:
                def __init__(self, day, month, year):
                    self.day, self.month, self.year = day, month, year

                @classmethod
                def from_text(cls, text):
                    d, m, y = text.split(".")
                    return cls(int(d), int(m), int(y))

                @staticmethod
                def is_leap_year(year):
                    return year % 4 == 0 and (year % 100 != 0 or year % 400 == 0)

            d = Date.from_text("24.12.2026")
            print(d.day, d.month)
            print(Date.is_leap_year(2028))
        """, output = """
            24 12
            True
        """)
        tip("An underscore at the start (`self._balance`) signals: “Internal detail, please don't change it from outside.” Python doesn't forbid it – but it's an important convention.")

        output("""
            class A:
                x = 5

            a = A()
            b = A()
            print(a.x + b.x)
        """, "10", "5", "Error", "0")
        output("""
            class K:
                def __init__(self, r):
                    self.r = r

                @property
                def d(self):
                    return self.r * 2

            print(K(4).d)
        """, "8", "4", "Error", "<bound method>")
        choice("What does a `@classmethod` receive as its first parameter?", "The class (`cls`)", "The object (`self`)", "Nothing", "The class name as text")
        truth("A `@staticmethod` has no access to `self`.", true)
        typeOutput("""
            class Counter:
                total = 0

                def __init__(self):
                    Counter.total += 1

            for _ in range(4):
                Counter()
            print(Counter.total)
        """, "4")
        fill("Turn `area` into a property.", """
            class Square:
                def __init__(self, s):
                    self.s = s

                ??
                def area(self):
                    return self.s ** 2

            print(Square(5).area)
        """, answers = listOf("@property"), distractors = listOf("@staticmethod", "@classmethod", "property"), output = "25")
        choice("What does `self._secret` mean by convention?", "An internal attribute – don't use it from outside", "Python forbids any access", "A constant", "A method")
        write("Create a class `Rectangle(width, height)` with a `@property` called `area` and a `@classmethod` called `square(side)` that creates a rectangle with equal sides. Print `Rectangle(2, 5).area` and `Rectangle.square(4).area`.",
            solution = """
                class Rectangle:
                    def __init__(self, width, height):
                        self.width = width
                        self.height = height

                    @property
                    def area(self):
                        return self.width * self.height

                    @classmethod
                    def square(cls, side):
                        return cls(side, side)

                print(Rectangle(2, 5).area)
                print(Rectangle.square(4).area)
            """,
            output = """
                10
                16
            """,
            mustContain = listOf("@property", "@classmethod"),
            hint = "`@classmethod def square(cls, side): return cls(side, side)`")
    }

    lesson("Dataclasses", "Data classes without the typing", LessonIcon.BOX) {
        text("Many classes are simply **data containers**. With `@dataclass`, Python writes `__init__`, `__repr__` and `__eq__` for you automatically:")
        code("""
            from dataclasses import dataclass

            @dataclass
            class Point:
                x: int
                y: int

            p = Point(3, 4)
            print(p)
            print(p == Point(3, 4))
            print(p.x + p.y)
        """, output = """
            Point(x=3, y=4)
            True
            7
        """)
        text("The fields are written with **type hints**. Default values and your own methods are possible too:")
        code("""
            from dataclasses import dataclass, field

            @dataclass
            class Task:
                title: str
                done: bool = False
                tags: list[str] = field(default_factory=list)

                def check_off(self):
                    self.done = True

            t = Task("Practice Python", tags=["learning"])
            t.check_off()
            print(t)
        """, output = "Task(title='Practice Python', done=True, tags=['learning'])")
        warn("Lists as default values only work with `field(default_factory=list)` – otherwise all objects would share the same list.")
        tip("With `@dataclass(frozen=True)` the objects become immutable – like tuples with names.")

        output("""
            from dataclasses import dataclass

            @dataclass
            class Animal:
                name: str
                legs: int

            print(Animal("Spider", 8))
        """, "Animal(name='Spider', legs=8)", "Spider 8", "<Animal object>", "Error")
        output("""
            from dataclasses import dataclass

            @dataclass
            class P:
                x: int

            print(P(1) == P(1))
        """, "True", "False", "Error", "None", explain = "Dataclasses automatically create an `__eq__` that compares the fields.")
        choice("What does `@dataclass` create automatically?", "`__init__`, `__repr__` and `__eq__`", "Only `__init__`", "A database", "Only `__str__`")
        truth("In a dataclass, the fields are written with type hints like `name: str`.", true)
        typeOutput("""
            from dataclasses import dataclass

            @dataclass
            class Account:
                owner: str
                balance: float = 0.0

            a = Account("Ada")
            print(a.balance)
        """, "0.0")
        fill("Turn the class into a dataclass.", """
            from dataclasses import dataclass

            ??
            class Color:
                r: int
                g: int
                b: int

            print(Color(255, 0, 0))
        """, answers = listOf("@dataclass"), distractors = listOf("@property", "@classmethod", "dataclass"), output = "Color(r=255, g=0, b=0)")
        output("""
            from dataclasses import dataclass

            @dataclass
            class Movie:
                title: str
                year: int

            movies = [Movie("B", 2020), Movie("A", 1999)]
            print(min(movies, key=lambda m: m.year).title)
        """, "A", "B", "1999", "Error")
        write("Create a dataclass `Product` with `name: str`, `price: float` and `quantity: int = 1` and a method `total()` (price × quantity). Print `Product(\"Pen\", 1.5, 4).total()`.",
            solution = """
                from dataclasses import dataclass

                @dataclass
                class Product:
                    name: str
                    price: float
                    quantity: int = 1

                    def total(self):
                        return self.price * self.quantity

                print(Product("Pen", 1.5, 4).total())
            """,
            output = "6.0",
            test = """assert Product("X", 2.0).total() == 2.0""",
            mustContain = listOf("@dataclass"),
            hint = "Write the fields with types, then `def total(self): return self.price * self.quantity`.")
    }

    checkpoint()
}
