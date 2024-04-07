package helper

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.Uri
import coil3.annotation.InternalCoilApi
import coil3.compose.LocalPlatformContext
import coil3.decode.DataSource
import coil3.decode.ImageSource
import coil3.fetch.FetchResult
import coil3.fetch.Fetcher
import coil3.fetch.SourceFetchResult
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.Options
import coil3.util.MimeTypeMap
import okio.Path.Companion.toPath
import org.jetbrains.skiko.OS
import org.jetbrains.skiko.hostOs

val supportWindowsImageLoader : ImageLoader by lazy {
    ImageLoader.Builder(PlatformContext.INSTANCE)
        .components {
            add(OkHttpNetworkFetcherFactory())
            add(WindowsFileUriFetcher.Factory())
        }
        .build()
}

internal class WindowsFileUriFetcher(
    private val uri: Uri,
    private val options: Options,
) : Fetcher {

    @OptIn(InternalCoilApi::class, InternalCoilApi::class, InternalCoilApi::class)
    override suspend fun fetch(): FetchResult {
        val path = uri.toString().toPath()
        return SourceFetchResult(
            source = ImageSource(path, options.fileSystem),
            mimeType = MimeTypeMap.getMimeTypeFromExtension(path.name.substringAfterLast('.', "")),
            dataSource = DataSource.DISK,
        )
    }

    class Factory : Fetcher.Factory<Uri> {

        private val regex = "^[a-zA-Z]:\\\\.*".toRegex()

        override fun create(
            data: Uri,
            options: Options,
            imageLoader: ImageLoader,
        ): Fetcher? {
            if (hostOs != OS.Windows || !regex.matches(data.toString())) return null
            return WindowsFileUriFetcher(data, options)
        }
    }
}