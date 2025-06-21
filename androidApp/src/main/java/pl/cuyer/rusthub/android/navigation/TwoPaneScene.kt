package pl.cuyer.rusthub.android.navigation

import androidx.navigation3.runtime.NavEntry


/*
@Deprecated("Don't use")
class TwoPaneScene<T : Any>(
    override val key: Any,
    override val previousEntries: List<NavEntry<T>>,
    val firstEntry: NavEntry<T>,
    val secondEntry: NavEntry<T>
) : Scene<T> {
    override val entries: List<NavEntry<T>> = listOf(firstEntry, secondEntry)

    override val content: @Composable (() -> Unit) = {
        */
/*Row(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.weight(0.5f)) {
                firstEntry.content.invoke(firstEntry.key)
            }
            Column(modifier = Modifier.weight(0.5f)) {
                secondEntry.content.invoke(secondEntry.key)
            }
        }*//*

    }

    companion object {
        internal const val TWO_PANE_KEY = "TwoPane"
        */
/**
         * Helper function to add metadata to a [NavEntry] indicating it can be displayed
         * in a two-pane layout.
 *//*

        fun twoPane() = mapOf(TWO_PANE_KEY to true)
    }
}*/
