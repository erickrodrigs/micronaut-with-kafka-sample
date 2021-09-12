package com.erickrodrigues

import io.micronaut.context.env.Environment
import io.micronaut.runtime.Micronaut.*

fun main(args: Array<String>) {
    build()
        .args(*args)
        .packages("com.erickrodrigues")
        .defaultEnvironments(Environment.DEVELOPMENT)
        .start()
}

