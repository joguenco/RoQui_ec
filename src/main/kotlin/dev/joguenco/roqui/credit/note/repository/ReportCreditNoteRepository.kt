package dev.joguenco.roqui.credit.note.repository

import dev.joguenco.roqui.credit.note.model.ReportCreditNote
import dev.joguenco.roqui.invoice.repository.CustomReportInvoiceRepository
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import java.util.Date
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Transactional
@Repository
class ReportCreditNoteRepository : CustomReportInvoiceRepository<ReportCreditNote> {
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
        TODO("Not yet implemented")
    }
}
