package pl.cuyer.rusthub.android.util

import android.os.Build
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
fun prefersReducedMotion(): Boolean {
    val context = LocalContext.current
    val accessibilityManager = remember {
        context.getSystemService(AccessibilityManager::class.java)
    }
    val animatorScale = remember {
        Settings.Global.getFloat(
            context.contentResolver,
            Settings.Global.ANIMATOR_DURATION_SCALE,
            1f
        )
    }
    val a11yReduce = if (Build.VERSION.SDK_INT >= 34) {
        accessibilityManager?.isReduceMotionEnabled == true
    } else {
        false
    }
    return animatorScale == 0f || a11yReduce
}

