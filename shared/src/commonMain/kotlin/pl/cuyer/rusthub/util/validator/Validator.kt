package pl.cuyer.rusthub.util.validator

import androidx.compose.runtime.Immutable

/** Result of validation. */
@Immutable
data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null
)

/** Basic validator interface. */
fun interface Validator<T> {
    fun validate(value: T): ValidationResult
}
