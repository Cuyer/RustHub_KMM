package pl.cuyer.rusthub.android.designsystem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import pl.cuyer.rusthub.android.theme.spacing

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SearchHistoryShimmer(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.xxmedium)
    ) {
        items(5) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RectangleShape,
                colors = CardDefaults.elevatedCardColors()
            ) {
                Box(
                    modifier = Modifier
                        .padding(spacing.medium)
                        .fillMaxWidth(0.9f)
                        .height(20.dp)
                        .clip(RectangleShape)
                        .shimmer()
                        .clearAndSetSemantics {}
                )
            }
        }
    }
}
