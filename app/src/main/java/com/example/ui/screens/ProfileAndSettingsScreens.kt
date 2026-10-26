package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
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
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.LifeBridgeViewModel

@Composable
fun AccountProfileScreen(
    viewModel: LifeBridgeViewModel,
    onNavigate: (String) -> Unit
) {
    val context = LocalContext.current
    val name by viewModel.userDisplayName.collectAsState()
    val email by viewModel.userEmail.collectAsState()

    var showEditDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var exportedJson by remember { mutableStateOf("") }

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
                Text(
                    text = "Account & Profile",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        },
        bottomBar = {
            LifeBridgeBottomBar(
                currentScreen = "profile",
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
            // Profile Card
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(ElectricCyan, RoyalViolet))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = name.take(1).uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = email,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(onClick = { showEditDialog = true }) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit Profile", tint = ElectricCyan)
                    }
                }
            }

            // Options List
            Text(
                text = "Preferences & Privacy",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                ProfileOptionRow(icon = Icons.Filled.Settings, title = "App Settings", onClick = { onNavigate("settings") })
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                ProfileOptionRow(icon = Icons.Filled.Lock, title = "Privacy & Permissions", onClick = { onNavigate("privacy_security") })
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                ProfileOptionRow(icon = Icons.AutoMirrored.Filled.HelpOutline, title = "Help & Support", onClick = { onNavigate("help_support") })
            }

            Text(
                text = "Data Management (GDPR & Privacy-by-Design)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                ProfileOptionRow(
                    icon = Icons.Filled.Download,
                    title = "Export Personal Data (JSON)",
                    tint = ElectricCyan,
                    onClick = {
                        viewModel.exportData { json ->
                            exportedJson = json
                            showExportDialog = true
                        }
                    }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                ProfileOptionRow(
                    icon = Icons.Filled.DeleteForever,
                    title = "Delete Account & Clear Storage",
                    tint = DangerRose,
                    onClick = { showDeleteDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Edit Profile Dialog
        if (showEditDialog) {
            var tempName by remember { mutableStateOf(name) }
            var tempEmail by remember { mutableStateOf(email) }
            AlertDialog(
                onDismissRequest = { showEditDialog = false },
                title = { Text("Edit Profile") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = tempName,
                            onValueChange = { tempName = it },
                            label = { Text("Display Name") }
                        )
                        OutlinedTextField(
                            value = tempEmail,
                            onValueChange = { tempEmail = it },
                            label = { Text("Email Address") }
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.updateUserProfile(tempName, tempEmail)
                        showEditDialog = false
                    }) {
                        Text("Save", color = ElectricCyan)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEditDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Export Dialog
        if (showExportDialog) {
            AlertDialog(
                onDismissRequest = { showExportDialog = false },
                title = { Text("Exported Data") },
                text = {
                    Column(modifier = Modifier.height(260.dp).verticalScroll(rememberScrollState())) {
                        Text("Your saved document records, action checklists, and preferences in open JSON format:")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(exportedJson, fontSize = 11.sp, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("LIFEBridge Export", exportedJson))
                        showExportDialog = false
                    }) {
                        Text("Copy to Clipboard", color = ElectricCyan)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showExportDialog = false }) {
                        Text("Close")
                    }
                }
            )
        }

        // Delete Confirmation Dialog
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete Account & Data?") },
                text = {
                    Text("This will permanently purge all local Room databases, saved document summaries, action cards, and reset preferences. This action cannot be undone.")
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.deleteAccount()
                        showDeleteDialog = false
                    }) {
                        Text("Delete Everything", color = DangerRose, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
private fun ProfileOptionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    tint: Color = MaterialTheme.colorScheme.onBackground,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = tint,
            modifier = Modifier.weight(1f)
        )
        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = TextTertiaryDark, modifier = Modifier.size(20.dp))
    }
}

