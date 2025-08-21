package pl.cuyer.rusthub.android.feature.raid

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.designsystem.AppButton
import pl.cuyer.rusthub.android.designsystem.AppTextField
import pl.cuyer.rusthub.android.navigation.ObserveAsEvents
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.android.util.composeUtil.stringResource
import pl.cuyer.rusthub.presentation.features.raid.RaidFormAction
import pl.cuyer.rusthub.presentation.features.raid.RaidFormState
import pl.cuyer.rusthub.presentation.navigation.UiEvent
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RaidFormScreen(
    onNavigateUp: () -> Unit,
    state: State<RaidFormState>,
    onAction: (RaidFormAction) -> Unit,
    uiEvent: Flow<UiEvent>,
) {
    ObserveAsEvents(uiEvent) { event ->
        if (event is UiEvent.NavigateUp) onNavigateUp()
    }

    val context = LocalContext.current
    val nameState = rememberSyncedTextFieldState(state.value.name)
    val descriptionState = rememberSyncedTextFieldState(state.value.description)

    LaunchedEffect(nameState) {
        snapshotFlow { nameState.text.toString() }.collect { onAction(RaidFormAction.OnNameChange(it)) }
    }
    LaunchedEffect(descriptionState) {
        snapshotFlow { descriptionState.text.toString() }
            .collect { onAction(RaidFormAction.OnDescriptionChange(it)) }
    }

    SteamUserSearchDialog(state.value, onAction)

    val calendar = remember { Calendar.getInstance() }
    val selectedDate = remember { mutableStateOf(state.value.dateTime.substringBefore(' ')) }
    val timePicker = remember {
        TimePickerDialog(
            context,
            { _, hour, minute ->
                onAction(
                    RaidFormAction.OnDateTimeChange(
                        "${selectedDate.value} %02d:%02d".format(hour, minute)
                    )
                )
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
    }
    val datePicker = remember {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                selectedDate.value = "%04d-%02d-%02d".format(year, month + 1, day)
                timePicker.show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.value.name.ifBlank { stringResource(SharedRes.strings.new_raid) },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .padding(spacing.medium)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(spacing.medium)
            ) {
                AppTextField(
                    modifier = Modifier.fillMaxWidth(),
                    textFieldState = nameState,
                    labelText = stringResource(SharedRes.strings.raid_name),
                    placeholderText = "",
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                    isError = state.value.nameError,
                    errorText = stringResource(SharedRes.strings.required),
                    maxLength = 50,
                    showCharacterCounter = true
                )
                OutlinedTextField(
                    value = state.value.dateTime,
                    onValueChange = {},
                    label = { Text(stringResource(SharedRes.strings.raid_date_time)) },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { datePicker.show() }
                )
                OutlinedTextField(
                    value = state.value.steamId,
                    onValueChange = {},
                    label = { Text(stringResource(SharedRes.strings.raid_target)) },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onAction(RaidFormAction.OnSelectTargetClick) }
                )
                AppTextField(
                    modifier = Modifier.fillMaxWidth(),
                    textFieldState = descriptionState,
                    labelText = stringResource(SharedRes.strings.description),
                    placeholderText = "",
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done,
                    maxLength = 200,
                    lineLimits = TextFieldLineLimits.MultiLine(),
                    showCharacterCounter = true
                )
            }
            AppButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .align(Alignment.BottomCenter),
                onClick = { onAction(RaidFormAction.OnSave) },
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = contentColorFor(MaterialTheme.colorScheme.surfaceContainer)
                ),
            ) {
                Text(stringResource(SharedRes.strings.save))
            }
        }
    }
}

@Composable
private fun rememberSyncedTextFieldState(value: String): TextFieldState {
    val state = rememberTextFieldState(value)
    LaunchedEffect(value) {
        if (state.text.toString() != value) {
            state.setTextAndPlaceCursorAtEnd(value)
        }
    }
    return state
}
