package dev.joguenco.roqui.invoice.dto

import java.math.BigDecimal

data class TaxTotal(
    var taxCode: String? = null,
    var percentageCode: String? = null,
    var taxIva: BigDecimal? = null,
    var taxBase: BigDecimal? = null,
    var value: BigDecimal? = null,
)
