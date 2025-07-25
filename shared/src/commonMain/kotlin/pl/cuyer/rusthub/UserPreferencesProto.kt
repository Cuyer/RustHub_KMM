package pl.cuyer.rusthub

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ThemeProto {
    @SerialName("THEME_SYSTEM")
    THEME_SYSTEM,
    @SerialName("THEME_LIGHT")
    THEME_LIGHT,
    @SerialName("THEME_DARK")
    THEME_DARK
}

@Serializable
data class UserPreferencesProto(
    @SerialName("theme_config")
    val themeConfig: ThemeProto = ThemeProto.THEME_SYSTEM,
    @SerialName("use_dynamic_color")
    val useDynamicColor: Boolean = false,
    @SerialName("use_system_colors")
    val useSystemColors: Boolean = true,
)
