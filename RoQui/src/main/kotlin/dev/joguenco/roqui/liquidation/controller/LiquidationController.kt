package dev.joguenco.roqui.liquidation.controller

import dev.joguenco.roqui.electronic.ElectronicDocument
import dev.joguenco.roqui.electronic.TypeDocument
import dev.joguenco.roqui.electronic.dto.DocumentDto
import dev.joguenco.roqui.electronic.dto.StatusDto
import dev.joguenco.roqui.electronic.send.WebService
import dev.joguenco.roqui.electronic.service.DocumentService
import dev.joguenco.roqui.information.service.InformationService
import dev.joguenco.roqui.liquidation.service.LiquidationService
import dev.joguenco.roqui.liquidation.service.ReportLiquidationService
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
class LiquidationController {

    @Autowired lateinit var liquidationService: LiquidationService

    @Autowired lateinit var parameterService: ParameterService

    @Autowired lateinit var documentService: DocumentService

    @Autowired lateinit var webService: WebService

    @Autowired lateinit var reportLiquidationService: ReportLiquidationService

    @Autowired lateinit var informationService: InformationService

    @PostMapping("/liquidation/authorize")
    fun postAuthorize(@RequestBody document: DocumentDto): ResponseEntity<Any> {

        if (liquidationService.count(document.code, document.number) == 0L) {
            return ResponseEntity.notFound().build()
        }

        val buildLiquidation =
            ElectronicDocument(
                document.code,
                document.number,
                liquidationService,
                webService,
                parameterService,
                documentService,
            )

        try {
            val stateSend = StatusDto(buildLiquidation.process(TypeDocument.LIQUIDACION))
            TimeUnit.MILLISECONDS.sleep(2700)

            val stateCheck = StatusDto(buildLiquidation.check(informationService))
            if (stateCheck.status.isEmpty()) {
                return ResponseEntity.ok(stateSend)
            }

            return ResponseEntity.ok(stateCheck)
        } catch (e: Exception) {
            println("Error Authorize Liquidation ${e.message}")
            return ResponseEntity.badRequest().body(Message(e.message!!))
        }
    }

    @PostMapping("/liquidation/authorize/dates/{startDate}/{endDate}")
    fun postAuthorizeAll(
        @PathVariable(value = "startDate") startDate: String,
        @PathVariable(value = "endDate") endDate: String,
    ): ResponseEntity<out Any?> {

        val (status, message) = Validate.rangeOfDates(startDate, endDate)
        if (!status) {
            return ResponseEntity.badRequest().body(message)
        }

        val reportLiquidation =
            reportLiquidationService.getLiquidationByDatesAndStatus(
                startDate,
                endDate,
                "Unauthorized",
            )

        for (liquidation in reportLiquidation) {
            val buildLiquidation =
                ElectronicDocument(
                    liquidation.code!!,
                    liquidation.number!!,
                    liquidationService,
                    webService,
                    parameterService,
                    documentService,
                )

            try {
                StatusDto(buildLiquidation.process(TypeDocument.LIQUIDACION))
            } catch (e: Exception) {
                println("Error AuthorizeAll Liquidation ${e.message}")
                return ResponseEntity.badRequest().body(Message(e.message!!))
            }
        }

        TimeUnit.MILLISECONDS.sleep(2700)
        return checkAll(startDate, endDate)
    }

    @PostMapping("/liquidation/check/dates/{startDate}/{endDate}")
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
        val reportLiquidation =
            reportLiquidationService.getLiquidationByDatesAndStatus(
                startDate,
                endDate,
                "Unauthorized",
            )

        for (liquidation in reportLiquidation) {
            val buildLiquidation =
                ElectronicDocument(
                    liquidation.code!!,
                    liquidation.number!!,
                    liquidationService,
                    webService,
                    parameterService,
                    documentService,
                )
            buildLiquidation.setAccessKey(liquidation.accessKey!!)

            try {
                StatusDto(buildLiquidation.check(informationService))
            } catch (e: Exception) {
                println("Error checkAll Liquidation ${e.message}")
                return ResponseEntity.badRequest().body(Message(e.message!!))
            }
        }

        return ResponseEntity.ok().body(Message("Completed successfully"))
    }
}
