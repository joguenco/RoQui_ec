package dev.joguenco.roqui.parameter.service

import dev.joguenco.roqui.parameter.dto.EmailServerHttpDto
import org.springframework.stereotype.Service

@Service
class EmailClientHttpService(private val parameterService: ParameterService) {

    fun getEmailServerConfiguration(): EmailServerHttpDto {
        val server =
            try {
                parameterService.findValueByName("Email HTTP Server")
            } catch (e: Exception) {
                ""
            }
        val token =
            try {
                parameterService.findValueByName("Email HTTP Server Token")
            } catch (e: Exception) {
                ""
            }

        return EmailServerHttpDto(server, token)
    }

    fun update(emailServerDto: EmailServerHttpDto): Boolean {
        return try {
            val emailServerParam = parameterService.findByName("Email HTTP Server")
            emailServerParam.value = emailServerDto.url
            parameterService.update(emailServerParam)

            val emailServerTokenParam = parameterService.findByName("Email HTTP Server Token")
            emailServerTokenParam.value = emailServerDto.token
            parameterService.update(emailServerTokenParam)

            true
        } catch (e: Exception) {
            false
        }
    }
}
