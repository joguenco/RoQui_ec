package dev.joguenco.roqui.electronic

import dev.joguenco.definition.AutorizacionEstado
import dev.joguenco.definition.Estado
import dev.joguenco.roqui.electronic.ErrorMessage.getErrorResponse
import dev.joguenco.roqui.electronic.model.Document
import dev.joguenco.roqui.electronic.send.SendXML
import dev.joguenco.roqui.electronic.send.WebService
import dev.joguenco.roqui.electronic.service.DocumentService
import dev.joguenco.roqui.electronic.sign.SignerXml
import dev.joguenco.roqui.electronic.xml.BuildCreditNote
import dev.joguenco.roqui.electronic.xml.BuildDebitNote
import dev.joguenco.roqui.electronic.xml.BuildDeliveryNote
import dev.joguenco.roqui.electronic.xml.BuildInvoice
import dev.joguenco.roqui.electronic.xml.BuildLiquidation
import dev.joguenco.roqui.electronic.xml.BuildWithhold
import dev.joguenco.roqui.electronic.xml.PdfBuilder
import dev.joguenco.roqui.electronic.xml.validateXmlAgainstXsd
import dev.joguenco.roqui.email.EmailSmtp
import dev.joguenco.roqui.information.service.InformationService
import dev.joguenco.roqui.invoice.service.InvoiceService
import dev.joguenco.roqui.liquidation.service.LiquidationService
import dev.joguenco.roqui.note.credit.service.CreditNoteService
import dev.joguenco.roqui.note.debit.service.DebitNoteService
import dev.joguenco.roqui.note.delivery.service.DeliveryNoteService
import dev.joguenco.roqui.parameter.service.ParameterService
import dev.joguenco.roqui.util.DateUtil
import dev.joguenco.roqui.util.FilesUtil
import dev.joguenco.roqui.withhold.service.WithholdService
import java.io.File
import kotlin.NoSuchElementException
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import recepcion.ws.sri.gob.ec.Comprobante
import recepcion.ws.sri.gob.ec.RespuestaSolicitud

