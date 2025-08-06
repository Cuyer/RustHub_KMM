package pl.cuyer.rusthub.android.designsystem

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import pl.cuyer.rusthub.android.theme.RustHubTheme
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.domain.model.Monument

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MonumentListItem(
    modifier: Modifier = Modifier,
    monument: Monument,
    onClick: (String) -> Unit,
) {
    ElevatedCard(
        onClick = { monument.slug?.let(onClick) },
        shape = MaterialTheme.shapes.extraSmall,
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(spacing.xmedium)) {
            Text(
                text = monument.name.orEmpty(),
                style = MaterialTheme.typography.titleLargeEmphasized,
            )
        }
    }
}

@Preview
@Composable
private fun MonumentListItemPreview() {
    RustHubTheme {
        MonumentListItem(
            monument = Monument(name = "Airfield", slug = "airfield"),
            onClick = {},
        )
    }
}
