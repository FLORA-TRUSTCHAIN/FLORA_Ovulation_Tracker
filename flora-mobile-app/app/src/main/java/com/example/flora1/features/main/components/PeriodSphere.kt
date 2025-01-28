package com.example.flora1.features.main.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flora1.R
import com.example.flora1.core.presentation.designsystem.Flora1Theme
import java.time.LocalDate
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun PeriodSphere(
    selectedDay: Int,
    fertileDays: List<Int> = buildList { (1..3).forEach { add(it) } },
    ovulationDay: Int? = null,
    shouldShowPredictions: Boolean = true,
    periodDays: List<Int> = listOf(11, 12, 13, 14, 15, 16, 17),
    dateText: String = "Thu, 12 May",
    primaryText: String = "1 Day",
    onArcClicked: (offsetClicked: Offset, circlePositions: List<Pair<Float, Float>>, circleRadius: Float) -> Unit,
) {

    val context = LocalContext.current
    val density = LocalDensity.current


    var circlePositions: List<Pair<Float, Float>> = emptyList()
    val currentDate = remember {
        LocalDate.now()
    }


    /*
    I can find a better solution for getting the screen's width
     */
    val displayMetrics = context.resources.displayMetrics
    val diameter = displayMetrics.widthPixels
    val radius = ((diameter.toFloat()) / 2f)

    val innerCircleWidth = with(density) {
        (diameter.toFloat() / (1.7f)).toDp()
    }

    val primaryColor = MaterialTheme.colorScheme.primary

    val totalSweep = 300f
    val startAngle = 0f


    val offsetFactor = 0.2f // Adjust this factor to change the offset

    val offset = radius * offsetFactor
    LaunchedEffect(key1 = Unit) {

        circlePositions = buildList {
            (1..currentDate.month.maxLength()).forEach {
                add(
                    calculateCirclePosition(
                        day = it,
                        totalDays = 30,
                        totalSweep = totalSweep,
                        radius = radius - offset,
                        density = density,
                    )
                )
            }
        }.map {
            with(density) {
                Pair(it.first.toPx() + radius, it.second.toPx() + radius)
            }
        }
    }

    val dayIndicatorSizeFloat = radius * 0.3f
    val dayIndicatorSize: Dp = with(density) {
        dayIndicatorSizeFloat.toDp()
    }
    val todayRadius: Dp = with(density) {
        (radius * 0.1f).toDp()
    }
    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(
            modifier =
            Modifier
                .fillMaxWidth()
                .height(with(density) { diameter.toDp() })
                .pointerInput(Unit) {
                    detectTapGestures { offsetClicked ->
                        onArcClicked(offsetClicked, circlePositions, dayIndicatorSizeFloat / 2f)
                    }
                }
        ) {


            drawCircle(
                color = primaryColor,
                center = this.center,
                radius = radius,
            )

            drawArc(
                color = White,
                startAngle = startAngle,
                sweepAngle = totalSweep,
                useCenter = false,
                topLeft = this.center.copy(
                    x = this.center.x - radius + offset,
                    y = this.center.y - radius + offset
                ),
                size = androidx.compose.ui.geometry.Size(
                    width = radius * 2 - offset * 2,
                    height = radius * 2 - offset * 2
                ),
                style = Stroke(
                    width = radius * 0.1f,
                    cap = StrokeCap.Round
                )
            )


            if (fertileDays.isNotEmpty() && shouldShowPredictions)
                drawArc(
                    color = Color(0xFF1C7DE6),
                    startAngle = (fertileDays[0] - 1) * totalSweep / (currentDate.month.maxLength()).toFloat(),
                    sweepAngle = fertileDays.size * totalSweep / (currentDate.month.maxLength()).toFloat(),
                    useCenter = false,
                    topLeft = this.center.copy(
                        x = this.center.x - radius + offset,
                        y = this.center.y - radius + offset
                    ),
                    size = androidx.compose.ui.geometry.Size(
                        width = radius * 2 - offset * 2,
                        height = radius * 2 - offset * 2
                    ),
                    style = Stroke(
                        width = radius * 0.1f,
                        cap = StrokeCap.Round
                    )
                )

            if (periodDays.isNotEmpty())
                drawArc(
                    color = Color(0xFFA01C1C),
                    startAngle = (periodDays[0] - 1) * totalSweep / (currentDate.month.maxLength()).toFloat(),
                    sweepAngle = (periodDays.size) * totalSweep / (currentDate.month.maxLength()).toFloat(),
                    useCenter = false,
                    topLeft = this.center.copy(
                        x = this.center.x - radius + offset,
                        y = this.center.y - radius + offset
                    ),
                    size = androidx.compose.ui.geometry.Size(
                        width = radius * 2 - offset * 2,
                        height = radius * 2 - offset * 2
                    ),
                    style = Stroke(
                        width = radius * 0.1f,
                        cap = StrokeCap.Round
                    )
                )


        }


        Box(
            modifier = Modifier
                .width(innerCircleWidth)
                .height(innerCircleWidth)
                .clip(CircleShape)
                .background(White),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = dateText,
                    fontFamily = FontFamily(Font(R.font.raleway_regular)),
                    fontSize = 14.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = primaryText,
                    fontFamily = FontFamily(Font(R.font.raleway_extrabold)),
                    fontSize = 25.sp,
                    color = primaryColor,
                    textAlign = TextAlign.Center,
                )
            }
        }

        // Adjust this to change the position dynamically
        val (xPosition, yPosition) = calculateCirclePosition(
            day = selectedDay - 1,
            totalDays = currentDate.month.maxLength(),
            totalSweep = totalSweep,
            radius = radius - offset,
            density = density,
        )

        val (todayX, todayY) = remember(Unit) {
            calculateCirclePosition(
                day = currentDate.dayOfMonth - 1,
                totalDays = currentDate.month.maxLength(),
                totalSweep = totalSweep,
                radius = radius - offset,
                density = density,
            )
        }


        Box(
            modifier = Modifier
                .size(todayRadius * 0.9f)
                .offset(
                    x = todayX,
                    y = todayY
                )
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
        )

        ovulationDay?.let { ovulationDay ->
            if (shouldShowPredictions) {
                val (ovulationX, ovulationY) = remember {
                    calculateCirclePosition(
                        day = ovulationDay - 1,
                        totalDays = currentDate.month.maxLength(),
                        totalSweep = totalSweep,
                        radius = radius - offset,
                        density = density,
                    )
                }

                Box(
                    modifier = Modifier
                        .size(todayRadius * 0.9f)
                        .offset(
                            x = ovulationX,
                            y = ovulationY,
                        )
                        .clip(CircleShape)
                        .background(Color(0xFF03A9F4)),
                )
            }

        }



        Box(
            modifier = Modifier
                .size(dayIndicatorSize)

                .offset(
                    x = xPosition,
                    y = yPosition
                )
                .border(
                    width = if (selectedDay !in periodDays
                        && selectedDay !in fertileDays
                    ) 1.dp else 4.dp,
                    color = when (selectedDay) {
                        in fertileDays -> Color(0xFF1C7DE6)
                        in periodDays -> Color(0xFFA01C1C)
                        else -> Color.Black
                    },
                    shape = CircleShape,
                )
                // This padding if for the outer white colour in border to disappear
                // so that colours of arc and selected day can blend.
                .padding(1.dp)
                .clip(CircleShape)
                .background(White),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = selectedDay.toString(),
                fontFamily = FontFamily(Font(R.font.opensans_regular)),
                fontSize = 20.sp,
            )
        }


    }
}

