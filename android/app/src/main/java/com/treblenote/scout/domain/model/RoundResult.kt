package com.treblenote.scout.domain.model

data class RoundResult(
    val trailId: Int,
    val asked: Int,
    val correct: Int,
    val score: Int,
    val bestStreak: Int,
    val elapsedMs: Long,
    val missedNotes: List<Note>,
    val isNewBest: Boolean
) {
    val accuracy: Int = if (asked == 0) 0 else correct * 100 / asked
}
