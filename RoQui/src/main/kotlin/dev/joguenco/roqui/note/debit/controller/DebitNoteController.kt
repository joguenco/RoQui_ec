package dev.joguenco.roqui.note.debit.controller

import dev.joguenco.roqui.electronic.ElectronicDocument
import dev.joguenco.roqui.electronic.TypeDocument
import dev.joguenco.roqui.electronic.dto.DocumentDto
import dev.joguenco.roqui.electronic.dto.StatusDto
import dev.joguenco.roqui.electronic.send.WebService
import dev.joguenco.roqui.electronic.service.DocumentService
import dev.joguenco.roqui.information.service.InformationService
import dev.joguenco.roqui.note.debit.service.DebitNoteService
import dev.joguenco.roqui.note.debit.service.ReportDebitNoteService
import dev.joguenco.roqui.parameter.service.ParameterService
import dev.joguenco.roqui.shared.dto.Message
import dev.joguenco.roqui.util.Validate
import java.util.concurrent.TimeUnit
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/roqui/v1")
class DebitNoteController {

    @Autowired lateinit var debitNoteService: DebitNoteService

    @Autowired lateinit var parameterService: ParameterService

    @Autowired lateinit var documentService: DocumentService

    @Autowired lateinit var webService: WebService

    @Autowired lateinit var reportDebitNoteService: ReportDebitNoteService

    @Autowired lateinit var informationService: InformationService

    @PostMapping("/debit/note/authorize")
    fun postAuthorize(@RequestBody document: DocumentDto): ResponseEntity<Any> {

        if (debitNoteService.count(document.code, document.number) == 0L) {
            return ResponseEntity.notFound().build()
        }

        val buildDebitNote =
            ElectronicDocument(
                document.code,
                document.number,
                debitNoteService,
                webService,
                parameterService,
                documentService,
            )

        try {
            val stateSend = StatusDto(buildDebitNote.process(TypeDocument.NOTA_DEBITO))
            TimeUnit.MILLISECONDS.sleep(2700)

            val stateCheck = StatusDto(buildDebitNote.check(informationService))
            if (stateCheck.status.isEmpty()) {
                return ResponseEntity.ok(stateSend)
            }

            return ResponseEntity.ok(stateCheck)
        } catch (e: Exception) {
            println("Error Authorize Debit Note ${e.message}")
            return ResponseEntity.badRequest().body(Message(e.message!!))
        }
    }

    @PostMapping("/debit/note/authorize/dates/{startDate}/{endDate}")
    fun postAuthorizeAll(
        @PathVariable(value = "startDate") startDate: String,
        @PathVariable(value = "endDate") endDate: String,
    ): ResponseEntity<out Any?> {

        val (status, message) = Validate.rangeOfDates(startDate, endDate)
        if (!status) {
            return ResponseEntity.badRequest().body(message)
        }

        val reportDebitNote =
            reportDebitNoteService.getDebitNoteByDatesAndStatus(startDate, endDate, "Unauthorized")

        for (debitNote in reportDebitNote) {
            val buildDebitNote =
                ElectronicDocument(
                    debitNote.code!!,
                    debitNote.number!!,
                    debitNoteService,
                    webService,
                    parameterService,
                    documentService,
                )

            try {
                StatusDto(buildDebitNote.process(TypeDocument.NOTA_DEBITO))
            } catch (e: Exception) {
                println("Error AuthorizeAll Debit Note ${e.message}")
                return ResponseEntity.badRequest().body(Message(e.message!!))
            }
        }

        TimeUnit.MILLISECONDS.sleep(2700)
        return checkAll(startDate, endDate)
    }

    @PostMapping("/debit/note/check/dates/{startDate}/{endDate}")
    fun postCheckAll(
        @PathVariable(value = "startDate") startDate: String,
        @PathVariable(value = "endDate") endDate: String,
    ): ResponseEntity<out Any?> {

        val (status, message) = Validate.rangeOfDates(startDate, endDate)
        if (!status) {
            return ResponseEntity.badRequest().body(message)
        }

        return checkAll(startDate, endDate)
    }

    fun checkAll(startDate: String, endDate: String): ResponseEntity<out Any?> {
        val reportDebitNote =
            reportDebitNoteService.getDebitNoteByDatesAndStatus(startDate, endDate, "Unauthorized")

        for (debitNote in reportDebitNote) {
            val buildDebitNote =
                ElectronicDocument(
                    debitNote.code!!,
                    debitNote.number!!,
                    debitNoteService,
                    webService,
                    parameterService,
                    documentService,
                )
            buildDebitNote.setAccessKey(debitNote.accessKey!!)

            try {
                StatusDto(buildDebitNote.check(informationService))
            } catch (e: Exception) {
                println("Error checkAll Debit Note ${e.message}")
                return ResponseEntity.badRequest().body(Message(e.message!!))
            }
        }

        return ResponseEntity.ok().body(Message("Completed successfully"))
    }
}
