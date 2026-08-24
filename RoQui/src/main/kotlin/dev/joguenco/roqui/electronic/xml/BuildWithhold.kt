package dev.joguenco.roqui.electronic.xml

import dev.joguenco.roqui.util.FilesUtil
import dev.joguenco.roqui.withhold.service.WithholdService
import ec.gob.sri.withhold.v200.ComprobanteRetencion
import ec.gob.sri.withhold.v200.DocSustento
import ec.gob.sri.withhold.v200.ImpuestoDocSustento
import ec.gob.sri.withhold.v200.ImpuestosDocSustento
import ec.gob.sri.withhold.v200.InfoTributaria
import ec.gob.sri.withhold.v200.ObligadoContabilidad
import ec.gob.sri.withhold.v200.Pago
import ec.gob.sri.withhold.v200.Pagos
import ec.gob.sri.withhold.v200.Retencion
import ec.gob.sri.withhold.v200.Retenciones
import jakarta.xml.bind.JAXBContext
import jakarta.xml.bind.Marshaller
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.io.StringWriter
import java.math.BigDecimal
import java.text.SimpleDateFormat

class BuildWithhold(
    val code: String,
    val number: String,
    private val baseDirectory: String,
    private val withholdService: WithholdService,
) {

    private companion object {
        /** 07 = comprobante de retencion */
        const val COD_DOC = "07"

        /** Pago local. DonPos no registra pagos al exterior en compras. */
        const val PAGO_LOCAL = "01"

        /** El sujeto retenido no es parte relacionada. */
        const val PARTE_RELACIONADA = "NO"

        const val FORMA_PAGO = "01"
    }

    private val tributaryInformation = withholdService.getWithholdAndTaxpayer(code, number)

    fun xml(): Pair<String, String> {
        val comprobanteRetencion = ComprobanteRetencion()

        try {
            comprobanteRetencion.id = "comprobante"
            comprobanteRetencion.version = "2.0.0"
            comprobanteRetencion.infoTributaria = buildInfoTributaria()
            comprobanteRetencion.infoCompRetencion = buildInfoCompRetencion()
            comprobanteRetencion.docsSustento = buildDocsSustento()
            comprobanteRetencion.infoAdicional =
                buildAdditionalInformation(tributaryInformation.withhold.identification!!)

            val jaxbContext = JAXBContext.newInstance(ComprobanteRetencion::class.java)
            val marshaller = jaxbContext.createMarshaller()
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
            marshaller.setProperty("jaxb.encoding", "UTF-8")

            val stringWriter = StringWriter()
            stringWriter.use { marshaller.marshal(comprobanteRetencion, stringWriter) }

            val pathGenerated =
                FilesUtil.directory(
                    baseDirectory + "${File.separatorChar}generated",
                    tributaryInformation.withhold.date!!,
                )

            val out =
                OutputStreamWriter(
                    FileOutputStream(
                        "$pathGenerated${File.separatorChar}" +
                            "${comprobanteRetencion.infoTributaria.claveAcceso}.xml"
                    ),
                    "UTF-8",
                )

            marshaller.marshal(comprobanteRetencion, out)
            println(stringWriter)

            return Pair(pathGenerated, comprobanteRetencion.infoTributaria.claveAcceso)
        } catch (e: Exception) {
            println("Error BuildWithhold: ${e.message}")
            return Pair("", "")
        }
    }

    private fun buildInfoTributaria(): InfoTributaria {
        val infoTributaria = InfoTributaria()

        infoTributaria.ruc = tributaryInformation.taxpayer.identification
        infoTributaria.razonSocial = tributaryInformation.taxpayer.legalName
        infoTributaria.nombreComercial = tributaryInformation.establishmentBusinessName

        if (tributaryInformation.withhold.accessKey!!.length == 49) {
            infoTributaria.claveAcceso = tributaryInformation.withhold.accessKey
            infoTributaria.ambiente = infoTributaria.claveAcceso.substring(23, 24)
            infoTributaria.tipoEmision = infoTributaria.claveAcceso.substring(39, 40)
        }

        infoTributaria.codDoc = COD_DOC
        infoTributaria.estab = tributaryInformation.withhold.establishment
        infoTributaria.ptoEmi = tributaryInformation.withhold.emissionPoint
        infoTributaria.secuencial = tributaryInformation.withhold.sequence
        infoTributaria.dirMatriz = tributaryInformation.principalEstablishmentAddress
        infoTributaria.contribuyenteRimpe = tributaryInformation.taxpayer.regime
        infoTributaria.agenteRetencion = tributaryInformation.taxpayer.retentionAgent

        return infoTributaria
    }

    private fun buildInfoCompRetencion(): ComprobanteRetencion.InfoCompRetencion {
        val infoCompRetencion = ComprobanteRetencion.InfoCompRetencion()

        infoCompRetencion.fechaEmision =
            SimpleDateFormat("dd/MM/yyyy").format(tributaryInformation.withhold.date)
        infoCompRetencion.dirEstablecimiento = tributaryInformation.establishmentAddress
        infoCompRetencion.contribuyenteEspecial = tributaryInformation.taxpayer.specialTaxpayer

        if (tributaryInformation.taxpayer.forcedAccounting == "SI") {
            infoCompRetencion.obligadoContabilidad = ObligadoContabilidad.SI
        } else {
            infoCompRetencion.obligadoContabilidad = ObligadoContabilidad.NO
        }

        // El sujeto retenido es el PROVEEDOR de la compra
        infoCompRetencion.tipoIdentificacionSujetoRetenido =
            tributaryInformation.withhold.identificationType
        infoCompRetencion.razonSocialSujetoRetenido = tributaryInformation.withhold.legalName
        infoCompRetencion.identificacionSujetoRetenido =
            tributaryInformation.withhold.identification
        infoCompRetencion.parteRel = PARTE_RELACIONADA
        infoCompRetencion.periodoFiscal = tributaryInformation.withhold.fiscalPeriod

        return infoCompRetencion
    }

    private fun buildDocsSustento(): ComprobanteRetencion.DocsSustento {
        val docsSustento = ComprobanteRetencion.DocsSustento()
        val withhold = tributaryInformation.withhold

        val docSustento = DocSustento()
        docSustento.codSustento = withhold.codeSupport
        docSustento.codDocSustento = withhold.codeDocumentSupport
        docSustento.numDocSustento = withhold.numberDocumentSupport?.replace("-", "")
        docSustento.fechaEmisionDocSustento =
            SimpleDateFormat("dd/MM/yyyy").format(withhold.dateDocumentSupport)
        docSustento.numAutDocSustento = withhold.authorizationDocumentSupport
        docSustento.pagoLocExt = PAGO_LOCAL

        docSustento.totalSinImpuestos =
            withhold.totalWithoutTaxes!!.setScale(2, BigDecimal.ROUND_HALF_UP)
        docSustento.importeTotal = withhold.total!!.setScale(2, BigDecimal.ROUND_HALF_UP)

        docSustento.impuestosDocSustento = buildImpuestosDocSustento()
        docSustento.retenciones = buildRetenciones()
        docSustento.pagos = buildPagos(docSustento.importeTotal)

        docsSustento.docSustento.add(docSustento)

        return docsSustento
    }

    private fun buildImpuestosDocSustento(): ImpuestosDocSustento {
        val impuestos = ImpuestosDocSustento()
        val documentTaxes = withholdService.getWithholdDocumentTax(code, number)

        for (tax in documentTaxes) {
            val impuesto = ImpuestoDocSustento()
            impuesto.codImpuestoDocSustento = tax.taxCode
            impuesto.codigoPorcentaje = tax.percentageCode
            impuesto.baseImponible = tax.taxBase!!.setScale(2, BigDecimal.ROUND_HALF_UP)
            impuesto.tarifa = tax.taxIva!!.setScale(2, BigDecimal.ROUND_HALF_UP)
            impuesto.valorImpuesto = tax.value!!.setScale(2, BigDecimal.ROUND_HALF_UP)

            impuestos.impuestoDocSustento.add(impuesto)
        }

        return impuestos
    }

    /** Las retenciones aplicadas: lo que se le retuvo al proveedor. */
    private fun buildRetenciones(): Retenciones {
        val retenciones = Retenciones()
        val details = withholdService.getWithholdDetail(code, number)

        for (detail in details) {
            val retencion = Retencion()
            retencion.codigo = detail.taxCode
            retencion.codigoRetencion = detail.withholdCode
            retencion.baseImponible = detail.baseValue!!.setScale(2, BigDecimal.ROUND_HALF_UP)
            retencion.porcentajeRetener = detail.percentage!!.setScale(2, BigDecimal.ROUND_HALF_UP)
            retencion.valorRetenido = detail.withholdedValue!!.setScale(2, BigDecimal.ROUND_HALF_UP)

            retenciones.retencion.add(retencion)
        }

        return retenciones
    }

    private fun buildPagos(importeTotal: BigDecimal): Pagos {
        val pagos = Pagos()

        val pago = Pago()
        pago.formaPago = FORMA_PAGO
        pago.total = importeTotal

        pagos.pago.add(pago)

        return pagos
    }

    private fun buildAdditionalInformation(
        identification: String
    ): ComprobanteRetencion.InfoAdicional? {
        var infoAdicional = ComprobanteRetencion.InfoAdicional()
        val additionalInformation = withholdService.getWithholdInformation(identification)

        for (information in additionalInformation) {
            val campoAdicional = ComprobanteRetencion.InfoAdicional.CampoAdicional()
            campoAdicional.nombre = information.name
            campoAdicional.value = information.value

            infoAdicional.campoAdicional.add(campoAdicional)
        }

        infoAdicional = buildGeneralObservation(infoAdicional)

        return infoAdicional
    }

    private fun buildGeneralObservation(
        infoAdicional: ComprobanteRetencion.InfoAdicional
    ): ComprobanteRetencion.InfoAdicional {
        val generalObservation = withholdService.getGeneralObservation()

        for (observation in generalObservation) {
            val campoAdicional = ComprobanteRetencion.InfoAdicional.CampoAdicional()
            campoAdicional.nombre = observation.name
            campoAdicional.value = observation.value

            infoAdicional.campoAdicional.add(campoAdicional)
        }

        return infoAdicional
    }
}
