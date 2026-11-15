package dev.joguenco.roqui.note.debit.dto

import dev.joguenco.roqui.note.debit.model.DebitNote
import dev.joguenco.roqui.taxpayer.model.Taxpayer

data class TributaryInformation(
    val debitNote: DebitNote,
    val taxpayer: Taxpayer,
    val establishmentAddress: String? = null,
    val principalEstablishmentAddress: String? = null,
    val establishmentBusinessName: String? = null,
)
