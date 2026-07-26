package com.develofer.opositate.feature.lesson

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.develofer.opositate.R
import com.develofer.opositate.feature.test.data.model.LessonItem
import com.develofer.opositate.feature.test.presentation.component.StudyItemList
import com.develofer.opositate.main.MainViewModel

@Composable
fun LessonScreen(
    isDarkTheme: Boolean,
    mainViewModel: MainViewModel = hiltViewModel()
) {
    mainViewModel.showSystemUI()
    val screenTitle = stringResource(id = R.string.lesson_screen__app_bar_title__lesson)
    LaunchedEffect(Unit) { mainViewModel.setAppBarTitle(screenTitle) }
    Spacer(modifier = Modifier.size(24.dp))
    val lessonList = listOf(
        LessonItem(1, "Introducción", true),
        LessonItem(2, "Tema 1", true),
        LessonItem(3, "Tema 2", false),
        LessonItem(4, "Tema 3", true),
        LessonItem(5, "Tema 4", true),
        LessonItem(6, "Tema 5", false),
        LessonItem(7, "Tema 6", false),
        LessonItem(8, "Tema 7", false),
        LessonItem(9, "Tema 8", false),
        LessonItem(10, "Tema 9", false),
        LessonItem(11, "Tema 10", false),
        LessonItem(12, "Tema 11", false),
        LessonItem(13, "Tema 12", false),
        LessonItem(14, "Tema 13", false),
    )
    StudyItemList(lessonList, isDarkTheme)
    Spacer(modifier = Modifier.size(32.dp))
}

@Preview(showBackground = true)
@Composable
fun LessonScreenPreview() {
    LessonScreen(isDarkTheme = true)
}