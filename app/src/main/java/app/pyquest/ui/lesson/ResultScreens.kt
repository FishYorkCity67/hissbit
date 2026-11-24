package app.pyquest.ui.lesson

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.rounded.AcUnit
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.pyquest.data.Achievement
import app.pyquest.data.Reward
import app.pyquest.data.SessionMode
import app.pyquest.data.UserProgress
import app.pyquest.data.isActiveDay
import app.pyquest.data.isFrozenDay
import app.pyquest.i18n.LocalLanguage
import app.pyquest.i18n.tr
import app.pyquest.ui.code.codeTextStyle
import app.pyquest.ui.components.AchievementBadge
import app.pyquest.ui.components.BitsIcon
import app.pyquest.ui.components.ChunkyButton
import app.pyquest.ui.components.ConfettiBurst
import app.pyquest.ui.components.Monty
import app.pyquest.ui.components.MontyMood
import app.pyquest.ui.components.PyProgressBar
import app.pyquest.ui.components.StreakFlame
import app.pyquest.ui.components.rememberFeedback
import app.pyquest.ui.home.cardColor
import app.pyquest.ui.theme.FormWidth
import app.pyquest.ui.theme.PyTheme
import app.pyquest.ui.theme.centeredMaxWidth
import kotlinx.coroutines.delay
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle

private enum class ResultStep { SUMMARY, STREAK, ACHIEVEMENTS }

@Composable
fun ResultFlow(reward: Reward, progress: UserProgress?, onDone: () -> Unit) {
    val steps = remember(reward) {
        buildList {
            add(ResultStep.SUMMARY)
            if (reward.streakExtended) add(ResultStep.STREAK)
            if (reward.achievements.isNotEmpty()) add(ResultStep.ACHIEVEMENTS)
        }
    }
    var index by remember(reward) { mutableIntStateOf(0) }
    fun advance() {
        if (index + 1 < steps.size) index++ else onDone()
    }
    BackHandler { advance() }
    Box(
        Modifier
            .fillMaxSize()
            .background(PyTheme.colors.background)
    ) {
        AnimatedContent(
            targetState = steps[index],
            transitionSpec = { (fadeIn(tween(250)) + scaleIn(initialScale = 0.92f)).togetherWith(fadeOut(tween(150))) },
            label = "result",
        ) { step ->
            when (step) {
                ResultStep.SUMMARY -> SummaryStep(reward, ::advance)
                ResultStep.STREAK -> StreakStep(reward, progress, ::advance)
                ResultStep.ACHIEVEMENTS -> AchievementsStep(reward.achievements, ::advance)
            }
        }
    }
}

/** Eine Kennzahl auf dem Ergebnis-Bildschirm. */
private data class ResultStat(val label: String, val value: String, val color: Color)

