package app.pyquest.data

import app.pyquest.i18n.LanguagePref
import app.pyquest.i18n.Txt
import kotlinx.serialization.Serializable

/*
 * Achtung beim Ändern: Diese Klassen werden als JSON gespeichert. Damit bei Updates kein
 * Fortschritt verloren geht, dürfen Felder nicht umbenannt oder entfernt werden – neue Felder
 * brauchen immer einen Standardwert.
 */

const val DEFAULT_PLAYGROUND_CODE = """# Willkommen im Code-Spielplatz!
# Hier kannst du frei mit Python experimentieren.

name = input("Wie heißt du? ")
print(f"Hallo {name}! 🐍")

for i in range(1, 4):
    print(i, "x Python macht Spaß")
"""

const val DEFAULT_PLAYGROUND_CODE_EN = """# Welcome to the code playground!
# Experiment freely with Python here.

name = input("What's your name? ")
print(f"Hello {name}! 🐍")

for i in range(1, 4):
    print(i, "x Python is fun")
"""

const val DEFAULT_PLAYGROUND_JS = """// Willkommen im Code-Spielplatz!
// Hier kannst du frei mit JavaScript experimentieren.

const name = prompt("Wie heißt du?");
console.log(`Hallo ${'$'}{name}!`);

for (let i = 1; i <= 3; i++) {
  console.log(i, "x JavaScript macht Spaß");
}
"""

const val DEFAULT_PLAYGROUND_JS_EN = """// Welcome to the code playground!
// Experiment freely with JavaScript here.

const name = prompt("What's your name?");
console.log(`Hello ${'$'}{name}!`);

for (let i = 1; i <= 3; i++) {
  console.log(i, "x JavaScript is fun");
}
"""

@Serializable
data class LessonRecord(
    val completions: Int = 0,
    val bestAccuracy: Int = 0,
    val lastCompleted: Long = 0,
)

@Serializable
data class Snippet(
    val id: Long,
    val title: String,
    val code: String,
    val updated: Long = 0,
    /** "python" oder "javascript" */
    val language: String = "python",
)

@Serializable
data class UserProgress(
    val onboarded: Boolean = false,
    val name: String = "",
    val createdAt: Long = 0,
    /** Python: Einheiten mit kleinerer Nummer gelten als übersprungen (freigeschaltet). */
    val startUnit: Int = 1,
    val lessons: Map<String, LessonRecord> = emptyMap(),
    val xpByDay: Map<String, Int> = emptyMap(),
    val totalXp: Int = 0,
    /** In der App heißen sie "Bits" – der Feldname bleibt wegen gespeicherter Daten. */
    val gems: Int = 100,
    val hearts: Int = Game.MAX_HEARTS,
    val heartsRefillAt: Long = 0,
    val streak: Int = 0,
    val lastStreakDay: String? = null,
    val longestStreak: Int = 0,
    val streakFreezes: Int = 0,
    val frozenDays: List<String> = emptyList(),
    val lostStreak: Int = 0,
    val achievements: Map<String, Long> = emptyMap(),
    val mistakes: List<String> = emptyList(),
    val mistakesFixed: Int = 0,
    val codeRuns: Int = 0,
    val perfectLessons: Int = 0,
    val maxCombo: Int = 0,
    val goalDays: List<String> = emptyList(),
    val challengeDays: List<String> = emptyList(),
    val boostUntil: Long = 0,
    val ownedItems: List<String> = emptyList(),
    val equipped: String? = null,
    val purchases: Int = 0,
    val practiceSessions: Int = 0,
    val flags: List<String> = emptyList(),
    val playgroundCode: String = DEFAULT_PLAYGROUND_CODE,
    val snippets: List<Snippet> = emptyList(),
    // ---- ab Version 2.0 (Hissbit)
    /** Aktiver Kurs: "python" oder "javascript". */
    val course: String = "python",
    /** Startpunkt pro Kurs (außer Python, das nutzt [startUnit]). */
    val startUnits: Map<String, Int> = emptyMap(),
    val playgroundCodeJs: String = DEFAULT_PLAYGROUND_JS,
    /** Sprache im Spielplatz: "python" oder "javascript". */
    val playgroundLanguage: String = "python",
    /** Tage, an denen eine Challenge gelöst wurde – pro Kurs ("javascript:2026-10-19"). */
    val courseChallengeDays: List<String> = emptyList(),
)

