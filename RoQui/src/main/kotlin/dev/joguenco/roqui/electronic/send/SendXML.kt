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

        val (status, message) = isAliveServiceReception(ambientType)
        if (!status) {
            return getErrorResponse(message, accessKey)
        }

        val pathSigned =
            FilesUtil.directory(baseDirectory + "${File.separatorChar}signed", dateAccessKey)

        if (ambientType == AmbientType.PRODUCTION) {
            val statusSend =
                Send.execute(
                    webService.productionReception,
                    "$pathSigned${File.separatorChar}$accessKey.xml",
                )
            return statusSend
        } else {
            val statusSend =
                Send.execute(
                    webService.developmentReception,
                    "$pathSigned${File.separatorChar}$accessKey.xml",
                )
            return statusSend
        }
    }

    fun check(): AutorizacionEstado {
        val ambientType = getAmbientType(accessKey)

        val (status, message) = isAliveServiceAuthorization(ambientType)
        if (!status) {
            return getErrorAuthorization(message)
        }

        if (ambientType == AmbientType.PRODUCTION) {
            val response = Check.execute(webService.productionAuthorization, accessKey)
            return response
        } else {
            val response = Check.execute(webService.developmentAuthorization, accessKey)
            return response
        }
    }

    private fun isAliveServiceReception(ambientType: AmbientType): Pair<Boolean, String> {
        if (ambientType == AmbientType.PRODUCTION) {
            val (status, message) = WebService.isAlive(webService.productionReception)
            if (!status) {
                return false to message
            }
        } else {
            val (status, message) = WebService.isAlive(webService.developmentReception)
            if (!status) {
                return false to message
            }
        }

        return true to "Successful connection"
    }

    private fun isAliveServiceAuthorization(ambientType: AmbientType): Pair<Boolean, String> {
        if (ambientType == AmbientType.PRODUCTION) {
            val (status, message) = WebService.isAlive(webService.productionAuthorization)
            if (!status) {
                return false to message
            }
        } else {
            val (status, message) = WebService.isAlive(webService.developmentAuthorization)
            if (!status) {
                return false to message
            }
        }

        return true to "Successful connection"
    }

    private fun getAmbientType(accessKey: String): AmbientType {
        return if (accessKey.substring(23, 24) == "2") {
            AmbientType.PRODUCTION
        } else {
            AmbientType.DEVELOPMENT
        }
    }
}
