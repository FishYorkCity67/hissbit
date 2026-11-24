package app.pyquest.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.pyquest.ui.ambient.screenBackground
import app.pyquest.data.Course
import app.pyquest.data.CourseUnit
import app.pyquest.data.GlossaryTerm
import app.pyquest.data.RefCategory
import app.pyquest.data.RefEntry
import app.pyquest.data.UserProgress
import app.pyquest.data.isCompleted
import app.pyquest.i18n.Txt
import app.pyquest.i18n.t
import app.pyquest.i18n.tr
import app.pyquest.ui.code.codeTextStyle
import app.pyquest.ui.components.MarkdownText
import app.pyquest.ui.components.RunnableCode
import app.pyquest.ui.components.rememberFeedback
import app.pyquest.ui.components.vector
import app.pyquest.ui.theme.PyTheme
import app.pyquest.ui.theme.ReadingWidth
import app.pyquest.ui.theme.centeredMaxWidth
import app.pyquest.ui.theme.darken
import app.pyquest.ui.theme.lighten
import app.pyquest.ui.theme.onColor
import app.pyquest.ui.theme.unitColors

private enum class KnowledgeTab(val label: Txt) {
    REFERENCE(Txt("Nachschlagen", "Reference")),
    NOTES(Txt("Kurs-Notizen", "Course notes")),
    GLOSSARY(Txt("Glossar", "Glossary")),
    ERRORS(Txt("Fehler-Lexikon", "Error guide")),
}

@Composable
fun KnowledgeScreen(
    course: Course,
    progress: UserProgress,
    onOpenNotes: (CourseUnit) -> Unit,
    reference: List<RefCategory>,
    glossary: List<GlossaryTerm>,
    errors: List<RefEntry>,
    languageName: String = "Python",
) {
    val c = PyTheme.colors
    var tab by rememberSaveable { mutableStateOf(KnowledgeTab.REFERENCE) }
    var query by rememberSaveable { mutableStateOf("") }
    var expanded by rememberSaveable { mutableStateOf<String?>(null) }
    val q = query.trim().lowercase()

    Box(
        Modifier
            .fillMaxSize()
            .screenBackground()
    ) {
        Column(
            Modifier
                .fillMaxHeight()
                .centeredMaxWidth(ReadingWidth)
                .statusBarsPadding()
        ) {
            Column(Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                Text(tr("Wissen", "Library"), style = MaterialTheme.typography.headlineMedium, color = c.text)
                Text(
                    tr("Alles Wichtige zu $languageName – zum Nachschlagen.", "Everything important about $languageName, ready to look up."),
                    style = MaterialTheme.typography.bodyMedium,
                    color = c.textMuted,
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = {
                        Text(
                            if (course.id == app.pyquest.data.CourseId.JAVASCRIPT) tr("Suchen, z.B. „array“, „for“, „map“ …", "Search, e.g. “array”, “for”, “map” …")
                            else tr("Suchen, z.B. „liste“, „for“, „dict“ …", "Search, e.g. “list”, “for”, “dict” …")
                        )
                    },
                    leadingIcon = { Icon(Icons.Rounded.Search, null, tint = c.textMuted) },
                    trailingIcon = {
                        if (query.isNotEmpty()) IconButton(onClick = { query = "" }) { Icon(Icons.Rounded.Clear, tr("Leeren", "Clear"), tint = c.textMuted) }
                    },
                    singleLine = true,
                    shape = CircleShape,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = cardColor(),
                        unfocusedContainerColor = cardColor(),
                        unfocusedBorderColor = c.border,
                        focusedBorderColor = c.primary,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(10.dp))
                Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    KnowledgeTab.entries.forEach { t ->
                        val sel = t == tab
                        val bg by animateColorAsState(if (sel) c.primary else cardColor(), label = "tabBg")
                        Box(
                            Modifier
                                .clip(CircleShape)
                                .background(bg)
                                .border(1.dp, if (sel) c.primary else c.border, CircleShape)
                                .clickable { tab = t }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(t.label.t(), style = MaterialTheme.typography.labelLarge, color = if (sel) c.onPrimary else c.text)
                        }
                    }
                }
            }

            when (tab) {
                KnowledgeTab.REFERENCE -> ReferenceList(reference, q, expanded) { expanded = if (expanded == it) null else it }
                KnowledgeTab.NOTES -> NotesList(course, progress, q, onOpenNotes)
                KnowledgeTab.GLOSSARY -> GlossaryList(glossary, q)
                KnowledgeTab.ERRORS -> EntryList(errors, q, expanded) { expanded = if (expanded == it) null else it }
            }
        }
    }
}

private fun RefEntry.matches(q: String) = q.isEmpty() ||
    title.lowercase().contains(q) || summary.lowercase().contains(q) ||
    keywords.any { it.lowercase().contains(q) } || (code?.lowercase()?.contains(q) == true)

