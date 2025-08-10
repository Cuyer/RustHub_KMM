package pl.cuyer.rusthub.android.feature.monument

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import kotlinx.coroutines.launch
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.util.composeUtil.stringResource
import pl.cuyer.rusthub.domain.model.MonumentAttributes
import pl.cuyer.rusthub.domain.model.MonumentPuzzle
import pl.cuyer.rusthub.domain.model.MonumentSpawns
import pl.cuyer.rusthub.domain.model.UsableEntity
import pl.cuyer.rusthub.presentation.features.monument.MonumentDetailsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonumentDetailsScreen(
    state: State<MonumentDetailsState>,
    onNavigateUp: () -> Unit,
) {
    val pages = remember(state.value.monument) {
        state.value.monument?.let { monument ->
            buildList {
                monument.mapUrls
                    ?.takeIf { it.isNotEmpty() }
                    ?.let { add(PageData.Map(it)) }
                monument.attributes
                    ?.takeIf { it.hasContent() }
                    ?.let { add(PageData.Attributes(it)) }
                monument.spawns
                    ?.takeIf { it.hasContent() }
                    ?.let { add(PageData.Spawns(it)) }
                monument.usableEntities
                    ?.takeIf { it.isNotEmpty() }
                    ?.let { add(PageData.UsableEntities(it)) }
                monument.mining
                    ?.takeIf { it.hasContent() }
                    ?.let { add(PageData.Mining(it)) }
                monument.puzzles
                    ?.takeIf { it.isNotEmpty() }
                    ?.let { add(PageData.Puzzles(it)) }
            }
        } ?: emptyList()
    }
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
                .consumeWindowInsets(inner)
                .fillMaxSize()
        ) {
            if (pages.isNotEmpty()) {
                PrimaryScrollableTabRow(selectedTabIndex = pagerState.currentPage) {
                    pages.forEachIndexed { index, page ->
                        key(page.page.title) {
                            Tab(
                                selected = pagerState.currentPage == index,
                                onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                                text = { Text(stringResource(page.page.title)) }
                            )
                        }
                    }
                }
                HorizontalPager(
                    modifier = Modifier.fillMaxSize(),
                    state = pagerState
                ) { page ->
                    when (val data = pages[page]) {
                        is PageData.Attributes -> MonumentAttributesPage(
                            attributes = data.attributes,
                            modifier = Modifier.fillMaxSize()
                        )

                        is PageData.Spawns -> MonumentSpawnsPage(
                            spawns = data.spawns,
                            modifier = Modifier.fillMaxSize()
                        )

                        is PageData.UsableEntities -> MonumentUsableEntitiesPage(
                            entities = data.entities,
                            modifier = Modifier.fillMaxSize()
                        )

                        is PageData.Mining -> MonumentMiningPage(
                            mining = data.mining,
                            modifier = Modifier.fillMaxSize()
                        )

                        is PageData.Puzzles -> MonumentPuzzlesPage(
                            puzzles = data.puzzles,
                            modifier = Modifier.fillMaxSize()
                        )

                        is PageData.Map -> MonumentMapPage(
                            mapUrls = data.mapUrls,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

@Immutable
private enum class DetailsPage(val title: dev.icerock.moko.resources.StringResource) {
    MAP(SharedRes.strings.map),
    ATTRIBUTES(SharedRes.strings.attributes),
    SPAWNS(SharedRes.strings.spawns),
    USABLE_ENTITIES(SharedRes.strings.usable_entities),
    MINING(SharedRes.strings.mining),
    PUZZLES(SharedRes.strings.puzzles)
}

@Immutable
private sealed class PageData(val page: DetailsPage) {
    data class Map(val mapUrls: List<String>) : PageData(DetailsPage.MAP)
    data class Attributes(val attributes: MonumentAttributes) : PageData(DetailsPage.ATTRIBUTES)
    data class Spawns(val spawns: MonumentSpawns) : PageData(DetailsPage.SPAWNS)
    data class UsableEntities(val entities: List<UsableEntity>) : PageData(DetailsPage.USABLE_ENTITIES)
    data class Mining(val mining: pl.cuyer.rusthub.domain.model.Mining) : PageData(DetailsPage.MINING)
    data class Puzzles(val puzzles: List<MonumentPuzzle>) : PageData(DetailsPage.PUZZLES)
}

private fun MonumentAttributes.hasContent(): Boolean {
    return listOf(
        type,
        isSafezone,
        hasTunnelEntrance,
        hasChinookDropZone,
        allowsPatrolHeliCrash,
        recyclers,
        barrels,
        crates,
        scientists,
        medianRadiationLevel,
        maxRadiationLevel,
        hasRadiation
    ).any { it != null }
}

private fun MonumentSpawns.hasContent(): Boolean {
    return listOfNotNull(container, collectable, scientist, vehicle).any { it.isNotEmpty() }
}

private fun pl.cuyer.rusthub.domain.model.Mining.hasContent(): Boolean {
    return item != null || !productionItems.isNullOrEmpty() || timePerFuelSeconds != null
}
