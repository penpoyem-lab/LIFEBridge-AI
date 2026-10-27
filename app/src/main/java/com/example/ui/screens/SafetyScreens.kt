package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ScamAnalysis
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.LifeBridgeViewModel

@Composable
fun ScamCheckerScreen(
    viewModel: LifeBridgeViewModel,
    onNavigate: (String) -> Unit
) {
    var activeTab by remember { mutableStateOf("Message") } // "Message" or "Link"
    var inputContent by remember { mutableStateOf("") }
    val isChecking by viewModel.isCheckingSafety.collectAsState()
    val lastAnalysis by viewModel.lastSafetyAnalysis.collectAsState()

    val sampleScamMessage = "CONGRATULATIONS! You have won a cash reward of ₹50,000 from Government Benefit Pool. Click https://bit.ly/urgent-claim-reward immediately or your prize expires within 2 hours."
    val sampleBankPhishing = "Dear user, your electricity account is scheduled for disconnection tonight at 9:30 PM due to unverified KYC. Contact manager immediately at 9821094820 or verify at http://192.168.1.1/kyc."

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { onNavigate("home") }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                    Text(
                        text = "Safety & Scam Checker",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Tabs: Message / Link
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    listOf("Message", "Link").forEach { tab ->
                        val isSelected = activeTab == tab
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) ElectricCyan else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable {
                                    activeTab = tab
                                    if (tab == "Link" && !inputContent.startsWith("http")) {
                                        inputContent = "https://bit.ly/claim-prize-urgent"
                                    }
                                }
                                .testTag("safety_tab_$tab"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tab,
                                color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            LifeBridgeBottomBar(
                currentScreen = "safety",
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
            Text(
                text = if (activeTab == "Message") "Paste suspicious SMS, WhatsApp message, email, or job offer:"
                else "Enter the website address or shortened link to check its safety profile:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Input TextField
            OutlinedTextField(
                value = inputContent,
                onValueChange = { inputContent = it },
                label = { Text(if (activeTab == "Message") "Message text" else "Website URL") },
                placeholder = {
                    Text(if (activeTab == "Message") "Paste SMS or email text here..." else "https://example.com/...")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (activeTab == "Message") 130.dp else 70.dp)
                    .testTag("safety_input_field"),
                shape = RoundedCornerShape(14.dp)
            )

            // Quick Samples Chips
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SuggestionChip(
                    onClick = { inputContent = sampleScamMessage },
                    label = { Text("Prize Scam SMS", fontSize = 11.sp) }
                )
                SuggestionChip(
                    onClick = { inputContent = sampleBankPhishing },
                    label = { Text("KYC Urgent Phish", fontSize = 11.sp) }
                )
            }

            // Check Safety Action Button
            GradientButton(
                text = if (isChecking) "Checking Security Patterns..." else "Check for Warnings",
                enabled = !isChecking && inputContent.isNotBlank(),
                onClick = { viewModel.checkScamOrLink(inputContent, activeTab) },
                icon = Icons.Filled.Security,
                colors = listOf(WarningAmber, DangerRose),
                modifier = Modifier.testTag("check_safety_button")
            )

            if (isChecking) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .background(Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    MiddleScreenTransparentLoader(
                        sizeDp = 64.dp,
                        statusMessage = "Auditing Security Signatures...",
                        subMessage = "Cross-referencing global threat heuristics",
                        isAnimating = true
                    )
                }
            }

            // Results Display
            if (lastAnalysis != null) {
                val analysis = lastAnalysis!!
                SafetyAnalysisResultCard(analysis = analysis)
            }
        }
    }
}

@Composable
fun SafetyAnalysisResultCard(
    analysis: ScamAnalysis
) {
    val (badgeBg, badgeColor, icon) = when (analysis.riskLevel) {
        "High Risk" -> Triple(DangerRose.copy(alpha = 0.2f), DangerRose, Icons.Filled.Warning)
        "Warning Signs" -> Triple(WarningAmber.copy(alpha = 0.2f), WarningAmber, Icons.Filled.Info)
        else -> Triple(RestrainedMint.copy(alpha = 0.2f), RestrainedMint, Icons.Filled.CheckCircle)
    }

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("safety_result_card"),
        borderColor = badgeColor
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, tint = badgeColor)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = analysis.riskLevel,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = badgeColor
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(badgeBg)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Risk: ${analysis.score}/100",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = badgeColor
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = analysis.explanation,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Detected Indicators
        if (analysis.indicators.isNotEmpty()) {
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Warning Indicators Found:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(6.dp))
            analysis.indicators.forEach { indicator ->
                Row(
                    modifier = Modifier.padding(vertical = 3.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Filled.ReportProblem,
                        contentDescription = null,
                        tint = DangerRose,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = indicator,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

        // Recommended Safe Steps
        if (analysis.safeSteps.isNotEmpty()) {
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Recommended Safe Verification Steps:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = RestrainedMint
            )
            Spacer(modifier = Modifier.height(6.dp))
            analysis.safeSteps.forEach { step ->
                Row(
                    modifier = Modifier.padding(vertical = 3.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Filled.Shield,
                        contentDescription = null,
                        tint = RestrainedMint,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = step,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}
