package dev.joguenco.roqui.taxpayer.dto

data class TaxpayerDto(
    val id: Int? = null,
    val identification: String? = null,
    val legalName: String? = null,
    val forcedAccounting: String? = null,
    val specialTaxpayer: String? = null,
    val retentionAgent: String? = null,
    val rimpe: String? = null
)
