package pl.cuyer.rusthub.presentation.features.monument

import androidx.compose.runtime.Immutable
import pl.cuyer.rusthub.domain.model.Monument

@Immutable
data class MonumentDetailsState(
    val monument: Monument? = null,
    val isLoading: Boolean = true,
    val slug: String? = null,
)
