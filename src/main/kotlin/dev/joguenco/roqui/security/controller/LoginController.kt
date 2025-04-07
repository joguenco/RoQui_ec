package dev.joguenco.roqui.security.controller

import dev.joguenco.roqui.security.dto.UserDto
import dev.joguenco.roqui.security.service.AuthenticationService
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/roqui/v1")
class LoginController {

    @Autowired lateinit var authenticationService: AuthenticationService

    @PostMapping("/login")
    fun login(@RequestBody loginRequest: @Valid UserDto): AuthenticationResponse {
        return authenticationService.authentication(loginRequest)
    }

    @PostMapping("/logout")
    fun logout(): ResponseEntity<Void> {
        TODO()
    }
}
