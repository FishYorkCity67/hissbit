package app.pyquest.ui.onboarding

import android.Manifest
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.LocalCafe
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.RocketLaunch
import androidx.compose.material.icons.rounded.SignalCellularAlt
import androidx.compose.material.icons.rounded.SignalCellularAlt1Bar
import androidx.compose.material.icons.rounded.SignalCellularAlt2Bar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.pyquest.data.Accent
import app.pyquest.data.AppSettings
import app.pyquest.data.CourseId
import app.pyquest.data.ThemeMode
import app.pyquest.i18n.AppLanguage
import app.pyquest.i18n.LanguagePref
import app.pyquest.i18n.LocalLanguage
import app.pyquest.i18n.tr
import app.pyquest.notify.Reminders
import app.pyquest.ui.code.CodeBlock
import app.pyquest.ui.components.BitProgress
import app.pyquest.ui.components.ChunkyButton
import app.pyquest.ui.components.ChunkySurface
import app.pyquest.ui.components.HissbitWordmark
import app.pyquest.ui.components.LanguageBadge
import app.pyquest.ui.components.Monty
import app.pyquest.ui.components.MontyMood
import app.pyquest.ui.components.SpeechBubble
import app.pyquest.ui.components.rememberFeedback
import app.pyquest.ui.home.cardColor
import app.pyquest.ui.theme.FormWidth
import app.pyquest.ui.theme.PyQuestTheme
import app.pyquest.ui.theme.PyTheme
import app.pyquest.ui.theme.accentColors
import app.pyquest.ui.theme.centeredMaxWidth
import app.pyquest.ui.theme.onColor

private const val STEPS = 7

@Composable
fun OnboardingScreen(
    initial: AppSettings,
    courses: List<CourseId>,
    onDone: (name: String, course: CourseId, startUnit: Int, settings: AppSettings) -> Unit,
) {
    var step by rememberSaveable { mutableIntStateOf(0) }
    var name by rememberSaveable { mutableStateOf("") }
    var course by rememberSaveable { mutableStateOf(CourseId.PYTHON) }
    var startUnit by rememberSaveable { mutableIntStateOf(1) }
    var draft by remember { mutableStateOf(initial) }
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        onDone(name, course, startUnit, draft.copy(reminderEnabled = granted))
    }

    fun finish(withReminder: Boolean) {
        if (withReminder && Build.VERSION.SDK_INT >= 33 && !Reminders.hasPermission(context)) {
            draft = draft.copy(reminderEnabled = true)
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            onDone(name, course, startUnit, draft.copy(reminderEnabled = withReminder))
        }
    }

    BackHandler(enabled = step > 0) { step-- }

    PyQuestTheme(draft) {
        val c = PyTheme.colors
        Box(
            Modifier
                .fillMaxSize()
                .background(c.background)
        ) {
            Column(
                Modifier
                    .fillMaxHeight()
                    .centeredMaxWidth(FormWidth)
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .imePadding()
            ) {
                if (step > 0) {
                    Row(Modifier.padding(horizontal = 4.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { step-- }) { Icon(Icons.AutoMirrored.Rounded.ArrowBack, tr("Zurück", "Back"), tint = c.textMuted) }
                        BitProgress(step, STEPS - 1, modifier = Modifier.weight(1f).padding(end = 16.dp), color = c.primary)
                    }
                }
                AnimatedContent(
                    targetState = step,
                    transitionSpec = {
                        val forward = targetState > initialState
                        (slideInHorizontally(tween(320)) { if (forward) it / 2 else -it / 2 } + fadeIn(tween(250)))
                            .togetherWith(slideOutHorizontally(tween(250)) { if (forward) -it / 3 else it / 3 } + fadeOut(tween(200)))
                    },
                    modifier = Modifier.weight(1f),
                    label = "onboarding",
                ) { s ->
                    when (s) {
                        0 -> WelcomeStep(draft, { draft = it }) { step = 1 }
                        1 -> NameStep(name, { name = it }) { step = 2 }
                        2 -> CourseStep(courses, course, { course = it; startUnit = 1 }) { step = 3 }
                        3 -> ExperienceStep(course, startUnit, { startUnit = it }) { step = 4 }
                        4 -> GoalStep(draft.dailyGoal, { draft = draft.copy(dailyGoal = it) }) { step = 5 }
                        5 -> LookStep(draft, course, { draft = it }) { step = 6 }
                        else -> ReminderStep(draft, { draft = it }, onYes = { finish(true) }, onNo = { finish(false) })
                    }
                }
            }
        }
    }
}