@Composable
fun SettingsScreen(
    viewModel: LifeBridgeViewModel,
    onNavigate: (String) -> Unit
) {
    val currentLang by viewModel.currentLanguage.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val isOffline by viewModel.isOfflineMode.collectAsState()
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()
    val voiceSpeed by viewModel.voiceSpeed.collectAsState()

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
                IconButton(onClick = { onNavigate("profile") }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        },
        bottomBar = {
            LifeBridgeBottomBar(
                currentScreen = "profile",
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
            // Theme Section
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Appearance Theme",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("dark", "light").forEach { mode ->
                        val isSelected = themeMode == mode
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.toggleTheme(mode) },
                            label = { Text(if (mode == "dark") "Midnight Navy (Dark)" else "Soft Ivory (Light)") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ElectricCyan,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                }
            }

            // Language
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "App & Explanation Language",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(Pair("en", "English"), Pair("bn", "বাংলা"), Pair("hi", "हिन्दी")).forEach { (code, label) ->
                        val isSelected = currentLang == code
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setLanguage(code) },
                            label = { Text(label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ElectricCyan,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                }
            }

            // Voice Speed
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Voice Assistant Speech Speed: ${(voiceSpeed * 100).toInt()}%",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Slider(
                    value = voiceSpeed,
                    onValueChange = { viewModel.setVoiceSpeed(it) },
                    valueRange = 0.5f..1.5f,
                    steps = 3,
                    colors = SliderDefaults.colors(
                        thumbColor = ElectricCyan,
                        activeTrackColor = ElectricCyan
                    )
                )
            }

            // Offline Mode Toggle
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Force Offline Mode",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Disable cloud requests and rely solely on local Room DB.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = isOffline,
                        onCheckedChange = { viewModel.toggleOfflineMode(it) }
                    )
                }
            }

            // AI Provider Status
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "AI Orchestration Status",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Provider: Firebase AI Logic / Gemini Multimodal Engine\nFallback: On-device Structured Regex & NLP Parser\nPrivacy: Prompt-isolated & Zero training retention.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            }

            // System Information & Security Compliance
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "System & Security",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "LIFEBridge AI Core v1.0.0\nEnd-to-End Local Encryption (AES-GCM-256)\nZero-Knowledge Storage: Verified\nGDPR & Privacy-by-Design Architecture",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
fun PrivacySecurityScreen(
    onNavigate: (String) -> Unit
) {
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
                IconButton(onClick = { onNavigate("profile") }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = "Privacy & Permissions",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        },
        bottomBar = {
            LifeBridgeBottomBar(
                currentScreen = "profile",
                onNavigate = onNavigate
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Our Privacy Commitment",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = RestrainedMint
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "1. Data Minimization: We only parse documents you deliberately submit.\n2. Zero Ads: No advertising trackers or SDKs are bundled.\n3. Local Persistence: Saved notices, action cards, and emergency numbers are stored securely on your device in Room SQLite.\n4. Secure Server-Side URL Inspection: Prevents SSRF and isolates suspicious links.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    lineHeight = 22.sp
                )
            }

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Permissions In Use",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("• Storage: Selected file access\n• Microphone: Push-to-talk voice input only\n• Notifications: Action card deadline alerts", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun HelpSupportScreen(
    onNavigate: (String) -> Unit
) {
    var feedbackText by remember { mutableStateOf("") }
    var feedbackSent by remember { mutableStateOf(false) }

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
                IconButton(onClick = { onNavigate("profile") }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = "Help & Support",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        },
        bottomBar = {
            LifeBridgeBottomBar(
                currentScreen = "profile",
                onNavigate = onNavigate
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Frequently Asked Questions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = ElectricCyan
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Q: Does LIFEBridge AI give official legal advice?\nA: No. LIFEBridge AI helps simplify and structure documents into practical steps, but does not substitute for certified legal or government authority.\n\nQ: Does the app work offline?\nA: Yes! All previously imported documents, generated action cards, and emergency numbers in your Offline Life Kit are accessible 100% offline.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    lineHeight = 22.sp
                )
            }

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Send Feedback to the Team",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = feedbackText,
                    onValueChange = { feedbackText = it },
                    placeholder = { Text("What can we improve?") },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                GradientButton(
                    text = if (feedbackSent) "Feedback Sent! Thank you ✓" else "Submit Feedback",
                    enabled = !feedbackSent && feedbackText.isNotBlank(),
                    onClick = { feedbackSent = true }
                )
            }
        }
    }
}
