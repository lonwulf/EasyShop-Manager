package com.lonwulf.labs.easyshopmanager.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CardWithTitleComponent(modifier: Modifier = Modifier, title: String? = null, contents: @Composable () -> Unit) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(5.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.Start
        ) {
            title?.let {
                Text(text = it, style = MaterialTheme.typography.titleMedium)
            }
            contents.invoke()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CardWithTitleComponentPreview() {
    CardWithTitleComponent(title = "Basic information") {
        TextInputComponent(
            value = "",
            onValueChange = {},
            label = "Product name",
        )
    }
}