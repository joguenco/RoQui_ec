package dev.joguenco.roqui.electronic.xml

import dev.joguenco.roqui.note.delivery.service.DeliveryNoteService
import dev.joguenco.roqui.util.FilesUtil
import ec.gob.sri.note.delivery.v110.Destinatario
import ec.gob.sri.note.delivery.v110.Detalle
import ec.gob.sri.note.delivery.v110.GuiaRemision
import ec.gob.sri.note.delivery.v110.InfoTributaria
import ec.gob.sri.note.delivery.v110.ObligadoContabilidad
import jakarta.xml.bind.JAXBContext
import jakarta.xml.bind.Marshaller
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.io.StringWriter
import java.text.SimpleDateFormat

/**
 * Arma el XML de la guia de remision (codDoc 06).
 *
 * El documento tiene tres niveles: la cabecera con el transportista, un destinatario por cada
 * factura del viaje, y dentro de cada destinatario la mercaderia que le corresponde. Por eso el
 * detalle se pide por destinatario y no de una sola vez.
 */
class BuildDeliveryNote(
    val code: String,
    val number: String,
    private val baseDirectory: String,
    private val deliveryNoteService: DeliveryNoteService,
) {

    private companion object {
        /** 06 = guia de remision */
        const val COD_DOC = "06"

        /** El documento de sustento es siempre la factura de venta. */
        const val COD_DOC_SUSTENTO = "01"
    }

    private val tributaryInformation = deliveryNoteService.getDeliveryNoteAndTaxpayer(code, number)

    fun xml(): Pair<String, String> {
        val guiaRemision = GuiaRemision()

        try {
            guiaRemision.id = "comprobante"
            guiaRemision.version = "1.1.0"
            guiaRemision.infoTributaria = buildInfoTributaria()
            guiaRemision.infoGuiaRemision = buildInfoGuiaRemision()
            guiaRemision.destinatarios = buildDestinatarios()
            guiaRemision.infoAdicional = buildAdditionalInformation()

            val jaxbContext = JAXBContext.newInstance(GuiaRemision::class.java)
            val marshaller = jaxbContext.createMarshaller()
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
            marshaller.setProperty("jaxb.encoding", "UTF-8")

            val stringWriter = StringWriter()
            stringWriter.use { marshaller.marshal(guiaRemision, stringWriter) }

            val pathGenerated =
                FilesUtil.directory(
                    baseDirectory + "${File.separatorChar}generated",
                    tributaryInformation.deliveryNote.date!!,
                )

            val out =
                OutputStreamWriter(
                    FileOutputStream(
                        "$pathGenerated${File.separatorChar}" +
                            "${guiaRemision.infoTributaria.claveAcceso}.xml"
                    ),
                    "UTF-8",
                )

            marshaller.marshal(guiaRemision, out)
            println(stringWriter)

            return Pair(pathGenerated, guiaRemision.infoTributaria.claveAcceso)
        } catch (e: Exception) {
            println("Error BuildDeliveryNote: ${e.message}")
            return Pair("", "")
        }
    }

    private fun buildInfoTributaria(): InfoTributaria {
        val infoTributaria = InfoTributaria()

        infoTributaria.ruc = tributaryInformation.taxpayer.identification
        infoTributaria.razonSocial = tributaryInformation.taxpayer.legalName
        infoTributaria.nombreComercial = tributaryInformation.establishmentBusinessName

        if (tributaryInformation.deliveryNote.accessKey!!.length == 49) {
            infoTributaria.claveAcceso = tributaryInformation.deliveryNote.accessKey
            infoTributaria.ambiente = infoTributaria.claveAcceso.substring(23, 24)
            infoTributaria.tipoEmision = infoTributaria.claveAcceso.substring(39, 40)
        }

        infoTributaria.codDoc = COD_DOC
        infoTributaria.estab = tributaryInformation.deliveryNote.establishment
        infoTributaria.ptoEmi = tributaryInformation.deliveryNote.emissionPoint
        infoTributaria.secuencial = tributaryInformation.deliveryNote.sequence
        infoTributaria.dirMatriz = tributaryInformation.principalEstablishmentAddress
        infoTributaria.contribuyenteRimpe = tributaryInformation.taxpayer.regime
        infoTributaria.agenteRetencion = tributaryInformation.taxpayer.retentionAgent

        return infoTributaria
    }

    private fun buildInfoGuiaRemision(): GuiaRemision.InfoGuiaRemision {
        val info = GuiaRemision.InfoGuiaRemision()
        val note = tributaryInformation.deliveryNote

        info.dirEstablecimiento = tributaryInformation.establishmentAddress
        info.dirPartida = note.addressStart
        info.razonSocialTransportista = note.carrierLegalName
        info.tipoIdentificacionTransportista = note.carrierIdentificationType
        info.rucTransportista = note.carrierIdentification
        info.placa = note.plate

        info.contribuyenteEspecial = tributaryInformation.taxpayer.specialTaxpayer

        if (tributaryInformation.taxpayer.forcedAccounting == "SI") {
            info.obligadoContabilidad = ObligadoContabilidad.SI
        } else {
            info.obligadoContabilidad = ObligadoContabilidad.NO
        }

        info.fechaIniTransporte = SimpleDateFormat("dd/MM/yyyy").format(note.dateStartTransport)
        info.fechaFinTransporte = SimpleDateFormat("dd/MM/yyyy").format(note.dateEndTransport)

        return info
    }

    private fun buildDestinatarios(): GuiaRemision.Destinatarios {
        val destinatarios = GuiaRemision.Destinatarios()

        for (receiver in deliveryNoteService.getReceivers(code, number)) {
            val destinatario = Destinatario()

            destinatario.identificacionDestinatario = receiver.identification
            destinatario.razonSocialDestinatario = receiver.legalName
            destinatario.dirDestinatario = receiver.address
            destinatario.motivoTraslado = receiver.transferReason

            // El sustento es la factura que se esta despachando
            destinatario.codDocSustento = COD_DOC_SUSTENTO
            destinatario.numDocSustento = receiver.numberDocumentSupport
            destinatario.numAutDocSustento = receiver.authorizationDocumentSupport
            destinatario.fechaEmisionDocSustento =
                SimpleDateFormat("dd/MM/yyyy").format(receiver.dateDocumentSupport)

            destinatario.detalles = buildDetalles(receiver.line!!)

            destinatarios.destinatario.add(destinatario)
        }

        return destinatarios
    }

    /** La mercaderia de un destinatario, sacada de las lineas de su factura. */
    private fun buildDetalles(line: Long): Destinatario.Detalles {
        val detalles = Destinatario.Detalles()

        for (item in deliveryNoteService.getDetail(code, number, line)) {
            val detalle = Detalle()

            // codigoAdicional no se manda: es opcional para el SRI y en DonPos
            // products.code guarda el codigo de barras, que no es un codigo
            // adicional del producto.
            detalle.codigoInterno = item.principalCode
            detalle.descripcion = item.name
            detalle.cantidad = item.quantity

            detalles.detalle.add(detalle)
        }

        return detalles
    }

    /**
     * Campos adicionales del comprobante.
     *
     * Solo lleva la observacion general (RUC Proveedor). El correo, la direccion y el telefono del
     * cliente no van: en una guia de remision el destinatario ya sale completo en su propio bloque,
     * y repetirlos aqui solo alarga el PDF.
     *
     * Devuelve null si no hay ninguno: el XSD marca infoAdicional como opcional pero exige al menos
     * un campoAdicional adentro, asi que un bloque vacio hace fallar la validacion.
     */
    private fun buildAdditionalInformation(): GuiaRemision.InfoAdicional? {
        val infoAdditional = GuiaRemision.InfoAdicional()

        for (observation in deliveryNoteService.getGeneralObservation()) {
            val fieldAdditional = GuiaRemision.InfoAdicional.CampoAdicional()
            fieldAdditional.nombre = observation.name
            fieldAdditional.value = observation.value

            infoAdditional.campoAdicional.add(fieldAdditional)
        }

        return if (infoAdditional.campoAdicional.isEmpty()) null else infoAdditional
    }
}
