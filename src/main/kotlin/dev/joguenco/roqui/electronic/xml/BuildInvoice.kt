package dev.joguenco.roqui.electronic.xml

import dev.joguenco.roqui.invoice.service.InvoiceService
import dev.joguenco.roqui.util.FilesUtil
import ec.gob.sri.invoice.v210.*
import jakarta.xml.bind.JAXBContext
import jakarta.xml.bind.Marshaller
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.io.StringWriter
import java.math.BigDecimal
import java.text.SimpleDateFormat

class BuildInvoice(
    val code: String,
    val number: String,
    private val baseDirectory: String,
    private val invoiceService: InvoiceService,
) {

    private val tributaryInformation = invoiceService.getInvoiceAndTaxpayer(code, number)

    fun xml(): String {
        val factura = Factura()

        try {
            factura.id = "comprobante"
            factura.version = "2.1.0"
            factura.infoTributaria = buildInfoTributaria()
            factura.infoFactura = buildInfoFactura()
            factura.detalles = buildDetails()
            factura.infoAdicional =
                buildAdditionalInformation(tributaryInformation.invoice.identification!!)

            val jaxbContext = JAXBContext.newInstance(Factura::class.java)
            val marshaller = jaxbContext.createMarshaller()
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
            marshaller.setProperty("jaxb.encoding", "UTF-8")

            val stringWriter = StringWriter()
            stringWriter.use { marshaller.marshal(factura, stringWriter) }

            val pathGenerated =
                FilesUtil.directory(
                    baseDirectory + "${File.separatorChar}Generated",
                    tributaryInformation.invoice.date!!,
                )

            val out =
                OutputStreamWriter(
                    FileOutputStream(
                        "$pathGenerated${File.separatorChar}" +
                            "${factura.infoTributaria.claveAcceso}.xml"
                    ),
                    "UTF-8",
                )

            marshaller.marshal(factura, out)
            println(stringWriter)

            return factura.infoTributaria.claveAcceso
        } catch (e: Exception) {
            println(e.message)
            return ""
        }
    }

    private fun buildAdditionalInformation(identification: String): Factura.InfoAdicional? {
        val infoAdicional = Factura.InfoAdicional()
        val additionalInformation = invoiceService.getInvoiceInformation(identification)

        for (information in additionalInformation) {
            val campoAdicional = Factura.InfoAdicional.CampoAdicional()
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

        if (tributaryInformation.invoice.accessKey!!.length == 49) {
            infoTributaria.claveAcceso = tributaryInformation.invoice.accessKey
            infoTributaria.ambiente = infoTributaria.claveAcceso.substring(23, 24)
            infoTributaria.tipoEmision = infoTributaria.claveAcceso.substring(39, 40)
        }

        infoTributaria.codDoc = tributaryInformation.invoice.codeDocument
        infoTributaria.estab = tributaryInformation.invoice.establishment
        infoTributaria.ptoEmi = tributaryInformation.invoice.emissionPoint
        infoTributaria.secuencial = tributaryInformation.invoice.sequence
        infoTributaria.dirMatriz = tributaryInformation.principalEstablishmentAddress
        infoTributaria.contribuyenteRimpe = tributaryInformation.taxpayer.other
        infoTributaria.agenteRetencion = tributaryInformation.taxpayer.retentionAgent

        return infoTributaria
    }

    private fun buildInfoFactura(): Factura.InfoFactura {
        val infoFactura = Factura.InfoFactura()

        infoFactura.fechaEmision =
            SimpleDateFormat("dd/MM/yyyy").format(tributaryInformation.invoice.date)
        infoFactura.dirEstablecimiento = tributaryInformation.establishmentAddress
        infoFactura.contribuyenteEspecial = tributaryInformation.taxpayer.specialTaxpayer
        if (tributaryInformation.taxpayer.forcedAccounting == "SI") {
            infoFactura.obligadoContabilidad = ObligadoContabilidad.SI
        } else {
            infoFactura.obligadoContabilidad = ObligadoContabilidad.NO
        }
        infoFactura.tipoIdentificacionComprador = tributaryInformation.invoice.identificationType
        infoFactura.identificacionComprador = tributaryInformation.invoice.identification
        infoFactura.razonSocialComprador = tributaryInformation.invoice.legalName
        infoFactura.direccionComprador = tributaryInformation.invoice.address
        infoFactura.guiaRemision = tributaryInformation.invoice.deliveryNote
        infoFactura.totalSinImpuestos =
            tributaryInformation.invoice.totalWithoutTaxes!!.setScale(2, BigDecimal.ROUND_HALF_UP)
        infoFactura.importeTotal =
            tributaryInformation.invoice.total!!.setScale(2, BigDecimal.ROUND_HALF_UP)
        infoFactura.propina = BigDecimal(0).setScale(2)
        infoFactura.totalDescuento = BigDecimal(0).setScale(2)
        infoFactura.moneda = "DOLAR"

        infoFactura.totalConImpuestos = buildTotals()

        infoFactura.pagos = buildPayments()

        return infoFactura
    }

    private fun buildPayments(): Pagos {
        val pagos = Pagos()
        val payments = invoiceService.getInvoicePayment(code, number)

        for (payment in payments) {
            val pago = Pagos.Pago()
            pago.formaPago = payment.wayPay
            pago.total = payment.total!!.setScale(2, BigDecimal.ROUND_HALF_UP)
            pago.plazo = payment.paymentDeadline
            pago.unidadTiempo = payment.unitTime

            pagos.pago.add(pago)
        }

        return pagos
    }

    private fun buildTotals(): Factura.InfoFactura.TotalConImpuestos? {
        val totalConImpuestos = Factura.InfoFactura.TotalConImpuestos()
        val taxTotals = invoiceService.getInvoiceTax(code, number)

        for (taxTotal in taxTotals) {
            val totalImpuesto = Factura.InfoFactura.TotalConImpuestos.TotalImpuesto()
            totalImpuesto.codigo = taxTotal.taxCode
            totalImpuesto.codigoPorcentaje = taxTotal.percentageCode
            totalImpuesto.baseImponible = taxTotal.taxBase!!.setScale(2, BigDecimal.ROUND_HALF_UP)
            totalImpuesto.tarifa = taxTotal.taxIva!!.setScale(2, BigDecimal.ROUND_HALF_UP)
            totalImpuesto.valor = taxTotal.value!!.setScale(2, BigDecimal.ROUND_HALF_UP)

            totalConImpuestos.totalImpuesto.add(totalImpuesto)
        }

        return totalConImpuestos
    }

    private fun buildDetails(): Factura.Detalles {
        val invoiceDetail = invoiceService.getInvoiceDetail(code, number)
        val detalles = Factura.Detalles()

        for (detail in invoiceDetail) {
            val facturaDetalle = Factura.Detalles.Detalle()

            facturaDetalle.codigoPrincipal = detail.principalCode
            facturaDetalle.descripcion = detail.name
            facturaDetalle.unidadMedida = detail.unit
            facturaDetalle.cantidad = detail.quantity!!.setScale(2, BigDecimal.ROUND_HALF_UP)
            facturaDetalle.precioUnitario = detail.unitPrice!!.setScale(2, BigDecimal.ROUND_HALF_UP)
            facturaDetalle.descuento = BigDecimal(0).setScale(2)
            facturaDetalle.precioTotalSinImpuesto =
                detail.totalPriceWithoutTax!!.setScale(2, BigDecimal.ROUND_HALF_UP)
            facturaDetalle.impuestos = buildDetailTax(detail.principalCode!!, detail.line!!)

            detalles.detalle.add(facturaDetalle)
        }

        return detalles
    }

    private fun buildDetailTax(
        principalCode: String,
        line: Long,
    ): Factura.Detalles.Detalle.Impuestos? {
        val impuestos = Factura.Detalles.Detalle.Impuestos()
        val taxDetail = invoiceService.getInvoiceDetailTax(code, number, principalCode, line)

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
