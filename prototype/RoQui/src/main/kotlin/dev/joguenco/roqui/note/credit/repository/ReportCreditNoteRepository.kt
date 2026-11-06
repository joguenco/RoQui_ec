package dev.joguenco.roqui.note.credit.repository

import dev.joguenco.roqui.common.repository.CustomReportRepository
import dev.joguenco.roqui.note.credit.model.ReportCreditNote
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import java.util.Date
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Transactional
@Repository
class ReportCreditNoteRepository : CustomReportRepository<ReportCreditNote> {
    @PersistenceContext lateinit var entityManager: EntityManager

    /**
     * Find ReportCreditNote by date and status
     *
     * @param startDate start date
     * @param endDate end date
     * @param status status = Authorized, Unauthorized or All
     * @return list of ReportInvoice
     */
    override fun findByDatesAndStatus(
        startDate: Date,
        endDate: Date,
        status: String,
    ): MutableList<ReportCreditNote> {
        if ("Authorized" == status) {
            return entityManager
                .createQuery(
                    "from ReportCreditNote " +
                        "where date between :startDate and :endDate " +
                        "and status = 'AUTORIZADO'"
                )
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .resultList as MutableList<ReportCreditNote>
        } else if ("Unauthorized" == status) {
            return entityManager
                .createQuery(
                    "from ReportCreditNote " +
                        "where date between :startDate and :endDate " +
                        "and status != 'AUTORIZADO'"
                )
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .resultList as MutableList<ReportCreditNote>
        } else {
            return entityManager
                .createQuery(
                    "from ReportCreditNote " + "where date between :startDate and :endDate "
                )
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .resultList as MutableList<ReportCreditNote>
        }
    }
}
