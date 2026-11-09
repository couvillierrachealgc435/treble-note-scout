package com.treblenote.scout.presentation.game

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
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
import com.treblenote.scout.core.theme.SageSurface
import com.treblenote.scout.core.theme.TrailAccent
import com.treblenote.scout.core.ui.AnswerMark
import com.treblenote.scout.core.ui.AppBackground
import com.treblenote.scout.core.ui.LetterButton
import com.treblenote.scout.core.ui.PianoKeyboard
import com.treblenote.scout.core.ui.ScoutTrailFooter
import com.treblenote.scout.core.ui.SectionRule
import com.treblenote.scout.core.ui.StaffBoard
import com.treblenote.scout.core.ui.StaffNote
import com.treblenote.scout.core.ui.StatCard
import com.treblenote.scout.domain.model.RoundResult
import com.treblenote.scout.domain.model.RunMode
import com.treblenote.scout.presentation.dialog.PauseDialog

private const val LETTER_SLOTS = 4
private val LETTER_ROW_TOP = listOf('A', 'B', 'C', 'D')
private val LETTER_ROW_BOTTOM = listOf('E', 'F', 'G')

@Composable
fun GameRoute(
    trailId: Int,
    weakNotesOnly: Boolean,
    runId: Long,
    onHome: () -> Unit,
    onFinished: (RoundResult) -> Unit
) {
    val gameViewModel: GameViewModel = viewModel(factory = ServiceLocator.viewModelFactory)
    val state by gameViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(trailId, weakNotesOnly, runId) {
        gameViewModel.start(trailId, weakNotesOnly, runId)
    }

    LaunchedEffect(Unit) {
        gameViewModel.events.collect { event ->
            when (event) {
                is GameEvent.Finished -> onFinished(event.result)
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { gameViewModel.pause() }
    }

    GameScreen(
        state = state,
        onLetter = gameViewModel::onLetter,
        onKey = gameViewModel::onKey,
        onPause = gameViewModel::pause,
        onResume = gameViewModel::resume,
        onHome = onHome
    )
}

@Composable
fun GameScreen(
    state: GameUiState,
    onLetter: (Char) -> Unit,
    onKey: (Int) -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onHome: () -> Unit
) {
    val haptics = LocalHapticFeedback.current
    LaunchedEffect(state.noteSerial, state.lastCorrect) {
        if (state.lastCorrect && state.phase == GamePhase.REVEALING) {
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    val timerWidth by animateFloatAsState(
        targetValue = state.remainingFraction,
        animationSpec = tween(durationMillis = 90, easing = LinearEasing)
    )

    AppBackground(
        overlay = listOf(
            PaperMoss.copy(alpha = 0.96f),
            PaperMossDeep.copy(alpha = 0.94f),
            PaperMossShade.copy(alpha = 0.97f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 8.dp)
            ) {
                IconButton(onClick = onHome) {
                    Image(
                        painter = painterResource(R.drawable.icon_home),
                        contentDescription = stringResource(R.string.cd_home),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = stringResource(
                        R.string.game_counter,
                        stringResource(state.trailTitleRes),
                        state.displayIndex,
                        state.totalQuestions
                    ),
                    style = MaterialTheme.typography.labelLarge,
                    color = ForestText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
                )
                IconButton(onClick = onPause) {
                    Image(
                        painter = painterResource(R.drawable.icon_pause),
                        contentDescription = stringResource(R.string.cd_pause),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            SectionRule()

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                val note = state.currentNote
                StaffBoard(
                    clef = state.clef,
                    notes = if (note == null) {
                        emptyList()
                    } else {
                        listOf(
                            StaffNote(
                                note = note,
                                tint = noteTint(state),
                                serial = state.noteSerial
                            )
                        )
                    },
                    showLabels = state.labelsEnabled && state.phase == GamePhase.REVEALING,
                    animateDrop = true,
                    modifier = Modifier
                        .widthIn(max = 380.dp)
                        .fillMaxWidth()
                )

                if (state.timerEnabled) {
                    Box(
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .widthIn(max = 380.dp)
                            .fillMaxWidth()
                            .height(4.dp)
                            .background(SageSecondary, RoundedCornerShape(2.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(timerWidth.coerceIn(0f, 1f))
                                .fillMaxHeight()
                                .background(TrailAccent, RoundedCornerShape(2.dp))
                        )
                    }
                }

                if (state.phase == GamePhase.IDLE) {
                    Text(
                        text = stringResource(R.string.game_idle_hint),
                        style = MaterialTheme.typography.labelSmall,
                        color = PinePrimary.copy(alpha = 0.8f),
                        modifier = Modifier.padding(top = 14.dp)
                    )
                }
            }

            if (state.visibleStats >= 2) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    StatCard(
                        label = stringResource(R.string.stat_score),
                        value = state.score.toString(),
                        accent = PinePrimary,
                        compact = true,
                        announce = true,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = stringResource(R.string.stat_streak),
                        value = state.streak.toString(),
                        accent = TrailAccent,
                        compact = true,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = stringResource(R.string.stat_left),
                        value = state.remainingNotes.toString(),
                        accent = ForestText,
                        compact = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp),
                color = SageSurface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    SectionRule()
                    Spacer(modifier = Modifier.height(12.dp))

                    if (state.runMode == RunMode.LETTERS) {
                        LetterRow(
                            letters = LETTER_ROW_TOP,
                            state = state,
                            onLetter = onLetter
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LetterRow(
                            letters = LETTER_ROW_BOTTOM,
                            state = state,
                            onLetter = onLetter
                        )
                    } else {
                        PianoKeyboard(
                            lowMidi = state.keyboardLowMidi,
                            correctMidi = state.revealMidi,
                            wrongMidi = state.wrongMidi,
                            enabled = state.controlsEnabled,
                            onKey = onKey
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    ScoutTrailFooter(
                        step = state.scoutStep,
                        total = state.totalQuestions
                    )
                }
            }
        }

        if (state.paused) {
            PauseDialog(onResume = onResume, onMenu = onHome)
        }
    }
}

@Composable
private fun LetterRow(
    letters: List<Char>,
    state: GameUiState,
    onLetter: (Char) -> Unit
) {
    val idle = stringResource(R.string.key_state_idle)
    val correct = stringResource(R.string.key_state_correct)
    val wrong = stringResource(R.string.key_state_wrong)
    val slack = (LETTER_SLOTS - letters.size).coerceAtLeast(0) / 2f
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        if (slack > 0f) {
            Spacer(modifier = Modifier.weight(slack))
        }
        letters.forEach { letter ->
            val mark = when (letter) {
                state.revealLetter -> AnswerMark.CORRECT
                state.wrongLetter -> AnswerMark.WRONG
                else -> AnswerMark.IDLE
            }
            LetterButton(
                letter = letter,
                mark = mark,
                enabled = state.controlsEnabled,
                stateLabel = when (mark) {
                    AnswerMark.CORRECT -> correct
                    AnswerMark.WRONG -> wrong
                    AnswerMark.IDLE -> idle
                },
                onClick = { onLetter(letter) },
                modifier = Modifier.weight(1f)
            )
        }
        if (slack > 0f) {
            Spacer(modifier = Modifier.weight(slack))
        }
    }
}

private fun noteTint(state: GameUiState): Color = when {
    state.phase == GamePhase.REVEALING && state.lastCorrect -> PinePrimary
    state.phase == GamePhase.REVEALING -> TrailAccent
    else -> ForestText
}
