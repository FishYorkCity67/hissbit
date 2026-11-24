package app.pyquest.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import app.pyquest.data.CourseUnit
import app.pyquest.data.Lesson
import app.pyquest.i18n.tr
import app.pyquest.ui.code.codeTextStyle
import app.pyquest.ui.components.ChunkyButton
import app.pyquest.ui.components.Monty
import app.pyquest.ui.components.MontyMood
import app.pyquest.ui.components.TheoryBlocks
import app.pyquest.ui.components.vector
import app.pyquest.ui.theme.PyTheme
import app.pyquest.ui.theme.ReadingWidth
import app.pyquest.ui.theme.centeredMaxWidth
import app.pyquest.ui.theme.darken
import app.pyquest.ui.theme.lighten
import app.pyquest.ui.theme.unitColors

@Composable
private fun NotesHeader(title: String, onBack: () -> Unit) {
    val c = PyTheme.colors
    Row(Modifier.padding(horizontal = 4.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Rounded.ArrowBack, tr("Zurück", "Back"), tint = c.text) }
        Text(title, style = MaterialTheme.typography.titleLarge, color = c.text, maxLines = 1)
    }
    HorizontalDivider(thickness = 1.dp, color = c.border)
}

@Composable
private fun UnitHero(unit: CourseUnit, subtitle: String, @Suppress("UNUSED_PARAMETER") extension: String) {
    val c = PyTheme.colors
    val (color, _) = unitColors(unit.colorIndex)
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                tr("Einheit ${unit.number}", "Unit ${unit.number}"),
                style = MaterialTheme.typography.labelMedium,
                color = if (c.isDark) color.lighten(0.25f) else color.darken(0.85f),
            )
            Spacer(Modifier.height(2.dp))
            Text(unit.title, style = MaterialTheme.typography.headlineSmall, color = c.text)
            Spacer(Modifier.height(4.dp))
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = c.textMuted)
        }
        Monty(Modifier.padding(start = 8.dp), size = 76.dp, mood = MontyMood.TALKING)
    }
}

/** Alle Lernkarten einer Einheit – zum Nachlesen. */
@Composable
fun UnitNotesScreen(unit: CourseUnit, onBack: () -> Unit, extension: String = "py") {
    val c = PyTheme.colors
    Column(
        Modifier
            .fillMaxSize()
            .background(c.background)
            .statusBarsPadding()
    ) {
        NotesHeader(tr("Notizen", "Notes"), onBack)
        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                Modifier
                    .centeredMaxWidth(ReadingWidth)
                    .padding(16.dp)
                    .navigationBarsPadding()
            ) {
                UnitHero(unit, unit.description, extension)
                val (color, _) = unitColors(unit.colorIndex)
                unit.lessons.filter { it.theory.isNotEmpty() }.forEachIndexed { i, lesson ->
                    Spacer(Modifier.height(26.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(color.copy(alpha = 0.14f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(lesson.icon.vector(), null, tint = color, modifier = Modifier.size(20.dp))
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(tr("Lektion ${i + 1}", "Lesson ${i + 1}"), style = MaterialTheme.typography.labelMedium, color = c.textMuted)
                            Text(lesson.title, style = MaterialTheme.typography.titleLarge, color = c.text)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    TheoryBlocks(lesson.theory)
                }
                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

/** Lernkarte einer einzelnen Lektion (aus dem Pfad-Popup). */
@Composable
fun LessonTheoryScreen(lesson: Lesson, unit: CourseUnit, onBack: () -> Unit, onStart: () -> Unit, extension: String = "py") {
    val c = PyTheme.colors
    Column(
        Modifier
            .fillMaxSize()
            .background(c.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        NotesHeader(lesson.title, onBack)
        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                Modifier
                    .centeredMaxWidth(ReadingWidth)
                    .padding(16.dp)
            ) {
                UnitHero(unit, lesson.subtitle.ifEmpty { lesson.title }, extension)
                Spacer(Modifier.height(16.dp))
                TheoryBlocks(lesson.theory)
                Spacer(Modifier.height(24.dp))
            }
        }
        Box(
            Modifier
                .centeredMaxWidth(ReadingWidth)
                .padding(16.dp)
        ) {
            ChunkyButton(tr("Lektion starten", "Start lesson"), onClick = onStart, modifier = Modifier.fillMaxWidth())
        }
    }
}
