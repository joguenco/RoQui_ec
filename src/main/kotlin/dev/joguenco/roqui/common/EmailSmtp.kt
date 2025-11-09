package dev.joguenco.roqui.common

import dev.joguenco.roqui.information.service.InformationService
import dev.joguenco.roqui.parameter.model.Parameter
import dev.joguenco.roqui.parameter.service.ParameterService
import org.apache.commons.mail.DefaultAuthenticator
import org.apache.commons.mail.EmailAttachment
import org.apache.commons.mail.HtmlEmail

class EmailSmtp(
    val code: String,
    val number: String,
    val parameterService: ParameterService,
    val informationService: InformationService,
) {

    private val htmlEmail = HtmlEmail()
    private var pdfAttachment = EmailAttachment()
    private var xmlAttachment = EmailAttachment()

    fun send() {
        val identification: String =
            when (code) {
                "FV" -> {
                    informationService.getInvoice(code, number).identification
                }
                else -> ""
            }.toString()

        val receiverEmail = informationService.getEmailByIdentification(identification)

        if (!receiverEmail.isNullOrEmpty()) {
            val configuration = parameterService.getEmailSmtpConfiguration()
            val legalName = informationService.getLegalNameOfTaxpayer()
            initializeSmtpEmail(configuration, legalName)

            TODO("Implement email sending functionality")
        }
    }

    private fun initializeSmtpEmail(configuration: MutableList<Parameter>, sender: String) {
        val server = configuration.first { it.name == "Email SMTP Server" }.value!!
        val port = configuration.first { it.name == "Port Email SMTP Server" }.value!!
        val account = configuration.first { it.name == "Email Account" }.value!!
        val password = configuration.first { it.name == "Email Password Account" }.value!!

        htmlEmail.hostName = server
        htmlEmail.setSmtpPort(port.toInt())
        htmlEmail.isSSLOnConnect = true

        htmlEmail.setFrom(account, sender)
        htmlEmail.setAuthenticator(DefaultAuthenticator(account, password))
    }
}
