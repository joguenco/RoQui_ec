package dev.joguenco.roqui.taxpayer.dto

data class TaxpayerDto(
    var id: Int? = null,
    var identification: String? = null,
    var legalName: String? = null,
    var forcedAccounting: String? = null,
    var specialTaxpayer: String? = null,
    var retentionAgent: String? = null,
    var rimpe: String? = null
)
