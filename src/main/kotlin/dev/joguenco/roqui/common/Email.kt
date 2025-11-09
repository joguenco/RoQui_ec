package dev.joguenco.roqui.common

import dev.joguenco.roqui.information.service.InformationService
import dev.joguenco.roqui.parameter.service.ParameterService

class Email(
    val code: String,
    val number: String,
    val parameterService: ParameterService,
    val informationService: InformationService,
) {

    fun send() {

        if (code == "FV") {
            val invoice = informationService.getInvoice(code, number)
            val emailDestination =
                informationService.getEmailByIdentification(invoice.identification!!)
        }
    }
}
