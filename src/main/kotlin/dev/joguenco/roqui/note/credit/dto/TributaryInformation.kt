package dev.joguenco.roqui.note.credit.dto

import dev.joguenco.roqui.note.credit.model.CreditNote
import dev.joguenco.roqui.taxpayer.model.Taxpayer

data class TributaryInformation(
    val creditNote: CreditNote,
    val taxpayer: Taxpayer,
    val establishmentAddress: String? = null,
    val principalEstablishmentAddress: String? = null,
    val establishmentBusinessName: String? = null,
)
