package dev.joguenco.roqui.taxpayer.mapper

import dev.joguenco.roqui.taxpayer.dto.TaxpayerDto
import dev.joguenco.roqui.taxpayer.model.Taxpayer
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring")
interface ITaxpayerMapper {

//    @Mapping(source = "id", target = "id")
//    @Mapping(source = "identification", target = "identification")
//    @Mapping(source = "legalName", target = "legalName")
//    @Mapping(source = "forcedAccounting", target = "forcedAccounting")
//    @Mapping(source = "specialTaxpayer", target = "specialTaxpayer")
//    @Mapping(source = "retentionAgent", target = "retentionAgent")
//    @Mapping(source = "rimpe", target = "rimpe")
    fun toDto(taxpayerEntity: Taxpayer): TaxpayerDto
}