package dev.joguenco.roqui.note.delivery.service

import dev.joguenco.roqui.common.dto.ReportReciptDto
import dev.joguenco.roqui.common.repository.CustomReportRepository
import dev.joguenco.roqui.note.delivery.model.ReportDeliveryNote
import dev.joguenco.roqui.util.DateUtil
import org.springframework.stereotype.Service

@Service
class ReportDeliveryNoteService(
    private val reportDeliveryNoteRepository: CustomReportRepository<ReportDeliveryNote>
) {
    fun getDeliveryNoteByDatesAndStatus(
        startDate: String,
        endDate: String,
        status: String = "All",
    ): MutableList<ReportReciptDto> {

        val startDateForQuery = DateUtil.toDate(startDate)
        val endDateForQuery = DateUtil.toDate(endDate)

        val result =
            reportDeliveryNoteRepository.findByDatesAndStatus(
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
