package pl.cuyer.rusthub.domain.exception

open class ServersException(message: String) : RuntimeException(message)

class ServersQueryException(message: String) : ServersException(message)
class FavoriteLimitException(message: String) : ServersException(message)
class SubscriptionLimitException(message: String) : ServersException(message)
