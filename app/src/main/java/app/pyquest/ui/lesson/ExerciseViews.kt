package app.pyquest.ui.lesson

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Science
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.pyquest.data.ArrangeExercise
import app.pyquest.data.ChoiceExercise
import app.pyquest.data.CodeExercise
import app.pyquest.data.FillBlankExercise
import app.pyquest.data.MatchExercise
import app.pyquest.data.TrueFalseExercise
import app.pyquest.data.TypeExercise
import app.pyquest.i18n.tr
import app.pyquest.python.ConsoleChunk
import app.pyquest.python.RunResult
import app.pyquest.ui.code.CodeBlock
import app.pyquest.ui.code.CodeEditor
import app.pyquest.ui.code.ConsoleView
import app.pyquest.ui.code.EditHistory
import app.pyquest.ui.code.SymbolBar
import app.pyquest.ui.code.codeTextStyle
import app.pyquest.ui.code.highlightCode
import app.pyquest.ui.components.ChunkySurface
import app.pyquest.ui.components.LocalCourseId
import app.pyquest.ui.components.MarkdownText
import app.pyquest.ui.components.clickableNoRipple
import app.pyquest.ui.components.rememberFeedback
import app.pyquest.ui.components.shake
import app.pyquest.ui.home.cardColor
import app.pyquest.ui.theme.PyTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Gemeinsamer Zustand, den die Aufgabenansichten zum Anzeigen von Richtig/Falsch brauchen. */
data class ExerciseUiState(
    val locked: Boolean,
    val correct: Boolean?,
)

/** Aufgabentitel – darüber steht klein die Art der Aufgabe. */
@Composable
fun ExercisePrompt(kind: String, prompt: String) {
    val c = PyTheme.colors
    Text(kind.replaceFirstChar { it.uppercase() }, color = c.primaryText, style = MaterialTheme.typography.labelLarge)
    Spacer(Modifier.height(6.dp))
    MarkdownText(prompt, style = MaterialTheme.typography.titleLarge.copy(lineHeight = 30.sp), color = c.text)
}

// ------------------------------------------------------------------ Multiple Choice

@Composable
fun ChoiceView(
    inst: ExerciseInstance,
    e: ChoiceExercise,
    ui: ExerciseUiState,
    onAnswer: (Answer?) -> Unit,
) {
    val feedback = rememberFeedback()
    var selected by remember(inst.key) { mutableStateOf<Int?>(null) }
    Column {
        ExercisePrompt(
            if (e.isOutput) tr("Ausgabe vorhersagen", "predict the output") else tr("Wähle die richtige Antwort", "pick the right answer"),
            e.prompt,
        )
        if (e.code != null) {
            Spacer(Modifier.height(14.dp))
            CodeBlock(e.code, modifier = Modifier.fillMaxWidth())
        }
        Spacer(Modifier.height(18.dp))
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            inst.optionOrder.forEachIndexed { pos, optionIndex ->
                val isSelected = selected == optionIndex
                val state = when {
                    ui.locked && optionIndex == 0 && (isSelected || ui.correct == false) -> TileState.CORRECT
                    ui.locked && isSelected && ui.correct == false -> TileState.WRONG
                    isSelected -> TileState.SELECTED
                    else -> TileState.IDLE
                }
                OptionCard(
                    number = pos + 1,
                    text = e.options[optionIndex],
                    mono = e.monoOptions && !looksLikeProse(e.options[optionIndex]),
                    state = state,
                    enabled = !ui.locked,
                    onClick = {
                        feedback.select()
                        selected = optionIndex
                        onAnswer(Answer.Choice(optionIndex))
                    },
                )
            }
        }
    }
}

enum class TileState { IDLE, SELECTED, CORRECT, WRONG, USED, DISABLED }

/** Antworten wie "Nichts" oder "Ein Fehler" sind kein Code und werden normal gesetzt. */
private fun looksLikeProse(text: String): Boolean {
    val t = text.trim()
    val prose = listOf("Nichts", "Ein Fehler", "Fehler", "Eine Fehlermeldung", "Endlosschleife", "Eine Endlosschleife",
        "Nothing", "An error", "Error", "An error message", "Infinite loop", "An infinite loop")
    return prose.any { t == it || t.startsWith("$it ") }
}

