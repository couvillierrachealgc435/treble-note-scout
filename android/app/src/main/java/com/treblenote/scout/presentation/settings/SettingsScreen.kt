package com.treblenote.scout.presentation.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.treblenote.scout.R
import com.treblenote.scout.core.di.ServiceLocator
import com.treblenote.scout.core.theme.CardPaper
import com.treblenote.scout.core.theme.ForestText
import com.treblenote.scout.core.theme.PaperMoss
import com.treblenote.scout.core.theme.PaperMossDeep
import com.treblenote.scout.core.theme.PaperMossShade
import com.treblenote.scout.core.theme.PinePrimary
import com.treblenote.scout.core.theme.SageSecondary
import com.treblenote.scout.core.theme.SplashPaper
import com.treblenote.scout.core.theme.TrailAccent
import com.treblenote.scout.core.ui.AppBackground
import com.treblenote.scout.core.ui.GlassCard
import com.treblenote.scout.core.ui.SecondaryButton
import com.treblenote.scout.core.ui.SectionRule
import com.treblenote.scout.domain.model.AnswerWindow

@Composable
fun SettingsRoute(onMenu: () -> Unit) {
    val settingsViewModel: SettingsViewModel = viewModel(factory = ServiceLocator.viewModelFactory)
    val state by settingsViewModel.state.collectAsStateWithLifecycle()
    SettingsScreen(
        state = state,
        onTimer = settingsViewModel::setTimer,
        onTone = settingsViewModel::setTone,
        onLabels = settingsViewModel::setLabels,
        onHaptics = settingsViewModel::setHaptics,
        onWindow = settingsViewModel::setAnswerWindow,
        onMenu = onMenu
    )
}

@Composable
fun SettingsScreen(
    state: SettingsUiState,
    onTimer: (Boolean) -> Unit,
    onTone: (Boolean) -> Unit,
    onLabels: (Boolean) -> Unit,
    onHaptics: (Boolean) -> Unit,
    onWindow: (AnswerWindow) -> Unit,
    onMenu: () -> Unit
) {
    AppBackground(
        overlay = listOf(
            PaperMoss.copy(alpha = 0.96f),
            PaperMossDeep.copy(alpha = 0.95f),
            PaperMossShade.copy(alpha = 0.96f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 20.dp)
        ) {
            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = stringResource(R.string.settings_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = ForestText
                )
            }
            SectionRule()

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(14.dp))

                GlassCard(contentPadding = 8.dp) {
                    SettingRow(
                        label = stringResource(R.string.setting_timer),
                        checked = state.settings.timerEnabled,
                        onChange = onTimer
                    )
                    SettingRow(
                        label = stringResource(R.string.setting_tone),
                        checked = state.settings.toneEnabled,
                        onChange = onTone
                    )
                    SettingRow(
                        label = stringResource(R.string.setting_labels),
                        checked = state.settings.labelsEnabled,
                        onChange = onLabels
                    )
                    SettingRow(
                        label = stringResource(R.string.setting_haptics),
                        checked = state.settings.hapticsEnabled,
                        onChange = onHaptics
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = stringResource(R.string.setting_answer_window),
                    style = MaterialTheme.typography.labelLarge,
                    color = PinePrimary,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    state.windows.forEach { window ->
                        val active = state.settings.answerWindow == window
                        val label = when (window) {
                            AnswerWindow.RELAXED -> stringResource(R.string.window_relaxed)
                            AnswerWindow.STEADY -> stringResource(R.string.window_steady)
                            AnswerWindow.BRISK -> stringResource(R.string.window_brisk)
                        }
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .background(
                                    color = if (active) SageSecondary else CardPaper.copy(alpha = 0.7f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .border(
                                    border = BorderStroke(
                                        width = if (active) 1.5.dp else 1.dp,
                                        color = if (active) TrailAccent else SageSecondary
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { onWindow(window) }
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (active) ForestText else PinePrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = stringResource(R.string.settings_note),
                    style = MaterialTheme.typography.bodyMedium,
                    color = ForestText.copy(alpha = 0.75f)
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            SecondaryButton(
                text = stringResource(R.string.menu),
                onClick = onMenu
            )

            Spacer(modifier = Modifier.height(14.dp))
        }
    }
}

@Composable
private fun SettingRow(
    label: String,
    checked: Boolean,
    onChange: (Boolean) -> Unit
) {
    val onLabel = stringResource(R.string.state_on)
    val offLabel = stringResource(R.string.state_off)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 8.dp)
            .semantics { stateDescription = if (checked) onLabel else offLabel }
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = ForestText
        )
        Switch(
            checked = checked,
            onCheckedChange = onChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = SplashPaper,
                checkedTrackColor = PinePrimary,
                uncheckedThumbColor = PinePrimary,
                uncheckedTrackColor = SageSecondary
            )
        )
    }
}
