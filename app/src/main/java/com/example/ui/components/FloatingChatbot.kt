package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.ChatMessage
import com.example.data.model.MessageSender
import com.example.ui.theme.*
import com.example.viewmodel.LifeBridgeViewModel
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun FloatingChatbotOverlay(
    viewModel: LifeBridgeViewModel,
    onNavigate: (String) -> Unit
) {
    val isOpen by viewModel.isChatbotOpen.collectAsState()
    val isStudioVoiceHudActive by viewModel.isStudioVoiceHudActive.collectAsState()
    val offsetX by viewModel.chatbotOffsetX.collectAsState()
    val offsetY by viewModel.chatbotOffsetY.collectAsState()
    var isRadialExpanded by remember { mutableStateOf(false) }

    // Breathing pulse animation for cool AI icon halo
    val infiniteTransition = rememberInfiniteTransition(label = "chatbot_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )
    val haloAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "halo_alpha"
    )

    val isDark = MaterialTheme.colorScheme.background == UmbraDarkBg

    // Floating Draggable Chatbot Orb with Radial Action Wheel
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        viewModel.updateChatbotPosition(dragAmount.x, dragAmount.y)
                    }
                }
                .testTag("chatbot_floating_button"),
            contentAlignment = Alignment.Center
        ) {
            // Expandable Radial Action Buttons
            AnimatedVisibility(
                visible = isRadialExpanded,
                enter = fadeIn(tween(250)) + scaleIn(tween(250)),
                exit = fadeOut(tween(200)) + scaleOut(tween(200))
            ) {
                Box(
                    modifier = Modifier.size(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Radial 1: Voice Memo Recording (-135 degrees / Top-Left)
                    RadialActionWheelItem(
                        icon = Icons.Filled.Mic,
                        label = "Memo",
                        color = Color(0xFF8CE8FF),
                        offsetX = -65.dp,
                        offsetY = -65.dp,
                        onClick = {
                            viewModel.saveVoiceMemo("Executive Voice Note: Urgent administrative action item recorded at ${java.text.SimpleDateFormat("HH:mm", java.util.Locale.US).format(java.util.Date())}")
                            isRadialExpanded = false
                        }
                    )

                    // Radial 2: Studio Voice Waveform HUD (-90 degrees / Top)
                    RadialActionWheelItem(
                        icon = Icons.Filled.GraphicEq,
                        label = "Voice HUD",
                        color = Color(0xFFE8D5B5),
                        offsetX = 0.dp,
                        offsetY = -85.dp,
                        onClick = {
                            viewModel.setStudioVoiceHudActive(true)
                            isRadialExpanded = false
                        }
                    )

                    // Radial 3: Document Camera Scan (-180 degrees / Left)
                    RadialActionWheelItem(
                        icon = Icons.Filled.CameraAlt,
                        label = "Fast Scan",
                        color = Color(0xFFFFB28E),
                        offsetX = -85.dp,
                        offsetY = 0.dp,
                        onClick = {
                            onNavigate("scanner")
                            isRadialExpanded = false
                        }
                    )

                    // Radial 4: Security Audit Center (-45 degrees / Top-Right)
                    RadialActionWheelItem(
                        icon = Icons.Filled.Shield,
                        label = "Audit Vault",
                        color = Color(0xFF00E676),
                        offsetX = 65.dp,
                        offsetY = -65.dp,
                        onClick = {
                            onNavigate("security_audit")
                            isRadialExpanded = false
                        }
                    )
                }
            }

            // Pulsing Glacier Ambient Glow Halo
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            listOf(
                                Color(0xFF8CE8FF).copy(alpha = haloAlpha),
                                Color(0xFFD69EFF).copy(alpha = haloAlpha * 0.7f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // 3D Liquid Crystal Spherical Chatbot Button
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .shadow(
                        elevation = 12.dp,
                        shape = CircleShape,
                        spotColor = Color(0x6600C6FF),
                        ambientColor = Color(0x448A2BE2)
                    )
                    .clip(CircleShape)
                    .background(
                        brush = if (isDark) {
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF2C2D38),
                                    Color(0xFF1B1C24),
                                    Color(0xFF12131A)
                                ),
                                start = Offset(0f, 0f),
                                end = Offset(120f, 120f)
                            )
                        } else {
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF8CE8FF),
                                    Color(0xFFFFB28E),
                                    Color(0xFFD69EFF)
                                ),
                                start = Offset(0f, 0f),
                                end = Offset(120f, 120f)
                            )
                        }
                    )
                    .border(
                        width = 1.6.dp,
                        brush = Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.95f),
                                Color.White.copy(alpha = 0.30f),
                                Color(0xFF8CE8FF).copy(alpha = 0.60f)
                            )
                        ),
                        shape = CircleShape
                    )
                    .clickable { viewModel.toggleChatbot(true) },
                contentAlignment = Alignment.Center
            ) {
                // Top-lit 3D glass spherical highlight
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.48f)
                        .align(Alignment.TopCenter)
                        .clip(RoundedCornerShape(topStart = 29.dp, topEnd = 29.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(
                                    Color.White.copy(alpha = 0.45f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // High-Tech AI Neural Crystal Icon
                Icon(
                    imageVector = Icons.Filled.AutoAwesome,
                    contentDescription = "LIFEBridge Assistant",
                    tint = if (isDark) Color(0xFF8CE8FF) else Color(0xFF1C1D24),
                    modifier = Modifier
                        .size(28.dp)
                        .scale(pulseScale)
                )

                // Radial Wheel Quick-Action Toggle Badge
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = 2.dp, y = (-2).dp)
                        .clip(CircleShape)
                        .background(
                            if (isRadialExpanded) Color(0xFFE8D5B5) else Color(0xFF00E676)
                        )
                        .border(1.5.dp, Color.White, CircleShape)
                        .clickable { isRadialExpanded = !isRadialExpanded },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isRadialExpanded) Icons.Filled.Close else Icons.Filled.Bolt,
                        contentDescription = "Quick Wheel",
                        tint = Color.Black,
                        modifier = Modifier.size(11.dp)
                    )
                }
            }
        }
    }

    // Liquid Glass Chatbot Dialog Sheet
    if (isOpen) {
        LiquidGlassChatbotSheet(
            viewModel = viewModel,
            onDismiss = { viewModel.toggleChatbot(false) },
            onNavigate = { route ->
                viewModel.toggleChatbot(false)
                onNavigate(route)
            }
        )
    }

    // Fullscreen Studio Voice Waveform HUD Dialog
    if (isStudioVoiceHudActive) {
        StudioVoiceWaveformHudDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.setStudioVoiceHudActive(false) }
        )
    }
}

