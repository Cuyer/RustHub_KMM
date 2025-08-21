package pl.cuyer.rusthub.android.feature.raid

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import kotlinx.coroutines.flow.collect
import coil3.compose.SubcomposeAsyncImage
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.designsystem.AppTextField
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.android.util.composeUtil.stringResource
import pl.cuyer.rusthub.domain.model.SteamUser
import pl.cuyer.rusthub.presentation.features.raid.RaidFormAction
import pl.cuyer.rusthub.presentation.features.raid.RaidFormState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SteamUserSearchDialog(state: RaidFormState, onAction: (RaidFormAction) -> Unit) {
    if (!state.searchDialogVisible) return

    AlertDialog(onDismissRequest = { onAction(RaidFormAction.OnDismissSearch) }) {
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
            AppTextField(
                textFieldState = queryState,
                labelText = stringResource(SharedRes.strings.steam_id),
                placeholderText = stringResource(SharedRes.strings.enter_steam_id_or_name),
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search,
                onSubmit = { onAction(RaidFormAction.OnSearchUser) },
                lineLimits = TextFieldLineLimits.SingleLine,
                maxLength = 50,
                showCharacterCounter = true
            )
            Button(onClick = { onAction(RaidFormAction.OnSearchUser) }) {
                Text(stringResource(SharedRes.strings.search))
            }
            when {
                state.searchLoading -> CircularProgressIndicator()
                state.foundUser != null -> {
                    SteamUserCard(user = state.foundUser!!) {
                        onAction(RaidFormAction.OnUserSelected(state.foundUser!!))
                    }
                }
                state.searchNotFound -> Text(stringResource(SharedRes.strings.no_user_found))
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
                model = user.avatar,
                contentDescription = null,
                modifier = Modifier,
                contentScale = ContentScale.Crop
            )
            Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
                Text(user.personaName, style = MaterialTheme.typography.titleMedium)
                Text(user.steamId, style = MaterialTheme.typography.bodyMedium)
                Text(
                    "State: ${user.personaState}",
                    color = personaStateColor(user.personaState),
                    style = MaterialTheme.typography.bodyMedium
                )
                user.lastLogoff?.let {
                    Text("Last logoff: $it", style = MaterialTheme.typography.bodySmall)
                }
                user.gameId?.let {
                    Text("Game: $it", style = MaterialTheme.typography.bodySmall)
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

