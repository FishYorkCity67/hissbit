package app.pyquest.ui.code

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import android.content.ClipData
import androidx.compose.material.icons.automirrored.rounded.FormatIndentDecrease
import androidx.compose.material.icons.automirrored.rounded.FormatIndentIncrease
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.rounded.Redo
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.automirrored.rounded.Undo
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import app.pyquest.data.CourseId
import app.pyquest.i18n.tr
import app.pyquest.python.ConsoleChunk
import app.pyquest.ui.components.LocalCourseId
import kotlinx.coroutines.launch
import app.pyquest.ui.components.rememberFeedback
import app.pyquest.ui.theme.EditorTheme
import app.pyquest.ui.theme.PyTheme
import app.pyquest.ui.theme.onColor

@Composable
fun codeTextStyle(scale: Float = 1f): TextStyle {
    val code = PyTheme.code
    return TextStyle(
        fontFamily = code.family,
        fontSize = code.fontSize * scale,
        lineHeight = (code.fontSize * scale) * 1.5f,
        fontWeight = FontWeight.Normal,
        fontFeatureSettings = code.fontFeatures,
    )
}

/** Schreibgeschützter, eingefärbter Code-Block. */
@Composable
fun CodeBlock(
    code: String,
    modifier: Modifier = Modifier,
    lineNumbers: Boolean = false,
    scale: Float = 1f,
    title: String? = null,
    actions: @Composable (() -> Unit)? = null,
) {
    val theme = PyTheme.code.theme
    val style = codeTextStyle(scale)
    val course = LocalCourseId.current
    val highlighted = remember(code, theme, course) { highlightCode(code, theme, course) }
    Column(
        modifier
            .clip(PyTheme.shapes.mediumShape)
            .background(theme.background)
    ) {
        if (title != null || actions != null) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(theme.gutter)
                    .padding(start = 14.dp, end = 4.dp, top = 2.dp, bottom = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Statt eines erfundenen Dateinamens steht hier einfach die Sprache.
                Text(
                    course.displayName,
                    color = theme.gutterText,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                actions?.invoke()
            }
        }
        Row(
            Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 12.dp)
        ) {
            if (lineNumbers) {
                Text(
                    text = (1..code.lines().size).joinToString("\n"),
                    style = style,
                    color = theme.gutterText,
                    modifier = Modifier.padding(start = 12.dp, end = 10.dp),
                )
            }
            Text(
                text = highlighted,
                style = style,
                softWrap = false,
                modifier = Modifier.padding(start = if (lineNumbers) 0.dp else 14.dp, end = 14.dp),
            )
        }
    }
}

@Composable
fun CodeHeaderAction(icon: ImageVector, description: String, onClick: () -> Unit, tint: Color = PyTheme.code.theme.gutterText) {
    Box(
        Modifier
            .size(36.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = description, tint = tint, modifier = Modifier.size(20.dp))
    }
}

@Composable
fun CopyCodeAction(code: String) {
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()
    val feedback = rememberFeedback()
    CodeHeaderAction(Icons.Rounded.ContentCopy, tr("Kopieren", "Copy"), onClick = {
        scope.launch { clipboard.setClipEntry(ClipEntry(ClipData.newPlainText("Code", code))) }
        feedback.tick()
    })
}

/**
 * Code-Editor mit Syntax-Highlighting, Zeilennummern, Auto-Einrückung
 * und Fehlerzeilen-Markierung.
 */
