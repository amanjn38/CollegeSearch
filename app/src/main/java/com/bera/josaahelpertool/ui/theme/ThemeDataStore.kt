package com.bera.josaahelpertool.ui.theme

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_theme_preferences")

enum class ThemeMode {
    SYSTEM, LIGHT, DARK
}

@Singleton
class ThemeDataStore @Inject constructor(
    private val context: Context
) {
    private val THEME_MODE_KEY = stringPreferencesKey("app_theme_mode")
    private val DYNAMIC_COLOR_KEY = booleanPreferencesKey("app_dynamic_color")

    val themeMode: Flow<ThemeMode> = context.dataStore.data.map { preferences ->
        try {
            val themeModeString = preferences[THEME_MODE_KEY]
            when (themeModeString) {
                ThemeMode.LIGHT.name -> ThemeMode.LIGHT
                ThemeMode.DARK.name -> ThemeMode.DARK
                else -> ThemeMode.SYSTEM
            }
        } catch (e: Exception) {
            // If there's any error reading the preference, default to SYSTEM
            Log.e("ThemeDataStore", "Error reading theme mode preference", e)
            ThemeMode.SYSTEM
        }
    }

    val dynamicColor: Flow<Boolean> = context.dataStore.data.map { preferences ->
        try {
            preferences[DYNAMIC_COLOR_KEY] ?: false
        } catch (e: Exception) {
            // If there's any error reading the preference, default to false
            Log.e("ThemeDataStore", "Error reading dynamic color preference", e)
            false
        }
    }

    suspend fun setThemeMode(themeMode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = themeMode.name
        }
    }

    suspend fun setDynamicColor(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DYNAMIC_COLOR_KEY] = enabled
        }
    }

    suspend fun clearPreferences() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
} 