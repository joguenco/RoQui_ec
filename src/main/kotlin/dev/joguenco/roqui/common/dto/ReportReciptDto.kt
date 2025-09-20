package dev.joguenco.roqui.common.dto

import java.math.BigDecimal
import java.util.Date

data class ReportReciptDto(
    val id: Long? = null,
    val code: String? = null,
    val number: String? = null,
    val accessKey: String? = null,
    val date: Date? = null,
    val total: BigDecimal? = null,
    val identification: String? = null,
    val legalName: String? = null,
    val email: String? = null,
    val status: String? = null,
)
