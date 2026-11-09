package com.treblenote.scout.core.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.treblenote.scout.data.audio.AudioTrackTonePlayer
import com.treblenote.scout.data.local.ScoutPreferences
import com.treblenote.scout.data.repository.ProgressRepositoryImpl
import com.treblenote.scout.data.repository.SettingsRepositoryImpl
import com.treblenote.scout.data.repository.TrailRepositoryImpl
import com.treblenote.scout.domain.usecase.BuildQuestionsUseCase
import com.treblenote.scout.domain.usecase.EvaluateAnswerUseCase
import com.treblenote.scout.domain.usecase.UnlockTrailsUseCase

object ServiceLocator {

    private var factory: ViewModelProvider.Factory? = null

    fun init(context: Context) {
        if (factory != null) return
        val preferences = ScoutPreferences(context.applicationContext)
        val progress = ProgressRepositoryImpl(preferences)
        factory = AppViewModelFactory.create(
            progress = progress,
            settings = SettingsRepositoryImpl(preferences),
            trails = TrailRepositoryImpl(),
            tonePlayer = AudioTrackTonePlayer(),
            buildQuestions = BuildQuestionsUseCase(progress),
            evaluateAnswer = EvaluateAnswerUseCase(),
            unlockTrails = UnlockTrailsUseCase(progress)
        )
    }

    val viewModelFactory: ViewModelProvider.Factory
        get() = checkNotNull(factory)
}
