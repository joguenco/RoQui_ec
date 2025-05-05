package dev.joguenco.roqui.parameter.service

import dev.joguenco.roqui.parameter.model.Parameter
import dev.joguenco.roqui.parameter.repository.CustomParameterRepository
import dev.joguenco.roqui.util.OwnEncryption
import java.io.File
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class ParameterService(private val parameterRepository: CustomParameterRepository) {

    @Value("\${key.property}") lateinit var keyProperty: String

    fun getBaseDirectory(): String {
        return parameterRepository.findValueByName("Base Directory")
    }

    fun getCertificatePath(): String {
        val certificatePath =
            getBaseDirectory() +
                File.separatorChar +
                "certificate" +
                File.separatorChar +
                parameterRepository.findValueByName("Certificate")

        return certificatePath
    }

    fun getCertificatePassword(): String {
        val password = parameterRepository.findValueByName("Certificate Password")
        OwnEncryption.setKey(keyProperty)
        return OwnEncryption.decrypt(password)
    }

    fun getPathLogo(): String {
        return parameterRepository.findValueByName("Logo")
    }

    fun findByName(name: String): Parameter {
        return parameterRepository.findByName(name)
    }

    fun update(parameter: Parameter) {
        parameterRepository.update(parameter)
    }
}
