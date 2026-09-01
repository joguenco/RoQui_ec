package dev.joguenco.roqui.note.delivery.dto

import dev.joguenco.roqui.note.delivery.model.DeliveryNote
import dev.joguenco.roqui.taxpayer.model.Taxpayer

data class TributaryInformation(
    val deliveryNote: DeliveryNote,
    val taxpayer: Taxpayer,
    val establishmentAddress: String? = null,
    val principalEstablishmentAddress: String? = null,
    val establishmentBusinessName: String? = null,
)
