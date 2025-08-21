package pl.cuyer.rusthub.android.feature.raid

import android.app.Activity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.designsystem.AppButton
import pl.cuyer.rusthub.android.designsystem.AppTextButton
import pl.cuyer.rusthub.android.designsystem.AppTextField
import pl.cuyer.rusthub.android.navigation.ObserveAsEvents
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.android.util.composeUtil.stringResource
import pl.cuyer.rusthub.presentation.features.raid.RaidFormAction
import pl.cuyer.rusthub.presentation.features.raid.RaidFormState
import pl.cuyer.rusthub.presentation.navigation.UiEvent
import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import pl.cuyer.rusthub.util.formatLocalDateTime
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.compose.BindEffect
import org.koin.compose.koinInject
import androidx.compose.material3.SelectableDates
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalTime::class)
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

    val permissionsController = koinInject<PermissionsController>()
    BindEffect(permissionsController)

    val nameState = rememberSyncedTextFieldState(state.value.name)
    val descriptionState = rememberSyncedTextFieldState(state.value.description)

    LaunchedEffect(nameState) {
        snapshotFlow { nameState.text.toString() }.collect { onAction(RaidFormAction.OnNameChange(it)) }
    }
    LaunchedEffect(descriptionState) {
        snapshotFlow { descriptionState.text.toString() }
            .collect { onAction(RaidFormAction.OnDescriptionChange(it)) }
    }

    val now = remember { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()) }
    var selectedDate by remember { mutableStateOf(state.value.dateTime.substringBefore(' ')) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    val todayUtc = remember {
        Clock.System.now()
            .toLocalDateTime(TimeZone.UTC).date
            .atStartOfDayIn(TimeZone.UTC)
            .toEpochMilliseconds()
    }
    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean = utcTimeMillis >= todayUtc
        }
    )
    val timePickerState = rememberTimePickerState(
        initialHour = now.hour,
        initialMinute = now.minute,
        is24Hour = true
    )

    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val title = if (state.value.id == null) {
                        stringResource(SharedRes.strings.new_raid)
                    } else {
                        stringResource(SharedRes.strings.edit_raid)
                    }
                    Text(
                        text = title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.SemiBold
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
                .fillMaxSize()
                .clickable(interactionSource = interactionSource, indication = null) {
                    focusManager.clearFocus()
                }
        ) {
            SteamUserSearchDialog(state.value, onAction)

            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        AppTextButton(onClick = {
                            val millis = datePickerState.selectedDateMillis
                            if (millis != null) {
                                val picked = Instant.fromEpochMilliseconds(millis)
                                    .toLocalDateTime(TimeZone.currentSystemDefault()).date
                                selectedDate = "%04d-%02d-%02d".format(
                                    picked.year,
                                    picked.month.number,
                                    picked.day
                                )
                                showTimePicker = true
                            }
                            showDatePicker = false
                        }) { Text(stringResource(SharedRes.strings.ok)) }
                    },
                    dismissButton = {
                        AppTextButton(onClick = { showDatePicker = false }) {
                            Text(stringResource(SharedRes.strings.cancel))
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            if (showTimePicker) {
                TimePickerDialog(
                    onDismissRequest = { showTimePicker = false },
                    confirmButton = {
                        AppTextButton(onClick = {
                            showTimePicker = false
                            onAction(
                                RaidFormAction.OnDateTimeChange(
                                    "$selectedDate %02d:%02d".format(
                                        timePickerState.hour,
                                        timePickerState.minute
                                    )
                                )
                            )
                        }) { Text(stringResource(SharedRes.strings.ok)) }
                    },
                    title = { Text(stringResource(SharedRes.strings.select_time)) },
                    dismissButton = {
                        AppTextButton(onClick = { showTimePicker = false }) {
                            Text(stringResource(SharedRes.strings.cancel))
                        }
                    }
                ) {
                    TimePicker(state = timePickerState)
                }
            }

            val scrollModifier = Modifier.verticalScroll(rememberScrollState())
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(spacing.medium)
                    .then(scrollModifier),
                verticalArrangement = Arrangement.spacedBy(spacing.medium)
            ) {
                Text(
                    text = stringResource(SharedRes.strings.raid_form_info),
                    style = MaterialTheme.typography.bodyMedium
                )
                AppTextField(
                    modifier = Modifier.fillMaxWidth(),
                    textFieldState = nameState,
                    labelText = stringResource(SharedRes.strings.raid_name),
                    placeholderText = "",
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done,
                    isError = state.value.nameError,
                    errorText = stringResource(SharedRes.strings.required),
                    maxLength = 50,
                    showCharacterCounter = true,
                    focusManager = focusManager
                )
                val formattedDate = remember(state.value.dateTime) {
                    try {
                        formatLocalDateTime(
                            LocalDateTime.parse(state.value.dateTime.replace(' ', 'T'))
                        )
                    } catch (e: Exception) {
                        state.value.dateTime
                    }
                }
                OutlinedTextField(
                    value = formattedDate,
                    trailingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                    onValueChange = {},
                    label = { Text(stringResource(SharedRes.strings.raid_date_time)) },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusEvent {
                            if (it.isFocused) {
                                showDatePicker = true
                                focusManager.clearFocus()
                            }
                        }
                )
                OutlinedTextField(
                    value = state.value.steamIds.joinToString(", "),
                    onValueChange = {},
                    label = { Text(stringResource(SharedRes.strings.raid_target)) },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusEvent {
                            if (it.isFocused) {
                                onAction(RaidFormAction.OnSelectTargetClick)
                                focusManager.clearFocus()
                            }
                        }
                )
                AppTextField(
                    focusManager = focusManager,
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

                AppButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
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