@Composable
private fun tileColors(state: TileState): Triple<Color, Color, Color> {
    val c = PyTheme.colors
    val card = cardColor()
    return when (state) {
        TileState.IDLE -> Triple(card, c.border, c.text)
        TileState.SELECTED -> Triple(c.primarySoft, c.primary, c.primaryText)
        TileState.CORRECT -> Triple(c.successSoft, c.success, c.onSuccessSoft)
        TileState.WRONG -> Triple(c.errorSoft, c.error, c.onErrorSoft)
        TileState.USED -> Triple(c.locked.copy(alpha = 0.5f), c.locked, Color.Transparent)
        TileState.DISABLED -> Triple(c.surfaceAlt, c.border, c.textMuted)
    }
}

/** Kleiner Kreis mit der Nummer der Antwort. */
@Composable
private fun KeyCap(content: @Composable () -> Unit, border: Color) {
    Box(
        Modifier
            .size(28.dp)
            .clip(CircleShape)
            .border(1.5.dp, border, CircleShape),
        contentAlignment = Alignment.Center,
    ) { content() }
}

@Composable
fun OptionCard(number: Int, text: String, mono: Boolean, state: TileState, enabled: Boolean, onClick: () -> Unit) {
    val (face, edge, fg) = tileColors(state)
    val animFace by animateColorAsState(face, tween(160), label = "face")
    val animEdge by animateColorAsState(edge, tween(160), label = "edge")
    ChunkySurface(
        onClick = onClick,
        enabled = enabled,
        face = animFace,
        edge = animEdge,
        borderWidth = if (state == TileState.IDLE || state == TileState.DISABLED) 1.dp else 2.dp,
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterStart,
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            KeyCap(border = animEdge, content = {
                when (state) {
                    TileState.CORRECT -> Icon(Icons.Rounded.Check, null, tint = fg, modifier = Modifier.size(18.dp))
                    TileState.WRONG -> Icon(Icons.Rounded.Close, null, tint = fg, modifier = Modifier.size(18.dp))
                    else -> Text(
                        "$number",
                        color = if (state == TileState.SELECTED) fg else PyTheme.colors.textMuted,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            })
            Spacer(Modifier.width(14.dp))
            if (mono) {
                Text(text, style = codeTextStyle(1.05f), color = fg, fontWeight = FontWeight.SemiBold)
            } else {
                MarkdownText(text, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold), color = fg)
            }
        }
    }
}

// ------------------------------------------------------------------ Richtig / Falsch

@Composable
fun TrueFalseView(inst: ExerciseInstance, e: TrueFalseExercise, ui: ExerciseUiState, onAnswer: (Answer?) -> Unit) {
    val feedback = rememberFeedback()
    var selected by remember(inst.key) { mutableStateOf<Boolean?>(null) }
    Column {
        ExercisePrompt(tr("Richtig oder falsch?", "true or false?"), e.prompt)
        if (e.code != null) {
            Spacer(Modifier.height(14.dp))
            CodeBlock(e.code, modifier = Modifier.fillMaxWidth())
        }
        Spacer(Modifier.height(22.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            listOf(true, false).forEach { value ->
                val isSel = selected == value
                val state = when {
                    ui.locked && value == e.answer && (isSel || ui.correct == false) -> TileState.CORRECT
                    ui.locked && isSel && ui.correct == false -> TileState.WRONG
                    isSel -> TileState.SELECTED
                    else -> TileState.IDLE
                }
                val (face, edge, fg) = tileColors(state)
                ChunkySurface(
                    onClick = {
                        feedback.select()
                        selected = value
                        onAnswer(Answer.Bool(value))
                    },
                    enabled = !ui.locked,
                    face = face,
                    edge = edge,
                    borderWidth = if (state == TileState.IDLE) 1.dp else 2.dp,
                    modifier = Modifier.weight(1f),
                    minHeight = 110.dp,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            if (value) Icons.Rounded.Check else Icons.Rounded.Close,
                            null,
                            tint = if (state == TileState.IDLE) (if (value) PyTheme.colors.success else PyTheme.colors.error) else fg,
                            modifier = Modifier.size(40.dp),
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            if (value) tr("Richtig", "True") else tr("Falsch", "False"),
                            style = MaterialTheme.typography.titleMedium,
                            color = if (state == TileState.IDLE) PyTheme.colors.text else fg,
                        )
                    }
                }
            }
        }
    }
}

