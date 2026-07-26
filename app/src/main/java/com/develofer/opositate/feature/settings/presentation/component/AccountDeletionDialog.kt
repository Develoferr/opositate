package com.develofer.opositate.feature.settings.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.develofer.opositate.feature.login.presentation.component.CustomLoginTextField
import com.develofer.opositate.main.components.common.LoadingButton
import com.develofer.opositate.main.data.model.UiResult
import com.develofer.opositate.ui.theme.ErrorLight

@Composable
fun AccountDeletionDialog(
    accountDeletionDialogViewModel: AccountDeletionDialogViewModel = hiltViewModel(),
    isDarkTheme: Boolean,
    onDismiss: () -> Unit,
    onDeleteAccountSuccess:() -> Unit
) {
    val uiState by accountDeletionDialogViewModel.uiState.collectAsState()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDarkTheme) Color.DarkGray else Color.White
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Eliminar cuenta",
                    style = MaterialTheme.typography.titleLarge,
                    color = ErrorLight,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "¿Estás seguro/a de que deseas eliminar tu cuenta? Esta acción es irreversible y no podrás recuperar tu progreso en el futuro.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "Si estás seguro/a y deseas eliminar tu cuenta, introduce el correo y contraseña de tu cuenta para continuar:",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                CustomLoginTextField(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    value = uiState.confirmationEmail,
                    onValueChange = { accountDeletionDialogViewModel.updateConfirmationEmail(it) },
                    label = "Correo",
                    isFocused = false,
                    onFocusChange = {},
                    supportingText = uiState.emailConfirmationError,
                    isDarkTheme = isDarkTheme
                )

                CustomLoginTextField(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    value = uiState.confirmationPassword,
                    onValueChange = { accountDeletionDialogViewModel.updateConfirmationPassword(it) },
                    label = "Contraseña",
                    isFocused = false,
                    onFocusChange = {},
                    supportingText = uiState.passwordConfirmationError,
                    isDarkTheme = isDarkTheme,
                    isPasswordField = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                LoadingButton(
                    text = "Eliminar cuenta",
                    state = uiState.deleteAccountResult,
                    onClick = {
                        accountDeletionDialogViewModel.deleteAccount()
                    },
                    onAnimationComplete = {
                        if (uiState.deleteAccountResult is UiResult.Success) {
                            onDeleteAccountSuccess()
                        } else if (uiState.deleteAccountResult is UiResult.Error) {
                            accountDeletionDialogViewModel.resetDeleteAccountState()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    idleBackgroundColor = ErrorLight,
                    successBackgroundColor = ErrorLight,
                    errorBackgroundColor = ErrorLight,
                    textComponent = {
                        Text(
                            text = "Eliminar cuenta",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Cancelar",
                        color = if (isDarkTheme) Color.White else Color.Black
                    )
                }
            }
        }
    }
}