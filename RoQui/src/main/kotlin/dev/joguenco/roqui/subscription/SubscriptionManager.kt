package dev.joguenco.roqui.subscription

import dev.joguenco.roqui.parameter.service.ParameterService
import dev.joguenco.roqui.subscription.dto.Subscription
import org.joda.time.DateTime
import org.joda.time.Days
import java.text.SimpleDateFormat
import java.util.Date

class SubscriptionManager(private val parameterService: ParameterService) {

    fun isAlive(): Boolean {
        val dateSubscription = parameterService.getSubscription()
        val remainingDays = Days.daysBetween(DateTime(Date()), DateTime(dateSubscription)).days
        return remainingDays >= 0
    }

    fun status(): Subscription {
        var endDate: Date = parameterService.errorDate()
        var remainingDays = -1

        endDate = parameterService.getSubscription()
        remainingDays = Days.daysBetween(DateTime(Date()), DateTime(endDate)).days
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")

        return Subscription(
            "",
            "",
            dateFormat.format(endDate),
            "aaaa-mm-dd",
            remainingDays
        )
    }
}