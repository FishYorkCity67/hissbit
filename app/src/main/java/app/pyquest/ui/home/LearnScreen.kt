package app.pyquest.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import app.pyquest.ui.ambient.screenBackground
import app.pyquest.data.AppSettings
import app.pyquest.data.CodeExercise
import app.pyquest.data.PythonTip
import app.pyquest.data.Course
import app.pyquest.data.CourseUnit
import app.pyquest.data.Lesson
import app.pyquest.data.LessonKind
import app.pyquest.data.PathStyle
import app.pyquest.data.UserProgress
import app.pyquest.data.currentLesson
import app.pyquest.data.isCompleted
import app.pyquest.data.isUnlocked
import app.pyquest.data.xpOn
import app.pyquest.i18n.tr
import app.pyquest.ui.code.codeTextStyle
import app.pyquest.ui.components.ChunkyButton
import app.pyquest.ui.components.ChunkySurface
import app.pyquest.ui.components.Monty
import app.pyquest.ui.components.MontyMood
import app.pyquest.ui.components.PyProgressBar
import app.pyquest.ui.components.clickableNoRipple
import app.pyquest.ui.components.rememberBob
import app.pyquest.ui.components.rememberFeedback
import app.pyquest.ui.components.rememberPulse
import app.pyquest.ui.components.vector
import app.pyquest.data.AmbientBackground
import app.pyquest.ui.theme.Adaptive
import app.pyquest.ui.theme.LocalAmbient
import app.pyquest.ui.theme.LocalLayoutPrefs
import app.pyquest.ui.theme.LocalMascotPrefs
import app.pyquest.ui.theme.PyTheme
import app.pyquest.ui.theme.WidthClass
import app.pyquest.ui.theme.centeredMaxWidth
import app.pyquest.ui.theme.darken
import app.pyquest.ui.theme.lighten
import app.pyquest.ui.theme.onColor
import app.pyquest.ui.theme.unitColors
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private sealed interface PathRow {
    val key: String

    data class Goal(override val key: String = "goal") : PathRow
    data class Header(val unit: CourseUnit) : PathRow {
        override val key get() = "h-${unit.id}"
    }
    data class Node(val unit: CourseUnit, val lesson: Lesson, val indexInUnit: Int, val globalIndex: Int) : PathRow {
        override val key get() = "n-${lesson.id}"
    }
    data class Footer(override val key: String = "footer") : PathRow
}

enum class NodeState { DONE, CURRENT, OPEN, LOCKED }

/** Lage eines Knotens in seiner Zeile – daraus wird die Schlangenspur zum nächsten Knoten berechnet. */
private data class NodeGeo(val offset: Dp, val size: Dp, val topExtra: Dp) {
    val rowHeight: Dp get() = topExtra + NodeTop + size + NodeBottom
    val centerY: Dp get() = topExtra + NodeTop + size / 2
}

private val NodeTop = 16.dp
private val NodeBottom = 36.dp

private fun nodeGeo(row: PathRow.Node, style: PathStyle): NodeGeo = NodeGeo(
    offset = if (style == PathStyle.STRAIGHT) 0.dp else (sin(row.indexInUnit * PI / 3.4) * 70).dp,
    size = if (row.lesson.kind == LessonKind.CHECKPOINT) 76.dp else 66.dp,
    // Platz für die "Start"-Markierung unter dem Kopf der Einheit
    topExtra = if (row.indexInUnit == 0) 34.dp else 0.dp,
)

/** Dateiendung im Reiter der Einheiten (später je nach Kurs). */
internal fun courseExtension(course: Course): String = course.id.extension

