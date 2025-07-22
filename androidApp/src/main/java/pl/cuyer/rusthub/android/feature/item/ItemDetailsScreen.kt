package pl.cuyer.rusthub.android.feature.item

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.animateBounds
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import pl.cuyer.rusthub.presentation.features.item.ItemDetailsState
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.util.composeUtil.stringResource
import pl.cuyer.rusthub.android.designsystem.LootingListItem
import dev.icerock.moko.resources.StringResource
import kotlinx.coroutines.launch
import pl.cuyer.rusthub.android.designsystem.ItemTooltipImage
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.domain.model.Crafting
import pl.cuyer.rusthub.domain.model.CraftingIngredient
import pl.cuyer.rusthub.domain.model.CraftingRecipe
import pl.cuyer.rusthub.domain.model.ResearchTableCost
import pl.cuyer.rusthub.domain.model.TechTreeCost
import pl.cuyer.rusthub.domain.model.Looting

import pl.cuyer.rusthub.domain.model.Recycling
import pl.cuyer.rusthub.domain.model.Recycler
import pl.cuyer.rusthub.domain.model.RecyclerOutput

private enum class DetailsPage(val title: StringResource) {
    LOOTING(SharedRes.strings.looting),
    CRAFTING(SharedRes.strings.crafting),
    RECYCLING(SharedRes.strings.recycling),
    RAIDING(SharedRes.strings.raiding)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
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
            if (availablePages.isNotEmpty()) {
                PrimaryScrollableTabRow(
                    selectedTabIndex = pagerState.currentPage,
                    edgePadding = 0.dp
                ) {
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

            HorizontalPager(
                modifier = Modifier
                    .fillMaxSize(),
                state = pagerState
            ) { page ->
                val (detailsPage, data) = availablePages[page]
                DetailsContent(detailsPage, data)
            }
        }
    }
}

@Composable
private fun DetailsContent(page: DetailsPage, content: Any?) {
    when (page) {
        DetailsPage.LOOTING -> {
            val looting = content as? List<Looting> ?: emptyList()
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = spacing.medium),
                verticalArrangement = Arrangement.spacedBy(spacing.medium),
            ) {
                items(looting, key = { it.from ?: it.hashCode().toString() }) { item ->
                    LootingListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateItem()
                            .padding(horizontal = spacing.xmedium),
                        looting = item
                    )
                }
            }
        }

        DetailsPage.CRAFTING -> {
            val crafting = content as? Crafting
            crafting?.let { CraftingContent(it) }
        }
        DetailsPage.RECYCLING -> {
            val recycling = content as? Recycling
            recycling?.let { RecyclingContent(it) }
        }

        else -> Text(content?.toString() ?: "")
    }
}

@Composable
private fun CraftingContent(crafting: Crafting) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = spacing.medium),
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        crafting.craftingRecipe?.let { recipe ->
            item {
                CraftingRecipeItemList(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItem()
                        .padding(horizontal = spacing.xmedium),
                    recipe = recipe
                )
            }
        }

        crafting.researchTableCost?.let { cost ->
            item {
                ResearchTableCostItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItem()
                        .padding(horizontal = spacing.xmedium),
                    cost = cost
                )
            }
        }
        crafting.techTreeCost?.let { cost ->
            item {
                TechTreeCostItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItem()
                        .padding(horizontal = spacing.xmedium),
                    cost = cost
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CraftingRecipeItemList(
    modifier: Modifier = Modifier,
    recipe: CraftingRecipe
) {
    ElevatedCard(
        shape = RectangleShape,
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(spacing.xxsmall)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.xxsmall, Alignment.Start),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.surfaceContainerHighest,
                            shape = RectangleShape
                        ),
                    textAlign = TextAlign.Center,
                    text = stringResource(SharedRes.strings.crafting_recipe),
                    style = MaterialTheme.typography.titleLargeEmphasized,
                    fontWeight = FontWeight.SemiBold
                )
            }

            CraftingRecipeRow(
                modifier = Modifier
                    .padding(vertical = spacing.medium)
                    .fillMaxWidth(),
                recipe = recipe
            )
        }
    }
}

@Composable
private fun CraftingRecipeRow(
    modifier: Modifier = Modifier,
    recipe: CraftingRecipe
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing.medium, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(spacing.small),
        itemVerticalAlignment = Alignment.CenterVertically
    ) {
        recipe.ingredients?.let { ingredients ->
            ingredients.forEachIndexed { index, ingredient ->
                ingredient.image?.let {
                    ItemTooltipImage(
                        imageUrl = it,
                        text = "x${ingredient.amount}",
                        tooltipText = ingredient.name
                    )
                }

                if (index != ingredients.lastIndex) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                }
            }
        }

        Icon(
            modifier = Modifier.size(24.dp),
            imageVector = Icons.AutoMirrored.Filled.ArrowRight,
            contentDescription = null
        )

        recipe.outputImage?.let {
            ItemTooltipImage(
                imageUrl = it,
                text = "x${recipe.outputAmount}",
                tooltipText = recipe.outputName
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ResearchTableCostItem(
    modifier: Modifier = Modifier,
    cost: ResearchTableCost
) {
    ElevatedCard(shape = RectangleShape, modifier = modifier) {
        Column(
            verticalArrangement = Arrangement.spacedBy(spacing.xxsmall)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.xxsmall, Alignment.Start),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.surfaceContainerHighest,
                            shape = RectangleShape
                        ),
                    textAlign = TextAlign.Center,
                    text = stringResource(SharedRes.strings.research_table_cost),
                    style = MaterialTheme.typography.titleLargeEmphasized,
                    fontWeight = FontWeight.SemiBold
                )
            }

            ResearchTableCostRow(
                modifier = Modifier
                    .padding(vertical = spacing.medium)
                    .fillMaxWidth(),
                cost = cost
            )
        }
    }
}

