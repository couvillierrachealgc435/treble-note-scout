package com.treblenote.scout.core.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import com.treblenote.scout.core.config.GameConfig
import com.treblenote.scout.core.theme.CardPaper
import com.treblenote.scout.core.theme.ForestText
import com.treblenote.scout.core.theme.ForestTextMuted
import com.treblenote.scout.core.theme.PinePrimary
import com.treblenote.scout.core.theme.SageSecondary
import com.treblenote.scout.core.theme.SplashPaper
import com.treblenote.scout.core.theme.TrailAccent
import com.treblenote.scout.domain.model.TrailState

@Composable
fun TrailCard(
    title: String,
    range: String,
    chipLabel: String,
    state: TrailState,
    selected: Boolean,
    index: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var unfolded by remember { mutableStateOf(false) }
    LaunchedEffect(index) { unfolded = true }
    val progress by animateFloatAsState(
        targetValue = if (unfolded) 1f else 0f,
        animationSpec = tween(
            durationMillis = GameConfig.CARD_UNFOLD_MS,
            delayMillis = index * 60,
            easing = FastOutSlowInEasing
        )
    )
    val enabled = state != TrailState.LOCKED
    val accent = when (state) {
        TrailState.DONE -> PinePrimary
        TrailState.OPEN -> TrailAccent
        TrailState.LOCKED -> ForestTextMuted
    }
    val titleColor = if (enabled) ForestText else ForestTextMuted

    Card(
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(if (selected) 2.dp else 1.dp, if (selected) accent else SageSecondary),
        elevation = CardDefaults.cardElevation(defaultElevation = if (selected) 3.dp else 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardPaper.copy(alpha = if (enabled) 0.86f else 0.5f),
            disabledContainerColor = CardPaper.copy(alpha = 0.5f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                rotationX = (1f - progress) * -70f
                alpha = progress
                transformOrigin = TransformOrigin(0.5f, 0f)
            }
            .clickable(enabled = enabled, onClick = onClick)
            .semantics { stateDescription = chipLabel }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Column(modifier = Modifier.padding(end = 12.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = titleColor
                )
                Text(
                    text = range,
                    style = MaterialTheme.typography.labelSmall,
                    color = titleColor.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Text(
                text = chipLabel,
                style = MaterialTheme.typography.labelSmall,
                color = if (state == TrailState.DONE) SplashPaper else accent,
                modifier = Modifier
                    .background(
                        color = if (state == TrailState.DONE) accent else accent.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(6.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            )
        }
    }
}
