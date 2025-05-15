package dev.joguenco.roqui.parameter.service

import dev.joguenco.roqui.exception.FileImportException
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import org.apache.coyote.BadRequestException
import org.joda.time.DateTime
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class LogoFileService(private val parameterService: ParameterService) {

    fun uploadFile(file: MultipartFile): String {
        throwIfFileEmpty(file)
        try {
            val path = renameCurrentFile()
            val root = Paths.get(path)

            Files.copy(
                file.inputStream,
                root.resolve(file.originalFilename),
                StandardCopyOption.REPLACE_EXISTING,
            )

            updateFile(file.originalFilename!!)

            return "Load file complete"
        } catch (ex: Exception) {
            println(ex.message)
            throw FileImportException("Error to load the file")
        }
    }

    fun updateFile(file: String) {
        val parameter = parameterService.findByName("Logo")

        parameter.value = file
        parameterService.update(parameter)
    }

    fun renameCurrentFile(): String {
        val logo = parameterService.getLogoPath()

        val fileAndPath = File(logo)
        if (fileAndPath.exists()) {
            val renameFile = File(logo + "." + DateTime.now())
            fileAndPath.renameTo(renameFile)
        }

        return fileAndPath.parentFile.absolutePath
    }

    private fun throwIfFileEmpty(file: MultipartFile) {
        if (file.isEmpty) throw BadRequestException("Empty file")
    }
}
