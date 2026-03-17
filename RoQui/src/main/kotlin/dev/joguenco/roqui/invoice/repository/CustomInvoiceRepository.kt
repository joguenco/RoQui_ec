package dev.joguenco.roqui.invoice.repository

import dev.joguenco.roqui.invoice.model.Invoice
import dev.joguenco.roqui.invoice.model.InvoiceDetail
import dev.joguenco.roqui.invoice.model.Payment
import dev.joguenco.roqui.invoice.model.TaxDetail

interface CustomInvoiceRepository {

    fun countByCodeAndNumber(code: String, number: String): Long

    fun findByCodeAndNumber(code: String, number: String): Invoice

    fun findDetailByCodeAndNumber(code: String, number: String): MutableList<InvoiceDetail>

    fun findDetailTax(
        code: String,
        number: String,
        principalCode: String,
        line: Long,
    ): MutableList<TaxDetail>

    fun findTotalTaxByCodeAndNumber(code: String, number: String): MutableList<TaxDetail>

    fun findPaymentByCodeAndNumber(code: String, number: String): MutableList<Payment>
}
