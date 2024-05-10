package models

import androidx.compose.ui.geometry.Rect
import org.jetbrains.skia.Pattern
import java.io.File

class ZoomPhoto(val photoFile: File) {

    companion object {
        val zoomNamePattern = Pattern.compile("""-(\d+\.\d+)-(\d+\.\d+)-(\d+\.\d+)-(\d+\.\d+)""")
    }

    val inWideRegionF: Rect?
        get() {
            val fileName = photoFile.nameWithoutExtension.replace("d", ".")
            val matcher = zoomNamePattern.matcher(fileName)
            if (!matcher.find()) {
                return null
            } else {
                try {
                    if (matcher.groupCount() == 4) {
                        val left = matcher.group(1).toFloatOrNull() ?: -1f
                        val top = matcher.group(2).toFloatOrNull() ?: -1f
                        val right = matcher.group(3).toFloatOrNull() ?: -1f
                        val bottom = matcher.group(4).toFloatOrNull() ?: -1f
                        return Rect(left, top, right, bottom)
                    } else {
                        return null
                    }
                } catch (e: Exception) {
                    return null
                }
            }
        }
}