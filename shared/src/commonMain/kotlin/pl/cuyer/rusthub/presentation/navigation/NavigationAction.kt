package pl.cuyer.rusthub.presentation.navigation

sealed interface NavigationAction {

    data class Navigate(
        val destination: Destination,

        val navOptions: NavOptionsBuilder.() -> Unit = {}
    ) : NavigationAction

    data object NavigateUp : NavigationAction
}


expect class NavOptions


expect class NavOptionsBuilder() {
    var launchSingleTop: Boolean
    fun popUpTo(destination: Destination?, inclusive: Boolean = false)
    fun build(): NavOptions
}