package pl.cuyer.rusthub.di

import pl.cuyer.rusthub.navigation.DefaultNavigator
import pl.cuyer.rusthub.navigation.Destination
import pl.cuyer.rusthub.navigation.Navigator

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