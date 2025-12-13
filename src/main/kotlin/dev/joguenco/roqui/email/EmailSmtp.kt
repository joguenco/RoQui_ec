package dev.joguenco.roqui.email

import dev.joguenco.roqui.information.service.InformationService
import dev.joguenco.roqui.parameter.model.Parameter
import dev.joguenco.roqui.parameter.service.ParameterService
import dev.joguenco.roqui.util.DateUtil
import dev.joguenco.roqui.util.FilesUtil
import dev.joguenco.roqui.util.OwnEncryption
import java.io.File
import java.nio.charset.StandardCharsets
import org.apache.commons.mail2.jakarta.EmailAttachment
import org.apache.commons.mail2.jakarta.HtmlEmail

class EmailSmtp(
    val code: String,
    val number: String,
    val parameterService: ParameterService,
    val informationService: InformationService,
) {

    constructor(
        parameterService: ParameterService,
        informationService: InformationService,
    ) : this("", "", parameterService, informationService)

    private val htmlEmail = HtmlEmail()

    fun send(): Boolean {
        if (!informationService.isAuthorized(code, number) || code == "" || number == "") {
            return false
        }

        val (serieNumber, identification, accessKey) = getSubjectIdentificationAndAccessKey()
        val receiverEmail = informationService.getEmailByIdentification(identification)

        if (!receiverEmail.isNullOrEmpty()) {
            val configuration = parameterService.getEmailSmtpConfiguration()
            val legalName = informationService.getLegalNameOfTaxpayer()
            initializeSmtpEmail(configuration, legalName)

            val baseDirectory = parameterService.getBaseDirectory()
            htmlEmail.subject = getDocumentTypeName() + " $serieNumber"

            var message = getHtmlMessage(parameterService.getEmailTemplate())
            message = message.replace("Nombre_Empresa", legalName)
            message = message.replace("Tipo_Comprobante", getDocumentTypeName("html"))
            message = message.replace("Numero_Comprobante", serieNumber)

            val cid = htmlEmail.embed(File(parameterService.getLogoPngPath()))
            htmlEmail.setHtmlMsg(message.replace("cid_replace", cid))

            val xml = attachmentResource(baseDirectory, accessKey, "XML")
            val pdf = attachmentResource(baseDirectory, accessKey, "PDF")

            htmlEmail.attach(xml)
            htmlEmail.attach(pdf)
            htmlEmail.addTo(receiverEmail)
            htmlEmail.send()

            return true
        }
        return false
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

        OwnEncryption.setKey(parameterService.keyProperty)
        htmlEmail.setAuthentication(account, OwnEncryption.decrypt(password))
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

    private fun attachmentResource(
        baseDirectory: String,
        accessKey: String,
        description: String,
    ): EmailAttachment {
        var resourceToAttach = EmailAttachment()

        if (description == "XML") {
            val authorizedPath =
                FilesUtil.directory(
                    baseDirectory + "${File.separatorChar}authorized",
                    DateUtil.accessKeyToDate(accessKey),
                )
            resourceToAttach =
                attachFile(File(authorizedPath + "${File.separatorChar}$accessKey.xml"))
        } else {
            val authorizedPath =
                FilesUtil.directory(
                    baseDirectory + "${File.separatorChar}pdf",
                    DateUtil.accessKeyToDate(accessKey),
                )
            resourceToAttach =
                attachFile(File(authorizedPath + "${File.separatorChar}$accessKey.pdf"))
        }

        resourceToAttach.description = description
        return resourceToAttach
    }

    /*
    returns: serieNumber, identification, accessKey
     */
    private fun getSubjectIdentificationAndAccessKey(): Triple<String, String, String> {
        when (code) {
            "FV" -> {
                val invoice = informationService.getInvoice(code, number)
                val serieNumber =
                    invoice.establishment + "-" + invoice.emissionPoint + "-" + invoice.sequence
                val accessKey = invoice.accessKey!!

                return Triple(
                    serieNumber,
                    informationService.getInvoice(code, number).identification!!,
                    accessKey,
                )
            }
            "DV" -> {
                val creditNote = informationService.getCreditNote(code, number)
                val serieNumber =
                    creditNote.establishment +
                        "-" +
                        creditNote.emissionPoint +
                        "-" +
                        creditNote.sequence
                val accessKey = creditNote.accessKey!!

                return Triple(
                    serieNumber,
                    informationService.getCreditNote(code, number).identification!!,
                    accessKey,
                )
            }
        }
        return Triple("", "", "")
    }

    /*
    type: text or html
     */
    private fun getDocumentTypeName(type: String = "text"): String {
        return when (code) {
            "FV" -> "Factura"
            "DV" -> if (type == "text") "Nota de Crédito" else "Nota de Cr&eacute;dito"
            else -> ""
        }
    }

    fun sendTest(emailAddress: String): Pair<Boolean, String> {
        val configuration = parameterService.getEmailSmtpConfiguration()
        val legalName = informationService.getLegalNameOfTaxpayer()
        initializeSmtpEmail(configuration, legalName)

        htmlEmail.subject = "RoQui Ecuador"

        var message = getHtmlMessage(parameterService.getEmailTemplate())
        message = message.replace("Nombre_Empresa", legalName)
        message = message.replace("Tipo_Comprobante", "Mi Comprobante de Prueba")
        message = message.replace("Numero_Comprobante", "000-000-000000001")
        val cid = htmlEmail.embed(File(parameterService.getLogoPngPath()))
        htmlEmail.setHtmlMsg(message.replace("cid_replace", cid))

        htmlEmail.addTo(emailAddress)
        try {
            htmlEmail.send()
        } catch (e: Exception) {
            return Pair(false, e.message.toString())
        }

        return Pair(true, "Test email sent successfully")
    }
}
