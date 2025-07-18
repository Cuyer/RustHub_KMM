package pl.cuyer.rusthub.android.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.Text
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.designsystem.bottomSheetNestedScroll
import pl.cuyer.rusthub.android.designsystem.settleCompat
import pl.cuyer.rusthub.android.theme.RustHubTheme
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.domain.model.Language
import pl.cuyer.rusthub.util.StringProvider
import pl.cuyer.rusthub.android.util.composeUtil.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageBottomSheet(
    sheetState: SheetState,
    current: Language,
    onSelect: (Language) -> Unit,
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
                text = stringResource(SharedRes.strings.language),
                style = MaterialTheme.typography.titleLarge
            )
            Language.entries.forEach { language ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = spacing.small)
                        .clickable {
                            onSelect(language)
                            onDismiss()
                        },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(language.displayName(stringProvider))
                    if (language == current) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun LanguageBottomSheetPreview() {
    val sheetState = androidx.compose.material3.rememberModalBottomSheetState()
    RustHubTheme {
        LanguageBottomSheet(
            sheetState = sheetState,
            current = Language.ENGLISH,
            onSelect = {},
            onDismiss = {}
        )
    }
}
