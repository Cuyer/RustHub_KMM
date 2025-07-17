package pl.cuyer.rusthub.android.util.composeUtil

import androidx.compose.runtime.Composable
import org.koin.compose.koinInject
import pl.cuyer.rusthub.util.StringProvider
import dev.icerock.moko.resources.StringResource

@Composable
fun stringResource(res: StringResource, vararg args: Any): String {
    val stringProvider = koinInject<StringProvider>()
    return stringProvider.get(res, *args)
}
