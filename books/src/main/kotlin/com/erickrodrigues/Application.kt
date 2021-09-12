package com.erickrodrigues

import io.micronaut.runtime.Micronaut.*

fun main(args: Array<String>) {
    build()
        .args(*args)
        .packages("com.erickrodrigues")
        .start()
}

