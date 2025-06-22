package pl.cuyer.rusthub.android.designsystem

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import pl.cuyer.rusthub.android.designsystem.AppButton
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pl.cuyer.rusthub.android.theme.RustHubTheme
import pl.cuyer.rusthub.common.getImageByFileName

@Composable
fun SignProviderButton(
    modifier: Modifier = Modifier,
    @DrawableRes image: Int,
    contentDescription: String,
    text: String,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    tint: Color? = null,
    onClick: () -> Unit
) {
    AppButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth(),
        colors = ButtonDefaults.buttonColors().copy(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
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

        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GoogleSignInButtonPreview() {
    RustHubTheme {
        SignProviderButton(
            onClick = {},
            image = getImageByFileName("ic_google").drawableResId,
            contentDescription = "Google logo",
            text = "Sign in with Google"
        )
    }
}
