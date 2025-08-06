package pl.cuyer.rusthub.android.feature.monument

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.paging.compose.LazyPagingItems
import kotlinx.coroutines.flow.Flow
import org.koin.compose.koinInject
import pl.cuyer.rusthub.android.BuildConfig
import pl.cuyer.rusthub.android.ads.NativeAdListItem
import pl.cuyer.rusthub.android.designsystem.MonumentListItem
import pl.cuyer.rusthub.android.designsystem.RustSearchBarTopAppBar
import pl.cuyer.rusthub.android.navigation.ObserveAsEvents
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.android.util.composeUtil.stringResource
import pl.cuyer.rusthub.presentation.features.ads.AdAction
import pl.cuyer.rusthub.presentation.features.ads.NativeAdState
import pl.cuyer.rusthub.presentation.features.monument.MonumentAction
import pl.cuyer.rusthub.presentation.features.monument.MonumentState
import pl.cuyer.rusthub.presentation.navigation.UiEvent
import pl.cuyer.rusthub.domain.model.Monument
import pl.cuyer.rusthub.domain.model.MonumentType
import pl.cuyer.rusthub.domain.model.displayName
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.util.StringProvider

@OptIn(ExperimentalMaterial3Api::class)
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
    val searchBarState = rememberSearchBarState()
    val textFieldState = rememberTextFieldState(state.value.searchText)
    val lazyListState = rememberLazyListState()
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
        topBar = {
            RustSearchBarTopAppBar(
                searchBarState = searchBarState,
                textFieldState = textFieldState,
                onSearchTriggered = {
                    onAction(MonumentAction.OnSearch(textFieldState.text.toString()))
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
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .consumeWindowInsets(inner)
                .fillMaxSize()
        ) {
            MonumentTypeChips(
                selected = state.value.selectedType,
                onSelectedChange = { onAction(MonumentAction.OnTypeChange(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(spacing.medium)
            )
            val adIndex = remember(pagedList.itemCount) {
                if (pagedList.itemCount > 0) {
                    if (pagedList.itemCount >= 5) 4 else pagedList.itemCount - 1
                } else -1
            }
            LazyColumn(
                contentPadding = PaddingValues(
                    bottom = WindowInsets.safeDrawing
                        .asPaddingValues().calculateBottomPadding()
                ),
                state = lazyListState,
                verticalArrangement = Arrangement.spacedBy(spacing.medium),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                itemsIndexed(pagedList.itemSnapshotList.items) { index, monument ->
                    if (showAds && index == adIndex) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            NativeAdListItem(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = spacing.xmedium),
                                ad = ads.value.ads[BuildConfig.MONUMENTS_ADMOB_NATIVE_AD_ID]
                            )
                            Spacer(modifier = Modifier.height(spacing.medium))
                        }
                    }
                    MonumentListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = spacing.xmedium),
                        monument = monument,
                        onClick = { slug -> onAction(MonumentAction.OnMonumentClick(slug)) }
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
