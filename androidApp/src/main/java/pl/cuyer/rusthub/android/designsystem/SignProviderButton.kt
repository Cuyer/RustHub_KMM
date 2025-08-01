package pl.cuyer.rusthub.android.designsystem

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.theme.RustHubTheme
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.android.util.composeUtil.stringResource
import pl.cuyer.rusthub.common.getImageByFileName
import pl.cuyer.rusthub.domain.model.Theme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SignProviderButton(
    modifier: Modifier = Modifier,
    @DrawableRes image: Int,
    contentDescription: String,
    text: String,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    tint: Color? = null,
    isLoading: Boolean = false,
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
        Spacer(Modifier.width(spacing.xxmedium))
        Text(
            text = text
        )
    }
}

@Preview
@Composable
private fun SignProviderButtonPreview() {
    var loading by remember { mutableStateOf(false) }
    LaunchedEffect(loading) {
        delay(1000)
        loading = !loading
    }
    RustHubTheme {
        SignProviderButton(
            image = getImageByFileName("ic_google").drawableResId,
            text = "Continue with Google",
            contentDescription = "",
            modifier = Modifier.fillMaxWidth(),
            isLoading = loading,
            backgroundColor = if (isSystemInDarkTheme()) Color.White else Color.Black,
            contentColor = if (isSystemInDarkTheme()) Color.Black else Color.White,
            onClick = { }
        )
    }
}

@Preview
@Composable
private fun SignProviderButtonLoadingPreview() {
    RustHubTheme {
        SignProviderButton(
            isLoading = true,
            image = getImageByFileName(
                name = if (isSystemInDarkTheme()) {
                    "ic_google"
                } else {
                    "ic_google"
                }
            ).drawableResId,
            contentDescription = "Google",
            text = "Sign with Google",
            onClick = {}
        )
    }
}

@Preview
@Composable
private fun SignProviderButtonDarkPreview() {
    RustHubTheme(darkTheme = true) {
        SignProviderButtonPreview()
    }
}
