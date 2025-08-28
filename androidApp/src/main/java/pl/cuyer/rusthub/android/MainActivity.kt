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
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.getViewModel
import pl.cuyer.rusthub.android.theme.RustHubTheme
import pl.cuyer.rusthub.android.util.composeUtil.isSystemInDarkTheme
import pl.cuyer.rusthub.domain.model.Theme
import pl.cuyer.rusthub.presentation.di.RustHubApplication
import pl.cuyer.rusthub.presentation.features.startup.StartupViewModel
import pl.cuyer.rusthub.util.InAppUpdateManager

class MainActivity : AppCompatActivity() {
    private lateinit var startupViewModel: StartupViewModel
    private lateinit var inAppUpdateManager: InAppUpdateManager

    private lateinit var updateLauncher: ActivityResultLauncher<IntentSenderRequest>

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        val app = application as RustHubApplication

        splashScreen.setKeepOnScreenCondition {
            !app.koinReady.isCompleted ||
                    (this::startupViewModel.isInitialized && startupViewModel.state.value.isLoading)
        }

        updateLauncher =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
                if (this::inAppUpdateManager.isInitialized) {
                    inAppUpdateManager.onUpdateResult(result, this)
                }
            }

        lifecycleScope.launch {
            app.koinReady.await()
            startupViewModel = getViewModel()
            inAppUpdateManager = get()

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

            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    MobileAds.initialize(this@MainActivity)
                }
            }

            inAppUpdateManager.setLauncher(updateLauncher, this@MainActivity)
            inAppUpdateManager.check(this@MainActivity)

            if (!lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)) {
                return@launch
            }

            setContent {
                RustHubTheme(
                    darkTheme = themeSettings.darkTheme,
                    dynamicColor = themeSettings.dynamicColor
                ) {
                    val state = startupViewModel.state.collectAsStateWithLifecycle()
                    if (!state.value.isLoading) {
                        RustHubBackground {
                            NavigationRoot(startDestination = state.value.startDestination)
                        }
                    }
                }
            }

            splashScreen.setKeepOnScreenCondition {
                startupViewModel.state.value.isLoading
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (this::inAppUpdateManager.isInitialized) {
            inAppUpdateManager.onResume(this)
        }
    }
}

@Immutable
private data class ThemeSettings(
    val darkTheme: Boolean,
    val dynamicColor: Boolean,
)
