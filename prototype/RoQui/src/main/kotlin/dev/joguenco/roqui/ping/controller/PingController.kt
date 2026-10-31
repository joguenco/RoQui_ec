package dev.joguenco.roqui.ping.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class PingController {

    @GetMapping("/ping") fun ping() = Pong()

    class Pong {
        val message = "pong"
    }
}
