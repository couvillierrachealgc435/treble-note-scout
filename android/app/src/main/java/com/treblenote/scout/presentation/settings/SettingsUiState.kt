package com.treblenote.scout.presentation.settings

import com.treblenote.scout.domain.model.AnswerWindow
import com.treblenote.scout.domain.model.ScoutSettings

data class SettingsUiState(
    val settings: ScoutSettings = ScoutSettings(),
    val windows: List<AnswerWindow> = listOf(
        AnswerWindow.RELAXED,
        AnswerWindow.STEADY,
        AnswerWindow.BRISK
    )
)
