package dev.joguenco.roqui.invoice.repository

import java.util.Date

interface CustomReportInvoiceRepository<T> {

    fun findByDatesAndStatus(startDate: Date, endDate: Date, status: String): MutableList<T>
}
