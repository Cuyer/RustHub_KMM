package pl.cuyer.rusthub.domain.exception

open class ConnectivityException(message: String) : RuntimeException(message)

class NetworkUnavailableException(message: String) : ConnectivityException(message)
class TimeoutException(message: String) : ConnectivityException(message)
