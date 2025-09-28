package jlu.kemiko.libman.scan

import android.content.Context
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun rememberCameraProvider(): ProcessCameraProvider? {
    val context = LocalContext.current
    var provider by remember(context) { mutableStateOf<ProcessCameraProvider?>(null) }

    DisposableEffect(context) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val executor = ContextCompat.getMainExecutor(context)
        cameraProviderFuture.addListener(
            {
                provider = cameraProviderFuture.get()
            },
            executor
        )
        onDispose {
            provider?.unbindAll()
            provider = null
        }
    }

    return provider
}

fun Context.getMainExecutor() = ContextCompat.getMainExecutor(this)
