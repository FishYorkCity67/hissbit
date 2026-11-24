package app.pyquest.data

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Gespeicherte Einstellungen älterer Versionen müssen weiter lesbar bleiben – sonst gingen Optik
 * und Wahl des Nutzers beim Update verloren. (Dieselbe Json-Konfiguration wie im Repository.)
 */
class SettingsCompatTest {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        coerceInputValues = true
    }

    @Test
    fun oldSettingsWithoutAmbientStillLoad() {
        val old = """{"themeMode":"DARK","accent":"CHERRY","uiFont":"NUNITO","dailyGoal":30,"schema":2}"""
        val s = json.decodeFromString<AppSettings>(old).migrated()
        assertEquals(ThemeMode.DARK, s.themeMode)
        assertEquals(Accent.CHERRY, s.accent)
        assertEquals(30, s.dailyGoal)
        assertEquals(AmbientBackground.NONE, s.ambient)
    }

    @Test
    fun ambientRoundTrips() {
        for (kind in AmbientBackground.entries) {
            val back = json.decodeFromString<AppSettings>(json.encodeToString(AppSettings(ambient = kind)))
            assertEquals(kind, back.ambient)
        }
    }

    @Test
    fun migrationKeepsTheChosenBackground() {
        val s = AppSettings(ambient = AmbientBackground.BLOSSOM, schema = 0).migrated()
        assertEquals(AmbientBackground.BLOSSOM, s.ambient)
    }

    @Test
    fun unknownAmbientFallsBackToOff() {
        // z. B. nach dem Einspielen einer Sicherung aus einer späteren Version
        val s = json.decodeFromString<AppSettings>("""{"ambient":"AURORA"}""")
        assertEquals(AmbientBackground.NONE, s.ambient)
    }

    @Test
    fun migrationMovesTheOldDefaultFontToElegant() {
        val s = AppSettings(uiFont = UiFont.NUNITO, schema = 2).migrated()
        assertEquals(UiFont.LUXE, s.uiFont)
        assertEquals(SETTINGS_SCHEMA, s.schema)
    }

    @Test
    fun migrationKeepsDeliberateFontChoices() {
        assertEquals(UiFont.LEXEND, AppSettings(uiFont = UiFont.LEXEND, schema = 2).migrated().uiFont)
        assertEquals(UiFont.SYSTEM, AppSettings(uiFont = UiFont.SYSTEM, schema = 2).migrated().uiFont)
        // schon auf "Elegant" gestellt: bleibt
        assertEquals(UiFont.LUXE, AppSettings(uiFont = UiFont.LUXE, schema = 3).migrated().uiFont)
    }

    @Test
    fun newInstallStartsElegantWithTheBackgroundOff() {
        val s = AppSettings()
        assertEquals(UiFont.LUXE, s.uiFont)
        assertEquals(AmbientBackground.NONE, s.ambient)
    }

    @Test
    fun fontNamesStayStable() {
        // Diese Namen stehen in gespeicherten Einstellungen und Sicherungsdateien – nie ändern.
        assertEquals(setOf("LUXE", "NUNITO", "LEXEND", "SYSTEM"), UiFont.entries.map { it.name }.toSet())
    }

    @Test
    fun enumNamesStayStable() {
        // Diese Namen stehen in gespeicherten Einstellungen und Sicherungsdateien – nie ändern.
        assertEquals(listOf("NONE", "BLOSSOM", "SNOW", "LEAVES", "FIREFLIES"), AmbientBackground.entries.map { it.name })
    }
}
