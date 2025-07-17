package pl.cuyer.rusthub.android

import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.cuyer.rusthub.android.theme.RustHubTheme
import pl.cuyer.rusthub.domain.repository.settings.SettingsDataSource
import pl.cuyer.rusthub.presentation.features.startup.StartupViewModel
import pl.cuyer.rusthub.presentation.ui.Colors
import pl.cuyer.rusthub.util.InAppUpdateManager

class MainActivity : AppCompatActivity() {
    private val startupViewModel: StartupViewModel by viewModel()
    private val inAppUpdateManager: InAppUpdateManager by inject()

    private val settingsDataSource: SettingsDataSource by inject()

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
            RustHubTheme {
                if (!state.value.isLoading) {
                    NavigationRoot(startDestination = state.value.startDestination)
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

