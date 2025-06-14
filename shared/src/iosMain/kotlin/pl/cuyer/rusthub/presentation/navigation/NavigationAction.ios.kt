package pl.cuyer.rusthub.presentation.navigation

data class IosNavOptions(
    val launchSingleTop: Boolean,
    val popUpTo: Destination?,
    val popUpToInclusive: Boolean
)

actual typealias NavOptions = IosNavOptions

actual class NavOptionsBuilder actual constructor() {
    actual var launchSingleTop: Boolean = false
    private var popUpToDestination: Destination? = null
    private var popUpToInclusive: Boolean = false

    actual fun popUpTo(destination: Destination?, inclusive: Boolean) {
        popUpToDestination = destination
        popUpToInclusive = inclusive
    }

    actual fun build(): NavOptions = IosNavOptions(
        launchSingleTop = launchSingleTop,
        popUpTo = popUpToDestination,
        popUpToInclusive = popUpToInclusive
    )
}