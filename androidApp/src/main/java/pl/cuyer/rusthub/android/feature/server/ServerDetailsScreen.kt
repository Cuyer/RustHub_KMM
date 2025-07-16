package pl.cuyer.rusthub.android.feature.server

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import coil3.compose.AsyncImage
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.compose.BindEffect
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.compose.koinInject
import pl.cuyer.rusthub.android.designsystem.NotificationInfoDialog
import pl.cuyer.rusthub.android.designsystem.ServerDetail
import pl.cuyer.rusthub.android.designsystem.ServerWebsite
import pl.cuyer.rusthub.android.theme.RustHubTheme
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.SharedRes
import androidx.compose.ui.platform.LocalContext
import pl.cuyer.rusthub.android.navigation.ObserveAsEvents
import pl.cuyer.rusthub.domain.model.Flag
import pl.cuyer.rusthub.domain.model.Flag.Companion.toDrawable
import pl.cuyer.rusthub.domain.model.ServerStatus
import pl.cuyer.rusthub.domain.model.Theme
import pl.cuyer.rusthub.domain.model.displayName
import pl.cuyer.rusthub.presentation.features.server.ServerDetailsAction
import pl.cuyer.rusthub.presentation.features.server.ServerDetailsState
import pl.cuyer.rusthub.presentation.navigation.ServerDetails
import pl.cuyer.rusthub.presentation.navigation.UiEvent

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ServerDetailsScreen(
    onNavigate: (NavKey) -> Unit,
    stateProvider: () -> State<ServerDetailsState>,
    onAction: (ServerDetailsAction) -> Unit,
    uiEvent: Flow<UiEvent>
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val lazyListState = rememberLazyListState()
    val state = stateProvider().value
    val context = LocalContext.current

    ObserveAsEvents(uiEvent) { event ->
        if (event is UiEvent.Navigate) onNavigate(event.destination)
    }

    val permissionsController = koinInject<PermissionsController>()
    BindEffect(permissionsController)

    if (state.showNotificationInfo) {
        NotificationInfoDialog(
            showDialog = true,
            onConfirm = { onAction(ServerDetailsAction.OnSubscribe) },
            onDismiss = { onAction(ServerDetailsAction.OnDismissNotificationInfo) }
        )
    }

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.serverName ?: "",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
                    var expanded by remember { mutableStateOf(false) }
                    val rotation by animateFloatAsState(if (expanded) 90f else 0f)
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = null,
                            modifier = Modifier.rotate(rotation)
                        )
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    if (state.details?.isFavorite == true) {
                                        SharedRes.strings.remove_from_favourites.getString(context)
                                    } else {
                                        SharedRes.strings.add_to_favourites.getString(context)
                                    }
                                )
                            },
                            onClick = {
                                expanded = false
                                onAction(ServerDetailsAction.OnToggleFavourite)
                            },
                            leadingIcon = {
                                val icon = if (state.details?.isFavorite == true) {
                                    Icons.Filled.Favorite
                                } else {
                                    Icons.Outlined.FavoriteBorder
                                }
                                Icon(icon, contentDescription = null)
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    if (state.details?.isSubscribed == true) {
                                        SharedRes.strings.turn_off_notifications.getString(context)
                                    } else {
                                        SharedRes.strings.turn_on_notifications.getString(context)
                                    }
                                )
                            },
                            onClick = {
                                expanded = false
                                onAction(ServerDetailsAction.OnSubscribe)
                            },
                            leadingIcon = {
                                val icon = if (state.details?.isSubscribed == true) {
                                    Icons.Filled.Notifications
                                } else {
                                    Icons.Outlined.NotificationsNone
                                }
                                Icon(icon, contentDescription = null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(SharedRes.strings.share.getString(context)) },
                            onClick = {
                                expanded = false
                                onAction(ServerDetailsAction.OnShare)
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Share, contentDescription = null)
                            }
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        if (state.details != null) {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                state.details?.let {
                    item {
                        Text(
                            modifier = Modifier.padding(spacing.medium),
                            style = MaterialTheme.typography.titleLarge,
                            text = SharedRes.strings.general_info.getString(context)
                        )

                        it.headerImage?.let {
                            AsyncImage(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = spacing.medium),
                                model = it,
                                contentDescription = null,
                            )
                        }

                        it.ranking?.let {
                            ServerDetail(
                                modifier = Modifier.padding(spacing.medium),
                                label = SharedRes.strings.ranking.getString(context),
                                value = it.toInt()
                            )
                        }
                        it.serverStatus?.let {
                            ServerDetail(
                                modifier = Modifier.padding(spacing.medium),
                                label = SharedRes.strings.status.getString(context),
                                value = it.name,
                                valueColor = if (it == ServerStatus.ONLINE) Color(0xFF00C853) else Color(
                                    0xFFF44336
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
                                    text = SharedRes.strings.ip.getString(context) + ": "
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
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = SharedRes.strings.icon_to_copy_ip_address.getString(context)
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
                                    text = SharedRes.strings.country.getString(context) + ":"
                                )
                                Flag.fromDisplayName(it.displayName)?.let { flag ->
                                    Image(
                                        painter = painterResource(flag.toDrawable()),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(26.dp)
                                    )
                                }
                            }
                        }

                        it.averageFps?.let {
                            ServerDetail(
                                modifier = Modifier.padding(spacing.medium),
                                label = SharedRes.strings.average_fps.getString(context),
                                value = it.toString()
                            )
                        }

                        it.lastWipe?.let {
                            ServerDetail(
                                modifier = Modifier.padding(spacing.medium),
                                label = SharedRes.strings.last_wipe.getString(context),
                                value = it
                            )
                        }

                        it.nextWipe?.let {
                            ServerDetail(
                                modifier = Modifier.padding(spacing.medium),
                                label = SharedRes.strings.next_wipe.getString(context),
                                value = it
                            )
                        }

                        it.nextMapWipe?.let {
                            ServerDetail(
                                modifier = Modifier.padding(spacing.medium),
                                label = SharedRes.strings.next_map_wipe.getString(context),
                                value = it
                            )
                        }

                        it.pve?.let {
                            ServerDetail(
                                modifier = Modifier.padding(spacing.medium),
                                label = SharedRes.strings.pve.getString(context),
                                value = if (it) SharedRes.strings.true_str.getString(context) else SharedRes.strings.no.getString(context)
                            )
                        }

                        it.website?.let { url ->
                            ServerWebsite(
                                website = url,
                                spacing = spacing,
                                urlColor = Color(0xFF1E88E5)
                            )
                        }

                        it.isOfficial?.let {
                            ServerDetail(
                                modifier = Modifier.padding(spacing.medium),
                                label = SharedRes.strings.official.getString(context),
                                value = if (it) SharedRes.strings.true_str.getString(context) else SharedRes.strings.false_str.getString(context)
                            )
                        }

                        it.isPremium?.let {
                            ServerDetail(
                                modifier = Modifier.padding(spacing.medium),
                                label = SharedRes.strings.premium.getString(context),
                                value = if (it) SharedRes.strings.true_str.getString(context) else SharedRes.strings.false_str.getString(context)
                            )
                        }
                    }

                    item {
                        HorizontalDivider(modifier = Modifier.padding(vertical = spacing.medium))
                        Text(
                            modifier = Modifier.padding(spacing.medium),
                            style = MaterialTheme.typography.titleLarge,
                            text = SharedRes.strings.settings.getString(context)
                        )

                        it.maxGroup?.let {
                            ServerDetail(
                                modifier = Modifier.padding(spacing.medium),
                                label = SharedRes.strings.group_limit.getString(context),
                                value = if (it == 999999L) SharedRes.strings.none.getString(context) else it.toString()
                            )
                        }

                        it.blueprints?.let {
                            ServerDetail(
                                modifier = Modifier.padding(spacing.medium),
                                label = SharedRes.strings.blueprints.getString(context),
                                value = if (it) SharedRes.strings.enabled.getString(context) else SharedRes.strings.disabled.getString(context)
                            )
                        }

                        it.kits?.let {
                            ServerDetail(
                                modifier = Modifier.padding(spacing.medium),
                                label = SharedRes.strings.kits.getString(context),
                                value = if (it) SharedRes.strings.yes.getString(context) else SharedRes.strings.no_kits.getString(context)
                            )
                        }

                        it.decay?.let {
                            ServerDetail(
                                modifier = Modifier.padding(spacing.medium),
                                label = SharedRes.strings.decay.getString(context),
                                value = it * 100L
                            )
                        }

                        it.upkeep?.let {
                            ServerDetail(
                                modifier = Modifier.padding(spacing.medium),
                                label = SharedRes.strings.upkeep.getString(context),
                                value = it * 100L
                            )
                        }

                        it.rates?.let {
                            ServerDetail(
                                modifier = Modifier.padding(spacing.medium),
                                label = SharedRes.strings.rates.getString(context),
                                value = it
                            )
                        }
                    }
                    item {
                        HorizontalDivider(modifier = Modifier.padding(vertical = spacing.medium))
                        Text(
                            modifier = Modifier.padding(spacing.medium),
                            style = MaterialTheme.typography.titleLarge,
                            text = SharedRes.strings.description.getString(context)
                        )
                        HtmlStyledText(
                            modifier = Modifier.padding(spacing.medium),
                            html = it.description ?: ""
                        )
                    }
                    item {
                        HorizontalDivider(modifier = Modifier.padding(vertical = spacing.medium))
                        Text(
                            modifier = Modifier.padding(spacing.medium),
                            style = MaterialTheme.typography.titleLarge,
                            text = SharedRes.strings.map_information.getString(context)
                        )
                        it.seed?.let {
                            ServerDetail(
                                modifier = Modifier.padding(spacing.medium),
                                label = SharedRes.strings.seed.getString(context),
                                value = it.toString()
                            )
                        }
                        it.mapSize?.let {
                            ServerDetail(
                                modifier = Modifier.padding(spacing.medium),
                                label = SharedRes.strings.map_size.getString(context),
                                value = it
                            )
                        }
                        it.mapName?.let {
                            ServerDetail(
                                modifier = Modifier.padding(spacing.medium),
                                label = SharedRes.strings.map_name.getString(context),
                                value = it.displayName
                            )
                        }
                        it.monuments?.let {
                            ServerDetail(
                                modifier = Modifier.padding(spacing.medium),
                                label = SharedRes.strings.monuments.getString(context),
                                value = it
                            )
                        }
                        it.mapUrl?.let {
                            ServerWebsite(
                                label = SharedRes.strings.additional_information_available_at.getString(context),
                                website = it,
                                alias = SharedRes.strings.rustmaps.getString(context),
                                spacing = spacing,
                                urlColor = Color(0xFF1E88E5)
                            )
                        }

                        it.mapImage?.let {
                            AsyncImage(
                                modifier = Modifier.padding(
                                    start = spacing.medium,
                                    end = spacing.medium,
                                    bottom = spacing.medium
                                ),
                                model = it,
                                contentDescription = SharedRes.strings.rust_map_image.getString(context)
                            )
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
                        SpanStyle(color = Color(0xFF00897B)),
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
                    SpanStyle(color = Color(0xFF00897B)),
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
    RustHubTheme(theme = Theme.SYSTEM) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            ServerDetailsScreen(
                onNavigate = {},
                stateProvider = { mutableStateOf(ServerDetailsState()) },
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