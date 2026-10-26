package com.example.ui.screens

import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.parseJsonStringList
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.LifeBridgeViewModel
import kotlinx.coroutines.launch

@Composable
fun UniversalInputScreen(
    viewModel: LifeBridgeViewModel,
    onNavigate: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var pastedText by remember { mutableStateOf("") }
    var documentTitle by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Government") }

    val categories = listOf("Government", "Bills", "School", "Receipts", "Employment", "Other")

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                var fileName = "Imported Document"
                context.contentResolver.query(it, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (cursor.moveToFirst() && nameIndex >= 0) {
                        fileName = cursor.getString(nameIndex) ?: fileName
                    }
                }
                documentTitle = fileName.substringBeforeLast(".")
                val content = context.contentResolver.openInputStream(it)?.bufferedReader()?.use { reader ->
                    reader.readText()
                } ?: ""
                if (content.isNotBlank()) {
                    pastedText = content
                    scope.launch { snackbarHostState.showSnackbar("File '$fileName' loaded.") }
                } else {
                    scope.launch { snackbarHostState.showSnackbar("Selected '$fileName'. Verify or add description below.") }
                }
            } catch (e: Exception) {
                scope.launch { snackbarHostState.showSnackbar("Could not read file: ${e.localizedMessage}") }
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                    text = "Add Information",
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
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Choose how you want to add your document or message:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // 4 Input Mode Options
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InputModeCard(
                    icon = Icons.Filled.CloudUpload,
                    title = "Upload File",
                    subtitle = "PDF, Image, DOC",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        filePickerLauncher.launch("*/*")
                    }
                )

                InputModeCard(
                    icon = Icons.Filled.CameraAlt,
                    title = "Take Photo",
                    subtitle = "Scan with Camera",
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate("scanner") }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InputModeCard(
                    icon = Icons.Filled.ContentPaste,
                    title = "Paste Text",
                    subtitle = "From SMS, Email",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                        val clip = clipboard?.primaryClip
                        if (clip != null && clip.itemCount > 0) {
                            val text = clip.getItemAt(0).coerceToText(context).toString()
                            if (text.isNotBlank()) {
                                pastedText = text
                                if (documentTitle.isBlank()) {
                                    documentTitle = text.lines().firstOrNull()?.trim()?.take(40) ?: "Pasted Notice"
                                }
                                scope.launch { snackbarHostState.showSnackbar("Pasted text from clipboard.") }
                            } else {
                                scope.launch { snackbarHostState.showSnackbar("Clipboard is empty.") }
                            }
                        } else {
                            scope.launch { snackbarHostState.showSnackbar("Clipboard is empty. Copy text first.") }
                        }
                    }
                )

                InputModeCard(
                    icon = Icons.Filled.Mic,
                    title = "Voice Input",
                    subtitle = "Speak to record",
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate("voice") }
                )
            }

            // Quick Live Voice Translate Feature Pill (Google Translate Style)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(PureWhite, PastelMint.copy(alpha = 0.45f))
                        )
                    )
                    .border(1.dp, Umbra.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
                    .clickable { onNavigate("voice_translate") }
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Umbra),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.RecordVoiceOver,
                                contentDescription = null,
                                tint = PureWhite,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Live Voice Translate",
                                fontWeight = FontWeight.Bold,
                                color = Umbra,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Real-time dual-speaker conversation (Google Translate style)",
                                color = CharcoalMuted,
                                fontSize = 11.sp
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Open",
                        tint = Umbra,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Document Title
            OutlinedTextField(
                value = documentTitle,
                onValueChange = { documentTitle = it },
                label = { Text("Document Title (Optional)") },
                placeholder = { Text("e.g. Scholarship Application Notice") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_document_title"),
                shape = RoundedCornerShape(14.dp)
            )

            // Category Chips
            Text(
                text = "Document Category",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )

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

            // Text Content Box
            OutlinedTextField(
                value = pastedText,
                onValueChange = { pastedText = it },
                label = { Text("Document or Message Text") },
                placeholder = { Text("Paste or type the text of your letter, bill, scholarship, or notice here...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .testTag("input_document_text"),
                shape = RoundedCornerShape(14.dp),
                maxLines = 8
            )

            // Action Button
            GradientButton(
                text = "Analyze & Explain Document",
                onClick = {
                    if (pastedText.isBlank()) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Please enter, paste, or upload document text first.")
                        }
                    } else {
                        val title = if (documentTitle.isNotBlank()) documentTitle else "Document Notice"
                        viewModel.processDocument(title, pastedText, selectedCategory)
                    }
                },
                icon = Icons.Filled.AutoAwesome,
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .testTag("start_analysis_button")
            )
        }
    }
}

