package pl.cuyer.rusthub.android

import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.cuyer.rusthub.android.feature.startup.StartupScreen
import pl.cuyer.rusthub.android.theme.RustHubTheme
import pl.cuyer.rusthub.android.util.composeUtil.isSystemInDarkTheme
import pl.cuyer.rusthub.domain.model.Theme
import pl.cuyer.rusthub.presentation.features.startup.StartupViewModel
import pl.cuyer.rusthub.util.InAppUpdateManager

class MainActivity : AppCompatActivity() {
    private val startupViewModel: StartupViewModel by viewModel()
    private val inAppUpdateManager: InAppUpdateManager by inject()

    private lateinit var updateLauncher: ActivityResultLauncher<IntentSenderRequest>

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        var themeSettings by mutableStateOf(
            ThemeSettings(
                darkTheme = false,
                dynamicColor = false
            )
        )

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
                            navigationBarStyle = if (darkTheme) {
                                SystemBarStyle.dark(
                                    scrim = Color.TRANSPARENT
                                )
                            } else {
                                SystemBarStyle.light(
                                    scrim = Color.TRANSPARENT,
                                    darkScrim = Color.TRANSPARENT
                                )
                            },
                            statusBarStyle = if (darkTheme) {
                                SystemBarStyle.dark(
                                    scrim = Color.TRANSPARENT
                                )
                            } else {
                                SystemBarStyle.light(
                                    scrim = Color.TRANSPARENT,
                                    darkScrim = Color.TRANSPARENT
                                )
                            }
                        )
                    }
            }
        }

        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                MobileAds.initialize(this@MainActivity)
            }
        }
        updateLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            inAppUpdateManager.onUpdateResult(result, this)
        }
        inAppUpdateManager.setLauncher(updateLauncher, this)

        inAppUpdateManager.check(this)


        setContent {
            RustHubTheme(
                darkTheme = themeSettings.darkTheme,
                dynamicColor = themeSettings.dynamicColor
            ) {
                val state = startupViewModel.state.collectAsStateWithLifecycle()
                RustHubBackground {
                    if (state.value.isLoading) {
                        StartupScreen(
                            showSkip = { state.value.showSkip },
                            onSkip = { startupViewModel.skipFetching() }
                        )
                    } else {
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

@Immutable
private data class ThemeSettings(
    val darkTheme: Boolean,
    val dynamicColor: Boolean,
)

