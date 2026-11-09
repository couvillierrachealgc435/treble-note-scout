package com.treblenote.scout.presentation.trailmap

import androidx.lifecycle.ViewModel
import com.treblenote.scout.domain.model.TrailState
import com.treblenote.scout.domain.repository.ProgressRepository
import com.treblenote.scout.domain.repository.TrailRepository
import com.treblenote.scout.domain.usecase.UnlockTrailsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TrailMapViewModel(
    private val progress: ProgressRepository,
    private val trails: TrailRepository,
    private val unlockTrails: UnlockTrailsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(TrailMapUiState())
    val state: StateFlow<TrailMapUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        val entries = unlockTrails(trails.trails())
        val stored = progress.currentTrailId()
        val selected = entries.firstOrNull { it.trail.id == stored && it.selectable }?.trail?.id
            ?: entries.firstOrNull { it.state == TrailState.OPEN }?.trail?.id
            ?: entries.firstOrNull { it.selectable }?.trail?.id
        _state.update { it.copy(entries = entries, selectedTrailId = selected) }
    }

    fun select(trailId: Int) {
        val entry = _state.value.entries.firstOrNull { it.trail.id == trailId } ?: return
        if (!entry.selectable) return
        _state.update { it.copy(selectedTrailId = trailId) }
    }

    fun confirmSelection(): Int? {
        val trailId = _state.value.selectedTrailId ?: return null
        progress.setCurrentTrail(trailId)
        return trailId
    }
}