@Composable
private fun InputModeCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = modifier.height(115.dp),
        shape = RoundedCornerShape(16.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = ElectricCyan,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun DocumentScannerScreen(
    viewModel: LifeBridgeViewModel,
    onNavigate: (String) -> Unit
) {
    val context = LocalContext.current

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            var docName = "Scanned Document"
            context.contentResolver.query(it, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (cursor.moveToFirst() && nameIndex >= 0) {
                    docName = cursor.getString(nameIndex) ?: docName
                }
            }
            viewModel.processDocument(
                title = docName.substringBeforeLast("."),
                content = "Document image ($docName) uploaded successfully. Initializing visual OCR and semantic information extraction.",
                category = "Scanned"
            )
        }
    }

    val cameraCaptureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            val timestamp = System.currentTimeMillis() % 10000
            viewModel.processDocument(
                title = "Camera Scan #$timestamp",
                content = "Camera capture image (${bitmap.width}x${bitmap.height} px) acquired. Extracting structured key points, due dates, and action items.",
                category = "Scanned"
            )
        }
    }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onNavigate("universal_input") }) {
                    Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
                }

                Text(
                    text = "Scan Document",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                IconButton(onClick = { /* Flash toggle */ }) {
                    Icon(Icons.Filled.FlashOn, contentDescription = "Flash", tint = Color.White)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Viewfinder frame
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(24.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(2.dp, ElectricCyan, RoundedCornerShape(20.dp))
                    .background(Color(0x331E293B)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.CropFree,
                        contentDescription = null,
                        tint = ElectricCyan.copy(alpha = 0.8f),
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Position document inside the frame",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Hold still for sharp text extraction",
                        color = TextSecondaryDark,
                        fontSize = 12.sp
                    )
                }
            }

            // Bottom controls
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F172A))
                    .padding(24.dp)
                    .windowInsetsPadding(WindowInsets.navigationBars),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = {
                        photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }) {
                        Text("Gallery", color = Color.White)
                    }

                    // Shutter button
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Brush.radialGradient(listOf(ElectricCyan, ElectricBlue)))
                            .clickable {
                                cameraCaptureLauncher.launch(null)
                            }
                            .testTag("scanner_shutter_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .border(2.dp, Color.White, CircleShape)
                        )
                    }

                    TextButton(onClick = { onNavigate("universal_input") }) {
                        Text("Manual", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun ProcessingProgressScreen(
    viewModel: LifeBridgeViewModel,
    onNavigate: (String) -> Unit
) {
    val progress by viewModel.processingProgress.collectAsState()
    val stage by viewModel.processingStage.collectAsState()

    val steps = listOf(
        "Extracting text",
        "Detecting language",
        "Analyzing document",
        "Generating explanation"
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Circular progress indicator with percentage
            Box(
                modifier = Modifier.size(160.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxSize(),
                    color = ElectricCyan,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    strokeWidth = 10.dp
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Processing...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Step items
            steps.forEachIndexed { index, stepName ->
                val isCompleted = progress > (index + 1) * 0.22f
                val isCurrent = !isCompleted && progress >= index * 0.22f

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isCompleted -> RestrainedMint.copy(alpha = 0.2f)
                                    isCurrent -> ElectricCyan.copy(alpha = 0.2f)
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCompleted) {
                            Icon(Icons.Filled.Check, contentDescription = null, tint = RestrainedMint, modifier = Modifier.size(16.dp))
                        } else if (isCurrent) {
                            CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp, color = ElectricCyan)
                        } else {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(TextTertiaryDark))
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = stepName,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (isCompleted || isCurrent) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Estimated time: < 3 seconds",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ExplanationResultsScreen(
    viewModel: LifeBridgeViewModel,
    onNavigate: (String) -> Unit
) {
    val explanation by viewModel.lastExplanation.collectAsState()

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
                    text = "Document Analysis",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                IconButton(onClick = {
                    explanation?.let { viewModel.speak(it.summary) }
                }) {
                    Icon(Icons.Filled.VolumeUp, contentDescription = "Listen", tint = ElectricCyan)
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
        if (explanation == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No analysis available", color = MaterialTheme.colorScheme.onBackground)
            }
        } else {
            val expl = explanation!!
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .testTag("explanation_results_column"),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Category & Badge
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CategoryBadge(category = expl.documentType)

                        if (expl.needsHumanReview) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(WarningAmber.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Needs Review",
                                    color = WarningAmber,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Summary Card
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Summary",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = expl.summary,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Important Information & Facts
                if (expl.importantFacts.isNotEmpty()) {
                    item {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Important Facts",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            expl.importantFacts.forEach { fact ->
                                Row(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        Icons.Filled.CheckCircle,
                                        contentDescription = null,
                                        tint = RestrainedMint,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = fact,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            }
                        }
                    }
                }

                // Deadlines & Dates
                if (expl.dates.isNotEmpty()) {
                    item {
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            borderColor = WarningAmber.copy(alpha = 0.5f)
                        ) {
                            Text(
                                text = "Deadlines & Dates",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = WarningAmber
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            expl.dates.forEach { dateItem ->
                                Row(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        Icons.Filled.Event,
                                        contentDescription = null,
                                        tint = WarningAmber,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = dateItem,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            }
                        }
                    }
                }

                // Requirements & Documents
                if (expl.requirements.isNotEmpty()) {
                    item {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Required Documents / Tasks",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            expl.requirements.forEach { req ->
                                Row(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        Icons.Filled.Description,
                                        contentDescription = null,
                                        tint = ElectricCyan,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = req,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            }
                        }
                    }
                }

                // Warnings & Uncertainties
                if (expl.warnings.isNotEmpty() || expl.uncertainties.isNotEmpty()) {
                    item {
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            borderColor = DangerRose.copy(alpha = 0.5f)
                        ) {
                            Text(
                                text = "Potential Warnings & Ambiguities",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = DangerRose
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            expl.warnings.forEach { warning ->
                                Row(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        Icons.Filled.Warning,
                                        contentDescription = null,
                                        tint = DangerRose,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = warning,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            }
                            expl.uncertainties.forEach { uncert ->
                                Row(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        Icons.Filled.HelpOutline,
                                        contentDescription = null,
                                        tint = WarningAmber,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = uncert,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                // Action Cards CTA
                item {
                    GradientButton(
                        text = "View Generated Action Cards (${expl.actionCards.size})",
                        onClick = { onNavigate("action_cards") },
                        icon = Icons.Filled.Checklist,
                        modifier = Modifier.testTag("view_action_cards_button")
                    )
                }
            }
        }
    }
}

@Composable
fun ActionCardsScreen(
    viewModel: LifeBridgeViewModel,
    onNavigate: (String) -> Unit
) {
    val cards by viewModel.actionCards.collectAsState()
    val filter by viewModel.cardFilter.collectAsState()

    val filteredCards = when (filter) {
        "Pending" -> cards.filter { !it.isCompleted }
        "Completed" -> cards.filter { it.isCompleted }
        else -> cards
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { onNavigate("home") }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                        Text(
                            text = "Action Cards",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    IconButton(onClick = { onNavigate("reminders") }) {
                        Icon(Icons.Filled.NotificationsNone, contentDescription = "Reminders", tint = ElectricCyan)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Tabs: All, Pending, Completed
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("All", "Pending", "Completed").forEach { tab ->
                        val isSelected = filter == tab
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isSelected) ElectricCyan else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .clickable { viewModel.setCardFilter(tab) }
                                .testTag("card_filter_$tab"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tab,
                                color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
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
    ) { padding ->
        if (filteredCards.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.TaskAlt,
                        contentDescription = null,
                        tint = RestrainedMint,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No $filter action cards",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .testTag("action_cards_list"),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredCards, key = { it.id }) { card ->
                    ActionCardItemView(
                        card = card,
                        onToggleComplete = { viewModel.toggleCardCompleted(card.id, card.isCompleted) },
                        onToggleReminder = { viewModel.toggleCardReminder(card.id, card.reminderScheduled) },
                        onDelete = { viewModel.deleteCard(card.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionCardItemView(
    card: com.example.data.local.entities.ActionCardEntity,
    onToggleComplete: () -> Unit,
    onToggleReminder: () -> Unit,
    onDelete: () -> Unit
) {
    val checklist = remember(card.checklistJson) { parseJsonStringList(card.checklistJson) }

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("action_card_view_${card.id}"),
        borderColor = if (card.isCompleted) RestrainedMint.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = card.isCompleted,
                    onCheckedChange = { onToggleComplete() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = RestrainedMint,
                        uncheckedColor = TextSecondaryDark
                    )
                )

                Column {
                    Text(
                        text = card.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (card.isCompleted) TextSecondaryDark else MaterialTheme.colorScheme.onBackground
                    )

                    if (card.dueDate != null) {
                        Text(
                            text = "Due: ${card.dueDate}",
                            style = MaterialTheme.typography.bodySmall,
                            color = WarningAmber
                        )
                    }
                }
            }

            PriorityBadge(priority = card.priority)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = card.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (checklist.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Checklist:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            checklist.forEach { item ->
                Row(
                    modifier = Modifier.padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(ElectricCyan)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = item,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

        if (card.safeNextStep != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                    .padding(8.dp)
            ) {
                Text(
                    text = "Safe Next Step: ${card.safeNextStep}",
                    style = MaterialTheme.typography.bodySmall,
                    color = ElectricCyan,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Card Action Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = onToggleReminder) {
                    Icon(
                        imageVector = if (card.reminderScheduled) Icons.Filled.NotificationsActive else Icons.Outlined.Notifications,
                        contentDescription = "Set Reminder",
                        tint = if (card.reminderScheduled) WarningAmber else TextSecondaryDark
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

            TextButton(onClick = onToggleComplete) {
                Text(
                    text = if (card.isCompleted) "Mark Pending" else "Mark Complete ✓",
                    color = if (card.isCompleted) TextSecondaryDark else RestrainedMint,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
