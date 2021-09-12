package com.erickrodrigues.filters

import com.erickrodrigues.broker.AnalyticsClient
import com.erickrodrigues.domain.Book
import io.micronaut.http.HttpRequest
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Filter
import io.micronaut.http.filter.HttpServerFilter
import io.micronaut.http.filter.ServerFilterChain
import org.reactivestreams.Publisher
import reactor.core.publisher.Flux

@Filter("/books/?*")
class AnalyticsFilter(private val analyticsClient: AnalyticsClient) : HttpServerFilter {
    override fun doFilter(request: HttpRequest<*>?, chain: ServerFilterChain?): Publisher<MutableHttpResponse<*>> =
        Flux.from(chain?.proceed(request))
            .flatMap { response: MutableHttpResponse<*> ->
                val book = response.getBody(Book::class.java).orElse(null)
                if (book == null) {
                    Flux.just(response)
                } else {
                    Flux.from(analyticsClient.updateAnalytics(book)).map { _ -> response }
                }
            }
}