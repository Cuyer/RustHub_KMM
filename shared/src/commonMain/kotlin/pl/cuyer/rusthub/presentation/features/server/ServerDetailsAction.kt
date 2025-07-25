package pl.cuyer.rusthub.presentation.features.server

import androidx.compose.runtime.Immutable

@Immutable
sealed interface ServerDetailsAction {
    @Immutable
    data class OnSaveToClipboard(val ipAddress: String) : ServerDetailsAction
    @Immutable
    data object OnToggleFavourite : ServerDetailsAction
    @Immutable
    data object OnSubscribe : ServerDetailsAction
    @Immutable
    data object OnDismissSubscriptionDialog : ServerDetailsAction
    @Immutable
    data object OnDismissNotificationInfo : ServerDetailsAction
    @Immutable
    data object OnShare : ServerDetailsAction
    @Immutable
    data object OnShowMap : ServerDetailsAction
    @Immutable
    data object OnDismissMap : ServerDetailsAction
}