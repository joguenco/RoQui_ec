package dev.joguenco.roqui.note.delivery.dto

import java.util.Date

/** La guia no lleva total ni correo: va el transportista con su placa. */
data class ReportDeliveryNoteDto(
    val id: Long? = null,
    val code: String? = null,
    val number: String? = null,
    val accessKey: String? = null,
    val date: Date? = null,
    val identification: String? = null,
    val legalName: String? = null,
    val plate: String? = null,
    val status: String? = null,
)
