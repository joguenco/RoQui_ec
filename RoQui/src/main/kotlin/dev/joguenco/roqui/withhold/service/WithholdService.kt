package dev.joguenco.roqui.withhold.service

import dev.joguenco.roqui.information.model.GeneralObservation
import dev.joguenco.roqui.information.model.Information
import dev.joguenco.roqui.information.repository.InformationRepository
import dev.joguenco.roqui.taxpayer.repository.EstablishmentRepository
import dev.joguenco.roqui.taxpayer.repository.TaxpayerRepository
import dev.joguenco.roqui.withhold.dto.TributaryInformation
import dev.joguenco.roqui.withhold.model.WithholdDetail
import dev.joguenco.roqui.withhold.model.WithholdDocumentTax
import dev.joguenco.roqui.withhold.model.WithholdSupport
import dev.joguenco.roqui.withhold.repository.CustomWithholdRepository
import org.springframework.stereotype.Service

@Service
class WithholdService(
    private val withholdRepository: CustomWithholdRepository,
    private val taxPayerRepository: TaxpayerRepository,
    private val establishmentRepository: EstablishmentRepository,
    private val informationRepository: InformationRepository,
) {
    fun count(code: String, number: String): Long {
        return withholdRepository.countByCodeAndNumber(code, number)
    }

    fun getWithholdAndTaxpayer(code: String, number: String): TributaryInformation {
        val withhold = withholdRepository.findByCodeAndNumber(code, number)
        val taxpayer = taxPayerRepository.findById(1).get()
        val establishment = establishmentRepository.findByCode(withhold.establishment!!)
        val principalEstablishmentAddress = establishmentRepository.findPrincipal().address

        val tributaryInformation =
            TributaryInformation(
                withhold,
                taxpayer,
                establishment.address,
                principalEstablishmentAddress,
                establishment.businessName,
            )

        return tributaryInformation
    }

    /** Las retenciones aplicadas: una por cada linea del comprobante. */
    fun getWithholdDetail(code: String, number: String): MutableList<WithholdDetail> {
        return withholdRepository.findDetailByCodeAndNumber(code, number)
    }

    /** Un bloque docSustento por cada sustento que use la compra. */
    fun getWithholdSupport(code: String, number: String): MutableList<WithholdSupport> {
        return withholdRepository.findSupportByCodeAndNumber(code, number)
    }

    /**
     * Impuestos que ya traia la compra. No son retenciones: el XSD los exige aparte, dentro del
     * docSustento.
     */
    fun getWithholdDocumentTax(code: String, number: String): MutableList<WithholdDocumentTax> {
        return withholdRepository.findDocumentTaxByCodeAndNumber(code, number)
    }

    fun getWithholdInformation(identification: String): MutableList<Information> {
        return informationRepository.findInformationByIdentification(identification)
    }

    fun getGeneralObservation(): MutableList<GeneralObservation> {
        return informationRepository.findGeneralObservation()
    }
}
