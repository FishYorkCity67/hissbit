package app.pyquest.data

/**
 * Die Kurse der App. [key] steht im gespeicherten Fortschritt (z.B. aktiver Kurs) –
 * nicht ändern. Lektions-IDs von Python haben kein Präfix (aus PyQuest-Zeiten),
 * die anderen Kurse beginnen mit "[idPrefix]-".
 */
enum class CourseId(
    val key: String,
    val idPrefix: String,
    val extension: String,
    val displayName: String,
    val emoji: String,
) {
    PYTHON("python", "", "py", "Python", "Py"),
    JAVASCRIPT("javascript", "js", "js", "JavaScript", "JS");

    companion object {
        fun byKey(key: String?): CourseId = entries.firstOrNull { it.key == key } ?: PYTHON
    }
}
