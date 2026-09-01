package dev.joguenco.roqui.note.delivery.controller

import dev.joguenco.roqui.common.dto.ReportReciptDto
import dev.joguenco.roqui.note.delivery.service.ReportDeliveryNoteService
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
class ReportDeliveryNoteController {

    @Autowired lateinit var reportDeliveryNoteService: ReportDeliveryNoteService

    @GetMapping("/delivery-note/report/dates/{startDate}/{endDate}/status/{status}")
    fun getDeliveryNoteByDatesAndStatus(
        @PathVariable(value = "startDate") startDate: String,
        @PathVariable(value = "endDate") endDate: String,
        @PathVariable(value = "status") status: String,
    ): ResponseEntity<out Any?> {

        val (statusValidation, message) = Validate.rangeOfDates(startDate, endDate)
        if (!statusValidation) {
            return ResponseEntity.badRequest().body(Message(message))
        }
        val reportDeliveryNote =
            reportDeliveryNoteService.getDeliveryNoteByDatesAndStatus(startDate, endDate, status)
        return ResponseEntity<MutableList<ReportReciptDto>>(reportDeliveryNote, HttpStatus.OK)
    }
}
