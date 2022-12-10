package dev.vvasiliev.view.composable.splash.background

import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.withInfiniteAnimationFrameMillis
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.ShaderBrush
import dev.vvasiliev.view.composable.splash.shader.SAMPLE_SHADER_BACKGROUND

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun ShaderBackgroundScreen(modifier: Modifier = Modifier,content: @Composable () -> Unit) {
    val iTime by produceState(initialValue = 0f){
        while (true){
            withInfiniteAnimationFrameMillis {
                value = it / 100f
            }
        }
    }
    Box(contentAlignment = Alignment.CenterStart,modifier = modifier.drawWithCache {
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