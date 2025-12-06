package pl.cuyer.rusthub.android.designsystem

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Velocity


@OptIn(ExperimentalMaterial3Api::class)
@Deprecated("No longer needed")
@Composable
fun Modifier.bottomSheetNestedScroll(
    sheetState: SheetState,
    onFling: (velocity: Float) -> Unit,
): Modifier {
    val connection = remember(sheetState) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                return if (delta < 0 && source == NestedScrollSource.UserInput) {
                    val consumed = sheetState.dispatchRawDeltaCompat(delta)
                    Offset(0f, consumed)
                } else {
                    Offset.Zero
                }
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource,
            ): Offset {
                return if (source == NestedScrollSource.UserInput) {
                    val consumed = sheetState.dispatchRawDeltaCompat(available.y)
                    Offset(0f, consumed)
                } else {
                    Offset.Zero
                }
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                val toFling = available.y
                val currentOffset = sheetState.requireOffset()
                val minAnchor = sheetState.minAnchorCompat()
                return if (toFling < 0 && currentOffset > minAnchor) {
                    onFling(toFling)
                    available
                } else {
                    Velocity.Zero
                }
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                onFling(available.y)
                return available
            }
        }
    }
    return this.nestedScroll(connection)
}