package pl.cuyer.rusthub.navigation
import androidx.navigation.NavOptions as AndroidNavOptions

// Map our common NavOptions to Android's NavOptions.
actual typealias NavOptions = AndroidNavOptions

actual class NavOptionsBuilder actual constructor() {
    actual var launchSingleTop: Boolean = false
    private var popUpToDestination: Destination? = null
    private var popUpToInclusive: Boolean = false

    actual fun popUpTo(destination: Destination?, inclusive: Boolean) {
        popUpToDestination = destination
        popUpToInclusive = inclusive
    }

    actual fun build(): NavOptions {
        val builder = AndroidNavOptions.Builder().setLaunchSingleTop(launchSingleTop)
        popUpToDestination?.let { builder.setPopUpTo(it, popUpToInclusive) }
        return builder.build()
    }
}