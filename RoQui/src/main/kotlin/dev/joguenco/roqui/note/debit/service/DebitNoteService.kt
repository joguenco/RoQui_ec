package dev.joguenco.roqui.note.debit.service

import dev.joguenco.roqui.information.model.GeneralObservation
import dev.joguenco.roqui.information.model.Information
import dev.joguenco.roqui.information.repository.InformationRepository
import dev.joguenco.roqui.invoice.dto.TaxTotal
import dev.joguenco.roqui.invoice.model.Payment
import dev.joguenco.roqui.note.debit.dto.TributaryInformation
import dev.joguenco.roqui.note.debit.model.DebitNoteDetail
import dev.joguenco.roqui.note.debit.repository.CustomDebitNoteRepository
import dev.joguenco.roqui.taxpayer.repository.EstablishmentRepository
import dev.joguenco.roqui.taxpayer.repository.TaxpayerRepository
import java.math.BigDecimal
import kotlin.collections.getOrDefault
import org.springframework.stereotype.Service

@Service
class DebitNoteService(
    private val debitNoteRepository: CustomDebitNoteRepository,
    private val taxPayerRepository: TaxpayerRepository,
    private val establishmentRepository: EstablishmentRepository,
    private val informationRepository: InformationRepository,
) {
    fun count(code: String, number: String): Long {
        return debitNoteRepository.countByCodeAndNumber(code, number)
    }

    fun getDebitNoteAndTaxpayer(code: String, number: String): TributaryInformation {
        val debitNote = debitNoteRepository.findByCodeAndNumber(code, number)
        val taxpayer = taxPayerRepository.findById(1).get()
        val establishment = establishmentRepository.findByCode(debitNote.establishment!!)
        val principalEstablishmentAddress = establishmentRepository.findPrincipal().address

        val tributaryInformation =
            TributaryInformation(
                debitNote,
                taxpayer,
                establishment.address,
                principalEstablishmentAddress,
                establishment.businessName,
            )

        return tributaryInformation
    }

    fun getDebitNoteDetail(code: String, number: String): MutableList<DebitNoteDetail> {
        return debitNoteRepository.findDetailByCodeAndNumber(code, number)
    }

    fun getDebitNotePayment(code: String, number: String): MutableList<Payment> {
        return debitNoteRepository.findPaymentByCodeAndNumber(code, number)
    }

    fun getDebitNoteTax(code: String, number: String): MutableList<TaxTotal> {
        val taxDetails = debitNoteRepository.findTotalTaxByCodeAndNumber(code, number)

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

    fun getDebitNoteInformation(identification: String): MutableList<Information> {
        return informationRepository.findInformationByIdentification(identification)
    }

    fun getGeneralObservation(): MutableList<GeneralObservation> {
        return informationRepository.findGeneralObservation()
    }
}
