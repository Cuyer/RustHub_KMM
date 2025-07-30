package pl.cuyer.rusthub.domain.usecase.ads

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.domain.model.ads.NativeAdWrapper
import pl.cuyer.rusthub.domain.repository.ads.NativeAdRepository

class GetNativeAdUseCase(private val repository: NativeAdRepository) {
    operator fun invoke(adId: String): Flow<NativeAdWrapper?> {
        return repository.get(adId)
    }
}