class ElectronicDocument(
    val code: String,
    val number: String,
    private val webService: WebService,
    private val parameterService: ParameterService,
    private val documentService: DocumentService,
) {

    private var invoiceService: InvoiceService? = null
    private var creditNoteService: CreditNoteService? = null
    private var debitNoteService: DebitNoteService? = null
    private var liquidationService: LiquidationService? = null
    private var withholdService: WithholdService? = null
    private var deliveryNoteService: DeliveryNoteService? = null

    constructor(
        code: String,
        number: String,
        invoiceService: InvoiceService,
        webService: WebService,
        parameterService: ParameterService,
        documentService: DocumentService,
    ) : this(code, number, webService, parameterService, documentService) {
        this.invoiceService = invoiceService
    }

    constructor(
        code: String,
        number: String,
        creditNoteService: CreditNoteService,
        webService: WebService,
        parameterService: ParameterService,
        documentService: DocumentService,
    ) : this(code, number, webService, parameterService, documentService) {
        this.creditNoteService = creditNoteService
    }

    // constructor
    constructor(
        code: String,
        number: String,
        debitNoteService: DebitNoteService,
        webService: WebService,
        parameterService: ParameterService,
        documentService: DocumentService,
    ) : this(code, number, webService, parameterService, documentService) {
        this.debitNoteService = debitNoteService
    }

    constructor(
        code: String,
        number: String,
        liquidationService: LiquidationService,
        webService: WebService,
        parameterService: ParameterService,
        documentService: DocumentService,
    ) : this(code, number, webService, parameterService, documentService) {
        this.liquidationService = liquidationService
    }

    constructor(
        code: String,
        number: String,
        withholdService: WithholdService,
        webService: WebService,
        parameterService: ParameterService,
        documentService: DocumentService,
    ) : this(code, number, webService, parameterService, documentService) {
        this.withholdService = withholdService
    }

    constructor(
        code: String,
        number: String,
        deliveryNoteService: DeliveryNoteService,
        webService: WebService,
        parameterService: ParameterService,
        documentService: DocumentService,
    ) : this(code, number, webService, parameterService, documentService) {
        this.deliveryNoteService = deliveryNoteService
    }

    // FIN
    private var accessKey: String = ""
    private var baseDirectory = ""

    init {
        baseDirectory = parameterService.getBaseDirectory()
    }

    fun setAccessKey(accessKey: String) {
        this.accessKey = accessKey
    }

    fun process(type: TypeDocument): String {
        var statusResponse = Estado.NO_PROCESADO.descripcion
        var generatedDirectory = ""
        val classLoader = ElectronicDocument::class.java.classLoader
        val xsdFolder = classLoader.getResource("./xsd").path
        var xsdFile = ""

        if (type == TypeDocument.FACTURA) {
            val build = BuildInvoice(code, number, baseDirectory, invoiceService!!)
            val result = build.xml()
            generatedDirectory = result.first
            accessKey = result.second
            xsdFile = "${xsdFolder}${File.separatorChar}Factura_V2.1.0.xsd"
        } else if (type == TypeDocument.NOTA_CREDITO) {
            val build = BuildCreditNote(code, number, baseDirectory, creditNoteService!!)
            val result = build.xml()
            generatedDirectory = result.first
            accessKey = result.second
            xsdFile = "${xsdFolder}${File.separatorChar}NotaCredito_V1.1.0.xsd"
        } else if (type == TypeDocument.NOTA_DEBITO) {
            val build = BuildDebitNote(code, number, baseDirectory, debitNoteService!!)
            val result = build.xml()
            generatedDirectory = result.first
            accessKey = result.second
            xsdFile = "${xsdFolder}${File.separatorChar}NotaDebito_V1.0.0.xsd"
        } else if (type == TypeDocument.LIQUIDACION) {
            val build = BuildLiquidation(code, number, baseDirectory, liquidationService!!)
            val result = build.xml()
            generatedDirectory = result.first
            accessKey = result.second
            xsdFile = "${xsdFolder}${File.separatorChar}LiquidacionCompra_V1.1.0.xsd"
        } else if (type == TypeDocument.RETENCION) {
            val build = BuildWithhold(code, number, baseDirectory, withholdService!!)
            val result = build.xml()
            generatedDirectory = result.first
            accessKey = result.second
            xsdFile = "${xsdFolder}${File.separatorChar}ComprobanteRetencion_V2.0.0.xsd"
        } else if (type == TypeDocument.GUIA) {
            val build = BuildDeliveryNote(code, number, baseDirectory, deliveryNoteService!!)
            val result = build.xml()
            generatedDirectory = result.first
            accessKey = result.second
            xsdFile = "${xsdFolder}${File.separatorChar}GuiaRemision_V1.1.0.xsd"
        }

        if (accessKey.isEmpty()) {
            return ""
        }

        val isValid =
            validateXmlAgainstXsd(
                File("$generatedDirectory${File.separatorChar}$accessKey.xml"),
                File(xsdFile),
            )

        if (!isValid.first) {
            statusResponse = saveResponse(getErrorResponse(isValid.second, accessKey))
            return statusResponse
        }

        val pathLogo = parameterService.getLogoJpegPath()
        val printPdf = PdfBuilder(accessKey, baseDirectory, pathLogo)
        printPdf.pdf()

        val certificatePath = parameterService.getCertificatePath()
        val certificatePassword = parameterService.getCertificatePassword()

        val signer = SignerXml(accessKey, baseDirectory, certificatePath, certificatePassword)

        val (status, message) = signer.sign()
        if (status) {
            val xml = SendXML(accessKey, baseDirectory, webService)
            val response = xml.send()

            response.let { statusResponse = saveResponse(it) }

            return statusResponse
        } else {
            statusResponse = saveResponse(getErrorResponse(message, accessKey))
        }

        return statusResponse
    }

    fun check(informationService: InformationService): String {
        val xml = SendXML(accessKey, baseDirectory, webService)
        val response = xml.check()

        val status = saveResponse(response)

        if (Estado.AUTORIZADO.descripcion.equals(response.autorizacion.estado)) {
            val pathLogo = parameterService.getLogoJpegPath()
            val printPdf =
                PdfBuilder(
                    accessKey,
                    baseDirectory,
                    pathLogo,
                    response.autorizacion.numeroAutorizacion,
                    DateUtil.formatDate(
                        DateUtil.extractDate(response.autorizacion.fechaAutorizacion)
                    ),
                )
            printPdf.pdf()

            val tempDir = System.getProperty("java.io.tmpdir")
            val dateAccessKey = DateUtil.accessKeyToDate(accessKey)
            val authorizedFolder =
                FilesUtil.directory(
                    baseDirectory + "${File.separatorChar}authorized",
                    dateAccessKey,
                )
            if (FilesUtil.isFileExists(tempDir + "${File.separatorChar}$accessKey.xml")) {
                val file = File(tempDir + "${File.separatorChar}$accessKey.xml")
                file.copyTo(File(authorizedFolder + "${File.separatorChar}$accessKey.xml"), true)
                file.delete()
            }
        } else {
            val tempDir = System.getProperty("java.io.tmpdir")
            val dateAccessKey = DateUtil.accessKeyToDate(accessKey)
            val refusedFolder =
                FilesUtil.directory(baseDirectory + "${File.separatorChar}refused", dateAccessKey)
            if (FilesUtil.isFileExists(tempDir + "${File.separatorChar}$accessKey.xml")) {
                val file = File(tempDir + "${File.separatorChar}$accessKey.xml")
                file.copyTo(File(refusedFolder + "${File.separatorChar}$accessKey.xml"), true)
                file.delete()
            }
        }

        if (status == "AUTORIZADO") {
            sendEmail(informationService)
        }

        return status
    }

    private fun saveResponse(response: AutorizacionEstado): String {
        val date = DateUtil.getDatetime()
        var message = "$date |"

        if (response.autorizacion.mensajes != null) {
            for (i in response.autorizacion.mensajes.mensaje.indices) {
                val messageResponse = response.autorizacion.mensajes.mensaje[i]
                if (messageResponse.mensaje != null) {
                    message = message + concatMessage(messageResponse)
                }
            }
        } else {
            return ""
        }

        try {
            val document = documentService.getByCodeAndNumber(code, number)

            if (document.observation!!.length > 4500) {
                document.observation = ""
            }

            document.observation = "$message ${document.observation}"

            if (response.autorizacion.fechaAutorizacion != null) {
                document.observation =
                    " | ${response.autorizacion.numeroAutorizacion} " +
                        "${response.autorizacion.fechaAutorizacion} ${document.observation}"

                document.authorization = response.autorizacion.numeroAutorizacion

                document.authorizationDate =
                    DateUtil.extractDate(response.autorizacion.fechaAutorizacion)
            }
            document.status = response.autorizacion.estado

            documentService.saveDocument(document)
        } catch (e: NoSuchElementException) {
            val document = Document(code, number, message, response.autorizacion.estado)

            if (response.autorizacion.fechaAutorizacion != null) {
                document.observation =
                    " | ${response.autorizacion.numeroAutorizacion} " +
                        "${response.autorizacion.fechaAutorizacion} ${document.observation}"

                document.authorization = response.autorizacion.numeroAutorizacion

                document.authorizationDate =
                    DateUtil.extractDate(response.autorizacion.fechaAutorizacion)
            }
            documentService.saveDocument(document)
        }
        return response.autorizacion.estado
    }

    private fun saveResponse(response: RespuestaSolicitud): String {
        var receipt: Comprobante
        val date = DateUtil.getDatetime()
        var message = "$date |"

        if (response.comprobantes == null) {
            return ""
        }

        if (response.comprobantes.comprobante.size > 0) {
            for (i in response.comprobantes.comprobante.indices) {
                receipt = response.comprobantes.comprobante[i] as Comprobante

                for (m in receipt.mensajes.mensaje.indices) {
                    val messageReceipt = receipt.mensajes.mensaje[m]
                    if (messageReceipt.mensaje != null) {
                        message = message + concatMessage(messageReceipt)
                    }
                }
                message += " "
            }
        }

        try {
            val document = documentService.getByCodeAndNumber(code, number)

            document.observation = message + " | " + document.observation
            document.status = response.estado
            documentService.saveDocument(document)
        } catch (e: NoSuchElementException) {
            val document = Document(code, number, message, response.estado)
            documentService.saveDocument(document)
        }

        return response.estado
    }

    fun concatMessage(message: recepcion.ws.sri.gob.ec.Mensaje): String {
        return " ${message.tipo} ${message.identificador}: ${message.mensaje} - ${message.informacionAdicional}"
    }

    fun concatMessage(message: autorizacion.ws.sri.gob.ec.Mensaje): String {
        return " ${message.tipo} ${message.identificador}: ${message.mensaje} - ${message.informacionAdicional}"
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun sendEmail(informationService: InformationService) {
        GlobalScope.launch {
            val emailSmtp = EmailSmtp(code, number, parameterService, informationService)
            emailSmtp.send()
        }
    }
}
