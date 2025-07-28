package pl.cuyer.rusthub.android.navigation

import androidx.navigation3.runtime.NavKey

internal fun navigateBottomBar(backStack: MutableList<NavKey>, item: BottomNavKey) {
    if (!item.isInHierarchy(backStack.lastOrNull())) {
        while (backStack.isNotEmpty() && !item.isInHierarchy(backStack.last())) {
            backStack.removeLastOrNull()
        }
        backStack.add(item.root)
    } else if (backStack.lastOrNull() != item.root) {
        while (backStack.isNotEmpty() && backStack.last() != item.root) {
            backStack.removeLastOrNull()
        }
    }
}

