package dev.joguenco.roqui.taxpayer.repository

import dev.joguenco.roqui.taxpayer.model.Establishment

interface CustomEstablishmentRepository {

    fun findByCode(code: String): Establishment

    fun findPrincipal(): Establishment
}
