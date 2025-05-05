package dev.joguenco.roqui.certificate.service

import dev.joguenco.roqui.exception.FileImportException
import dev.joguenco.roqui.parameter.service.ParameterService
import dev.joguenco.roqui.util.OwnEncryption
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import org.apache.coyote.BadRequestException
import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class CertificateFileService(private val parameterService: ParameterService) {

    @Value("\${key.property}") lateinit var keyProperty: String

    fun uploadFile(file: MultipartFile, password: String): String {
        throwIfFileEmpty(file)
        try {
            val path = renameCurrentFile()
            val root = Paths.get(path)

            Files.copy(
                file.inputStream,
                root.resolve(file.originalFilename),
                StandardCopyOption.REPLACE_EXISTING,
            )

            updateCertificateFile(file.originalFilename!!)
            updateCertificatePassword(password)

            return "Load file complete"
        } catch (ex: Exception) {
            println(ex.message)
            throw FileImportException("Error to load the file")
        }
    }

    fun renameCurrentFile(): String {
        val p12File = parameterService.getCertificatePath()

        val fileAndPath = File(p12File)
        if (fileAndPath.exists()) {
            val renameFile = File(p12File + "." + DateTime.now())
            fileAndPath.renameTo(renameFile)
        }

        return fileAndPath.parentFile.absolutePath
    }

    fun updateCertificateFile(file: String) {
        val parameter = parameterService.findByName("Certificate")

        parameter.value = file
        parameterService.update(parameter)
    }

    fun updateCertificatePassword(password: String) {
        val parameter = parameterService.findByName("Certificate Password")

        OwnEncryption.setKey(keyProperty)
        parameter.value = OwnEncryption.encrypt(password)
        parameterService.update(parameter)
    }

    private fun throwIfFileEmpty(file: MultipartFile) {
        if (file.isEmpty) throw BadRequestException("Empty file")
    }
}
