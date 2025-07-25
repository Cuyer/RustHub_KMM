package pl.cuyer.rusthub.domain.model

import kotlinx.serialization.Serializable
import androidx.compose.runtime.Immutable

@Serializable
@Immutable
data class UserExistsInfo(
    val exists: Boolean,
    val provider: AuthProvider? = null
)
