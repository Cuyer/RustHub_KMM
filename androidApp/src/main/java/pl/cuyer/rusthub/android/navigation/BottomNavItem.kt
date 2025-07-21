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
import dev.icerock.moko.resources.StringResource
import androidx.navigation3.runtime.NavKey

internal data class BottomNavItem(
    val root: NavKey,
    val icon: ImageVector,
    val label: StringResource,
    val isInHierarchy: (NavKey?) -> Boolean
)

internal val bottomNavItems = listOf(
    BottomNavItem(
        root = ServerList,
        icon = Icons.AutoMirrored.Filled.List,
        label = SharedRes.strings.servers,
        isInHierarchy = { it is ServerList || it is ServerDetails }
    ),
    BottomNavItem(
        root = ItemList,
        icon = Icons.Filled.Inventory,
        label = SharedRes.strings.items,
        isInHierarchy = { it is ItemList || it is ItemDetails }
    ),
    BottomNavItem(
        root = Settings,
        icon = Icons.Filled.Settings,
        label = SharedRes.strings.settings,
        isInHierarchy = { it is Settings }
    )
)
