package pl.cuyer.rusthub.presentation.navigation

interface NavigationOptions {
    val popUpTo: Destination?
    val inclusive: Boolean
}

data class DefaultNavigationOptions(
    override val popUpTo: Destination? = null,
    override val inclusive: Boolean = false
) : NavigationOptions