package dev.joguenco.roqui.parameter.controller

import dev.joguenco.roqui.parameter.dto.BaseDirectoryDto
import dev.joguenco.roqui.parameter.service.ParameterService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/roqui/v1")
class ParameterController {

    @Autowired lateinit var parameterService: ParameterService

    @GetMapping("/parameter/base/directory")
    fun getBaseDirectory(): ResponseEntity<Any> {
        val baseDirectory = parameterService.getBaseDirectory()

        if (baseDirectory.isEmpty()) {
            return ResponseEntity.noContent().build()
        }

        return ResponseEntity.ok(BaseDirectoryDto(baseDirectory))
    }
}
