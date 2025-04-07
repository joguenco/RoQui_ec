package dev.joguenco.roqui.certificate.dto

import java.util.*

data class CertificateDto(
    val ownerCertificate: String,
    val issuerCertificate: String,
    val dateExpiry: Date? = null,
    val dateIssued: Date? = null,
    val daysToExpiry: Int,
    val serialNumber: String,
)