@Serializable
enum class ThemeMode(val label: Txt) {
    SYSTEM(Txt("System", "System")),
    LIGHT(Txt("Hell", "Light")),
    DARK(Txt("Dunkel", "Dark")),
    AMOLED(Txt("AMOLED", "AMOLED")),
}

@Serializable
enum class Accent(val label: Txt) {
    HISSBIT(Txt("Hissbit-Grün", "Hissbit green")),
    PYTHON(Txt("Python", "Python")),
    GREEN(Txt("Dschungel", "Jungle")),
    OCEAN(Txt("Ozean", "Ocean")),
    VIOLET(Txt("Violett", "Violet")),
    SUNSET(Txt("Sonnenuntergang", "Sunset")),
    CHERRY(Txt("Kirschblüte", "Cherry blossom")),
    MINT(Txt("Lagune", "Lagoon")),
    INDIGO(Txt("Mitternacht", "Midnight")),
    DYNAMIC(Txt("Material You", "Material You")),
}

@Serializable
enum class UiFont(val label: Txt) {
    /** Seit 2.3 der Standard: Überschriften in einer Serifenschrift, Text in einer ruhigen Grotesk. */
    LUXE(Txt("Elegant", "Elegant")),
    NUNITO(Txt("Nunito", "Nunito")),
    LEXEND(Txt("Lexend", "Lexend")),
    SYSTEM(Txt("System", "System")),
}

@Serializable
enum class CodeFont(val label: Txt) {
    JETBRAINS(Txt("JetBrains Mono", "JetBrains Mono")),
    FIRA(Txt("Fira Code (Ligaturen)", "Fira Code (ligatures)")),
    SOURCE(Txt("Source Code Pro", "Source Code Pro")),
    SYSTEM(Txt("System Mono", "System mono")),
}

@Serializable
enum class CornerStyle(val label: Txt) {
    ROUND(Txt("Rund", "Round")),
    SOFT(Txt("Weich", "Soft")),
    SHARP(Txt("Eckig", "Sharp")),
}

@Serializable
enum class PathStyle(val label: Txt) {
    WINDING(Txt("Kurvig", "Winding")),
    STRAIGHT(Txt("Gerade", "Straight")),
}

@Serializable
enum class AnimationLevel(val label: Txt) {
    FULL(Txt("Voll", "Full")),
    REDUCED(Txt("Reduziert", "Reduced")),
    OFF(Txt("Aus", "Off")),
}

/** Dezenter, animierter Hintergrund hinter den Hauptseiten (ab Version 2.3). Namen nie ändern – sie stehen in den gespeicherten Einstellungen. */
@Serializable
enum class AmbientBackground(val label: Txt) {
    NONE(Txt("Aus", "Off")),
    BLOSSOM(Txt("Kirschblüten", "Cherry blossoms")),
    SNOW(Txt("Schnee", "Snowfall")),
    LEAVES(Txt("Herbstlaub", "Autumn leaves")),
    FIREFLIES(Txt("Glühwürmchen", "Fireflies")),
}

@Serializable
enum class MascotColor(val label: Txt) {
    BLUE(Txt("Python-Blau", "Python blue")),
    GREEN(Txt("Grün", "Green")),
    YELLOW(Txt("Gelb", "Yellow")),
    PURPLE(Txt("Lila", "Purple")),
    PINK(Txt("Pink", "Pink")),
    RED(Txt("Rot", "Red")),
    TEAL(Txt("Hissbit-Grün", "Hissbit green")),
}

