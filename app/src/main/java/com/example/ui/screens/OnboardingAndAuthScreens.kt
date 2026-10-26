package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SupportedLanguages
import com.example.ui.components.GlassCard
import com.example.ui.components.GradientButton
import com.example.ui.theme.*
import com.example.viewmodel.LifeBridgeViewModel

@Composable
fun SplashScreen(
    onTimeout: () -> Unit
) {
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(2000)
        onTimeout()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "splash_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF161720),
                        Color(0xFF0F1017),
                        Color(0xFF08090E)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            // Floating 3D Glacier Aurora Sphere
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(pulseScale),
                contentAlignment = Alignment.Center
            ) {
                // Pulsing ambient halo
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    Color(0xFF8CE8FF).copy(alpha = 0.45f),
                                    Color(0xFFD69EFF).copy(alpha = 0.25f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // 3D Glass Crystal Orb
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .shadow(elevation = 16.dp, shape = CircleShape, spotColor = Color(0xFF8CE8FF))
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF8CE8FF), Color(0xFFFFB28E), Color(0xFFD69EFF))
                            )
                        )
                        .border(
                            width = 2.dp,
                            brush = Brush.verticalGradient(
                                listOf(Color.White, Color.White.copy(alpha = 0.3f))
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Top-lit reflection
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.48f)
                            .align(Alignment.TopCenter)
                            .clip(RoundedCornerShape(topStart = 44.dp, topEnd = 44.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.White.copy(alpha = 0.55f), Color.Transparent)
                                )
                            )
                    )

                    Icon(
                        imageVector = Icons.Filled.AutoAwesome,
                        contentDescription = null,
                        tint = Color(0xFF12131A),
                        modifier = Modifier.size(44.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "LIFEBridge AI",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                ),
                color = Color.White
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Understand More • Worry Less • Take the Next Step",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                ),
                color = Color(0xFFA0A5B8),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(44.dp))

            // Glacier Liquid Progress Capsule
            Box(
                modifier = Modifier
                    .width(140.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0x22FFFFFF))
            ) {
                val progressAnimation by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1500, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "splash_loading_bar"
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth(progressAnimation)
                        .fillMaxHeight()
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF8CE8FF), Color(0xFFFFB28E), Color(0xFFD69EFF))
                            )
                        )
                )
            }
        }
    }
}

