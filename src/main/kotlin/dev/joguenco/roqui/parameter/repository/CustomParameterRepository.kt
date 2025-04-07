package dev.joguenco.roqui.parameter.repository

interface CustomParameterRepository {

    fun findValueByName(name: String): String
}
