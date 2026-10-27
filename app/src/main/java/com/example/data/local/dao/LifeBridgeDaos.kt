package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entities.ActionCardEntity
import com.example.data.local.entities.DocumentEntity
import com.example.data.local.entities.EmergencyContactEntity
import com.example.data.local.entities.SafetyCheckEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentDao {
    @Query("SELECT * FROM documents ORDER BY createdAt DESC")
    fun getAllDocuments(): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents WHERE id = :id LIMIT 1")
    suspend fun getDocumentById(id: String): DocumentEntity?

    @Query("SELECT * FROM documents WHERE title LIKE '%' || :query || '%' OR summary LIKE '%' || :query || '%' OR originalText LIKE '%' || :query || '%'")
    fun searchDocuments(query: String): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents ORDER BY createdAt DESC LIMIT 8")
    suspend fun getRecentDocumentsSync(): List<DocumentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(doc: DocumentEntity)

    @Update
    suspend fun updateDocument(doc: DocumentEntity)

    @Delete
    suspend fun deleteDocument(doc: DocumentEntity)

    @Query("DELETE FROM documents WHERE id = :id")
    suspend fun deleteDocumentById(id: String)

    @Query("DELETE FROM documents")
    suspend fun clearAll()
}

@Dao
interface ActionCardDao {
    @Query("SELECT * FROM action_cards ORDER BY createdAt DESC")
    fun getAllCards(): Flow<List<ActionCardEntity>>

    @Query("SELECT * FROM action_cards WHERE documentId = :docId")
    fun getCardsForDocument(docId: String): Flow<List<ActionCardEntity>>

    @Query("SELECT * FROM action_cards WHERE isCompleted = 0 ORDER BY createdAt DESC LIMIT 8")
    suspend fun getPendingCardsSync(): List<ActionCardEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: ActionCardEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCards(cards: List<ActionCardEntity>)

    @Update
    suspend fun updateCard(card: ActionCardEntity)

    @Query("UPDATE action_cards SET isCompleted = :completed WHERE id = :id")
    suspend fun setCompleted(id: String, completed: Boolean)

    @Query("UPDATE action_cards SET reminderScheduled = :scheduled WHERE id = :id")
    suspend fun setReminder(id: String, scheduled: Boolean)

    @Delete
    suspend fun deleteCard(card: ActionCardEntity)

    @Query("DELETE FROM action_cards WHERE id = :id")
    suspend fun deleteCardById(id: String)

    @Query("DELETE FROM action_cards")
    suspend fun clearAll()
}

@Dao
interface SafetyCheckDao {
    @Query("SELECT * FROM safety_checks ORDER BY timestamp DESC")
    fun getAllChecks(): Flow<List<SafetyCheckEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheck(check: SafetyCheckEntity)

    @Query("DELETE FROM safety_checks")
    suspend fun clearAll()
}

@Dao
interface EmergencyContactDao {
    @Query("SELECT * FROM emergency_contacts ORDER BY id ASC")
    fun getAllEmergencyContacts(): Flow<List<EmergencyContactEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContacts(contacts: List<EmergencyContactEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: EmergencyContactEntity)
}

@Dao
interface NoteRecallDao {
    @Query("SELECT * FROM note_recalls ORDER BY createdAt DESC")
    fun getAllRecalls(): Flow<List<com.example.data.local.entities.NoteRecallEntity>>

    @Query("SELECT * FROM note_recalls WHERE isTriggered = 1 AND isDone = 0 ORDER BY createdAt DESC")
    fun getActiveRecalls(): Flow<List<com.example.data.local.entities.NoteRecallEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecall(recall: com.example.data.local.entities.NoteRecallEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecalls(recalls: List<com.example.data.local.entities.NoteRecallEntity>)

    @Update
    suspend fun updateRecall(recall: com.example.data.local.entities.NoteRecallEntity)

    @Query("UPDATE note_recalls SET isDone = 1, isTriggered = 0 WHERE id = :id")
    suspend fun markDone(id: String)

    @Query("UPDATE note_recalls SET scheduledTimeFormatted = :newTime, isTriggered = 0 WHERE id = :id")
    suspend fun snooze(id: String, newTime: String)

    @Query("DELETE FROM note_recalls WHERE id = :id")
    suspend fun deleteRecallById(id: String)

    @Query("DELETE FROM note_recalls")
    suspend fun clearAll()
}
