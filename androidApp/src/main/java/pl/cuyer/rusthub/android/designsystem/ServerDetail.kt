package pl.cuyer.rusthub.android.designsystem

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ServerDetail(
    modifier: Modifier = Modifier,
    label: String,
    value: String
) {
    Row(
        modifier = modifier
    ) {
        Text(
            style = MaterialTheme.typography.bodyLarge.copy(
            ),
            text = "$label: "
        )
        Text(
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.primary
            ),
            text = value
        )

    }
}

@Composable
fun ServerDetail(
    modifier: Modifier = Modifier,
    label: String,
    value: Int
) {
    val animatedValue by animateIntAsState(value)
    Row(
        modifier = modifier
    ) {
        Text(
            style = MaterialTheme.typography.bodyLarge.copy(
            ),
            text = "$label: "
        )
        Text(
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.primary
            ),
            text = animatedValue.toString()
        )

    }
}

@Composable
fun ServerDetail(
    modifier: Modifier = Modifier,
    label: String,
    value: Float
) {
    val animatedValue by animateFloatAsState(value)
    Row(
        modifier = modifier
    ) {
        Text(
            style = MaterialTheme.typography.bodyLarge.copy(
            ),
            text = "$label: "
        )
        Text(
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.primary
            ),
            text = "$animatedValue%"
        )

    }
}

@Preview
@Composable
private fun ServerDetailPreview() {

}