package com.example.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.LifeBridgeViewModel

/**
 * Editorial Liquid Glass App Menu Drawer.
 * Triggered by the 4-box glyph button in the top-left corner of the app.
 * Features Apple-inspired liquid glass frosted backdrop, specular edge reflections,
 * refraction glow gradients, executive user profile card, full navigation suites,
 * and quick executive system actions.
 */
@Composable
fun LiquidGlassAppMenu(
    viewModel: LifeBridgeViewModel,
    onNavigate: (String) -> Unit
) {
    val isOpen by viewModel.isAppMenuOpen.collectAsState()
    val isDark = MaterialTheme.colorScheme.background == UmbraDarkBg
    val isOffline by viewModel.isOfflineMode.collectAsState()
    val isVaultUnlocked by viewModel.isVaultUnlocked.collectAsState()
    val userName by viewModel.userDisplayName.collectAsState()
    val currentScreen by viewModel.currentScreen.collectAsState()

    // Handle back button when drawer is open
    if (isOpen) {
        BackHandler {
            viewModel.toggleAppMenu(false)
        }
    }

    AnimatedVisibility(
        visible = isOpen,
        enter = fadeIn(animationSpec = tween(240)),
        exit = fadeOut(animationSpec = tween(200))
    ) {
        // Scrim backdrop covering the entire screen
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.52f))
                .clickable { viewModel.toggleAppMenu(false) }
                .testTag("app_menu_scrim")
        ) {
            // Liquid Glass Slide-In Left Drawer
            AnimatedVisibility(
                visible = isOpen,
                enter = slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = spring(
                        dampingRatio = 0.82f,
                        stiffness = Spring.StiffnessMediumLow
                    )
                ),
                exit = slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(220, easing = FastOutSlowInEasing)
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.86f)
                        .widthIn(max = 360.dp)
                        .shadow(
                            elevation = 32.dp,
                            shape = RoundedCornerShape(topEnd = 32.dp, bottomEnd = 32.dp),
                            spotColor = if (isDark) Color(0x90000000) else Color(0x35000000)
                        )
                        .clip(RoundedCornerShape(topEnd = 32.dp, bottomEnd = 32.dp))
                        // Multi-stop liquid glass frosted gradient
                        .background(
                            brush = if (isDark) {
                                Brush.linearGradient(
                                    listOf(
                                        Color(0xF5181A22),
                                        Color(0xEE222633),
                                        Color(0xF513141C),
                                        Color(0xEA1C202B)
                                    )
                                )
                            } else {
                                Brush.linearGradient(
                                    listOf(
                                        Color(0xF8FFFFFF),
                                        Color(0xF0F7F9FD),
                                        Color(0xF5FCF6EE),
                                        Color(0xEEF1F5FD)
                                    )
                                )
                            }
                        )
                        // Specular hairline reflection border
                        .border(
                            width = 1.4.dp,
                            brush = Brush.verticalGradient(
                                listOf(
                                    Color.White.copy(alpha = if (isDark) 0.85f else 0.98f),
                                    Color(0xFF8CE8FF).copy(alpha = if (isDark) 0.40f else 0.50f),
                                    Color(0xFFFFB28E).copy(alpha = 0.25f),
                                    Color.White.copy(alpha = 0.15f)
                                )
                            ),
                            shape = RoundedCornerShape(topEnd = 32.dp, bottomEnd = 32.dp)
                        )
                        // Prevent click-through closing
                        .clickable(enabled = false) {}
                ) {
                    // Refraction ambient glow orbs behind content
                    Box(
                        modifier = Modifier
                            .size(180.dp)
                            .align(Alignment.TopEnd)
                            .offset(x = 60.dp, y = (-30).dp)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        (if (isDark) Color(0xFF6366F1) else Color(0xFFFFD1B3)).copy(alpha = 0.25f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .align(Alignment.CenterStart)
                            .offset(x = (-60).dp, y = 80.dp)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        (if (isDark) Color(0xFF0EA5E9) else Color(0xFFBCE3F7)).copy(alpha = 0.20f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    // Top Edge Specular Sheen Ribbon
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
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

                    // Drawer Scrollable Content
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                            .navigationBarsPadding()
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                            .testTag("liquid_glass_app_menu_content"),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // --- Header: 4-Box Brand Icon, Title & Close Button ---
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp, bottom = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    // Liquid Glass 4-Box Brand Emblem Badge
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                Brush.linearGradient(
                                                    listOf(
                                                        Color(0xFF8CE8FF).copy(alpha = 0.65f),
                                                        Color(0xFFFFB28E).copy(alpha = 0.65f)
                                                    )
                                                )
                                            )
                                            .border(
                                                1.dp,
                                                Color.White.copy(alpha = 0.90f),
                                                RoundedCornerShape(12.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        // 4-box geometric layout matching the launcher icon
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                                Box(modifier = Modifier.size(7.dp).clip(RoundedCornerShape(2.dp)).background(Umbra))
                                                Box(modifier = Modifier.size(7.dp).clip(RoundedCornerShape(2.dp)).background(Umbra))
                                            }
                                            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                                Box(modifier = Modifier.size(7.dp).clip(RoundedCornerShape(2.dp)).background(Umbra))
                                                Box(modifier = Modifier.size(7.dp).clip(RoundedCornerShape(2.dp)).background(Umbra))
                                            }
                                        }
                                    }

                                    Column {
                                        Text(
                                            text = "LIFEBridge",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.ExtraBold,
                                                letterSpacing = (-0.3).sp
                                            ),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Universal Executive Suite",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Medium
                                            ),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                // Frosted Close Button
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isDark) Color(0x33FFFFFF) else Color(0x40FFFFFF)
                                        )
                                        .border(
                                            1.dp,
                                            Color.White.copy(alpha = if (isDark) 0.35f else 0.70f),
                                            CircleShape
                                        )
                                        .clickable { viewModel.toggleAppMenu(false) }
                                        .testTag("app_menu_close_button"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = "Close Menu",
                                        tint = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        // --- User Profile & Executive Status Card ---
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(
                                        if (isDark) Color(0x4D2A2E3D) else Color(0x80FFFFFF)
                                    )
                                    .border(
                                        1.dp,
                                        Color.White.copy(alpha = if (isDark) 0.25f else 0.85f),
                                        RoundedCornerShape(20.dp)
                                    )
                                    .clickable {
                                        viewModel.toggleAppMenu(false)
                                        onNavigate("profile")
                                    }
                                    .padding(14.dp)
                                    .testTag("app_menu_profile_card")
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        // User Avatar with liquid glass rim
                                        Box(
                                            modifier = Modifier
                                                .size(46.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    Brush.linearGradient(
                                                        listOf(Color(0xFFEBD368), Color(0xFFE3A894))
                                                    )
                                                )
                                                .border(1.5.dp, Color.White, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = if (userName.isNotEmpty()) userName.take(1).uppercase() else "A",
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                color = Umbra
                                            )
                                        }

                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = userName.ifEmpty { "Alex Kumar" },
                                                    style = MaterialTheme.typography.bodyLarge.copy(
                                                        fontWeight = FontWeight.Bold
                                                    ),
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Icon(
                                                    imageVector = Icons.Filled.CheckCircle,
                                                    contentDescription = "Verified",
                                                    tint = Color(0xFF10B981),
                                                    modifier = Modifier.size(15.dp)
                                                )
                                            }

                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(Color(0xFF6366F1).copy(alpha = 0.15f))
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(
                                                            imageVector = Icons.Filled.WorkspacePremium,
                                                            contentDescription = null,
                                                            tint = Color(0xFF6366F1),
                                                            modifier = Modifier.size(11.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(3.dp))
                                                        Text(
                                                            text = "Executive Tier",
                                                            style = MaterialTheme.typography.labelSmall.copy(
                                                                fontSize = 9.sp,
                                                                fontWeight = FontWeight.SemiBold
                                                            ),
                                                            color = if (isDark) Color(0xFFA5B4FC) else Color(0xFF4F46E5)
                                                        )
                                                    }
                                                }

                                                Text(
                                                    text = if (isOffline) "Offline" else "Online",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                                    color = if (isOffline) Color(0xFFF59E0B) else Color(0xFF10B981)
                                                )
                                            }
                                        }
                                    }

                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                        contentDescription = "Profile",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }

                        // --- Section 1: Core Command Center ---
                        item {
                            MenuSectionHeader("COMMAND CENTER")
                        }

                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                LiquidGlassMenuItem(
                                    title = "Dashboard",
                                    subtitle = "Executive overview & daily status",
                                    icon = Icons.Filled.Home,
                                    iconBgColor = Color(0xFF0EA5E9),
                                    isSelected = currentScreen == "home",
                                    onClick = {
                                        viewModel.toggleAppMenu(false)
                                        onNavigate("home")
                                    },
                                    testTag = "app_menu_item_home"
                                )

                                LiquidGlassMenuItem(
                                    title = "Universal Scanner & OCR",
                                    subtitle = "Document capture & legal seals",
                                    icon = Icons.Filled.DocumentScanner,
                                    iconBgColor = Color(0xFFF59E0B),
                                    isSelected = currentScreen in listOf("universal_input", "scanner"),
                                    onClick = {
                                        viewModel.toggleAppMenu(false)
                                        onNavigate("universal_input")
                                    },
                                    testTag = "app_menu_item_scanner"
                                )

                                LiquidGlassMenuItem(
                                    title = "Compliance Vault",
                                    subtitle = "SHA-256 encrypted records",
                                    icon = Icons.Filled.Folder,
                                    iconBgColor = Color(0xFF8B5CF6),
                                    isSelected = currentScreen == "library",
                                    onClick = {
                                        viewModel.toggleAppMenu(false)
                                        onNavigate("library")
                                    },
                                    testTag = "app_menu_item_vault"
                                )

                                LiquidGlassMenuItem(
                                    title = "Scam & Safety Shield",
                                    subtitle = "Zero-trust message fraud checker",
                                    icon = Icons.Filled.Shield,
                                    iconBgColor = Color(0xFFEF4444),
                                    isSelected = currentScreen in listOf("safety", "safety_checker"),
                                    onClick = {
                                        viewModel.toggleAppMenu(false)
                                        onNavigate("safety")
                                    },
                                    testTag = "app_menu_item_safety"
                                )

                                LiquidGlassMenuItem(
                                    title = "Studio Voice Waveform HUD",
                                    subtitle = "Multi-turn live voice phonetic HUD",
                                    icon = Icons.Filled.GraphicEq,
                                    iconBgColor = Color(0xFFEC4899),
                                    isSelected = currentScreen == "voice_translate",
                                    onClick = {
                                        viewModel.toggleAppMenu(false)
                                        onNavigate("voice_translate")
                                    },
                                    testTag = "app_menu_item_voice_hud"
                                )

                                LiquidGlassMenuItem(
                                    title = "Reminders & Action Cards",
                                    subtitle = "Expiring notices & deadlines",
                                    icon = Icons.Filled.Alarm,
                                    iconBgColor = Color(0xFF10B981),
                                    isSelected = currentScreen == "reminders",
                                    onClick = {
                                        viewModel.toggleAppMenu(false)
                                        onNavigate("reminders")
                                    },
                                    testTag = "app_menu_item_reminders"
                                )
                            }
                        }

                        // --- Section 2: Executive Intelligence ---
                        item {
                            MenuSectionHeader("EXECUTIVE GOVERNANCE")
                        }

                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                LiquidGlassMenuItem(
                                    title = "Security & Biometric Audit",
                                    subtitle = "Hardware locks & tamper log trail",
                                    icon = Icons.Filled.Security,
                                    iconBgColor = Color(0xFF6366F1),
                                    isSelected = currentScreen == "security_audit",
                                    badgeText = if (isVaultUnlocked) "Unlocked" else "Locked",
                                    badgeColor = if (isVaultUnlocked) Color(0xFF10B981) else Color(0xFFEF4444),
                                    onClick = {
                                        viewModel.toggleAppMenu(false)
                                        onNavigate("security_audit")
                                    },
                                    testTag = "app_menu_item_security_audit"
                                )

                                LiquidGlassMenuItem(
                                    title = "Compliance PDF Generator",
                                    subtitle = "Formal ISO legal export headers",
                                    icon = Icons.Filled.Assessment,
                                    iconBgColor = Color(0xFF3B82F6),
                                    isSelected = currentScreen == "compliance_report",
                                    onClick = {
                                        viewModel.toggleAppMenu(false)
                                        onNavigate("compliance_report")
                                    },
                                    testTag = "app_menu_item_compliance"
                                )

                                LiquidGlassMenuItem(
                                    title = "Offline Life Kit",
                                    subtitle = "Emergency zero-connectivity cache",
                                    icon = Icons.Filled.CloudOff,
                                    iconBgColor = Color(0xFF8F917C),
                                    isSelected = currentScreen == "offline_kit",
                                    onClick = {
                                        viewModel.toggleAppMenu(false)
                                        onNavigate("offline_kit")
                                    },
                                    testTag = "app_menu_item_offline_kit"
                                )
                            }
                        }

                        // --- Section 3: System, Search & Support ---
                        item {
                            MenuSectionHeader("SYSTEM & PREFERENCES")
                        }

                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                LiquidGlassMenuItem(
                                    title = "Search History & Records",
                                    subtitle = "Instant regex & semantic search",
                                    icon = Icons.Filled.Search,
                                    iconBgColor = Color(0xFF64748B),
                                    isSelected = currentScreen == "search",
                                    onClick = {
                                        viewModel.toggleAppMenu(false)
                                        onNavigate("search")
                                    },
                                    testTag = "app_menu_item_search"
                                )

                                LiquidGlassMenuItem(
                                    title = "Settings & Preferences",
                                    subtitle = "Language, themes & notifications",
                                    icon = Icons.Filled.Settings,
                                    iconBgColor = Color(0xFF475569),
                                    isSelected = currentScreen == "settings",
                                    onClick = {
                                        viewModel.toggleAppMenu(false)
                                        onNavigate("settings")
                                    },
                                    testTag = "app_menu_item_settings"
                                )

                                LiquidGlassMenuItem(
                                    title = "Help & Emergency Support",
                                    subtitle = "Crisis lines & support guidance",
                                    icon = Icons.AutoMirrored.Filled.HelpOutline,
                                    iconBgColor = Color(0xFF0D9488),
                                    isSelected = currentScreen == "help_support",
                                    onClick = {
                                        viewModel.toggleAppMenu(false)
                                        onNavigate("help_support")
                                    },
                                    testTag = "app_menu_item_help"
                                )
                            }
                        }

                        // --- Quick Executive Controls Box ---
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(
                                        if (isDark) Color(0x33FFFFFF) else Color(0x66FFFFFF)
                                    )
                                    .border(
                                        1.dp,
                                        Color.White.copy(alpha = if (isDark) 0.20f else 0.70f),
                                        RoundedCornerShape(18.dp)
                                    )
                                    .padding(14.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    // Offline Mode Toggle
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (isOffline) Icons.Filled.CloudOff else Icons.Filled.CloudDone,
                                                contentDescription = null,
                                                tint = if (isOffline) Color(0xFFF59E0B) else Color(0xFF10B981),
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Column {
                                                Text(
                                                    text = "Offline Mode",
                                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    text = if (isOffline) "Running local encrypted cache" else "Live sync enabled",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }

                                        Switch(
                                            checked = isOffline,
                                            onCheckedChange = { viewModel.toggleOfflineMode(it) },
                                            colors = SwitchDefaults.colors(
                                                checkedThumbColor = Color.White,
                                                checkedTrackColor = Color(0xFFF59E0B)
                                            ),
                                            modifier = Modifier.testTag("app_menu_offline_switch")
                                        )
                                    }

                                    HorizontalDivider(
                                        color = (if (isDark) Color.White else Color.Black).copy(alpha = 0.08f)
                                    )

                                    // Quick Voice Memo & Vault Lock Actions
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        // Quick Memo Button
                                        Button(
                                            onClick = {
                                                viewModel.toggleAppMenu(false)
                                                viewModel.saveVoiceMemo("Quick Executive Voice Note", "Voice recorded via App Menu")
                                            },
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(40.dp)
                                                .testTag("app_menu_quick_memo_button"),
                                            shape = RoundedCornerShape(12.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (isDark) Color(0xFF2E3242) else Color(0xFFE8EEF8),
                                                contentColor = if (isDark) Color.White else Umbra
                                            ),
                                            contentPadding = PaddingValues(horizontal = 8.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Mic,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp),
                                                tint = Color(0xFF6366F1)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "Voice Memo",
                                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                            )
                                        }

                                        // Lock Vault Button
                                        Button(
                                            onClick = {
                                                viewModel.lockVault()
                                            },
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(40.dp)
                                                .testTag("app_menu_lock_vault_button"),
                                            shape = RoundedCornerShape(12.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (isDark) Color(0xFF382329) else Color(0xFFFDE8E8),
                                                contentColor = Color(0xFFEF4444)
                                            ),
                                            contentPadding = PaddingValues(horizontal = 8.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Lock,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp),
                                                tint = Color(0xFFEF4444)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (isVaultUnlocked) "Lock Vault" else "Locked",
                                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // --- Footer: Cryptographic Signature & Build Info ---
                        item {
                            Spacer(modifier = Modifier.height(4.dp))
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "LIFEBridge Enterprise v2.4.0 (Build 412)",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "End-to-End Encrypted On-Device Vault",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Normal
                                    ),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MenuSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall.copy(
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp
        ),
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f),
        modifier = Modifier.padding(start = 6.dp, top = 4.dp, bottom = 2.dp)
    )
}

