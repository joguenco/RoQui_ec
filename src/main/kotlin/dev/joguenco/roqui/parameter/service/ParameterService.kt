package dev.joguenco.roqui.parameter.service

import dev.joguenco.roqui.parameter.repository.CustomParameterRepository
import org.springframework.stereotype.Service

@Service
class ParameterService(private val parameterRepository: CustomParameterRepository) {

    fun getBaseDirectory(): String {
        return parameterRepository.findValueByName("Base Directory")
    }

    fun getCertificatePath(): String {
        return parameterRepository.findValueByName("Certificate")
    }

    fun getCertificatePassword(): String {
        return parameterRepository.findValueByName("Certificate Password")
    }

    fun getPathLogo(): String {
        return parameterRepository.findValueByName("Logo")
    }
}
