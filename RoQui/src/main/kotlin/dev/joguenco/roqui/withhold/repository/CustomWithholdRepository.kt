package dev.joguenco.roqui.withhold.repository

import dev.joguenco.roqui.withhold.model.Withhold
import dev.joguenco.roqui.withhold.model.WithholdDetail
import dev.joguenco.roqui.withhold.model.WithholdDocumentTax

interface CustomWithholdRepository {

    fun countByCodeAndNumber(code: String, number: String): Long

    fun findByCodeAndNumber(code: String, number: String): Withhold

    fun findDetailByCodeAndNumber(code: String, number: String): MutableList<WithholdDetail>

    fun findDocumentTaxByCodeAndNumber(
        code: String,
        number: String,
    ): MutableList<WithholdDocumentTax>
}
