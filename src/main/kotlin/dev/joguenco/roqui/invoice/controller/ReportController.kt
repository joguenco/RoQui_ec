package dev.joguenco.roqui.invoice.controller

import dev.joguenco.roqui.invoice.dto.ReportInvoiceDto
import dev.joguenco.roqui.invoice.service.ReportInvoiceService
import dev.joguenco.roqui.shared.dto.Message
import dev.joguenco.roqui.util.Validate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/roqui/v1")
class ReportController {

    @Autowired lateinit var reportInvoiceService: ReportInvoiceService

    @GetMapping("/invoice/report/dates/{startDate}/{endDate}/status/{status}")
    fun getInvoiceByDatesAndStatus(
        @PathVariable(value = "startDate") startDate: String,
        @PathVariable(value = "endDate") endDate: String,
        @PathVariable(value = "status") status: String,
    ): ResponseEntity<out Any?> {

        val (statusValidation, message) = Validate.rangeOfDates(startDate, endDate)
        if (!statusValidation) {
            return ResponseEntity.badRequest().body(Message(message))
        }
        val reportInvoice =
            reportInvoiceService.getInvoiceByDatesAndStatus(startDate, endDate, status)
        return ResponseEntity<MutableList<ReportInvoiceDto>>(reportInvoice, HttpStatus.OK)
    }
}
