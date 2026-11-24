package app.pyquest.content.en

import app.pyquest.content.LessonBuilder
import app.pyquest.data.CodeExercise
import app.pyquest.data.LessonIcon
import app.pyquest.data.LessonKind
import app.pyquest.i18n.AppLanguage

/** Tägliche Challenges auf Englisch – gleiche Reihenfolge und IDs wie die deutschen. */
fun buildEnChallenges(): List<CodeExercise> {
    val b = LessonBuilder("challenge", AppLanguage.EN)

    fun ch(title: String, description: String, signature: String, solution: String, tests: String, hint: String) {
        b.write(
            prompt = "**$title:** $description",
            starter = signature.trimIndent() + "\n    # Your code here\n    pass",
            solution = solution,
            test = tests.trimIndent() + "\nprint(\"✅ All tests passed!\")",
            hint = hint,
        )
    }

    ch("Palindrome Check", "Write `is_palindrome(text)` that returns `True` if a word reads the same forwards and backwards (upper and lower case don't matter).",
        "def is_palindrome(text):",
        """
            def is_palindrome(text):
                t = text.lower()
                return t == t[::-1]
        """,
        """
            assert is_palindrome("Anna") == True
            assert is_palindrome("Level") == True
            assert is_palindrome("Python") == False
            assert is_palindrome("") == True
        """,
        "`text[::-1]` reverses a string, and `.lower()` makes it lowercase.")
    ch("FizzBuzz", "Write `fizzbuzz(n)`: return `\"FizzBuzz\"` if n is divisible by 3 and 5, `\"Fizz\"` for 3, `\"Buzz\"` for 5 – otherwise the number as a string.",
        "def fizzbuzz(n):",
        """
            def fizzbuzz(n):
                if n % 15 == 0:
                    return "FizzBuzz"
                if n % 3 == 0:
                    return "Fizz"
                if n % 5 == 0:
                    return "Buzz"
                return str(n)
        """,
        """
            assert fizzbuzz(3) == "Fizz"
            assert fizzbuzz(10) == "Buzz"
            assert fizzbuzz(30) == "FizzBuzz"
            assert fizzbuzz(7) == "7"
        """,
        "Check the “divisible by 15” case first!")
    ch("Counting Vowels", "Write `vowels(text)` that counts how many vowels (a, e, i, o, u) a text contains – upper and lower case don't matter.",
        "def vowels(text):",
        """
            def vowels(text):
                return sum(1 for c in text.lower() if c in "aeiou")
        """,
        """
            assert vowels("Hello") == 2
            assert vowels("PYTHON") == 1
            assert vowels("Apple Pie") == 4
            assert vowels("xyz") == 0
        """,
        "Loop through `text.lower()` and count every letter that appears in `\"aeiou\"`.")
    ch("Largest Number", "Write `largest(numbers)` that returns the largest number in a list – **without** using `max()`.",
        "def largest(numbers):",
        """
            def largest(numbers):
                best = numbers[0]
                for n in numbers:
                    if n > best:
                        best = n
                return best
        """,
        """
            assert largest([3, 9, 2]) == 9
            assert largest([-5, -2, -9]) == -2
            assert largest([7]) == 7
        """,
        "Remember the largest number so far and compare every other number with it.")
    ch("Reversing Words", "Write `reverse_words(sentence)` that reverses the order of the words: `\"I like Python\"` → `\"Python like I\"`.",
        "def reverse_words(sentence):",
        """
            def reverse_words(sentence):
                return " ".join(sentence.split()[::-1])
        """,
        """
            assert reverse_words("I like Python") == "Python like I"
            assert reverse_words("Hello") == "Hello"
            assert reverse_words("a b c d") == "d c b a"
        """,
        "`split()`, then reverse the list with `[::-1]` and put it back together with `\" \".join()`.")
    ch("Prime Test", "Write `is_prime(n)` that returns `True` if n is a prime number.",
        "def is_prime(n):",
        """
            def is_prime(n):
                if n < 2:
                    return False
                for d in range(2, int(n ** 0.5) + 1):
                    if n % d == 0:
                        return False
                return True
        """,
        """
            assert is_prime(2) and is_prime(13) and is_prime(97)
            assert not is_prime(1)
            assert not is_prime(0)
            assert not is_prime(15)
            assert not is_prime(100)
        """,
        "Numbers below 2 aren't primes. Check the divisors from 2 up to the square root of n.")
    ch("Factorial", "Write `factorial(n)` that calculates n! = 1 · 2 · … · n (0! = 1).",
        "def factorial(n):",
        """
            def factorial(n):
                result = 1
                for i in range(2, n + 1):
                    result *= i
                return result
        """,
        """
            assert factorial(0) == 1
            assert factorial(1) == 1
            assert factorial(5) == 120
            assert factorial(10) == 3628800
        """,
        "Start with `result = 1` and multiply in a loop.")
    ch("Anagram", "Write `is_anagram(a, b)`: two words are anagrams if they consist of the same letters (upper/lower case and spaces don't matter).",
        "def is_anagram(a, b):",
        """
            def is_anagram(a, b):
                def norm(s):
                    return sorted(s.lower().replace(" ", ""))
                return norm(a) == norm(b)
        """,
        """
            assert is_anagram("Listen", "Silent")
            assert is_anagram("Tom Marvolo Riddle", "I am Lord Voldemort")
            assert not is_anagram("Python", "Java")
        """,
        "Compare the sorted letters: `sorted(text.lower().replace(\" \", \"\"))`.")
    ch("Digit Sum", "Write `digit_sum(n)` that calculates the sum of all the digits of a positive number.",
        "def digit_sum(n):",
        """
            def digit_sum(n):
                return sum(int(d) for d in str(n))
        """,
        """
            assert digit_sum(123) == 6
            assert digit_sum(9999) == 36
            assert digit_sum(7) == 7
        """,
        "Turn the number into a string and add up `int()` of every digit.")
    ch("Removing Duplicates", "Write `without_duplicates(items)` that returns a new list without repeated values – the **order** should stay the same.",
        "def without_duplicates(items):",
        """
            def without_duplicates(items):
                result = []
                for x in items:
                    if x not in result:
                        result.append(x)
                return result
        """,
        """
            assert without_duplicates([3, 1, 3, 2, 1]) == [3, 1, 2]
            assert without_duplicates(["a", "a"]) == ["a"]
            assert without_duplicates([]) == []
        """,
        "Only append values that aren't in the result yet – or use `list(dict.fromkeys(items))`.")
    ch("Temperature Converter", "Write `fahrenheit(celsius)` that converts Celsius to Fahrenheit (F = C · 9/5 + 32), rounded to one decimal place.",
        "def fahrenheit(celsius):",
        """
            def fahrenheit(celsius):
                return round(celsius * 9 / 5 + 32, 1)
        """,
        """
            assert fahrenheit(0) == 32.0
            assert fahrenheit(100) == 212.0
            assert fahrenheit(-40) == -40.0
            assert fahrenheit(36.6) == 97.9
        """,
        "`round(celsius * 9 / 5 + 32, 1)`")
    ch("Counting Letters", "Write `count_letters(text)` that returns a dictionary mapping every letter (in lowercase) to how often it appears. Ignore spaces and punctuation.",
        "def count_letters(text):",
        """
            def count_letters(text):
                result = {}
                for c in text.lower():
                    if c.isalpha():
                        result[c] = result.get(c, 0) + 1
                return result
        """,
        """
            assert count_letters("Aha!") == {"a": 2, "h": 1}
            assert count_letters("") == {}
            assert count_letters("Ab ba") == {"a": 2, "b": 2}
        """,
        "Only count characters where `c.isalpha()` is true – with `result.get(c, 0) + 1`.")
    ch("Second Largest Number", "Write `second_largest(numbers)` that returns the second largest **distinct** number.",
        "def second_largest(numbers):",
        """
            def second_largest(numbers):
                return sorted(set(numbers))[-2]
        """,
        """
            assert second_largest([4, 9, 1, 9]) == 4
            assert second_largest([1, 2]) == 1
            assert second_largest([5, 5, 3, 7, 7]) == 5
        """,
        "Remove duplicates with `set()`, sort, and take the second-to-last element.")
    ch("Caesar Cipher", "Write `caesar(text, k)` that shifts every **lowercase letter** a–z by k places in the alphabet (after z it continues at a). Other characters stay the same.",
        "def caesar(text, k):",
        """
            def caesar(text, k):
                result = ""
                for c in text:
                    if "a" <= c <= "z":
                        result += chr((ord(c) - ord("a") + k) % 26 + ord("a"))
                    else:
                        result += c
                return result
        """,
        """
            assert caesar("abc", 1) == "bcd"
            assert caesar("xyz", 3) == "abc"
            assert caesar("hello world!", 13) == "uryyb jbeyq!"
            assert caesar(caesar("python", 5), 21) == "python"
        """,
        "`ord()` gives you the number of a letter, `chr()` turns it back into a letter. `% 26` takes care of wrapping around.")
    ch("Leap Year", "Write `is_leap_year(year)`: a year is a leap year if it's divisible by 4 – unless it's divisible by 100 but not by 400.",
        "def is_leap_year(year):",
        """
            def is_leap_year(year):
                return year % 4 == 0 and (year % 100 != 0 or year % 400 == 0)
        """,
        """
            assert is_leap_year(2024)
            assert is_leap_year(2000)
            assert not is_leap_year(1900)
            assert not is_leap_year(2026)
        """,
        "`year % 4 == 0 and (year % 100 != 0 or year % 400 == 0)`")
    ch("Fair Average", "Write `fair_average(values)`: remove the smallest and the largest value and return the average of the remaining values.",
        "def fair_average(values):",
        """
            def fair_average(values):
                rest = sorted(values)[1:-1]
                return sum(rest) / len(rest)
        """,
        """
            assert fair_average([1, 5, 5, 9]) == 5.0
            assert fair_average([10, 2, 4, 6, 8]) == 6.0
        """,
        "Sort, then cut off the first and the last element with `[1:-1]`.")
    ch("Roman Numerals", "Write `roman(n)` that converts a number from 1 to 3999 into Roman numerals (e.g. 1994 → `MCMXCIV`).",
        "def roman(n):",
        """
            def roman(n):
                values = [(1000, "M"), (900, "CM"), (500, "D"), (400, "CD"), (100, "C"), (90, "XC"),
                          (50, "L"), (40, "XL"), (10, "X"), (9, "IX"), (5, "V"), (4, "IV"), (1, "I")]
                result = ""
                for value, symbol in values:
                    while n >= value:
                        result += symbol
                        n -= value
                return result
        """,
        """
            assert roman(3) == "III"
            assert roman(9) == "IX"
            assert roman(58) == "LVIII"
            assert roman(1994) == "MCMXCIV"
            assert roman(2026) == "MMXXVI"
        """,
        "Make a list of value–symbol pairs (including 900 → CM, 4 → IV …) and subtract greedily.")
    ch("Fibonacci List", "Write `fib_list(n)` that returns the first n Fibonacci numbers as a list (starting with 0, 1).",
        "def fib_list(n):",
        """
            def fib_list(n):
                result = []
                a, b = 0, 1
                for _ in range(n):
                    result.append(a)
                    a, b = b, a + b
                return result
        """,
        """
            assert fib_list(0) == []
            assert fib_list(1) == [0]
            assert fib_list(8) == [0, 1, 1, 2, 3, 5, 8, 13]
        """,
        "`a, b = b, a + b` elegantly calculates the next number.")
    ch("Password Checker", "Write `is_secure(password)`: a password is secure if it has at least 8 characters and contains at least one digit, one uppercase letter and one lowercase letter.",
        "def is_secure(password):",
        """
            def is_secure(password):
                return (len(password) >= 8
                        and any(c.isdigit() for c in password)
                        and any(c.isupper() for c in password)
                        and any(c.islower() for c in password))
        """,
        """
            assert is_secure("Python123")
            assert not is_secure("python123")
            assert not is_secure("PYTHON123")
            assert not is_secure("Py1")
            assert not is_secure("Pythonista")
        """,
        "Combine `len()` with `any(c.isdigit() for c in password)` and so on.")
    ch("Grouping by Length", "Write `group_by_length(words)` that returns a dictionary: word length → list of the words with that length (in their original order).",
        "def group_by_length(words):",
        """
            def group_by_length(words):
                groups = {}
                for w in words:
                    groups.setdefault(len(w), []).append(w)
                return groups
        """,
        """
            assert group_by_length(["ab", "cde", "fg"]) == {2: ["ab", "fg"], 3: ["cde"]}
            assert group_by_length([]) == {}
        """,
        "`groups.setdefault(len(w), []).append(w)` creates missing lists automatically.")
    ch("Transposing a Matrix", "Write `transpose(matrix)` that swaps the rows and columns of a matrix (a list of lists).",
        "def transpose(matrix):",
        """
            def transpose(matrix):
                return [list(row) for row in zip(*matrix)]
        """,
        """
            assert transpose([[1, 2, 3], [4, 5, 6]]) == [[1, 4], [2, 5], [3, 6]]
            assert transpose([[1]]) == [[1]]
        """,
        "`zip(*matrix)` gives you the columns – turn them into lists.")
    ch("Binary Number", "Write `binary(n)` that converts a positive number into a binary string – **without** `bin()` (tip: keep dividing by 2 and collect the remainders).",
        "def binary(n):",
        """
            def binary(n):
                if n == 0:
                    return "0"
                digits = ""
                while n > 0:
                    digits = str(n % 2) + digits
                    n //= 2
                return digits
        """,
        """
            assert binary(0) == "0"
            assert binary(5) == "101"
            assert binary(10) == "1010"
            assert binary(255) == "11111111"
        """,
        "The remainder `n % 2` is the next digit (from the right), then `n //= 2`.")
    ch("Most Frequent Element", "Write `most_frequent(items)` that returns the element that appears most often (if there's a tie, the one that appears first).",
        "def most_frequent(items):",
        """
            def most_frequent(items):
                from collections import Counter
                return Counter(items).most_common(1)[0][0]
        """,
        """
            assert most_frequent([1, 3, 3, 2]) == 3
            assert most_frequent(["a", "b", "a"]) == "a"
            assert most_frequent([5, 6]) == 5
        """,
        "`Counter(items).most_common(1)` gives you the most frequent element together with its count.")
    ch("Finding Divisors", "Write `divisors(n)` that returns a sorted list of all the divisors of n.",
        "def divisors(n):",
        """
            def divisors(n):
                return [d for d in range(1, n + 1) if n % d == 0]
        """,
        """
            assert divisors(12) == [1, 2, 3, 4, 6, 12]
            assert divisors(7) == [1, 7]
            assert divisors(1) == [1]
        """,
        "A list comprehension with `if n % d == 0`.")
    ch("Digital Root", "Write `digital_root(n)`: keep calculating the digit sum until only one digit is left (e.g. 9875 → 29 → 11 → 2).",
        "def digital_root(n):",
        """
            def digital_root(n):
                while n >= 10:
                    n = sum(int(d) for d in str(n))
                return n
        """,
        """
            assert digital_root(9875) == 2
            assert digital_root(5) == 5
            assert digital_root(999999) == 9
        """,
        "A `while` loop that runs as long as `n >= 10`.")
    ch("Bracket Check", "Write `brackets_ok(text)` that checks whether all round brackets `(` and `)` are opened and closed correctly.",
        "def brackets_ok(text):",
        """
            def brackets_ok(text):
                open_count = 0
                for c in text:
                    if c == "(":
                        open_count += 1
                    elif c == ")":
                        open_count -= 1
                        if open_count < 0:
                            return False
                return open_count == 0
        """,
        """
            assert brackets_ok("(a(b)c)")
            assert brackets_ok("")
            assert not brackets_ok("(()")
            assert not brackets_ok(")(")
        """,
        "Count the open brackets. If the counter goes below 0, it's wrong – and at the end it has to be 0.")
    ch("Run-Length Encoding", "Write `rle(text)` that combines identical consecutive characters: `\"aaabcc\"` → `\"a3b1c2\"`.",
        "def rle(text):",
        """
            def rle(text):
                if not text:
                    return ""
                result = ""
                current = text[0]
                count = 0
                for c in text:
                    if c == current:
                        count += 1
                    else:
                        result += current + str(count)
                        current = c
                        count = 1
                return result + current + str(count)
        """,
        """
            assert rle("aaabcc") == "a3b1c2"
            assert rle("x") == "x1"
            assert rle("") == ""
            assert rle("aabbaa") == "a2b2a2"
        """,
        "Remember the current character and how often it came up. When the character changes, write both into the result.")
    ch("Greatest Common Divisor", "Write `gcd(a, b)` using the **Euclidean algorithm** – without `math.gcd`.",
        "def gcd(a, b):",
        """
            def gcd(a, b):
                while b:
                    a, b = b, a % b
                return a
        """,
        """
            assert gcd(12, 18) == 6
            assert gcd(17, 5) == 1
            assert gcd(100, 75) == 25
        """,
        "As long as `b` isn't 0: `a, b = b, a % b`. Then `a` is the GCD.")
    ch("Numbers in Words", "Write `in_words(n)` that returns the numbers 0 to 12 as English words (e.g. 7 → `\"seven\"`).",
        "def in_words(n):",
        """
            def in_words(n):
                words = ["zero", "one", "two", "three", "four", "five", "six",
                         "seven", "eight", "nine", "ten", "eleven", "twelve"]
                return words[n]
        """,
        """
            assert in_words(0) == "zero"
            assert in_words(7) == "seven"
            assert in_words(12) == "twelve"
        """,
        "A list with all the words – the index is the number.")
    ch("Longest Word", "Write `longest(sentence)` that returns the longest word of a sentence (if there's a tie, the first one).",
        "def longest(sentence):",
        """
            def longest(sentence):
                return max(sentence.split(), key=len)
        """,
        """
            assert longest("I enjoy programming") == "programming"
            assert longest("ab cd ef") == "ab"
        """,
        "`max(sentence.split(), key=len)`")
    ch("Average per Subject", "Write `average_per_subject(scores)` that takes a dictionary subject → list of scores and returns a new dictionary subject → average (rounded to 2 decimal places).",
        "def average_per_subject(scores):",
        """
            def average_per_subject(scores):
                return {subject: round(sum(s) / len(s), 2) for subject, s in scores.items()}
        """,
        """
            result = average_per_subject({"Math": [90, 85, 78], "Art": [70, 95]})
            assert result == {"Math": 84.33, "Art": 82.5}
        """,
        "A dict comprehension over `scores.items()` with `round(sum(s) / len(s), 2)`.")
    ch("Counting Words", "Write `word_count(text)` that counts how many words a text has (words are separated by spaces; several spaces in a row don't count twice).",
        "def word_count(text):",
        """
            def word_count(text):
                return len(text.split())
        """,
        """
            assert word_count("Python is great") == 3
            assert word_count("  lots   of   spaces  ") == 3
            assert word_count("") == 0
        """,
        "`split()` without an argument takes care of multiple spaces.")

    return b.build("challenge", "challenge", "Challenges", "", LessonKind.LESSON, LessonIcon.ROCKET)
        .exercises.filterIsInstance<CodeExercise>()
}
