package dev.joguenco.roqui.parameter.repository

import dev.joguenco.roqui.parameter.model.Asset
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Transactional
@Repository
class AssetRepository : CustomAssetRepository {

    @PersistenceContext lateinit var entityManager: EntityManager

    override fun findByName(name: String): Asset {
        return entityManager
            .createQuery("from Asset where name = :name")
            .setParameter("name", name)
            .singleResult as Asset
    }
}