@Composable
fun LearnScreen(
    course: Course,
    progress: UserProgress,
    settings: AppSettings,
    now: Long,
    onStartLesson: (Lesson) -> Unit,
    onOpenTheory: (Lesson) -> Unit,
    onOpenNotes: (CourseUnit) -> Unit,
    onOpenShop: () -> Unit,
    onOpenPractice: () -> Unit,
    topBarLeading: (@Composable () -> Unit)? = null,
    challenges: List<CodeExercise> = emptyList(),
    tips: List<PythonTip> = emptyList(),
    challengeDone: Boolean = false,
) {
    val c = PyTheme.colors
    val window = Adaptive.window
    val sidePanel = window.showSidePanel
    val rows = remember(course, sidePanel) {
        buildList {
            if (!sidePanel) add(PathRow.Goal())
            var g = 0
            course.units.forEach { u ->
                add(PathRow.Header(u))
                u.lessons.forEachIndexed { i, l -> add(PathRow.Node(u, l, i, g++)) }
            }
            add(PathRow.Footer())
        }
    }
    val current = remember(progress, course) { course.currentLesson(progress) }
    val currentRowIndex = rows.indexOfFirst { it is PathRow.Node && it.lesson.id == current?.id }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var initialScrollDone by rememberSaveable { mutableStateOf(false) }
    var lastCurrent by rememberSaveable { mutableStateOf<String?>(null) }
    var selected by remember { mutableStateOf<String?>(null) }
    var celebrate by remember { mutableStateOf<String?>(null) }
    var sheet by remember { mutableStateOf<HomeSheet?>(null) }
    val density = LocalDensity.current
    val pathStyle = LocalLayoutPrefs.current.pathStyle

    fun stateOf(l: Lesson): NodeState = when {
        progress.isCompleted(l.id) -> NodeState.DONE
        l.id == current?.id -> NodeState.CURRENT
        course.isUnlocked(l, progress) -> NodeState.OPEN
        else -> NodeState.LOCKED
    }

    LaunchedEffect(currentRowIndex) {
        if (currentRowIndex < 0) return@LaunchedEffect
        val offset = -with(density) { 220.dp.roundToPx() }
        if (!initialScrollDone) {
            listState.scrollToItem(currentRowIndex, offset)
            initialScrollDone = true
        } else if (lastCurrent != null && lastCurrent != current?.id) {
            listState.animateScrollToItem(currentRowIndex, offset)
            celebrate = current?.id
        }
        lastCurrent = current?.id
    }

    Row(
        Modifier
            .fillMaxSize()
            .screenBackground()
    ) {
        Column(
            Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            TopStatsBar(
                progress, settings, now,
                onStreak = { sheet = HomeSheet.STREAK },
                onGems = onOpenShop,
                onHearts = { sheet = HomeSheet.HEARTS },
                leading = topBarLeading,
            )
            Box(Modifier.weight(1f)) {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(bottom = 120.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    rows.forEachIndexed { index, row ->
                        when (row) {
                            is PathRow.Goal -> item(key = row.key) { DailyGoalCard(progress, settings) }
                            is PathRow.Header -> stickyHeader(key = row.key) {
                                UnitHeader(row.unit, course, progress, onNotes = { onOpenNotes(row.unit) })
                            }
                            is PathRow.Node -> item(key = row.key) {
                                val l = row.lesson
                                val next = (rows.getOrNull(index + 1) as? PathRow.Node)?.takeIf { it.unit.id == row.unit.id }
                                PathNodeRow(
                                    row = row,
                                    geo = nodeGeo(row, pathStyle),
                                    nextGeo = next?.let { nodeGeo(it, pathStyle) },
                                    state = stateOf(l),
                                    totalInUnit = row.unit.lessons.size,
                                    selected = selected == l.id,
                                    celebrate = celebrate == l.id,
                                    accuracy = progress.lessons[l.id]?.bestAccuracy ?: 0,
                                    onClick = { selected = if (selected == l.id) null else l.id },
                                    onDismiss = { if (selected == l.id) selected = null },
                                    onStart = {
                                        selected = null
                                        onStartLesson(l)
                                    },
                                    onTheory = {
                                        selected = null
                                        onOpenTheory(l)
                                    },
                                )
                            }
                            is PathRow.Footer -> item(key = row.key) { PathFooter() }
                        }
                    }
                }
                JumpToCurrentButton(listState, currentRowIndex) {
                    scope.launch {
                        listState.animateScrollToItem(currentRowIndex, -with(density) { 220.dp.roundToPx() })
                    }
                }
            }
        }
        if (sidePanel) {
            VerticalDivider(thickness = 1.dp, color = c.border)
            LearnSidePanel(
                course = course,
                progress = progress,
                settings = settings,
                now = now,
                width = if (window.widthClass == WidthClass.EXPANDED) 340.dp else 296.dp,
                onStreak = { sheet = HomeSheet.STREAK },
                onHearts = { sheet = HomeSheet.HEARTS },
                onShop = onOpenShop,
                onPractice = onOpenPractice,
                challenges = challenges,
                tips = tips,
                challengeDone = challengeDone,
                tipLabel = course.id.displayName,
            )
        }
    }

    when (sheet) {
        HomeSheet.STREAK -> StreakSheet(progress, onDismiss = { sheet = null }, onShop = { sheet = null; onOpenShop() })
        HomeSheet.HEARTS -> HeartsSheet(
            progress, settings, now,
            onDismiss = { sheet = null },
            onShop = { sheet = null; onOpenShop() },
            onPractice = { sheet = null; onOpenPractice() },
        )
        null -> Unit
    }
}

