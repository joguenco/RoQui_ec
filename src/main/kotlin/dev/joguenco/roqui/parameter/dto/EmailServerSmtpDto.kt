package dev.joguenco.roqui.parameter.dto

import com.fasterxml.jackson.annotation.JsonProperty

enum class EmailEncryption {
    @JsonProperty("None") NONE,
    @JsonProperty("SSL/TLS") SSL_TLS,
}

data class EmailServerSmtpDto(
    val url: String,
    val port: Int,
    val account: String,
    val password: String,
    val encryption: EmailEncryption,
)
