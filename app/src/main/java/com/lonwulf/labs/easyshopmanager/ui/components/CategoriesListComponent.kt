package com.lonwulf.labs.easyshopmanager.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lonwulf.labs.easyshopmanager.core.domain.model.Category
import com.lonwulf.labs.easyshopmanager.presentation.ui.components.CoilImageLoader

@Composable
fun CategoriesListComponent(modifier: Modifier = Modifier, categoriesList: List<Category> = emptyList()) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Adaptive(minSize = 80.dp),
        contentPadding = PaddingValues(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        items(categoriesList) { category ->
            CategoryItemComponent(
                name = category.name,
                image = category.image,
                onclick = {}
            )
        }
        item {
            CategoryItemComponent(
                name = "All categories",
                imageVector = Icons.Default.Category,
                onclick = {}
            )
        }
    }
}

@Composable
fun CategoryItemComponent(
    modifier: Modifier = Modifier,
    name: String,
    imageVector: ImageVector? = null,
    image: String? = null,
    onclick: (catName: String) -> Unit,
) {
    Column(
        modifier = modifier
            .width(100.dp)
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(10.dp))
            .clickable { onclick(name) }
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (imageVector != null) {
            Icon(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        CircleShape
                    )
                    .padding(8.dp),
                imageVector = imageVector,
                contentDescription = name,
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        } else {
            CoilImageLoader(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                url = image,
                isCategory = true,
                onError = {}
            )
        }

        Text(
            text = name,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CategoriesListComponentPreview() {
    CategoriesListComponent()
}