@Composable
private fun SummaryStep(reward: Reward, onNext: () -> Unit) {
    val c = PyTheme.colors
    val fx = rememberFeedback()
    LaunchedEffect(Unit) { fx.complete() }
    val r = reward.result
    val title = when {
        reward.unitCompleted != null -> tr("Einheit geschafft!", "Unit complete!")
        r.mode == SessionMode.CHECKPOINT -> tr("Checkpoint bestanden!", "Checkpoint passed!")
        r.mode == SessionMode.CHALLENGE -> tr("Challenge gelöst!", "Challenge solved!")
        r.mode == SessionMode.LESSON && r.perfect -> tr("Perfekte Lektion!", "Perfect lesson!")
        r.mode == SessionMode.LESSON -> tr("Lektion geschafft!", "Lesson complete!")
        else -> tr("Training beendet!", "Practice done!")
    }
    val minutes = r.durationMs / 60000
    val seconds = (r.durationMs / 1000) % 60
    val stats = listOf(
        ResultStat(if (reward.boosted) tr("XP (2× Boost)", "XP (2× boost)") else "XP", "+${reward.xp}", c.xp),
        ResultStat(tr("Genauigkeit", "Accuracy"), "${r.accuracy} %", c.success),
        ResultStat(tr("Zeit", "Time"), "%d:%02d".format(minutes, seconds), c.text),
    )
    var shown by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        delay(300)
        while (shown < stats.size) {
            shown++
            fx.tick()
            delay(180)
        }
    }

    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxHeight()
                .centeredMaxWidth(FormWidth)
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(12.dp))
            Monty(size = 132.dp, mood = MontyMood.CELEBRATE)
            Spacer(Modifier.height(14.dp))
            Text(title, style = MaterialTheme.typography.headlineMedium, color = c.text, textAlign = TextAlign.Center)
            Spacer(Modifier.height(6.dp))
            Text(
                reward.unitCompleted?.let { tr("Einheit ${it.number}: ${it.title} ist abgeschlossen.", "Unit ${it.number}: ${it.title} is complete.") }
                    ?: if (r.mistakes == 0) tr("${r.total} Aufgaben, keine Fehler", "${r.total} exercises, no mistakes")
                    else tr("${r.total} Aufgaben, ${r.mistakes} Fehler korrigiert", "${r.total} exercises, ${r.mistakes} mistakes fixed"),
                style = MaterialTheme.typography.bodyLarge,
                color = c.textMuted,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(22.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                stats.forEachIndexed { i, stat ->
                    val alpha by animateFloatAsState(if (i < shown) 1f else 0f, tween(220), label = "stat$i")
                    Column(
                        Modifier
                            .weight(1f)
                            .graphicsLayer { this.alpha = alpha }
                            .clip(PyTheme.shapes.mediumShape)
                            .background(c.surfaceAlt)
                            .padding(horizontal = 12.dp, vertical = 14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(stat.value, style = MaterialTheme.typography.titleLarge, color = stat.color, maxLines = 1)
                        Spacer(Modifier.height(2.dp))
                        Text(stat.label, style = MaterialTheme.typography.labelMedium, color = c.textMuted, maxLines = 1, textAlign = TextAlign.Center)
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                BitsIcon(size = 18.dp)
                Spacer(Modifier.width(6.dp))
                Text(
                    "+${reward.gems} Bits" + if (reward.heartsGained > 0) tr("  ·  +${reward.heartsGained} Herz", "  ·  +${reward.heartsGained} heart") else "",
                    style = MaterialTheme.typography.labelLarge,
                    color = c.textMuted,
                )
            }
            if (reward.dailyGoal > 0) {
                Spacer(Modifier.height(16.dp))
                GoalProgress(reward)
            }
            Spacer(Modifier.height(24.dp))
            ChunkyButton(tr("Weiter", "Continue"), onClick = onNext, modifier = Modifier.fillMaxWidth())
        }
        ConfettiBurst(trigger = reward)
    }
}

@Composable
private fun GoalProgress(reward: Reward) {
    val c = PyTheme.colors
    val reached = reward.xpToday >= reward.dailyGoal
    Column(
        Modifier
            .fillMaxWidth()
            .clip(PyTheme.shapes.mediumShape)
            .background(c.surfaceAlt)
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(if (reached) Icons.Rounded.EmojiEvents else Icons.Rounded.Flag, null, tint = c.gold, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text(
                when {
                    reward.goalReachedNow -> tr("Tagesziel erreicht! +10 Bits", "Daily goal reached! +10 bits")
                    reached -> tr("Tagesziel erreicht", "Daily goal reached")
                    else -> tr("Tagesziel", "Daily goal")
                },
                style = MaterialTheme.typography.titleSmall,
                color = c.text,
                modifier = Modifier.weight(1f),
            )
            Text("${reward.xpToday} / ${reward.dailyGoal} XP", style = MaterialTheme.typography.labelLarge, color = c.textMuted)
        }
        Spacer(Modifier.height(10.dp))
        PyProgressBar(reward.xpToday.toFloat() / reward.dailyGoal, color = c.gold, track = c.border, height = 8.dp, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun StreakStep(reward: Reward, progress: UserProgress?, onNext: () -> Unit) {
    val c = PyTheme.colors
    val fx = rememberFeedback()
    val count = remember { Animatable((reward.streak - 1).coerceAtLeast(0).toFloat()) }
    var bump by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(450)
        fx.streak()
        bump = true
        count.animateTo(reward.streak.toFloat(), tween(600, easing = FastOutSlowInEasing))
    }
    val scale by animateFloatAsState(if (bump) 1f else 0.7f, spring(dampingRatio = 0.35f, stiffness = Spring.StiffnessLow), label = "flame")
    val message = when {
        reward.streak == 1 -> tr(
            "Der Anfang ist gemacht! Komm morgen wieder, um deinen Streak zu verlängern.",
            "You've started! Come back tomorrow to grow your streak.",
        )
        reward.streak < 7 -> tr("Jeden Tag ein bisschen – Bit für Bit wirst du zum Profi!", "A little every day – bit by bit you become a pro!")
        reward.streak < 30 -> tr("Wahnsinn! Du bist richtig im Flow.", "Amazing! You're really in the flow.")
        else -> tr("Legendär! Dein Durchhaltevermögen ist beeindruckend.", "Legendary! Your persistence is impressive.")
    }
    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxHeight()
                .centeredMaxWidth(FormWidth)
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.weight(1f))
            Box(Modifier.graphicsLayer { scaleX = scale; scaleY = scale }) {
                StreakFlame(size = 150.dp, active = true)
            }
            Text(
                "${count.value.toInt()}",
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 84.sp),
                color = c.streak,
            )
            Text(
                if (reward.streak == 1) tr("Tag Streak", "day streak") else tr("Tage Streak", "day streak"),
                style = MaterialTheme.typography.headlineSmall,
                color = c.streak,
            )
            Spacer(Modifier.height(24.dp))
            if (progress != null) WeekRow(progress)
            Spacer(Modifier.height(20.dp))
            Text(message, style = MaterialTheme.typography.bodyLarge, color = c.textMuted, textAlign = TextAlign.Center)
            Spacer(Modifier.weight(1.3f))
            ChunkyButton(tr("Weiter", "Continue"), onClick = onNext, modifier = Modifier.fillMaxWidth(), color = c.streak, shadow = c.streakDark)
        }
        ConfettiBurst(trigger = "streak${reward.streak}", count = 90)
    }
}

