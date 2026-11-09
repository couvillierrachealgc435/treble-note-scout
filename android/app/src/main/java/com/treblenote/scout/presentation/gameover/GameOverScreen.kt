package com.treblenote.scout.presentation.gameover

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.treblenote.scout.R
import com.treblenote.scout.core.di.ServiceLocator
import com.treblenote.scout.core.theme.ForestText
import com.treblenote.scout.core.theme.PaperMossDeep
import com.treblenote.scout.core.theme.PaperMossShade
import com.treblenote.scout.core.theme.PinePrimary
import com.treblenote.scout.core.theme.SageSecondary
import com.treblenote.scout.core.theme.SplashPaper
import com.treblenote.scout.core.theme.TrailAccent
import com.treblenote.scout.core.ui.AppBackground
import com.treblenote.scout.core.ui.PrimaryButton
import com.treblenote.scout.core.ui.SecondaryButton
import com.treblenote.scout.core.ui.StaffBoard
import com.treblenote.scout.core.ui.StaffNote
import com.treblenote.scout.core.ui.StatCard
import com.treblenote.scout.domain.model.RoundResult

@Composable
fun GameOverRoute(
    result: RoundResult,
    onScoutAgain: () -> Unit,
    onWeakCamp: () -> Unit,
    onMenu: () -> Unit
) {
    val gameOverViewModel: GameOverViewModel = viewModel(factory = ServiceLocator.viewModelFactory)
    val state by gameOverViewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(result) { gameOverViewModel.present(result) }
    GameOverScreen(
        state = state,
        onScoutAgain = onScoutAgain,
        onWeakCamp = onWeakCamp,
        onMenu = onMenu
    )
}

@Composable
fun GameOverScreen(
    state: GameOverUiState,
    onScoutAgain: () -> Unit,
    onWeakCamp: () -> Unit,
    onMenu: () -> Unit
) {
    var ribbonVisible by remember { mutableStateOf(false) }
    LaunchedEffect(state.isNewBest) { ribbonVisible = state.isNewBest }
    val ribbonScale by animateFloatAsState(
        targetValue = if (ribbonVisible) 1f else 0.6f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessLow)
    )

    AppBackground(
        overlay = listOf(
            PaperMossDeep.copy(alpha = 0.96f),
            PaperMossShade.copy(alpha = 0.97f),
            SageSecondary.copy(alpha = 0.95f)
        )
    ) {
        Image(
            painter = painterResource(R.drawable.sprite_4),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 68.dp, end = 16.dp)
                .size(72.dp)
                .graphicsLayer { alpha = 0.5f }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Box(modifier = Modifier.widthIn(max = 220.dp)) {
                    Text(
                        text = stringResource(
                            if (state.isWin) R.string.gameover_win else R.string.gameover_lose
                        ),
                        style = MaterialTheme.typography.displaySmall,
                        color = if (state.isWin) PinePrimary else TrailAccent,
                        modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
                    )
                }

                if (state.isNewBest) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = TrailAccent,
                        modifier = Modifier
                            .padding(top = 14.dp)
                            .graphicsLayer {
                                scaleX = ribbonScale
                                scaleY = ribbonScale
                            }
                    ) {
                        Text(
                            text = stringResource(R.string.new_best),
                            style = MaterialTheme.typography.labelLarge,
                            color = SplashPaper,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                        )
                    }
                }

                if (state.showStats) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp)
                    ) {
                        StatCard(
                            label = stringResource(R.string.stat_accuracy),
                            value = stringResource(R.string.percent_value, state.accuracy),
                            accent = PinePrimary,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            label = stringResource(R.string.stat_time),
                            value = stringResource(
                                R.string.time_value,
                                state.elapsedMinutes,
                                state.elapsedSeconds
                            ),
                            accent = ForestText,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            label = stringResource(R.string.stat_best_streak),
                            value = state.bestStreak.toString(),
                            accent = TrailAccent,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Text(
                    text = stringResource(R.string.gameover_missed_title),
                    style = MaterialTheme.typography.labelLarge,
                    color = PinePrimary,
                    modifier = Modifier.padding(top = 22.dp, bottom = 10.dp)
                )

                if (state.hasMisses) {
                    StaffBoard(
                        clef = state.clef,
                        notes = state.missedNotes.map { note ->
                            StaffNote(note = note, tint = TrailAccent)
                        },
                        showLabels = true,
                        modifier = Modifier
                            .widthIn(max = 380.dp)
                            .fillMaxWidth()
                    )
                } else {
                    Text(
                        text = stringResource(R.string.gameover_no_misses),
                        style = MaterialTheme.typography.bodyMedium,
                        color = ForestText.copy(alpha = 0.8f),
                        textAlign = TextAlign.Start
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            PrimaryButton(
                text = stringResource(R.string.scout_again),
                onClick = onScoutAgain
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SecondaryButton(
                    text = stringResource(R.string.weak_note_camp),
                    onClick = onWeakCamp,
                    enabled = state.hasMisses,
                    modifier = Modifier.weight(1f)
                )
                SecondaryButton(
                    text = stringResource(R.string.menu),
                    onClick = onMenu,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
        }
    }
}
