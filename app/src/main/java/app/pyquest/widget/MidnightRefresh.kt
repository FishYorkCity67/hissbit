package app.pyquest.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import app.pyquest.notify.Reminders
import app.pyquest.pyApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

/**
 * Zeichnet die Widgets kurz nach Mitternacht neu – sonst stünde dort bis zur nächsten
 * stündlichen Aktualisierung noch "✓ geschafft" vom Vortag.
 */
object MidnightRefresh {
    private const val WORK_NAME = "midnight_refresh"

    fun schedule(context: Context, replace: Boolean) {
        val now = LocalDateTime.now()
        val next = now.toLocalDate().plusDays(1).atStartOfDay().plusMinutes(1)
        val request = OneTimeWorkRequestBuilder<MidnightWorker>()
            .setInitialDelay(Duration.between(now, next).toMillis(), TimeUnit.MILLISECONDS)
            .build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            WORK_NAME,
            if (replace) ExistingWorkPolicy.REPLACE else ExistingWorkPolicy.KEEP,
            request,
        )
    }
}

class MidnightWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        WidgetUpdater.updateNow(applicationContext)
        MidnightRefresh.schedule(applicationContext, replace = true)
        return Result.success()
    }
}

/** Uhrzeit oder Zeitzone geändert: Widgets, Mitternachts-Job und Erinnerung neu ausrichten. */
class TimeChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val app = context.pyApp
        val pending = goAsync()
        CoroutineScope(SupervisorJob() + Dispatchers.Default).launch {
            try {
                MidnightRefresh.schedule(app, replace = true)
                Reminders.schedule(app, app.repository.currentSettings(), replace = true)
                WidgetUpdater.updateNow(app)
            } finally {
                pending.finish()
            }
        }
    }
}
