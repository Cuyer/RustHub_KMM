package pl.cuyer.rusthub.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.graphics.toColorInt
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.compose.KoinContext
import pl.cuyer.rusthub.android.theme.RustHubTheme
import pl.cuyer.rusthub.presentation.features.startup.StartupViewModel
import pl.cuyer.rusthub.presentation.ui.Colors

class MainActivity : ComponentActivity() {
    private val startupViewModel: StartupViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition {
            startupViewModel.state.value.isLoading
        }

        enableEdgeToEdge(
            navigationBarStyle = SystemBarStyle.auto(
                lightScrim,
                darkScrim
            )
        )

        super.onCreate(savedInstanceState)

        setContent {
            val state = startupViewModel.state.collectAsStateWithLifecycle()
            KoinContext {
                RustHubTheme {
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

