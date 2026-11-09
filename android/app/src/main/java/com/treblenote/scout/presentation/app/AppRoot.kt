package com.treblenote.scout.presentation.app

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.treblenote.scout.core.di.ServiceLocator
import com.treblenote.scout.core.navigation.Screen
import com.treblenote.scout.presentation.game.GameRoute
import com.treblenote.scout.presentation.gameover.GameOverRoute
import com.treblenote.scout.presentation.menu.MenuRoute
import com.treblenote.scout.presentation.noteguide.NoteGuideRoute
import com.treblenote.scout.presentation.settings.SettingsRoute
import com.treblenote.scout.presentation.splash.SplashRoute
import com.treblenote.scout.presentation.trailmap.TrailMapRoute

@Composable
fun AppRoot() {
    val appViewModel: AppViewModel = viewModel(factory = ServiceLocator.viewModelFactory)
    val screen by appViewModel.screen.collectAsStateWithLifecycle()
    val canGoBack by appViewModel.canGoBack.collectAsStateWithLifecycle()

    BackHandler(enabled = canGoBack) { appViewModel.back() }

    AnimatedContent(
        targetState = screen,
        modifier = Modifier.fillMaxSize(),
        transitionSpec = {
            if (targetState is Screen.Game) {
                slideInHorizontally(
                    animationSpec = tween(280)
                ) { full -> full / 4 } + fadeIn(tween(280)) togetherWith fadeOut(tween(220))
            } else {
                fadeIn(tween(420)) togetherWith fadeOut(tween(300))
            }
        },
        label = "screen"
    ) { target ->
        when (target) {
            is Screen.Splash -> SplashRoute(
                onFinished = { appViewModel.replace(Screen.Menu) }
            )

            is Screen.Menu -> MenuRoute(
                onPlay = { trailId ->
                    appViewModel.navigate(
                        Screen.Game(trailId, false, System.currentTimeMillis())
                    )
                },
                onTrailMap = { appViewModel.navigate(Screen.TrailMap) },
                onScoutKit = { appViewModel.navigate(Screen.Settings) },
                onGuide = { appViewModel.navigate(Screen.NoteGuide) }
            )

            is Screen.TrailMap -> TrailMapRoute(
                onStartTrail = { trailId ->
                    appViewModel.navigate(
                        Screen.Game(trailId, false, System.currentTimeMillis())
                    )
                },
                onMenu = { appViewModel.backToMenu() }
            )

            is Screen.NoteGuide -> NoteGuideRoute(
                onMenu = { appViewModel.backToMenu() }
            )

            is Screen.Settings -> SettingsRoute(
                onMenu = { appViewModel.backToMenu() }
            )

            is Screen.Game -> GameRoute(
                trailId = target.trailId,
                weakNotesOnly = target.weakNotesOnly,
                runId = target.runId,
                onHome = { appViewModel.backToMenu() },
                onFinished = { result -> appViewModel.replace(Screen.GameOver(result)) }
            )

            is Screen.GameOver -> GameOverRoute(
                result = target.result,
                onScoutAgain = {
                    appViewModel.replace(
                        Screen.Game(target.result.trailId, false, System.currentTimeMillis())
                    )
                },
                onWeakCamp = {
                    appViewModel.replace(
                        Screen.Game(target.result.trailId, true, System.currentTimeMillis())
                    )
                },
                onMenu = { appViewModel.backToMenu() }
            )
        }
    }
}
