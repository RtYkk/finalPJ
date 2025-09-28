package jlu.kemiko.libman.ui.loans

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview as CameraPreview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import jlu.kemiko.libman.scan.IsbnBarcodeAnalyzer
import jlu.kemiko.libman.scan.ScannerState
import jlu.kemiko.libman.scan.getMainExecutor
import jlu.kemiko.libman.scan.rememberCameraProvider
import jlu.kemiko.libman.ui.components.LibmanPrimaryButton
import jlu.kemiko.libman.ui.components.LibmanSurfaceCard
import jlu.kemiko.libman.ui.theme.LibmanTheme

@Composable
fun LoanScannerRoute(
    onIsbnConfirmed: (String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoanScannerViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProvider = rememberCameraProvider()
    val previewView = rememberPreviewView()

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        hasPermission = granted
        if (granted) {
            viewModel.beginScanning()
        } else {
            viewModel.onError("Camera permission is required to scan ISBN codes.")
        }
    }

    LaunchedEffect(hasPermission) {
        if (hasPermission && state is ScannerState.Idle) {
            viewModel.beginScanning()
        }
    }

    DisposableEffect(cameraProvider, hasPermission, state) {
        if (!hasPermission || cameraProvider == null) {
            onDispose { }
        } else {
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            val preview = buildPreview(previewView)
            val analysis = buildImageAnalysis()
            val analyzer = IsbnBarcodeAnalyzer(
                shouldProcess = viewModel::isScanning,
                onIsbnDetected = { isbn ->
                    if (viewModel.isScanning()) {
                        viewModel.onIsbnDetected(isbn)
                    }
                },
                onError = { error -> viewModel.onError("Failed to read ISBN", error) }
            )
            analysis.setAnalyzer(context.getMainExecutor(), analyzer)

            var bound = false
            try {
                bindCameraUseCases(cameraProvider, lifecycleOwner, cameraSelector, preview, analysis)
                bound = true
            } catch (error: Exception) {
                viewModel.onError("Unable to start camera preview", error)
            }

            onDispose {
                analysis.clearAnalyzer()
                analyzer.close()
                if (bound) {
                    cameraProvider.unbindAll()
                }
            }
        }
    }

    LoanScannerScreen(
        state = state,
        hasPermission = hasPermission,
        previewView = previewView,
        onRequestPermission = {
            if (!hasPermission) {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        },
        onScanAgain = {
            viewModel.reset()
        },
        onUseIsbn = { isbn ->
            onIsbnConfirmed(isbn)
            onClose()
        },
        modifier = modifier
    )
}

private fun buildPreview(previewView: PreviewView): CameraPreview =
    CameraPreview.Builder()
        .build()
        .apply {
            setSurfaceProvider(previewView.surfaceProvider)
        }

private fun buildImageAnalysis(): ImageAnalysis =
    ImageAnalysis.Builder()
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build()

private fun bindCameraUseCases(
    cameraProvider: ProcessCameraProvider,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner,
    cameraSelector: CameraSelector,
    preview: CameraPreview,
    analysis: ImageAnalysis
) {
    cameraProvider.unbindAll()
    cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, analysis)
}

@Composable
private fun rememberPreviewView(): PreviewView {
    val context = LocalContext.current
    return remember(context) {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        }
    }
}

@Composable
private fun LoanScannerScreen(
    state: ScannerState,
    hasPermission: Boolean,
    previewView: PreviewView,
    onRequestPermission: () -> Unit,
    onScanAgain: () -> Unit,
    onUseIsbn: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LibmanTheme.spacing

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = spacing.large, vertical = spacing.large),
        verticalArrangement = Arrangement.spacedBy(spacing.large)
    ) {
        Text(
            text = "Scan ISBN",
            style = LibmanTheme.typography.headlineMedium
        )

        if (!hasPermission) {
            PermissionCard(onRequestPermission = onRequestPermission)
            return@Column
        }

        LibmanSurfaceCard(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = true),
            tonal = false
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                AndroidView(factory = { previewView })
            }
        }

        when (state) {
            ScannerState.Idle, ScannerState.Scanning -> {
                Text(
                    text = "Align the book's barcode within the frame.",
                    style = LibmanTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            is ScannerState.Success -> {
                ScanSuccessCard(
                    isbn = state.isbn13,
                    onScanAgain = onScanAgain,
                    onUseIsbn = onUseIsbn
                )
            }

            is ScannerState.Error -> {
                ScanErrorCard(message = state.message, onScanAgain = onScanAgain, onRequestPermission = onRequestPermission)
            }
        }
    }
}

@Composable
private fun PermissionCard(onRequestPermission: () -> Unit) {
    val spacing = LibmanTheme.spacing
    LibmanSurfaceCard(tonal = true) {
        Column(
            verticalArrangement = Arrangement.spacedBy(spacing.small)
        ) {
            Text(
                text = "Camera access required",
                style = LibmanTheme.typography.titleMedium
            )
            Text(
                text = "Grant camera permission to scan ISBN barcodes.",
                style = LibmanTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            LibmanPrimaryButton(
                text = "Grant permission",
                onClick = onRequestPermission
            )
        }
    }
}

@Composable
private fun ScanSuccessCard(
    isbn: String,
    onScanAgain: () -> Unit,
    onUseIsbn: (String) -> Unit
) {
    val spacing = LibmanTheme.spacing
    LibmanSurfaceCard(tonal = true) {
        Column(
            verticalArrangement = Arrangement.spacedBy(spacing.small)
        ) {
            Text(
                text = "ISBN detected",
                style = LibmanTheme.typography.titleMedium
            )
            Text(
                text = isbn,
                style = LibmanTheme.typography.headlineSmall
            )
            Text(
                text = "Tap below to scan another book.",
                style = LibmanTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            LibmanPrimaryButton(
                text = "Use this ISBN",
                onClick = { onUseIsbn(isbn) },
                modifier = Modifier.fillMaxWidth()
            )
            LibmanPrimaryButton(
                text = "Scan another",
                onClick = onScanAgain,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ScanErrorCard(
    message: String,
    onScanAgain: () -> Unit,
    onRequestPermission: () -> Unit
) {
    val spacing = LibmanTheme.spacing
    LibmanSurfaceCard(tonal = true) {
        Column(
            verticalArrangement = Arrangement.spacedBy(spacing.small)
        ) {
            Text(
                text = "Unable to scan",
                style = LibmanTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error
            )
            Text(
                text = message,
                style = LibmanTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
                LibmanPrimaryButton(
                    text = "Try again",
                    onClick = onScanAgain,
                    modifier = Modifier.fillMaxWidth()
                )
                LibmanPrimaryButton(
                    text = "Grant permission",
                    onClick = onRequestPermission,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoanScannerPreview() {
    LibmanTheme {
        LoanScannerScreen(
            state = ScannerState.Scanning,
            hasPermission = true,
            previewView = PreviewView(LocalContext.current),
            onRequestPermission = {},
            onScanAgain = {},
            onUseIsbn = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoanScannerSuccessPreview() {
    LibmanTheme {
        LoanScannerScreen(
            state = ScannerState.Success("9781234567890"),
            hasPermission = true,
            previewView = PreviewView(LocalContext.current),
            onRequestPermission = {},
            onScanAgain = {},
            onUseIsbn = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoanScannerPermissionPreview() {
    LibmanTheme {
        LoanScannerScreen(
            state = ScannerState.Idle,
            hasPermission = false,
            previewView = PreviewView(LocalContext.current),
            onRequestPermission = {},
            onScanAgain = {},
            onUseIsbn = {}
        )
    }
}
