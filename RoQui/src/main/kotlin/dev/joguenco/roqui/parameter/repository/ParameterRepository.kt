package dev.joguenco.roqui.parameter.repository

import dev.joguenco.roqui.parameter.model.Parameter
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
                .createQuery(
                    "select value from Parameter " + "where name = :name " + "and status = true"
                )
                .setParameter("name", name)
                .resultList[0]
                as String

        return value
    }

    override fun findByName(name: String): Parameter {
        return entityManager
            .createQuery("from Parameter " + "where name = :name " + "and status = true")
            .setParameter("name", name)
            .singleResult as Parameter
    }

    override fun update(parameter: Parameter) {
        val p = findByName(parameter.name!!)
        p.value = parameter.value
        entityManager.flush()
    }

    override fun findEmailSmtpConfiguration(): MutableList<Parameter> {
        return entityManager.createQuery("from Parameter " + "where type = 'Email SMTP'").resultList
            as MutableList<Parameter>
    }
}
