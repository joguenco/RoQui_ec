package dev.joguenco.roqui.security.util

import dev.joguenco.roqui.parameter.service.ParameterService
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

fun isValidApiKey(requestApiKey: String?, parameterService: ParameterService): Boolean {
    val apiKey = parameterService.getApiKey()

    return !(requestApiKey.isNullOrBlank() || apiKey.isBlank()) &&
        MessageDigest.isEqual(
            requestApiKey.toByteArray(StandardCharsets.UTF_8),
            apiKey.toByteArray(StandardCharsets.UTF_8),
        )
}
