package com.lonwulf.labs.easyshopmanager.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lonwulf.labs.easyshopmanager.R

@Composable
fun ProductItemComponent(
    modifier: Modifier = Modifier,
    name: String,
    make: String,
    qty: String,
    onDelete: () -> Unit,
    onclick: (() -> Unit)? = null
) {
    ElevatedCard(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(5.dp),
        modifier = modifier.fillMaxWidth()
            .padding(5.dp),
        colors = CardDefaults.cardColors(contentColor = Color.White),
        onClick = {onclick?.invoke() }
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = name, style = MaterialTheme.typography.bodyMedium)
            Text(text = make, style = MaterialTheme.typography.bodyMedium)
            Text(text = "qty: $qty", style = MaterialTheme.typography.bodyMedium)
            Image(
                painter = painterResource(R.drawable.outline_delete_icn),
                contentDescription = "delete icon",
                modifier = Modifier.size(24.dp)
                    .clickable{ onDelete() }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun showSingleComponent() {
    ProductItemComponent(name = "Awei earpods", make = "Awei", qty = "3", onDelete = {}, onclick = {})
}

@Preview(showBackground = false)
@Composable
fun showListItemComponent() {
    LazyColumn(contentPadding = PaddingValues(5.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)) {
        items(5) {
            ProductItemComponent(name = "Awei earpods", make = "Awei", qty = "3", onDelete = {}, onclick = {})
        }
    }
}