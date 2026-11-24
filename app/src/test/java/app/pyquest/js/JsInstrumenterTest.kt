package app.pyquest.js

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class JsInstrumenterTest {
    private fun inst(code: String) = JsInstrumenter.instrument(code).code

    @Test
    fun whileLoop() {
        assertEquals("while (__hb_g() && (x < 3)) { x++ }", inst("while (x < 3) { x++ }"))
    }

    @Test
    fun doWhile() {
        assertEquals("do { x++ } while (__hb_g() && (x < 3));", inst("do { x++ } while (x < 3);"))
    }

    @Test
    fun classicFor() {
        assertEquals("for (let i = 0; __hb_g() && ( i < 3); i++) {}", inst("for (let i = 0; i < 3; i++) {}"))
        assertEquals("for (; __hb_g();) {}", inst("for (;;) {}"))
        assertEquals("for (let i = 0; __hb_g() ; i++) {}", inst("for (let i = 0; ; i++) {}"))
    }

    @Test
    fun forOf() {
        assertEquals("for (const x of __hb_gi(arr, \"arr\")) {}", inst("for (const x of arr) {}"))
        assertEquals(
            "for (const [k, v] of __hb_gi(Object.entries(o), \"Object.entries(...)\")) {}",
            inst("for (const [k, v] of Object.entries(o)) {}"),
        )
        assertEquals("for (const of of __hb_gi(ofs, \"ofs\")) {}", inst("for (const of of ofs) {}"))
    }

    @Test
    fun leavesOtherCodeAlone() {
        val samples = listOf(
            "for (const k in obj) {}",
            "for await (const x of stream) {}",
            "const s = 'while (true) {}';",
            "// while (true) {}",
            "/* for (;;) {} */",
            "const re = /while (x)/g;",
            "const t = `for (;;) {}`;",
            "obj.while(1); obj?.for(2);",
            "const o = { while: 1, for: 2 };",
            "const broken = 'no end\nwhile (true) {}",
        )
        for (s in samples) assertEquals(s, inst(s))
    }

    @Test
    fun insideTemplateExpression() {
        assertEquals(
            "`\${list.map(x => { while (__hb_g() && (x)) {} return x })}`",
            inst("`\${list.map(x => { while (x) {} return x })}`"),
        )
    }

    @Test
    fun nestedLoopsAndLineNumbersStay() {
        val code = "let n = 0;\nfor (let i = 0; i < 3; i++) {\n  while (n < i) n++;\n}\nconsole.log(n);"
        val out = inst(code)
        assertEquals(code.count { it == '\n' }, out.count { it == '\n' })
        assertEquals(
            "let n = 0;\nfor (let i = 0; __hb_g() && ( i < 3); i++) {\n  while (__hb_g() && (n < i)) n++;\n}\nconsole.log(n);",
            out,
        )
    }

    @Test
    fun columnsMapBack() {
        val r = JsInstrumenter.instrument("while (x < 3) { y.z }")
        // "y" steht im Original in Spalte 17, im neuen Code 17 + "__hb_g() && (".length + ")".length
        val newCol = r.code.indexOf("y.z") + 1
        assertEquals(17, r.originalColumn(1, newCol))
        // Spalten vor der Einfügung bleiben gleich
        assertEquals(1, r.originalColumn(1, 1))
        // Spalten in anderen Zeilen bleiben gleich
        assertEquals(5, r.originalColumn(2, 5))
    }

    @Test
    fun regexAfterKeywords() {
        val code = "if (x) { return /a(b/.test(s) }\nwhile (y) {}"
        assertEquals("if (x) { return /a(b/.test(s) }\nwhile (__hb_g() && (y)) {}", inst(code))
    }

    @Test
    fun divisionIsNotRegex() {
        val code = "const a = (b + c) / 2; while (a / 2 > 1) {}"
        assertEquals("const a = (b + c) / 2; while (__hb_g() && (a / 2 > 1)) {}", inst(code))
    }

    @Test
    fun unbalancedStaysUntouched() {
        assertFalse(JsInstrumenter.instrument("while (x < 3 {").changed)
        assertFalse(JsInstrumenter.instrument("for (let i = 0; i < 3; i++ {").changed)
    }
}
