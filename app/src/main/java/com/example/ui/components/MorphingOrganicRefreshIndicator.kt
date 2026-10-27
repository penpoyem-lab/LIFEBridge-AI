package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Curated high-quality aesthetic color palette cycling one-by-one,
 * starting with the exact warm olive-gold from the reference video.
 */
/**
 * Curated high-definition aesthetic color palette cycling one-by-one,
 * starting with the exact periwinkle and frames from the reference video.
 */
val AestheticVideoPalette = listOf(
    Color(0xFF748FFC), // 1. Video Frame 0: Sky Periwinkle Blue
    Color(0xFFFF6B6B), // 2. Video Coral Sunset
    Color(0xFF20C997), // 3. Botanical Mint Emerald
    Color(0xFFFCC419), // 4. Golden Honey Amber
    Color(0xFFF06595), // 5. Mulberry Blossom Rose
    Color(0xFF845EF7), // 6. Royal Lavender Violet
    Color(0xFF22B8CF), // 7. Electric Arctic Cyan
    Color(0xFFFD7E14)  // 8. Warm Saffron Tangerine
)

/**
 * Curated high-quality aesthetic color palette cycling one-by-one,
 * starting with the exact warm olive-gold from the reference video.
 */
val OrganicAestheticColors = listOf(
    Color(0xFF5D5A1B), // 1. Video Olive Gold (exact frame color)
    Color(0xFFC45A38), // 2. Terracotta Coral
    Color(0xFF2C6B53), // 3. Botanical Sage Jade
    Color(0xFF334479), // 4. Swiss Royal Indigo
    Color(0xFFBE6E1B), // 5. Honey Saffron Amber
    Color(0xFF7E3354), // 6. Mulberry Wine
    Color(0xFF2D6E7E), // 7. Aegean Slate Teal
    Color(0xFF8A7336)  // 8. Warm Brass Ochre
)

/**
 * Smooth Hermite step interpolation between 0 and 1.
 */
private fun smoothStep(t: Float): Float {
    val clamped = t.coerceIn(0f, 1f)
    return clamped * clamped * (3f - 2f * clamped)
}

/**
 * Standalone Sinusoidal Wavy Open-Stroke Loader from the exact video attachment:
 * - Radial harmonic sine wave stroke along circumference: r(theta) = R + A * sin(12 * theta)
 * - Open arc stroke with StrokeCap.Round (not a filled blob)
 * - Indeterminate expansion/contraction (short squiggly worm -> full undulating loop -> contracts)
 * - 100% transparent background
 * - Smooth one-by-one cycling across high-aesthetic color palette
 */
