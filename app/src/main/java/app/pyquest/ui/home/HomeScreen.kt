package app.pyquest.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import app.pyquest.ui.ambient.screenBackground
import app.pyquest.data.AnimationLevel
import app.pyquest.data.AppSettings
import app.pyquest.data.CourseUnit
import app.pyquest.data.Game
import app.pyquest.data.Lesson
import app.pyquest.data.SessionMode
import app.pyquest.data.UserProgress
import app.pyquest.data.challengeDoneToday
import app.pyquest.i18n.Txt
import app.pyquest.i18n.t
import app.pyquest.i18n.tr
import app.pyquest.ui.AppViewModel
import app.pyquest.ui.MainTab
import app.pyquest.ui.components.HissbitLogo
import app.pyquest.ui.components.HissbitWordmark
import app.pyquest.ui.components.Monty
import app.pyquest.ui.components.MontyMood
import app.pyquest.ui.components.clickableNoRipple
import app.pyquest.ui.components.rememberFeedback
import app.pyquest.ui.playground.PlaygroundScreen
import app.pyquest.ui.playground.PlaygroundViewModel
import app.pyquest.ui.theme.Adaptive
import app.pyquest.ui.theme.PyTheme
import app.pyquest.ui.theme.WidthClass

private data class TabItem(val tab: MainTab, val label: Txt, val icon: ImageVector, val iconOff: ImageVector)

private val Tabs = listOf(
    TabItem(MainTab.LEARN, Txt("Lernen", "Learn"), Icons.Rounded.Home, Icons.Outlined.Home),
    TabItem(MainTab.PRACTICE, Txt("Üben", "Practice"), Icons.Rounded.FitnessCenter, Icons.Outlined.FitnessCenter),
    TabItem(MainTab.CODE, Txt("Code", "Code"), Icons.Rounded.Code, Icons.Outlined.Code),
    TabItem(MainTab.KNOWLEDGE, Txt("Wissen", "Library"), Icons.AutoMirrored.Rounded.MenuBook, Icons.AutoMirrored.Outlined.MenuBook),
    TabItem(MainTab.PROFILE, Txt("Profil", "Profile"), Icons.Rounded.Person, Icons.Outlined.Person),
)

