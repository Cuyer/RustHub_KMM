package pl.cuyer.rusthub.android.designsystem

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopSearchBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RustSearchBarTopAppBar(
    searchBarState: SearchBarState,
    textFieldState: TextFieldState,
    onSearchTriggered: () -> Unit,
    onOpenFilters: () -> Unit,
) {
    val scrollBehavior = SearchBarDefaults.enterAlwaysSearchBarScrollBehavior()
    val coroutineScope = rememberCoroutineScope()

    val inputField = @Composable {
        SearchBarDefaults.InputField(
            modifier = Modifier.fillMaxWidth(),
            searchBarState = searchBarState,
            textFieldState = textFieldState,
            onSearch = {
                coroutineScope.launch {
                    searchBarState.animateToCollapsed()
                    onSearchTriggered()
                }
            },
            placeholder = { Text("Search servers...") },
            leadingIcon = {
                if (searchBarState.currentValue == SearchBarValue.Expanded) {
                    IconButton(
                        onClick = {
                            coroutineScope.launch { searchBarState.animateToCollapsed() }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                } else {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
            },
            trailingIcon = {
                Row {
                    if (textFieldState.text.isNotEmpty()) {
                        IconButton(onClick = {
                            textFieldState.setTextAndPlaceCursorAtEnd("")
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                    if (searchBarState.currentValue == SearchBarValue.Collapsed) {
                        IconButton(onClick = onOpenFilters) {
                            Icon(Icons.Default.FilterList, contentDescription = "Open Filters")
                        }
                    }
                }
            }
        )
    }

    TopSearchBar(
        state = searchBarState,
        scrollBehavior = scrollBehavior,
        inputField = inputField
    )

    ExpandedFullScreenSearchBar(
        state = searchBarState,
        inputField = inputField,
    ) {
        SearchHistory(text = searchBarState.currentValue.toString())
    }
}