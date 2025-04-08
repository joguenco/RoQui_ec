package dev.joguenco.roqui.security.controller

import dev.joguenco.roqui.security.dto.LoginDto
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
    fun login(@RequestBody loginRequest: @Valid UserDto): LoginDto {
        return authenticationService.authentication(loginRequest)
    }

    @PostMapping("/logout")
    fun logout(): ResponseEntity<Void> {
        TODO()
    }
}
