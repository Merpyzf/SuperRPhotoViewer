import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.intui.standalone.Inter
import org.jetbrains.jewel.intui.standalone.theme.IntUiTheme
import org.jetbrains.jewel.intui.standalone.theme.createDefaultTextStyle
import org.jetbrains.jewel.intui.standalone.theme.default
import org.jetbrains.jewel.intui.standalone.theme.lightThemeDefinition
import org.jetbrains.jewel.intui.window.decoratedWindow
import org.jetbrains.jewel.intui.window.styling.light
import org.jetbrains.jewel.ui.ComponentStyling
import org.jetbrains.jewel.window.DecoratedWindow
import org.jetbrains.jewel.window.TitleBar
import org.jetbrains.jewel.window.newFullscreenControls
import org.jetbrains.jewel.window.styling.TitleBarStyle
import viewmodels.AppViewModel

private val viewModel: AppViewModel by lazy {
    AppViewModel()
}

fun main() = application {
    val textStyle = JewelTheme.createDefaultTextStyle(fontFamily = FontFamily.Inter)
    val themeDefinition = JewelTheme.lightThemeDefinition(
        defaultTextStyle = textStyle
    )
    IntUiTheme(
        theme = themeDefinition,
        styling =
        ComponentStyling.default().decoratedWindow(
            titleBarStyle = TitleBarStyle.light(),
        ),
        swingCompatMode = true,
    ) {
        DecoratedWindow(onCloseRequest = {
            exitApplication()
        }, state = rememberWindowState(size = DpSize(1000.dp, 800.dp))) {
            TitleBar(modifier = Modifier.newFullscreenControls()) {
                Text("SuperRPhotoViewer", color = Color.White)
            }
            App(viewModel)
        }
    }
}


