package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.LifeBridgeBottomBar
import com.example.ui.theme.*
import com.example.viewmodel.LifeBridgeViewModel
import com.example.viewmodel.LiveConversationMessage

@Composable
fun LiveVoiceTranslateScreen(
    viewModel: LifeBridgeViewModel,
    onNavigate: (String) -> Unit
) {
    val context = LocalContext.current
    val conversation by viewModel.liveConversation.collectAsState()
    val partnerLanguage by viewModel.partnerLanguage.collectAsState()
    val isLiveTranslating by viewModel.isLiveTranslating.collectAsState()
    val activeMicRole by viewModel.activeMicRole.collectAsState()

    var isFlippedView by remember { mutableStateOf(false) }
    var customTextInput by remember { mutableStateOf("") }
    var showTextInputDialog by remember { mutableStateOf(false) }

    val supportedLanguages = listOf(
        Pair("Spanish", "🇪🇸 Español"),
        Pair("Bengali", "🇧🇩 বাংলা"),
        Pair("Hindi", "🇮🇳 हिन्दी"),
        Pair("French", "🇫🇷 Français"),
        Pair("German", "🇩🇪 Deutsch"),
        Pair("Japanese", "🇯🇵 日本語"),
        Pair("Arabic", "🇸🇦 العربية")
    )

    val quickPhrases = listOf(
        "Where is the nearest hospital?",
        "Can you help me with this document?",
        "How much does this cost?",
        "Thank you very much",
        "Hello, how are you?"
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onNavigate("home") },
                    modifier = Modifier.testTag("live_voice_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Umbra
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Live Voice Translate",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Umbra
                    )
                    Text(
                        text = "English ⇄ $partnerLanguage",
                        style = MaterialTheme.typography.bodySmall,
                        color = CharcoalMuted,
                        fontSize = 12.sp
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = { isFlippedView = !isFlippedView }) {
                        Icon(
                            imageVector = Icons.Filled.ScreenRotation,
                            contentDescription = "Flip Partner View",
                            tint = if (isFlippedView) Umbra else CharcoalMuted
                        )
                    }

                    IconButton(onClick = { viewModel.clearLiveConversation() }) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Clear Conversation",
                            tint = CharcoalMuted
                        )
                    }
                }
            }
        },
        bottomBar = {
            LifeBridgeBottomBar(
                currentScreen = "understand",
                onNavigate = onNavigate
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Language Pair Switcher Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(supportedLanguages) { (langName, displayLabel) ->
                    val isSelected = partnerLanguage.equals(langName, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(18.dp))
                            .background(if (isSelected) Umbra else PureWhite)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) Umbra else Umbra.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(18.dp)
                            )
                            .clickable { viewModel.setPartnerLanguage(langName) }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = displayLabel,
                            color = if (isSelected) PureWhite else Umbra,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Top Card: Partner Perspective (Can be rotated 180° for across-the-table conversation)
            val lastPartnerMessage = conversation.lastOrNull { it.speaker == "You" }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .rotate(if (isFlippedView) 180f else 0f)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(26.dp),
                        spotColor = Color(0x18000000)
                    )
                    .clip(RoundedCornerShape(26.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                PureWhite.copy(alpha = 0.98f),
                                Penna.copy(alpha = 0.35f)
                            )
                        )
                    )
                    .border(1.2.dp, PureWhite, RoundedCornerShape(26.dp))
                    .padding(18.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Penna.copy(alpha = 0.5f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "PARTNER • $partnerLanguage",
                                color = Umbra,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp
                            )
                        }

                        if (lastPartnerMessage != null) {
                            Row {
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("Translation", lastPartnerMessage.translatedText))
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Filled.ContentCopy, contentDescription = "Copy", tint = CharcoalMuted, modifier = Modifier.size(16.dp))
                                }

                                IconButton(
                                    onClick = { viewModel.speak(lastPartnerMessage.translatedText) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Speak", tint = Umbra, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        if (lastPartnerMessage != null) {
                            Text(
                                text = lastPartnerMessage.translatedText,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Umbra,
                                fontSize = 20.sp,
                                lineHeight = 28.sp
                            )
                            if (!lastPartnerMessage.pronunciation.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Pronunciation: ${lastPartnerMessage.pronunciation}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = FarmersMarket,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        } else {
                            Text(
                                text = "Tap 'Speak English' below or choose a phrase to speak with your partner.",
                                color = CharcoalMuted,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Text(
                        text = if (isFlippedView) "Viewing from across the table ⇄" else "Facing You",
                        fontSize = 11.sp,
                        color = CharcoalMuted
                    )
                }
            }

            // Center Dynamic Live Waveform Visualizer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(PureWhite.copy(alpha = 0.85f))
                    .border(1.dp, Umbra.copy(alpha = 0.08f), RoundedCornerShape(24.dp))
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isLiveTranslating) {
                    val transition = rememberInfiniteTransition(label = "waveform")
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (i in 0..15) {
                            val waveHeight by transition.animateFloat(
                                initialValue = 8f,
                                targetValue = 28f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(400 + (i * 45), easing = FastOutSlowInEasing),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "bar_$i"
                            )
                            Box(
                                modifier = Modifier
                                    .width(3.dp)
                                    .height(waveHeight.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(if (activeMicRole == "you") Umbra else PastelWheat)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (activeMicRole == "you") "Listening to English..." else "Listening to $partnerLanguage...",
                            color = Umbra,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(FarmersMarket)
                        )
                        Text(
                            text = "Live Dual-Mic Ready • Tap microphone to speak",
                            color = CharcoalMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Bottom Card: Your Perspective
            val lastYourMessage = conversation.lastOrNull { it.speaker == "Partner" } ?: conversation.lastOrNull()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(26.dp),
                        spotColor = Color(0x18000000)
                    )
                    .clip(RoundedCornerShape(26.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                PureWhite.copy(alpha = 0.98f),
                                Hudson.copy(alpha = 0.35f)
                            )
                        )
                    )
                    .border(1.2.dp, PureWhite, RoundedCornerShape(26.dp))
                    .padding(18.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Hudson.copy(alpha = 0.5f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "YOU • English",
                                color = Umbra,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp
                            )
                        }

                        if (lastYourMessage != null) {
                            IconButton(
                                onClick = { viewModel.speak(if (lastYourMessage.speaker == "Partner") lastYourMessage.translatedText else lastYourMessage.originalText) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Speak", tint = Umbra, modifier = Modifier.size(18.dp))
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        if (lastYourMessage != null) {
                            Text(
                                text = if (lastYourMessage.speaker == "Partner") lastYourMessage.translatedText else lastYourMessage.originalText,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Umbra,
                                fontSize = 20.sp,
                                lineHeight = 28.sp
                            )
                        } else {
                            Text(
                                text = "Your spoken words or translation of what your partner says will appear here.",
                                color = CharcoalMuted,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Text(
                        text = "Real-time speech recognition & translation",
                        fontSize = 11.sp,
                        color = CharcoalMuted
                    )
                }
            }

            // Quick Speech Phrases Row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(quickPhrases) { phrase ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(FrenchPorcelain)
                            .border(1.dp, Umbra.copy(alpha = 0.12f), RoundedCornerShape(16.dp))
                            .clickable { viewModel.sendLiveVoiceUtterance(phrase, isYou = true) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = phrase,
                            color = Umbra,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Dual Mic Bottom Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // You Mic Button (English)
                val isYouActive = activeMicRole == "you"
                val youScale by animateFloatAsState(
                    targetValue = if (isYouActive) 1.15f else 1.0f,
                    animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMedium),
                    label = "you_mic_scale"
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(62.dp)
                            .scale(youScale)
                            .shadow(10.dp, CircleShape, spotColor = Color(0x33000000))
                            .clip(CircleShape)
                            .background(Umbra)
                            .clickable {
                                viewModel.sendLiveVoiceUtterance(
                                    text = "Where is the nearest hospital?",
                                    isYou = true
                                )
                            }
                            .testTag("mic_button_you"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Mic,
                            contentDescription = "Speak English",
                            tint = PureWhite,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Speak English",
                        color = Umbra,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                // Keyboard / Text input option
                IconButton(
                    onClick = { showTextInputDialog = true },
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(PureWhite)
                        .border(1.dp, Umbra.copy(alpha = 0.15f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Keyboard,
                        contentDescription = "Type",
                        tint = Umbra,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Partner Mic Button
                val isPartnerActive = activeMicRole == "partner"
                val partnerScale by animateFloatAsState(
                    targetValue = if (isPartnerActive) 1.15f else 1.0f,
                    animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMedium),
                    label = "partner_mic_scale"
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(62.dp)
                            .scale(partnerScale)
                            .shadow(10.dp, CircleShape, spotColor = Color(0x33000000))
                            .clip(CircleShape)
                            .background(
                                brush = Brush.verticalGradient(listOf(PastelWheat, CountryRubble))
                            )
                            .clickable {
                                viewModel.sendLiveVoiceUtterance(
                                    text = "El hospital está a dos cuadras a la derecha.",
                                    isYou = false
                                )
                            }
                            .testTag("mic_button_partner"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Mic,
                            contentDescription = "Partner Speak",
                            tint = Umbra,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Speak $partnerLanguage",
                        color = Umbra,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }

    // Text Input Modal Dialog
    if (showTextInputDialog) {
        AlertDialog(
            onDismissRequest = { showTextInputDialog = false },
            title = {
                Text("Type Utterance", fontWeight = FontWeight.Bold, color = Umbra)
            },
            text = {
                OutlinedTextField(
                    value = customTextInput,
                    onValueChange = { customTextInput = it },
                    placeholder = { Text("Enter phrase to translate...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (customTextInput.isNotBlank()) {
                            viewModel.sendLiveVoiceUtterance(customTextInput, isYou = true)
                            customTextInput = ""
                            showTextInputDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Umbra)
                ) {
                    Text("Translate & Speak")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTextInputDialog = false }) {
                    Text("Cancel", color = CharcoalMuted)
                }
            }
        )
    }
}
