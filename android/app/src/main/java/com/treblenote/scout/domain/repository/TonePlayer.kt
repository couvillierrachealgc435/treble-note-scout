package com.treblenote.scout.domain.repository

interface TonePlayer {
    fun play(midi: Int)
    fun stop()
}
