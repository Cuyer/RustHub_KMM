package pl.cuyer.rusthub.android.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import android.os.Build
import android.os.Build.VERSION_CODES
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.designsystem.SwitchWithText
import pl.cuyer.rusthub.android.designsystem.SwitchWithTextHorizontal
import pl.cuyer.rusthub.android.designsystem.bottomSheetNestedScroll
import pl.cuyer.rusthub.android.designsystem.settleCompat
import pl.cuyer.rusthub.android.theme.RustHubTheme
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.domain.model.Theme
import pl.cuyer.rusthub.util.StringProvider
import pl.cuyer.rusthub.android.util.composeUtil.stringResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ThemeBottomSheet(
    sheetState: SheetState,
    current: Theme,
    dynamicColors: Boolean,
    useSystemColors: Boolean,
    onThemeChange: (Theme) -> Unit,
    onDynamicColorsChange: (Boolean) -> Unit,
    onUseSystemColorsChange: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                modifier = Modifier
                    .padding(horizontal = spacing.medium),
                text = stringResource(SharedRes.strings.select_theme),
                style = MaterialTheme.typography.titleLargeEmphasized,
                fontWeight = FontWeight.SemiBold
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = spacing.medium))
            SwitchWithTextHorizontal(
                text = stringResource(SharedRes.strings.system_colors),
                isChecked = { useSystemColors },
                onCheckedChange = onUseSystemColorsChange
            )
            SwitchWithTextHorizontal(
                text = stringResource(SharedRes.strings.dark_mode),
                isChecked = { current == Theme.DARK },
                onCheckedChange = { onThemeChange(if (it) Theme.DARK else Theme.LIGHT) },
                enabled = { !useSystemColors }
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                SwitchWithTextHorizontal(
                    text = stringResource(SharedRes.strings.dynamic_colors),
                    isChecked = { dynamicColors },
                    onCheckedChange = onDynamicColorsChange
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun ThemeBottomSheetPreview() {
    val sheetState = androidx.compose.material3.rememberModalBottomSheetState()
    RustHubTheme {
        ThemeBottomSheet(
            sheetState = sheetState,
            current = Theme.LIGHT,
            dynamicColors = false,
            useSystemColors = false,
            onThemeChange = {},
            onDynamicColorsChange = {},
            onUseSystemColorsChange = {},
            onDismiss = {}
        )
    }
}
