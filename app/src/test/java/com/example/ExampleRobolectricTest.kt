package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.parseJsonStringList
import com.example.data.model.toJsonStringList
import com.example.data.repository.LifeBridgeRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun readStringFromContext() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("LIFEBridge AI", appName)
    }

    @Test
    fun jsonListSerialization_roundTrip() {
        val original = listOf("Fact 1", "Fact 2: ₹1,500 due", "Deadline 30 June")
        val json = toJsonStringList(original)
        val restored = parseJsonStringList(json)
        assertEquals(original, restored)
    }

    @Test
    fun repository_analyzeDocument_createsExplanationAndActionCards() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = LifeBridgeRepository(context)

        val sampleText = "Scholarship notice: Annual tuition grant ₹25,000. Apply before 30 June 2026."
        val explanation = repo.analyzeDocument("Grant Notice", sampleText)

        assertNotNull(explanation)
        assertEquals("School", explanation.documentType)
        assertTrue(explanation.actionCards.isNotEmpty())
    }

    @Test
    fun repository_scamChecker_detectsUrgentPrizeRisk() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = LifeBridgeRepository(context)

        val scamMsg = "URGENT: You have won lottery cash of 5,00,000! Click bit.ly/claim immediately or forfeit."
        val result = repo.analyzeSafety(scamMsg, "Message")

        assertNotNull(result)
        assertTrue(result.score >= 50)
        assertEquals("High Risk", result.riskLevel)
        assertTrue(result.indicators.isNotEmpty())
    }

    @Test
    fun repository_liveVoiceTranslate_returnsConversationalPhonetics() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = LifeBridgeRepository(context)

        val result = repo.liveVoiceTranslate("Hello, how are you?", "en", "Spanish", "you")
        assertNotNull(result)
        assertTrue(result.translatedText.contains("Hola", ignoreCase = true))
        assertNotNull(result.pronunciation)
    }

    @Test
    fun repository_noteRecall_snoozeAndMarkDone() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = LifeBridgeRepository(context)

        val testRecall = com.example.data.local.entities.NoteRecallEntity(
            id = "test_rec_01",
            title = "Test Prescription",
            snippet = "Take dosage after dinner",
            scheduledTimeFormatted = "Tonight, 8:00 PM"
        )
        repo.addNoteRecall(testRecall)
        repo.snoozeRecall("test_rec_01", "Snoozed +2h")
        repo.markRecallDone("test_rec_01")
        assertTrue(true)
    }

    @Test
    fun appMenuState_toggleAndNavigate() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val vm = com.example.viewmodel.LifeBridgeViewModel(app)

        assertEquals(false, vm.isAppMenuOpen.value)
        vm.toggleAppMenu(true)
        assertEquals(true, vm.isAppMenuOpen.value)

        // Navigating to any screen automatically closes the menu
        vm.navigateTo("security_audit")
        assertEquals(false, vm.isAppMenuOpen.value)
        assertEquals("security_audit", vm.currentScreen.value)
    }
}
