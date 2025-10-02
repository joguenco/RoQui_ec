package dev.joguenco.roqui.email.controller

import dev.joguenco.roqui.electronic.dto.DocumentDto
import dev.joguenco.roqui.email.EmailSmtp
import dev.joguenco.roqui.information.service.InformationService
import dev.joguenco.roqui.parameter.service.ParameterService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/roqui/v1")
class EmailController {

    @Autowired lateinit var parameterService: ParameterService
    @Autowired lateinit var informationService: InformationService

    @PostMapping("/email/send")
    fun postSendEmail(@RequestBody document: DocumentDto): ResponseEntity<Any> {
        val emailSmtp =
            EmailSmtp(document.code, document.number, parameterService, informationService)
        emailSmtp.send()
        return ResponseEntity.ok().build()
    }
}
