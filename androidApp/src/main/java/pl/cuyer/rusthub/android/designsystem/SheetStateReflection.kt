package pl.cuyer.rusthub.android.designsystem

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import kotlin.coroutines.Continuation
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@OptIn(ExperimentalMaterial3Api::class)
internal fun SheetState.dispatchRawDeltaCompat(delta: Float): Float {
    val anchoredField = SheetState::class.java.getDeclaredField("anchoredDraggableState")
    anchoredField.isAccessible = true
    val anchored = anchoredField.get(this)
    val method = anchored.javaClass.getDeclaredMethod("dispatchRawDelta", Float::class.java)
    method.isAccessible = true
    return method.invoke(anchored, delta) as Float
}

@OptIn(ExperimentalMaterial3Api::class)
internal fun SheetState.minAnchorCompat(): Float {
    val anchoredField = SheetState::class.java.getDeclaredField("anchoredDraggableState")
    anchoredField.isAccessible = true
    val anchored = anchoredField.get(this)
    val getter = anchored.javaClass.getDeclaredMethod("getAnchors")
    getter.isAccessible = true
    val anchors = getter.invoke(anchored)
    val minMethod = anchors.javaClass.getDeclaredMethod("minAnchor")
    minMethod.isAccessible = true
    return minMethod.invoke(anchors) as Float
}

@OptIn(ExperimentalMaterial3Api::class)
internal suspend fun SheetState.settleCompat(velocity: Float) {
    val anchoredField = SheetState::class.java.getDeclaredField("anchoredDraggableState")
    anchoredField.isAccessible = true
    val anchored = anchoredField.get(this)
    val method = anchored.javaClass.getDeclaredMethod(
        "settle",
        Float::class.java,
        Continuation::class.java
    )
    method.isAccessible = true
    return suspendCoroutine { cont ->
        try {
            method.invoke(anchored, velocity, cont)
        } catch (e: Throwable) {
            cont.resumeWithException(e.cause ?: e)
        }
    }
}