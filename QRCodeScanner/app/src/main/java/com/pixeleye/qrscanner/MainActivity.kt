package com.pixeleye.qrscanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.Modifier
import com.google.android.gms.ads.MobileAds

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Mobile Ads SDK
        MobileAds.initialize(this) {}
        
        enableEdgeToEdge()
        setContent {
            QRScannerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    QRScannerScreen()
                }
            }
        }
    }
}

@androidx.compose.runtime.Composable
fun QRScannerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @androidx.compose.runtime.Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme()
    } else {
        lightColorScheme()
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
