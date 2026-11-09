package com.treblenote.scout.presentation.noteguide

import androidx.lifecycle.ViewModel
import com.treblenote.scout.domain.model.MemoryPhrase
import com.treblenote.scout.domain.model.Note
import com.treblenote.scout.domain.repository.TrailRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class NoteGuideUiState(
    val trebleNotes: List<Note> = emptyList(),
    val bassNotes: List<Note> = emptyList(),
    val phrases: List<MemoryPhrase> = emptyList()
)

class NoteGuideViewModel(private val trails: TrailRepository) : ViewModel() {

    private val _state = MutableStateFlow(NoteGuideUiState())
    val state: StateFlow<NoteGuideUiState> = _state.asStateFlow()

    init {
        _state.value = NoteGuideUiState(
            trebleNotes = trails.trebleReference(),
            bassNotes = trails.bassReference(),
            phrases = trails.memoryPhrases()
        )
    }
}
