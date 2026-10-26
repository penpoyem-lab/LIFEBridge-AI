package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.ActionCardDao
import com.example.data.local.dao.ChatMessageDao
import com.example.data.local.dao.DocumentDao
import com.example.data.local.dao.EmergencyContactDao
import com.example.data.local.dao.NoteRecallDao
import com.example.data.local.dao.SafetyCheckDao
import com.example.data.local.entities.ActionCardEntity
import com.example.data.local.entities.ChatMessageEntity
import com.example.data.local.entities.DocumentEntity
import com.example.data.local.entities.EmergencyContactEntity
import com.example.data.local.entities.NoteRecallEntity
import com.example.data.local.entities.SafetyCheckEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        DocumentEntity::class,
        ActionCardEntity::class,
        SafetyCheckEntity::class,
        EmergencyContactEntity::class,
        NoteRecallEntity::class,
        ChatMessageEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class LifeBridgeDatabase : RoomDatabase() {
    abstract fun documentDao(): DocumentDao
    abstract fun actionCardDao(): ActionCardDao
    abstract fun safetyCheckDao(): SafetyCheckDao
    abstract fun emergencyContactDao(): EmergencyContactDao
    abstract fun noteRecallDao(): NoteRecallDao
    abstract fun chatMessageDao(): ChatMessageDao

    companion object {
        @Volatile
        private var INSTANCE: LifeBridgeDatabase? = null

        fun getDatabase(context: Context): LifeBridgeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LifeBridgeDatabase::class.java,
                    "lifebridge_database"
                )
                    .addCallback(DatabaseCallback())
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database)
                    }
                }
            }
        }

        private suspend fun populateInitialData(database: LifeBridgeDatabase) {
            // Seed Emergency Contacts
            val initialEmergencyContacts = listOf(
                EmergencyContactEntity("1", "National Police Service", "100", "Police", "Immediate emergency police dispatch and emergency safety response"),
                EmergencyContactEntity("2", "Emergency Medical / Ambulance", "102", "Medical", "Emergency paramedic response, patient transport & trauma care"),
                EmergencyContactEntity("3", "Fire & Rescue Service", "101", "Fire", "Fire control, search and rescue, hazardous materials containment"),
                EmergencyContactEntity("4", "Women's Safety Helpline", "1091", "Helpline", "24/7 confidential safety assistance, legal guidance & crisis support"),
                EmergencyContactEntity("5", "Disaster Response Management", "1070", "Disaster", "Natural disaster crisis command, flood, storm & cyclone coordination"),
                EmergencyContactEntity("6", "Childline Support Helpline", "1098", "Helpline", "National 24-hour emergency toll-free outreach for children in need"),
                EmergencyContactEntity("7", "Mental Health Crisis Helpline (KIRAN)", "1800-599-0019", "Helpline", "24/7 psychological support, stress & suicide prevention helpline")
            )
            database.emergencyContactDao().insertContacts(initialEmergencyContacts)

            // Seed Sample Document 1: Government Notice
            val doc1 = DocumentEntity(
                id = "doc_govt_001",
                title = "Govt Student Support Scheme Notice",
                category = "Government",
                originalText = "Government Student Support Program 2026-27.\nApplicants enrolled in higher secondary education are eligible for a direct tuition grant up to ₹50,000. Required: Income certificate, previous mark sheet, bank passbook copy. Final application submission deadline is 30 June 2026. Incomplete forms will be rejected.",
                summary = "Official notice regarding the Student Support Scheme offering tuition grants up to ₹50,000 for eligible students. Requires income certificate and academic records.",
                detectedLanguage = "English",
                importantFactsJson = "[\"Grant amount up to ₹50,000\", \"Applicable for higher secondary students\", \"Direct bank transfer\"]",
                datesJson = "[\"Final submission deadline: 30 June 2026\", \"Eligibility assessment: 15 July 2026\"]",
                requirementsJson = "[\"Income certificate issued after Jan 2026\", \"Previous academic mark sheet with min 60%\", \"Active bank account passbook copy\"]",
                warningsJson = "[\"Incomplete submissions will be disqualified without notice\"]",
                uncertaintiesJson = "[\"Verify whether regional state students require additional domicile certificate\"]",
                createdAt = System.currentTimeMillis() - 7200000,
                isFavorite = true,
                fileSizeFormatted = "245 KB"
            )

            // Seed Sample Document 2: Electricity Utility Bill
            val doc2 = DocumentEntity(
                id = "doc_bill_002",
                title = "Residential Electricity Bill - April",
                category = "Bills",
                originalText = "Power Distribution Corp.\nCustomer ID: 9482-1029-44\nBilling Period: 01 Mar - 31 Mar 2026\nDue Date: 15 May 2026\nNet Amount Payable: ₹1,840.00\nLate Payment Surcharge: ₹150 after due date.",
                summary = "Monthly electricity invoice for billing period March 2026 with total payable amount of ₹1,840 due on 15 May 2026.",
                detectedLanguage = "English",
                importantFactsJson = "[\"Total due: ₹1,840.00\", \"Customer ID: 9482-1029-44\", \"Units consumed: 210 kWh\"]",
                datesJson = "[\"Due date: 15 May 2026\", \"Meter reading: 31 Mar 2026\"]",
                requirementsJson = "[\"Payment via digital portal or nearest branch cash counter\"]",
                warningsJson = "[\"Late payment surcharge ₹150 applies after 15 May 2026\"]",
                uncertaintiesJson = "[]",
                createdAt = System.currentTimeMillis() - 86400000,
                isFavorite = false,
                fileSizeFormatted = "118 KB"
            )

            database.documentDao().insertDocument(doc1)
            database.documentDao().insertDocument(doc2)

            // Seed Action Cards
            val card1 = ActionCardEntity(
                id = "card_001",
                documentId = "doc_govt_001",
                title = "Submit required documents for Student Scheme",
                description = "Locate and prepare your income certificate, academic mark sheet, and bank passbook.",
                priority = "High",
                dueDate = "30 Jun 2026",
                isCompleted = false,
                checklistJson = "[\"Obtain fresh income certificate from local revenue officer\", \"Attest copy of Class 12 mark sheet\", \"Ensure Aadhaar is linked to bank account\"]",
                safeNextStep = "Visit district portal at scholarships.gov.in or nearest citizen center",
                reminderScheduled = true
            )

            val card2 = ActionCardEntity(
                id = "card_002",
                documentId = "doc_govt_001",
                title = "Check eligibility criteria details",
                description = "Confirm minimum percentage requirement and family annual income cap.",
                priority = "Medium",
                dueDate = "10 Jul 2026",
                isCompleted = false,
                checklistJson = "[\"Review section 4.2 of official notification\", \"Confirm accredited institution status\"]",
                safeNextStep = "Check FAQ section on student support portal",
                reminderScheduled = false
            )

            val card3 = ActionCardEntity(
                id = "card_003",
                documentId = "doc_bill_002",
                title = "Pay electricity bill before late penalty",
                description = "Pay ₹1,840 before 15 May 2026 to avoid ₹150 late surcharge.",
                priority = "Medium",
                dueDate = "15 May 2026",
                isCompleted = false,
                checklistJson = "[\"Verify consumer number 9482-1029-44\", \"Execute UPI or card payment\"]",
                safeNextStep = "Open official electricity board payment gateway",
                reminderScheduled = true
            )

            database.actionCardDao().insertCards(listOf(card1, card2, card3))

            // Seed Sample Safety Check
            val check1 = SafetyCheckEntity(
                id = "check_001",
                checkType = "Message",
                inputContent = "You have won a lottery prize of ₹5,00,000! Claim now by clicking this link: https://bit.ly/win-prize-urgent immediately or forfeit.",
                riskLevel = "High Risk",
                score = 92,
                indicatorsJson = "[\"Urgency language detected ('immediately or forfeit')\", \"Shortened suspicious link (bit.ly)\", \"Unsolicited financial prize claim\", \"No legitimate company identification\"]",
                safeStepsJson = "[\"Do NOT click the link or provide any bank credentials\", \"Block and report the sender number\", \"Remember: Legitimate lotteries never ask for upfront payment\"]",
                explanation = "This message demonstrates classic advance-fee prize fraud indicators including artificial urgency and deceptive links."
            )
            database.safetyCheckDao().insertCheck(check1)

            // Seed Unique Aesthetic Note Recalls
            val recall1 = NoteRecallEntity(
                id = "recall_001",
                title = "⚡ Electricity Rebate Expiring",
                snippet = "Claim your ₹350 rebate before 5:00 PM today by submitting bill ref #9482-1029.",
                category = "Deadline",
                scheduledTimeFormatted = "Today, 5:00 PM",
                isTriggered = true,
                isDone = false,
                accentColorHex = "#EBD368", // PastelWheat
                audioNoteText = "Recall notice: Your early payment electricity rebate of 350 rupees expires at 5 PM today. Consumer bill reference is 9482-1029."
            )
            val recall2 = NoteRecallEntity(
                id = "recall_002",
                title = "💊 Antibiotic Course - Dose 4",
                snippet = "Take 500mg Amoxicillin with water after your evening meal.",
                category = "Prescription",
                scheduledTimeFormatted = "Tonight, 8:30 PM",
                isTriggered = true,
                isDone = false,
                accentColorHex = "#EBDBD3", // Hudson
                audioNoteText = "Prescription reminder: Take your 500 milligram Amoxicillin dose with plenty of water after dinner."
            )
            val recall3 = NoteRecallEntity(
                id = "recall_003",
                title = "📑 Student Grant Domicile Certificate",
                snippet = "Upload gazetted officer signed certificate to scholarship portal.",
                category = "Notice",
                scheduledTimeFormatted = "Tomorrow, 10:00 AM",
                isTriggered = false,
                isDone = false,
                accentColorHex = "#B9C7E0", // Penna
                audioNoteText = "Important notice: Ensure your signed domicile certificate is uploaded to the student scholarship portal."
            )
            database.noteRecallDao().insertRecalls(listOf(recall1, recall2, recall3))

            // Seed Initial Chatbot Welcome Message
            val initialWelcome = ChatMessageEntity(
                id = "welcome_msg_001",
                text = "Hello! I am your LIFEBridge Assistant. I can help you decode complex documents, verify potential scams, translate conversations live, or organize your emergency kits. How can I help you today?",
                sender = "ASSISTANT",
                timestamp = System.currentTimeMillis()
            )
            database.chatMessageDao().insertMessage(initialWelcome)
        }
    }
}
