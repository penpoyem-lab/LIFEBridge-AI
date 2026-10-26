package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.DocumentEntity
import com.example.data.model.parseJsonStringList
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.LifeBridgeViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DocumentLibraryScreen(
    viewModel: LifeBridgeViewModel,
    onNavigate: (String) -> Unit
) {
    val documents by viewModel.documents.collectAsState()
    var selectedCategory by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }

    val categories = listOf("All", "Government", "Bills", "School", "Receipts", "Employment")

    val filteredDocs = documents.filter { doc ->
        (selectedCategory == "All" || doc.category.equals(selectedCategory, ignoreCase = true)) &&
                (searchQuery.isBlank() || doc.title.contains(searchQuery, ignoreCase = true) || doc.summary.contains(searchQuery, ignoreCase = true))
    }

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
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Document Library",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    IconButton(onClick = { onNavigate("offline_kit") }) {
                        Icon(Icons.Filled.MedicalServices, contentDescription = "Offline Kit", tint = RestrainedMint)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search titles, notices, keywords...") },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Filled.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("library_search_field"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Category Chips
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { cat ->
                        val isSelected = selectedCategory == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ElectricCyan,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                }
            }
        },
        bottomBar = {
            LifeBridgeBottomBar(
                currentScreen = "library",
                onNavigate = onNavigate
            )
        }
    ) { padding ->
        if (filteredDocs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.FolderOpen,
                        contentDescription = null,
                        tint = TextTertiaryDark,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No documents found",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Scan or import a document to see it here",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .testTag("document_library_list"),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredDocs, key = { it.id }) { doc ->
                    DocumentLibraryCard(
                        doc = doc,
                        onClick = { viewModel.selectDocument(doc) },
                        onFavorite = { viewModel.toggleDocumentFavorite(doc.id) },
                        onDelete = { viewModel.deleteDocument(doc.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun DocumentLibraryCard(
    doc: DocumentEntity,
    onClick: () -> Unit,
    onFavorite: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormatted = remember(doc.createdAt) {
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(doc.createdAt))
    }

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("library_card_${doc.id}"),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (doc.category.lowercase()) {
                        "government" -> Icons.Filled.AccountBalance
                        "bills" -> Icons.AutoMirrored.Filled.ReceiptLong
                        "school" -> Icons.Filled.School
                        else -> Icons.Filled.Description
                    },
                    contentDescription = null,
                    tint = ElectricCyan,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = doc.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(3.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CategoryBadge(category = doc.category)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "$dateFormatted • ${doc.fileSizeFormatted}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row {
                IconButton(onClick = onFavorite) {
                    Icon(
                        imageVector = if (doc.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (doc.isFavorite) DangerRose else TextTertiaryDark
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Delete",
                        tint = TextTertiaryDark
                    )
                }
            }
        }
    }
}

@Composable
fun DocumentDetailScreen(
    viewModel: LifeBridgeViewModel,
    onNavigate: (String) -> Unit
) {
    val selectedDoc by viewModel.selectedDocument.collectAsState()
    var showRawText by remember { mutableStateOf(false) }

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
                IconButton(onClick = { onNavigate("library") }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }

                Text(
                    text = "Document Details",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                IconButton(onClick = {
                    selectedDoc?.let { viewModel.speak(it.summary) }
                }) {
                    Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Listen", tint = ElectricCyan)
                }
            }
        },
        bottomBar = {
            LifeBridgeBottomBar(
                currentScreen = "library",
                onNavigate = onNavigate
            )
        }
    ) { padding ->
        if (selectedDoc == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No document selected", color = MaterialTheme.colorScheme.onBackground)
            }
        } else {
            val doc = selectedDoc!!
            val importantFacts = remember(doc.importantFactsJson) { parseJsonStringList(doc.importantFactsJson) }
            val dates = remember(doc.datesJson) { parseJsonStringList(doc.datesJson) }
            val requirements = remember(doc.requirementsJson) { parseJsonStringList(doc.requirementsJson) }
            val warnings = remember(doc.warningsJson) { parseJsonStringList(doc.warningsJson) }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .testTag("document_detail_column"),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Info
                item {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CategoryBadge(category = doc.category)
                            Text(
                                text = doc.fileSizeFormatted,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = doc.title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                // Summary
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Summary",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = doc.summary,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Important Facts
                if (importantFacts.isNotEmpty()) {
                    item {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Important Facts",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            importantFacts.forEach { fact ->
                                Text(
                                    text = "• $fact",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                // Deadlines
                if (dates.isNotEmpty()) {
                    item {
                        GlassCard(modifier = Modifier.fillMaxWidth(), borderColor = WarningAmber.copy(alpha = 0.5f)) {
                            Text(
                                text = "Key Dates & Deadlines",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = WarningAmber
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            dates.forEach { d ->
                                Text(
                                    text = "📅 $d",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                // Original Text Toggle
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Original Document Text",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            TextButton(onClick = { showRawText = !showRawText }) {
                                Text(if (showRawText) "Hide" else "Show", color = ElectricCyan)
                            }
                        }

                        if (showRawText) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = doc.originalText,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Action buttons
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { onNavigate("action_cards") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan)
                        ) {
                            Text("View Action Cards", color = Color.Black, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { viewModel.deleteDocument(doc.id) },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Delete", color = DangerRose)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OfflineLifeKitScreen(
    viewModel: LifeBridgeViewModel,
    onNavigate: (String) -> Unit
) {
    val context = LocalContext.current
    val emergencyContacts by viewModel.emergencyContacts.collectAsState()
    val documents by viewModel.documents.collectAsState()
    val actionCards by viewModel.actionCards.collectAsState()
    val isOffline by viewModel.isOfflineMode.collectAsState()

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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { onNavigate("home") }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                    Text(
                        text = "Offline Life Kit",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Switch(
                    checked = isOffline,
                    onCheckedChange = { viewModel.toggleOfflineMode(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = RestrainedMint
                    )
                )
            }
        },
        bottomBar = {
            LifeBridgeBottomBar(
                currentScreen = "library",
                onNavigate = onNavigate
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .testTag("offline_life_kit_column"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Status Banner
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = RestrainedMint.copy(alpha = 0.15f),
                    borderColor = RestrainedMint
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(RestrainedMint.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.OfflineBolt, contentDescription = null, tint = RestrainedMint)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "100% Offline Accessible",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = RestrainedMint
                            )
                            Text(
                                text = "Saved documents, emergency contacts, and checklists work without cellular or internet data.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            }

            // Quick Stats 3-Grid
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GlassCard(modifier = Modifier.weight(1f)) {
                        Text(text = "${documents.size}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = ElectricCyan)
                        Text(text = "Saved Summaries", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    GlassCard(modifier = Modifier.weight(1f)) {
                        Text(text = "${actionCards.size}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = RoyalViolet)
                        Text(text = "Action Cards", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    GlassCard(modifier = Modifier.weight(1f)) {
                        Text(text = "${emergencyContacts.size}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = DangerRose)
                        Text(text = "Emergency Helplines", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // Emergency Helplines Section
            item {
                Text(
                    text = "Emergency Directory (Direct Dial)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            items(emergencyContacts) { contact ->
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("emergency_contact_${contact.number}"),
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${contact.number}"))
                        context.startActivity(intent)
                    }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    when (contact.category.lowercase()) {
                                        "police" -> ElectricBlue.copy(alpha = 0.2f)
                                        "medical" -> DangerRose.copy(alpha = 0.2f)
                                        "fire" -> WarningAmber.copy(alpha = 0.2f)
                                        else -> RoyalViolet.copy(alpha = 0.2f)
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (contact.category.lowercase()) {
                                    "police" -> Icons.Filled.LocalPolice
                                    "medical" -> Icons.Filled.LocalHospital
                                    "fire" -> Icons.Filled.LocalFireDepartment
                                    else -> Icons.Filled.Phone
                                },
                                contentDescription = null,
                                tint = when (contact.category.lowercase()) {
                                    "police" -> ElectricBlue
                                    "medical" -> DangerRose
                                    "fire" -> WarningAmber
                                    else -> RoyalViolet
                                }
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = contact.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = contact.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(RestrainedMint.copy(alpha = 0.2f))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Dial ${contact.number}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = RestrainedMint
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RemindersScreen(
    viewModel: LifeBridgeViewModel,
    onNavigate: (String) -> Unit
) {
    val cards by viewModel.actionCards.collectAsState()
    val scheduledCards = cards.filter { it.reminderScheduled }
    val recalls by viewModel.allNoteRecalls.collectAsState()

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
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Umbra)
                }
                Text(
                    text = "Note Recalls & Reminders",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Umbra
                )
            }
        },
        bottomBar = {
            LifeBridgeBottomBar(
                currentScreen = "library",
                onNavigate = onNavigate
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Note Recalls Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Aesthetic Note Recalls",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Umbra
                    )
                    Text(
                        text = "${recalls.size} Active",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = FarmersMarket
                    )
                }
            }

            if (recalls.isEmpty()) {
                item {
                    Text("No active note recalls.", color = CharcoalMuted, fontSize = 13.sp)
                }
            } else {
                items(recalls, key = { it.id }) { recall ->
                    AestheticNoteRecallCard(
                        recall = recall,
                        onListen = { viewModel.readRecallAudio(recall.audioNoteText ?: recall.snippet) },
                        onSnooze = { viewModel.snoozeRecall(recall.id) },
                        onDone = { viewModel.completeRecall(recall.id) },
                        onDismiss = { viewModel.dismissRecallTemporarily(recall.id) }
                    )
                }
            }

            // Action Card Reminders Section
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Scheduled Action Card Deadlines",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Umbra
                )
            }

            if (scheduledCards.isEmpty()) {
                item {
                    Text("No scheduled action card deadlines. Tap the bell icon on any Action Card to schedule.", color = CharcoalMuted, fontSize = 13.sp)
                }
            } else {
                items(scheduledCards, key = { it.id }) { card ->
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = PureWhite
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = card.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Umbra
                                )
                                Text(
                                    text = "Scheduled Deadline: ${card.dueDate ?: "Upcoming"}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = FarmersMarket
                                )
                            }

                            IconButton(onClick = { viewModel.toggleCardReminder(card.id, true) }) {
                                Icon(Icons.Filled.NotificationsActive, contentDescription = "Cancel Reminder", tint = Umbra)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchHistoryScreen(
    viewModel: LifeBridgeViewModel,
    onNavigate: (String) -> Unit
) {
    val query by viewModel.searchQuery.collectAsState()
    val documents by viewModel.documents.collectAsState()

    val searchResults = remember(query, documents) {
        if (query.isBlank()) emptyList()
        else documents.filter { it.title.contains(query, ignoreCase = true) || it.summary.contains(query, ignoreCase = true) }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onNavigate("home") }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                OutlinedTextField(
                    value = query,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("Search documents, notices, facts...") },
                    modifier = Modifier.fillMaxWidth().testTag("search_input_query"),
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(Icons.Filled.Clear, contentDescription = "Clear")
                            }
                        }
                    }
                )
            }
        },
        bottomBar = {
            LifeBridgeBottomBar(
                currentScreen = "home",
                onNavigate = onNavigate
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (query.isBlank()) {
                item {
                    Text(
                        text = "Recent Searches",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                items(listOf("Govt Notice", "Scholarship", "Electricity Bill", "Lottery Scam")) { term ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.setSearchQuery(term) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.History, contentDescription = null, tint = TextTertiaryDark, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = term, color = MaterialTheme.colorScheme.onBackground)
                    }
                }
            } else if (searchResults.isEmpty()) {
                item {
                    Text("No results for \"$query\"", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                items(searchResults) { doc ->
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { viewModel.selectDocument(doc) }
                    ) {
                        Text(text = doc.title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                        Text(text = doc.summary, maxLines = 2, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationsScreen(
    onNavigate: (String) -> Unit
) {
    val notifications = remember {
        listOf(
            Triple("Action Card Reminder", "Verify your student scholarship income certificate before 30 June.", "2 hours ago"),
            Triple("New Document Analyzed", "Your electricity utility bill was processed into 1 Action Card.", "Yesterday"),
            Triple("Offline Kit Updated", "Emergency numbers and preparedness guides saved locally.", "3 days ago")
        )
    }

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
                    text = "Notification Center",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        },
        bottomBar = {
            LifeBridgeBottomBar(
                currentScreen = "home",
                onNavigate = onNavigate
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(notifications) { (title, desc, time) ->
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                        Text(text = time, fontSize = 11.sp, color = TextTertiaryDark)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
