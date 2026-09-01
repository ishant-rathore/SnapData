package com.example.snapdata.ui.camera

import android.content.Context
import android.net.Uri
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.snapdata.logging.AppLogger
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Controller interface allowing UI controls (shutter, flash, etc.) to command the active CameraX instance.
 */
class CameraController internal constructor() {
    internal var imageCapture: ImageCapture? = null
    internal var cameraControl: CameraControl? = null
    internal var cameraInfo: CameraInfo? = null
    internal var cameraExecutor: ExecutorService? = null

    var hasFlashUnit by mutableStateOf(false)
        internal set

    var isTorchEnabled by mutableStateOf(false)
        internal set

    var isReady by mutableStateOf(false)
        internal set

    /**
     * Sets hardware torch on or off.
     */
    fun setTorch(enable: Boolean, context: Context) {
        if (!hasFlashUnit || cameraControl == null) {
            AppLogger.w(AppLogger.LogDomain.CAMERA, "Torch toggle ignored: Flash unit unavailable")
            return
        }
        try {
            cameraControl?.enableTorch(enable)?.addListener({
                isTorchEnabled = enable
                AppLogger.d(AppLogger.LogDomain.CAMERA, "Hardware torch state updated: $enable")
            }, cameraExecutor ?: ContextCompat.getMainExecutor(context))
        } catch (e: Exception) {
            AppLogger.e(AppLogger.LogDomain.CAMERA, "Failed to toggle torch: ${e.localizedMessage}", e)
        }
    }

    /**
     * Captures a high-resolution still image directly from CameraX.
     */
    fun captureImage(
        context: Context,
        outputFile: File,
        onSuccess: (Uri) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val capture = imageCapture
        val executor = cameraExecutor
        if (capture == null || executor == null) {
            val err = IllegalStateException("Camera is not initialized or ready for capture")
            AppLogger.e(AppLogger.LogDomain.CAMERA, "Capture failed: ${err.message}", err)
            onError(err)
            return
        }

        val outputOptions = ImageCapture.OutputFileOptions.Builder(outputFile).build()
        AppLogger.i(AppLogger.LogDomain.CAMERA, "Starting CameraX image capture to file: ${outputFile.name}")

        capture.takePicture(
            outputOptions,
            executor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = outputFileResults.savedUri ?: Uri.fromFile(outputFile)
                    AppLogger.i(AppLogger.LogDomain.CAMERA, "CameraX capture saved successfully: $savedUri (size=${outputFile.length()} bytes)")
                    ContextCompat.getMainExecutor(context).execute {
                        onSuccess(savedUri)
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    AppLogger.e(AppLogger.LogDomain.CAMERA, "CameraX capture failed: ${exception.message}", exception)
                    ContextCompat.getMainExecutor(context).execute {
                        onError(exception)
                    }
                }
            }
        )
    }
}

@Composable
fun rememberCameraController(): CameraController {
    return remember { CameraController() }
}

/**
 * Production-grade, lifecycle-aware CameraX Live Preview Composable.
 *
 * Visual layer guarantees:
 * - Uses TextureView (ImplementationMode.COMPATIBLE) to allow Compose rounded clipping,
 *   reticle overlays, laser sweep animations, and alpha blending without z-order black artifacting.
 * - Manages ProcessCameraProvider lifecycle binding cleanly.
 * - Exposes real hardware flash/torch control and ImageCapture capabilities.
 */
