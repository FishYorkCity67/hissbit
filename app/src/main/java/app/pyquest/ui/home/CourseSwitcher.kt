package app.pyquest.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import app.pyquest.data.Course
import app.pyquest.data.CourseId
import app.pyquest.data.UserProgress
import app.pyquest.data.completedLessonCount
import app.pyquest.i18n.tr
import app.pyquest.ui.components.LanguageBadge
import app.pyquest.ui.components.PyProgressBar
import app.pyquest.ui.components.rememberFeedback
import app.pyquest.ui.theme.PyTheme
import app.pyquest.ui.theme.lighten
import app.pyquest.ui.theme.onColor
import app.pyquest.ui.theme.unitColors

/** Kleiner Chip in der Kopfzeile: zeigt den aktiven Kurs und öffnet den Kurswechsel. */
@Composable
fun CourseChip(course: CourseId, onClick: () -> Unit) {
    val c = PyTheme.colors
    val feedback = rememberFeedback()
    Row(
        Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable {
                feedback.tap()
                onClick()
            }
            .padding(start = 6.dp, end = 4.dp, top = 6.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LanguageBadge(course, size = 22.dp)
        Spacer(Modifier.width(8.dp))
        Text(course.displayName, style = MaterialTheme.typography.titleSmall, color = c.text)
        Icon(Icons.Rounded.KeyboardArrowDown, null, tint = c.textMuted, modifier = Modifier.size(20.dp))
    }
}

/** Auswahl des Kurses – mit Fortschritt je Kurs. */
@Composable
fun CourseSwitcherSheet(
    courses: List<Pair<CourseId, Course>>,
    current: CourseId,
    progress: UserProgress,
    onPick: (CourseId) -> Unit,
    onDismiss: () -> Unit,
) {
    val c = PyTheme.colors
    val state = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = state, containerColor = c.surface) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(tr("Kurs wählen", "Choose a course"), style = MaterialTheme.typography.titleLarge, color = c.text)
            Text(
                tr(
                    "Dein Fortschritt, dein Streak und deine Bits gelten für alle Kurse gemeinsam.",
                    "Your progress, streak and bits are shared across all courses.",
                ),
                style = MaterialTheme.typography.bodySmall,
                color = c.textMuted,
            )
            Spacer(Modifier.height(4.dp))
            courses.forEach { (id, course) ->
                val done = course.completedLessonCount(progress)
                val sel = id == current
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(PyTheme.shapes.largeShape)
                        .background(if (sel) c.primarySoft else c.surface)
                        .border(if (sel) 2.dp else 1.dp, if (sel) c.primary else c.border, PyTheme.shapes.largeShape)
                        .clickable { onPick(id) }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    LanguageBadge(id, size = 44.dp)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(id.displayName, style = MaterialTheme.typography.titleMedium, color = c.text)
                        Spacer(Modifier.height(4.dp))
                        PyProgressBar(done.toFloat() / course.lessons.size, color = c.primary, height = 6.dp, modifier = Modifier.fillMaxWidth())
                        Spacer(Modifier.height(3.dp))
                        Text(
                            tr("$done von ${course.lessons.size} Lektionen", "$done of ${course.lessons.size} lessons"),
                            style = MaterialTheme.typography.bodySmall,
                            color = c.textMuted,
                        )
                    }
                    if (sel) {
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.Rounded.Check, null, tint = c.primaryText)
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}
