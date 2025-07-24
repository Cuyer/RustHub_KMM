package pl.cuyer.rusthub.android.feature.settings

import android.app.Activity
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.animateBounds
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.designsystem.AppButton
import pl.cuyer.rusthub.android.designsystem.AppSecureTextField
import pl.cuyer.rusthub.android.navigation.ObserveAsEvents
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.android.util.composeUtil.keyboardAsState
import pl.cuyer.rusthub.common.getImageByFileName
import pl.cuyer.rusthub.presentation.features.auth.password.ChangePasswordAction
import pl.cuyer.rusthub.presentation.features.auth.password.ChangePasswordState
import pl.cuyer.rusthub.presentation.navigation.UiEvent
import pl.cuyer.rusthub.android.util.composeUtil.stringResource
import androidx.compose.runtime.snapshotFlow

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class,
    ExperimentalSharedTransitionApi::class
)
@Composable
fun ChangePasswordScreen(
    onNavigateUp: () -> Unit,
    uiEvent: Flow<UiEvent>,
    state: State<ChangePasswordState>,
    onAction: (ChangePasswordAction) -> Unit
) {
    ObserveAsEvents(uiEvent) { event ->
        if (event is UiEvent.NavigateUp) onNavigateUp()
    }

    val context = LocalContext.current
    val windowSizeClass = calculateWindowSizeClass(context as Activity)
    val isTabletMode = windowSizeClass.widthSizeClass >= WindowWidthSizeClass.Medium
    val interactionSource = remember { MutableInteractionSource() }
    val focusManager = LocalFocusManager.current

    Scaffold(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        topBar = {
            TopAppBar(
                title = { Text(
                    text = stringResource(SharedRes.strings.change_password),
                    fontWeight = FontWeight.SemiBold
                ) },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateUp,
                        modifier = Modifier.minimumInteractiveComponentSize()
                    ) {
                        Icon(
                            tint = contentColorFor(TopAppBarDefaults.topAppBarColors().containerColor),
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(SharedRes.strings.navigate_up)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LookaheadScope {
            if (isTabletMode) {
                ChangePasswordScreenExpanded(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(spacing.medium)
                        .animateBounds(this)
                        .clickable(interactionSource, null) { focusManager.clearFocus() },
                    oldPassword = { state.value.oldPassword },
                    newPassword = { state.value.newPassword },
                    oldPasswordError = { state.value.oldPasswordError },
                    newPasswordError = { state.value.newPasswordError },
                    isLoading = { state.value.isLoading },
                    onAction = onAction
                )
            } else {
                ChangePasswordScreenCompact(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(innerPadding)
                        .padding(spacing.medium)
                        .animateBounds(this)
                        .clickable(interactionSource, null) { focusManager.clearFocus() },
                    oldPassword = { state.value.oldPassword },
                    newPassword = { state.value.newPassword },
                    oldPasswordError = { state.value.oldPasswordError },
                    newPasswordError = { state.value.newPasswordError },
                    isLoading = { state.value.isLoading },
                    onAction = onAction
                )
            }
        }
    }
}

@Composable
private fun ChangePasswordScreenCompact(
    modifier: Modifier = Modifier,
    oldPassword: () -> String,
    newPassword: () -> String,
    oldPasswordError: () -> String?,
    newPasswordError: () -> String?,
    isLoading: () -> Boolean,
    onAction: (ChangePasswordAction) -> Unit
) {
    val focusManager = LocalFocusManager.current
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
    val oldState = rememberTextFieldState(oldPassword())
    LaunchedEffect(oldPassword()) { oldState.setTextAndPlaceCursorAtEnd(oldPassword()) }
    val newState = rememberTextFieldState(newPassword())
    LaunchedEffect(newPassword()) { newState.setTextAndPlaceCursorAtEnd(newPassword()) }


    LaunchedEffect(oldState) {
        snapshotFlow { oldState.text }
            .collect { typed ->
                onAction(ChangePasswordAction.OnOldPasswordChange(typed.toString()))
            }
    }

    LaunchedEffect(newState) {
        snapshotFlow { newState.text }
            .collect { typed ->
                onAction(ChangePasswordAction.OnNewPasswordChange(typed.toString()))
            }
    }
        ChangePasswordStaticContent()
        ChangePasswordFields(
            oldPasswordState = oldState,
            newPasswordState = newState,
            oldPasswordError = oldPasswordError,
            newPasswordError = newPasswordError,
            onAction = onAction,
            focusManager = focusManager
        )
        AppButton(
            modifier = Modifier
                .imePadding()
                .fillMaxWidth(),
            enabled = { oldState.text.isNotBlank() && newState.text.isNotBlank() },
            isLoading = isLoading,
            onClick = {
                focusManager.clearFocus()
                onAction(ChangePasswordAction.OnChange)
            }
        ) { Text(stringResource(SharedRes.strings.change_password)) }
    }
}

@Composable
private fun ChangePasswordScreenExpanded(
    modifier: Modifier = Modifier,
    oldPassword: () -> String,
    newPassword: () -> String,
    oldPasswordError: () -> String?,
    newPasswordError: () -> String?,
    isLoading: () -> Boolean,
    onAction: (ChangePasswordAction) -> Unit
) {
    val focusManager = LocalFocusManager.current
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ChangePasswordStaticContent(modifier = Modifier.weight(1f))
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(spacing.small)
        ) {
            val oldState = rememberTextFieldState(oldPassword())
            LaunchedEffect(oldPassword()) { oldState.setTextAndPlaceCursorAtEnd(oldPassword()) }
            val newState = rememberTextFieldState(newPassword())
            LaunchedEffect(newPassword()) { newState.setTextAndPlaceCursorAtEnd(newPassword()) }

            LaunchedEffect(oldState) {
                snapshotFlow { oldState.text }
                    .collect { typed ->
                        onAction(ChangePasswordAction.OnOldPasswordChange(typed.toString()))
                    }
            }

            LaunchedEffect(newState) {
                snapshotFlow { newState.text }
                    .collect { typed ->
                        onAction(ChangePasswordAction.OnNewPasswordChange(typed.toString()))
                    }
            }
            ChangePasswordFields(
                oldPasswordState = oldState,
                newPasswordState = newState,
                oldPasswordError = oldPasswordError,
                newPasswordError = newPasswordError,
                onAction = onAction,
                focusManager = focusManager
            )
            AppButton(
                modifier = Modifier
                    .imePadding()
                    .fillMaxWidth(),
                isLoading = isLoading,
                onClick = {
                    focusManager.clearFocus()
                    onAction(ChangePasswordAction.OnChange)
                }
            ) { Text(stringResource(SharedRes.strings.change_password)) }
        }
    }
}

