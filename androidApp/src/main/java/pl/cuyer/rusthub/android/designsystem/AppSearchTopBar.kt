package pl.cuyer.rusthub.android.designsystem


import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material3.AppBarWithSearch
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ExpandedDockedSearchBar
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.StringResource
import kotlinx.coroutines.launch
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.theme.RustHubTheme
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.android.util.composeUtil.stringResource
import pl.cuyer.rusthub.presentation.model.SearchQueryUi


@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class,
    ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun RustSearchBarTopAppBar(
    textFieldState: TextFieldState,
    onSearchTriggered: () -> Unit,
    onOpenFilters: () -> Unit,
    searchQueryUi: () -> List<SearchQueryUi>,
    onDelete: (String) -> Unit,
    onClearSearchQuery: () -> Unit,
    showFiltersIcon: Boolean = true,
    filtersCount: () -> Int = { 0 },
    placeholderRes: StringResource = SharedRes.strings.search_servers,
) {
    val coroutineScope = rememberCoroutineScope()
    val windowSizeClass = calculateWindowSizeClass(LocalActivity.current as Activity)
    val isTabletMode = remember(windowSizeClass.widthSizeClass) { windowSizeClass.widthSizeClass >= WindowWidthSizeClass.Medium }
    val searchBarState = rememberStableSearchBarState()

    LaunchedEffect(textFieldState.text) {
        if (textFieldState.text.isBlank()) onClearSearchQuery()
    }

    val inputField: @Composable () -> Unit = {
        SearchBarDefaults.InputField(
            modifier = if (!isTabletMode) Modifier.fillMaxWidth() else Modifier,
            searchBarState = searchBarState,
            textFieldState = textFieldState,
            onSearch = {
                onSearchTriggered()
                coroutineScope.launch { searchBarState.animateToCollapsed() }
            },
            placeholder = { Text(stringResource(placeholderRes)) },
            leadingIcon = {
                if (searchBarState.currentValue == SearchBarValue.Expanded) {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                searchBarState.animateToCollapsed()
                            }
                        },
                        modifier = Modifier.minimumInteractiveComponentSize()
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(SharedRes.strings.back)
                        )
                    }
                } else {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = stringResource(SharedRes.strings.search)
                    )
                }
            },
            trailingIcon = {
                Row {
                    if (textFieldState.text.isNotEmpty()) {
                        IconButton(
                            onClick = { textFieldState.setTextAndPlaceCursorAtEnd("") },
                            modifier = Modifier.minimumInteractiveComponentSize()
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = stringResource(SharedRes.strings.clear)
                            )
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
                        visible = showFiltersIcon &&
                                searchBarState.currentValue == SearchBarValue.Collapsed &&
                                textFieldState.text.isEmpty()
                    ) {
                        IconButton(
                            onClick = onOpenFilters,
                            modifier = Modifier.minimumInteractiveComponentSize()
                        ) {
                            BadgedBox(
                                badge = {
                                    Crossfade(filtersCount()) { count ->
                                        if (count > 0) {
                                            Badge(
                                                modifier = Modifier.offset(y = (-3).dp)
                                            ) {
                                                Text(
                                                    text = filtersCount().toString()
                                                )
                                            }
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    tint = contentColorFor(SearchBarDefaults.colors().containerColor),
                                    imageVector = Icons.Default.FilterList,
                                    contentDescription = stringResource(SharedRes.strings.open_filters)
                                )
                            }
                        }
                    }
                }
            }
        )
    }

    AppBarWithSearch(
        modifier = Modifier.fillMaxWidth(),
        state = searchBarState,
        inputField = inputField,
        scrollBehavior = null
    )

    if (!isTabletMode) {
        ExpandedFullScreenSearchBar(
            state = searchBarState,
            inputField = inputField
        ) {
            SearchHistorySuggestions(
                searchQueryUi = searchQueryUi,
                textFieldState = textFieldState,
                searchBarState = searchBarState,
                onDelete = onDelete,
                onSearchTriggered = onSearchTriggered
            )
        }
    } else {
        ExpandedDockedSearchBar(
            state = searchBarState,
            inputField = inputField
        ) {
            SearchHistorySuggestions(
                searchQueryUi = searchQueryUi,
                textFieldState = textFieldState,
                searchBarState = searchBarState,
                onDelete = onDelete,
                onSearchTriggered = onSearchTriggered
            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchHistorySuggestions(
    searchQueryUi: () -> List<SearchQueryUi>,
    textFieldState: TextFieldState,
    searchBarState: SearchBarState,
    onDelete: (String) -> Unit,
    onSearchTriggered: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(spacing.xxmedium)
    ) {
        item(key = "label", contentType = "label") {
            AnimatedVisibility(
                visible = searchQueryUi().isNotEmpty(),
                enter = fadeIn(
                    animationSpec = spring(
                        stiffness = Spring.StiffnessLow,
                        dampingRatio = Spring.DampingRatioLowBouncy,
                    )
                ),
                exit = fadeOut(
                    animationSpec = spring(
                        stiffness = Spring.StiffnessLow,
                        dampingRatio = Spring.DampingRatioLowBouncy,
                    )
                )
            ) {
                Row {
                    Text(
                        modifier = Modifier
                            .animateItem()
                            .padding(spacing.medium),
                        text = stringResource(SharedRes.strings.recent_searches)
                    )
                }
            }
        }
        items(
            searchQueryUi(),
            key = { it.query },
            contentType = { "history" }
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
                            Icons.Default.Delete,
                            contentDescription = stringResource(SharedRes.strings.delete),
                            tint = MaterialTheme.colorScheme.onError
                        )
                    }
                },
                onDismiss = { if (it == SwipeToDismissBoxValue.EndToStart) onDelete(item.query) },
                enableDismissFromStartToEnd = false
            ) {
                ListItem(
                    modifier = Modifier
                        .animateItem()
                        .fillMaxWidth()
                        .clickable {
                            textFieldState.setTextAndPlaceCursorAtEnd(item.query)
                            onSearchTriggered()
                            coroutineScope.launch { searchBarState.animateToCollapsed() }
                        },
                    colors = ListItemDefaults.colors().copy(
                        containerColor = SearchBarDefaults.colors().containerColor
                    ),
                    headlineContent = { Text(item.query) }
                )
            }
        }
        if (searchQueryUi().isNotEmpty()) {
            item(key = "clear", contentType = "clear") {
                Row(
                    modifier = Modifier
                        .animateItem()
                        .fillMaxWidth()
                        .padding(spacing.medium),
                    horizontalArrangement = Arrangement.Center
                ) {
                    AppButton(
                        onClick = {
                            onDelete("")
                            textFieldState.setTextAndPlaceCursorAtEnd("")
                            coroutineScope.launch { searchBarState.animateToCollapsed() }
                        },
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            contentColor = MaterialTheme.colorScheme.onBackground
                        ),
                    ) {
                        Text(stringResource(SharedRes.strings.clear_search_history))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(device = "spec:parent=pixel_5,orientation=portrait")
@Composable
private fun AppSearchTopBarPreview() {
    RustHubTheme {
        Scaffold(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground,
            topBar = {
                RustSearchBarTopAppBar(
                    textFieldState = TextFieldState(""),
                    onSearchTriggered = {},
                    onOpenFilters = {},
                    searchQueryUi = { emptyList() },
                    onDelete = {},
                    onClearSearchQuery = {},
                    placeholderRes = SharedRes.strings.search_servers
                )
            }
        ) {
            Box(
                Modifier
                    .padding(it)
                    .fillMaxSize()
            )
        }
    }
}
