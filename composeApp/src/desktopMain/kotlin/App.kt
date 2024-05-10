import androidx.compose.animation.Crossfade
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
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
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.rememberWindowState
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker
import com.dokar.sonner.ToastType
import com.dokar.sonner.Toaster
import com.dokar.sonner.rememberToasterState
import components.*
import helper.supportWindowsImageLoader
import kotlinx.coroutines.launch
import models.PreviewWidePhoto
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.jewel.ui.Orientation
import org.jetbrains.jewel.ui.component.*
import org.jetbrains.jewel.ui.component.VerticalScrollbar
import viewmodels.AppViewModel
import java.awt.Desktop

@Composable
@Preview
fun App(viewModel: AppViewModel) {
    val scope = rememberCoroutineScope()
    var showDirPicker by remember { mutableStateOf(false) }
    var showLoading by remember { mutableStateOf(false) }

    val toaster = rememberToasterState()
    Toaster(toaster)

    DirectoryPicker(
        showDirPicker,
        initialDirectory = "/",
        title = "请选择巡检照片所在的文件夹"
    ) { path ->
        showDirPicker = false
        if (path == null) {
            return@DirectoryPicker
        }
        scope.launch {
            try {
                showLoading = true
                // 图片选择完毕后会去加载所有的图片，并筛选出首页需要展示的超清矩阵预览图
                viewModel.setPhotoDirPath(path)
                showLoading = false
            } catch (e: Exception) {
                toaster.show("👹${e.message}", type = ToastType.Error)
            }
        }
    }

    if (viewModel.isOpenWidePhoto) {
        Window(onCloseRequest = {
            viewModel.isOpenWidePhoto = false
        }, title = "超清矩阵照片预览", state = rememberWindowState(size = DpSize(1000.dp, 800.dp)), content = {
            if (viewModel.currOpenWidePhoto != null) {
                SuperRSegmentView(viewModel.currOpenWidePhoto!!)
            }
        })
    }

    Column(modifier = Modifier.background(Color.White).fillMaxSize()) {
        Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
            Crossfade(targetState = showLoading) { isLoading ->
                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicatorBig()
                    }
                } else {
                    if (viewModel.photoFiles.isEmpty() && viewModel.photoDirPath.isBlank()) {
                        PlaceHolder(message = "")
                    } else if (viewModel.photoFiles.isEmpty()) {
                        PlaceHolder(message = "😶‍🌫️无内容，请检查您选择的文件夹")
                    } else {
                        SuperRPreviewImageList(viewModel, modifier = Modifier.fillMaxSize())
                    }
                }
            }
        }
        Divider(orientation = Orientation.Horizontal, color = Color.Black.copy(0.2f), thickness = 1.dp)
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedButton(onClick = {
                showDirPicker = true
            }) {
                Text(text = "选择文件夹", fontSize = titleSize, lineHeight = titleSize)
            }
            Spacer(modifier = Modifier.width(12.dp))
            SelectionContainer {
                if (viewModel.photoDirPath.isBlank()) {
                    Text(
                        text = "✨请选择要查看的文件夹",
                        color = secondaryTextColor,
                        fontSize = titleSize,
                        lineHeight = titleSize
                    )
                } else {
                    Text(text = viewModel.photoDirPath, color = textColor, fontSize = titleSize, lineHeight = titleSize)
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            if (viewModel.photoDirPath.isNotBlank()) {
                Text(
                    text = "照片数量：${viewModel.previewWidePhotos.size}",
                    color = textColor,
                    fontSize = titleSize,
                    lineHeight = titleSize
                )
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SuperRPreviewImageList(viewModel: AppViewModel, modifier: Modifier = Modifier) {
    val state =
        rememberLazyGridState()

    Box(modifier = Modifier.then(modifier)) {
        LazyVerticalGrid(
            GridCells.Fixed(3), modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(12.dp), state = state
        ) {
            items(viewModel.previewWidePhotos) {
                Box(modifier = Modifier.padding(12.dp)) {
                    val widePreviewImage = it
                    SuperRPreviewImageItem(
                        widePreviewImage,
                        modifier = Modifier.fillMaxWidth().onPointerEvent(PointerEventType.Press) {
                            viewModel.isOpenWidePhoto = true
                            viewModel.currOpenWidePhoto = widePreviewImage
                        })
                }
            }
        }
        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = ScrollbarAdapter(scrollState = state),
        )
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SuperRPreviewImageItem(image: PreviewWidePhoto, modifier: Modifier = Modifier) {
    val painter = rememberAsyncImagePainter(
        model = image.imageFilePath,
        imageLoader = supportWindowsImageLoader,
        contentScale = ContentScale.Inside
    )
    Column(modifier = modifier, horizontalAlignment = Alignment.Start) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.33f)
                .border(1.dp, Color.Black.copy(0.1f), RoundedCornerShape(8.dp))
                .clip(
                    RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painter,
                contentDescription = "",
                modifier = Modifier.fillMaxSize(),
            )
            Crossfade(painter.state) {
                when (painter.state) {
                    is AsyncImagePainter.State.Success -> {

                    }

                    is AsyncImagePainter.State.Loading -> {
                        CircularProgressIndicator()
                    }

                    is AsyncImagePainter.State.Error -> {
                        Text(
                            text = "图片加载失败",
                            color = Color.Red.copy(0.7f),
                            fontSize = subTitleSize,
                            lineHeight = subTitleSize
                        )
                    }

                    AsyncImagePainter.State.Empty -> {

                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            Badge(text = "阶段：${image.stage}", color = Color(0xFF722ed1))
            Spacer(modifier = Modifier.width(6.dp))
            Badge(text = "航点：${image.waypointIndex}", color = Color(0xFF2db7f5))
            Spacer(modifier = Modifier.width(6.dp))
            Badge(text = "${image.type.text}(${image.zoomPhotos.size}张)", color = Color(0xFF87d068))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "${image.photoFile.nameWithoutExtension}",
            color = textColor,
            fontSize = subTitleSize,
        )
    }
}

@Composable
fun Badge(text: String, color: Color) {
    Text(
        text, fontSize = subTitleSize,
        color = Color.White,
        lineHeight = subTitleSize,
        modifier = Modifier.wrapContentSize()
            .background(color, shape = RoundedCornerShape(3.dp))
            .border(1.dp, Color.Black.copy(0.1f), RoundedCornerShape(3.dp))
            .clip(shape = RoundedCornerShape(3.dp))
            .padding(horizontal = 6.dp, vertical = 4.dp)
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SuperRSegmentView(previewWidePhoto: PreviewWidePhoto) {
    var imageSize = Size.Zero
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        AsyncImage(
            model = previewWidePhoto.imageFilePath,
            imageLoader = supportWindowsImageLoader,
            contentDescription = "",
            contentScale = ContentScale.Fit,
            modifier = Modifier.drawWithContent {
                this.drawContent()

                imageSize = this.size

                for (zoomPhoto in previewWidePhoto.zoomPhotos) {
                    val inWideRegionF = zoomPhoto.inWideRegionF ?: continue
                    val left = inWideRegionF.left * size.width
                    val top = inWideRegionF.top * size.height
                    val right = inWideRegionF.right * size.width
                    val bottom = inWideRegionF.bottom * size.height

                    this.drawRect(
                        color = Color(0xFFf5222d),
                        topLeft = Offset(left, top),
                        size = Size(right - left, bottom - top),
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
                for (zoomPhoto in previewWidePhoto.zoomPhotos) {
                    val inWideRegionF = zoomPhoto.inWideRegionF ?: continue
                    val left = inWideRegionF.left * imageSize.width
                    val top = inWideRegionF.top * imageSize.height
                    val right = inWideRegionF.right * imageSize.width
                    val bottom = inWideRegionF.bottom * imageSize.height
                    val rect = Rect(left, top, right, bottom)
                    if (rect.contains(offset = Offset(position.x, position.y))) {
                        if (Desktop.isDesktopSupported()) {
                            val desktop = Desktop.getDesktop()
                            if (desktop.isSupported(Desktop.Action.OPEN)) {
                                try {
                                    desktop.open(zoomPhoto.photoFile)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            } else {
                                println("Desktop not support open")
                            }
                        } else {
                            println("Desktop not support")
                        }
                    }
                }
            }
        )
    }
}