@Composable
private fun StepScaffold(
    mood: MontyMood,
    bubble: String,
    button: String,
    buttonEnabled: Boolean = true,
    onNext: () -> Unit,
    secondary: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Column(Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Monty(size = 92.dp, mood = mood)
                Spacer(Modifier.width(8.dp))
                SpeechBubble(bubble, modifier = Modifier.weight(1f).padding(bottom = 24.dp))
            }
            Spacer(Modifier.height(20.dp))
            content()
            Spacer(Modifier.height(20.dp))
        }
        ChunkyButton(button, onClick = onNext, enabled = buttonEnabled, modifier = Modifier.fillMaxWidth())
        if (secondary != null) secondary() else Spacer(Modifier.height(12.dp))
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun WelcomeStep(draft: AppSettings, onChange: (AppSettings) -> Unit, onNext: () -> Unit) {
    val c = PyTheme.colors
    val lang = LocalLanguage.current
    val feedback = rememberFeedback()
    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Sprache gleich am Anfang wählbar
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            listOf(AppLanguage.DE to "Deutsch", AppLanguage.EN to "English").forEach { (l, label) ->
                val sel = lang == l
                Box(
                    Modifier
                        .padding(start = 8.dp)
                        .clip(CircleShape)
                        .background(if (sel) c.primarySoft else cardColor())
                        .border(if (sel) 2.dp else 1.dp, if (sel) c.primary else c.border, CircleShape)
                        .clickable {
                            feedback.select()
                            onChange(draft.copy(language = if (l == AppLanguage.EN) LanguagePref.EN else LanguagePref.DE))
                        }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    Text(label, style = MaterialTheme.typography.labelLarge, color = if (sel) c.primaryText else c.text)
                }
            }
        }
        Spacer(Modifier.weight(1f))
        Monty(size = 180.dp, mood = MontyMood.TALKING)
        Spacer(Modifier.height(18.dp))
        HissbitWordmark(logoSize = 40.dp)
        Spacer(Modifier.height(10.dp))
        Text(
            tr(
                "Hi, ich bin Monty!\nIch bringe dir Programmieren bei – Bit für Bit, jeden Tag ein bisschen.",
                "Hi, I'm Monty!\nI'll teach you to code – bit by bit, a little every day.",
            ),
            style = MaterialTheme.typography.titleMedium.copy(lineHeight = 26.sp),
            color = c.textMuted,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.weight(1.2f))
        ChunkyButton(tr("Los geht's", "Let's go"), onClick = onNext, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(10.dp))
        Text(
            tr("Komplett offline · echter Code · kein Konto nötig", "Fully offline · real code · no account needed"),
            style = MaterialTheme.typography.bodySmall,
            color = c.textMuted,
        )
    }
}

@Composable
private fun NameStep(name: String, onName: (String) -> Unit, onNext: () -> Unit) {
    val c = PyTheme.colors
    StepScaffold(
        MontyMood.HAPPY,
        tr("Wie darf ich dich nennen?", "What should I call you?"),
        if (name.isBlank()) tr("Überspringen", "Skip") else tr("Weiter", "Continue"),
        onNext = onNext,
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { onName(it.take(30)) },
            placeholder = { Text(tr("Dein Name", "Your name")) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Done),
            shape = PyTheme.shapes.mediumShape,
            textStyle = MaterialTheme.typography.titleMedium,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = cardColor(),
                unfocusedContainerColor = cardColor(),
                unfocusedBorderColor = c.border,
            ),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun OptionCard(selected: Boolean, icon: ImageVector?, title: String, subtitle: String, leading: (@Composable () -> Unit)? = null, onClick: () -> Unit) {
    val c = PyTheme.colors
    val feedback = rememberFeedback()
    ChunkySurface(
        onClick = {
            feedback.select()
            onClick()
        },
        face = if (selected) c.primarySoft else cardColor(),
        edge = if (selected) c.primary else c.border,
        borderWidth = if (selected) 2.dp else 1.dp,
        shape = PyTheme.shapes.mediumShape,
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterStart,
        contentPadding = PaddingValues(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (leading != null) {
                leading()
            } else if (icon != null) {
                Box(
                    Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (selected) c.primary.copy(alpha = 0.16f) else c.surfaceAlt),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(icon, null, tint = c.primaryText, modifier = Modifier.size(22.dp))
                }
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, color = if (selected) c.primaryText else c.text)
                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = c.textMuted)
            }
            if (selected) Icon(Icons.Rounded.Check, null, tint = c.primaryText)
        }
    }
}

