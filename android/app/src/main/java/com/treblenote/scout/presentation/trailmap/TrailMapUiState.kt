package com.treblenote.scout.presentation.trailmap

import com.treblenote.scout.domain.model.TrailStatus

data class TrailMapUiState(
    val entries: List<TrailStatus> = emptyList(),
    val selectedTrailId: Int? = null
) {
    val canStart: Boolean = selectedTrailId != null
}
