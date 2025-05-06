package dev.joguenco.roqui.certificate.controller

import dev.joguenco.roqui.certificate.service.CertificateService
import dev.joguenco.roqui.exception.FileImportException
import dev.joguenco.roqui.shared.dto.Message
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/roqui/v1")
class CertificateController {

    @Autowired lateinit var certificateService: CertificateService

    @GetMapping("/certificate")
    fun getCertificate(): ResponseEntity<Any> {
        if (!certificateService.checkCertificateFile()) {
            return ResponseEntity(Message("Certificate file not found"), HttpStatus.NOT_FOUND)
        }

        val certificate = certificateService.getCertificateInformation()
        return ResponseEntity.ok(certificate)
    }

    @PostMapping("/certificate/load")
    fun loadCertificate(
        @RequestParam("file") file: MultipartFile,
        @RequestParam("password") password: String,
    ): ResponseEntity<Any> {
        val message = Message()
        return try {
            val status = certificateService.uploadFile(file, password)

            if (status) message.message = "The certificate has been updated successfully"
            else message.message = "Failed to update the certificate"

            ResponseEntity(message, HttpStatus.OK)
        } catch (ex: FileImportException) {
            message.message = ex.message.toString()
            ResponseEntity(message, HttpStatus.BAD_REQUEST)
        }
    }
}
