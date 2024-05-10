package models

import androidx.compose.runtime.mutableStateListOf
import java.io.File

enum class CaptureType(val text: String) {
    NORMAL("普通拍照"),
    INTERVAL("间隔拍照"),
    SUPER_R("超清矩阵")
}

class PreviewWidePhoto(var type: CaptureType, var photoFile: File) {
    val imageFilePath: String
        get() {
            return photoFile.absolutePath
        }
    val zoomPhotos = mutableStateListOf<ZoomPhoto>()

    var waypointIndex: Int? = null
    var stage: Int? = null
}