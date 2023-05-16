package dev.vvasiliev.view.composable.composable

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.vvasiliev.view.composable.composable.PaintConfig.DefaultPaintConfig.colors

@Composable
fun DynamicRangeComposable(
    modifier: Modifier = Modifier,
    paintConfig: PaintConfig = PaintConfig.DefaultPaintConfig
) {

    var selectionRegionStart: MutableState<Float> = remember { mutableStateOf(0f) }
    var selectionRegionEnd: MutableState<Float> = remember { mutableStateOf(0f) }

    Column {
        Box(
            modifier
                .drawWithCache {
                    if (selectionRegionEnd.value == 0f)
                        selectionRegionEnd.value = size.width
                    else if (selectionRegionEnd.value > size.width)
                        selectionRegionEnd.value = size.width

                    onDrawWithContent {
                        drawRoundRect(
                            brush = paintConfig.brush,
                            topLeft = Offset(selectionRegionStart.value, 0f),
                            size = Size(
                                width = selectionRegionEnd.value - selectionRegionStart.value,
                                height = size.height
                            ),
                            cornerRadius = paintConfig.cornerRadius,
                            style = paintConfig.style,
                        )
                    }
                }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { change, _ ->
                        val middle = (selectionRegionStart.value + selectionRegionEnd.value) / 2
                        if (change.position.x < size.width && change.position.x > 0f)
                            when (change.position.x) {
                                in 0f..middle -> {
                                    selectionRegionStart.value = change.position.x
                                }
                                in middle..Float.MAX_VALUE -> {
                                    selectionRegionEnd.value = change.position.x
                                }
                            }
                    }
                })
    }
}

open class PaintConfig internal constructor(
    val colors: List<Color>,
    val brush: Brush,
    val style: DrawStyle,
    val cornerRadius: CornerRadius
) {
    object DefaultPaintConfig :
        PaintConfig(
            colors = listOf(
                Color(0xFF03A9F4),
                Color(0xFF47C2FA)
            ),
            brush = Brush.horizontalGradient(
                colors = colors
            ),
            cornerRadius = CornerRadius(22f, 22f),
            style = Fill
        )
}

@Preview
@Composable
fun DynamicRangeViewPreview() {
    DynamicRangeComposable(modifier = Modifier.size(132.dp))
}