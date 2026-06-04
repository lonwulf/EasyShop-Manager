package com.lonwulf.labs.easyshopmanager.auth.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle

@Composable
fun TermsAndPrivacyText(
    onTermsClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    modifier: Modifier = Modifier,
    normalTextColor: Color = MaterialTheme.colorScheme.onBackground,
    linkTextColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    val annotatedString = buildAnnotatedString {
        withStyle(SpanStyle(color = normalTextColor)) {
            append("By signing up, you agree to our ")
        }
        withLink(
            LinkAnnotation.Clickable(
                tag = "TERMS",
                styles = TextLinkStyles(
                    style = SpanStyle(
                        color = linkTextColor,
                        textDecoration = TextDecoration.Underline,
                    )
                ),
                linkInteractionListener = { onTermsClick() }
            )
        ) {
            append("Terms of Service")
        }
        withStyle(SpanStyle(color = normalTextColor)) {
            append(" and ")
        }
        withLink(
            LinkAnnotation.Clickable(
                tag = "PRIVACY",
                styles = TextLinkStyles(
                    style = SpanStyle(
                        color = linkTextColor,
                        textDecoration = TextDecoration.Underline,
                    )
                ),
                linkInteractionListener = { onPrivacyClick() }
            )
        ) {
            append("Privacy Policy")
        }
    }

    Text(
        text = annotatedString,
        modifier = modifier.fillMaxWidth(),
        style = MaterialTheme.typography.bodySmall,
        textAlign = TextAlign.Center,
    )
}