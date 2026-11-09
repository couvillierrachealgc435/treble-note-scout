package com.treblenote.scout.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.treblenote.scout.core.theme.ForestTextMuted
import com.treblenote.scout.core.theme.PaperMoss
import com.treblenote.scout.core.theme.PinePrimary

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val labelColor = if (enabled) PinePrimary else ForestTextMuted
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, labelColor),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = PaperMoss.copy(alpha = if (enabled) 0.92f else 0.45f),
            contentColor = labelColor,
            disabledContainerColor = PaperMoss.copy(alpha = 0.45f),
            disabledContentColor = ForestTextMuted
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = labelColor,
            letterSpacing = 2.sp,
            textAlign = TextAlign.Center
        )
    }
}
