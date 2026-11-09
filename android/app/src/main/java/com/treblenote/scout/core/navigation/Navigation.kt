package com.treblenote.scout.core.navigation

import com.treblenote.scout.domain.model.RoundResult

sealed interface Screen {

    data object Splash : Screen

    data object Menu : Screen

    data object TrailMap : Screen

    data object NoteGuide : Screen

    data object Settings : Screen

    data class Game(
        val trailId: Int,
        val weakNotesOnly: Boolean,
        val runId: Long
    ) : Screen

    data class GameOver(val result: RoundResult) : Screen
}
