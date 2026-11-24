package app.pyquest.content

import app.pyquest.data.Course
import app.pyquest.data.CourseId
import app.pyquest.i18n.AppLanguage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Gespeicherter Fortschritt verweist auf Lektions- und Aufgaben-IDs (z.B. "u03-l2" und
 * "u03-l2-e4"). Diese IDs entstehen aus der Reihenfolge im Kurs. Würde eine Lektion
 * eingefügt oder eine Aufgabe entfernt, zeigte der Fortschritt auf falsche Inhalte.
 *
 * Der Test vergleicht jeden Kurs mit einer gespeicherten Liste aller bisherigen IDs: Jede
 * dort aufgeführte ID muss es weiterhin geben – mit demselben Aufgabentyp. Neue IDs
 * (z.B. angehängte Lektionen) sind erlaubt. Außerdem müssen alle Übersetzungen eines Kurses
 * exakt dieselben IDs haben, damit beim Sprachwechsel nichts verloren geht.
 */
class ContentIdsStableTest {

    private fun idsOf(course: Course): List<String> = buildList {
        course.lessons.forEach { lesson ->
            add("lesson\t${lesson.id}")
            lesson.exercises.forEach { add("exercise\t${it.id}\t${it::class.simpleName}") }
        }
    }

    private fun checkSnapshot(id: CourseId) {
        val snapshot = File("src/test/resources/content-ids-${id.key}.txt")
        val current = idsOf(Catalog.course(id, AppLanguage.DE))
        if (!snapshot.exists()) {
            snapshot.parentFile?.mkdirs()
            snapshot.writeText(current.joinToString("\n") + "\n")
            println("Snapshot angelegt (${id.key}): ${current.size} IDs")
            return
        }
        val known = current.toSet()
        val missing = snapshot.readLines().filter { it.isNotBlank() && it !in known }
        assertTrue(
            "${id.key}: Diese IDs gibt es nicht mehr (oder mit anderem Typ) – gespeicherter Fortschritt würde " +
                "auf falsche Inhalte zeigen:\n" + missing.take(30).joinToString("\n"),
            missing.isEmpty(),
        )
    }

    @Test
    fun savedProgressStillMatchesContent() {
        for (id in Catalog.courses) checkSnapshot(id)
    }

    /** Auch unfertige Übersetzungen: Jede schon übersetzte Einheit muss exakt zur deutschen passen. */
    @Test
    fun draftTranslationsMatchUnitByUnit() {
        for (id in CourseId.entries) {
            val german = Catalog.draftBundle(id, AppLanguage.DE)?.course ?: continue
            for (lang in AppLanguage.entries) {
                if (lang == AppLanguage.DE) continue
                val draft = Catalog.draftBundle(id, lang)?.course ?: continue
                draft.units.forEachIndexed { i, unit ->
                    val de = german.units[i]
                    val expected = idsOf(Course(listOf(de), id))
                    val actual = idsOf(Course(listOf(unit), id))
                    assertEquals("${id.key}/${lang.code} Einheit ${i + 1} („${unit.title}“) weicht strukturell ab", expected, actual)
                }
            }
        }
    }

    @Test
    fun translationsHaveIdenticalIds() {
        for (id in Catalog.courses) {
            val german = idsOf(Catalog.course(id, AppLanguage.DE))
            for (lang in AppLanguage.entries) {
                val bundle = Catalog.exactBundle(id, lang) ?: continue
                assertEquals("${id.key}/${lang.code}: andere Lektionen/Aufgaben als die deutsche Fassung", german, idsOf(bundle.course))
                assertEquals("${id.key}/${lang.code}: andere Anzahl Challenges",
                    Catalog.bundle(id, AppLanguage.DE).challenges.map { it.id }, bundle.challenges.map { it.id })
            }
        }
    }
}
