package pl.cuyer.rusthub.domain.exception

open class HttpStatusException(message: String) : RuntimeException(message)

class UnauthorizedException(message: String) : HttpStatusException(message)
class ForbiddenException(message: String) : HttpStatusException(message)
class NotFoundException(message: String) : HttpStatusException(message)
class TooManyRequestsException(message: String) : HttpStatusException(message)
class ServiceUnavailableException(message: String) : HttpStatusException(message)
class BadRequestException(message: String) : HttpStatusException(message)
class InternalServerErrorException(message: String) : HttpStatusException(message)
