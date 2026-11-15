package dev.joguenco.roqui.information.service

import dev.joguenco.roqui.electronic.repository.CustomDocumentRepository
import dev.joguenco.roqui.information.repository.InformationRepository
import dev.joguenco.roqui.invoice.repository.CustomInvoiceRepository
import dev.joguenco.roqui.liquidation.repository.CustomLiquidationRepository
import dev.joguenco.roqui.note.credit.repository.CustomCreditNoteRepository
import dev.joguenco.roqui.note.debit.repository.CustomDebitNoteRepository
import dev.joguenco.roqui.withhold.repository.CustomWithholdRepository
import org.springframework.stereotype.Service

@Service
class InformationService(
    private val informationRepository: InformationRepository,
    private val invoiceRepository: CustomInvoiceRepository,
    private val creditNoteRepository: CustomCreditNoteRepository,
    private val debitNoteRepository: CustomDebitNoteRepository,
    private val liquidationRepository: CustomLiquidationRepository,
    private val withholdRepository: CustomWithholdRepository,
    private val documentRepository: CustomDocumentRepository,
) {

    fun getEmailByIdentification(identification: String): String? {
        return informationRepository.findEmailByIdentification(identification)
    }

    fun getInvoice(code: String, number: String) =
        invoiceRepository.findByCodeAndNumber(code, number)

    fun getCreditNote(code: String, number: String) =
        creditNoteRepository.findByCodeAndNumber(code, number)

    fun getDebitNote(code: String, number: String) =
        debitNoteRepository.findByCodeAndNumber(code, number)

    fun getLiquidation(code: String, number: String) =
        liquidationRepository.findByCodeAndNumber(code, number)

    fun getWithhold(code: String, number: String) =
        withholdRepository.findByCodeAndNumber(code, number)

    fun getLegalNameOfTaxpayer(): String {
        return informationRepository.findLegalNameOfTaxpayer()
    }

    fun isAuthorized(code: String, number: String): Boolean {
        try {
            val document = documentRepository.findByCodeAndNumber(code, number)
            return document.status == "AUTORIZADO"
        } catch (e: NoSuchElementException) {
            return false
        }
    }
}
