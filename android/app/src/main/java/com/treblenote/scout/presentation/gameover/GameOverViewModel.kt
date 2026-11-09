package com.treblenote.scout.presentation.gameover

import androidx.lifecycle.ViewModel
import com.treblenote.scout.core.config.GameConfig
import com.treblenote.scout.domain.model.RoundResult
import com.treblenote.scout.domain.repository.ProgressRepository
import com.treblenote.scout.domain.repository.TrailRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameOverViewModel(
    private val progress: ProgressRepository,
    private val trails: TrailRepository
) : ViewModel() {

    private val _state = MutableStateFlow(GameOverUiState())
    val state: StateFlow<GameOverUiState> = _state.asStateFlow()

    fun present(result: RoundResult) {
        val seconds = (result.elapsedMs / 1000L).toInt()
        _state.value = GameOverUiState(
            asked = result.asked,
            correct = result.correct,
            accuracy = result.accuracy,
            score = result.score,
            bestStreak = result.bestStreak,
            overallBest = progress.bestStreak(),
            elapsedMinutes = seconds / 60,
            elapsedSeconds = seconds % 60,
            isWin = result.accuracy >= GameConfig.UNLOCK_ACCURACY,
            isNewBest = result.isNewBest,
            missedNotes = result.missedNotes.take(MAX_MISSED_SHOWN),
            clef = trails.trailById(result.trailId).clef
        )
    }

    private companion object {
        const val MAX_MISSED_SHOWN = 3
    }
}
