package com.develofer.opositate.main.components.appbar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.develofer.opositate.R
import com.develofer.opositate.feature.login.presentation.component.CustomBodyText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomAppBar(
    title: State<String>,
    isDarkTheme: Boolean,
    actions: @Composable () -> Unit = {},
    logout: () -> Unit,
    uploadTestCatalog: (() -> Unit)? = null,
) {
    var expanded by remember { mutableStateOf(false) }

    CenterAlignedTopAppBar(
        windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor =
                if (isDarkTheme) MaterialTheme.colorScheme.background
                    else MaterialTheme.colorScheme.primary,
            scrolledContainerColor = Color.Unspecified,
            navigationIconContentColor = Color.Unspecified,
            titleContentColor = Color.Unspecified,
            actionIconContentColor = Color.Unspecified
        ),
        navigationIcon = {
            IconButton(
                onClick = { /* Handle navigation icon click */ },
                modifier = Modifier.padding(start = 10.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.custom_app_bar__content_description__menu),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        title = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CustomBodyText(
                    text = title.value,
                    isDarkTheme = isDarkTheme,
                    textSize = 25.sp,

                )
            }
        },
        actions = {
            IconButton(
                onClick = { expanded = !expanded },
                modifier = Modifier.padding(end = 10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = stringResource(R.string.custom_app_bar__content_description__more_options),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Cerrar sesión") },
                    onClick = {
                        logout()
                        expanded = false
                    }
                )
                if (uploadTestCatalog != null) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.custom_app_bar__menu__upload_test_catalog)) },
                        onClick = {
                            uploadTestCatalog()
                            expanded = false
                        }
                    )
                }
            }
        }
    )
}