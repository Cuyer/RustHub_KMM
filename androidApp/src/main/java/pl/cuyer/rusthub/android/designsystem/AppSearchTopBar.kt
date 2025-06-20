package pl.cuyer.rusthub.android.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarScrollBehavior
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopSearchBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.presentation.model.SearchQueryUi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RustSearchBarTopAppBar(
    searchBarState: SearchBarState,
    textFieldState: TextFieldState,
    onSearchTriggered: () -> Unit,
    onOpenFilters: () -> Unit,
    searchQueryUi: List<SearchQueryUi>,
    onDelete: (String) -> Unit,
    scrollBehavior: SearchBarScrollBehavior
) {
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
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(spacing.xxmedium)
        ) {
            items(
                items = searchQueryUi,
                key = { it.query }
            ) { item ->
                val swipeState = rememberSwipeToDismissBoxState()
                SwipeToDismissBox(
                    state = swipeState,
                    backgroundContent = {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.error)
                                .padding(end = spacing.medium),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.onError
                            )
                        }
                    },
                    onDismiss = { value ->
                        if (value == SwipeToDismissBoxValue.EndToStart) {
                            onDelete(item.query)
                        }
                    },
                    enableDismissFromStartToEnd = false,
                    content = {
                        Text(
                            text = item.query,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    onClick = {
                                        textFieldState.setTextAndPlaceCursorAtEnd(item.query)
                                        coroutineScope.launch {
                                            searchBarState.animateToCollapsed()
                                            onSearchTriggered()
                                        }
                                    }
                                )
                                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                                .padding(spacing.medium)
                        )
                    }
                )
            }
        }
    }
}