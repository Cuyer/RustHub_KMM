package pl.cuyer.rusthub.android.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class Spacing(
    val none: Dp = 0.dp,
    val xxxsmall: Dp = 2.dp,
    val xxsmall: Dp = 2.dp,
    val xsmall: Dp = 4.dp,
    val small: Dp = 6.dp,
    val xxmedium: Dp = 8.dp,
    val xmedium: Dp = 12.dp,
    val medium: Dp = 16.dp,
    val large: Dp = 32.dp,
    val extraLarge: Dp = 64.dp
)

val LocalSpacing = compositionLocalOf { Spacing() }

/**
 * Represents a set of spacing values.
 *
 * - [none]: No space (0.dp)
 *   [xxxsmall]: Extra extra extra small space (1.dp)
 * - [xxsmall]: Extra extra small space (2.dp)
 * - [xsmall]: Extra small space (4.dp)
 * - [small]: Small space (6.dp)
 * - [xxmedium]: Extra extra medium space (8.dp)
 * - [xmedium]: Extra medium space (12.dp)
 * - [medium]: Medium space (16.dp)
 * - [large]: Large space (32.dp)
 * - [extraLarge]: Extra large space (64.dp)
 */

val spacing: Spacing
    @Composable
    @ReadOnlyComposable
    get() = LocalSpacing.current