// ------------------------------------------------------------------ Kacheln

@Composable
fun WordTile(text: String, state: TileState, mono: Boolean = true, onClick: (() -> Unit)?) {
    val (face, edge, fg) = tileColors(state)
    val used = state == TileState.USED
    Box {
        ChunkySurface(
            onClick = if (used) null else onClick,
            enabled = onClick != null && !used,
            face = face,
            edge = edge,
            borderWidth = 1.dp,
            shape = PyTheme.shapes.smallShape,
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 9.dp),
        ) {
            Text(
                text,
                style = if (mono) codeTextStyle(1.02f) else MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = fg,
                modifier = Modifier.alpha(if (used) 0f else 1f),
            )
        }
    }
}

// ------------------------------------------------------------------ Lückentext

@Composable
fun FillBlankView(inst: ExerciseInstance, e: FillBlankExercise, ui: ExerciseUiState, wrongPulse: Int, onAnswer: (Answer?) -> Unit) {
    val feedback = rememberFeedback()
    val theme = PyTheme.code.theme
    // slots[i] = Index in der Bank oder -1
    val slots = remember(inst.key) { mutableStateListOf<Int>().apply { repeat(e.answers.size) { add(-1) } } }

    fun report() {
        onAnswer(if (slots.all { it >= 0 }) Answer.Tiles(slots.map { inst.bank[it] }) else null)
    }

    Column {
        ExercisePrompt(tr("Fülle die Lücken", "fill in the blanks"), e.prompt)
        Spacer(Modifier.height(14.dp))
        val lines = remember(e.template) { e.template.lines() }
        val lineSlotStart = remember(e.template) {
            lines.runningFold(0) { acc, l -> acc + (l.split(FillBlankExercise.BLANK).size - 1) }
        }
        Column(
            Modifier
                .fillMaxWidth()
                .clip(PyTheme.shapes.mediumShape)
                .background(theme.background)
                .padding(horizontal = 12.dp, vertical = 12.dp)
                .shake(if (ui.correct == false) wrongPulse else 0),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            lines.forEachIndexed { lineIndex, line ->
                val parts = line.split(FillBlankExercise.BLANK)
                FlowRow(verticalArrangement = Arrangement.Center, itemVerticalAlignment = Alignment.CenterVertically) {
                    parts.forEachIndexed { pi, part ->
                        if (part.isNotEmpty()) {
                            Text(highlightCode(part, theme, LocalCourseId.current), style = codeTextStyle(1.02f), softWrap = false)
                        }
                        if (pi < parts.lastIndex) {
                            val slotIndex = lineSlotStart[lineIndex] + pi
                            val bankIndex = slots.getOrElse(slotIndex) { -1 }
                            val state = when {
                                ui.locked && ui.correct == true -> TileState.CORRECT
                                ui.locked && ui.correct == false && bankIndex >= 0 && inst.bank[bankIndex] != e.answers[slotIndex] -> TileState.WRONG
                                ui.locked && ui.correct == false -> TileState.CORRECT
                                else -> TileState.SELECTED
                            }
                            BlankSlot(
                                text = bankIndex.takeIf { it >= 0 }?.let { inst.bank[it] },
                                state = state,
                                onClick = if (!ui.locked && bankIndex >= 0) ({
                                    feedback.tap()
                                    slots[slotIndex] = -1
                                    report()
                                }) else null,
                            )
                        }
                    }
                    if (line.isEmpty()) Text(" ", style = codeTextStyle())
                }
            }
        }
        Spacer(Modifier.height(22.dp))
        FlowRow(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            inst.bank.forEachIndexed { bi, word ->
                val used = bi in slots
                WordTile(
                    text = word,
                    state = if (used) TileState.USED else if (ui.locked) TileState.DISABLED else TileState.IDLE,
                    onClick = if (ui.locked) null else ({
                        val free = slots.indexOf(-1)
                        if (free >= 0) {
                            feedback.select()
                            slots[free] = bi
                            report()
                        }
                    }),
                )
            }
        }
    }
}