@Composable
private fun LiquidGlassMenuItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconBgColor: Color,
    isSelected: Boolean = false,
    badgeText: String? = null,
    badgeColor: Color = Color(0xFF10B981),
    onClick: () -> Unit,
    testTag: String
) {
    val isDark = MaterialTheme.colorScheme.background == UmbraDarkBg

    val cardBg = when {
        isSelected -> if (isDark) Color(0x66333D55) else Color(0x80DCE8FF)
        else -> Color.Transparent
    }

    val borderColor = when {
        isSelected -> Color.White.copy(alpha = if (isDark) 0.45f else 0.85f)
        else -> Color.Transparent
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(cardBg)
            .border(if (isSelected) 1.dp else 0.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 9.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            // Frosted Icon Chip
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .shadow(
                        elevation = if (isSelected) 6.dp else 2.dp,
                        shape = RoundedCornerShape(11.dp),
                        spotColor = iconBgColor.copy(alpha = 0.45f)
                    )
                    .clip(RoundedCornerShape(11.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                iconBgColor.copy(alpha = 0.85f),
                                iconBgColor
                            )
                        )
                    )
                    .border(
                        1.dp,
                        Color.White.copy(alpha = 0.85f),
                        RoundedCornerShape(11.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(19.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    maxLines = 1
                )
            }
        }

        // Optional status pill or arrow
        if (badgeText != null) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(badgeColor.copy(alpha = 0.15f))
                    .border(0.8.dp, badgeColor.copy(alpha = 0.40f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = badgeText,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = badgeColor
                )
            }
        } else {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = if (isSelected) 0.8f else 0.35f),
                modifier = Modifier.size(12.dp)
            )
        }
    }
}
