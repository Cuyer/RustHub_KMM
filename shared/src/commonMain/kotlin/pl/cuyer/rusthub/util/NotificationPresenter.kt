package pl.cuyer.rusthub.util

import pl.cuyer.rusthub.domain.model.NotificationType

expect class NotificationPresenter {
    fun show(id: String, type: NotificationType)
}