@Composable
fun HomeScreen(
    app: AppViewModel,
    playground: PlaygroundViewModel,
    progress: UserProgress,
    settings: AppSettings,
    now: Long,
    onStartLesson: (Lesson) -> Unit,
    onStartSession: (SessionMode) -> Unit,
    onOpenTheory: (Lesson) -> Unit,
    onOpenNotes: (CourseUnit) -> Unit,
    onOpenShop: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    val c = PyTheme.colors
    val window = Adaptive.window
    val tab = MainTab.entries[app.selectedTab]
    val animLevel = PyTheme.anim
    BackHandler(enabled = tab != MainTab.LEARN) { app.selectedTab = MainTab.LEARN.ordinal }

    val bundle = app.bundle
    val courseId = app.courseId
    val challengeDone = progress.challengeDoneToday(courseId)
    var showCourses by remember { mutableStateOf(false) }
    val courseChip: (@Composable () -> Unit)? = if (app.availableCourses.size > 1) {
        { CourseChip(courseId) { showCourses = true } }
    } else null

    val tabContent: @Composable (Modifier) -> Unit = { modifier ->
        Box(modifier) {
            AnimatedContent(
                targetState = tab,
                transitionSpec = {
                    val direction = if (targetState.ordinal >= initialState.ordinal) 1 else -1
                    when (animLevel) {
                        AnimationLevel.FULL ->
                            (fadeIn(tween(260, delayMillis = 40)) +
                                slideInHorizontally(tween(360, easing = FastOutSlowInEasing)) { (it * 0.10f).toInt() * direction })
                                .togetherWith(
                                    fadeOut(tween(150)) +
                                        slideOutHorizontally(tween(320, easing = FastOutSlowInEasing)) { -(it * 0.10f).toInt() * direction },
                                )
                        AnimationLevel.REDUCED -> fadeIn(tween(180)).togetherWith(fadeOut(tween(120)))
                        AnimationLevel.OFF -> EnterTransition.None.togetherWith(ExitTransition.None)
                    }
                },
                label = "tabs",
            ) { t ->
                when (t) {
                    MainTab.LEARN -> LearnScreen(
                        course = bundle.course,
                        progress = progress,
                        settings = settings,
                        now = now,
                        onStartLesson = onStartLesson,
                        onOpenTheory = onOpenTheory,
                        onOpenNotes = onOpenNotes,
                        onOpenShop = onOpenShop,
                        onOpenPractice = { app.selectedTab = MainTab.PRACTICE.ordinal },
                        topBarLeading = courseChip,
                        challenges = bundle.challenges,
                        tips = bundle.tips,
                        challengeDone = challengeDone,
                    )
                    MainTab.PRACTICE -> PracticeScreen(bundle.course, bundle.challenges, progress, onStartSession, challengeDone)
                    MainTab.CODE -> PlaygroundScreen(app, playground)
                    MainTab.KNOWLEDGE -> KnowledgeScreen(
                        bundle.course, progress, onOpenNotes,
                        reference = bundle.reference,
                        glossary = bundle.glossary,
                        errors = bundle.errors,
                        languageName = courseId.displayName,
                    )
                    MainTab.PROFILE -> ProfileScreen(
                        course = bundle.course,
                        progress = progress,
                        settings = settings,
                        onSettings = onOpenSettings,
                        onShop = onOpenShop,
                        onRename = { name -> app.updateProgress { it.copy(name = name.trim()) } },
                        allCourses = app.allCourses,
                    )
                }
            }
        }
    }
    if (window.useNavRail) {
        // Großes Display (z.B. Innendisplay des Z Fold): Navigation als Seitenleiste
        Row(
            Modifier
                .fillMaxSize()
                .screenBackground()
        ) {
            SideNav(tab, expanded = window.widthClass == WidthClass.EXPANDED) { app.selectedTab = it.ordinal }
            VerticalDivider(thickness = 1.dp, color = c.border)
            tabContent(Modifier.weight(1f).fillMaxHeight())
        }
    } else {
        Column(
            Modifier
                .fillMaxSize()
                .screenBackground()
        ) {
            tabContent(Modifier.weight(1f))
            // Beim Tippen im Spielplatz braucht die Tastatur den Platz
            if (!(tab == MainTab.CODE && WindowInsets.isImeVisible)) {
                BottomBar(tab) { app.selectedTab = it.ordinal }
            }
        }
    }

    if (showCourses) {
        CourseSwitcherSheet(
            courses = app.availableCourses.map { it to app.allCourses.first { c -> c.id == it } },
            current = courseId,
            progress = progress,
            onPick = { id ->
                showCourses = false
                if (id != courseId) app.switchCourse(id)
            },
            onDismiss = { showCourses = false },
        )
    }

    if (progress.lostStreak > 0) {
        LostStreakDialog(
            lost = progress.lostStreak,
            gems = progress.gems,
            onRepair = { app.repairStreak() },
            onDismiss = { app.dismissLostStreak() },
        )
    }
}

/** Seitenleiste für große Displays: schmal mit Icons (MEDIUM) oder breit mit Beschriftung (EXPANDED). */
@Composable
private fun SideNav(selected: MainTab, expanded: Boolean, onSelect: (MainTab) -> Unit) {
    val c = PyTheme.colors
    val feedback = rememberFeedback()
    Column(
        Modifier
            .fillMaxHeight()
            .width(if (expanded) 236.dp else 92.dp)
            .screenBackground()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 10.dp, vertical = 14.dp),
        horizontalAlignment = if (expanded) Alignment.Start else Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        if (expanded) {
            HissbitWordmark(Modifier.padding(start = 8.dp, bottom = 18.dp), logoSize = 38.dp)
        } else {
            Box(Modifier.padding(bottom = 18.dp)) { HissbitLogo(size = 42.dp) }
        }
        Tabs.forEach { item ->
            val isSel = item.tab == selected
            val bg by animateColorAsState(if (isSel) c.primarySoft else Color.Transparent, label = "railBg")
            val tint by animateColorAsState(if (isSel) c.primaryText else c.textMuted, label = "railTint")
            val bounce = 1f
            val itemModifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(bg)
                .clickableNoRipple {
                    if (!isSel) feedback.tap()
                    onSelect(item.tab)
                }
            val label = item.label.t()
            if (expanded) {
                Row(
                    itemModifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(if (isSel) item.icon else item.iconOff, null, tint = tint, modifier = Modifier.size(26.dp).graphicsLayer { scaleX = bounce; scaleY = bounce })
                    Spacer(Modifier.width(14.dp))
                    Text(label, style = MaterialTheme.typography.labelLarge, color = tint)
                }
            } else {
                Column(
                    itemModifier
                        .width(72.dp)
                        .padding(vertical = 9.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(if (isSel) item.icon else item.iconOff, label, tint = tint, modifier = Modifier.size(26.dp).graphicsLayer { scaleX = bounce; scaleY = bounce })
                    Spacer(Modifier.height(3.dp))
                    Text(label, style = MaterialTheme.typography.labelSmall, color = tint)
                }
            }
        }
        Spacer(Modifier.weight(1f))
        if (expanded) {
            Box(Modifier.align(Alignment.CenterHorizontally)) { Monty(size = 96.dp, mood = MontyMood.HAPPY) }
        }
    }
}

