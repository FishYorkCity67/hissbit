package app.pyquest.widget

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Aktualisiert alle Homescreen-Widgets (gebündelt, damit schnelle Änderungen nicht ruckeln). */
object WidgetUpdater {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var pending: Job? = null

    fun requestUpdate(context: Context) {
        val app = context.applicationContext
        pending?.cancel()
        pending = scope.launch {
            delay(600)
            updateNow(app)
        }
    }

    suspend fun updateNow(context: Context) {
        runCatching { updateAllWidgets(context.applicationContext) }
    }
}
