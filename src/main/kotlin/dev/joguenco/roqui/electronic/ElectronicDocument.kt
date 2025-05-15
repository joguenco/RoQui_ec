package dev.joguenco.roqui.electronic

import dev.joguenco.definition.AutorizacionEstado
import dev.joguenco.definition.Estado
import dev.joguenco.roqui.electronic.ErrorMessage.getErrorResponse
import dev.joguenco.roqui.electronic.model.Document
import dev.joguenco.roqui.electronic.send.SendXML
import dev.joguenco.roqui.electronic.send.WebService
import dev.joguenco.roqui.electronic.service.DocumentService
import dev.joguenco.roqui.electronic.sign.SignerXml
import dev.joguenco.roqui.electronic.xml.BuildInvoice
import dev.joguenco.roqui.electronic.xml.PdfInvoice
import dev.joguenco.roqui.invoice.service.InvoiceService
import dev.joguenco.roqui.parameter.service.ParameterService
import dev.joguenco.roqui.util.DateUtil
import dev.joguenco.roqui.util.FilesUtil
import java.io.File
import kotlin.NoSuchElementException
import recepcion.ws.sri.gob.ec.Comprobante
import recepcion.ws.sri.gob.ec.RespuestaSolicitud

class ElectronicDocument(
    val code: String,
    val number: String,
    private val invoiceService: InvoiceService,
    private val webService: WebService,
    private val parameterService: ParameterService,
    private val documentService: DocumentService,
) {

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

        if (type == TypeDocument.FACTURA) {
            val build = BuildInvoice(code, number, baseDirectory, invoiceService)
            accessKey = build.xml()
        }

        if (accessKey.isEmpty()) {
            return ""
        }

        val pathLogo = parameterService.getLogoPath()
        val printPdf = PdfInvoice(accessKey, baseDirectory, pathLogo)
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

    fun check(): String {
        val xml = SendXML(accessKey, baseDirectory, webService)
        val response = xml.check()

        val status = saveResponse(response)

        if (Estado.AUTORIZADO.descripcion.equals(response.autorizacion.estado)) {
            val pathLogo = parameterService.getLogoPath()
            val printPdf =
                PdfInvoice(
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
}
