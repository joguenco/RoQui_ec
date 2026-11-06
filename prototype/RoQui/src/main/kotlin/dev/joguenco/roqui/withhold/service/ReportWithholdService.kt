package dev.joguenco.roqui.withhold.service

import dev.joguenco.roqui.common.dto.ReportReciptDto
import dev.joguenco.roqui.common.repository.CustomReportRepository
import dev.joguenco.roqui.util.DateUtil
import dev.joguenco.roqui.withhold.model.ReportWithhold
import org.springframework.stereotype.Service

@Service
class ReportWithholdService(
    private val reportWithholdRepository: CustomReportRepository<ReportWithhold>
) {
    fun getWithholdByDatesAndStatus(
        startDate: String,
        endDate: String,
        status: String = "All",
    ): MutableList<ReportReciptDto> {

        val startDateForQuery = DateUtil.toDate(startDate)
        val endDateForQuery = DateUtil.toDate(endDate)

        val result =
            reportWithholdRepository.findByDatesAndStatus(
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
