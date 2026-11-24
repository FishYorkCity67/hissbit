package app.pyquest.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.AcUnit
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.pyquest.ui.ambient.screenBackground
import app.pyquest.data.AppSettings
import app.pyquest.data.Game
import app.pyquest.data.UserProgress
import app.pyquest.data.currentStreak
import app.pyquest.data.isActiveDay
import app.pyquest.data.isFrozenDay
import app.pyquest.data.streakDoneToday
import app.pyquest.i18n.LocalLanguage
import app.pyquest.i18n.plural
import app.pyquest.i18n.tr
import app.pyquest.ui.components.BitsIcon
import app.pyquest.ui.components.ChunkyButton
import app.pyquest.ui.components.HissbitLogo
import app.pyquest.ui.components.StreakFlame
import app.pyquest.ui.components.rememberFeedback
import app.pyquest.ui.theme.Adaptive
import app.pyquest.ui.theme.PyTheme
import kotlinx.coroutines.delay
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle

@Composable
fun TopStatsBar(
    progress: UserProgress,
    settings: AppSettings,
    now: Long,
    onStreak: () -> Unit,
    onGems: () -> Unit,
    onHearts: () -> Unit,
    leading: (@Composable () -> Unit)? = null,
) {
    val c = PyTheme.colors
    val streak = progress.currentStreak()
    val doneToday = progress.streakDoneToday()
    Column(
        Modifier
            .fillMaxWidth()
            .screenBackground()
            .statusBarsPadding()
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(58.dp)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Auf großen Displays steht das Logo schon in der Seitenleiste
            if (!Adaptive.window.useNavRail) {
                HissbitLogo(size = 34.dp)
                Spacer(Modifier.width(8.dp))
            }
            leading?.invoke()
            Spacer(Modifier.weight(1f))
            StatChip(
                icon = {
                    Icon(
                        Icons.Rounded.LocalFireDepartment,
                        null,
                        tint = if (doneToday) c.streak else c.lockedIcon,
                        modifier = Modifier.size(22.dp),
                    )
                },
                value = streak.toString(),
                color = if (doneToday) c.streak else c.textMuted,
                badge = if (progress.streakFreezes > 0) {
                    { Icon(Icons.Rounded.AcUnit, null, tint = c.freeze, modifier = Modifier.size(12.dp)) }
                } else null,
                onClick = onStreak,
            )
            Spacer(Modifier.width(6.dp))
            StatChip(
                icon = { BitsIcon(size = 20.dp) },
                value = progress.gems.toString(),
                color = c.gem,
                onClick = onGems,
            )
            Spacer(Modifier.width(6.dp))
            StatChip(
                icon = { Icon(Icons.Rounded.Favorite, null, tint = c.heart, modifier = Modifier.size(21.dp)) },
                value = if (settings.heartsEnabled) progress.hearts.toString() else "∞",
                color = c.heart,
                onClick = onHearts,
            )
        }
        HorizontalDivider(thickness = 1.dp, color = c.border)
    }
}

@Composable
private fun StatChip(
    icon: @Composable () -> Unit,
    value: String,
    color: Color,
    onClick: () -> Unit,
    badge: (@Composable () -> Unit)? = null,
) {
    val feedback = rememberFeedback()
    Row(
        Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable {
                feedback.tap()
                onClick()
            }
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        icon()
        Spacer(Modifier.width(5.dp))
        AnimatedContent(
            targetState = value,
            transitionSpec = { (slideInVertically { -it } + fadeIn()).togetherWith(slideOutVertically { it } + fadeOut()) },
            label = "stat",
        ) { v ->
            Text(v, color = color, style = MaterialTheme.typography.titleSmall)
        }
        if (badge != null) {
            Spacer(Modifier.width(3.dp))
            badge()
        }
    }
}

