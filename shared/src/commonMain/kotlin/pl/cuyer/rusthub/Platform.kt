package pl.cuyer.rusthub

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform