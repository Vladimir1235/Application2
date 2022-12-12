package dev.vvasiliev.view.composable.splash.screen

import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.vvasiliev.view.composable.splash.background.BasicBackgroundScreen
import dev.vvasiliev.view.composable.splash.background.ShaderBackgroundScreen

@Composable
fun SplashScreen() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        ShaderBackgroundScreen {
            SplashScreenContent()
        }
    else BasicBackgroundScreen {
        SplashScreenContent()
    }
}

@Composable
fun SplashScreenContent() {
    val headerStyle = TextStyle(color = Color.White, fontSize = 95.sp, fontWeight = FontWeight.Bold)
    val poweredBlockStyle =
        TextStyle(color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.ExtraLight)
    Column(
        Modifier.padding(12.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.Start,
    ) {
        Text(text = "My Player", style = headerStyle)
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 24.dp), horizontalArrangement = Arrangement.End) {
            Text(text = "Powered by ExoPlayer", style = poweredBlockStyle)
        }
    }

}