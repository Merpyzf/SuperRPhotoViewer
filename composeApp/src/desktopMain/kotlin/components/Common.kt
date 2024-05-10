package components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun PlaceHolder(modifier: Modifier = Modifier.fillMaxSize(), message: String) {
    Column(modifier = Modifier.then(modifier), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Image(
            painter = painterResource(DrawableResource("drawable/undraw_flying_drone.xml")),
            modifier = Modifier.fillMaxWidth(0.4f),
            contentDescription = null
        )
        Spacer(modifier = Modifier.fillMaxWidth().height(36.dp))
        Text(text = message, color = textColor, fontSize = largeTitleSize)
    }
}