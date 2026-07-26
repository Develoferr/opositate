package com.develofer.opositate.feature.profile.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.develofer.opositate.ui.theme.Gray700
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.DotProperties
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.IndicatorCount
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.PopupProperties

@Composable
fun LineChartComponent(
    isDarkTheme: Boolean,
    data: List<Line> = emptyList(),
    minValue: Double = 0.0,
    maxValue: Double = 10.0,
    labels: List<String> = listOf("F", "MF", "M", "MD", "D")
) {
    Box(
        modifier = Modifier
            .height(200.dp)
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
            .padding(bottom = 8.dp)
    ) {
        val gradientColors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
        LineChart(
            data = remember { data },
            animationMode = AnimationMode.Together(delayBuilder = {
                it * 500L
            }),
            dotsProperties = DotProperties(
                enabled = true,
                animationEnabled = true,
                radius = 4.dp,
                strokeWidth = 2.dp,
                strokeColor = Brush.verticalGradient(colors = gradientColors),
            ),
            minValue = minValue,
            maxValue = maxValue,
            labelProperties = LabelProperties(
                enabled = true,
                textStyle = TextStyle(
                    fontSize = 10.sp,
                    color = if (isDarkTheme) Gray700 else Color.DarkGray,
                    fontWeight = FontWeight.W400
                ),
                labels = labels,
            ),
            indicatorProperties = HorizontalIndicatorProperties(
                textStyle = TextStyle(
                    fontSize = 10.sp,
                    color = if (isDarkTheme) Gray700 else Color.DarkGray,
                    fontWeight = FontWeight.W400
                ),
                count = IndicatorCount.CountBased(11),
                enabled = true
            ),
            labelHelperProperties = LabelHelperProperties(
                enabled = false
            ),
            gridProperties = GridProperties(
                enabled = true,
                xAxisProperties = GridProperties.AxisProperties(
                    enabled = true,
                    lineCount = 11,
                )
            ),
            popupProperties = PopupProperties(
                enabled = true,
                textStyle = TextStyle(
                    fontSize = 10.sp,
                    color = if (isDarkTheme) Gray700 else Color.DarkGray,
                    fontWeight = FontWeight.W400
                ),
                mode = PopupProperties.Mode.PointMode(threshold = 5.dp)
            ),
        )
    }
}

@Composable
fun ColumnChartComponent(
    isDarkTheme: Boolean,
    data: List<Bars> = emptyList(),
    minValue: Double = 0.0,
    maxValue: Double = 100.0
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        ColumnChart(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            data = remember { data },
            gridProperties = GridProperties(
                enabled = true,
                xAxisProperties = GridProperties.AxisProperties(
                    enabled = true,
                    lineCount = 11,
                )
            ),
            barProperties = BarProperties(

            ),
            maxValue = maxValue,
            minValue = minValue,
            labelProperties = LabelProperties(
                textStyle = TextStyle(
                    fontSize = 10.sp,
                    color = if (isDarkTheme) Gray700 else Color.DarkGray,
                    fontWeight = FontWeight.W400
                ),
                enabled = true
            ),
            indicatorProperties = HorizontalIndicatorProperties(
                textStyle = TextStyle(
                    fontSize = 10.sp,
                    color = if (isDarkTheme) Gray700 else Color.DarkGray,
                    fontWeight = FontWeight.W400
                ),
                count = IndicatorCount.CountBased(11),
                enabled = true
            ),
            labelHelperProperties = LabelHelperProperties(
                enabled = false
            ),
            popupProperties = PopupProperties(
                enabled = true
            )
        )
    }
}