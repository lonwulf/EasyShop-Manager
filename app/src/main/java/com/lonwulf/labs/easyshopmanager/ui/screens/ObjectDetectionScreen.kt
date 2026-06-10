package com.lonwulf.labs.easyshopmanager.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.lonwulf.labs.camera.source.repository.ProminentObjectProcessor
import com.lonwulf.labs.camera.ui.camera.CameraSource
import com.lonwulf.labs.camera.ui.camera.CameraSourcePreview
import com.lonwulf.labs.camera.ui.camera.GraphicOverlay
import com.lonwulf.labs.camera.ui.camera.GraphicOverlayView
import com.lonwulf.labs.camera.ui.objectDetection.MultiObjectProcessor
import com.lonwulf.labs.camera.ui.viewModel.CameraXViewModel
import com.lonwulf.labs.camera.util.PreferenceUtils
import com.lonwulf.labs.easyshopmanager.R
import com.lonwulf.labs.easyshopmanager.navigation.NavComposable
import com.lonwulf.labs.easyshopmanager.ui.components.ProductList
import org.koin.androidx.compose.koinViewModel


class ObjectDetectionScreenComposable : NavComposable {
    @Composable
    override fun Composable(
        navHostController: NavHostController,
    ) {
        ObjectDetectionScreen()
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ObjectDetectionScreen(cameraXViewModel: CameraXViewModel = koinViewModel()) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val workflowState by cameraXViewModel.workflowState.collectAsState()
    val objectToSearch by cameraXViewModel.objectToSearch.collectAsState()
    val searchedObject by cameraXViewModel.searchedObject.collectAsState()

    val showBottomSheet = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var flashEnabled by remember { mutableStateOf(false) }

    val graphicOverlayView = remember {
        GraphicOverlay(context, null)
    }

    val cameraSourceInstance = remember {
        CameraSource(graphicOverlayView)
    }

    DisposableEffect(Unit) {
        cameraXViewModel.markCameraLive()
        onDispose {
            cameraXViewModel.markCameraFrozen()
        }
    }

    LaunchedEffect(cameraXViewModel) {
        cameraSourceInstance.setFrameProcessor(
            if (PreferenceUtils.isMultipleObjectsMode()) {
                MultiObjectProcessor(graphicOverlayView, cameraXViewModel)
            } else {
                ProminentObjectProcessor(graphicOverlayView, cameraXViewModel)
            }
        )
        cameraXViewModel.setWorkflowState(CameraXViewModel.WorkflowState.DETECTING)
    }

    LaunchedEffect(objectToSearch) {
        objectToSearch?.let { detectedObject ->
//            searchEngine.search(detectedObject) { obj, products ->
//                cameraXViewModel.onSearchCompleted(obj, products)
//            }
        }
    }

    LaunchedEffect(searchedObject) {
        if (searchedObject != null) {
            showBottomSheet.value = true
        } else {
            showBottomSheet.value = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        CameraSourcePreview(
            cameraSource = cameraSourceInstance,
            modifier = Modifier.fillMaxSize()
        ) {
            GraphicOverlayView(
                graphicOverlay = graphicOverlayView,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {

            }) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }
            Row {
                IconButton(onClick = {
                    flashEnabled = !flashEnabled
                    cameraSourceInstance.updateFlashMode(if (flashEnabled) "torch" else "off")
                }) {
                    Icon(
                        if (flashEnabled) Icons.Default.FlashOn else Icons.Default.FlashOff,
                        contentDescription = "Flash",
                        tint = Color.White
                    )
                }
                IconButton(onClick = { /* Open Settings */ }) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
                }
            }
        }

        // Prompt Chip
        AnimatedVisibility(
            visible = workflowState == CameraXViewModel.WorkflowState.DETECTING || workflowState == CameraXViewModel.WorkflowState.CONFIRMING || workflowState == CameraXViewModel.WorkflowState.SEARCHING,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        ) {
            val promptText = when (workflowState) {
                CameraXViewModel.WorkflowState.CONFIRMING -> stringResource(R.string.prompt_hold_camera_steady)
                CameraXViewModel.WorkflowState.SEARCHING -> stringResource(R.string.prompt_searching)
                else -> stringResource(R.string.prompt_point_at_an_object)
            }
            Surface(
                color = Color.Black.copy(alpha = 0.7f),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = promptText,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }

        // Search Progress Bar
        if (workflowState == CameraXViewModel.WorkflowState.SEARCHING) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color.White
            )
        }

        // Manual Search Button
        AnimatedVisibility(
            visible = workflowState == CameraXViewModel.WorkflowState.CONFIRMED && !PreferenceUtils.isAutoSearchEnabled(),
            enter = fadeIn() + expandIn(),
            exit = fadeOut() + shrinkOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        ) {
            Button(
                onClick = { cameraXViewModel.onSearchButtonClicked() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
            ) {
                Text(text = stringResource(R.string.product_search_button))
            }
        }

        if (showBottomSheet.value) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet.value = false
                    cameraXViewModel.setWorkflowState(CameraXViewModel.WorkflowState.DETECTING)
                },
                sheetState = sheetState,
                content = {
                    searchedObject?.let { obj ->
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = pluralStringResource(R.plurals.bottom_sheet_title, count = obj.productList.size, obj.productList.size),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            ProductList(productList = obj.productList)
                        }
                    }
                }
            )
        }
    }
}