/**
 * Untere Navigationsleiste: eine schmale, mittig stehende Kapsel mit gleich großen Feldern.
 * Links und rechts sowie oben und unten gleiche Abstände; der Bereich der Gestenleiste bleibt
 * frei – so wirkt die Leiste nicht unten „dick“, und nichts verdeckt die Szenerie dahinter.
 * Die Markierung gleitet beim Wechsel sanft von Feld zu Feld.
 */
@Composable
private fun BottomBar(selected: MainTab, onSelect: (MainTab) -> Unit) {
    val c = PyTheme.colors
    val feedback = rememberFeedback()
    val outer = PyTheme.shapes.large + 8.dp
    val inner = outer - 5.dp
    val gap = 2.dp
    val index = Tabs.indexOfFirst { it.tab == selected }.coerceAtLeast(0)
    val position by animateFloatAsState(
        targetValue = index.toFloat(),
        animationSpec = if (PyTheme.anim == AnimationLevel.OFF) snap() else spring(dampingRatio = 0.8f, stiffness = 320f),
        label = "tabIndicator",
    )
    Box(
        Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(start = 14.dp, end = 14.dp, top = 6.dp, bottom = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        BoxWithConstraints(
            Modifier
                .widthIn(max = 480.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(outer))
                .background(cardColor())
                .border(1.dp, c.border, RoundedCornerShape(outer))
                .padding(5.dp),
        ) {
            val cellWidth = (maxWidth - gap * (Tabs.size - 1)) / Tabs.size
            // gleitende Markierung hinter den Feldern
            Box(
                Modifier
                    .offset { IntOffset(((cellWidth + gap) * position).roundToPx(), 0) }
                    .width(cellWidth)
                    .height(48.dp)
                    .clip(RoundedCornerShape(inner))
                    .background(c.primarySoft)
            )
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(gap)) {
                Tabs.forEach { item ->
                    val isSel = item.tab == selected
                    val tint by animateColorAsState(if (isSel) c.primaryText else c.textMuted, tween(220), label = "tabTint")
                    val label = item.label.t()
                    Column(
                        Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(inner))
                            .clickableNoRipple {
                                if (!isSel) feedback.tap()
                                onSelect(item.tab)
                            },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Icon(if (isSel) item.icon else item.iconOff, null, tint = tint, modifier = Modifier.size(22.dp))
                        Spacer(Modifier.height(2.dp))
                        Text(label, style = MaterialTheme.typography.labelSmall, color = tint, maxLines = 1)
                    }
                }
            }
        }
    }
}
@Composable
private fun LostStreakDialog(lost: Int, gems: Int, onRepair: () -> Boolean, onDismiss: () -> Unit) {
    val c = PyTheme.colors
    val canRepair = gems >= Game.PRICE_REPAIR
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Monty(size = 84.dp, mood = MontyMood.SAD) },
        title = {
            Text(
                tr("Dein $lost-Tage-Streak ist gerissen", "Your $lost-day streak ended"),
                textAlign = TextAlign.Center,
            )
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    tr(
                        "Kein Problem – das passiert! Du kannst ihn für ${Game.PRICE_REPAIR} Bits reparieren oder heute einfach neu starten.",
                        "No worries – it happens! Repair it for ${Game.PRICE_REPAIR} bits or simply start fresh today.",
                    ),
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    tr(
                        "Tipp: Mit einem Streak-Freeze aus dem Shop passiert das nicht mehr.",
                        "Tip: A streak freeze from the shop protects you next time.",
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = c.textMuted,
                    textAlign = TextAlign.Center,
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onRepair() }, enabled = canRepair) {
                Text(
                    tr("Reparieren (${Game.PRICE_REPAIR} Bits)", "Repair (${Game.PRICE_REPAIR} bits)"),
                    color = if (canRepair) c.gem else c.textMuted,
                )
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(tr("Neu starten", "Start fresh")) } },
        containerColor = c.surface,
    )
}
