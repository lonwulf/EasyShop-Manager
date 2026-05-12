package com.lonwulf.labs.easyshopmanager.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp

@Composable
fun UpButtonComponent(modifier: Modifier = Modifier, onclick: () -> Unit) {
    Box(
        modifier = modifier
            .wrapContentSize()
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = onclick,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.background
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                contentDescription = "back",
                tint = MaterialTheme.colorScheme.primaryContainer
            )
        }
    }
}