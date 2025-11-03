package dev.joguenco.roqui.parameter.service

import dev.joguenco.roqui.parameter.dto.EmailServerSmtpDto
import org.springframework.stereotype.Service

@Service
class EmailClientSmtpService(private val parameterService: ParameterService) {

    fun getEmailServerConfiguration(): EmailServerSmtpDto {
        val server =
            try {
                parameterService.findValueByName("Email SMTP Server")
            } catch (e: Exception) {
                ""
            }
        val port =
            try {
                Integer.parseInt(parameterService.findValueByName("Port Email SMTP Server"))
            } catch (e: Exception) {
                0
            }
        val account =
            try {
                parameterService.findValueByName("Email Account")
            } catch (e: Exception) {
                ""
            }
        val password =
            try {
                parameterService.findValueByName("Email Password Account")
            } catch (e: Exception) {
                ""
            }

        return EmailServerSmtpDto(server, port, account, password)
    }

    fun update(emailServerSmtpDto: EmailServerSmtpDto): Boolean {
        return try {
            val urlServer = parameterService.findByName("Email SMTP Server")
            urlServer.value = emailServerSmtpDto.url
            parameterService.update(urlServer)

            val portServer = parameterService.findByName("Port Email SMTP Server")
            portServer.value = emailServerSmtpDto.port.toString()
            parameterService.update(portServer)

            val account = parameterService.findByName("Email Account")
            account.value = emailServerSmtpDto.account
            parameterService.update(account)

            val password = parameterService.findByName("Email Password Account")
            password.value = emailServerSmtpDto.password
            parameterService.update(password)

            true
        } catch (e: Exception) {
            false
        }
    }
}
