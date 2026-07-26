package com.develofer.opositate.feature.calendar.presentation.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.develofer.opositate.R
import com.develofer.opositate.feature.calendar.presentation.components.CalendarContent
import com.develofer.opositate.feature.calendar.presentation.viewmodel.CalendarViewModel
import com.develofer.opositate.main.MainViewModel

@Composable
fun CalendarScreen(
    isDarkTheme: Boolean,
    mainViewModel: MainViewModel = hiltViewModel(),
    calendarViewModel: CalendarViewModel = hiltViewModel()
) {
    mainViewModel.showSystemUI()
    val calendarState by calendarViewModel.uiState.collectAsState()
    val weekConfiguration by calendarViewModel.weekConfiguration.collectAsState()
    val screenTitle = stringResource(id = R.string.calendar_screen__app_bar_title__calendar)
    LaunchedEffect(Unit) { mainViewModel.setAppBarTitle(screenTitle) }
    CalendarContent (
        yearMonth = calendarState.yearMonth,
        dates = calendarState.dates,
        onDateClick = { date ->

        },
        onPreviousMonthClicked = { calendarViewModel.toPreviousMonth() },
        onNextMonthClicked = { calendarViewModel.toNextMonth() },
        isDarkTheme = isDarkTheme,
        weekConfiguration = weekConfiguration
    )
}