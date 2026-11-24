package app.pyquest.ui.home

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.pyquest.ui.ambient.screenBackground
import app.pyquest.data.Achievement
import app.pyquest.data.AppSettings
import app.pyquest.data.Course
import app.pyquest.data.UserProgress
import app.pyquest.data.achievementStats
import app.pyquest.data.completedLessonCount
import app.pyquest.data.currentStreak
import app.pyquest.data.isCompleted
import app.pyquest.data.xpOn
import app.pyquest.i18n.AppLanguage
import app.pyquest.i18n.LocalLanguage
import app.pyquest.i18n.tr
import app.pyquest.ui.code.codeTextStyle
import app.pyquest.ui.components.AchievementBadge
import app.pyquest.ui.components.BitsIcon
import app.pyquest.ui.components.ChunkyButton
import app.pyquest.ui.components.Monty
import app.pyquest.ui.components.MontyMood
import app.pyquest.ui.components.PyProgressBar
import app.pyquest.ui.theme.Adaptive
import app.pyquest.ui.theme.PyTheme
import app.pyquest.ui.theme.unitColors
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle

@Composable
fun ProfileScreen(
    course: Course,
    progress: UserProgress,
    settings: AppSettings,
    onSettings: () -> Unit,
    onShop: () -> Unit,
    onRename: (String) -> Unit,
    allCourses: List<Course> = listOf(course),
) {
    val c = PyTheme.colors
    val lang = LocalLanguage.current
    var renaming by remember { mutableStateOf(false) }
    var selectedAchievement by remember { mutableStateOf<Achievement?>(null) }
    val stats = remember(progress, allCourses) { progress.achievementStats(allCourses) }

    val window = Adaptive.window
    val headerSection: @Composable () -> Unit = {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier
                    .size(92.dp)
                    .clip(CircleShape)
                    .background(c.primarySoft),
                contentAlignment = Alignment.Center,
            ) {
                Monty(size = 78.dp, mood = MontyMood.HAPPY)
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        progress.name.ifBlank { tr("Code-Neuling", "Code newbie") },
                        style = MaterialTheme.typography.headlineSmall,
                        color = c.text,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    IconButton(onClick = { renaming = true }) {
                        Icon(Icons.Rounded.Edit, tr("Namen ändern", "Change name"), tint = c.textMuted, modifier = Modifier.size(20.dp))
                    }
                }
                val since = if (progress.createdAt > 0) {
                    Instant.ofEpochMilli(progress.createdAt).atZone(ZoneId.systemDefault()).toLocalDate()
                        .format(DateTimeFormatter.ofPattern("MMMM yyyy", lang.locale))
                } else tr("heute", "today")
                Text(tr("Dabei seit $since", "Learning since $since"), style = MaterialTheme.typography.bodyMedium, color = c.textMuted)
                Spacer(Modifier.height(8.dp))
                Text(
                    levelTitle(progress.totalXp, lang),
                    style = MaterialTheme.typography.labelLarge,
                    color = c.primaryText,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(c.primarySoft)
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                )
            }
        }

        Row(Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            // In schmalen Spalten (z.B. Profil auf dem Fold-Innendisplay) ohne Icons, damit der Text passt
            val roomy = !window.twoColumns
            ChunkyButton(
                tr("Einstellungen", "Settings"),
                onClick = onSettings,
                outlined = true,
                icon = if (roomy) Icons.Rounded.Settings else null,
                modifier = Modifier.weight(1f),
            )
            ChunkyButton(tr("Shop", "Shop"), onClick = onShop, outlined = true, icon = Icons.Rounded.ShoppingBag, modifier = Modifier.weight(1f))
        }
    }
    val statsSection: @Composable () -> Unit = {
        SectionTitle(tr("Statistik", "Stats"))
        Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatTile(Icons.Rounded.LocalFireDepartment, c.streak, "${progress.currentStreak()}", tr("Tage Streak", "day streak"), Modifier.weight(1f))
                StatTile(Icons.Rounded.Bolt, c.xp, "${progress.totalXp}", tr("Gesamt-XP", "total XP"), Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatTile(Icons.Rounded.EmojiEvents, c.gold, "${course.completedLessonCount(progress)}/${course.lessons.size}", tr("Lektionen", "lessons"), Modifier.weight(1f))
                StatTile(Icons.AutoMirrored.Rounded.TrendingUp, c.success, "${maxOf(progress.longestStreak, progress.streak)}", tr("Längster Streak", "longest streak"), Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatTile(Icons.Rounded.Code, c.gem, "${progress.codeRuns}", tr("Programme gestartet", "programs run"), Modifier.weight(1f))
                StatTile(Icons.Rounded.EmojiEvents, c.primaryText, "${progress.achievements.size}/${Achievement.entries.size}", tr("Erfolge", "achievements"), Modifier.weight(1f))
            }
        }
    }
    val weekSection: @Composable () -> Unit = {
        SectionTitle(tr("Diese Woche", "This week"))
        WeeklyChart(progress, settings.dailyGoal, Modifier.padding(horizontal = 16.dp))
    }
    val calendarSection: @Composable () -> Unit = {
        SectionTitle(tr("Lernkalender", "Learning calendar"))
        MonthCalendar(progress, Modifier.padding(horizontal = 16.dp))
    }
    val courseSection: @Composable () -> Unit = {
        SectionTitle(tr("Kursfortschritt", "Course progress"))
        Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            course.units.forEach { u ->
                val done = u.lessons.count { progress.isCompleted(it.id) }
                val (color, _) = unitColors(u.colorIndex)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("${u.number}", style = MaterialTheme.typography.labelLarge, color = c.textMuted, modifier = Modifier.width(28.dp))
                    Column(Modifier.weight(1f)) {
                        Row {
                            Text(u.title, style = MaterialTheme.typography.bodyMedium, color = c.text, modifier = Modifier.weight(1f))
                            Text("$done/${u.lessons.size}", style = MaterialTheme.typography.labelMedium, color = c.textMuted)
                        }
                        Spacer(Modifier.height(4.dp))
                        PyProgressBar(done.toFloat() / u.lessons.size, color = color, height = 8.dp, modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        }
    }
    val achievementsSection: @Composable () -> Unit = {
        SectionTitle(tr("Erfolge", "Achievements"))
        val achievements = Achievement.entries
        Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            achievements.chunked(3).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    row.forEach { a ->
                        val unlocked = a.name in progress.achievements
                        Column(
                            Modifier
                                .weight(1f)
                                .clip(PyTheme.shapes.mediumShape)
                                .clickable { selectedAchievement = a }
                                .padding(vertical = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            AchievementBadge(a, unlocked, size = 62.dp)
                            Spacer(Modifier.height(6.dp))
                            Text(
                                a.title.of(lang),
                                style = MaterialTheme.typography.labelMedium,
                                color = if (unlocked) c.text else c.textMuted,
                                textAlign = TextAlign.Center,
                                maxLines = 2,
                            )
                            if (!unlocked && a.target > 1) {
                                Spacer(Modifier.height(4.dp))
                                PyProgressBar(a.progress(stats), color = c.gold, height = 6.dp, modifier = Modifier.width(60.dp))
                            }
                        }
                    }
                    repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
    }

    if (window.twoColumns) {
        // Innendisplay des Z Fold / Tablet: zwei Spalten nebeneinander
        Row(
            Modifier
                .fillMaxSize()
                .screenBackground()
                .statusBarsPadding()
        ) {
            Column(
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
            ) {
                headerSection()
                statsSection()
                weekSection()
                Spacer(Modifier.height(40.dp))
            }
            Column(
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
            ) {
                calendarSection()
                courseSection()
                achievementsSection()
                Spacer(Modifier.height(40.dp))
            }
        }
    } else {
        Column(
            Modifier
                .fillMaxSize()
                .screenBackground()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
        ) {
            headerSection()
            statsSection()
            weekSection()
            calendarSection()
            courseSection()
            achievementsSection()
            Spacer(Modifier.height(120.dp))
        }
    }

    if (renaming) {
        var name by remember { mutableStateOf(progress.name) }
        AlertDialog(
            onDismissRequest = { renaming = false },
            title = { Text(tr("Wie heißt du?", "What's your name?")) },
            text = {
                OutlinedTextField(value = name, onValueChange = { name = it.take(30) }, singleLine = true, label = { Text(tr("Name", "Name")) })
            },
            confirmButton = {
                TextButton(onClick = {
                    onRename(name)
                    renaming = false
                }) { Text(tr("Speichern", "Save")) }
            },
            dismissButton = { TextButton(onClick = { renaming = false }) { Text(tr("Abbrechen", "Cancel")) } },
            containerColor = c.surface,
        )
    }

    selectedAchievement?.let { a ->
        val unlocked = a.name in progress.achievements
        AlertDialog(
            onDismissRequest = { selectedAchievement = null },
            icon = { AchievementBadge(a, unlocked, size = 72.dp) },
            title = { Text(a.title.of(lang), textAlign = TextAlign.Center) },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(a.description.of(lang), textAlign = TextAlign.Center)
                    Spacer(Modifier.height(8.dp))
                    if (unlocked) {
                        val date = Instant.ofEpochMilli(progress.achievements[a.name] ?: 0).atZone(ZoneId.systemDefault()).toLocalDate()
                        val pattern = if (lang == AppLanguage.EN) "MMMM d, yyyy" else "d. MMMM yyyy"
                        Text(
                            tr("Freigeschaltet am ", "Unlocked on ") + date.format(DateTimeFormatter.ofPattern(pattern, lang.locale)),
                            color = c.success,
                        )
                    } else if (a.target > 1) {
                        Text(tr("Fortschritt: ", "Progress: ") + "${a.metric(stats).coerceAtLeast(0)} / ${a.target}", color = c.textMuted)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(tr("Belohnung: ${a.gems}", "Reward: ${a.gems}"), color = c.gem)
                        Spacer(Modifier.width(4.dp))
                        BitsIcon(size = 16.dp)
                    }
                }
            },
            confirmButton = { TextButton(onClick = { selectedAchievement = null }) { Text("OK") } },
            containerColor = c.surface,
        )
    }
}

