package com.lonwulf.labs.easyshopmanager.presentation.screens

import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.lonwulf.labs.easyshopmanager.navigation.NavComposable
import com.lonwulf.labs.easyshopmanager.presentation.viewmodel.LiveBarcodeViewModel
import com.lonwulf.labs.easyshopmanager.scanner.BarcodeScannerProcessor
import com.lonwulf.labs.easyshopmanager.scanner.camera.CameraXScannerController
import com.lonwulf.labs.easyshopmanager.scanner.camera.GraphicOverlay
import com.lonwulf.labs.easyshopmanager.scanner.objectDetection.ObjectDetectionProcessor
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

class LiveBarcodeScreenComposable : NavComposable {
    @Composable
    override fun Composable(
        navHostController: NavHostController,
        snackbarHostState: SnackbarHostState
    ) {
        val viewModel: LiveBarcodeViewModel = koinViewModel()
        LiveBarcodeScreen(viewModel = viewModel)
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveBarcodeScreen(
    modifier: Modifier = Modifier,
    onClose: () -> Unit = {},
    viewModel: LiveBarcodeViewModel,
) {
    val ctx = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    val detectedBarcode by viewModel.detectedBarcode.collectAsStateWithLifecycle()
    val objectCandidate by viewModel.objectCandidate.collectAsStateWithLifecycle()
    val objectConfirmationProgress by viewModel.objectConfirmationProgress.collectAsStateWithLifecycle()

    val previewView = remember {
        PreviewView(ctx).apply { scaleType = PreviewView.ScaleType.FILL_CENTER }
    }
    val barcodeOverlay = remember { GraphicOverlay(ctx) }
    val objectOverlay = remember { GraphicOverlay(ctx) }
    val controller = remember { CameraXScannerController(ctx) }

    val barcodeProcessor = remember(barcodeOverlay) {
        BarcodeScannerProcessor(
            graphicOverlay = barcodeOverlay,
            zoomCallback = null,
            callback = viewModel
        )
    }
    val objectProcessor = remember(objectOverlay) {
        ObjectDetectionProcessor(
            graphicOverlay = objectOverlay,
            callback = viewModel
        )
    }

    var overlaysReady by remember { mutableStateOf(false) }
    var cameraStarted by remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner) {
        onDispose {
            viewModel.setCameraLive(false)
            controller.stop()
        }
    }

    // Start CameraX once the overlay views are measured.
    LaunchedEffect(overlaysReady) {
        if (overlaysReady && !cameraStarted) {
            cameraStarted = true
            viewModel.setCameraLive(true)
            controller.start(
                lifecycleOwner = lifecycleOwner,
                previewView = previewView,
                barcodeProcessor = barcodeProcessor,
                barcodeOverlay = barcodeOverlay,
                objectProcessor = objectProcessor,
                objectOverlay = objectOverlay,
                onAnalysisSize = { width, height ->
                    barcodeOverlay.setCameraInfo(width, height)
                    objectOverlay.setCameraInfo(width, height)
                }
            )
        }
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var sheetUserDismissed by remember { mutableStateOf(false) }

    LaunchedEffect(detectedBarcode) {
        if (detectedBarcode != null) {
            sheetUserDismissed = false
            coroutineScope.launch { sheetState.show() }
        } else {
            coroutineScope.launch { sheetState.hide() }
        }
    }

    ModalBottomSheet(
        onDismissRequest = {
            sheetUserDismissed = true
            coroutineScope.launch { sheetState.hide() }
            onClose()
        },
        sheetState = sheetState,
    ) {
        if (detectedBarcode != null) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = detectedBarcode!!.rawValue)
                Text(
                    text = buildString {
                        append(detectedBarcode!!.format)
                        detectedBarcode!!.valueType?.takeIf { it.isNotBlank() }?.let { value ->
                            append(" (")
                            append(value)
                            append(")")
                        }
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(text = "Selected object")
                if (objectCandidate != null) {
                    Text(text = objectCandidate!!.label ?: "Unknown")
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { objectConfirmationProgress.coerceIn(0f, 1f) },
                    )
                    Text(text = "${(objectConfirmationProgress * 100).toInt()}%")
                } else {
                    Text(text = "No candidate in center reticle")
                }
            }
        } else {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Point the camera at a barcode.")
                Text(text = "Then align an item with the center reticle.")
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { previewView },
        )

        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged { size ->
                    overlaysReady = size.width > 0 && size.height > 0
                },
            factory = { barcodeOverlay },
        )

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { objectOverlay },
        )
    }
}