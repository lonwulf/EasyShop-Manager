package com.lonwulf.labs.easyshopmanager.presentation.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle

@Composable
fun SpannableClickableText(
    normalText: String,
    clickableText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    normalTextColor: Color = MaterialTheme.colorScheme.onBackground,
    clickableTextColor: Color = MaterialTheme.colorScheme.primaryContainer,
) {
    val annotatedString = buildAnnotatedString {
        withStyle(SpanStyle(color = normalTextColor)) {
            append(normalText)
        }
        withLink(
            LinkAnnotation.Clickable(
                tag = "CLICKABLE",
                styles = TextLinkStyles(
                    style = SpanStyle(
                        color = clickableTextColor,
                        fontWeight = FontWeight.Bold,
                    )
                ),
                linkInteractionListener = { onClick() }
            )
        ) {
            append(clickableText)
        }
    }

    Text(
        text = annotatedString,
        modifier = modifier,
        style = MaterialTheme.typography.bodyMedium,
    )
}