@Composable
fun CodeEditor(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    history: EditHistory? = null,
    errorLine: Int? = null,
    scrollVertically: Boolean = false,
    minLines: Int = 5,
    readOnly: Boolean = false,
    focusRequester: FocusRequester? = null,
) {
    val code = PyTheme.code
    val theme = code.theme
    val style = codeTextStyle().copy(color = theme.text)
    val course = LocalCourseId.current
    val transformation = remember(theme, course) { CodeVisualTransformation(theme, course) }
    var layout by remember { mutableStateOf<TextLayoutResult?>(null) }
    val lineCount = value.text.count { it == '\n' } + 1
    val shownLines = maxOf(lineCount, minLines)
    val errorColor = Color(0x33FF4B4B)
    val lineHighlight = if (theme.isDark) Color(0x0FFFFFFF) else Color(0x0A000000)
    val cursorLine = value.text.take(value.selection.start.coerceIn(0, value.text.length)).count { it == '\n' }

    val outer = if (scrollVertically) Modifier.verticalScroll(rememberScrollState()) else Modifier
    Box(modifier.background(theme.background)) {
        Row(outer.fillMaxWidth()) {
            if (code.lineNumbers) {
                Text(
                    text = buildAnnotatedString {
                        for (i in 1..shownLines) {
                            if (errorLine == i) pushStyle(SpanStyle(color = Color(0xFFFF6B6B), fontWeight = FontWeight.Bold))
                            else if (i - 1 == cursorLine) pushStyle(SpanStyle(color = theme.text))
                            else pushStyle(SpanStyle(color = theme.gutterText))
                            append(i.toString())
                            pop()
                            if (i < shownLines) append('\n')
                        }
                    },
                    style = style,
                    modifier = Modifier
                        .background(theme.gutter)
                        .padding(start = 10.dp, end = 8.dp, top = 12.dp, bottom = 12.dp)
                        .widthIn(min = 22.dp),
                )
            }
            BoxWithConstraints(Modifier.weight(1f)) {
                val viewport = maxWidth
                Box(Modifier.horizontalScroll(rememberScrollState())) {
                BasicTextField(
                    value = value,
                    onValueChange = { next ->
                        val smart = EditorOps.smartEdit(value, next, code.autoIndent, course)
                        history?.record(value, smart)
                        onValueChange(smart)
                    },
                    readOnly = readOnly,
                    textStyle = style,
                    cursorBrush = SolidColor(theme.cursor),
                    visualTransformation = transformation,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        autoCorrectEnabled = false,
                        keyboardType = KeyboardType.Ascii,
                        imeAction = ImeAction.Default,
                    ),
                    onTextLayout = { layout = it },
                    minLines = minLines,
                    // Innenabstand in der Decoration: Beim Fokussieren scrollt sonst
                    // die horizontale Ansicht um genau diesen Abstand.
                    decorationBox = { inner ->
                        Box(Modifier.padding(start = 10.dp, end = 24.dp, top = 12.dp, bottom = 12.dp)) { inner() }
                    },
                    modifier = Modifier
                        .widthIn(min = viewport)
                        .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier)
                        .drawBehind {
                            val l = layout ?: return@drawBehind
                            fun band(line: Int, color: Color) {
                                if (line < 0 || line >= l.lineCount) return
                                val top = l.getLineTop(line)
                                val bottom = l.getLineBottom(line)
                                drawRect(color, topLeft = Offset(-20f, top + 12.dp.toPx()), size = Size(size.width + 40f, bottom - top))
                            }
                            band(cursorLine, lineHighlight)
                            if (errorLine != null) band(errorLine - 1, errorColor)
                        },
                )
                }
            }
        }
    }
}

/** Leiste mit Sonderzeichen, die auf Handytastaturen schwer zu erreichen sind. */
@Composable
fun SymbolBar(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    history: EditHistory?,
    modifier: Modifier = Modifier,
) {
    val theme = PyTheme.code.theme
    val feedback = rememberFeedback()
    val course = LocalCourseId.current
    val unit = EditorOps.indentFor(course)
    fun apply(next: TextFieldValue) {
        history?.record(value, next)
        onValueChange(next)
        feedback.tap()
    }
    val js = course == CourseId.JAVASCRIPT
    val symbols = if (js) {
        listOf("{", "}", "(", ")", ";", "=", "=>", "\"", "'", "`", "[", "]", "+", "-", "*", "/", "%", "<", ">", "!", "&", "|", "?", ":", ".", ",", "_", "$", "\\")
    } else {
        listOf("(", ")", ":", "\"", "'", "=", "[", "]", "{", "}", "+", "-", "*", "/", "%", "<", ">", "!", "#", "_", ",", ".", "&", "|", "\\", "@")
    }
    Row(
        modifier
            .fillMaxWidth()
            .background(theme.gutter)
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 6.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SymbolKey(icon = Icons.AutoMirrored.Rounded.FormatIndentIncrease, theme = theme) { apply(EditorOps.insert(value, unit)) }
        SymbolKey(icon = Icons.AutoMirrored.Rounded.FormatIndentDecrease, theme = theme) { apply(EditorOps.dedent(value, unit)) }
        if (history != null) {
            SymbolKey(icon = Icons.AutoMirrored.Rounded.Undo, theme = theme, enabled = history.canUndo) {
                history.undo(value)?.let { onValueChange(it); feedback.tap() }
            }
            SymbolKey(icon = Icons.AutoMirrored.Rounded.Redo, theme = theme, enabled = history.canRedo) {
                history.redo(value)?.let { onValueChange(it); feedback.tap() }
            }
        }
        SymbolKey(label = "()", theme = theme) { apply(EditorOps.insertPair(value, "(", ")")) }
        if (js) SymbolKey(label = "{}", theme = theme) { apply(EditorOps.insertPair(value, "{", "}")) }
        SymbolKey(label = "\"\"", theme = theme) { apply(EditorOps.insertPair(value, "\"", "\"")) }
        if (js) SymbolKey(label = "``", theme = theme) { apply(EditorOps.insertPair(value, "`", "`")) }
        symbols.forEach { s ->
            SymbolKey(label = s, theme = theme) {
                apply(EditorOps.insert(value, s))
            }
        }
    }
}

