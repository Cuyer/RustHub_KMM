package pl.cuyer.rusthub.util

expect class TopicSubscriber {
    fun subscribe(topic: String)
    fun unsubscribe(topic: String)
}
