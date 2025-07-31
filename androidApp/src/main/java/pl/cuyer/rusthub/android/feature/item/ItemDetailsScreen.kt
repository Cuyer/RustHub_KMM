package pl.cuyer.rusthub.android.feature.item

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.animateBounds
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
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
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import pl.cuyer.rusthub.android.designsystem.LootContentListItem
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
import pl.cuyer.rusthub.domain.model.LootContent
import pl.cuyer.rusthub.domain.model.RaidItem
import pl.cuyer.rusthub.domain.model.RaidResource
import pl.cuyer.rusthub.domain.model.Raiding

import pl.cuyer.rusthub.domain.model.Recycling
import pl.cuyer.rusthub.domain.model.Recycler
import pl.cuyer.rusthub.domain.model.RecyclerOutput
import pl.cuyer.rusthub.domain.model.RustItem
import pl.cuyer.rusthub.domain.model.hasContent
import kotlin.math.roundToInt

@Immutable
private enum class DetailsPage(val title: StringResource) {
    LOOTING(SharedRes.strings.looting),
    CONTENTS(SharedRes.strings.contents),
    CRAFTING(SharedRes.strings.crafting),
    RECYCLING(SharedRes.strings.recycling),
    RAIDING(SharedRes.strings.raiding)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun ItemDetailsScreen(
    state: State<ItemDetailsState>,
    onNavigateUp: () -> Unit,
) {
    // Build list of only available details pages and their content
    val availablePages = remember(state.value.item) {
        state.value.item?.let { item ->
            buildList {
                item.looting?.takeIf { it.isNotEmpty() }
                    ?.let { add(DetailsPage.LOOTING to it) }
                item.lootContents?.takeIf { it.isNotEmpty() }
                    ?.let { add(DetailsPage.CONTENTS to it) }
                item.crafting?.takeIf { it.hasContent() }
                    ?.let { add(DetailsPage.CRAFTING to it) }
                item.recycling?.takeIf { it.hasContent() }
                    ?.let { add(DetailsPage.RECYCLING to it) }
                item.raiding?.takeIf { it.isNotEmpty() }
                    ?.let { add(DetailsPage.RAIDING to (it to item)) }
            }
        } ?: emptyList()
    }
    val pagerState = rememberPagerState { availablePages.size }
    val scope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.value.item?.name.orEmpty(),
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
                .consumeWindowInsets(innerPadding)
                .fillMaxSize()
        ) {
            if (availablePages.isNotEmpty()) {
                PrimaryScrollableTabRow(
                    selectedTabIndex = pagerState.currentPage
                ) {
                    availablePages.forEachIndexed { index, (page, _) ->
                        key(
                            page.title
                        ) {
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
                contentPadding = WindowInsets.safeDrawing.asPaddingValues(),
                verticalArrangement = Arrangement.spacedBy(spacing.medium),
            ) {
                items(
                    looting,
                    key = { it.from ?: it.hashCode().toString() },
                    contentType = { "looting" }
                ) { item ->
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

        DetailsPage.CONTENTS -> {
            val contents = content as? List<LootContent> ?: emptyList()
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = WindowInsets.safeDrawing.asPaddingValues(),
                verticalArrangement = Arrangement.spacedBy(spacing.medium),
            ) {
                items(
                    contents,
                    key = { it.spawn ?: it.hashCode().toString() },
                    contentType = { "contents" }
                ) { item ->
                    LootContentListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateItem()
                            .padding(horizontal = spacing.xmedium),
                        content = item
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

        DetailsPage.RAIDING -> {
            val raidingPair = content as? Pair<List<Raiding>, RustItem>
            raidingPair?.let { (raiding, item) ->
                RaidingContent(
                    iconUrl = item.iconUrl ?: item.image,
                    health = item.health,
                    raiding = raiding
                )
            }
        }
    }
}

@Composable
private fun CraftingContent(crafting: Crafting) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = WindowInsets.safeDrawing.asPaddingValues(),
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        crafting.craftingRecipe?.let { recipe ->
            item(key = "recipe", contentType = "recipe") {
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
            item(key = "research_table_cost", contentType = "research_table_cost") {
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
            item(key = "tech_tree_cost", contentType = "tech_tree_cost") {
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
        shape = MaterialTheme.shapes.extraSmall,
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
                            shape = MaterialTheme.shapes.extraSmall
                        )
                        .padding(vertical = spacing.medium),
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
    ElevatedCard(shape = MaterialTheme.shapes.extraSmall, modifier = modifier) {
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
                            shape = MaterialTheme.shapes.extraSmall
                        )
                        .padding(vertical = spacing.medium),
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
    ElevatedCard(shape = MaterialTheme.shapes.extraSmall, modifier = modifier) {
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
                            shape = MaterialTheme.shapes.extraSmall
                        )
                        .padding(vertical = spacing.medium),
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
        contentPadding = WindowInsets.safeDrawing.asPaddingValues(),
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        recycling.safezoneRecycler?.let { recycler ->
            item(key = "safezone_recycler", contentType = "recycler") {
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
            item(key = "radtown_recycler", contentType = "recycler") {
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
    ElevatedCard(shape = MaterialTheme.shapes.extraSmall, modifier = modifier) {
        Column(
            verticalArrangement = Arrangement.spacedBy(spacing.xxsmall),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainerHighest,
                        shape = MaterialTheme.shapes.extraSmall
                    )
                    .padding(vertical = spacing.xmedium),
                horizontalArrangement = Arrangement.spacedBy(
                    spacing.small,
                    Alignment.CenterHorizontally
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                recycler.image?.let { imageUrl ->
                    ItemTooltipImage(
                        imageUrl = imageUrl
                    )
                }

                Text(
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
                if (it.isNotEmpty()) {
                    RecyclerOutputRow(
                        modifier = Modifier
                            .padding(vertical = spacing.medium)
                            .fillMaxWidth(),
                        outputs = it,
                        label = stringResource(SharedRes.strings.extra_chance_output),
                        extraChance = true
                    )
                }
            }
        }
    }
}

@Composable
private fun RecyclerOutputRow(
    modifier: Modifier = Modifier,
    outputs: List<RecyclerOutput>,
    label: String,
    extraChance: Boolean = false
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
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(
                spacing.medium,
                Alignment.CenterHorizontally
            ),
            verticalArrangement = Arrangement.spacedBy(spacing.small),
            itemVerticalAlignment = Alignment.CenterVertically
        ) {
            outputs.forEach { output ->
                output.image?.let { image ->
                    val outputString = if (extraChance) {
                        "${output.amount?.times(100)}%"
                    } else {
                        "x${output.amount?.toInt()}"
                    }

                    ItemTooltipImage(
                        imageUrl = image,
                        text = outputString,
                        tooltipText = output.name
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RaidingContent(
    iconUrl: String?,
    health: Int?,
    raiding: List<Raiding>
) {
    val maxHealth by remember { mutableFloatStateOf(health?.toFloat() ?: 0f) }
    val sliderState =
        rememberSliderState(value = health?.toFloat() ?: 0f, valueRange = 0f..maxHealth)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = WindowInsets.safeDrawing.asPaddingValues(),
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        item(key = "slider", contentType = "slider") {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItem()
                    .padding(horizontal = spacing.xmedium),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(spacing.small)
            ) {
                iconUrl?.let { ItemTooltipImage(imageUrl = it, size = 120) }
                Slider(
                    state = sliderState
                )
                Text(
                    text = "${sliderState.value.roundToInt()} / ${maxHealth.roundToInt()} " +
                            stringResource(SharedRes.strings.hp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        items(
            raiding,
            key = { it.hashCode() },
            contentType = { "raiding" }
        ) { raid ->
            RaidingItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItem()
                    .padding(horizontal = spacing.xmedium),
                raiding = raid,
                fraction = {
                    if (maxHealth > 0f) sliderState.value / maxHealth else 1f
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun RaidingItem(
    modifier: Modifier = Modifier,
    raiding: Raiding,
    fraction: () -> Float
) {
    ElevatedCard(shape = MaterialTheme.shapes.extraSmall, modifier = modifier) {
        Column(
            verticalArrangement = Arrangement.spacedBy(spacing.xxsmall),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            raiding.startingItem?.let { start ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.surfaceContainerHighest,
                            shape = MaterialTheme.shapes.extraSmall
                        )
                        .padding(vertical = spacing.xmedium),
                    horizontalArrangement = Arrangement.spacedBy(
                        spacing.small,
                        Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    start.icon?.let { ItemTooltipImage(imageUrl = it) }
                    Text(
                        textAlign = TextAlign.Center,
                        text = start.name.orEmpty(),
                        style = MaterialTheme.typography.titleLargeEmphasized,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            raiding.timeToRaid?.let {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = spacing.medium),
                    textAlign = TextAlign.Center,
                    text = stringResource(
                        SharedRes.strings.time_to_raid,
                        formatRaidDuration((it * fraction()).toInt())
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            raiding.amount?.let {
                RaidingOutputRow(
                    modifier = Modifier
                        .padding(vertical = spacing.medium)
                        .fillMaxWidth(),
                    items = it,
                    label = stringResource(SharedRes.strings.amount),
                    fraction = fraction
                )
            }

            raiding.rawMaterialCost?.let {
                if (it.isNotEmpty()) {
                    RaidingResourceRow(
                        modifier = Modifier
                            .padding(vertical = spacing.medium)
                            .fillMaxWidth(),
                        resources = it,
                        label = stringResource(SharedRes.strings.raw_material_cost),
                        fraction = fraction
                    )
                }
            }
        }
    }
}

@Composable
private fun RaidingOutputRow(
    modifier: Modifier = Modifier,
    items: List<RaidItem>,
    label: String,
    fraction: () -> Float
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
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(
                spacing.medium,
                Alignment.CenterHorizontally
            ),
            verticalArrangement = Arrangement.spacedBy(spacing.small),
            itemVerticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { output ->
                output.icon?.let { image ->
                    ItemTooltipImage(
                        imageUrl = image,
                        text = output.amount?.let { (it * fraction()).roundToInt() }
                            ?.let { "x$it" },
                        tooltipText = output.name
                    )
                }
            }
        }
    }
}

@Composable
private fun RaidingResourceRow(
    modifier: Modifier = Modifier,
    resources: List<RaidResource>,
    label: String,
    fraction: () -> Float
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
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(
                spacing.medium,
                Alignment.CenterHorizontally
            ),
            verticalArrangement = Arrangement.spacedBy(spacing.small),
            itemVerticalAlignment = Alignment.CenterVertically
        ) {
            resources.forEach { res ->
                res.icon?.let { image ->
                    val tooltip = res.mixingTableAmount?.let {
                        res.name?.let { name ->
                            "$name\n" + stringResource(
                                SharedRes.strings.using_mixing_table,
                                it
                            )
                        }
                    } ?: res.name
                    ItemTooltipImage(
                        imageUrl = image,
                        text = res.amount?.let { (it * fraction()).roundToInt() }?.let { "x$it" },
                        tooltipText = tooltip
                    )
                }
            }
        }
    }
}

private fun formatRaidDuration(seconds: Int): String {
    val totalMinutes = seconds / 60
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    val remainingSeconds = seconds % 60

    return when {
        hours > 0 -> "${hours}h ${minutes}m"
        totalMinutes > 0 -> "${minutes}m ${remainingSeconds}s"
        else -> "${seconds}s"
    }
}
