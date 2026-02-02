package com.pixeleye.qrscanner

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun AdBanner(modifier: Modifier = Modifier) {
    AndroidView(
            modifier = modifier.fillMaxWidth(),
            factory = { context ->
                AdView(context).apply {
                    setAdSize(AdSize.BANNER)
                    // Test Ad Unit ID for Banner
                    adUnitId = "ca-app-pub-3679572083540798/6162736902"
                    loadAd(AdRequest.Builder().build())
                }
            }
    )
}
