package com.lonwulf.labs.easyshopmanager.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lonwulf.labs.easyshopmanager.domain.model.Category

@Composable
fun CategoriesListComponent(modifier: Modifier = Modifier, categoriesList: List<Category> = emptyList()) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 50.dp),
        contentPadding = PaddingValues(5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        item {
            CategoryItemComponent(name = "All categories")
        }

        items(categoriesList) {
            CategoryItemComponent(name = it.name, image = it.image)
        }
    }
}

@Composable
fun CategoryItemComponent(modifier: Modifier = Modifier, name: String, image: String? = null) {
    Box(
        modifier = modifier
            .wrapContentSize()
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(10.dp))
    ) {

    }
}

@Preview(showBackground = true)
@Composable
fun CategoriesListComponentPreview() {
    CategoriesListComponent()
}