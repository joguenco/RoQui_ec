package dev.joguenco.roqui.invoice.controller

import dev.joguenco.roqui.electronic.ElectronicDocument
import dev.joguenco.roqui.electronic.TypeDocument
import dev.joguenco.roqui.electronic.send.WebService
import dev.joguenco.roqui.electronic.service.DocumentService
import dev.joguenco.roqui.invoice.service.InvoiceService
import dev.mestizos.roqui.parameter.service.ParameterService
import dev.joguenco.roqui.util.dto.DocumentDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.TimeUnit

@RestController
@RequestMapping("/roqui/v1")
class InvoiceController {

    @Autowired
    lateinit var invoiceService: InvoiceService

    @Autowired
    lateinit var parameterService: ParameterService

    @Autowired
    lateinit var documentService: DocumentService

    @Autowired
    lateinit var webService: WebService

    @PostMapping("/invoice/authorize")
    fun postAuthorize(@RequestBody document: DocumentDto): ResponseEntity<Any> {

        if (invoiceService.count(document.code, document.number) == 0L) {
            return ResponseEntity.notFound().build()
        }

        val buildInvoice = ElectronicDocument(
            document.code,
            document.number,
            invoiceService,
            webService,
            parameterService,
            documentService
        )

        buildInvoice.process(TypeDocument.FACTURA)

        TimeUnit.SECONDS.sleep(3)

        buildInvoice.check()

        return ResponseEntity.ok().build()
    }
}