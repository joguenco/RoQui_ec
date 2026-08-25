package dev.joguenco.roqui.liquidation.dto

import dev.joguenco.roqui.liquidation.model.Liquidation
import dev.joguenco.roqui.taxpayer.model.Taxpayer

data class TributaryInformation(
    val liquidation: Liquidation,
    val taxpayer: Taxpayer,
    val establishmentAddress: String? = null,
    val principalEstablishmentAddress: String? = null,
    val establishmentBusinessName: String? = null,
)
