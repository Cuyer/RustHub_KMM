package pl.cuyer.rusthub.util

import androidx.appcompat.app.AppCompatDelegate
import pl.cuyer.rusthub.domain.model.Theme

actual fun updateAppTheme(theme: Theme) {
    val mode = when (theme) {
        Theme.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
        Theme.DARK -> AppCompatDelegate.MODE_NIGHT_YES
        Theme.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    }
    AppCompatDelegate.setDefaultNightMode(mode)
}
