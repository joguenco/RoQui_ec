package dev.joguenco.roqui.invoice.dto

import dev.joguenco.roqui.invoice.model.Invoice
import dev.joguenco.roqui.taxpayer.model.Taxpayer

data class TributaryInformation(
    val invoice: Invoice,
    val taxpayer: Taxpayer,
    val establishmentAddress: String? = null,
    val principalEstablishmentAddress: String? = null,
    val establishmentBusinessName: String? = null
)