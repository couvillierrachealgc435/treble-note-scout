package com.treblenote.scout.domain.usecase

import com.treblenote.scout.core.config.GameConfig
import com.treblenote.scout.domain.model.Trail
import com.treblenote.scout.domain.model.TrailState
import com.treblenote.scout.domain.model.TrailStatus
import com.treblenote.scout.domain.repository.ProgressRepository

class UnlockTrailsUseCase(private val progress: ProgressRepository) {

    operator fun invoke(trails: List<Trail>): List<TrailStatus> =
        trails.mapIndexed { index, trail ->
            val accuracy = progress.trailAccuracy(trail.id)
            val previousAccuracy = if (index == 0) {
                GameConfig.UNLOCK_ACCURACY
            } else {
                progress.trailAccuracy(trails[index - 1].id)
            }
            val state = when {
                progress.isTrailDone(trail.id) -> TrailState.DONE
                previousAccuracy >= GameConfig.UNLOCK_ACCURACY -> TrailState.OPEN
                else -> TrailState.LOCKED
            }
            TrailStatus(trail, state, accuracy)
        }

    fun firstPlayable(trails: List<Trail>): Int =
        invoke(trails).firstOrNull { it.state == TrailState.OPEN }?.trail?.id
            ?: trails.first().id
}
