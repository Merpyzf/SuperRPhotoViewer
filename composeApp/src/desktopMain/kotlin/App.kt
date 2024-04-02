import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.onClick
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.rememberWindowState
import androidx.compose.ui.window.singleWindowApplication
import coil3.compose.AsyncImage
import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker
import models.WidePreviewImage
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.ui.tooling.preview.Preview
import viewmodels.AppViewModel
import java.awt.Desktop

var isShowSegmentImage by mutableStateOf(false)
var currentWidePreviewImage by mutableStateOf<WidePreviewImage?>(null)

@OptIn(ExperimentalResourceApi::class)
@Composable
@Preview
fun App(viewModel: AppViewModel) {
    var showDirPicker by remember { mutableStateOf(false) }
    var photoDirPath by remember { mutableStateOf("") }

    MaterialTheme {
        Column(modifier = Modifier.background(Color.White).fillMaxSize()) {
            Row(modifier = Modifier.fillMaxWidth().padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = {
                    showDirPicker = true
                }) {
                    Text(text = "选择照片文件夹")
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = photoDirPath)
            }

            SuperRPreviewImageList(viewModel.superRPreviewImages, modifier = Modifier.fillMaxSize())

            DirectoryPicker(showDirPicker) { path ->
                showDirPicker = false
                path?.let {
                    photoDirPath = it
                    viewModel.getSuperRImageList(photoDirPath)
                }
            }

            if (isShowSegmentImage) {
                Window(onCloseRequest = {
                    isShowSegmentImage = false
                }, title = "超清矩阵照片预览", state = rememberWindowState(size = DpSize(1000.dp, 800.dp)), content = {
                    if (currentWidePreviewImage != null) {
                        SuperRSegmentView(currentWidePreviewImage!!)
                    }
                })
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SuperRPreviewImageList(images: List<WidePreviewImage>, modifier: Modifier = Modifier) {
    LazyVerticalGrid(
        GridCells.Fixed(3), modifier = modifier, contentPadding = PaddingValues(12.dp)
    ) {
        items(images) {
            Box(modifier = Modifier.padding(12.dp)) {
                val widePreviewImage = it
                SuperRPreviewImageItem(
                    widePreviewImage,
                    modifier = Modifier.fillMaxWidth().onPointerEvent(PointerEventType.Press) {
                        isShowSegmentImage = true
                        currentWidePreviewImage = widePreviewImage
                    })
            }
        }
    }
}

@Composable
fun SuperRPreviewImageItem(image: WidePreviewImage, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        val path = image.imageFile.path.replace("\\", "/")
        println(path)
        AsyncImage(
            path,
            contentDescription = "",
            contentScale = ContentScale.Inside,
            modifier = Modifier.fillMaxWidth().aspectRatio(1.33f)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "${image.imageFile.nameWithoutExtension}(${image.zoomSegmentImages.size}张)")
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SuperRSegmentView(widePreviewImage: WidePreviewImage) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        AsyncImage(
            model = widePreviewImage.imageFile.path.replace("\\", "/"),
            contentDescription = "",
            contentScale = ContentScale.Fit,
            modifier = Modifier.drawWithContent {
                this.drawContent()

                val size = this.size
                // FIXME: 后面查看新拍摄的图片时移除此处的纠错逻辑
                val scaleWRatio = size.width / rawWideImageSize.width
                val scaleHRatio = size.height / rawWideImageSize.height

//                val scaleWRatio = (size.width / 1.3333334f) / rawWideImageSize.width
//                val scaleHRatio = (size.height / 1.074074f) / rawWideImageSize.height

                println("scaleWRatio: ${scaleWRatio}, scaleHRation: ${scaleHRatio}")

                println("zoomSegmentImages.size: ${widePreviewImage.zoomSegmentImages.size}")

                for (zoomSegmentImage in widePreviewImage.zoomSegmentImages) {
                    zoomSegmentImage.scaleRect(scaleWRatio, scaleHRatio)
                    val scaledRect = zoomSegmentImage.scaledRect
                    this.drawRect(
                        Color.White,
                        topLeft = Offset(scaledRect.left, scaledRect.top),
                        size = Size(scaledRect.right - scaledRect.left, scaledRect.bottom - scaledRect.top),
                        style = Stroke(
                            width = 2.dp.toPx(), cap = StrokeCap.Round, pathEffect = PathEffect.dashPathEffect(
                                floatArrayOf(
                                    6.dp.toPx(), 6.dp.toPx()
                                )
                            )
                        )
                    )
                }
            }.onPointerEvent(PointerEventType.Press) {
                val position = it.changes.first().position
                println("position: (${position.x}, ${position.y})")

                val clickedSegmentImage = widePreviewImage.zoomSegmentImages.find {
                    it.scaledRect.contains(offset = Offset(position.x, position.y))
                }
                if (clickedSegmentImage == null) {
                    println("点击位置不包含分割图片")
                } else {
                    if (Desktop.isDesktopSupported()) {
                        println("Desktop support")
                        val desktop = Desktop.getDesktop()
                        if (desktop.isSupported(Desktop.Action.OPEN)) {
                            try {
                                desktop.open(clickedSegmentImage?.imageFile)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else {
                            println("Desktop not support open")
                        }
                    } else {
                        println("Desktop not support")
                    }

                    println("点击位置包含分割图片：${clickedSegmentImage?.imageFile?.path}")

                }
            }
        )
    }
}