package com.treblenote.scout.core.di

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.treblenote.scout.domain.repository.ProgressRepository
import com.treblenote.scout.domain.repository.SettingsRepository
import com.treblenote.scout.domain.repository.TonePlayer
import com.treblenote.scout.domain.repository.TrailRepository
import com.treblenote.scout.domain.usecase.BuildQuestionsUseCase
import com.treblenote.scout.domain.usecase.EvaluateAnswerUseCase
import com.treblenote.scout.domain.usecase.UnlockTrailsUseCase
import com.treblenote.scout.presentation.app.AppViewModel
import com.treblenote.scout.presentation.game.GameViewModel
import com.treblenote.scout.presentation.gameover.GameOverViewModel
import com.treblenote.scout.presentation.menu.MenuViewModel
import com.treblenote.scout.presentation.noteguide.NoteGuideViewModel
import com.treblenote.scout.presentation.settings.SettingsViewModel
import com.treblenote.scout.presentation.splash.SplashViewModel
import com.treblenote.scout.presentation.trailmap.TrailMapViewModel

object AppViewModelFactory {

    fun create(
        progress: ProgressRepository,
        settings: SettingsRepository,
        trails: TrailRepository,
        tonePlayer: TonePlayer,
        buildQuestions: BuildQuestionsUseCase,
        evaluateAnswer: EvaluateAnswerUseCase,
        unlockTrails: UnlockTrailsUseCase
    ): ViewModelProvider.Factory = viewModelFactory {
        initializer { AppViewModel() }
        initializer { SplashViewModel() }
        initializer { MenuViewModel(progress, trails, unlockTrails) }
        initializer { TrailMapViewModel(progress, trails, unlockTrails) }
        initializer {
            GameViewModel(progress, settings, tonePlayer, trails, buildQuestions, evaluateAnswer)
        }
        initializer { GameOverViewModel(progress, trails) }
        initializer { NoteGuideViewModel(trails) }
        initializer { SettingsViewModel(settings) }
    }
}
