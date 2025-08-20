package pl.cuyer.rusthub.util

import pl.cuyer.rusthub.presentation.snackbar.SnackbarController

actual class AppCheckTokenProvider actual constructor(
    private val stringProvider: StringProvider,
    private val snackbarController: SnackbarController,
) {
    actual suspend fun currentToken(): String? = null
}
