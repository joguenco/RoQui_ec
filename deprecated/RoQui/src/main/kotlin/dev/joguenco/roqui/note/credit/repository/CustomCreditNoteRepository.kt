package dev.joguenco.roqui.note.credit.repository

import dev.joguenco.roqui.invoice.model.TaxDetail
import dev.joguenco.roqui.note.credit.model.CreditNote
import dev.joguenco.roqui.note.credit.model.CreditNoteDetail

interface CustomCreditNoteRepository {

    fun countByCodeAndNumber(code: String, number: String): Long

    fun findByCodeAndNumber(code: String, number: String): CreditNote

    fun findDetailByCodeAndNumber(code: String, number: String): MutableList<CreditNoteDetail>

    fun findDetailTax(
        code: String,
        number: String,
        principalCode: String,
        line: Long,
    ): MutableList<TaxDetail>

    fun findTotalTaxByCodeAndNumber(code: String, number: String): MutableList<TaxDetail>
}
