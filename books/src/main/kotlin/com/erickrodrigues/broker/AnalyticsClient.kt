package com.erickrodrigues.broker

import com.erickrodrigues.domain.Book
import io.micronaut.configuration.kafka.annotation.KafkaClient
import io.micronaut.configuration.kafka.annotation.Topic
import reactor.core.publisher.Mono

@KafkaClient
interface AnalyticsClient {

    @Topic("analytics")
    fun updateAnalytics(book: Book): Mono<Book>
}