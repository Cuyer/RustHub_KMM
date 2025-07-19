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
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.util.composeUtil.stringResource
import pl.cuyer.rusthub.android.designsystem.AppButton
import pl.cuyer.rusthub.android.designsystem.AppSecureTextField
import pl.cuyer.rusthub.android.navigation.ObserveAsEvents
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.common.getImageByFileName
import pl.cuyer.rusthub.domain.model.AuthProvider
import pl.cuyer.rusthub.presentation.features.auth.delete.DeleteAccountAction
import pl.cuyer.rusthub.presentation.features.auth.delete.DeleteAccountState
import pl.cuyer.rusthub.presentation.navigation.UiEvent

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class,
    ExperimentalSharedTransitionApi::class
)
@Composable
fun DeleteAccountScreen(
    onNavigateUp: () -> Unit,
    onNavigate: (NavKey) -> Unit,
    uiEvent: Flow<UiEvent>,
    stateProvider: () -> State<DeleteAccountState>,
    onAction: (DeleteAccountAction) -> Unit
) {
    val state = stateProvider()
    ObserveAsEvents(uiEvent) { event ->
        if (event is UiEvent.Navigate) onNavigate(event.destination)
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
                    text = stringResource(SharedRes.strings.delete_account),
                    fontWeight = FontWeight.SemiBold
                ) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            tint = contentColorFor(TopAppBarDefaults.topAppBarColors().containerColor),
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        LookaheadScope {
            if (isTabletMode) {
                DeleteAccountScreenExpanded(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(spacing.medium)
                        .animateBounds(this)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) { focusManager.clearFocus() },
                    state = state.value,
                    onAction = onAction
                )
            } else {
                DeleteAccountScreenCompact(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(innerPadding)
                        .padding(spacing.medium)
                        .animateBounds(this)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) { focusManager.clearFocus() },
                    state = state.value,
                    onAction = onAction
                )
            }
        }
    }
}

@Composable
private fun DeleteAccountScreenCompact(
    modifier: Modifier = Modifier,
    state: DeleteAccountState,
    onAction: (DeleteAccountAction) -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        val focusManager = LocalFocusManager.current
        DeleteAccountStaticContent()
        DeleteAccountFields(
            provider = state.provider,
            password = state.password,
            passwordError = state.passwordError,
            onAction = onAction
        )
        AppButton(
            modifier = Modifier
                .imePadding()
                .fillMaxWidth(),
            enabled = if (state.provider == AuthProvider.GOOGLE) true else state.password.isNotBlank(),
            isLoading = state.isLoading,
            onClick = {
                focusManager.clearFocus()
                onAction(DeleteAccountAction.OnDelete)
            }
        ) { Text(stringResource(SharedRes.strings.delete_account)) }
    }
}

@Composable
private fun DeleteAccountScreenExpanded(
    modifier: Modifier = Modifier,
    state: DeleteAccountState,
    onAction: (DeleteAccountAction) -> Unit
) {
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
            val focusManager = LocalFocusManager.current
            DeleteAccountFields(
                provider = state.provider,
                password = state.password,
                passwordError = state.passwordError,
                onAction = onAction
            )
            AppButton(
                modifier = Modifier
                    .imePadding()
                    .fillMaxWidth(),
                enabled = if (state.provider == AuthProvider.GOOGLE) true else state.password.isNotBlank(),
                isLoading = state.isLoading,
                onClick = {
                    focusManager.clearFocus()
                    onAction(DeleteAccountAction.OnDelete)
                }
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
    password: String,
    passwordError: String?,
    onAction: (DeleteAccountAction) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        val focusManager = LocalFocusManager.current
        if (provider != AuthProvider.GOOGLE) {
            AppSecureTextField(
                value = password,
                onValueChange = { onAction(DeleteAccountAction.OnPasswordChange(it)) },
                labelText = stringResource(SharedRes.strings.password),
                placeholderText = stringResource(SharedRes.strings.enter_your_password),
                onSubmit = {
                    focusManager.clearFocus()
                    onAction(DeleteAccountAction.OnDelete)
                },
                isError = passwordError != null,
                errorText = passwordError,
                modifier = Modifier.fillMaxWidth(),
                imeAction = if (password.isNotBlank()) ImeAction.Send else ImeAction.Done
            )
        }
    }
}
