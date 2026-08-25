package dev.joguenco.roqui.liquidation.repository

import dev.joguenco.roqui.common.repository.CustomReportRepository
import dev.joguenco.roqui.liquidation.model.ReportLiquidation
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import java.util.Date
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Transactional
@Repository
class ReportLiquidationRepository : CustomReportRepository<ReportLiquidation> {
    @PersistenceContext lateinit var entityManager: EntityManager

    override fun findByDatesAndStatus(
        startDate: Date,
        endDate: Date,
        status: String,
    ): MutableList<ReportLiquidation> {
        if ("Authorized" == status) {
            return entityManager
                .createQuery(
                    "from ReportLiquidation " +
                        "where date between :startDate and :endDate " +
                        "and status = 'AUTORIZADO'"
                )
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .resultList as MutableList<ReportLiquidation>
        } else if ("Unauthorized" == status) {
            return entityManager
                .createQuery(
                    "from ReportLiquidation " +
                        "where date between :startDate and :endDate " +
                        "and status != 'AUTORIZADO'"
                )
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .resultList as MutableList<ReportLiquidation>
        } else {
            return entityManager
                .createQuery(
                    "from ReportLiquidation " + "where date between :startDate and :endDate "
                )
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .resultList as MutableList<ReportLiquidation>
        }
    }
}
