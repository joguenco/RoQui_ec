package dev.joguenco.roqui.note.debit.repository

import dev.joguenco.roqui.invoice.model.Payment
import dev.joguenco.roqui.invoice.model.TaxDetail
import dev.joguenco.roqui.note.debit.model.DebitNote
import dev.joguenco.roqui.note.debit.model.DebitNoteDetail

interface CustomDebitNoteRepository {

    fun countByCodeAndNumber(code: String, number: String): Long

    fun findByCodeAndNumber(code: String, number: String): DebitNote

    fun findDetailByCodeAndNumber(code: String, number: String): MutableList<DebitNoteDetail>

    fun findTotalTaxByCodeAndNumber(code: String, number: String): MutableList<TaxDetail>

    // Verifiqué que InfoNotaDebito sí tiene pagos. La nota de crédito no
    fun findPaymentByCodeAndNumber(code: String, number: String): MutableList<Payment>
}
