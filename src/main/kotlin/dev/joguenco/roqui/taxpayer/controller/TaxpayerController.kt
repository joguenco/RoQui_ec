package dev.joguenco.roqui.taxpayer.controller

import dev.joguenco.roqui.taxpayer.service.TaxpayerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/roqui/v1")
class TaxpayerController {

    @Autowired
    lateinit var taxpayerService: TaxpayerService

    @GetMapping("/taxpayer")
    fun getTaxpayer() : ResponseEntity<Any> {
        val taxpayer = taxpayerService.getTaxpayer()

        if (taxpayer.id == null) {
            return ResponseEntity.noContent().build()
        }

        return ResponseEntity.ok(taxpayer)
    }
}