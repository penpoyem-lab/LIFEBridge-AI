package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entities.AuditLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AuditLogDao {
    @Query("SELECT * FROM security_audit_logs ORDER BY timestamp DESC LIMIT 100")
    fun getAllLogs(): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: AuditLogEntity)

    @Query("DELETE FROM security_audit_logs")
    suspend fun clearAllLogs()

    @Query("SELECT COUNT(*) FROM security_audit_logs WHERE severity = 'CRITICAL'")
    suspend fun getCriticalEventCount(): Int
}