@Composable
private fun CourseStep(courses: List<CourseId>, selected: CourseId, onSelect: (CourseId) -> Unit, onNext: () -> Unit) {
    StepScaffold(
        MontyMood.THINKING,
        tr("Womit möchtest du anfangen? Den anderen Kurs kannst du jederzeit dazunehmen.", "Where do you want to start? You can add the other course any time."),
        tr("Weiter", "Continue"),
        onNext = onNext,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            courses.forEach { id ->
                when (id) {
                    CourseId.PYTHON -> OptionCard(
                        selected == id, null, "Python",
                        tr("Ideal zum Einstieg: klare Sprache, vielseitig – von Spielen bis Daten.", "Great for beginners: clear syntax, from games to data."),
                        leading = { LanguageBadge(id, size = 40.dp) },
                    ) { onSelect(id) }
                    CourseId.JAVASCRIPT -> OptionCard(
                        selected == id, null, "JavaScript",
                        tr("Die Sprache des Webs – läuft in jedem Browser.", "The language of the web – runs in every browser."),
                        leading = { LanguageBadge(id, size = 40.dp) },
                    ) { onSelect(id) }
                }
            }
        }
    }
}

@Composable
private fun ExperienceStep(course: CourseId, startUnit: Int, onSelect: (Int) -> Unit, onNext: () -> Unit) {
    val js = course == CourseId.JAVASCRIPT
    StepScaffold(
        MontyMood.THINKING,
        tr("Wie viel ${course.displayName} kannst du schon?", "How much ${course.displayName} do you know already?"),
        tr("Weiter", "Continue"),
        onNext = onNext,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OptionCard(startUnit == 1, Icons.Rounded.SignalCellularAlt1Bar, tr("Ich bin ganz neu", "I'm completely new"), tr("Wir starten ganz von vorne – perfekt!", "We start from scratch – perfect!")) { onSelect(1) }
            OptionCard(
                startUnit == 4, Icons.Rounded.SignalCellularAlt2Bar, tr("Ich kenne die Basics", "I know the basics"),
                if (js) tr("console.log, Variablen und Strings sitzen schon", "console.log, variables and strings are fine")
                else tr("print, Variablen und Strings sitzen schon", "print, variables and strings are fine"),
            ) { onSelect(4) }
            OptionCard(
                startUnit == 7, Icons.Rounded.SignalCellularAlt, tr("Ich kann schon einiges", "I know quite a bit"),
                tr("if, Schleifen und Listen kenne ich", "I know if, loops and lists"),
            ) { onSelect(7) }
        }
        Spacer(Modifier.height(12.dp))
        Text(
            tr(
                "Übersprungene Einheiten bleiben freigeschaltet – du kannst jederzeit zurückgehen.",
                "Skipped units stay unlocked – you can go back any time.",
            ),
            style = MaterialTheme.typography.bodySmall,
            color = PyTheme.colors.textMuted,
        )
    }
}

@Composable
private fun GoalStep(goal: Int, onSelect: (Int) -> Unit, onNext: () -> Unit) {
    StepScaffold(MontyMood.HAPPY, tr("Wie viel möchtest du am Tag lernen?", "How much do you want to learn per day?"), tr("Weiter", "Continue"), onNext = onNext) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OptionCard(goal == 10, Icons.Rounded.LocalCafe, tr("Locker · 10 XP", "Casual · 10 XP"), tr("ca. 5 Minuten am Tag", "about 5 minutes a day")) { onSelect(10) }
            OptionCard(goal == 20, Icons.AutoMirrored.Rounded.MenuBook, tr("Normal · 20 XP", "Regular · 20 XP"), tr("ca. 10 Minuten am Tag", "about 10 minutes a day")) { onSelect(20) }
            OptionCard(goal == 30, Icons.Rounded.LocalFireDepartment, tr("Ernsthaft · 30 XP", "Serious · 30 XP"), tr("ca. 15 Minuten am Tag", "about 15 minutes a day")) { onSelect(30) }
            OptionCard(goal == 50, Icons.Rounded.RocketLaunch, tr("Intensiv · 50 XP", "Intense · 50 XP"), tr("20 Minuten oder mehr", "20 minutes or more")) { onSelect(50) }
        }
    }
}

