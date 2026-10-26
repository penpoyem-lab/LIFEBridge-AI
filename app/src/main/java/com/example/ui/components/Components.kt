package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
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
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
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
    val isDark = MaterialTheme.colorScheme.background == UmbraDarkBg

    // Categorize active screen
    val isHome = currentScreen == "home"
    val isUnderstand = currentScreen in listOf("understand", "universal_input", "scanner", "processing_progress", "explanation_results", "action_cards", "translation", "voice", "voice_translate", "live_voice")
    val isSafety = currentScreen in listOf("safety", "safety_checker", "safety_results", "link_safety")
    val isLibrary = currentScreen in listOf("library", "document_detail", "offline_kit", "reminders")
    val isProfile = currentScreen in listOf("profile", "search", "notifications", "settings", "privacy_permissions", "help_support", "account_management", "empty_state", "loading_state", "offline_state", "error_state", "retry_state")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 10.dp, vertical = 8.dp)
            .testTag("bottom_navigation_bar"),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 440.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ==========================================================
            // 1. LEFT ORB: 3D Spherical Liquid Glass Home Button
            // ==========================================================
            val homeScale by animateFloatAsState(
                targetValue = if (isHome) 1.08f else 1.0f,
                animationSpec = spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMediumLow),
                label = "home_orb_scale"
            )

            Box(
                modifier = Modifier
                    .size(54.dp)
                    .scale(homeScale)
                    .shadow(
                        elevation = 14.dp,
                        shape = CircleShape,
                        spotColor = if (isDark) Color(0x88000000) else Color(0x35000000),
                        ambientColor = if (isDark) Color(0x55000000) else Color(0x20000000)
                    )
                    .clip(CircleShape)
                    .background(
                        brush = if (isDark) {
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF5A524A),
                                    Color(0xFF332D28),
                                    Color(0xFF191716)
                                ),
                                center = Offset(45f, 40f),
                                radius = 90f
                            )
                        } else {
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFE8E1D5),
                                    Color(0xFFC7BFAE),
                                    Color(0xFF8F877A)
                                ),
                                center = Offset(45f, 40f),
                                radius = 90f
                            )
                        }
                    )
                    .border(
                        width = 1.35.dp,
                        brush = Brush.verticalGradient(
                            0.0f to Color.White.copy(alpha = if (isDark) 0.85f else 0.95f),
                            0.45f to Color.White.copy(alpha = if (isDark) 0.30f else 0.50f),
                            1.0f to Color.White.copy(alpha = if (isDark) 0.60f else 0.75f)
                        ),
                        shape = CircleShape
                    )
                    .clickable { onNavigate("home") }
                    .testTag("nav_item_home"),
                contentAlignment = Alignment.Center
            ) {
                // Top crescent reflection highlight
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(18.dp)
                        .align(Alignment.TopCenter)
                        .offset(y = 2.dp)
                        .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.White.copy(alpha = if (isDark) 0.35f else 0.55f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Bold black Home Icon matching the screenshot
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "Home",
                    tint = if (isHome) {
                        if (isDark) Color(0xFF0F0E0D) else Color(0xFF151311)
                    } else {
                        if (isDark) Color(0xFF221F1D) else Color(0xFF2C2825)
                    },
                    modifier = Modifier.size(24.dp)
                )

                // Edge triangle cue pointing right into the capsule
                Canvas(
                    modifier = Modifier
                        .size(6.dp, 8.dp)
                        .align(Alignment.CenterEnd)
                        .offset(x = (-3).dp)
                ) {
                    val path = Path().apply {
                        moveTo(0f, 0f)
                        lineTo(size.width, size.height / 2f)
                        lineTo(0f, size.height)
                        close()
                    }
                    drawPath(path, color = Color.White.copy(alpha = 0.90f))
                }
            }

            // ==========================================================
            // 2. CENTER FLOATING LIQUID GLASS CAPSULE (Glacier Aurora)
            // ==========================================================
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(58.dp)
                    .shadow(
                        elevation = 16.dp,
                        shape = RoundedCornerShape(29.dp),
                        spotColor = if (isDark) Color(0x66000000) else Color(0x30000000),
                        ambientColor = if (isDark) Color(0x40F9B588) else Color(0x25F9B588)
                    )
                    .clip(RoundedCornerShape(29.dp))
                    .background(
                        brush = if (isDark) {
                            // Dark mode iridescent glacier/aurora liquid glass
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0x603E3E4C),
                                    Color(0x45244158),
                                    Color(0x505E3C36),
                                    Color(0x65754526),
                                    Color(0x55582D4A)
                                )
                            )
                        } else {
                            // Light mode glacier liquid glass with peach/amber & frosted cyan
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0x95F7F8FB),
                                    Color(0x85D0E3F2),
                                    Color(0x92F8D0BF),
                                    Color(0x9CFBB27D),
                                    Color(0x8CEB976E)
                                )
                            )
                        }
                    )
                    .border(
                        width = 1.35.dp,
                        brush = Brush.verticalGradient(
                            0.0f to Color.White.copy(alpha = if (isDark) 0.85f else 0.98f),
                            0.45f to Color.White.copy(alpha = if (isDark) 0.25f else 0.45f),
                            1.0f to if (isDark) Color(0xFFFFD4AF).copy(alpha = 0.50f) else Color(0xFFFFBE8F).copy(alpha = 0.70f)
                        ),
                        shape = RoundedCornerShape(29.dp)
                    )
                    .padding(horizontal = 7.dp, vertical = 6.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                // Top edge specular sheen ribbon
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.2.dp)
                        .padding(horizontal = 16.dp)
                        .align(Alignment.TopCenter)
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color.Transparent,
                                    Color.White.copy(alpha = if (isDark) 0.70f else 0.95f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // --- Left Squircle Preview Thumbnail Card ---
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .shadow(4.dp, RoundedCornerShape(11.dp), spotColor = Color(0x30000000))
                            .clip(RoundedCornerShape(11.dp))
                            .background(
                                brush = when {
                                    isUnderstand -> Brush.linearGradient(listOf(Color(0xFF0EA5E9), Color(0xFF1E293B)))
                                    isSafety -> Brush.linearGradient(listOf(Color(0xFFEF4444), Color(0xFF7F1D1D)))
                                    isLibrary -> Brush.linearGradient(listOf(Color(0xFF8B5CF6), Color(0xFF312E81)))
                                    else -> Brush.linearGradient(listOf(Color(0xFF475569), Color(0xFF0F172A)))
                                }
                            )
                            .border(1.dp, Color.White.copy(alpha = 0.85f), RoundedCornerShape(11.dp))
                            .clickable {
                                when {
                                    isUnderstand -> onNavigate("universal_input")
                                    isSafety -> onNavigate("safety_checker")
                                    isLibrary -> onNavigate("library")
                                    else -> onNavigate("universal_input")
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when {
                                isUnderstand -> Icons.Filled.Lightbulb
                                isSafety -> Icons.Filled.Shield
                                isLibrary -> Icons.Filled.Folder
                                else -> Icons.Filled.AutoAwesome
                            },
                            contentDescription = "Active Preview",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // --- Center Interactive Tabs (Understand, Safety, Library) with Italic Active Label ---
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val centerTabs = listOf(
                            Triple("understand", "Understand", Icons.Filled.Lightbulb as ImageVector),
                            Triple("safety", "Safety", Icons.Filled.Shield as ImageVector),
                            Triple("library", "Library", Icons.Filled.Folder as ImageVector)
                        )

                        centerTabs.forEach { tab ->
                            val route = tab.first
                            val label = tab.second
                            val icon = tab.third
                            val isSelected = when (route) {
                                "understand" -> isUnderstand
                                "safety" -> isSafety
                                "library" -> isLibrary
                                else -> false
                            }

                            val tabScale by animateFloatAsState(
                                targetValue = if (isSelected) 1.05f else 1.0f,
                                animationSpec = spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMediumLow),
                                label = "tab_scale_${route}"
                            )

                            Box(
                                modifier = Modifier
                                    .scale(tabScale)
                                    .clip(RoundedCornerShape(16.dp))
                                    .then(
                                        if (isSelected) {
                                            Modifier
                                                .background(
                                                    brush = Brush.verticalGradient(
                                                        listOf(
                                                            Color.White.copy(alpha = if (isDark) 0.35f else 0.55f),
                                                            Color.White.copy(alpha = if (isDark) 0.15f else 0.30f)
                                                        )
                                                    )
                                                )
                                                .border(
                                                    width = 1.dp,
                                                    color = Color.White.copy(alpha = 0.65f),
                                                    shape = RoundedCornerShape(16.dp)
                                                )
                                        } else Modifier
                                    )
                                    .clickable {
                                        val target = when (route) {
                                            "understand" -> "universal_input"
                                            "safety" -> "safety_checker"
                                            else -> route
                                        }
                                        onNavigate(target)
                                    }
                                    .padding(horizontal = if (isSelected) 7.dp else 4.dp, vertical = 4.dp)
                                    .testTag("nav_item_${route}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = label,
                                        tint = if (isSelected) {
                                            if (isDark) Color.White else Color(0xFF101217)
                                        } else {
                                            if (isDark) Color(0xFFB5BAC7) else Color(0xFF4A4E5A)
                                        },
                                        modifier = Modifier.size(if (isSelected) 17.dp else 16.dp)
                                    )

                                    if (isSelected) {
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontFamily = FontFamily.Serif,
                                                fontStyle = FontStyle.Italic,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 11.sp,
                                                letterSpacing = 0.2.sp
                                            ),
                                            color = if (isDark) Color.White else Color(0xFF101217),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // --- Right Black Play Action Button ▶ ---
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(
                                if (isDark) Color(0xCC000000) else Color(0xE60A0A0A)
                            )
                            .clickable {
                                // Quick action: launches voice assistant or universal input
                                onNavigate("voice_translate")
                            }
                            .testTag("bottom_bar_play_action"),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(9.dp, 11.dp)) {
                            val path = Path().apply {
                                moveTo(0f, 0f)
                                lineTo(size.width, size.height / 2f)
                                lineTo(0f, size.height)
                                close()
                            }
                            drawPath(path, color = Color.White)
                        }
                    }
                }
            }

            // ==========================================================
            // 3. RIGHT ORB: Magenta/Amethyst Glowing Liquid Glass Button
            // ==========================================================
            val profileScale by animateFloatAsState(
                targetValue = if (isProfile) 1.08f else 1.0f,
                animationSpec = spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMediumLow),
                label = "profile_orb_scale"
            )

            Box(
                modifier = Modifier
                    .size(54.dp)
                    .scale(profileScale)
                    .shadow(
                        elevation = 14.dp,
                        shape = CircleShape,
                        spotColor = Color(0x77D81B60),
                        ambientColor = Color(0x44D81B60)
                    )
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFE85FB4),
                                Color(0xFFC02A88),
                                Color(0xFF6B104E)
                            ),
                            center = Offset(45f, 40f),
                            radius = 90f
                        )
                    )
                    .border(
                        width = 1.35.dp,
                        brush = Brush.verticalGradient(
                            0.0f to Color.White.copy(alpha = 0.92f),
                            0.5f to Color(0xFFFFB6EA).copy(alpha = 0.45f),
                            1.0f to Color.White.copy(alpha = 0.70f)
                        ),
                        shape = CircleShape
                    )
                    .clickable { onNavigate("profile") }
                    .testTag("nav_item_profile"),
                contentAlignment = Alignment.Center
            ) {
                // Top crescent glass highlight
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(18.dp)
                        .align(Alignment.TopCenter)
                        .offset(y = 2.dp)
                        .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.White.copy(alpha = 0.50f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Bold black Search / Magnifying Glass Icon matching the screenshot
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search & Profile",
                    tint = Color(0xFF140810),
                    modifier = Modifier.size(24.dp)
                )

                // Edge triangle cue pointing rightward
                Canvas(
                    modifier = Modifier
                        .size(6.dp, 8.dp)
                        .align(Alignment.CenterEnd)
                        .offset(x = (-3).dp)
                ) {
                    val path = Path().apply {
                        moveTo(0f, 0f)
                        lineTo(size.width, size.height / 2f)
                        lineTo(0f, size.height)
                        close()
                    }
                    drawPath(path, color = Color.White.copy(alpha = 0.90f))
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
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
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
