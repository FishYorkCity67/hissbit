package app.pyquest.ui.settings

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CloudDownload
import androidx.compose.material.icons.rounded.CloudUpload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.pyquest.data.AmbientBackground
import app.pyquest.ui.ambient.screenBackground
import app.pyquest.ui.ambient.AmbientBackdrop
import app.pyquest.BuildConfig
import app.pyquest.audio.Sfx
import app.pyquest.audio.SoundFx
import app.pyquest.data.Accent
import app.pyquest.data.AnimationLevel
import app.pyquest.data.AppSettings
import app.pyquest.data.BackupFile
import app.pyquest.data.CodeFont
import app.pyquest.data.CornerStyle
import app.pyquest.data.EditorThemeId
import app.pyquest.data.ImportResult
import app.pyquest.data.MascotColor
import app.pyquest.data.PathStyle
import app.pyquest.data.ThemeMode
import app.pyquest.data.UiFont
import app.pyquest.i18n.AppLanguage
import app.pyquest.i18n.LanguagePref
import app.pyquest.i18n.LocalLanguage
import app.pyquest.i18n.tr
import app.pyquest.notify.Reminders
import app.pyquest.ui.code.CodeBlock
import app.pyquest.ui.code.highlightPython
import app.pyquest.ui.components.ChunkyButton
import app.pyquest.ui.components.Monty
import app.pyquest.ui.components.MontyMood
import app.pyquest.ui.components.rememberFeedback
import app.pyquest.ui.home.cardColor
import app.pyquest.ui.theme.EditorThemes
import app.pyquest.ui.theme.Fonts
import app.pyquest.ui.theme.PyTheme
import app.pyquest.ui.theme.ReadingWidth
import app.pyquest.ui.theme.accentColors
import app.pyquest.ui.theme.centeredMaxWidth
import app.pyquest.ui.theme.mascotPalette
import app.pyquest.ui.theme.onColor
import app.pyquest.ui.theme.toShapes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private const val PREVIEW_CODE = """def hallo(name):
    # Begrüßt jemanden
    for i in range(3):
        print(f"Hallo {name}!", i)
    return True"""

private const val PREVIEW_CODE_EN = """def hello(name):
    # Greets someone
    for i in range(3):
        print(f"Hello {name}!", i)
    return True"""

/** Was die Einstellungen mit Sicherungsdateien machen dürfen (kommt vom Repository). */
class BackupActions(
    val export: suspend () -> String,
    val parse: (String) -> ImportResult,
    val restore: (BackupFile) -> Unit,
)

