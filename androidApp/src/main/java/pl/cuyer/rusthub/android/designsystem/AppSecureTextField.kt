package pl.cuyer.rusthub.android.designsystem

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.KeyboardAction
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
    requestFocus: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    var passwordVisible by remember { mutableStateOf(false) }
    val isKeyboardOpen by keyboardAsState()
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(isKeyboardOpen) {
        if (!isKeyboardOpen) {
            focusManager.clearFocus()
        }
    }

    LaunchedEffect(lifecycleOwner.lifecycle) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            focusRequester.requestFocus()
        }
    }

    OutlinedTextField(
        modifier = if (requestFocus) modifier
            .focusRequester(focusRequester) else modifier,
        state = textFieldState,
        readOnly = false,
        lineLimits = TextFieldLineLimits.SingleLine,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.None,
            keyboardType = KeyboardType.Password,
            imeAction = imeAction
        ),
        onKeyboardAction = KeyboardActionHandler { action ->
            when (action) {
                KeyboardAction.Send -> {
                    focusManager.clearFocus()
                    onSubmit()
                    true
                }
                KeyboardAction.Done -> {
                    focusManager.clearFocus()
                    true
                }
                else -> false
            }
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
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
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
        interactionSource = interactionSource,
        colors = OutlinedTextFieldDefaults.colors(),
        supportingText = if (isError && errorText != null) {
            { Text(errorText) }
        } else null
    )
}
