package com.treblenote.scout.presentation.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.treblenote.scout.R
import com.treblenote.scout.core.di.ServiceLocator
import com.treblenote.scout.core.theme.SageSecondary
import com.treblenote.scout.core.theme.PinePrimary
import com.treblenote.scout.core.theme.SplashInk
import com.treblenote.scout.core.theme.SplashInkDeep
import com.treblenote.scout.core.theme.SplashPaper
import com.treblenote.scout.core.theme.TrailAccent
import com.treblenote.scout.core.ui.AppBackground

@Composable
fun SplashRoute(onFinished: () -> Unit) {
    val splashViewModel: SplashViewModel = viewModel(factory = ServiceLocator.viewModelFactory)
    val finished by splashViewModel.finished.collectAsStateWithLifecycle()
    SplashScreen(finished = finished, onFinished = onFinished)
}

@Composable
fun SplashScreen(
    finished: Boolean,
    onFinished: () -> Unit
) {
    LaunchedEffect(finished) {
        if (finished) onFinished()
    }

    val transition = rememberInfiniteTransition(label = "splash")
    val breathe by transition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathe"
    )
    val glow by transition.animateFloat(
        initialValue = 0.75f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )
    val drift by transition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "drift"
    )

    AppBackground(
        overlay = listOf(
            SplashInk.copy(alpha = 0.92f),
            SplashInkDeep.copy(alpha = 0.97f)
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 32.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Canvas(
                    modifier = Modifier
                        .width(240.dp)
                        .height(96.dp)
                ) {
                    val gap = 16.dp.toPx()
                    val top = (size.height - gap * 4) / 2f
                    for (line in 0 until 5) {
                        val y = top + line * gap
                        drawLine(
                            color = SageSecondary.copy(alpha = 0.30f * glow),
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 1.5.dp.toPx()
                        )
                    }
                }
                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = stringResource(R.string.cd_logo),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(168.dp)
                        .graphicsLayer {
                            scaleX = breathe
                            scaleY = breathe
                            alpha = glow
                        }
                )
                Image(
                    painter = painterResource(R.drawable.sprite_4),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .offset(x = (-16).dp, y = drift.dp)
                        .size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            Text(
                text = stringResource(R.string.app_title),
                style = MaterialTheme.typography.displaySmall,
                color = SplashPaper,
                fontSize = 36.sp,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = stringResource(R.string.splash_subtitle),
                style = MaterialTheme.typography.labelLarge,
                color = SageSecondary,
                letterSpacing = 3.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(44.dp))

            LinearProgressIndicator(
                color = TrailAccent,
                trackColor = PinePrimary.copy(alpha = 0.45f),
                modifier = Modifier
                    .width(180.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.splash_loading),
                style = MaterialTheme.typography.labelSmall,
                color = SageSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
