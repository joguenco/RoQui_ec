package dev.joguenco.roqui.taxpayer.service

import dev.joguenco.roqui.taxpayer.dto.EstablishmentDto
import dev.joguenco.roqui.taxpayer.dto.TaxpayerDto
import dev.joguenco.roqui.taxpayer.mapper.EstablishmentMapper
import dev.joguenco.roqui.taxpayer.mapper.TaxpayerMapper
import dev.joguenco.roqui.taxpayer.repository.EstablishmentRepository
import dev.joguenco.roqui.taxpayer.repository.TaxpayerRepository
import kotlin.jvm.optionals.getOrNull
import org.springframework.stereotype.Service

@Service
class TaxpayerService(
    private val taxPayerRepository: TaxpayerRepository,
    private val establishmentRepository: EstablishmentRepository,
    val taxpayerMapper: TaxpayerMapper,
    val establishmentMapper: EstablishmentMapper,
) {

    fun getTaxpayer(): TaxpayerDto {
        val taxpayer = taxPayerRepository.findById(1).getOrNull()

        return if (taxpayer == null) {
            TaxpayerDto()
        } else {
            taxpayerMapper.toDto(taxpayer)
        }
    }

    fun getEstablishments(): List<EstablishmentDto> {
        return establishmentRepository.findAll().map { establishmentMapper.toDto(it) }
    }
}
