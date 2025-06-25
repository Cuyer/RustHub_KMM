package pl.cuyer.rusthub.util

import com.google.firebase.messaging.FirebaseMessaging

actual class TopicSubscriber {
    actual fun subscribe(topic: String) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
    }

    actual fun unsubscribe(topic: String) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
    }
}
