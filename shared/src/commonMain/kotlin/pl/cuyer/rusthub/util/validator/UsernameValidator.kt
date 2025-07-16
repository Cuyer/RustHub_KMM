package pl.cuyer.rusthub.util.validator

import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.util.StringProvider

/** Username validator requiring at least 3 characters. */
class UsernameValidator(
    private val stringProvider: StringProvider
) : Validator<String> {
    override fun validate(value: String): ValidationResult {
        return if (value.length >= 3) {
            ValidationResult(true, null)
        } else {
            ValidationResult(
                false,
                stringProvider.get(SharedRes.strings.username_too_short)
            )
        }
    }
}
