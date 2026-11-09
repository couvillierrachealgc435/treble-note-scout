package com.treblenote.scout.domain.repository

import com.treblenote.scout.domain.model.MemoryPhrase
import com.treblenote.scout.domain.model.Note
import com.treblenote.scout.domain.model.Trail

interface TrailRepository {
    fun trails(): List<Trail>
    fun trailById(trailId: Int): Trail
    fun memoryPhrases(): List<MemoryPhrase>
    fun trebleReference(): List<Note>
    fun bassReference(): List<Note>
}
