package pl.cuyer.rusthub.domain.exception

open class HttpStatusException(message: String) : RuntimeException(message)

class UnauthorizedException(message: String) : HttpStatusException(message)
class ForbiddenException(message: String) : HttpStatusException(message)
class NotFoundException(message: String) : HttpStatusException(message)
