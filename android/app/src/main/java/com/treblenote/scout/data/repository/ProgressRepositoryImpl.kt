package com.treblenote.scout.data.repository

import com.treblenote.scout.core.config.GameConfig
import com.treblenote.scout.data.local.ScoutPreferences
import com.treblenote.scout.domain.model.RoundResult
import com.treblenote.scout.domain.repository.ProgressRepository

class ProgressRepositoryImpl(private val prefs: ScoutPreferences) : ProgressRepository {

    override fun currentTrailId(): Int = prefs.readInt(KEY_CURRENT_TRAIL, 1)

    override fun setCurrentTrail(trailId: Int) {
        prefs.writeInt(KEY_CURRENT_TRAIL, trailId)
    }

    override fun bestStreak(): Int = prefs.readInt(KEY_BEST_STREAK, 0)

    override fun trailAccuracy(trailId: Int): Int = prefs.readInt(PREFIX_ACCURACY + trailId, 0)

    override fun isTrailDone(trailId: Int): Boolean = prefs.readBoolean(PREFIX_DONE + trailId, false)

    override fun missCounts(): Map<String, Int> = prefs.intsWithPrefix(PREFIX_MISS)

    override fun recordMiss(noteId: String) {
        val key = PREFIX_MISS + noteId
        prefs.writeInt(key, prefs.readInt(key, 0) + 1)
    }

    override fun totalAsked(): Int = prefs.readInt(KEY_TOTAL_ASKED, 0)

    override fun totalCorrect(): Int = prefs.readInt(KEY_TOTAL_CORRECT, 0)

    override fun saveRound(result: RoundResult) {
        if (result.bestStreak > bestStreak()) {
            prefs.writeInt(KEY_BEST_STREAK, result.bestStreak)
        }
        if (result.accuracy > trailAccuracy(result.trailId)) {
            prefs.writeInt(PREFIX_ACCURACY + result.trailId, result.accuracy)
        }
        if (result.accuracy >= GameConfig.UNLOCK_ACCURACY) {
            prefs.writeBoolean(PREFIX_DONE + result.trailId, true)
        }
        result.missedNotes.forEach { recordMiss(it.id) }
        prefs.writeInt(KEY_TOTAL_ASKED, totalAsked() + result.asked)
        prefs.writeInt(KEY_TOTAL_CORRECT, totalCorrect() + result.correct)
    }

    private companion object {
        const val KEY_CURRENT_TRAIL = "current_trail"
        const val KEY_BEST_STREAK = "best_streak"
        const val KEY_TOTAL_ASKED = "total_asked"
        const val KEY_TOTAL_CORRECT = "total_correct"
        const val PREFIX_ACCURACY = "trail_accuracy_"
        const val PREFIX_DONE = "trail_done_"
        const val PREFIX_MISS = "miss_"
    }
}
