package dev.joguenco.roqui.security.dto

import jakarta.validation.constraints.NotBlank

data class UserDto(val username: @NotBlank String? = null, val password: String? = null)
