package pl.cuyer.rusthub.presentation.di

import pl.cuyer.rusthub.presentation.navigation.DefaultNavigator
import pl.cuyer.rusthub.presentation.navigation.Destination
import pl.cuyer.rusthub.presentation.navigation.Navigator

interface AppModule {
    val navigator: Navigator
}

class AppModuleImpl() : AppModule {

    override val navigator: Navigator by lazy {
        DefaultNavigator(
            startDestination = Destination.HomeGraph
        )
    }
}