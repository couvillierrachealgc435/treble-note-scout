package com.treblenote.scout.presentation.trailmap

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.treblenote.scout.R
import com.treblenote.scout.core.di.ServiceLocator
import com.treblenote.scout.core.theme.ForestText
import com.treblenote.scout.core.theme.PaperMoss
import com.treblenote.scout.core.theme.PaperMossDeep
import com.treblenote.scout.core.theme.PaperMossShade
import com.treblenote.scout.core.theme.PinePrimary
import com.treblenote.scout.core.ui.AppBackground
import com.treblenote.scout.core.ui.PrimaryButton
import com.treblenote.scout.core.ui.SecondaryButton
import com.treblenote.scout.core.ui.SectionRule
import com.treblenote.scout.core.ui.TrailCard
import com.treblenote.scout.domain.model.TrailState

@Composable
fun TrailMapRoute(
    onStartTrail: (Int) -> Unit,
    onMenu: () -> Unit
) {
    val trailMapViewModel: TrailMapViewModel = viewModel(factory = ServiceLocator.viewModelFactory)
    val state by trailMapViewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { trailMapViewModel.refresh() }
    TrailMapScreen(
        state = state,
        onSelect = trailMapViewModel::select,
        onStart = {
            val trailId = trailMapViewModel.confirmSelection()
            if (trailId != null) onStartTrail(trailId)
        },
        onMenu = onMenu
    )
}

@Composable
fun TrailMapScreen(
    state: TrailMapUiState,
    onSelect: (Int) -> Unit,
    onStart: () -> Unit,
    onMenu: () -> Unit
) {
    AppBackground(
        overlay = listOf(
            PaperMoss.copy(alpha = 0.95f),
            PaperMossDeep.copy(alpha = 0.94f),
            PaperMossShade.copy(alpha = 0.96f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = stringResource(R.string.trail_map_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = ForestText
                )
                Image(
                    painter = painterResource(R.drawable.sprite_4),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(40.dp)
                )
            }
            SectionRule()
            Spacer(modifier = Modifier.height(12.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                state.entries.forEachIndexed { index, entry ->
                    val chip = when (entry.state) {
                        TrailState.DONE -> stringResource(R.string.trail_state_done)
                        TrailState.OPEN -> stringResource(R.string.trail_state_open)
                        TrailState.LOCKED -> stringResource(R.string.trail_state_locked)
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = if (index % 2 == 0) 0.dp else 36.dp,
                                end = if (index % 2 == 0) 36.dp else 0.dp
                            )
                    ) {
                        TrailCard(
                            title = stringResource(entry.trail.titleRes),
                            range = stringResource(entry.trail.rangeRes),
                            chipLabel = chip,
                            state = entry.state,
                            selected = entry.trail.id == state.selectedTrailId,
                            index = index,
                            onClick = { onSelect(entry.trail.id) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            Text(
                text = stringResource(R.string.trail_map_hint),
                style = MaterialTheme.typography.labelSmall,
                color = PinePrimary.copy(alpha = 0.8f),
                modifier = Modifier.padding(vertical = 10.dp)
            )

            PrimaryButton(
                text = stringResource(R.string.start_trail),
                onClick = onStart,
                enabled = state.canStart
            )

            Spacer(modifier = Modifier.height(10.dp))

            SecondaryButton(
                text = stringResource(R.string.menu),
                onClick = onMenu
            )

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