@Composable
fun WavyCircularStrokeLoader(
    modifier: Modifier = Modifier,
    sizeDp: Dp = 64.dp,
    strokeWidthDp: Dp = 3.8.dp,
    waveCount: Int = 12,
    waveAmplitudeFraction: Float = 0.12f,
    isAnimating: Boolean = true,
    manualProgress: Float? = null
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wavy_stroke_loader")

    // Continuous smooth rotation (2400ms per full cycle)
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wavy_rotation"
    )

    // Indeterminate arc sweep expansion and contraction
    val arcPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wavy_arc_phase"
    )

    // Secondary subtle wave phase undulation to make waves breathe organically
    val wavePhaseAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wavy_undulation"
    )

    // Color cycle progress across the aesthetic palette
    val colorCycleProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = AestheticVideoPalette.size.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1800 * AestheticVideoPalette.size,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "wavy_color_cycle"
    )

    // Compute current color
    val currentColor = remember(colorCycleProgress, manualProgress) {
        if (manualProgress != null) {
            val idx = (manualProgress * (AestheticVideoPalette.size - 1)).toInt() % AestheticVideoPalette.size
            val nextIdx = (idx + 1) % AestheticVideoPalette.size
            val f = (manualProgress * (AestheticVideoPalette.size - 1)) - idx
            lerp(AestheticVideoPalette[idx], AestheticVideoPalette[nextIdx], f.coerceIn(0f, 1f))
        } else {
            val colorIdx = colorCycleProgress.toInt() % AestheticVideoPalette.size
            val nextColorIdx = (colorIdx + 1) % AestheticVideoPalette.size
            val colorFraction = smoothStep(colorCycleProgress - colorCycleProgress.toInt())
            lerp(AestheticVideoPalette[colorIdx], AestheticVideoPalette[nextColorIdx], colorFraction)
        }
    }

    // Determine arc sweep range
    val (startRad, endRad) = remember(arcPhase, manualProgress, rotationAngle) {
        if (manualProgress != null) {
            val sweep = (manualProgress.coerceIn(0.08f, 1.0f)) * (2f * PI.toFloat())
            Pair(0f, sweep)
        } else if (isAnimating) {
            // Expansion (0 -> 0.5) and contraction (0.5 -> 1.0)
            val headProgress = if (arcPhase < 0.5f) {
                smoothStep(arcPhase * 2f)
            } else 1.0f

            val tailProgress = if (arcPhase >= 0.5f) {
                smoothStep((arcPhase - 0.5f) * 2f)
            } else 0f

            val minSweep = (25f / 360f) * (2f * PI.toFloat())
            val maxSweep = (310f / 360f) * (2f * PI.toFloat())

            val start = tailProgress * maxSweep
            val end = (minSweep + headProgress * (maxSweep - minSweep)).coerceAtLeast(start + minSweep)
            Pair(start, end)
        } else {
            Pair(0f, (280f / 360f) * (2f * PI.toFloat()))
        }
    }

    val currentRotation = if (isAnimating) rotationAngle else (manualProgress ?: 0f) * 180f

    Box(
        modifier = modifier
            .size(sizeDp)
            .background(Color.Transparent)
            .rotate(currentRotation),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
        ) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val baseRadius = (size.minDimension / 2f) * 0.74f
            val waveAmplitude = baseRadius * waveAmplitudeFraction
            val strokeWidthPx = strokeWidthDp.toPx()

            val sweepAngleTotal = (endRad - startRad)
            val sampleCount = (sweepAngleTotal * 40f).toInt().coerceIn(30, 240)
            val angleStep = sweepAngleTotal / sampleCount

            val path = Path()

            for (i in 0..sampleCount) {
                val currentTheta = startRad + (i * angleStep)
                // Modulate radius by sinusoidal harmonic
                val radius = baseRadius + waveAmplitude * sin((waveCount * currentTheta) + (if (isAnimating) wavePhaseAngle else 0f))
                val px = center.x + radius * cos(currentTheta)
                val py = center.y + radius * sin(currentTheta)

                if (i == 0) {
                    path.moveTo(px, py)
                } else {
                    path.lineTo(px, py)
                }
            }

            // Draw glowing halo stroke under layer
            drawPath(
                path = path,
                color = currentColor.copy(alpha = 0.28f),
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = strokeWidthPx * 1.9f,
                    cap = androidx.compose.ui.graphics.StrokeCap.Round,
                    join = androidx.compose.ui.graphics.StrokeJoin.Round
                )
            )

            // Draw crisp primary wavy stroke
            drawPath(
                path = path,
                color = currentColor,
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = strokeWidthPx,
                    cap = androidx.compose.ui.graphics.StrokeCap.Round,
                    join = androidx.compose.ui.graphics.StrokeJoin.Round
                )
            )
        }
    }
}

/**
 * Computes the normalized radial distance r(theta) for the 3 morphing stages:
 * Stage 0: 14-scalloped seal (Frame 00:00)
 * Stage 1: Rounded pentagon / 5-gon (Frame 00:01)
 * Stage 2: 8-pointed rounded star (Frame 00:02)
 */
private fun computeRadiusFactor(theta: Float, stageProgress: Float): Float {
    // Stage 0: 14 scallops (crests & valleys)
    val r0 = 1.0f + 0.115f * cos(14f * theta)

    // Stage 1: Rounded 5-gon (pentagon with rounded corners and curved sides)
    val r1 = 1.0f + 0.165f * cos(5f * theta) - 0.04f * cos(10f * theta)

    // Stage 2: 8-pointed star (8 rounded peaks)
    val r2 = 1.0f + 0.185f * cos(8f * theta) - 0.045f * cos(16f * theta)

    val wrapped = stageProgress % 3f
    val currentStage = wrapped.toInt()
    val frac = smoothStep(wrapped - currentStage)

    return when (currentStage) {
        0 -> r0 * (1f - frac) + r1 * frac
        1 -> r1 * (1f - frac) + r2 * frac
        else -> r2 * (1f - frac) + r0 * frac
    }
}

/**
 * Standalone organic shape morphing canvas that reproduces the exact video animation:
 * - 14-scallop flower -> rounded pentagon -> 8-pointed star
 * - Smooth one-by-one cycling across aesthetic colors
 * - Continuous organic rotation and subtle scale breath
 */
