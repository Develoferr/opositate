package com.develofer.opositate.feature.profile.presentation.screen

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.develofer.opositate.R
import com.develofer.opositate.feature.profile.presentation.components.ConfirmDialog
import com.develofer.opositate.feature.profile.presentation.components.FieldValidationType
import com.develofer.opositate.feature.profile.presentation.model.ProfileDialogType
import com.develofer.opositate.feature.profile.presentation.viewmodel.ProfileViewModel
import com.develofer.opositate.main.MainViewModel
import com.develofer.opositate.main.components.common.CustomBaseAlertDialog
import com.develofer.opositate.main.components.common.GradientElevatedAssistChip
import com.develofer.opositate.main.coordinator.DialogState
import com.develofer.opositate.main.data.model.UiResult
import com.develofer.opositate.ui.theme.OpositateTheme

@Composable
fun ProfileScreen(
    isDarkTheme: Boolean,
    mainViewModel: MainViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel(),
    navigateToLogin: () -> Unit,
) {
    SetupSystemUI(mainViewModel)

    val uiState = profileViewModel.uiState.collectAsState()

    HandleDialog(
        dialogState = uiState.value.dialogStateCoordinator.dialogState.collectAsState().value,
        closeDialog = { profileViewModel.hideDialog() },
        updateUserName = { newUserName -> profileViewModel.updateUserName(newUserName) },
        updateEmail = { newEmail -> profileViewModel.updateEmail(newEmail) },
        updatePassword = { email -> profileViewModel.updatePassword(email) },
        logOut = {
            profileViewModel.logOut()
            navigateToLogin()
        },
        updateEmailUiResult = uiState.value.updateEmailResult,
        updatePasswordUiResult = uiState.value.updatePasswordResult,
        updateUserNameUiResult = uiState.value.updateUserNameResult,
        cleanUpState = { profileViewModel.cleanUpState() },
        isDarkTheme = isDarkTheme
    )

    Column(
        modifier = Modifier.fillMaxSize().background(if (isDarkTheme) Color.Unspecified else Color.White),
    ) {
        var selectedTabIndex by remember { mutableIntStateOf(0) }

        TabRow(
            selectedTabIndex,
            isDarkTheme,
            onTabSelected = { index -> selectedTabIndex = index }
        )
        when (selectedTabIndex) {
            0 -> AccountSection(
                isDarkTheme = isDarkTheme,
                userName = uiState.value.userName,
                userEmail = uiState.value.userEmail,
                showDialog = { dialogType -> profileViewModel.showDialog(dialogType) },
            )
            1 -> GeneralStatisticsSection(
                isDarkTheme = isDarkTheme
            )
            2 -> ProgressSection(
                scoresByGroup = uiState.value.scoresByGroup,
                isDarkTheme = isDarkTheme
            )
            3 -> AbilityScoresSection(
                items = uiState.value.userScores.scores
            )
            4 -> AchievementsSection(
                isDarkTheme = isDarkTheme
            )
        }
    }
}

@Composable
fun HandleDialog(
    dialogState: DialogState<ProfileDialogType>,
    closeDialog: () -> Unit,
    updateUserName: (String) -> Unit,
    updateEmail: (String) -> Unit,
    updatePassword: (String) -> Unit,
    logOut: () -> Unit,
    updateEmailUiResult: UiResult,
    updatePasswordUiResult: UiResult,
    updateUserNameUiResult: UiResult,
    cleanUpState: () -> Unit,
    isDarkTheme: Boolean
) {
    if (dialogState.isVisible) {
        when (dialogState.dialogType) {
            ProfileDialogType.SIGN_OUT -> SignOutDialog(closeDialog, logOut)
            ProfileDialogType.UPDATE_PASSWORD -> UpdatePasswordDialog(closeDialog, updatePassword, updatePasswordUiResult, cleanUpState, isDarkTheme)
            ProfileDialogType.UPDATE_USERNAME -> UpdateUsernameDialog(closeDialog, updateUserName, updateUserNameUiResult, cleanUpState, isDarkTheme)
            ProfileDialogType.UPDATE_EMAIL -> UpdateEmailDialog(closeDialog, updateEmail, updateEmailUiResult, cleanUpState, isDarkTheme)
            null -> {}
        }
    }
}

@Composable
fun SignOutDialog(closeDialog: () -> Unit, signOut: () -> Unit) {
    CustomBaseAlertDialog(
        title = { Text("Cerrando Sesión") },
        content = { Text("¿Estás seguro de que deseas cerrar sesión?") },
        confirmButton = {
            TextButton(onClick = { signOut() }) {
                Text("Cerrar Sesión".uppercase())
            }
        },
        dismissButton = {
            TextButton(onClick = { closeDialog() }) {
                Text("Cancelar".uppercase())
            }
        },
        onDismiss = { closeDialog() }
    )
}

