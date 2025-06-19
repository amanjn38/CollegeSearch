package com.bera.josaahelpertool.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val themeDataStore: ThemeDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(ThemeUiState())
    val uiState: StateFlow<ThemeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                themeDataStore.themeMode,
                themeDataStore.dynamicColor
            ) { themeMode, dynamicColor ->
                ThemeUiState(
                    themeMode = themeMode,
                    dynamicColor = dynamicColor
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun setThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            themeDataStore.setThemeMode(themeMode)
        }
    }

    fun toggleTheme() {
        val currentMode = _uiState.value.themeMode
        val newMode = when (currentMode) {
            ThemeMode.LIGHT -> ThemeMode.DARK
            ThemeMode.DARK -> ThemeMode.LIGHT
            ThemeMode.SYSTEM -> ThemeMode.DARK // If system, switch to dark
        }
        setThemeMode(newMode)
    }

    fun setDynamicColor(enabled: Boolean) {
        viewModelScope.launch {
            themeDataStore.setDynamicColor(enabled)
        }
    }

    @Composable
    fun shouldUseDarkTheme(): Boolean {
        val uiState by uiState.collectAsState()
        return when (uiState.themeMode) {
            ThemeMode.LIGHT -> false
            ThemeMode.DARK -> true
            ThemeMode.SYSTEM -> isSystemInDarkTheme()
        }
    }
}

data class ThemeUiState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val dynamicColor: Boolean = false
) 