@Composable
private fun BlankSlot(text: String?, state: TileState, onClick: (() -> Unit)?) {
    val c = PyTheme.colors
    val (face, edge, fg) = tileColors(if (text == null) TileState.IDLE else state)
    val scale by animateFloatAsState(
        if (text == null) 0.94f else 1f,
        spring(dampingRatio = 0.42f, stiffness = 600f),
        label = "slot",
    )
    Box(
        Modifier
            .padding(horizontal = 3.dp, vertical = 2.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .widthIn(min = 56.dp)
            .heightIn(min = 32.dp)
            .clip(PyTheme.shapes.smallShape)
            .background(if (text == null) c.primary.copy(alpha = 0.06f) else face)
            .border(
                if (text == null) 1.5.dp else 2.dp,
                if (text == null) c.primary.copy(alpha = 0.45f) else edge,
                PyTheme.shapes.smallShape,
            )
            .then(if (onClick != null) Modifier.clickableNoRipple(onClick = onClick) else Modifier)
            .padding(horizontal = 8.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (text != null) {
            Text(text, style = codeTextStyle(1.02f), color = fg, fontWeight = FontWeight.Bold)
        }
    }
}

// ------------------------------------------------------------------ Code-Puzzle

@Composable
fun ArrangeView(inst: ExerciseInstance, e: ArrangeExercise, ui: ExerciseUiState, wrongPulse: Int, onAnswer: (Answer?) -> Unit) {
    val feedback = rememberFeedback()
    val theme = PyTheme.code.theme
    val placed = remember(inst.key) { mutableStateListOf<Int>() }

    fun report() {
        onAnswer(if (placed.size == e.lines.size) Answer.Tiles(placed.map { inst.bank[it] }) else null)
    }

    Column {
        ExercisePrompt(tr("Bringe den Code in die richtige Reihenfolge", "put the code in the right order"), e.prompt)
        Spacer(Modifier.height(14.dp))
        Column(
            Modifier
                .fillMaxWidth()
                .heightIn(min = (e.lines.size * 44 + 24).dp)
                .clip(PyTheme.shapes.mediumShape)
                .background(theme.background)
                .padding(10.dp)
                .shake(if (ui.correct == false) wrongPulse else 0),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            if (placed.isEmpty()) {
                Text(
                    tr("Tippe die Zeilen unten in der richtigen Reihenfolge an.", "Tap the lines below in the right order."),
                    color = theme.comment,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(8.dp),
                )
            }
            placed.forEachIndexed { pos, bi ->
                val line = inst.bank[bi]
                val state = when {
                    ui.locked && ui.correct == true -> TileState.CORRECT
                    ui.locked && ui.correct == false -> if (e.lines.getOrNull(pos) == line) TileState.CORRECT else TileState.WRONG
                    else -> TileState.IDLE
                }
                CodeLineTile(line, state, onClick = if (ui.locked) null else ({
                    feedback.tap()
                    placed.removeAt(pos)
                    report()
                }))
            }
        }
        Spacer(Modifier.height(18.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            inst.bank.forEachIndexed { bi, line ->
                val used = bi in placed
                CodeLineTile(
                    line,
                    if (used) TileState.USED else if (ui.locked) TileState.DISABLED else TileState.IDLE,
                    onClick = if (ui.locked || used) null else ({
                        feedback.select()
                        placed += bi
                        report()
                    }),
                )
            }
        }
    }
}

@Composable
private fun CodeLineTile(line: String, state: TileState, onClick: (() -> Unit)?) {
    val theme = PyTheme.code.theme
    val (face, edge, _) = tileColors(state)
    val used = state == TileState.USED
    val indent = line.takeWhile { it == ' ' }.length
    ChunkySurface(
        onClick = onClick,
        enabled = onClick != null,
        face = if (state == TileState.IDLE || state == TileState.DISABLED) theme.gutter else face,
        edge = if (state == TileState.IDLE || state == TileState.DISABLED) theme.gutterText.copy(alpha = 0.3f) else edge,
        borderWidth = 1.dp,
        shape = PyTheme.shapes.smallShape,
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterStart,
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
    ) {
        Row(Modifier.alpha(if (used) 0f else 1f), verticalAlignment = Alignment.CenterVertically) {
            if (indent > 0) {
                // Einrückung sichtbar machen
                repeat(indent / 4) {
                    Box(
                        Modifier
                            .padding(end = 4.dp)
                            .size(width = 18.dp, height = 18.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(theme.gutterText.copy(alpha = 0.16f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("→", color = theme.gutterText, fontSize = 11.sp)
                    }
                }
            }
            Text(highlightCode(line.trimStart(), theme, LocalCourseId.current), style = codeTextStyle(1.02f), softWrap = false)
        }
    }
}

// ------------------------------------------------------------------ Eintippen

@Composable
fun TypeView(inst: ExerciseInstance, e: TypeExercise, ui: ExerciseUiState, onAnswer: (Answer?) -> Unit) {
    val c = PyTheme.colors
    var text by remember(inst.key) { mutableStateOf("") }
    Column {
        ExercisePrompt(if (e.isOutput) tr("Ausgabe eintippen", "type the output") else tr("Schreibe die Antwort", "type your answer"), e.prompt)
        if (e.code != null) {
            Spacer(Modifier.height(14.dp))
            CodeBlock(e.code, modifier = Modifier.fillMaxWidth())
        }
        Spacer(Modifier.height(18.dp))
        val borderColor = when (ui.correct) {
            true -> c.success
            false -> c.error
            null -> c.border
        }
        OutlinedTextField(
            value = text,
            onValueChange = {
                text = it
                onAnswer(if (it.isBlank()) null else Answer.Text(it))
            },
            enabled = !ui.locked,
            placeholder = { Text(e.placeholder?.let { p -> if (p == "Ausgabe") tr("Ausgabe", "Output") else p } ?: tr("Deine Antwort", "Your answer"), style = codeTextStyle()) },
            textStyle = codeTextStyle(1.1f).copy(color = c.text),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrectEnabled = false,
                keyboardType = KeyboardType.Ascii,
            ),
            shape = PyTheme.shapes.mediumShape,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (ui.correct == null) c.primary else borderColor,
                unfocusedBorderColor = borderColor,
                disabledBorderColor = borderColor,
                disabledTextColor = c.text,
                focusedContainerColor = cardColor(),
                unfocusedContainerColor = cardColor(),
                disabledContainerColor = cardColor(),
            ),
            minLines = 2,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(8.dp))
        Text(
            tr("Tipp: Groß-/Kleinschreibung zählt, Leerzeichen sind egal.", "Tip: Upper/lower case matters, spaces don't."),
            color = c.textMuted,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

// ------------------------------------------------------------------ Paare finden

@Composable
fun MatchView(inst: ExerciseInstance, e: MatchExercise, ui: ExerciseUiState, onAnswer: (Answer?) -> Unit, onAutoCheck: () -> Unit) {
    val feedback = rememberFeedback()
    val scope = rememberCoroutineScope()
    val left = remember(inst.key) { e.pairs.indices.shuffled() }
    val right = remember(inst.key) { e.pairs.indices.shuffled() }
    var selLeft by remember(inst.key) { mutableStateOf<Int?>(null) }
    var selRight by remember(inst.key) { mutableStateOf<Int?>(null) }
    val matched = remember(inst.key) { mutableStateListOf<Int>() }
    var wrong by remember(inst.key) { mutableStateOf<Pair<Int, Int>?>(null) }
    var flash by remember(inst.key) { mutableStateOf<Int?>(null) }
    var shakeCount by remember(inst.key) { mutableIntStateOf(0) }

    fun tryMatch() {
        val l = selLeft ?: return
        val r = selRight ?: return
        if (l == r) {
            feedback.correct()
            matched += l
            flash = l
            selLeft = null
            selRight = null
            scope.launch {
                delay(350)
                if (flash == l) flash = null
            }
            if (matched.size == e.pairs.size) {
                onAnswer(Answer.Matched)
                scope.launch {
                    delay(450)
                    onAutoCheck()
                }
            }
        } else {
            feedback.wrong()
            wrong = l to r
            shakeCount++
            scope.launch {
                delay(500)
                wrong = null
                selLeft = null
                selRight = null
            }
        }
    }

    Column {
        ExercisePrompt(tr("Paare finden", "match the pairs"), e.prompt)
        Spacer(Modifier.height(18.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.shake(shakeCount)) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                left.forEach { i ->
                    val st = when {
                        flash == i -> TileState.CORRECT
                        i in matched -> TileState.DISABLED
                        wrong?.first == i -> TileState.WRONG
                        selLeft == i -> TileState.SELECTED
                        else -> TileState.IDLE
                    }
                    MatchTile(e.pairs[i].first, st, mono = true, enabled = !ui.locked && i !in matched) {
                        feedback.select()
                        selLeft = i
                        tryMatch()
                    }
                }
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                right.forEach { i ->
                    val st = when {
                        flash == i -> TileState.CORRECT
                        i in matched -> TileState.DISABLED
                        wrong?.second == i -> TileState.WRONG
                        selRight == i -> TileState.SELECTED
                        else -> TileState.IDLE
                    }
                    MatchTile(e.pairs[i].second, st, mono = false, enabled = !ui.locked && i !in matched) {
                        feedback.select()
                        selRight = i
                        tryMatch()
                    }
                }
            }
        }
    }
}

@Composable
private fun MatchTile(text: String, state: TileState, mono: Boolean, enabled: Boolean, onClick: () -> Unit) {
    val (face, edge, fg) = tileColors(state)
    val animFace by animateColorAsState(face, tween(150), label = "mface")
    ChunkySurface(
        onClick = onClick,
        enabled = enabled,
        face = animFace,
        edge = edge,
        borderWidth = if (state == TileState.IDLE || state == TileState.DISABLED) 1.dp else 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (state == TileState.DISABLED) 0.55f else 1f),
        minHeight = 58.dp,
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
    ) {
        Text(
            text,
            style = if (mono) codeTextStyle(0.95f) else MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = fg,
            textAlign = TextAlign.Center,
        )
    }
}

// ------------------------------------------------------------------ Programmieraufgabe

@Composable
fun CodeTaskView(
    inst: ExerciseInstance,
    e: CodeExercise,
    ui: ExerciseUiState,
    run: suspend (String, List<String>, (ConsoleChunk) -> Unit) -> RunResult,
    onAnswer: (Answer?) -> Unit,
    lastCheck: RunResult?,
) {
    val c = PyTheme.colors
    val scope = rememberCoroutineScope()
    val feedback = rememberFeedback()
    var value by remember(inst.key) { mutableStateOf(TextFieldValue(e.starter, TextRange(e.starter.length))) }
    val history = remember(inst.key) { EditHistory() }
    val console = remember(inst.key) { mutableStateListOf<ConsoleChunk>() }
    var running by remember(inst.key) { mutableStateOf(false) }
    var errorLine by remember(inst.key) { mutableStateOf<Int?>(null) }
    var showHint by remember(inst.key) { mutableStateOf(false) }
    val timeLimit = tr("Zeitlimit überschritten – Endlosschleife?", "Time limit exceeded – infinite loop?")
    val noOutput = tr("(keine Ausgabe)", "(no output)")

    LaunchedEffect(inst.key) { onAnswer(if (e.starter.isBlank()) null else Answer.Code(e.starter)) }
    LaunchedEffect(lastCheck) {
        if (lastCheck != null) {
            console.clear()
            if (lastCheck.stdout.isNotEmpty()) console += ConsoleChunk(lastCheck.stdout, 0)
            if (lastCheck.stderr.isNotEmpty()) console += ConsoleChunk(lastCheck.stderr, 1)
            lastCheck.error?.let { console += ConsoleChunk((if (console.isEmpty()) "" else "\n") + it, 1) }
            if (lastCheck.timedOut) console += ConsoleChunk("\n$timeLimit", 1)
            errorLine = lastCheck.errorLine
        }
    }

    fun testRun() {
        if (running) return
        feedback.run()
        running = true
        console.clear()
        errorLine = null
        scope.launch {
            val r = run(value.text, e.stdin) { chunk -> console += chunk }
            if (r.error != null) console += ConsoleChunk((if (console.isEmpty()) "" else "\n") + r.error, 1)
            if (r.timedOut) console += ConsoleChunk("\n$timeLimit", 1)
            if (r.ok && console.isEmpty()) console += ConsoleChunk(noOutput, 3)
            errorLine = r.errorLine
            running = false
        }
    }

    Column {
        ExercisePrompt(tr("Programmieraufgabe", "coding task"), e.prompt)
        if (e.expectedOutput != null) {
            Spacer(Modifier.height(12.dp))
            Text(tr("Erwartete Ausgabe", "Expected output"), color = c.textMuted, style = MaterialTheme.typography.labelMedium)
            Spacer(Modifier.height(4.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(PyTheme.shapes.smallShape)
                    .background(PyTheme.code.theme.background)
                    .border(1.dp, c.border, PyTheme.shapes.smallShape)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(e.expectedOutput, style = codeTextStyle(0.95f), color = PyTheme.code.theme.text)
            }
        } else if (e.testCode != null) {
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Science, contentDescription = null, tint = c.textMuted, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text(
                    tr("Beim Prüfen testet Hissbit deinen Code automatisch.", "When you check, Hissbit tests your code automatically."),
                    color = c.textMuted,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
        if (e.stdin.isNotEmpty()) {
            Spacer(Modifier.height(6.dp))
            Text(
                tr("Eingaben für input(): ", "Inputs for input(): ") + e.stdin.joinToString(", ") { "\"$it\"" },
                color = c.textMuted,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        Spacer(Modifier.height(12.dp))
        val editorArea = remember(inst.key) { BringIntoViewRequester() }
        Column(
            Modifier
                .fillMaxWidth()
                .bringIntoViewRequester(editorArea)
                .onFocusChanged { state ->
                    // Wenn die Tastatur aufgeht: Editor und Symbolleiste komplett zeigen
                    if (state.hasFocus) scope.launch {
                        delay(350)
                        editorArea.bringIntoView()
                    }
                }
                .clip(PyTheme.shapes.mediumShape)
                .border(
                    1.5.dp,
                    if (ui.correct == true) c.success else if (ui.correct == false) c.error else c.border,
                    PyTheme.shapes.mediumShape,
                )
        ) {
            CodeEditor(
                value = value,
                onValueChange = {
                    value = it
                    onAnswer(if (it.text.isBlank()) null else Answer.Code(it.text))
                },
                history = history,
                errorLine = errorLine,
                minLines = 6,
                readOnly = ui.locked,
                modifier = Modifier.fillMaxWidth(),
            )
            if (!ui.locked) SymbolBar(value, { value = it; onAnswer(if (it.text.isBlank()) null else Answer.Code(it.text)) }, history)
        }
        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            ChunkySurface(
                onClick = { testRun() },
                enabled = !running,
                face = cardColor(),
                edge = c.border,
                borderWidth = 1.dp,
                shape = CircleShape,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.PlayArrow, null, tint = c.success, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(if (running) tr("Läuft …", "Running …") else tr("Testen", "Run"), style = MaterialTheme.typography.labelLarge, color = c.text)
                }
            }
            Spacer(Modifier.width(10.dp))
            if (!ui.locked) {
                ChunkySurface(
                    onClick = {
                        value = TextFieldValue(e.starter, TextRange(e.starter.length))
                        history.clear()
                        console.clear()
                        errorLine = null
                        onAnswer(if (e.starter.isBlank()) null else Answer.Code(e.starter))
                    },
                    face = cardColor(),
                    edge = c.border,
                    borderWidth = 1.dp,
                    shape = CircleShape,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
                ) {
                    Icon(Icons.Rounded.Refresh, tr("Zurücksetzen", "Reset"), tint = c.textMuted, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(Modifier.weight(1f))
            if (e.hint != null && !ui.locked) {
                Text(
                    if (showHint) tr("Tipp ausblenden", "Hide hint") else tr("Tipp anzeigen", "Show hint"),
                    color = c.primaryText,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickableNoRipple { showHint = !showHint }
                        .padding(8.dp),
                )
            }
        }
        if (showHint && e.hint != null) {
            Spacer(Modifier.height(8.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(PyTheme.shapes.smallShape)
                    .background(c.gold.copy(alpha = 0.14f))
                    .border(1.dp, c.gold.copy(alpha = 0.35f), PyTheme.shapes.smallShape)
                    .padding(12.dp)
            ) {
                MarkdownText(e.hint, style = MaterialTheme.typography.bodyMedium)
            }
        }
        if (console.isNotEmpty() || running) {
            Spacer(Modifier.height(10.dp))
            ConsoleView(
                chunks = console.toList(),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 60.dp, max = 220.dp)
                    .clip(PyTheme.shapes.mediumShape),
                placeholder = tr("Läuft …", "Running …"),
            )
        }
    }
}
