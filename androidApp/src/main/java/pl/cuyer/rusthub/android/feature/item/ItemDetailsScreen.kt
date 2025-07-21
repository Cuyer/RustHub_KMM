package pl.cuyer.rusthub.android.feature.item

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.presentation.features.item.ItemState
import pl.cuyer.rusthub.presentation.navigation.UiEvent

@Composable
fun ItemDetailsScreen(
    onNavigate: (NavKey) -> Unit,
    stateProvider: () -> State<ItemState>,
    onAction: (Any) -> Unit,
    uiEvent: Flow<UiEvent>
) {
    Text("Item details")
}
