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
import androidx.compose.foundation.layout.consumeWindowInsets
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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.util.composeUtil.stringResource
import pl.cuyer.rusthub.android.designsystem.AppButton
import pl.cuyer.rusthub.android.designsystem.AppSecureTextField
import pl.cuyer.rusthub.android.theme.spacing
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.getValue
import androidx.compose.ui.focus.FocusManager
import pl.cuyer.rusthub.android.util.composeUtil.keyboardAsState
import pl.cuyer.rusthub.common.getImageByFileName
import pl.cuyer.rusthub.domain.model.AuthProvider
import pl.cuyer.rusthub.presentation.features.auth.delete.DeleteAccountAction
import pl.cuyer.rusthub.presentation.features.auth.delete.DeleteAccountState
import pl.cuyer.rusthub.android.designsystem.AppTextButton

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class,
    ExperimentalSharedTransitionApi::class
)
@Composable
fun DeleteAccountScreen(
    onNavigateUp: () -> Unit,
    state: State<DeleteAccountState>,
    onAction: (DeleteAccountAction) -> Unit
) {
    val context = LocalContext.current
    val windowSizeClass = calculateWindowSizeClass(context as Activity)
    val isTabletMode = windowSizeClass.widthSizeClass >= WindowWidthSizeClass.Medium
    val interactionSource = remember { MutableInteractionSource() }
    val focusManager = LocalFocusManager.current
    val currentState = state.value

    if (currentState.showSubscriptionDialog) {
        AlertDialog(
            onDismissRequest = { onAction(DeleteAccountAction.OnDismissDialog) },
            title = { Text(text = stringResource(SharedRes.strings.delete_account)) },
            text = { Text(text = stringResource(SharedRes.strings.delete_account_subscription_warning)) },
            confirmButton = {
                AppButton(onClick = { onAction(DeleteAccountAction.OnConfirmDelete) }) {
                    Text(stringResource(SharedRes.strings.delete))
                }
            },
            dismissButton = {
                AppTextButton(onClick = { onAction(DeleteAccountAction.OnDismissDialog) }) {
                    Text(stringResource(SharedRes.strings.cancel))
                }
            }
        )
    }

    Scaffold(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        topBar = {
            TopAppBar(
                title = { Text(
                    text = stringResource(SharedRes.strings.delete_account),
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
        if (isTabletMode) {
            DeleteAccountScreenExpanded(
                modifier = Modifier
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
                    .fillMaxSize()
                    .padding(spacing.medium)
                    .semantics { hideFromAccessibility() }
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) { focusManager.clearFocus() },
                provider = currentState.provider,
                password = currentState.password,
                passwordError = currentState.passwordError,
                isLoading = currentState.isLoading,
                onAction = onAction
            )
        } else {
            DeleteAccountScreenCompact(
                modifier = Modifier
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(spacing.medium)
                    .semantics { hideFromAccessibility() }
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) { focusManager.clearFocus() },
                provider = currentState.provider,
                password = currentState.password,
                passwordError = currentState.passwordError,
                isLoading = currentState.isLoading,
                onAction = onAction
            )
        }
    }
}

@Composable
private fun DeleteAccountScreenCompact(
    modifier: Modifier = Modifier,
    provider: AuthProvider?,
    password: String,
    passwordError: String?,
    isLoading: Boolean,
    onAction: (DeleteAccountAction) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val passState = rememberSyncedTextFieldState(password)
    val latestAction = rememberUpdatedState(onAction)
    val onDelete = remember(focusManager) {
        {
            focusManager.clearFocus()
            latestAction.value(DeleteAccountAction.OnPasswordChange(passState.text.toString()))
            latestAction.value(DeleteAccountAction.OnDelete)
        }
    }
    val buttonEnabled by remember(provider, passState) {
        derivedStateOf {
            if (provider == AuthProvider.GOOGLE) true else passState.text.isNotBlank()
        }
    }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        DeleteAccountStaticContent()
        DeleteAccountFields(
            provider = provider,
            passwordState = passState,
            passwordError = passwordError,
            onDelete = onDelete,
            focusManager = focusManager
        )
        AppButton(
            modifier = Modifier
                .fillMaxWidth(),
            enabled = buttonEnabled,
            isLoading = isLoading,
            onClick = onDelete
        ) { Text(stringResource(SharedRes.strings.delete_account)) }
    }
}

@Composable
private fun DeleteAccountScreenExpanded(
    modifier: Modifier = Modifier,
    provider: AuthProvider?,
    password: String,
    passwordError: String?,
    isLoading: Boolean,
    onAction: (DeleteAccountAction) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val passState = rememberSyncedTextFieldState(password)
    val latestAction = rememberUpdatedState(onAction)
    val onDelete = remember(focusManager) {
        {
            focusManager.clearFocus()
            latestAction.value(DeleteAccountAction.OnPasswordChange(passState.text.toString()))
            latestAction.value(DeleteAccountAction.OnDelete)
        }
    }
    val buttonEnabled by remember(provider, passState) {
        derivedStateOf {
            if (provider == AuthProvider.GOOGLE) true else passState.text.isNotBlank()
        }
    }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        DeleteAccountStaticContent(modifier = Modifier.weight(1f))
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(spacing.small)
        ) {
            DeleteAccountFields(
                provider = provider,
                passwordState = passState,
                passwordError = passwordError,
                onDelete = onDelete,
                focusManager = focusManager
            )
            AppButton(
                modifier = Modifier
                    .fillMaxWidth(),
                enabled = buttonEnabled,
                isLoading = isLoading,
                onClick = onDelete
            ) { Text(stringResource(SharedRes.strings.delete_account)) }
        }
    }
}

@Composable
private fun DeleteAccountStaticContent(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Icon(
            modifier = Modifier.size(64.dp),
            painter = painterResource(getImageByFileName("ic_bin").drawableResId),
            contentDescription = stringResource(SharedRes.strings.delete_account_button)
        )
        Spacer(modifier = Modifier.height(spacing.small))
        Text(
            text = stringResource(SharedRes.strings.deleting_your_account_is_irreversible_all_your_data_will_be_removed),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun DeleteAccountFields(
    provider: AuthProvider?,
    passwordState: TextFieldState,
    passwordError: String?,
    onDelete: () -> Unit,
    focusManager: FocusManager
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        val keyboardState = keyboardAsState()
        if (provider != AuthProvider.GOOGLE) {
            AppSecureTextField(
                textFieldState = passwordState,
                labelText = stringResource(SharedRes.strings.password),
                placeholderText = stringResource(SharedRes.strings.enter_your_password),
                onSubmit = onDelete,
                isError = passwordError != null,
                errorText = passwordError,
                modifier = Modifier.fillMaxWidth(),
                imeAction = if (passwordState.text.isNotBlank()) ImeAction.Send else ImeAction.Done,
                focusManager = focusManager,
                keyboardState = keyboardState
            )
        }
    }
}

@Composable
private fun rememberSyncedTextFieldState(
    value: String,
): TextFieldState {
    val state = rememberTextFieldState(value)
    LaunchedEffect(value) {
        if (state.text.toString() != value) {
            state.setTextAndPlaceCursorAtEnd(value)
        }
    }
    return state
}
