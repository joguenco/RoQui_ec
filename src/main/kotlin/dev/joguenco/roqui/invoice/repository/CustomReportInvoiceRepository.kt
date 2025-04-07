package dev.joguenco.roqui.invoice.repository

import dev.joguenco.roqui.invoice.model.ReportInvoice
import java.util.Date

interface CustomReportInvoiceRepository {

    fun findByDatesAndStatus(
        startDate: Date,
        endDate: Date,
        status: String,
    ): MutableList<ReportInvoice>
}
