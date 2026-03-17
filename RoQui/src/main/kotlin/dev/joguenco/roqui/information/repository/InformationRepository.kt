package dev.joguenco.roqui.information.repository

import dev.joguenco.roqui.information.model.Information
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Transactional
@Repository
class InformationRepository : CustomIInformationRepository {

    @PersistenceContext lateinit var entityManager: EntityManager

    override fun findInformationByIdentification(identification: String): MutableList<Information> {
        val information =
            entityManager
                .createQuery("from Information " + "where identification = :identification")
                .setParameter("identification", identification)
                .resultList as MutableList<Information>

        return information
    }

    override fun findEmailByIdentification(identification: String): String? {
        val email =
            entityManager
                .createQuery(
                    "select value from Information " +
                        "where identification = :identification " +
                        "and name = 'Email'"
                )
                .setParameter("identification", identification)
                .resultList

        return if (email.isNotEmpty()) email[0] as String else null
    }

    override fun findLegalNameOfTaxpayer(): String {
        return entityManager.createQuery("select legalName from Taxpayer").singleResult as String
    }
}
