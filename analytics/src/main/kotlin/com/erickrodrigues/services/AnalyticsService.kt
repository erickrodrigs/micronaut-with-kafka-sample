package com.erickrodrigues.services

import com.erickrodrigues.domain.Book
import com.erickrodrigues.domain.BookAnalytics
import jakarta.inject.Singleton
import java.util.concurrent.ConcurrentHashMap

@Singleton
class AnalyticsService {
    private val bookAnalytics: MutableMap<Book, Long> = ConcurrentHashMap()

    fun updateBookAnalytics(book: Book) {
        bookAnalytics.compute(book) {
            k, v -> if (v == null) return@compute 1L else return@compute v + 1
        }
    }

    fun listAnalytics(): List<BookAnalytics> =
        bookAnalytics.entries.map { (key, value) -> BookAnalytics(key.isbn, value) }
}