enum class HomeSheet { STREAK, HEARTS }

/** Kartenfarbe: im dunklen Design etwas heller als der Hintergrund, im hellen weiß. */
@Composable
internal fun cardColor(): Color = PyTheme.colors.let { if (it.isDark) it.surfaceAlt else it.surface }

@Composable
private fun DailyGoalCard(progress: UserProgress, settings: AppSettings) {
    val c = PyTheme.colors
    val xp = progress.xpOn(LocalDate.now())
    val goal = settings.dailyGoal
    val done = xp >= goal
    Row(
        Modifier
            .centeredMaxWidth(PathWidth)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clip(PyTheme.shapes.mediumShape)
            .background(c.surfaceAlt)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            if (done) Icons.Rounded.EmojiEvents else Icons.Rounded.Flag,
            contentDescription = null,
            tint = c.gold,
            modifier = Modifier.size(24.dp),
        )
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Row {
                Text(
                    if (done) tr("Tagesziel erreicht", "Daily goal reached") else tr("Tagesziel", "Daily goal"),
                    style = MaterialTheme.typography.titleSmall,
                    color = c.text,
                    modifier = Modifier.weight(1f),
                )
                Text("$xp / $goal XP", style = MaterialTheme.typography.labelMedium, color = c.textMuted)
            }
            Spacer(Modifier.height(8.dp))
            PyProgressBar(xp.toFloat() / goal, color = c.gold, track = c.border, height = 8.dp, modifier = Modifier.fillMaxWidth())
        }
    }
}

/** Kopf einer Einheit: Nummer, Titel, Beschreibung und Fortschritt – ohne Kasten, wie eine Kapitelüberschrift. */
@Composable
private fun UnitHeader(unit: CourseUnit, @Suppress("UNUSED_PARAMETER") course: Course, progress: UserProgress, onNotes: () -> Unit) {
    val c = PyTheme.colors
    val (color, _) = unitColors(unit.colorIndex)
    val done = unit.lessons.count { progress.isCompleted(it.id) }
    val unitLabel = if (c.isDark) color.lighten(0.25f) else color.darken(0.85f)
    // Die Überschrift bleibt oben angeheftet und muss dort deckend sein, damit nichts durchscheint. Mit Hintergrundbild
    // ist sie weiter unten im Bild durchsichtig – sonst zerschnitte ihr weißer Kasten die Szenerie beim Scrollen.
    val ambient = LocalAmbient.current != AmbientBackground.NONE
    val bg = c.background
    var nearTop by remember { mutableStateOf(true) }
    Column(
        Modifier
            .fillMaxWidth()
            .onPlaced { nearTop = it.positionInParent().y < it.size.height }
            .drawBehind { if (!ambient || nearTop) drawRect(bg) }
    ) {
        Row(
            Modifier
                .centeredMaxWidth(PathWidth)
                .padding(start = 20.dp, end = 12.dp, top = 14.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    (tr("Einheit ${unit.number}", "Unit ${unit.number}") + " · $done/${unit.lessons.size}").uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.4.sp, fontWeight = FontWeight.SemiBold),
                    color = unitLabel,
                )
                Spacer(Modifier.height(2.dp))
                Text(unit.title, style = MaterialTheme.typography.titleLarge, color = c.text)
                Text(unit.description, style = MaterialTheme.typography.bodySmall, color = c.textMuted, maxLines = 2)
            }
            Spacer(Modifier.width(8.dp))
            Box(
                Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .clickableNoRipple(onClick = onNotes)
                    .border(1.dp, c.border, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.AutoMirrored.Rounded.MenuBook, tr("Notizen", "Notes"), tint = c.textMuted, modifier = Modifier.size(22.dp))
            }
        }
        PyProgressBar(
            done.toFloat() / unit.lessons.size,
            color = color,
            track = c.border,
            height = 3.dp,
            modifier = Modifier
                .centeredMaxWidth(PathWidth)
                .padding(horizontal = 20.dp),
        )
    }
}

