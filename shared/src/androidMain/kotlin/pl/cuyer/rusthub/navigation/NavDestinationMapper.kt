package pl.cuyer.rusthub.navigation

import androidx.navigation.NavDestination

fun NavDestination.mapToDestination(): Destination? {
    val packageName = Destination::class.java.`package`?.name ?: return null

    return Destination::class.sealedSubclasses
        .mapNotNull { it.objectInstance }
        .find {
            val constructedRoute = "${packageName}.Destination.${it::class.simpleName}"
            constructedRoute == this.parent?.route
        }
}