package dev.joguenco.roqui.parameter.service

import dev.joguenco.roqui.parameter.dto.EmailServerDto
import org.springframework.stereotype.Service

@Service
class EmailServerService(private val parameterService: ParameterService) {

    fun getEmailServerConfiguration(): EmailServerDto {
        val server =
            try {
                parameterService.findValueByName("Email Server")
            } catch (e: Exception) {
                ""
            }
        val token =
            try {
                parameterService.findValueByName("Email Server Token")
            } catch (e: Exception) {
                ""
            }

        return EmailServerDto(server, token)
    }

    fun update(emailServerDto: EmailServerDto): Boolean {
        return try {
            val emailServerParam = parameterService.findByName("Email Server")
            emailServerParam.value = emailServerDto.url
            parameterService.update(emailServerParam)

            val emailServerTokenParam = parameterService.findByName("Email Server Token")
            emailServerTokenParam.value = emailServerDto.token
            parameterService.update(emailServerTokenParam)

            true
        } catch (e: Exception) {
            false
        }
    }
}
