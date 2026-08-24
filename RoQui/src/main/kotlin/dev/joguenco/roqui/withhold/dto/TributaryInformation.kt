package dev.joguenco.roqui.withhold.dto

import dev.joguenco.roqui.taxpayer.model.Taxpayer
import dev.joguenco.roqui.withhold.model.Withhold

data class TributaryInformation(
    val withhold: Withhold,
    val taxpayer: Taxpayer,
    val establishmentAddress: String? = null,
    val principalEstablishmentAddress: String? = null,
    val establishmentBusinessName: String? = null,
)
