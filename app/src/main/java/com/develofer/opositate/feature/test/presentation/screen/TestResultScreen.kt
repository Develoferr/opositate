package com.develofer.opositate.feature.test.presentation.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.aay.compose.baseComponents.model.LegendPosition
import com.aay.compose.donutChart.PieChart
import com.aay.compose.donutChart.model.PieChartData
import com.develofer.opositate.R
import com.develofer.opositate.feature.test.presentation.model.TestResultUiState
import com.develofer.opositate.feature.test.presentation.viewmodel.TestResultViewModel
import com.develofer.opositate.ui.theme.Gray600
import com.develofer.opositate.utils.StringConstants.EMPTY_STRING
import com.develofer.opositate.utils.StringConstants.TWO_DECIMALS_FORMAT
import com.develofer.opositate.utils.StringConstants.TWO_DIGITS_FORMAT
import java.util.Locale

@Composable
fun TestResultScreen(
    testResultId: Int,
    testTypeId: Int,
    difficultId: Int?,
    groupId: Int?,
    abilityId: Int?,
    taskId: Int?,
    testResultViewModel: TestResultViewModel = hiltViewModel(),
    isDarkTheme: Boolean
) {
    val uiState by testResultViewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isTestActive) {
        testResultViewModel.getTestResult(testResultId, testTypeId, difficultId, groupId, abilityId, taskId)
    }

    uiState.testResult?.let { testResult ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            if (uiState.currentQuestionIndex > -1) {
                TestHeader(uiState.currentQuestionIndex, testResult.questions.size, testResult.completionTime)
                CurrentQuestionContent(uiState, isDarkTheme)
            } else {
                TestStatisticsContent(uiState, testResult.completionTime)
            }
            NavigationButtons(
                currentQuestionIndex = uiState.currentQuestionIndex,
                totalQuestions = testResult.questions.size,
                onPrevious = { testResultViewModel.changeQuestion(false) },
                onNext = { testResultViewModel.changeQuestion(true) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun TestHeader(currentQuestionIndex: Int, totalQuestions: Int, completionTime: Int?) {
    val showHeader = currentQuestionIndex > -1
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val questionText = if (showHeader) stringResource(
            R.string.number_indicator_format,
            currentQuestionIndex + 1, totalQuestions
        ) else EMPTY_STRING
        Text(
            text = questionText,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        val timerText = if (showHeader) {
            stringResource(id = R.string.time_format,
                String.format(Locale.getDefault(), TWO_DIGITS_FORMAT, (completionTime ?: 0) / 60),
                String.format(Locale.getDefault(), TWO_DIGITS_FORMAT, (completionTime ?: 0) % 60)
            )
        } else EMPTY_STRING
        Text(
            text = timerText,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
fun CurrentQuestionContent(uiState: TestResultUiState, isDarkTheme: Boolean) {
    uiState.testResult?.questions?.get(uiState.currentQuestionIndex)?.let { currentQuestion ->
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
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(items = currentQuestion.options) { index, option ->
                val borderColor by animateColorAsState(
                    targetValue = when {
                        (currentQuestion.correctAnswer == index) && (currentQuestion.selectedAnswer == index) && isDarkTheme -> MaterialTheme.colorScheme.primary
                        (currentQuestion.correctAnswer == index) && (currentQuestion.selectedAnswer == index) && !isDarkTheme -> MaterialTheme.colorScheme.secondary
                        (currentQuestion.correctAnswer == index) && (currentQuestion.selectedAnswer != index) -> MaterialTheme.colorScheme.secondary
                        (currentQuestion.correctAnswer != index) && (currentQuestion.selectedAnswer == index) -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.outlineVariant
                    }, label = EMPTY_STRING
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(2.dp, borderColor)
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
}

@Composable
fun TestStatisticsContent(uiState: TestResultUiState, completionTime: Int) {
    val numCorrect = uiState.testResult?.questions?.count { it.selectedAnswer == it.correctAnswer }
    val numIncorrect = uiState.testResult?.questions?.count { it.selectedAnswer != it.correctAnswer && it.selectedAnswer != null }
    val numUnanswered = uiState.testResult?.questions?.count { it.selectedAnswer == null }

    Column(
        modifier = Modifier
            .padding(start = 16.dp)
            .padding(end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(uiState.testResult?.name ?: EMPTY_STRING, fontWeight = FontWeight.Bold, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(20.dp))
        Row {
            Text(stringResource(R.string.test_result_screen__text__score), fontWeight = FontWeight.Bold, fontSize = 28.sp)
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                String.format(
                    Locale.getDefault(),
                    TWO_DECIMALS_FORMAT,
                    uiState.testResult?.score?.times(10) ?: 0.0f),
                fontWeight = FontWeight.Bold, fontSize = 32.sp)
        }
        Text(
            stringResource(R.string.test_result_screen__text__time_taken) +
                stringResource(
                    id = R.string.time_format,
                    String.format(Locale.getDefault(), TWO_DIGITS_FORMAT, (completionTime) / 60),
                    String.format(Locale.getDefault(), TWO_DIGITS_FORMAT, (completionTime) % 60)
                )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            buildAnnotatedString {
                withStyle(style = SpanStyle(fontSize = 12.sp)) {
                    append(stringResource(R.string.test_result_screen__text__question_correct))
                }
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                    append(
                        stringResource(
                            id = R.string.number_indicator_format_int,
                            numCorrect ?: 0, uiState.testResult?.questions?.size ?: 0
                        ),
                    )
                }
            }
        )
        Text(
            buildAnnotatedString {
                withStyle(style = SpanStyle(fontSize = 12.sp)) {
                    append(stringResource(R.string.test_result_screen__text__question_incorrect))
                }
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.error)) {
                    append(
                        stringResource(
                            id = R.string.number_indicator_format_int,
                            numIncorrect ?: 0, uiState.testResult?.questions?.size ?: 0
                        ),
                    )
                }
            }
        )
        Text(
            buildAnnotatedString {
                withStyle(style = SpanStyle(fontSize = 12.sp)) {
                    append(stringResource(R.string.test_result_screen__text__question_unanswered))
                }
                withStyle(style = SpanStyle(fontSize = 16.sp, color = Gray600)) {
                    append(
                        stringResource(
                            id = R.string.number_indicator_format_int,
                            numUnanswered ?: 0, uiState.testResult?.questions?.size ?: 0
                        ),
                    )
                }
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        PieChart(numCorrect, numIncorrect, numUnanswered)
    }

}

@Composable
fun NavigationButtons(
    modifier: Modifier = Modifier,
    currentQuestionIndex: Int,
    totalQuestions: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Spacer(modifier = modifier)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (currentQuestionIndex > -1) {
            IconButton(onClick = onPrevious) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.test_solving_screen__content_description_before_button))
            }
        } else {
            Spacer(modifier = Modifier.width(48.dp))
        }
        if (currentQuestionIndex < totalQuestions - 1) {
            IconButton(onClick = onNext) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, stringResource(R.string.test_solving_screen__content_description__after_button))
            }
        } else {
            Spacer(modifier = Modifier.width(48.dp))
        }
    }
}

@Composable
fun PieChart(numCorrect: Int?, numIncorrect: Int?, numUnanswered: Int?) {

    val testPieChartData: List<PieChartData> = listOf(
        PieChartData(
            partName = "Correct",
            data = numCorrect?.toDouble() ?: 0.0,
            color = Color(0xFF22A699),
        ),
        PieChartData(
            partName = "Incorrect",
            data = numIncorrect?.toDouble() ?: 0.0,
            color = Color(0xFFF24C3D),
        ),
        PieChartData(
            partName = "Unanswered",
            data = numUnanswered?.toDouble() ?: 0.0,
            color = Gray600,
        ),
    )

    Spacer(modifier = Modifier.height(16.dp))
    PieChart(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.8f),
        pieChartData = testPieChartData,
        ratioLineColor = MaterialTheme.colorScheme.outlineVariant,
        textRatioStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground),
        outerCircularColor = MaterialTheme.colorScheme.onBackground,
        descriptionStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground),
        legendPosition = LegendPosition.BOTTOM
    )
}


