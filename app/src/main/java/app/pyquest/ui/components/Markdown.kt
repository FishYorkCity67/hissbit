package app.pyquest.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import app.pyquest.ui.theme.PyTheme

/**
 * Minimaler Markdown-Ersatz für Lerntexte: **fett**, *kursiv*, `code`
 * und Aufzählungen mit "- ".
 */
fun miniMarkdown(
    text: String,
    textColor: Color,
    codeColor: Color,
    codeBackground: Color,
    codeFamily: FontFamily,
    boldColor: Color = textColor,
    codeFeatures: String? = "calt 0, liga 0",
): AnnotatedString = buildAnnotatedString {
    val lines = text.lines()
    lines.forEachIndexed { index, rawLine ->
        val line = when {
            rawLine.startsWith("- ") -> "•  " + rawLine.removePrefix("- ")
            else -> rawLine
        }
        var i = 0
        while (i < line.length) {
            val c = line[i]
            when {
                // ``code mit ` darin`` – z. B. JavaScript-Template-Strings
                c == '`' && i + 1 < line.length && line[i + 1] == '`' && line.indexOf("``", i + 2) > i -> {
                    val end = line.indexOf("``", i + 2)
                    var inner = line.substring(i + 2, end)
                    if (inner.length >= 2 && inner.startsWith(" ") && inner.endsWith(" ")) inner = inner.substring(1, inner.length - 1)
                    pushStyle(
                        SpanStyle(
                            fontFamily = codeFamily,
                            color = codeColor,
                            background = codeBackground,
                            fontWeight = FontWeight.SemiBold,
                            fontFeatureSettings = codeFeatures,
                        )
                    )
                    append(" ")
                    append(inner)
                    append(" ")
                    pop()
                    i = end + 2
                }
                c == '`' -> {
                    val end = line.indexOf('`', i + 1)
                    if (end > i) {
                        pushStyle(
                            SpanStyle(
                                fontFamily = codeFamily,
                                color = codeColor,
                                background = codeBackground,
                                fontWeight = FontWeight.SemiBold,
                                fontFeatureSettings = codeFeatures,
                            )
                        )
                        append(" ")
                        append(line.substring(i + 1, end))
                        append(" ")
                        pop()
                        i = end + 1
                    } else {
                        append(c); i++
                    }
                }
                c == '*' && i + 1 < line.length && line[i + 1] == '*' -> {
                    val end = line.indexOf("**", i + 2)
                    if (end > i) {
                        pushStyle(SpanStyle(fontWeight = FontWeight.ExtraBold, color = boldColor))
                        append(line.substring(i + 2, end))
                        pop()
                        i = end + 2
                    } else {
                        append(c); i++
                    }
                }
                c == '*' && i + 1 < line.length && line[i + 1] != ' ' -> {
                    val end = line.indexOf('*', i + 1)
                    if (end > i + 1) {
                        pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
                        append(line.substring(i + 1, end))
                        pop()
                        i = end + 1
                    } else {
                        append(c); i++
                    }
                }
                else -> {
                    append(c); i++
                }
            }
        }
        if (index < lines.lastIndex) append('\n')
    }
}

@Composable
fun MarkdownText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    color: Color = PyTheme.colors.text,
    textAlign: TextAlign? = null,
) {
    val c = PyTheme.colors
    val code = PyTheme.code
    val annotated = remember(text, color, c, code.family, code.fontFeatures) {
        miniMarkdown(
            text = text,
            textColor = color,
            codeColor = if (c.isDark) Color(0xFFFFD27A) else Color(0xFFB4235A),
            codeBackground = if (c.isDark) Color(0x22FFFFFF) else Color(0x14000000),
            codeFamily = code.family,
            boldColor = color,
            codeFeatures = code.fontFeatures,
        )
    }
    Text(annotated, modifier = modifier, style = style, color = color, textAlign = textAlign)
}
