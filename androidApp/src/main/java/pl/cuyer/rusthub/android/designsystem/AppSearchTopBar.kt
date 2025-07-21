package pl.cuyer.rusthub.android.designsystem


import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExpandedDockedSearchBar
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopSearchBar
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
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
    searchBarState: SearchBarState,
    textFieldState: TextFieldState,
    onSearchTriggered: () -> Unit,
    onOpenFilters: () -> Unit,
    searchQueryUi: List<SearchQueryUi>,
    onDelete: (String) -> Unit,
    onClearSearchQuery: () -> Unit,
    isLoadingSearchHistory: Boolean,
    showFiltersIcon: Boolean = true,
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val windowSizeClass = calculateWindowSizeClass(context as Activity)
    val isTabletMode = windowSizeClass.widthSizeClass >= WindowWidthSizeClass.Medium

    LaunchedEffect(textFieldState.text) {
        if (textFieldState.text.isBlank()) onClearSearchQuery()
    }

    val inputField: @Composable () -> Unit = {
        SearchBarDefaults.InputField(
            modifier = if (!isTabletMode) Modifier.fillMaxWidth() else Modifier,
            searchBarState = searchBarState,
            textFieldState = textFieldState,
            onSearch = {
                coroutineScope.launch {
                    searchBarState.animateToCollapsed()
                    onSearchTriggered()
                }
            },
            placeholder = { Text(stringResource(SharedRes.strings.search_servers)) },
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
                            Icon(
                                tint = contentColorFor(SearchBarDefaults.colors().containerColor),
                                imageVector = Icons.Default.FilterList,
                                contentDescription = stringResource(SharedRes.strings.open_filters)
                            )
                        }
                    }
                }
            }
        )
    }

    TopSearchBar(
        modifier = Modifier.fillMaxWidth(),
        state = searchBarState,
        scrollBehavior = null,
        inputField = inputField
    )

    val suggestions: @Composable () -> Unit = {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(spacing.xxmedium)
        ) {
            item {
                AnimatedVisibility(visible = searchQueryUi.isNotEmpty()) {
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
            items(searchQueryUi, key = { it.query }) { item ->
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
                    ElevatedCard(
                        onClick = {
                            textFieldState.setTextAndPlaceCursorAtEnd(item.query)
                            coroutineScope.launch {
                                searchBarState.animateToCollapsed()
                                onSearchTriggered()
                            }
                        },
                        modifier = Modifier
                            .animateItem()
                            .fillMaxWidth(),
                        shape = RectangleShape,
                        colors = CardDefaults.elevatedCardColors()
                    ) {
                        Text(
                            text = item.query,
                            modifier = Modifier.padding(spacing.medium)
                        )
                    }
                }
            }
            if (searchQueryUi.isNotEmpty()) {
                item {
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

    if (!isTabletMode) {
        ExpandedFullScreenSearchBar(
            state = searchBarState,
            inputField = inputField
        ) {
            if (isLoadingSearchHistory) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    LoadingIndicator()
                }
            } else {
                suggestions()
            }
        }
    } else {
        ExpandedDockedSearchBar(
            state = searchBarState,
            inputField = inputField
        ) {
            if (isLoadingSearchHistory) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    LoadingIndicator()
                }
            } else {
                suggestions()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(device = "spec:parent=pixel_5,orientation=portrait")
@Composable
private fun AppSearchTopBarPreview() {
    RustHubTheme() {
        Scaffold(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground,
            topBar = {
                RustSearchBarTopAppBar(
                    searchBarState = rememberSearchBarState(),
                    textFieldState = TextFieldState(""),
                    onSearchTriggered = {},
                    onOpenFilters = {},
                    searchQueryUi = emptyList(),
                    onDelete = {},
                    onClearSearchQuery = {},
                    isLoadingSearchHistory = false
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