package dev.joguenco.roqui.note.delivery.repository

import dev.joguenco.roqui.common.repository.CustomReportRepository
import dev.joguenco.roqui.note.delivery.model.ReportDeliveryNote
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import java.util.Date
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Transactional
@Repository
class ReportDeliveryNoteRepository : CustomReportRepository<ReportDeliveryNote> {
    @PersistenceContext lateinit var entityManager: EntityManager

    override fun findByDatesAndStatus(
        startDate: Date,
        endDate: Date,
        status: String,
    ): MutableList<ReportDeliveryNote> {
        if ("Authorized" == status) {
            return entityManager
                .createQuery(
                    "from ReportDeliveryNote " +
                        "where date between :startDate and :endDate " +
                        "and status = 'AUTORIZADO'"
                )
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .resultList as MutableList<ReportDeliveryNote>
        } else if ("Unauthorized" == status) {
            return entityManager
                .createQuery(
                    "from ReportDeliveryNote " +
                        "where date between :startDate and :endDate " +
                        "and status != 'AUTORIZADO'"
                )
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .resultList as MutableList<ReportDeliveryNote>
        } else {
            return entityManager
                .createQuery(
                    "from ReportDeliveryNote " + "where date between :startDate and :endDate "
                )
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .resultList as MutableList<ReportDeliveryNote>
        }
    }
}
