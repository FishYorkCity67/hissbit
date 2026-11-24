package app.pyquest.python

import kotlinx.coroutines.flow.StateFlow

/** Gemeinsame Schnittstelle für die Ausführung von Lernenden-Code (Python, JavaScript). */
interface CodeRunner {
    /** Version der Laufzeit (z.B. "3.14.8"), null solange sie startet. */
    val version: StateFlow<String?>

    /** Name der Sprache für Anzeigen ("Python", "JavaScript"). */
    val languageName: String

    /**
     * Führt [code] aus. Ohne [input] läuft das Programm mit festen Eingaben ([stdin]) und
     * Zeitlimit; mit [input] interaktiv (Spielplatz) ohne Zeitlimit.
     */
    suspend fun run(
        code: String,
        stdin: List<String> = emptyList(),
        timeoutSeconds: Double = 6.0,
        input: InputChannel? = null,
        onOutput: ((ConsoleChunk) -> Unit)? = null,
    ): RunResult

    /** Bricht das laufende Programm ab. */
    fun cancel()

    /** Startet die Laufzeit vorab im Hintergrund. */
    fun warmUp() {}
}
