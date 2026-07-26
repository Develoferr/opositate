package com.develofer.opositate.main.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.toRoute
import com.develofer.opositate.BuildConfig
import com.develofer.opositate.feature.calendar.presentation.screen.CalendarScreen
import com.develofer.opositate.feature.lesson.LessonScreen
import com.develofer.opositate.feature.login.presentation.screen.LoginScreen
import com.develofer.opositate.feature.login.presentation.screen.RegisterScreen
import com.develofer.opositate.feature.profile.presentation.screen.ProfileScreen
import com.develofer.opositate.feature.settings.presentation.screen.SettingsScreen
import com.develofer.opositate.feature.test.presentation.screen.TestResultScreen
import com.develofer.opositate.feature.test.presentation.screen.TestScreen
import com.develofer.opositate.feature.test.presentation.screen.TestSolvingScreen
import com.develofer.opositate.main.MainViewModel
import com.develofer.opositate.main.components.appbar.CustomAppBar
import com.develofer.opositate.main.components.navbar.CustomBottomNavigationBar
import com.develofer.opositate.ui.theme.OpositateTheme

@Composable
fun AppNavigation(
    navHostController: NavHostController,
    startDestination: Route,
    mainViewModel: MainViewModel = hiltViewModel(),
    appBarTitle: State<String>,
    isDarkTheme: Boolean
) {
    val currentRoute = navHostController.currentBackStackEntryAsState().value?.destination?.route
    OpositateTheme(darkTheme = isDarkTheme, currentRoute = currentRoute) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                bottomBar =  {
                    if (currentRoute in listOf(
                            ProfileNavigation.route,
                            TestNavigation.route,
                            LessonNavigation.route,
                            CalendarNavigation.route,
                            SettingsNavigation.route
                        )
                    ) {
                        CustomBottomNavigationBar(
                            navHostController,
                            isDarkTheme
                        )
                    }
                },
                topBar = {
                    if (currentRoute in listOf(
                            ProfileNavigation.route,
                            TestNavigation.route,
                            LessonNavigation.route,
                            CalendarNavigation.route,
                            SettingsNavigation.route
                        )
                    ) {
                        CustomAppBar(
                            title = appBarTitle,
                            isDarkTheme = isDarkTheme,
                            logout = {
                                mainViewModel.logout()
                                navigateToLogin(navHostController)
                            },
                            uploadTestCatalog = if (BuildConfig.DEBUG) {
                                { mainViewModel.uploadTestCatalog() }
                            } else {
                                null
                            },
                        )
                    }
                }
            ) { paddingValues ->
                NavHost(
                    navHostController,
                    startDestination = startDestination,
                    modifier = Modifier.padding(paddingValues)
                ) {
                    composable<LoginNavigation> {
                        LoginScreen(
                            navigateToRegister = { navHostController.navigate(RegisterNavigation) },
                            navigateToProfile = { navigateToProfile(navHostController) },
                            isDarkTheme = isDarkTheme,
                            mainViewModel = mainViewModel
                        )
                    }
                    composable<RegisterNavigation> {
                        RegisterScreen(
                            navigateToLogin = { navigateToLogin(navHostController) },
                            isDarkTheme = isDarkTheme
                        )
                    }
                    composable<ProfileNavigation> {
                        ProfileScreen(
                            isDarkTheme = isDarkTheme,
                            mainViewModel = mainViewModel,
                            navigateToLogin = { navigateToLogin(navHostController) }
                        )
                    }
                    composable<TestNavigation> {
                        TestScreen(
                            navigateToTestSolvingGeneralTest = { testType, difficultId -> navHostController.navigate(TestSolvingNavigation(testType, difficultId, null, null, null)) },
                            navigateToTestSolvingGroupAbilityTest = { testType, difficultId, groupId -> navHostController.navigate(TestSolvingNavigation(testType, difficultId, groupId, null, null)) },
                            navigateToTestSolvingAbilityTest = { testType, difficultId, abilityId -> navHostController.navigate(TestSolvingNavigation(testType, difficultId, null, abilityId, null)) },
                            navigateToTestSolvingTaskTest = { testType, difficultId, abilityId, taskId -> navHostController.navigate(TestSolvingNavigation(testType, difficultId, null, abilityId, taskId)) },
                            isDarkTheme = isDarkTheme,
                            mainViewModel = mainViewModel
                        )
                    }
                    composable<LessonNavigation> {
                        LessonScreen(
                            isDarkTheme = isDarkTheme,
                            mainViewModel = mainViewModel
                        )
                    }
                    composable<CalendarNavigation> {
                        CalendarScreen(
                            isDarkTheme = isDarkTheme,
                            mainViewModel = mainViewModel
                        )
                    }
                    composable<TestSolvingNavigation> { backStackEntry ->
                        val testSolvingNavigation: TestSolvingNavigation = backStackEntry.toRoute()
                        TestSolvingScreen(
                            isDarkTheme = isDarkTheme,
                            testTypeId = testSolvingNavigation.testTypeId,
                            difficultId = testSolvingNavigation.difficultId,
                            groupId = testSolvingNavigation.groupId,
                            abilityId = testSolvingNavigation.abilityId,
                            taskId = testSolvingNavigation.taskId,
                            mainViewModel = mainViewModel,
                            navigateToTestResultGeneralTest = { testType, difficultId, testResultId -> navHostController.navigate(TestResultNavigation(testType, difficultId, null, null, null, testResultId)) },
                            navigateToTestResultGroupAbilityTest = { testType, difficultId, groupId, testResultId -> navHostController.navigate(TestResultNavigation(testType, difficultId, groupId, null, null, testResultId)) },
                            navigateToTestResultAbilityTest = { testType, difficultId, abilityId, testResultId -> navHostController.navigate(TestResultNavigation(testType, difficultId, null, abilityId, null, testResultId)) },
                            navigateToTestResultTaskTest = { testType, difficultId, abilityId, taskId, testResultId -> navHostController.navigate(TestResultNavigation(testType, difficultId, null, abilityId, taskId, testResultId)) },
                        )
                    }
                    composable<TestResultNavigation> { backStackEntry ->
                        val testResultNavigation: TestResultNavigation = backStackEntry.toRoute()
                        TestResultScreen(
                            testResultId = testResultNavigation.testResultId,
                            testTypeId = testResultNavigation.testTypeId,
                            difficultId = testResultNavigation.difficultId,
                            groupId = testResultNavigation.groupId,
                            abilityId = testResultNavigation.abilityId,
                            taskId = testResultNavigation.taskId,
                            isDarkTheme = isDarkTheme
                        )
                    }
                    composable<SettingsNavigation> {
                        SettingsScreen(
                            isDarkTheme = isDarkTheme,
                            mainViewModel = mainViewModel,
                            navigateToLogin = { navigateToLogin(navHostController) }
                        )
                    }
                }
            }
        }
    }

}

private fun navigateToProfile(navController: NavHostController) {
    navController.navigate(ProfileNavigation) {
        popUpTo(0) { inclusive = true }
        launchSingleTop = true
    }
}

private fun navigateToLogin(navController: NavHostController) {
    navController.navigate(LoginNavigation) {
        popUpTo(0) { inclusive = true }
        launchSingleTop = true
    }
}