package pl.cuyer.rusthub.android.navigation

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.Scene
import androidx.navigation3.ui.SceneStrategy

// --- TwoPaneSceneStrategy ---
/**
 * A [SceneStrategy] that activates a [TwoPaneScene] if the window is wide enough
 * and the top two back stack entries declare support for two-pane display.
 */
@Deprecated("Don't use")
class TwoPaneSceneStrategy<T : Any> : SceneStrategy<T> {
    @OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3WindowSizeClassApi::class)
    @Composable
    override fun calculateScene(
        entries: List<NavEntry<T>>,
        onBack: (Int) -> Unit
    ): Scene<T>? {

        /*        val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

                // Condition 1: Only return a Scene if the window is sufficiently wide to render two panes.
                // We use isWidthAtLeastBreakpoint with WIDTH_DP_MEDIUM_LOWER_BOUND (600dp).
                if (!windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND)) {
                    return null
                }

                val lastTwoEntries = entries.takeLast(2)

                // Condition 2: Only return a Scene if there are two entries, and both have declared
                // they can be displayed in a two pane scene.
                return if (lastTwoEntries.size == 2 &&
                    lastTwoEntries.all { it.metadata.containsKey(TwoPaneScene.TWO_PANE_KEY) }
                ) {
                    val firstEntry = lastTwoEntries.first()
                    val secondEntry = lastTwoEntries.last()

                    // The scene key must uniquely represent the state of the scene.
                    val sceneKey = Pair(firstEntry.key, secondEntry.key)

                    TwoPaneScene(
                        key = sceneKey,
                        // Where we go back to is a UX decision. In this case, we only remove the top
                        // entry from the back stack, despite displaying two entries in this scene.
                        // This is because in this app we only ever add one entry to the
                        // back stack at a time. It would therefore be confusing to the user to add one
                        // when navigating forward, but remove two when navigating back.
                        previousEntries = entries.dropLast(1),
                        firstEntry = firstEntry,
                        secondEntry = secondEntry
                    )
                } else {
                    null
                }*/
        return null
    }
}