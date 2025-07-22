package pl.cuyer.rusthub.android.designsystem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.theme.RustHubTheme
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.android.util.composeUtil.stringResource
import pl.cuyer.rusthub.domain.model.CraftingIngredient

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CraftingListItem(
    modifier: Modifier = Modifier,
    ingredient: CraftingIngredient
) {
    ElevatedCard(
        shape = RectangleShape,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = spacing.xmedium, vertical = spacing.xxmedium),
            horizontalArrangement = Arrangement.spacedBy(spacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                modifier = Modifier.size(48.dp),
                model = ingredient.image,
                contentDescription = ingredient.name,
                contentScale = ContentScale.Crop
            )
            Column(verticalArrangement = Arrangement.spacedBy(spacing.xxsmall)) {
                Text(
                    text = ingredient.name.orEmpty(),
                    style = MaterialTheme.typography.titleLargeEmphasized
                )
                ingredient.amount?.let {
                    val label = stringResource(SharedRes.strings.amount)
                    Text(
                        text = "$label $it",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun CraftingListItemPreview() {
    RustHubTheme {
        CraftingListItem(
            ingredient = CraftingIngredient(
                name = "Stone",
                image = "",
                amount = 100
            )
        )
    }
}
