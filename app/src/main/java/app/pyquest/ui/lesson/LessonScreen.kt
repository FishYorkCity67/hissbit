package app.pyquest.ui.lesson

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.pyquest.data.ArrangeExercise
import app.pyquest.data.ChoiceExercise
import app.pyquest.data.CodeExercise
import app.pyquest.data.FillBlankExercise
import app.pyquest.data.Game
import app.pyquest.data.MatchExercise
import app.pyquest.data.SessionMode
import app.pyquest.data.TrueFalseExercise
import app.pyquest.data.TypeExercise
import app.pyquest.i18n.tr
import app.pyquest.python.ConsoleChunk
import app.pyquest.python.RunResult
import app.pyquest.python.normalizeOutput
import app.pyquest.ui.code.CodeBlock
import app.pyquest.ui.code.codeTextStyle
import app.pyquest.ui.components.BitProgress
import app.pyquest.ui.components.ChunkyButton
import app.pyquest.ui.components.ConfettiBurst
import app.pyquest.ui.components.LocalRunner
import app.pyquest.ui.components.MarkdownText
import app.pyquest.ui.components.Monty
import app.pyquest.ui.components.MontyMood
import app.pyquest.ui.components.SpeechBubble
import app.pyquest.ui.components.TheoryBlocks
import app.pyquest.ui.components.rememberFeedback
import app.pyquest.ui.home.cardColor
import app.pyquest.ui.theme.FormWidth
import app.pyquest.ui.theme.PyTheme
import app.pyquest.ui.theme.ReadingWidth
import app.pyquest.ui.theme.centeredMaxWidth
import app.pyquest.ui.theme.darken
import app.pyquest.ui.theme.lighten
import app.pyquest.ui.theme.onColor
import app.pyquest.ui.theme.unitColors

@Composable
fun LessonScreen(
    vm: LessonViewModel,
    onClose: () -> Unit,
    onFinished: () -> Unit,
    onGoPractice: () -> Unit,
) {
    val c = PyTheme.colors
    var confirmQuit by remember { mutableStateOf(false) }

    BackHandler {
        if (vm.phase == LessonPhase.EXERCISE) confirmQuit = true else onClose()
    }

    LaunchedEffect(vm.phase) {
        if (vm.phase == LessonPhase.FINISHED) onFinished()
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(c.background)
    ) {
        when (vm.phase) {
            LessonPhase.LOADING, LessonPhase.FINISHING, LessonPhase.FINISHED -> CenterMonty(
                if (vm.phase == LessonPhase.LOADING) tr("Ich bereite die Aufgaben vor …", "Getting your exercises ready …")
                else tr("Gleich geschafft …", "Almost there …")
            )
            LessonPhase.EMPTY -> EmptySession(onClose)
            LessonPhase.THEORY -> TheoryPhase(vm, onClose)
            LessonPhase.EXERCISE -> ExercisePhase(vm, onQuit = { confirmQuit = true })
            LessonPhase.OUT_OF_HEARTS -> OutOfHearts(vm, onClose, onGoPractice)
        }
    }

    if (confirmQuit) {
        AlertDialog(
            onDismissRequest = { confirmQuit = false },
            icon = { Monty(size = 72.dp, mood = MontyMood.SAD) },
            title = { Text(tr("Wirklich aufhören?", "Quit for real?"), textAlign = TextAlign.Center) },
            text = {
                Text(
                    tr(
                        "Wenn du jetzt gehst, ist der Fortschritt in dieser Lektion weg.",
                        "If you leave now, your progress in this lesson is lost.",
                    ),
                    textAlign = TextAlign.Center,
                )
            },
            confirmButton = {
                TextButton(onClick = { confirmQuit = false }) { Text(tr("Weiterlernen", "Keep learning")) }
            },
            dismissButton = {
                TextButton(onClick = {
                    confirmQuit = false
                    onClose()
                }) { Text(tr("Beenden", "Quit"), color = c.error) }
            },
            containerColor = c.surface,
        )
    }
}

@Composable
private fun CenterMonty(text: String) {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Monty(size = 120.dp, mood = MontyMood.THINKING)
        Spacer(Modifier.height(16.dp))
        Text(text, style = MaterialTheme.typography.titleMedium, color = PyTheme.colors.textMuted)
    }
}