private fun levelTitle(xp: Int, lang: AppLanguage): String = when {
    xp >= 8000 -> lang.pick("Großmeister:in", "Grandmaster")
    xp >= 5000 -> lang.pick("Schlangenbeschwörer:in", "Snake charmer")
    xp >= 2500 -> lang.pick("Code-Profi", "Code pro")
    xp >= 1200 -> lang.pick("Fortgeschritten", "Advanced")
    xp >= 500 -> lang.pick("Aufsteiger:in", "Rising star")
    xp >= 100 -> lang.pick("Lernende:r", "Learner")
    else -> lang.pick("Frisch geschlüpft", "Freshly hatched")
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleLarge,
        color = PyTheme.colors.text,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 10.dp),
    )
}

@Composable
private fun StatTile(icon: ImageVector, color: Color, value: String, label: String, modifier: Modifier) {
    val c = PyTheme.colors
    Row(
        modifier
            .clip(PyTheme.shapes.mediumShape)
            .background(c.surfaceAlt)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(10.dp))
        Column {
            Text(value, style = MaterialTheme.typography.titleMedium, color = c.text, maxLines = 1)
            Text(label, style = MaterialTheme.typography.bodySmall, color = c.textMuted, maxLines = 2)
        }
    }
}

/** Balkendiagramm der XP der letzten 7 Tage, mit Tagesziel-Linie. */
@Composable
private fun WeeklyChart(progress: UserProgress, goal: Int, modifier: Modifier = Modifier) {
    val c = PyTheme.colors
    val locale = LocalLanguage.current.locale
    val today = LocalDate.now()
    val days = (6 downTo 0).map { today.minusDays(it.toLong()) }
    val values = days.map { progress.xpOn(it) }
    val max = maxOf(values.maxOrNull() ?: 0, goal, 10)
    val grow = remember { Animatable(0f) }
    LaunchedEffect(Unit) { grow.animateTo(1f, tween(900, easing = FastOutSlowInEasing)) }
    Column(
        modifier
            .fillMaxWidth()
            .clip(PyTheme.shapes.mediumShape)
            .background(c.surfaceAlt)
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("${values.sum()} XP", style = MaterialTheme.typography.titleMedium, color = c.text, modifier = Modifier.weight(1f))
            Text(tr("Tagesziel: $goal XP", "Daily goal: $goal XP"), style = MaterialTheme.typography.labelMedium, color = c.textMuted)
        }
        Spacer(Modifier.height(12.dp))
        val barColor = c.primary
        val todayColor = c.gold
        val goalColor = c.textMuted.copy(alpha = 0.6f)
        val track = c.border.copy(alpha = 0.7f)
        Canvas(
            Modifier
                .fillMaxWidth()
                .height(140.dp)
        ) {
            val n = values.size
            val gap = 10.dp.toPx()
            val barW = (size.width - gap * (n - 1)) / n
            values.forEachIndexed { i, v ->
                val x = i * (barW + gap)
                drawRoundRect(track, topLeft = Offset(x, 0f), size = Size(barW, size.height), cornerRadius = CornerRadius(6.dp.toPx()))
                val h = size.height * (v.toFloat() / max) * grow.value
                if (h > 0f) {
                    drawRoundRect(
                        if (i == n - 1) todayColor else barColor,
                        topLeft = Offset(x, size.height - h),
                        size = Size(barW, h),
                        cornerRadius = CornerRadius(6.dp.toPx()),
                    )
                }
            }
            val gy = size.height * (1f - goal.toFloat() / max)
            drawLine(goalColor, Offset(0f, gy), Offset(size.width, gy), 2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f)))
        }
        Spacer(Modifier.height(6.dp))
        Row(Modifier.fillMaxWidth()) {
            days.forEach { d ->
                Text(
                    d.dayOfWeek.getDisplayName(TextStyle.SHORT_STANDALONE, locale).take(2),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (d == today) c.gold else c.textMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}
