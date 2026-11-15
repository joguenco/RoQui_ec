package dev.joguenco.roqui.electronic

import autorizacion.ws.sri.gob.ec.Autorizacion
import dev.joguenco.definition.AutorizacionEstado
import dev.joguenco.definition.Estado
import recepcion.ws.sri.gob.ec.Comprobante
import recepcion.ws.sri.gob.ec.RespuestaSolicitud

object ErrorMessage {
    fun getErrorResponse(errorMessage: String, accessKey: String): RespuestaSolicitud {
        val response = RespuestaSolicitud()
        val receipt = Comprobante()
        receipt.claveAcceso = accessKey

        val message = recepcion.ws.sri.gob.ec.Mensaje()
        message.mensaje = errorMessage
        message.tipo = "ERROR"

        receipt.mensajes = Comprobante.Mensajes()

        receipt.mensajes.mensaje.add(message)

        response.comprobantes = RespuestaSolicitud.Comprobantes()
        response.comprobantes.comprobante.add(receipt)

        response.estado = "ERROR"
        return response
    }

    fun getErrorAuthorization(errorMessage: String): AutorizacionEstado {
        val receipt = Autorizacion()

        val message = autorizacion.ws.sri.gob.ec.Mensaje()
        message.mensaje = errorMessage
        message.tipo = "ERROR"

        receipt.mensajes = Autorizacion.Mensajes()

        receipt.mensajes.mensaje.add(message)
        receipt.estado = "ERROR"

        return AutorizacionEstado(receipt, Estado.NO_PROCESADO)
    }
}
