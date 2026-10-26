package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.animation.animateColorAsState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import kotlinx.coroutines.delay
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(24.dp),
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color = MaterialTheme.colorScheme.outline,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val clickModifier = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else Modifier

    Column(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor)
            .border(1.dp, borderColor, shape)
            .then(clickModifier)
            .padding(18.dp),
        content = content
    )
}

@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    colors: List<Color> = listOf(Umbra, Color(0xFF2C2C2E))
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .clip(RoundedCornerShape(27.dp))
            .background(
                if (enabled) Brush.horizontalGradient(colors)
                else Brush.horizontalGradient(listOf(CountryRubble.copy(alpha = 0.5f), CountryRubble.copy(alpha = 0.3f)))
            )
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PureWhite,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                color = PureWhite,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun EditorialSquircleCard(
    title: String,
    subtitle: String? = null,
    backgroundColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    decorativeIcon: ImageVector? = null,
    textColor: Color = Umbra
) {
    Box(
        modifier = modifier
            .height(145.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(18.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    lineHeight = 22.sp
                )
                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = textColor.copy(alpha = 0.75f)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                if (decorativeIcon != null) {
                    Icon(
                        imageVector = decorativeIcon,
                        contentDescription = null,
                        tint = textColor.copy(alpha = 0.28f),
                        modifier = Modifier.size(32.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                // ↗ Diagonal arrow circle
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(textColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Open",
                        tint = textColor,
                        modifier = Modifier
                            .size(16.dp)
                            .rotate(-45f)
                    )
                }
            }
        }
    }
}

@Composable
fun PriorityBadge(priority: String) {
    val (bgColor, textColor) = when (priority.lowercase()) {
        "high" -> Pair(PastelCoral.copy(alpha = 0.3f), Color(0xFFC2410C))
        "medium" -> Pair(PastelWheat.copy(alpha = 0.4f), Color(0xFF854D0E))
        else -> Pair(PastelMint.copy(alpha = 0.45f), Color(0xFF166534))
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .padding(horizontal = 9.dp, vertical = 4.dp)
    ) {
        Text(
            text = priority.replaceFirstChar { it.uppercase() },
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun CategoryBadge(category: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Hudson.copy(alpha = 0.5f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = category,
            color = Umbra,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun WaveformVisualizer(
    isAnimating: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")
    val heights = listOf(
        infiniteTransition.animateFloat(
            initialValue = 12f, targetValue = 48f,
            animationSpec = infiniteRepeatable(tween(450, easing = LinearEasing), RepeatMode.Reverse), label = "h1"
        ),
        infiniteTransition.animateFloat(
            initialValue = 24f, targetValue = 64f,
            animationSpec = infiniteRepeatable(tween(350, easing = LinearEasing), RepeatMode.Reverse), label = "h2"
        ),
        infiniteTransition.animateFloat(
            initialValue = 16f, targetValue = 56f,
            animationSpec = infiniteRepeatable(tween(550, easing = LinearEasing), RepeatMode.Reverse), label = "h3"
        ),
        infiniteTransition.animateFloat(
            initialValue = 32f, targetValue = 72f,
            animationSpec = infiniteRepeatable(tween(400, easing = LinearEasing), RepeatMode.Reverse), label = "h4"
        ),
        infiniteTransition.animateFloat(
            initialValue = 20f, targetValue = 50f,
            animationSpec = infiniteRepeatable(tween(480, easing = LinearEasing), RepeatMode.Reverse), label = "h5"
        )
    )

    Row(
        modifier = modifier.height(80.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        heights.forEach { animHeight ->
            val heightDp = if (isAnimating) animHeight.value.dp else 16.dp
            Box(
                modifier = Modifier
                    .width(8.dp)
                    .height(heightDp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Umbra)
            )
        }
    }
}

@Composable
fun RiskGauge(score: Int, riskLevel: String) {
    val (color, label) = when {
        score >= 70 -> Pair(PastelCoral, "High Risk")
        score >= 35 -> Pair(PastelWheat, "Warning Signs")
        else -> Pair(FarmersMarket, "Verified Safe")
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.25f))
                .border(6.dp, color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$score",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Umbra,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "/ 100",
                    style = MaterialTheme.typography.bodySmall,
                    color = CharcoalMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(color.copy(alpha = 0.35f))
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Text(
                text = label,
                color = Umbra,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun LifeBridgeBottomBar(
    currentScreen: String,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        NavigationTab("home", "Home", Icons.Filled.Home, Icons.Outlined.Home),
        NavigationTab("understand", "Understand", Icons.Filled.Lightbulb, Icons.Outlined.Lightbulb),
        NavigationTab("safety", "Safety", Icons.Filled.Shield, Icons.Outlined.Shield),
        NavigationTab("library", "Library", Icons.Filled.Folder, Icons.Outlined.Folder),
        NavigationTab("profile", "Profile", Icons.Filled.Person, Icons.Outlined.Person)
    )

    val activeIndex = when {
        currentScreen == "home" -> 0
        currentScreen in listOf("understand", "universal_input", "scanner", "processing_progress", "explanation_results", "action_cards", "translation", "voice", "voice_translate") -> 1
        currentScreen in listOf("safety", "safety_checker", "safety_results", "link_safety") -> 2
        currentScreen in listOf("library", "document_detail", "offline_kit", "reminders", "search") -> 3
        else -> 4
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .testTag("bottom_navigation_bar"),
        contentAlignment = Alignment.Center
    ) {
        val isDark = MaterialTheme.colorScheme.background == UmbraDarkBg
        // Translucent Frosted Glass Dock Colors
        val dockBg = if (isDark) Color(0xD01C1C20) else Color(0xD6FAFAFC)
        val inactiveIconTint = if (isDark) Color(0xFF9E9E9E) else Umbra.copy(alpha = 0.55f)

        // Apple Translucent Frosted Glass Dock with Specular Edge Lighting
        BoxWithConstraints(
            modifier = Modifier
                .widthIn(max = 380.dp)
                .fillMaxWidth()
                .height(64.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(32.dp),
                    spotColor = if (isDark) Color(0x66000000) else Color(0x28000000),
                    ambientColor = if (isDark) Color(0x38000000) else Color(0x14000000)
                )
                .clip(RoundedCornerShape(32.dp))
                .background(dockBg)
                .border(
                    width = 1.25.dp,
                    brush = Brush.verticalGradient(
                        0.0f to if (isDark) Color.White.copy(alpha = 0.45f) else Color.White.copy(alpha = 0.95f),
                        0.40f to if (isDark) Color.White.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.40f),
                        1.0f to if (isDark) Color.White.copy(alpha = 0.04f) else Color.White.copy(alpha = 0.15f)
                    ),
                    shape = RoundedCornerShape(32.dp)
                )
                .padding(horizontal = 6.dp, vertical = 6.dp)
        ) {
            val totalWidth = maxWidth
            val tabWidth = totalWidth / items.size

            // Dynamic Viscous Jelly Spring Squash-and-Stretch calculation
            var isTransitioning by remember { mutableStateOf(false) }
            LaunchedEffect(activeIndex) {
                isTransitioning = true
                delay(160)
                isTransitioning = false
            }

            val animatedOffset by animateDpAsState(
                targetValue = tabWidth * activeIndex,
                animationSpec = spring(
                    dampingRatio = 0.62f, // Viscous liquid jelly damping
                    stiffness = Spring.StiffnessMediumLow
                ),
                label = "liquid_capsule_sliding"
            )

            val capsuleStretchX by animateFloatAsState(
                targetValue = if (isTransitioning) 1.15f else 1.0f,
                animationSpec = spring(
                    dampingRatio = 0.58f,
                    stiffness = Spring.StiffnessMediumLow
                ),
                label = "capsule_stretch_x"
            )

            val capsuleSquashY by animateFloatAsState(
                targetValue = if (isTransitioning) 0.88f else 1.0f,
                animationSpec = spring(
                    dampingRatio = 0.58f,
                    stiffness = Spring.StiffnessMediumLow
                ),
                label = "capsule_squash_y"
            )

            // Top specular glass lens reflection strip along dock
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .padding(horizontal = 20.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = if (isDark) 0.35f else 0.75f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Apple Liquid Glass Sliding Capsule with Specular Rim & Squash-Stretch
            Box(
                modifier = Modifier
                    .offset(x = animatedOffset)
                    .width(tabWidth)
                    .fillMaxHeight()
                    .padding(horizontal = 3.dp, vertical = 2.dp)
                    .graphicsLayer {
                        scaleX = capsuleStretchX
                        scaleY = capsuleSquashY
                    }
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(20.dp),
                        spotColor = if (isDark) Color(0x55000000) else Color(0x35000000)
                    )
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        brush = if (isDark) {
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0x60FFFFFF),
                                    Color(0x22FFFFFF),
                                    Color(0x38FFFFFF)
                                ),
                                start = Offset(0f, 0f),
                                end = Offset(120f, 120f)
                            )
                        } else {
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xEB2E2E34),
                                    Color(0xF518181B)
                                ),
                                start = Offset(0f, 0f),
                                end = Offset(120f, 120f)
                            )
                        }
                    )
                    .border(
                        width = 1.2.dp,
                        brush = Brush.verticalGradient(
                            0.0f to Color.White.copy(alpha = if (isDark) 0.85f else 0.50f),
                            0.5f to Color.White.copy(alpha = if (isDark) 0.20f else 0.15f),
                            1.0f to Color.White.copy(alpha = if (isDark) 0.45f else 0.10f)
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
            ) {
                // Top-half specular gloss refraction inside liquid capsule
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.50f)
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(
                                    Color.White.copy(alpha = if (isDark) 0.28f else 0.18f),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }

            // Interactive Navigation Items Row over sliding capsule
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEachIndexed { index, tab ->
                    val isSelected = index == activeIndex
                    val iconTint by animateColorAsState(
                        targetValue = if (isSelected) PureWhite else inactiveIconTint,
                        animationSpec = tween(220),
                        label = "icon_color"
                    )
                    val iconScale by animateFloatAsState(
                        targetValue = if (isSelected) 1.15f else 1.0f,
                        animationSpec = spring(
                            dampingRatio = 0.60f,
                            stiffness = Spring.StiffnessMedium
                        ),
                        label = "icon_scale"
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(20.dp))
                            .clickable {
                                val target = when (tab.route) {
                                    "understand" -> "universal_input"
                                    "safety" -> "safety_checker"
                                    else -> tab.route
                                }
                                onNavigate(target)
                            }
                            .testTag("nav_item_${tab.route}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                            contentDescription = tab.label,
                            tint = iconTint,
                            modifier = Modifier
                                .size(23.dp)
                                .scale(iconScale)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Unique & Aesthetic Liquid Glass Note Recall Card
 */
@Composable
fun AestheticNoteRecallCard(
    recall: com.example.data.local.entities.NoteRecallEntity,
    onListen: () -> Unit,
    onSnooze: () -> Unit,
    onDone: () -> Unit,
    onDismiss: () -> Unit,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val accentColor = when (recall.category) {
        "Deadline" -> PastelWheat
        "Prescription" -> Hudson
        "Notice" -> Penna
        else -> PastelMint
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(26.dp),
                spotColor = Color(0x1F000000),
                ambientColor = Color(0x10000000)
            )
            .clip(RoundedCornerShape(26.dp))
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        PureWhite.copy(alpha = 0.95f),
                        accentColor.copy(alpha = 0.22f)
                    )
                )
            )
            .border(
                width = 1.2.dp,
                brush = Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.95f),
                        accentColor.copy(alpha = 0.40f)
                    )
                ),
                shape = RoundedCornerShape(26.dp)
            )
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(18.dp)
            .testTag("note_recall_card_${recall.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Row: Pulsing Recall Beacon + Time Tag + Dismiss
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Pulsing animated indicator beacon
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Umbra)
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Umbra.copy(alpha = 0.08f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "NOTE RECALL • ${recall.scheduledTimeFormatted.uppercase()}",
                            color = Umbra,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            letterSpacing = 0.8.sp
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Dismiss",
                        tint = CharcoalMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title
            Text(
                text = recall.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Umbra,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Snippet
            Text(
                text = recall.snippet,
                style = MaterialTheme.typography.bodyMedium,
                color = CharcoalMuted,
                lineHeight = 20.sp,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Action Pills Row: [Listen Audio] [Snooze 1h] [Mark Done]
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Listen Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Umbra)
                        .clickable { onListen() }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.VolumeUp,
                            contentDescription = "Listen",
                            tint = PureWhite,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Listen",
                            color = PureWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                // Snooze Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(PureWhite.copy(alpha = 0.85f))
                        .border(1.dp, Umbra.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                        .clickable { onSnooze() }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Schedule,
                            contentDescription = "Snooze",
                            tint = Umbra,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Snooze 1h",
                            color = Umbra,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    }
                }

                // Done Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(PureWhite.copy(alpha = 0.85f))
                        .border(1.dp, Umbra.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                        .clickable { onDone() }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Done",
                            tint = Umbra,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Done",
                            color = Umbra,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

data class NavigationTab(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)
