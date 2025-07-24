package pl.cuyer.rusthub.android.designsystem

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedSecureTextField
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SecureTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import pl.cuyer.rusthub.android.util.composeUtil.keyboardAsState
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.util.composeUtil.stringResource

@Composable
fun AppSecureTextField(
    modifier: Modifier = Modifier,
    textFieldState: TextFieldState,
    labelText: String,
    placeholderText: String,
    onSubmit: () -> Unit,
    imeAction: ImeAction,
    isError: Boolean = false,
    errorText: String? = null,
    requestFocus: Boolean = false,
    keyboardState: State<Boolean>? = null,
    focusManager: FocusManager? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    var passwordVisible by remember { mutableStateOf(false) }
    val resolvedKeyboardState = keyboardState ?: keyboardAsState()
    val isKeyboardOpen by resolvedKeyboardState
    val resolvedFocusManager = focusManager ?: LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(isKeyboardOpen) {
        if (!isKeyboardOpen) {
            resolvedFocusManager.clearFocus()
        }
    }

    LaunchedEffect(lifecycleOwner.lifecycle) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            focusRequester.requestFocus()
        }
    }

    val keyboardActionHandler =
        KeyboardActionHandler { performDefaultAction ->
            // Define the default behavior
            performDefaultAction()

            // Additional custom behaviors
            when (imeAction) {

                ImeAction.Next -> {
                    resolvedFocusManager.moveFocus(FocusDirection.Down)
                }

                ImeAction.Done -> {
                    resolvedFocusManager.clearFocus()
                }

                ImeAction.Send -> {
                    resolvedFocusManager.clearFocus()
                    onSubmit()
                }

                else -> {
                    performDefaultAction()
                }
            }
        }

    OutlinedSecureTextField(
        textObfuscationMode = if (passwordVisible) TextObfuscationMode.Visible else TextObfuscationMode.RevealLastTyped,
        modifier = if (requestFocus) modifier.focusRequester(focusRequester) else modifier,
        state = textFieldState,
        label = {
            Text(
                text = labelText
            )
        },
        placeholder = {
            Text(
                text = placeholderText
            )
        },
        trailingIcon = {
            Crossfade(targetState = textFieldState.text.isNotEmpty()) { hasText ->
                if (hasText) {
                    IconButton(
                        onClick = { passwordVisible = !passwordVisible },
                        modifier = Modifier.minimumInteractiveComponentSize()
                    ) {
                        Crossfade(
                            targetState = passwordVisible,
                            label = stringResource(SharedRes.strings.show_or_hide_password)
                        ) { isVisible ->
                            Icon(
                                imageVector = if (isVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = if (isVisible) {
                                    stringResource(SharedRes.strings.hide_password)
                                } else {
                                    stringResource(SharedRes.strings.show_password)
                                }
                            )
                        }
                    }
                }
            }
        },
        isError = isError,
        interactionSource = interactionSource,
        supportingText = if (isError && errorText != null) {
            { Text(errorText) }
        } else null,
        onKeyboardAction = keyboardActionHandler
    )
}
