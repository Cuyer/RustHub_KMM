package pl.cuyer.rusthub.data.network

import io.github.aakira.napier.Napier
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.http.HttpStatusCode
import pl.cuyer.rusthub.domain.repository.auth.AuthDataSource
import pl.cuyer.rusthub.presentation.user.UserEvent
import pl.cuyer.rusthub.presentation.user.UserEventController

class ForbiddenResponsePluginConfig {
    lateinit var authDataSource: AuthDataSource
    lateinit var userEventController: UserEventController
}

val ForbiddenResponsePlugin = createClientPlugin("ForbiddenResponsePlugin", ::ForbiddenResponsePluginConfig) {
    val dataSource = pluginConfig.authDataSource
    val userEventController = pluginConfig.userEventController
    onResponse { response ->
        if (response.status == HttpStatusCode.Forbidden) {
            try {
                dataSource.deleteUser()
                userEventController.sendEvent(UserEvent.LoggedOut)
            } catch (e: Exception) {
                Napier.e(message = "Failed to delete user on token refresh failure", throwable = e)
            }
        }
    }
}
