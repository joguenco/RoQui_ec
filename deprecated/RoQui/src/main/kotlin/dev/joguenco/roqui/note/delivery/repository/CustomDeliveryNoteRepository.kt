package dev.joguenco.roqui.note.delivery.repository

import dev.joguenco.roqui.note.delivery.model.DeliveryNote
import dev.joguenco.roqui.note.delivery.model.DeliveryNoteDetail
import dev.joguenco.roqui.note.delivery.model.DeliveryNoteReceiver

interface CustomDeliveryNoteRepository {

    fun countByCodeAndNumber(code: String, number: String): Long

    fun findByCodeAndNumber(code: String, number: String): DeliveryNote

    fun findReceiverByCodeAndNumber(code: String, number: String): MutableList<DeliveryNoteReceiver>

    fun findDetailByCodeAndNumberAndLine(
        code: String,
        number: String,
        line: Long,
    ): MutableList<DeliveryNoteDetail>
}