@Composable
fun StreakSheet(progress: UserProgress, onDismiss: () -> Unit, onShop: () -> Unit) {
    val c = PyTheme.colors
    val state = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val streak = progress.currentStreak()
    val doneToday = progress.streakDoneToday()
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = state, containerColor = c.surface) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("$streak", style = MaterialTheme.typography.displayMedium, color = if (doneToday) c.streak else c.textMuted)
                    Text(
                        if (streak == 1) tr("Tag Streak", "day streak") else tr("Tage Streak", "day streak"),
                        style = MaterialTheme.typography.titleMedium,
                        color = c.text,
                    )
                }
                StreakFlame(size = 78.dp, active = doneToday)
            }
            Spacer(Modifier.height(12.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(PyTheme.shapes.mediumShape)
                    .background(if (doneToday) c.streak.copy(alpha = 0.14f) else c.surfaceAlt)
                    .padding(14.dp)
            ) {
                Text(
                    when {
                        doneToday -> tr("Super! Für heute ist dein Streak gesichert.", "Great! Your streak is safe for today.")
                        streak > 0 -> tr(
                            "Mach heute eine Lektion, sonst verlierst du deinen Streak!",
                            "Finish a lesson today or you'll lose your streak!",
                        )
                        else -> tr(
                            "Schließe eine Lektion ab, um einen neuen Streak zu starten.",
                            "Finish a lesson to start a new streak.",
                        )
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = c.text,
                )
            }
            Spacer(Modifier.height(16.dp))
            MonthCalendar(progress)
            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                InfoTile(
                    tr("Längster Streak", "Longest streak"),
                    plural(maxOf(progress.longestStreak, streak), "Tag", "Tage", "day", "days"),
                    Modifier.weight(1f),
                )
                InfoTile(tr("Streak-Freezes", "Streak freezes"), "${progress.streakFreezes} / ${Game.MAX_FREEZES}", Modifier.weight(1f))
            }
            Spacer(Modifier.height(16.dp))
            ChunkyButton(
                tr("Streak-Freeze kaufen", "Buy a streak freeze"),
                onClick = onShop,
                color = c.freeze,
                shadow = c.freeze,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun InfoTile(label: String, value: String, modifier: Modifier) {
    val c = PyTheme.colors
    Column(
        modifier
            .clip(PyTheme.shapes.mediumShape)
            .background(c.surfaceAlt)
            .padding(12.dp)
    ) {
        Text(value, style = MaterialTheme.typography.titleMedium, color = c.text)
        Text(label, style = MaterialTheme.typography.bodySmall, color = c.textMuted)
    }
}

/** Monatskalender mit Lerntagen (Glut-Orange) und Freeze-Tagen (Eisblau). */
@Composable
fun MonthCalendar(progress: UserProgress, modifier: Modifier = Modifier) {
    val c = PyTheme.colors
    val locale = LocalLanguage.current.locale
    var month by remember { mutableStateOf(YearMonth.now()) }
    val today = LocalDate.now()
    Column(
        modifier
            .fillMaxWidth()
            .clip(PyTheme.shapes.mediumShape)
            .background(c.surfaceAlt)
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { month = month.minusMonths(1) }) {
                Icon(Icons.AutoMirrored.Rounded.KeyboardArrowLeft, tr("Voriger Monat", "Previous month"), tint = c.textMuted)
            }
            Text(
                month.month.getDisplayName(TextStyle.FULL_STANDALONE, locale).replaceFirstChar { it.titlecase(locale) } + " " + month.year,
                style = MaterialTheme.typography.titleMedium,
                color = c.text,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
            )
            IconButton(onClick = { month = month.plusMonths(1) }, enabled = month < YearMonth.now()) {
                Icon(
                    Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                    tr("Nächster Monat", "Next month"),
                    tint = if (month < YearMonth.now()) c.textMuted else c.locked,
                )
            }
        }
        Row(Modifier.fillMaxWidth()) {
            DayOfWeek.entries.forEach {
                Text(
                    it.getDisplayName(TextStyle.SHORT_STANDALONE, locale).take(2),
                    style = MaterialTheme.typography.labelSmall,
                    color = c.textMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        val first = month.atDay(1)
        val leading = first.dayOfWeek.value - 1
        val days = month.lengthOfMonth()
        val rows = (leading + days + 6) / 7
        for (r in 0 until rows) {
            Row(Modifier.fillMaxWidth()) {
                for (col in 0 until 7) {
                    val dayNum = r * 7 + col - leading + 1
                    Box(Modifier.weight(1f).aspectRatio(1f).padding(3.dp), contentAlignment = Alignment.Center) {
                        if (dayNum in 1..days) {
                            val date = month.atDay(dayNum)
                            val active = progress.isActiveDay(date)
                            val frozen = progress.isFrozenDay(date)
                            val isToday = date == today
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        when {
                                            active -> c.streak
                                            frozen -> c.freeze
                                            else -> Color.Transparent
                                        }
                                    )
                                    .then(if (isToday) Modifier.border(2.dp, c.streak, RoundedCornerShape(10.dp)) else Modifier),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    "$dayNum",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = when {
                                        active || frozen -> Color.White
                                        date.isAfter(today) -> c.lockedIcon
                                        else -> c.textMuted
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HeartsSheet(
    progress: UserProgress,
    settings: AppSettings,
    now: Long,
    onDismiss: () -> Unit,
    onShop: () -> Unit,
    onPractice: () -> Unit,
) {
    val c = PyTheme.colors
    val state = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    // Eigener Sekundentakt, solange das Blatt offen ist – der App-Takt tickt nur alle 10 s.
    val ticking by produceState(now) {
        while (true) {
            value = System.currentTimeMillis()
            delay(1000)
        }
    }
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = state, containerColor = c.surface) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(tr("Herzen", "Hearts"), style = MaterialTheme.typography.headlineSmall, color = c.text)
            Spacer(Modifier.height(12.dp))
            if (!settings.heartsEnabled) {
                Text(
                    tr(
                        "Das Herzen-System ist ausgeschaltet – du kannst unbegrenzt Fehler machen. Du kannst es in den Einstellungen wieder aktivieren.",
                        "Hearts are switched off – make as many mistakes as you like. You can turn them back on in the settings.",
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                    color = c.textMuted,
                    textAlign = TextAlign.Center,
                )
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    repeat(Game.MAX_HEARTS) { i ->
                        Icon(
                            if (i < progress.hearts) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                            null,
                            tint = if (i < progress.hearts) c.heart else c.lockedIcon,
                            modifier = Modifier.size(38.dp),
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                val text = if (progress.hearts >= Game.MAX_HEARTS) {
                    tr(
                        "Deine Herzen sind voll. Für jeden Fehler in einer Lektion verlierst du eins.",
                        "Your hearts are full. You lose one for every mistake in a lesson.",
                    )
                } else {
                    val ms = (progress.heartsRefillAt - ticking).coerceAtLeast(0)
                    val time = "%d:%02d".format(ms / 60000, (ms / 1000) % 60)
                    tr(
                        "Nächstes Herz in $time Min. Üben bringt dir auch Herzen!",
                        "Next heart in $time min. Practising earns hearts too!",
                    )
                }
                Text(text, style = MaterialTheme.typography.bodyLarge, color = c.textMuted, textAlign = TextAlign.Center)
                Spacer(Modifier.height(18.dp))
                ChunkyButton(
                    tr("Auffüllen für ${Game.PRICE_REFILL} Bits", "Refill for ${Game.PRICE_REFILL} bits"),
                    onClick = onShop,
                    enabled = progress.hearts < Game.MAX_HEARTS,
                    color = c.gem,
                    shadow = c.gemDark,
                    icon = null,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(10.dp))
                ChunkyButton(
                    tr("Üben, um Herzen zu verdienen", "Practise to earn hearts"),
                    onClick = onPractice,
                    outlined = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}
