package pl.cuyer.rusthub.android.feature.server

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.cash.paging.LoadState
import app.cash.paging.PagingData
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
import app.cash.paging.compose.itemContentType
import app.cash.paging.compose.itemKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.Clock.System
import kotlinx.datetime.Instant
import pl.cuyer.rusthub.android.designsystem.ServerListItem
import pl.cuyer.rusthub.android.model.Label
import pl.cuyer.rusthub.android.navigation.ObserveAsEvents
import pl.cuyer.rusthub.android.theme.RustHubTheme
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.android.util.composeUtil.isAppInForeground
import pl.cuyer.rusthub.domain.model.Flag.Companion.toDrawable
import pl.cuyer.rusthub.domain.model.ServerInfo
import pl.cuyer.rusthub.presentation.features.ServerAction
import pl.cuyer.rusthub.presentation.features.ServerState
import pl.cuyer.rusthub.presentation.navigation.Destination
import pl.cuyer.rusthub.presentation.navigation.UiEvent
import java.util.Locale
import java.util.UUID
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerScreen(
    onNavigate: (Destination) -> Unit,
    stateProvider: () -> StateFlow<ServerState>,
    onAction: (ServerAction) -> Unit,
    pagedList: LazyPagingItems<ServerInfo>,
    uiEvent: Flow<UiEvent>
) {
    val state = stateProvider().collectAsStateWithLifecycle()

    ObserveAsEvents(uiEvent) { event ->
        if (event is UiEvent.Navigate) onNavigate(event.destination)
    }

    val isRefreshing = pagedList.loadState.refresh is androidx.paging.LoadState.Loading

    val pullToRefreshState = rememberPullToRefreshState()

    val isAppInForeground by isAppInForeground()

    LaunchedEffect(isAppInForeground, state.value.isLoading) {
        if (!isAppInForeground && state.value.isLoading) {
            onAction(ServerAction.OnStopAllJobs)
        }
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { onAction(ServerAction.OnRefresh) },
        state = pullToRefreshState,
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(state.value.isLoading) { isLoading ->
            if (isLoading) {

            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(spacing.medium)
                ) {
                    items(
                        count = pagedList.itemCount,
                        key = pagedList.itemKey { it.id ?: UUID.randomUUID() },
                        contentType = pagedList.itemContentType()
                    ) { index ->
                        pagedList[index]?.let { item ->
                            val labels by rememberUpdatedState(createLabels(item))
                            val details by rememberUpdatedState(createDetails(item))
                            ServerListItem(
                                modifier = Modifier.padding(horizontal = spacing.xmedium),
                                serverName = item.name.orEmpty(),
                                flag = item.serverFlag.toDrawable(),
                                labels = labels,
                                details = details
                            )
                        }
                    }
                }
            }
        }
    }
}


private fun createDetails(item: ServerInfo): Map<String, String> {
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

    item.ranking?.let { details["Ranking"] = it.roundToInt().toString() }
    item.cycle?.let { details["Cycle"] = "~ " + String.format(Locale.getDefault(), "%.2f", it) + " days" }
    item.serverCapacity?.let { details["Players"] = "${item.playerCount ?: 0}/${it}" }
    item.mapName?.let { details["Map"] = it.name }
    item.modded?.let { details["Modded"] = if (it) "Yes" else "No" }

    return details
}

private fun createLabels(item: ServerInfo): List<Label> {
    val labels = mutableListOf<Label>()

    item.wipeSchedule?.let {
        labels.add(Label(text = it.name))
    }
    item.difficulty?.let {
        labels.add(Label(text = it.name))
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
                stateProvider = { MutableStateFlow(ServerState(isLoading = false)) },
                onAction = {},
                onNavigate = {},
                uiEvent = MutableStateFlow(UiEvent.Navigate(Destination.ServerDetails)),
                pagedList = flowOf(PagingData.from(emptyList<ServerInfo>())).collectAsLazyPagingItems(),
            )
        }
    }
}