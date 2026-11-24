package app.pyquest.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// Der Dateiname bleibt "pyquest": Hier liegt der gesamte bisherige Fortschritt.
private val Context.pyDataStore: DataStore<Preferences> by preferencesDataStore(name = "pyquest")

/** Inhalt einer Sicherungsdatei ("Fortschritt sichern"). */
@Serializable
data class BackupFile(
    val app: String = BACKUP_APP_ID,
    val format: Int = 1,
    val exportedAt: Long = 0,
    val appVersion: String = "",
    val progress: UserProgress,
    val settings: AppSettings,
)

const val BACKUP_APP_ID = "hissbit"

sealed interface ImportResult {
    data class Ok(val backup: BackupFile) : ImportResult
    data object NotABackup : ImportResult
}

/** Speichert Fortschritt und Einstellungen als JSON in DataStore. */
class Repository(context: Context, scope: CoroutineScope, private val onProgressChanged: () -> Unit) {
    private val store = context.applicationContext.pyDataStore
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        coerceInputValues = true
    }
    private val prettyJson = Json(json) { prettyPrint = true }
    private val keyProgress = stringPreferencesKey("progress")
    private val keySettings = stringPreferencesKey("settings")

    private val progressFlow: Flow<UserProgress> = store.data
        .map { decodeProgress(it) }
        .distinctUntilChanged()

    private val settingsFlow: Flow<AppSettings> = store.data
        .map { decodeSettings(it) }
        .distinctUntilChanged()

    /** null bis die Daten zum ersten Mal geladen sind. */
    val progress: StateFlow<UserProgress?> = progressFlow.stateIn(scope, SharingStarted.Eagerly, null)
    val settings: StateFlow<AppSettings?> = settingsFlow.stateIn(scope, SharingStarted.Eagerly, null)

    suspend fun currentProgress(): UserProgress = progressFlow.first()
    suspend fun currentSettings(): AppSettings = settingsFlow.first()

    private fun decodeProgress(prefs: Preferences): UserProgress =
        prefs[keyProgress]?.let { runCatching { json.decodeFromString<UserProgress>(it) }.getOrNull() } ?: UserProgress()

    private fun decodeSettings(prefs: Preferences): AppSettings =
        (prefs[keySettings]?.let { runCatching { json.decodeFromString<AppSettings>(it) }.getOrNull() } ?: AppSettings()).migrated()

    /** Atomare Änderung am Fortschritt; liefert den neuen Stand. */
    suspend fun updateProgress(transform: (UserProgress) -> UserProgress): UserProgress {
        var result: UserProgress? = null
        store.edit { prefs ->
            val next = transform(decodeProgress(prefs))
            prefs[keyProgress] = json.encodeToString(UserProgress.serializer(), next)
            result = next
        }
        onProgressChanged()
        return result ?: currentProgress()
    }

    /** Wie [updateProgress], aber mit zusätzlichem Rückgabewert aus der Transformation. */
    suspend fun <T> updateProgressWith(transform: (UserProgress) -> Pair<UserProgress, T>): T {
        var out: T? = null
        var set = false
        store.edit { prefs ->
            val (next, value) = transform(decodeProgress(prefs))
            prefs[keyProgress] = json.encodeToString(UserProgress.serializer(), next)
            out = value
            set = true
        }
        onProgressChanged()
        check(set)
        @Suppress("UNCHECKED_CAST")
        return out as T
    }

    suspend fun updateSettings(transform: (AppSettings) -> AppSettings): AppSettings {
        var result: AppSettings? = null
        store.edit { prefs ->
            val next = transform(decodeSettings(prefs))
            prefs[keySettings] = json.encodeToString(AppSettings.serializer(), next)
            result = next
        }
        onProgressChanged()
        return result ?: currentSettings()
    }

    suspend fun resetProgress(keepSettings: Boolean = true) {
        store.edit { prefs ->
            prefs.remove(keyProgress)
            if (!keepSettings) prefs.remove(keySettings)
        }
        onProgressChanged()
    }

    // ------------------------------------------------------------ Sicherung als Datei

    /** Kompletter Stand als lesbare JSON-Datei – übersteht auch eine Neuinstallation. */
    suspend fun exportBackup(appVersion: String): String = prettyJson.encodeToString(
        BackupFile.serializer(),
        BackupFile(
            exportedAt = System.currentTimeMillis(),
            appVersion = appVersion,
            progress = currentProgress(),
            settings = currentSettings(),
        ),
    )

    /** Prüft eine Sicherungsdatei, ohne etwas zu verändern. */
    fun parseBackup(text: String): ImportResult {
        val backup = runCatching { json.decodeFromString(BackupFile.serializer(), text) }.getOrNull()
            ?: return ImportResult.NotABackup
        if (backup.app != BACKUP_APP_ID) return ImportResult.NotABackup
        return ImportResult.Ok(backup)
    }

    /** Ersetzt den aktuellen Stand durch die Sicherung. */
    suspend fun restoreBackup(backup: BackupFile) {
        store.edit { prefs ->
            prefs[keyProgress] = json.encodeToString(UserProgress.serializer(), backup.progress.copy(onboarded = true))
            prefs[keySettings] = json.encodeToString(AppSettings.serializer(), backup.settings.migrated())
        }
        onProgressChanged()
    }
}
