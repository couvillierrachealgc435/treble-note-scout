package com.treblenote.scout.domain.model

enum class Accidental {
    NATURAL,
    SHARP
}

data class Note(
    val midi: Int,
    val letter: Char,
    val octave: Int,
    val accidental: Accidental
) {
    val id: String = if (accidental == Accidental.SHARP) "$letter${octave}s" else "$letter$octave"

    val label: String = if (accidental == Accidental.SHARP) "$letter♯$octave" else "$letter$octave"

    val diatonicIndex: Int = octave * 7 + DIATONIC_ORDER.indexOf(letter)

    companion object {
        val DIATONIC_ORDER = listOf('C', 'D', 'E', 'F', 'G', 'A', 'B')

        private val PITCH_LETTERS = charArrayOf('C', 'C', 'D', 'D', 'E', 'F', 'F', 'G', 'G', 'A', 'A', 'B')

        private val PITCH_SHARPS = booleanArrayOf(
            false, true, false, true, false, false, true, false, true, false, true, false
        )

        fun fromMidi(midi: Int): Note {
            val pitchClass = ((midi % 12) + 12) % 12
            val octave = midi / 12 - 1
            val accidental = if (PITCH_SHARPS[pitchClass]) Accidental.SHARP else Accidental.NATURAL
            return Note(midi, PITCH_LETTERS[pitchClass], octave, accidental)
        }
    }
}
