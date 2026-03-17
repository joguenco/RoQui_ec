package dev.joguenco.roqui.parameter.repository

import dev.joguenco.roqui.parameter.model.Asset

interface CustomAssetRepository {

    fun findByName(name: String): Asset
}
