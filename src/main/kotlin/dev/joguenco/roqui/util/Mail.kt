package dev.joguenco.roqui.util

import dev.joguenco.roqui.information.repository.InformationRepository
import dev.joguenco.roqui.parameter.service.ParameterService

class Mail(
    code: String,
    number: String,
    val parameterService: ParameterService,
    val informationRepository: InformationRepository,
) {

    fun send() {}
}
