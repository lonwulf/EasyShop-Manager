package com.lonwulf.labs.easyshopmanager.auth.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ContinueWithEmailComponent(){
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        HorizontalDivider(
            Modifier
                .weight(0.3f)
                .height(1.dp)
                .background(MaterialTheme.colorScheme.primaryContainer)
        )
        Text(
            text = "OR CONTINUE WITH EMAIL",
            style = MaterialTheme.typography.bodyMedium
        )
        HorizontalDivider(
            Modifier
                .weight(0.3f)
                .height(1.dp)
                .background(MaterialTheme.colorScheme.primaryContainer)
        )
    }
}