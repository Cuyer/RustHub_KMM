package pl.cuyer.rusthub.android.feature.item

import android.app.Activity
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.animateBounds
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import app.cash.paging.PagingData
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import org.koin.compose.koinInject
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.designsystem.RustSearchBarTopAppBar
import pl.cuyer.rusthub.android.navigation.ObserveAsEvents
import pl.cuyer.rusthub.android.theme.RustHubTheme
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.android.util.HandlePagingItems
import pl.cuyer.rusthub.android.util.composeUtil.stringResource
import pl.cuyer.rusthub.domain.model.ItemCategory
import pl.cuyer.rusthub.domain.model.RustItem
import pl.cuyer.rusthub.domain.model.displayName
import pl.cuyer.rusthub.util.StringProvider
import pl.cuyer.rusthub.presentation.features.item.ItemAction
import pl.cuyer.rusthub.presentation.features.item.ItemState
import pl.cuyer.rusthub.presentation.navigation.ItemList
import pl.cuyer.rusthub.presentation.navigation.UiEvent

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class,
    ExperimentalSharedTransitionApi::class
)
@Composable
fun ItemScreen(
    onNavigate: (NavKey) -> Unit,
    stateProvider: () -> State<ItemState>,
    onAction: (ItemAction) -> Unit,
    pagedList: LazyPagingItems<RustItem>,
    uiEvent: Flow<UiEvent>
) {
    val state = stateProvider()
    val searchBarState = rememberSearchBarState()
    val textFieldState = rememberTextFieldState()
    val scrollBehavior = SearchBarDefaults.enterAlwaysSearchBarScrollBehavior()
    val lazyListState = rememberLazyListState()

    ObserveAsEvents(uiEvent) { event ->
        if (event is UiEvent.Navigate) onNavigate(event.destination)
    }

    val context = LocalContext.current
    val windowSizeClass = calculateWindowSizeClass(context as Activity)
    val isTabletMode = windowSizeClass.widthSizeClass >= WindowWidthSizeClass.Medium

    Scaffold(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        topBar = {
            LookaheadScope {
                Column(
                    modifier = with(scrollBehavior) { Modifier.searchBarScrollBehavior() }
                        .animateBounds(this)
                ) {
                    RustSearchBarTopAppBar(
                        searchBarState = searchBarState,
                        textFieldState = textFieldState,
                        onSearchTriggered = {
                            onAction(ItemAction.OnSearch(textFieldState.text.toString()))
                        },
                        onOpenFilters = {},
                        searchQueryUi = emptyList(),
                        onDelete = {},
                        onClearSearchQuery = { onAction(ItemAction.OnClearSearchQuery) },
                        isLoadingSearchHistory = false,
                        showFiltersIcon = false,
                        placeholderRes = SharedRes.strings.search_items
                    )
                    ItemCategoryChips(
                        selected = state.value.selectedCategory,
                        onSelectedChange = {
                            onAction(ItemAction.OnCategoryChange(it))
                        },
                        modifier = Modifier
                            .navigationBarsPadding()
                            .padding(horizontal = spacing.xmedium)
                            .then(
                                if (isTabletMode) Modifier.displayCutoutPadding() else Modifier
                            )
                    )
                }
            }
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        HandlePagingItems(pagedList) {
            onRefresh { Box(Modifier.fillMaxSize()) }
            onError { }
            onEmpty {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(SharedRes.strings.no_items_available),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }
            onSuccess { items ->
                LazyColumn(state = lazyListState, modifier = Modifier.padding(innerPadding)) {
                    onPagingItems(key = { it.id ?: it.slug ?: it.hashCode() }) { item ->
                        Text(
                            text = item.name.orEmpty(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ItemCategoryChips(
    selected: ItemCategory?,
    onSelectedChange: (ItemCategory?) -> Unit,
    modifier: Modifier = Modifier
) {
    val sp = koinInject<StringProvider>()
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        item {
            FilterChip(
                selected = selected == null,
                onClick = { onSelectedChange(null) },
                label = { Text(stringResource(SharedRes.strings.all)) }
            )
        }
        items(ItemCategory.entries) { category ->
            val text by rememberUpdatedState(category.displayName(sp))
            FilterChip(
                selected = selected == category,
                onClick = { onSelectedChange(category) },
                label = { Text(text) }
            )
        }
    }
}

@Preview
@Composable
private fun ItemScreenPreview() {
    RustHubTheme {
        ItemScreen(
            stateProvider = { mutableStateOf(ItemState(isRefreshing = false)) },
            onAction = {},
            onNavigate = {},
            uiEvent = flowOf(UiEvent.Navigate(ItemList)),
            pagedList = flowOf(PagingData.from(emptyList<RustItem>())).collectAsLazyPagingItems()
        )
    }
}
