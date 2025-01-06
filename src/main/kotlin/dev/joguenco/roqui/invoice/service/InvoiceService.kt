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
import org.springframework.stereotype.Service

@Service
class InvoiceService(
    private val invoiceRepository: CustomInvoiceRepository,
    private val taxPayerRepository: TaxpayerRepository,
    private val establishmentRepository: EstablishmentRepository,
    private val parameterRepository: ParameterRepository,
    private val informationRepository: InformationRepository
) {

    fun count(code: String, number: String): Long {
        return invoiceRepository.countByCodeAndNumber(code, number)
    }

    fun getInvoiceAndTaxpayer(code: String, number: String): TributaryInformation {
        val invoice = invoiceRepository.findByCodeAndNumber(code, number)
        val taxpayer = taxPayerRepository.findById(1).get()
        val establishment = establishmentRepository.findByCode(invoice.establishment!!)
        val principalEstablishmentAddress = establishmentRepository.findPrincipal().address

        val tributaryInformation = TributaryInformation(
            invoice,
            taxpayer,
            establishment.address,
            principalEstablishmentAddress,
            establishment.businessName
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
        line: Long
    ): MutableList<TaxDetail> {
        return invoiceRepository.findDetailTax(code, number, principalCode, line)
    }

    fun getInvoiceTax(code: String, number: String): MutableList<TaxTotal> {
        return invoiceRepository.findTotalTaxByCodeAndNumber(code, number)
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