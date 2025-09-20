package dev.joguenco.roqui.note.credit.controller

import dev.joguenco.roqui.electronic.send.WebService
import dev.joguenco.roqui.electronic.service.DocumentService
import dev.joguenco.roqui.note.credit.service.CreditNoteService
import dev.joguenco.roqui.note.credit.service.ReportCreditNoteService
import dev.joguenco.roqui.parameter.service.ParameterService
import org.springframework.beans.factory.annotation.Autowired

class CreditNoteController {

    @Autowired lateinit var creditNoteService: CreditNoteService

    @Autowired lateinit var parameterService: ParameterService

    @Autowired lateinit var documentService: DocumentService

    @Autowired lateinit var webService: WebService

    @Autowired lateinit var reportCreditNoteService: ReportCreditNoteService
}
