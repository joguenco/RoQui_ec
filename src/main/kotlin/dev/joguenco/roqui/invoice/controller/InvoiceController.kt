package dev.joguenco.roqui.invoice.controller

import dev.joguenco.roqui.electronic.ElectronicDocument
import dev.joguenco.roqui.electronic.TypeDocument
import dev.joguenco.roqui.electronic.dto.DocumentDto
import dev.joguenco.roqui.electronic.dto.StatusDto
import dev.joguenco.roqui.electronic.send.WebService
import dev.joguenco.roqui.electronic.service.DocumentService
import dev.joguenco.roqui.invoice.dto.ReportInvoiceDto
import dev.joguenco.roqui.invoice.service.InvoiceService
import dev.joguenco.roqui.invoice.service.ReportInvoiceService
import dev.joguenco.roqui.parameter.service.ParameterService
import java.util.concurrent.TimeUnit
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
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
            TimeUnit.SECONDS.sleep(3)

            val stateCheck = StatusDto(buildInvoice.check())
            if (stateCheck.status.isEmpty()) {
                return ResponseEntity.ok(stateSend)
            }

            return ResponseEntity.ok(stateCheck)
        } catch (e: Exception) {
            println("Error postAuthorize ${e.message}")
            return ResponseEntity.badRequest().body(e.message)
        }
    }

    @GetMapping("/invoice/authorize/dates/{startDate}/{endDate}")
    fun getAuthorizeAll(
        @PathVariable(value = "startDate") startDate: String,
        @PathVariable(value = "endDate") endDate: String,
    ): ResponseEntity<out Any?> {

        var reportInvoice =
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
                println("Error getAuthorizeAll ${e.message}")
                return ResponseEntity.badRequest().body(e.message)
            }
        }

        reportInvoice.clear()
        reportInvoice = reportInvoiceService.getInvoiceByDatesAndStatus(startDate, endDate)

        return ResponseEntity<MutableList<ReportInvoiceDto>>(reportInvoice, HttpStatus.OK)
    }

    @GetMapping("/invoice/check/dates/{startDate}/{endDate}")
    fun getCheckAll(
        @PathVariable(value = "startDate") startDate: String,
        @PathVariable(value = "endDate") endDate: String,
    ): ResponseEntity<out Any?> {

        var reportInvoice =
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
                StatusDto(buildInvoice.check())
            } catch (e: Exception) {
                println("Error getCheckAll ${e.message}")
                return ResponseEntity.badRequest().body(e.message)
            }
        }

        reportInvoice.clear()
        reportInvoice = reportInvoiceService.getInvoiceByDatesAndStatus(startDate, endDate)

        return ResponseEntity<MutableList<ReportInvoiceDto>>(reportInvoice, HttpStatus.OK)
    }
}
