package com.treblenote.scout.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.treblenote.scout.core.theme.SageSecondary

@Composable
fun SectionRule(
    modifier: Modifier = Modifier,
    color: Color = SageSecondary,
    thickness: Dp = 1.dp
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(thickness)
            .background(color)
    )
}

@Composable
fun AccentRule(
    modifier: Modifier = Modifier,
    color: Color,
    width: Dp = 56.dp,
    thickness: Dp = 3.dp
) {
    Box(
        modifier = modifier
            .width(width)
            .height(thickness)
            .background(color)
    )
}
