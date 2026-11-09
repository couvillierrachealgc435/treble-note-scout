package com.treblenote.scout.core.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.treblenote.scout.R
import com.treblenote.scout.core.config.GameConfig
import com.treblenote.scout.core.theme.SageSecondary

@Composable
fun ScoutTrailFooter(
    step: Int,
    total: Int,
    modifier: Modifier = Modifier
) {
    val badgeSize = 28.dp
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(36.dp)
    ) {
        val span = maxWidth - badgeSize
        val safeTotal = (total - 1).coerceAtLeast(1)
        val fraction = (step.toFloat() / safeTotal).coerceIn(0f, 1f)
        val badgeX by animateDpAsState(
            targetValue = span * fraction,
            animationSpec = tween(
                durationMillis = GameConfig.SCOUT_STEP_MS,
                easing = FastOutSlowInEasing
            )
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            val y = size.height / 2f
            drawLine(
                color = SageSecondary,
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 4.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 10f))
            )
        }

        Box(modifier = Modifier.offset(x = badgeX)) {
            Image(
                painter = painterResource(R.drawable.sprite_3),
                contentDescription = stringResource(R.string.cd_scout_badge),
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(badgeSize)
            )
        }
    }
}
