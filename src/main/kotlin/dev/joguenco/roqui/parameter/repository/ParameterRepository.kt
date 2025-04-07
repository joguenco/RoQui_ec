package dev.joguenco.roqui.parameter.repository

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Transactional
@Repository
class ParameterRepository : CustomParameterRepository {

    @PersistenceContext lateinit var entityManager: EntityManager

    override fun findValueByName(name: String): String {
        val value =
            entityManager
                .createQuery("select value from Parameter " + "where name = :name")
                .setParameter("name", name)
                .resultList[0]
                as String

        return value
    }
}