@Composable
fun SettingsScreen(
    settings: AppSettings,
    pythonVersion: String?,
    onChange: ((AppSettings) -> AppSettings) -> Unit,
    onResetProgress: () -> Unit,
    onBack: () -> Unit,
    backup: BackupActions,
) {
    val c = PyTheme.colors
    val lang = LocalLanguage.current
    val context = LocalContext.current
    val feedback = rememberFeedback()
    val scope = rememberCoroutineScope()
    var confirmReset by remember { mutableStateOf(false) }
    var pickTime by remember { mutableStateOf(false) }
    var pendingRestore by remember { mutableStateOf<BackupFile?>(null) }
    var notice by remember { mutableStateOf<String?>(null) }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        onChange { it.copy(reminderEnabled = granted) }
    }
    val savedText = tr("Sicherung gespeichert. Bewahre die Datei gut auf!", "Backup saved. Keep the file somewhere safe!")
    val failedText = tr("Das hat leider nicht geklappt.", "Sorry, that didn't work.")
    val notBackupText = tr("Das ist keine Hissbit-Sicherung.", "That's not a Hissbit backup.")
    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch {
            val ok = runCatching {
                val json = backup.export()
                withContext(Dispatchers.IO) {
                    context.contentResolver.openOutputStream(uri)?.use { it.write(json.toByteArray(Charsets.UTF_8)) } ?: error("no stream")
                }
            }.isSuccess
            notice = if (ok) savedText else failedText
            if (ok) feedback.correct() else feedback.wrong()
        }
    }
    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch {
            val text = runCatching {
                withContext(Dispatchers.IO) {
                    context.contentResolver.openInputStream(uri)?.use { it.readBytes().toString(Charsets.UTF_8) }
                }
            }.getOrNull()
            when (val r = text?.let(backup.parse)) {
                is ImportResult.Ok -> pendingRestore = r.backup
                else -> {
                    notice = if (text == null) failedText else notBackupText
                    feedback.wrong()
                }
            }
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .screenBackground()
            .statusBarsPadding()
    ) {
        Row(
            Modifier
                .centeredMaxWidth(ReadingWidth)
                .padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Rounded.ArrowBack, tr("Zurück", "Back"), tint = c.text) }
            Text(tr("Einstellungen", "Settings"), style = MaterialTheme.typography.titleLarge, color = c.text)
        }
        HorizontalDivider(thickness = 1.dp, color = c.border)
        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                Modifier
                    .centeredMaxWidth(ReadingWidth)
                    .padding(horizontal = 16.dp)
                    .navigationBarsPadding()
            ) {
                // ---------------------------------------------------------- Sprache
                Section(tr("Sprache", "Language"))
                Label(tr("App-Sprache", "App language"))
                val languageOptions = listOf(
                    LanguagePref.SYSTEM to tr("Wie das Gerät", "Device default"),
                    LanguagePref.DE to "Deutsch",
                    LanguagePref.EN to "English",
                )
                ChipRow(languageOptions, languageOptions.first { it.first == settings.language }, { it.second }) { v ->
                    onChange { it.copy(language = v.first) }
                }
                Text(
                    tr(
                        "Gilt für die Oberfläche und die Kurse. Dein Fortschritt bleibt beim Wechseln erhalten.",
                        "Applies to the interface and the courses. Your progress is kept when you switch.",
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = c.textMuted,
                    modifier = Modifier.padding(top = 6.dp),
                )

                // ---------------------------------------------------------- Darstellung
                Section(tr("Darstellung", "Appearance"))
                Label(tr("Design", "Theme"))
                ChipRow(ThemeMode.entries, settings.themeMode, { it.label.of(lang) }) { v -> onChange { it.copy(themeMode = v) } }

                Label(tr("Akzentfarbe", "Accent colour"))
                val accents = Accent.entries.filter { it != Accent.DYNAMIC || Build.VERSION.SDK_INT >= 31 }
                FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    accents.forEach { a ->
                        ColorDot(
                            color = accentColors(a, c.isDark).primary,
                            second = accentColors(a, c.isDark).secondary,
                            selected = settings.accent == a,
                            dynamic = a == Accent.DYNAMIC,
                            label = a.label.of(lang),
                        ) {
                            feedback.select()
                            onChange { it.copy(accent = a) }
                        }
                    }
                }

                Label(tr("Hintergrund", "Background"))
                Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AmbientBackground.entries.forEach { kind ->
                        val selected = settings.ambient == kind
                        Column(
                            Modifier
                                .width(124.dp)
                                .clip(PyTheme.shapes.mediumShape)
                                .border(if (selected) 2.5.dp else 1.dp, if (selected) c.primary else c.border, PyTheme.shapes.mediumShape)
                                .clickable {
                                    feedback.select()
                                    onChange { it.copy(ambient = kind) }
                                }
                        ) {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height(92.dp)
                                    .background(c.background),
                                contentAlignment = Alignment.Center,
                            ) {
                                if (kind == AmbientBackground.NONE) {
                                    Icon(Icons.Rounded.Block, null, tint = c.textMuted, modifier = Modifier.size(26.dp))
                                } else {
                                    AmbientBackdrop(kind, Modifier.fillMaxSize(), animated = false, scale = 0.34f, sceneHeight = 0.9f, lift = 0.03f)
                                }
                            }
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .height(40.dp)
                                    .background(c.surface)
                                    .padding(horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(kind.label.of(lang), style = MaterialTheme.typography.labelMedium, color = c.text, maxLines = 2, modifier = Modifier.weight(1f))
                                if (selected) Icon(Icons.Rounded.Check, null, tint = c.primaryText, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
                Text(
                    tr(
                        "Läuft ruhig hinter den Hauptseiten – nicht in Lektionen und im Code-Editor. Bei „Animationen: Aus“ bleibt das Bild stehen.",
                        "Plays quietly behind the main pages – not in lessons or the code editor. With “Animations: Off” the picture stays still.",
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = c.textMuted,
                    modifier = Modifier.padding(top = 6.dp),
                )

                Label(tr("Schriftart", "Font"))
                ChipRow(UiFont.entries, settings.uiFont, { it.label.of(lang) }, fontFor = { Fonts.display(it) }) { v -> onChange { it.copy(uiFont = v) } }

                Label(tr("Textgröße: ${(settings.textScale * 100).toInt()} %", "Text size: ${(settings.textScale * 100).toInt()}%"))
                var scale by remember(settings.textScale) { mutableFloatStateOf(settings.textScale) }
                Slider(
                    value = scale,
                    onValueChange = { scale = it },
                    onValueChangeFinished = { onChange { it.copy(textScale = (scale * 20).toInt() / 20f) } },
                    valueRange = 0.85f..1.3f,
                    steps = 8,
                    colors = SliderDefaults.colors(thumbColor = c.primary, activeTrackColor = c.primary, inactiveTrackColor = c.locked),
                )

                Label(tr("Ecken", "Corners"))
                ChipRow(CornerStyle.entries, settings.corners, { it.label.of(lang) }, shapeFor = { it.toShapes().medium }) { v -> onChange { it.copy(corners = v) } }

                Label(tr("Lernpfad", "Learning path"))
                ChipRow(PathStyle.entries, settings.pathStyle, { it.label.of(lang) }) { v -> onChange { it.copy(pathStyle = v) } }

                ToggleRow(tr("Monty anzeigen", "Show Monty"), tr("Das Maskottchen auf dem Lernpfad", "The mascot on the learning path"), settings.showMascot) { v ->
                    onChange { it.copy(showMascot = v) }
                }
                Label(tr("Montys Farbe", "Monty's colour"))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Monty(size = 72.dp, mood = MontyMood.HAPPY, palette = mascotPalette(settings.mascotColor))
                    Spacer(Modifier.width(12.dp))
                    FlowRow(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        MascotColor.entries.forEach { m ->
                            val p = mascotPalette(m)
                            ColorDot(p.body, p.belly, settings.mascotColor == m, dynamic = false, label = null, size = 38) {
                                feedback.select()
                                onChange { it.copy(mascotColor = m) }
                            }
                        }
                    }
                }

                // ---------------------------------------------------------- Editor
                Section(tr("Code-Editor", "Code editor"))
                Label(tr("Farbschema", "Colour scheme"))
                Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    EditorThemeId.entries.forEach { id ->
                        val theme = EditorThemes.resolve(id, c.isDark)
                        val selected = settings.editorTheme == id
                        Column(
                            Modifier
                                .width(150.dp)
                                .clip(PyTheme.shapes.mediumShape)
                                .border(if (selected) 2.5.dp else 1.dp, if (selected) c.primary else c.border, PyTheme.shapes.mediumShape)
                                .clickable {
                                    feedback.select()
                                    onChange { it.copy(editorTheme = id) }
                                }
                        ) {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height(70.dp)
                                    .background(theme.background)
                                    .padding(8.dp)
                            ) {
                                Text(
                                    highlightPython("for x in range(3):\n    print(\"Hi\", x)", theme),
                                    style = PyTheme.code.let { cs -> MaterialTheme.typography.bodySmall.copy(fontFamily = cs.family) },
                                    softWrap = false,
                                )
                            }
                            Row(Modifier.fillMaxWidth().background(c.surface).padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(id.label.of(lang), style = MaterialTheme.typography.labelMedium, color = c.text, modifier = Modifier.weight(1f))
                                if (selected) Icon(Icons.Rounded.Check, null, tint = c.primaryText, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
                Label(tr("Code-Schrift", "Code font"))
                ChipRow(CodeFont.entries, settings.codeFont, { it.label.of(lang) }, fontFor = { Fonts.code(it) }) { v -> onChange { it.copy(codeFont = v) } }
                Label(tr("Code-Schriftgröße: ${settings.codeFontSize} sp", "Code font size: ${settings.codeFontSize} sp"))
                var codeSize by remember(settings.codeFontSize) { mutableFloatStateOf(settings.codeFontSize.toFloat()) }
                Slider(
                    value = codeSize,
                    onValueChange = { codeSize = it },
                    onValueChangeFinished = { onChange { it.copy(codeFontSize = codeSize.toInt()) } },
                    valueRange = 11f..20f,
                    steps = 8,
                    colors = SliderDefaults.colors(thumbColor = c.primary, activeTrackColor = c.primary, inactiveTrackColor = c.locked),
                )
                CodeBlock(
                    if (lang == AppLanguage.EN) PREVIEW_CODE_EN else PREVIEW_CODE,
                    modifier = Modifier.fillMaxWidth(),
                    lineNumbers = settings.lineNumbers,
                    title = tr("vorschau.py", "preview.py"),
                )
                ToggleRow(tr("Zeilennummern", "Line numbers"), null, settings.lineNumbers) { v -> onChange { it.copy(lineNumbers = v) } }
                ToggleRow(
                    tr("Automatisch einrücken", "Auto-indent"),
                    tr("Nach einem Doppelpunkt oder einer Klammer rückt der Editor die nächste Zeile ein", "After a colon or bracket the editor indents the next line"),
                    settings.autoIndent,
                ) { v -> onChange { it.copy(autoIndent = v) } }

                // ---------------------------------------------------------- Feedback
                Section(tr("Animationen & Feedback", "Animations & feedback"))
                Label(tr("Animationen", "Animations"))
                ChipRow(AnimationLevel.entries, settings.animations, { it.label.of(lang) }) { v -> onChange { it.copy(animations = v) } }
                ToggleRow(tr("Soundeffekte", "Sound effects"), null, settings.sounds) { v ->
                    onChange { it.copy(sounds = v) }
                    if (v) {
                        SoundFx.enabled = true
                        SoundFx.play(Sfx.CORRECT)
                    }
                }
                ToggleRow(tr("Vibration", "Vibration"), tr("Haptisches Feedback bei Antworten", "Haptic feedback for answers"), settings.haptics) { v ->
                    onChange { it.copy(haptics = v) }
                }
                ToggleRow(tr("Bit-Regen", "Bit confetti"), tr("Feiern beim Abschließen von Lektionen", "Celebrate when you finish lessons"), settings.confetti) { v ->
                    onChange { it.copy(confetti = v) }
                }

                // ---------------------------------------------------------- Lernen
                Section(tr("Lernen", "Learning"))
                Label(tr("Tagesziel", "Daily goal"))
                val goals = listOf(
                    10 to tr("Locker", "Casual"),
                    20 to tr("Normal", "Regular"),
                    30 to tr("Ernsthaft", "Serious"),
                    50 to tr("Intensiv", "Intense"),
                )
                ChipRow(goals, goals.firstOrNull { it.first == settings.dailyGoal } ?: goals[1], { "${it.second} · ${it.first} XP" }) { v ->
                    onChange { it.copy(dailyGoal = v.first) }
                }
                ToggleRow(
                    tr("Herzen-System", "Hearts"),
                    tr("Fehler kosten Herzen. Aus = entspannter Modus ohne Limit", "Mistakes cost hearts. Off = relaxed mode without limits"),
                    settings.heartsEnabled,
                ) { v -> onChange { it.copy(heartsEnabled = v) } }
                ToggleRow(
                    tr("Lernkarten zeigen", "Show lesson notes"),
                    tr("Vor neuen Lektionen die Erklärung anzeigen", "Show the explanation before new lessons"),
                    settings.showTheory,
                ) { v -> onChange { it.copy(showTheory = v) } }
                ToggleRow(
                    tr("Tägliche Erinnerung", "Daily reminder"),
                    tr("Benachrichtigung, wenn du heute noch nicht gelernt hast", "A notification if you haven't learned yet today"),
                    settings.reminderEnabled,
                ) { v ->
                    if (v && Build.VERSION.SDK_INT >= 33 && !Reminders.hasPermission(context)) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        onChange { it.copy(reminderEnabled = v) }
                    }
                }
                if (settings.reminderEnabled) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(PyTheme.shapes.mediumShape)
                            .clickable { pickTime = true }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(tr("Uhrzeit", "Time"), style = MaterialTheme.typography.bodyLarge, color = c.text, modifier = Modifier.weight(1f))
                        Text(
                            tr("%02d:%02d Uhr", "%02d:%02d").format(settings.reminderHour, settings.reminderMinute),
                            style = MaterialTheme.typography.titleMedium,
                            color = c.primaryText,
                        )
                    }
                }

                // ---------------------------------------------------------- Daten
                Section(tr("Daten & Sicherung", "Data & backup"))
                Text(
                    tr(
                        "Updates behalten deinen Fortschritt automatisch. Für ein neues Handy oder eine Neuinstallation kannst du ihn hier als Datei sichern.",
                        "Updates keep your progress automatically. For a new phone or a fresh install you can save it as a file here.",
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = c.textMuted,
                    modifier = Modifier.padding(bottom = 10.dp),
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ChunkyButton(
                        tr("Sichern", "Back up"),
                        icon = Icons.Rounded.CloudUpload,
                        onClick = { exportLauncher.launch("hissbit-backup-${LocalDate.now()}.json") },
                        modifier = Modifier.weight(1f),
                    )
                    ChunkyButton(
                        tr("Wiederherstellen", "Restore"),
                        icon = Icons.Rounded.CloudDownload,
                        onClick = { importLauncher.launch(arrayOf("application/json", "text/plain", "application/octet-stream")) },
                        outlined = true,
                        modifier = Modifier.weight(1f),
                    )
                }
                notice?.let {
                    Spacer(Modifier.height(8.dp))
                    Text(it, style = MaterialTheme.typography.bodySmall, color = c.primaryText)
                }
                Spacer(Modifier.height(14.dp))
                ChunkyButton(
                    tr("Fortschritt zurücksetzen", "Reset progress"),
                    onClick = { confirmReset = true },
                    outlined = true,
                    color = c.error,
                    modifier = Modifier.fillMaxWidth(),
                )

                Section(tr("Über Hissbit", "About Hissbit"))
                Text(
                    "Hissbit ${BuildConfig.VERSION_NAME}\n" +
                        tr(
                            "Python ${pythonVersion ?: "3.14"} läuft direkt auf deinem Gerät – komplett offline.\n\n",
                            "Python ${pythonVersion ?: "3.14"} runs right on your device – fully offline.\n\n",
                        ) +
                        tr(
                            "Enthalten: CPython (PSF-Lizenz) über Chaquopy (MIT-Lizenz), Schriftarten Nunito, Lexend, JetBrains Mono, Fira Code und Source Code Pro (SIL Open Font License). Alle Töne und Grafiken wurden für Hissbit selbst erzeugt.",
                            "Includes CPython (PSF licence) via Chaquopy (MIT licence) and the fonts Nunito, Lexend, JetBrains Mono, Fira Code and Source Code Pro (SIL Open Font License). All sounds and graphics were made for Hissbit.",
                        ),
                    style = MaterialTheme.typography.bodySmall,
                    color = c.textMuted,
                )
                Spacer(Modifier.height(40.dp))
            }
        }
    }

    if (confirmReset) {
        AlertDialog(
            onDismissRequest = { confirmReset = false },
            icon = { Monty(size = 72.dp, mood = MontyMood.SURPRISED) },
            title = { Text(tr("Alles zurücksetzen?", "Reset everything?"), textAlign = TextAlign.Center) },
            text = {
                Text(
                    tr(
                        "Dein gesamter Lernfortschritt, XP, Streak und Erfolge werden gelöscht. Deine Einstellungen bleiben erhalten. Tipp: Sichere vorher deinen Fortschritt.",
                        "All your progress, XP, streak and achievements will be deleted. Your settings stay. Tip: back up your progress first.",
                    ),
                    textAlign = TextAlign.Center,
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    confirmReset = false
                    onResetProgress()
                }) { Text(tr("Zurücksetzen", "Reset"), color = c.error) }
            },
            dismissButton = { TextButton(onClick = { confirmReset = false }) { Text(tr("Abbrechen", "Cancel")) } },
            containerColor = c.surface,
        )
    }

    pendingRestore?.let { b ->
        val date = if (b.exportedAt > 0) {
            val pattern = if (lang == AppLanguage.EN) "MMMM d, yyyy, HH:mm" else "d. MMMM yyyy, HH:mm"
            Instant.ofEpochMilli(b.exportedAt).atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(pattern, lang.locale))
        } else "?"
        AlertDialog(
            onDismissRequest = { pendingRestore = null },
            icon = { Monty(size = 72.dp, mood = MontyMood.SURPRISED) },
            title = { Text(tr("Sicherung wiederherstellen?", "Restore this backup?"), textAlign = TextAlign.Center) },
            text = {
                Text(
                    tr(
                        "Gesichert am $date\n${b.progress.totalXp} XP · ${b.progress.lessons.size} Lektionen · ${b.progress.streak} Tage Streak\n\nDein aktueller Stand wird dadurch ersetzt.",
                        "Saved on $date\n${b.progress.totalXp} XP · ${b.progress.lessons.size} lessons · ${b.progress.streak}-day streak\n\nThis replaces your current progress.",
                    ),
                    textAlign = TextAlign.Center,
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    backup.restore(b)
                    pendingRestore = null
                    notice = null
                    feedback.correct()
                }) { Text(tr("Wiederherstellen", "Restore"), color = c.primaryText) }
            },
            dismissButton = { TextButton(onClick = { pendingRestore = null }) { Text(tr("Abbrechen", "Cancel")) } },
            containerColor = c.surface,
        )
    }

    if (pickTime) {
        val state = rememberTimePickerState(settings.reminderHour, settings.reminderMinute, is24Hour = true)
        AlertDialog(
            onDismissRequest = { pickTime = false },
            title = { Text(tr("Erinnerungszeit", "Reminder time")) },
            text = {
                TimePicker(
                    state = state,
                    colors = TimePickerDefaults.colors(
                        selectorColor = c.primary,
                        timeSelectorSelectedContainerColor = c.primarySoft,
                        timeSelectorSelectedContentColor = c.primaryText,
                        clockDialColor = c.surfaceAlt,
                    ),
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onChange { it.copy(reminderHour = state.hour, reminderMinute = state.minute) }
                    pickTime = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { pickTime = false }) { Text(tr("Abbrechen", "Cancel")) } },
            containerColor = c.surface,
        )
    }
}

