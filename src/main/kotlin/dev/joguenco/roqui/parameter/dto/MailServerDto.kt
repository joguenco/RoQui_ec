package dev.joguenco.roqui.parameter.dto

data class MailServerDto(
    val server: String,
    val port: Int,
    val email: String,
    val password: String,
    val template: String,
)