@Composable
private fun LookStep(draft: AppSettings, course: CourseId, onChange: (AppSettings) -> Unit, onNext: () -> Unit) {
    val c = PyTheme.colors
    val lang = LocalLanguage.current
    val feedback = rememberFeedback()
    StepScaffold(
        MontyMood.HAPPY,
        tr("Mach Hissbit zu **deiner** App! Das kannst du später jederzeit ändern.", "Make Hissbit **yours**! You can change this any time later."),
        tr("Weiter", "Continue"),
        onNext = onNext,
    ) {
        Text(tr("Design", "Theme"), style = MaterialTheme.typography.titleSmall, color = c.text)
        Spacer(Modifier.height(8.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ThemeMode.entries.forEach { m ->
                val sel = draft.themeMode == m
                Box(
                    Modifier
                        .clip(CircleShape)
                        .background(if (sel) c.primarySoft else cardColor())
                        .border(if (sel) 2.dp else 1.dp, if (sel) c.primary else c.border, CircleShape)
                        .clickable {
                            feedback.select()
                            onChange(draft.copy(themeMode = m))
                        }
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(m.label.of(lang), style = MaterialTheme.typography.labelLarge, color = if (sel) c.primaryText else c.text)
                }
            }
        }
        Spacer(Modifier.height(18.dp))
        Text(tr("Farbe", "Colour"), style = MaterialTheme.typography.titleSmall, color = c.text)
        Spacer(Modifier.height(8.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Accent.entries.filter { it != Accent.DYNAMIC || Build.VERSION.SDK_INT >= 31 }.forEach { a ->
                val sel = draft.accent == a
                Box(
                    Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .border(3.dp, if (sel) c.text else Color.Transparent, CircleShape)
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(accentColors(a, c.isDark).primary)
                        .clickable {
                            feedback.select()
                            onChange(draft.copy(accent = a))
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    if (sel) Icon(Icons.Rounded.Check, null, tint = accentColors(a, c.isDark).primary.onColor())
                    else if (a == Accent.DYNAMIC) Icon(Icons.Rounded.Palette, null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        }
        Spacer(Modifier.height(18.dp))
        Text(tr("Vorschau", "Preview"), style = MaterialTheme.typography.titleSmall, color = c.text)
        Spacer(Modifier.height(8.dp))
        if (course == CourseId.JAVASCRIPT) {
            CodeBlock(
                tr("const name = \"Monty\";\nconsole.log(`Hallo \${name}!`);", "const name = \"Monty\";\nconsole.log(`Hello \${name}!`);"),
                modifier = Modifier.fillMaxWidth(),
                title = tr("hallo.js", "hello.js"),
            )
        } else {
            CodeBlock(
                tr("name = \"Monty\"\nprint(f\"Hallo {name}!\")", "name = \"Monty\"\nprint(f\"Hello {name}!\")"),
                modifier = Modifier.fillMaxWidth(),
                title = tr("hallo.py", "hello.py"),
            )
        }
        Spacer(Modifier.height(12.dp))
        ChunkyButton(tr("So sieht ein Knopf aus", "This is what a button looks like"), onClick = {}, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun ReminderStep(draft: AppSettings, onChange: (AppSettings) -> Unit, onYes: () -> Unit, onNo: () -> Unit) {
    val c = PyTheme.colors
    val times = listOf(8 to 0, 12 to 30, 16 to 0, 18 to 0, 19 to 30, 21 to 0)
    StepScaffold(
        MontyMood.TALKING,
        tr("Soll ich dich jeden Tag erinnern? So bleibt dein Streak am Leben!", "Shall I remind you every day? That keeps your streak alive!"),
        tr("Ja, erinnere mich", "Yes, remind me"),
        onNext = onYes,
        secondary = {
            TextButton(onClick = onNo, modifier = Modifier.fillMaxWidth()) {
                Text(tr("Nein danke", "No thanks"), color = c.textMuted, style = MaterialTheme.typography.labelLarge)
            }
        },
    ) {
        Text(tr("Wann passt es dir am besten?", "When suits you best?"), style = MaterialTheme.typography.titleSmall, color = c.text)
        Spacer(Modifier.height(10.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            times.forEach { (h, m) ->
                val sel = draft.reminderHour == h && draft.reminderMinute == m
                Box(
                    Modifier
                        .clip(CircleShape)
                        .background(if (sel) c.primarySoft else cardColor())
                        .border(if (sel) 2.dp else 1.dp, if (sel) c.primary else c.border, CircleShape)
                        .clickable { onChange(draft.copy(reminderHour = h, reminderMinute = m)) }
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text("%02d:%02d".format(h, m), style = MaterialTheme.typography.labelLarge, color = if (sel) c.primaryText else c.text)
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Text(
            tr(
                "Die Erinnerung kommt nur, wenn du an dem Tag noch nicht gelernt hast. Du kannst sie in den Einstellungen ändern.",
                "The reminder only comes if you haven't learned yet that day. You can change it in the settings.",
            ),
            style = MaterialTheme.typography.bodySmall,
            color = c.textMuted,
        )
    }
}
