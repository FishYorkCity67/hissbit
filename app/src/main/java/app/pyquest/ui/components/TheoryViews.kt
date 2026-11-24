package app.pyquest.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import app.pyquest.data.TheoryBlock
import app.pyquest.data.CourseId
import app.pyquest.i18n.tr
import app.pyquest.python.CodeRunner
import app.pyquest.ui.code.CodeBlock
import app.pyquest.ui.code.CodeHeaderAction
import app.pyquest.ui.code.CopyCodeAction
import app.pyquest.ui.code.codeTextStyle
import app.pyquest.ui.theme.PyTheme
import kotlinx.coroutines.launch

/** Laufzeit für die aktuelle Kurssprache (Python oder JavaScript). */
val LocalRunner = staticCompositionLocalOf<CodeRunner?> { null }

/** Kurs, zu dem die angezeigten Inhalte gehören – bestimmt Dateiendung und Einfärbung. */
val LocalCourseId = staticCompositionLocalOf { CourseId.PYTHON }


/** Öffnet Code im Spielplatz (wird von der Navigation bereitgestellt). */
val LocalOpenInPlayground = staticCompositionLocalOf<(String, CourseId) -> Unit> { { _, _ -> } }

@Composable
fun TheoryBlocks(blocks: List<TheoryBlock>, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(14.dp)) {
        blocks.forEach { block ->
            when (block) {
                is TheoryBlock.Heading -> Text(
                    block.text,
                    style = MaterialTheme.typography.titleLarge,
                    color = PyTheme.colors.text,
                    modifier = Modifier.padding(top = 6.dp),
                )
                is TheoryBlock.Text -> MarkdownText(block.markdown)
                is TheoryBlock.Code -> RunnableCode(block.code, block.output, block.stdin, block.runnable)
                is TheoryBlock.Tip -> NoteCard(Icons.Rounded.Lightbulb, block.markdown, PyTheme.colors.gold)
                is TheoryBlock.Warning -> NoteCard(Icons.Rounded.WarningAmber, block.markdown, PyTheme.colors.error)
            }
        }
    }
}

/** Hinweis- oder Warnkasten: zart eingefärbt, mit Symbol statt Emoji. */
@Composable
fun NoteCard(icon: ImageVector, markdown: String, accent: Color) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(PyTheme.shapes.mediumShape)
            .background(accent.copy(alpha = 0.10f))
            .padding(14.dp)
    ) {
        Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.padding(top = 1.dp).size(22.dp))
        Spacer(Modifier.width(12.dp))
        MarkdownText(markdown, style = MaterialTheme.typography.bodyMedium)
    }
}

/** Code-Beispiel mit ▶-Knopf: führt den Code direkt aus und zeigt die echte Ausgabe. */
@Composable
fun RunnableCode(code: String, staticOutput: String?, stdin: List<String>, runnable: Boolean) {
    val python = LocalRunner.current
    val courseId = LocalCourseId.current
    val openInPlayground = LocalOpenInPlayground.current
    val scope = rememberCoroutineScope()
    val feedback = rememberFeedback()
    var output by remember(code) { mutableStateOf(staticOutput) }
    var error by remember(code) { mutableStateOf<String?>(null) }
    var running by remember(code) { mutableStateOf(false) }
    var ran by remember(code) { mutableStateOf(false) }
    val theme = PyTheme.code.theme
    val c = PyTheme.colors
    val noOutputLabel = tr("(keine Ausgabe)", "(no output)")
    val timeLimitLabel = tr("Zeitlimit überschritten", "Time limit exceeded")

    Column(Modifier.fillMaxWidth()) {
        CodeBlock(
            code = code,
            modifier = Modifier.fillMaxWidth(),
            title = tr("beispiel", "example") + "." + courseId.extension,
            actions = {
                CopyCodeAction(code)
                CodeHeaderAction(Icons.AutoMirrored.Rounded.OpenInNew, tr("Im Spielplatz öffnen", "Open in playground"), onClick = { openInPlayground(code, courseId) })
                if (runnable && python != null) {
                    CodeHeaderAction(Icons.Rounded.PlayArrow, tr("Ausführen", "Run"), tint = c.success, onClick = {
                        if (running) return@CodeHeaderAction
                        feedback.run()
                        running = true
                        scope.launch {
                            val shown = StringBuilder()
                            val noOutput = noOutputLabel
                            val timeLimit = timeLimitLabel
                            val r = python.run(code, stdin, timeoutSeconds = 6.0, onOutput = { chunk ->
                                if (chunk.kind == 0 || chunk.kind == 2) synchronized(shown) { shown.append(chunk.text) }
                            })
                            val text = synchronized(shown) { shown.toString() }
                            output = text.trimEnd('\n').ifEmpty { if (r.ok) noOutput else "" }
                            error = r.error ?: if (r.timedOut) timeLimit else null
                            running = false
                            ran = true
                        }
                    })
                }
            },
        )
        AnimatedVisibility(visible = output != null || error != null || running, enter = expandVertically() + fadeIn()) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
                    .clip(PyTheme.shapes.smallShape)
                    .background(theme.gutter)
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        if (ran) tr("Ausgabe (gerade ausgeführt)", "Output (just ran)") else tr("Ausgabe", "Output"),
                        style = MaterialTheme.typography.labelMedium,
                        color = theme.gutterText,
                    )
                    if (running) {
                        Spacer(Modifier.width(8.dp))
                        CircularProgressIndicator(Modifier.height(14.dp).width(14.dp), strokeWidth = 2.dp, color = theme.gutterText)
                    }
                }
                Spacer(Modifier.height(4.dp))
                output?.takeIf { it.isNotEmpty() }?.let { Text(it, style = codeTextStyle(0.95f), color = theme.text) }
                error?.let { Text(it, style = codeTextStyle(0.9f), color = Color(0xFFFF6B6B)) }
            }
        }
    }
}

/** Sprechblase neben Monty. */
@Composable
fun SpeechBubble(text: String, modifier: Modifier = Modifier) {
    val c = PyTheme.colors
    Box(
        modifier
            .clip(PyTheme.shapes.mediumShape)
            .background(c.surface)
            .border(1.dp, c.border, PyTheme.shapes.mediumShape)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        MarkdownText(text, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold))
    }
}
