package pl.cuyer.rusthub.android

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.graphics.toColorInt
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.cuyer.rusthub.android.theme.RustHubTheme
import pl.cuyer.rusthub.domain.model.Theme
import pl.cuyer.rusthub.presentation.features.startup.StartupViewModel
import pl.cuyer.rusthub.presentation.ui.Colors
import pl.cuyer.rusthub.util.InAppUpdateManager

class MainActivity : AppCompatActivity() {
    private val startupViewModel: StartupViewModel by viewModel()
    private val inAppUpdateManager: InAppUpdateManager by inject()

    private var themeSettings by mutableStateOf(ThemeSettings(false, false))


    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition {
            startupViewModel.state.value.isLoading
        }

        val systemDark =
            (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
                Configuration.UI_MODE_NIGHT_YES
        themeSettings = ThemeSettings(systemDark, false)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                lightScrim,
                darkScrim
            ) { themeSettings.darkTheme },
            navigationBarStyle = SystemBarStyle.auto(
                lightScrim,
                darkScrim
            ) { themeSettings.darkTheme }
        )


        super.onCreate(savedInstanceState)

        inAppUpdateManager.check(this)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                startupViewModel.state.collect { state ->
                    val systemDark =
                        (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
                            Configuration.UI_MODE_NIGHT_YES
                    val darkTheme = when (state.theme) {
                        Theme.DARK -> true
                        Theme.LIGHT -> false
                        else -> systemDark
                    }
                    themeSettings = ThemeSettings(darkTheme, state.dynamicColors)
                    enableEdgeToEdge(
                        statusBarStyle = SystemBarStyle.auto(
                            lightScrim,
                            darkScrim
                        ) { darkTheme },
                        navigationBarStyle = SystemBarStyle.auto(
                            lightScrim,
                            darkScrim
                        ) { darkTheme }
                    )
                }
            }
        }

        setContent {
            val state = startupViewModel.state.collectAsStateWithLifecycle()
            RustHubTheme(
                darkTheme = themeSettings.darkTheme,
                dynamicColor = themeSettings.dynamicColor
            ) {
                RustHubBackground {
                    if (!state.value.isLoading) {
                        NavigationRoot(startDestination = state.value.startDestination)
                    }
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

private data class ThemeSettings(
    val darkTheme: Boolean,
    val dynamicColor: Boolean,
)