@Composable
private fun PathNodeRow(
    row: PathRow.Node,
    geo: NodeGeo,
    nextGeo: NodeGeo?,
    state: NodeState,
    totalInUnit: Int,
    selected: Boolean,
    celebrate: Boolean,
    accuracy: Int,
    onClick: () -> Unit,
    onDismiss: () -> Unit,
    onStart: () -> Unit,
    onTheory: () -> Unit,
) {
    val c = PyTheme.colors
    val (unitColor, _) = unitColors(row.unit.colorIndex)
    val mascot = LocalMascotPrefs.current
    val isCheckpoint = row.lesson.kind == LessonKind.CHECKPOINT
    val density = LocalDensity.current
    val feedback = rememberFeedback()
    val animate = PyTheme.animate

    val pop by animateFloatAsState(
        targetValue = if (celebrate) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.35f, stiffness = Spring.StiffnessLow),
        label = "pop",
    )
    // Knoten "ploppen" beim ersten Erscheinen auf
    var appeared by rememberSaveable { mutableStateOf(!animate) }
    val appear = remember { Animatable(if (appeared) 1f else 0f) }
    LaunchedEffect(Unit) {
        if (!appeared) {
            appear.animateTo(1f, spring(dampingRatio = 0.55f, stiffness = 320f))
            appeared = true
        }
    }

    val trailDone = state == NodeState.DONE
    val trailColor = if (trailDone) unitColor.copy(alpha = 0.45f) else c.lockedIcon.copy(alpha = 0.55f)
    Box(
        Modifier
            .fillMaxWidth()
            .height(geo.rowHeight)
            .drawBehind {
                val next = nextGeo ?: return@drawBehind
                // Verbindungslinie zum nächsten Knoten (reicht in dessen Zeile hinein)
                val x0 = size.width / 2 + geo.offset.toPx()
                val y0 = geo.centerY.toPx()
                val x1 = size.width / 2 + next.offset.toPx()
                val y1 = size.height + next.centerY.toPx()
                val dy = y1 - y0
                val path = Path().apply {
                    moveTo(x0, y0)
                    cubicTo(x0, y0 + dy * 0.55f, x1, y1 - dy * 0.55f, x1, y1)
                }
                // Erledigte Abschnitte als feine Linie, kommende als Punktreihe
                val style = if (trailDone) Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                else Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round, pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.01f, 12.dp.toPx()), 0f))
                drawPath(path, trailColor, style = style)
            },
        contentAlignment = Alignment.TopCenter,
    ) {
        // Monty neben dem Pfad
        val showMonty = mascot.show && row.indexInUnit == (if (row.unit.number % 2 == 1) 2 else 6) && totalInUnit > row.indexInUnit
        if (showMonty) {
            val side = if (geo.offset.value > 0) -1 else 1
            Box(Modifier.offset(x = (side * 122).dp, y = 4.dp)) {
                Monty(size = 92.dp, mood = if (state == NodeState.DONE) MontyMood.HAPPY else MontyMood.THINKING)
            }
        }

        Box(Modifier.offset(x = geo.offset, y = geo.topExtra + NodeTop)) {
            val bob = rememberBob(enabled = state == NodeState.CURRENT && !selected)
            if (state == NodeState.CURRENT) {
                // ruhiger Ring um den aktuellen Knoten
                Canvas(Modifier.size(geo.size + 16.dp).offset(x = (-8).dp, y = (-8).dp)) {
                    drawCircle(unitColor.copy(alpha = 0.35f), style = Stroke(3.dp.toPx()), radius = size.minDimension / 2 - 1.5.dp.toPx())
                }
            }
            if (celebrate && animate) BitBurst(unitColor, geo.size, pop)
            val perfect = state == NodeState.DONE && accuracy >= 100 && !isCheckpoint
            val face = when (state) {
                NodeState.DONE -> if (perfect) c.gold else unitColor
                NodeState.CURRENT, NodeState.OPEN -> unitColor
                NodeState.LOCKED -> c.locked
            }
            ChunkySurface(
                onClick = {
                    feedback.select()
                    onClick()
                },
                face = face,
                borderWidth = 0.dp,
                shape = CircleShape,
                modifier = Modifier
                    .size(geo.size)
                    .graphicsLayer {
                        val s = (0.6f + 0.4f * appear.value) * (1f + pop * 0.14f)
                        scaleX = s
                        scaleY = s
                        alpha = appear.value
                    },
            ) {
                val icon: ImageVector = when {
                    state == NodeState.LOCKED -> if (isCheckpoint) row.lesson.icon.vector() else Icons.Rounded.Lock
                    state == NodeState.DONE && !isCheckpoint -> if (perfect) Icons.Rounded.Star else Icons.Rounded.Check
                    else -> row.lesson.icon.vector()
                }
                Icon(
                    icon,
                    contentDescription = row.lesson.title,
                    // Auf den farbigen Knoten immer weiß – auch auf Gold, damit alle Knoten gleich wirken.
                    tint = if (state == NodeState.LOCKED) c.lockedIcon else Color.White,
                    modifier = Modifier.size(if (isCheckpoint) 40.dp else 32.dp),
                )
            }
            if (state == NodeState.CURRENT && !selected) {
                StartTag(
                    color = unitColor,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset { IntOffset(0, with(density) { (-40).dp.roundToPx() } + bob.toInt() * 2) },
                )
            }
        }

        if (selected) {
            Popup(
                alignment = Alignment.TopCenter,
                offset = IntOffset(0, with(density) { (geo.size + 28.dp + geo.topExtra).roundToPx() }),
                onDismissRequest = onDismiss,
                properties = PopupProperties(focusable = true),
            ) {
                LessonPopup(row, state, unitColor, geo.offset, onStart, onTheory)
            }
        }
    }
}

