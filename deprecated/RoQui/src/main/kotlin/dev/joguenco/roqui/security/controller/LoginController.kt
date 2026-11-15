package dev.joguenco.roqui.security.controller

import dev.joguenco.roqui.security.dto.AuthTokensDto
import dev.joguenco.roqui.security.dto.RefreshTokenDto
import dev.joguenco.roqui.security.dto.UserDto
import dev.joguenco.roqui.security.service.AuthenticationService
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/roqui/v1")
class LoginController {

    @Autowired lateinit var authenticationService: AuthenticationService

    @PostMapping("/login")
    fun login(@RequestBody loginRequest: @Valid UserDto): AuthTokensDto {
        return authenticationService.authentication(loginRequest)
    }

    @PostMapping("/refresh")
    fun refreshToken(@RequestBody refreshToken: RefreshTokenDto): AuthTokensDto {
        val value = authenticationService.refreshAccessToken(refreshToken.token)
        return if (value != null) {
            AuthTokensDto(accessToken = value, refreshToken = refreshToken.token)
        } else {
            throw IllegalArgumentException("Invalid refresh token")
        }
    }

    @PostMapping("/logout")
    fun logout(): ResponseEntity<Void> {
        TODO("Implement logout logic if needed (e.g., invalidate tokens)")
    }
}
