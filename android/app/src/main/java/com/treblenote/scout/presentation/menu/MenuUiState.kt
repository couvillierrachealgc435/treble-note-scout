package com.treblenote.scout.presentation.menu

import com.treblenote.scout.R

data class MenuUiState(
    val trailId: Int = 1,
    val trailTitleRes: Int = R.string.trail_1_title,
    val progressFraction: Float = 0f,
    val accuracyPercent: Int = 0,
    val bestStreak: Int = 0,
    val hasHistory: Boolean = false,
    val weakNotes: String = ""
) {
    val showStats: Boolean = accuracyPercent > 0 && bestStreak > 0
    val showWeakNotes: Boolean = weakNotes.isNotEmpty()
}
