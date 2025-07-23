package pl.cuyer.rusthub.android.designsystem

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pl.cuyer.rusthub.android.theme.RustHubTheme
import pl.cuyer.rusthub.android.util.composeUtil.keyboardAsState

@Composable
fun AppTextField(
    modifier: Modifier = Modifier,
    textFieldState: TextFieldState,
    labelText: String,
    placeholderText: String,
    keyboardType: KeyboardType,
    trailingIcon: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    imeAction: ImeAction,
    requestFocus: Boolean = false,
    onSubmit: () -> Unit = { },
    isError: Boolean = false,
    errorText: String? = null,
    maxLength: Int? = null
) {

    val interactionSource = remember {
        MutableInteractionSource()
    }
    val isKeyboardOpen by keyboardAsState()
    val focusManager = LocalFocusManager.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val focusRequester = remember { FocusRequester() }
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

    val keyboardActionHandler =
        KeyboardActionHandler { performDefaultAction ->
            performDefaultAction()

            when (imeAction) {

                ImeAction.Next -> {
                    focusManager.moveFocus(FocusDirection.Down)
                }

                ImeAction.Done -> {
                    focusManager.clearFocus()
                }

                ImeAction.Send -> {
                    focusManager.clearFocus()
                    onSubmit()
                }

                else -> {
                    performDefaultAction()
                }
            }
        }

    OutlinedTextField(
        modifier = if (requestFocus) modifier.focusRequester(focusRequester) else modifier,
        state = textFieldState,
        lineLimits = TextFieldLineLimits.SingleLine,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.None,
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        onKeyboardAction = keyboardActionHandler,
        label = {
            Text(text = labelText)
        },
        placeholder = {
            Text(text = placeholderText)
        },
        interactionSource = interactionSource,
        inputTransformation = maxLength?.let { InputTransformation.maxLength(it) },
        suffix = suffix,
        isError = isError,
        supportingText = if (isError && errorText != null) {
            { Text(errorText) }
        } else null
    )
}

@Composable
@Preview
private fun AppTextFieldPreview() {
    RustHubTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            val state = rememberTextFieldState()
            AppTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                textFieldState = state,
                labelText = "E-mail",
                placeholderText = "Wpisz swój email",
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
                requestFocus = false
            )
        }
    }
}
