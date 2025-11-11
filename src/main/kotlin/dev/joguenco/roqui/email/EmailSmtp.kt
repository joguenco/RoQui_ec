package dev.joguenco.roqui.email

import dev.joguenco.roqui.information.service.InformationService
import dev.joguenco.roqui.parameter.model.Parameter
import dev.joguenco.roqui.parameter.service.ParameterService
import java.io.File
import java.nio.charset.StandardCharsets
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
        val (subject, identification) = getSubjectAndIdentification()
        val receiverEmail = informationService.getEmailByIdentification(identification)

        if (!receiverEmail.isNullOrEmpty()) {
            val configuration = parameterService.getEmailSmtpConfiguration()
            val legalName = informationService.getLegalNameOfTaxpayer()
            initializeSmtpEmail(configuration, legalName)

            val baseDirectory = parameterService.getBaseDirectory()
            htmlEmail.subject = subject

            val cid = htmlEmail.embed(File(parameterService.getLogoPath()))
            val message = getHtmlMessage(parameterService.getResourcePath("mail.html"))
            htmlEmail.setHtmlMsg(message.replace("cid_replace", cid))

            htmlEmail.addTo(receiverEmail)
            htmlEmail.send()
        }
    }

    private fun initializeSmtpEmail(configuration: MutableList<Parameter>, sender: String) {
        val server = configuration.first { it.name == "Email SMTP Server" }.value!!
        val port = configuration.first { it.name == "Port Email SMTP Server" }.value!!
        val account = configuration.first { it.name == "Email Account" }.value!!
        val password = configuration.first { it.name == "Email Password Account" }.value!!
        val encryption = configuration.first { it.name == "Email Encryption" }.value!!

        htmlEmail.hostName = server
        htmlEmail.setSmtpPort(port.toInt())
        if (encryption == "SSL/TLS") {
            htmlEmail.sslSmtpPort = port
            htmlEmail.isSSLOnConnect = true
        }

        htmlEmail.setFrom(account, sender)
        htmlEmail.setAuthenticator(DefaultAuthenticator(account, password))
    }

    private fun getHtmlMessage(templatePath: String): String {
        val encoded = File(templatePath).readBytes()
        return String(encoded, StandardCharsets.UTF_8)
    }

    private fun attachFile(file: File): EmailAttachment {
        val attachment = EmailAttachment()
        attachment.path = file.absolutePath
        attachment.name = file.name
        attachment.disposition = EmailAttachment.ATTACHMENT
        return attachment
    }

    private fun setXmlAttachment(file: File, description: String) {
        xmlAttachment = attachFile(file)
        xmlAttachment.description = description
    }

    private fun setPdfAttachment(file: File, description: String) {
        pdfAttachment = attachFile(file)
        pdfAttachment.description = description
    }

    private fun getSubjectAndIdentification(): Pair<String, String> {
        when (code) {
            "FV" -> {
                val document =
                    informationService.getInvoice(code, number).establishment +
                        "-" +
                        informationService.getInvoice(code, number).emissionPoint +
                        "-" +
                        informationService.getInvoice(code, number).sequence
                return Pair(
                    "Factura Electrónica $document",
                    informationService.getInvoice(code, number).identification!!,
                )
            }
        }
        return Pair("", "")
    }
}