@Composable
fun RadialActionWheelItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color,
    offsetX: androidx.compose.ui.unit.Dp,
    offsetY: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .offset(x = offsetX, y = offsetY)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .shadow(8.dp, CircleShape, spotColor = color)
                .clip(CircleShape)
                .background(Color(0xE61A1C24))
                .border(1.2.dp, color.copy(alpha = 0.8f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            ),
            color = Color.White
        )
    }
}

@Composable
fun LiquidGlassChatbotSheet(
    viewModel: LifeBridgeViewModel,
    onDismiss: () -> Unit,
    onNavigate: (String) -> Unit
) {
    val messages by viewModel.chatMessages.collectAsState()
    val isThinking by viewModel.isChatbotThinking.collectAsState()
    val contextSuggestion by viewModel.currentScreenContextSuggestion.collectAsState()
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val isDark = MaterialTheme.colorScheme.background == UmbraDarkBg

    // Auto-scroll on new messages
    LaunchedEffect(messages.size, isThinking) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.55f))
                .clickable { onDismiss() }
                .padding(horizontal = 12.dp, vertical = 24.dp)
                .testTag("chatbot_dialog"),
            contentAlignment = Alignment.Center
        ) {
            // Liquid Glass Dialog Container
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.88f)
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        if (isDark) Color(0xF21C1C22) else Color(0xF4FFFFFF)
                    )
                    .border(
                        width = 1.5.dp,
                        brush = Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = if (isDark) 0.55f else 0.95f),
                                Color(0xFF8CE8FF).copy(alpha = 0.30f),
                                Color.White.copy(alpha = 0.10f)
                            )
                        ),
                        shape = RoundedCornerShape(28.dp)
                    )
                    .clickable(enabled = false) {} // Prevent click-through to dismiss
                    .padding(top = 16.dp, bottom = 12.dp)
            ) {
                // Header: Glacier Liquid Bar with Title, Controls & Relocate Anchors
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF8CE8FF), Color(0xFFD69EFF))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AutoAwesome,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "LIFEBridge Assistant",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF00E676))
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "Ready to Help in Everything",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Top Action Buttons (Reposition & Close)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { viewModel.clearChatMessages() },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = "Clear Chat",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(19.dp)
                            )
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }

                // Drag & Position Anchor Bar ("tap to select here to another place")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Anchor Bubble:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        PositionChip("Top Left") { viewModel.setChatbotPosition(-250f, -480f) }
                        PositionChip("Top Right") { viewModel.setChatbotPosition(0f, -480f) }
                        PositionChip("Bottom Left") { viewModel.setChatbotPosition(-250f, 0f) }
                        PositionChip("Bottom Right") { viewModel.setChatbotPosition(0f, 0f) }
                    }
                }

                // Proactive Screen Context Intelligence Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isDark) Color(0x338CE8FF) else Color(0x1F8CE8FF))
                        .border(
                            width = 1.dp,
                            brush = Brush.horizontalGradient(
                                listOf(Color(0xFF8CE8FF).copy(alpha = 0.7f), Color(0xFFD69EFF).copy(alpha = 0.5f))
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Bolt,
                                contentDescription = null,
                                tint = Color(0xFF8CE8FF),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Screen Context Intelligence",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF8CE8FF)
                                    )
                                )
                                Text(
                                    text = contextSuggestion.prompt,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 2
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                onDismiss()
                                onNavigate(contextSuggestion.targetRoute)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF8CE8FF),
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text(
                                text = contextSuggestion.actionLabel,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    modifier = Modifier.padding(vertical = 6.dp)
                )

                // Quick Prompt Suggestion Chips
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val promptSuggestions = listOf(
                        "🛡️ Is this SMS a scam?",
                        "📄 How do I scan a hospital bill?",
                        "🗣️ Start live voice translation",
                        "⏰ Remind me about scholarship deadlines",
                        "🆘 Open emergency life kit"
                    )
                    items(promptSuggestions) { prompt ->
                        SuggestionChip(
                            onClick = {
                                viewModel.sendChatMessage(prompt)
                            },
                            label = { Text(prompt, fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = if (isDark) Color(0x33FFFFFF) else Color(0x15000000)
                            ),
                            border = SuggestionChipDefaults.suggestionChipBorder(
                                enabled = true,
                                borderColor = Color(0xFF8CE8FF).copy(alpha = 0.35f)
                            )
                        )
                    }
                }

                // Chat Messages Scrollable Area
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(messages) { message ->
                        ChatBubbleItem(
                            message = message,
                            isDark = isDark,
                            onSpeak = { text -> viewModel.speak(text) },
                            onNavigateAction = { route -> onNavigate(route) }
                        )
                    }

                    if (isThinking) {
                        item {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isDark) Color(0x33FFFFFF) else Color(0x15000000))
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                MorphingOrganicBlob(
                                    modifier = Modifier.background(Color.Transparent),
                                    isAnimating = true,
                                    sizeDp = 22.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Analyzing with Gemini AI intelligence...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Input Bar with Liquid Glass Outline
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chatbot_input_field"),
                        placeholder = {
                            Text(
                                "Ask anything about documents, scams, life...",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        },
                        shape = RoundedCornerShape(24.dp),
                        singleLine = false,
                        maxLines = 3,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                if (inputText.isNotBlank()) {
                                    viewModel.sendChatMessage(inputText)
                                    inputText = ""
                                    focusManager.clearFocus()
                                }
                            }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF8CE8FF),
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                            focusedContainerColor = if (isDark) Color(0x22FFFFFF) else Color(0x0C000000),
                            unfocusedContainerColor = if (isDark) Color(0x18FFFFFF) else Color(0x08000000)
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                viewModel.sendChatMessage(inputText)
                                inputText = ""
                                focusManager.clearFocus()
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF8CE8FF), Color(0xFFD69EFF))
                                )
                            )
                            .testTag("chatbot_send_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PositionChip(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x228CE8FF))
            .clickable(onClick = onClick)
            .padding(horizontal = 7.dp, vertical = 3.dp)
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun ChatBubbleItem(
    message: ChatMessage,
    isDark: Boolean,
    onSpeak: (String) -> Unit,
    onNavigateAction: (String) -> Unit
) {
    val isUser = message.sender == MessageSender.USER

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 320.dp)
                .shadow(
                    elevation = if (isUser) 2.dp else 4.dp,
                    shape = RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 20.dp,
                        bottomStart = if (isUser) 20.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 20.dp
                    )
                )
                .clip(
                    RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 20.dp,
                        bottomStart = if (isUser) 20.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 20.dp
                    )
                )
                .background(
                    if (isUser) {
                        if (isDark) Brush.linearGradient(listOf(Color(0xFF383944), Color(0xFF2B2C36)))
                        else Brush.linearGradient(listOf(Color(0xFF1E2028), Color(0xFF12141A)))
                    } else {
                        if (isDark) Brush.linearGradient(listOf(Color(0x40FFFFFF), Color(0x20FFFFFF)))
                        else Brush.linearGradient(
                            listOf(
                                Color(0xEEFFFFFF),
                                Color(0xFFF0F8FF)
                            )
                        )
                    }
                )
                .border(
                    width = 1.dp,
                    brush = if (isUser) {
                        Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.25f), Color.Transparent))
                    } else {
                        Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.85f), Color(0xFF8CE8FF).copy(alpha = 0.4f)))
                    },
                    shape = RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 20.dp,
                        bottomStart = if (isUser) 20.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 20.dp
                    )
                )
                .padding(14.dp)
        ) {
            Column {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                )

                // Optional Action Route Button
                if (!message.actionRoute.isNullOrBlank() && !message.actionLabel.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { onNavigateAction(message.actionRoute) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF8CE8FF),
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = message.actionLabel,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }

                // Audio Listen Button for assistant responses
                if (!isUser) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { onSpeak(message.text) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Listen",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StudioVoiceWaveformHudDialog(
    viewModel: LifeBridgeViewModel,
    onDismiss: () -> Unit
) {
    var isListening by remember { mutableStateOf(true) }
    var memoLogged by remember { mutableStateOf(false) }

    // Waveform continuous harmonic phase animation
    val infiniteTransition = rememberInfiniteTransition(label = "hud_waveform_anim")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase_anim"
    )
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_anim"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.75f))
                .padding(horizontal = 14.dp, vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            // Obsidian Luxury Glass Container
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.92f)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color(0xF50E1017))
                    .border(
                        width = 1.5.dp,
                        brush = Brush.verticalGradient(
                            listOf(
                                Color(0xFFE8D5B5).copy(alpha = 0.65f),
                                Color(0xFF8CE8FF).copy(alpha = 0.35f),
                                Color(0xFF1E2230)
                            )
                        ),
                        shape = RoundedCornerShape(28.dp)
                    )
                    .padding(20.dp)
            ) {
                // Header: Swiss Editorial Title & Telemetry Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (isListening) Color(0xFF00E676) else Color(0xFFFFB28E))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isListening) "LIVE TELEMETRY STREAM" else "TELEMETRY PAUSED",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    letterSpacing = 1.2.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isListening) Color(0xFF00E676) else Color(0xFFFFB28E)
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Studio Voice Waveform HUD",
                            fontFamily = FontFamily.Serif,
                            fontStyle = FontStyle.Italic,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color(0xFFFDFBF7)
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0x33FFFFFF))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close HUD",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // High-Tech Waveform Canvas Visualizer
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF08090E))
                        .border(1.dp, Color(0xFF8CE8FF).copy(alpha = 0.25f), RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val width = size.width
                        val height = size.height
                        val midY = height / 2f
                        val baseAmp = if (isListening) 26.dp.toPx() else 8.dp.toPx()

                        // Wave 1: Primary Harmonic (Cyan)
                        val path1 = Path()
                        path1.moveTo(0f, midY)
                        val pointsCount = 80
                        for (i in 0..pointsCount) {
                            val x = (width / pointsCount) * i
                            val progress = x / width
                            val envelope = kotlin.math.sin(progress * PI.toFloat()) // zero at edges
                            val y = midY + kotlin.math.sin(progress * 4 * PI.toFloat() + phase) * baseAmp * envelope
                            if (i == 0) path1.moveTo(x, y) else path1.lineTo(x, y)
                        }
                        drawPath(
                            path = path1,
                            color = Color(0xFF8CE8FF),
                            style = Stroke(width = 3.dp.toPx())
                        )

                        // Wave 2: Secondary Harmonic (Champagne Gold)
                        val path2 = Path()
                        for (i in 0..pointsCount) {
                            val x = (width / pointsCount) * i
                            val progress = x / width
                            val envelope = kotlin.math.sin(progress * PI.toFloat())
                            val y = midY + kotlin.math.sin(progress * 6 * PI.toFloat() + phase * 1.4f) * (baseAmp * 0.75f) * envelope
                            if (i == 0) path2.moveTo(x, y) else path2.lineTo(x, y)
                        }
                        drawPath(
                            path = path2,
                            color = Color(0xFFE8D5B5).copy(alpha = 0.85f),
                            style = Stroke(width = 2.dp.toPx())
                        )

                        // Wave 3: Ambient Under-Harmonic (Lavender)
                        val path3 = Path()
                        for (i in 0..pointsCount) {
                            val x = (width / pointsCount) * i
                            val progress = x / width
                            val envelope = kotlin.math.sin(progress * PI.toFloat())
                            val y = midY + kotlin.math.cos(progress * 3 * PI.toFloat() - phase * 0.8f) * (baseAmp * 0.45f) * envelope
                            if (i == 0) path3.moveTo(x, y) else path3.lineTo(x, y)
                        }
                        drawPath(
                            path = path3,
                            color = Color(0xFFD69EFF).copy(alpha = 0.6f),
                            style = Stroke(width = 1.5.dp.toPx())
                        )
                    }

                    // Bottom Telemetry Info Strip
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "48.0 kHz • 24-bit Lossless",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = Color(0xFF8CE8FF).copy(alpha = 0.8f)
                        )
                        Text(
                            text = if (isListening) "-12.4 dBFS PEAK" else "-INF dB",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = Color(0xFFE8D5B5).copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Dual-Speaker Multi-Turn Transcript Stream
                Text(
                    text = "DUAL-SPEAKER MULTI-TURN LOG",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.1.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE8D5B5)
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Turn 1: User (English)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0x228CE8FF))
                            .border(1.dp, Color(0xFF8CE8FF).copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "SPEAKER A • You (English)",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF8CE8FF)
                                    )
                                )
                                Text(
                                    text = "10:14:22 AM",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = Color.White.copy(alpha = 0.6f)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "“Can you verify if this government grant submission requires a verified notary seal before Friday?”",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White
                            )
                        }
                    }

                    // Turn 2: Partner (Spanish)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0x22E8D5B5))
                            .border(1.dp, Color(0xFFE8D5B5).copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "SPEAKER B • Legal Counselor (Spanish)",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFE8D5B5)
                                    )
                                )
                                IconButton(
                                    onClick = {
                                        viewModel.speak("Sí, la solicitud requiere certificación notarial antes del viernes.")
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                        contentDescription = "Speak",
                                        tint = Color(0xFFE8D5B5),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "“Sí, la solicitud requiere certificación notarial antes del viernes.”",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Phonetics: See, lah soh-lee-see-TOOD reh-kyeh-reh sehr-tee-fee-kah-SYON noh-tah-RYAHL...",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    fontStyle = FontStyle.Italic
                                ),
                                color = Color(0xFFE8D5B5).copy(alpha = 0.85f)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "English: Yes, the application requires notarization before Friday.",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = Color.White.copy(alpha = 0.75f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bottom Action Dock
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mic Listening Toggle
                    Button(
                        onClick = { isListening = !isListening },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isListening) Color(0xFFEF4444) else Color(0xFF8CE8FF),
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            imageVector = if (isListening) Icons.Filled.Stop else Icons.Filled.Mic,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isListening) "Pause Mic" else "Resume Mic",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Save as Voice Memo Action
                    Button(
                        onClick = {
                            viewModel.saveVoiceMemo(
                                transcript = "Grant submission notary requirement verified. Due Friday with stamp.",
                                translated = "Verificación de certificación notarial completada."
                            )
                            memoLogged = true
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (memoLogged) Color(0xFF00E676) else Color(0xFFE8D5B5),
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            imageVector = if (memoLogged) Icons.Filled.Check else Icons.Filled.BookmarkBorder,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (memoLogged) "Saved" else "Log Memo",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
