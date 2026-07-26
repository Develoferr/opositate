package com.develofer.opositate.feature.profile.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.develofer.opositate.feature.login.presentation.component.CustomLoginTextField
import com.develofer.opositate.feature.login.presentation.model.TextFieldErrors.ValidateFieldErrors
import com.develofer.opositate.feature.profile.presentation.viewmodel.ConfirmDialogViewModel
import com.develofer.opositate.main.components.common.LoadingButton
import com.develofer.opositate.main.data.model.UiResult

enum class FieldValidationType {
    EMAIL,
    TEXT,
    PASSWORD,
    NONE
}

@Composable
fun ConfirmDialog(
    viewModel: ConfirmDialogViewModel = hiltViewModel(),
    title: String,
    firstFieldLabel: String,
    secondFieldLabel: String,
    confirmButtonText: String,
    cancelButtonText: String,
    firstFieldType: FieldValidationType = FieldValidationType.NONE,
    secondFieldType: FieldValidationType = FieldValidationType.NONE,
    onConfirm: (String) -> Unit,
    onCancel: () -> Unit,
    onDismissRequest: () -> Unit,
    uiResult: UiResult,
    onAnimationComplete: () -> Unit,
    explainingText: String? = null,
    isDarkTheme: Boolean
) {
    val uiState by viewModel.uiState.collectAsState()

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                modifier = Modifier.padding(horizontal = 8.dp),
                text = title.uppercase()
            )
        },
        text = {
            Column {
                explainingText?.let {
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        text = explainingText
                    )
                }
                CustomLoginTextField(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    value = uiState.firstFieldValue,
                    onValueChange = { viewModel.onFirstFieldChanged(it) },
                    label = firstFieldLabel.uppercase(),
                    isFocused = uiState.isFirstFieldFocused,
                    onFocusChange = { viewModel.onFirstFieldFocusChanged(it) },
                    isPasswordField = firstFieldType == FieldValidationType.PASSWORD,
                    supportingText = uiState.firstFieldValidationError,
                    isDarkTheme = isDarkTheme,
                    textLetterSpacing = 0.sp,
                    labelFontSize = 13.sp,
                )
                CustomLoginTextField(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    value = uiState.secondFieldValue,
                    onValueChange = { viewModel.onSecondFieldChanged(it) },
                    label = secondFieldLabel.uppercase(),
                    isFocused = uiState.isSecondFieldFocused,
                    onFocusChange = { viewModel.onSecondFieldFocusChanged(it) },
                    isPasswordField = secondFieldType == FieldValidationType.PASSWORD,
                    supportingText = uiState.secondFieldValidationError,
                    isDarkTheme = isDarkTheme,
                    textLetterSpacing = 0.sp,
                    labelFontSize = 13.sp,
                )
            }
        },
        confirmButton = {
            LoadingButton(
                state = uiResult,
                onClick = {
                    val isFieldsValid = viewModel.validateFields(
                        firstFieldType,
                        secondFieldType,
                        shouldValidateSecondField = true
                    )
                    if (isFieldsValid) onConfirm(uiState.firstFieldValue)
                },
                onAnimationComplete = {
                    onAnimationComplete()
                    viewModel.cleanUpState()
                    onDismissRequest()
                },
                modifier = Modifier.padding(horizontal = 8.dp),
                text = confirmButtonText
            )
        },
        dismissButton = {
            TextButton(onClick = {
                viewModel.cleanUpState()
                onCancel()
            }) {
                Text(text = cancelButtonText.uppercase())
            }
        }
    )
}

data class GenericDialogUiState(
    val firstFieldValue: String = "",
    val secondFieldValue: String = "",
    val isFirstFieldFocused: Boolean = false,
    val isSecondFieldFocused: Boolean = false,
    val firstFieldValidationError: ValidateFieldErrors = ValidateFieldErrors.NONE,
    val secondFieldValidationError: ValidateFieldErrors = ValidateFieldErrors.NONE,
    val isPasswordVisible: Boolean = false
)