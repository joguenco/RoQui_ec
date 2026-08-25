package dev.joguenco.roqui.withhold.repository

import dev.joguenco.roqui.withhold.model.Withhold
import dev.joguenco.roqui.withhold.model.WithholdDetail
import dev.joguenco.roqui.withhold.model.WithholdDocumentTax
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Transactional
@Repository
class WithholdRepository : CustomWithholdRepository {
    @PersistenceContext lateinit var entityManager: EntityManager

    override fun countByCodeAndNumber(code: String, number: String): Long {
        val count =
            entityManager
                .createQuery(
                    "select count(*) from Withhold " +
                        "where code = :code " +
                        "and number = :number"
                )
                .setParameter("code", code)
                .setParameter("number", number)
                .singleResult as Long

        return count
    }

    override fun findByCodeAndNumber(code: String, number: String): Withhold {
        return entityManager
            .createQuery("from Withhold where code = :code and number = :number")
            .setParameter("code", code)
            .setParameter("number", number)
            .resultList
            .get(0) as Withhold
    }

    override fun findDetailByCodeAndNumber(
        code: String,
        number: String,
    ): MutableList<WithholdDetail> {
        return entityManager
            .createQuery(
                "from WithholdDetail where code = :code and number = :number order by line"
            )
            .setParameter("code", code)
            .setParameter("number", number)
            .resultList as MutableList<WithholdDetail>
    }

    override fun findDocumentTaxByCodeAndNumber(
        code: String,
        number: String,
    ): MutableList<WithholdDocumentTax> {
        return entityManager
            .createQuery("from WithholdDocumentTax where code = :code and number = :number")
            .setParameter("code", code)
            .setParameter("number", number)
            .resultList as MutableList<WithholdDocumentTax>
    }
}
