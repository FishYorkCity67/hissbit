package app.pyquest.content.js.en

import app.pyquest.content.LessonBuilder
import app.pyquest.content.js.D
import app.pyquest.data.CodeExercise
import app.pyquest.data.LessonIcon
import app.pyquest.data.LessonKind
import app.pyquest.i18n.AppLanguage

/**
 * Daily JavaScript challenges in English – same order and IDs as the German ones. The tests run in their own
 * block so that the helper name `check` can't collide with names from the solution code.
 */
fun buildJsEnChallenges(): List<CodeExercise> {
    val b = LessonBuilder("js-challenge", AppLanguage.EN)

    fun ch(title: String, description: String, signature: String, solution: String, tests: String, hint: String) {
        b.write(
            prompt = "**$title:** $description",
            starter = signature.trimIndent() + "\n  // Your code here\n}",
            solution = solution,
            test = "{\n" + CHECK_HELPER + "\n" + tests.trimIndent() + "\n}\nconsole.log(\"✅ All tests passed!\");",
            hint = hint,
        )
    }

    ch("Palindrome Check", "Write `isPalindrome(text)` that returns `true` if a word reads the same forwards and backwards (upper and lower case don't matter).",
        "function isPalindrome(text) {",
        """
            function isPalindrome(text) {
              const t = text.toLowerCase();
              return t === [...t].reverse().join("");
            }
        """,
        """
            check(() => isPalindrome("Anna"), true);
            check(() => isPalindrome("Level"), true);
            check(() => isPalindrome("Monty"), false);
            check(() => isPalindrome(""), true);
        """,
        "With `[...text].reverse().join(\"\")` you reverse a string, and with `toLowerCase()` you make it lowercase.")
    ch("FizzBuzz", "Write `fizzbuzz(n)`: return `\"FizzBuzz\"` if n is divisible by 3 and 5, `\"Fizz\"` for 3, `\"Buzz\"` for 5 – otherwise the number as a string.",
        "function fizzbuzz(n) {",
        """
            function fizzbuzz(n) {
              if (n % 15 === 0) return "FizzBuzz";
              if (n % 3 === 0) return "Fizz";
              if (n % 5 === 0) return "Buzz";
              return String(n);
            }
        """,
        """
            check(() => fizzbuzz(3), "Fizz");
            check(() => fizzbuzz(10), "Buzz");
            check(() => fizzbuzz(30), "FizzBuzz");
            check(() => fizzbuzz(7), "7");
        """,
        "Check the case “divisible by 15” first!")
    ch("Count Vowels", "Write `countVowels(text)` that counts how many vowels (a, e, i, o, u) a text contains – upper and lower case don't matter.",
        "function countVowels(text) {",
        """
            function countVowels(text) {
              let count = 0;
              for (const letter of text.toLowerCase()) {
                if ("aeiou".includes(letter)) count++;
              }
              return count;
            }
        """,
        """
            check(() => countVowels("Hello World"), 3);
            check(() => countVowels("JAVASCRIPT"), 3);
            check(() => countVowels("Queue"), 4);
            check(() => countVowels("xyz"), 0);
        """,
        "Loop over the letters with `for…of` and check with `\"aeiou\".includes(letter)`.")
    ch("Largest Number", "Write `largest(numbers)` that returns the largest number in an array. For an empty array, return `undefined`.",
        "function largest(numbers) {",
        """
            function largest(numbers) {
              if (numbers.length === 0) return undefined;
              let max = numbers[0];
              for (const n of numbers) {
                if (n > max) max = n;
              }
              return max;
            }
        """,
        """
            check(() => largest([3, 9, 2]), 9);
            check(() => largest([-5, -1, -9]), -1);
            check(() => largest([42]), 42);
            check(() => largest([]), undefined);
        """,
        "Remember the largest number so far and compare each further number with it – or use `Math.max(...numbers)`.")
    ch("Digit Sum", "Write `digitSum(n)` that returns the sum of all digits of a positive whole number (e.g. 1234 → 10).",
        "function digitSum(n) {",
        """
            function digitSum(n) {
              return String(n).split("").reduce((s, d) => s + Number(d), 0);
            }
        """,
        """
            check(() => digitSum(1234), 10);
            check(() => digitSum(9), 9);
            check(() => digitSum(1000), 1);
            check(() => digitSum(99999), 45);
        """,
        "Turn the number into text with `String(n)`, split it into digits and add them up as numbers.")
    ch("Reverse Words", "Write `reverseWords(sentence)` that reverses the order of the words: `\"Hello beautiful world\"` → `\"world beautiful Hello\"`.",
        "function reverseWords(sentence) {",
        """
            function reverseWords(sentence) {
              return sentence.split(" ").reverse().join(" ");
            }
        """,
        """
            check(() => reverseWords("Hello beautiful world"), "world beautiful Hello");
            check(() => reverseWords("one two"), "two one");
            check(() => reverseWords("Solo"), "Solo");
        """,
        "`split(\" \")`, `reverse()` and `join(\" \")`.")
    ch("Factorial", "Write `factorial(n)` that calculates n! = 1 · 2 · … · n. 0! = 1 applies.",
        "function factorial(n) {",
        """
            function factorial(n) {
              let result = 1;
              for (let i = 2; i <= n; i++) result *= i;
              return result;
            }
        """,
        """
            check(() => factorial(0), 1);
            check(() => factorial(1), 1);
            check(() => factorial(5), 120);
            check(() => factorial(10), 3628800);
        """,
        "Start with `result = 1` and multiply by 2, 3, … up to n in a loop.")
    ch("Prime Number", "Write `isPrime(n)` that returns `true` if n is a prime number (only divisible by 1 and itself, greater than 1).",
        "function isPrime(n) {",
        """
            function isPrime(n) {
              if (n < 2) return false;
              for (let d = 2; d * d <= n; d++) {
                if (n % d === 0) return false;
              }
              return true;
            }
        """,
        """
            check(() => isPrime(2), true);
            check(() => isPrime(17), true);
            check(() => isPrime(1), false);
            check(() => isPrime(21), false);
            check(() => isPrime(97), true);
        """,
        "Test all divisors from 2 up to the square root of n. If you find one, n is not prime.")
    ch("Remove Duplicates", "Write `removeDuplicates(list)` that returns a new array without duplicate values – the order of first appearance is kept.",
        "function removeDuplicates(list) {",
        """
            function removeDuplicates(list) {
              return [...new Set(list)];
            }
        """,
        """
            check(() => removeDuplicates([1, 2, 2, 3, 1]), [1, 2, 3]);
            check(() => removeDuplicates(["b", "a", "b"]), ["b", "a"]);
            check(() => removeDuplicates([]), []);
        """,
        "A `Set` stores each value only once: `[...new Set(list)]`.")
    ch("Anagram", "Write `isAnagram(a, b)`: `true` if both words consist of exactly the same letters (upper and lower case don't matter), e.g. “Listen” and “Silent”.",
        "function isAnagram(a, b) {",
        """
            function isAnagram(a, b) {
              const sorted = (w) => [...w.toLowerCase()].sort().join("");
              return sorted(a) === sorted(b);
            }
        """,
        """
            check(() => isAnagram("Listen", "Silent"), true);
            check(() => isAnagram("Angered", "Enraged"), true);
            check(() => isAnagram("House", "Mouse"), false);
            check(() => isAnagram("ab", "abb"), false);
        """,
        "Sort the letters of both words and compare the results.")
    ch("Title Case", "Write `titleCase(text)` that makes every word start with a capital letter: `\"hello beautiful world\"` → `\"Hello Beautiful World\"`.",
        "function titleCase(text) {",
        """
            function titleCase(text) {
              return text
                .split(" ")
                .map((w) => (w ? w[0].toUpperCase() + w.slice(1) : w))
                .join(" ");
            }
        """,
        """
            check(() => titleCase("hello beautiful world"), "Hello Beautiful World");
            check(() => titleCase("javaScript"), "JavaScript");
            check(() => titleCase("a b c"), "A B C");
        """,
        "Split into words, make `w[0]` uppercase for each one and append `w.slice(1)`.")
    ch("Count Characters", "Write `countChars(text)` that returns an object telling how often each character occurs.",
        "function countChars(text) {",
        """
            function countChars(text) {
              const result = {};
              for (const c of text) {
                result[c] = (result[c] ?? 0) + 1;
              }
              return result;
            }
        """,
        """
            check(() => countChars("banana"), { b: 1, a: 3, n: 2 });
            check(() => countChars("aaa"), { a: 3 });
            check(() => countChars(""), {});
        """,
        "An empty object `{}` as the counter, then `result[c] = (result[c] ?? 0) + 1`.")
    ch("Only Even Numbers", "Write `onlyEven(numbers)` that returns a new array with all the even numbers.",
        "function onlyEven(numbers) {",
        """
            function onlyEven(numbers) {
              return numbers.filter((n) => n % 2 === 0);
            }
        """,
        """
            check(() => onlyEven([1, 2, 3, 4, 5, 6]), [2, 4, 6]);
            check(() => onlyEven([7, 9]), []);
            check(() => onlyEven([0, -2]), [0, -2]);
        """,
        "`filter` with the condition `n % 2 === 0`.")
    ch("Average", "Write `average(numbers)` that returns the mean of an array – `0` for an empty array.",
        "function average(numbers) {",
        """
            function average(numbers) {
              if (numbers.length === 0) return 0;
              return numbers.reduce((s, n) => s + n, 0) / numbers.length;
            }
        """,
        """
            check(() => average([2, 4, 6]), 4);
            check(() => average([5]), 5);
            check(() => average([1, 2]), 1.5);
            check(() => average([]), 0);
        """,
        "Divide the `reduce` sum by the length – handle the empty case beforehand.")
    ch("Fibonacci", "Write `fibonacci(n)` that returns an array with the first n Fibonacci numbers: 0, 1, 1, 2, 3, 5, …",
        "function fibonacci(n) {",
        """
            function fibonacci(n) {
              const sequence = [];
              let a = 0;
              let b = 1;
              for (let i = 0; i < n; i++) {
                sequence.push(a);
                [a, b] = [b, a + b];
              }
              return sequence;
            }
        """,
        """
            check(() => fibonacci(1), [0]);
            check(() => fibonacci(7), [0, 1, 1, 2, 3, 5, 8]);
            check(() => fibonacci(0), []);
        """,
        "Two variables `a` and `b`; in every round store `a` and then do `[a, b] = [b, a + b]`.")
    ch("Longest Word", "Write `longestWord(sentence)` that returns the longest word. In a tie, the first one wins.",
        "function longestWord(sentence) {",
        """
            function longestWord(sentence) {
              let best = "";
              for (const w of sentence.split(" ")) {
                if (w.length > best.length) best = w;
              }
              return best;
            }
        """,
        """
            check(() => longestWord("I learn JavaScript today"), "JavaScript");
            check(() => longestWord("ab cd ef"), "ab");
            check(() => longestWord("Hello"), "Hello");
        """,
        "Remember the longest word so far – only replace it if the new one is really longer.")
    ch("Caesar Cipher", "Write `caesar(text, n)` that shifts every lowercase letter a–z by n places in the alphabet (after z it continues at a). Other characters stay the same.",
        "function caesar(text, n) {",
        """
            function caesar(text, n) {
              let result = "";
              for (const c of text) {
                if (c >= "a" && c <= "z") {
                  const pos = (c.charCodeAt(0) - 97 + n) % 26;
                  result += String.fromCharCode(97 + pos);
                } else {
                  result += c;
                }
              }
              return result;
            }
        """,
        """
            check(() => caesar("abc", 1), "bcd");
            check(() => caesar("xyz", 3), "abc");
            check(() => caesar("hello world!", 13), "uryyb jbeyq!");
        """,
        "`c.charCodeAt(0) - 97` is the position in the alphabet, `% 26` makes it wrap around, and `String.fromCharCode` turns it back into a letter.")
    ch("Countdown Array", "Write `countdown(n)` that returns the array `[n, n-1, …, 1]`.",
        "function countdown(n) {",
        """
            function countdown(n) {
              const result = [];
              for (let i = n; i >= 1; i--) result.push(i);
              return result;
            }
        """,
        """
            check(() => countdown(3), [3, 2, 1]);
            check(() => countdown(1), [1]);
            check(() => countdown(0), []);
        """,
        "A loop that counts backwards from n to 1.")
    ch("Top of the Class", "Write `best(students)`: the array contains objects with `name` and `points`. Return the name of the person with the most points.",
        "function best(students) {",
        """
            function best(students) {
              return students.reduce((top, s) => (s.points > top.points ? s : top)).name;
            }
        """,
        """
            check(() => best([{ name: "Ada", points: 80 }, { name: "Ben", points: 95 }]), "Ben");
            check(() => best([{ name: "Cleo", points: 10 }]), "Cleo");
            check(() => best([{ name: "X", points: 5 }, { name: "Y", points: 3 }, { name: "Z", points: 4 }]), "X");
        """,
        "Compare the points in a loop or with `reduce`, and return `.name` at the end.")
    ch("Sum up to n", "Write `sumTo(n)` that calculates 1 + 2 + … + n.",
        "function sumTo(n) {",
        """
            function sumTo(n) {
              let s = 0;
              for (let i = 1; i <= n; i++) s += i;
              return s;
            }
        """,
        """
            check(() => sumTo(1), 1);
            check(() => sumTo(10), 55);
            check(() => sumTo(100), 5050);
        """,
        "A loop – or little Gauss's formula: `n * (n + 1) / 2`.")
    ch("Leap Year", "Write `isLeapYear(year)`: a year is a leap year if it's divisible by 4 – unless it's divisible by 100, in which case only if it's also divisible by 400.",
        "function isLeapYear(year) {",
        """
            function isLeapYear(year) {
              return (year % 4 === 0 && year % 100 !== 0) || year % 400 === 0;
            }
        """,
        """
            check(() => isLeapYear(2024), true);
            check(() => isLeapYear(1900), false);
            check(() => isLeapYear(2000), true);
            check(() => isLeapYear(2023), false);
        """,
        "Combine the three rules with `&&` and `||`.")
    ch("Grouping", "Write `groupBy(list, key)` that collects objects into an object of arrays, grouped by the value of a property.",
        "function groupBy(list, key) {",
        """
            function groupBy(list, key) {
              const groups = {};
              for (const item of list) {
                const k = item[key];
                if (!groups[k]) groups[k] = [];
                groups[k].push(item);
              }
              return groups;
            }
        """,
        """
            const animals = [
              { name: "Rex", kind: "dog" },
              { name: "Mimi", kind: "cat" },
              { name: "Bello", kind: "dog" },
            ];
            check(() => groupBy(animals, "kind"), {
              dog: [{ name: "Rex", kind: "dog" }, { name: "Bello", kind: "dog" }],
              cat: [{ name: "Mimi", kind: "cat" }],
            });
            check(() => groupBy([], "x"), {});
        """,
        "For each new key, create an empty array and `push` the element into it.")
    ch("Flatten", "Write `flatten(arrays)` that joins an array of arrays into a single array: `[[1, 2], [3], []]` → `[1, 2, 3]`.",
        "function flatten(arrays) {",
        """
            function flatten(arrays) {
              const result = [];
              for (const a of arrays) result.push(...a);
              return result;
            }
        """,
        """
            check(() => flatten([[1, 2], [3], []]), [1, 2, 3]);
            check(() => flatten([["a"], ["b", "c"]]), ["a", "b", "c"]);
            check(() => flatten([]), []);
        """,
        "`result.push(...a)` appends all elements of a sub-array. There's also the built-in method `arrays.flat()`.")
    ch("Intersection", "Write `common(a, b)` that returns all values from `a` that also appear in `b` (in the order of `a`, without duplicates).",
        "function common(a, b) {",
        """
            function common(a, b) {
              const inB = new Set(b);
              return [...new Set(a.filter((x) => inB.has(x)))];
            }
        """,
        """
            check(() => common([1, 2, 3, 4], [4, 2, 9]), [2, 4]);
            check(() => common(["a", "a", "b"], ["a"]), ["a"]);
            check(() => common([1, 2], [3]), []);
        """,
        "With `filter` and `includes` (or a `Set`) you find common values; another `Set` removes duplicates.")
    ch("Second Largest Number", "Write `secondLargest(numbers)` that returns the second-largest **distinct** number – if there is none, `undefined`.",
        "function secondLargest(numbers) {",
        """
            function secondLargest(numbers) {
              const sorted = [...new Set(numbers)].sort((a, b) => b - a);
              return sorted[1];
            }
        """,
        """
            check(() => secondLargest([4, 9, 2, 9]), 4);
            check(() => secondLargest([1, 2]), 1);
            check(() => secondLargest([5, 5]), undefined);
            check(() => secondLargest([10, 100, 25]), 25);
        """,
        "Remove duplicates, sort in descending order (don't forget the compare function!) and take index 1.")
    ch("Compress Text", "Write `compress(text)` that merges identical characters in a row: `\"aaabcc\"` → `\"a3b1c2\"`.",
        "function compress(text) {",
        """
            function compress(text) {
              let result = "";
              let i = 0;
              while (i < text.length) {
                let j = i;
                while (j < text.length && text[j] === text[i]) j++;
                result += text[i] + (j - i);
                i = j;
              }
              return result;
            }
        """,
        """
            check(() => compress("aaabcc"), "a3b1c2");
            check(() => compress("x"), "x1");
            check(() => compress(""), "");
            check(() => compress("aabbaa"), "a2b2a2");
        """,
        "Count how long the same character repeats, append character + count, and jump ahead.")
    ch("Check Brackets", "Write `bracketsOk(text)` that returns `true` if all round brackets are opened and closed correctly.",
        "function bracketsOk(text) {",
        """
            function bracketsOk(text) {
              let depth = 0;
              for (const c of text) {
                if (c === "(") depth++;
                if (c === ")") {
                  depth--;
                  if (depth < 0) return false;
                }
              }
              return depth === 0;
            }
        """,
        """
            check(() => bracketsOk("(()())"), true);
            check(() => bracketsOk("(a + b) * (c)"), true);
            check(() => bracketsOk("(()"), false);
            check(() => bracketsOk(")("), false);
        """,
        "Keep count of open brackets. If the counter drops below 0, it's wrong – at the end it must be exactly 0.")
    ch("Binary Number", "Write `toBinary(n)` that returns a positive whole number as a binary string (e.g. 10 → `\"1010\"`), without using `toString(2)`.",
        "function toBinary(n) {",
        """
            function toBinary(n) {
              if (n === 0) return "0";
              let result = "";
              while (n > 0) {
                result = (n % 2) + result;
                n = Math.floor(n / 2);
              }
              return result;
            }
        """,
        """
            check(() => toBinary(10), "1010");
            check(() => toBinary(1), "1");
            check(() => toBinary(0), "0");
            check(() => toBinary(255), "11111111");
        """,
        "Keep dividing by 2 – the remainders read from back to front give the binary number.")
    ch("Format Time", "Write `formatTime(seconds)` that formats seconds as `M:SS`, e.g. 125 → `\"2:05\"`.",
        "function formatTime(seconds) {",
        """
            function formatTime(seconds) {
              const m = Math.floor(seconds / 60);
              const s = seconds % 60;
              return `${D}{m}:${D}{String(s).padStart(2, "0")}`;
            }
        """,
        """
            check(() => formatTime(125), "2:05");
            check(() => formatTime(59), "0:59");
            check(() => formatTime(600), "10:00");
        """,
        "Minutes with `Math.floor(seconds / 60)`, seconds with `%`, leading zero with `padStart(2, \"0\")`.")
    ch("Count Words", "Write `countWords(text)` that counts how many words a text has. Several spaces in a row count as one gap.",
        "function countWords(text) {",
        """
            function countWords(text) {
              return text.split(" ").filter((w) => w !== "").length;
            }
        """,
        """
            check(() => countWords("Hello world"), 2);
            check(() => countWords("  many   spaces here "), 3);
            check(() => countWords(""), 0);
        """,
        "After `split(\" \")`, several spaces in a row produce empty strings – filter them out.")
    ch("Rotate Array", "Write `rotate(list, k)` that shifts the elements k places to the right: `rotate([1, 2, 3, 4], 1)` → `[4, 1, 2, 3]`.",
        "function rotate(list, k) {",
        """
            function rotate(list, k) {
              if (list.length === 0) return [];
              const n = k % list.length;
              if (n === 0) return [...list];
              return [...list.slice(-n), ...list.slice(0, -n)];
            }
        """,
        """
            check(() => rotate([1, 2, 3, 4], 1), [4, 1, 2, 3]);
            check(() => rotate([1, 2, 3, 4], 2), [3, 4, 1, 2]);
            check(() => rotate([1, 2, 3], 3), [1, 2, 3]);
            check(() => rotate([1, 2, 3], 4), [3, 1, 2]);
            check(() => rotate([], 5), []);
        """,
        "With `k % list.length` the shift stays in range. The last k elements move to the front: `slice(-k)` and `slice(0, -k)`.")
    return b.build("js-challenge", "js-challenge", "Challenges", "", LessonKind.LESSON, LessonIcon.ROCKET)
        .exercises.filterIsInstance<CodeExercise>()
}

/** Test helper for challenges: compares via JSON and reports clearly what's wrong. */
private val CHECK_HELPER = """
    const check = (call, expected) => {
      const actual = call();
      const text = (v) => (v === undefined ? "undefined" : JSON.stringify(v));
      if (text(actual) !== text(expected)) {
        const what = String(call).replace(/^\(\)\s*=>\s*/, "");
        throw new Error(what + " should return " + text(expected) + ", but returns " + text(actual));
      }
    };
""".trimIndent()
