package dev.joguenco.roqui.ping.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/roqui/v1")
class PingController {

    @GetMapping("/ping") fun ping() = Pong()

    class Pong {
        val message = "pong"
    }
}
