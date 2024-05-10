package viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import helper.IntervalWidePhotoNameRegex
import helper.NormalWidePhotoNameRegex
import helper.SuperRWidePhotoNameRegex
import kotlinx.coroutines.*
import models.PreviewWidePhoto
import models.ZoomPhoto
import java.io.File
import java.io.FileNotFoundException
import java.util.regex.Pattern

class AppViewModel {
    private val viewModelScopeLazy = lazy {
        CoroutineScope(SupervisorJob() + Dispatchers.Main)
    }
    val scope by viewModelScopeLazy

    var photoDirPath by mutableStateOf("")
    var photoFiles = mutableListOf<File>()

    // 首页展示的超清矩阵广角照片
    val previewWidePhotos = mutableStateListOf<PreviewWidePhoto>()

    var isOpenWidePhoto by mutableStateOf(false)
    var currOpenWidePhoto by mutableStateOf<PreviewWidePhoto?>(null)

    suspend fun setPhotoDirPath(photoDirPath: String) {
        this.photoDirPath = photoDirPath
        withContext(Dispatchers.IO) {
            val photoDir = File(this@AppViewModel.photoDirPath)
            if (!photoDir.exists()) {
                throw FileNotFoundException("出错了，所选文件夹不存在")
            } else if (photoDir.listFiles()!!.isEmpty()) {
                throw FileNotFoundException("所选文件夹下没有任何文件")
            } else {
                photoFiles.clear()
                loadPhotoFiles(this, File(photoDirPath), photoFiles)
                previewWidePhotos.clear()
                previewWidePhotos.addAll(filterSuperRWidePhotos(photoFiles))
                println("photoFiles.size：${photoFiles.size}")
            }
        }
    }

    private fun loadPhotoFiles(scope: CoroutineScope, file: File, photoFiles: MutableList<File>) {
        if (!scope.isActive) {
            return
        }
        println("scope.isActive: ${scope.isActive} ,loadPhotoFiles: ${file.path}")
        if (!file.exists()) {
            return
        }
        if (file.isDirectory) {
            val listFiles = file.listFiles() ?: return
            for (f in listFiles) {
                loadPhotoFiles(scope, f, photoFiles)
            }
        } else {
            if (file.length() != 0L && (file.extension.equals("png", ignoreCase = true) || file.extension.equals(
                    "jpg",
                    ignoreCase = true
                ))
            ) {
                photoFiles.add(file)
            }
        }
    }

    private fun filterSuperRWidePhotos(photoFiles: List<File>): List<PreviewWidePhoto> {
        val previewWidePhotos = mutableListOf<PreviewWidePhoto>()
        for (photoFile in photoFiles) {
            val photoNameRegexList = mutableListOf(
                IntervalWidePhotoNameRegex(photoFile.nameWithoutExtension),
                NormalWidePhotoNameRegex(photoFile.nameWithoutExtension),
                SuperRWidePhotoNameRegex(photoFile.nameWithoutExtension)
            )

            for (photoNameRegex in photoNameRegexList) {
                if (photoNameRegex.isMatch()) {
                    val widePhoto =
                        PreviewWidePhoto(photoNameRegex.captureType(), photoFile).apply {
                            this.waypointIndex = photoNameRegex.waypointIndex()
                            this.stage = photoNameRegex.stage()
                        }
                    previewWidePhotos.add(widePhoto)
                    val zoomPhotos = photoNameRegex.getAllZoomPhotos(photoFiles).map { ZoomPhoto(it) }
                    widePhoto.zoomPhotos.apply {
                        this.clear()
                        this.addAll(zoomPhotos)
                    }
                    break
                }
            }
        }
        previewWidePhotos.sortBy {
            it.waypointIndex ?: 0
        }
        return previewWidePhotos
    }

    fun clear() {
        if (viewModelScopeLazy.isInitialized()) {
            viewModelScopeLazy.value.cancel()
        }
    }
}

fun main() {
    val name = "DJI_20240308172418_0001_W_widepreview-1"
    val pattern = Pattern.compile("W_widepreview-(\\d+)")
    val matcher = pattern.matcher(name)
    val matches = matcher.find()
    print(matches)

}