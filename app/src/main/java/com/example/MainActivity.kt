package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.*
import com.example.ui.theme.LIFEBridgeTheme
import com.example.viewmodel.LifeBridgeViewModel
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: LifeBridgeViewModel = viewModel()
            val themeMode by viewModel.themeMode.collectAsState()
            val isDark = themeMode == "dark"

            LIFEBridgeTheme(darkTheme = isDark) {
                LifeBridgeApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun LifeBridgeApp(viewModel: LifeBridgeViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.snackbarMessage.collectLatest { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    // Android Hardware / Gesture Back Handling
    if (currentScreen != "home" && currentScreen != "splash") {
        BackHandler {
            when (currentScreen) {
                "scanner" -> viewModel.navigateTo("universal_input")
                "processing_progress" -> viewModel.navigateTo("universal_input")
                "explanation_results" -> viewModel.navigateTo("home")
                "action_cards" -> viewModel.navigateTo("home")
                "document_detail" -> viewModel.navigateTo("library")
                "safety_results" -> viewModel.navigateTo("safety_checker")
                "signup", "forgot_password" -> viewModel.navigateTo("login")
                else -> viewModel.navigateTo("home")
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { _ ->
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            when (currentScreen) {
                "splash" -> SplashScreen(
                    onTimeout = { viewModel.navigateTo("home") }
                )
                "onboarding_1", "onboarding" -> OnboardingScreen(
                    viewModel = viewModel,
                    onComplete = { viewModel.navigateTo("language_selection") }
                )
                "language_selection" -> LanguageSelectionScreen(
                    viewModel = viewModel,
                    onContinue = { viewModel.navigateTo("permissions") }
                )
                "permissions" -> PermissionEducationScreen(
                    onContinue = { viewModel.completeOnboarding() }
                )
                "signup" -> SignUpScreen(
                    viewModel = viewModel,
                    onNavigateToLogin = { viewModel.navigateTo("login") },
                    onSuccess = { viewModel.navigateTo("home") }
                )
                "login" -> LoginScreen(
                    viewModel = viewModel,
                    onNavigateToSignUp = { viewModel.navigateTo("signup") },
                    onNavigateToForgot = { viewModel.navigateTo("forgot_password") },
                    onSuccess = { viewModel.navigateTo("home") }
                )
                "forgot_password" -> PasswordRecoveryScreen(
                    onBack = { viewModel.navigateTo("login") }
                )
                "home" -> HomeScreen(
                    viewModel = viewModel,
                    onNavigate = { viewModel.navigateTo(it) }
                )
                "universal_input" -> UniversalInputScreen(
                    viewModel = viewModel,
                    onNavigate = { viewModel.navigateTo(it) }
                )
                "scanner" -> DocumentScannerScreen(
                    viewModel = viewModel,
                    onNavigate = { viewModel.navigateTo(it) }
                )
                "processing_progress" -> ProcessingProgressScreen(
                    viewModel = viewModel,
                    onNavigate = { viewModel.navigateTo(it) }
                )
                "explanation_results" -> ExplanationResultsScreen(
                    viewModel = viewModel,
                    onNavigate = { viewModel.navigateTo(it) }
                )
                "action_cards" -> ActionCardsScreen(
                    viewModel = viewModel,
                    onNavigate = { viewModel.navigateTo(it) }
                )
                "translation" -> TranslationScreen(
                    viewModel = viewModel,
                    onNavigate = { viewModel.navigateTo(it) }
                )
                "voice_translate", "live_voice" -> LiveVoiceTranslateScreen(
                    viewModel = viewModel,
                    onNavigate = { viewModel.navigateTo(it) }
                )
                "voice" -> VoiceAssistantScreen(
                    viewModel = viewModel,
                    onNavigate = { viewModel.navigateTo(it) }
                )
                "safety", "safety_checker", "safety_results" -> ScamCheckerScreen(
                    viewModel = viewModel,
                    onNavigate = { viewModel.navigateTo(it) }
                )
                "library" -> DocumentLibraryScreen(
                    viewModel = viewModel,
                    onNavigate = { viewModel.navigateTo(it) }
                )
                "document_detail" -> DocumentDetailScreen(
                    viewModel = viewModel,
                    onNavigate = { viewModel.navigateTo(it) }
                )
                "offline_kit" -> OfflineLifeKitScreen(
                    viewModel = viewModel,
                    onNavigate = { viewModel.navigateTo(it) }
                )
                "reminders" -> RemindersScreen(
                    viewModel = viewModel,
                    onNavigate = { viewModel.navigateTo(it) }
                )
                "search" -> SearchHistoryScreen(
                    viewModel = viewModel,
                    onNavigate = { viewModel.navigateTo(it) }
                )
                "notifications" -> NotificationsScreen(
                    onNavigate = { viewModel.navigateTo(it) }
                )
                "profile" -> AccountProfileScreen(
                    viewModel = viewModel,
                    onNavigate = { viewModel.navigateTo(it) }
                )
                "settings" -> SettingsScreen(
                    viewModel = viewModel,
                    onNavigate = { viewModel.navigateTo(it) }
                )
                "privacy_security" -> PrivacySecurityScreen(
                    onNavigate = { viewModel.navigateTo(it) }
                )
                "help_support" -> HelpSupportScreen(
                    onNavigate = { viewModel.navigateTo(it) }
                )
                "empty_state" -> EmptyStateScreen(
                    onAddDocument = { viewModel.navigateTo("universal_input") },
                    onBack = { viewModel.navigateTo("profile") }
                )
                "loading_state" -> LoadingStateScreen(
                    onBack = { viewModel.navigateTo("profile") }
                )
                "offline_state" -> OfflineStateScreen(
                    onViewOfflineKit = { viewModel.navigateTo("offline_kit") },
                    onBack = { viewModel.navigateTo("profile") }
                )
                "error_state" -> ErrorStateScreen(
                    onRetry = { viewModel.navigateTo("home") },
                    onBack = { viewModel.navigateTo("profile") }
                )
                "retry_state" -> RetryStateScreen(
                    onRetry = { viewModel.navigateTo("home") },
                    onBack = { viewModel.navigateTo("profile") }
                )
                else -> HomeScreen(
                    viewModel = viewModel,
                    onNavigate = { viewModel.navigateTo(it) }
                )
            }
        }
    }
}
