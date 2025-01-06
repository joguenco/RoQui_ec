package dev.joguenco.roqui.electronic.send

import dev.joguenco.client.sri.Check
import dev.joguenco.client.sri.Send
import dev.joguenco.definition.AutorizacionEstado
import dev.joguenco.roqui.util.DateUtil
import dev.joguenco.roqui.util.FilesUtil
import recepcion.ws.sri.gob.ec.Comprobante
import recepcion.ws.sri.gob.ec.Mensaje
import recepcion.ws.sri.gob.ec.RespuestaSolicitud
import java.io.File

class SendXML(
    private val accessKey: String,
    private val baseDirectory: String,
    private val webService: WebService
) {
    fun send(): RespuestaSolicitud {
        val dateAccessKey = DateUtil.accessKeyToDate(accessKey)

        val ambientType = getAmbientType(accessKey)

        val (status, message) = isAliveService(ambientType)
        if (!status) {
            return getErrorResponse(message)
        }

        val pathSigned = FilesUtil
            .directory(
                baseDirectory + "${File.separatorChar}Signed",
                dateAccessKey
            )

        val statusSend = Send.execute(
            "$pathSigned${File.separatorChar}$accessKey.xml"
        )

        return statusSend
    }

    fun check(): AutorizacionEstado {
        val ambientType = getAmbientType(accessKey)

        val response = Check.execute(accessKey)
        return response
    }

    private fun isAliveService(ambientType: AmbientType): Pair<Boolean, String> {
        if (ambientType == AmbientType.PRODUCTION) {
            if (!WebService.isAlive(webService.productionReception)) {
                val message = webService.productionReception
                return false to message
            }
        } else {
            if (!WebService.isAlive(webService.developmentReception)) {
                val message = webService.developmentReception
                return false to message
            }
        }

        return true to ""
    }

    private fun getAmbientType(accessKey: String): AmbientType {
        return if (accessKey.substring(23, 24) == "2") {
            AmbientType.PRODUCTION
        } else {
            AmbientType.DEVELOPMENT
        }
    }

    private fun getErrorResponse(webServiceUrl: String): RespuestaSolicitud {
        val response = RespuestaSolicitud()
        val receipt = Comprobante()
        receipt.claveAcceso = accessKey

        val message = Mensaje()
        message.mensaje = "Error web service connection $webServiceUrl"
        message.tipo = "ERROR"

        receipt.mensajes = Comprobante.Mensajes()

        receipt.mensajes.mensaje.add(message)

        response.comprobantes = RespuestaSolicitud.Comprobantes()
        response.comprobantes.comprobante.add(receipt)

        response.estado = "ERROR"
        return response
    }
}