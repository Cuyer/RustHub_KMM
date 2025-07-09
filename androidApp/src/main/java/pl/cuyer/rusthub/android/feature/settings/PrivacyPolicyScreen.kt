package pl.cuyer.rusthub.android.feature.settings

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(url: String, onNavigateUp: () -> Unit, title: String = "Privacy policy") {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate up"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            factory = { context ->
                WebView(context).apply {
                    webViewClient = WebViewClient()
                    loadUrl(url)
                }
            },
            update = { it.loadUrl(url) }
        )
    }
}

