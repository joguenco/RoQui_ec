package dev.joguenco.roqui.taxpayer.mapper

import dev.joguenco.roqui.taxpayer.dto.TaxpayerDto
import dev.joguenco.roqui.taxpayer.model.Taxpayer
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface TaxpayerMapper {

    fun toDto(taxpayerEntity: Taxpayer): TaxpayerDto
}