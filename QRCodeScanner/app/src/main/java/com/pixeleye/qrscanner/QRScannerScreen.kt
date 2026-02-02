package com.pixeleye.qrscanner

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun QRScannerScreen(
    viewModel: QRScannerViewModel = viewModel()
) {
    val context = LocalContext.current
    val cameraPermissionState = rememberPermissionState(
        android.Manifest.permission.CAMERA
    )

    val scanResult by viewModel.scanResult.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()

    // Show bottom sheet when result is available
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    LaunchedEffect(scanResult) {
        if (scanResult != null) {
            sheetState.show()
        }
    }

    // Automatically request camera permission on launch
    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (cameraPermissionState.status.isGranted) {
            CameraPreview(
                onBarcodeDetected = { barcode ->
                    if (isScanning) {
                        viewModel.onBarcodeDetected(barcode)
                        // Vibrate on detection
                        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) 
                            as? android.os.Vibrator
                        vibrator?.vibrate(200)
                    }
                }
            )

            // Scanning overlay with guide rectangle
            ScanningOverlay()
            
            // Add Banner Ad at the bottom
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 16.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                AdBanner()
            }
        } else {
            PermissionRequestScreen(cameraPermissionState)
        }

        // Bottom sheet for scan results
        if (scanResult != null) {
            ModalBottomSheet(
                onDismissRequest = { 
                    viewModel.dismissResult()
                },
                sheetState = sheetState
            ) {
                ScanResultContent(
                    result = scanResult!!,
                    onDismiss = { viewModel.dismissResult() },
                    context = context
                )
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionRequestScreen(permissionState: PermissionState) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Camera permission is required to scan QR codes",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { permissionState.launchPermissionRequest() }) {
            Text("Grant Permission")
        }
    }
}

@Composable
fun ScanningOverlay() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Semi-transparent background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
        )

        // Scanning rectangle
        Box(
            modifier = Modifier
                .size(300.dp)
                .border(
                    width = 3.dp,
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp)
                )
                .background(Color.Transparent)
        ) {
            // Corner markers
            val cornerSize = 30.dp
            val cornerThickness = 4.dp

            // Top-left corner
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .width(cornerSize)
                    .height(cornerThickness)
                    .background(MaterialTheme.colorScheme.primary)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .width(cornerThickness)
                    .height(cornerSize)
                    .background(MaterialTheme.colorScheme.primary)
            )

            // Top-right corner
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .width(cornerSize)
                    .height(cornerThickness)
                    .background(MaterialTheme.colorScheme.primary)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .width(cornerThickness)
                    .height(cornerSize)
                    .background(MaterialTheme.colorScheme.primary)
            )

            // Bottom-left corner
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .width(cornerSize)
                    .height(cornerThickness)
                    .background(MaterialTheme.colorScheme.primary)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .width(cornerThickness)
                    .height(cornerSize)
                    .background(MaterialTheme.colorScheme.primary)
            )

            // Bottom-right corner
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .width(cornerSize)
                    .height(cornerThickness)
                    .background(MaterialTheme.colorScheme.primary)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .width(cornerThickness)
                    .height(cornerSize)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }

        // Instruction text
        Text(
            text = "Position QR code within frame",
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 100.dp)
        )
    }
}

@Composable
fun ScanResultContent(
    result: ScanResult,
    onDismiss: () -> Unit,
    context: Context
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Text(
            text = "Scan Result",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Text(
                text = result.text,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (result.isUrl) {
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(result.text))
                        context.startActivity(intent)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Open URL")
                }
            }

            OutlinedButton(
                onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) 
                        as ClipboardManager
                    val clip = ClipData.newPlainText("QR Code", result.text)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Copy")
            }

            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.weight(1f)
            ) {
                Text("Close")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
