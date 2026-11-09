package com.treblenote.scout.domain.model

enum class AnswerWindow(val millis: Long) {
    RELAXED(8000L),
    STEADY(5000L),
    BRISK(3000L)
}

data class ScoutSettings(
    val timerEnabled: Boolean = true,
    val toneEnabled: Boolean = true,
    val labelsEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val answerWindow: AnswerWindow = AnswerWindow.STEADY
)
