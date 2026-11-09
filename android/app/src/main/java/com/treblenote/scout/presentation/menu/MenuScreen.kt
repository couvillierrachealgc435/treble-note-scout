package com.treblenote.scout.presentation.menu

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.treblenote.scout.R
import com.treblenote.scout.core.di.ServiceLocator
import com.treblenote.scout.core.theme.ForestText
import com.treblenote.scout.core.theme.PaperMoss
import com.treblenote.scout.core.theme.PaperMossDeep
import com.treblenote.scout.core.theme.PaperMossShade
import com.treblenote.scout.core.theme.PinePrimary
import com.treblenote.scout.core.theme.SageSecondary
import com.treblenote.scout.core.theme.TrailAccent
import com.treblenote.scout.core.ui.AccentRule
import com.treblenote.scout.core.ui.AppBackground
import com.treblenote.scout.core.ui.GlassCard
import com.treblenote.scout.core.ui.PrimaryButton
import com.treblenote.scout.core.ui.SecondaryButton
import com.treblenote.scout.core.ui.StatCard

@Composable
fun MenuRoute(
    onPlay: (Int) -> Unit,
    onTrailMap: () -> Unit,
    onScoutKit: () -> Unit,
    onGuide: () -> Unit
) {
    val menuViewModel: MenuViewModel = viewModel(factory = ServiceLocator.viewModelFactory)
    val state by menuViewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { menuViewModel.refresh() }
    MenuScreen(
        state = state,
        onPlay = { onPlay(state.trailId) },
        onTrailMap = onTrailMap,
        onScoutKit = onScoutKit,
        onGuide = onGuide
    )
}

@Composable
fun MenuScreen(
    state: MenuUiState,
    onPlay: () -> Unit,
    onTrailMap: () -> Unit,
    onScoutKit: () -> Unit,
    onGuide: () -> Unit
) {
    var visible by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val heroReveal by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 420, easing = FastOutSlowInEasing)
    )
    val cardReveal by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 420, delayMillis = 140, easing = FastOutSlowInEasing)
    )
    val ctaReveal by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 420, delayMillis = 210, easing = FastOutSlowInEasing)
    )

    AppBackground(
        overlay = listOf(
            PaperMoss.copy(alpha = 0.95f),
            PaperMossDeep.copy(alpha = 0.93f),
            PaperMossShade.copy(alpha = 0.95f)
        )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            for (index in 0 until 3) {
                val y = size.height * (0.26f + index * 0.045f)
                drawLine(
                    color = SageSecondary,
                    start = Offset(0f, y),
                    end = Offset(size.width * 0.34f, y),
                    strokeWidth = 2.dp.toPx()
                )
            }
            drawCircle(
                color = PinePrimary.copy(alpha = 0.06f),
                radius = 110.dp.toPx(),
                center = Offset(size.width * 0.78f, size.height * 0.33f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(R.drawable.icon_home),
                        contentDescription = stringResource(R.string.cd_trailhead),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = stringResource(R.string.menu_header),
                        style = MaterialTheme.typography.labelLarge,
                        color = PinePrimary,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                }
                TextButton(onClick = onGuide) {
                    Text(
                        text = stringResource(R.string.guide),
                        style = MaterialTheme.typography.labelLarge,
                        color = PinePrimary
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(212.dp)
                    .graphicsLayer {
                        alpha = heroReveal
                        translationY = (1f - heroReveal) * 16.dp.toPx()
                    }
            ) {
                Image(
                    painter = painterResource(R.drawable.sprite_1),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 16.dp, y = (-6).dp)
                        .size(128.dp)
                )
                Image(
                    painter = painterResource(R.drawable.sprite_3),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (-96).dp, y = (-2).dp)
                        .size(52.dp)
                )
                Column(modifier = Modifier.align(Alignment.TopStart)) {
                    Text(
                        text = stringResource(R.string.hero_line_1),
                        style = MaterialTheme.typography.displayLarge,
                        color = ForestText
                    )
                    Text(
                        text = stringResource(R.string.hero_line_2),
                        style = MaterialTheme.typography.displayLarge,
                        color = ForestText
                    )
                    Text(
                        text = stringResource(R.string.hero_line_3),
                        style = MaterialTheme.typography.displayLarge,
                        color = PinePrimary
                    )
                    AccentRule(
                        color = TrailAccent,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                    Text(
                        text = stringResource(R.string.menu_tagline),
                        style = MaterialTheme.typography.bodyMedium,
                        color = PinePrimary.copy(alpha = 0.85f),
                        maxLines = 2,
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .width(210.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            if (state.hasHistory) {
                GlassCard(
                    modifier = Modifier.graphicsLayer {
                        alpha = cardReveal
                        translationY = (1f - cardReveal) * 16.dp.toPx()
                    }
                ) {
                    Text(
                        text = stringResource(
                            R.string.menu_current_trail,
                            stringResource(state.trailTitleRes)
                        ),
                        style = MaterialTheme.typography.titleMedium,
                        color = ForestText
                    )
                    Box(
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .fillMaxWidth()
                            .height(6.dp)
                            .background(SageSecondary, RoundedCornerShape(3.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(state.progressFraction.coerceIn(0f, 1f))
                                .fillMaxHeight()
                                .background(PinePrimary, RoundedCornerShape(3.dp))
                        )
                    }
                    if (state.showWeakNotes) {
                        Text(
                            text = stringResource(R.string.menu_most_missed, state.weakNotes),
                            style = MaterialTheme.typography.labelSmall,
                            color = TrailAccent,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (state.showStats) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            alpha = cardReveal
                            translationY = (1f - cardReveal) * 16.dp.toPx()
                        }
                ) {
                    StatCard(
                        label = stringResource(R.string.stat_accuracy),
                        value = stringResource(R.string.percent_value, state.accuracyPercent),
                        accent = PinePrimary,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = stringResource(R.string.stat_best_streak),
                        value = state.bestStreak.toString(),
                        accent = TrailAccent,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            PrimaryButton(
                text = stringResource(R.string.play),
                onClick = onPlay,
                modifier = Modifier.graphicsLayer {
                    alpha = ctaReveal
                    translationY = (1f - ctaReveal) * 16.dp.toPx()
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer { alpha = ctaReveal }
            ) {
                SecondaryButton(
                    text = stringResource(R.string.trail_map),
                    onClick = onTrailMap,
                    modifier = Modifier.weight(1f)
                )
                SecondaryButton(
                    text = stringResource(R.string.scout_kit),
                    onClick = onScoutKit,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
