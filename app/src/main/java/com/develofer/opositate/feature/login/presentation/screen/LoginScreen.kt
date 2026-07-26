package com.develofer.opositate.feature.login.presentation.screen

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.develofer.opositate.R
import com.develofer.opositate.feature.login.presentation.component.CustomLoginLogo
import com.develofer.opositate.feature.login.presentation.component.CustomLoginTextButton
import com.develofer.opositate.feature.login.presentation.component.CustomLoginTextField
import com.develofer.opositate.feature.login.presentation.component.LoginHeader
import com.develofer.opositate.feature.login.presentation.component.LoginLoadingButton
import com.develofer.opositate.feature.login.presentation.model.LoginUiState
import com.develofer.opositate.feature.login.presentation.utils.KeyboardAwareScreen
import com.develofer.opositate.feature.login.presentation.viewmodel.LoginViewModel
import com.develofer.opositate.feature.profile.presentation.components.ConfirmDialog
import com.develofer.opositate.feature.profile.presentation.components.FieldValidationType
import com.develofer.opositate.main.MainViewModel
import com.develofer.opositate.main.data.model.UiResult
import com.develofer.opositate.utils.StringConstants.EMPTY_STRING

@Composable
fun LoginScreen(
    navigateToRegister: () -> Unit,
    navigateToProfile: () -> Unit,
    isDarkTheme: Boolean,
    mainViewModel: MainViewModel = hiltViewModel(),
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    var isKeyboardVisible by remember { mutableStateOf(false) }
    ViewCompat.setOnApplyWindowInsetsListener(LocalView.current) { _, insets ->
        isKeyboardVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
        insets
    }
    val focusManager = LocalFocusManager.current

    val uiState by loginViewModel.uiState.collectAsState()

    mainViewModel.hideSystemUI()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) }
    ) {
        KeyboardAwareScreen()
        CustomLoginLogo(
            isDarkTheme = isDarkTheme, isKeyboardVisible = isKeyboardVisible,
            modifier = Modifier.align(Alignment.TopCenter)
        )
        LoginContent(
            uiState = uiState, loginViewModel = loginViewModel, isDarkTheme = isDarkTheme,
            isKeyBoardVisible = isKeyboardVisible, clearFocus = { focusManager.clearFocus() },
            onForgotPasswordClick = { loginViewModel.toggleResetPasswordDialogVisibility(true) },
            navigateToRegister = { navigateToRegister() },
            navigateToProfile = { navigateToProfile() }
        )
        LoginResetPasswordDialog(
            showResetPasswordDialog = uiState.showResetPasswordDialog, focusManager = focusManager,
            loginViewModel = loginViewModel,
            hideDialog = { loginViewModel.toggleResetPasswordDialogVisibility(false) },
            updatePassword = { loginViewModel.updatePassword(it) },
            updatePasswordUiResult = uiState.updatePasswordState,
            cleanUpState = { loginViewModel.cleanUpState() },
            isDarkTheme = isDarkTheme
        )
    }
}

@Composable
private fun LoginContent(
    uiState: LoginUiState,
    loginViewModel: LoginViewModel,
    isDarkTheme: Boolean,
    isKeyBoardVisible: Boolean,
    onForgotPasswordClick: () -> Unit,
    clearFocus: () -> Unit,
    navigateToRegister: () -> Unit,
    navigateToProfile: () -> Unit,
) {
    val animatedPaddingTop by animateDpAsState(
        targetValue  = if (isKeyBoardVisible) 50.dp else if (isDarkTheme) 280.dp else 240.dp,
        label = EMPTY_STRING
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = animatedPaddingTop),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LoginHeader(
            isDarkTheme = isDarkTheme,
            titleTextId = R.string.login_screen__title_text__welcome,
            subtitleTextId = R.string.login_screen__text__sign_in
        )
        LoginFields(uiState, loginViewModel, Modifier.align(Alignment.CenterHorizontally), isDarkTheme, onForgotPasswordClick)
        LoginButtons(loginViewModel, isDarkTheme, clearFocus, navigateToRegister, uiState.loginState, navigateToProfile, uiState.googleLoginState)
    }
}

