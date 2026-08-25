package dev.joguenco.roqui.electronic.xml

import dev.joguenco.roqui.note.debit.service.DebitNoteService
import dev.joguenco.roqui.util.FilesUtil
import ec.gob.sri.note.debit.v100.Impuesto
import ec.gob.sri.note.debit.v100.InfoTributaria
import ec.gob.sri.note.debit.v100.NotaDebito
import ec.gob.sri.note.debit.v100.ObligadoContabilidad
import ec.gob.sri.note.debit.v100.Pago
import jakarta.xml.bind.JAXBContext
import jakarta.xml.bind.Marshaller
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.io.StringWriter
import java.math.BigDecimal
import java.text.SimpleDateFormat

class BuildDebitNote(
    val code: String,
    val number: String,
    private val baseDirectory: String,
    private val debitNoteService: DebitNoteService,
) {

    private val tributaryInformation = debitNoteService.getDebitNoteAndTaxpayer(code, number)

    fun xml(): Pair<String, String> {
        val notaDebito = NotaDebito()

        try {
            notaDebito.id = "comprobante"
            notaDebito.version = "1.0.0"
            notaDebito.infoTributaria = buildInfoTributaria()
            notaDebito.infoNotaDebito = buildInfoNotaDebito()
            notaDebito.motivos = buildMotivos()
            notaDebito.infoAdicional =
                buildAdditionalInformation(tributaryInformation.debitNote.identification!!)

            val jaxbContext = JAXBContext.newInstance(NotaDebito::class.java)
            val marshaller = jaxbContext.createMarshaller()
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
            marshaller.setProperty("jaxb.encoding", "UTF-8")

            val stringWriter = StringWriter()
            stringWriter.use { marshaller.marshal(notaDebito, stringWriter) }

            val pathGenerated =
                FilesUtil.directory(
                    baseDirectory + "${File.separatorChar}generated",
                    tributaryInformation.debitNote.date!!,
                )

            val out =
                OutputStreamWriter(
                    FileOutputStream(
                        "$pathGenerated${File.separatorChar}" +
                            "${notaDebito.infoTributaria.claveAcceso}.xml"
                    ),
                    "UTF-8",
                )

            marshaller.marshal(notaDebito, out)
            println(stringWriter)

            return Pair(pathGenerated, notaDebito.infoTributaria.claveAcceso)
        } catch (e: Exception) {
            println("Error BuildDebitNote: ${e.message}")
            return Pair("", "")
        }
    }

    private fun buildInfoTributaria(): InfoTributaria {
        val infoTributaria = InfoTributaria()

        infoTributaria.ruc = tributaryInformation.taxpayer.identification
        infoTributaria.razonSocial = tributaryInformation.taxpayer.legalName
        infoTributaria.nombreComercial = tributaryInformation.establishmentBusinessName

        if (tributaryInformation.debitNote.accessKey!!.length == 49) {
            infoTributaria.claveAcceso = tributaryInformation.debitNote.accessKey
            infoTributaria.ambiente = infoTributaria.claveAcceso.substring(23, 24)
            infoTributaria.tipoEmision = infoTributaria.claveAcceso.substring(39, 40)
        }

        infoTributaria.codDoc = tributaryInformation.debitNote.codeDocument
        infoTributaria.estab = tributaryInformation.debitNote.establishment
        infoTributaria.ptoEmi = tributaryInformation.debitNote.emissionPoint
        infoTributaria.secuencial = tributaryInformation.debitNote.sequence
        infoTributaria.dirMatriz = tributaryInformation.principalEstablishmentAddress
        infoTributaria.contribuyenteRimpe = tributaryInformation.taxpayer.regime
        infoTributaria.agenteRetencion = tributaryInformation.taxpayer.retentionAgent

        return infoTributaria
    }

    private fun buildInfoNotaDebito(): NotaDebito.InfoNotaDebito {
        val infoNotaDebito = NotaDebito.InfoNotaDebito()

        infoNotaDebito.fechaEmision =
            SimpleDateFormat("dd/MM/yyyy").format(tributaryInformation.debitNote.date)
        infoNotaDebito.dirEstablecimiento = tributaryInformation.establishmentAddress
        infoNotaDebito.tipoIdentificacionComprador =
            tributaryInformation.debitNote.identificationType
        infoNotaDebito.razonSocialComprador = tributaryInformation.debitNote.legalName
        infoNotaDebito.identificacionComprador = tributaryInformation.debitNote.identification
        infoNotaDebito.contribuyenteEspecial = tributaryInformation.taxpayer.specialTaxpayer

        if (tributaryInformation.taxpayer.forcedAccounting == "SI") {
            infoNotaDebito.obligadoContabilidad = ObligadoContabilidad.SI
        } else {
            infoNotaDebito.obligadoContabilidad = ObligadoContabilidad.NO
        }

        infoNotaDebito.codDocModificado = tributaryInformation.debitNote.updatedCodeDocument

        if (tributaryInformation.debitNote.updatedNumberDocument?.length == 15) {
            val establishment =
                tributaryInformation.debitNote.updatedNumberDocument!!.substring(0, 3)
            val emissionPoint =
                tributaryInformation.debitNote.updatedNumberDocument!!.substring(3, 6)
            val sequential = tributaryInformation.debitNote.updatedNumberDocument!!.substring(6, 15)
            infoNotaDebito.numDocModificado = "$establishment-$emissionPoint-$sequential"
        }

        if (tributaryInformation.debitNote.updatedDateDocument != null) {
            infoNotaDebito.fechaEmisionDocSustento =
                SimpleDateFormat("dd/MM/yyyy")
                    .format(tributaryInformation.debitNote.updatedDateDocument)
        }

        infoNotaDebito.totalSinImpuestos =
            tributaryInformation.debitNote.totalWithoutTaxes!!.setScale(2, BigDecimal.ROUND_HALF_UP)

        infoNotaDebito.impuestos = buildTotals()

        infoNotaDebito.valorTotal =
            tributaryInformation.debitNote.total!!.setScale(2, BigDecimal.ROUND_HALF_UP)

        buildPayments(infoNotaDebito)

        return infoNotaDebito
    }

    private fun buildTotals(): NotaDebito.InfoNotaDebito.Impuestos {
        val impuestos = NotaDebito.InfoNotaDebito.Impuestos()
        val taxTotals = debitNoteService.getDebitNoteTax(code, number)

        for (taxTotal in taxTotals) {
            val impuesto = Impuesto()
            impuesto.codigo = taxTotal.taxCode
            impuesto.codigoPorcentaje = taxTotal.percentageCode
            impuesto.tarifa = taxTotal.taxIva!!.setScale(2, BigDecimal.ROUND_HALF_UP)
            impuesto.baseImponible = taxTotal.taxBase!!.setScale(2, BigDecimal.ROUND_HALF_UP)
            impuesto.valor = taxTotal.value!!.setScale(2, BigDecimal.ROUND_HALF_UP)

            impuestos.impuesto.add(impuesto)
        }

        return impuestos
    }

    private fun buildMotivos(): NotaDebito.Motivos {
        val motivos = NotaDebito.Motivos()
        val debitNoteDetails = debitNoteService.getDebitNoteDetail(code, number)

        for (detail in debitNoteDetails) {
            val motivo = NotaDebito.Motivos.Motivo()
            motivo.razon = detail.reason
            motivo.valor = detail.value!!.setScale(2, BigDecimal.ROUND_HALF_UP)

            motivos.motivo.add(motivo)
        }

        return motivos
    }

    private fun buildPayments(infoNotaDebito: NotaDebito.InfoNotaDebito) {
        val payments = debitNoteService.getDebitNotePayment(code, number)

        if (payments.isEmpty()) {
            return
        }

        val pagos = NotaDebito.InfoNotaDebito.Pagos()

        for (payment in payments) {
            val pago = Pago()
            pago.formaPago = payment.wayPay
            pago.total = payment.total!!.setScale(2, BigDecimal.ROUND_HALF_UP)
            pago.plazo = payment.paymentDeadline
            pago.unidadTiempo = payment.unitTime

            pagos.pago.add(pago)
        }

        infoNotaDebito.pagos.add(pagos)
    }

    private fun buildAdditionalInformation(identification: String): NotaDebito.InfoAdicional? {
        var infoAdicional = NotaDebito.InfoAdicional()
        val additionalInformation = debitNoteService.getDebitNoteInformation(identification)

        for (information in additionalInformation) {
            val campoAdicional = NotaDebito.InfoAdicional.CampoAdicional()
            campoAdicional.nombre = information.name
            campoAdicional.value = information.value

            infoAdicional.campoAdicional.add(campoAdicional)
        }

        infoAdicional = buildGeneralObservation(infoAdicional)

        return infoAdicional
    }

    private fun buildGeneralObservation(
        infoAdicional: NotaDebito.InfoAdicional
    ): NotaDebito.InfoAdicional {
        val generalObservation = debitNoteService.getGeneralObservation()

        for (observation in generalObservation) {
            val campoAdicional = NotaDebito.InfoAdicional.CampoAdicional()
            campoAdicional.nombre = observation.name
            campoAdicional.value = observation.value

            infoAdicional.campoAdicional.add(campoAdicional)
        }

        return infoAdicional
    }
}