fun calculateCirclePosition(
    day: Int,
    totalDays: Int,
    totalSweep: Float,
    radius: Float,
    density: Density
): Pair<Dp, Dp> {
    val sweepAnglePerDay = totalSweep / totalDays.toFloat()
    val angleInDegrees = 0 + sweepAnglePerDay * day
    val angleInRadians = Math.toRadians(angleInDegrees.toDouble())

    val x = (radius * cos(angleInRadians)).toFloat()
    val y = (radius * sin(angleInRadians)).toFloat()

    return with(density) {
        Pair(x.toDp(), y.toDp())
    }
}

@Preview(name = "")
@Composable
fun SpherePreview() {

    var day by remember {
        mutableIntStateOf(0)
    }

    Flora1Theme {
        PeriodSphere(
            selectedDay = day,
            ovulationDay = 14,
            fertileDays = buildList { (11..15).forEach { add(it) } },
            periodDays = listOf(4, 5, 6),
            onArcClicked = { offsetClicked, circlePositions, arcSize ->
                circlePositions.forEachIndexed { index, position ->
                    if (abs(offsetClicked.x - position.first) < arcSize / 2f
                        && abs(offsetClicked.y - position.second) < arcSize / 2f
                    ) {
                        day = index + 1
                        return@forEachIndexed
                    }
                }
            }
        )
    }


}


/*
    drawIntoCanvas { canvas ->
                    val paint = Paint().asFrameworkPaint().apply {
                        isAntiAlias = true
                        textSize = 20.sp.toPx()
                        color = android.graphics.Color.BLACK
                        textAlign = android.graphics.Paint.Align.LEFT
                    }

                    (0..30).forEach {
                        val step = it.toFloat() * totalSweep/(31f)
                        val path = Path().apply {
                            addArc(
                                RectF(
                                    center.x - radius + offset,
                                    center.y - radius + offset,
                                    center.x + radius - offset,
                                    center.y + radius - offset
                                ),
                                startAngle + step,
                                totalSweep/31f + step,
                            )
                        }
                        canvas.nativeCanvas.drawTextOnPath(it.toString() , path, 0f, 20f, paint)
                    }

                }

                This kinda works for numbers
 */

