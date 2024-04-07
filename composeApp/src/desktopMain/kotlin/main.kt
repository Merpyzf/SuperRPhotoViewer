import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
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
    }
}