package pl.cuyer.rusthub.android.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.presentation.navigation.ItemDetails
import pl.cuyer.rusthub.presentation.navigation.ItemList
import pl.cuyer.rusthub.presentation.navigation.ServerDetails
import pl.cuyer.rusthub.presentation.navigation.ServerList
import pl.cuyer.rusthub.presentation.navigation.Settings
import androidx.navigation3.runtime.NavKey

sealed class BottomNavItem(
    val root: NavKey,
    val icon: ImageVector,
    val label: Int
) {
    object Servers : BottomNavItem(
        root = ServerList,
        icon = Icons.AutoMirrored.Filled.List,
        label = SharedRes.strings.servers
    ) {
        override fun isFor(key: NavKey) = key is ServerList || key is ServerDetails
    }

    object Items : BottomNavItem(
        root = ItemList,
        icon = Icons.Filled.Inventory,
        label = SharedRes.strings.items
    ) {
        override fun isFor(key: NavKey) = key is ItemList || key is ItemDetails
    }

    object AppSettings : BottomNavItem(
        root = Settings,
        icon = Icons.Filled.Settings,
        label = SharedRes.strings.settings
    ) {
        override fun isFor(key: NavKey) = key is Settings
    }

    abstract fun isFor(key: NavKey): Boolean
}
