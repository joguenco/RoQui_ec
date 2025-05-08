package dev.joguenco.roqui.util

import java.io.File
import java.time.LocalDateTime
import java.util.Date

class FilesUtil {

    companion object {
        fun directory(path: String, date: Date): String {
            val dateLocal: LocalDateTime = java.sql.Timestamp(date.time).toLocalDateTime()
            val year = dateLocal.year
            val month = dateLocal.month.value

            val directory = path + "${File.separatorChar}" + year + "${File.separatorChar}" + month

            val folder = File(directory)

            if (!folder.isDirectory) {
                folder.mkdirs()
            }

            return directory
        }

        fun isDirectoryExists(path: String): Boolean {
            val file = File(path)
            return file.exists() && file.isDirectory
        }

        fun isFileExists(path: String): Boolean {
            val file = File(path)
            return file.exists()
        }

        fun createDirectory(path: String): Boolean {
            val file = File(path)
            return if (!file.exists()) {
                file.mkdirs()
            } else {
                false
            }
        }
    }
}
