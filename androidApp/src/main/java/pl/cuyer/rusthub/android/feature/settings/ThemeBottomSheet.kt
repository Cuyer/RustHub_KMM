package pl.cuyer.rusthub.android.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.designsystem.SwitchWithText
import pl.cuyer.rusthub.android.designsystem.bottomSheetNestedScroll
import pl.cuyer.rusthub.android.designsystem.settleCompat
import pl.cuyer.rusthub.android.theme.RustHubTheme
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.domain.model.Theme
import pl.cuyer.rusthub.util.StringProvider
import pl.cuyer.rusthub.android.util.composeUtil.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeBottomSheet(
    sheetState: SheetState,
    current: Theme,
    dynamicColors: Boolean,
    onThemeChange: (Theme) -> Unit,
    onDynamicColorsChange: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val stringProvider = koinInject<StringProvider>()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = Modifier.bottomSheetNestedScroll(sheetState) { velocity ->
            coroutineScope.launch { sheetState.settleCompat(velocity) }
                .invokeOnCompletion { if (!sheetState.isVisible) onDismiss() }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.medium),
            verticalArrangement = Arrangement.spacedBy(spacing.medium)
        ) {
            Text(
                text = stringResource(SharedRes.strings.theme),
                style = MaterialTheme.typography.titleLarge
            )
            SwitchWithText(
                text = stringResource(SharedRes.strings.dark_mode),
                isChecked = current == Theme.DARK,
                onCheckedChange = { onThemeChange(if (it) Theme.DARK else Theme.LIGHT) }
            )
            SwitchWithText(
                text = stringResource(SharedRes.strings.dynamic_colors),
                isChecked = dynamicColors,
                onCheckedChange = onDynamicColorsChange
            )
        }
    }
}

@Preview
@Composable
private fun ThemeBottomSheetPreview() {
    val sheetState = androidx.compose.material3.rememberModalBottomSheetState()
    RustHubTheme {
        ThemeBottomSheet(
            sheetState = sheetState,
            current = Theme.LIGHT,
            dynamicColors = false,
            onThemeChange = {},
            onDynamicColorsChange = {},
            onDismiss = {}
        )
    }
}
