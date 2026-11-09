package com.treblenote.scout.presentation.app

import androidx.lifecycle.ViewModel
import com.treblenote.scout.core.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppViewModel : ViewModel() {

    private val backStack = mutableListOf<Screen>()

    private val _screen = MutableStateFlow<Screen>(Screen.Splash)
    val screen: StateFlow<Screen> = _screen.asStateFlow()

    private val _canGoBack = MutableStateFlow(false)
    val canGoBack: StateFlow<Boolean> = _canGoBack.asStateFlow()

    fun navigate(target: Screen) {
        val current = _screen.value
        if (current != Screen.Splash) {
            backStack.add(current)
        }
        _screen.value = target
        syncBack()
    }

    fun replace(target: Screen) {
        _screen.value = target
        syncBack()
    }

    fun back() {
        if (backStack.isEmpty()) return
        val previous = backStack.removeAt(backStack.lastIndex)
        _screen.value = previous
        syncBack()
    }

    fun backToMenu() {
        backStack.clear()
        _screen.value = Screen.Menu
        syncBack()
    }

    private fun syncBack() {
        _canGoBack.value = backStack.isNotEmpty()
    }
}
