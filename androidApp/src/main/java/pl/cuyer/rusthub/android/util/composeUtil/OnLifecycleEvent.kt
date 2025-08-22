package pl.cuyer.rusthub.android.util.composeUtil

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun OnLifecycleEvent(onEvent: (Lifecycle.Event) -> Unit) {
    val activity = LocalActivity.current as? ComponentActivity
    val current = rememberUpdatedState(onEvent)

    if (activity == null) return // preview or not inside an activity

    DisposableEffect(activity) {
        val observer = LifecycleEventObserver { _, event ->
            current.value(event)
        }
        activity.lifecycle.addObserver(observer)
        onDispose { activity.lifecycle.removeObserver(observer) }
    }
}
