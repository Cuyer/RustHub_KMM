package pl.cuyer.rusthub.android.designsystem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import pl.cuyer.rusthub.android.theme.spacing

@Composable
fun ItemDetailsShimmer(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = spacing.xmedium, vertical = spacing.medium),
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        ItemListItemShimmer(modifier = Modifier.fillMaxWidth())
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing.medium),
            modifier = Modifier.fillMaxWidth()
        ) {
            repeat(3) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(32.dp)
                        .clip(MaterialTheme.shapes.extraSmall)
                        .shimmer()
                )
            }
        }
        repeat(5) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .shimmer()
            )
        }
    }
}