/** Ein Ring, der sich beim Freischalten einer neuen Lektion vom Knoten aus ausbreitet. */
@Composable
private fun BitBurst(color: Color, nodeSize: Dp, progress: Float) {
    if (progress <= 0.01f) return
    val reach = nodeSize * 0.9f
    Canvas(Modifier.size(nodeSize)) {
        val center = Offset(size.width / 2, size.height / 2)
        val r = size.minDimension / 2 + (reach.toPx() - size.minDimension / 2) * progress
        val alpha = (1f - progress).coerceIn(0f, 1f)
        drawCircle(color.copy(alpha = 0.4f * alpha), radius = r, center = center, style = Stroke(3.dp.toPx()))
    }
}

@Composable
private fun StartTag(@Suppress("UNUSED_PARAMETER") color: Color, modifier: Modifier = Modifier) {
    // Schlichte Kapsel in Anthrazit (im dunklen Design Elfenbein) mit gesperrter Beschriftung
    val c = PyTheme.colors
    val ink = c.text
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier
                .clip(RoundedCornerShape(50))
                .background(ink)
                .padding(horizontal = 14.dp, vertical = 5.dp)
        ) {
            Text(
                tr("START", "START"),
                color = c.background,
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.4.sp, fontWeight = FontWeight.SemiBold),
            )
        }
        Canvas(Modifier.size(width = 12.dp, height = 6.dp)) {
            val p = Path().apply {
                moveTo(0f, 0f)
                lineTo(size.width, 0f)
                lineTo(size.width / 2, size.height)
                close()
            }
            drawPath(p, ink)
        }
    }
}

