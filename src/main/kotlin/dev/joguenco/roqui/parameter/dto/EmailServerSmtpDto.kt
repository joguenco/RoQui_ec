package dev.joguenco.roqui.parameter.dto

data class EmailServerSmtpDto(
    val url: String,
    val port: Int,
    val account: String,
    val password: String,
)
