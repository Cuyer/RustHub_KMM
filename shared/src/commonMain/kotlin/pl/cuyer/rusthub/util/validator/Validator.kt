package pl.cuyer.rusthub.util.validator

/** Result of validation. */
data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null
)

/** Basic validator interface. */
fun interface Validator<T> {
    fun validate(value: T): ValidationResult
}
