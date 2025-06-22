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
    override fun onCreate(savedInstanceState: Bundle?) {
        var keepSplash = true
        val startDestination = mutableStateOf<NavKey>(Onboarding)
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
            val user = getKoin().get<GetUserUseCase>().invoke().firstOrNull()
            startDestination.value = if (user != null) ServerList else Onboarding
            keepSplash = false
        }

        setContent {
            KoinContext(
                content = {
                    RustHubTheme {
                        NavigationRoot(startDestination = startDestination.value)
                    }
                }
            )
        }
    }
}

private val lightScrim = (Colors.SCRIM_LIGHT).toColorInt()
private val darkScrim = (Colors.SCRIM_DARK).toColorInt()

