package com.treblenote.scout.presentation.noteguide

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.treblenote.scout.R
import com.treblenote.scout.core.di.ServiceLocator
import com.treblenote.scout.core.theme.ForestText
import com.treblenote.scout.core.theme.PaperMoss
import com.treblenote.scout.core.theme.PaperMossDeep
import com.treblenote.scout.core.theme.PaperMossShade
import com.treblenote.scout.core.theme.PinePrimary
import com.treblenote.scout.core.theme.TrailAccent
import com.treblenote.scout.core.ui.AppBackground
import com.treblenote.scout.core.ui.GlassCard
import com.treblenote.scout.core.ui.SecondaryButton
import com.treblenote.scout.core.ui.SectionRule
import com.treblenote.scout.core.ui.StaffBoard
import com.treblenote.scout.core.ui.StaffNote
import com.treblenote.scout.domain.model.Clef

@Composable
fun NoteGuideRoute(onMenu: () -> Unit) {
    val noteGuideViewModel: NoteGuideViewModel = viewModel(factory = ServiceLocator.viewModelFactory)
    val state by noteGuideViewModel.state.collectAsStateWithLifecycle()
    NoteGuideScreen(state = state, onMenu = onMenu)
}

@Composable
fun NoteGuideScreen(
    state: NoteGuideUiState,
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = stringResource(R.string.noteguide_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = ForestText
                )
                Image(
                    painter = painterResource(R.drawable.sprite_5),
                    contentDescription = stringResource(R.string.cd_keyboard_chart),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(72.dp)
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

                Text(
                    text = stringResource(R.string.noteguide_treble),
                    style = MaterialTheme.typography.labelLarge,
                    color = PinePrimary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                StaffBoard(
                    clef = Clef.TREBLE,
                    notes = state.trebleNotes.map { StaffNote(note = it, tint = ForestText) },
                    showLabels = true,
                    modifier = Modifier
                        .widthIn(max = 380.dp)
                        .fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = stringResource(R.string.noteguide_bass),
                    style = MaterialTheme.typography.labelLarge,
                    color = PinePrimary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                StaffBoard(
                    clef = Clef.BASS,
                    notes = state.bassNotes.map { StaffNote(note = it, tint = ForestText) },
                    showLabels = true,
                    modifier = Modifier
                        .widthIn(max = 380.dp)
                        .fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = stringResource(R.string.noteguide_phrases),
                    style = MaterialTheme.typography.labelLarge,
                    color = PinePrimary,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                state.phrases.forEach { phrase ->
                    GlassCard(
                        contentPadding = 14.dp,
                        modifier = Modifier.padding(bottom = 10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = phrase.initials,
                                style = MaterialTheme.typography.titleLarge,
                                color = TrailAccent
                            )
                            Column(modifier = Modifier.padding(start = 14.dp)) {
                                Text(
                                    text = stringResource(phrase.titleRes),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PinePrimary
                                )
                                Text(
                                    text = stringResource(phrase.phraseRes),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = ForestText,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            SecondaryButton(
                text = stringResource(R.string.menu),
                onClick = onMenu
            )

            Spacer(modifier = Modifier.height(14.dp))
        }
    }
}
