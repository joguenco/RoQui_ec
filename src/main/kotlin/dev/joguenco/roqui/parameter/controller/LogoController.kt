package dev.joguenco.roqui.parameter.controller

import dev.joguenco.roqui.exception.FileImportException
import dev.joguenco.roqui.parameter.service.LogoService
import dev.joguenco.roqui.shared.dto.Message
import java.nio.file.Path
import java.nio.file.Paths
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.http.HttpHeaders
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
class LogoController {
    @Autowired lateinit var logoService: LogoService

    @GetMapping("/parameter/logo")
    fun getLogo(): ResponseEntity<Resource> {
        return try {
            val logoPath: String = logoService.getLogoPath()

            val path: Path = Paths.get(logoPath)
            val resource: Resource = UrlResource(path.toUri())

            if (resource.exists() || resource.isReadable) {
                ResponseEntity.ok()
                    .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"${path.fileName}\"",
                    )
                    .header(HttpHeaders.CONTENT_TYPE, "image/png")
                    .body(resource)
            } else {
                println("Error al obtener la imagen")
                return ResponseEntity.notFound().build()
            }
        } catch (e: Exception) {
            println("Error al obtener la imagen: ${e.message}")
            return ResponseEntity.badRequest().build()
        }
    }

    @PostMapping("/parameter/logo/load")
    fun loadCertificate(@RequestParam("file") file: MultipartFile): ResponseEntity<Any> {
        return try {
            val status = logoService.uploadLogo(file)

            if (status) {
                ResponseEntity.ok(Message("The file has been updated successfully"))
            } else ResponseEntity.ok(Message("Failed to update the file"))
        } catch (ex: FileImportException) {
            ResponseEntity(Message(ex.message.toString()), HttpStatus.BAD_REQUEST)
        }
            as ResponseEntity<Any>
    }
}
