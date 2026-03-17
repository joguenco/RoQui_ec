package dev.joguenco.roqui.parameter.service

import dev.joguenco.roqui.parameter.model.Asset
import dev.joguenco.roqui.parameter.repository.CustomAssetRepository
import org.springframework.stereotype.Service

@Service
class AssetService(private val assetRepository: CustomAssetRepository) {

    fun findByName(name: String): Asset {
        return assetRepository.findByName(name)
    }
}
