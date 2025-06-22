package pl.cuyer.rusthub.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.graphics.toColorInt
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.runtime.mutableStateOf
import androidx.navigation3.runtime.NavKey
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.compose.KoinContext
import pl.cuyer.rusthub.android.theme.RustHubTheme
import pl.cuyer.rusthub.presentation.features.startup.StartupViewModel
import pl.cuyer.rusthub.presentation.navigation.Onboarding
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.koin.android.ext.android.getKoin
import org.koin.compose.KoinContext
import pl.cuyer.rusthub.android.theme.RustHubTheme
import pl.cuyer.rusthub.domain.usecase.GetUserUseCase
import pl.cuyer.rusthub.presentation.navigation.Onboarding
import pl.cuyer.rusthub.presentation.navigation.ServerList
import pl.cuyer.rusthub.presentation.ui.Colors

class MainActivity : ComponentActivity() {
    private val startupViewModel: StartupViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        var keepSplash = true
        val startDestination = mutableStateOf<NavKey>(Onboarding)
        val isLoading = mutableStateOf(true)
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { keepSplash }

        enableEdgeToEdge(
            navigationBarStyle = SystemBarStyle.auto(
                lightScrim,
                darkScrim
            )
        )

        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            startupViewModel.state.collect { state ->
                startDestination.value = state.startDestination
                keepSplash = state.isLoading
                isLoading.value = state.isLoading
            }
            val user = getKoin().get<GetUserUseCase>().invoke().firstOrNull()
            startDestination.value = if (user != null) ServerList else Onboarding
            keepSplash = false
        }

        setContent {
            KoinContext(
                content = {
                    RustHubTheme {
                        if (!isLoading.value) {
                            NavigationRoot(startDestination = startDestination.value)
                        }
                    }
                }
            )
        }
    }
}

private val lightScrim = (Colors.SCRIM_LIGHT).toColorInt()
private val darkScrim = (Colors.SCRIM_DARK).toColorInt()

