package app.pyquest.ui.playground

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.pyquest.data.CourseId
import app.pyquest.data.ExampleProgram
import app.pyquest.data.Snippet
import app.pyquest.i18n.LocalLanguage
import app.pyquest.i18n.tr
import app.pyquest.ui.AppViewModel
import app.pyquest.ui.code.CodeEditor
import app.pyquest.ui.code.ConsoleView
import app.pyquest.ui.code.SymbolBar
import app.pyquest.ui.code.codeTextStyle
import app.pyquest.ui.components.ChunkyButton
import app.pyquest.ui.components.LanguageBadge
import app.pyquest.ui.components.LocalCourseId
import app.pyquest.ui.components.MarkdownText
import app.pyquest.ui.components.rememberFeedback
import app.pyquest.ui.home.cardColor
import app.pyquest.ui.theme.Adaptive
import app.pyquest.ui.theme.PyTheme

private enum class PlaygroundSheet { EXAMPLES, SNIPPETS }

@Composable
fun PlaygroundScreen(app: AppViewModel, vm: PlaygroundViewModel) {
    val c = PyTheme.colors
    val theme = PyTheme.code.theme
    val lang = LocalLanguage.current
    val progress by app.progress.collectAsState()
    val request by app.playgroundRequest.collectAsState()
    val console by vm.console.collectAsState()
    val waiting by vm.input.waiting.collectAsState()
    val version by vm.runner.version.collectAsState()
    val feedback = rememberFeedback()
    val window = Adaptive.window
    var showConsole by rememberSaveable { mutableStateOf(false) }
    var sheet by remember { mutableStateOf<PlaygroundSheet?>(null) }
    var saveDialog by remember { mutableStateOf(false) }
    val languages = app.playgroundLanguages

    LaunchedEffect(progress != null) {
        progress?.let { p ->
            val l = CourseId.byKey(p.playgroundLanguage).takeIf { it in languages } ?: CourseId.PYTHON
            vm.load(app.playgroundCodeFor(p, l), l)
        }
    }
    LaunchedEffect(request) {
        request?.let { (code, l) ->
            progress?.let { p -> vm.load(app.playgroundCodeFor(p, l), l) }
            if (l != vm.language) vm.switchLanguage(l, code)
            vm.replaceCode(code)
            app.playgroundRequest.value = null
        }
    }
    LaunchedEffect(vm.code.text, vm.language) {
        if (progress != null) app.savePlaygroundCode(vm.language, vm.code.text)
    }
    LaunchedEffect(waiting) { if (waiting != null) showConsole = true }
    // Laufzeit schon starten, während man noch tippt – dann ist der erste Lauf sofort da.
    LaunchedEffect(vm.language) { vm.runner.warmUp() }

    val imeVisible = WindowInsets.isImeVisible
    val consoleVisible = showConsole && (!imeVisible || waiting != null)

    CompositionLocalProvider(LocalCourseId provides vm.language) {
        Column(
            Modifier
                .fillMaxSize()
                .background(c.background)
                .statusBarsPadding()
                .imePadding()
        ) {
            // Kopfzeile
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 4.dp, top = 6.dp, bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(tr("Code-Spielplatz", "Code playground"), style = MaterialTheme.typography.titleLarge, color = c.text)
                    Text(
                        "${vm.language.displayName} ${version ?: tr("wird gestartet …", "starting …")} · " + tr("komplett offline", "fully offline"),
                        style = MaterialTheme.typography.bodySmall,
                        color = c.textMuted,
                    )
                }
                IconButton(onClick = { sheet = PlaygroundSheet.EXAMPLES }) {
                    Icon(Icons.AutoMirrored.Rounded.MenuBook, tr("Beispiele", "Examples"), tint = c.primaryText)
                }
                IconButton(onClick = { sheet = PlaygroundSheet.SNIPPETS }) {
                    Icon(Icons.Rounded.FolderOpen, tr("Meine Programme", "My programs"), tint = c.primaryText)
                }
                IconButton(onClick = { saveDialog = true }) {
                    Icon(Icons.Rounded.Save, tr("Speichern", "Save"), tint = c.primaryText)
                }
            }
            if (languages.size > 1) {
                LanguageSwitch(languages, vm.language, Modifier.padding(start = 12.dp, end = 12.dp, bottom = 8.dp)) { to ->
                    val p = progress ?: return@LanguageSwitch
                    feedback.select()
                    app.savePlaygroundCode(vm.language, vm.code.text)
                    vm.switchLanguage(to, app.playgroundCodeFor(p, to))
                    app.setPlaygroundLanguage(to)
                }
            }

            val editor: @Composable (Modifier) -> Unit = { modifier ->
                Column(
                    modifier
                        .clip(PyTheme.shapes.mediumShape)
                        .background(theme.background)
                        .border(1.dp, c.border, PyTheme.shapes.mediumShape)
                ) {
                    CodeEditor(
                        value = vm.code,
                        onValueChange = { vm.code = it },
                        history = vm.history,
                        errorLine = vm.errorLine,
                        scrollVertically = true,
                        minLines = 12,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                    )
                    SymbolBar(vm.code, { vm.code = it }, vm.history)
                }
            }
            val runButton: @Composable () -> Unit = {
                ChunkyButton(
                    text = if (vm.running) tr("Stopp", "Stop") else tr("Ausführen", "Run"),
                    icon = if (vm.running) Icons.Rounded.Stop else Icons.Rounded.PlayArrow,
                    onClick = {
                        if (vm.running) {
                            vm.stop()
                        } else {
                            feedback.run()
                            vm.run(lang, onStarted = {
                                showConsole = true
                                app.recordCodeRun()
                            })
                        }
                    },
                    color = if (vm.running) c.error else c.success,
                    shadow = if (vm.running) c.errorDark else c.successDark,
                    height = 46.dp,
                    sound = false,
                )
            }
            val consolePanel: @Composable (Modifier) -> Unit = { modifier ->
                Column(
                    modifier
                        .clip(PyTheme.shapes.mediumShape)
                        .background(theme.background)
                        .border(1.dp, c.border, PyTheme.shapes.mediumShape)
                ) {
                    val hint = vm.lastResult?.hint
                    if (hint != null) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .background(c.gold.copy(alpha = 0.12f))
                                .padding(10.dp)
                        ) {
                            Icon(Icons.Rounded.Lightbulb, null, tint = c.gold, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text(vm.lastResult?.errorType ?: "", style = MaterialTheme.typography.labelLarge, color = theme.text)
                                MarkdownText(hint, style = MaterialTheme.typography.bodySmall, color = theme.text)
                            }
                        }
                    }
                    ConsoleView(
                        chunks = console,
                        waitingPrompt = waiting,
                        onSubmitInput = { vm.submitInput(it) },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                    )
                }
            }

            if (window.splitPlayground) {
                // Großes Display: Editor links, Konsole rechts – beide immer sichtbar
                Row(
                    Modifier
                        .weight(1f)
                        .padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
                        .navigationBarsPadding(),
                ) {
                    Column(
                        Modifier
                            .weight(1.15f)
                            .fillMaxHeight()
                    ) {
                        editor(Modifier.weight(1f).fillMaxWidth())
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            runButton()
                            Spacer(Modifier.weight(1f))
                            IconButton(onClick = { vm.clearConsole() }) {
                                Icon(Icons.Rounded.DeleteSweep, tr("Konsole leeren", "Clear console"), tint = c.textMuted)
                            }
                        }
                    }
                    Spacer(Modifier.width(10.dp))
                    consolePanel(
                        Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )
                }
            } else {
                editor(
                    Modifier
                        .weight(1f)
                        .padding(horizontal = 10.dp)
                )

                // Steuerleiste
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    runButton()
                    Spacer(Modifier.width(10.dp))
                    Row(
                        Modifier
                            .clip(CircleShape)
                            .background(cardColor())
                            .border(1.dp, c.border, CircleShape)
                            .clickable { showConsole = !showConsole }
                            .padding(horizontal = 14.dp, vertical = 11.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(tr("Konsole", "Console"), style = MaterialTheme.typography.labelLarge, color = c.text)
                        Icon(if (consoleVisible) Icons.Rounded.KeyboardArrowDown else Icons.Rounded.KeyboardArrowUp, null, tint = c.textMuted)
                    }
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = { vm.clearConsole() }) {
                        Icon(Icons.Rounded.DeleteSweep, tr("Konsole leeren", "Clear console"), tint = c.textMuted)
                    }
                }

                // Konsole
                AnimatedVisibility(
                    visible = consoleVisible,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut(),
                ) {
                    consolePanel(
                        Modifier
                            .fillMaxWidth()
                            .height(if (waiting != null && imeVisible) 170.dp else 260.dp)
                            .padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
                    )
                }
                if (!consoleVisible) Spacer(Modifier.navigationBarsPadding())
            }
        }
    }

    when (sheet) {
        PlaygroundSheet.EXAMPLES -> ExamplesSheet(
            examples = app.examplesFor(vm.language),
            onPick = {
                vm.replaceCode(it.code)
                sheet = null
            },
            onDismiss = { sheet = null },
        )
        PlaygroundSheet.SNIPPETS -> SnippetsSheet(
            snippets = progress?.snippets.orEmpty().filter { it.language == vm.language.key },
            onOpen = {
                vm.replaceCode(it.code)
                sheet = null
            },
            onDelete = { app.deleteSnippet(it.id) },
            onDismiss = { sheet = null },
        )
        null -> Unit
    }

    if (saveDialog) {
        val commentPrefix = if (vm.language == CourseId.PYTHON) "#" else "//"
        var title by remember {
            mutableStateOf(vm.code.text.lineSequence().firstOrNull { it.startsWith(commentPrefix) }?.removePrefix(commentPrefix)?.trim()?.take(40) ?: "")
        }
        val fallback = tr("Mein Programm", "My program")
        AlertDialog(
            onDismissRequest = { saveDialog = false },
            title = { Text(tr("Programm speichern", "Save program")) },
            text = {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it.take(60) },
                    label = { Text(tr("Name", "Name")) },
                    singleLine = true,
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    app.saveSnippet(title.ifBlank { fallback }, vm.code.text, vm.language)
                    saveDialog = false
                    feedback.correct()
                }) { Text(tr("Speichern", "Save")) }
            },
            dismissButton = { TextButton(onClick = { saveDialog = false }) { Text(tr("Abbrechen", "Cancel")) } },
            containerColor = c.surface,
        )
    }
}

