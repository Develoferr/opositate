package com.develofer.opositate.feature.settings.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.develofer.opositate.R
import com.develofer.opositate.feature.profile.presentation.components.OpositateCard
import com.develofer.opositate.feature.settings.presentation.component.AccountDeletionDialog
import com.develofer.opositate.feature.settings.presentation.component.SettingsAssistChip
import com.develofer.opositate.feature.settings.presentation.viewmodel.SettingsViewModel
import com.develofer.opositate.main.MainViewModel
import com.develofer.opositate.ui.theme.ErrorLight
import com.develofer.opositate.ui.theme.Gray800
import com.develofer.opositate.ui.theme.Gray900
import com.develofer.opositate.ui.theme.Secondary500

@Composable
fun SettingsScreen(
    isDarkTheme: Boolean,
    mainViewModel: MainViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    navigateToLogin: () -> Unit
) {
    mainViewModel.showSystemUI()
    val screenTitle = stringResource(id = R.string.settings_screen__app_bar_title__settings)
    LaunchedEffect(Unit) { mainViewModel.setAppBarTitle(screenTitle) }

    val mondayStartWeek by settingsViewModel.mondayStartWeek.collectAsState(initial = true)
    val manualThemeEnabled by settingsViewModel.autoThemeSelection.collectAsState(initial = true)
    val isDarkThemeManual by settingsViewModel.darkThemeManual.collectAsState(initial = false)

    HandledDeleteAccountDialog(
        setShowDeleteAccountDialog = { showDialog -> settingsViewModel.setShowDeleteAccountDialog(showDialog) },
        showDeleteAccountDialog = settingsViewModel.showDeleteAccountDialog,
        isDarkTheme = isDarkTheme,
        navigateToLogin = navigateToLogin
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDarkTheme) Color.Unspecified else Color.White)
    ) {
        CalendarCustomizationZone(
            isDarkTheme = isDarkTheme,
            isMondayStart = mondayStartWeek,
            onWeekStartChanged = { settingsViewModel.setMondayStartWeek(it) }
        )
        ThemeCustomizationZone(
            isDarkTheme = isDarkTheme,
            manualThemeEnabled = manualThemeEnabled,
            isDarkThemeManual = isDarkThemeManual,
            onManualThemeToggle = { settingsViewModel.setAutoThemeSelection(!it) },
            onThemeManualChanged = { settingsViewModel.setDarkThemeManual(it) }
        )
        AccountDangerZone(
            isDarkTheme = isDarkTheme,
            setShowDeleteAccountDialog = { settingsViewModel.setShowDeleteAccountDialog(true) }
        )
    }
}

@Composable
fun HandledDeleteAccountDialog(
    showDeleteAccountDialog: Boolean,
    isDarkTheme: Boolean,
    setShowDeleteAccountDialog: (Boolean) -> Unit,
    navigateToLogin: () -> Unit
) {
    if (showDeleteAccountDialog) {
        AccountDeletionDialog(
            isDarkTheme = isDarkTheme,
            onDismiss = { setShowDeleteAccountDialog(false) },
            onDeleteAccountSuccess = {
                navigateToLogin()
            }
        )
    }
}

@Composable
fun CalendarCustomizationZone(
    isDarkTheme: Boolean,
    isMondayStart: Boolean?,
    onWeekStartChanged: (Boolean) -> Unit
) {
    isMondayStart?.let {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            modifier = Modifier.padding(start = 16.dp),
            text = "Personalización de Calendario",
        )

        Spacer(modifier = Modifier.height(16.dp))

        OpositateCard(
            isDarkTheme = isDarkTheme,
            content = {
                Row(
                    modifier = Modifier.padding(horizontal = 18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Día de inicio de semana",
                        fontSize = 12.sp,
                        modifier = Modifier.weight(1f)
                    )

                    Row {
                        Spacer(modifier = Modifier.width(8.dp))
                        SettingsAssistChip(
                            selected = isMondayStart,
                            onWeekStartChanged = { if (!isMondayStart) onWeekStartChanged(true) },
                            text = "Lunes"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        SettingsAssistChip(
                            selected = !isMondayStart,
                            onWeekStartChanged = { if (isMondayStart) onWeekStartChanged(false) },
                            text = "Domingo"
                        )
                    }
                }
            },
            onClick = { }
        )
    }
}

@Composable
fun ThemeCustomizationZone(
    isDarkTheme: Boolean,
    manualThemeEnabled: Boolean,
    isDarkThemeManual: Boolean,
    onManualThemeToggle: (Boolean) -> Unit,
    onThemeManualChanged: (Boolean) -> Unit
) {
    Spacer(modifier = Modifier.height(32.dp))

    Text(
        modifier = Modifier.padding(start = 16.dp),
        text = "Personalización de Tema",
    )

    Spacer(modifier = Modifier.height(16.dp))

    OpositateCard(
        isDarkTheme = isDarkTheme,
        content = {
            Column {
                Row(
                    modifier = Modifier.padding(horizontal = 18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Selección manual de tema",
                        fontSize = 12.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = !manualThemeEnabled,
                        onCheckedChange = { onManualThemeToggle(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Gray900,
                            checkedTrackColor = Secondary500
                        ),
                        modifier = Modifier.scale(0.7f)
                    )
                }

                if (!manualThemeEnabled) {
                    HorizontalDivider(thickness = 1.dp, color = Gray800)
                    Row(
                        modifier = Modifier.padding(horizontal = 18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Tema",
                            fontSize = 12.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Row {
                            Spacer(modifier = Modifier.width(8.dp))
                            SettingsAssistChip(
                                selected = isDarkThemeManual,
                                onWeekStartChanged = { onThemeManualChanged(true) },
                                text = "Oscuro"
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            SettingsAssistChip(
                                selected = !isDarkThemeManual,
                                onWeekStartChanged = { onThemeManualChanged(false) },
                                text = "Claro"
                            )
                        }
                    }
                }
            }
        },
        onClick = { }
    )
}

@Composable
fun AccountDangerZone(
    isDarkTheme: Boolean,
    setShowDeleteAccountDialog: () -> Unit
) {
    Spacer(modifier = Modifier.height(32.dp))
    Text(
        modifier = Modifier.padding(start = 16.dp),
        text = "Zona de Peligro",
        color = ErrorLight
    )

    Spacer(modifier = Modifier.height(16.dp))
    OpositateCard(
        isDarkTheme = isDarkTheme,
        content = {
            Row {
                Text(
                    modifier = Modifier.padding(start = 18.dp, top = 10.dp, bottom = 10.dp),
                    text = "Eliminar cuenta",
                    fontSize = 12.sp,
                    color = ErrorLight
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    modifier = Modifier
                        .padding(end = 18.dp)
                        .align(Alignment.CenterVertically),
                    painter = painterResource(id = R.drawable.ic_close),
                    tint = ErrorLight,
                    contentDescription = "Botón"
                )
            }

        },
        onClick = {
            setShowDeleteAccountDialog()
        }
    )
}