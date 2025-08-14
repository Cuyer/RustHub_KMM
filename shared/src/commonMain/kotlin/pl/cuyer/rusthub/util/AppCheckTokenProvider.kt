package pl.cuyer.rusthub.util

import pl.cuyer.rusthub.presentation.snackbar.SnackbarController

/** Provides Firebase App Check tokens. */
expect class AppCheckTokenProvider(
    stringProvider: StringProvider,
    snackbarController: SnackbarController,
) {
    suspend fun currentToken(): String?
}
