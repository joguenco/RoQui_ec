package dev.joguenco.roqui.security.controller

import dev.joguenco.roqui.security.dto.UserDto
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/roqui/v1")
class LoginController {

    @PostMapping("/login")
    fun login(@RequestBody loginRequest: @Valid UserDto?): ResponseEntity<Any> {
        return ResponseEntity.ok().build()
    }

    @PostMapping("/logout")
    fun logout(): ResponseEntity<Void> {
        TODO()
    }
}