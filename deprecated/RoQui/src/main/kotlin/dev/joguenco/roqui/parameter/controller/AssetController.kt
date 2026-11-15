package dev.joguenco.roqui.parameter.controller

import dev.joguenco.roqui.parameter.dto.AssetDto
import dev.joguenco.roqui.parameter.service.AssetService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/roqui/v1")
class AssetController {
    @Autowired lateinit var assetService: AssetService

    @GetMapping("/asset/environment")
    fun getEnvironment(): ResponseEntity<AssetDto> {
        val environment = assetService.findByName("Electronic.Environment")

        val environmentName =
            when (environment.value) {
                "1" -> "Pruebas"
                "2" -> "Producción"
                else -> "Indefinido"
            }

        return ResponseEntity.ok(AssetDto(environment.name, environmentName))
    }
}