@Composable
private fun ResearchTableCostRow(
    modifier: Modifier = Modifier,
    cost: ResearchTableCost
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing.medium, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(spacing.small),
        itemVerticalAlignment = Alignment.CenterVertically
    ) {
        cost.itemImage?.let {
            ItemTooltipImage(
                imageUrl = it,
                text = "x${cost.itemAmount}",
                tooltipText = cost.itemName
            )
        }

        Icon(
            modifier = Modifier.size(24.dp),
            imageVector = Icons.Default.Add,
            contentDescription = null
        )

        cost.scrapImage?.let {
            ItemTooltipImage(
                imageUrl = it,
                text = "x${cost.scrapAmount}",
                tooltipText = cost.scrapName
            )
        }

        Icon(
            modifier = Modifier.size(24.dp),
            imageVector = Icons.AutoMirrored.Filled.ArrowRight,
            contentDescription = null
        )

        cost.outputImage?.let {
            ItemTooltipImage(
                imageUrl = it,
                tooltipText = cost.outputName,
                addBlueprint = true
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun TechTreeCostItem(
    modifier: Modifier = Modifier,
    cost: TechTreeCost
) {
    ElevatedCard(shape = RectangleShape, modifier = modifier) {
        Column(
            verticalArrangement = Arrangement.spacedBy(spacing.xxsmall)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.xxsmall, Alignment.Start),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.surfaceContainerHighest,
                            shape = RectangleShape
                        ),
                    textAlign = TextAlign.Center,
                    text = stringResource(SharedRes.strings.tech_tree_cost),
                    style = MaterialTheme.typography.titleLargeEmphasized,
                    fontWeight = FontWeight.SemiBold
                )
            }

            TechTreeCostRow(
                modifier = Modifier
                    .padding(vertical = spacing.medium)
                    .fillMaxWidth(),
                cost = cost
            )
        }
    }
}

@Composable
private fun TechTreeCostRow(
    modifier: Modifier = Modifier,
    cost: TechTreeCost
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing.medium, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(spacing.small),
        itemVerticalAlignment = Alignment.CenterVertically
    ) {
        cost.workbenchImage?.let {
            ItemTooltipImage(
                imageUrl = it,
                tooltipText = cost.workbenchName
            )
        }

        Icon(
            modifier = Modifier.size(24.dp),
            imageVector = Icons.Default.Add,
            contentDescription = null
        )

        cost.scrapImage?.let {
            ItemTooltipImage(
                imageUrl = it,
                text = "x${cost.scrapAmount}",
                tooltipText = cost.scrapName
            )
        }
    }
}

@Composable
private fun RecyclingContent(recycling: Recycling) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = spacing.medium),
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        recycling.safezoneRecycler?.let { recycler ->
            item {
                RecyclerItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItem()
                        .padding(horizontal = spacing.xmedium),
                    recycler = recycler,
                    title = stringResource(SharedRes.strings.safezone_recycler)
                )
            }
        }

        recycling.radtownRecycler?.let { recycler ->
            item {
                RecyclerItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItem()
                        .padding(horizontal = spacing.xmedium),
                    recycler = recycler,
                    title = stringResource(SharedRes.strings.radtown_recycler)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun RecyclerItem(
    modifier: Modifier = Modifier,
    recycler: Recycler,
    title: String
) {
    ElevatedCard(shape = RectangleShape, modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.xxsmall)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.xxsmall, Alignment.Start),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.surfaceContainerHighest,
                            shape = RectangleShape
                        ),
                    textAlign = TextAlign.Center,
                    text = title,
                    style = MaterialTheme.typography.titleLargeEmphasized,
                    fontWeight = FontWeight.SemiBold
                )
            }

            recycler.guarantedOutput?.let {
                RecyclerOutputRow(
                    modifier = Modifier
                        .padding(vertical = spacing.medium)
                        .fillMaxWidth(),
                    outputs = it,
                    label = stringResource(SharedRes.strings.guaranteed_output)
                )
            }

            recycler.extraChanceOutput?.let {
                RecyclerOutputRow(
                    modifier = Modifier
                        .padding(vertical = spacing.medium)
                        .fillMaxWidth(),
                    outputs = it,
                    label = stringResource(SharedRes.strings.extra_chance_output)
                )
            }
        }
    }
}

@Composable
private fun RecyclerOutputRow(
    modifier: Modifier = Modifier,
    outputs: List<RecyclerOutput>,
    label: String
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(spacing.small)) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = label,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(spacing.medium, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(spacing.small),
            itemVerticalAlignment = Alignment.CenterVertically
        ) {
            outputs.forEach { output ->
                output.image?.let { image ->
                    ItemTooltipImage(
                        imageUrl = image,
                        text = output.amount?.let { "x${it}" },
                        tooltipText = output.name
                    )
                }
            }
        }
    }
}