@Composable
private fun EmptySession(onClose: () -> Unit) {
    Column(
        Modifier
            .fillMaxHeight()
            .centeredMaxWidth(FormWidth)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Monty(size = 130.dp, mood = MontyMood.HAPPY)
        Spacer(Modifier.height(16.dp))
        Text(
            tr("Hier gibt es gerade nichts zu üben", "Nothing to practise here yet"),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            tr(
                "Schließe zuerst ein paar Lektionen ab – dann stelle ich dir hier passende Aufgaben zusammen.",
                "Finish a few lessons first – then I'll put together matching exercises for you here.",
            ),
            style = MaterialTheme.typography.bodyLarge,
            color = PyTheme.colors.textMuted,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(24.dp))
        ChunkyButton(tr("Zurück", "Back"), onClick = onClose, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun TheoryPhase(vm: LessonViewModel, onClose: () -> Unit) {
    val c = PyTheme.colors
    val lesson = vm.lesson ?: return
    val unit = vm.unit
    val (unitColor, _) = unitColors(unit?.colorIndex ?: 0)
    val lessonNumber = (unit?.lessons?.indexOfFirst { it.id == lesson.id } ?: 0) + 1
    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Row(
            Modifier
                .centeredMaxWidth(ReadingWidth)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onClose) { Icon(Icons.Rounded.Close, tr("Schließen", "Close"), tint = c.textMuted) }
            Spacer(Modifier.weight(1f))
            Text(tr("Lernkarte", "Lesson notes"), style = MaterialTheme.typography.labelLarge, color = c.textMuted)
            Spacer(Modifier.width(16.dp))
        }
        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                Modifier
                    .centeredMaxWidth(ReadingWidth)
                    .padding(horizontal = 20.dp)
            ) {
                val unitLabel = if (c.isDark) unitColor.lighten(0.25f) else unitColor.darken(0.85f)
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            tr("Einheit ${unit?.number ?: 0} · Lektion $lessonNumber", "Unit ${unit?.number ?: 0} · Lesson $lessonNumber"),
                            style = MaterialTheme.typography.labelMedium,
                            color = unitLabel,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(lesson.title, style = MaterialTheme.typography.headlineSmall, color = c.text)
                        if (lesson.subtitle.isNotEmpty()) {
                            Spacer(Modifier.height(4.dp))
                            Text(lesson.subtitle, style = MaterialTheme.typography.bodyMedium, color = c.textMuted)
                        }
                    }
                    Monty(Modifier.padding(start = 8.dp), size = 76.dp, mood = MontyMood.TALKING)
                }
                Spacer(Modifier.height(14.dp))
                HorizontalDivider(thickness = 1.dp, color = c.border)
                Spacer(Modifier.height(18.dp))
                TheoryBlocks(lesson.theory)
                Spacer(Modifier.height(24.dp))
            }
        }
        Box(
            Modifier
                .centeredMaxWidth(ReadingWidth)
                .background(c.background)
                .padding(16.dp)
        ) {
            ChunkyButton(tr("Los geht's!", "Let's go!"), onClick = { vm.startExercises() }, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun ExercisePhase(vm: LessonViewModel, onQuit: () -> Unit) {
    val c = PyTheme.colors
    val python = LocalRunner.current
    val feedbackFx = rememberFeedback()
    val inst = vm.current ?: return
    val fb = vm.feedback
    var correctBurst by remember { mutableIntStateOf(0) }
    val unavailable = tr("Python ist nicht verfügbar.", "Python is not available.")

    LaunchedEffect(fb) {
        if (fb != null) {
            if (fb.correct) {
                feedbackFx.correct()
                correctBurst++
                if (vm.combo == 5 || vm.combo == 10 || vm.combo == 15) feedbackFx.combo()
            } else if (!fb.revealed) {
                feedbackFx.wrong()
            }
        }
    }

    val runForCheck: suspend (String, List<String>) -> RunResult = { code, stdin ->
        python?.run(code, stdin, timeoutSeconds = 6.0) ?: RunResult.failure(unavailable)
    }
    val runForTest: suspend (String, List<String>, (ConsoleChunk) -> Unit) -> RunResult = { code, stdin, sink ->
        python?.run(code, stdin, timeoutSeconds = 6.0, onOutput = sink) ?: RunResult.failure(unavailable)
    }

    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
        ) {
            // Kopfzeile: Schließen, Fortschritt Bit für Bit, Herzen
            Row(
                Modifier
                    .centeredMaxWidth(ReadingWidth)
                    .padding(start = 6.dp, end = 16.dp, top = 6.dp, bottom = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onQuit) { Icon(Icons.Rounded.Close, tr("Beenden", "Quit"), tint = c.textMuted) }
                val comboHot = vm.combo >= 5
                BitProgress(
                    done = vm.solved,
                    total = vm.total,
                    color = if (comboHot) c.streak else c.success,
                    modifier = Modifier.weight(1f),
                )
                Spacer(Modifier.width(12.dp))
                if (vm.mode == SessionMode.LESSON && vm.lesson?.theory?.isNotEmpty() == true) {
                    IconButton(onClick = { vm.showTheoryAgain() }, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.AutoMirrored.Rounded.MenuBook, tr("Lernkarte", "Lesson notes"), tint = c.textMuted, modifier = Modifier.size(22.dp))
                    }
                    Spacer(Modifier.width(6.dp))
                }
                Icon(
                    Icons.Rounded.Favorite,
                    null,
                    tint = if (vm.costsHearts) c.heart else c.heart.copy(alpha = 0.5f),
                    modifier = Modifier.size(22.dp),
                )
                Spacer(Modifier.width(4.dp))
                AnimatedContent(targetState = if (vm.costsHearts) vm.hearts.toString() else "∞", label = "hearts") { h ->
                    Text(h, style = MaterialTheme.typography.titleMedium, color = c.heart)
                }
            }

            // Combo-Anzeige
            AnimatedVisibility(
                visible = vm.combo >= 3 && fb?.correct != false,
                enter = scaleIn(spring(dampingRatio = 0.5f)) + fadeIn(),
                exit = scaleOut() + fadeOut(),
                modifier = Modifier.align(Alignment.CenterHorizontally),
            ) {
                Row(
                    Modifier
                        .padding(top = 4.dp)
                        .clip(CircleShape)
                        .background(c.streak.copy(alpha = 0.12f))
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Rounded.LocalFireDepartment, null, tint = c.streak, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(tr("${vm.combo} in Folge", "${vm.combo} in a row"), style = MaterialTheme.typography.labelMedium, color = c.streak)
                }
            }

            // Aufgabe
            Box(Modifier.weight(1f)) {
                AnimatedContent(
                    targetState = inst,
                    transitionSpec = {
                        (slideInHorizontally(spring(stiffness = Spring.StiffnessMediumLow)) { it / 2 } + fadeIn(tween(220)))
                            .togetherWith(slideOutHorizontally(tween(200)) { -it / 3 } + fadeOut(tween(160)))
                    },
                    contentKey = { it.key },
                    label = "exercise",
                ) { current ->
                    val ui = ExerciseUiState(locked = fb != null, correct = fb?.correct)
                    Column(
                        Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Column(
                            Modifier
                                .centeredMaxWidth(ReadingWidth)
                                .padding(horizontal = 20.dp, vertical = 12.dp)
                        ) {
                            if (current.retry) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Rounded.Replay, null, tint = c.streak, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text(tr("Nochmal üben", "One more try"), style = MaterialTheme.typography.labelMedium, color = c.streak)
                                }
                                Spacer(Modifier.height(6.dp))
                            }
                            val onAnswer: (Answer?) -> Unit = { vm.answer = it }
                            when (val e = current.exercise) {
                                is ChoiceExercise -> ChoiceView(current, e, ui, onAnswer)
                                is TrueFalseExercise -> TrueFalseView(current, e, ui, onAnswer)
                                is FillBlankExercise -> FillBlankView(current, e, ui, vm.wrongPulse, onAnswer)
                                is ArrangeExercise -> ArrangeView(current, e, ui, vm.wrongPulse, onAnswer)
                                is TypeExercise -> TypeView(current, e, ui, onAnswer)
                                is MatchExercise -> MatchView(current, e, ui, onAnswer, onAutoCheck = { vm.check(runForCheck) })
                                is CodeExercise -> CodeTaskView(current, e, ui, runForTest, onAnswer, lastCheck = fb?.run)
                            }
                            Spacer(Modifier.height(24.dp))
                        }
                    }
                }
            }

            // Unten: Prüfen-Knopf oder Rückmeldung
            AnimatedContent(
                targetState = fb,
                transitionSpec = {
                    (slideInVertically(spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMediumLow)) { it } + fadeIn())
                        .togetherWith(fadeOut(tween(120)))
                },
                label = "bottom",
            ) { state ->
                if (state == null) {
                    Column(
                        Modifier
                            .centeredMaxWidth(ReadingWidth)
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        ChunkyButton(
                            text = tr("Prüfen", "Check"),
                            onClick = { vm.check(runForCheck) },
                            enabled = vm.answer != null,
                            loading = vm.checking,
                            sound = false,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                } else {
                    FeedbackPanel(
                        state = state,
                        costsHeart = vm.costsHearts,
                        onContinue = { vm.next() },
                        onRetry = { vm.retry() },
                        onReveal = { vm.reveal() },
                    )
                }
            }
        }

        // Kleiner Bit-Regen bei jeder richtigen Antwort, mehr bei Combo-Meilensteinen
        if (correctBurst > 0 && fb?.correct == true) {
            val milestone = vm.combo == 5 || vm.combo == 10 || vm.combo == 15
            ConfettiBurst(
                trigger = correctBurst,
                count = if (milestone) 90 else 26,
                durationMs = if (milestone) 2000 else 1100,
                originY = 0.82f,
                spread = if (milestone) 1f else 0.55f,
            )
        }
    }
}

