package dev.joguenco.roqui.parameter.service

import dev.joguenco.roqui.parameter.model.Parameter
import dev.joguenco.roqui.util.FilesUtil.Companion.isDirectoryExists
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class ResourceService(
    private val parameterService: ParameterService,
    private val fileService: FileService,
) {

    fun getLogoJpegPath(): String {
        return parameterService.getLogoJpegPath()
    }

    fun getLogoPngPath(): String {
        return parameterService.getLogoPngPath()
    }

    fun uploadResource(file: MultipartFile): Boolean {
        val directory = parameterService.getResourceDirectory()
        if (!isDirectoryExists(directory)) {
            return false
        }

        fileService.uploadFile(file)
        return true
    }

    fun findByName(name: String): Parameter {
        return parameterService.findByName(name)
    }

    fun update(parameter: Parameter) {
        parameterService.update(parameter)
    }

    fun getResource(name: String): String {
        val parameter =
            try {
                parameterService.findByName(name)
            } catch (e: Exception) {
                throw Exception("Parameter $name not found in parameters")
            }
        if (fileService.existResource(parameter.value!!)) {
            return parameter.value ?: ""
        } else {
            throw Exception("Resource $name not found in resource directory")
        }
    }
}
