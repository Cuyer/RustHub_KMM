package pl.cuyer.rusthub.domain.model

data class User(
    val email: String?,
    val username: String,
    val accessToken: String,
    val refreshToken: String?
)
