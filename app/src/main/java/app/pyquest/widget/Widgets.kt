package app.pyquest.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.LinearProgressIndicator
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontFamily
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import app.pyquest.MainActivity
import app.pyquest.R
import app.pyquest.content.Catalog
import app.pyquest.data.CourseId
import app.pyquest.data.PythonTip
import app.pyquest.i18n.AppLanguage
import androidx.core.net.toUri
import app.pyquest.data.currentLesson
import app.pyquest.data.currentStreak
import app.pyquest.data.isActiveDay
import app.pyquest.data.isFrozenDay
import app.pyquest.data.streakDoneToday
import app.pyquest.data.xpOn
import app.pyquest.pyApp
import java.time.LocalDate
import androidx.glance.unit.ColorProvider as FixedColor

/** Momentaufnahme der Daten, die die Widgets anzeigen. */
data class WidgetData(
    val streak: Int,
    val doneToday: Boolean,
    val xpToday: Int,
    val goal: Int,
    val week: List<Int>,
    val nextLesson: String?,
    val onboarded: Boolean,
    val lang: AppLanguage,
    val course: CourseId,
)

private suspend fun loadWidgetData(context: Context): WidgetData {
    val app = context.pyApp
    val p = app.repository.currentProgress()
    val s = app.repository.currentSettings()
    val today = LocalDate.now()
    val monday = today.minusDays((today.dayOfWeek.value - 1).toLong())
    val week = (0 until 7).map { i ->
        val d = monday.plusDays(i.toLong())
        when {
            p.isActiveDay(d) -> 2
            p.isFrozenDay(d) -> 1
            d.isAfter(today) -> -1
            else -> 0
        }
    }
    return WidgetData(
        streak = p.currentStreak(today),
        doneToday = p.streakDoneToday(today),
        xpToday = p.xpOn(today),
        goal = s.dailyGoal,
        week = week,
        nextLesson = Catalog.course(CourseId.byKey(p.course), s.language.resolve()).currentLesson(p)?.title,
        onboarded = p.onboarded,
        lang = s.language.resolve(),
        course = CourseId.byKey(p.course),
    )
}

/** Alle Widgets neu zeichnen. */
suspend fun updateAllWidgets(context: Context) {
    StreakWidget().updateAll(context)
    DailyGoalWidget().updateAll(context)
    TipWidget().updateAll(context)
}

private fun openIntent(context: Context, target: String): Intent =
    Intent(context, MainActivity::class.java).apply {
        action = Intent.ACTION_VIEW
        data = "hissbit://open/$target".toUri()
        putExtra(MainActivity.EXTRA_OPEN, target)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
    }

private val White = FixedColor(Color.White)
private val WhiteSoft = FixedColor(Color(0xDDFFFFFF))
private val TextMain = ColorProvider(day = Color(0xFF1D1B19), night = Color(0xFFF2EEE8))
private val TextMuted = ColorProvider(day = Color(0xFF6E6860), night = Color(0xFFA59F96))
private val Orange = FixedColor(Color(0xFFE27B3A))
private val Gold = ColorProvider(day = Color(0xFFC39A3C), night = Color(0xFFD9B055))

// ---------------------------------------------------------------- Streak

class StreakWidget : GlanceAppWidget() {
    override val sizeMode: SizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val data = loadWidgetData(context)
        val intent = openIntent(context, MainActivity.OPEN_LESSON)
        provideContent { StreakContent(data, intent) }
    }
}

