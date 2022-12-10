package dev.vvasiliev.view.composable.primitive

import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.withInfiniteAnimationFrameMillis
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.ShaderBrush
import dev.vvasiliev.view.composable.shader.SAMPLE_SHADER_BACKGROUND

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun ShaderBackgroundScreen(content: @Composable () -> Unit) {
    val iTime by produceState(initialValue = 0f){
        while (true){
            withInfiniteAnimationFrameMillis {
                value = it / 50000f
            }
        }
    }
    Box(modifier = Modifier.drawWithCache {
       val shader = RuntimeShader(SAMPLE_SHADER_BACKGROUND)
        val shaderBrush = ShaderBrush(shader)
        shader.setFloatUniform("iResolution", size.width, size.height)
        onDrawBehind{
            shader.setFloatUniform("iTime", iTime)
            drawRect(shaderBrush)
        }
    }.fillMaxSize()){
        content()
    }
}