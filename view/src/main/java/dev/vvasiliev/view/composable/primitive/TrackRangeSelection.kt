package dev.vvasiliev.view.composable.primitive

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.max

const val duration: Long = 10L

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TrackRangeSelection(
    modifier: Modifier,
    duration: Long,
    maxFrequency: Long,
    timeLabel: @Composable (String) -> Unit,
    frequencyLabel: @Composable (String) -> Unit,
    interactivePlot: @Composable () -> Unit

) {
    val timeLabels = @Composable {
        repeat(duration.toInt()) {
            timeLabel(it.toString())
        }
    }

    val frequencyLabels = @Composable {
        for (index in maxFrequency downTo -maxFrequency) {
            frequencyLabel(index.toString())
        }
    }

    Layout(
        contents = listOf(timeLabels, frequencyLabels, interactivePlot),
        modifier = modifier.wrapContentSize()
    ) { (timeLabelMeasurables, frequencyLabelMeasurables, interactivePlotMeasurable), constraints: Constraints ->

        val timeLabelPlaceables = timeLabelMeasurables.map { measurable ->
            val timeLablePlaceable = measurable.measure(constraints)
            timeLablePlaceable
        }

        val frequencyLabelPlaceable = frequencyLabelMeasurables.map { measurable ->
            val placeable = measurable.measure(constraints)
            placeable
        }

        val maxH = frequencyLabelPlaceable.sumOf { it.height }
        val maxW = timeLabelPlaceables.sumOf { it.width }

        val interactivePlotPlaceable = interactivePlotMeasurable.first().measure(
            constraints.copy(
                maxWidth = maxW,
                maxHeight = maxH
            )
        )

        val totalWidth =
            frequencyLabelPlaceable.first().width + timeLabelPlaceables.sumOf { it.width }
        val totalHeight =
            timeLabelPlaceables.first().height + frequencyLabelPlaceable.sumOf { it.height }

        layout(totalWidth, totalHeight) {

            frequencyLabelPlaceable.mapIndexed { index, placeable ->
                placeable.place(0, index * placeable.height)
            }
            interactivePlotPlaceable.place(
                frequencyLabelPlaceable.first().width,
                0
            )

            timeLabelPlaceables.mapIndexed { index, placeable ->
                placeable.place(
                    index * placeable.width,
                    frequencyLabelPlaceable.sumOf { it.height })
            }
        }
    }
}

fun Modifier.timeLabelData(height: Int, width: Int) = this.then(TimeLabelData(height, width))
fun Modifier.frequencyLabelData(height: Int, width: Int) =
    this.then(FrequencyLabelData(height, width))

class TimeLabelData constructor(val height: Int, val width: Int) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?): Any = this@TimeLabelData
}

class FrequencyLabelData(val height: Int, val width: Int) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?): Any = this@FrequencyLabelData
}

@Preview
@Composable
fun TrackRangeSelectionPreview() {
    TrackRangeSelection(
        modifier = Modifier,
        duration = duration,
        maxFrequency = duration,
        timeLabel = { input -> Text(text = input) },
        frequencyLabel = { input -> Text(text = input) },
        interactivePlot = {
            Box(
                modifier = Modifier
                    .background(Color.Cyan)
                    .fillMaxSize()
            )
        }
    )
}