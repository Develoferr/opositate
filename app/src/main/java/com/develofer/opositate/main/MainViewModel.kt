package com.develofer.opositate.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.develofer.opositate.R
import com.develofer.opositate.content.testcatalog.domain.UploadTestCatalogUseCase
import com.develofer.opositate.feature.settings.domain.usecase.GetThemePreferencesUseCase
import com.develofer.opositate.main.data.model.Result
import com.develofer.opositate.main.data.provider.ResourceProvider
import com.develofer.opositate.main.domain.GetUserUseCase
import com.develofer.opositate.main.domain.LogoutUseCase
import com.develofer.opositate.main.navigation.LoginNavigation
import com.develofer.opositate.main.navigation.ProfileNavigation
import com.develofer.opositate.main.navigation.Route
import com.develofer.opositate.main.utils.ThemePreferences
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getThemePreferencesUseCase: GetThemePreferencesUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val uploadTestCatalogUseCase: UploadTestCatalogUseCase,
    resourceProvider: ResourceProvider,
) : ViewModel() {

    private val _isUserNotRetrieved = MutableStateFlow(true)
    val isUserNotRetrieved: StateFlow<Boolean> get() = _isUserNotRetrieved

    private val _isSystemUIVisible = MutableStateFlow(false)
    val isSystemUIVisible: StateFlow<Boolean> get() = _isSystemUIVisible

    private val _appBarTitle = MutableStateFlow(resourceProvider.getString(R.string.profile_screen__app_bar_title__profile))
    val appBarTitle: StateFlow<String> get() = _appBarTitle

    private val _themePreferences = MutableStateFlow(ThemePreferences())
    val themePreferences: StateFlow<ThemePreferences> = _themePreferences.asStateFlow()

    private var currentUser = MutableStateFlow<FirebaseUser?>(null)

    init {
        getUserAuth()
        loadThemePreferences()
    }

    private fun loadThemePreferences() {
        viewModelScope.launch {
            combine(
                getThemePreferencesUseCase.getAutoThemeSelection(),
                getThemePreferencesUseCase.getDarkThemeManual()
            ) { autoThemeSelection, manualDarkTheme ->
                ThemePreferences(
                    isAutoThemeEnabled = autoThemeSelection,
                    isDarkThemeManual = manualDarkTheme
                )
            }.collect { preferences ->
                _themePreferences.value = preferences
            }
        }
    }

    fun setAppBarTitle(title: String) {
        _appBarTitle.value = title
    }

    private fun getUserAuth() {
        viewModelScope.launch {
            when (val result = getUserUseCase()) {
                is Result.Success -> {
                    currentUser.value = result.data
                    _isUserNotRetrieved.value = false
                }
                is Result.Error -> {
                    _isUserNotRetrieved.value = false
                }
                is Result.Loading -> { }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            when (logoutUseCase()) {
                is Result.Success -> {
                    currentUser.value = null
                    _isUserNotRetrieved.value = true
                }
                is Result.Error -> { /* Handle error */ }
                is Result.Loading -> { /* Handle loading */ }
            }
        }
    }

    fun hideSystemUI() {
        _isSystemUIVisible.value = false
    }

    fun showSystemUI() {
        _isSystemUIVisible.value = true
    }

    fun getStartDestination(): Route {
        return if (currentUser.value != null) {
            ProfileNavigation
        } else {
            LoginNavigation
        }
    }

    fun uploadTestCatalog(abilityId: Int = 0) {
        viewModelScope.launch {
            uploadTestCatalogUseCase(abilityId)
        }
    }
}
