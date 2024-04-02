package helper.ext

import java.io.File
import java.util.regex.Pattern

fun File.isWidePreviewImage(): Boolean {
    val name = this.nameWithoutExtension
    val pattern = Pattern.compile("W_widepreview-\\d+")
    return pattern.matcher(name).find()
}

fun File.waypointIndex(): Int? {
    val name = this.nameWithoutExtension
    val pattern = Pattern.compile("W_superR-widepreview-(\\d+)")
    val matcher = pattern.matcher(name)
    return if (matcher.find()){
         matcher.group(1).toInt()
    }else{
        null
    }
}

fun File.isSuperRSegmentImage(): Boolean {
    val name = this.nameWithoutExtension
    val pattern = Pattern.compile("_Z_(\\d+)-(\\d+)-(\\d+)-(\\d+)-(\\d+)-(\\d+)-zoom")
    return pattern.matcher(name).find()
}