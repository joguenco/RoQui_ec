package dev.joguenco.roqui

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication class RoquiApplication

fun main(args: Array<String>) {
    runApplication<RoquiApplication>(*args)
}
