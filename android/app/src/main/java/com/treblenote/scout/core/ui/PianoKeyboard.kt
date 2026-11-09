package com.treblenote.scout.core.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import com.treblenote.scout.R
import com.treblenote.scout.core.theme.CardPaper
import com.treblenote.scout.core.theme.ForestText
import com.treblenote.scout.core.theme.PinePrimary
import com.treblenote.scout.core.theme.SageSecondary
import com.treblenote.scout.core.theme.TrailAccent
import com.treblenote.scout.domain.model.Note

private val WHITE_OFFSETS = listOf(0, 2, 4, 5, 7, 9, 11)
private val BLACK_OFFSETS = listOf(1, 3, 6, 8, 10)
private val BLACK_ANCHORS = listOf(0, 1, 3, 4, 5)

@Composable
fun PianoKeyboard(
    lowMidi: Int,
    correctMidi: Int?,
    wrongMidi: Int?,
    enabled: Boolean,
    onKey: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val idleLabel = stringResource(R.string.key_state_idle)
    val correctLabel = stringResource(R.string.key_state_correct)
    val wrongLabel = stringResource(R.string.key_state_wrong)

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val whiteWidth = maxWidth / WHITE_OFFSETS.size
        val blackWidth = whiteWidth * 0.62f

        Row(modifier = Modifier.fillMaxWidth()) {
            WHITE_OFFSETS.forEach { offset ->
                val midi = lowMidi + offset
                val note = Note.fromMidi(midi)
                val mark = markFor(midi, correctMidi, wrongMidi)
                val target = when (mark) {
                    AnswerMark.CORRECT -> PinePrimary
                    AnswerMark.WRONG -> TrailAccent
                    AnswerMark.IDLE -> CardPaper
                }
                val container by animateColorAsState(
                    targetValue = target,
                    animationSpec = tween(durationMillis = 120)
                )
                val shape = RoundedCornerShape(
                    topStart = 0.dp,
                    topEnd = 0.dp,
                    bottomStart = 6.dp,
                    bottomEnd = 6.dp
                )
                Box(
                    modifier = Modifier
                        .width(whiteWidth)
                        .height(120.dp)
                        .background(container, shape)
                        .border(BorderStroke(1.dp, ForestText.copy(alpha = 0.55f)), shape)
                        .clickable(enabled = enabled) { onKey(midi) }
                        .semantics {
                            contentDescription = note.label
                            stateDescription = when (mark) {
                                AnswerMark.CORRECT -> correctLabel
                                AnswerMark.WRONG -> wrongLabel
                                AnswerMark.IDLE -> idleLabel
                            }
                        }
                )
            }
        }

        BLACK_OFFSETS.forEachIndexed { index, offset ->
            val midi = lowMidi + offset
            val note = Note.fromMidi(midi)
            val mark = markFor(midi, correctMidi, wrongMidi)
            val target = when (mark) {
                AnswerMark.CORRECT -> PinePrimary
                AnswerMark.WRONG -> TrailAccent
                AnswerMark.IDLE -> ForestText
            }
            val container by animateColorAsState(
                targetValue = target,
                animationSpec = tween(durationMillis = 120)
            )
            val shape = RoundedCornerShape(
                topStart = 0.dp,
                topEnd = 0.dp,
                bottomStart = 4.dp,
                bottomEnd = 4.dp
            )
            Box(
                modifier = Modifier
                    .offset(x = whiteWidth * (BLACK_ANCHORS[index] + 1) - blackWidth / 2f)
                    .width(blackWidth)
                    .height(74.dp)
                    .background(container, shape)
                    .border(BorderStroke(1.dp, SageSecondary.copy(alpha = 0.6f)), shape)
                    .clickable(enabled = enabled) { onKey(midi) }
                    .semantics {
                        contentDescription = note.label
                        stateDescription = when (mark) {
                            AnswerMark.CORRECT -> correctLabel
                            AnswerMark.WRONG -> wrongLabel
                            AnswerMark.IDLE -> idleLabel
                        }
                    }
            )
        }
    }
}

private fun markFor(midi: Int, correctMidi: Int?, wrongMidi: Int?): AnswerMark = when (midi) {
    correctMidi -> AnswerMark.CORRECT
    wrongMidi -> AnswerMark.WRONG
    else -> AnswerMark.IDLE
}
