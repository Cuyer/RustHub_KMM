package pl.cuyer.rusthub.domain.model

import kotlinx.serialization.Serializable
import androidx.compose.runtime.Immutable

@Serializable
@Immutable
enum class AuthProvider {
    LOCAL,
    GOOGLE,
    ANONYMOUS
}
