package com.treblenote.scout

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.treblenote.scout.core.di.ServiceLocator
import com.treblenote.scout.core.theme.AppTheme
import com.treblenote.scout.presentation.app.AppRoot

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        ServiceLocator.init(applicationContext)
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                AppRoot()
            }
        }
    }
}
