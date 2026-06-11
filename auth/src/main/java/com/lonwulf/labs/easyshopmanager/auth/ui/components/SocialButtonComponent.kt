package com.lonwulf.labs.easyshopmanager.auth.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp


@Composable
fun SocialButtonComponent(
    modifier: Modifier = Modifier,
    socialImg: ImageVector,
    onClick: () -> Unit,
    text: String = ""
) {
    ElevatedButton(
        modifier = modifier.padding(5.dp),
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        elevation = ButtonDefaults.buttonElevation(4.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(5.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = socialImg,
                contentDescription = socialImg.name.lowercase()
            )
            if (text.isNotEmpty()) Text(text = text)
        }
    }
}