package dev.joguenco.roqui.invoice.dto

import java.math.BigDecimal

data class TaxTotal(
    val taxCode: String,
    val percentageCode: String,
    val taxIva: BigDecimal,
    val taxBase: BigDecimal,
    val value: BigDecimal,
)
