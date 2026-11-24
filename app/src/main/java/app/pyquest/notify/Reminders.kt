package app.pyquest.notify

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import app.pyquest.MainActivity
import app.pyquest.R
import app.pyquest.data.AppSettings
import app.pyquest.data.CourseId
import app.pyquest.data.currentStreak
import app.pyquest.data.streakDoneToday
import app.pyquest.i18n.AppLanguage
import app.pyquest.pyApp
import app.pyquest.widget.WidgetUpdater
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

object Reminders {
    const val CHANNEL_ID = "daily_reminder"
    private const val WORK_NAME = "daily_reminder"
    private const val NOTIFICATION_ID = 4711

    /** Legt den Benachrichtigungskanal an (bzw. benennt ihn in der aktuellen Sprache um). */
    fun createChannel(context: Context, lang: AppLanguage = AppLanguage.system()) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            lang.pick("Tägliche Erinnerung", "Daily reminder"),
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = lang.pick("Erinnert dich daran, deinen Streak zu halten.", "Reminds you to keep your streak.")
        }
        context.getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
    }

    fun hasPermission(context: Context): Boolean =
        Build.VERSION.SDK_INT < 33 ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

    /** Plant die nächste Erinnerung (WorkManager plant sich danach selbst neu ein). */
    fun schedule(context: Context, settings: AppSettings, replace: Boolean) {
        val wm = WorkManager.getInstance(context)
        createChannel(context, settings.language.resolve())
        if (!settings.reminderEnabled) {
            wm.cancelUniqueWork(WORK_NAME)
            return
        }
        val now = LocalDateTime.now()
        var next = now.withHour(settings.reminderHour).withMinute(settings.reminderMinute).withSecond(0).withNano(0)
        if (!next.isAfter(now.plusSeconds(30))) next = next.plusDays(1)
        val delay = Duration.between(now, next).toMillis()
        val request = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()
        wm.enqueueUniqueWork(WORK_NAME, if (replace) ExistingWorkPolicy.REPLACE else ExistingWorkPolicy.KEEP, request)
    }

    fun show(context: Context, streak: Int, lang: AppLanguage, course: CourseId) {
        if (!hasPermission(context)) return
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(MainActivity.EXTRA_OPEN, MainActivity.OPEN_LESSON)
        }
        val pending = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val name = course.displayName
        val (title, text) = if (lang == AppLanguage.EN) {
            if (streak > 0) {
                listOf(
                    "🔥 Your $streak-day streak is in danger!" to "One short lesson is enough to save it.",
                    "Monty is waiting for you 🐍" to "Keep your $streak-day streak alive – just 5 minutes!",
                    "Don't give up! 🔥 $streak days" to "Your $name lesson for today is still waiting.",
                ).random()
            } else {
                listOf(
                    "Time to code! 🐍" to "Start a new streak today – Monty is looking forward to it.",
                    "5 minutes for your brain 🧠" to "One lesson a day, bit by bit.",
                    "Ready for some code? 💻" to "Your next lesson is waiting.",
                ).random()
            }
        } else {
            if (streak > 0) {
                listOf(
                    "🔥 Dein $streak-Tage-Streak ist in Gefahr!" to "Eine kurze Lektion reicht, um ihn zu retten.",
                    "Monty wartet auf dich 🐍" to "Halte deinen $streak-Tage-Streak am Leben – nur 5 Minuten!",
                    "Nicht aufgeben! 🔥 $streak Tage" to "Heute fehlt noch deine $name-Lektion.",
                ).random()
            } else {
                listOf(
                    "Zeit zum Coden! 🐍" to "Starte heute einen neuen Streak – Monty freut sich auf dich.",
                    "5 Minuten für dein Gehirn 🧠" to "Eine Lektion am Tag – Bit für Bit.",
                    "Bereit für neuen Code? 💻" to "Deine nächste Lektion wartet schon.",
                ).random()
            }
        }
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_flame)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setColor(0xFF2CE2AA.toInt())
            .setContentIntent(pending)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
        } catch (_: SecurityException) {
        }
    }
}

class ReminderWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val app = applicationContext.pyApp
        val settings = app.repository.currentSettings()
        val progress = app.repository.currentProgress()
        if (settings.reminderEnabled && progress.onboarded && !progress.streakDoneToday()) {
            Reminders.show(applicationContext, progress.currentStreak(), settings.language.resolve(), CourseId.byKey(progress.course))
        }
        WidgetUpdater.updateNow(applicationContext)
        Reminders.schedule(applicationContext, settings, replace = true)
        return Result.success()
    }
}
