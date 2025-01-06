package dev.joguenco.roqui.certificate.controller

import dev.joguenco.roqui.certificate.service.CertificateService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/roqui/v1")
class CertificateController {

    @Autowired
    lateinit var certificateService: CertificateService

    @GetMapping("/certificate")
    fun getCertificate() : ResponseEntity<Any> {
        val certificate = certificateService.getCertificateInformation()

        return ResponseEntity.ok(certificate)
    }
}