@Composable
fun MorphingOrganicBlob(
    modifier: Modifier = Modifier,
    isAnimating: Boolean = true,
    manualProgress: Float? = null,
    sizeDp: Dp = 32.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "organic_morph")

    // Shape morph cycle: 0 -> 3 (3 seconds per full cycle)
    val animatedStageProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "stage_progress"
    )

    // Continuous slow rotation
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Subtle organic breathing scale
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    // Color cycle progress across the palette
    val colorProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = AestheticVideoPalette.size.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 2000 * AestheticVideoPalette.size,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "color_progress"
    )

    val currentStageProgress = manualProgress ?: (if (isAnimating) animatedStageProgress else 0f)
    val currentRotation = if (isAnimating) rotationAngle else (manualProgress ?: 0f) * 120f
    val currentScale = if (isAnimating) pulseScale else 1f

    // Calculate current interpolated color
    val currentColor = remember(colorProgress, manualProgress) {
        if (manualProgress != null) {
            val idx = (manualProgress * 2f).toInt() % AestheticVideoPalette.size
            val nextIdx = (idx + 1) % AestheticVideoPalette.size
            val f = smoothStep((manualProgress * 2f) - (manualProgress * 2f).toInt())
            lerp(AestheticVideoPalette[idx], AestheticVideoPalette[nextIdx], f)
        } else {
            val colorIdx = colorProgress.toInt() % AestheticVideoPalette.size
            val nextColorIdx = (colorIdx + 1) % AestheticVideoPalette.size
            val colorFraction = smoothStep(colorProgress - colorProgress.toInt())
            lerp(AestheticVideoPalette[colorIdx], AestheticVideoPalette[nextColorIdx], colorFraction)
        }
    }

    Box(
        modifier = modifier
            .size(sizeDp)
            .scale(currentScale)
            .rotate(currentRotation),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val baseRadius = (size.minDimension / 2f) * 0.72f

            val path = Path()
            val sampleCount = 300
            val angleStep = (2f * PI.toFloat()) / sampleCount

            for (i in 0 until sampleCount) {
                val theta = i * angleStep
                val radiusFactor = computeRadiusFactor(theta, currentStageProgress)
                val r = baseRadius * radiusFactor
                val x = center.x + r * cos(theta)
                val y = center.y + r * sin(theta)

                if (i == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }
            }
            path.close()

            // Subtle luminous inner core fill
            drawPath(
                path = path,
                color = currentColor.copy(alpha = 0.88f)
            )

            // Outer specular glow stroke
            drawPath(
                path = path,
                color = currentColor,
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = 2.5f * density,
                    cap = androidx.compose.ui.graphics.StrokeCap.Round,
                    join = androidx.compose.ui.graphics.StrokeJoin.Round
                )
            )
        }
    }
}

/**
 * Universal Middle-of-Screen Transparent Loading Indicator.
 * Faithfully renders the video animation:
 * - Positioned in the exact center/middle of the screen
 * - 100% transparent background (no box, no container card, no opaque backdrop)
 * - Sinusoidal wavy arc stroke rotating and undulating along the circle
 * - Aesthetic colors cycling one-by-one smoothly (Sky Periwinkle -> Coral -> Mint -> Honey -> Mulberry -> Lavender -> Cyan)
 * - High-definition Swiss editorial typography
 */
@Composable
fun MiddleScreenTransparentLoader(
    modifier: Modifier = Modifier,
    statusMessage: String? = null,
    subMessage: String? = null,
    sizeDp: Dp = 68.dp,
    isAnimating: Boolean = true
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .background(Color.Transparent)
                .padding(24.dp)
        ) {
            // Video-Accurate Wavy Circular Stroke Loader with 100% Transparent Background
            WavyCircularStrokeLoader(
                isAnimating = isAnimating,
                sizeDp = sizeDp,
                strokeWidthDp = 4.2.dp
            )

            if (!statusMessage.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = statusMessage,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    letterSpacing = 0.3.sp
                )
            }

            if (!subMessage.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = subMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 0.2.sp
                )
            }
        }
    }
}

/**
 * Transparent Liquid Glass Badge with subtle drop shadow and specular border.
 */
@Composable
fun LiquidGlassMorphingBadge(
    modifier: Modifier = Modifier,
    isRefreshing: Boolean = true,
    manualProgress: Float? = null,
    badgeSize: Dp = 52.dp,
    blobSize: Dp = 30.dp
) {
    val isDark = MaterialTheme.colorScheme.background.value == UmbraDarkBg.value

    Box(
        modifier = modifier
            .size(badgeSize)
            .shadow(
                elevation = 6.dp,
                shape = CircleShape,
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.12f)
            )
            .clip(CircleShape)
            .background(
                Brush.verticalGradient(
                    if (isDark) listOf(
                        Color(0xFF2C2C2E).copy(alpha = 0.85f),
                        Color(0xFF1C1C1E).copy(alpha = 0.70f)
                    ) else listOf(
                        Color.White.copy(alpha = 0.88f),
                        Color(0xFFFAF9F5).copy(alpha = 0.72f)
                    )
                )
            )
            .border(
                width = 1.2.dp,
                brush = Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = if (isDark) 0.35f else 0.95f),
                        Color.White.copy(alpha = if (isDark) 0.08f else 0.40f)
                    )
                ),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        MorphingOrganicBlob(
            isAnimating = isRefreshing,
            manualProgress = manualProgress,
            sizeDp = blobSize
        )
    }
}

