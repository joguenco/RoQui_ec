package dev.joguenco.roqui.parameter.service

import dev.joguenco.roqui.parameter.dto.MailServerDto
import org.springframework.stereotype.Service

@Service
class MailServerService(private val parameterService: ParameterService) {

    fun getMailServerInformation(): MailServerDto {
        val server = parameterService.findValueByName("Mail Server")
        val port = parameterService.findValueByName("Mail Server Port")
        val email = parameterService.findValueByName("Account Mail Server")
        val password = "******"
        val template = parameterService.findValueByName("Template")

        return MailServerDto(server, port.toInt(), email, password, template)
    }
}
