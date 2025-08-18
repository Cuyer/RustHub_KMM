package pl.cuyer.rusthub.android.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Event
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.presentation.navigation.ItemDetails
import pl.cuyer.rusthub.presentation.navigation.ItemList
import pl.cuyer.rusthub.presentation.navigation.MonumentDetails
import pl.cuyer.rusthub.presentation.navigation.MonumentList
import pl.cuyer.rusthub.presentation.navigation.ServerDetails
import pl.cuyer.rusthub.presentation.navigation.ServerList
import pl.cuyer.rusthub.presentation.navigation.Settings as SettingsNav
import pl.cuyer.rusthub.presentation.navigation.RaidScheduler
import dev.icerock.moko.resources.StringResource
import androidx.navigation3.runtime.NavKey

@Immutable
internal sealed interface BottomNavKey {
    val root: NavKey
    val icon: ImageVector
    val label: StringResource
    val isInHierarchy: (NavKey?) -> Boolean

    @Immutable
    data object Servers : BottomNavKey {
        override val root = ServerList
        override val icon = Icons.AutoMirrored.Filled.List
        override val label = SharedRes.strings.servers
        override val isInHierarchy: (NavKey?) -> Boolean = { it is ServerList || it is ServerDetails }
    }

    @Immutable
    data object Items : BottomNavKey {
        override val root = ItemList
        override val icon = Icons.Filled.Inventory
        override val label = SharedRes.strings.items
        override val isInHierarchy: (NavKey?) -> Boolean = { it is ItemList || it is ItemDetails }
    }

    @Immutable
    data object Monuments : BottomNavKey {
        override val root = MonumentList
        override val icon = Icons.Filled.AccountBalance
        override val label = SharedRes.strings.monuments
        override val isInHierarchy: (NavKey?) -> Boolean = {
            it is MonumentList || it is MonumentDetails
        }
    }

    @Immutable
    data object Raids : BottomNavKey {
        override val root = RaidScheduler
        override val icon = Icons.Filled.Event
        override val label = SharedRes.strings.raids
        override val isInHierarchy: (NavKey?) -> Boolean = { it is RaidScheduler }
    }

    @Immutable
    data object Settings : BottomNavKey {
        override val root = SettingsNav
        override val icon = Icons.Filled.Settings
        override val label = SharedRes.strings.settings
        override val isInHierarchy: (NavKey?) -> Boolean = { it is SettingsNav }
    }
}

internal val bottomNavItems: List<BottomNavKey> = listOf(
    BottomNavKey.Servers,
    BottomNavKey.Items,
    BottomNavKey.Monuments,
    BottomNavKey.Raids,
    BottomNavKey.Settings
)
