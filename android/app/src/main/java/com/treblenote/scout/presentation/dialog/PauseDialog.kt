package com.treblenote.scout.presentation.dialog

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.treblenote.scout.R
import com.treblenote.scout.core.theme.CardPaper
import com.treblenote.scout.core.theme.ForestText
import com.treblenote.scout.core.theme.PinePrimary
import com.treblenote.scout.core.theme.TrailAccent

@Composable
fun PauseDialog(
    onResume: () -> Unit,
    onMenu: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onResume,
        shape = RoundedCornerShape(12.dp),
        containerColor = CardPaper,
        title = {
            Text(
                text = stringResource(R.string.pause_title),
                style = MaterialTheme.typography.headlineSmall,
                color = ForestText
            )
        },
        text = {
            Text(
                text = stringResource(R.string.pause_message),
                style = MaterialTheme.typography.bodyMedium,
                color = ForestText.copy(alpha = 0.78f)
            )
        },
        confirmButton = {
            TextButton(onClick = onResume) {
                Text(
                    text = stringResource(R.string.resume),
                    style = MaterialTheme.typography.labelLarge,
                    color = PinePrimary
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onMenu) {
                Text(
                    text = stringResource(R.string.menu),
                    style = MaterialTheme.typography.labelLarge,
                    color = TrailAccent
                )
            }
        }
    )
}
