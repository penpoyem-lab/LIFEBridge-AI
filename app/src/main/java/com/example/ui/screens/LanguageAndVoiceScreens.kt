package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SupportedLanguages
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.LifeBridgeViewModel

@Composable
fun TranslationScreen(
    viewModel: LifeBridgeViewModel,
    onNavigate: (String) -> Unit
) {
    val context = LocalContext.current
    val input by viewModel.translationInput.collectAsState()
    val output by viewModel.translationOutput.collectAsState()
    val targetLang by viewModel.targetLanguage.collectAsState()

    val quickPhrases = listOf(
        "Hello, how are you?",
        "Turn Complex Information Into Clear Steps",
        "Make complicated things simple.",
        "Pay before the due date"
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onNavigate("home") }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = "Translate Text",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        },
        bottomBar = {
            LifeBridgeBottomBar(
                currentScreen = "understand",
                onNavigate = onNavigate
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Source Input Card
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Source Text (Any Language)",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = input,
                    onValueChange = { viewModel.setTranslationInput(it) },
                    placeholder = { Text("Enter text to translate...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .testTag("translation_input_field"),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Quick Samples
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(quickPhrases) { phrase ->
                    SuggestionChip(
                        onClick = {
                            viewModel.setTranslationInput(phrase)
                            viewModel.executeTranslation()
                        },
                        label = { Text(phrase, maxLines = 1, fontSize = 12.sp) }
                    )
                }
            }

            // Target Language Selector
            Text(
                text = "Target Language",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(SupportedLanguages) { lang ->
                    val isSelected = targetLang == lang.displayName || targetLang == lang.code
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            viewModel.setTargetLanguage(lang.displayName)
                            viewModel.executeTranslation()
                        },
                        label = { Text("${lang.displayName} (${lang.nativeName})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElectricCyan,
                            selectedLabelColor = Color.Black
                        )
                    )
                }
            }

            // Translate Action Button
            GradientButton(
                text = "Translate Now",
                onClick = { viewModel.executeTranslation() },
                icon = Icons.Filled.Translate,
                modifier = Modifier.testTag("execute_translation_button")
            )

            // Translation Output Card
            if (output.isNotBlank()) {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = ElectricCyan
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Translated ($targetLang)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = ElectricCyan
                        )

                        Row {
                            IconButton(onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Translated Text", output)
                                clipboard.setPrimaryClip(clip)
                            }) {
                                Icon(Icons.Filled.ContentCopy, contentDescription = "Copy", tint = TextSecondaryDark)
                            }

                            IconButton(onClick = { viewModel.speak(output) }) {
                                Icon(Icons.Filled.VolumeUp, contentDescription = "Speak", tint = ElectricCyan)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = output,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}

@Composable
fun VoiceAssistantScreen(
    viewModel: LifeBridgeViewModel,
    onNavigate: (String) -> Unit
) {
    val isListening by viewModel.isVoiceListening.collectAsState()
    val transcript by viewModel.voiceTranscript.collectAsState()
    val response by viewModel.voiceResponse.collectAsState()
    val currentLang by viewModel.currentLanguage.collectAsState()

    val promptChips = listOf(
        "Explain my electricity bill deadline",
        "What documents are needed for student scholarship?",
        "Check if lottery SMS is safe"
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onNavigate("home") }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }

                Text(
                    text = "Voice Assistant",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                IconButton(onClick = { onNavigate("settings") }) {
                    Icon(Icons.Filled.Settings, contentDescription = "Voice Settings")
                }
            }
        },
        bottomBar = {
            LifeBridgeBottomBar(
                currentScreen = "understand",
                onNavigate = onNavigate
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Language selector pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Language, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Voice Language: ${if (currentLang == "bn") "Bengali" else if (currentLang == "hi") "Hindi" else "English"}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Audio Waveform visualizer
            WaveformVisualizer(
                isAnimating = isListening,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            // Large Push-to-Talk Button
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(
                        if (isListening) Brush.radialGradient(listOf(DangerRose, RoyalViolet))
                        else Brush.radialGradient(listOf(ElectricCyan, ElectricBlue))
                    )
                    .clickable { viewModel.toggleVoiceListening() }
                    .testTag("push_to_talk_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isListening) Icons.Filled.GraphicEq else Icons.Filled.Mic,
                    contentDescription = "Speak",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }

            Text(
                text = if (isListening) "Listening... Speak now" else "Tap to speak in English, বাংলা, or हिन्दी",
                style = MaterialTheme.typography.bodyMedium,
                color = if (isListening) ElectricCyan else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )

            // Suggested Voice Prompts
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Try asking:",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                promptChips.forEach { prompt ->
                    SuggestionChip(
                        onClick = {
                            viewModel.speak(
                                when {
                                    prompt.contains("electricity") -> "Your electricity bill of ₹1,840 is due on May 15, 2026. A surcharge of ₹150 applies if delayed."
                                    prompt.contains("scholarship") -> "For the student scholarship, you need your income certificate, academic mark sheet, and bank passbook by June 30."
                                    else -> "Lottery prize messages asking for bank details or upfront fees are typical advance-fee scams. Do not click links."
                                }
                            )
                        },
                        label = { Text(prompt, fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                    )
                }
            }

            // User Transcript Card
            if (transcript.isNotBlank()) {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Person, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "You said:",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondaryDark
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = transcript,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            // Assistant Response Card
            if (response.isNotBlank()) {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = RoyalViolet
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = RoyalViolet, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "LIFEBridge Assistant:",
                                style = MaterialTheme.typography.labelMedium,
                                color = RoyalViolet,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        IconButton(onClick = { viewModel.speak(response) }) {
                            Icon(Icons.Filled.VolumeUp, contentDescription = "Play Response", tint = ElectricCyan)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = response,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}
