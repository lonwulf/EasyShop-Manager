package com.lonwulf.labs.easyshopmanager.ui.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ErrorResult
import coil3.request.ImageRequest
import coil3.request.crossfade

@Composable
fun CoilImageLoader(
    modifier: Modifier = Modifier,
    url: String? = null,
    isCategory: Boolean = false,
    onError: ((ErrorResult) -> Unit)? = null
) {
    var isLoaded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        url?.let {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(it)
                    .crossfade(true)
                    .listener(
                        onStart = {
                            Log.d("ImageDisplay:", "Image loading started: $it")
                        },
                        onSuccess = { _, _ -> isLoaded = true },
                        onError = { _, errorResult -> onError?.invoke(errorResult) }
                    )
                    .build(),
                contentDescription = it,
                modifier = modifier.matchParentSize(),
                contentScale = ContentScale.Crop
            )
        } ?: Image(
            modifier = modifier
                .matchParentSize()
                .background(MaterialTheme.colorScheme.surfaceVariant, if (isCategory) CircleShape else RectangleShape)
                .padding(8.dp),
            imageVector = Icons.Default.Category,
            contentDescription = "default_img",
        )

        if (!isLoaded && url != null) {
            CircularProgressIndicator(
                modifier = Modifier
                    .matchParentSize(),
                color = MaterialTheme.colorScheme.primaryContainer
            )
        }
    }
}