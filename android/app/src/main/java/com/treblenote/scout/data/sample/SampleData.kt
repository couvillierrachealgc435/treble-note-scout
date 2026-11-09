package com.treblenote.scout.data.sample

import com.treblenote.scout.R
import com.treblenote.scout.domain.model.Clef
import com.treblenote.scout.domain.model.MemoryPhrase
import com.treblenote.scout.domain.model.Note
import com.treblenote.scout.domain.model.RunMode
import com.treblenote.scout.domain.model.Trail

object SampleData {

    val trails: List<Trail> = listOf(
        Trail(
            id = 1,
            titleRes = R.string.trail_1_title,
            rangeRes = R.string.trail_1_range,
            clef = Clef.TREBLE,
            runMode = RunMode.LETTERS,
            midiNotes = listOf(60, 62, 64, 65, 67),
            keyboardLowMidi = 60
        ),
        Trail(
            id = 2,
            titleRes = R.string.trail_2_title,
            rangeRes = R.string.trail_2_range,
            clef = Clef.TREBLE,
            runMode = RunMode.LETTERS,
            midiNotes = listOf(64, 67, 71, 74, 77),
            keyboardLowMidi = 60
        ),
        Trail(
            id = 3,
            titleRes = R.string.trail_3_title,
            rangeRes = R.string.trail_3_range,
            clef = Clef.TREBLE,
            runMode = RunMode.LETTERS,
            midiNotes = listOf(65, 69, 72, 76),
            keyboardLowMidi = 60
        ),
        Trail(
            id = 4,
            titleRes = R.string.trail_4_title,
            rangeRes = R.string.trail_4_range,
            clef = Clef.TREBLE,
            runMode = RunMode.LETTERS,
            midiNotes = listOf(57, 59, 60, 81, 83, 84),
            keyboardLowMidi = 60
        ),
        Trail(
            id = 5,
            titleRes = R.string.trail_5_title,
            rangeRes = R.string.trail_5_range,
            clef = Clef.BASS,
            runMode = RunMode.KEYBOARD,
            midiNotes = listOf(48, 50, 52, 53, 55, 57, 59),
            keyboardLowMidi = 48
        ),
        Trail(
            id = 6,
            titleRes = R.string.trail_6_title,
            rangeRes = R.string.trail_6_range,
            clef = Clef.TREBLE,
            runMode = RunMode.KEYBOARD,
            midiNotes = listOf(60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71),
            keyboardLowMidi = 60
        )
    )

    val memoryPhrases: List<MemoryPhrase> = listOf(
        MemoryPhrase(R.string.phrase_treble_lines_title, R.string.phrase_treble_lines, "EGBDF"),
        MemoryPhrase(R.string.phrase_treble_spaces_title, R.string.phrase_treble_spaces, "FACE"),
        MemoryPhrase(R.string.phrase_bass_lines_title, R.string.phrase_bass_lines, "GBDFA"),
        MemoryPhrase(R.string.phrase_bass_spaces_title, R.string.phrase_bass_spaces, "ACEG")
    )

    val trebleReference: List<Note> = listOf(64, 65, 67, 69, 71, 72, 74, 76, 77).map { Note.fromMidi(it) }

    val bassReference: List<Note> = listOf(43, 45, 47, 48, 50, 52, 53, 55, 57).map { Note.fromMidi(it) }

    fun trailById(trailId: Int): Trail = trails.firstOrNull { it.id == trailId } ?: trails.first()
}
