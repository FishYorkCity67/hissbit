package app.pyquest.ui.shop

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.rounded.AcUnit
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.pyquest.ui.ambient.screenBackground
import app.pyquest.data.Game
import app.pyquest.data.PurchaseResult
import app.pyquest.data.ShopItem
import app.pyquest.data.UserProgress
import app.pyquest.data.boostActive
import app.pyquest.i18n.LocalLanguage
import app.pyquest.i18n.tr
import app.pyquest.ui.components.BitsIcon
import app.pyquest.ui.components.ChunkyButton
import app.pyquest.ui.components.ChunkySurface
import app.pyquest.ui.components.ConfettiBurst
import app.pyquest.ui.components.Monty
import app.pyquest.ui.components.MontyMood
import app.pyquest.ui.components.rememberFeedback
import app.pyquest.ui.home.cardColor
import app.pyquest.ui.theme.PyTheme
import app.pyquest.ui.theme.ReadingWidth
import app.pyquest.ui.theme.centeredMaxWidth
import app.pyquest.ui.theme.onColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ShopScreen(
    progress: UserProgress,
    now: Long,
    onBuy: suspend (ShopItem) -> PurchaseResult,
    onEquip: (String?) -> Unit,
    onBack: () -> Unit,
) {
    val c = PyTheme.colors
    val lang = LocalLanguage.current
    val scope = rememberCoroutineScope()
    val feedback = rememberFeedback()
    var message by remember { mutableStateOf<Pair<Boolean, String>?>(null) }
    var celebrate by remember { mutableIntStateOf(0) }
    var preview by remember { mutableStateOf<String?>(null) }
    val shown = preview ?: progress.equipped

    LaunchedEffect(message) {
        if (message != null) {
            delay(2600)
            message = null
        }
    }

    fun buy(item: ShopItem) {
        scope.launch {
            when (val r = onBuy(item)) {
                is PurchaseResult.Ok -> {
                    feedback.coin()
                    celebrate++
                    preview = null
                    message = true to lang.pick("${item.title.of(lang)} gekauft!", "${item.title.of(lang)} bought!")
                }
                is PurchaseResult.Failed -> {
                    feedback.wrong()
                    message = false to r.reason.of(lang)
                }
            }
        }
    }

    Box(Modifier.fillMaxSize().screenBackground()) {
        Column(Modifier.fillMaxSize().statusBarsPadding()) {
            Row(
                Modifier
                    .centeredMaxWidth(ReadingWidth)
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Rounded.ArrowBack, tr("Zurück", "Back"), tint = c.text) }
                Text(tr("Shop", "Shop"), style = MaterialTheme.typography.titleLarge, color = c.text, modifier = Modifier.weight(1f))
                Row(
                    Modifier
                        .padding(end = 12.dp)
                        .clip(CircleShape)
                        .background(c.gem.copy(alpha = 0.15f))
                        .border(1.dp, c.gem.copy(alpha = 0.35f), CircleShape)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    BitsIcon(size = 20.dp)
                    Spacer(Modifier.width(5.dp))
                    AnimatedContent(
                        targetState = progress.gems,
                        transitionSpec = { (slideInVertically { -it } + fadeIn()).togetherWith(slideOutVertically { it } + fadeOut()) },
                        label = "bits",
                    ) { g -> Text("$g", style = MaterialTheme.typography.titleMedium, color = c.gem) }
                }
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
                        .padding(16.dp)
                        .navigationBarsPadding()
                ) {
                    Text(tr("Power-ups", "Power-ups"), style = MaterialTheme.typography.titleLarge, color = c.text)
                    Spacer(Modifier.height(10.dp))
                    PowerUp(
                        ShopItem.FREEZE, Icons.Rounded.AcUnit, c.freeze,
                        status = tr("Im Besitz: ${progress.streakFreezes} / ${Game.MAX_FREEZES}", "Owned: ${progress.streakFreezes} / ${Game.MAX_FREEZES}"),
                        enabled = progress.streakFreezes < Game.MAX_FREEZES,
                        bits = progress.gems,
                    ) { buy(ShopItem.FREEZE) }
                    Spacer(Modifier.height(10.dp))
                    PowerUp(
                        ShopItem.REFILL, Icons.Rounded.Favorite, c.heart,
                        status = tr("Herzen: ${progress.hearts} / ${Game.MAX_HEARTS}", "Hearts: ${progress.hearts} / ${Game.MAX_HEARTS}"),
                        enabled = progress.hearts < Game.MAX_HEARTS,
                        bits = progress.gems,
                    ) { buy(ShopItem.REFILL) }
                    Spacer(Modifier.height(10.dp))
                    val boostLeft = ((progress.boostUntil - now) / 60000).coerceAtLeast(0)
                    PowerUp(
                        ShopItem.BOOST, Icons.Rounded.Bolt, c.xp,
                        status = if (progress.boostActive(now)) tr("Aktiv – noch $boostLeft Min.", "Active – $boostLeft min left")
                        else tr("Perfekt für eine Lern-Session", "Perfect for a learning session"),
                        enabled = true,
                        bits = progress.gems,
                    ) { buy(ShopItem.BOOST) }

                    Spacer(Modifier.height(26.dp))
                    Text(tr("Montys Garderobe", "Monty's wardrobe"), style = MaterialTheme.typography.titleLarge, color = c.text)
                    Text(
                        tr("Kaufe Accessoires und tippe sie an, um sie Monty anzuziehen.", "Buy accessories and tap them to dress Monty up."),
                        style = MaterialTheme.typography.bodyMedium,
                        color = c.textMuted,
                    )
                    Spacer(Modifier.height(12.dp))
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .clip(PyTheme.shapes.largeShape)
                            .background(c.surfaceAlt)
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Monty(size = 150.dp, mood = if (preview != null) MontyMood.SURPRISED else MontyMood.HAPPY, accessory = shown)
                    }
                    Spacer(Modifier.height(12.dp))
                    val cosmetics = ShopItem.entries.filter { it.cosmetic }
                    cosmetics.chunked(2).forEach { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            row.forEach { item ->
                                CosmeticCard(
                                    item = item,
                                    owned = item.id in progress.ownedItems,
                                    equipped = progress.equipped == item.id,
                                    previewing = preview == item.id,
                                    canAfford = progress.gems >= item.price,
                                    modifier = Modifier.weight(1f),
                                    onPreview = { preview = if (preview == item.id) null else item.id },
                                    onBuy = { buy(item) },
                                    onEquip = {
                                        feedback.select()
                                        preview = null
                                        onEquip(if (progress.equipped == item.id) null else item.id)
                                    },
                                )
                            }
                            if (row.size == 1) Spacer(Modifier.weight(1f))
                        }
                        Spacer(Modifier.height(10.dp))
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        tr(
                            "Bits verdienst du mit Lektionen, perfekten Runden, Tageszielen, Challenges und Erfolgen.",
                            "You earn bits with lessons, perfect rounds, daily goals, challenges and achievements.",
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = c.textMuted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(30.dp))
                }
            }
        }

        AnimatedVisibility(
            visible = message != null,
            enter = slideInVertically { -it } + fadeIn(),
            exit = slideOutVertically { -it } + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 64.dp),
        ) {
            val (ok, text) = message ?: (true to "")
            val bg = if (ok) c.success else c.error
            Box(
                Modifier
                    .clip(CircleShape)
                    .background(bg)
                    .padding(horizontal = 18.dp, vertical = 10.dp)
            ) {
                Text(text, color = bg.onColor(), style = MaterialTheme.typography.labelLarge)
            }
        }
        if (celebrate > 0) ConfettiBurst(trigger = celebrate, count = 70, durationMs = 2000)
    }
}