@Composable
private fun FeedbackPanel(
    state: FeedbackState,
    costsHeart: Boolean,
    onContinue: () -> Unit,
    onRetry: () -> Unit,
    onReveal: () -> Unit,
) {
    val c = PyTheme.colors
    val soft = if (state.correct) c.successSoft else c.errorSoft
    val fg = if (state.correct) c.onSuccessSoft else c.onErrorSoft
    val main = if (state.correct) c.success else c.error
    val mainDark = if (state.correct) c.successDark else c.errorDark
    val shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    Box(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(soft)
    ) {
        Column(
            Modifier
                .centeredMaxWidth(ReadingWidth)
                .heightIn(max = 470.dp)
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 18.dp, end = 18.dp, top = 16.dp, bottom = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(main),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        if (state.correct) Icons.Rounded.Check else Icons.Rounded.Close,
                        null,
                        tint = main.onColor(),
                        modifier = Modifier.size(20.dp),
                    )
                }
                Spacer(Modifier.width(10.dp))
                Text(state.title, style = MaterialTheme.typography.titleLarge, color = fg, modifier = Modifier.weight(1f))
            }
            Column(
                Modifier
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp)
            ) {
                if (!state.correct && state.run != null && !state.revealed) {
                    Spacer(Modifier.height(12.dp))
                    CodeResultDiff(state)
                }
                if (!state.correct && state.solution != null && (state.revealed || !state.canRetry)) {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        if (state.revealed) tr("Lösung:", "Solution:") else tr("Richtige Lösung:", "Correct answer:"),
                        style = MaterialTheme.typography.labelLarge,
                        color = c.textMuted,
                    )
                    Spacer(Modifier.height(6.dp))
                    if (state.solutionIsCode) {
                        CodeBlock(state.solution, modifier = Modifier.fillMaxWidth())
                    } else {
                        MarkdownText(state.solution, color = c.text, style = MaterialTheme.typography.bodyLarge)
                    }
                }
                if (state.explanation != null && (state.correct || !state.canRetry || state.revealed)) {
                    Spacer(Modifier.height(12.dp))
                    MarkdownText(state.explanation, color = c.text, style = MaterialTheme.typography.bodyMedium)
                }
                if (state.canRetry && state.explanation != null) {
                    Spacer(Modifier.height(12.dp))
                    MarkdownText(tr("**Tipp:** ", "**Tip:** ") + state.explanation, color = c.text, style = MaterialTheme.typography.bodyMedium)
                }
            }
            Column(Modifier.padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 14.dp)) {
                if (state.canRetry) {
                    ChunkyButton(
                        tr("Nochmal versuchen", "Try again"),
                        onClick = onRetry,
                        color = main,
                        shadow = mainDark,
                        modifier = Modifier.fillMaxWidth(),
                        sound = false,
                    )
                    Spacer(Modifier.height(4.dp))
                    TextButton(onClick = onReveal, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        Text(
                            if (costsHeart) tr("Lösung ansehen (kostet ein Herz)", "Show solution (costs a heart)")
                            else tr("Lösung ansehen", "Show solution"),
                            color = c.textMuted,
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                } else {
                    ChunkyButton(
                        tr("Weiter", "Continue"),
                        onClick = onContinue,
                        color = main,
                        shadow = mainDark,
                        modifier = Modifier.fillMaxWidth(),
                        sound = false,
                    )
                }
            }
        }
    }
}

