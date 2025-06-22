package pl.cuyer.rusthub.util.validator

/** Username validator requiring at least 3 characters. */
object UsernameValidator : Validator<String> {
    override fun validate(value: String): ValidationResult {
        return if (value.length >= 3) {
            ValidationResult(true, null)
        } else {
            ValidationResult(false, "Username too short")
        }
    }
}
