package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.LifeBridgeViewModel
import java.util.Calendar

@Composable
fun HomeScreen(
    viewModel: LifeBridgeViewModel,
    onNavigate: (String) -> Unit
) {
    val documents by viewModel.documents.collectAsState()
    val actionCards by viewModel.actionCards.collectAsState()
    val isOffline by viewModel.isOfflineMode.collectAsState()
    val userName by viewModel.userDisplayName.collectAsState()
    val activeRecalls by viewModel.activeNoteRecalls.collectAsState()
    val dismissedRecallIds by viewModel.dismissedRecallIds.collectAsState()

    val visibleRecall = activeRecalls.firstOrNull { it.id !in dismissedRecallIds }

    var selectedMoodIndex by remember { mutableStateOf(1) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            LifeBridgeBottomBar(
                currentScreen = "home",
                onNavigate = onNavigate
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("home_screen_column"),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            // Editorial Top Bar: Geometric Brand Glyph & Circular Action
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Geometric four-diamond brand glyph
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .clickable { onNavigate("profile") },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(2.dp)).background(Umbra))
                                Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(2.dp)).background(Umbra))
                            }
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(2.dp)).background(Umbra))
                                Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(2.dp)).background(Umbra))
                            }
                        }
                    }

                    // Circular outline button
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .border(1.2.dp, Umbra.copy(alpha = 0.2f), CircleShape)
                            .clickable { onNavigate("search") },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search",
                            tint = Umbra,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                }
            }

            // Daily Reflection / Greeting Card Section (Matching Screenshot)
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Daily reflection",
                        style = MaterialTheme.typography.bodyMedium,
                        color = CharcoalMuted,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = buildAnnotatedString {
                            append("Hello, $userName \uD83C\uDF3F\nHow do you feel about your ")
                            withStyle(SpanStyle(fontWeight = FontWeight.ExtraBold, color = Umbra)) {
                                append("current documents?")
                            }
                        },
                        style = MaterialTheme.typography.headlineMedium,
                        color = Umbra,
                        lineHeight = 32.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Warm peach reflection input pill with ↗ button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(Hudson.copy(alpha = 0.5f))
                            .clickable { onNavigate("search") }
                            .padding(horizontal = 18.dp, vertical = 14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Your reflection, document or question...",
                                color = CharcoalMuted,
                                fontSize = 14.sp
                            )
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(Umbra.copy(alpha = 0.08f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Search",
                                    tint = Umbra,
                                    modifier = Modifier
                                        .size(15.dp)
                                        .rotate(-45f)
                                )
                            }
                        }
                    }
                }
            }

            // Unique & Aesthetic Note Recall Notification (Liquid Glass Card)
            if (visibleRecall != null) {
                item {
                    AestheticNoteRecallCard(
                        recall = visibleRecall,
                        onListen = {
                            viewModel.readRecallAudio(visibleRecall.audioNoteText ?: visibleRecall.snippet)
                        },
                        onSnooze = {
                            viewModel.snoozeRecall(visibleRecall.id)
                        },
                        onDone = {
                            viewModel.completeRecall(visibleRecall.id)
                        },
                        onDismiss = {
                            viewModel.dismissRecallTemporarily(visibleRecall.id)
                        },
                        onClick = {
                            if (visibleRecall.documentId != null) {
                                onNavigate("document_detail")
                            } else {
                                onNavigate("reminders")
                            }
                        }
                    )
                }
            }

            // Daily Mood / Status Log (Row of pastel circular avatars from screenshot)
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Daily Status Log",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Umbra
                        )
                        Text(
                            text = "••",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val moods = listOf(
                            Pair("Calm", PastelWheat),
                            Pair("Peaceful", FarmersMarket),
                            Pair("Alert", PastelCoral),
                            Pair("Confident", Penna),
                            Pair("Concern", CountryRubble),
                            Pair("Active", PastelMint)
                        )

                        moods.forEachIndexed { index, (label, color) ->
                            val isSelected = selectedMoodIndex == index
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        width = if (isSelected) 2.5.dp else 0.dp,
                                        color = if (isSelected) Umbra else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { selectedMoodIndex = index },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = when (index) {
                                        0 -> "☺"
                                        1 -> "☻"
                                        2 -> "!"
                                        3 -> "★"
                                        4 -> "•"
                                        else -> "⚡"
                                    },
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Umbra
                                )
                            }
                        }
                    }
                }
            }

            // 2x2 Squircle Cards Grid ("Practices / Exercises based on your needs")
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Practices",
                        style = MaterialTheme.typography.bodyMedium,
                        color = CharcoalMuted,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = buildAnnotatedString {
                            append("Exercises based on ")
                            withStyle(SpanStyle(fontWeight = FontWeight.ExtraBold, color = Umbra)) {
                                append("your needs")
                            }
                        },
                        style = MaterialTheme.typography.headlineSmall,
                        color = Umbra
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Row 1: Penna (Blue) & PastelWheat (Yellow)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        EditorialSquircleCard(
                            title = "My Documents\n& Notices",
                            subtitle = "Universal Input",
                            backgroundColor = Penna,
                            decorativeIcon = Icons.Filled.Description,
                            onClick = { onNavigate("universal_input") },
                            modifier = Modifier.weight(1f)
                        )

                        EditorialSquircleCard(
                            title = "Build\nConfidence",
                            subtitle = "Verify & Safety",
                            backgroundColor = PastelWheat,
                            decorativeIcon = Icons.Filled.Shield,
                            onClick = { onNavigate("safety_checker") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Row 2: PastelMint (Green) & Hudson (Peach)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        EditorialSquircleCard(
                            title = "Diversity\n& Inclusion",
                            subtitle = "Live Voice Translate",
                            backgroundColor = PastelMint,
                            decorativeIcon = Icons.Filled.RecordVoiceOver,
                            onClick = { onNavigate("voice_translate") },
                            modifier = Modifier.weight(1f)
                        )

                        EditorialSquircleCard(
                            title = "Behavioral\nActivation",
                            subtitle = "Life Kit & Steps",
                            backgroundColor = Hudson,
                            decorativeIcon = Icons.Filled.MedicalServices,
                            onClick = { onNavigate("offline_kit") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Progress Card (Matching Screenshot: "Your progress - 89% with bubbly circles")
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    backgroundColor = PureWhite
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Your progress",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Umbra
                        )
                        Text(
                            text = "••",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "89%",
                                style = MaterialTheme.typography.headlineLarge,
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold,
                                color = Umbra,
                                lineHeight = 50.sp
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.End,
                            modifier = Modifier.widthIn(max = 160.dp)
                        ) {
                            Text(
                                text = "Of the weekly plan and action items completed",
                                style = MaterialTheme.typography.bodySmall,
                                color = CharcoalMuted,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Bubbly circle grid graphic from screenshot
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val circleColors = listOf(
                            PastelMint, PastelMint, Penna, PastelMint,
                            Penna, PastelMint, PastelWheat, PastelMint
                        )
                        circleColors.forEach { c ->
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(c.copy(alpha = 0.65f))
                            )
                        }
                    }
                }
            }

            // Recent Documents Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Documents",
                        style = MaterialTheme.typography.titleMedium,
                        color = Umbra,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "See All",
                        style = MaterialTheme.typography.bodyMedium,
                        color = FarmersMarket,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { onNavigate("library") }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (documents.isEmpty()) {
                    GlassCard(modifier = Modifier.fillMaxWidth(), backgroundColor = PureWhite) {
                        Text(
                            text = "No documents imported yet. Tap 'My Documents & Notices' above to scan or upload your first notice.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = CharcoalMuted
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        documents.take(3).forEach { doc ->
                            GlassCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("doc_item_${doc.id}"),
                                backgroundColor = PureWhite,
                                onClick = { viewModel.selectDocument(doc) }
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(Penna.copy(alpha = 0.5f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Description,
                                            contentDescription = null,
                                            tint = Umbra
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(14.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = doc.title,
                                            style = MaterialTheme.typography.titleSmall,
                                            color = Umbra,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1
                                        )
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            CategoryBadge(category = doc.category)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = doc.fileSizeFormatted,
                                                fontSize = 11.sp,
                                                color = CharcoalMuted
                                            )
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .size(30.dp)
                                            .clip(CircleShape)
                                            .background(Hudson.copy(alpha = 0.5f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = "Open",
                                            tint = Umbra,
                                            modifier = Modifier
                                                .size(14.dp)
                                                .rotate(-45f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Pending Action Cards Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Pending Action Steps",
                        style = MaterialTheme.typography.titleMedium,
                        color = Umbra,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "All Cards (${actionCards.count { !it.isCompleted }})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = FarmersMarket,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { onNavigate("action_cards") }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                val pendingCards = actionCards.filter { !it.isCompleted }.take(2)
                if (pendingCards.isEmpty()) {
                    GlassCard(modifier = Modifier.fillMaxWidth(), backgroundColor = PureWhite) {
                        Text(
                            text = "All action cards completed! You're completely up to date.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = FarmersMarket
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        pendingCards.forEach { card ->
                            GlassCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("action_card_${card.id}"),
                                backgroundColor = PureWhite,
                                onClick = { onNavigate("action_cards") }
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = card.isCompleted,
                                        onCheckedChange = { viewModel.toggleCardCompleted(card.id, card.isCompleted) },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = Umbra,
                                            uncheckedColor = CountryRubble
                                        )
                                    )

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = card.title,
                                            style = MaterialTheme.typography.titleSmall,
                                            color = Umbra,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (card.dueDate != null) {
                                            Text(
                                                text = "Due: ${card.dueDate}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color(0xFF854D0E)
                                            )
                                        }
                                    }

                                    PriorityBadge(priority = card.priority)
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
