package dev.joguenco.roqui.util.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class DocumentDto(
    @JsonProperty("code")
    val code: String,
    @JsonProperty("number")
    val number: String
)
