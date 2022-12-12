package dev.vvasiliev.view.composable.primitive

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun InteractableProgress(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    foregroundColor: Color = MaterialTheme.colorScheme.primaryContainer,
    progressState: Float,
    onStateChanged: (Float) -> Unit
) {
    Box(modifier = modifier
        .clip(RoundedCornerShape(50))
        .drawWithCache {
            val currentProgress = size.width * progressState
            onDrawWithContent {
                drawLine(
                    backgroundColor,
                    start = Offset(x = 0f, y = size.height / 2),
                    end = Offset(x = size.width, y = size.height / 2),
                    strokeWidth = size.height,
                    cap = StrokeCap.Round
                )
                drawLine(
                    foregroundColor,
                    start = Offset(x = 0f, y = size.height / 2),
                    end = Offset(x = currentProgress, y = size.height / 2),
                    strokeWidth = size.height,
                    cap = StrokeCap.Round
                )
            }
        }
        .fillMaxWidth()
        .pointerInput(Unit) {
            detectDragGestures { change, dragAmount ->
                onStateChanged(change.position.x / size.width)
            }
        })
}

@Composable
@Preview(showBackground = true)
fun IntercatableProgressPreview() {
    var progressState by remember {
        mutableStateOf(0.4f)
    }
    InteractableProgress(
        foregroundColor = Color.Gray,
        backgroundColor = Color.LightGray,
        modifier = Modifier.height(12.dp),
        progressState = progressState) {
        progressState = it
    }
}