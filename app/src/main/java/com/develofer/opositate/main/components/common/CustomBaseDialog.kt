package com.develofer.opositate.main.components.common

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.develofer.opositate.R
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun SuccessDialog(
    modifier: Modifier = Modifier,
    isDialogVisible: Boolean = true,
    onDismiss: () -> Unit = {},
    delayTime: Long = 0,
    confirmButton: @Composable () -> Unit = {},
    dismissButton: @Composable (() -> Unit) = {},
    title: @Composable (() -> Unit)?,
    content: @Composable (() -> Unit)? = {
        Text(text = stringResource(R.string.custom_base_dialog__default__exit_operation))
    }
) {
    CustomBaseAlertDialog(
        modifier = modifier.clip(shape = RoundedCornerShape(13.dp)),
        isDialogVisible = isDialogVisible,
        onDismiss = onDismiss,
        title = title,
        content = content ,
        confirmButton = confirmButton,
        dismissButton = dismissButton,
        delayTime = delayTime
    )
}

@Composable
fun ErrorDialog(
    modifier: Modifier = Modifier,
    isDialogVisible: Boolean = true,
    onDismiss: () -> Unit = {},
    delayTime: Long = 0,
    confirmButton: @Composable () -> Unit,
    title: @Composable (() -> Unit)? = {
        Text(text = stringResource(R.string.custom_base_dialog__default__error))
    },
    text: @Composable (() -> Unit)? = {
        Text(text = stringResource(R.string.custom_base_dialog__default__try_again))
    }
) {
    CustomBaseAlertDialog(
        modifier = modifier,
        isDialogVisible = isDialogVisible,
        onDismiss = onDismiss,
        title = title,
        content = text ,
        confirmButton = confirmButton,
        delayTime = delayTime,

    )
}

@Composable
fun CustomBaseAlertDialog(
    modifier: Modifier = Modifier,
    isDialogVisible: Boolean = true,
    onDismiss: () -> Unit = {},
    delayTime: Long = 0,
    title: @Composable (() -> Unit)?,
    content: @Composable (() -> Unit)?,
    dismissButton: @Composable (() -> Unit)? = null,
    confirmButton: @Composable (() -> Unit)
) {
    if (isDialogVisible) {
        AlertDialog(
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(23.dp),
            modifier = modifier,
            onDismissRequest = onDismiss,
            title = title,
            text = content,
            confirmButton = confirmButton,
            dismissButton = dismissButton,
        )

    }
    if (delayTime > 0) {
        LaunchedEffect(isDialogVisible) {
            delay(delayTime.milliseconds)
            onDismiss()
        }
    }
}

@Composable
fun CustomBaseDialog(
    onDismiss: () -> Unit,
    content: @Composable () -> Unit,
    properties: DialogProperties = DialogProperties(),
    delayTime: Long = 0,
    isDialogVisible: Boolean
) {
    Dialog(
        onDismissRequest = onDismiss,
        content = content,
        properties = properties
    )
    if (delayTime > 0) {
        LaunchedEffect(isDialogVisible) {
            delay(delayTime.milliseconds)
            onDismiss()
        }
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun CustomBaseDialogPreview() {
    Column {
        SuccessDialog(
            onDismiss = {
            },
            isDialogVisible = true,
            delayTime = 3000,
            title = { Text(text = stringResource(id = R.string.login_screen__title_text__login_successful)) },
            content = { Text(stringResource(id = R.string.login_screen__text__login_successful)) },
            confirmButton = {  },
        )
    }
}
