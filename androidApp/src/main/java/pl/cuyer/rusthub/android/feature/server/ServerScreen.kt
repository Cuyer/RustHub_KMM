package pl.cuyer.rusthub.android.feature.server

import android.app.Activity
import android.content.Context
import androidx.activity.compose.LocalActivity
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
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
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
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.koin.compose.koinInject
import org.koin.java.KoinJavaComponent.inject
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.BuildConfig
import pl.cuyer.rusthub.android.designsystem.FilterBottomSheet
import pl.cuyer.rusthub.android.designsystem.RustSearchBarTopAppBar
import pl.cuyer.rusthub.android.designsystem.ServerListItem
import pl.cuyer.rusthub.android.designsystem.ServerListItemShimmer
import pl.cuyer.rusthub.android.model.Label
import pl.cuyer.rusthub.android.navigation.ObserveAsEvents
import pl.cuyer.rusthub.android.ads.NativeAdCard
import pl.cuyer.rusthub.presentation.features.ads.NativeAdViewModel
import org.koin.compose.viewmodel.koinViewModel
import pl.cuyer.rusthub.android.theme.RustHubTheme
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.android.util.HandlePagingItems
import pl.cuyer.rusthub.android.util.composeUtil.stringResource
import pl.cuyer.rusthub.domain.model.ServerFilter
import pl.cuyer.rusthub.domain.model.ServerStatus
import pl.cuyer.rusthub.domain.model.WipeSchedule
import pl.cuyer.rusthub.domain.model.WipeType
import pl.cuyer.rusthub.presentation.features.server.ServerAction
import pl.cuyer.rusthub.presentation.features.server.ServerState
import pl.cuyer.rusthub.presentation.model.ServerInfoUi
import pl.cuyer.rusthub.presentation.model.createDetails
import pl.cuyer.rusthub.presentation.model.createLabels
import pl.cuyer.rusthub.presentation.navigation.ServerDetails
import pl.cuyer.rusthub.presentation.navigation.UiEvent
import pl.cuyer.rusthub.util.StringProvider
import pl.cuyer.rusthub.util.AdsConsentManager
import java.util.Locale
import java.util.UUID

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalSharedTransitionApi::class
)
@Composable
fun ServerScreen(
    onNavigate: (NavKey) -> Unit,
    state: State<ServerState>,
    onAction: (ServerAction) -> Unit,
    pagedList: LazyPagingItems<ServerInfoUi>,
    uiEvent: Flow<UiEvent>,
    showAds: Boolean
) {
    var showSheet by rememberSaveable { mutableStateOf(false) }
    val searchBarState = rememberSearchBarState()
    val textFieldState = rememberTextFieldState()
    val scrollBehavior = SearchBarDefaults.enterAlwaysSearchBarScrollBehavior()

    ObserveAsEvents(uiEvent) { event ->
        if (event is UiEvent.Navigate) onNavigate(event.destination)
    }

    val coroutineScope = rememberCoroutineScope()

    val pullToRefreshState = rememberPullToRefreshState()

    val lazyListState = rememberLazyListState()

    val isAtTop by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex == 0 && lazyListState.firstVisibleItemScrollOffset == 0
        }
    }

    val context: Context = LocalContext.current
    val adsConsentManager = koinInject<AdsConsentManager>()
    val adViewModel: NativeAdViewModel = koinViewModel()
    val activity = LocalActivity.current as Activity

    LaunchedEffect(adsConsentManager, context) {
        adsConsentManager.gatherConsent(activity) { _ ->
            if (adsConsentManager.canRequestAds) {
                coroutineScope.launch {
                    withContext(Dispatchers.IO) { MobileAds.initialize(context) }
                    adViewModel.preloadBatch(BuildConfig.SERVERS_ADMOB_NATIVE_AD_ID)
                }
            }
        }
    }

    val windowSizeClass = calculateWindowSizeClass(context as Activity)

    val isTabletMode = windowSizeClass.widthSizeClass >= WindowWidthSizeClass.Medium

    val stringProvider = koinInject<StringProvider>()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    Scaffold(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        topBar = {
            LookaheadScope {
                Column(
                    modifier = with(scrollBehavior)
                    { Modifier.searchBarScrollBehavior() }
                        .animateBounds(this)
                ) {
                    RustSearchBarTopAppBar(
                        searchBarState = searchBarState,
                        textFieldState = textFieldState,
                        onSearchTriggered = {
                            onAction(ServerAction.OnSearch(textFieldState.text.toString()))
                        },
                        onOpenFilters = { showSheet = true },
                        searchQueryUi = { state.value.searchQuery },
                        onDelete = {
                            if (it.isBlank()) onAction(ServerAction.DeleteSearchQueries) else onAction(
                                ServerAction.DeleteSearchQueryByQuery(it)
                            )
                        },
                        onClearSearchQuery = {
                            onAction(ServerAction.OnClearSearchQuery)
                        },
                        isLoadingSearchHistory = { state.value.isLoadingSearchHistory },
                        showFiltersIcon = true
                    )
                    ServerFilterChips(
                        selected = state.value.filter,
                        onSelectedChange = {
                            onAction(ServerAction.OnFilterChange(it))
                            pagedList.refresh()
                        },
                        modifier = Modifier
                            .padding(horizontal = spacing.xmedium)
                            .then(
                                if (isTabletMode) Modifier.displayCutoutPadding() else Modifier
                            )

                    )
                    AnimatedVisibility(
                        visible = !state.value.isConnected,
                        enter = slideInVertically(
                            animationSpec = spring(stiffness = Spring.StiffnessLow, dampingRatio = Spring.DampingRatioLowBouncy)
                        ),
                        exit = slideOutVertically(
                            animationSpec = spring(stiffness = Spring.StiffnessLow, dampingRatio = Spring.DampingRatioLowBouncy)
                        )
                    ) {
                        Text(
                            textAlign = TextAlign.Center,
                            text = stringResource(SharedRes.strings.offline_cached_servers_info),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = spacing.xsmall)
                                .background(MaterialTheme.colorScheme.secondary)
                        )
                    }
                }
            }
        },
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = false,
            onRefresh = {
                pagedList.refresh()
            },
            state = pullToRefreshState,
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .fillMaxSize()
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
                        verticalArrangement = Arrangement.spacedBy(spacing.medium)
                    ) {
                        items(6) {
                            ServerListItemShimmer(
                                modifier = Modifier
                                    .animateItem()
                                    .padding(horizontal = spacing.xmedium)
                            )
                        }
                    }
                },
                onEmpty = {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        item {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    val message = if (!state.value.isConnected) {
                                        stringResource(SharedRes.strings.no_servers_available_offline)
                                    } else {
                                        stringResource(SharedRes.strings.no_servers_available)
                                    }
                                    Text(
                                        text = "( •_•)?",
                                        style = MaterialTheme.typography.headlineLarge,
                                        textAlign = TextAlign.Center,
                                        fontSize = 96.sp
                                    )
                                    Text(
                                        text = message,
                                        style = MaterialTheme.typography.bodyLarge,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                },
                onError = { error ->
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        item {
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
                    onAction(ServerAction.OnError(error))
                }
            ) {
                LazyColumn(
                    state = lazyListState,
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
                    val keyProvider = pagedList.itemKey { it.id ?: UUID.randomUUID() }
                    for (listIndex in 0 until pagedList.itemCount) {
                        val server = pagedList[listIndex] ?: continue
                        if (showAds && (listIndex + 1) % 7 == 0) {
                            item(key = "ad_${(listIndex + 1) / 7}", contentType = "ad") {
                                NativeAdCard(
                                    slot = (listIndex + 1) / 7,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = spacing.xmedium),
                                    adId = BuildConfig.SERVERS_ADMOB_NATIVE_AD_ID
                                )
                                Spacer(modifier = Modifier.height(spacing.medium))
                            }
                        }
                        item(key = keyProvider(listIndex), contentType = "server") {
                            val interactionSource = remember { MutableInteractionSource() }
                            ServerListItem(
                                modifier = Modifier
                                    .animateItem()
                                    .padding(horizontal = spacing.xmedium)
                                    .combinedClickable(
                                        interactionSource = interactionSource,
                                        onLongClick = {
                                            onAction(ServerAction.OnLongServerClick(server.serverIp))
                                        },
                                        onClick = {
                                            onAction(
                                                ServerAction.OnServerClick(
                                                    server.id ?: Long.MAX_VALUE,
                                                    server.name ?: ""
                                                )
                                            )
                                        },
                                        onClickLabel = stringResource(SharedRes.strings.view_details),
                                    ),
                                serverName = server.name.orEmpty(),
                                flag = server.serverFlag,
                                labels = { server.createLabels(stringProvider) },
                                details = { server.createDetails(stringProvider) },
                                isOnline = server.serverStatus == ServerStatus.ONLINE
                            )
                        }
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
                                    .padding(6.dp)
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
            if (showSheet) {
                FilterBottomSheet(
                    filters = state.value.filters,
                    isLoadingFilters = state.value.isLoadingFilters,
                    sheetState = sheetState,
                    onDismiss = {
                        showSheet = false
                    },
                    onDismissAndRefresh = {
                        showSheet = false
                        pagedList.refresh()
                    },
                    onAction = onAction
                )
            }
        }
    }
}

@Composable
private fun ServerFilterChips(
    selected: ServerFilter,
    onSelectedChange: (ServerFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        item {
            FilterChip(
                selected = selected == ServerFilter.ALL,
                onClick = { onSelectedChange(ServerFilter.ALL) },
                label = { Text(stringResource(SharedRes.strings.all)) }
            )
        }
        item {
            FilterChip(
                selected = selected == ServerFilter.FAVOURITES,
                onClick = { onSelectedChange(ServerFilter.FAVOURITES) },
                label = { Text(stringResource(SharedRes.strings.favourites)) }
            )
        }
        item {
            FilterChip(
                selected = selected == ServerFilter.SUBSCRIBED,
                onClick = { onSelectedChange(ServerFilter.SUBSCRIBED) },
                label = { Text(stringResource(SharedRes.strings.subscribed)) }
            )
        }
    }
}

@Preview
@Composable
private fun ServerScreenPreview() {
    RustHubTheme() {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val state = remember { mutableStateOf(ServerState(isRefreshing = false)) }
            ServerScreen(
                state = state,
                onAction = {},
                onNavigate = {},
                uiEvent = MutableStateFlow(
                    UiEvent.Navigate(
                        ServerDetails(
                            id = 1,
                            name = "Repulsion"
                        )
                    )
                ),
                pagedList = flowOf(PagingData.from(emptyList<ServerInfoUi>())).collectAsLazyPagingItems(),
                showAds = true
            )
        }
    }
}