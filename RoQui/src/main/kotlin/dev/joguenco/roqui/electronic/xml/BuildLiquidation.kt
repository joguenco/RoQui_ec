package dev.joguenco.roqui.electronic.xml

import dev.joguenco.roqui.liquidation.service.LiquidationService
import dev.joguenco.roqui.util.FilesUtil
import ec.gob.sri.liquidation.v110.Impuesto
import ec.gob.sri.liquidation.v110.InfoTributaria
import ec.gob.sri.liquidation.v110.LiquidacionCompra
import ec.gob.sri.liquidation.v110.ObligadoContabilidad
import jakarta.xml.bind.JAXBContext
import jakarta.xml.bind.Marshaller
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.io.StringWriter
import java.math.BigDecimal
import java.text.SimpleDateFormat

class BuildLiquidation(
    val code: String,
    val number: String,
    private val baseDirectory: String,
    private val liquidationService: LiquidationService,
) {

    private val tributaryInformation = liquidationService.getLiquidationAndTaxpayer(code, number)

    fun xml(): Pair<String, String> {
        val liquidacionCompra = LiquidacionCompra()

        try {
            liquidacionCompra.id = "comprobante"
            liquidacionCompra.version = "1.1.0"
            liquidacionCompra.infoTributaria = buildInfoTributaria()
            liquidacionCompra.infoLiquidacionCompra = buildInfoLiquidacionCompra()
            liquidacionCompra.detalles = buildDetails()
            liquidacionCompra.infoAdicional =
                buildAdditionalInformation(tributaryInformation.liquidation.identification!!)

            val jaxbContext = JAXBContext.newInstance(LiquidacionCompra::class.java)
            val marshaller = jaxbContext.createMarshaller()
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
            marshaller.setProperty("jaxb.encoding", "UTF-8")

            val stringWriter = StringWriter()
            stringWriter.use { marshaller.marshal(liquidacionCompra, stringWriter) }

            val pathGenerated =
                FilesUtil.directory(
                    baseDirectory + "${File.separatorChar}generated",
                    tributaryInformation.liquidation.date!!,
                )

            val out =
                OutputStreamWriter(
                    FileOutputStream(
                        "$pathGenerated${File.separatorChar}" +
                            "${liquidacionCompra.infoTributaria.claveAcceso}.xml"
                    ),
                    "UTF-8",
                )

            marshaller.marshal(liquidacionCompra, out)
            println(stringWriter)

            return Pair(pathGenerated, liquidacionCompra.infoTributaria.claveAcceso)
        } catch (e: Exception) {
            println("Error BuildLiquidation: ${e.message}")
            return Pair("", "")
        }
    }

    private fun buildInfoTributaria(): InfoTributaria {
        val infoTributaria = InfoTributaria()

        infoTributaria.ruc = tributaryInformation.taxpayer.identification
        infoTributaria.razonSocial = tributaryInformation.taxpayer.legalName
        infoTributaria.nombreComercial = tributaryInformation.establishmentBusinessName

        if (tributaryInformation.liquidation.accessKey!!.length == 49) {
            infoTributaria.claveAcceso = tributaryInformation.liquidation.accessKey
            infoTributaria.ambiente = infoTributaria.claveAcceso.substring(23, 24)
            infoTributaria.tipoEmision = infoTributaria.claveAcceso.substring(39, 40)
        }

        infoTributaria.codDoc = tributaryInformation.liquidation.codeDocument
        infoTributaria.estab = tributaryInformation.liquidation.establishment
        infoTributaria.ptoEmi = tributaryInformation.liquidation.emissionPoint
        infoTributaria.secuencial = tributaryInformation.liquidation.sequence
        infoTributaria.dirMatriz = tributaryInformation.principalEstablishmentAddress
        infoTributaria.contribuyenteRimpe = tributaryInformation.taxpayer.regime
        infoTributaria.agenteRetencion = tributaryInformation.taxpayer.retentionAgent

        return infoTributaria
    }

    private fun buildInfoLiquidacionCompra(): LiquidacionCompra.InfoLiquidacionCompra {
        val infoLiquidacionCompra = LiquidacionCompra.InfoLiquidacionCompra()

        infoLiquidacionCompra.fechaEmision =
            SimpleDateFormat("dd/MM/yyyy").format(tributaryInformation.liquidation.date)
        infoLiquidacionCompra.dirEstablecimiento = tributaryInformation.establishmentAddress
        infoLiquidacionCompra.contribuyenteEspecial = tributaryInformation.taxpayer.specialTaxpayer

        if (tributaryInformation.taxpayer.forcedAccounting == "SI") {
            infoLiquidacionCompra.obligadoContabilidad = ObligadoContabilidad.SI
        } else {
            infoLiquidacionCompra.obligadoContabilidad = ObligadoContabilidad.NO
        }

        // En la liquidacion de compra el sujeto es el PROVEEDOR, no el comprador
        infoLiquidacionCompra.tipoIdentificacionProveedor =
            tributaryInformation.liquidation.identificationType
        infoLiquidacionCompra.razonSocialProveedor = tributaryInformation.liquidation.legalName
        infoLiquidacionCompra.identificacionProveedor =
            tributaryInformation.liquidation.identification
        infoLiquidacionCompra.direccionProveedor = tributaryInformation.liquidation.address

        infoLiquidacionCompra.totalSinImpuestos =
            tributaryInformation.liquidation.totalWithoutTaxes!!.setScale(
                2,
                BigDecimal.ROUND_HALF_UP,
            )
        infoLiquidacionCompra.totalDescuento =
            tributaryInformation.liquidation.discount!!.setScale(2, BigDecimal.ROUND_HALF_UP)

        infoLiquidacionCompra.totalConImpuestos = buildTotals()

        infoLiquidacionCompra.importeTotal =
            tributaryInformation.liquidation.total!!.setScale(2, BigDecimal.ROUND_HALF_UP)
        infoLiquidacionCompra.moneda = "DOLAR"

        return infoLiquidacionCompra
    }

    private fun buildTotals(): LiquidacionCompra.InfoLiquidacionCompra.TotalConImpuestos {
        val totalConImpuestos = LiquidacionCompra.InfoLiquidacionCompra.TotalConImpuestos()
        val taxTotals = liquidationService.getLiquidationTax(code, number)

        for (taxTotal in taxTotals) {
            val totalImpuesto =
                LiquidacionCompra.InfoLiquidacionCompra.TotalConImpuestos.TotalImpuesto()
            totalImpuesto.codigo = taxTotal.taxCode
            totalImpuesto.codigoPorcentaje = taxTotal.percentageCode
            totalImpuesto.baseImponible = taxTotal.taxBase!!.setScale(2, BigDecimal.ROUND_HALF_UP)
            totalImpuesto.tarifa = taxTotal.taxIva!!.setScale(2, BigDecimal.ROUND_HALF_UP)
            totalImpuesto.valor = taxTotal.value!!.setScale(2, BigDecimal.ROUND_HALF_UP)

            totalConImpuestos.totalImpuesto.add(totalImpuesto)
        }

        return totalConImpuestos
    }

    private fun buildDetails(): LiquidacionCompra.Detalles {
        val liquidationDetail = liquidationService.getLiquidationDetail(code, number)
        val detalles = LiquidacionCompra.Detalles()

        for (detail in liquidationDetail) {
            val liquidacionDetalle = LiquidacionCompra.Detalles.Detalle()

            liquidacionDetalle.codigoPrincipal = detail.principalCode
            liquidacionDetalle.descripcion = detail.name
            liquidacionDetalle.unidadMedida = detail.unit
            liquidacionDetalle.cantidad = detail.quantity!!.setScale(2, BigDecimal.ROUND_HALF_UP)
            liquidacionDetalle.precioUnitario =
                detail.unitPrice!!.setScale(2, BigDecimal.ROUND_HALF_UP)
            liquidacionDetalle.descuento = detail.discount!!.setScale(2, BigDecimal.ROUND_HALF_UP)
            liquidacionDetalle.precioTotalSinImpuesto =
                detail.totalPriceWithoutTax!!.setScale(2, BigDecimal.ROUND_HALF_UP)
            liquidacionDetalle.impuestos = buildDetailTax(detail.principalCode!!, detail.line!!)

            detalles.detalle.add(liquidacionDetalle)
        }

        return detalles
    }

    private fun buildDetailTax(
        principalCode: String,
        line: Long,
    ): LiquidacionCompra.Detalles.Detalle.Impuestos {
        val impuestos = LiquidacionCompra.Detalles.Detalle.Impuestos()
        val taxDetail =
            liquidationService.getLiquidationDetailTax(code, number, principalCode, line)

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

    private fun buildAdditionalInformation(
        identification: String
    ): LiquidacionCompra.InfoAdicional? {
        var infoAdicional = LiquidacionCompra.InfoAdicional()
        val additionalInformation = liquidationService.getLiquidationInformation(identification)

        for (information in additionalInformation) {
            val campoAdicional = LiquidacionCompra.InfoAdicional.CampoAdicional()
            campoAdicional.nombre = information.name
            campoAdicional.value = information.value

            infoAdicional.campoAdicional.add(campoAdicional)
        }

        infoAdicional = buildGeneralObservation(infoAdicional)

        return infoAdicional
    }

    private fun buildGeneralObservation(
        infoAdicional: LiquidacionCompra.InfoAdicional
    ): LiquidacionCompra.InfoAdicional {
        val generalObservation = liquidationService.getGeneralObservation()

        for (observation in generalObservation) {
            val campoAdicional = LiquidacionCompra.InfoAdicional.CampoAdicional()
            campoAdicional.nombre = observation.name
            campoAdicional.value = observation.value

            infoAdicional.campoAdicional.add(campoAdicional)
        }

        return infoAdicional
    }
}
