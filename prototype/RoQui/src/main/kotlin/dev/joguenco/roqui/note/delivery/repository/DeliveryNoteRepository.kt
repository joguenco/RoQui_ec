package dev.joguenco.roqui.note.delivery.repository

import dev.joguenco.roqui.note.delivery.model.DeliveryNote
import dev.joguenco.roqui.note.delivery.model.DeliveryNoteDetail
import dev.joguenco.roqui.note.delivery.model.DeliveryNoteReceiver
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Transactional
@Repository
class DeliveryNoteRepository : CustomDeliveryNoteRepository {
    @PersistenceContext lateinit var entityManager: EntityManager

    override fun countByCodeAndNumber(code: String, number: String): Long {
        val count =
            entityManager
                .createQuery(
                    "select count(*) from DeliveryNote " +
                        "where code = :code " +
                        "and number = :number"
                )
                .setParameter("code", code)
                .setParameter("number", number)
                .singleResult as Long

        return count
    }

    override fun findByCodeAndNumber(code: String, number: String): DeliveryNote {
        return entityManager
            .createQuery("from DeliveryNote where code = :code and number = :number")
            .setParameter("code", code)
            .setParameter("number", number)
            .resultList
            .get(0) as DeliveryNote
    }

    override fun findReceiverByCodeAndNumber(
        code: String,
        number: String,
    ): MutableList<DeliveryNoteReceiver> {
        return entityManager
            .createQuery(
                "from DeliveryNoteReceiver where code = :code and number = :number order by line"
            )
            .setParameter("code", code)
            .setParameter("number", number)
            .resultList as MutableList<DeliveryNoteReceiver>
    }

    /** La mercaderia de un destinatario: se filtra por su linea. */
    override fun findDetailByCodeAndNumberAndLine(
        code: String,
        number: String,
        line: Long,
    ): MutableList<DeliveryNoteDetail> {
        return entityManager
            .createQuery(
                "from DeliveryNoteDetail " +
                    "where code = :code and number = :number and line = :line"
            )
            .setParameter("code", code)
            .setParameter("number", number)
            .setParameter("line", line)
            .resultList as MutableList<DeliveryNoteDetail>
    }
}