@Composable
fun UpdatePasswordDialog(
    closeDialog: () -> Unit,
    updatePassword: (String) -> Unit,
    updatePasswordUiResult: UiResult,
    cleanUpState: () -> Unit,
    isDarkTheme: Boolean
) {
    ConfirmDialog(
        title = "Reestablecer contraseña",
        firstFieldLabel = "Correo",
        secondFieldLabel = "Confirmar correo",
        confirmButtonText = "Reestablecer".uppercase(),
        cancelButtonText = "Cancelar",
        firstFieldType = FieldValidationType.EMAIL,
        secondFieldType = FieldValidationType.EMAIL,
        onConfirm = { email -> updatePassword(email) },
        onCancel = { closeDialog() },
        onDismissRequest = { closeDialog() },
        uiResult = updatePasswordUiResult,
        onAnimationComplete = { cleanUpState() },
        isDarkTheme = isDarkTheme,
        explainingText = "Ingrese su dirección de correo\nEnviaremos un mensaje con un enlace para reestablecer su contraseña"
    )
}

@Composable
fun UpdateUsernameDialog(
    closeDialog: () -> Unit,
    updateUserName: (String) -> Unit,
    updateUserNameUiResult: UiResult,
    cleanUpState: () -> Unit,
    isDarkTheme: Boolean
) {
    ConfirmDialog(
        title = "Actualiza tu nombre de usuario",
        firstFieldLabel = "Nuevo Usuario",
        secondFieldLabel = "Confirmar Usuario",
        confirmButtonText = "Actualizar".uppercase(),
        cancelButtonText = "Cancelar",
        firstFieldType = FieldValidationType.TEXT,
        secondFieldType = FieldValidationType.TEXT,
        onConfirm = { newUserName -> updateUserName(newUserName) },
        onCancel = { closeDialog() },
        onDismissRequest = { closeDialog() },
        uiResult = updateUserNameUiResult,
        onAnimationComplete = { cleanUpState() },
        isDarkTheme = isDarkTheme
    )
}

@Composable
fun UpdateEmailDialog(
    closeDialog: () -> Unit,
    updateEmail: (String) -> Unit,
    updateEmailUiResult: UiResult,
    cleanUpState: () -> Unit,
    isDarkTheme: Boolean
) {
    ConfirmDialog(
        title = "Actualizar Correo",
        firstFieldLabel = "Nuevo Correo",
        secondFieldLabel = "Confirmar Correo",
        confirmButtonText = "Actualizar".uppercase(),
        cancelButtonText = "Cancelar",
        firstFieldType = FieldValidationType.EMAIL,
        secondFieldType = FieldValidationType.EMAIL,
        onConfirm = { newEmail -> updateEmail(newEmail) },
        onCancel = { closeDialog() },
        onDismissRequest = { closeDialog() },
        uiResult = updateEmailUiResult,
        onAnimationComplete = { cleanUpState() },
        isDarkTheme = isDarkTheme
    )
}

@Composable
private fun SetupSystemUI(mainViewModel: MainViewModel) {
    mainViewModel.showSystemUI()
    val screenTitle = stringResource(id = R.string.profile_screen__app_bar_title__profile)
    LaunchedEffect(Unit) { mainViewModel.setAppBarTitle(screenTitle) }
}

data class TabItem(
    val title: String = "",
    val content: @Composable () -> Unit
)

@Composable
private fun TabRow(
    selectedTabIndex: Int,
    isDarkTheme: Boolean,
    onTabSelected: (Int) -> Unit
) {
    val tabItems = listOf(
        TabItem(
            content = { IconButton(onClick = { onTabSelected(0) }, modifier = Modifier.size(28.dp)) { Icon(modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.onBackground, painter = painterResource(id = R.drawable.ic_tab_profile_3), contentDescription = "Botón mostrar perfil") } }
        ),
        TabItem(
            title = stringResource(id = R.string.profile_screen__title_text__statistics),
            content = { IconButton(onClick = { onTabSelected(1) }, modifier = Modifier.size(28.dp)) { Icon(modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.onBackground, painter = painterResource(id = R.drawable.ic_bar_chart), contentDescription = "Botón mostrar estadísticas") } }
        ),
        TabItem(
            title = stringResource(id = R.string.profile_screen__title_text__scores),
            content = { IconButton(onClick = { onTabSelected(2) }, modifier = Modifier.size(28.dp)) { Icon(modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.onBackground, painter = painterResource(id = R.drawable.ic_line_chart_2), contentDescription = "Botón mostrar puntajes") } }
        ),
        TabItem(
            title = stringResource(id = R.string.profile_screen__title_text__chart),
            content = { IconButton(onClick = { onTabSelected(3) }, modifier = Modifier.size(28.dp)) { Icon(modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.onBackground, painter = painterResource(id = R.drawable.ic_tab_puzzle_piece_2), contentDescription = "Botón mostrar gráfica") } }
        ),
        TabItem(
            content = { IconButton(onClick = { onTabSelected(4) }, modifier = Modifier.size(28.dp)) { Icon(modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.onBackground, painter = painterResource(id = R.drawable.ic_tab_trophy_4), contentDescription = "Botón mostrar logros") } }
        )
    )

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = if (isDarkTheme) 0.dp else 4.dp)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        items(tabItems.size) {
            GradientElevatedAssistChip(
                onClick = { onTabSelected(it) },
                leadingIcon = tabItems[it].content,
                isSelected = selectedTabIndex == it,
                isDarkTheme = isDarkTheme
            )
            if (it != tabItems.size - 1) Spacer(modifier = Modifier.width(16.dp))
        }
    }
    Spacer(modifier = Modifier
        .fillMaxWidth()
        .size(8.dp))
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun ProfileScreenPreview() {
    OpositateTheme(darkTheme = true) {
        ProfileScreen(true, navigateToLogin = { })
    }
}