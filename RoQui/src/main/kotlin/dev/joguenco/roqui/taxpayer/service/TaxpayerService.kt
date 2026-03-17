package dev.joguenco.roqui.taxpayer.service

import dev.joguenco.roqui.taxpayer.dto.TaxpayerDto
import dev.joguenco.roqui.taxpayer.mapper.TaxpayerMapper
import dev.joguenco.roqui.taxpayer.repository.TaxpayerRepository
import kotlin.jvm.optionals.getOrNull
import org.springframework.stereotype.Service

@Service
class TaxpayerService(
    private val taxPayerRepository: TaxpayerRepository,
    val taxpayerMapper: TaxpayerMapper,
) {

    fun getTaxpayer(): TaxpayerDto {
        val taxpayer = taxPayerRepository.findById(1).getOrNull()

        return if (taxpayer == null) {
            TaxpayerDto()
        } else {
            taxpayerMapper.toDto(taxpayer)
        }
    }
}