@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    controller: CameraController = rememberCameraController(),
    isFlashOn: Boolean = false,
    onCameraReady: () -> Unit = {},
    onCameraError: (Throwable) -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    var previewViewRef by remember { mutableStateOf<PreviewView?>(null) }

    // Sync hardware torch when isFlashOn prop changes
    LaunchedEffect(isFlashOn, controller.isReady) {
        if (controller.isReady) {
            controller.setTorch(isFlashOn, context)
        }
    }

    DisposableEffect(lifecycleOwner) {
        AppLogger.i(AppLogger.LogDomain.CAMERA, "CAMERA_INIT_START: Initializing CameraX preview component")
        controller.cameraExecutor = cameraExecutor

        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            try {
                val provider = cameraProviderFuture.get()
                cameraProvider = provider
                AppLogger.i(AppLogger.LogDomain.CAMERA, "CAMERA_PROVIDER_READY: ProcessCameraProvider instance acquired")
            } catch (e: Exception) {
                AppLogger.e(AppLogger.LogDomain.CAMERA, "CAMERA_BIND_FAILURE: Failed to acquire ProcessCameraProvider: ${e.localizedMessage}", e)
                onCameraError(e)
            }
        }, ContextCompat.getMainExecutor(context))

        onDispose {
            AppLogger.i(AppLogger.LogDomain.CAMERA, "Releasing CameraX use-cases and shutting down camera executor")
            controller.isReady = false
            controller.isTorchEnabled = false
            controller.imageCapture = null
            controller.cameraControl = null
            controller.cameraInfo = null
            try {
                cameraProvider?.unbindAll()
            } catch (e: Exception) {
                AppLogger.w(AppLogger.LogDomain.CAMERA, "Error unbinding camera on dispose: ${e.localizedMessage}")
            }
            cameraExecutor.shutdown()
        }
    }

    // Effect to bind camera use cases once Provider and PreviewView are both available
    LaunchedEffect(cameraProvider, previewViewRef, lifecycleOwner) {
        val provider = cameraProvider
        val previewView = previewViewRef
        if (provider != null && previewView != null) {
            try {
                AppLogger.i(AppLogger.LogDomain.CAMERA, "CAMERA_BIND_START: Unbinding previous use cases and binding to lifecycle")
                provider.unbindAll()

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                // 1. Preview UseCase
                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                        AppLogger.d(AppLogger.LogDomain.CAMERA, "CAMERA_PREVIEW_CREATED: Surface provider connected to PreviewView")
                    }

                // 2. ImageCapture UseCase (Low Latency Mode for Fast Document Scanning)
                val imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .build()

                // 3. ImageAnalysis UseCase (Frame streaming for document detection)
                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { analysis ->
                        analysis.setAnalyzer(cameraExecutor) { imageProxy ->
                            // Process real-time frames safely off the UI thread
                            try {
                                // Real-time frame telemetry / analysis
                            } finally {
                                imageProxy.close()
                            }
                        }
                    }

                // Verify Camera availability before binding
                if (!provider.hasCamera(cameraSelector)) {
                    val err = IllegalStateException("Device has no default back camera")
                    AppLogger.w(AppLogger.LogDomain.CAMERA, "CAMERA_UNAVAILABLE: Default back camera not present on device")
                    onCameraError(err)
                    return@LaunchedEffect
                }

                // Bind all 3 use cases to the LifecycleOwner
                val camera = provider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture,
                    imageAnalysis
                )

                controller.imageCapture = imageCapture
                controller.cameraControl = camera.cameraControl
                controller.cameraInfo = camera.cameraInfo
                controller.hasFlashUnit = camera.cameraInfo.hasFlashUnit()
                controller.isReady = true

                // Apply initial flash state
                if (isFlashOn && controller.hasFlashUnit) {
                    controller.setTorch(true, context)
                }

                AppLogger.i(AppLogger.LogDomain.CAMERA, "CAMERA_BIND_SUCCESS: Live CameraX preview bound to lifecycle successfully (hasFlash=${controller.hasFlashUnit})")
                onCameraReady()
            } catch (e: Exception) {
                AppLogger.e(AppLogger.LogDomain.CAMERA, "CAMERA_BIND_FAILURE: Exception binding CameraX to lifecycle: ${e.localizedMessage}", e)
                controller.isReady = false
                onCameraError(e)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("camera_preview_view")
    ) {
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    // COMPATIBLE (TextureView) enables proper clipping, rounded corners, and overlay layering in Compose
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    previewViewRef = this
                }
            },
            update = { view ->
                if (previewViewRef != view) {
                    previewViewRef = view
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}
