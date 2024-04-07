package viewmodels

import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.*
import models.WidePreviewImage
import java.io.File
import java.util.regex.Pattern

class AppViewModel {
    private val viewModelScopeLazy = lazy {
        CoroutineScope(SupervisorJob() + Dispatchers.Main)
    }
    private val viewModelScope by viewModelScopeLazy

    val superRPreviewImages = mutableStateListOf<WidePreviewImage>()

    init {

    }

    fun getSuperRImageList(dirPath: String) {
        viewModelScope.launch() {
            println("viewModelScope launch 所在线程：${Thread.currentThread().name}")
            val result = withContext(Dispatchers.IO) {
                val allFileList = getAllFilesRecursively(dirPath)
                val pattern = Pattern.compile("W_superR-widepreview-(\\d+)")
                val allWideImageFileList = allFileList.filter {
                    pattern.matcher(it.nameWithoutExtension).find()
                }.map {
                    WidePreviewImage(it, allFileList)
                }.sortedBy {
                    it.imageFile.lastModified()
                }

                print("广角图片数量：${allWideImageFileList.size}")
                superRPreviewImages.apply {
                    clear()
                    addAll(allWideImageFileList)
                }
            }
        }
    }

    private fun getAllFilesRecursively(dirPath: String): List<File> {
        val fileList = mutableListOf<File>()
        val dir = File(dirPath)
        for (file in dir.listFiles()!!) {
            if (file.isDirectory) {
                getAllFilesRecursively(file.path).also {
                    fileList.addAll(it)
                }
            } else {
                fileList.add(file)
            }
        }
        return fileList
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