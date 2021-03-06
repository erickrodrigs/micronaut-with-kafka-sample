package com.erickrodrigues.controllers

import com.erickrodrigues.domain.Book
import io.micronaut.configuration.kafka.annotation.KafkaListener
import io.micronaut.configuration.kafka.annotation.OffsetReset
import io.micronaut.configuration.kafka.annotation.Topic
import io.micronaut.core.type.Argument
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.micronaut.test.support.TestPropertyProvider
import jakarta.inject.Inject
import org.awaitility.Awaitility
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.util.*
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.TimeUnit

@Testcontainers
@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookControllerTest : TestPropertyProvider {

    companion object {
        val received: MutableCollection<Book> = ConcurrentLinkedDeque()
    }

    @Container
    val kafka = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"))

    @Inject
    lateinit var analyticsListener: AnalyticsListener

    @Inject
    @field:Client("/")
    lateinit var client: HttpClient

    @Test
    fun testMessageIsPublishedToKafkaWhenBookFound() {
        val isbn = "1491950358"
        val result: Optional<Book> = retrieveGet("/books/$isbn") as Optional<Book>

        assertNotNull(result)
        assertTrue(result.isPresent)
        assertEquals(isbn, result.get().isbn)

        Awaitility.await().atMost(5, TimeUnit.SECONDS).until { !received.isEmpty() }
        assertEquals(1, received.size)

        val bookFromKafka = received.iterator().next()
        assertNotNull(bookFromKafka)
        assertEquals(isbn, bookFromKafka.isbn)
    }

    @Test
    fun testMessageIsNotPublishedToKafkaWhenBookNotFound() {
        assertThrows(HttpClientResponseException::class.java) { retrieveGet("/books/INVALID") }
        Thread.sleep(5_000)
        assertEquals(0, received.size)
    }

    override fun getProperties(): Map<String, String> {
        kafka.start()
        return mapOf("kafka.bootstrap.servers" to kafka.bootstrapServers)
    }

    @AfterEach
    fun cleanup() {
        received.clear()
    }

    private fun retrieveGet(url: String) =
        client.toBlocking()
            .retrieve(HttpRequest.GET<Any>(url),
                Argument.of(Optional::class.java, Book::class.java))


    @KafkaListener(offsetReset = OffsetReset.EARLIEST)
    class AnalyticsListener {
        @Topic("analytics")
        fun updateAnalytics(book: Book) {
            received.add(book)
        }
    }
}
