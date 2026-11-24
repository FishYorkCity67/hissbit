package app.pyquest.i18n

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.serialization.Serializable
import java.util.Locale

/** Sprache der Oberfläche und der Kursinhalte. */
enum class AppLanguage(val code: String, val locale: Locale) {
    DE("de", Locale.GERMAN),
    EN("en", Locale.ENGLISH);

    fun pick(de: String, en: String): String = if (this == EN) en else de

    companion object {
        fun system(): AppLanguage = if (Locale.getDefault().language == "de") DE else EN
    }
}

/** Was in den Einstellungen gewählt ist – "System" folgt der Gerätesprache. */
@Serializable
enum class LanguagePref {
    SYSTEM, DE, EN;

    fun resolve(): AppLanguage = when (this) {
        SYSTEM -> AppLanguage.system()
        DE -> AppLanguage.DE
        EN -> AppLanguage.EN
    }
}

/** Ein Text in allen App-Sprachen – der Compiler sorgt dafür, dass keine Übersetzung fehlt. */
@Immutable
data class Txt(val de: String, val en: String) {
    fun of(lang: AppLanguage): String = lang.pick(de, en)
}

val LocalLanguage = staticCompositionLocalOf { AppLanguage.DE }

/** Text in der aktuellen Sprache: `tr("Weiter", "Continue")`. */
@Composable
@ReadOnlyComposable
fun tr(de: String, en: String): String = LocalLanguage.current.pick(de, en)

@Composable
@ReadOnlyComposable
fun Txt.t(): String = of(LocalLanguage.current)

/** Kurzform für Mengenangaben: `plural(n, "Tag", "Tage", "day", "days")`. */
@Composable
@ReadOnlyComposable
fun plural(n: Int, deOne: String, deMany: String, enOne: String, enMany: String): String =
    "$n " + if (n == 1) tr(deOne, enOne) else tr(deMany, enMany)
