package pl.cuyer.rusthub.util.validator

import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.util.StringProvider

/** Simple e-mail validator using regex. */
class EmailValidator(
    private val stringProvider: StringProvider
) : Validator<String> {
    private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")

    override fun validate(value: String): ValidationResult {
        return if (emailRegex.matches(value)) {
            ValidationResult(true, null)
        } else {
            ValidationResult(
                false,
                stringProvider.get(SharedRes.strings.invalid_email_format)
            )
        }
    }
}
