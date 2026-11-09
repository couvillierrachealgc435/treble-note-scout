package com.treblenote.scout.presentation.game

import com.treblenote.scout.R
import com.treblenote.scout.core.config.GameConfig
import com.treblenote.scout.domain.model.Clef
import com.treblenote.scout.domain.model.Note
import com.treblenote.scout.domain.model.RoundResult
import com.treblenote.scout.domain.model.RunMode

enum class GamePhase {
    IDLE,
    PLAYING,
    REVEALING,
    FINISHED
}

data class GameUiState(
    val trailId: Int = 1,
    val trailTitleRes: Int = R.string.trail_1_title,
    val clef: Clef = Clef.TREBLE,
    val runMode: RunMode = RunMode.LETTERS,
    val keyboardLowMidi: Int = 60,
    val phase: GamePhase = GamePhase.IDLE,
    val questionIndex: Int = 0,
    val totalQuestions: Int = GameConfig.QUESTIONS_PER_TRAIL,
    val currentNote: Note? = null,
    val noteSerial: Int = 0,
    val score: Int = 0,
    val streak: Int = 0,
    val bestStreak: Int = 0,
    val correct: Int = 0,
    val misses: Int = 0,
    val remainingFraction: Float = 1f,
    val timerEnabled: Boolean = true,
    val labelsEnabled: Boolean = true,
    val inputLocked: Boolean = true,
    val lastCorrect: Boolean = false,
    val revealLetter: Char? = null,
    val revealMidi: Int? = null,
    val wrongLetter: Char? = null,
    val wrongMidi: Int? = null,
    val scoutStep: Int = 0,
    val paused: Boolean = false
) {
    val displayIndex: Int = (questionIndex + 1).coerceAtMost(totalQuestions)
    val remainingNotes: Int = (totalQuestions - questionIndex).coerceAtLeast(0)
    val controlsEnabled: Boolean = phase == GamePhase.PLAYING && !inputLocked && !paused
    val visibleStats: Int = listOf(score > 0, streak > 0, remainingNotes > 0).count { it }
}

sealed interface GameEvent {
    data class Finished(val result: RoundResult) : GameEvent
}
