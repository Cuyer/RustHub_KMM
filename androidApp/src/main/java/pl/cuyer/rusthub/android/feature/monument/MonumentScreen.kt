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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavKey
import androidx.paging.compose.LazyPagingItems
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.BuildConfig
import pl.cuyer.rusthub.android.ads.NativeAdListItem
import pl.cuyer.rusthub.android.designsystem.MonumentListItem
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
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
    val syncState = state.value.syncState
    val searchBarState = rememberSearchBarState()
    val textFieldState = rememberTextFieldState(state.value.searchText)
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
            onAdAction(AdAction.LoadAd(BuildConfig.MONUMENTS_ADMOB_NATIVE_AD_ID))
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
                            onAction(
                                MonumentAction.OnSearch(textFieldState.text.toString())
                            )
                        },
                        onOpenFilters = {},
                        searchQueryUi = { emptyList() },
                        onDelete = {},
                        onClearSearchQuery = {},
                        isLoadingSearchHistory = { false },
                        placeholderRes = SharedRes.strings.search_monuments,
                        showFiltersIcon = false,
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
            Column(modifier = Modifier.fillMaxSize()) {
                MonumentTypeChips(
                    selected = state.value.selectedType,
                    onSelectedChange = { onAction(MonumentAction.OnTypeChange(it)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(spacing.medium)
                )
                if (syncState == MonumentSyncState.PENDING) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    HandlePagingItems(
                        items = { pagedList },
                        onError = {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                item(key = "error", contentType = "error") {
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
                        },
                        onEmpty = {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                item(key = "empty", contentType = "empty") {
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
                                            text = stringResource(
                                                SharedRes.strings.no_items_available
                                            ),
                                            style = MaterialTheme.typography.bodyLarge,
                                            textAlign = TextAlign.Center
                                        )
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
                                bottom = WindowInsets.safeDrawing.asPaddingValues()
                                    .calculateBottomPadding()
                            ),
                            state = lazyListState,
                            verticalArrangement = Arrangement.spacedBy(spacing.medium),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            onPagingItemsIndexed(
                                key = { index, monument ->
                                    if (showAds && index == adIndex) "ad" else monument.slug
                                },
                                contentType = { index, _ ->
                                    if (showAds && index == adIndex) "ad" else "monument"
                                }
                            ) { index, monument ->
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
                                            ad = ads.value.ads[
                                                BuildConfig.MONUMENTS_ADMOB_NATIVE_AD_ID
                                            ]
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
                                    onClick = { slug ->
                                        onAction(MonumentAction.OnMonumentClick(slug))
                                    }
                                )
                            }
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
                        contentDescription = stringResource(
                            SharedRes.strings.scroll_to_top
                        ),
                        tint = contentColorFor(FloatingActionButtonDefaults.containerColor)
                    )
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

