package com.lonwulf.labs.easyshopmanager.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.lonwulf.labs.easyshopmanager.navigation.NavComposable
import com.lonwulf.labs.easyshopmanager.presentation.viewmodel.WorkflowViewModel
import com.lonwulf.labs.easyshopmanager.scanner.camera.CameraSource
import com.lonwulf.labs.easyshopmanager.scanner.camera.GraphicOverlay

class LiveBarcodeScreenComposable : NavComposable {
    @Composable
    override fun Composable(
        navHostController: NavHostController,
        snackbarHostState: SnackbarHostState
    ) {
        TODO("Not yet implemented")
    }

}

@Composable
fun LiveBarcodeScreen(modifier: Modifier = Modifier, onClose: () -> Unit = {}, viewModel: WorkflowViewModel) {
    val ctx = LocalContext.current
    val workflowState by viewModel.workflowState.collectAsState()
    val detectedBarcode by viewModel.detectedBarcode.collectAsState()

    val flashEnabled by remember { mutableStateOf(false) }

    val cameraSource = remember {
        CameraSource(ctx, GraphicOverlay(ctx, attrs), /* cameraId */ "0")
    }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context->
                val preview = CameraSourcePreview(context)
                val overlay = GraphicOverlay(context)

                preview.start(cameraSource)

            }
        )
    }
}