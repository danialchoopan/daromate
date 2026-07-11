package ir.danialchoopan.medimate.presentation.viewmodel

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode

    private val _notificationsEnabled = MutableStateFlow(true)
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled

    private val _alarmsEnabled = MutableStateFlow(true)
    val alarmsEnabled: StateFlow<Boolean> = _alarmsEnabled

    init {
        viewModelScope.launch {
            context.dataStore.data.map { preferences ->
                preferences[DARK_MODE_KEY] ?: false
            }.collect { isDark ->
                _isDarkMode.value = isDark
            }
        }
        viewModelScope.launch {
            context.dataStore.data.map { preferences ->
                preferences[NOTIFICATIONS_KEY] ?: true
            }.collect { enabled ->
                _notificationsEnabled.value = enabled
            }
        }
        viewModelScope.launch {
            context.dataStore.data.map { preferences ->
                preferences[ALARMS_KEY] ?: true
            }.collect { enabled ->
                _alarmsEnabled.value = enabled
            }
        }
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            context.dataStore.edit { preferences ->
                preferences[DARK_MODE_KEY] = enabled
            }
            _isDarkMode.value = enabled
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            context.dataStore.edit { preferences ->
                preferences[NOTIFICATIONS_KEY] = enabled
            }
            _notificationsEnabled.value = enabled
        }
    }

    fun setAlarmsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            context.dataStore.edit { preferences ->
                preferences[ALARMS_KEY] = enabled
            }
            _alarmsEnabled.value = enabled
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            context.dataStore.edit { it.clear() }
            _isDarkMode.value = false
            _notificationsEnabled.value = true
            _alarmsEnabled.value = true
        }
    }

    companion object {
        val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
        val NOTIFICATIONS_KEY = booleanPreferencesKey("notifications_enabled")
        val ALARMS_KEY = booleanPreferencesKey("alarms_enabled")
    }
}