@Composable
private fun LessonPopup(
    row: PathRow.Node,
    state: NodeState,
    color: Color,
    nodeOffset: Dp,
    onStart: () -> Unit,
    onTheory: () -> Unit,
) {
    val c = PyTheme.colors
    val visible = remember { MutableTransitionState(false).apply { targetState = true } }
    val locked = state == NodeState.LOCKED
    val card = cardColor()
    AnimatedVisibility(
        visibleState = visible,
        enter = scaleIn(
            spring(dampingRatio = 0.62f, stiffness = Spring.StiffnessMedium),
            initialScale = 0.75f,
            transformOrigin = TransformOrigin(0.5f, 0f),
        ) + fadeIn(tween(150)),
        exit = scaleOut() + fadeOut(),
    ) {
        Column(
            Modifier
                .widthIn(max = 360.dp)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // kleiner Pfeil zum Knoten, in der Farbe der Karte mit deren Rand
            val border = c.border
            Canvas(Modifier.fillMaxWidth().height(9.dp).offset(y = 1.dp)) {
                val x = size.width / 2 + nodeOffset.toPx()
                val half = 10.dp.toPx()
                val p = Path().apply {
                    moveTo(x - half, size.height)
                    lineTo(x, 0f)
                    lineTo(x + half, size.height)
                    close()
                }
                drawPath(p, card)
                val edge = Path().apply {
                    moveTo(x - half, size.height)
                    lineTo(x, 0f)
                    lineTo(x + half, size.height)
                }
                drawPath(edge, border, style = Stroke(1.dp.toPx()))
            }
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(PyTheme.shapes.largeShape)
                    .background(card)
                    .border(1.dp, c.border, PyTheme.shapes.largeShape)
            ) {
                Column(Modifier.padding(18.dp)) {
                    Text(row.lesson.title, style = MaterialTheme.typography.titleLarge, color = c.text)
                    Spacer(Modifier.height(2.dp))
                    val sub = when {
                        locked -> tr(
                            "Schließe zuerst die vorherigen Lektionen ab, um diese freizuschalten!",
                            "Finish the previous lessons first to unlock this one!",
                        )
                        row.lesson.kind == LessonKind.CHECKPOINT -> tr(
                            "Teste dein Wissen aus der ganzen Einheit.",
                            "Test what you learned in the whole unit.",
                        )
                        row.lesson.subtitle.isNotEmpty() -> row.lesson.subtitle
                        else -> tr(
                            "Lektion ${row.indexInUnit + 1} von ${row.unit.lessons.size}",
                            "Lesson ${row.indexInUnit + 1} of ${row.unit.lessons.size}",
                        )
                    }
                    Text(sub, style = MaterialTheme.typography.bodyMedium, color = c.textMuted)
                    Spacer(Modifier.height(14.dp))
                    if (locked) {
                        ChunkyButton(tr("Gesperrt", "Locked"), onClick = {}, enabled = false, modifier = Modifier.fillMaxWidth())
                    } else {
                        val xp = if (row.lesson.kind == LessonKind.CHECKPOINT) 20 else 10
                        ChunkyButton(
                            if (state == NodeState.DONE) tr("Wiederholen · +$xp XP", "Practise again · +$xp XP")
                            else tr("Los geht's · +$xp XP", "Let's go · +$xp XP"),
                            onClick = onStart,
                            color = color,
                            shadow = color,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        if (row.lesson.theory.isNotEmpty()) {
                            Spacer(Modifier.height(8.dp))
                            ChunkyButton(
                                tr("Lernkarte ansehen", "View the lesson notes"),
                                onClick = onTheory,
                                outlined = true,
                                color = c.text,
                                icon = Icons.AutoMirrored.Rounded.MenuBook,
                                height = 46.dp,
                                sound = false,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PathFooter() {
    val c = PyTheme.colors
    Column(
        Modifier
            .centeredMaxWidth(PathWidth)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Monty(size = 110.dp, mood = MontyMood.CELEBRATE, accessory = "hat_grad")
        Spacer(Modifier.height(12.dp))
        Text(
            tr("Das Ziel: echte Programme schreiben!", "The goal: writing real programs!"),
            style = MaterialTheme.typography.titleLarge,
            color = c.text,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            tr(
                "Wenn du alle Einheiten geschafft hast, kannst du richtige Programme schreiben. Übe danach weiter mit Tages-Challenges und im Spielplatz!",
                "Once you've finished every unit you can write real programs. Keep going with daily challenges and the playground!",
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = c.textMuted,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun JumpToCurrentButton(listState: LazyListState, target: Int, onClick: () -> Unit) {
    if (target < 0) return
    val direction by remember(target) {
        derivedStateOf {
            val visible = listState.layoutInfo.visibleItemsInfo
            when {
                visible.isEmpty() -> 0
                visible.any { it.index == target } -> 0
                visible.first().index > target -> -1
                else -> 1
            }
        }
    }
    Box(Modifier.fillMaxSize().padding(20.dp), contentAlignment = Alignment.BottomEnd) {
        AnimatedVisibility(direction != 0, enter = scaleIn() + fadeIn(), exit = scaleOut() + fadeOut()) {
            ChunkySurface(
                onClick = onClick,
                face = cardColor(),
                edge = PyTheme.colors.border,
                borderWidth = 1.dp,
                shape = CircleShape,
                modifier = Modifier.size(54.dp),
            ) {
                Icon(
                    if (direction < 0) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                    contentDescription = tr("Zur aktuellen Lektion", "Jump to the current lesson"),
                    tint = PyTheme.colors.primaryText,
                    modifier = Modifier.size(30.dp),
                )
            }
        }
    }
}