@Composable
private fun Section(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.titleLarge,
        color = PyTheme.colors.text,
        modifier = Modifier.padding(top = 30.dp, bottom = 2.dp),
    )
}

@Composable
private fun Label(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleSmall,
        color = PyTheme.colors.text,
        modifier = Modifier.padding(top = 14.dp, bottom = 8.dp),
    )
}

@Composable
private fun <T> ChipRow(
    options: List<T>,
    selected: T,
    label: (T) -> String,
    fontFor: ((T) -> FontFamily)? = null,
    shapeFor: ((T) -> Dp)? = null,
    onSelect: (T) -> Unit,
) {
    val c = PyTheme.colors
    val feedback = rememberFeedback()
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { o ->
            val sel = o == selected
            val shape = shapeFor?.let { RoundedCornerShape(it(o)) } ?: CircleShape
            Box(
                Modifier
                    .clip(shape)
                    .background(if (sel) c.primarySoft else cardColor())
                    .border(if (sel) 2.dp else 1.dp, if (sel) c.primary else c.border, shape)
                    .clickable {
                        feedback.select()
                        onSelect(o)
                    }
                    .padding(horizontal = 14.dp, vertical = 9.dp)
            ) {
                Text(
                    label(o),
                    style = MaterialTheme.typography.labelLarge.let { s -> fontFor?.let { f -> s.copy(fontFamily = f(o)) } ?: s },
                    color = if (sel) c.primaryText else c.text,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun ColorDot(
    color: Color,
    second: Color,
    selected: Boolean,
    dynamic: Boolean,
    label: String?,
    size: Int = 46,
    onClick: () -> Unit,
) {
    val c = PyTheme.colors
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier
                .size(size.dp)
                .clip(CircleShape)
                .border(3.dp, if (selected) c.text else Color.Transparent, CircleShape)
                .padding(4.dp)
                .clip(CircleShape)
                .background(color)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            if (dynamic) Icon(Icons.Rounded.Palette, null, tint = Color.White, modifier = Modifier.size(20.dp))
            else if (selected) Icon(Icons.Rounded.Check, null, tint = color.onColor(), modifier = Modifier.size(20.dp))
        }
        if (label != null) {
            Spacer(Modifier.height(4.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, color = if (selected) c.text else c.textMuted)
        }
    }
}

@Composable
private fun ToggleRow(title: String, subtitle: String?, checked: Boolean, onChange: (Boolean) -> Unit) {
    val c = PyTheme.colors
    Row(
        Modifier
            .fillMaxWidth()
            .clip(PyTheme.shapes.mediumShape)
            .clickable { onChange(!checked) }
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, color = c.text, fontWeight = FontWeight.SemiBold)
            if (subtitle != null) Text(subtitle, style = MaterialTheme.typography.bodySmall, color = c.textMuted)
        }
        Spacer(Modifier.width(12.dp))
        Switch(
            checked = checked,
            onCheckedChange = onChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = c.primary,
                uncheckedThumbColor = c.lockedIcon,
                uncheckedTrackColor = c.locked,
                uncheckedBorderColor = c.border,
            ),
        )
    }
}
