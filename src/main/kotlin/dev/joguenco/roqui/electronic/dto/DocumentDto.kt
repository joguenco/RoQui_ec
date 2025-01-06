package dev.joguenco.roqui.electronic.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class DocumentDto(
    @JsonProperty("code")
    val code: String,
    @JsonProperty("number")
    val number: String
)