@Composable
fun OnboardingScreen(
    viewModel: LifeBridgeViewModel,
    onComplete: () -> Unit
) {
    var step by remember { mutableStateOf(1) }

    data class SlideInfo(
        val title: String,
        val subtitle: String,
        val icon: androidx.compose.ui.graphics.vector.ImageVector,
        val badgeText: String,
        val tagList: List<String>
    )

    val slides = listOf(
        SlideInfo(
            title = "Universal Document Intelligence",
            subtitle = "Upload, scan or paste complex paperwork. We extract payment deadlines, eligibility rules, and create actionable task cards automatically.",
            icon = Icons.Filled.Description,
            badgeText = "Auto-Extract Deadlines & Requirements",
            tagList = listOf("Scholarships", "Hospital Bills", "Contracts", "Notices")
        ),
        SlideInfo(
            title = "Scam & Phishing Defense Shield",
            subtitle = "Verify suspicious SMS, lottery claims, urgent money requests, and unknown links before clicking or sending funds.",
            icon = Icons.Filled.Shield,
            badgeText = "Real-Time Social Engineering Radar",
            tagList = listOf("Fraud Detector", "Link Analysis", "Safety Advice")
        ),
        SlideInfo(
            title = "Live Split-Screen Voice Studio",
            subtitle = "Speak naturally with dual-mic real-time translation. Includes dynamic audio waveform visualizer and natural voice playback in any language.",
            icon = Icons.Filled.RecordVoiceOver,
            badgeText = "Face-to-Face Dual Conversation",
            tagList = listOf("English", "Bengali (বাংলা)", "Hindi (हिन्दी)", "Spanish")
        ),
        SlideInfo(
            title = "Draggable Floating AI Chatbot",
            subtitle = "Tap or drag the floating liquid assistant anywhere on screen. Ask questions, receive deep answers, and trigger actions with zero friction.",
            icon = Icons.Filled.AutoAwesome,
            badgeText = "24/7 Context-Aware Intelligence",
            tagList = listOf("Movable Bubble", "Room Storage", "Real AI Engine")
        )
    )

    val currentSlide = slides[step - 1]
    val isDark = MaterialTheme.colorScheme.background == UmbraDarkBg

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 18.dp)
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                // Liquid Spring Indicator Dots
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(slides.size) { index ->
                        val isCurrent = step == index + 1
                        val dotWidth by androidx.compose.animation.core.animateDpAsState(
                            targetValue = if (isCurrent) 28.dp else 8.dp,
                            animationSpec = androidx.compose.animation.core.spring(dampingRatio = 0.65f),
                            label = "indicator_dot_width"
                        )

                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .height(8.dp)
                                .width(dotWidth)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    if (isCurrent) {
                                        Brush.horizontalGradient(
                                            listOf(Color(0xFF8CE8FF), Color(0xFFD69EFF))
                                        )
                                    } else {
                                        if (isDark) Brush.linearGradient(listOf(Color(0x33FFFFFF), Color(0x22FFFFFF)))
                                        else Brush.linearGradient(listOf(Color(0x25000000), Color(0x18000000)))
                                    }
                                )
                        )
                    }
                }

                // Glacier Liquid Action Button
                Button(
                    onClick = {
                        if (step < slides.size) step++ else onComplete()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("onboarding_next_button"),
                    shape = RoundedCornerShape(27.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFF8CE8FF),
                                        Color(0xFFFFB28E),
                                        Color(0xFFD69EFF)
                                    )
                                )
                            )
                            .border(
                                width = 1.dp,
                                color = Color.White.copy(alpha = 0.8f),
                                shape = RoundedCornerShape(27.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (step < slides.size) "Continue" else "Launch LIFEBridge",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF12131A)
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Color(0xFF12131A),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                if (step < slides.size) {
                    TextButton(
                        onClick = onComplete,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 8.dp)
                            .testTag("onboarding_skip_button")
                    ) {
                        Text(
                            text = "Skip Tour",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Unique Liquid Glass Hero Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 16.dp,
                        shape = RoundedCornerShape(32.dp),
                        spotColor = Color(0x388CE8FF),
                        ambientColor = Color(0x22D69EFF)
                    )
                    .clip(RoundedCornerShape(32.dp))
                    .background(
                        if (isDark) Color(0xD01F202A) else Color(0xF2FFFFFF)
                    )
                    .border(
                        width = 1.5.dp,
                        brush = Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = if (isDark) 0.65f else 0.98f),
                                Color(0xFF8CE8FF).copy(alpha = 0.35f),
                                Color.White.copy(alpha = 0.15f)
                            )
                        ),
                        shape = RoundedCornerShape(32.dp)
                    )
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Floating 3D Crystal Icon Orb
                    Box(
                        modifier = Modifier
                            .size(92.dp)
                            .shadow(elevation = 10.dp, shape = CircleShape, spotColor = Color(0xFF8CE8FF))
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        Color(0xFF8CE8FF),
                                        Color(0xFFFFB28E),
                                        Color(0xFFD69EFF)
                                    )
                                )
                            )
                            .border(1.5.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        // Top specular shine
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.48f)
                                .align(Alignment.TopCenter)
                                .clip(RoundedCornerShape(topStart = 46.dp, topEnd = 46.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.White.copy(alpha = 0.60f), Color.Transparent)
                                )
                            )
                        )

                        Icon(
                            imageVector = currentSlide.icon,
                            contentDescription = null,
                            tint = Color(0xFF14151D),
                            modifier = Modifier.size(46.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Pill Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0x208CE8FF))
                            .border(1.dp, Color(0xFF8CE8FF).copy(alpha = 0.45f), RoundedCornerShape(16.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = currentSlide.badgeText,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (isDark) Color(0xFF8CE8FF) else Color(0xFF00789E)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Feature Tag Chips
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        currentSlide.tagList.take(3).forEach { tag ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isDark) Color(0x33FFFFFF) else Color(0x12000000))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = tag,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = currentSlide.title,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                ),
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = currentSlide.subtitle,
                style = MaterialTheme.typography.bodyLarge.copy(
                    lineHeight = 22.sp,
                    fontSize = 14.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun LanguageSelectionScreen(
    viewModel: LifeBridgeViewModel,
    onContinue: () -> Unit
) {
    val selectedLang by viewModel.currentLanguage.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Select Your Language",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                GradientButton(
                    text = "Continue",
                    onClick = onContinue,
                    modifier = Modifier.testTag("language_continue_button")
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = "Choose your preferred language for explanations, voice summaries, and task cards.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            SupportedLanguages.forEach { lang ->
                val isSelected = selectedLang == lang.code
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .testTag("lang_select_${lang.code}"),
                    shape = RoundedCornerShape(16.dp),
                    backgroundColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                    else MaterialTheme.colorScheme.surfaceVariant,
                    borderColor = if (isSelected) ElectricCyan else MaterialTheme.colorScheme.outline,
                    onClick = { viewModel.setLanguage(lang.code) }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = lang.displayName,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = lang.nativeName,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }

                        RadioButton(
                            selected = isSelected,
                            onClick = { viewModel.setLanguage(lang.code) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = ElectricCyan,
                                unselectedColor = TextTertiaryDark
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PermissionEducationScreen(
    onContinue: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "We Need a Few Permissions",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                GradientButton(
                    text = "Grant & Continue",
                    onClick = onContinue,
                    modifier = Modifier.testTag("permissions_continue_button")
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = "These help us provide the best experience and keep your data safe. LIFEBridge AI adheres strictly to data minimization and never sells your data.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            PermissionCard(
                icon = Icons.Filled.Folder,
                title = "Storage & Documents",
                description = "To import photos, PDF notices, and save offline summaries."
            )

            PermissionCard(
                icon = Icons.Filled.Mic,
                title = "Microphone (Optional)",
                description = "For push-to-talk voice commands and multilingual voice questions."
            )

            PermissionCard(
                icon = Icons.Filled.Notifications,
                title = "Notifications",
                description = "To remind you about critical deadlines and expiring document dates."
            )
        }
    }
}

@Composable
private fun PermissionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = ElectricCyan,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun SignUpScreen(
    viewModel: LifeBridgeViewModel,
    onNavigateToLogin: () -> Unit,
    onSuccess: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            IconButton(
                onClick = onNavigateToLogin,
                modifier = Modifier.statusBarsPadding()
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Create Your Account",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "Join LIFEBridge AI to synchronize documents and access verified safety intelligence.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("signup_name_field"),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("signup_email_field"),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("signup_password_field"),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            GradientButton(
                text = "Create Account",
                onClick = {
                    val finalName = if (name.isNotBlank()) name else "Alex Kumar"
                    val finalEmail = if (email.isNotBlank()) email else "alex@example.com"
                    viewModel.updateUserProfile(finalName, finalEmail)
                    onSuccess()
                },
                modifier = Modifier.testTag("signup_submit_button")
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Already have an account? ", color = TextSecondaryDark, fontSize = 14.sp)
                Text(
                    text = "Login",
                    color = ElectricCyan,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }
        }
    }
}

@Composable
fun LoginScreen(
    viewModel: LifeBridgeViewModel,
    onNavigateToSignUp: () -> Unit,
    onNavigateToForgot: () -> Unit,
    onSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                    .border(1.dp, ElectricCyan.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Spa,
                    contentDescription = null,
                    tint = ElectricCyan,
                    modifier = Modifier.size(38.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Welcome Back",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "Sign in to continue to LIFEBridge AI",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, bottom = 28.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_email_field"),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_password_field"),
                shape = RoundedCornerShape(12.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = "Forgot password?",
                    color = ElectricCyan,
                    fontSize = 13.sp,
                    modifier = Modifier.clickable { onNavigateToForgot() }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            GradientButton(
                text = "Login",
                onClick = onSuccess,
                modifier = Modifier.testTag("login_submit_button")
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onSuccess, // Guest mode bypass
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(listOf(ElectricCyan, RoyalViolet))
                )
            ) {
                Text("Continue as Guest (Offline Kit)", color = MaterialTheme.colorScheme.onBackground)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Don't have an account? ", color = TextSecondaryDark, fontSize = 14.sp)
                Text(
                    text = "Sign Up",
                    color = ElectricCyan,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToSignUp() }
                )
            }
        }
    }
}

@Composable
fun PasswordRecoveryScreen(
    onBack: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var sent by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            IconButton(
                onClick = onBack,
                modifier = Modifier.statusBarsPadding()
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Reset Your Password",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "Enter your email and we'll send you instructions to reset your password.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, bottom = 28.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            GradientButton(
                text = if (sent) "Instructions Sent ✓" else "Send Reset Link",
                enabled = !sent,
                onClick = { sent = true }
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onBack) {
                Text("Back to Login", color = ElectricCyan)
            }
        }
    }
}
