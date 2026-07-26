package com.develofer.opositate.feature.login.presentation.screen

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
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
import com.develofer.opositate.feature.login.presentation.model.RegisterUiState
import com.develofer.opositate.feature.login.presentation.utils.KeyboardAwareScreen
import com.develofer.opositate.feature.login.presentation.viewmodel.RegisterViewModel
import com.develofer.opositate.main.data.model.UiResult
import com.develofer.opositate.ui.theme.OpositateTheme

@Composable
fun RegisterScreen(
    navigateToLogin: () -> Unit,
    isDarkTheme: Boolean,
    registerViewModel: RegisterViewModel =  hiltViewModel()
) {
    var isKeyboardVisible by remember { mutableStateOf(false) }
    ViewCompat.setOnApplyWindowInsetsListener(LocalView.current) { _, insets ->
        isKeyboardVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
        insets
    }
    val focusManager = LocalFocusManager.current

    val uiState by registerViewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize().background(MaterialTheme.colorScheme.background)
            .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) }
    ) {
        KeyboardAwareScreen()
        CustomLoginLogo(
            isDarkTheme = isDarkTheme,
            isKeyboardVisible = isKeyboardVisible,
            modifier = Modifier.align(Alignment.TopCenter)
        )
        RegisterContent(
            uiState = uiState,
            isDarkTheme = isDarkTheme,
            isKeyboardVisible = isKeyboardVisible,
            registerViewModel = registerViewModel,
            navigateToLogin = { navigateToLogin() },
            clearFocus = { focusManager.clearFocus() },
            registerState = uiState.registerState,
            cleanUpState = { registerViewModel.cleanUpState() }
        )
    }
}

@Composable
private fun RegisterContent(
    uiState: RegisterUiState,
    isDarkTheme: Boolean,
    isKeyboardVisible: Boolean,
    registerViewModel: RegisterViewModel,
    navigateToLogin: () -> Unit,
    clearFocus: () -> Unit,
    registerState: UiResult,
    cleanUpState: () -> Unit
) {
    val animatedPaddingTop by animateDpAsState(
        targetValue  = if (isKeyboardVisible) 50.dp else if (isDarkTheme) 280.dp else 240.dp,
        label = "padding top"
    )
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp).padding(top = animatedPaddingTop),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LoginHeader(
            isDarkTheme,
            R.string.register_screen__title_text__register,
            R.string.register_screen__text__journey_begins
        )
        RegisterFields(uiState, registerViewModel, isDarkTheme)
        RegisterButtons(isDarkTheme, registerViewModel, navigateToLogin, clearFocus, registerState, cleanUpState)
    }
}

@Composable
fun RegisterFields(uiState: RegisterUiState, registerViewModel: RegisterViewModel, isDarkTheme: Boolean) {
    CustomLoginTextField(
        value = uiState.username, onValueChange = {
            registerViewModel.onUsernameChanged(it)
            registerViewModel.validateUsername()
        },
        label = stringResource(id = R.string.register_screen__label_text_field__user).uppercase(), isFocused = uiState.isUsernameFocused,
        onFocusChange = { registerViewModel.onUsernameFocusChanged(it) },
        supportingText = uiState.usernameValidateFieldError, isDarkTheme = isDarkTheme,
        haveToolTip = true, toolTipText = stringResource(id = R.string.register_screen__user_text_field__tool_tip)
    )

    CustomLoginTextField(
        value = uiState.email, onValueChange = {
            registerViewModel.onEmailChanged(it)
            registerViewModel.validateEmail()
        }, label = stringResource(id = R.string.register_screen__label_text_field__email).uppercase(), isFocused = uiState.isEmailFocused,
        onFocusChange = { registerViewModel.onEmailFocusChanged(it) },
        supportingText = uiState.emailValidateFieldError, isDarkTheme = isDarkTheme
    )

    CustomLoginTextField(
        value = uiState.password, onValueChange = {
            registerViewModel.onPasswordChanged(it)
            registerViewModel.validatePassword()
        },
        label = stringResource(id = R.string.register_screen__label_text_field__password).uppercase(), isFocused = uiState.isPasswordFocused,
        onFocusChange = { registerViewModel.onPasswordFocusChanged(it) }, isPasswordField = true,
        supportingText = uiState.passwordValidateFieldError, isDarkTheme = isDarkTheme
    )
}

@Composable
fun RegisterButtons(
    isDarkTheme: Boolean,
    registerViewModel: RegisterViewModel,
    navigateToLogin: () -> Unit,
    clearFocus: () -> Unit,
    registerState: UiResult,
    cleanUpState: () -> Unit
) {
    Spacer(modifier = Modifier.height(26.dp))
    LoginLoadingButton(
        modifier = Modifier.padding(horizontal = 64.dp),
        loadingState = registerState,
        onclick = {
            registerViewModel.register()
            clearFocus()
        }, onAnimationComplete = {
            if (registerState == UiResult.Success) navigateToLogin()
            cleanUpState()
        }, isDarkTheme = isDarkTheme,
        text = stringResource(id = R.string.register_screen__text_btn__register).uppercase()
    )
    Spacer(modifier = Modifier.height(12.dp))
    CustomLoginTextButton(
        onClick = { navigateToLogin() },
        text = stringResource(id = R.string.register_screen__text_btn__already_have_account),
        isDarkTheme = isDarkTheme
    )
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun RegisterPreview() {
    OpositateTheme(darkTheme = true) {
        RegisterScreen({}, isDarkTheme = true)
    }
}