package dev.joguenco.roqui.liquidation.repository

import dev.joguenco.roqui.liquidation.model.Liquidation
import dev.joguenco.roqui.liquidation.model.LiquidationDetail
import dev.joguenco.roqui.liquidation.model.LiquidationTax

interface CustomLiquidationRepository {

    fun countByCodeAndNumber(code: String, number: String): Long

    fun findByCodeAndNumber(code: String, number: String): Liquidation

    fun findDetailByCodeAndNumber(code: String, number: String): MutableList<LiquidationDetail>

    fun findDetailTax(
        code: String,
        number: String,
        principalCode: String,
        line: Long,
    ): MutableList<LiquidationTax>

    // La liquidacion de compra no lleva formas de pago desde DonPos:
    // la tabla purchases no guarda pagos, y el XSD marca <pagos> como opcional.
    fun findTotalTaxByCodeAndNumber(code: String, number: String): MutableList<LiquidationTax>
}
