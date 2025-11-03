package pl.cuyer.rusthub.android.feature.item

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.animateBounds
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavKey
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.BuildConfig
import pl.cuyer.rusthub.android.ads.NativeAdCard
import pl.cuyer.rusthub.android.designsystem.ItemListItem
import pl.cuyer.rusthub.android.designsystem.ItemListItemShimmer
import pl.cuyer.rusthub.android.designsystem.RustSearchBarTopAppBar
import pl.cuyer.rusthub.android.navigation.ObserveAsEvents
import pl.cuyer.rusthub.android.theme.RustHubTheme
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.android.util.HandlePagingItems
import pl.cuyer.rusthub.android.util.composeUtil.stringResource
import pl.cuyer.rusthub.domain.model.ItemCategory
import pl.cuyer.rusthub.domain.model.ItemSummary
import pl.cuyer.rusthub.domain.model.displayName
import pl.cuyer.rusthub.presentation.features.ads.AdAction
import pl.cuyer.rusthub.presentation.features.ads.NativeAdState
import pl.cuyer.rusthub.presentation.features.item.ItemAction
import pl.cuyer.rusthub.presentation.features.item.ItemState
import pl.cuyer.rusthub.presentation.navigation.ItemList
import pl.cuyer.rusthub.presentation.navigation.UiEvent
import pl.cuyer.rusthub.util.StringProvider

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class,
    ExperimentalSharedTransitionApi::class, ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun ItemScreen(
    onNavigate: (NavKey) -> Unit,
    state: State<ItemState>,
    onAction: (ItemAction) -> Unit,
    pagedList: LazyPagingItems<ItemSummary>,
    uiEvent: Flow<UiEvent>,
    showAds: Boolean,
    adState: State<NativeAdState>,
    onAdAction: (AdAction) -> Unit
) {
    val textFieldState = rememberTextFieldState()
    val scrollBehavior = SearchBarDefaults.enterAlwaysSearchBarScrollBehavior()
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val pullToRefreshState = rememberPullToRefreshState()
    val isAtTop by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex == 0 &&
                    lazyListState.firstVisibleItemScrollOffset == 0
        }
    }

    ObserveAsEvents(uiEvent) { event ->
        when (event) {
            is UiEvent.Navigate -> onNavigate(event.destination)
            is UiEvent.OnScrollToIndex -> {
                coroutineScope.launch {
                    val targetIndex = event.index.coerceAtLeast(0)
                    lazyListState.animateScrollToItem(targetIndex)
                    scrollBehavior.scrollOffset = 1f
                }
            }
            UiEvent.NavigateUp -> Unit
        }
    }

    LaunchedEffect(showAds) {
        if (showAds) {
            onAdAction(AdAction.LoadAd(BuildConfig.ITEMS_ADMOB_NATIVE_AD_ID))
        }
    }
    Scaffold(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        floatingActionButton = {
            AnimatedVisibility(
                visible = !isAtTop,
                enter = slideInVertically(
                    initialOffsetY = { it },
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
                exit = slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(durationMillis = 150)
                ),
            ) {
                FloatingActionButton(
                    onClick = {
                        coroutineScope.launch {
                            lazyListState.animateScrollToItem(0)
                            scrollBehavior.scrollOffset = 1f
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowUpward,
                        contentDescription = stringResource(SharedRes.strings.scroll_to_top),
                        tint = contentColorFor(FloatingActionButtonDefaults.containerColor)
                    )
                }
            }
        },
        topBar = {
            Column(
                modifier = with(scrollBehavior) { Modifier.searchBarScrollBehavior() }
            ) {
                RustSearchBarTopAppBar(
                    textFieldState = textFieldState,
                    onSearchTriggered = {
                        onAction(ItemAction.OnSearch(textFieldState.text.toString()))
                    },
                    onOpenFilters = {},
                    searchQueryUi = { state.value.searchQueries },
                    onDelete = {
                        if (it.isBlank()) onAction(ItemAction.DeleteSearchQueries)
                        else onAction(ItemAction.DeleteSearchQueryByQuery(it))
                    },
                    onClearSearchQuery = { onAction(ItemAction.OnClearSearchQuery) },
                    showFiltersIcon = false,
                    placeholderRes = SharedRes.strings.search_items
                )
                ItemCategoryChips(
                    selected = state.value.selectedCategory,
                    onSelectedChange = {
                        onAction(ItemAction.OnCategoryChange(it))
                    },
                    modifier = Modifier
                        .padding(horizontal = spacing.xmedium)
                )
                AnimatedVisibility(
                    visible = !state.value.isConnected,
                    enter = slideInVertically(
                        animationSpec = spring(
                            stiffness = Spring.StiffnessLow,
                            dampingRatio = Spring.DampingRatioLowBouncy
                        )
                    ),
                    exit = slideOutVertically(
                        animationSpec = spring(
                            stiffness = Spring.StiffnessLow,
                            dampingRatio = Spring.DampingRatioLowBouncy
                        )
                    )
                ) {
                    Text(
                        textAlign = TextAlign.Center,
                        text = stringResource(SharedRes.strings.offline_cached_items_info),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = spacing.xsmall)
                            .background(MaterialTheme.colorScheme.secondary)
                    )
                }
            }
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = false,
            onRefresh = { pagedList.refresh() },
            state = pullToRefreshState,
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .fillMaxSize(),
        ) {
            HandlePagingItems(
                items = { pagedList },
                onRefresh = {
                    LazyColumn(
                        contentPadding = PaddingValues(
                            top = 0.dp,
                            bottom = WindowInsets.safeDrawing.asPaddingValues()
                                .calculateBottomPadding(),
                            start = 0.dp,
                            end = 0.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(spacing.medium),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(
                            count = 8,
                            key = { it },
                            contentType = { "shimmer" }
                        ) {
                            ItemListItemShimmer(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .animateItem()
                                    .padding(horizontal = spacing.xmedium)
                            )
                        }
                    }
                },
                onError = { error ->
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        item(key = "error", contentType = "error") {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "(×_×)",
                                        style = MaterialTheme.typography.headlineLarge,
                                        textAlign = TextAlign.Center,
                                        fontSize = 96.sp
                                    )
                                    Text(
                                        text = stringResource(SharedRes.strings.error_oops),
                                        style = MaterialTheme.typography.bodyLarge,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                    onAction(ItemAction.OnError(error))
                },
                onEmpty = {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        item(key = "empty", contentType = "empty") {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "( •_•)?",
                                        style = MaterialTheme.typography.headlineLarge,
                                        textAlign = TextAlign.Center,
                                        fontSize = 96.sp
                                    )
                                    val message = if (!state.value.isConnected) {
                                        stringResource(SharedRes.strings.no_items_available_offline)
                                    } else {
                                        stringResource(SharedRes.strings.no_items_available)
                                    }
                                    Text(
                                        text = message,
                                        style = MaterialTheme.typography.bodyLarge,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            ) {
                LazyColumn(
                    contentPadding = PaddingValues(
                        top = 0.dp,
                        bottom = WindowInsets.safeDrawing.asPaddingValues()
                            .calculateBottomPadding(),
                        start = 0.dp,
                        end = 0.dp
                    ),
                    state = lazyListState,
                    verticalArrangement = Arrangement.spacedBy(spacing.medium),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (showAds && pagedList.itemCount > 0 && pagedList.loadState.refresh is LoadState.NotLoading) {
                        item(
                            key = "ad",
                            contentType = "ad"
                        ) {
                            NativeAdCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .animateItem()
                                    .padding(horizontal = spacing.xmedium),
                                ad = { adState.value.ads[BuildConfig.ITEMS_ADMOB_NATIVE_AD_ID] }
                            )
                        }
                    }
                    onPagingItemsIndexed(
                        key = { _, item -> item.id },
                        contentType = { _, _ -> "item" }
                    ) { _, item ->
                        ItemListItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateItem()
                                .padding(horizontal = spacing.xmedium),
                            item = item,
                            onClick = { id, name ->
                                onAction(ItemAction.OnItemClick(id, name))
                            }
                        )
                    }
                    onAppendItem {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                Modifier
                                    .animateItem()
                            )
                        }
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
            val text = remember(category, sp) { category.displayName(sp) }
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
    val state = remember { mutableStateOf(ItemState()) }
    RustHubTheme {
        ItemScreen(
            state = state,
            onAction = {},
            onNavigate = {},
            uiEvent = flowOf(UiEvent.Navigate(ItemList)),
            pagedList = flowOf(PagingData.from(emptyList<ItemSummary>())).collectAsLazyPagingItems(),
            showAds = true,
            adState = remember { mutableStateOf(NativeAdState()) },
            onAdAction = {}
        )
    }
}
