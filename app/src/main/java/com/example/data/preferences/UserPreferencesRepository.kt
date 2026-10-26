package com.example.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "lifebridge_prefs")

class UserPreferencesRepository(private val context: Context) {

    companion object {
        val KEY_LANGUAGE = stringPreferencesKey("pref_language")
        val KEY_THEME_MODE = stringPreferencesKey("pref_theme_mode")
        val KEY_OFFLINE_MODE = booleanPreferencesKey("pref_offline_mode")
        val KEY_NOTIFICATIONS = booleanPreferencesKey("pref_notifications")
        val KEY_VOICE_SPEED = floatPreferencesKey("pref_voice_speed")
        val KEY_DISPLAY_NAME = stringPreferencesKey("pref_display_name")
        val KEY_EMAIL = stringPreferencesKey("pref_email")
        val KEY_ONBOARDED = booleanPreferencesKey("pref_onboarded")
        val KEY_AUTH_TOKEN = stringPreferencesKey("pref_auth_token")
    }

    val languageFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_LANGUAGE] ?: "en"
    }

    val themeModeFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_THEME_MODE] ?: "light"
    }

    val isOfflineModeFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_OFFLINE_MODE] ?: false
    }

    val notificationsEnabledFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_NOTIFICATIONS] ?: true
    }

    val voiceSpeedFlow: Flow<Float> = context.dataStore.data.map { prefs ->
        prefs[KEY_VOICE_SPEED] ?: 1.0f
    }

    val displayNameFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_DISPLAY_NAME] ?: "Alex Kumar"
    }

    val emailFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_EMAIL] ?: "alex@example.com"
    }

    val isOnboardedFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_ONBOARDED] ?: false
    }

    val authTokenFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_AUTH_TOKEN]
    }

    suspend fun setLanguage(lang: String) {
        context.dataStore.edit { prefs -> prefs[KEY_LANGUAGE] = lang }
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { prefs -> prefs[KEY_THEME_MODE] = mode }
    }

    suspend fun setOfflineMode(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[KEY_OFFLINE_MODE] = enabled }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[KEY_NOTIFICATIONS] = enabled }
    }

    suspend fun setVoiceSpeed(speed: Float) {
        context.dataStore.edit { prefs -> prefs[KEY_VOICE_SPEED] = speed }
    }

    suspend fun setUserProfile(name: String, email: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_DISPLAY_NAME] = name
            prefs[KEY_EMAIL] = email
        }
    }

    suspend fun setOnboarded(onboarded: Boolean) {
        context.dataStore.edit { prefs -> prefs[KEY_ONBOARDED] = onboarded }
    }

    suspend fun setAuthToken(token: String?) {
        context.dataStore.edit { prefs ->
            if (token != null) prefs[KEY_AUTH_TOKEN] = token else prefs.remove(KEY_AUTH_TOKEN)
        }
    }
}
