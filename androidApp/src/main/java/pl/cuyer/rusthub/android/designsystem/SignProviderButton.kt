package pl.cuyer.rusthub.android.designsystem

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pl.cuyer.rusthub.android.theme.RustHubTheme
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.common.getImageByFileName
import pl.cuyer.rusthub.domain.model.Theme

@Composable
fun SignProviderButton(
    modifier: Modifier = Modifier,
    @DrawableRes image: Int,
    contentDescription: String,
    text: String,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    tint: Color? = null,
    isLoading: () -> Boolean = { false },
    onClick: () -> Unit
) {
    AppButton(
        isLoading = isLoading,
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth(),
        colors = ButtonDefaults.elevatedButtonColors().copy(
            containerColor = backgroundColor,
            contentColor = contentColor,
            disabledContainerColor = backgroundColor,
            disabledContentColor = contentColor
        ),
        loadingIndicator = {
            CircularProgressIndicator(
                color = contentColor,
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp
            )
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(
                spacing.xxmedium,
                Alignment.CenterHorizontally
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            tint?.let {
                Icon(
                    painter = painterResource(id = image),
                    contentDescription = contentDescription,
                    modifier = Modifier.size(24.dp),
                    tint = it
                )
            } ?: run {
                Image(
                    painter = painterResource(id = image),
                    contentDescription = contentDescription,
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                text = text
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GoogleSignInButtonPreview() {
    RustHubTheme {
        SignProviderButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {},
            isLoading = { true },
            image = getImageByFileName("ic_google").drawableResId,
            backgroundColor = if (isSystemInDarkTheme()) Color.White else Color.Black,
            contentColor = if (isSystemInDarkTheme()) Color.Black else Color.White,
            contentDescription = "Google logo",
            text = "Continue with Google"
        )
    }
}
