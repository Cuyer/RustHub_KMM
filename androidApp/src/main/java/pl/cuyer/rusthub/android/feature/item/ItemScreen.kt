package pl.cuyer.rusthub.android.feature.item

import android.app.Activity
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.animateBounds
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavKey
import app.cash.paging.PagingData
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.BuildConfig
import pl.cuyer.rusthub.android.designsystem.ItemListItem
import pl.cuyer.rusthub.android.designsystem.ItemListItemShimmer
import pl.cuyer.rusthub.android.designsystem.RustSearchBarTopAppBar
import pl.cuyer.rusthub.android.navigation.ObserveAsEvents
import pl.cuyer.rusthub.android.theme.RustHubTheme
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.android.ads.NativeAdListItem
import pl.cuyer.rusthub.presentation.features.ads.AdAction
import pl.cuyer.rusthub.presentation.features.ads.NativeAdState
import pl.cuyer.rusthub.android.util.HandlePagingItems
import pl.cuyer.rusthub.android.util.composeUtil.stringResource
import pl.cuyer.rusthub.common.getImageByFileName
import pl.cuyer.rusthub.domain.model.ItemCategory
import pl.cuyer.rusthub.domain.model.RustItem
import pl.cuyer.rusthub.domain.model.ItemSyncState
import pl.cuyer.rusthub.domain.model.displayName
import pl.cuyer.rusthub.util.StringProvider
import pl.cuyer.rusthub.presentation.features.item.ItemAction
import pl.cuyer.rusthub.presentation.features.item.ItemState
import pl.cuyer.rusthub.presentation.features.server.ServerAction
import pl.cuyer.rusthub.presentation.navigation.ItemList
import pl.cuyer.rusthub.presentation.navigation.UiEvent

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class,
    ExperimentalSharedTransitionApi::class
)
@Composable
fun ItemScreen(
    onNavigate: (NavKey) -> Unit,
    state: State<ItemState>,
    onAction: (ItemAction) -> Unit,
    pagedList: LazyPagingItems<RustItem>,
    uiEvent: Flow<UiEvent>,
    showAds: Boolean,
    adState: State<NativeAdState>,
    onAdAction: (AdAction) -> Unit
) {
    val syncState = state.value.syncState
    val searchBarState = rememberSearchBarState()
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
    val ads = adState

    ObserveAsEvents(uiEvent) { event ->
        if (event is UiEvent.Navigate) onNavigate(event.destination)
    }

    LaunchedEffect(showAds) {
        if (showAds) {
            onAdAction(AdAction.LoadAd(BuildConfig.ITEMS_ADMOB_NATIVE_AD_ID))
        }
    }
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
                        searchQueryUi = { state.value.searchQuery },
                        onDelete = {
                            if (it.isBlank()) onAction(ItemAction.DeleteSearchQueries)
                            else onAction(ItemAction.DeleteSearchQueryByQuery(it))
                        },
                        onClearSearchQuery = { onAction(ItemAction.OnClearSearchQuery) },
                        isLoadingSearchHistory = { state.value.isLoadingSearchHistory },
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
                }
            }
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = false,
            onRefresh = {
                onAction(ItemAction.OnRefresh)
                pagedList.refresh()
            },
            state = pullToRefreshState,
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .fillMaxSize()
        ) {
            if (syncState == ItemSyncState.PENDING) {
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
            } else {
                HandlePagingItems(
                    items = { pagedList },
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
                                        Text(
                                            text = stringResource(SharedRes.strings.no_items_available),
                                            style = MaterialTheme.typography.bodyLarge,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                ) {
                    val adIndex = remember(pagedList.itemCount) {
                        if (pagedList.itemCount > 0) {
                            if (pagedList.itemCount >= 5) 4 else pagedList.itemCount - 1
                        } else -1
                    }
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
                    )
                    {
                        onPagingItemsIndexed(
                            key = { index, item ->
                                if (showAds && index == adIndex) "ad" else item.id ?: item.slug ?: item.hashCode()
                            },
                            contentType = { index, _ -> if (showAds && index == adIndex) "ad" else "item" }
                        ) { index, item ->
                            if (showAds && index == adIndex) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .animateItem()
                                ) {
                                    NativeAdListItem(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = spacing.xmedium),
                                        ad = ads.value.ads[BuildConfig.ITEMS_ADMOB_NATIVE_AD_ID]
                                    )
                                    Spacer(modifier = Modifier.height(spacing.medium))
                                }
                            }

                            ItemListItem(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .animateItem()
                                    .padding(horizontal = spacing.xmedium),
                                item = item,
                                onClick = { id ->
                                    onAction(ItemAction.OnItemClick(id))
                                }
                            )
                        }
                    }
                }
            }
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
                modifier = Modifier
                    .padding(spacing.medium)
                    .align(Alignment.BottomEnd)
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
    val state = remember { mutableStateOf(ItemState(isRefreshing = false)) }
    RustHubTheme {
        ItemScreen(
            state = state,
            onAction = {},
            onNavigate = {},
            uiEvent = flowOf(UiEvent.Navigate(ItemList)),
            pagedList = flowOf(PagingData.from(emptyList<RustItem>())).collectAsLazyPagingItems(),
            showAds = true,
            adState = remember { mutableStateOf(NativeAdState()) },
            onAdAction = {}
        )
    }
}
