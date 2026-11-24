package app.pyquest.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier

/** Klickbar ohne Ripple-Effekt (wir animieren selbst). */
fun Modifier.clickableNoRipple(enabled: Boolean = true, onClick: () -> Unit): Modifier =
    this.clickable(interactionSource = null, indication = null, enabled = enabled, onClick = onClick)
