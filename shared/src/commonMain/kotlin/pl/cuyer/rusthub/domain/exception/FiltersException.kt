package pl.cuyer.rusthub.domain.exception

open class FiltersException(message: String) : RuntimeException(message)

class FiltersOptionsException(message: String) : FiltersException(message)
