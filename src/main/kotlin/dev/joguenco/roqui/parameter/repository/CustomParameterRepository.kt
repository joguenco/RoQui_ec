package dev.joguenco.roqui.parameter.repository

import dev.joguenco.roqui.parameter.model.Parameter

interface CustomParameterRepository {

    fun findValueByName(name: String): String

    fun findByName(name: String): Parameter

    fun update(parameter: Parameter)
}
