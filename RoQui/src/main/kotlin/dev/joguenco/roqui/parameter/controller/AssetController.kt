package dev.joguenco.roqui.parameter.controller

import dev.joguenco.roqui.parameter.dto.AssetDto
import dev.joguenco.roqui.parameter.service.AssetService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/roqui/v1")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
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
