package helper

import models.CaptureType
import org.jetbrains.skia.Pattern
import superrphotoviewer.composeapp.generated.resources.Res
import java.io.File
import java.util.regex.Matcher

interface IPhotoNameRegex {
    fun isMatch(): Boolean
    fun captureType(): CaptureType
    fun stage(): Int?
    fun waypointIndex(): Int?
    fun position(): Int?
    fun getAllZoomPhotos(allPhotos: List<File>): List<File>
}

/**
 * 间隔拍照模式下的广角照片正则处理类
 */
class IntervalWidePhotoNameRegex(private val photoName: String) : IPhotoNameRegex {
    // W_stage0-sr-i-waypointIndex-position-left-top-right-bottom
    private val pattern = Pattern.compile("_(\\d+)_(\\d+)_W_stage(\\d+)-sr-i-(\\d+)-(\\d+)")
    private val matcher: Matcher by lazy {
        pattern.matcher(photoName)
    }

    override fun isMatch(): Boolean {
        return matcher.find()
    }

    override fun captureType(): CaptureType {
        return CaptureType.INTERVAL
    }

    fun photoDate(): String? {
        if (matcher.groupCount() < 1) {
            return null
        }
        return try {
            matcher.group(1)
        } catch (e: Exception) {
            null
        }
    }

    fun photoIndex(): String? {
        if (matcher.groupCount() < 2) {
            return null
        }
        return try {
            matcher.group(2)
        } catch (e: Exception) {
            null
        }
    }

    override fun stage(): Int? {
        if (matcher.groupCount() < 3) {
            return null
        }
        return try {
            matcher.group(3).toIntOrNull()
        } catch (e: Exception) {
            null
        }
    }

    override fun waypointIndex(): Int? {
        if (matcher.groupCount() < 4) {
            return null
        }
        return try {
            val waypointIndex = matcher.group(4).toIntOrNull()
            waypointIndex
        } catch (e: Exception) {
            null
        }
    }

    override fun position(): Int? {
        if (matcher.groupCount() < 5) {
            return null
        }
        return try {
            matcher.group(5).toIntOrNull()
        } catch (e: Exception) {
            null
        }
    }

    override fun getAllZoomPhotos(allPhotos: List<File>): List<File> {
        val identifier = "_${photoDate()}_${photoIndex()}_Z_stage${stage()}-sr-i-${waypointIndex()}-${position()}"
        println(identifier)
        return allPhotos.filter {
            it.nameWithoutExtension.contains(
                identifier
            )
        }
    }
}

/**
 * 普通拍照模式下的广角照片正则处理类
 */
class NormalWidePhotoNameRegex(private val photoName: String) : IPhotoNameRegex {
    // W_stage0-sr-n-waypointIndex-position-left-top-right-bottom
    private val pattern = Pattern.compile("_(\\d+)_(\\d+)_W_stage(\\d+)-sr-n-(\\d+)-(\\d+)")
    private val matcher: Matcher by lazy {
        pattern.matcher(photoName)
    }

    override fun isMatch(): Boolean {
        return matcher.find()
    }

    override fun captureType(): CaptureType {
        return CaptureType.NORMAL
    }

    fun photoDate(): String? {
        if (matcher.groupCount() < 1) {
            return null
        }
        return try {
            matcher.group(1)
        } catch (e: Exception) {
            null
        }
    }

    fun photoIndex(): String? {
        if (matcher.groupCount() < 2) {
            return null
        }
        return try {
            matcher.group(2)
        } catch (e: Exception) {
            null
        }
    }

    override fun stage(): Int? {
        if (matcher.groupCount() < 3) {
            return null
        }
        return try {
            matcher.group(3).toIntOrNull()
        } catch (e: Exception) {
            null
        }
    }

    override fun waypointIndex(): Int? {
        if (matcher.groupCount() < 4) {
            return null
        }
        return try {
            matcher.group(4).toIntOrNull()
        } catch (e: Exception) {
            null
        }
    }

    override fun position(): Int? {
        if (matcher.groupCount() < 5) {
            return null
        }
        return try {
            matcher.group(5).toIntOrNull()
        } catch (e: Exception) {
            null
        }
    }

    override fun getAllZoomPhotos(allPhotos: List<File>): List<File> {
        return allPhotos.filter {
            it.nameWithoutExtension.contains(
                "_${photoDate()}_${photoIndex()}_Z_stage${stage()}-sr-n-${waypointIndex()}-${position()}"
            )
        }
    }
}

/**
 * 超清矩阵拍摄模式下的广角照片名称正则处理类
 */
class SuperRWidePhotoNameRegex(
    private val photoName: String
) : IPhotoNameRegex {
    // W_stage0-sr-wide-waypointIndex-position
    private val pattern = Pattern.compile("W_stage(\\d+)-sr-wide-(\\d+)-(\\d+)")
    private val matcher: Matcher by lazy {
        pattern.matcher(photoName)
    }

    override fun isMatch(): Boolean {
        return matcher.find()
    }

    override fun captureType(): CaptureType {
        return CaptureType.SUPER_R
    }

    override fun stage(): Int? {
        if (matcher.groupCount() < 1) {
            return null
        }
        return try {
            matcher.group(1).toIntOrNull()
        } catch (e: Exception) {
            null
        }
    }

    override fun waypointIndex(): Int? {
        if (matcher.groupCount() < 2) {
            return null
        }
        return try {
            matcher.group(2).toIntOrNull()
        } catch (e: Exception) {
            null
        }
    }

    override fun position(): Int? {
        if (matcher.groupCount() < 3) {
            return null
        }
        return try {
            matcher.group(3).toIntOrNull()
        } catch (e: Exception) {
            null
        }
    }

    override fun getAllZoomPhotos(allPhotos: List<File>): List<File> {
        return allPhotos.filter {
            it.nameWithoutExtension.contains(
                "Z_stage${stage()}-sr-zoom-${waypointIndex()}-${position()}"
            )
        }
    }
}

fun main() {
//    val nameRegex = SuperRWidePhotoNameRegex(photoName = "DJI_20240508131139_0001_W_stage2-sr-wide-2-0.JPG")
    val nameRegex =
        IntervalWidePhotoNameRegex(photoName = "DJI_20240508132004_0001_W_stage2-sr-i-21-9-0d43-0d40-0d57-0d60")
//    val nameRegex =
//        NormalWidePhotoNameRegex(photoName = "DJI_20240508132004_0001_W_stage2-sr-n-21-9-0d43-0d40-0d57-0d60")
    val isMatch = nameRegex.isMatch()
    println("isMatch: ${isMatch}")
    val stage = nameRegex.stage()
    println("stage: ${stage}")
    val waypointIndex = nameRegex.waypointIndex()
    println("waypointIndex: $waypointIndex")
    val position = nameRegex.position()
    println("position: $position")
    val photoIndex = nameRegex.photoIndex()
    println("photoIndex: $photoIndex")
}