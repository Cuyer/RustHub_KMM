package pl.cuyer.rusthub.android.feature.monument

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import kotlinx.coroutines.launch
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.util.composeUtil.stringResource
import pl.cuyer.rusthub.presentation.features.monument.MonumentDetailsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonumentDetailsScreen(
    state: State<MonumentDetailsState>,
    onNavigateUp: () -> Unit,
) {
    val pages = state.value.monumentPages
    val pagerState = rememberPagerState { pages.size }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.value.monument?.name.orEmpty(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(SharedRes.strings.back),
                        )
                    }
                }
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
        ) {
            if (pages.isNotEmpty()) {
                PrimaryScrollableTabRow(selectedTabIndex = pagerState.currentPage) {
                    pages.forEachIndexed { index, page ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                            text = { Text(stringResource(page.title)) }
                        )
                    }
                }
                HorizontalPager(
                    modifier = Modifier.fillMaxSize(),
                    state = pagerState
                ) { page ->
                    when (pages[page]) {
                        MonumentPage.Spawns -> {
                            state.value.monument?.spawns?.let { spawns ->
                                MonumentSpawnsPage(
                                    spawns = spawns,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } ?: Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = stringResource(SharedRes.strings.coming_soon))
                            }
                        }

                        else -> Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = stringResource(SharedRes.strings.coming_soon))
                        }
                    }
                }
            }
        }
    }
}

private val MonumentDetailsState.monumentPages: List<MonumentPage>
    get() = buildList {
        monument?.attributes?.let { add(MonumentPage.Attributes) }
        monument?.spawns?.let { add(MonumentPage.Spawns) }
        monument?.usableEntities?.let { add(MonumentPage.UsableEntities) }
        monument?.mining?.let { add(MonumentPage.Mining) }
        monument?.puzzles?.let { add(MonumentPage.Puzzles) }
    }

private sealed interface MonumentPage {
    val title: dev.icerock.moko.resources.StringResource
    data object Attributes : MonumentPage { override val title = SharedRes.strings.attributes }
    data object Spawns : MonumentPage { override val title = SharedRes.strings.spawns }
    data object UsableEntities : MonumentPage { override val title = SharedRes.strings.usable_entities }
    data object Mining : MonumentPage { override val title = SharedRes.strings.mining }
    data object Puzzles : MonumentPage { override val title = SharedRes.strings.puzzles }
}
