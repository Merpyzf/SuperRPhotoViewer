package models

import androidx.compose.runtime.mutableStateListOf
import helper.ext.waypointIndex
import java.io.File
import java.util.regex.Pattern

class WidePreviewImage(var imageFile: File, val allImageFiles: List<File>) {
    val waypointIndex: Int?
        get() {
            return imageFile.waypointIndex()
        }
    var zoomSegmentImages = mutableStateListOf<ZoomSegmentImage>()

    init {
        println("_Z_${waypointIndex}-")
        val pattern = Pattern.compile("_Z_${waypointIndex}-")
        val zoomImageFiles = allImageFiles.filter {
            val matcher = pattern.matcher(it.nameWithoutExtension)
            matcher.find()
        }.map {
            ZoomSegmentImage(it)
        }
        zoomSegmentImages.apply {
            clear()
            addAll(zoomImageFiles)
        }
    }
}