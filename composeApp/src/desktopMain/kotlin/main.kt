import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import coil3.compose.AsyncImage
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import viewmodels.AppViewModel

val rawWideImageSize = Size(1440f, 1080f)

private val viewModel: AppViewModel by lazy {
    AppViewModel()
}

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "SuperRPhotoViewer",
        state = rememberWindowState(size = DpSize(1000.dp, 800.dp))
    ) {
        App(viewModel)
//        ImageDemo()
    }
}

val flowState = MutableStateFlow<Int>(1)
private val viewModelScope = CoroutineScope(Dispatchers.Main)

@Composable
fun ImageDemo() {
    val state = flowState.collectAsState()
//    val currState = rememberUpdatedState(state)

    LaunchedEffect(true) {
        delay(10 * 1000)
        println("LaunchedEffect=> ${state.value}")
    }

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("flowState: ${state.value}")
        AsyncImage(
            model = "https://img1.doubanio.com/view/photo/l/public/p2566397609.jpg",
            "",
            modifier = Modifier.blur(3.dp)
        )
        Button(onClick = {
            viewModelScope.launch {
                flowState.emit(flowState.value + 1)
            }
        }) {
            Text("点我")
        }
    }
}