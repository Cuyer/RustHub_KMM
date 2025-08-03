package pl.cuyer.rusthub.android.feature.about

import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import org.koin.compose.koinInject
import pl.cuyer.rusthub.BuildConfig
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.designsystem.AppTextButton
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.android.util.composeUtil.stringResource
import pl.cuyer.rusthub.common.getImageByFileName
import pl.cuyer.rusthub.util.AppInfo
import pl.cuyer.rusthub.util.EmailSender
import pl.cuyer.rusthub.util.UrlOpener
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onNavigateUp: () -> Unit) {
    val urlOpener = koinInject<UrlOpener>()
    val emailSender = koinInject<EmailSender>()
    val context = LocalContext.current
    val versionName = remember { AppInfo.versionName }
    val versionCode = remember {
        try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            pInfo.longVersionCode
        } catch (e: PackageManager.NameNotFoundException) {
            0L
        }
    }
    Scaffold(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(SharedRes.strings.about), fontWeight = FontWeight.SemiBold)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(SharedRes.strings.navigate_up),
                            tint = contentColorFor(TopAppBarDefaults.topAppBarColors().containerColor)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(spacing.medium),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing.medium)
        ) {
            Image(
                painter = painterResource(id = getImageByFileName("rusthub_logo").drawableResId),
                contentDescription = stringResource(SharedRes.strings.application_logo),
                modifier = Modifier.size(96.dp)
            )
            Text(stringResource(SharedRes.strings.app_name), style = MaterialTheme.typography.titleMedium)
            Text(
                text = stringResource(SharedRes.strings.app_version_build, versionName, versionCode),
                style = MaterialTheme.typography.bodyMedium
            )
            AppTextButton(onClick = { urlOpener.openUrl("https://rusthub.me") }) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(SharedRes.strings.website))
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowRight,
                        contentDescription = stringResource(SharedRes.strings.website)
                    )
                }
            }
            AppTextButton(onClick = {
                emailSender.sendEmail("rusthubapp@gmail.com", "RustHub - bug report")
            }) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(SharedRes.strings.report_bug))
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowRight,
                        contentDescription = stringResource(SharedRes.strings.report_bug_button)
                    )
                }
            }
            AppTextButton(onClick = {
                emailSender.sendEmail("rusthubapp@gmail.com", "RustHub - feature suggestion")
            }) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(SharedRes.strings.suggest_feature))
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowRight,
                        contentDescription = stringResource(SharedRes.strings.suggest_feature_button)
                    )
                }
            }
            val licensesTitle = stringResource(SharedRes.strings.licenses)
            AppTextButton(onClick = {
                OssLicensesMenuActivity.setActivityTitle(licensesTitle)
                context.startActivity(Intent(context, OssLicensesMenuActivity::class.java))
            }) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(SharedRes.strings.open_source_licenses))
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowRight,
                        contentDescription = stringResource(SharedRes.strings.open_source_licenses_button)
                    )
                }
            }

            val facepunch = stringResource(SharedRes.strings.facepunch)
            Text(
                text = stringResource(SharedRes.strings.disclaimer_facepunch, facepunch),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}