@Composable
private fun SymbolKey(
    theme: EditorTheme,
    label: String? = null,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    Box(
        Modifier
            .heightIn(min = 38.dp)
            .widthIn(min = 38.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(theme.background)
            .border(1.dp, theme.gutterText.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        val color = if (enabled) theme.text else theme.gutterText.copy(alpha = 0.4f)
        if (icon != null) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(19.dp))
        } else if (label != null) {
            Text(label, color = color, fontFamily = PyTheme.code.family, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

/** Konsole mit Ausgabe, Fehlern (rot) und Eingabezeile für input(). */
@Composable
fun ConsoleView(
    chunks: List<ConsoleChunk>,
    modifier: Modifier = Modifier,
    waitingPrompt: String? = null,
    onSubmitInput: (String) -> Unit = {},
    placeholder: String = tr("Hier erscheint die Ausgabe deines Programms.", "Your program\u0027s output appears here."),
    scroll: Boolean = true,
) {
    val theme = PyTheme.code.theme
    val style = codeTextStyle(0.95f)
    val scrollState = rememberScrollState()
    val text = remember(chunks, theme) {
        buildAnnotatedString {
            chunks.forEach { chunk ->
                val color = when (chunk.kind) {
                    1 -> Color(0xFFFF6B6B)
                    2 -> theme.function
                    3 -> theme.comment
                    else -> theme.text
                }
                pushStyle(SpanStyle(color = color, fontWeight = if (chunk.kind == 2) FontWeight.Bold else FontWeight.Normal))
                append(chunk.text)
                pop()
            }
        }
    }
    LaunchedEffect(text.length, waitingPrompt) {
        if (scroll) scrollState.animateScrollTo(scrollState.maxValue)
    }
    Column(modifier.background(theme.background)) {
        Column(
            Modifier
                .weight(1f, fill = false)
                .then(if (scroll) Modifier.verticalScroll(scrollState) else Modifier)
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            if (chunks.isEmpty() && waitingPrompt == null) {
                Text(placeholder, style = style, color = theme.comment)
            } else {
                Text(text, style = style)
            }
        }
        AnimatedVisibility(visible = waitingPrompt != null) {
            ConsoleInput(onSubmitInput)
        }
    }
}

@Composable
private fun ConsoleInput(onSubmit: (String) -> Unit) {
    val theme = PyTheme.code.theme
    val inputHint = if (LocalCourseId.current == CourseId.JAVASCRIPT) tr("Eingabe für prompt() …", "Input for prompt() …")
    else tr("Eingabe für input() …", "Input for input() …")
    var input by remember { mutableStateOf("") }
    val focus = remember { FocusRequester() }
    LaunchedEffect(Unit) { runCatching { focus.requestFocus() } }
    Row(
        Modifier
            .fillMaxWidth()
            .background(theme.gutter)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null, tint = theme.function)
        BasicTextField(
            value = input,
            onValueChange = { input = it.replace("\n", "") },
            singleLine = true,
            textStyle = codeTextStyle().copy(color = theme.text),
            cursorBrush = SolidColor(theme.cursor),
            keyboardOptions = KeyboardOptions(autoCorrectEnabled = false, imeAction = ImeAction.Send, capitalization = KeyboardCapitalization.None),
            keyboardActions = KeyboardActions(onSend = {
                onSubmit(input)
                input = ""
            }),
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 6.dp, vertical = 8.dp)
                .focusRequester(focus),
            decorationBox = { inner ->
                Box {
                    if (input.isEmpty()) Text(inputHint, style = codeTextStyle(), color = theme.comment)
                    inner()
                }
            },
        )
        Box(
            Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(PyTheme.colors.primary)
                .clickable {
                    onSubmit(input)
                    input = ""
                },
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.AutoMirrored.Rounded.Send, tr("Senden", "Send"), tint = PyTheme.colors.onPrimary, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun RunChip(onClick: () -> Unit, running: Boolean, label: String = tr("Ausführen", "Run")) {
    val c = PyTheme.colors
    val feedback = rememberFeedback()
    val bg = if (running) c.locked else c.success
    val fg = bg.onColor()
    Row(
        Modifier
            .clip(CircleShape)
            .background(bg)
            .clickable(enabled = !running) { feedback.run(); onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons.Rounded.PlayArrow, null, tint = fg, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(4.dp))
        Text(if (running) tr("Läuft …", "Running …") else label, color = fg, style = MaterialTheme.typography.labelMedium, letterSpacing = 0.02.em)
    }
}