@Composable
private fun StreakContent(data: WidgetData, intent: Intent) {
    val size = LocalSize.current
    val wide = size.width >= 180.dp
    val compact = size.height < 100.dp
    Box(
        GlanceModifier
            .fillMaxSize()
            .background(ImageProvider(if (data.doneToday) R.drawable.widget_bg_streak else R.drawable.widget_bg_streak_off))
            .clickable(actionStartActivity(intent))
            .padding(12.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (wide) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    ImageProvider(R.drawable.widget_flame),
                    contentDescription = null,
                    colorFilter = if (data.doneToday) null else ColorFilter.tint(WhiteSoft),
                    modifier = GlanceModifier.size(if (compact) 40.dp else 56.dp),
                )
                Spacer(GlanceModifier.width(12.dp))
                Column {
                    Text(
                        "${data.streak}",
                        style = TextStyle(color = White, fontSize = if (compact) 28.sp else 38.sp, fontWeight = FontWeight.Bold),
                    )
                    Text(data.lang.pick(if (data.streak == 1) "Tag Streak" else "Tage Streak", "day streak"), style = TextStyle(color = White, fontSize = 13.sp, fontWeight = FontWeight.Medium))
                    if (!compact) {
                        Text(
                            if (data.doneToday) data.lang.pick("✓ Heute geschafft", "✓ Done today") else data.lang.pick("Heute noch lernen!", "Learn today!"),
                            style = TextStyle(color = WhiteSoft, fontSize = 12.sp),
                        )
                    }
                }
            }
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    ImageProvider(R.drawable.widget_flame),
                    contentDescription = null,
                    colorFilter = if (data.doneToday) null else ColorFilter.tint(WhiteSoft),
                    modifier = GlanceModifier.size(if (compact) 28.dp else 44.dp),
                )
                Text(
                    "${data.streak}",
                    style = TextStyle(color = White, fontSize = if (compact) 22.sp else 30.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center),
                )
                if (!compact) {
                    Text(
                        if (data.doneToday) data.lang.pick("✓ geschafft", "✓ done") else data.lang.pick("Lernen!", "Learn!"),
                        style = TextStyle(color = WhiteSoft, fontSize = 12.sp, textAlign = TextAlign.Center),
                    )
                }
            }
        }
    }
}

class StreakWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = StreakWidget()
}

// ---------------------------------------------------------------- Tagesziel

class DailyGoalWidget : GlanceAppWidget() {
    override val sizeMode: SizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val data = loadWidgetData(context)
        val intent = openIntent(context, MainActivity.OPEN_LESSON)
        provideContent { GoalContent(data, intent) }
    }
}

@Composable
private fun GoalContent(data: WidgetData, intent: Intent) {
    val size = LocalSize.current
    val reached = data.xpToday >= data.goal
    val showWeek = size.height >= 130.dp
    Column(
        GlanceModifier
            .fillMaxSize()
            .background(ImageProvider(R.drawable.widget_bg_card))
            .clickable(actionStartActivity(intent))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(GlanceModifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Image(ImageProvider(R.drawable.widget_flame), null, GlanceModifier.size(22.dp), colorFilter = if (data.doneToday) null else ColorFilter.tint(TextMuted))
            Spacer(GlanceModifier.width(4.dp))
            Text("${data.streak}", style = TextStyle(color = if (data.doneToday) Orange else TextMuted, fontSize = 16.sp, fontWeight = FontWeight.Bold))
            Spacer(GlanceModifier.defaultWeight())
            Text(
                if (reached) data.lang.pick("Ziel erreicht", "Goal reached") else data.lang.pick("Tagesziel", "Daily goal"),
                style = TextStyle(color = if (reached) Gold else TextMain, fontSize = 14.sp, fontWeight = FontWeight.Bold),
            )
        }
        Spacer(GlanceModifier.height(8.dp))
        Row(GlanceModifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
            Text("${data.xpToday}", style = TextStyle(color = TextMain, fontSize = 26.sp, fontWeight = FontWeight.Bold))
            Text(" / ${data.goal} XP", style = TextStyle(color = TextMuted, fontSize = 14.sp))
        }
        Spacer(GlanceModifier.height(6.dp))
        LinearProgressIndicator(
            progress = (data.xpToday.toFloat() / data.goal).coerceIn(0f, 1f),
            modifier = GlanceModifier.fillMaxWidth().height(10.dp),
            color = Gold,
            backgroundColor = ColorProvider(day = Color(0xFFE9E4DC), night = Color(0xFF34312D)),
        )
        if (showWeek) {
            Spacer(GlanceModifier.height(10.dp))
            Row(GlanceModifier.fillMaxWidth()) {
                val labels = if (data.lang == AppLanguage.EN) listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su") else listOf("Mo", "Di", "Mi", "Do", "Fr", "Sa", "So")
                data.week.forEachIndexed { i, state ->
                    Column(GlanceModifier.defaultWeight(), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(labels[i], style = TextStyle(color = TextMuted, fontSize = 10.sp, textAlign = TextAlign.Center))
                        Image(
                            ImageProvider(
                                when (state) {
                                    2 -> R.drawable.widget_day_done
                                    1 -> R.drawable.widget_day_frozen
                                    else -> R.drawable.widget_day_empty
                                }
                            ),
                            contentDescription = null,
                            modifier = GlanceModifier.size(18.dp),
                        )
                    }
                }
            }
        }
        if (size.height >= 170.dp && data.nextLesson != null) {
            Spacer(GlanceModifier.height(8.dp))
            Text(
                data.lang.pick("Weiter mit: ${data.nextLesson} →", "Next up: ${data.nextLesson} →"),
                style = TextStyle(color = TextMuted, fontSize = 12.sp),
                maxLines = 1,
            )
        }
    }
}

class DailyGoalWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = DailyGoalWidget()
}

// ---------------------------------------------------------------- Tipp des Tages

private val TipOffsetKey = intPreferencesKey("tip_offset")

class TipWidget : GlanceAppWidget() {
    override val sizeMode: SizeMode = SizeMode.Exact
    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val intent = openIntent(context, MainActivity.OPEN_KNOWLEDGE)
        val app = context.pyApp
        val lang = app.repository.currentSettings().language.resolve()
        val course = CourseId.byKey(app.repository.currentProgress().course)
        val tips = Catalog.bundle(course, lang).tips
        provideContent {
            val prefs = currentState<Preferences>()
            val offset = prefs[TipOffsetKey] ?: 0
            TipContent(offset, intent, tips, lang, course)
        }
    }
}

@Composable
private fun TipContent(offset: Int, intent: Intent, tips: List<PythonTip>, lang: AppLanguage, course: CourseId) {
    if (tips.isEmpty()) return
    val index = Math.floorMod(LocalDate.now().toEpochDay() + offset, tips.size.toLong()).toInt()
    val tip = tips[index]
    val size = LocalSize.current
    Column(
        GlanceModifier
            .fillMaxSize()
            .background(ImageProvider(R.drawable.widget_bg_code))
            .padding(horizontal = 14.dp, vertical = 12.dp),
    ) {
        Row(GlanceModifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(lang.pick("${course.displayName}-Tipp", "${course.displayName} tip"), style = TextStyle(color = FixedColor(Color(0xFF52B891)), fontSize = 12.sp, fontWeight = FontWeight.Bold))
            Spacer(GlanceModifier.defaultWeight())
            Text(
                lang.pick("Nächster ›", "Next ›"),
                style = TextStyle(color = FixedColor(Color(0xFFA59F96)), fontSize = 12.sp, fontWeight = FontWeight.Bold),
                modifier = GlanceModifier.clickable(actionRunCallback<NextTipAction>()).padding(4.dp),
            )
        }
        Spacer(GlanceModifier.height(4.dp))
        Column(GlanceModifier.fillMaxWidth().defaultWeight().clickable(actionStartActivity(intent))) {
            Text(tip.title, style = TextStyle(color = FixedColor(Color(0xFFF2EEE8)), fontSize = 14.sp, fontWeight = FontWeight.Bold), maxLines = 2)
            Spacer(GlanceModifier.height(6.dp))
            Text(
                tip.code,
                style = TextStyle(color = FixedColor(Color(0xFFD9D4CB)), fontSize = if (size.width < 220.dp) 11.sp else 13.sp, fontFamily = FontFamily.Monospace),
                maxLines = if (size.height < 140.dp) 2 else 6,
            )
        }
    }
}

class NextTipAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        updateAppWidgetState(context, glanceId) { prefs ->
            prefs[TipOffsetKey] = (prefs[TipOffsetKey] ?: 0) + 1
        }
        TipWidget().update(context, glanceId)
    }
}

class TipWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = TipWidget()
}
