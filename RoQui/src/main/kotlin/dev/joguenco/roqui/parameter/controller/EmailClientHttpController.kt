package dev.joguenco.roqui.parameter.controller

import dev.joguenco.roqui.parameter.dto.EmailServerHttpDto
import dev.joguenco.roqui.parameter.service.EmailClientHttpService
import dev.joguenco.roqui.shared.dto.Message
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/roqui/v1")
class EmailClientHttpController {

    @Autowired lateinit var emailClientHttpService: EmailClientHttpService

    @GetMapping("/email/client/http")
    fun getEmailServerConfiguration(): ResponseEntity<Any> {
        return ResponseEntity.ok(emailClientHttpService.getEmailServerConfiguration())
    }

    @PostMapping("/email/client/http/update")
    fun upadeteEmailServerConfiguration(
        @RequestBody emailServerDto: EmailServerHttpDto
    ): ResponseEntity<Any> {
        val message = Message()

        return try {
            val status = emailClientHttpService.update(emailServerDto)

            if (status) message.message = "The configuration has been updated successfully"
            else message.message = "Failed to update the configuration"

            ResponseEntity(message, HttpStatus.OK)
        } catch (ex: Exception) {
            message.message = ex.message.toString()
            ResponseEntity(message, HttpStatus.BAD_REQUEST)
        }
    }
}
