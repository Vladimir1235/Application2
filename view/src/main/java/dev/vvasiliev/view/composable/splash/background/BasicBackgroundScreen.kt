package dev.vvasiliev.view.composable.splash.background

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.res.imageResource
import dev.vvasiliev.view.R

@Composable
fun BasicBackgroundScreen(modifier: Modifier = Modifier,content: @Composable ()->Unit){
    Box(modifier = modifier
        .fillMaxSize()
        .background(
            brush = ShaderBrush(ImageShader(ImageBitmap.imageResource(id = R.drawable.background), tileModeY = TileMode.Mirror))
        ), contentAlignment = Alignment.CenterStart){
        content()
    }
}