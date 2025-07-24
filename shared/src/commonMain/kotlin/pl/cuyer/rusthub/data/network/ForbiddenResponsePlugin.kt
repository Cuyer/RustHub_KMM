package pl.cuyer.rusthub.data.network

import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.http.HttpStatusCode
import pl.cuyer.rusthub.domain.repository.auth.AuthDataSource

class ForbiddenResponsePluginConfig {
    lateinit var authDataSource: AuthDataSource
}

val ForbiddenResponsePlugin = createClientPlugin("ForbiddenResponsePlugin", ::ForbiddenResponsePluginConfig) {
    val dataSource = pluginConfig.authDataSource
    onResponse { response ->
        if (response.status == HttpStatusCode.Forbidden) {
            dataSource.deleteUser()
        }
    }
}
