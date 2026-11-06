package dev.joguenco.roqui.withhold.repository

import dev.joguenco.roqui.common.repository.CustomReportRepository
import dev.joguenco.roqui.withhold.model.ReportWithhold
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import java.util.Date
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Transactional
@Repository
class ReportWithholdRepository : CustomReportRepository<ReportWithhold> {
    @PersistenceContext lateinit var entityManager: EntityManager

    override fun findByDatesAndStatus(
        startDate: Date,
        endDate: Date,
        status: String,
    ): MutableList<ReportWithhold> {
        if ("Authorized" == status) {
            return entityManager
                .createQuery(
                    "from ReportWithhold " +
                        "where date between :startDate and :endDate " +
                        "and status = 'AUTORIZADO'"
                )
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .resultList as MutableList<ReportWithhold>
        } else if ("Unauthorized" == status) {
            return entityManager
                .createQuery(
                    "from ReportWithhold " +
                        "where date between :startDate and :endDate " +
                        "and status != 'AUTORIZADO'"
                )
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .resultList as MutableList<ReportWithhold>
        } else {
            return entityManager
                .createQuery("from ReportWithhold " + "where date between :startDate and :endDate ")
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .resultList as MutableList<ReportWithhold>
        }
    }
}
