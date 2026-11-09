package com.treblenote.scout.domain.usecase

import com.treblenote.scout.domain.model.Note

class EvaluateAnswerUseCase {

    fun byLetter(note: Note, letter: Char): Boolean = note.letter == letter

    fun byKey(note: Note, midi: Int): Boolean = note.midi == midi
}
