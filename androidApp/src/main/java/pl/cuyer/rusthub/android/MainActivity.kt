package pl.cuyer.rusthub.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.core.graphics.toColorInt
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.cuyer.rusthub.android.theme.RustHubTheme
import pl.cuyer.rusthub.domain.model.Theme
import pl.cuyer.rusthub.presentation.features.startup.StartupViewModel
import pl.cuyer.rusthub.presentation.settings.SettingsController
import pl.cuyer.rusthub.presentation.ui.Colors
import pl.cuyer.rusthub.util.InAppUpdateManager
import pl.cuyer.rusthub.util.BiometricAuthenticator
import pl.cuyer.rusthub.presentation.navigation.Onboarding

class MainActivity : ComponentActivity() {
    private val startupViewModel: StartupViewModel by viewModel()
    private val settingsController: SettingsController by inject()
    private val inAppUpdateManager: InAppUpdateManager by inject()
    private val biometricAuthenticator: BiometricAuthenticator by inject()

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

        inAppUpdateManager.check(this)

        setContent {
            val state = startupViewModel.state.collectAsStateWithLifecycle()
            val appTheme = settingsController.theme.collectAsStateWithLifecycle()
            val biometricsEnabled = settingsController.biometricsEnabled.collectAsStateWithLifecycle()

            var allowed by rememberSaveable { mutableStateOf(!biometricsEnabled.value) }
            var startDest by remember { mutableStateOf(state.value.startDestination) }

            val darkTheme = when (appTheme.value) {
                Theme.LIGHT -> false
                Theme.DARK -> true
                Theme.SYSTEM -> isSystemInDarkTheme()
            }

            androidx.compose.runtime.LaunchedEffect(darkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = if (darkTheme) {
                        SystemBarStyle.dark(darkScrim)
                    } else {
                        SystemBarStyle.light(lightScrim, darkScrim)
                    },
                    navigationBarStyle = if (darkTheme) {
                        SystemBarStyle.dark(darkScrim)
                    } else {
                        SystemBarStyle.light(lightScrim, darkScrim)
                    }
                )
            }

            RustHubTheme(theme = appTheme.value) {
                LaunchedEffect(state.value.startDestination, biometricsEnabled.value) {
                    if (biometricsEnabled.value && state.value.startDestination != Onboarding) {
                        val result = biometricAuthenticator.authenticate(this@MainActivity)
                        allowed = result
                        startDest = if (result) state.value.startDestination else Onboarding
                    } else {
                        allowed = true
                        startDest = state.value.startDestination
                    }
                }

                if (!state.value.isLoading && allowed) {
                    NavigationRoot(startDestination = startDest)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        inAppUpdateManager.onResume(this)
    }
}

private val lightScrim = (Colors.SCRIM_LIGHT).toColorInt()
private val darkScrim = (Colors.SCRIM_DARK).toColorInt()

