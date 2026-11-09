package com.treblenote.scout.presentation.gameover

import com.treblenote.scout.domain.model.Clef
import com.treblenote.scout.domain.model.Note

data class GameOverUiState(
    val asked: Int = 0,
    val correct: Int = 0,
    val accuracy: Int = 0,
    val score: Int = 0,
    val bestStreak: Int = 0,
    val overallBest: Int = 0,
    val elapsedMinutes: Int = 0,
    val elapsedSeconds: Int = 0,
    val isWin: Boolean = false,
    val isNewBest: Boolean = false,
    val missedNotes: List<Note> = emptyList(),
    val clef: Clef = Clef.TREBLE
) {
    val hasMisses: Boolean = missedNotes.isNotEmpty()
    val showStats: Boolean = asked > 0
}
