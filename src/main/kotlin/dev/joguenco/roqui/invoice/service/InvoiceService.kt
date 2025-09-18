package dev.joguenco.roqui.invoice.service

import dev.joguenco.roqui.information.model.Information
import dev.joguenco.roqui.information.repository.InformationRepository
import dev.joguenco.roqui.invoice.dto.TaxTotal
import dev.joguenco.roqui.invoice.dto.TributaryInformation
import dev.joguenco.roqui.invoice.model.InvoiceDetail
import dev.joguenco.roqui.invoice.model.Payment
import dev.joguenco.roqui.invoice.model.TaxDetail
import dev.joguenco.roqui.invoice.repository.CustomInvoiceRepository
import dev.joguenco.roqui.parameter.repository.ParameterRepository
import dev.joguenco.roqui.taxpayer.repository.EstablishmentRepository
import dev.joguenco.roqui.taxpayer.repository.TaxpayerRepository
import java.math.BigDecimal
import kotlin.collections.forEach
import org.springframework.stereotype.Service

@Service
class InvoiceService(
    private val invoiceRepository: CustomInvoiceRepository,
    private val taxPayerRepository: TaxpayerRepository,
    private val establishmentRepository: EstablishmentRepository,
    private val parameterRepository: ParameterRepository,
    private val informationRepository: InformationRepository,
) {

    fun count(code: String, number: String): Long {
        return invoiceRepository.countByCodeAndNumber(code, number)
    }

    fun getInvoiceAndTaxpayer(code: String, number: String): TributaryInformation {
        val invoice = invoiceRepository.findByCodeAndNumber(code, number)
        val taxpayer = taxPayerRepository.findById(1).get()
        val establishment = establishmentRepository.findByCode(invoice.establishment!!)
        val principalEstablishmentAddress = establishmentRepository.findPrincipal().address

        val tributaryInformation =
            TributaryInformation(
                invoice,
                taxpayer,
                establishment.address,
                principalEstablishmentAddress,
                establishment.businessName,
            )

        return tributaryInformation
    }

    fun getInvoiceDetail(code: String, number: String): MutableList<InvoiceDetail> {
        return invoiceRepository.findDetailByCodeAndNumber(code, number)
    }

    fun getInvoiceDetailTax(
        code: String,
        number: String,
        principalCode: String,
        line: Long,
    ): MutableList<TaxDetail> {
        return invoiceRepository.findDetailTax(code, number, principalCode, line)
    }

    fun getInvoiceTax(code: String, number: String): MutableList<TaxTotal> {
        val taxDetails = invoiceRepository.findTotalTaxByCodeAndNumber(code, number)

        val groupedTaxes = mutableMapOf<Pair<String, String>, TaxTotal>()

        for (taxDetail in taxDetails) {
            val key = Pair(taxDetail.taxCode, taxDetail.percentageCode)
            val totalTax =
                groupedTaxes.getOrDefault(
                    key,
                    TaxTotal().apply {
                        taxCode = taxDetail.taxCode
                        percentageCode = taxDetail.percentageCode
                        taxIva = BigDecimal.ZERO
                        taxBase = BigDecimal.ZERO
                        value = BigDecimal.ZERO
                    },
                )

            groupedTaxes[key as Pair<String, String>] = totalTax
        }

        groupedTaxes.values.forEach {
            taxDetails
                .stream()
                .filter { imp ->
                    it.taxCode == imp.taxCode && it.percentageCode == imp.percentageCode
                }
                .forEach { imp ->
                    it.taxBase = it.taxBase?.add(imp.taxBase ?: BigDecimal.ZERO)
                    it.taxIva = imp.taxIva ?: BigDecimal.ZERO
                    it.value = it.value?.add(imp.value ?: BigDecimal.ZERO)
                }
        }

        val taxTotals = mutableListOf<TaxTotal>()

        groupedTaxes.values.forEach { i ->
            val totalTax = TaxTotal()
            totalTax.taxCode = i.taxCode
            totalTax.percentageCode = i.percentageCode
            totalTax.taxIva = i.taxIva?.setScale(2, BigDecimal.ROUND_HALF_UP)
            totalTax.taxBase = i.taxBase?.setScale(2, BigDecimal.ROUND_HALF_UP)
            totalTax.value = i.value?.setScale(2, BigDecimal.ROUND_HALF_UP)

            taxTotals.add(totalTax)
        }

        return taxTotals
    }

    fun getInvoicePayment(code: String, number: String): MutableList<Payment> {
        return invoiceRepository.findPaymentByCodeAndNumber(code, number)
    }

    fun getInvoiceInformation(identification: String): MutableList<Information> {
        return informationRepository.findInformationByIdentification(identification)
    }

    fun getBaseDirectory(): String {
        return parameterRepository.findValueByName("Base Directory")
    }
}
