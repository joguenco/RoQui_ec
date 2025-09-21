package dev.joguenco.roqui.electronic.xml

import dev.joguenco.roqui.note.credit.service.CreditNoteService
import dev.joguenco.roqui.util.FilesUtil
import ec.gob.sri.note.credit.v110.Impuesto
import ec.gob.sri.note.credit.v110.InfoTributaria
import ec.gob.sri.note.credit.v110.NotaCredito
import ec.gob.sri.note.credit.v110.ObligadoContabilidad
import ec.gob.sri.note.credit.v110.TotalConImpuestos
import jakarta.xml.bind.JAXBContext
import jakarta.xml.bind.Marshaller
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.io.StringWriter
import java.math.BigDecimal
import java.text.SimpleDateFormat

class BuildCreditNote(
    val code: String,
    val number: String,
    private val baseDirectory: String,
    private val creditNoteService: CreditNoteService,
) {

    private val tributaryInformation = creditNoteService.getCreditNoteAndTaxpayer(code, number)

    fun xml(): String {
        val notaCredito = NotaCredito()

        try {
            notaCredito.id = "comprobante"
            notaCredito.version = "1.1.0"
            notaCredito.infoTributaria = buildInfoTributaria()
            notaCredito.infoNotaCredito = buildInfoFactura()
            notaCredito.detalles = buildDetails()
            notaCredito.infoAdicional =
                buildAdditionalInformation(tributaryInformation.creditNote.identification!!)

            val jaxbContext = JAXBContext.newInstance(NotaCredito::class.java)
            val marshaller = jaxbContext.createMarshaller()
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
            marshaller.setProperty("jaxb.encoding", "UTF-8")

            val stringWriter = StringWriter()
            stringWriter.use { marshaller.marshal(notaCredito, stringWriter) }

            val pathGenerated =
                FilesUtil.directory(
                    baseDirectory + "${File.separatorChar}generated",
                    tributaryInformation.creditNote.date!!,
                )

            val out =
                OutputStreamWriter(
                    FileOutputStream(
                        "$pathGenerated${File.separatorChar}" +
                            "${notaCredito.infoTributaria.claveAcceso}.xml"
                    ),
                    "UTF-8",
                )

            marshaller.marshal(notaCredito, out)
            println(stringWriter)

            return notaCredito.infoTributaria.claveAcceso
        } catch (e: Exception) {
            println(e.message)
            return ""
        }
    }

    private fun buildAdditionalInformation(identification: String): NotaCredito.InfoAdicional? {
        val infoAdicional = NotaCredito.InfoAdicional()
        val additionalInformation = creditNoteService.getCreditNoteInformation(identification)

        for (information in additionalInformation) {
            val campoAdicional = NotaCredito.InfoAdicional.CampoAdicional()
            campoAdicional.nombre = information.name
            campoAdicional.value = information.value

            infoAdicional.campoAdicional.add(campoAdicional)
        }

        return infoAdicional
    }

    private fun buildInfoTributaria(): InfoTributaria {
        val infoTributaria = InfoTributaria()

        infoTributaria.ruc = tributaryInformation.taxpayer.identification
        infoTributaria.razonSocial = tributaryInformation.taxpayer.legalName
        infoTributaria.nombreComercial = tributaryInformation.establishmentBusinessName

        if (tributaryInformation.creditNote.accessKey!!.length == 49) {
            infoTributaria.claveAcceso = tributaryInformation.creditNote.accessKey
            infoTributaria.ambiente = infoTributaria.claveAcceso.substring(23, 24)
            infoTributaria.tipoEmision = infoTributaria.claveAcceso.substring(39, 40)
        }

        infoTributaria.codDoc = tributaryInformation.creditNote.codeDocument
        infoTributaria.estab = tributaryInformation.creditNote.establishment
        infoTributaria.ptoEmi = tributaryInformation.creditNote.emissionPoint
        infoTributaria.secuencial = tributaryInformation.creditNote.sequence
        infoTributaria.dirMatriz = tributaryInformation.principalEstablishmentAddress
        infoTributaria.contribuyenteRimpe = tributaryInformation.taxpayer.other
        infoTributaria.agenteRetencion = tributaryInformation.taxpayer.retentionAgent

        return infoTributaria
    }

    private fun buildInfoFactura(): NotaCredito.InfoNotaCredito {
        val infoNotaCredito = NotaCredito.InfoNotaCredito()

        infoNotaCredito.fechaEmision =
            SimpleDateFormat("dd/MM/yyyy").format(tributaryInformation.creditNote.date)
        infoNotaCredito.dirEstablecimiento = tributaryInformation.establishmentAddress
        infoNotaCredito.contribuyenteEspecial = tributaryInformation.taxpayer.specialTaxpayer
        if (tributaryInformation.taxpayer.forcedAccounting == "SI") {
            infoNotaCredito.obligadoContabilidad = ObligadoContabilidad.SI
        } else {
            infoNotaCredito.obligadoContabilidad = ObligadoContabilidad.NO
        }
        infoNotaCredito.tipoIdentificacionComprador =
            tributaryInformation.creditNote.identificationType
        infoNotaCredito.identificacionComprador = tributaryInformation.creditNote.identification
        infoNotaCredito.razonSocialComprador = tributaryInformation.creditNote.legalName

        infoNotaCredito.codDocModificado = tributaryInformation.creditNote.updatedCodeDocument
        if (tributaryInformation.creditNote.updatedNumberDocument?.length == 15) {
            val establishment =
                tributaryInformation.creditNote.updatedNumberDocument!!.substring(0, 3)
            val emissionPoint =
                tributaryInformation.creditNote.updatedNumberDocument!!.substring(3, 6)
            val secuential =
                tributaryInformation.creditNote.updatedNumberDocument!!.substring(6, 15)
            infoNotaCredito.numDocModificado = "$establishment-$emissionPoint-$secuential"
        }
        infoNotaCredito.fechaEmisionDocSustento =
            SimpleDateFormat("dd/MM/yyyy")
                .format(tributaryInformation.creditNote.updatedDateDocument!!)
        infoNotaCredito.motivo = tributaryInformation.creditNote.reason

        infoNotaCredito.totalSinImpuestos =
            tributaryInformation.creditNote.totalWithoutTaxes!!.setScale(
                2,
                BigDecimal.ROUND_HALF_UP,
            )
        infoNotaCredito.valorModificacion = tributaryInformation.creditNote.total!!.setScale(2)
        infoNotaCredito.moneda = "DOLAR"

        infoNotaCredito.totalConImpuestos = buildTotals()

        return infoNotaCredito
    }

    private fun buildTotals(): TotalConImpuestos {
        val totalConImpuestos = TotalConImpuestos()
        val taxTotals = creditNoteService.getCreditNoteTax(code, number)

        for (taxTotal in taxTotals) {
            val totalImpuesto = TotalConImpuestos.TotalImpuesto()
            totalImpuesto.codigo = taxTotal.taxCode
            totalImpuesto.codigoPorcentaje = taxTotal.percentageCode
            totalImpuesto.baseImponible = taxTotal.taxBase!!.setScale(2)
            totalImpuesto.valor = taxTotal.value!!.setScale(2)

            totalConImpuestos.totalImpuesto.add(totalImpuesto)
        }

        return totalConImpuestos
    }

    private fun buildDetails(): NotaCredito.Detalles {
        val creditNoteDetails = creditNoteService.getCreditNoteDetail(code, number)
        val detalles = NotaCredito.Detalles()

        for (detail in creditNoteDetails) {
            val notaCreditoDetalle = NotaCredito.Detalles.Detalle()

            notaCreditoDetalle.codigoInterno = detail.principalCode
            notaCreditoDetalle.descripcion = detail.name
            notaCreditoDetalle.cantidad = detail.quantity!!.setScale(2, BigDecimal.ROUND_HALF_UP)
            notaCreditoDetalle.precioUnitario =
                detail.unitPrice!!.setScale(2, BigDecimal.ROUND_HALF_UP)
            notaCreditoDetalle.descuento = BigDecimal(0).setScale(2)
            notaCreditoDetalle.precioTotalSinImpuesto =
                detail.totalPriceWithoutTax!!.setScale(2, BigDecimal.ROUND_HALF_UP)
            notaCreditoDetalle.impuestos = buildDetailTax(detail.principalCode!!, detail.line!!)

            detalles.detalle.add(notaCreditoDetalle)
        }

        return detalles
    }

    private fun buildDetailTax(
        principalCode: String,
        line: Long,
    ): NotaCredito.Detalles.Detalle.Impuestos? {
        val impuestos = NotaCredito.Detalles.Detalle.Impuestos()
        val taxDetail = creditNoteService.getCreditNoteDetailTax(code, number, principalCode, line)

        for (detail in taxDetail) {
            val impuesto = Impuesto()
            impuesto.codigo = detail.taxCode
            impuesto.codigoPorcentaje = detail.percentageCode
            impuesto.tarifa = detail.taxIva!!.setScale(2, BigDecimal.ROUND_HALF_UP)
            impuesto.baseImponible = detail.taxBase!!.setScale(2, BigDecimal.ROUND_HALF_UP)
            impuesto.valor = detail.value!!.setScale(2, BigDecimal.ROUND_HALF_UP)

            impuestos.impuesto.add(impuesto)
        }

        return impuestos
    }
}
