package pl.cuyer.rusthub.util.validator

/** Password validator requiring at least 8 characters including a digit. */
object PasswordValidator : Validator<String> {
    private val digitRegex = Regex(".*\\d.*")

    override fun validate(value: String): ValidationResult {
        return if (value.length >= 8 && digitRegex.containsMatchIn(value)) {
            ValidationResult(true, null)
        } else {
            ValidationResult(false, "Password must be 8+ chars and contain a digit")
        }
    }
}
