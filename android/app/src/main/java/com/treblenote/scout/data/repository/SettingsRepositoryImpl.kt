package com.treblenote.scout.data.repository

import com.treblenote.scout.data.local.ScoutPreferences
import com.treblenote.scout.domain.model.AnswerWindow
import com.treblenote.scout.domain.model.ScoutSettings
import com.treblenote.scout.domain.repository.SettingsRepository

class SettingsRepositoryImpl(private val prefs: ScoutPreferences) : SettingsRepository {

    override fun load(): ScoutSettings {
        val stored = prefs.readString(KEY_WINDOW, AnswerWindow.STEADY.name)
        val window = AnswerWindow.values().firstOrNull { it.name == stored } ?: AnswerWindow.STEADY
        return ScoutSettings(
            timerEnabled = prefs.readBoolean(KEY_TIMER, true),
            toneEnabled = prefs.readBoolean(KEY_TONE, true),
            labelsEnabled = prefs.readBoolean(KEY_LABELS, true),
            hapticsEnabled = prefs.readBoolean(KEY_HAPTICS, true),
            answerWindow = window
        )
    }

    override fun save(settings: ScoutSettings) {
        prefs.writeBoolean(KEY_TIMER, settings.timerEnabled)
        prefs.writeBoolean(KEY_TONE, settings.toneEnabled)
        prefs.writeBoolean(KEY_LABELS, settings.labelsEnabled)
        prefs.writeBoolean(KEY_HAPTICS, settings.hapticsEnabled)
        prefs.writeString(KEY_WINDOW, settings.answerWindow.name)
    }

    private companion object {
        const val KEY_TIMER = "setting_timer"
        const val KEY_TONE = "setting_tone"
        const val KEY_LABELS = "setting_labels"
        const val KEY_HAPTICS = "setting_haptics"
        const val KEY_WINDOW = "setting_answer_window"
    }
}
