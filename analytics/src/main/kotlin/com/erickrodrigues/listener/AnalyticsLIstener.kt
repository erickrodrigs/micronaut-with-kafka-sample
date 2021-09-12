package com.erickrodrigues.listener

import com.erickrodrigues.domain.Book
import com.erickrodrigues.services.AnalyticsService
import io.micronaut.configuration.kafka.annotation.KafkaListener
import io.micronaut.configuration.kafka.annotation.Topic
import io.micronaut.context.annotation.Requires
import io.micronaut.context.env.Environment

@Requires(notEnv = [Environment.TEST])
@KafkaListener
class AnalyticsListener(private val analyticsService: AnalyticsService) {

    @Topic("analytics")
    fun updateAnalytics(book: Book) = analyticsService.updateBookAnalytics(book)
}