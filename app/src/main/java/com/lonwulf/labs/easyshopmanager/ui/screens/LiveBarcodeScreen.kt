package com.lonwulf.labs.easyshopmanager.ui.screens

import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.lonwulf.labs.camera.domain.model.BarcodeField
import com.lonwulf.labs.camera.ui.barcodeDetection.BarcodeProcessor
import com.lonwulf.labs.camera.ui.barcodeDetection.BarcodeResultContent
import com.lonwulf.labs.camera.ui.camera.CameraSource
import com.lonwulf.labs.camera.ui.camera.GraphicOverlay
import com.lonwulf.labs.camera.ui.viewModel.CameraXViewModel
import com.lonwulf.labs.easyshopmanager.navigation.NavComposable
import org.koin.androidx.compose.koinViewModel

class LiveBarcodeScreenComposable : NavComposable {
    @Composable
    override fun Composable(
        navHostController: NavHostController,
    ) {
        LiveBarcodeScreen(
            onClose = { navHostController.popBackStack() }
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun LiveBarcodeScreen(
    modifier: Modifier = Modifier,
    onClose: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    viewModel: CameraXViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    val workflowState by viewModel.workflowState.collectAsState()
    val detectedBarcode by viewModel.detectedBarcode.collectAsState()

    val graphicOverlay = remember { GraphicOverlay(context, null) }
    val previewView = remember { PreviewView(context) }
    val cameraSource = remember { CameraSource(graphicOverlay) }

    var isFlashOn by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.markCameraLive()
        cameraSource.setFrameProcessor(BarcodeProcessor(graphicOverlay, viewModel))
    }

    LaunchedEffect(cameraPermissionState.status.isGranted) {
        if (cameraPermissionState.status.isGranted) {
            if (viewModel.workflowState.value == CameraXViewModel.WorkflowState.NOT_STARTED) {
                viewModel.setWorkflowState(CameraXViewModel.WorkflowState.DETECTING)
            }
        } else {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    LaunchedEffect(workflowState, cameraPermissionState.status.isGranted) {
        if (!cameraPermissionState.status.isGranted) return@LaunchedEffect

        when (workflowState) {
            CameraXViewModel.WorkflowState.DETECTING,
            CameraXViewModel.WorkflowState.CONFIRMING -> {
                cameraSource.start(lifecycleOwner, previewView.surfaceProvider)
            }

            CameraXViewModel.WorkflowState.SEARCHING -> {
                isFlashOn = false
                cameraSource.updateFlashMode("off")
            }

            else -> Unit
        }
    }

    LaunchedEffect(detectedBarcode) {
        if (detectedBarcode != null) {
            viewModel.markCameraFrozen()
        } else {
            viewModel.markCameraLive()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            cameraSource.release()
            viewModel.markCameraFrozen()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (cameraPermissionState.status.isGranted) {
//            CameraSourcePreview(
//                cameraSource = cameraSource,
//                modifier = Modifier.fillMaxSize()
//            ) {
//                GraphicOverlayView(
//                    graphicOverlay = graphicOverlayView,
//                    modifier = Modifier.fillMaxSize()
//                )
//            }
            AndroidView(
                factory = { previewView },
                modifier = Modifier.fillMaxSize()
            )

            AndroidView(
                factory = { graphicOverlay },
                modifier = Modifier.fillMaxSize()
            )
        }

        PromptChip(
            workflowState = workflowState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        AnimatedVisibility(
            visible = detectedBarcode != null,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    val barcodeFieldList = remember(detectedBarcode) {
                        listOf(
                            BarcodeField("Raw Value", detectedBarcode?.rawValue ?: "")
                        )
                    }

                    BarcodeResultContent(
                        barcodeFieldList = barcodeFieldList,
                        onDismiss = {
                            viewModel.setDetectedBarcode(null)
                        }
                    )

                    IconButton(
                        onClick = { viewModel.setDetectedBarcode(null) },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Dismiss result")
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }
            Row {
                IconButton(onClick = {
                    isFlashOn = !isFlashOn
                    cameraSource.updateFlashMode(if (isFlashOn) "torch" else "off")
                }) {
                    Icon(
                        imageVector = if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                        contentDescription = "Flash",
                        tint = Color.White
                    )
                }
                IconButton(onClick = onNavigateToSettings) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
                }
            }
        }
    }
}

@Composable
fun PromptChip(
    workflowState: CameraXViewModel.WorkflowState,
    modifier: Modifier = Modifier
) {
    val promptText = when (workflowState) {
        CameraXViewModel.WorkflowState.DETECTING -> "Point at a barcode"
        CameraXViewModel.WorkflowState.CONFIRMING -> "Move camera closer"
        CameraXViewModel.WorkflowState.SEARCHING -> "Searching..."
        else -> null
    }

    AnimatedVisibility(
        visible = promptText != null,
        modifier = modifier.padding(bottom = 64.dp),
        enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(),
        exit = fadeOut()
    ) {
        AssistChip(
            onClick = {},
            label = {
                Text(
                    text = promptText ?: "",
                    style = MaterialTheme.typography.labelLarge
                )
            },
            colors = AssistChipDefaults.assistChipColors(
                containerColor = Color.Black.copy(alpha = 0.7f),
                labelColor = Color.White
            ),
            shape = CircleShape,
            border = null
        )
    }
}
