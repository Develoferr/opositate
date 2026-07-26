package com.develofer.opositate.feature.test.presentation.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.develofer.opositate.R
import com.develofer.opositate.feature.test.presentation.viewmodel.TestSolvingViewModel
import com.develofer.opositate.main.MainViewModel
import com.develofer.opositate.main.data.provider.TestType
import com.develofer.opositate.utils.StringConstants.TWO_DIGITS_FORMAT
import kotlinx.coroutines.delay
import java.util.Locale
import kotlin.time.Duration.Companion.seconds

@Composable
fun TestSolvingScreen(
    testTypeId: Int,
    difficultId: Int?,
    groupId: Int?,
    abilityId: Int?,
    taskId: Int?,
    testSolvingViewModel: TestSolvingViewModel = hiltViewModel(),
    isDarkTheme: Boolean,
    mainViewModel: MainViewModel,
    navigateToTestResultGeneralTest: (testTypeId: Int, difficultId: Int, testResultId: Int) -> Unit,
    navigateToTestResultGroupAbilityTest: (testTypeId: Int, difficultId: Int, groupId: Int, testResultId: Int) -> Unit,
    navigateToTestResultAbilityTest: (testTypeId: Int, difficultId: Int, abilityId: Int, testResultId: Int) -> Unit,
    navigateToTestResultTaskTest: (testTypeId: Int, difficultId: Int, abilityId: Int, taskId: Int, testResultId: Int) -> Unit
) {
    val uiState by testSolvingViewModel.uiState.collectAsState()
    val maxTime = uiState.test?.maxTime
    val timeCount = uiState.timeCount

    val testFinishModeCondition = if (maxTime == 0) true
                    else timeCount < (maxTime ?: 0)
    mainViewModel.hideSystemUI()
    LaunchedEffect(uiState.isTestActive) {
        testSolvingViewModel.getTest(
            testTypeId,
            difficultId,
            groupId,
            abilityId,
            taskId
        )
        if (uiState.isTestActive) {
            while (testFinishModeCondition) {
                delay(1.seconds)
                testSolvingViewModel.incrementTime()
            }
            if (timeCount == maxTime) testSolvingViewModel.toggleTestActive(false)
        }
    }

    if (uiState.showStartDialog) {
        Dialog(onDismissRequest = { }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.test_solving_screen__text_title).uppercase(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Text(
                        text = stringResource(R.string.test_solving_screen__text_description),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Button(
                        onClick = {
                            testSolvingViewModel.toggleShowStartDialogVisibility(false)
                            testSolvingViewModel.toggleTestActive(true)
                        }
                    ) {
                        Text(stringResource(R.string.test_solving_screen__text_btn__start).uppercase())
                    }
                }
            }
        }
    }

    if (uiState.showPauseDialog) {
        Dialog(onDismissRequest = { }) {
            Card(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.test_solving_screen__text__pause_title),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 24.dp),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = stringResource(R.string.test_solving_screen__text__pause_body),
                        modifier = Modifier.padding(vertical = 24.dp),
                        textAlign = TextAlign.Center
                    )
                    Button(
                        onClick = {
//                                testSolvingViewModel.saveProgress()
//                                showPauseDialog = false
//                                isTestActive = false
                        }
                    ) {
                        Text(stringResource(R.string.test_solving_screen__text__save_progress))
                    }
                    Button(
                        onClick = {
                            testSolvingViewModel.toggleShowPauseDialogVisibility(false)
                            testSolvingViewModel.toggleTestActive(true)
                        }
                    ) {
                        Text(stringResource(R.string.test_solving_screen__text__continue))
                    }
                }
            }
        }
    }

    uiState.test?.let {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(
                        id = R.string.number_indicator_format,
                        (uiState.currentQuestionIndex + 1),
                        uiState.test?.questions?.size ?: 0
                    ),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                if (maxTime != null) {
                    Text(
                        text = stringResource(id = R.string.time_format,
                            String.format(Locale.getDefault(), TWO_DIGITS_FORMAT, timeCount / 60),
                            String.format(Locale.getDefault(), TWO_DIGITS_FORMAT, timeCount % 60)
                        ),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (maxTime == 0) MaterialTheme.colorScheme.secondary
                        else
                            if (timeCount > (maxTime * 0.84)) Color.Red
                            else MaterialTheme.colorScheme.secondary
                    )
                }
                IconButton(onClick = {
                    testSolvingViewModel.toggleTestActive(false)
                    testSolvingViewModel.toggleShowPauseDialogVisibility(true)
                }) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(id = R.drawable.ic_pause),
                        contentDescription = stringResource(R.string.test_solving_screen__content_description_before_button)
                    )
                }
            }

            uiState.test?.questions?.get(uiState.currentQuestionIndex)?.let { currentQuestion ->
                Text(
                    text = currentQuestion.question,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    textAlign = TextAlign.Center
                )

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items = currentQuestion.options) { option ->
                        val isSelected = uiState.test?.questions?.get(uiState.currentQuestionIndex)?.selectedAnswer == currentQuestion.options.indexOf(option)
                        val borderColor by animateColorAsState(
                            targetValue = if (isSelected) {
                                if (isDarkTheme) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.secondary
                            } else MaterialTheme.colorScheme.outline
                            , label = "border color"
                        )
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    testSolvingViewModel.updateSelectedAnswer(
                                        currentQuestion.options.indexOf(option)
                                    )
                                },
                            border = BorderStroke(
                                if (isSelected && !isDarkTheme) 2.dp else 1.dp,
                                borderColor)
                        ) {
                            Text(
                                text = option,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (uiState.currentQuestionIndex > 0) {
                    IconButton(
                        onClick = { testSolvingViewModel.updateQuestionIndex(false) }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack,
                            stringResource(R.string.test_solving_screen__content_description_before_button))
                    }
                } else {
                    Spacer(modifier = Modifier.width(48.dp))
                }
                uiState.test?.questions?.size?.let { testSize ->
                    if (uiState.currentQuestionIndex == testSize - 1) {
                        Button(
                            onClick = {
                                testSolvingViewModel.toggleTestActive(false)
                                testSolvingViewModel.correctTest {
                                    val test = uiState.test
                                    if (test != null) {
                                        when (test.testType) {
                                            TestType.GENERAL -> navigateToTestResultGeneralTest(0, difficultId ?: 0, 0)
                                            TestType.GROUP -> navigateToTestResultGroupAbilityTest(1, difficultId ?: 0, groupId ?: 0, 0)
                                            TestType.ABILITY -> navigateToTestResultAbilityTest(2, difficultId ?: 0, abilityId ?: 0, 0)
                                            TestType.TASK -> navigateToTestResultTaskTest(3, difficultId ?: 0, abilityId ?: 0, taskId ?: 0, 0)
                                            TestType.CUSTOM -> {}
                                        }
                                    }
                                }
                            }
                        ) {
                            Text(stringResource(R.string.test_solving_screen__text_btn__finish).uppercase())
                        }
                    }

                    if (uiState.currentQuestionIndex < testSize - 1) {
                        IconButton(
                            onClick = { testSolvingViewModel.updateQuestionIndex(true) }
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowForward,
                                stringResource(R.string.test_solving_screen__content_description__after_button))
                        }
                    } else {
                        Spacer(modifier = Modifier.width(48.dp))
                    }
                }
            }
        }
    }
}