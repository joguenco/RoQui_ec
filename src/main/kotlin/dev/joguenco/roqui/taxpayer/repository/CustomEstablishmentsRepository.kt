package dev.joguenco.roqui.taxpayer.repository

import dev.joguenco.roqui.taxpayer.model.Establishment

interface CustomEstablishmentsRepository {

    fun findByCode(code: String): Establishment
    fun findPrincipal(): Establishment
}