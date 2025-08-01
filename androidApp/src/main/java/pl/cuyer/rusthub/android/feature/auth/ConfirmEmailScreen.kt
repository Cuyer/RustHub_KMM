package pl.cuyer.rusthub.android.feature.auth

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.animateBounds
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.flow.Flow
import org.koin.compose.koinInject
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.util.composeUtil.stringResource
import pl.cuyer.rusthub.android.designsystem.AppButton
import pl.cuyer.rusthub.android.designsystem.AppTextButton
import pl.cuyer.rusthub.android.designsystem.shimmer
import pl.cuyer.rusthub.android.designsystem.defaultFadeTransition
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import pl.cuyer.rusthub.android.navigation.ObserveAsEvents
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.common.getImageByFileName
import pl.cuyer.rusthub.presentation.features.auth.confirm.ConfirmEmailAction
import pl.cuyer.rusthub.presentation.features.auth.confirm.ConfirmEmailState
import pl.cuyer.rusthub.presentation.navigation.UiEvent
import pl.cuyer.rusthub.util.StringProvider

@OptIn(
    ExperimentalMaterial3WindowSizeClassApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalSharedTransitionApi::class, ExperimentalMaterial3ExpressiveApi::class,
)
@Composable
fun ConfirmEmailScreen(
    uiEvent: Flow<UiEvent>,
    state: State<ConfirmEmailState>,
    onAction: (ConfirmEmailAction) -> Unit,
    onNavigateUp: () -> Unit = {},
    onNavigate: (NavKey) -> Unit,
) {
    val context = LocalContext.current
    val windowSizeClass = calculateWindowSizeClass(context as Activity)
    val isTabletMode = windowSizeClass.widthSizeClass >= WindowWidthSizeClass.Medium
    val interactionSource = remember { MutableInteractionSource() }
    val focusManager = LocalFocusManager.current

    ObserveAsEvents(uiEvent) { event ->
        when (event) {
            is UiEvent.Navigate -> onNavigate(event.destination)
            is UiEvent.NavigateUp -> onNavigateUp()
        }
    }
    BackHandler {
        onAction(ConfirmEmailAction.OnBack)
    }

    Scaffold(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(SharedRes.strings.confirm_your_email),
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { onAction(ConfirmEmailAction.OnBack) },
                        modifier = Modifier.minimumInteractiveComponentSize()
                    ) {
                        Icon(
                            tint = contentColorFor(TopAppBarDefaults.topAppBarColors().containerColor),
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(SharedRes.strings.navigate_up)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .fillMaxSize()
                .clickable(interactionSource, null) { focusManager.clearFocus() },
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = state.value.resendLoading,
                transitionSpec = { defaultFadeTransition() }
            ) { loading ->
                if (loading) {
                    if (isTabletMode) {
                        ConfirmEmailShimmerExpanded()
                    } else {
                        ConfirmEmailShimmerCompact()
                    }
                } else {
                    if (isTabletMode) {
                        ConfirmEmailScreenExpanded(
                            email = { state.value.email },
                            isLoading = { state.value.isLoading },
                            onAction = onAction
                        )
                    } else {
                        ConfirmEmailScreenCompact(
                            email = { state.value.email },
                            isLoading = { state.value.isLoading },
                            onAction = onAction
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ConfirmEmailScreenCompact(
    email: () -> String,
    isLoading: () -> Boolean,
    onAction: (ConfirmEmailAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(spacing.medium),
        verticalArrangement = Arrangement.spacedBy(spacing.small),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ConfirmEmailStaticContent(email)
        AppButton(
            onClick = { onAction(ConfirmEmailAction.OnConfirm) },
            isLoading = isLoading(),
            modifier = Modifier
                .fillMaxWidth()
        ) { Text(stringResource(SharedRes.strings.confirmed)) }
        AppTextButton(
            onClick = { onAction(ConfirmEmailAction.OnResend) },
        ) {
            Text(stringResource(SharedRes.strings.resend_email))
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ConfirmEmailScreenExpanded(
    email: () -> String,
    isLoading: () -> Boolean,
    onAction: (ConfirmEmailAction) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(spacing.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ConfirmEmailStaticContent(email, Modifier.weight(1f))
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(spacing.small),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.size(spacing.medium))
            AppButton(
                onClick = { onAction(ConfirmEmailAction.OnConfirm) },
                isLoading = isLoading(),
                modifier = Modifier
                    .fillMaxWidth()
            ) { Text(stringResource(SharedRes.strings.confirmed)) }
            AppTextButton(
                onClick = { onAction(ConfirmEmailAction.OnResend) }
            ) {
                Text(stringResource(SharedRes.strings.resend_email))
            }
        }
    }
}

@Composable
private fun ConfirmEmailStaticContent(email: () -> String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
        Image(
            modifier = Modifier.size(64.dp),
            painter = painterResource(getImageByFileName("ic_mail").drawableResId),
            contentDescription = stringResource(SharedRes.strings.mail_icon),
        )
        Spacer(Modifier.size(spacing.small))

        val template = stringResource(SharedRes.strings.confirmation_sent_message)
        val parts = template.split("%s")

        val annotated = buildAnnotatedString {
            append(parts.first())
            withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) { append(email()) }
            if (parts.size > 1) append(parts[1])
        }

        Text(
            text = annotated,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun ConfirmEmailShimmerCompact() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(spacing.medium),
        verticalArrangement = Arrangement.spacedBy(spacing.small),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(MaterialTheme.shapes.extraSmall)
                .shimmer()
        )
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(20.dp)
                .clip(MaterialTheme.shapes.extraSmall)
                .shimmer()
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(MaterialTheme.shapes.extraSmall)
                .shimmer()
        )
        Box(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(36.dp)
                .clip(MaterialTheme.shapes.extraSmall)
                .shimmer()
        )
    }
}

@Composable
private fun ConfirmEmailShimmerExpanded() {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(spacing.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .shimmer()
            )
            Spacer(Modifier.size(spacing.small))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(20.dp)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .shimmer()
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(spacing.small),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.size(spacing.medium))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .shimmer()
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(36.dp)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .shimmer()
            )
        }
    }
}