@Composable
private fun CodeResultDiff(state: FeedbackState) {
    val c = PyTheme.colors
    val run = state.run ?: return
    if (run.error != null) {
        if (run.hint != null) {
            Text(tr("Was bedeutet der Fehler?", "What does the error mean?"), style = MaterialTheme.typography.labelLarge, color = c.textMuted)
            Spacer(Modifier.height(4.dp))
            MarkdownText(run.hint, color = c.text, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(6.dp))
        }
        Box(
            Modifier
                .fillMaxWidth()
                .clip(PyTheme.shapes.smallShape)
                .background(PyTheme.code.theme.background)
                .padding(10.dp)
        ) {
            Text(run.error.lines().takeLast(3).joinToString("\n"), style = codeTextStyle(0.9f), color = c.onErrorSoft)
        }
        return
    }
    if (state.expectedOutput != null && normalizeOutput(run.stdout) != normalizeOutput(state.expectedOutput)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutputBox(tr("Deine Ausgabe", "Your output"), run.stdout.ifBlank { tr("(leer)", "(empty)") }, Modifier.weight(1f))
            OutputBox(tr("Erwartet", "Expected"), state.expectedOutput, Modifier.weight(1f))
        }
    }
}

@Composable
private fun OutputBox(label: String, text: String, modifier: Modifier) {
    val c = PyTheme.colors
    val theme = PyTheme.code.theme
    Column(modifier) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = c.textMuted)
        Spacer(Modifier.height(4.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .clip(PyTheme.shapes.smallShape)
                .background(theme.background)
                .padding(8.dp)
        ) {
            Text(text.trimEnd('\n'), style = codeTextStyle(0.85f), color = theme.text)
        }
    }
}

