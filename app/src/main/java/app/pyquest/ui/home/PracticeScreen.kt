package app.pyquest.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.RocketLaunch
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import app.pyquest.ui.ambient.screenBackground
import app.pyquest.data.CodeExercise
import app.pyquest.data.Course
import app.pyquest.data.LessonKind
import app.pyquest.data.SessionBuilder
import app.pyquest.data.SessionMode
import app.pyquest.data.UserProgress
import app.pyquest.data.challengeDoneToday
import app.pyquest.data.isCompleted
import app.pyquest.i18n.tr
import app.pyquest.ui.code.CodeBlock
import app.pyquest.ui.code.codeTextStyle
import app.pyquest.ui.components.BitsIcon
import app.pyquest.ui.components.ChunkySurface
import app.pyquest.ui.components.MarkdownText
import app.pyquest.ui.components.Monty
import app.pyquest.ui.components.MontyMood
import app.pyquest.ui.components.SpeechBubble
import app.pyquest.ui.theme.PyTheme
import app.pyquest.ui.theme.ReadingWidth
import app.pyquest.ui.theme.centeredMaxWidth
import app.pyquest.ui.theme.lighten
import app.pyquest.ui.theme.onColor

@Composable
fun PracticeScreen(
    course: Course,
    challenges: List<CodeExercise>,
    progress: UserProgress,
    onStart: (SessionMode) -> Unit,
    challengeDone: Boolean = progress.challengeDoneToday(),
) {
    val c = PyTheme.colors
    val completedLessons = course.lessons.count { it.kind != LessonKind.CHECKPOINT && progress.isCompleted(it.id) }
    val codeAvailable = course.lessons.any { l -> l.kind != LessonKind.CHECKPOINT && progress.isCompleted(l.id) && l.exercises.any { it is CodeExercise } }
    val mistakes = progress.mistakes.count { course.exercises.containsKey(it) }
    val challenge = SessionBuilder.dailyChallenge(challenges)

    Column(
        Modifier
            .fillMaxSize()
            .screenBackground()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            Modifier
                .centeredMaxWidth(ReadingWidth)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(tr("Üben", "Practice"), style = MaterialTheme.typography.headlineMedium, color = c.text)
            Spacer(Modifier.height(4.dp))
            Text(
                tr("Wiederholen festigt dein Wissen – und bringt dir Herzen zurück.", "Reviewing makes knowledge stick – and earns hearts back."),
                style = MaterialTheme.typography.bodyMedium,
                color = c.textMuted,
            )
            Spacer(Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Monty(size = 86.dp, mood = if (completedLessons == 0) MontyMood.THINKING else MontyMood.HAPPY)
                Spacer(Modifier.width(8.dp))
                SpeechBubble(
                    when {
                        completedLessons == 0 -> tr(
                            "Schließe zuerst eine Lektion ab – dann können wir hier gemeinsam üben!",
                            "Finish a lesson first – then we can practise together here!",
                        )
                        mistakes == 1 -> tr(
                            "Es gibt **eine** Aufgabe, die wir uns nochmal anschauen sollten.",
                            "There's **one** exercise we should look at again.",
                        )
                        mistakes > 1 -> tr(
                            "Du hast **$mistakes** Aufgaben, die wir nochmal anschauen sollten.",
                            "You have **$mistakes** exercises we should look at again.",
                        )
                        else -> tr("Alles sitzt! Lust auf eine Challenge?", "All good! Fancy a challenge?")
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 18.dp),
                )
            }
            Spacer(Modifier.height(16.dp))

            // Tages-Challenge
            if (challenge != null) {
                // Anthrazit-Karte (im dunklen Design Elfenbein) statt grellem Farbblock
                val fg = if (challengeDone) c.text else c.background
                ChunkySurface(
                    onClick = if (challengeDone) null else ({ onStart(SessionMode.CHALLENGE) }),
                    face = if (challengeDone) c.surface else c.text,
                    edge = c.border,
                    borderWidth = if (challengeDone) 1.dp else 0.dp,
                    shape = PyTheme.shapes.largeShape,
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.TopStart,
                    contentPadding = PaddingValues(18.dp),
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                if (challengeDone) Icons.Rounded.Check else Icons.Rounded.RocketLaunch,
                                null,
                                tint = if (challengeDone) c.success else fg,
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                tr("Tages-Challenge", "Daily challenge"),
                                style = MaterialTheme.typography.labelLarge,
                                color = if (challengeDone) c.success else fg,
                            )
                            Spacer(Modifier.weight(1f))
                            if (challengeDone) {
                                Text(tr("Erledigt", "Done"), style = MaterialTheme.typography.labelLarge, color = c.success)
                            } else {
                                Text("+20", style = MaterialTheme.typography.labelLarge, color = fg)
                                Spacer(Modifier.width(4.dp))
                                BitsIcon(size = 18.dp, color = if (c.isDark) c.gemDark else c.secondary)
                            }
                        }
                        Spacer(Modifier.height(10.dp))
                        MarkdownText(challenge.prompt, color = fg, style = MaterialTheme.typography.bodyLarge)
                        if (!challengeDone && challenge.starter.isNotBlank()) {
                            Spacer(Modifier.height(10.dp))
                            CodeBlock(challenge.starter, modifier = Modifier.fillMaxWidth(), scale = 0.9f)
                        }
                        if (challengeDone) {
                            Spacer(Modifier.height(6.dp))
                            Text(
                                tr("Morgen wartet eine neue Challenge auf dich.", "A new challenge is waiting for you tomorrow."),
                                style = MaterialTheme.typography.bodySmall,
                                color = c.textMuted,
                            )
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            PracticeCard(
                title = tr("Fehler wiederholen", "Fix your mistakes"),
                subtitle = when {
                    mistakes == 1 -> tr("1 Aufgabe, bei der du dich vertan hast", "1 exercise you got wrong")
                    mistakes > 1 -> tr("$mistakes Aufgaben, bei denen du dich vertan hast", "$mistakes exercises you got wrong")
                    else -> tr("Keine offenen Fehler – stark!", "No open mistakes – nice!")
                },
                icon = Icons.Rounded.BugReport,
                color = c.error,
                enabled = mistakes > 0,
                onClick = { onStart(SessionMode.MISTAKES) },
            )
            Spacer(Modifier.height(12.dp))
            PracticeCard(
                title = tr("Schnelltraining", "Quick practice"),
                subtitle = if (completedLessons > 0) tr("10 gemischte Aufgaben aus deinen Lektionen · +1 Herz", "10 mixed exercises from your lessons · +1 heart")
                else tr("Verfügbar nach deiner ersten Lektion", "Available after your first lesson"),
                icon = Icons.Rounded.Shuffle,
                color = c.success,
                enabled = completedLessons > 0,
                onClick = { onStart(SessionMode.PRACTICE) },
            )
            Spacer(Modifier.height(12.dp))
            PracticeCard(
                title = tr("Code-Werkstatt", "Code workshop"),
                subtitle = if (codeAvailable) tr("3 Programmieraufgaben – schreib echten Code", "3 coding tasks – write real code")
                else tr("Verfügbar, sobald du Programmieraufgaben gelöst hast", "Available once you've solved coding tasks"),
                icon = Icons.Rounded.Build,
                color = c.gem,
                enabled = codeAvailable,
                onClick = { onStart(SessionMode.CODE_PRACTICE) },
            )
            Spacer(Modifier.height(24.dp))
            Text(
                tr("Übungsrunden kosten keine Herzen und zählen für deinen Streak.", "Practice rounds don't cost hearts and count for your streak."),
                style = MaterialTheme.typography.bodySmall,
                color = c.textMuted,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
            Spacer(Modifier.height(100.dp))
        }
    }
}

@Composable
private fun PracticeCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val c = PyTheme.colors
    ChunkySurface(
        onClick = if (enabled) onClick else null,
        face = c.surface,
        edge = c.border,
        borderWidth = 1.dp,
        shape = PyTheme.shapes.largeShape,
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterStart,
        contentPadding = PaddingValues(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(if (enabled) c.surfaceAlt else c.locked),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    if (enabled) icon else Icons.Rounded.Lock,
                    null,
                    tint = if (enabled) c.text else c.lockedIcon,
                    modifier = Modifier.size(24.dp),
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, color = if (enabled) c.text else c.textMuted)
                Spacer(Modifier.height(2.dp))
                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = c.textMuted)
            }
            if (enabled) {
                Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null, tint = c.textMuted, modifier = Modifier.size(26.dp))
            }
        }
    }
}
