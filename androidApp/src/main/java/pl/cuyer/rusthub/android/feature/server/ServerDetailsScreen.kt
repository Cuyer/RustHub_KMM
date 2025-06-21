package pl.cuyer.rusthub.android.feature.server

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import pl.cuyer.rusthub.android.designsystem.ServerDetail
import pl.cuyer.rusthub.android.theme.RustHubTheme
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.domain.model.Flag
import pl.cuyer.rusthub.domain.model.Flag.Companion.toDrawable
import pl.cuyer.rusthub.domain.model.ServerStatus
import pl.cuyer.rusthub.domain.model.displayName
import pl.cuyer.rusthub.presentation.features.ServerDetailsAction
import pl.cuyer.rusthub.presentation.features.ServerDetailsState
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

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .navigationBarsPadding(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stateProvider().value.serverName ?: "",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        AnimatedContent(targetState = stateProvider().value.details != null) { detailsReady ->
            if (detailsReady) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .consumeWindowInsets(innerPadding)
                ) {
                    stateProvider().value.details!!.let {
                        item {
                            Text(
                                modifier = Modifier.padding(spacing.medium),
                                style = MaterialTheme.typography.titleLarge,
                                text = "General info"
                            )

                            it.ranking?.let {
                                ServerDetail(
                                    modifier = Modifier.padding(spacing.medium),
                                    label = "Ranking",
                                    value = it.toInt()
                                )
                            }
                            it.serverStatus?.let {
                                ServerDetail(
                                    modifier = Modifier.padding(spacing.medium),
                                    label = "Status",
                                    value = it.name,
                                    valueColor = if (it == ServerStatus.ONLINE) Color(0xFF00C853) else Color(
                                        0xFFF44336
                                    )
                                )
                            }
                            it.serverIp?.let {
                                Row(
                                    modifier = Modifier.padding(spacing.medium),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        style = MaterialTheme.typography.bodyLarge,
                                        text = "IP: "
                                    )
                                    Text(
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            color = MaterialTheme.colorScheme.primary
                                        ),
                                        text = it
                                    )
                                    Spacer(modifier = Modifier.width(spacing.small))
                                    IconButton(
                                        onClick = { /*TODO*/ }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Icon to copy IP address"
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
                                        text = "Country:"
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
                                    label = "Average FPS",
                                    value = it.toString()
                                )
                            }

                            it.lastWipe?.let {
                                ServerDetail(
                                    modifier = Modifier.padding(spacing.medium),
                                    label = "Last wipe",
                                    value = it
                                )
                            }

                            it.pve?.let {
                                ServerDetail(
                                    modifier = Modifier.padding(spacing.medium),
                                    label = "PVE",
                                    value = if (it) "True" else "False"
                                )
                            }

                            it.website?.let {
                                ServerDetail(
                                    modifier = Modifier.padding(spacing.medium),
                                    label = "Website",
                                    value = it
                                )
                            }

                            it.isOfficial?.let {
                                ServerDetail(
                                    modifier = Modifier.padding(spacing.medium),
                                    label = "Official",
                                    value = if (it) "True" else "False"
                                )
                            }

                            it.isPremium?.let {
                                ServerDetail(
                                    modifier = Modifier.padding(spacing.medium),
                                    label = "Premium",
                                    value = if (it) "True" else "False"
                                )
                            }
                        }

                        item {
                            Text(
                                modifier = Modifier.padding(spacing.medium),
                                style = MaterialTheme.typography.titleLarge,
                                text = "Settings"
                            )

                            it.maxGroup?.let {
                                ServerDetail(
                                    modifier = Modifier.padding(spacing.medium),
                                    label = "Group limit",
                                    value = if (it == 999999L) "None" else it.toString()
                                )
                            }

                            it.blueprints?.let {
                                ServerDetail(
                                    modifier = Modifier.padding(spacing.medium),
                                    label = "Blueprints",
                                    value = if (it) "Enabled" else "Disabled"
                                )
                            }

                            it.kits?.let {
                                ServerDetail(
                                    modifier = Modifier.padding(spacing.medium),
                                    label = "Kits",
                                    value = if (it) "Yes" else "No kits"
                                )
                            }

                            it.decay?.let {
                                ServerDetail(
                                    modifier = Modifier.padding(spacing.medium),
                                    label = "Decay",
                                    value = it * 100L
                                )
                            }

                            it.upkeep?.let {
                                ServerDetail(
                                    modifier = Modifier.padding(spacing.medium),
                                    label = "Upkeep",
                                    value = it * 100L
                                )
                            }

                            it.rates?.let {
                                ServerDetail(
                                    modifier = Modifier.padding(spacing.medium),
                                    label = "Rates",
                                    value = it
                                )
                            }
                        }
                        item {
                            Text(
                                modifier = Modifier.padding(spacing.medium),
                                style = MaterialTheme.typography.titleLarge,
                                text = "Description"
                            )
                            HtmlStyledText(
                                modifier = Modifier.padding(spacing.medium),
                                html = it.description ?: ""
                            )
                        }
                        item {
                            Text(
                                modifier = Modifier.padding(spacing.medium),
                                style = MaterialTheme.typography.titleLarge,
                                text = "Map information"
                            )
                            it.seed?.let {
                                ServerDetail(
                                    modifier = Modifier.padding(spacing.medium),
                                    label = "Seed",
                                    value = it.toString()
                                )
                            }
                            it.mapSize?.let {
                                ServerDetail(
                                    modifier = Modifier.padding(spacing.medium),
                                    label = "Map size",
                                    value = it
                                )
                            }
                            it.mapName?.let {
                                ServerDetail(
                                    modifier = Modifier.padding(spacing.medium),
                                    label = "Map name",
                                    value = it.displayName
                                )
                            }
                            it.monuments?.let {
                                ServerDetail(
                                    modifier = Modifier.padding(spacing.medium),
                                    label = "Monuments",
                                    value = it
                                )
                            }
                            it.mapImage?.let {
                                AsyncImage(
                                    model = it,
                                    contentDescription = null,
                                )
                            }
                        }
                    }
                }
            } else {
                LoadingIndicator()
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
    RustHubTheme {
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