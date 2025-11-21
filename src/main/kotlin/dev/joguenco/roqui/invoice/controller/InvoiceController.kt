package dev.joguenco.roqui.invoice.controller

import dev.joguenco.roqui.electronic.ElectronicDocument
import dev.joguenco.roqui.electronic.TypeDocument
import dev.joguenco.roqui.electronic.dto.DocumentDto
import dev.joguenco.roqui.electronic.dto.StatusDto
import dev.joguenco.roqui.electronic.send.WebService
import dev.joguenco.roqui.electronic.service.DocumentService
import dev.joguenco.roqui.information.service.InformationService
import dev.joguenco.roqui.invoice.service.InvoiceService
import dev.joguenco.roqui.invoice.service.ReportInvoiceService
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
class InvoiceController {

    @Autowired lateinit var invoiceService: InvoiceService

    @Autowired lateinit var parameterService: ParameterService

    @Autowired lateinit var documentService: DocumentService

    @Autowired lateinit var webService: WebService

    @Autowired lateinit var reportInvoiceService: ReportInvoiceService

    @Autowired lateinit var informationService: InformationService

    @PostMapping("/invoice/authorize")
    fun postAuthorize(@RequestBody document: DocumentDto): ResponseEntity<Any> {

        if (invoiceService.count(document.code, document.number) == 0L) {
            return ResponseEntity.notFound().build()
        }

        val buildInvoice =
            ElectronicDocument(
                document.code,
                document.number,
                invoiceService,
                webService,
                parameterService,
                documentService,
            )

        try {
            val stateSend = StatusDto(buildInvoice.process(TypeDocument.FACTURA))
            TimeUnit.MILLISECONDS.sleep(2700)

            val stateCheck = StatusDto(buildInvoice.check(informationService))
            if (stateCheck.status.isEmpty()) {
                return ResponseEntity.ok(stateSend)
            }

            return ResponseEntity.ok(stateCheck)
        } catch (e: Exception) {
            println("Error Authorize ${e.message}")
            return ResponseEntity.badRequest().body(Message(e.message!!))
        }
    }

    @PostMapping("/invoice/authorize/dates/{startDate}/{endDate}")
    fun postAuthorizeAll(
        @PathVariable(value = "startDate") startDate: String,
        @PathVariable(value = "endDate") endDate: String,
    ): ResponseEntity<out Any?> {

        val (status, message) = Validate.rangeOfDates(startDate, endDate)
        if (!status) {
            return ResponseEntity.badRequest().body(message)
        }

        val reportInvoice =
            reportInvoiceService.getInvoiceByDatesAndStatus(startDate, endDate, "Unauthorized")

        for (invoice in reportInvoice) {
            val buildInvoice =
                ElectronicDocument(
                    invoice.code!!,
                    invoice.number!!,
                    invoiceService,
                    webService,
                    parameterService,
                    documentService,
                )

            try {
                StatusDto(buildInvoice.process(TypeDocument.FACTURA))
            } catch (e: Exception) {
                println("Error AuthorizeAll ${e.message}")
                return ResponseEntity.badRequest().body(Message(e.message!!))
            }
        }

        TimeUnit.MILLISECONDS.sleep(2700)
        return checkAll(startDate, endDate)
    }

    @PostMapping("/invoice/check/dates/{startDate}/{endDate}")
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
        val reportInvoice =
            reportInvoiceService.getInvoiceByDatesAndStatus(startDate, endDate, "Unauthorized")

        for (invoice in reportInvoice) {
            val buildInvoice =
                ElectronicDocument(
                    invoice.code!!,
                    invoice.number!!,
                    invoiceService,
                    webService,
                    parameterService,
                    documentService,
                )
            buildInvoice.setAccessKey(invoice.accessKey!!)

            try {
                StatusDto(buildInvoice.check(informationService))
            } catch (e: Exception) {
                println("Error checkAll ${e.message}")
                return ResponseEntity.badRequest().body(Message(e.message!!))
            }
        }

        return ResponseEntity.ok().body(Message("Completed successfully"))
    }
}
