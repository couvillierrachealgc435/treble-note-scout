package com.treblenote.scout.core.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.treblenote.scout.R
import com.treblenote.scout.core.theme.CardPaper
import com.treblenote.scout.core.theme.ForestText
import com.treblenote.scout.core.theme.SageSecondary
import com.treblenote.scout.domain.model.Accidental
import com.treblenote.scout.domain.model.Clef
import com.treblenote.scout.domain.model.Note

data class StaffNote(
    val note: Note,
    val tint: Color,
    val serial: Int = 0
)

private val BOARD_PAD = 12.dp
private val BOARD_BORDER = 2.dp
private val BOARD_FRAME = BOARD_PAD + BOARD_BORDER
private val STAFF_LINE_GAP = 14.dp
private val STAFF_STEP = STAFF_LINE_GAP / 2
private val CLEF_WIDTH = 44.dp
private val CLEF_HEIGHT = 64.dp
private val LABEL_ROW = 22.dp
private const val LEDGER_SLOTS = 3
private const val TREBLE_BOTTOM_INDEX = 30
private const val BASS_BOTTOM_INDEX = 18

@Composable
fun StaffBoard(
    clef: Clef,
    notes: List<StaffNote>,
    modifier: Modifier = Modifier,
    showLabels: Boolean = false,
    animateDrop: Boolean = false
) {
    val innerHeight = STAFF_LINE_GAP * (4 + LEDGER_SLOTS * 2)
    val contentHeight = innerHeight + if (showLabels) LABEL_ROW else 0.dp
    val serial = notes.firstOrNull()?.serial ?: 0
    var settled by remember(serial) { mutableStateOf(!animateDrop) }
    LaunchedEffect(serial) { settled = true }
    val dropOffset by animateDpAsState(
        targetValue = if (settled) 0.dp else (-30).dp,
        animationSpec = spring(dampingRatio = 0.42f, stiffness = 1800f)
    )

    Card(
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(BOARD_BORDER, SageSecondary),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = CardPaper.copy(alpha = 0.88f)),
        modifier = modifier
            .fillMaxWidth()
            .height(contentHeight + BOARD_FRAME * 2)
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(BOARD_FRAME)
        ) {
            val innerWidth = maxWidth
            val noteStart = CLEF_WIDTH + 20.dp
            val noteSpan = innerWidth - noteStart - 10.dp
            val bottomLineY = STAFF_LINE_GAP * (LEDGER_SLOTS + 4)
            val bottomIndex = if (clef == Clef.TREBLE) TREBLE_BOTTOM_INDEX else BASS_BOTTOM_INDEX
            val count = notes.size.coerceAtLeast(1)

            val placed = notes.mapIndexed { index, item ->
                val x = noteStart + noteSpan * ((index + 0.5f) / count)
                val steps = item.note.diatonicIndex - bottomIndex
                val y = bottomLineY - STAFF_STEP * steps
                PlacedNote(item, x, y, steps)
            }

            Image(
                painter = painterResource(
                    if (clef == Clef.TREBLE) R.drawable.sprite_1 else R.drawable.sprite_2
                ),
                contentDescription = stringResource(
                    if (clef == Clef.TREBLE) R.string.cd_treble_clef else R.string.cd_bass_clef
                ),
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .offset(x = 0.dp, y = STAFF_LINE_GAP * LEDGER_SLOTS - 4.dp)
                    .width(CLEF_WIDTH)
                    .height(CLEF_HEIGHT)
            )

            Canvas(modifier = Modifier.fillMaxSize()) {
                val lineStroke = 1.5.dp.toPx()
                val gap = STAFF_LINE_GAP.toPx()
                val top = (STAFF_LINE_GAP * LEDGER_SLOTS).toPx()
                for (line in 0 until 5) {
                    val y = top + line * gap
                    drawLine(
                        color = ForestText.copy(alpha = 0.72f),
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = lineStroke
                    )
                }
                placed.forEach { item ->
                    drawNote(
                        item = item,
                        dropPx = if (animateDrop) dropOffset.toPx() else 0f,
                        lineStroke = lineStroke,
                        stepPx = STAFF_STEP.toPx(),
                        headWidth = 18.dp.toPx(),
                        headHeight = 13.dp.toPx(),
                        ledgerHalf = 16.dp.toPx(),
                        stemHeight = 40.dp.toPx(),
                        stemWidth = 2.dp.toPx(),
                        bottomLineYPx = (STAFF_LINE_GAP * (LEDGER_SLOTS + 4)).toPx()
                    )
                }
            }

            placed.forEach { item ->
                if (item.staffNote.note.accidental == Accidental.SHARP) {
                    Text(
                        text = SHARP_GLYPH,
                        style = MaterialTheme.typography.titleLarge,
                        color = item.staffNote.tint,
                        modifier = Modifier.offset(
                            x = item.x - 26.dp,
                            y = item.y - 14.dp + (if (animateDrop) dropOffset else 0.dp)
                        )
                    )
                }
                if (showLabels) {
                    Text(
                        text = item.staffNote.note.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = item.staffNote.tint,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .offset(x = item.x - 16.dp, y = innerHeight + 2.dp)
                            .width(32.dp)
                    )
                }
            }
        }
    }
}

private data class PlacedNote(
    val staffNote: StaffNote,
    val x: Dp,
    val y: Dp,
    val steps: Int
)

private const val SHARP_GLYPH = "♯"

private fun DrawScope.drawNote(
    item: PlacedNote,
    dropPx: Float,
    lineStroke: Float,
    stepPx: Float,
    headWidth: Float,
    headHeight: Float,
    ledgerHalf: Float,
    stemHeight: Float,
    stemWidth: Float,
    bottomLineYPx: Float
) {
    val centerX = item.x.toPx()
    val centerY = item.y.toPx() + dropPx

    if (item.steps < 0) {
        var step = -2
        while (step >= item.steps) {
            val y = bottomLineYPx - stepPx * step
            drawLine(
                color = ForestText.copy(alpha = 0.72f),
                start = Offset(centerX - ledgerHalf, y),
                end = Offset(centerX + ledgerHalf, y),
                strokeWidth = lineStroke
            )
            step -= 2
        }
    } else if (item.steps > 8) {
        var step = 10
        while (step <= item.steps) {
            val y = bottomLineYPx - stepPx * step
            drawLine(
                color = ForestText.copy(alpha = 0.72f),
                start = Offset(centerX - ledgerHalf, y),
                end = Offset(centerX + ledgerHalf, y),
                strokeWidth = lineStroke
            )
            step += 2
        }
    }

    drawOval(
        color = item.staffNote.tint,
        topLeft = Offset(centerX - headWidth / 2f, centerY - headHeight / 2f),
        size = Size(headWidth, headHeight)
    )

    val stemUp = item.steps < 4
    val stemX = if (stemUp) centerX + headWidth / 2f - stemWidth else centerX - headWidth / 2f
    val stemTop = if (stemUp) centerY - stemHeight else centerY
    drawRect(
        color = item.staffNote.tint,
        topLeft = Offset(stemX, stemTop),
        size = Size(stemWidth, stemHeight)
    )
}
