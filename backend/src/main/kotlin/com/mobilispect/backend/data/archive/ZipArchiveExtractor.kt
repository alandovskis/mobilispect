package com.mobilispect.backend.data.archive

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import kotlin.io.path.createTempDirectory

private const val BUFFER_SIZE = 1024

/**
 * An [ArchiveExtractor] that supports zip files.
 */
internal class ZipArchiveExtractor : ArchiveExtractor {
    override fun extract(archive: String): Result<String> {
        return try {
            val destDir = createTempDirectory().toFile()
            val archiveInputStream = ZipInputStream(FileInputStream(archive))
            var zipEntry = archiveInputStream.nextEntry
            while (zipEntry != null) {
                val newFile = newFile(destDir, zipEntry)
                // fix for Windows-created archives
                val parent = newFile.parentFile
                if (!parent.isDirectory && !parent.mkdirs()) {
                    throw IOException("Failed to create directory $parent")
                }

                val out = FileOutputStream(newFile)
                var len: Int
                val buffer = ByteArray(BUFFER_SIZE)
                while (archiveInputStream.read(buffer).also { len = it } > 0) {
                    out.write(buffer, 0, len)
                }
                out.close()
                zipEntry = archiveInputStream.nextEntry
            }
            Result.success(destDir.toString())
        } catch (e: IOException) {
            Result.failure(e)
        }
    }


    @Throws(IOException::class)
    // Source: https://www.baeldung.com/java-compress-and-uncompress
    private fun newFile(destinationDir: File, zipEntry: ZipEntry): File {
        val destFile = File(destinationDir, zipEntry.name)
        val destDirPath: String = destinationDir.canonicalPath
        val destFilePath: String = destFile.canonicalPath
        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw IOException("Entry is outside of the target dir: " + zipEntry.name)
        }
        return destFile
    }
}
