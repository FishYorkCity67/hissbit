package app.pyquest.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.CallSplit
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.AutoFixHigh
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Extension
import androidx.compose.material.icons.rounded.Forum
import androidx.compose.material.icons.rounded.GpsFixed
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Loop
import androidx.compose.material.icons.rounded.MilitaryTech
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material.icons.rounded.RocketLaunch
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.pyquest.data.Achievement
import app.pyquest.data.AchievementIcon
import app.pyquest.data.LessonIcon
import app.pyquest.ui.theme.PyTheme
import app.pyquest.ui.theme.onColor

fun LessonIcon.vector(): ImageVector = when (this) {
    LessonIcon.STAR -> Icons.Rounded.Star
    LessonIcon.CODE -> Icons.Rounded.Code
    LessonIcon.BOOK -> Icons.AutoMirrored.Rounded.MenuBook
    LessonIcon.PUZZLE -> Icons.Rounded.Extension
    LessonIcon.ROCKET -> Icons.Rounded.RocketLaunch
    LessonIcon.BRAIN -> Icons.Rounded.Psychology
    LessonIcon.TROPHY -> Icons.Rounded.EmojiEvents
    LessonIcon.BUG -> Icons.Rounded.BugReport
    LessonIcon.BOX -> Icons.Rounded.Inventory2
    LessonIcon.LOOP -> Icons.Rounded.Loop
    LessonIcon.BRANCH -> Icons.AutoMirrored.Rounded.CallSplit
    LessonIcon.CLASS -> Icons.Rounded.Category
    LessonIcon.FILE -> Icons.Rounded.Description
    LessonIcon.MAGIC -> Icons.Rounded.AutoFixHigh
    LessonIcon.CHAT -> Icons.Rounded.Forum
    LessonIcon.CALC -> Icons.Rounded.Calculate
}

fun AchievementIcon.vector(): ImageVector = when (this) {
    AchievementIcon.FLAME -> Icons.Rounded.LocalFireDepartment
    AchievementIcon.BOLT -> Icons.Rounded.Bolt
    AchievementIcon.STAR -> Icons.Rounded.Star
    AchievementIcon.TROPHY -> Icons.Rounded.EmojiEvents
    AchievementIcon.CODE -> Icons.Rounded.Code
    AchievementIcon.MOON -> Icons.Rounded.DarkMode
    AchievementIcon.SUN -> Icons.Rounded.WbSunny
    AchievementIcon.TARGET -> Icons.Rounded.GpsFixed
    AchievementIcon.GEM -> Icons.Rounded.ShoppingBag
    AchievementIcon.BRAIN -> Icons.Rounded.Psychology
    AchievementIcon.MEDAL -> Icons.Rounded.MilitaryTech
    AchievementIcon.ROCKET -> Icons.Rounded.RocketLaunch
    AchievementIcon.BUG -> Icons.Rounded.BugReport
    AchievementIcon.CROWN -> Icons.Rounded.WorkspacePremium
}

private fun achievementColor(icon: AchievementIcon): Color = when (icon) {
    AchievementIcon.FLAME -> Color(0xFFE07A1F)
    AchievementIcon.BOLT -> Color(0xFFD69E12)
    AchievementIcon.STAR, AchievementIcon.MEDAL -> Color(0xFF22A060)
    AchievementIcon.TROPHY, AchievementIcon.CROWN -> Color(0xFFC9932A)
    AchievementIcon.CODE, AchievementIcon.BUG -> Color(0xFF2F7DD6)
    AchievementIcon.MOON -> Color(0xFF4E5BB8)
    AchievementIcon.SUN -> Color(0xFFE0882F)
    AchievementIcon.TARGET -> Color(0xFFD64D6F)
    AchievementIcon.GEM -> Color(0xFF2F7DE8)
    AchievementIcon.BRAIN -> Color(0xFF15958F)
    AchievementIcon.ROCKET -> Color(0xFF1E86B8)
}

/** Rundes Abzeichen für Erfolge: farbig, wenn freigeschaltet, sonst grau mit Schloss. */
@Composable
fun AchievementBadge(achievement: Achievement, unlocked: Boolean, size: Dp = 64.dp, modifier: Modifier = Modifier) {
    val c = PyTheme.colors
    val face = if (unlocked) achievementColor(achievement.icon) else c.locked
    Box(
        modifier
            .size(size)
            .clip(CircleShape)
            .background(face),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            if (unlocked) achievement.icon.vector() else Icons.Rounded.Lock,
            contentDescription = null,
            tint = if (unlocked) face.onColor() else c.lockedIcon,
            modifier = Modifier.size(size * 0.46f),
        )
    }
}