@Composable
private fun ReferenceList(categories: List<RefCategory>, q: String, expanded: String?, onToggle: (String) -> Unit) {
    val c = PyTheme.colors
    LazyColumn(contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 110.dp)) {
        categories.forEach { cat ->
            val entries = cat.entries.filter { it.matches(q) || cat.title.lowercase().contains(q) }
            if (entries.isNotEmpty()) {
                item(key = "cat-${cat.id}") {
                    Row(Modifier.padding(top = 18.dp, bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(cat.icon.vector(), null, tint = c.primaryText, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(cat.title, style = MaterialTheme.typography.titleSmall, color = c.primaryText)
                    }
                }
                items(entries, key = { "ref-${cat.id}-${it.id}" }) { e ->
                    EntryCard(e, expanded == "${cat.id}/${e.id}") { onToggle("${cat.id}/${e.id}") }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
        if (categories.all { cat -> cat.entries.none { it.matches(q) } && !cat.title.lowercase().contains(q) }) {
            item { EmptySearch() }
        }
    }
}

@Composable
private fun EntryList(entries: List<RefEntry>, q: String, expanded: String?, onToggle: (String) -> Unit) {
    val filtered = entries.filter { it.matches(q) }
    LazyColumn(contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 110.dp)) {
        items(filtered, key = { "err-${it.id}" }) { e ->
            EntryCard(e, expanded == "err/${e.id}") { onToggle("err/${e.id}") }
            Spacer(Modifier.height(8.dp))
        }
        if (filtered.isEmpty()) item { EmptySearch() }
    }
}

@Composable
private fun EntryCard(entry: RefEntry, expanded: Boolean, onToggle: () -> Unit) {
    val c = PyTheme.colors
    val feedback = rememberFeedback()
    val border by animateColorAsState(if (expanded) c.primary else c.border, label = "entryBorder")
    Column(
        Modifier
            .fillMaxWidth()
            .clip(PyTheme.shapes.mediumShape)
            .background(cardColor())
            .border(1.dp, border, PyTheme.shapes.mediumShape)
            .clickable {
                feedback.tap()
                onToggle()
            }
            .animateContentSize()
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(entry.title, style = MaterialTheme.typography.titleSmall, color = c.text)
                Spacer(Modifier.height(2.dp))
                MarkdownText(entry.summary, style = MaterialTheme.typography.bodyMedium, color = c.textMuted)
            }
            Icon(if (expanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore, null, tint = c.textMuted)
        }
        AnimatedVisibility(expanded, enter = expandVertically() + fadeIn(), exit = shrinkVertically() + fadeOut()) {
            Column {
                if (entry.body.isNotBlank()) {
                    Spacer(Modifier.height(10.dp))
                    MarkdownText(entry.body, style = MaterialTheme.typography.bodyMedium)
                }
                if (entry.code != null) {
                    Spacer(Modifier.height(10.dp))
                    RunnableCode(entry.code, null, emptyList(), runnable = true)
                }
            }
        }
    }
}

@Composable
private fun NotesList(course: Course, progress: UserProgress, q: String, onOpenNotes: (CourseUnit) -> Unit) {
    val c = PyTheme.colors
    val units = course.units.filter { u ->
        q.isEmpty() || u.title.lowercase().contains(q) || u.lessons.any { it.title.lowercase().contains(q) }
    }
    LazyColumn(contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 110.dp)) {
        items(units, key = { "notes-${it.id}" }) { u ->
            val (color, _) = unitColors(u.colorIndex)
            val done = u.lessons.count { progress.isCompleted(it.id) }
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(PyTheme.shapes.mediumShape)
                    .background(cardColor())
                    .border(1.dp, c.border, PyTheme.shapes.mediumShape)
                    .clickable { onOpenNotes(u) }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("${u.number}", style = MaterialTheme.typography.titleMedium, color = if (c.isDark) color.lighten(0.2f) else color.darken(0.85f))
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(u.title, style = MaterialTheme.typography.titleSmall, color = c.text)
                    Text(
                        u.lessons.filter { it.theory.isNotEmpty() }.joinToString(" · ") { it.title },
                        style = MaterialTheme.typography.bodySmall,
                        color = c.textMuted,
                        maxLines = 2,
                    )
                }
                Text("$done/${u.lessons.size}", style = MaterialTheme.typography.labelMedium, color = c.textMuted)
                Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null, tint = c.textMuted)
            }
            Spacer(Modifier.height(8.dp))
        }
        if (units.isEmpty()) item { EmptySearch() }
    }
}

@Composable
private fun GlossaryList(terms: List<GlossaryTerm>, q: String) {
    val c = PyTheme.colors
    val filtered = terms.filter { q.isEmpty() || it.term.lowercase().contains(q) || it.meaning.lowercase().contains(q) }
        .sortedBy { it.term.lowercase() }
    LazyColumn(contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 110.dp)) {
        var lastLetter: Char? = null
        filtered.forEach { t ->
            val letter = t.term.first().uppercaseChar()
            if (letter != lastLetter) {
                lastLetter = letter
                item(key = "letter-$letter") {
                    Text(
                        letter.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        color = c.primaryText,
                        modifier = Modifier.padding(top = 14.dp, bottom = 4.dp),
                    )
                }
            }
            item(key = "term-${t.term}") {
                Column(Modifier.padding(vertical = 6.dp)) {
                    Text(t.term, style = MaterialTheme.typography.titleSmall, color = c.text)
                    MarkdownText(t.meaning, style = MaterialTheme.typography.bodyMedium, color = c.textMuted)
                }
            }
        }
        if (filtered.isEmpty()) item { EmptySearch() }
    }
}

@Composable
private fun EmptySearch() {
    Text(
        tr("Nichts gefunden. Versuch es mit einem anderen Suchwort.", "Nothing found. Try another search term."),
        style = MaterialTheme.typography.bodyMedium,
        color = PyTheme.colors.textMuted,
        modifier = Modifier.padding(24.dp),
    )
}
