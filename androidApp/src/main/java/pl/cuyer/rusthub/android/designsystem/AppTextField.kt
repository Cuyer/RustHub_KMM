package pl.cuyer.rusthub.android.designsystem

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import pl.cuyer.rusthub.android.theme.RustHubTheme
import pl.cuyer.rusthub.domain.model.Theme
import pl.cuyer.rusthub.android.util.composeUtil.keyboardAsState

@Composable
fun AppTextField(
    modifier: Modifier = Modifier,
    value: String,
    labelText: String,
    placeholderText: String,
    keyboardType: KeyboardType,
    trailingIcon: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    imeAction: ImeAction,
    requestFocus: Boolean = false,
    onSubmit: () -> Unit = { },
    onValueChange: (String) -> Unit = {},
    isError: Boolean = false,
    errorText: String? = null
) {

    val interactionSource = remember {
        MutableInteractionSource()
    }
    val isKeyboardOpen by keyboardAsState()
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(isKeyboardOpen) {
        if (!isKeyboardOpen) {
            focusManager.clearFocus()
        }
    }

    OutlinedTextField(
        modifier = if (requestFocus) modifier
            .focusRequester(focusRequester) else modifier,
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.None,
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(
            onNext = {
                if (imeAction == ImeAction.Next) {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            },
            onSend = {
                if (imeAction == ImeAction.Send) {
                    focusManager.clearFocus()
                    onSubmit()
                }
            }
        ),
        trailingIcon = trailingIcon,
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
        visualTransformation = VisualTransformation.None,
        colors = OutlinedTextFieldDefaults.colors(),
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
    RustHubTheme(theme = Theme.LIGHT) {
        Column(modifier = Modifier.fillMaxSize()) {
            AppTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                labelText = "E-mail",
                placeholderText = "Wpisz swój email",
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
                requestFocus = false,
                value = "",
                onValueChange = {}
            )
        }
    }
}