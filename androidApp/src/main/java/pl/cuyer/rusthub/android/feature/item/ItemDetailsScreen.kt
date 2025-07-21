package pl.cuyer.rusthub.android.feature.item

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import pl.cuyer.rusthub.presentation.features.item.ItemDetailsState
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.util.composeUtil.stringResource
import dev.icerock.moko.resources.StringResource
import kotlinx.coroutines.launch

private enum class DetailsPage(val title: StringResource) {
    LOOTING(SharedRes.strings.looting),
    CRAFTING(SharedRes.strings.crafting),
    RECYCLING(SharedRes.strings.recycling),
    RAIDING(SharedRes.strings.raiding)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailsScreen(
    stateProvider: () -> State<ItemDetailsState>,
    onNavigateUp: () -> Unit,
) {
    val state = stateProvider().value

    // Build list of only available details pages and their content
    val availablePages = remember(state.item) {
        buildList {
            state.item?.looting?.takeIf { it.isNotEmpty() }?.let { add(DetailsPage.LOOTING to it) }
            state.item?.crafting?.let { add(DetailsPage.CRAFTING to it) }
            state.item?.recycling?.let { add(DetailsPage.RECYCLING to it) }
            state.item?.raiding?.takeIf { it.isNotEmpty() }?.let { add(DetailsPage.RAIDING to it) }
        }
    }
    val pagerState = rememberPagerState { availablePages.size }
    val scope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.item?.name.orEmpty(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.SemiBold
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
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // TabRow for each available page
            if (availablePages.isNotEmpty()) {
                PrimaryTabRow(selectedTabIndex = pagerState.currentPage) {
                    availablePages.forEachIndexed { index, (page, _) ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            text = { Text(stringResource(page.title)) }
                        )
                    }
                }
            }

            HorizontalPager(state = pagerState) { page ->
                val (detailsPage, data) = availablePages[page]
                DetailsContent(detailsPage, data)
            }
        }
    }
}

@Composable
private fun DetailsContent(page: DetailsPage, content: Any?) {
    // Specialize per-page if needed
    Text(content?.toString() ?: "")
}
