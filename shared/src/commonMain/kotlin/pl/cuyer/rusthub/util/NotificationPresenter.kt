package pl.cuyer.rusthub.util

import pl.cuyer.rusthub.domain.model.NotificationType

expect class NotificationPresenter {
    fun show(name: String, type: NotificationType, timestamp: String)
}
