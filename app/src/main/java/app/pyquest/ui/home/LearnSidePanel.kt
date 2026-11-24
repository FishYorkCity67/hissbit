package app.pyquest.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.RocketLaunch
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.pyquest.data.AppSettings
import app.pyquest.data.CodeExercise
import app.pyquest.data.Course
import app.pyquest.data.Game
import app.pyquest.data.PythonTip
import app.pyquest.data.SessionBuilder
import app.pyquest.data.UserProgress
import app.pyquest.data.challengeDoneToday
import app.pyquest.data.completedLessonCount
import app.pyquest.data.currentLesson
import app.pyquest.data.currentStreak
import app.pyquest.data.isActiveDay
import app.pyquest.data.isFrozenDay
import app.pyquest.data.streakDoneToday
import app.pyquest.data.xpOn
import app.pyquest.i18n.LocalLanguage
import app.pyquest.i18n.plural
import app.pyquest.i18n.tr
import app.pyquest.ui.code.CodeBlock
import app.pyquest.ui.code.codeTextStyle
import app.pyquest.ui.components.BitsIcon
import app.pyquest.ui.components.MarkdownText
import app.pyquest.ui.components.Monty
import app.pyquest.ui.components.MontyMood
import app.pyquest.ui.components.PyProgressBar
import app.pyquest.ui.components.StreakFlame
import app.pyquest.ui.components.rememberFeedback
import app.pyquest.ui.theme.PyTheme
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle

/** Maximale Breite des Lernpfads auf großen Displays. */
internal val PathWidth = 560.dp