@Composable
private fun OutOfHearts(vm: LessonViewModel, onClose: () -> Unit, onGoPractice: () -> Unit) {
    val c = PyTheme.colors
    var failed by remember { mutableStateOf(false) }
    Column(
        Modifier
            .fillMaxHeight()
            .centeredMaxWidth(FormWidth)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Monty(size = 140.dp, mood = MontyMood.SAD)
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            repeat(Game.MAX_HEARTS) {
                Icon(Icons.Rounded.Favorite, null, tint = c.locked, modifier = Modifier.size(28.dp))
            }
        }
        Spacer(Modifier.height(16.dp))
        Text(tr("Keine Herzen mehr!", "Out of hearts!"), style = MaterialTheme.typography.headlineSmall, color = c.text)
        Spacer(Modifier.height(8.dp))
        Text(
            tr(
                "Herzen füllen sich mit der Zeit wieder auf (eins alle 20 Minuten). Du kannst auch üben, um Herzen zu verdienen.",
                "Hearts refill over time (one every 20 minutes). You can also practise to earn hearts.",
            ),
            style = MaterialTheme.typography.bodyLarge,
            color = c.textMuted,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(28.dp))
        ChunkyButton(
            tr("Auffüllen für ${Game.PRICE_REFILL} Bits", "Refill for ${Game.PRICE_REFILL} bits"),
            onClick = { vm.refillWithGems { ok -> failed = !ok } },
            enabled = vm.gems >= Game.PRICE_REFILL,
            color = c.gem,
            shadow = c.gemDark,
            modifier = Modifier.fillMaxWidth(),
        )
        if (failed) {
            Spacer(Modifier.height(6.dp))
            Text(tr("Nicht genug Bits.", "Not enough bits."), color = c.error, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(Modifier.height(12.dp))
        ChunkyButton(
            tr("Üben, um Herzen zu verdienen", "Practise to earn hearts"),
            onClick = onGoPractice,
            outlined = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(12.dp))
        TextButton(onClick = onClose) { Text(tr("Lektion beenden", "End lesson"), color = c.textMuted) }
        Spacer(Modifier.height(8.dp))
        SpeechBubble(tr("Du hast gerade ${vm.gems} Bits.", "You have ${vm.gems} bits right now."))
    }
}
