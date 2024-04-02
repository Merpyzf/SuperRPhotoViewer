package models

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Rect
import java.io.File
import java.util.regex.Pattern

class ZoomSegmentImage(val imageFile: File) {
    var rawRect by mutableStateOf(Rect(0f, 0f, 0f, 0f))
    var scaledRect by mutableStateOf(Rect(0f, 0f, 0f, 0f))
    
    init {
        initRawRect()
    }
  
    private fun initRawRect() {
        val name = imageFile.nameWithoutExtension
        val pattern = Pattern.compile("_Z_\\d+-\\d+-(\\d+)-(\\d+)-(\\d+)-(\\d+)-zoom")
        val matcher = pattern.matcher(name)
        val isFind = matcher.find()
        if (!isFind) {
            return
        }
        val left = matcher.group(1).toFloatOrNull() ?: 0f
        val top = matcher.group(2).toFloatOrNull() ?: 0f
        val right = matcher.group(3).toFloatOrNull() ?: 0f
        val bottom = matcher.group(4).toFloatOrNull() ?: 0f
        
        rawRect = Rect(left, top, right, bottom)
        scaledRect = Rect(left, top, right, bottom)
    }

    fun scaleRect(scaleWRatio: Float, scaleHRatio: Float) {
        scaledRect = rawRect.scale(scaleWRatio, scaleHRatio)
    }

    private fun Rect.scale(wRatio: Float, hRatio: Float): Rect {
        val left = this.left * wRatio
        val top = this.top * hRatio
        val right = this.right * wRatio
        val bottom = this.bottom * hRatio
        return Rect(left, top, right, bottom)
    }
}