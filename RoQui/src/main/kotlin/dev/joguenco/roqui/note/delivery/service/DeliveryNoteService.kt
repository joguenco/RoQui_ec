package dev.joguenco.roqui.note.delivery.service

import dev.joguenco.roqui.information.model.GeneralObservation
import dev.joguenco.roqui.information.repository.InformationRepository
import dev.joguenco.roqui.note.delivery.dto.TributaryInformation
import dev.joguenco.roqui.note.delivery.model.DeliveryNoteDetail
import dev.joguenco.roqui.note.delivery.model.DeliveryNoteReceiver
import dev.joguenco.roqui.note.delivery.repository.CustomDeliveryNoteRepository
import dev.joguenco.roqui.taxpayer.repository.EstablishmentRepository
import dev.joguenco.roqui.taxpayer.repository.TaxpayerRepository
import org.springframework.stereotype.Service

@Service
class DeliveryNoteService(
    private val deliveryNoteRepository: CustomDeliveryNoteRepository,
    private val taxPayerRepository: TaxpayerRepository,
    private val establishmentRepository: EstablishmentRepository,
    private val informationRepository: InformationRepository,
) {
    fun count(code: String, number: String): Long {
        return deliveryNoteRepository.countByCodeAndNumber(code, number)
    }

    fun getDeliveryNoteAndTaxpayer(code: String, number: String): TributaryInformation {
        val deliveryNote = deliveryNoteRepository.findByCodeAndNumber(code, number)
        val taxpayer = taxPayerRepository.findById(1).get()
        val establishment = establishmentRepository.findByCode(deliveryNote.establishment!!)
        val principalEstablishmentAddress = establishmentRepository.findPrincipal().address

        return TributaryInformation(
            deliveryNote,
            taxpayer,
            establishment.address,
            principalEstablishmentAddress,
            establishment.businessName,
        )
    }

    /** Los destinatarios del viaje: uno por cada factura despachada. */
    fun getReceivers(code: String, number: String): MutableList<DeliveryNoteReceiver> {
        return deliveryNoteRepository.findReceiverByCodeAndNumber(code, number)
    }

    /** La mercaderia de un destinatario. El SRI la exige dentro de cada uno. */
    fun getDetail(code: String, number: String, line: Long): MutableList<DeliveryNoteDetail> {
        return deliveryNoteRepository.findDetailByCodeAndNumberAndLine(code, number, line)
    }

    fun getGeneralObservation(): MutableList<GeneralObservation> {
        return informationRepository.findGeneralObservation()
    }
}
