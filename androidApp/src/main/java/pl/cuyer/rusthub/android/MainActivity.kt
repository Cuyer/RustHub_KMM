package pl.cuyer.rusthub.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.graphics.toColorInt
import org.koin.compose.KoinContext
import pl.cuyer.rusthub.android.theme.RustHubTheme
import pl.cuyer.rusthub.presentation.ui.Colors

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            navigationBarStyle = SystemBarStyle.auto(
                lightScrim,
                darkScrim
            )
        )
        super.onCreate(savedInstanceState)
        setContent {
            KoinContext(
                content = {
                    RustHubTheme {
                        NavigationRoot()
                    }
                }
            )

        }
    }
}

private val lightScrim = (Colors.SCRIM_LIGHT).toColorInt()
private val darkScrim = (Colors.SCRIM_DARK).toColorInt()

