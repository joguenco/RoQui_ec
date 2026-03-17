package dev.joguenco.roqui.note.credit.repository

import dev.joguenco.roqui.invoice.model.TaxDetail
import dev.joguenco.roqui.note.credit.model.CreditNote
import dev.joguenco.roqui.note.credit.model.CreditNoteDetail
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Transactional
@Repository
class CreditNoteRepository : CustomCreditNoteRepository {
    @PersistenceContext lateinit var entityManager: EntityManager

    override fun countByCodeAndNumber(code: String, number: String): Long {
        val count =
            entityManager
                .createQuery(
                    "select count(*) from CreditNote " +
                        "where code = :code " +
                        "and number = :number"
                )
                .setParameter("code", code)
                .setParameter("number", number)
                .singleResult as Long

        return count
    }

    override fun findByCodeAndNumber(code: String, number: String): CreditNote {
        return entityManager
            .createQuery("from CreditNote where code = :code and number = :number")
            .setParameter("code", code)
            .setParameter("number", number)
            .resultList
            .get(0) as CreditNote
    }

    override fun findDetailByCodeAndNumber(
        code: String,
        number: String,
    ): MutableList<CreditNoteDetail> {
        return entityManager
            .createQuery("from CreditNoteDetail where code = :code and number = :number")
            .setParameter("code", code)
            .setParameter("number", number)
            .resultList as MutableList<CreditNoteDetail>
    }

    override fun findDetailTax(
        code: String,
        number: String,
        principalCode: String,
        line: Long,
    ): MutableList<TaxDetail> {
        return entityManager
            .createQuery(
                "from TaxDetail " +
                    "where code = :code " +
                    "and number = :number " +
                    "and principalCode = :principalCode " +
                    "and line = :line"
            )
            .setParameter("code", code)
            .setParameter("number", number)
            .setParameter("principalCode", principalCode)
            .setParameter("line", line)
            .resultList as MutableList<TaxDetail>
    }

    override fun findTotalTaxByCodeAndNumber(code: String, number: String): MutableList<TaxDetail> {
        return entityManager
            .createQuery("from TaxDetail where code = :code and number = :number ")
            .setParameter("code", code)
            .setParameter("number", number)
            .resultList as MutableList<TaxDetail>
    }
}
