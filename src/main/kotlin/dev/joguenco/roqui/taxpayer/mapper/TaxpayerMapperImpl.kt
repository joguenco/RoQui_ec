package dev.joguenco.roqui.taxpayer.mapper

import dev.joguenco.roqui.taxpayer.dto.TaxpayerDto
import dev.joguenco.roqui.taxpayer.model.Taxpayer

class TaxpayerMapperImpl : ITaxpayerMapper {

    override fun toDto(taxpayerEntity: Taxpayer): TaxpayerDto {
        return TaxpayerDto(
            id = taxpayerEntity.id,
            identification = taxpayerEntity.identification,
            legalName = taxpayerEntity.legalName,
            forcedAccounting = taxpayerEntity.forcedAccounting,
            specialTaxpayer = taxpayerEntity.specialTaxpayer,
            retentionAgent = taxpayerEntity.retentionAgent,
            rimpe = taxpayerEntity.rimpe
        )
    }
}