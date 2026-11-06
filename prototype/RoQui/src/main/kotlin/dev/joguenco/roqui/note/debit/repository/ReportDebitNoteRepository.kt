package dev.joguenco.roqui.note.debit.repository

import dev.joguenco.roqui.common.repository.CustomReportRepository
import dev.joguenco.roqui.note.debit.model.ReportDebitNote
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import java.util.Date
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Transactional
@Repository
class ReportDebitNoteRepository : CustomReportRepository<ReportDebitNote> {
    @PersistenceContext lateinit var entityManager: EntityManager

    override fun findByDatesAndStatus(
        startDate: Date,
        endDate: Date,
        status: String,
    ): MutableList<ReportDebitNote> {
        if ("Authorized" == status) {
            return entityManager
                .createQuery(
                    "from ReportDebitNote " +
                        "where date between :startDate and :endDate " +
                        "and status = 'AUTORIZADO'"
                )
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .resultList as MutableList<ReportDebitNote>
        } else if ("Unauthorized" == status) {
            return entityManager
                .createQuery(
                    "from ReportDebitNote " +
                        "where date between :startDate and :endDate " +
                        "and status != 'AUTORIZADO'"
                )
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .resultList as MutableList<ReportDebitNote>
        } else {
            return entityManager
                .createQuery(
                    "from ReportDebitNote " + "where date between :startDate and :endDate "
                )
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .resultList as MutableList<ReportDebitNote>
        }
    }
}
