package dev.joguenco.roqui.parameter.service

import dev.joguenco.roqui.parameter.dto.EmailEncryption
import dev.joguenco.roqui.parameter.dto.EmailServerSmtpDto
import org.springframework.stereotype.Service

@Service
class EmailClientSmtpService(private val parameterService: ParameterService) {

    fun getEmailServerConfiguration(): EmailServerSmtpDto {

        val configuration = parameterService.getEmailSmtpConfiguration()

        val server = configuration.first { it.name == "Email SMTP Server" }.value!!
        val port = configuration.first { it.name == "Port Email SMTP Server" }.value!!
        val account = configuration.first { it.name == "Email Account" }.value!!
        val password = configuration.first { it.name == "Email Password Account" }.value!!
        val encryption = configuration.first { it.name == "Email Encryption" }.value!!

        return EmailServerSmtpDto(
            server,
            port.toInt(),
            account,
            password,
            when (encryption) {
                "None" -> EmailEncryption.NONE
                "SSL/TLS" -> EmailEncryption.SSL_TLS
                else -> EmailEncryption.NONE
            },
        )
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

            val encryption = parameterService.findByName("Email Encryption")
            encryption.value =
                when (emailServerSmtpDto.encryption) {
                    EmailEncryption.NONE -> "None"
                    EmailEncryption.SSL_TLS -> "SSL/TLS"
                }
            parameterService.update(encryption)

            true
        } catch (e: Exception) {
            false
        }
    }
}
