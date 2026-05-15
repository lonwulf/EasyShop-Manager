package com.lonwulf.labs.easyshopmanager.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ErrorResult
import coil3.request.ImageRequest
import coil3.request.crossfade

@Composable
fun CoilImageLoader(
    url: String,
    onError: (ErrorResult) -> Unit
) {
    var isLoaded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 5.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(url)
                .crossfade(true)
                .listener(
                    onStart = {
                        Log.d("ImageDisplay:", "Image loading started: $url")
                    },
                    onSuccess = { _, _ -> isLoaded = true },
                    onError = { _, errorResult -> onError(errorResult) }
                )
                .build(),
            contentDescription = url,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )

        if (!isLoaded) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primaryContainer
            )
        }
    }
}