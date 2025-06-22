package pl.cuyer.rusthub.data.network.auth.model.mapper

import pl.cuyer.rusthub.data.network.auth.model.AccessTokenDto
import pl.cuyer.rusthub.data.network.auth.model.TokenPairDto
import pl.cuyer.rusthub.domain.model.AccessToken
import pl.cuyer.rusthub.domain.model.TokenPair

fun AccessTokenDto.toDomain(): AccessToken {
    return AccessToken(
        accessToken = accessToken,
        username = username
    )
}

fun TokenPairDto.toDomain(): TokenPair {
    return TokenPair(
        accessToken = accessToken,
        refreshToken = refreshToken,
        username = username,
        email = email
    )
}