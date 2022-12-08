package dev.vvasiliev.view.composable.primitive

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.Button
import androidx.compose.material.Text

@Composable
fun CustomButton(){
    Button(onClick = {}){
        Text("Ok")
    }
}

@Composable
@Preview(showSystemUi = true, showBackground = true)
fun ButtonPreview(){
    CustomButton()
}