package com.lonwulf.labs.easyshopmanager.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SearchFieldComponent(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    onSearchClick: () -> Unit
) {
    OutlinedTextField(
        shape = RoundedCornerShape(10.dp),
        modifier = modifier.fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text("Search products") },
        singleLine = true,
        trailingIcon = {
            IconButton(onClick = onSearchClick) {
                Icon(
                    contentDescription = "search icon",
                    imageVector = Icons.Outlined.Search
                )
            }
        }
    )
}

@Composable
@Preview(showBackground = true)
fun previewSearchFieldComponent() {
    SearchFieldComponent(
        value = "Search query",
        onValueChange = {},
        onSearchClick = {},
    )
}
