package pl.cuyer.rusthub.util.validator

/** Simple e-mail validator using regex. */
object EmailValidator : Validator<String> {
    private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")

    override fun validate(value: String): ValidationResult {
        return if (emailRegex.matches(value)) {
            ValidationResult(true, null)
        } else {
            ValidationResult(false, "Invalid e-mail format")
        }
    }
}
