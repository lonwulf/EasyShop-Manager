package com.lonwulf.labs.easyshopmanager.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.DialogProperties
import com.lonwulf.labs.easyshopmanager.R
import com.lonwulf.labs.easyshopmanager.ui.theme.EasyShopManagerTheme

@Composable
fun CustomAlertDialogComponent(
    type: AlertDialogType,
    msg: String,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    titleContentColor: Color,
    textContentColor: Color,
    imageVector: ImageVector,
    iconTint: Color,
    title: String,
) {

    val isError = type == AlertDialogType.ERROR_DIALOG

    AlertDialog(
        titleContentColor = titleContentColor,
        textContentColor = textContentColor,
        containerColor = MaterialTheme.colorScheme.background,
        icon = {
            Icon(
                imageVector = imageVector,
                contentDescription = imageVector::class.java.simpleName,
                tint = iconTint
            )
        },
        title = { Text(text = title) },
        text = { Text(text = msg) },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            if (isError) {
                TextButton(onClick = onConfirmation) {
                    Text(stringResource(R.string.retry), color = MaterialTheme.colorScheme.primary)
                }
            } else {
                TextButton(onClick = onDismissRequest) {
                    Text(stringResource(android.R.string.ok), color = MaterialTheme.colorScheme.primary)
                }
            }

        },
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false),
        dismissButton = if (isError) {
            {
                TextButton(onClick = onDismissRequest) {
                    Text(stringResource(R.string.dismiss))
                }
            }
        } else null
    )
}

enum class AlertDialogType {
    SUCCESS_DIALOG, ERROR_DIALOG, WARNING_DIALOG
}


@Preview(showBackground = true)
@Composable
fun SuccessAlertDialog() {
    EasyShopManagerTheme {
        CustomAlertDialogComponent(
            type = AlertDialogType.SUCCESS_DIALOG,
            msg = "Operation completed!",
            onDismissRequest = { /* dismiss */ },
            onConfirmation = { /* continue */ },
            titleContentColor = Color.Green,
            textContentColor = Color.Black,
            imageVector = Icons.Filled.CheckCircle,
            iconTint = Color.Green,
            title = stringResource(R.string.success)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ErrorAlertDialog() {
    EasyShopManagerTheme {
        CustomAlertDialogComponent(
            type = AlertDialogType.ERROR_DIALOG,
            msg = "Something went wrong.",
            onDismissRequest = { /* dismiss */ },
            onConfirmation = { /* retry */ },
            titleContentColor = Color.Red,
            textContentColor = Color.Black,
            imageVector = Icons.Filled.ErrorOutline,
            iconTint = Color.Red,
            title = stringResource(R.string.error)
        )
    }
}