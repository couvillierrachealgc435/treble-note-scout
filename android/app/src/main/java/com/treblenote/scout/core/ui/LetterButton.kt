package com.treblenote.scout.core.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import com.treblenote.scout.core.theme.ForestText
import com.treblenote.scout.core.theme.ForestTextMuted
import com.treblenote.scout.core.theme.PaperMoss
import com.treblenote.scout.core.theme.PinePrimary
import com.treblenote.scout.core.theme.SageSecondary
import com.treblenote.scout.core.theme.SplashPaper
import com.treblenote.scout.core.theme.TrailAccent

enum class AnswerMark {
    IDLE,
    CORRECT,
    WRONG
}

@Composable
fun LetterButton(
    letter: Char,
    mark: AnswerMark,
    enabled: Boolean,
    stateLabel: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val targetContainer = when (mark) {
        AnswerMark.CORRECT -> PinePrimary
        AnswerMark.WRONG -> TrailAccent
        AnswerMark.IDLE -> if (enabled) PaperMoss else SageSecondary.copy(alpha = 0.4f)
    }
    val container by animateColorAsState(
        targetValue = targetContainer,
        animationSpec = tween(durationMillis = 120)
    )
    val label = when {
        mark != AnswerMark.IDLE -> SplashPaper
        enabled -> ForestText
        else -> ForestTextMuted
    }

    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, if (enabled) PinePrimary else ForestTextMuted),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = container,
            contentColor = label,
            disabledContainerColor = container,
            disabledContentColor = label
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .semantics { stateDescription = stateLabel }
    ) {
        Text(
            text = letter.toString(),
            style = MaterialTheme.typography.titleLarge,
            color = label
        )
    }
}
