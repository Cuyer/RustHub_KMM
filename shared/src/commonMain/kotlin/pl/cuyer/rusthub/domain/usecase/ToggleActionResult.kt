package pl.cuyer.rusthub.domain.usecase

sealed interface ToggleActionResult {
    object Success : ToggleActionResult
    object Queued : ToggleActionResult
    data class Error(val exception: Throwable) : ToggleActionResult
}
