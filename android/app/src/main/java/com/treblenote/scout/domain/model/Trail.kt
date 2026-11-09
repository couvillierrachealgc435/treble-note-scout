package com.treblenote.scout.domain.model

data class Trail(
    val id: Int,
    val titleRes: Int,
    val rangeRes: Int,
    val clef: Clef,
    val runMode: RunMode,
    val midiNotes: List<Int>,
    val keyboardLowMidi: Int
) {
    val notes: List<Note> = midiNotes.map { Note.fromMidi(it) }
}

enum class TrailState {
    DONE,
    OPEN,
    LOCKED
}

data class TrailStatus(
    val trail: Trail,
    val state: TrailState,
    val accuracy: Int
) {
    val selectable: Boolean = state != TrailState.LOCKED
}
