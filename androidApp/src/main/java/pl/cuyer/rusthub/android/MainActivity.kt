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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.cuyer.rusthub.android.theme.RustHubTheme
import pl.cuyer.rusthub.android.util.composeUtil.isSystemInDarkTheme
import pl.cuyer.rusthub.domain.model.Theme
import pl.cuyer.rusthub.presentation.features.startup.StartupViewModel
import pl.cuyer.rusthub.presentation.ui.Colors
import pl.cuyer.rusthub.util.InAppUpdateManager
import pl.cuyer.rusthub.android.feature.startup.StartupScreen

class MainActivity : AppCompatActivity() {
    private val startupViewModel: StartupViewModel by viewModel()
    private val inAppUpdateManager: InAppUpdateManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        var themeSettings by mutableStateOf(
            ThemeSettings(
                darkTheme = false,
                dynamicColor = false
            )
        )

        inAppUpdateManager.check(this)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                combine(
                    isSystemInDarkTheme(),
                    startupViewModel.state
                ) { systemDark, uiState ->
                    ThemeSettings(
                        darkTheme = when (uiState.theme) {
                            Theme.SYSTEM -> systemDark
                            Theme.LIGHT -> false
                            Theme.DARK -> true
                        },
                        dynamicColor = uiState.dynamicColors
                    )
                }.onEach { themeSettings = it }
                    .map { it.darkTheme }
                    .distinctUntilChanged()
                    .collect { darkTheme ->
                        enableEdgeToEdge(
                            statusBarStyle = SystemBarStyle.auto(
                                lightScrim = android.graphics.Color.TRANSPARENT,
                                darkScrim = android.graphics.Color.TRANSPARENT,
                            ) { darkTheme },
                            navigationBarStyle = SystemBarStyle.auto(
                                lightScrim = lightScrim,
                                darkScrim = darkScrim,
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
                    if (state.value.isLoading) {
                        StartupScreen(
                            showSkip = state.value.showSkip,
                            onSkip = { startupViewModel.skipFetching() }
                        )
                    } else {
                        NavigationRoot()
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

