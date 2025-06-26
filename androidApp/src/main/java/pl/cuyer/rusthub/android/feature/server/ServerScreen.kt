package pl.cuyer.rusthub.android.feature.server

import android.app.Activity
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import app.cash.paging.PagingData
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock.System
import kotlinx.datetime.Instant
import pl.cuyer.rusthub.android.designsystem.FilterBottomSheet
import pl.cuyer.rusthub.android.designsystem.RustSearchBarTopAppBar
import pl.cuyer.rusthub.android.designsystem.ServerListItem
import pl.cuyer.rusthub.android.designsystem.ServerListItemShimmer
import pl.cuyer.rusthub.android.model.Label
import pl.cuyer.rusthub.android.navigation.ObserveAsEvents
import pl.cuyer.rusthub.android.theme.RustHubTheme
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.android.util.HandlePagingItems
import pl.cuyer.rusthub.domain.exception.NetworkUnavailableException
import pl.cuyer.rusthub.domain.exception.TimeoutException
import pl.cuyer.rusthub.domain.model.Flag.Companion.toDrawable
import pl.cuyer.rusthub.domain.model.ServerFilter
import pl.cuyer.rusthub.domain.model.ServerStatus
import pl.cuyer.rusthub.domain.model.WipeType
import pl.cuyer.rusthub.presentation.features.server.ServerAction
import pl.cuyer.rusthub.presentation.features.server.ServerState
import pl.cuyer.rusthub.presentation.model.ServerInfoUi
import pl.cuyer.rusthub.presentation.navigation.ServerDetails
import pl.cuyer.rusthub.presentation.navigation.UiEvent
import java.util.Locale
import java.util.UUID

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalMaterial3WindowSizeClassApi::class
)
@Composable
fun ServerScreen(
    onNavigate: (NavKey) -> Unit,
    stateProvider: () -> State<ServerState>,
    onAction: (ServerAction) -> Unit,
    pagedList: LazyPagingItems<ServerInfoUi>,
    uiEvent: Flow<UiEvent>
) {
    val state = stateProvider()

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

    val windowSizeClass = calculateWindowSizeClass(context as Activity)

    val isTabletMode = windowSizeClass.widthSizeClass >= WindowWidthSizeClass.Medium

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    Scaffold(
        topBar = {
            Column(
                modifier = with(scrollBehavior)
                { Modifier.searchBarScrollBehavior() }
            ) {
                RustSearchBarTopAppBar(
                    searchBarState = searchBarState,
                    textFieldState = textFieldState,
                    onSearchTriggered = {
                        onAction(ServerAction.OnSearch(textFieldState.text.toString()))
                    },
                    onOpenFilters = { showSheet = true },
                    searchQueryUi = state.value.searchQuery,
                    onDelete = {
                        if (it.isBlank()) onAction(ServerAction.DeleteSearchQueries) else onAction(
                            ServerAction.DeleteSearchQueryByQuery(it)
                        )
                    },
                    onClearSearchQuery = {
                        onAction(ServerAction.OnClearSearchQuery)
                    },
                    isLoadingSearchHistory = state.value.isLoadingSearchHistory
                )
                ServerFilterChips(
                    selected = state.value.filter,
                    onSelectedChange = {
                        onAction(ServerAction.OnFilterChange(it))
                        pagedList.refresh()
                    },
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(horizontal = spacing.xmedium)
                        .then(
                            if (isTabletMode) Modifier.displayCutoutPadding() else Modifier
                        )

                )
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
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            HandlePagingItems(pagedList) {
                onRefresh {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(spacing.medium)
                    ) {
                        items(
                            count = 6
                        ) {
                            ServerListItemShimmer(
                                modifier = Modifier
                                    .animateItem()
                                    .padding(horizontal = spacing.xmedium)
                            )
                        }
                    }
                }
                onError { error ->
                    when (error) {
                        is NetworkUnavailableException, is TimeoutException -> Unit
                        else -> onAction(ServerAction.OnError(error.message ?: "Unknown Error"))
                    }
                }
                onSuccess { items ->
                    LazyColumn(
                        state = lazyListState,
                        verticalArrangement = Arrangement.spacedBy(spacing.medium),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        onPagingItems(key = { it.id ?: UUID.randomUUID() }) { item ->
                            val interactionSource = remember { MutableInteractionSource() }
                            val labels by rememberUpdatedState(createLabels(item))
                            val details by rememberUpdatedState(createDetails(item))
                            ServerListItem(
                                modifier = Modifier
                                    .animateItem()
                                    .padding(horizontal = spacing.xmedium)
                                    .combinedClickable(
                                        interactionSource = interactionSource,
                                        onLongClick = {
                                            onAction(ServerAction.OnLongServerClick(item.serverIp))
                                        },
                                        onClick = {
                                            onAction(
                                                ServerAction.OnServerClick(
                                                    item.id ?: Long.MAX_VALUE,
                                                    item.name ?: ""
                                                )
                                            )
                                        }
                                    ),
                                serverName = item.name.orEmpty(),
                                flag = item.serverFlag.toDrawable(),
                                labels = labels,
                                details = details,
                                isOnline = item.serverStatus == ServerStatus.ONLINE
                            )
                        }
                        onAppendItem {
                            CircularProgressIndicator(
                                Modifier
                                    .animateItem()
                                    .padding(6.dp)
                            )
                        }
                    }
                }
            }
            if (showSheet) {
                FilterBottomSheet(
                    stateProvider = { state },
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
                    Icon(Icons.Default.ArrowUpward, contentDescription = "Scroll to top")
                }
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
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        FilterChip(
            selected = selected == ServerFilter.ALL,
            onClick = { onSelectedChange(ServerFilter.ALL) },
            label = { Text("All") }
        )
        FilterChip(
            selected = selected == ServerFilter.FAVOURITES,
            onClick = { onSelectedChange(ServerFilter.FAVOURITES) },
            label = { Text("Favourites") }
        )
        FilterChip(
            selected = selected == ServerFilter.SUBSCRIBED,
            onClick = { onSelectedChange(ServerFilter.SUBSCRIBED) },
            label = { Text("Subscribed") }
        )
    }
}


private fun createDetails(item: ServerInfoUi): Map<String, String> {
    val details = mutableMapOf<String, String>()

    item.wipe?.let { wipeInstant: Instant ->
        val now = System.now()
        val duration = now - wipeInstant
        val minutesAgo = duration.inWholeMinutes

        val parsedTimeAgo = when (minutesAgo) {
            in 0..60 -> "$minutesAgo minutes ago"
            in 61..1440 -> "${minutesAgo / 60} hours ago"
            in 1441..10080 -> "${minutesAgo / 1440} days ago"
            else -> "${minutesAgo / 10080} weeks ago"
        }

        details["Wipe"] = parsedTimeAgo
    }

    item.ranking?.let { details["Ranking"] = it.toInt().toString() }
    item.cycle?.let {
        details["Cycle"] = "~ " + String.format(Locale.getDefault(), "%.2f", it) + " days"
    }
    item.serverCapacity?.let { details["Players"] = "${item.playerCount ?: 0}/${it}" }
    item.mapName?.let { details["Map"] = it.name }
    item.modded?.let { details["Modded"] = if (it) "Yes" else "No" }

    return details
}

private fun createLabels(item: ServerInfoUi): List<Label> {
    val labels = mutableListOf<Label>()

    item.wipeSchedule?.let {
        labels.add(Label(text = it.name))
    }
    item.difficulty?.let {
        labels.add(Label(text = it.name))
    }
    if (item.isOfficial == true) labels.add(Label(text = "Official"))

    item.wipeType?.let {
        if (it != WipeType.UNKNOWN) {
            labels.add(Label(text = it.name + " Wipe"))
        }
    }

    return labels
}

@Preview
@Composable
private fun ServerScreenPreview() {
    RustHubTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            ServerScreen(
                stateProvider = { mutableStateOf(ServerState(isRefreshing = false)) },
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
                pagedList = flowOf(PagingData.from(emptyList<ServerInfoUi>())).collectAsLazyPagingItems()
            )
        }
    }
}