@Composable
private fun LoginFields(
    uiState: LoginUiState, loginViewModel: LoginViewModel, modifier: Modifier,
    isDarkTheme: Boolean, onForgotPasswordClick: () -> Unit
) {
    CustomLoginTextField(
        value = uiState.email,
        onValueChange = {
            loginViewModel.onEmailChanged(it)
            loginViewModel.validateEmail()
        },
        label = stringResource(id = R.string.login_screen__label_text_field__email).uppercase(), isFocused = uiState.isEmailFocused,
        onFocusChange = { loginViewModel.onEmailFocusChanged(it) }, isPasswordField = false,
        supportingText = uiState.emailValidateFieldError, isDarkTheme = isDarkTheme
    )
    CustomLoginTextField(
        value = uiState.password,
        onValueChange = {
            loginViewModel.onPasswordChanged(it)
            loginViewModel.validatePassword()
        },
        label = stringResource(id = R.string.login_screen__label_text_field__password).uppercase(), isFocused = uiState.isPasswordFocused,
        onFocusChange = { loginViewModel.onPasswordFocusChanged(it) }, isPasswordField = true,
        supportingText = uiState.passwordValidateFieldError, isDarkTheme = isDarkTheme
    )
    ForgotPasswordButton(modifier, isDarkTheme, onForgotPasswordClick)
}

@Composable
private fun ForgotPasswordButton(modifier: Modifier, isDarkTheme: Boolean, onForgotPasswordClick: () -> Unit) {
    CustomLoginTextButton(
        onClick = { onForgotPasswordClick() }, isDarkTheme = isDarkTheme, modifier = modifier.offset(y = (-14).dp),
        text = stringResource(id = R.string.login_screen__text__forget_your_password),
    )
}

@Composable
private fun LoginButtons(
    loginViewModel: LoginViewModel,
    isDarkTheme: Boolean,
    clearFocus: () -> Unit = {},
    navigateToRegister: () -> Unit,
    loginState: UiResult,
    navigateToProfile: () -> Unit,
    googleLoginState: UiResult
) {
    Spacer(modifier = Modifier.height(16.dp))
    LoginLoadingButton(
        modifier = Modifier.padding(horizontal = 128.dp),
        loadingState = loginState,
        onclick = {
            loginViewModel.login()
            clearFocus()
        },
        onAnimationComplete = {
            if (loginState == UiResult.Success) navigateToProfile()
            loginViewModel.cleanUpState()
        },
        isDarkTheme = isDarkTheme,
        text = stringResource(id = R.string.login_screen__text_btn__go).uppercase()
    )
    Spacer(modifier = Modifier.height(16.dp))
    LoginLoadingButton(
        modifier = Modifier.padding(horizontal = 16.dp),
        loadingState = googleLoginState,
        onclick = {  },
        onAnimationComplete = {  },
        isDarkTheme = isDarkTheme,
        text = stringResource(id = R.string.login_screen__text_btn__google).uppercase(),
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_google_icon),
                contentDescription = null,
                modifier = Modifier.size(25.dp),
            )
        }
    )
    Spacer(modifier = Modifier.height(12.dp))
    GoToRegisterButton(onClick = { navigateToRegister() }, isDarkTheme)
}

@Composable
private fun GoToRegisterButton(onClick: () -> Unit, isDarkTheme: Boolean) {
    CustomLoginTextButton(
        onClick = { onClick() }, isDarkTheme = isDarkTheme,
        text = stringResource(id = R.string.login_screen__text_btn__new_user).uppercase()
    )
}

@Composable
fun LoginResetPasswordDialog(
    showResetPasswordDialog: Boolean,
    focusManager: FocusManager,
    loginViewModel: LoginViewModel,
    hideDialog: () -> Unit,
    updatePasswordUiResult: UiResult,
    cleanUpState: () -> Unit,
    updatePassword: (String) -> Unit,
    isDarkTheme: Boolean
) {
    if (showResetPasswordDialog) {
        clearFocus(focusManager, loginViewModel)
        ConfirmDialog(
            title = "Reestablecer contraseña",
            firstFieldLabel = "Correo",
            secondFieldLabel = "Confirmar correo",
            confirmButtonText = "Reestablecer",
            cancelButtonText = "Cancelar",
            firstFieldType = FieldValidationType.EMAIL,
            secondFieldType = FieldValidationType.EMAIL,
            onConfirm = { email -> updatePassword(email) },
            onCancel = { hideDialog() },
            onDismissRequest = { hideDialog() },
            uiResult = updatePasswordUiResult,
            isDarkTheme = isDarkTheme,
            onAnimationComplete = {
                cleanUpState()
                hideDialog()
            },
            explainingText = "Ingrese su dirección de correo\nEnviaremos un mensaje con un enlace para reestablecer su contraseña"
        )
    }
}

private fun clearFocus(focusManager: FocusManager, loginViewModel: LoginViewModel) {
    focusManager.clearFocus()
    loginViewModel.onEmailFocusChanged(false)
    loginViewModel.onPasswordFocusChanged(false)
}