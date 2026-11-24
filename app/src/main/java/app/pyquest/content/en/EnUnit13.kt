package app.pyquest.content.en

import app.pyquest.content.CourseBuilder
import app.pyquest.data.LessonIcon

fun CourseBuilder.enUnit13() = unit("Files", "Saving and reading data, processing tables") {
    lesson("Writing & Reading", "open, with and the file modes", LessonIcon.FILE) {
        text("Programs can save data in **files** so that it's still there after the program ends. You open a file with `open()` – ideally together with `with`, so it's closed again automatically:")
        code("""
            with open("greeting.txt", "w", encoding="utf-8") as file:
                file.write("Hello file!\n")
                file.write("Second line\n")

            with open("greeting.txt", encoding="utf-8") as file:
                content = file.read()
            print(content)
        """, output = """
            Hello file!
            Second line
        """)
        text("""
            The second parameter is the **mode**:
            - `"r"` read (default)
            - `"w"` write – **overwrites** the file!
            - `"a"` append
            - `"x"` create new – error if it already exists
        """)
        warn("`write()` does **not** add a line break – you have to write the `\\n` yourself.")
        tip("Always specify `encoding=\"utf-8\"` – then special characters and emojis work reliably too.")
        text("The files are saved in the app's storage – in the code playground you can keep using them in your own programs.")

        choice("Which mode **overwrites** a file?", "\"w\"", "\"r\"", "\"a\"", "\"x\"", mono = true)
        truth("With `with open(...)`, the file is closed automatically at the end of the block.", true)
        output("""
            with open("t.txt", "w", encoding="utf-8") as f:
                f.write("abc")
            with open("t.txt", encoding="utf-8") as f:
                print(f.read())
        """, "abc", "t.txt", "3", "Error")
        output("""
            with open("n.txt", "w") as f:
                f.write("1")
            with open("n.txt", "w") as f:
                f.write("2")
            with open("n.txt") as f:
                print(f.read())
        """, "2", "12", "1", "Error", explain = "The mode `\"w\"` overwrites the old content.")
        typeOutput("""
            with open("a.txt", "w") as f:
                f.write("Py")
            with open("a.txt", "a") as f:
                f.write("thon")
            with open("a.txt") as f:
                print(f.read())
        """, "Python")
        fill("Open the file for **appending**.", """
            with open("log.txt", ??, encoding="utf-8") as f:
                f.write("New entry\n")
            print("saved")
        """, answers = listOf("\"a\""), distractors = listOf("\"r\"", "\"append\"", "\"+\""), output = "saved")
        match("\"r\"" to "read", "\"w\"" to "write (overwrite)", "\"a\"" to "append", "\"x\"" to "create new")
        write("Write the words `apple`, `pear` and `cherry`, each on its own line, into the file `fruit.txt`. Then read the file back in and print its content.",
            solution = """
                with open("fruit.txt", "w", encoding="utf-8") as f:
                    for fruit in ["apple", "pear", "cherry"]:
                        f.write(fruit + "\n")

                with open("fruit.txt", encoding="utf-8") as f:
                    print(f.read())
            """,
            output = """
                apple
                pear
                cherry
            """,
            mustContain = listOf("open"),
            hint = "Write with `\"w\"` and `f.write(fruit + \"\\n\")`, then read it back with `f.read()`.")
    }

    lesson("Line by Line", "Big files one line at a time", LessonIcon.LOOP) {
        text("It's best to read big files **line by line** – simply with a `for` loop over the file:")
        code("""
            with open("scores.txt", "w", encoding="utf-8") as f:
                f.write("Ada;95\nLinus;78\nGrace;88\n")

            with open("scores.txt", encoding="utf-8") as f:
                for line in f:
                    name, score = line.strip().split(";")
                    print(f"{name} scored {score}")
        """, output = """
            Ada scored 95
            Linus scored 78
            Grace scored 88
        """)
        text("Every line ends with a line break `\\n` – you remove it with `strip()`. `readlines()` gives you all the lines as a list:")
        code("""
            with open("scores.txt", "w", encoding="utf-8") as f:
                f.write("Ada;95\nLinus;78\nGrace;88\n")

            with open("scores.txt", encoding="utf-8") as f:
                lines = f.readlines()
            print(len(lines))
            print(repr(lines[0]))
        """, output = """
            3
            'Ada;95\n'
        """)
        tip("For writing many lines, `print(..., file=f)` is handy – `print` adds the line break automatically.")
        code("""
            with open("list.txt", "w", encoding="utf-8") as f:
                for i in range(1, 4):
                    print("Entry", i, file=f)

            with open("list.txt", encoding="utf-8") as f:
                print(f.read())
        """, output = """
            Entry 1
            Entry 2
            Entry 3
        """)
        warn("If a file doesn't exist, opening it for reading causes a **FileNotFoundError**. Check beforehand with `os.path.exists()` or catch the error.")

        output("""
            with open("z.txt", "w") as f:
                f.write("a\nb\nc\n")
            with open("z.txt") as f:
                print(len(f.readlines()))
        """, "3", "1", "6", "Error")
        output("""
            with open("y.txt", "w") as f:
                f.write("x\ny\n")
            with open("y.txt") as f:
                for line in f:
                    print(line.strip().upper())
        """, "X\nY", "x\ny", "XY", "Error")
        truth("Every line you read usually still has the line break `\\n` at the end.", true)
        choice("What happens with `open(\"doesnotexist.txt\")`?", "A FileNotFoundError", "An empty file is created", "It returns `None`", "Nothing")
        typeOutput("""
            with open("p.txt", "w") as f:
                print("Hello", "World", file=f)
            with open("p.txt") as f:
                print(f.read().strip())
        """, "Hello World")
        fill("Read the file line by line and count the lines.", """
            with open("numbers.txt", "w") as f:
                f.write("4\n8\n15\n")
            count = 0
            with open("numbers.txt") as f:
                ?? line ?? f:
                    count += 1
            print(count)
        """, answers = listOf("for", "in"), distractors = listOf("while", "of", "if"), output = "3")
        output("""
            try:
                with open("surely_missing_123.txt") as f:
                    print(f.read())
            except FileNotFoundError:
                print("File missing")
        """, "File missing", "Error", "None", "Nothing")
        write("The file `points.txt` contains one number per line – it's created for you at the top of the code. Read all the numbers and print their **sum**.",
            starter = """
                with open("points.txt", "w") as f:
                    f.write("12\n7\n30\n1\n")

                # Your code starts here:
            """,
            solution = """
                with open("points.txt", "w") as f:
                    f.write("12\n7\n30\n1\n")

                total = 0
                with open("points.txt") as f:
                    for line in f:
                        total += int(line)
                print(total)
            """,
            output = "50",
            mustContain = listOf("open"),
            hint = "Read line by line and add `int(line)` – `int()` ignores the `\\n` at the end.")
    }

    lesson("pathlib & CSV", "Modern paths and tables", LessonIcon.FILE) {
        text("The modern `pathlib` module makes working with paths and files especially convenient:")
        code("""
            from pathlib import Path

            file = Path("note.txt")
            file.write_text("Shopping: milk, bread", encoding="utf-8")
            print(file.exists())
            print(file.read_text(encoding="utf-8"))
            print(file.suffix, file.stem)
        """, output = """
            True
            Shopping: milk, bread
            .txt note
        """)
        text("**CSV files** store tables as text – every line is a record, and the values are separated by commas. Spreadsheet programs can open them. The `csv` module helps with reading and writing:")
        code("""
            import csv

            with open("class.csv", "w", newline="", encoding="utf-8") as f:
                writer = csv.writer(f)
                writer.writerow(["Name", "Age"])
                writer.writerow(["Ada", 14])
                writer.writerow(["Linus", 15])

            with open("class.csv", encoding="utf-8") as f:
                for row in csv.DictReader(f):
                    print(row["Name"], "is", row["Age"])
        """, output = """
            Ada is 14
            Linus is 15
        """)
        tip("`csv.DictReader` gives you each row as a dictionary – with the column names from the first row as keys.")
        warn("When writing CSV files, always specify `newline=\"\"` – otherwise you get empty lines in between on Windows.")

        output("""
            from pathlib import Path
            print(Path("picture.png").suffix)
        """, ".png", "png", "picture", "picture.png")
        output("""
            from pathlib import Path
            p = Path("x.txt")
            p.write_text("42")
            print(int(p.read_text()) + 1)
        """, "43", "421", "42", "Error")
        truth("`csv.DictReader` gives you each row as a dictionary.", true)
        choice("What typically separates the values in a CSV file?", "A comma", "A colon", "A tab is mandatory", "Nothing")
        typeOutput("""
            from pathlib import Path
            print(Path("data/report.pdf").stem)
        """, "report")
        fill("Check whether the file exists.", """
            from pathlib import Path
            p = Path("does_not_exist_123.txt")
            print(p.??())
        """, answers = listOf("exists"), distractors = listOf("is", "open", "read_text"), output = "False")
        output("""
            import csv
            with open("t.csv", "w", newline="") as f:
                csv.writer(f).writerow(["a", "b", "c"])
            with open("t.csv") as f:
                print(next(csv.reader(f)))
        """, "['a', 'b', 'c']", "a,b,c", "['a,b,c']", "Error")
        write("Use the `csv` module to write a file `shopping.csv` with the columns `item` and `price` and two rows: bread 2.5 and milk 1.5. Then read it with `csv.DictReader` and print the **sum of the prices**.",
            solution = """
                import csv

                with open("shopping.csv", "w", newline="", encoding="utf-8") as f:
                    w = csv.writer(f)
                    w.writerow(["item", "price"])
                    w.writerow(["bread", 2.5])
                    w.writerow(["milk", 1.5])

                total = 0
                with open("shopping.csv", encoding="utf-8") as f:
                    for row in csv.DictReader(f):
                        total += float(row["price"])
                print(total)
            """,
            output = "4.0",
            mustContain = listOf("csv"),
            hint = "When reading, all values are strings – convert the price with `float()`.")
    }

    checkpoint()
}
