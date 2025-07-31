package pl.cuyer.rusthub.data.network

import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import pl.cuyer.rusthub.common.user.UserEvent
import pl.cuyer.rusthub.common.user.UserEventController
import pl.cuyer.rusthub.domain.repository.auth.AuthDataSource
import pl.cuyer.rusthub.util.CrashReporter

class ForbiddenResponsePluginConfig {
    lateinit var authDataSource: AuthDataSource
    lateinit var userEventController: UserEventController
}

val ForbiddenResponsePlugin = createClientPlugin("ForbiddenResponsePlugin", ::ForbiddenResponsePluginConfig) {
    val dataSource = pluginConfig.authDataSource
    val userEventController = pluginConfig.userEventController
    val deleteMutex = Mutex()
    onResponse { response ->
        if (response.status == HttpStatusCode.Forbidden) {
            try {
                deleteMutex.withLock {
                    if (dataSource.getUserOnce() != null) {
                        dataSource.deleteUser()
                        withContext(Dispatchers.Main.immediate) {
                            userEventController.sendEvent(UserEvent.LoggedOut)
                        }
                    }
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                CrashReporter.recordException(e)
            }
        }
    }
}