@Serializable
enum class EditorThemeId(val label: Txt) {
    AUTO(Txt("Automatisch", "Automatic")),
    MONTY_LIGHT(Txt("Monty Hell", "Monty Light")),
    MONTY_DARK(Txt("Monty Dunkel", "Monty Dark")),
    MONOKAI(Txt("Monokai", "Monokai")),
    DRACULA(Txt("Dracula", "Dracula")),
    ONE_DARK(Txt("One Dark", "One Dark")),
    NORD(Txt("Nord", "Nord")),
    GRUVBOX(Txt("Gruvbox", "Gruvbox")),
    SOLARIZED(Txt("Solarized", "Solarized")),
    GITHUB(Txt("GitHub Hell", "GitHub Light")),
}

/** Aktuelle Version des Einstellungs-Formats (siehe [migrated]). */
const val SETTINGS_SCHEMA = 3

@Serializable
data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val accent: Accent = Accent.HISSBIT,
    val uiFont: UiFont = UiFont.LUXE,
    val textScale: Float = 1f,
    val corners: CornerStyle = CornerStyle.ROUND,
    val pathStyle: PathStyle = PathStyle.WINDING,
    val showMascot: Boolean = true,
    val mascotColor: MascotColor = MascotColor.TEAL,
    val editorTheme: EditorThemeId = EditorThemeId.AUTO,
    val codeFont: CodeFont = CodeFont.JETBRAINS,
    val codeFontSize: Int = 14,
    val lineNumbers: Boolean = true,
    val autoIndent: Boolean = true,
    val animations: AnimationLevel = AnimationLevel.FULL,
    val sounds: Boolean = true,
    val haptics: Boolean = true,
    val confetti: Boolean = true,
    val dailyGoal: Int = 20,
    val heartsEnabled: Boolean = true,
    val showTheory: Boolean = true,
    val reminderEnabled: Boolean = false,
    val reminderHour: Int = 18,
    val reminderMinute: Int = 0,
    // ---- ab Version 2.0 (Hissbit)
    val language: LanguagePref = LanguagePref.SYSTEM,
    // ---- ab Version 2.3
    /** Animierter Hintergrund; fehlt in alten Daten (= aus). */
    val ambient: AmbientBackground = AmbientBackground.NONE,
    /** Format-Version für einmalige Umstellungen; fehlt in alten Daten (= 0). */
    val schema: Int = 0,
)

/**
 * Einmalige Umstellungen alter Einstellungen. Wer noch die Standard-Optik von PyQuest 1.0
 * hatte, bekommt den neuen Hissbit-Look – bewusst gewählte Einstellungen bleiben unverändert.
 */
fun AppSettings.migrated(): AppSettings {
    var s = this
    if (s.schema < 1) {
        s = s.copy(
            accent = if (s.accent == Accent.PYTHON) Accent.HISSBIT else s.accent,
            uiFont = if (s.uiFont == UiFont.NUNITO) UiFont.LEXEND else s.uiFont,
            mascotColor = if (s.mascotColor == MascotColor.BLUE) MascotColor.TEAL else s.mascotColor,
            schema = 1,
        )
    }
    if (s.schema < 2) {
        // Version 2.2: schlichteres Design – Lexend war bis dahin nur der Standard, zurück zu Nunito.
        s = s.copy(
            uiFont = if (s.uiFont == UiFont.LEXEND) UiFont.NUNITO else s.uiFont,
            schema = 2,
        )
    }
    if (s.schema < 3) {
        // Version 2.3: edlerer Look – wer noch die Standardschrift Nunito hatte, bekommt "Elegant"; Lexend und System bleiben.
        s = s.copy(
            uiFont = if (s.uiFont == UiFont.NUNITO) UiFont.LUXE else s.uiFont,
            schema = 3,
        )
    }
    return s
}
