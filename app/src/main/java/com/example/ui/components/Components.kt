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
    val isHomeSelected = currentScreen == "home"
    val isUnderstandSelected = currentScreen in listOf("understand", "universal_input", "scanner", "processing_progress", "explanation_results", "action_cards", "translation", "voice", "voice_translate")
    val isSafetySelected = currentScreen in listOf("safety", "safety_checker", "safety_results", "link_safety")
    val isLibrarySelected = currentScreen in listOf("library", "document_detail", "offline_kit", "reminders", "search")
    val isProfileSelected = currentScreen in listOf("profile", "settings", "privacy_security", "help_support")

    val isDark = MaterialTheme.colorScheme.background == UmbraDarkBg

    // Floating Composite Glacier Liquid Navigation Bar
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .testTag("bottom_navigation_bar"),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. Left Floating Circular Glass Orb (Home)
        val homeScale by animateFloatAsState(
            targetValue = if (isHomeSelected) 1.08f else 1.0f,
            animationSpec = spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMedium),
            label = "home_orb_scale"
        )

        Box(
            modifier = Modifier
                .size(54.dp)
                .scale(homeScale)
                .shadow(
                    elevation = if (isHomeSelected) 14.dp else 8.dp,
                    shape = CircleShape,
                    spotColor = if (isDark) Color(0x66000000) else Color(0x33000000)
                )
                .clip(CircleShape)
                .background(
                    brush = if (isDark) {
                        if (isHomeSelected) {
                            Brush.linearGradient(listOf(Color(0xFF383842), Color(0xFF222228)))
                        } else {
                            Brush.linearGradient(listOf(Color(0xFF25252C), Color(0xFF16161A)))
                        }
                    } else {
                        if (isHomeSelected) {
                            Brush.linearGradient(listOf(Color(0xFFD6D6DE), Color(0xFFB8B8C2)))
                        } else {
                            Brush.linearGradient(listOf(Color(0xFFF2F2F7), Color(0xFFE5E5EA)))
                        }
                    }
                )
                .border(
                    width = 1.4.dp,
                    brush = Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = if (isDark) 0.55f else 0.95f),
                            Color.White.copy(alpha = if (isDark) 0.10f else 0.40f)
                        )
                    ),
                    shape = CircleShape
                )
                .clickable { onNavigate("home") }
                .testTag("nav_item_home"),
            contentAlignment = Alignment.Center
        ) {
            // Top-lit 3D glass spherical highlight
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.48f)
                    .align(Alignment.TopCenter)
                    .clip(RoundedCornerShape(topStart = 27.dp, topEnd = 27.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = if (isDark) 0.35f else 0.60f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Icon(
                imageVector = Icons.Filled.Home,
                contentDescription = "Home",
                tint = if (isDark) {
                    if (isHomeSelected) Color.White else Color(0xFFA0A0AA)
                } else {
                    if (isHomeSelected) Color(0xFF111114) else Color(0xFF555560)
                },
                modifier = Modifier.size(24.dp)
            )
        }

        // 2. Center Floating Glacier Liquid Glass Capsule
        val activeTitle = when {
            isUnderstandSelected -> "Understand Docs"
            isSafetySelected -> "Safety Shield"
            isLibrarySelected -> "Life Library"
            isProfileSelected -> "Alex Profile"
            else -> "LIFEBridge Active"
        }

        val activeIcon = when {
            isUnderstandSelected -> Icons.Filled.Lightbulb
            isSafetySelected -> Icons.Filled.Shield
            isLibrarySelected -> Icons.Filled.Folder
            isProfileSelected -> Icons.Filled.Person
            else -> Icons.Filled.AutoAwesome
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .height(58.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(29.dp),
                    spotColor = if (isDark) Color(0x66000000) else Color(0x388CE8FF),
                    ambientColor = if (isDark) Color(0x33000000) else Color(0x22D69EFF)
                )
                .clip(RoundedCornerShape(29.dp))
                .background(
                    brush = if (isDark) {
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xD82A2B35),
                                Color(0xEE1A1B22),
                                Color(0xD824252E)
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(200f, 100f)
                        )
                    } else {
                        // High-contrast vibrant glacier liquid aurora gradient
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF8CE8FF).copy(alpha = 0.85f),
                                Color(0xFFFFB28E).copy(alpha = 0.82f),
                                Color(0xFFFFD479).copy(alpha = 0.78f),
                                Color(0xFFD69EFF).copy(alpha = 0.85f),
                                Color(0xFFEAF5FF).copy(alpha = 0.95f)
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(300f, 120f)
                        )
                    }
                )
                .border(
                    width = 1.35.dp,
                    brush = Brush.verticalGradient(
                        0.0f to Color.White.copy(alpha = if (isDark) 0.65f else 0.98f),
                        0.5f to Color(0xFF8CE8FF).copy(alpha = 0.40f),
                        1.0f to Color.White.copy(alpha = if (isDark) 0.10f else 0.35f)
                    ),
                    shape = RoundedCornerShape(29.dp)
                )
                .padding(horizontal = 6.dp, vertical = 5.dp),
            contentAlignment = Alignment.Center
        ) {
            // Upper specular reflection arc inside capsule
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.48f)
                    .align(Alignment.TopCenter)
                    .clip(RoundedCornerShape(topStart = 29.dp, topEnd = 29.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = if (isDark) 0.22f else 0.45f),
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
                // Mini squircle thumbnail card
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .shadow(elevation = 4.dp, shape = RoundedCornerShape(14.dp))
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (isDark) Color(0xFF2C2D38) else Color(0xCCFFFFFF)
                        )
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.75f),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clickable {
                            val target = when {
                                isUnderstandSelected -> "universal_input"
                                isSafetySelected -> "safety_checker"
                                isLibrarySelected -> "library"
                                else -> "universal_input"
                            }
                            onNavigate(target)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = activeIcon,
                        contentDescription = activeTitle,
                        tint = if (isDark) Color(0xFF8CE8FF) else Color(0xFF14151A),
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Center Navigation Tabs & Title
                Row(
                    modifier = Modifier.weight(1f).padding(horizontal = 6.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Understand Tab
                    IconButton(
                        onClick = { onNavigate("universal_input") },
                        modifier = Modifier.size(36.dp).testTag("nav_item_understand")
                    ) {
                        Icon(
                            imageVector = if (isUnderstandSelected) Icons.Filled.Lightbulb else Icons.Outlined.Lightbulb,
                            contentDescription = "Understand",
                            tint = if (isUnderstandSelected) {
                                if (isDark) Color(0xFF8CE8FF) else Color(0xFF111115)
                            } else {
                                if (isDark) Color(0xFF8E8E98) else Color(0xAA111115)
                            },
                            modifier = Modifier.size(if (isUnderstandSelected) 23.dp else 19.dp)
                        )
                    }

                    // Safety Tab
                    IconButton(
                        onClick = { onNavigate("safety_checker") },
                        modifier = Modifier.size(36.dp).testTag("nav_item_safety")
                    ) {
                        Icon(
                            imageVector = if (isSafetySelected) Icons.Filled.Shield else Icons.Outlined.Shield,
                            contentDescription = "Safety",
                            tint = if (isSafetySelected) {
                                if (isDark) Color(0xFF8CE8FF) else Color(0xFF111115)
                            } else {
                                if (isDark) Color(0xFF8E8E98) else Color(0xAA111115)
                            },
                            modifier = Modifier.size(if (isSafetySelected) 23.dp else 19.dp)
                        )
                    }

                    // Library Tab
                    IconButton(
                        onClick = { onNavigate("library") },
                        modifier = Modifier.size(36.dp).testTag("nav_item_library")
                    ) {
                        Icon(
                            imageVector = if (isLibrarySelected) Icons.Filled.Folder else Icons.Outlined.Folder,
                            contentDescription = "Library",
                            tint = if (isLibrarySelected) {
                                if (isDark) Color(0xFF8CE8FF) else Color(0xFF111115)
                            } else {
                                if (isDark) Color(0xFF8E8E98) else Color(0xAA111115)
                            },
                            modifier = Modifier.size(if (isLibrarySelected) 23.dp else 19.dp)
                        )
                    }
                }

                // Forward Play/Action Arrow inside Glacier Capsule
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            if (isDark) Color(0x33FFFFFF) else Color(0x28000000)
                        )
                        .clickable {
                            val nextTarget = when {
                                isHomeSelected -> "universal_input"
                                isUnderstandSelected -> "voice_translate"
                                isSafetySelected -> "offline_kit"
                                else -> "universal_input"
                            }
                            onNavigate(nextTarget)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "Action",
                        tint = if (isDark) Color.White else Color(0xFF111115),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // 3. Right Floating Circular Glass Orb (Profile / Search with Magenta/Violet Glow)
        val profileScale by animateFloatAsState(
            targetValue = if (isProfileSelected) 1.08f else 1.0f,
            animationSpec = spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMedium),
            label = "profile_orb_scale"
        )

        Box(
            modifier = Modifier
                .size(54.dp)
                .scale(profileScale)
                .shadow(
                    elevation = if (isProfileSelected) 16.dp else 10.dp,
                    shape = CircleShape,
                    spotColor = Color(0x889C27B0),
                    ambientColor = Color(0x55D69EFF)
                )
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFD69EFF),
                            Color(0xFFBA68C8),
                            Color(0xFF8E24AA)
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(100f, 100f)
                    )
                )
                .border(
                    width = 1.4.dp,
                    brush = Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.95f),
                            Color(0xFFD69EFF).copy(alpha = 0.60f),
                            Color.White.copy(alpha = 0.20f)
                        )
                    ),
                    shape = CircleShape
                )
                .clickable { onNavigate("profile") }
                .testTag("nav_item_profile"),
            contentAlignment = Alignment.Center
        ) {
            // Spherical 3D glass dome reflection
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.48f)
                    .align(Alignment.TopCenter)
                    .clip(RoundedCornerShape(topStart = 27.dp, topEnd = 27.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.55f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = "Profile",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
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
