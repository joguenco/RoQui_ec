package dev.joguenco.roqui.invoice.service

import dev.joguenco.roqui.invoice.dto.ReportInvoiceDto
import dev.joguenco.roqui.invoice.model.ReportInvoice
import dev.joguenco.roqui.invoice.repository.CustomReportInvoiceRepository
import dev.joguenco.roqui.util.DateUtil
import org.springframework.stereotype.Service

@Service
class ReportInvoiceService(
    private val reportInvoiceRepository: CustomReportInvoiceRepository<ReportInvoice>
) {

    fun getInvoiceByDatesAndStatus(
        startDate: String,
        endDate: String,
        status: String = "All",
    ): MutableList<ReportInvoiceDto> {

        val startDateForQuery = DateUtil.toDate(startDate)
        val endDateForQuery = DateUtil.toDate(endDate)

        val result =
            reportInvoiceRepository.findByDatesAndStatus(startDateForQuery, endDateForQuery, status)

        return result
            .map {
                ReportInvoiceDto(
                    id = it.id,
                    code = it.code,
                    number = it.number,
                    accessKey = it.accessKey,
                    date = it.date,
                    total = it.total,
                    identification = it.identification,
                    legalName = it.legalName,
                    email = it.email,
                    status = it.status,
                )
            }
            .toMutableList()
    }
}
