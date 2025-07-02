package pl.cuyer.rusthub.data.network.auth.model.mapper

import pl.cuyer.rusthub.data.network.auth.model.AccessTokenDto
import pl.cuyer.rusthub.data.network.auth.model.TokenPairDto
import pl.cuyer.rusthub.data.network.auth.model.UserExistsResponseDto
import pl.cuyer.rusthub.domain.model.AccessToken
import pl.cuyer.rusthub.domain.model.TokenPair
import pl.cuyer.rusthub.domain.model.AuthProvider
import pl.cuyer.rusthub.domain.model.UserExistsInfo

fun AccessTokenDto.toDomain(): AccessToken {
    return AccessToken(
        accessToken = accessToken,
        username = username,
        provider = AuthProvider.valueOf(provider)
    )
}

fun TokenPairDto.toDomain(): TokenPair {
    return TokenPair(
        accessToken = accessToken,
        refreshToken = refreshToken,
        username = username,
        email = email,
        provider = AuthProvider.valueOf(provider)
    )
}

fun UserExistsResponseDto.toDomain(): UserExistsInfo {
    return UserExistsInfo(
        exists = exists,
        provider = provider
    )
}