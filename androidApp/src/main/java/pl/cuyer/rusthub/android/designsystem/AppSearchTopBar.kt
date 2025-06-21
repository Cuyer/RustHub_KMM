package pl.cuyer.rusthub.android.designsystem

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.material3.Button
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarScrollBehavior
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopSearchBar
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch
import pl.cuyer.rusthub.android.theme.RustHubTheme
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.presentation.model.SearchQueryUi

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun RustSearchBarTopAppBar(
    searchBarState: SearchBarState,
    textFieldState: TextFieldState,
    onSearchTriggered: () -> Unit,
    onOpenFilters: () -> Unit,
    searchQueryUi: List<SearchQueryUi>,
    onDelete: (String) -> Unit,
    onClearSearchQuery: () -> Unit,
    scrollBehavior: SearchBarScrollBehavior
) {
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(textFieldState.text) {
        if (textFieldState.text.isBlank()) onClearSearchQuery()
    }

    val inputField = @Composable {
        SearchBarDefaults.InputField(
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
                        }) {
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
                    AnimatedVisibility(
                        enter = slideInHorizontally(
                            initialOffsetX = { it },
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessVeryLow
                            )
                        ) + scaleIn(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        ),
                        exit = slideOutHorizontally(
                            targetOffsetX = { it },
                            animationSpec = tween(durationMillis = 150)
                        ),
                        visible = searchBarState.currentValue == SearchBarValue.Collapsed && textFieldState.text.isEmpty()
                    ) {
                        IconButton(onClick = onOpenFilters) {
                            Icon(Icons.Default.FilterList, contentDescription = "Open Filters")
                        }
                    }
                }
            })
    }

    TopSearchBar(
        state = searchBarState, scrollBehavior = scrollBehavior, inputField = inputField
    )

    ExpandedFullScreenSearchBar(
        state = searchBarState,
        inputField = inputField,
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(spacing.xxmedium)
        ) {
            item {
                AnimatedVisibility(
                    visible = searchQueryUi.isNotEmpty()
                ) {
                    Row {
                        Text(
                            modifier = Modifier
                                .animateItem()
                                .padding(spacing.medium),
                            text = "Recent searches"
                        )
                    }
                }
            }

            items(
                items = searchQueryUi, key = { it.query }) { item ->
                val swipeState = rememberSwipeToDismissBoxState()
                SwipeToDismissBox(state = swipeState, backgroundContent = {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.error)
                            .padding(end = spacing.medium), contentAlignment = Alignment.CenterEnd
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.onError
                        )
                    }
                }, onDismiss = { value ->
                    if (value == SwipeToDismissBoxValue.EndToStart) {
                        onDelete(item.query)
                    }
                }, enableDismissFromStartToEnd = false, content = {
                    Text(
                        text = item.query,
                        modifier = Modifier
                            .animateItem()
                            .fillMaxWidth()
                            .clickable(
                                onClick = {
                                    textFieldState.setTextAndPlaceCursorAtEnd(item.query)
                                    coroutineScope.launch {
                                        searchBarState.animateToCollapsed()
                                        onSearchTriggered()
                                    }
                                })
                            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                            .padding(spacing.medium)
                    )
                })
            }
            item {
                AnimatedVisibility(
                    visible = searchQueryUi.isNotEmpty()
                ) {
                    Row(
                        modifier = Modifier
                            .animateItem()
                            .fillMaxWidth()
                            .padding(spacing.medium),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = {
                                onDelete("")
                                coroutineScope.launch {
                                    searchBarState.animateToCollapsed()
                                }
                            }) {
                            Text(
                                text = "Clear search history"
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(device = "spec:parent=pixel_5,orientation=landscape")
@Composable
private fun AppSearchTopBarPreview() {
    RustHubTheme {
        Scaffold(
            topBar = {
                RustSearchBarTopAppBar(
                    searchBarState = rememberSearchBarState(),
                    textFieldState = TextFieldState(""),
                    onSearchTriggered = {},
                    onOpenFilters = {},
                    searchQueryUi = emptyList(),
                    onDelete = {},
                    onClearSearchQuery = {},
                    scrollBehavior = SearchBarDefaults.enterAlwaysSearchBarScrollBehavior()
                )
            }
        ) {
            Box(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
            )
        }
    }
}