/**
 * Complete Pull-to-Refresh Container with Liquid Glass & Video Morphing Animation.
 * Wraps content with pull physics, spring release, haptic feedback, and cryptographic verification status.
 */
@Composable
fun LiquidGlassPullToRefreshContainer(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()

    val pullDistanceThreshold = 80.dp
    val pullDistanceThresholdPx = with(density) { pullDistanceThreshold.toPx() }
    val maxPullPx = with(density) { 150.dp.toPx() }
    val restingHeightPx = with(density) { 72.dp.toPx() }

    val pullOffsetPx = remember { Animatable(0f) }
    var hasFiredHaptic by remember { mutableStateOf(false) }

    // When isRefreshing changes to false, smoothly spring back to 0
    LaunchedEffect(isRefreshing) {
        if (!isRefreshing && pullOffsetPx.value > 0f) {
            pullOffsetPx.animateTo(
                targetValue = 0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
            hasFiredHaptic = false
        } else if (isRefreshing) {
            // Smoothly collapse pull offset so content is centered and animation shines in the middle of screen
            pullOffsetPx.animateTo(
                targetValue = 0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        }
    }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y < 0 && pullOffsetPx.value > 0f && !isRefreshing) {
                    val consumedY = available.y.coerceAtLeast(-pullOffsetPx.value)
                    coroutineScope.launch {
                        pullOffsetPx.snapTo(pullOffsetPx.value + consumedY)
                    }
                    return Offset(0f, consumedY)
                }
                return Offset.Zero
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                if (available.y > 0 && !isRefreshing) {
                    val current = pullOffsetPx.value
                    val damping = 0.55f / (1f + (current / maxPullPx))
                    val newOffset = (current + available.y * damping).coerceIn(0f, maxPullPx)

                    if (newOffset >= pullDistanceThresholdPx && !hasFiredHaptic) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        hasFiredHaptic = true
                    } else if (newOffset < pullDistanceThresholdPx && hasFiredHaptic) {
                        hasFiredHaptic = false
                    }

                    coroutineScope.launch {
                        pullOffsetPx.snapTo(newOffset)
                    }
                    return Offset(0f, available.y)
                }
                return Offset.Zero
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                if (!isRefreshing) {
                    if (pullOffsetPx.value >= pullDistanceThresholdPx) {
                        onRefresh()
                        pullOffsetPx.animateTo(
                            targetValue = 0f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        )
                    } else {
                        pullOffsetPx.animateTo(
                            targetValue = 0f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        )
                    }
                }
                return Velocity.Zero
            }
        }
    }

    val pullProgress = (pullOffsetPx.value / pullDistanceThresholdPx).coerceIn(0f, 1.5f)

    Box(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)
    ) {
        // Content with vertical offset translation during pull
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationY = pullOffsetPx.value
                    alpha = if (isRefreshing) 0.45f else 1f
                }
        ) {
            content()
        }

        // Pulling indicator at top while dragging downward
        if (pullOffsetPx.value > 6f && !isRefreshing) {
            val indicatorAlpha = (pullOffsetPx.value / 30f).coerceIn(0f, 1f)
            val indicatorScale = (0.5f + (pullProgress * 0.5f)).coerceIn(0.5f, 1.05f)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(with(density) { pullOffsetPx.value.toDp() })
                    .padding(top = 10.dp)
                    .graphicsLayer {
                        alpha = indicatorAlpha
                        scaleX = indicatorScale
                        scaleY = indicatorScale
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Floating Wavy Arc Stroke preview matching the video
                    WavyCircularStrokeLoader(
                        isAnimating = false,
                        manualProgress = pullProgress,
                        sizeDp = 44.dp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (pullProgress >= 1f) "Release to synchronize records" else "Pull down to refresh vault",
                        style = MaterialTheme.typography.labelSmall,
                        color = Umbra.copy(alpha = 0.75f),
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.SansSerif,
                        letterSpacing = 0.3.sp
                    )
                }
            }
        }

        // Refreshing State: Animation displayed in the MIDDLE OF THE SCREEN with TRANSPARENT BACKGROUND
        if (isRefreshing) {
            MiddleScreenTransparentLoader(
                statusMessage = "Synchronizing Vault",
                subMessage = "Cryptographic SHA-256 seal verified",
                sizeDp = 76.dp,
                isAnimating = true
            )
        }
    }
}
