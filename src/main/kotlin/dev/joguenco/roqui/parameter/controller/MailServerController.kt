package dev.joguenco.roqui.parameter.controller

import dev.joguenco.roqui.parameter.service.MailServerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/roqui/v1")
class MailServerController {

    @Autowired lateinit var mailServerService: MailServerService

    @GetMapping("/mail/server")
    fun getMailServerInformation(): ResponseEntity<Any> {
        return ResponseEntity.ok(mailServerService.getMailServerInformation())
    }
}
