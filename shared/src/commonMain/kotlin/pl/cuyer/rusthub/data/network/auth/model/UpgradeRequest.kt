package pl.cuyer.rusthub.data.network.auth.model

data class UpgradeRequest(
    val email: String,
    val username: String,
    val password: String
)
