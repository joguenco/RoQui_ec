package dev.joguenco.roqui.information.repository

import dev.joguenco.roqui.information.model.GeneralInformation
import dev.joguenco.roqui.information.model.Information

interface CustomIInformationRepository {
    fun findInformationByIdentification(identification: String): MutableList<Information>

    fun findEmailByIdentification(identification: String): String?

    fun findLegalNameOfTaxpayer(): String

    fun findGeneralInformation(): MutableList<GeneralInformation>
}
