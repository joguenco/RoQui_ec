package dev.joguenco.roqui.parameter.service

import dev.joguenco.roqui.parameter.model.Parameter
import dev.joguenco.roqui.util.FilesUtil.Companion.isDirectoryExists
import java.io.File
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class LogoService(
    private val parameterService: ParameterService,
    private val logoFileService: LogoFileService,
) {

    fun getLogoPath(): String {
        return parameterService.getLogoPath()
    }

    fun uploadLogo(file: MultipartFile): Boolean {
        val directory = parameterService.getBaseDirectory() + File.separatorChar + "image"
        if (!isDirectoryExists(directory)) {
            return false
        }

        logoFileService.uploadFile(file)
        return true
    }

    fun findByName(name: String): Parameter {
        return parameterService.findByName(name)
    }

    fun update(parameter: Parameter) {
        parameterService.update(parameter)
    }
}