/** Umschalter zwischen den Programmiersprachen – die Markierung gleitet mit. */
@Composable
private fun LanguageSwitch(languages: List<CourseId>, selected: CourseId, modifier: Modifier, onSelect: (CourseId) -> Unit) {
    val c = PyTheme.colors
    Row(
        modifier
            .clip(CircleShape)
            .background(cardColor())
            .border(1.dp, c.border, CircleShape)
            .padding(4.dp)
    ) {
        languages.forEach { l ->
            val sel = l == selected
            val bg by animateColorAsState(if (sel) c.primary else cardColor(), label = "langBg")
            Row(
                Modifier
                    .clip(CircleShape)
                    .background(bg)
                    .clickable(enabled = !sel) { onSelect(l) }
                    .padding(horizontal = 14.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LanguageBadge(l, size = 18.dp)
                Spacer(Modifier.width(7.dp))
                Text(l.displayName, style = MaterialTheme.typography.labelLarge, color = if (sel) c.onPrimary else c.text)
            }
        }
    }
}

@Composable
private fun ExamplesSheet(examples: List<ExampleProgram>, onPick: (ExampleProgram) -> Unit, onDismiss: () -> Unit) {
    val c = PyTheme.colors
    val state = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = state, containerColor = c.surface) {
        Text(tr("Beispielprogramme", "Example programs"), style = MaterialTheme.typography.titleLarge, color = c.text, modifier = Modifier.padding(horizontal = 20.dp))
        Text(
            tr("Tippe auf ein Beispiel, um es in den Editor zu laden.", "Tap an example to load it into the editor."),
            style = MaterialTheme.typography.bodySmall,
            color = c.textMuted,
            modifier = Modifier.padding(horizontal = 20.dp),
        )
        Spacer(Modifier.height(8.dp))
        val grouped = remember(examples) { examples.groupBy { it.category } }
        LazyColumn(Modifier.fillMaxWidth().navigationBarsPadding(), contentPadding = PaddingValues(bottom = 24.dp)) {
            grouped.forEach { (category, list) ->
                item(key = "cat-$category") {
                    Text(
                        category,
                        style = MaterialTheme.typography.labelLarge,
                        color = c.primaryText,
                        modifier = Modifier.padding(start = 20.dp, top = 14.dp, bottom = 4.dp),
                    )
                }
                items(list, key = { "ex-${it.title}" }) { ex ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { onPick(ex) }
                            .padding(horizontal = 20.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(ex.title, style = MaterialTheme.typography.titleSmall, color = c.text)
                            Text(ex.description, style = MaterialTheme.typography.bodySmall, color = c.textMuted, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SnippetsSheet(snippets: List<Snippet>, onOpen: (Snippet) -> Unit, onDelete: (Snippet) -> Unit, onDismiss: () -> Unit) {
    val c = PyTheme.colors
    val state = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = state, containerColor = c.surface) {
        Text(tr("Meine Programme", "My programs"), style = MaterialTheme.typography.titleLarge, color = c.text, modifier = Modifier.padding(horizontal = 20.dp))
        Spacer(Modifier.height(8.dp))
        if (snippets.isEmpty()) {
            Text(
                tr(
                    "Noch nichts gespeichert. Tippe im Spielplatz auf das Speichern-Symbol, um dein Programm zu sichern.",
                    "Nothing saved yet. Tap the save icon in the playground to keep your program.",
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = c.textMuted,
                modifier = Modifier.padding(20.dp),
            )
        }
        LazyColumn(Modifier.fillMaxWidth().navigationBarsPadding(), contentPadding = PaddingValues(bottom = 24.dp)) {
            items(snippets, key = { it.id }) { s ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable { onOpen(s) }
                        .padding(start = 20.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(s.title, style = MaterialTheme.typography.titleSmall, color = c.text)
                        Text(
                            s.code.lines().firstOrNull { it.isNotBlank() && !it.startsWith("#") && !it.startsWith("//") } ?: "",
                            style = codeTextStyle(0.85f),
                            color = c.textMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    IconButton(onClick = { onDelete(s) }) {
                        Icon(Icons.Rounded.Delete, tr("Löschen", "Delete"), tint = c.textMuted)
                    }
                }
                HorizontalDivider(color = c.border, modifier = Modifier.padding(horizontal = 20.dp))
            }
        }
    }
}
