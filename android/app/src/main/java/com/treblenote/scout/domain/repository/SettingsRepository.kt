package com.treblenote.scout.domain.repository

import com.treblenote.scout.domain.model.ScoutSettings

interface SettingsRepository {
    fun load(): ScoutSettings
    fun save(settings: ScoutSettings)
}
