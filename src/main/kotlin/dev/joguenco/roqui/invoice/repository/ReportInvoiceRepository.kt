package dev.joguenco.roqui.invoice.repository

import dev.joguenco.roqui.invoice.model.ReportInvoice
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import java.util.*
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Transactional
@Repository
class ReportInvoiceRepository : CustomReportInvoiceRepository {

    @PersistenceContext lateinit var entityManager: EntityManager

    /**
     * Find ReportInvoice by date and status
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
    ): MutableList<ReportInvoice> {

        if ("Authorized" == status) {
            return entityManager
                .createQuery(
                    "from ReportInvoice " +
                        "where date between :startDate and :endDate " +
                        "and status = 'AUTORIZADO'"
                )
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .resultList as MutableList<ReportInvoice>
        } else if ("Unauthorized" == status) {
            return entityManager
                .createQuery(
                    "from ReportInvoice " +
                        "where date between :startDate and :endDate " +
                        "and status != 'AUTORIZADO'"
                )
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .resultList as MutableList<ReportInvoice>
        } else {
            return entityManager
                .createQuery("from ReportInvoice " + "where date between :startDate and :endDate ")
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .resultList as MutableList<ReportInvoice>
        }
    }
}