/** Mo–So der aktuellen Woche mit Häkchen für Lerntage. */
@Composable
fun WeekRow(progress: UserProgress, today: LocalDate = LocalDate.now()) {
    val c = PyTheme.colors
    val locale = LocalLanguage.current.locale
    val monday = today.minusDays((today.dayOfWeek.value - 1).toLong())
    Row(
        Modifier
            .fillMaxWidth()
            .clip(PyTheme.shapes.mediumShape)
            .background(c.surfaceAlt)
            .padding(vertical = 14.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        for (i in 0 until 7) {
            val day = monday.plusDays(i.toLong())
            val active = progress.isActiveDay(day)
            val frozen = progress.isFrozenDay(day)
            val isToday = day == today
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    DayOfWeek.of(i + 1).getDisplayName(TextStyle.SHORT_STANDALONE, locale).take(2),
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isToday) c.streak else c.textMuted,
                )
                Spacer(Modifier.height(6.dp))
                Box(
                    Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            when {
                                active -> c.streak
                                frozen -> c.freeze
                                else -> c.locked
                            }
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    when {
                        active -> Icon(Icons.Rounded.Check, null, tint = Color.White, modifier = Modifier.size(20.dp))
                        frozen -> Icon(Icons.Rounded.AcUnit, null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun AchievementsStep(achievements: List<Achievement>, onNext: () -> Unit) {
    val c = PyTheme.colors
    val fx = rememberFeedback()
    val lang = LocalLanguage.current
    var shown by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(150)
        shown = true
        fx.unlock()
    }
    val badgeScale by animateFloatAsState(
        if (shown) 1f else 0.2f,
        spring(dampingRatio = 0.38f, stiffness = Spring.StiffnessLow),
        label = "badge",
    )
    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxHeight()
                .centeredMaxWidth(FormWidth)
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.weight(1f))
            Box(
                Modifier.graphicsLayer {
                    scaleX = badgeScale
                    scaleY = badgeScale
                    rotationZ = (1f - badgeScale) * -40f
                },
                contentAlignment = Alignment.Center,
            ) {
                AchievementBadge(achievements.first(), unlocked = true, size = 132.dp)
            }
            Spacer(Modifier.height(20.dp))
            Text(
                if (achievements.size == 1) tr("Erfolg freigeschaltet!", "Achievement unlocked!")
                else tr("${achievements.size} Erfolge freigeschaltet!", "${achievements.size} achievements unlocked!"),
                style = MaterialTheme.typography.headlineSmall,
                color = c.text,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(24.dp))
            achievements.take(4).forEach { a ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clip(PyTheme.shapes.mediumShape)
                        .background(c.surfaceAlt)
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AchievementBadge(a, unlocked = true, size = 58.dp)
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text(a.title.of(lang), style = MaterialTheme.typography.titleMedium, color = c.text)
                        Text(a.description.of(lang), style = MaterialTheme.typography.bodyMedium, color = c.textMuted)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("+${a.gems}", style = MaterialTheme.typography.labelLarge, color = c.gem)
                        Spacer(Modifier.width(3.dp))
                        BitsIcon(size = 16.dp)
                    }
                }
            }
            Spacer(Modifier.weight(1.3f))
            ChunkyButton(tr("Super!", "Awesome!"), onClick = onNext, modifier = Modifier.fillMaxWidth(), color = c.gold, shadow = c.goldDark)
        }
        ConfettiBurst(trigger = achievements)
    }
}
