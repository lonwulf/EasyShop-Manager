package com.lonwulf.labs.easyshopmanager.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

enum class FabType {
    SMALL, MEDIUM, LARGE
}

@Composable
fun CustomFabComponent(modifier: Modifier = Modifier, onclick: () -> Unit, fabType: FabType = FabType.MEDIUM) {

}