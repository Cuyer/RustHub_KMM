package pl.cuyer.rusthub.domain.usecase

import androidx.compose.runtime.Immutable

@Immutable
sealed interface ToggleActionResult {
    @Immutable
    object Success : ToggleActionResult
    @Immutable
    object Queued : ToggleActionResult
    @Immutable
    data class Error(val exception: Throwable) : ToggleActionResult
}
