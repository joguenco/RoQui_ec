package dev.joguenco.roqui.taxpayer.mapper

import dev.joguenco.roqui.taxpayer.dto.EstablishmentDto
import dev.joguenco.roqui.taxpayer.model.Establishment
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface EstablishmentMapper {

    fun toDto(establishmentEntity: Establishment): EstablishmentDto
}
