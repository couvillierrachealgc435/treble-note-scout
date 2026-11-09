package com.treblenote.scout.domain.repository

import com.treblenote.scout.domain.model.RoundResult

interface ProgressRepository {
    fun currentTrailId(): Int
    fun setCurrentTrail(trailId: Int)
    fun bestStreak(): Int
    fun trailAccuracy(trailId: Int): Int
    fun isTrailDone(trailId: Int): Boolean
    fun missCounts(): Map<String, Int>
    fun recordMiss(noteId: String)
    fun totalAsked(): Int
    fun totalCorrect(): Int
    fun saveRound(result: RoundResult)
}