@Composable
private fun PriceTag(price: Int, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        BitsIcon(size = 16.dp, color = color)
        Spacer(Modifier.width(4.dp))
        Text("$price", style = MaterialTheme.typography.labelLarge, color = color)
    }
}

@Composable
private fun PowerUp(item: ShopItem, icon: ImageVector, color: Color, status: String, enabled: Boolean, bits: Int, onBuy: () -> Unit) {
    val c = PyTheme.colors
    val lang = LocalLanguage.current
    Row(
        Modifier
            .fillMaxWidth()
            .clip(PyTheme.shapes.largeShape)
            .background(cardColor())
            .border(1.dp, c.border, PyTheme.shapes.largeShape)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(26.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(item.title.of(lang), style = MaterialTheme.typography.titleMedium, color = c.text)
            Text(item.description.of(lang), style = MaterialTheme.typography.bodySmall, color = c.textMuted)
            Text(status, style = MaterialTheme.typography.labelMedium, color = color)
        }
        Spacer(Modifier.width(8.dp))
        ChunkyButton(
            text = "${item.price}",
            onClick = onBuy,
            enabled = enabled && bits >= item.price,
            color = c.gem,
            shadow = c.gemDark,
            height = 42.dp,
            sound = false,
        )
    }
}

@Composable
private fun CosmeticCard(
    item: ShopItem,
    owned: Boolean,
    equipped: Boolean,
    previewing: Boolean,
    canAfford: Boolean,
    modifier: Modifier,
    onPreview: () -> Unit,
    onBuy: () -> Unit,
    onEquip: () -> Unit,
) {
    val c = PyTheme.colors
    val lang = LocalLanguage.current
    ChunkySurface(
        onClick = if (owned) onEquip else onPreview,
        face = if (equipped) c.primarySoft else cardColor(),
        edge = if (equipped || previewing) c.primary else c.border,
        borderWidth = if (equipped || previewing) 2.dp else 1.dp,
        shape = PyTheme.shapes.largeShape,
        modifier = modifier,
        contentPadding = PaddingValues(10.dp),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Monty(size = 74.dp, mood = MontyMood.HAPPY, accessory = item.id, animated = false)
            Text(item.title.of(lang), style = MaterialTheme.typography.titleSmall, color = c.text, textAlign = TextAlign.Center)
            Spacer(Modifier.height(6.dp))
            when {
                equipped -> Text(tr("✓ Angezogen", "✓ Wearing"), style = MaterialTheme.typography.labelLarge, color = c.primaryText)
                owned -> Text(tr("Anziehen", "Put on"), style = MaterialTheme.typography.labelLarge, color = c.textMuted)
                previewing -> ChunkyButton(
                    tr("Kaufen · ${item.price}", "Buy · ${item.price}"), onClick = onBuy, enabled = canAfford,
                    color = c.gem, shadow = c.gemDark, height = 38.dp, sound = false,
                    modifier = Modifier.fillMaxWidth(),
                )
                else -> PriceTag(item.price, if (canAfford) c.gem else c.textMuted)
            }
        }
    }
}
