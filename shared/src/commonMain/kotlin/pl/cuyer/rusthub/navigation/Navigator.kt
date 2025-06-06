package pl.cuyer.rusthub.navigation

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

interface Navigator {
    val startDestination: Destination
    val navigationActions: Flow<NavigationAction>

    suspend fun navigate(
        destination: Destination,
        options: NavigationOptions? = null
    )

    suspend fun navigateUp()
}

class DefaultNavigator(
    override val startDestination: Destination
) : Navigator {

    private val _navigationActions = Channel<NavigationAction>(UNLIMITED)

    override val navigationActions = _navigationActions.receiveAsFlow()

    override suspend fun navigate(
        destination: Destination,
        options: NavigationOptions?
    ) {
        _navigationActions.send(
            NavigationAction.Navigate(
                destination = destination,
                navOptions = {
                    launchSingleTop = true
                    options?.popUpTo?.let { destination ->
                        popUpTo(destination, options.inclusive)
                    }
                }
            )
        )
    }

    override suspend fun navigateUp() {
        _navigationActions.send(NavigationAction.NavigateUp)
    }
}