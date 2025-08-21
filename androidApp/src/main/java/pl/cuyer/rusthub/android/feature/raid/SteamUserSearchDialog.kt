package pl.cuyer.rusthub.android.feature.raid

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.flow.collect
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.designsystem.AppTextField
import pl.cuyer.rusthub.android.designsystem.shimmer
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.android.util.composeUtil.stringResource
import pl.cuyer.rusthub.domain.model.SteamUser
import pl.cuyer.rusthub.presentation.features.raid.RaidFormAction
import pl.cuyer.rusthub.presentation.features.raid.RaidFormState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SteamUserSearchDialog(state: RaidFormState, onAction: (RaidFormAction) -> Unit) {
    if (!state.searchDialogVisible) return

    BasicAlertDialog(
        onDismissRequest = { onAction(RaidFormAction.OnDismissSearch) },
        properties = DialogProperties()
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
        ) {
            Column(
                modifier = Modifier.padding(spacing.medium),
                verticalArrangement = Arrangement.spacedBy(spacing.medium)
            ) {
                val queryState = rememberTextFieldState(state.searchQuery)
                LaunchedEffect(state.searchQuery) {
                    queryState.setTextAndPlaceCursorAtEnd(state.searchQuery)
                }
                LaunchedEffect(queryState) {
                    snapshotFlow { queryState.text.toString() }
                        .collect { onAction(RaidFormAction.OnSearchQueryChange(it)) }
                }
                Text(
                    text = stringResource(SharedRes.strings.search),
                    fontWeight = FontWeight.SemiBold
                )
                AppTextField(
                    textFieldState = queryState,
                    labelText = stringResource(SharedRes.strings.steam_id),
                    placeholderText = stringResource(SharedRes.strings.enter_steam_id_or_name),
                    keyboardType = KeyboardType.NumberPassword,
                    imeAction = ImeAction.Search,
                    onSubmit = { onAction(RaidFormAction.OnSearchUser) },
                    lineLimits = TextFieldLineLimits.SingleLine,
                    maxLength = 50,
                    showCharacterCounter = true,
                    requestFocus = true
                )
                when {
                    state.searchLoading -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    state.foundUser != null -> {
                        SteamUserCard(user = state.foundUser!!) {
                            onAction(RaidFormAction.OnUserSelected(state.foundUser!!))
                        }
                    }

                    state.searchNotFound -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(stringResource(SharedRes.strings.no_user_found))
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.medium, Alignment.End)
                ) {
                    TextButton(
                        modifier = Modifier
                            .height(48.dp)
                            .wrapContentWidth(),
                        shape = MaterialTheme.shapes.extraSmall,
                        onClick = {
                            onAction(RaidFormAction.OnDismissSearch)
                        }
                    ) {
                        Text(stringResource(SharedRes.strings.cancel))
                    }
                    Button(
                        modifier = Modifier
                            .wrapContentWidth()
                            .height(48.dp),
                        shape = MaterialTheme.shapes.extraSmall,
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer,
                            contentColor = contentColorFor(MaterialTheme.colorScheme.surfaceContainer)
                        ),
                        onClick = {
                            onAction(RaidFormAction.OnSearchUser)
                        }
                    ) {
                        Text(stringResource(SharedRes.strings.search))
                    }
                }
            }
        }
    }
}

@Composable
private fun SteamUserCard(user: SteamUser, onClick: () -> Unit) {
    ElevatedCard(onClick = onClick) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.medium),
            horizontalArrangement = Arrangement.spacedBy(spacing.medium)
        ) {
            SubcomposeAsyncImage(
                modifier = Modifier.size(48.dp),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(user.avatar)
                    .crossfade(true)
                    .build(),
                contentDescription = "User avatar",
                loading = {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .shimmer()
                    )
                }
            )
            Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
                val offline = stringResource(SharedRes.strings.offline)
                val online = stringResource(SharedRes.strings.online)
                val busy = stringResource(SharedRes.strings.busy)
                val away = stringResource(SharedRes.strings.away)
                val snooze = stringResource(SharedRes.strings.snooze)
                val lookingToTrade = stringResource(SharedRes.strings.looking_to_trade)
                val lookingToPlay = stringResource(SharedRes.strings.looking_to_play)
                val unknown = stringResource(SharedRes.strings.unknown)
                Text(user.personaName, style = MaterialTheme.typography.titleMedium)
                Text(user.steamId, style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = when(user.personaState) {
                        0 -> offline
                        1 -> online
                        2 -> busy
                        3 -> away
                        4 -> snooze
                        5 -> lookingToTrade
                        6 -> lookingToPlay
                        else -> unknown
                    },
                    color = personaStateColor(user.personaState),
                    style = MaterialTheme.typography.bodyMedium
                )
                user.lastLogoff?.let {
                    Text(
                        stringResource(SharedRes.strings.last_logoff, it),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                user.gameId?.let {
                    Text(
                        stringResource(SharedRes.strings.game, it),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

private fun personaStateColor(state: Int): Color = when (state) {
    1 -> Color(0xFF4CAF50)
    2 -> Color(0xFFF44336)
    3 -> Color(0xFFFFC107)
    else -> Color.Gray
}

