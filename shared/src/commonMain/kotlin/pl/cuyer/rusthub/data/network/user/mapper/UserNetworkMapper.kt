package pl.cuyer.rusthub.data.network.user.mapper

import pl.cuyer.rusthub.data.network.user.model.UserInfoDto
import pl.cuyer.rusthub.domain.model.AuthProvider
import pl.cuyer.rusthub.domain.model.User

fun UserInfoDto.toDomain(): User {
    return User(
        email = email,
        username = username,
        accessToken = "",
        refreshToken = null,
        provider = AuthProvider.valueOf(provider),
        subscribed = subscribed,
        emailConfirmed = emailConfirmed
    )
}
