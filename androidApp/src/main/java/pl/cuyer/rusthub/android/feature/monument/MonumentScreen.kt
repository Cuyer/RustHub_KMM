package pl.cuyer.rusthub.android.feature.monument

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.animateBounds
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavKey
import androidx.paging.compose.LazyPagingItems
import androidx.paging.LoadState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.BuildConfig
import pl.cuyer.rusthub.android.ads.NativeAdListItem
import pl.cuyer.rusthub.android.designsystem.MonumentListItem
import pl.cuyer.rusthub.android.designsystem.MonumentListItemShimmer
import pl.cuyer.rusthub.android.designsystem.RustSearchBarTopAppBar
import pl.cuyer.rusthub.android.navigation.ObserveAsEvents
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.android.util.HandlePagingItems
import pl.cuyer.rusthub.android.util.composeUtil.stringResource
import pl.cuyer.rusthub.domain.model.Monument
import pl.cuyer.rusthub.domain.model.MonumentSyncState
import pl.cuyer.rusthub.domain.model.MonumentType
import pl.cuyer.rusthub.domain.model.displayName
import pl.cuyer.rusthub.presentation.features.ads.AdAction
import pl.cuyer.rusthub.presentation.features.ads.NativeAdState
import pl.cuyer.rusthub.presentation.features.monument.MonumentAction
import pl.cuyer.rusthub.presentation.features.monument.MonumentState
import pl.cuyer.rusthub.presentation.navigation.UiEvent
import pl.cuyer.rusthub.util.StringProvider

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class
)
@Composable
fun MonumentScreen(
    state: State<MonumentState>,
    onAction: (MonumentAction) -> Unit,
    pagedList: LazyPagingItems<Monument>,
    uiEvent: Flow<UiEvent>,
    onNavigate: (NavKey) -> Unit,
    showAds: Boolean,
    adState: State<NativeAdState>,
    onAdAction: (AdAction) -> Unit,
) {
    val textFieldState = rememberTextFieldState("")
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
    val loadState = pagedList.loadState
    var isInitialLoadFinished by remember { mutableStateOf(false) }

    LaunchedEffect(
        loadState.refresh,
        loadState.prepend,
        loadState.append
    ) {
        if (!isInitialLoadFinished &&
            loadState.refresh is LoadState.NotLoading &&
            loadState.prepend !is LoadState.Loading &&
            loadState.append !is LoadState.Loading
        ) {
            isInitialLoadFinished = true
        }
    }

    val shouldShowAds = showAds && isInitialLoadFinished

    ObserveAsEvents(uiEvent) { event ->
        if (event is UiEvent.Navigate) onNavigate(event.destination)
    }

    LaunchedEffect(showAds) {
        if (showAds) {
            onAdAction(AdAction.LoadAd(BuildConfig.MONUMENTS_ADMOB_NATIVE_AD_ID))
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
            LookaheadScope {
                Column(
                    modifier = with(scrollBehavior) { Modifier.searchBarScrollBehavior() }
                        .animateBounds(this)
                ) {
                    RustSearchBarTopAppBar(
                        textFieldState = textFieldState,
                        onSearchTriggered = {
                            onAction(MonumentAction.OnSearch(textFieldState.text.toString()))
                        },
                        onOpenFilters = {},
                        searchQueryUi = { state.value.searchQueries },
                        onDelete = {
                            if (it.isBlank()) onAction(MonumentAction.DeleteSearchQueries)
                            else onAction(MonumentAction.DeleteSearchQueryByQuery(it))
                        },
                        onClearSearchQuery = { onAction(MonumentAction.OnClearSearchQuery) },
                        placeholderRes = SharedRes.strings.search_monuments,
                        showFiltersIcon = false,
                    )
                    MonumentTypeChips(
                        selected = state.value.selectedType,
                        onSelectedChange = { onAction(MonumentAction.OnTypeChange(it)) },
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
                onAction(MonumentAction.OnRefresh)
                pagedList.refresh()
            },
            state = pullToRefreshState,
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .fillMaxSize()
        ) {
            if (state.value.syncState == MonumentSyncState.PENDING) {
                LazyColumn(
                    contentPadding = PaddingValues(
                        top = 0.dp,
                        bottom = WindowInsets.safeDrawing.asPaddingValues().calculateBottomPadding(),
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
                        MonumentListItemShimmer(
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
                                            fontSize = 96.sp,
                                        )
                                        Text(
                                            text = stringResource(SharedRes.strings.error_oops),
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    }
                                }
                            }
                        }
                        onAction(MonumentAction.OnError(error))
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
                                            fontSize = 96.sp,
                                        )
                                        Text(
                                            text = stringResource(SharedRes.strings.no_monuments_available),
                                            style = MaterialTheme.typography.bodyLarge
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
                            bottom = WindowInsets.safeDrawing.asPaddingValues().calculateBottomPadding(),
                            start = 0.dp,
                            end = 0.dp
                        ),
                        state = lazyListState,
                        verticalArrangement = Arrangement.spacedBy(spacing.medium),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        onPagingItemsIndexed(
                            key = { index, item ->
                                if (shouldShowAds && index == adIndex) "ad" else item.slug ?: index
                            },
                            contentType = { index, _ -> if (shouldShowAds && index == adIndex) "ad" else "monument" }
                        ) { index, monument ->
                            if (shouldShowAds && index == adIndex) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .animateItem()
                                ) {
                                    NativeAdListItem(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = spacing.xmedium),
                                        ad = { ads.value.ads[BuildConfig.MONUMENTS_ADMOB_NATIVE_AD_ID] }
                                    )
                                    Spacer(modifier = Modifier.height(spacing.medium))
                                }
                            }
                            MonumentListItem(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .animateItem()
                                    .padding(horizontal = spacing.xmedium),
                                monument = monument,
                                onClick = { slug -> onAction(MonumentAction.OnMonumentClick(slug)) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MonumentTypeChips(
    selected: MonumentType?,
    onSelectedChange: (MonumentType?) -> Unit,
    modifier: Modifier = Modifier,
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
        items(MonumentType.entries.size) { index ->
            val type = MonumentType.entries[index]
            val text by rememberUpdatedState(type.displayName(sp))
            FilterChip(
                selected = selected == type,
                onClick = { onSelectedChange(type) },
                label = { Text(text) }
            )
        }
    }
}

