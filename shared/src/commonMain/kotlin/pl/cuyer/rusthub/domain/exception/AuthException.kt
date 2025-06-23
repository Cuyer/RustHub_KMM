package pl.cuyer.rusthub.domain.exception

open class AuthException(message: String) : RuntimeException(message)

class UserAlreadyExistsException(message: String) : AuthException(message)
class InvalidCredentialsException(message: String) : AuthException(message)
class InvalidRefreshTokenException(message: String) : AuthException(message)
class AnonymousUpgradeException(message: String) : AuthException(message)
