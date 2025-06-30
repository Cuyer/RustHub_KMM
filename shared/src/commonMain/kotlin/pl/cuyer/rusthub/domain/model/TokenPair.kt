package pl.cuyer.rusthub.domain.model

data class TokenPair(
    val accessToken: String,
    val refreshToken: String,
    val username: String,
    val email: String,
    val provider: AuthProvider
)
