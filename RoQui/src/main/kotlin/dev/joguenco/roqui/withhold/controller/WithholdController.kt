package dev.joguenco.roqui.withhold.controller

import dev.joguenco.roqui.electronic.ElectronicDocument
import dev.joguenco.roqui.electronic.TypeDocument
import dev.joguenco.roqui.electronic.dto.DocumentDto
import dev.joguenco.roqui.electronic.dto.StatusDto
import dev.joguenco.roqui.electronic.send.WebService
import dev.joguenco.roqui.electronic.service.DocumentService
import dev.joguenco.roqui.information.service.InformationService
import dev.joguenco.roqui.parameter.service.ParameterService
import dev.joguenco.roqui.shared.dto.Message
import dev.joguenco.roqui.util.Validate
import dev.joguenco.roqui.withhold.service.ReportWithholdService
import dev.joguenco.roqui.withhold.service.WithholdService
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
class WithholdController {

    @Autowired lateinit var withholdService: WithholdService

    @Autowired lateinit var parameterService: ParameterService

    @Autowired lateinit var documentService: DocumentService

    @Autowired lateinit var webService: WebService

    @Autowired lateinit var reportWithholdService: ReportWithholdService

    @Autowired lateinit var informationService: InformationService

    @PostMapping("/withhold/authorize")
    fun postAuthorize(@RequestBody document: DocumentDto): ResponseEntity<Any> {

        if (withholdService.count(document.code, document.number) == 0L) {
            return ResponseEntity.notFound().build()
        }

        val buildWithhold =
            ElectronicDocument(
                document.code,
                document.number,
                withholdService,
                webService,
                parameterService,
                documentService,
            )

        try {
            val stateSend = StatusDto(buildWithhold.process(TypeDocument.RETENCION))
            TimeUnit.MILLISECONDS.sleep(2700)

            val stateCheck = StatusDto(buildWithhold.check(informationService))
            if (stateCheck.status.isEmpty()) {
                return ResponseEntity.ok(stateSend)
            }

            return ResponseEntity.ok(stateCheck)
        } catch (e: Exception) {
            println("Error Authorize Withhold ${e.message}")
            return ResponseEntity.badRequest().body(Message(e.message!!))
        }
    }

    @PostMapping("/withhold/authorize/dates/{startDate}/{endDate}")
    fun postAuthorizeAll(
        @PathVariable(value = "startDate") startDate: String,
        @PathVariable(value = "endDate") endDate: String,
    ): ResponseEntity<out Any?> {

        val (status, message) = Validate.rangeOfDates(startDate, endDate)
        if (!status) {
            return ResponseEntity.badRequest().body(message)
        }

        val reportWithhold =
            reportWithholdService.getWithholdByDatesAndStatus(startDate, endDate, "Unauthorized")

        for (withhold in reportWithhold) {
            val buildWithhold =
                ElectronicDocument(
                    withhold.code!!,
                    withhold.number!!,
                    withholdService,
                    webService,
                    parameterService,
                    documentService,
                )

            try {
                StatusDto(buildWithhold.process(TypeDocument.RETENCION))
            } catch (e: Exception) {
                println("Error AuthorizeAll Withhold ${e.message}")
                return ResponseEntity.badRequest().body(Message(e.message!!))
            }
        }

        TimeUnit.MILLISECONDS.sleep(2700)
        return checkAll(startDate, endDate)
    }

    @PostMapping("/withhold/check/dates/{startDate}/{endDate}")
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
        val reportWithhold =
            reportWithholdService.getWithholdByDatesAndStatus(startDate, endDate, "Unauthorized")

        for (withhold in reportWithhold) {
            val buildWithhold =
                ElectronicDocument(
                    withhold.code!!,
                    withhold.number!!,
                    withholdService,
                    webService,
                    parameterService,
                    documentService,
                )
            buildWithhold.setAccessKey(withhold.accessKey!!)

            try {
                StatusDto(buildWithhold.check(informationService))
            } catch (e: Exception) {
                println("Error checkAll Withhold ${e.message}")
                return ResponseEntity.badRequest().body(Message(e.message!!))
            }
        }

        return ResponseEntity.ok().body(Message("Completed successfully"))
    }
}
