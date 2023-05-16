package dev.vvasiliev.view.composable.composable

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun MyGreetingLayout(
    star: @Composable () -> Unit,
    star2: @Composable () -> Unit,
    sinLine: @Composable () -> Unit,
    text1: @Composable () -> Unit,
    text2: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        2f,
        26f,
        // No offset for the 1st animation
        infiniteRepeatable(tween(2600, delayMillis = 120), RepeatMode.Reverse)
    )
    Layout(
        modifier = Modifier
            .background(Color(0xFF202E30))
            .drawBehind {
                drawLine(
                    Color(0x81EC407A),
                    Offset(size.width, (size.height / 7) * (scale / 2)),
                    Offset(0f, size.height / 2),
                    strokeWidth = 8f
                )
                drawLine(
                    Color(0x975C6BC0),
                    Offset(-size.width, 45 * scale),
                    Offset(size.width, size.height),
                    strokeWidth = 8f
                )
            },
        content = { star(); sinLine(); star2(); text1(); text2(); }) { measurables: List<Measurable>, constraints: Constraints ->

        val layoutWidth = constraints.maxWidth
        val layoutHeight = constraints.maxHeight

        val starPlaceable = measurables[0].measure(constraints)
        val sinLinePlaceable = measurables[1].measure(constraints)
        val star2Placeable = measurables[2].measure(constraints)
        val text1Placeable = measurables[3].measure(constraints)
        val text2Placeable = measurables[4].measure(constraints)

        layout(width = layoutWidth, height = layoutHeight) {
            starPlaceable.place(position = IntOffset(0, 0))
            text1Placeable.place(layoutWidth / 4, layoutHeight / 3 - sinLinePlaceable.height)
            sinLinePlaceable.place(
                layoutWidth - sinLinePlaceable.width,
                layoutHeight / 4 + starPlaceable.height + 4
            )
            text2Placeable.place(
                layoutWidth / 4,
                layoutHeight / 2 + sinLinePlaceable.height + 4
            )
            star2Placeable.placeRelative(
                layoutWidth / 2,
                layoutHeight / 2 - sinLinePlaceable.height + starPlaceable.height
            )
        }
    }
}

@JvmInline
private value class Angle(val angle: Int) {
    companion object {
        val PI = 180f
        val ROUND = 360
    }
}

@Composable
private fun StarComposable(size: Dp, color: Color, step: Angle) {
    val path = Path()

    val infiniteTransition = rememberInfiniteTransition()

    val starScale by infiniteTransition.animateFloat(
        0f,
        270f,
        // No offset for the 1st animation
        infiniteRepeatable(
            tween(
                (Math.random() * 3900).toInt(),
                delayMillis = (Math.random() * 34).toInt()
            ), RepeatMode.Reverse
        )
    )
    for (angle in 0..Angle.ROUND step step.angle) {
        val sin = sin(Math.toRadians(angle.toDouble()))
        val cos = cos(Math.toRadians(angle.toDouble()))

        val endPoint = Offset((size.value * cos).toFloat(), (size.value * sin).toFloat())
        path.lineTo(x = endPoint.x, y = endPoint.y)
        path.moveTo(x = 0f, y = 0f)

    }

    Spacer(modifier = Modifier
        .size(size)
        .rotate(starScale)
        .drawWithCache {

            val translationX = this.size.width / 2
            val translationY = this.size.height / 2

            onDrawBehind {
                rotate(degrees = Angle.PI) {
                    translate(translationX, translationY) {
                        drawPath(
                            path, color = color, style = Stroke(
                                cap = StrokeCap.Square,
                                width = size.value / 18
                            )
                        )
                    }
                }
            }
        }
    )
}

@Composable
private fun SinLine(height: Dp, width: Dp, color: Color) {

    val points: MutableList<Offset> = mutableListOf()
    val infiniteTransition = rememberInfiniteTransition()

    val widthLocal = LocalDensity.current.run { width.toPx() }

    val sinScale by infiniteTransition.animateFloat(
        0f,
        widthLocal,
        // No offset for the 1st animation
        infiniteRepeatable(
            tween(
                (Math.random() * 3600).toInt(),
                delayMillis = (Math.random() * 10).toInt()
            ), RepeatMode.Reverse
        )
    )

    Spacer(modifier = Modifier
        .size(width = width, height = height)
        .drawWithCache {
            for (x in 0..sinScale.toInt() * 5) {
                if (x == 0) points.clear()
                points.add(
                    Offset(
                        x = x.toFloat(),
                        y = sin(Math.toRadians(x.toDouble())).toFloat() * height.value
                    )
                )
            }
            onDrawBehind {
                scale(scaleX = 0.15f, scaleY = 0.5f) {
                    translate(left = -2f * size.width, top = 0.5f * size.height) {
                        drawPoints(
                            points,
                            pointMode = PointMode.Lines,
                            brush = SolidColor(value = color),
                            strokeWidth = height.value / 2,
                            cap = StrokeCap.Round
                        )
                    }
                }
            }
        })
}

@Composable
fun WelcomingScreen() {
    MyGreetingLayout(
        star = { StarComposable(179.dp, Color(0xFF03A9F4), step = Angle(60)) },
        star2 = { StarComposable(269.dp, Color(0xFF3AA788), step = Angle(60)) },
        sinLine = { SinLine(width = 249.dp, height = 59.dp, color = Color(0xFFDA6743)) },
        text1 = {
            Text(
                text = "Мьюзік",
                style = MaterialTheme.typography.displayLarge.copy(color = Color(0xFFEF5350))
            )
        },
        text2 = {
            Text(
                text = "Прэміўм",
                style = MaterialTheme.typography.displayLarge.copy(color = Color(0xFFDA9126))
            )
        })
}

@Preview(device = Devices.NEXUS_6)
@Composable
fun MyGreetingComposablePreview() {
    WelcomingScreen()
//    Column {
//        StarComposable(69.dp, Color(0xFF03A9F4), step = Angle(60))
//        SinLine(width = 69.dp, height = 25.dp, color = Color(0xFF03A9F4))
//    }
}