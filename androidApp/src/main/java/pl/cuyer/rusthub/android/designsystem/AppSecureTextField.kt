package pl.cuyer.rusthub.android.designsystem

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import pl.cuyer.rusthub.android.util.composeUtil.keyboardAsState

@Composable
fun AppSecureTextField(
    modifier: Modifier = Modifier,
    textFieldState: () -> TextFieldState,
    labelText: String,
    placeholderText: String,
    onSubmit: () -> Unit,
    imeAction: ImeAction
) {
    val interactionSource = remember { MutableInteractionSource() }
    var passwordVisible by remember { mutableStateOf(false) }
    val isKeyboardOpen by keyboardAsState()
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(isKeyboardOpen) {
        if (!isKeyboardOpen) {
            focusManager.clearFocus()
        }
    }

    OutlinedTextField(
        modifier = modifier.focusRequester(focusRequester),
        value = textFieldState().text.toString(),
        onValueChange = { newValue ->
            textFieldState().edit { replace(0, length, newValue) }
        },
        readOnly = false,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.None,
            keyboardType = KeyboardType.Password,
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(
            onSend = {
                focusManager.clearFocus()
                onSubmit()
            },
            onDone = {
                focusManager.clearFocus()
            }
        ),
        trailingIcon = {
            Crossfade(targetState = textFieldState().text.isNotEmpty()) { hasText ->
                if (hasText) {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Crossfade(
                            targetState = passwordVisible,
                            label = "Show or hide password"
                        ) { isVisible ->
                            Icon(
                                imageVector = if (isVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = if (isVisible) "Hide password" else "Show password"
                            )
                        }
                    }
                }
            }
        },
        isError = false,
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
        colors = OutlinedTextFieldDefaults.colors()
    )
}