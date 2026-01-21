package dev.joguenco.roqui.subscription.controller

import dev.joguenco.roqui.parameter.service.ParameterService
import dev.joguenco.roqui.subscription.SubscriptionManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/roqui/v1")
class SubscriptionController {

    @Autowired
    lateinit var parameterService: ParameterService

    @Value("\${key.user}")
    lateinit var keyUser: String

    @Value("\${key.password}")
    lateinit var keyPassword: String

    @GetMapping("/subscription")
    fun getSuscripcion(): ResponseEntity<Any> {

        return ResponseEntity(SubscriptionManager(parameterService).status(), HttpStatus.OK)
    }
}