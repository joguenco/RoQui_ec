package dev.joguenco.roqui.electronic.send

import dev.joguenco.client.sri.Check
import dev.joguenco.client.sri.Send
import dev.joguenco.definition.AutorizacionEstado
import dev.joguenco.roqui.electronic.ErrorMessage.getErrorAuthorization
import dev.joguenco.roqui.electronic.ErrorMessage.getErrorResponse
import dev.joguenco.roqui.util.DateUtil
import dev.joguenco.roqui.util.FilesUtil
import java.io.File
import recepcion.ws.sri.gob.ec.RespuestaSolicitud

class SendXML(
    private val accessKey: String,
    private val baseDirectory: String,
    private val webService: WebService,
) {
    fun send(): RespuestaSolicitud {
        val dateAccessKey = DateUtil.accessKeyToDate(accessKey)

        val ambientType = getAmbientType(accessKey)

        val (status, message) = isAliveService(ambientType)
        if (!status) {
            return getErrorResponse(message, accessKey)
        }

        val pathSigned =
            FilesUtil.directory(baseDirectory + "${File.separatorChar}signed", dateAccessKey)

        val statusSend = Send.execute("$pathSigned${File.separatorChar}$accessKey.xml")

        return statusSend
    }

    fun check(): AutorizacionEstado {
        val ambientType = getAmbientType(accessKey)

        val (status, message) = isAliveService(ambientType)
        if (!status) {
            return getErrorAuthorization(message)
        }

        val response = Check.execute(accessKey)
        return response
    }

    private fun isAliveService(ambientType: AmbientType): Pair<Boolean, String> {
        if (ambientType == AmbientType.PRODUCTION) {
            if (!WebService.isAlive(webService.productionReception)) {
                val message =
                    "Error de conexión con el servicio web $webService.productionReception"
                return false to message
            }
        } else {
            if (!WebService.isAlive(webService.developmentReception)) {
                val message =
                    "Error de conexión con el servicio web $webService.developmentReception"
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
}
