package dev.joguenco.roqui.note.debit.repository

import dev.joguenco.roqui.invoice.model.Payment
import dev.joguenco.roqui.invoice.model.TaxDetail
import dev.joguenco.roqui.note.debit.model.DebitNote
import dev.joguenco.roqui.note.debit.model.DebitNoteDetail
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Transactional
@Repository
class DebitNoteRepository : CustomDebitNoteRepository {
    @PersistenceContext lateinit var entityManager: EntityManager

    override fun countByCodeAndNumber(code: String, number: String): Long {
        val count =
            entityManager
                .createQuery(
                    "select count(*) from DebitNote " +
                        "where code = :code " +
                        "and number = :number"
                )
                .setParameter("code", code)
                .setParameter("number", number)
                .singleResult as Long

        return count
    }

    override fun findByCodeAndNumber(code: String, number: String): DebitNote {
        return entityManager
            .createQuery("from DebitNote where code = :code and number = :number")
            .setParameter("code", code)
            .setParameter("number", number)
            .resultList
            .get(0) as DebitNote
    }

    override fun findDetailByCodeAndNumber(
        code: String,
        number: String,
    ): MutableList<DebitNoteDetail> {
        return entityManager
            .createQuery("from DebitNoteDetail where code = :code and number = :number")
            .setParameter("code", code)
            .setParameter("number", number)
            .resultList as MutableList<DebitNoteDetail>
    }

    override fun findTotalTaxByCodeAndNumber(code: String, number: String): MutableList<TaxDetail> {
        return entityManager
            .createQuery("from TaxDetail where code = :code and number = :number ")
            .setParameter("code", code)
            .setParameter("number", number)
            .resultList as MutableList<TaxDetail>
    }

    override fun findPaymentByCodeAndNumber(code: String, number: String): MutableList<Payment> {
        return entityManager
            .createQuery("from Payment where code = :code and number = :number")
            .setParameter("code", code)
            .setParameter("number", number)
            .resultList as MutableList<Payment>
    }
}
