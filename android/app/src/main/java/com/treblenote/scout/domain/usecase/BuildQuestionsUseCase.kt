package com.treblenote.scout.domain.usecase

import com.treblenote.scout.domain.model.Note
import com.treblenote.scout.domain.model.Trail
import com.treblenote.scout.domain.repository.ProgressRepository

class BuildQuestionsUseCase(private val progress: ProgressRepository) {

    operator fun invoke(trail: Trail, count: Int, weakNotesOnly: Boolean): List<Note> {
        val misses = progress.missCounts()
        val pool = if (weakNotesOnly) {
            trail.notes.filter { (misses[it.id] ?: 0) > 0 }.ifEmpty { trail.notes }
        } else {
            trail.notes
        }
        if (pool.isEmpty()) return emptyList()

        val weighted = mutableListOf<Note>()
        pool.forEach { note ->
            val weight = 1 + minOf(misses[note.id] ?: 0, MAX_BIAS)
            repeat(weight) { weighted.add(note) }
        }

        val questions = mutableListOf<Note>()
        var previousId: String? = null
        var guard = 0
        while (questions.size < count && guard < count * GUARD_FACTOR) {
            guard++
            val candidate = weighted.random()
            if (pool.size > 1 && candidate.id == previousId) continue
            questions.add(candidate)
            previousId = candidate.id
        }
        while (questions.size < count) {
            questions.add(pool[questions.size % pool.size])
        }
        return questions
    }

    private companion object {
        const val MAX_BIAS = 3
        const val GUARD_FACTOR = 24
    }
}
