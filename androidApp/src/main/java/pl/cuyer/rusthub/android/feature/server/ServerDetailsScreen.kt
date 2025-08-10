package pl.cuyer.rusthub.android.feature.server

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.animateBounds
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import coil3.compose.SubcomposeAsyncImage
import pl.cuyer.rusthub.common.getImageByFileName
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.compose.BindEffect
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.compose.koinInject
import pl.cuyer.rusthub.android.designsystem.MapDialog
import pl.cuyer.rusthub.android.designsystem.NotificationInfoDialog
import pl.cuyer.rusthub.android.designsystem.ServerDetail
import pl.cuyer.rusthub.android.designsystem.ServerWebsite
import pl.cuyer.rusthub.android.theme.RustHubTheme
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.util.composeUtil.stringResource
import androidx.compose.ui.platform.LocalContext
import pl.cuyer.rusthub.android.designsystem.shimmer
import pl.cuyer.rusthub.android.navigation.ObserveAsEvents
import pl.cuyer.rusthub.domain.model.Flag
import pl.cuyer.rusthub.domain.model.ServerStatus
import pl.cuyer.rusthub.domain.model.Theme
import pl.cuyer.rusthub.domain.model.displayName
import pl.cuyer.rusthub.domain.model.toDrawable
import pl.cuyer.rusthub.presentation.features.server.ServerDetailsAction
import pl.cuyer.rusthub.presentation.features.server.ServerDetailsState
import pl.cuyer.rusthub.presentation.navigation.ServerDetails
import pl.cuyer.rusthub.presentation.navigation.UiEvent

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalSharedTransitionApi::class
)
@Composable
fun ServerDetailsScreen(
    onNavigate: (NavKey) -> Unit,
    onNavigateUp: () -> Unit,
    state: State<ServerDetailsState>,
    onAction: (ServerDetailsAction) -> Unit,
    uiEvent: Flow<UiEvent>
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val lazyListState = rememberLazyListState()
    ObserveAsEvents(uiEvent) { event ->
        if (event is UiEvent.Navigate) onNavigate(event.destination)
    }

    val permissionsController = koinInject<PermissionsController>()
    BindEffect(permissionsController)

    if (state.value.showNotificationInfo) {
        NotificationInfoDialog(
            showDialog = true,
            onConfirm = { onAction(ServerDetailsAction.OnSubscribe) },
            onDismiss = { onAction(ServerDetailsAction.OnDismissNotificationInfo) }
        )
    }

    state.value.details?.mapImage?.let { mapUrl ->
        if (state.value.showMap) {
            MapDialog(imageModel = mapUrl) {
                onAction(ServerDetailsAction.OnDismissMap)
            }
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.value.serverName ?: "",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(SharedRes.strings.back)
                        )
                    }
                },
                actions = {
                    var expanded by remember { mutableStateOf(false) }
                    val rotation by animateFloatAsState(if (expanded) 90f else 0f)
                    IconButton(
                        onClick = { expanded = !expanded },
                        modifier = Modifier.minimumInteractiveComponentSize()
                    ) {
                        Icon(
                            tint = contentColorFor(SearchBarDefaults.colors().containerColor),
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = stringResource(SharedRes.strings.other_options),
                            modifier = Modifier.rotate(rotation)
                        )
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    if (state.value.details?.isFavorite == true) {
                                        stringResource(SharedRes.strings.remove_from_favourites)
                                    } else {
                                        stringResource(SharedRes.strings.add_to_favourites)
                                    }
                                )
                            },
                            onClick = {
                                expanded = false
                                onAction(ServerDetailsAction.OnToggleFavourite)
                            },
                            leadingIcon = {
                                val icon = if (state.value.details?.isFavorite == true) {
                                    Icons.Filled.Favorite
                                } else {
                                    Icons.Outlined.FavoriteBorder
                                }
                                val cd = if (state.value.details?.isFavorite == true) {
                                    stringResource(SharedRes.strings.remove_from_favourites)
                                } else {
                                    stringResource(SharedRes.strings.add_to_favourites)
                                }
                                Icon(imageVector = icon, contentDescription = cd)
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    if (state.value.details?.isSubscribed == true) {
                                        stringResource(SharedRes.strings.turn_off_notifications)
                                    } else {
                                        stringResource(SharedRes.strings.turn_on_notifications)
                                    }
                                )
                            },
                            onClick = {
                                expanded = false
                                onAction(ServerDetailsAction.OnSubscribe)
                            },
                            leadingIcon = {
                                val icon = if (state.value.details?.isSubscribed == true) {
                                    Icons.Filled.Notifications
                                } else {
                                    Icons.Outlined.NotificationsNone
                                }
                                val cd = if (state.value.details?.isSubscribed == true) {
                                    stringResource(SharedRes.strings.turn_off_notifications)
                                } else {
                                    stringResource(SharedRes.strings.turn_on_notifications)
                                }
                                Icon(imageVector = icon, contentDescription = cd)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(SharedRes.strings.share)) },
                            onClick = {
                                expanded = false
                                onAction(ServerDetailsAction.OnShare)
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = stringResource(SharedRes.strings.share)
                                )
                            }
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .fillMaxSize()
        ) {
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
                    text = stringResource(SharedRes.strings.offline),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = spacing.xsmall)
                        .background(MaterialTheme.colorScheme.secondary)
                )
            }
            if (state.value.details != null) {
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier.fillMaxSize()
                ) {
                    state.value.details?.let {
                        item(key = "general_info", contentType = "general_info") {
                            Text(
                                modifier = Modifier.padding(spacing.medium),
                                style = MaterialTheme.typography.titleLarge,
                                text = stringResource(SharedRes.strings.general_info)
                            )

                            it.headerImage?.let {
                                SubcomposeAsyncImage(
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = spacing.medium),
                                    model = it,
                                    contentDescription = stringResource(
                                        SharedRes.strings.server_header_image
                                    ),
                                    loading = {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(200.dp)
                                                .shimmer()
                                        )
                                    },
                                    error = {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(200.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = stringResource(SharedRes.strings.error_image_emote),
                                                style = MaterialTheme.typography.headlineMedium
                                            )
                                        }
                                    }
                                )
                            }

                            it.ranking?.let {
                                ServerDetail(
                                    modifier = Modifier.padding(spacing.medium),
                                    label = stringResource(SharedRes.strings.ranking),
                                    value = it.toInt()
                                )
                            }
                            it.serverStatus?.let {
                                ServerDetail(
                                    modifier = Modifier.padding(spacing.medium),
                                    label = stringResource(SharedRes.strings.status),
                                    value = it.name,
                                    valueColor = if (it == ServerStatus.ONLINE) Color(0xFF008939) else Color(
                                        0xFFEA1B0C
                                    )
                                )
                            }
                            it.serverIp?.let {
                                Row(
                                    modifier = Modifier.padding(horizontal = spacing.medium),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        style = MaterialTheme.typography.bodyLarge,
                                        text = stringResource(SharedRes.strings.ip) + ": "
                                    )
                                    Text(
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            color = MaterialTheme.colorScheme.primary
                                        ),
                                        text = it
                                    )
                                    Spacer(modifier = Modifier.width(spacing.small))
                                    IconButton(
                                        onClick = {
                                            onAction(
                                                ServerDetailsAction.OnSaveToClipboard(
                                                    it
                                                )
                                            )
                                        },
                                        modifier = Modifier.minimumInteractiveComponentSize()
                                    ) {
                                        Icon(
                                            tint = contentColorFor(TopAppBarDefaults.topAppBarColors().containerColor),
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = stringResource(SharedRes.strings.icon_to_copy_ip_address)
                                        )
                                    }
                                }
                            }
                            it.serverFlag?.let {
                                Row(
                                    modifier = Modifier.padding(spacing.medium),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(spacing.small)
                                ) {
                                    Text(
                                        style = MaterialTheme.typography.bodyLarge,
                                        text = stringResource(SharedRes.strings.country) + ":"
                                    )
                                    Flag.fromDisplayName(it.displayName)?.let { flag ->
                                        Image(
                                            painter = painterResource(flag.toDrawable()),
                                            contentDescription = flag.displayName,
                                            modifier = Modifier
                                                .size(26.dp)
                                        )
                                    }
                                }
                            }

                            it.averageFps?.let {
                                ServerDetail(
                                    modifier = Modifier.padding(spacing.medium),
                                    label = stringResource(SharedRes.strings.average_fps),
                                    value = it.toString()
                                )
                            }

                            it.lastWipe?.let {
                                ServerDetail(
                                    modifier = Modifier.padding(spacing.medium),
                                    label = stringResource(SharedRes.strings.last_wipe),
                                    value = it
                                )
                            }

                            it.nextWipe?.let {
                                ServerDetail(
                                    modifier = Modifier.padding(spacing.medium),
                                    label = stringResource(SharedRes.strings.next_wipe),
                                    value = it
                                )
                            }

                            it.nextMapWipe?.let {
                                ServerDetail(
                                    modifier = Modifier.padding(spacing.medium),
                                    label = stringResource(SharedRes.strings.next_map_wipe),
                                    value = it
                                )
                            }

                            it.pve?.let {
                                ServerDetail(
                                    modifier = Modifier.padding(spacing.medium),
                                    label = stringResource(SharedRes.strings.pve),
                                    value = if (it) stringResource(SharedRes.strings.true_str) else stringResource(
                                        SharedRes.strings.no
                                    )
                                )
                            }

                            it.website?.let { url ->
                                ServerWebsite(
                                    website = url,
                                    spacing = spacing,
                                    urlColor = Color(0xFF1779CE)
                                )
                            }

                            it.isOfficial?.let {
                                ServerDetail(
                                    modifier = Modifier.padding(spacing.medium),
                                    label = stringResource(SharedRes.strings.official),
                                    value = if (it) stringResource(SharedRes.strings.true_str) else stringResource(
                                        SharedRes.strings.false_str
                                    )
                                )
                            }

                            it.isPremium?.let {
                                ServerDetail(
                                    modifier = Modifier.padding(spacing.medium),
                                    label = stringResource(SharedRes.strings.premium),
                                    value = if (it) stringResource(SharedRes.strings.true_str) else stringResource(
                                        SharedRes.strings.false_str
                                    )
                                )
                            }
                        }

                        item(key = "settings", contentType = "settings") {
                            HorizontalDivider(modifier = Modifier.padding(vertical = spacing.medium))
                            Text(
                                modifier = Modifier.padding(spacing.medium),
                                style = MaterialTheme.typography.titleLarge,
                                text = stringResource(SharedRes.strings.settings)
                            )

                            it.maxGroup?.let {
                                ServerDetail(
                                    modifier = Modifier.padding(spacing.medium),
                                    label = stringResource(SharedRes.strings.group_limit),
                                    value = if (it == 999999L) stringResource(SharedRes.strings.none) else it.toString()
                                )
                            }

                            it.blueprints?.let {
                                ServerDetail(
                                    modifier = Modifier.padding(spacing.medium),
                                    label = stringResource(SharedRes.strings.blueprints),
                                    value = if (it) stringResource(SharedRes.strings.enabled) else stringResource(
                                        SharedRes.strings.disabled
                                    )
                                )
                            }

                            it.kits?.let {
                                ServerDetail(
                                    modifier = Modifier.padding(spacing.medium),
                                    label = stringResource(SharedRes.strings.kits),
                                    value = if (it) stringResource(SharedRes.strings.yes) else stringResource(
                                        SharedRes.strings.no_kits
                                    )
                                )
                            }

                            it.decay?.let {
                                ServerDetail(
                                    modifier = Modifier.padding(spacing.medium),
                                    label = stringResource(SharedRes.strings.decay),
                                    value = it * 100L
                                )
                            }

                            it.upkeep?.let {
                                ServerDetail(
                                    modifier = Modifier.padding(spacing.medium),
                                    label = stringResource(SharedRes.strings.upkeep),
                                    value = it * 100L
                                )
                            }

                            it.rates?.let {
                                ServerDetail(
                                    modifier = Modifier.padding(spacing.medium),
                                    label = stringResource(SharedRes.strings.rates),
                                    value = it
                                )
                            }
                        }
                        item(key = "description", contentType = "description") {
                            HorizontalDivider(modifier = Modifier.padding(vertical = spacing.medium))
                            Text(
                                modifier = Modifier.padding(spacing.medium),
                                style = MaterialTheme.typography.titleLarge,
                                text = stringResource(SharedRes.strings.description)
                            )
                            HtmlStyledText(
                                modifier = Modifier.padding(spacing.medium),
                                html = it.description ?: ""
                            )
                        }
                        item(key = "map_info", contentType = "map_info") {
                            HorizontalDivider(modifier = Modifier.padding(vertical = spacing.medium))
                            Text(
                                modifier = Modifier.padding(spacing.medium),
                                style = MaterialTheme.typography.titleLarge,
                                text = stringResource(SharedRes.strings.map_information)
                            )
                            it.seed?.let {
                                ServerDetail(
                                    modifier = Modifier.padding(spacing.medium),
                                    label = stringResource(SharedRes.strings.seed),
                                    value = it.toString()
                                )
                            }
                            it.mapSize?.let {
                                ServerDetail(
                                    modifier = Modifier.padding(spacing.medium),
                                    label = stringResource(SharedRes.strings.map_size),
                                    value = it
                                )
                            }
                            it.mapName?.let {
                                ServerDetail(
                                    modifier = Modifier.padding(spacing.medium),
                                    label = stringResource(SharedRes.strings.map_name),
                                    value = it.displayName
                                )
                            }
                            it.monuments?.let {
                                ServerDetail(
                                    modifier = Modifier.padding(spacing.medium),
                                    label = stringResource(SharedRes.strings.monuments),
                                    value = it
                                )
                            }
                            it.mapUrl?.let {
                                ServerWebsite(
                                    label = stringResource(SharedRes.strings.additional_information_available_at),
                                    website = it,
                                    alias = stringResource(SharedRes.strings.rustmaps),
                                    spacing = spacing,
                                    urlColor = Color(0xFF1779CE)
                                )
                            }

                            it.mapImage?.let {
                                SubcomposeAsyncImage(
                                    modifier = Modifier
                                        .padding(
                                            start = spacing.medium,
                                            end = spacing.medium,
                                            bottom = spacing.medium
                                        )
                                        .clickable { onAction(ServerDetailsAction.OnShowMap) },
                                    model = it,
                                    contentDescription = stringResource(
                                        SharedRes.strings.rust_map_image
                                    ),
                                    loading = {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(200.dp)
                                                .shimmer()
                                        )
                                    },
                                    error = {
                                        Image(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(200.dp),
                                            painter = painterResource(id = getImageByFileName("il_not_found").drawableResId),
                                            contentDescription = stringResource(SharedRes.strings.error_not_found)
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HtmlStyledText(html: String, modifier: Modifier = Modifier) {
    Text(
        style = MaterialTheme.typography.bodyMedium,
        text = parseHtmlToAnnotatedString(html),
        modifier = modifier
    )
}

fun parseHtmlToAnnotatedString(html: String): AnnotatedString {
    val builder = AnnotatedString.Builder()

    // Replace escaped \t or literal tab marker with actual space or tab
    val normalizedHtml = html.replace("\\t", "    ").replace("\t", "    ")

    val tagRegex = Regex("<(/?)(b|i|u|t)>")
    val tagStack = ArrayDeque<String>()
    var currentIndex = 0

    val matches = tagRegex.findAll(normalizedHtml)
    for (match in matches) {
        val tag = match.groupValues[2]
        val isClosing = match.groupValues[1] == "/"
        val start = match.range.first

        if (start > currentIndex) {
            val text = normalizedHtml.substring(currentIndex, start)
            val startIndex = builder.length
            builder.append(text)
            tagStack.forEach { openTag ->
                when (openTag) {
                    "b" -> builder.addStyle(
                        SpanStyle(fontWeight = FontWeight.Bold),
                        startIndex,
                        builder.length
                    )

                    "i" -> builder.addStyle(
                        SpanStyle(fontStyle = FontStyle.Italic),
                        startIndex,
                        builder.length
                    )

                    "u" -> builder.addStyle(
                        SpanStyle(textDecoration = TextDecoration.Underline),
                        startIndex,
                        builder.length
                    )

                    "t" -> builder.addStyle(
                        SpanStyle(color = Color(0xFF008578)),
                        startIndex,
                        builder.length
                    )
                }
            }
        }

        if (!isClosing) {
            tagStack.addLast(tag)
        } else {
            tagStack.removeLastOrNull()
        }

        currentIndex = match.range.last + 1
    }

    if (currentIndex < normalizedHtml.length) {
        val text = normalizedHtml.substring(currentIndex)
        val startIndex = builder.length
        builder.append(text)
        tagStack.forEach { openTag ->
            when (openTag) {
                "b" -> builder.addStyle(
                    SpanStyle(fontWeight = FontWeight.Bold),
                    startIndex,
                    builder.length
                )

                "i" -> builder.addStyle(
                    SpanStyle(fontStyle = FontStyle.Italic),
                    startIndex,
                    builder.length
                )

                "u" -> builder.addStyle(
                    SpanStyle(textDecoration = TextDecoration.Underline),
                    startIndex,
                    builder.length
                )

                "t" -> builder.addStyle(
                    SpanStyle(color = Color(0xFF008578)),
                    startIndex,
                    builder.length
                )
            }
        }
    }

    return builder.toAnnotatedString()
}


@Preview
@Composable
private fun ServerDetailsPrev() {
    RustHubTheme() {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            ServerDetailsScreen(
                onNavigate = {},
                onNavigateUp = {},
                state = mutableStateOf(ServerDetailsState()),
                onAction = {},
                uiEvent = MutableStateFlow(
                    UiEvent.Navigate(
                        ServerDetails(
                            id = 1,
                            name = "Repulsion"
                        )
                    )
                )
            )
        }
    }
}