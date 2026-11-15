package dev.joguenco.roqui.electronic.controller

import dev.joguenco.roqui.electronic.service.DocumentService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/roqui/v1")
class DocumentController {

    @Autowired lateinit var documentService: DocumentService

    @GetMapping("/document/code/{code}/number/{number}")
    fun getDocument(
        @PathVariable(value = "code") code: String,
        @PathVariable(value = "number") number: String,
    ): ResponseEntity<Any> {

        if (code.isEmpty() || number.isEmpty()) {
            return ResponseEntity.badRequest().body("The code or number is empty")
        }

        val document = documentService.getByCodeAndNumber(code, number)

        if (document == null) {
            return ResponseEntity.notFound().build()
        }

        return ResponseEntity.ok(document)
    }
}