/** Info-Spalte neben dem Lernpfad – nur auf großen Displays (z.B. Innendisplay des Z Fold 7). */
@Composable
fun LearnSidePanel(
    course: Course,
    progress: UserProgress,
    settings: AppSettings,
    now: Long,
    width: Dp,
    onStreak: () -> Unit,
    onHearts: () -> Unit,
    onShop: () -> Unit,
    onPractice: () -> Unit,
    challenges: List<CodeExercise>,
    tips: List<PythonTip>,
    challengeDone: Boolean,
    tipLabel: String,
) {
    val c = PyTheme.colors
    val feedback = rememberFeedback()
    val today = LocalDate.now()
    Column(
        Modifier
            .width(width)
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // Streak
        val streak = progress.currentStreak(today)
        val doneToday = progress.streakDoneToday(today)
        PanelCard(onClick = onStreak) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                StreakFlame(size = 44.dp, active = doneToday)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        plural(streak, "Tag", "Tage", "day", "days") + " Streak",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (doneToday) c.streak else c.text,
                    )
                    Text(
                        if (doneToday) tr("Heute geschafft – stark!", "Done for today – great!") else tr("Heute noch eine Lektion!", "One lesson to go today!"),
                        style = MaterialTheme.typography.bodySmall,
                        color = c.textMuted,
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            MiniWeek(progress, today)
        }

        // Tagesziel
        val xp = progress.xpOn(today)
        val goal = settings.dailyGoal
        PanelCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(if (xp >= goal) Icons.Rounded.EmojiEvents else Icons.Rounded.Flag, null, tint = c.gold, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    if (xp >= goal) tr("Tagesziel erreicht", "Daily goal reached") else tr("Tagesziel", "Daily goal"),
                    style = MaterialTheme.typography.titleSmall,
                    color = c.text,
                    modifier = Modifier.weight(1f),
                )
                Text("$xp / $goal XP", style = MaterialTheme.typography.labelMedium, color = c.textMuted)
            }
            Spacer(Modifier.height(10.dp))
            PyProgressBar(xp.toFloat() / goal, color = c.gold, track = c.border, height = 8.dp, modifier = Modifier.fillMaxWidth())
        }

        // Herzen & Bits
        PanelCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(
                    Modifier
                        .weight(1f)
                        .clip(PyTheme.shapes.smallShape)
                        .clickable { feedback.tap(); onHearts() }
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Rounded.Favorite, null, tint = c.heart, modifier = Modifier.size(26.dp))
                    Spacer(Modifier.width(6.dp))
                    Column {
                        Text(if (settings.heartsEnabled) "${progress.hearts} / ${Game.MAX_HEARTS}" else "∞", style = MaterialTheme.typography.titleMedium, color = c.heart)
                        val refill = if (settings.heartsEnabled && progress.hearts < Game.MAX_HEARTS) {
                            val ms = (progress.heartsRefillAt - now).coerceAtLeast(0)
                            val min = (ms / 60000) + 1
                            tr("+1 in $min Min.", "+1 in $min min")
                        } else tr("Herzen", "Hearts")
                        Text(refill, style = MaterialTheme.typography.bodySmall, color = c.textMuted)
                    }
                }
                Row(
                    Modifier
                        .weight(1f)
                        .clip(PyTheme.shapes.smallShape)
                        .clickable { feedback.tap(); onShop() }
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    BitsIcon(size = 26.dp)
                    Spacer(Modifier.width(6.dp))
                    Column {
                        Text("${progress.gems}", style = MaterialTheme.typography.titleMedium, color = c.gem)
                        Text(tr("Zum Shop", "Open shop"), style = MaterialTheme.typography.bodySmall, color = c.textMuted)
                    }
                }
            }
        }

        // Tages-Challenge
        val challenge = SessionBuilder.dailyChallenge(challenges, today)
        if (challenge != null) {
            PanelCard(onClick = if (challengeDone) null else onPractice, highlight = !challengeDone) {
                val fg = if (challengeDone) c.text else c.onPrimary
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.RocketLaunch, null, tint = fg, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(tr("Tages-Challenge", "Daily challenge"), style = MaterialTheme.typography.titleSmall, color = fg, modifier = Modifier.weight(1f))
                    if (challengeDone) {
                        Icon(Icons.Rounded.Check, null, tint = c.success)
                    } else {
                        Text("+20", style = MaterialTheme.typography.labelLarge, color = fg)
                        Spacer(Modifier.width(3.dp))
                        BitsIcon(size = 16.dp, color = fg)
                    }
                }
                Spacer(Modifier.height(6.dp))
                MarkdownText(
                    if (challengeDone) tr("Erledigt! Morgen wartet eine neue Aufgabe.", "Done! A new one is waiting tomorrow.")
                    else challenge.prompt.substringBefore(":**").removePrefix("**") + tr(" – tippe hier, um loszulegen.", " – tap here to start."),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (challengeDone) c.textMuted else fg,
                )
            }
        }

        // Tipp des Tages
        if (tips.isNotEmpty()) {
            var tipOffset by rememberSaveable { mutableIntStateOf(0) }
            val tip = tips[Math.floorMod(today.toEpochDay() + tipOffset, tips.size.toLong()).toInt()]
            PanelCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(tr("$tipLabel-Tipp", "$tipLabel tip"), style = MaterialTheme.typography.labelLarge, color = c.primaryText, modifier = Modifier.weight(1f))
                    Text(
                        tr("Nächster ›", "Next ›"),
                        style = MaterialTheme.typography.labelMedium,
                        color = c.primaryText,
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { feedback.tap(); tipOffset++ }
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(tip.title, style = MaterialTheme.typography.titleSmall, color = c.text, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(8.dp))
                CodeBlock(tip.code, modifier = Modifier.fillMaxWidth(), scale = 0.88f)
            }
        }

        // Kursfortschritt
        val done = course.completedLessonCount(progress)
        val currentLesson = course.currentLesson(progress)
        PanelCard {
            Text(tr("Kursfortschritt", "Course progress"), style = MaterialTheme.typography.titleSmall, color = c.text)
            Spacer(Modifier.height(6.dp))
            PyProgressBar(done.toFloat() / course.lessons.size, color = c.success, track = c.border, height = 8.dp, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(6.dp))
            Text(
                tr("$done von ${course.lessons.size} Lektionen", "$done of ${course.lessons.size} lessons") +
                    (currentLesson?.let { tr(" · weiter mit „${it.title}“", " · next up: “${it.title}”") } ?: tr(" · alles geschafft!", " · all done!")),
                style = MaterialTheme.typography.bodySmall,
                color = c.textMuted,
            )
        }

        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Monty(size = 110.dp, mood = if (doneToday) MontyMood.CELEBRATE else MontyMood.HAPPY)
        }
    }
}

@Composable
private fun PanelCard(
    onClick: (() -> Unit)? = null,
    highlight: Boolean = false,
    content: @Composable ColumnScope.() -> Unit,
) {
    val c = PyTheme.colors
    val shape = PyTheme.shapes.largeShape
    Column(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(if (highlight) c.primary else c.surfaceAlt)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(14.dp),
        content = content,
    )
}

@Composable
private fun MiniWeek(progress: UserProgress, today: LocalDate) {
    val c = PyTheme.colors
    val locale = LocalLanguage.current.locale
    val monday = today.minusDays((today.dayOfWeek.value - 1).toLong())
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        for (i in 0 until 7) {
            val day = monday.plusDays(i.toLong())
            val active = progress.isActiveDay(day)
            val frozen = progress.isFrozenDay(day)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    DayOfWeek.of(i + 1).getDisplayName(TextStyle.SHORT_STANDALONE, locale).take(2),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (day == today) c.streak else c.textMuted,
                )
                Spacer(Modifier.height(4.dp))
                Box(
                    Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            when {
                                active -> c.streak
                                frozen -> c.freeze
                                else -> c.locked
                            }
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    if (active) Icon(Icons.Rounded.Check, null, tint = Color.White, modifier = Modifier.size(15.dp))
                }
            }
        }
    }
}
