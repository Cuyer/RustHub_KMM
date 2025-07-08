package pl.cuyer.rusthub.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.graphics.toColorInt
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.compose.KoinContext
import pl.cuyer.rusthub.android.theme.RustHubTheme
import pl.cuyer.rusthub.presentation.features.startup.StartupViewModel
import pl.cuyer.rusthub.presentation.settings.SettingsController
import pl.cuyer.rusthub.presentation.ui.Colors

class MainActivity : ComponentActivity() {
    private val startupViewModel: StartupViewModel by viewModel()
    private val settingsController: SettingsController by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition {
            startupViewModel.state.value.isLoading
        }

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                lightScrim,
                darkScrim
            ),
            navigationBarStyle = SystemBarStyle.auto(
                lightScrim,
                darkScrim
            )
        )

        super.onCreate(savedInstanceState)

        setContent {
            val state = startupViewModel.state.collectAsStateWithLifecycle()
            val appTheme = settingsController.theme.collectAsStateWithLifecycle()

            val darkTheme = when (appTheme.value) {
                pl.cuyer.rusthub.domain.model.Theme.LIGHT -> false
                pl.cuyer.rusthub.domain.model.Theme.DARK -> true
                pl.cuyer.rusthub.domain.model.Theme.SYSTEM -> androidx.compose.foundation.isSystemInDarkTheme()
            }

            androidx.compose.runtime.LaunchedEffect(darkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = if (darkTheme) {
                        SystemBarStyle.dark(darkScrim)
                    } else {
                        SystemBarStyle.light(lightScrim)
                    },
                    navigationBarStyle = if (darkTheme) {
                        SystemBarStyle.dark(darkScrim)
                    } else {
                        SystemBarStyle.light(lightScrim)
                    }
                )
            }

            KoinContext {
                RustHubTheme(theme = appTheme.value) {
                    if (!state.value.isLoading) {
                        NavigationRoot(startDestination = state.value.startDestination)
                    }
                }
            }
        }
    }
}

private val lightScrim = (Colors.SCRIM_LIGHT).toColorInt()
private val darkScrim = (Colors.SCRIM_DARK).toColorInt()

