package dev.joguenco.roqui.note.credit.service

import dev.joguenco.roqui.common.dto.ReportReciptDto
import dev.joguenco.roqui.common.repository.CustomReportRepository
import dev.joguenco.roqui.note.credit.model.ReportCreditNote
import dev.joguenco.roqui.util.DateUtil
import org.springframework.stereotype.Service

@Service
class ReportCreditNoteService(
    private val reportCreditNoteRepository: CustomReportRepository<ReportCreditNote>
) {
    fun getCreditNoteByDatesAndStatus(
        startDate: String,
        endDate: String,
        status: String = "All",
    ): MutableList<ReportReciptDto> {

        val startDateForQuery = DateUtil.toDate(startDate)
        val endDateForQuery = DateUtil.toDate(endDate)

        val result =
            reportCreditNoteRepository.findByDatesAndStatus(
                startDateForQuery,
                endDateForQuery,
                status,
            )

        return result
            .map {
                ReportReciptDto(
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