@Composable
private fun ChangePasswordStaticContent(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Icon(
            modifier = Modifier.size(64.dp),
            painter = painterResource(getImageByFileName("ic_padlock").drawableResId),
            contentDescription = stringResource(SharedRes.strings.padlock_icon)
        )
        Spacer(modifier = Modifier.height(spacing.small))
        Text(
            text = stringResource(SharedRes.strings.enter_your_current_password_and_pick_a_new_one),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun ChangePasswordFields(
    oldPasswordState: TextFieldState,
    newPasswordState: TextFieldState,
    oldPasswordError: () -> String?,
    newPasswordError: () -> String?,
    onAction: (ChangePasswordAction) -> Unit,
    focusManager: FocusManager
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        val keyboardState = keyboardAsState()
        AppSecureTextField(
            requestFocus = true,
            textFieldState = oldPasswordState,
            labelText = stringResource(SharedRes.strings.old_password),
            placeholderText = stringResource(SharedRes.strings.enter_old_password),
            isError = oldPasswordError() != null,
            errorText = oldPasswordError(),
            modifier = Modifier.fillMaxWidth(),
            imeAction = ImeAction.Next,
            onSubmit = { },
            focusManager = focusManager,
            keyboardState = keyboardState
        )
        AppSecureTextField(
            textFieldState = newPasswordState,
            labelText = stringResource(SharedRes.strings.new_password),
            placeholderText = stringResource(SharedRes.strings.enter_new_password),
            onSubmit = {
                focusManager.clearFocus()
                onAction(ChangePasswordAction.OnChange)
            },
            isError = newPasswordError() != null,
            errorText = newPasswordError(),
            modifier = Modifier.fillMaxWidth(),
            imeAction = if (oldPasswordState.text.isNotBlank() && newPasswordState.text.isNotBlank()) ImeAction.Send else ImeAction.Done,
            focusManager = focusManager,
            keyboardState = keyboardState
        )
    }
}
