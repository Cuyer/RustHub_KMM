package pl.cuyer.rusthub.util.validator

import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.util.StringProvider

/** Password validator requiring at least 8 characters including a digit. */
class PasswordValidator(
    private val stringProvider: StringProvider
) : Validator<String> {
    private val digitRegex = Regex(".*\\d.*")

    override fun validate(value: String): ValidationResult {
        return if (value.length >= 8 && digitRegex.containsMatchIn(value)) {
            ValidationResult(true, null)
        } else {
            ValidationResult(
                false,
                stringProvider.get(SharedRes.strings.password_requirements)
            )
        }
    }
}
