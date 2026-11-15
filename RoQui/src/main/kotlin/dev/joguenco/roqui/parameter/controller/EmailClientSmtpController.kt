package dev.joguenco.roqui.parameter.controller

import dev.joguenco.roqui.common.dto.Message
import dev.joguenco.roqui.parameter.dto.EmailServerSmtpDto
import dev.joguenco.roqui.parameter.service.EmailClientSmtpService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/roqui/v1")
@PreAuthorize("hasAnyRole('ADMIN')")
class EmailClientSmtpController {

    private val log = LoggerFactory.getLogger(EmailClientSmtpController::class.java)

    @Autowired lateinit var emailClientSmtpService: EmailClientSmtpService

    @GetMapping("/email/client/smtp")
    fun getEmailServerConfiguration(): ResponseEntity<Any> {
        return ResponseEntity.ok(emailClientSmtpService.getEmailServerConfiguration())
    }

    @PostMapping("/email/client/smtp/update")
    fun upadeteEmailServerConfiguration(
        @RequestBody emailServerDto: EmailServerSmtpDto
    ): ResponseEntity<Any> {
        val message = Message()

        return try {
            val status = emailClientSmtpService.update(emailServerDto)

            if (status) message.message = "The configuration has been updated successfully"
            else message.message = "Failed to update the configuration"

            ResponseEntity(message, HttpStatus.OK)
        } catch (ex: Exception) {
            log.error("Error updating the email configuration: ${ex.message}")
            message.message = ex.message.toString()
            ResponseEntity(message, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}
