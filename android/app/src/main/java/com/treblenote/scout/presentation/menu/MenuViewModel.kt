package com.treblenote.scout.presentation.menu

import androidx.lifecycle.ViewModel
import com.treblenote.scout.domain.model.Note
import com.treblenote.scout.domain.repository.ProgressRepository
import com.treblenote.scout.domain.repository.TrailRepository
import com.treblenote.scout.domain.usecase.UnlockTrailsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MenuViewModel(
    private val progress: ProgressRepository,
    private val trails: TrailRepository,
    private val unlockTrails: UnlockTrailsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MenuUiState())
    val state: StateFlow<MenuUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        val catalog = trails.trails()
        val storedId = progress.currentTrailId()
        val trailId = if (catalog.any { it.id == storedId }) {
            storedId
        } else {
            unlockTrails.firstPlayable(catalog)
        }
        val trail = trails.trailById(trailId)
        val asked = progress.totalAsked()
        val accuracy = if (asked == 0) 0 else progress.totalCorrect() * 100 / asked
        val done = unlockTrails(catalog).count { status -> progress.isTrailDone(status.trail.id) }

        _state.update {
            it.copy(
                trailId = trail.id,
                trailTitleRes = trail.titleRes,
                progressFraction = done.toFloat() / catalog.size,
                accuracyPercent = accuracy,
                bestStreak = progress.bestStreak(),
                hasHistory = asked > 0,
                weakNotes = weakNoteLabels()
            )
        }
    }

    private fun weakNoteLabels(): String = progress.missCounts()
        .entries
        .sortedByDescending { it.value }
        .take(2)
        .mapNotNull { entry -> noteLabel(entry.key) }
        .joinToString(separator = " · ")

    private fun noteLabel(noteId: String): String? {
        val letter = noteId.firstOrNull() ?: return null
        val octave = noteId.getOrNull(1)?.digitToIntOrNull() ?: return null
        val sharp = noteId.length > 2
        val base = Note.DIATONIC_ORDER.indexOf(letter)
        if (base < 0) return null
        return if (sharp) "$letter♯$octave" else "$letter$octave"
    }
}
