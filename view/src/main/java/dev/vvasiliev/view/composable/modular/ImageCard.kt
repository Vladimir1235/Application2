package dev.vvasiliev.view.composable.modular

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ImageCard(
    data: ImageCardModel,
    actionModel: ImageCardActionModel,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(10),
        modifier = modifier
            .clip(RoundedCornerShape(10))
            .clickable {
                actionModel.onCardClick()
            }
            .width(280.dp)
            .height(220.dp)
    ) {
        Column(
            Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
        ) {
            Image(
                imageVector = Icons.Default.AccountBox,
                contentDescription = "Default Image",
                Modifier
                    .fillMaxWidth(1f)
                    .fillMaxHeight(0.5f)
            )
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = data.title,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = data.text,
                    style = MaterialTheme.typography.bodySmall,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Justify
                )
            }
        }
    }
}

@Composable
@Preview(showSystemUi = true, showBackground = true)
fun ImageCardPreview() {
    val model = ImageCardModel.mock()
    val actionModel = ImageCardActionModel {}
    ImageCard(data = model, actionModel = actionModel, modifier = Modifier.size(50.dp))
}

data class ImageCardModel(val title: String, val text: String, val imageLink: Uri) {
    companion object {
        fun mock() = ImageCardModel(
            title = "Some real big title text which is few rows long sadjhkjkad shjasdhkj sda hjksadhj kdsahkj dhas kjhjkdsahjk dsahkj dsaq hjk",
            "\"Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur?\"",
            Uri.EMPTY
        )
    }
}

fun interface ImageCardActionModel {
    fun onCardLongClick() = {}
    fun onTitleLongClick(title: String) = {}
    fun onTextLongClick(text: String) = {}

    abstract fun onCardClick()
}