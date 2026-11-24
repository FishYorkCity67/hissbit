package app.pyquest.ui.playground

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import app.pyquest.PyQuestApp
import app.pyquest.data.CourseId
import app.pyquest.i18n.AppLanguage
import app.pyquest.python.CodeRunner
import app.pyquest.python.ConsoleChunk
import app.pyquest.python.InputChannel
import app.pyquest.python.RunResult
import app.pyquest.ui.code.EditHistory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlaygroundViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as PyQuestApp

    /** Sprache, in der gerade programmiert wird. */
    var language by mutableStateOf(CourseId.PYTHON)
        private set
    val runner: CodeRunner get() = app.runnerFor(language)

    var code by mutableStateOf(TextFieldValue(""))
    val history = EditHistory()
    val input = InputChannel()

    private val _console = MutableStateFlow<List<ConsoleChunk>>(emptyList())
    val console: StateFlow<List<ConsoleChunk>> = _console.asStateFlow()

    var running by mutableStateOf(false)
        private set
    var lastResult by mutableStateOf<RunResult?>(null)
        private set
    var errorLine by mutableStateOf<Int?>(null)
        private set
    private var loaded = false

    fun load(initial: String, lang: CourseId) {
        if (loaded) return
        loaded = true
        language = lang
        code = TextFieldValue(initial, TextRange(initial.length))
    }

    fun replaceCode(text: String) {
        history.record(code, TextFieldValue(text))
        code = TextFieldValue(text, TextRange(text.length))
        errorLine = null
    }

    /** Wechselt die Sprache und lädt den dort zuletzt geschriebenen Code. */
    fun switchLanguage(to: CourseId, codeOfTarget: String) {
        if (to == language) return
        if (running) stop()
        language = to
        history.clear()
        code = TextFieldValue(codeOfTarget, TextRange(codeOfTarget.length))
        errorLine = null
        clearConsole()
    }

    private fun append(chunk: ConsoleChunk) {
        _console.update { list ->
            val last = list.lastOrNull()
            if (last != null && last.kind == chunk.kind && chunk.kind != 2) list.dropLast(1) + ConsoleChunk(last.text + chunk.text, last.kind)
            else list + chunk
        }
    }

    fun clearConsole() {
        _console.value = emptyList()
        lastResult = null
    }

    fun run(lang: AppLanguage, onStarted: () -> Unit) {
        if (running) return
        running = true
        errorLine = null
        lastResult = null
        _console.value = emptyList()
        onStarted()
        val activeRunner = runner
        viewModelScope.launch {
            val result = activeRunner.run(code.text, input = input, onOutput = ::append)
            when {
                result.cancelled -> append(ConsoleChunk(lang.pick("\n■ Programm gestoppt", "\n■ Program stopped"), 3))
                result.error != null -> append(ConsoleChunk((if (_console.value.isEmpty()) "" else "\n") + result.error, 1))
                else -> append(ConsoleChunk(lang.pick("\n✓ Fertig in ${result.durationMs} ms", "\n✓ Done in ${result.durationMs} ms"), 3))
            }
            errorLine = result.errorLine
            lastResult = result
            running = false
        }
    }

    fun stop() {
        runner.cancel()
    }

    fun submitInput(line: String) {
        append(ConsoleChunk(line + "\n", 2))
        input.submit(line)
    }

    override fun onCleared() {
        runner.cancel()
        super.onCleared()
    }
}
