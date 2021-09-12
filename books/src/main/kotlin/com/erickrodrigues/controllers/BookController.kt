package com.erickrodrigues.controllers

import com.erickrodrigues.services.BookService
import com.erickrodrigues.domain.Book
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import java.util.*

@Controller("/books")
class BookController(private val bookService: BookService) {

    @Get
    fun listAll(): List<Book> = bookService.listAll()

    @Get("/{isbn}")
    fun findBook(isbn: String): Optional<Book> = bookService.findByIsbn(isbn)
}
