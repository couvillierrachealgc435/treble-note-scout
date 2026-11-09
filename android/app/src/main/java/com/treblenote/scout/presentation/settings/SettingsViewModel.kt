package com.treblenote.scout.presentation.settings

import androidx.lifecycle.ViewModel
import com.treblenote.scout.domain.model.AnswerWindow
import com.treblenote.scout.domain.model.ScoutSettings
import com.treblenote.scout.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel(private val settingsRepository: SettingsRepository) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    init {
        _state.value = SettingsUiState(settings = settingsRepository.load())
    }

    fun setTimer(enabled: Boolean) {
        mutate { current -> current.copy(timerEnabled = enabled) }
    }

    fun setTone(enabled: Boolean) {
        mutate { current -> current.copy(toneEnabled = enabled) }
    }

    fun setLabels(enabled: Boolean) {
        mutate { current -> current.copy(labelsEnabled = enabled) }
    }

    fun setHaptics(enabled: Boolean) {
        mutate { current -> current.copy(hapticsEnabled = enabled) }
    }

    fun setAnswerWindow(window: AnswerWindow) {
        mutate { current -> current.copy(answerWindow = window) }
    }

    private fun mutate(transform: (ScoutSettings) -> ScoutSettings) {
        val updated = transform(_state.value.settings)
        settingsRepository.save(updated)
        _state.value = _state.value.copy(settings = updated)
    }
}
