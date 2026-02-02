package com.pixeleye.qrscanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ScanResult(
    val text: String,
    val isUrl: Boolean
)

class QRScannerViewModel : ViewModel() {
    private val _scanResult = MutableStateFlow<ScanResult?>(null)
    val scanResult: StateFlow<ScanResult?> = _scanResult.asStateFlow()

    private val _isScanning = MutableStateFlow(true)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    fun onBarcodeDetected(text: String) {
        viewModelScope.launch {
            if (_isScanning.value) {
                _isScanning.value = false
                val isUrl = text.startsWith("http://") || 
                           text.startsWith("https://") ||
                           text.startsWith("www.")
                _scanResult.value = ScanResult(text, isUrl)
            }
        }
    }

    fun dismissResult() {
        _scanResult.value = null
        _isScanning.value = true
    }

    fun resetScanning() {
        _isScanning.value = true
    }
}
