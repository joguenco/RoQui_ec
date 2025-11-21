package dev.joguenco.roqui.note.credit.controller

import dev.joguenco.roqui.electronic.ElectronicDocument
import dev.joguenco.roqui.electronic.TypeDocument
import dev.joguenco.roqui.electronic.dto.DocumentDto
import dev.joguenco.roqui.electronic.dto.StatusDto
import dev.joguenco.roqui.electronic.send.WebService
import dev.joguenco.roqui.electronic.service.DocumentService
import dev.joguenco.roqui.information.service.InformationService
import dev.joguenco.roqui.note.credit.service.CreditNoteService
import dev.joguenco.roqui.note.credit.service.ReportCreditNoteService
import dev.joguenco.roqui.parameter.service.ParameterService
import dev.joguenco.roqui.shared.dto.Message
import java.util.concurrent.TimeUnit
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/roqui/v1")
class CreditNoteController {

    @Autowired lateinit var creditNoteService: CreditNoteService

    @Autowired lateinit var parameterService: ParameterService

    @Autowired lateinit var documentService: DocumentService

    @Autowired lateinit var webService: WebService

    @Autowired lateinit var reportCreditNoteService: ReportCreditNoteService

    @Autowired lateinit var informationService: InformationService

    @PostMapping("/credit/note/authorize")
    fun postAuthorize(@RequestBody document: DocumentDto): ResponseEntity<Any> {

        if (creditNoteService.count(document.code, document.number) == 0L) {
            return ResponseEntity.notFound().build()
        }

        val buildCreditNote =
            ElectronicDocument(
                document.code,
                document.number,
                creditNoteService,
                webService,
                parameterService,
                documentService,
            )

        try {
            val stateSend = StatusDto(buildCreditNote.process(TypeDocument.NOTA_CREDITO))
            TimeUnit.SECONDS.sleep(3)

            val stateCheck = StatusDto(buildCreditNote.check(informationService))
            if (stateCheck.status.isEmpty()) {
                return ResponseEntity.ok(stateSend)
            }

            return ResponseEntity.ok(stateCheck)
        } catch (e: Exception) {
            println("Error Authorize ${e.message}")
            return ResponseEntity.badRequest().body(Message(e.message!!))
        }
    }
}
