package com.develofer.opositate.feature.test.presentation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.develofer.opositate.R
import com.develofer.opositate.feature.test.presentation.component.TestContent
import com.develofer.opositate.feature.test.presentation.viewmodel.TestViewModel
import com.develofer.opositate.main.MainViewModel
import com.develofer.opositate.main.components.common.SuccessDialog
import com.develofer.opositate.main.data.provider.TestType

@Composable
fun TestScreen(
    navigateToTestSolvingGeneralTest: (testTypeId: Int, difficultId: Int) -> Unit,
    navigateToTestSolvingGroupAbilityTest: (testTypeId: Int, difficultId: Int, groupId: Int) -> Unit,
    navigateToTestSolvingAbilityTest: (testTypeId: Int, difficultId: Int, abilityId: Int) -> Unit,
    navigateToTestSolvingTaskTest: (testTypeId: Int, difficultId: Int, abilityId: Int, taskId: Int) -> Unit,
    isDarkTheme: Boolean,
    mainViewModel: MainViewModel = hiltViewModel(),
    testViewModel: TestViewModel = hiltViewModel()
) {
    mainViewModel.showSystemUI()
    val screenTitle = stringResource(id = R.string.test_screen__app_bar_title__test)
    LaunchedEffect(Unit) { mainViewModel.setAppBarTitle(screenTitle) }

    var showNewTestDialog by remember { mutableStateOf(false) }
    var selectedDifficultId by remember { mutableIntStateOf(0) }
    var selectedGroupId by remember { mutableIntStateOf(0) }
    var selectedAbilityId by remember { mutableIntStateOf(0) }
    var selectedTaskId by remember { mutableIntStateOf(0) }
    var selectedTestName by remember { mutableStateOf("") }
    var selectedTestType by remember { mutableStateOf(TestType.GENERAL) }


    val testList by testViewModel.testAsks.collectAsState()
    TestContent(
        asksByGroup = testList,
        onClickItem = { difficultId, groupId, abilityId, taskId, testName, testType ->
            selectedDifficultId = difficultId
            selectedGroupId = groupId
            selectedAbilityId = abilityId
            selectedTaskId = taskId
            selectedTestName = testName
            selectedTestType = testType
            showNewTestDialog = true
        },
        isDarkTheme = isDarkTheme
    )
    if (showNewTestDialog) {
        SuccessDialog(
            modifier = Modifier.clip(shape = RoundedCornerShape(13.dp)),
            isDialogVisible = showNewTestDialog,
            onDismiss = { showNewTestDialog = false },
            title = { Text(text = "Empempezar nuevo test") },
            content = {
                val options = listOf(
                    "Fácil" to 0,
                    "Media" to 1,
                    "Difícil" to 2
                )
                Column {
                    Text(
                        text = buildAnnotatedString {
                            append(if (selectedTestType == TestType.GENERAL) "Vas a comenzar un test " else "Vas a comenzar un test sobre ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(selectedTestName)
                            }
                        },
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                    Text(text = "Elige la dificultad del test")
                    Spacer(modifier = Modifier.padding(8.dp))
                    options.forEach { (label, id) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedDifficultId = id
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedDifficultId == id,
                                onClick = {
                                    selectedDifficultId = id
                                }
                            )
                            Text(
                                text = label,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }

            },
            confirmButton = {
                TextButton(onClick = {
                    showNewTestDialog = false

                    when (selectedTestType) {
                        TestType.GENERAL -> navigateToTestSolvingGeneralTest(0, selectedDifficultId)
                        TestType.GROUP -> navigateToTestSolvingGroupAbilityTest(1, selectedDifficultId, selectedGroupId)
                        TestType.ABILITY -> navigateToTestSolvingAbilityTest(2, selectedDifficultId, selectedAbilityId)
                        TestType.TASK -> navigateToTestSolvingTaskTest(3, selectedDifficultId, selectedAbilityId, selectedTaskId)
                        TestType.CUSTOM -> {  }
                    }
                }) {
                    Text(
                        text = "Empezar"
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewTestDialog = false }) {
                    Text(
                        "Cancelar"
                    )
                }
            }
        )
    }
}