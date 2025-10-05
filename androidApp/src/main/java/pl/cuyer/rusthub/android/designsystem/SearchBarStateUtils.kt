package pl.cuyer.rusthub.android.designsystem

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.tokens.MotionSchemeKeyTokens
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberStableSearchBarState(
    initialValue: SearchBarValue = SearchBarValue.Collapsed,
): SearchBarState {
    val animationSpecForExpand = remember { MotionSchemeKeyTokens.SlowSpatial.value() }
    val animationSpecForCollapse = remember { MotionSchemeKeyTokens.DefaultSpatial.value() }

    return rememberSaveable(
        initialValue,
        saver = SearchBarState.Saver(
            animationSpecForExpand = animationSpecForExpand,
            animationSpecForCollapse = animationSpecForCollapse,
        ),
    ) {
        SearchBarState(
            initialValue = initialValue,
            animationSpecForExpand = animationSpecForExpand,
            animationSpecForCollapse = animationSpecForCollapse,
        )
    }
}
