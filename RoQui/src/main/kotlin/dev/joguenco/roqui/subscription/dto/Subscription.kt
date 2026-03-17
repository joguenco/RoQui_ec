package dev.joguenco.roqui.subscription.dto

data class Subscription(
    val subscriber: String,
    val name: String,
    val endDate: String,
    val formatDate: String,
    val remainingDays: Int
)