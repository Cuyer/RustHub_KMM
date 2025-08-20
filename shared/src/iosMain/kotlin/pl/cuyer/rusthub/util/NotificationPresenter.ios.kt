package pl.cuyer.rusthub.util

import pl.cuyer.rusthub.domain.model.NotificationType

actual class NotificationPresenter {
    actual fun show(name: String, type: NotificationType, timestamp: String) { /* no-op */ }
}
