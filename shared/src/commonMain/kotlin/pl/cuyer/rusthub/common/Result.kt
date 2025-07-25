package pl.cuyer.rusthub.common

import androidx.compose.runtime.Immutable


@Immutable
sealed interface Result<out T> {
    @Immutable
    data class Success<T>(val data: T) : Result<T>
    @Immutable
    data class Error(val exception: Throwable) : Result<Nothing>
}
