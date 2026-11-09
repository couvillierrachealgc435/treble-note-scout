package com.treblenote.scout.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.treblenote.scout.core.config.GameConfig
import com.treblenote.scout.domain.model.AnswerWindow
import com.treblenote.scout.domain.model.Note
import com.treblenote.scout.domain.model.RoundResult
import com.treblenote.scout.domain.model.ScoutSettings
import com.treblenote.scout.domain.repository.ProgressRepository
import com.treblenote.scout.domain.repository.SettingsRepository
import com.treblenote.scout.domain.repository.TonePlayer
import com.treblenote.scout.domain.repository.TrailRepository
import com.treblenote.scout.domain.usecase.BuildQuestionsUseCase
import com.treblenote.scout.domain.usecase.EvaluateAnswerUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class GameViewModel(
    private val progress: ProgressRepository,
    private val settingsRepository: SettingsRepository,
    private val tonePlayer: TonePlayer,
    private val trails: TrailRepository,
    private val buildQuestions: BuildQuestionsUseCase,
    private val evaluateAnswer: EvaluateAnswerUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(GameUiState())
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    private val eventChannel = Channel<GameEvent>(Channel.BUFFERED)
    val events: Flow<GameEvent> = eventChannel.receiveAsFlow()

    private var questions: List<Note> = emptyList()
    private var missedNotes = mutableListOf<Note>()
    private var settings = ScoutSettings()
    private var timerJob: Job? = null
    private var revealJob: Job? = null
    private var startedAt = 0L
    private var activeRunId = 0L

    fun start(trailId: Int, weakNotesOnly: Boolean, runId: Long) {
        if (activeRunId == runId) return
        activeRunId = runId
        cancelJobs()
        settings = settingsRepository.load()
        val trail = trails.trailById(trailId)
        progress.setCurrentTrail(trailId)
        questions = buildQuestions(trail, GameConfig.QUESTIONS_PER_TRAIL, weakNotesOnly)
        missedNotes = mutableListOf()
        startedAt = System.currentTimeMillis()
        _state.value = GameUiState(
            trailId = trail.id,
            trailTitleRes = trail.titleRes,
            clef = trail.clef,
            runMode = trail.runMode,
            keyboardLowMidi = trail.keyboardLowMidi,
            totalQuestions = questions.size.coerceAtLeast(1),
            timerEnabled = settings.timerEnabled,
            labelsEnabled = settings.labelsEnabled
        )
        askNext()
    }

    fun onLetter(letter: Char) {
        val current = _state.value
        val note = current.currentNote ?: return
        if (!current.controlsEnabled) return
        resolve(evaluateAnswer.byLetter(note, letter), note, letter, null)
    }

    fun onKey(midi: Int) {
        val current = _state.value
        val note = current.currentNote ?: return
        if (!current.controlsEnabled) return
        resolve(evaluateAnswer.byKey(note, midi), note, null, midi)
    }

    fun pause() {
        if (_state.value.phase == GamePhase.FINISHED) return
        timerJob?.cancel()
        revealJob?.cancel()
        tonePlayer.stop()
        _state.update { it.copy(paused = true, inputLocked = true) }
    }

    fun resume() {
        val current = _state.value
        if (!current.paused || current.phase == GamePhase.FINISHED) return
        _state.update { it.copy(paused = false, inputLocked = current.phase != GamePhase.PLAYING) }
        when (_state.value.phase) {
            GamePhase.PLAYING -> startTimer()
            GamePhase.REVEALING -> scheduleAdvance()
            else -> Unit
        }
    }

    override fun onCleared() {
        cancelJobs()
        tonePlayer.stop()
        super.onCleared()
    }

    private fun askNext() {
        val index = _state.value.questionIndex
        if (index >= questions.size) {
            finish()
            return
        }
        val note = questions[index]
        _state.update {
            it.copy(
                phase = GamePhase.PLAYING,
                currentNote = note,
                noteSerial = it.noteSerial + 1,
                inputLocked = false,
                paused = false,
                remainingFraction = 1f,
                revealLetter = null,
                revealMidi = null,
                wrongLetter = null,
                wrongMidi = null
            )
        }
        startTimer()
    }

    private fun resolve(isCorrect: Boolean, note: Note, letter: Char?, midi: Int?) {
        timerJob?.cancel()
        if (isCorrect) {
            if (settings.toneEnabled) {
                tonePlayer.play(note.midi)
            }
            _state.update {
                val streak = it.streak + 1
                it.copy(
                    phase = GamePhase.REVEALING,
                    inputLocked = true,
                    lastCorrect = true,
                    score = it.score + SCORE_BASE + it.streak * SCORE_STREAK_BONUS,
                    streak = streak,
                    bestStreak = maxOf(it.bestStreak, streak),
                    correct = it.correct + 1,
                    scoutStep = it.scoutStep + 1,
                    revealLetter = note.letter,
                    revealMidi = note.midi
                )
            }
        } else {
            missedNotes.add(note)
            _state.update {
                it.copy(
                    phase = GamePhase.REVEALING,
                    inputLocked = true,
                    lastCorrect = false,
                    streak = 0,
                    misses = it.misses + 1,
                    revealLetter = note.letter,
                    revealMidi = note.midi,
                    wrongLetter = letter,
                    wrongMidi = midi
                )
            }
        }

        scheduleAdvance()
    }

    private fun scheduleAdvance() {
        revealJob?.cancel()
        revealJob = viewModelScope.launch {
            delay(GameConfig.REVEAL_MS)
            advance()
        }
    }

    private fun advance() {
        _state.update { it.copy(questionIndex = it.questionIndex + 1) }
        val current = _state.value
        val outOfLives = settings.answerWindow == AnswerWindow.BRISK &&
            current.misses >= GameConfig.BRISK_LIVES
        if (outOfLives) finish() else askNext()
    }

    private fun startTimer() {
        timerJob?.cancel()
        if (!settings.timerEnabled) {
            _state.update { it.copy(remainingFraction = 1f) }
            return
        }
        val window = settings.answerWindow.millis
        timerJob = viewModelScope.launch {
            var elapsed = 0L
            while (isActive && elapsed < window) {
                delay(GameConfig.TIMER_TICK_MS)
                elapsed += GameConfig.TIMER_TICK_MS
                val fraction = (1f - elapsed.toFloat() / window).coerceIn(0f, 1f)
                _state.update { it.copy(remainingFraction = fraction) }
            }
            if (isActive) {
                val note = _state.value.currentNote
                if (note != null) {
                    resolve(false, note, null, null)
                }
            }
        }
    }

    private fun finish() {
        cancelJobs()
        val current = _state.value
        val asked = current.questionIndex.coerceAtLeast(1)
        val result = RoundResult(
            trailId = current.trailId,
            asked = asked,
            correct = current.correct,
            score = current.score,
            bestStreak = current.bestStreak,
            elapsedMs = System.currentTimeMillis() - startedAt,
            missedNotes = missedNotes.distinctBy { it.id },
            isNewBest = current.bestStreak > progress.bestStreak()
        )
        progress.saveRound(result)
        _state.update {
            it.copy(
                phase = GamePhase.FINISHED,
                inputLocked = true,
                paused = false,
                currentNote = null
            )
        }
        viewModelScope.launch {
            delay(GameConfig.FINISH_DELAY_MS)
            eventChannel.send(GameEvent.Finished(result))
        }
    }

    private fun cancelJobs() {
        timerJob?.cancel()
        revealJob?.cancel()
        timerJob = null
        revealJob = null
    }

    private companion object {
        const val SCORE_BASE = 10
        const val SCORE_STREAK_BONUS = 2
    }
}
