package dev.joguenco.roqui.parameter.service

import dev.joguenco.roqui.parameter.dto.MailServerDto
import org.springframework.stereotype.Service

@Service
class MailServerService(private val parameterService: ParameterService) {

    fun getMailServerInformation(): MailServerDto {
        val server =
            try {
                parameterService.findValueByName("Mail Server")
            } catch (e: Exception) {
                ""
            }
        val port =
            try {
                parameterService.findValueByName("Mail Server Port")
            } catch (e: Exception) {
                "0"
            }
        val email =
            try {
                parameterService.findValueByName("Account Mail Server")
            } catch (e: Exception) {
                "0"
            }
        val template =
            try {
                parameterService.findValueByName("Template")
            } catch (e: Exception) {
                ""
            }

        return MailServerDto(server, port.toInt(), email, "", template)
    }
}
