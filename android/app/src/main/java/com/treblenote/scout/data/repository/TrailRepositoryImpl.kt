package com.treblenote.scout.data.repository

import com.treblenote.scout.data.sample.SampleData
import com.treblenote.scout.domain.model.MemoryPhrase
import com.treblenote.scout.domain.model.Note
import com.treblenote.scout.domain.model.Trail
import com.treblenote.scout.domain.repository.TrailRepository

class TrailRepositoryImpl : TrailRepository {

    override fun trails(): List<Trail> = SampleData.trails

    override fun trailById(trailId: Int): Trail = SampleData.trailById(trailId)

    override fun memoryPhrases(): List<MemoryPhrase> = SampleData.memoryPhrases

    override fun trebleReference(): List<Note> = SampleData.trebleReference

    override fun bassReference(): List<Note> = SampleData.bassReference
}
