package pl.cuyer.rusthub.data.network

import io.github.aakira.napier.Napier
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.withContext
import pl.cuyer.rusthub.domain.repository.auth.AuthDataSource
import pl.cuyer.rusthub.common.user.UserEvent
import pl.cuyer.rusthub.common.user.UserEventController
import kotlin.coroutines.coroutineContext

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
                withContext(Dispatchers.Main.immediate) {
                    userEventController.sendEvent(UserEvent.LoggedOut)
                }
            } catch (e: Exception) {
                Napier.e(message = "Failed to delete user on token refresh failure", throwable = e)
            }
        }
    }
}
